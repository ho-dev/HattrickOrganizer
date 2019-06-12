package tool.updater;

import java.io.*;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;

import core.HO;
import core.file.ZipHelper;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.net.MyConnector;
import core.net.login.LoginWaitDialog;
import core.util.HOLogger;

public final class UpdateController {

    public static final String UPDATES_URL = "http://ho1.sourceforge.net/onlinefiles";
    private static final String DEV_URL = "https://akasolace.github.io/HO/release_notes(dev).html";
    private static final String BETA_URL = "https://akasolace.github.io/HO/release_notes(beta).html";
    private static final String STABLE_URL = "https://akasolace.github.io/HO/release_notes(stable).html";

    /**
     * Check the external site for the latest version according to user preference regarding release channel
     */
    public static void check4update(boolean isMac) {
        VersionInfo devVersion = MyConnector.instance().getLatestVersion();
        VersionInfo betaVersion = MyConnector.instance().getLatestBetaVersion();
        VersionInfo stableVersion = MyConnector.instance().getLatestStableVersion();
        VersionInfo updVersion = null;

        switch (core.model.UserParameter.temp().ReleaseChannel) {
            case "Stable":
                if (compareToCurrentVersions(stableVersion)) {
                    updVersion = stableVersion;
                }
                break;
            case "Beta":
                if (compareTwoVersions(stableVersion, betaVersion)) {
                    if (compareToCurrentVersions(stableVersion))
                        updVersion = stableVersion;
                }
                else if (compareToCurrentVersions(betaVersion)) {
                    updVersion = betaVersion;
                }
                break;
            default:
                if (compareTwoVersions(stableVersion, devVersion)) {
                    if (compareToCurrentVersions(stableVersion))
                        updVersion = stableVersion;
                }
                else if (compareTwoVersions(betaVersion, devVersion)) {
                    if (compareToCurrentVersions(betaVersion))
                        updVersion = betaVersion;
                }
                else if (compareToCurrentVersions(devVersion)) {
                    updVersion = devVersion;
                }
                break;
        }

        if (updVersion != null) {
            String versionType = updVersion.getversionType();
            String updateAvailable;
            String releaseNoteUrl;
            switch (versionType){
                case "DEV":
                    updateAvailable = HOVerwaltung.instance().getLanguageString("updateDEVavailable");
                    releaseNoteUrl = DEV_URL;
                    break;
                case "BETA":
                    updateAvailable = HOVerwaltung.instance().getLanguageString("updateBETAavailable");
                    releaseNoteUrl = BETA_URL;
                    break;
                default:
                    updateAvailable = HOVerwaltung.instance().getLanguageString("updateStableavailable");
                    releaseNoteUrl = STABLE_URL;
                    break;
            }

            if(isMac) {
                /**
                 * Update is not supported for macOS platform. Hence, instead we will present to the user,
                 * a direct url link for downloading the relevant osX app package -according to his release channel preference.
                 */
                String macos_zip_download_url = get_HO_zip_download_url(updVersion.getfullVersion(), updVersion.getVersion(),versionType);
                macos_zip_download_url = macos_zip_download_url.replace(".zip","_macOS.zip");
                JOptionPane.showMessageDialog(HOMainFrame.instance(),
                        new UpdaterPanel("<html><body>" + updateAvailable + "<br/><br/>"
                                + "<font color=gray>" + HOVerwaltung.instance().getLanguageString("ls.version") + ":</font>"
                                + updVersion.getVersionString() + "<br/>"
                                + "<font color=gray>" + HOVerwaltung.instance().getLanguageString("Released") + ":</font>"
                                + updVersion.getReleaseDate() + "<br/><br/>"
                                + HOVerwaltung.instance().getLanguageString("ls.button.update.available") + "<br/>"
                                + "</body></html>",
                                releaseNoteUrl,
                                macos_zip_download_url
                        ),
                        HOVerwaltung.instance().getLanguageString("confirmation.title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                int update = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
                        new UpdaterPanel("<html><body>" + updateAvailable + "<br/><br/>"
                                + "<font color=gray>" + HOVerwaltung.instance().getLanguageString("ls.version") + ":</font>"
                                + updVersion.getVersionString() + "<br/>"
                                + "<font color=gray>" + HOVerwaltung.instance().getLanguageString("Released") + ":</font>"
                                + updVersion.getReleaseDate() + "<br/><br/>"
                                + HOVerwaltung.instance().getLanguageString("ls.button.update") + "?</body></html>",
                                releaseNoteUrl),
                        HOVerwaltung.instance().getLanguageString("confirmation.title"),
                        JOptionPane.YES_NO_OPTION);

                // Warning, if install via package, ask user to confirmation
                if (update == JOptionPane.YES_OPTION &&
                        System.getProperty("install.mode","").equalsIgnoreCase("pkg") &&
                        versionType.equals("RELEASE")) {
                    update = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
                            HOVerwaltung.instance().getLanguageString("ls.button.update.linux.pkg.warning") + "?",
                            HOVerwaltung.instance().getLanguageString("confirmation.title"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                }

                if (update == JOptionPane.YES_OPTION) {
                    updateHO(updVersion.getfullVersion(), updVersion.getVersion(), versionType);
                }
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

    public static String get_HO_zip_download_url(String full_version, double version, String versionType) {
        if (versionType == "DEV") {
            return "https://github.com/akasolace/HO/releases/download/dev/HO_" + full_version + ".zip";
        } else {
            String ver = Double.toString(version);
            return "https://github.com/akasolace/HO/releases/download/" + ver + "/HO_" + full_version + ".zip";
        }
    }

    public static void updateHO(String full_version, double version, String versionType) {
        updateHO(get_HO_zip_download_url(full_version, version, versionType));
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

    public static boolean compareTwoVersions(VersionInfo a, VersionInfo b) {
        if (a.getVersion() > b.getVersion() ||
                (a.getVersion() == b.getVersion() && a.getBuild() > b.getBuild()))
            return true;

        return false;
    }

    public static boolean compareToCurrentVersions(VersionInfo a) {
        if (a.getVersion() > HO.VERSION ||
                (a.getVersion() == HO.VERSION && a.getBuild() > HO.getRevisionNumber()))
            return true;

        return false;
    }
}
