package core;

import core.db.DBManager;
import core.db.User;
import core.db.backup.BackupHelper;
import core.gui.HOMainFrame;
import core.gui.SplashFrame;
import core.gui.model.UserColumnController;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.training.TrainingManager;
import core.util.ExceptionHandler;
import core.util.HOLogger;
import core.util.IOUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Main HO starter class.
 *
 * @author thomas.werth
 */



public class HO {

	/**
	 * Release Notes: ============== The first SVN commit AFTER a release should
	 * include an increased VERSION number with DEVELOPMENT set to true an
	 * updated VERSION number in conf/addToZip/version.txt new headers in
	 * conf/addToZip/release_notes.txt and conf/addToZip/changelog.txt The last
	 * SVN commit BEFORE a release should set DEVELOPMENT to false.
	 */

	/**
	 * Preparing updates from inside HO:
	 * 
	 * Beta versions: Upload to development and update betaversion.htm in same folder
	 * 
	 * Full release: Upload a zip with no revision info to the final folder. Update
	 * version.htm in onlinefiles folder. 
	 */
	

	/**
	 * HO Version
	 */
	public static final double VERSION = 1.435d;
	/**
	 * language version
	 */
	public static final int SPRACHVERSION = 2;
	private static int revision = 0;
	/**
	 * Is this a development version? Note that a "development" version can a
	 * release ("Beta" or "DEV" version). The DEVELOPMENT flag is used by the
	 * ant build script. Keep around.
	 */
	private static final boolean DEVELOPMENT = true;
	/**
	 * A RELEASE is when a build artifact gets delivered to users. Note that
	 * even a DEVELOPMENT version can be a RELEASE ("Beta"). So when a version
	 * is build (no matter if DEVELOPMENT or not), this flag should be set to
	 * true. The main purpose for the flag is to disable code (unfinished new
	 * features, debug code) which should not be seen in a release.
	 */
	private static final boolean RELEASE = false;

	public static boolean isDevelopment() {
		return DEVELOPMENT;
	}

	public static boolean isRelease() {
		return RELEASE;
	}

	/**
	 * Main method to start a HOMainFrame.
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		final long start = System.currentTimeMillis();

		System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

		if ((args != null) && (args.length > 0)) {
			String debugLvl = args[0].trim().toUpperCase();

			if (debugLvl.equals("INFO")) {
				HOLogger.instance().setLogLevel(HOLogger.INFORMATION);
			} else if (debugLvl.equals("DEBUG")) {
				HOLogger.instance().setLogLevel(HOLogger.DEBUG);
			} else if (debugLvl.equals("WARNING")) {
				HOLogger.instance().setLogLevel(HOLogger.WARNING);
			} else if (debugLvl.equals("ERROR")) {
				HOLogger.instance().setLogLevel(HOLogger.ERROR);
			}
		}

		// Usermanagement Login-Dialog
		try {
			if (!User.getCurrentUser().isSingleUser()) {
				JComboBox comboBox = new JComboBox(User.getAllUser().toArray());
				int choice = JOptionPane.showConfirmDialog(null, comboBox, "Login",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (choice == JOptionPane.OK_OPTION) {
					User.INDEX = comboBox.getSelectedIndex();
				} else {
					System.exit(0);
				}
			}
		} catch (Exception ex) {
			HOLogger.instance().log(HO.class, ex);
		}

		// Startbild
		final SplashFrame interuptionsWindow = new SplashFrame();

		// Backup
		if (User.getCurrentUser().isHSQLDB()) {
			interuptionsWindow.setInfoText(1, "Backup Database");
			BackupHelper.backup(new File(User.getCurrentUser().getDBPath()));
		}

		// Standardparameter aus der DB holen
		interuptionsWindow.setInfoText(2, "Initialize Database");
		DBManager.instance().loadUserParameter();

		// init Theme
		try {
			ThemeManager.instance().setCurrentTheme(UserParameter.instance().theme);
		} catch (Exception e) {
			HOLogger.instance().log(HO.class, "Can´t load Theme:" + UserParameter.instance().theme);
			JOptionPane.showMessageDialog(null, e.getMessage(), "Can´t load Theme: "
					+ UserParameter.instance().theme, JOptionPane.WARNING_MESSAGE);
		}
		// Init!
		interuptionsWindow.setInfoText(3, "Initialize Data-Administration");

		// Ask for language at first start
		if (DBManager.instance().isFirstStart()) {
			interuptionsWindow.setVisible(false);
			new core.option.InitOptionsDialog();
			interuptionsWindow.setVisible(true);
		}

		// Check -> Sprachdatei in Ordnung?
		interuptionsWindow.setInfoText(4, "Check Languagefiles");
		HOVerwaltung.checkLanguageFile(UserParameter.instance().sprachDatei);
		HOVerwaltung.instance().setResource(UserParameter.instance().sprachDatei);

		if (DBManager.instance().isFirstStart()) {
			interuptionsWindow.setVisible(false);
			JOptionPane.showMessageDialog(null,
					HOVerwaltung.instance().getLanguageString("firststartup.infowinmessage"),
					HOVerwaltung.instance().getLanguageString("firststartup.infowinmessage.title"), JOptionPane.INFORMATION_MESSAGE);
			interuptionsWindow.setVisible(true);
		}

		interuptionsWindow.setInfoText(5, "Load latest Data");
		HOVerwaltung.instance().loadLatestHoModel();
		interuptionsWindow.setInfoText(6, "Load  XtraDaten");

		// TableColumn
		UserColumnController.instance().load();

		// Die Währung auf die aus dem HRF setzen
		float faktorgeld = (float) HOVerwaltung.instance().getModel().getXtraDaten()
				.getCurrencyRate();

		if (faktorgeld > -1) {
			UserParameter.instance().faktorGeld = faktorgeld;
		}

		// Training
		interuptionsWindow.setInfoText(7, "Initialize Training");

		// Training erstellen -> dabei Trainingswochen berechnen auf Grundlage
		// der manuellen DB Einträge
		TrainingManager.instance().refreshTrainingWeeks();

		interuptionsWindow.setInfoText(8, "Prepare to show");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				HOMainFrame.instance().setVisible(true);

				// Startbild weg
				interuptionsWindow.setVisible(false);
				interuptionsWindow.dispose();

				HOLogger.instance().log(HO.class, "Zeit:" + (System.currentTimeMillis() - start));
			}
		});
	}

	public static int getRevisionNumber() {
	return 0;
	}
}
