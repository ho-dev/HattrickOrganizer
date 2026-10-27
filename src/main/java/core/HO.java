package core;

import core.db.DBManager;
import core.db.backup.BackupHelper;
import core.db.user.UserManager;
import core.gui.HOMainFrame;
import core.gui.SplashFrame;
import core.gui.model.UserColumnController;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.jmx.StatementCacheMonitor;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.net.Connector;
import core.training.TrainingManager;
import core.util.*;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.management.*;
import javax.swing.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static java.awt.event.KeyEvent.VK_1;

public class HO {

    private static final HOLogger log = HOLogger.instance();

    @Getter
    private static final HOBuild hoBuild = HOBuildLoader.getBuild();
	private static OSUtils.OS platform;
	private static boolean portable_version; // Used to determine the location of the DB
	public static OSUtils.OS getPlatform() {return platform; }
	public static boolean isDevelopment() {
        return Optional.ofNullable(hoBuild).map(HOBuild::isDevelopment).orElse(false);
	}
	public static boolean isBeta() {
		return Optional.ofNullable(hoBuild).map(HOBuild::isBeta).orElse(false);
	}
	public static boolean isRelease() {
        return Optional.ofNullable(hoBuild).map(HOBuild::isRelease).orElse(false);
	}

	public static String getVersionString() {
        return hoBuild.getVersionString();
	}

	public static boolean isPortableVersion() {
		return portable_version;
	}

	// Only used to run test
	public static void setPortable_version(boolean portable_version) {
		HO.portable_version = portable_version;
	}

	/**
	 *  HO entry point
	 */
	public static void main(String[] args) {
		portable_version = true;
		platform = OSUtils.getOS();

		if (platform == OSUtils.OS.MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.showGroupBox", "true");
			System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		}

		System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

		if (args != null) {
			args = Arrays.stream(args).map(String::toUpperCase).toArray(String[]::new);
			if(Arrays.asList(args).contains("INSTALLED")) {portable_version = false;}
			String arg;
			for (String _arg : args) {
				arg = _arg.trim().toUpperCase();
				switch (arg) {
                    case "INFO" -> log.setLogLevel(HOLogger.INFORMATION);
                    case "DEBUG" -> log.setLogLevel(HOLogger.DEBUG);
                    case "WARNING" -> log.setLogLevel(HOLogger.WARNING);
                    case "ERROR" -> log.setLogLevel(HOLogger.ERROR);
				}
			}
		}

        // Initialize file logging
        log.initializeFileLogging(Paths.get(UserManager.instance().getDbParentFolder(), "logs"));

        log.info(HO.class, "HO-Version: %s".formatted(hoBuild.getVersionString()));

        evaluateEnvironmentVariables();

		// Login selection in case of multi-users DB
		try {
			if (!UserManager.instance().isSingleUser()) {

				var options = createOptionsArray();
				var choice = JOptionPane.showOptionDialog(
						null,
						"",
						"Login",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						null,
						options,
						null );

				if (choice != JOptionPane.CLOSED_OPTION) {
					UserManager.instance().setINDEX(choice);
				} else {
					System.exit(0);
				}
			}
		} catch (Exception ex) {
            log.log(HO.class, ex);
		}

		// start display splash image
		final SplashFrame interruptionWindow = new SplashFrame();

		// Backup
		interruptionWindow.setInfoText(1, "Backup Database");
		BackupHelper.backup(new File(UserManager.instance().getCurrentUser().getDbFolder()));


		// Load user parameters from the DB
		interruptionWindow.setInfoText(2, "Initialize Database");
		DBManager.instance().loadUserParameter();

		// init Theme
		try {
			ThemeManager.instance().setCurrentTheme();
		} catch (Exception e) {
            log.log(HO.class, "Can´t load Theme:" + UserParameter.instance().theme);
			JOptionPane.showMessageDialog(null, e.getMessage(), "Can´t load Theme: "
					+ UserParameter.instance().theme, JOptionPane.WARNING_MESSAGE);
		}
		// Init!
		interruptionWindow.setInfoText(3, "Initialize Data-Administration");

		// Ask for language at first start
		if (DBManager.instance().isFirstStart()) {
			interruptionWindow.setVisible(false);
			new core.option.InitOptionsDialog();
			interruptionWindow.setVisible(true);
		}

		// Check if language file available
		interruptionWindow.setInfoText(4, "Check Language files");
		HOVerwaltung.checkLanguageFile(UserParameter.instance().sprachDatei);
		TranslationFacility.setLanguage(UserParameter.instance().sprachDatei);

		if (DBManager.instance().isFirstStart()) {
			interruptionWindow.setVisible(false);
			JOptionPane.showMessageDialog(null,
					TranslationFacility.tr("firststartup.infowinmessage"),
					TranslationFacility.tr("firststartup.infowinmessage.title"), JOptionPane.INFORMATION_MESSAGE);
			interruptionWindow.setVisible(true);
		}

		interruptionWindow.setInfoText(5, "Load latest Data");
		HOVerwaltung.instance().loadLatestHoModel();
		interruptionWindow.setInfoText(6, "Load  XtraDaten");

		// Load table columns information
		UserColumnController.instance().load();

		// Set the currency from HRF
		var model = HOVerwaltung.instance().getModel();
		if ( model != null) {
			var xtra = HOVerwaltung.instance().getModel().getXtraDaten();
			if (xtra != null) {
				float fxRate = (float) xtra.getCurrencyRate();
				if (fxRate > -1) UserParameter.instance().currencyRate = fxRate;
			}
		}
		// Upgrade database configuration
		if (!DBManager.instance().isFirstStart()) {
			interruptionWindow.setInfoText(7, "Upgrade DB configuration");
			DBManager.instance().updateConfig();
		}

		initJmxSupport();

		// Training
		interruptionWindow.setInfoText(8, "Initialize Training");

		// Training estimation calculated on DB manual entries

		TrainingManager.instance();

		interruptionWindow.setInfoText(9, "Prepare to show");
		SwingUtilities.invokeLater(() -> {
			HOMainFrame.instance().setVisible(true);

			// stop display splash image
			interruptionWindow.setVisible(false);
			interruptionWindow.dispose();
		});
	}

	private static void initJmxSupport() {
		if (HO.isDevelopment()) {
			try {
				MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
				platformMBeanServer.registerMBean(
						new StatementCacheMonitor(),
						new ObjectName("io.github.ho-dev:name=StatementCacheMonitor")
				);
			} catch (MalformedObjectNameException | NotCompliantMBeanException | InstanceAlreadyExistsException |
					 MBeanRegistrationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static Object[] createOptionsArray() {
		var buttons = new ArrayList<JButton>();
		int keyEvent = VK_1;
		for ( var user : UserManager.instance().getAllUser() ) {
			buttons.add(createIconButton(user.getTeamName(), user.getClubLogo(), keyEvent++));
		}
		return buttons.toArray();
	}

	static JButton createIconButton(String teamName, String iconPath, int keyEvent){
		//return getScaledIcon(HOIconName.NO_CLUB_LOGO, width, height);
		var width = 210;
		var height = (int)(width*26./21.);
		Icon scaledIcon;
		try {
			var buttonIcon = ImageIO.read(new File(iconPath));
			var icon = new ImageIcon(buttonIcon);
			scaledIcon = ImageUtilities.getScaledIcon(icon, width, height);
		}catch (Exception exception){
			scaledIcon = new ImageIcon();
		}
		var ret  = new JButton(teamName,scaledIcon);
		ret.setVerticalTextPosition(AbstractButton.BOTTOM);
		ret.setHorizontalTextPosition(AbstractButton.CENTER);
		ret.setMnemonic(keyEvent);

		ret.addActionListener(evt -> {
			var pane = getOptionPane((JComponent)evt.getSource());
			// set the value of the option pane
			pane.setValue(ret);
			var w = SwingUtilities.getWindowAncestor(ret);
			if (w != null) {
				w.setVisible(false);
			}
		});
		return ret;
	}

	private static JOptionPane getOptionPane(JComponent source) {
		var ret = source;
		while (ret != null &&  ! (ret instanceof JOptionPane)) {
			ret = (JComponent) ret.getParent();
		}
		return (JOptionPane) ret;
	}

    private static void evaluateEnvironmentVariables() {
        log.info(HO.class, "Evaluating environment variables.");

        HOEnvironmentVariable.HO_SAVE_DOWNLOADED_XML.valueAsBoolean().ifPresent(saveDownloadedXml -> {
            Connector.setSaveDownloadedXml(saveDownloadedXml);
            log.info(HO.class, "Setting saving of downloaded XML files to '%s'".formatted(saveDownloadedXml));
        });
    }
}
