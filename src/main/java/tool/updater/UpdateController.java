package tool.updater;

import core.file.ZipHelper;
import core.file.xml.Extension;
import core.gui.HOMainFrame;
import core.model.HOParameter;
import core.model.HOVerwaltung;
//import core.model.News;
//import core.model.UserParameter;
import core.net.MyConnector;
import core.net.login.LoginWaitDialog;
import core.util.HOLogger;

import java.io.File;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;
import core.HO;

public final class UpdateController {


	public static final String UPDATES_URL = "http://ho1.sourceforge.net/onlinefiles";

	/**
	 * Check the external site for the latest version according to user preference regarding release channel
	 */
	public static void check4update(boolean bInformationOnly) {
		VersionInfo version;
		switch (core.model.UserParameter.temp().ReleaseChannel) {
			case "Stable":
				version = MyConnector.instance().getLatestStableVersion();
				break;
			case "Beta":
				version = MyConnector.instance().getLatestBetaVersion();
				break;
			default:
				version = MyConnector.instance().getLatestVersion();
				break;
		}

		if ((version != null) && (version.getBuild() > HO.RevisionNumber))
		     {
			String versionType = version.getversionType();
			String updateAvailable;
			switch (versionType){
				case "DEV":
					updateAvailable = HOVerwaltung.instance().getLanguageString("updateDEVavailable");
					break;
				case "BETA":
					updateAvailable = HOVerwaltung.instance().getLanguageString("updateBETAavailable");
					break;
				default:
					updateAvailable = HOVerwaltung.instance().getLanguageString("updateStableavailable");
					break;
			}


			if(bInformationOnly){
				JOptionPane.showMessageDialog(HOMainFrame.instance(),
						updateAvailable + "\n\n"
								+ HOVerwaltung.instance().getLanguageString("ls.version") + ": "
								+ version.getVersionString() + "\n"
								+ HOVerwaltung.instance().getLanguageString("Released") + ": "
								+ version.getReleaseDate() + "\n\n"
								+ HOVerwaltung.instance().getLanguageString("ls.button.update.available") + "\n"
								+ getHOupdateURL(version.getfullVersion(), version.getVersion(), versionType),
						       HOVerwaltung.instance().getLanguageString("confirmation.title"),
						JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				int update = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
						updateAvailable + "\n\n"
								+ HOVerwaltung.instance().getLanguageString("ls.version") + ": "
								+ version.getVersionString() + "\n"
								+ HOVerwaltung.instance().getLanguageString("Released") + ": "
								+ version.getReleaseDate() + "\n\n"
								+ HOVerwaltung.instance().getLanguageString("ls.button.update") + "?",
						HOVerwaltung.instance().getLanguageString("confirmation.title"),
						JOptionPane.YES_NO_OPTION);

				if (update == JOptionPane.YES_OPTION) {
						updateHO(version.getfullVersion(), version.getVersion(), versionType);}
				}

		} else {
			final int currRev = HO.getRevisionNumber();
			JOptionPane.showMessageDialog(HOMainFrame.instance(), HOVerwaltung.instance()
					.getLanguageString("updatenotavailable")
					+ "\n\n"
					+ HOVerwaltung.instance().getLanguageString("ls.version")
					+ ": "
					+ HO.VERSION
					+ (currRev > 1 ? " (Build " + currRev + ")" : ""), HOVerwaltung.instance()
					.getLanguageString("ls.menu.file.update") + " - "+ HOVerwaltung.instance()
					.getLanguageString("ls.menu.file.update.ho"), JOptionPane.INFORMATION_MESSAGE);

		}
	}

	public static String getHOupdateURL(String full_version, double version, String versionType) {
		if (versionType == "DEV") {
			return "https://github.com/akasolace/HO/releases/download/dev/HO_" + full_version + ".zip";
		} else {
			String ver = Double.toString(version);
			return "https://github.com/akasolace/HO/releases/download/" + ver + "/HO_" + ver + ".zip";
		}
	}

	public static void updateHO(String full_version, double version, String versionType) {
            updateHO(getHOupdateURL(full_version, version, versionType));
    }

	public static void updateHO(final String urlString) {
		File tmp = new File("update.zip");
		LoginWaitDialog wait = new LoginWaitDialog(HOMainFrame.instance());
		wait.setVisible(true);
		HOLogger.instance().debug(UpdateController.class, "Try to download: " + urlString);
		if (!UpdateHelper.download(urlString, tmp)) {
			wait.setVisible(false);
			return;
		}
		wait.setVisible(false);
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile("update.zip");
			String dir = System.getProperty("user.dir");
			ZipHelper.extractFile(zipFile, "buildResources/Win/HO.bat", dir);
			ZipHelper.extractFile(zipFile, "buildResources/Linux/HO.sh", dir);
			ZipHelper.extractFile(zipFile, "HOUpdater.class", dir);
		} catch (Exception e) {
			HOLogger.instance().log(UpdateController.class, e);
			return;
		} finally {
			ZipHelper.close(zipFile);
		}
		JOptionPane.showMessageDialog(null,
				HOVerwaltung.instance().getLanguageString("NeustartErforderlich"), HOVerwaltung.instance()
				.getLanguageString("ls.menu.file.update") + " - "+ HOVerwaltung.instance()
				.getLanguageString("ls.menu.file.update.ho"),
				JOptionPane.INFORMATION_MESSAGE);

		HOMainFrame.instance().beenden();
	}


}
