package core.gui;

import core.HO;
import core.db.DBManager;
import core.file.hrf.HRFImport;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.tabbedPane.HOTabbedPane;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.FormulaFactors;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.match.Weather;
import core.model.player.Player;
import core.module.IModule;
import core.module.ModuleManager;
import core.module.config.ModuleConfig;
import core.net.DownloadDialog;
import core.net.MyConnector;
import core.net.login.ProxySettings;
import core.option.OptionenDialog;
import core.option.db.UserAdministrationDialog;
import core.util.*;
import module.lineup.LineupMasterView;
import module.lineup.LineupPanel;
import module.matches.MatchesPanel;
import module.playerOverview.PlayerOverviewPanel;
import module.playeranalysis.PlayerAnalysisModulePanel;
import module.transfer.TransfersPanel;
import tool.ToolManager;
import tool.dbcleanup.DBCleanupTool;
import tool.updater.UpdateController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The Main HO window
 */
public final class HOMainFrame extends JFrame implements Refreshable {

	private static HOMainFrame m_clHOMainFrame;
	private static boolean m_HOMainFrame_initialized = false;
	private InfoPanel m_jpInfoPanel;

	public static final AtomicBoolean launching = new AtomicBoolean(false);

	// Components
	private HOTabbedPane hoTabbedPane;

	private final Vector<String> m_vOptionPanelNames = new Vector<>();
	private final Vector<JPanel> m_vOptionPanels = new Vector<>();

	private final AtomicBoolean isAppTerminated = new AtomicBoolean(false); // set when HO should be terminated
	private final List<ApplicationClosingListener> applicationClosingListener = new ArrayList<>();

	// Menu color depending of version
	private final Color c_beta = new Color(162, 201, 255);
	private final Color c_dev = new Color(235, 170, 170);

//	// TODO: Move this to a model backing the main window
//	public Player getSelectedPlayer() {
//		return m_selectedPlayer;
//	}
//
//	private Player m_selectedPlayer;

	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Singleton
	 */
	private HOMainFrame() {
		launching.set(true);

		if (OSUtils.isMac()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.showGroupBox", "true");
		}

		// Log HO! version
		HOLogger.instance().info(getClass(),
			"This is HO! version " + HO.getVersionString() + ", have fun!");

		// Log Operating System
		HOLogger.instance().info(
			getClass(),
			"Operating system found: " + System.getProperty("os.name") + " on "
				+ System.getProperty("os.arch") + " (" + System.getProperty("os.version")
				+ ")");

		// Log Java version
		HOLogger.instance().info(
			getClass(),
			"Using java: " + System.getProperty("java.version") + " ("
				+ System.getProperty("java.vendor") + ")");

		RefreshManager.instance().registerRefreshable(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtilities.updateComponentTreeUI(this);

		setFrameTitle();
		setFrameIconImage();

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addListeners();

		initProxy();
		initComponents();
		initMenu();

		RefreshManager.instance().doRefresh();
		launching.set(false);
	}

	private void setFrameTitle() {
		var teamName = HOVerwaltung.instance().getModel().getBasics().getTeamName();
		String frameTitle = StringUtils.isEmpty(teamName) ? "" : teamName;

		if (!HO.isRelease()) {
			frameTitle += " (" + TranslationFacility.tr("ls.java.version") + ": " + System.getProperty("java.version") + ")";
		}

		setTitle(frameTitle);
	}

	private void setFrameIconImage() {
		String iconName = HOIconName.LOGO16_STABLE;
		if (!HO.isRelease()) {
			iconName = HOIconName.LOGO16 + "_" + HO.getVersionType().toLowerCase();
		}

		final Image iconImage = ImageUtilities.iconToImage(ThemeManager.getIcon(iconName));

		if (OSUtils.isMac()) {
			try {
				final Taskbar taskbar = Taskbar.getTaskbar();
				taskbar.setIconImage(iconImage);
			} catch (final UnsupportedOperationException e) {
				HOLogger.instance().error(HOMainFrame.class, "OS doesn't support operation: " + e.getMessage());
			}
		} else {
			this.setIconImage(iconImage);
		}
	}

	public void addApplicationClosingListener(ApplicationClosingListener listener) {
		if (!this.applicationClosingListener.contains(listener)) {
			this.applicationClosingListener.add(listener);
		}
	}

	private void fireApplicationClosing() {
		for (int i = this.applicationClosingListener.size() - 1; i >= 0; i--) {
			try {
				this.applicationClosingListener.get(i).applicationClosing();
			} catch (Exception ex) {
				new ExceptionDialog("Error", ex);
			}
		}
	}

	/**
	 * Getter for the singleton HOMainFrame instance.
	 */
	public static HOMainFrame instance() {
		if (m_clHOMainFrame == null) {
			m_clHOMainFrame = new HOMainFrame();
		}

		m_HOMainFrame_initialized = true;
		return m_clHOMainFrame;
	}

	public static boolean isHOMainFrame_initialized() {
		return m_HOMainFrame_initialized;
	}

//	public void selectPlayer(Player player) {
//		if (m_selectedPlayer != player) {
//			m_selectedPlayer = player;
//			var lineupPanel = getLineupPanel();
//			if (lineupPanel != null) lineupPanel.setPlayer(player.getPlayerId());
//			getSpielerUebersichtPanel().setPlayer(player);
//		}
//	}

	public LineupPanel getLineupPanel() {
		Container c = getTabbedPane().getModulePanel(IModule.LINEUP);
		if (c instanceof LineupPanel) {
			return (LineupPanel) c;
		} else if (c != null) {
			return ((LineupMasterView) c).getLineupPanel();
		} else {
			return null;
		}
	}

	private InfoPanel getInfoPanel() {
		if (m_jpInfoPanel == null) {
			m_jpInfoPanel = new InfoPanel();
		}
		return m_jpInfoPanel;
	}

	public void setWaitInformation() {
		setInformation(TranslationFacility.tr("BitteWarten"), 1);
	}

	public void resetInformation() {
		setInformation("", -100);
	}

	public void setInformation(String information) {
		setInformation(information, -100);
	}

	public void setInformationCompleted() {
		setInformation(Helper.getTranslation("ls.update_status.complete"), -100);
	}

	public void setInformation(String information, int progress) {
		if (launching.get()) return;
		getInfoPanel().setInformation(information, progress);
	}

	public void setInformation(String information, Color color) {
		if (launching.get()) return;
		getInfoPanel().setInformation(information, color);
	}

	public void updateProgress(int progress) {
		if (launching.get()) return;
		getInfoPanel().setProgressbarValue(progress);
	}

	public PlayerAnalysisModulePanel getSpielerAnalyseMainPanel() {
		return ((PlayerAnalysisModulePanel) getTabbedPane().getModulePanel(IModule.PLAYERANALYSIS));
	}

	public PlayerOverviewPanel getSpielerUebersichtPanel() {
		return ((PlayerOverviewPanel) getTabbedPane().getModulePanel(IModule.PLAYEROVERVIEW));
	}

	public HOTabbedPane getTabbedPane() {
		return hoTabbedPane;
	}

	/**
	 * Get the current weather.
	 */
	public static Weather getWeather() {
		if (m_clHOMainFrame == null) {
			return Weather.PARTIALLY_CLOUDY;
		}
		var lineup = instance().getLineupPanel();
		if (lineup != null) return lineup.getWeather();
		return Weather.NULL;
	}

	/**
	 * Get the transfer scout panel.
	 */
	public TransfersPanel getTransferScoutPanel() {
		return ((TransfersPanel) getTabbedPane().getModulePanel(IModule.TRANSFERS));
	}

	private void openURL(String url) {
		try {
			BrowserLauncher.openURL(url);
		} catch (Exception ex) {
			HOLogger.instance().log(HOMainFrame.class, ex);
		}
	}

	/**
	 * closing HO
	 */
	public void shutdown() {

		CursorToolkit.startWaitCursor(getRootPane());
		try {
			fireApplicationClosing();

			// TODO: instead of calling XY.instance().save() from here, those classes should register an ApplicationClosingListener
			HOLogger.instance().debug(getClass(), "Shutting down HO!");
			// save current UserParameter
			saveUserParameter();
			HOLogger.instance().debug(getClass(), "UserParameters saved");
			// Save scout list
			getTransferScoutPanel().getScoutPanel().saveScoutListe();
			HOLogger.instance().debug(getClass(), "ScoutList saved");
			// Save formula factors
			FormulaFactors.instance().save();
			HOLogger.instance().debug(getClass(), "FormulaFactors saved");
			// Save module configs
			ModuleConfig.instance().save();
			HOLogger.instance().debug(getClass(), "Module configurations saved");
			// Disconnect
			DBManager.instance().disconnect();
			HOLogger.instance().debug(getClass(), "Disconnected");

			isAppTerminated.set(true); // enable System.exit in windowClosed()
			HOLogger.instance().info(getClass(), "Shutdown complete! isAppTerminated: " + isAppTerminated.get());

			// Dispose makes frame windowClosed as soon as all modules windowClosing all complete.
			try {
				dispose();
			} catch (Exception ignored) {
			}
		} catch (Exception ee) {
			HOLogger.instance().error(getClass(), ee);
		} finally {
			CursorToolkit.stopWaitCursor(getRootPane());
		}
	}

	/**
	 * Builds frame.
	 */
	public void initComponents() {
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(5000);

		if (HO.isDevelopment()) {
			getContentPane().setBackground(c_dev);
		} else if (HO.isBeta()) {
			getContentPane().setBackground(c_beta);
		} else {
			setContentPane(new ImagePanel());
		}

		getContentPane().setLayout(new BorderLayout());

		hoTabbedPane = new HOTabbedPane();

		IModule[] activeModules = ModuleManager.instance().getModules(true);
		for (IModule module : activeModules) {
			if (module.hasMainTab() && module.isStartup()) {
				hoTabbedPane.showTab(module.getModuleId());
			}
		}

		getContentPane().add(hoTabbedPane, BorderLayout.CENTER);
		if (hoTabbedPane.getTabCount() > 0) {
			hoTabbedPane.setSelectedIndex(0);
		}

		getContentPane().add(getInfoPanel(), BorderLayout.SOUTH);

		setLocation(UserParameter.instance().hoMainFrame_PositionX,
			UserParameter.instance().hoMainFrame_PositionY);
		setSize(UserParameter.instance().hoMainFrame_width,
			UserParameter.instance().hoMainFrame_height);
	}

	/**
	 * Initialises the menu.
	 */
	public void initMenu() {
		// No F10!
		((InputMap) UIManager.get("Table.ancestorInputMap")).remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));

		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());

		final JMenu functionsMenu = new JMenu(TranslationFacility.tr("ls.menu.functions")); //Functions
		final JMenu modulesMenu = new JMenu(TranslationFacility.tr("ls.menu.modules"));   //Modules

		initModuleFunctionMenus(functionsMenu, modulesMenu);

		// Help =========================================================================

		// add Top Level Menus
		menuBar.add(functionsMenu);
		menuBar.add(new ToolManager().getToolMenu());
		menuBar.add(modulesMenu);

		menuBar.add(createHelpMenu());

		if (!HO.isRelease()) {
			menuBar.add(DebugMode.getDeveloperMenu());
		}

		SwingUtilities.updateComponentTreeUI(menuBar);
		this.setJMenuBar(menuBar);
	}

	private void initModuleFunctionMenus(JMenu functionsMenu, JMenu modulesMenu) {
		// Modules
        var listOfModules = Arrays.stream(ModuleManager.instance().getModules(true)).sorted(Comparator.comparing(IModule::getMenuOrder)).toList();
    	for (IModule activeModule : listOfModules) {
			if (activeModule.hasMainTab()) {
				JMenuItem showTabMenuItem = new JMenuItem(activeModule.getDescription());
				showTabMenuItem.setAccelerator(activeModule.getKeyStroke());
				showTabMenuItem.putClientProperty("MODULE", activeModule);
				showTabMenuItem.addActionListener(e -> {
					JMenuItem item = (JMenuItem) e.getSource();
					IModule module = (IModule) item.getClientProperty("MODULE");
					getTabbedPane().showTab(module.getModuleId());
					RefreshManager.instance().doRefresh();
				});
				functionsMenu.add(showTabMenuItem);
			}
			if (activeModule.hasMenu()) {
				modulesMenu.add(activeModule.getMenu());
			}
		}
	}

	private JMenu createHelpMenu() {
		final JMenu helpMenu = new JMenu(TranslationFacility.tr("ls.menu.help"));      //Help

		final JMenuItem homepageMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.help.projecthomepage"));
		homepageMenuItem.addActionListener(e -> openURL("https://ho-dev.github.io/HattrickOrganizer/"));  //   Help | HomePage
		helpMenu.add(homepageMenuItem);

		final JMenuItem helpMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.help.help"));
		helpMenuItem.addActionListener(e -> openURL("https://ho.readthedocs.io/"));     //   Help | Wiki
		helpMenu.add(helpMenuItem);

		final JMenuItem bugReportMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.help.reportabug"));
		bugReportMenuItem.addActionListener(e -> openURL("https://github.com/ho-dev/HattrickOrganizer/issues/new/choose"));    //   Help | Report a bug
		helpMenu.add(bugReportMenuItem);
		helpMenu.addSeparator();

		final JMenuItem checkUpdateMuenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.file.update.ho"));

		checkUpdateMuenuItem.addActionListener(e -> UpdateController.check4update(true));
		helpMenu.add(checkUpdateMuenuItem);// Help | check update

		final JMenuItem changelogMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.help.changelog"));
		changelogMenuItem.addActionListener(e -> {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				try {
					File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
					URI logFile;
					if (!OSUtils.isMac()) {
						logFile = jarFile.getParentFile().toPath().resolve("changelog.html").toUri();
					} else {
						logFile = jarFile.getParentFile().getParentFile().getParentFile().toPath().resolve("changelog.html").toUri();
					}
					Desktop.getDesktop().browse(logFile);
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(this,
						TranslationFacility.tr("Changelog.error"),
						TranslationFacility.tr("Fehler"),
						JOptionPane.ERROR_MESSAGE
					);
					HOLogger.instance().error(HOMainFrame.class, "Error opening changelog panel: " + exc);
				}
			}
		});
		helpMenu.add(changelogMenuItem);                // Help | changelog
		helpMenu.addSeparator();

		final JMenuItem aboutMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.help.about"));
		aboutMenuItem.addActionListener(e -> Credits.showCredits(HOMainFrame.instance()));   // Help | About
		helpMenu.add(aboutMenuItem);
		return helpMenu;
	}

	private JMenu createFileMenu() {
		final JMenu fileMenu = new JMenu(TranslationFacility.tr("ls.menu.file"));       //File

		final JMenuItem downloadMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.file.download"));
		downloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		downloadMenuItem.addActionListener(e -> DownloadDialog.instance());
		fileMenu.add(downloadMenuItem);

		// Import HRF
		final JMenuItem importMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.file.importfromhrf"));
		importMenuItem.addActionListener(e -> new HRFImport(this));
		fileMenu.add(importMenuItem);
		fileMenu.addSeparator();

		// Sub-skill recalculation
		final JMenuItem subSkillFullRecalcMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.file.subskillrecalculation"));
		subSkillFullRecalcMenuItem.addActionListener(e -> {
			var from = HODateTime.now().minus(128 * 7, ChronoUnit.DAYS);
			if (JOptionPane.showConfirmDialog(this,
				TranslationFacility.tr("Subskill.Recalc.Full") + "\n" + TranslationFacility.tr("subskill.Recalc.Start"),
				Helper.getTranslation("ls.menu.file.subskillrecalculation"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
				HOVerwaltung.instance().recalcSubskills(true, from.toDbTimestamp());
			}
		});
		fileMenu.add(subSkillFullRecalcMenuItem);

		final JMenuItem subSkillRecalc7WeeksMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.file.subskillrecalculation7weeks"));
		subSkillRecalc7WeeksMenuItem.addActionListener(e -> {
			var nextTraining = HOVerwaltung.instance().getModel().getXtraDaten().getNextTrainingDate();
			var from = nextTraining.minus(7 * 7, ChronoUnit.DAYS);
			if (JOptionPane.showConfirmDialog(this,
				TranslationFacility.tr("subskill.Recalc.7w") + "\n" +
					TranslationFacility.tr("subskill.Recalc.1stTrainingweek") + ": " +
					from.toLocaleHTWeek().toString() + "\n" +
					TranslationFacility.tr("subskill.Recalc.Start"),
				Helper.getTranslation("ls.menu.file.subskillrecalculation7weeks"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
				HOVerwaltung.instance().recalcSubskills(true, from.toDbTimestamp());
			}
		});
		fileMenu.add(subSkillRecalc7WeeksMenuItem);
		fileMenu.addSeparator();

		// Options
		final JMenuItem preferencesMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.file.preferences"));
		preferencesMenuItem.addActionListener(e -> new OptionenDialog(this).setVisible(true));
		fileMenu.add(preferencesMenuItem);

		final JMenuItem userAdministrationOptionsMenu = new JMenuItem(TranslationFacility.tr("ls.menu.file.database.dbuseradministration"));
		userAdministrationOptionsMenu.addActionListener(e -> new UserAdministrationDialog(this).setVisible(true));

		final JMenuItem m_jmiDbCleanupTool = new JMenuItem(TranslationFacility.tr("ls.menu.file.database.databasecleanup"));
		m_jmiDbCleanupTool.addActionListener(e -> {
			DBCleanupTool dbCleanupTool = new DBCleanupTool();
			dbCleanupTool.showDialog(HOMainFrame.instance());
		});

		fileMenu.add(userAdministrationOptionsMenu);
		fileMenu.add(m_jmiDbCleanupTool);

		fileMenu.addSeparator();

		// Toggle full screen mode
		final JMenuItem fullScreenMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.file.fullscreen"));
		if (FullScreen.instance().isFullScreenSupported(this)) {
			fullScreenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11,
				KeyEvent.SHIFT_DOWN_MASK));
		} else {
			fullScreenMenuItem.setEnabled(false);
		}
		fullScreenMenuItem.addActionListener(e -> FullScreen.instance().toggle(this));
		fileMenu.add(fullScreenMenuItem);

		fileMenu.addSeparator();

		// Quit
		final JMenuItem quitMenuItem = new JMenuItem(TranslationFacility.tr("ls.menu.file.quit"));
		quitMenuItem.addActionListener(e -> {
			// Restore normal window mode (i.e. leave full screen)
			FullScreen.instance().restoreNormalMode(this);
			// Fire CloseEvent, so all Modules get informed
			this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		});
		fileMenu.add(quitMenuItem);

		return fileMenu;
	}

	/**
	 * Proxy setup.
	 */
	private void initProxy() {
		if (UserParameter.instance().ProxyAktiv) {
			ProxySettings settings = new ProxySettings();
			settings.setProxyHost(UserParameter.instance().ProxyHost);
			settings.setUseProxy(UserParameter.instance().ProxyAktiv);
			if (!StringUtils.isEmpty(UserParameter.instance().ProxyPort)) {
				settings.setProxyPort(Integer.parseInt(UserParameter.instance().ProxyPort));
			}
			settings.setAuthenticationNeeded(UserParameter.instance().ProxyAuthAktiv);
			settings.setUsername(UserParameter.instance().ProxyAuthName);
			settings.setPassword(UserParameter.instance().ProxyAuthPassword);
			MyConnector.instance().enableProxy(settings);
		}
	}

	/**
	 * Get all option panel names.
	 */
	public Vector<String> getOptionPanelNames() {
		return m_vOptionPanelNames;
	}

	/**
	 * Get all option panels.
	 */
	public Vector<JPanel> getOptionPanels() {
		return m_vOptionPanels;
	}

	/**
	 * Reinit, set currency.
	 */
	@Override
	public void reInit() {
		// Set the currency from the HRF file.
		try {
			var xtra = HOVerwaltung.instance().getModel().getXtraDaten();
			if (xtra == null) return;
			float faktorgeld = (float) xtra.getCurrencyRate();

			if (faktorgeld > -1) {
				UserParameter.instance().currencyRate = faktorgeld;
			}
		} catch (Exception e) {
			HOLogger.instance().log(HOMainFrame.class, "Currency changed failed! " + e.getMessage());
		}
	}

	/**
	 * Called when data is changed.
	 */
	@Override
	public void refresh() {
		// nix?
	}

	// --------------------------------------------------------------
	public void showMatch(final int matchid) {
		hoTabbedPane.showTab(IModule.MATCHES);
		final MatchesPanel matchesPanel = (MatchesPanel) getTabbedPane().getModulePanel(IModule.MATCHES);
		SwingUtilities.invokeLater(() -> {
			assert matchesPanel != null;
			matchesPanel.showMatch(matchid);
		});
	}

	// ----------------Helper methods---------------------------------

	/**
	 * Shows the tab associated with module with ID <code>moduleId</code>.
	 *
	 * @param moduleId ID of the module whose tab should be shown.
	 */
	public void showTab(int moduleId) {
		hoTabbedPane.showTab(moduleId);
	}

	/**
	 * Gets the current user parameters, and stores them in the database.
	 */
	private void saveUserParameter() {
		UserParameter parameter = UserParameter.instance();

		parameter.hoMainFrame_PositionX = Math.max(getLocation().x, 0);
		parameter.hoMainFrame_PositionY = Math.max(getLocation().y, 0);

		final GraphicsDevice currentDevice = this.getGraphicsConfiguration().getDevice();

		parameter.hoMainFrame_width = Math.min(getSize().width,
			getToolkit().getScreenSize().width - parameter.hoMainFrame_PositionX + currentDevice.getDefaultConfiguration().getBounds().x);
		parameter.hoMainFrame_height = Math.min(getSize().height,
			getToolkit().getScreenSize().height - parameter.hoMainFrame_PositionY + currentDevice.getDefaultConfiguration().getBounds().y);

		final var lineupPanel = getLineupPanel();

		assert lineupPanel != null;
		parameter.aufstellungsAssistentPanel_gruppe = lineupPanel.getAssistantGroup();
		parameter.aufstellungsAssistentPanel_reihenfolge = lineupPanel.getAssistantOrder();
		parameter.lineupAssistentPanel_include_group = !lineupPanel.isAssistantSelectedGroupExcluded();
		parameter.aufstellungsAssistentPanel_cbfilter = lineupPanel.isAssistantGroupFilter();
		parameter.aufstellungsAssistentPanel_idealPosition = lineupPanel.isAssistantBestPositionFirst();
		parameter.aufstellungsAssistentPanel_form = lineupPanel.isAssistantConsiderForm();
		parameter.aufstellungsAssistentPanel_averagerating = lineupPanel.isUseAverageRating();
		parameter.aufstellungsAssistentPanel_verletzt = lineupPanel.isAssistantIgnoreInjured();
		parameter.aufstellungsAssistentPanel_gesperrt = lineupPanel.isAssistantIgnoreSuspended();
		parameter.aufstellungsAssistentPanel_notLast = lineupPanel.isAssistantExcludeLastMatch();

		// PlayerOverviewPanel
		if (getTabbedPane().isModuleTabVisible(IModule.PLAYEROVERVIEW)) {
			final int[] sup = getSpielerUebersichtPanel().getDividerLocations();
			parameter.spielerUebersichtsPanel_horizontalLeftSplitPane = sup[0];
			parameter.spielerUebersichtsPanel_horizontalRightSplitPane = sup[1];
			parameter.spielerUebersichtsPanel_verticalSplitPane = sup[2];
//			getSpielerUebersichtPanel().saveColumnOrder();
		}

		// Lineup Panel
		if (getTabbedPane().isModuleTabVisible(IModule.LINEUP)) {
			final int[] ap = getLineupPanel().getDividerLocations();
			parameter.lineupPanel_verticalSplitLocation = ap[0];
			parameter.lineupPanel_horizontalSplitLocation = ap[1];
//			getLineupPanel().saveColumnOrder();
		}

		// TransferScoutPanel
		if (getTabbedPane().isModuleTabVisible(IModule.TRANSFERS)) {
			parameter.transferScoutPanel_horizontalSplitPane = getTransferScoutPanel().getScoutPanel().getDividerLocation();
		}

		for (var module : ModuleManager.instance().getModules(true)) {
			module.storeUserSettings();
		}

		DBManager.instance().saveUserParameter();
	}

	private void addListeners() {
		addWindowListener(new WindowAdapter() {
			/**
			 * Finally shutting down the application when the main window is
			 * closed. This is initiated through the call to dispose().
			 * System.exit is called only in the case when @see shutdown() is
			 * called in advance. This event is called when switching into full
			 * screen mode, too.
			 *
			 * @param windowEvent
			 *            is ignored
			 */
			@Override
			public void windowClosed(WindowEvent windowEvent) {
				HOLogger.instance().info(getClass(), "exiting...");
				if (isAppTerminated.get()) {
					System.exit(0);
				} else {
					HOLogger.instance().warning(getClass(), "The app is not fully terminated yet.");
				}
			}

			/**
			 * Close HO window.
			 */
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				UserParameter userParam = UserParameter.instance();
				if (!userParam.confirmOnExit) {
					HOLogger.instance().info(getClass(), "shutting down HO... ");
					shutdown();
					return;
				}

				JCheckBox dontAskAgain = new JCheckBox(TranslationFacility.tr("ls.menu.file.quit.neveraskagain"));
				Object[] params = {TranslationFacility.tr("confirm.exit.message"), dontAskAgain};

				int choice = JOptionPane.showConfirmDialog(
						HOMainFrame.this,
						params,
						TranslationFacility.tr("confirm.exit.title"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
				);

				if (dontAskAgain.isSelected()) {
					UserParameter.instance().confirmOnExit = false;
				}
				if (choice == JOptionPane.YES_OPTION) {
					HOLogger.instance().info(getClass(), "shutting down HO... ");
					shutdown();
				}
				}
			});
	}
}
