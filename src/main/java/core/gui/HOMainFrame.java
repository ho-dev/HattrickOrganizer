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
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The Main HO window
 */
public final class HOMainFrame extends JFrame implements Refreshable, ActionListener {

	private static HOMainFrame m_clHOMainFrame;
	private static boolean m_HOMainFrame_initialized=false;
	private InfoPanel m_jpInfoPanel;
	private final JMenuBar m_jmMenuBar = new JMenuBar();
	// Top level Menu
	private final JMenu m_jmFile = new JMenu(HOVerwaltung.instance().getLanguageString("ls.menu.file"));       //File
	private final JMenu m_jmFunctions = new JMenu(HOVerwaltung.instance().getLanguageString("ls.menu.functions")); //Functions
	private final JMenu m_jmModules = new JMenu(HOVerwaltung.instance().getLanguageString("ls.menu.modules"));   //Modules
	private final JMenu m_jmHelp = new JMenu(HOVerwaltung.instance().getLanguageString("ls.menu.help"));      //Help

	// Sub Level Menus

	// -----------  File
	private final JMenuItem m_jmDownloadItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.download"));
	private final JMenuItem m_jmImportItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.importfromhrf"));
	private final JMenuItem m_jmSubksillFull = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation"));
	private final JMenuItem m_jmSubksillRecalc7 = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation7weeks"));
	private final JMenuItem m_jmOptionen = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.preferences"));
	//private final JMenu databaseMenu = new JMenu(HOVerwaltung.instance().getLanguageString("ls.menu.file.database"));
	private final JMenuItem userAdministrationOptionsMenu = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.database.dbuseradministration"));
	private final JMenuItem m_jmiDbCleanupTool = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.database.databasecleanup"));
	private final JMenuItem m_jmFullScreenItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.fullscreen"));
	private final JMenuItem m_jmBeendenItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.quit"));


	// -----------  Functions

	// -----------  Modules

	// -----------  Help
	private final JMenuItem m_jmHomepageItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.help.projecthomepage"));
	private final JMenuItem m_jmWikiItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.help.help"));
	private final JMenuItem m_jmReportAbug = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.help.reportabug"));
	private final JMenuItem m_jmAboutAbout = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.help.about"));
	private final JMenuItem m_jmCheckUpdate = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.update.ho"));
	private final JMenuItem m_jmChangelog = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.help.changelog"));

	public static AtomicBoolean launching = new AtomicBoolean(false);


	// Components
	private HOTabbedPane m_jtpTabbedPane;

	private final Vector<String> m_vOptionPanelNames = new Vector<>();
	private final Vector<JPanel> m_vOptionPanels = new Vector<>();

	private final AtomicBoolean isAppTerminated = new AtomicBoolean(false); // set when HO should be terminated
	private final List<ApplicationClosingListener> applicationClosingListener = new ArrayList<>();

	// Menu color depending of version
	private final Color c_beta = new Color(162, 201, 255);
	private final Color c_dev = new Color(235, 170, 170);

	public Player getSelectedPlayer() {
		return m_selectedPlayer;
	}

	private Player m_selectedPlayer;

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
		initMenue();

		RefreshManager.instance().doRefresh();
		launching.set(false);
	}

	private void setFrameTitle() {
		var teamName = HOVerwaltung.instance().getModel().getBasics().getTeamName();
//		String teamName = DBManager.instance().getBasics(DBManager.instance().getLatestHRF().getHrfId()).getTeamName();

		String frameTitle = StringUtils.isEmpty(teamName) ? "" : teamName;
		
		if (!HO.isRelease()) {
			frameTitle += " (" + HOVerwaltung.instance().getLanguageString("ls.java.version") + ": " + System.getProperty("java.version") + ")";
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
				ExceptionDialog dlg = new ExceptionDialog("Error", ex);
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

	public static boolean isHOMainFrame_initialized(){
		return m_HOMainFrame_initialized;
	}

	public void selectPlayer(Player player) {
		if ( m_selectedPlayer != player) {
			m_selectedPlayer = player;
			var lineupPanel = getLineupPanel();
			if ( lineupPanel != null ) lineupPanel.setPlayer(player.getPlayerID());
			getSpielerUebersichtPanel().setPlayer(player);
		}
	}

	public LineupPanel getLineupPanel() {
		Container c = getTabbedPane().getModulePanel(IModule.LINEUP);
		if (c instanceof LineupPanel) {
			return (LineupPanel) c;
		} else if (c != null) {
			return ((LineupMasterView) c).getLineupPanel();
		}
		else {
			return null;
		}
	}

	private InfoPanel getInfoPanel(){
		if (m_jpInfoPanel == null) {
			m_jpInfoPanel = new InfoPanel();
		}
		return m_jpInfoPanel;
	}
	public void setWaitInformation(){setInformation(HOVerwaltung.instance().getLanguageString("BitteWarten"), 1);}
	public void resetInformation(){setInformation("",-100);}
	public void setInformation( String information) { setInformation(information,-100);}
	public void setInformationCompleted() { setInformation(Helper.getTranslation("ls.update_status.complete"), 100);}
	public void setInformation( String information, int progress){
		if (launching.get()) return;
		getInfoPanel().setInformation(information, progress);
	}
	public void setInformation( String information, Color color){
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
		return m_jtpTabbedPane;
	}

	/**
	 * Get the current weather.
	 */
	public static Weather getWeather() {
		if (m_clHOMainFrame == null) {
			return Weather.PARTIALLY_CLOUDY;
		}
		var lineup =  instance().getLineupPanel();
		if ( lineup != null ) return lineup.getWeather();
		return Weather.NULL;
	}

	/**
	 * Get the transfer scout panel.
	 */
	public TransfersPanel getTransferScoutPanel() {
		return ((TransfersPanel) getTabbedPane().getModulePanel(IModule.TRANSFERS));
	}

	/**
	 * Handle action events.
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		final Object source = actionEvent.getSource();

		if (source.equals(m_jmImportItem)) { // HRF Import
			new HRFImport(this);
		} else if (source.equals(m_jmDownloadItem)) { // HRF Download
				DownloadDialog.instance();
		} else if (source.equals(m_jmOptionen)) { // Options
			new OptionenDialog(this).setVisible(true);
		} else if (source.equals(userAdministrationOptionsMenu)) {
			new UserAdministrationDialog(this).setVisible(true);
		} else if (source.equals(m_jmiDbCleanupTool)) {
			DBCleanupTool dbCleanupTool = new DBCleanupTool();
			dbCleanupTool.showDialog(HOMainFrame.instance());
		} else if (source.equals(m_jmSubksillFull)) { // recalc training (2 seasons = 32)
			var from = HODateTime.now().minus(64*7, ChronoUnit.DAYS);
			if (JOptionPane.showConfirmDialog(this, Helper.getTranslation("SubskillRecalcFull"),
					Helper.getTranslation("ls.menu.file.subskillrecalculation"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
				HOVerwaltung.instance().recalcSubskills(true, from.toDbTimestamp());
			}
		} else if (source.equals(m_jmSubksillRecalc7)) { // recalc training (7 weeks)
			var from = HODateTime.now().minus(7*7, ChronoUnit.DAYS);
			if (JOptionPane.showConfirmDialog(this, Helper.getTranslation("subskillRecalc7w", Date.from(from.instant)),
					Helper.getTranslation("ls.menu.file.subskillrecalculation7weeks"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
				HOVerwaltung.instance().recalcSubskills(true, from.toDbTimestamp());
			}
		} else if (source.equals(m_jmFullScreenItem)) { // Toggle full screen mode
			FullScreen.instance().toggle(this);
		} else if (source.equals(m_jmBeendenItem)) { // Quit
			// Restore normal window mode (i.e. leave full screen)
			FullScreen.instance().restoreNormalMode(this);
			// Fire CloseEvent, so all Modules get informed
			this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} else if (source.equals(m_jmAboutAbout)) {
			Credits.showCredits(HOMainFrame.instance());
		} else if (source.equals(m_jmHomepageItem)) { // Homepage
			openURL("https://akasolace.github.io/HO/");
		} else if (source.equals(m_jmWikiItem)) { // User Guide
			openURL("https://ho.readthedocs.io/");
		} else if (source.equals(m_jmReportAbug)) { // Report a bug
			openURL("https://github.com/akasolace/HO/issues/new/choose");
		}

		else if (source.equals(m_jmCheckUpdate)) {
			UpdateController.check4update(true);
		}
		else if (source.equals(m_jmChangelog)) {
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
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this,
							HOVerwaltung.instance().getLanguageString("Changelog.error"),
							HOVerwaltung.instance().getLanguageString("Fehler"),
							JOptionPane.ERROR_MESSAGE
					);
					e.printStackTrace();
				}
			}
		}
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
		}
		else if (HO.isBeta()) {
			getContentPane().setBackground(c_beta);
		}
		else {
			setContentPane(new ImagePanel());
		}

		getContentPane().setLayout(new BorderLayout());

		m_jtpTabbedPane = new HOTabbedPane();

		IModule[] activeModules = ModuleManager.instance().getModules(true);
		for (IModule module : activeModules) {
			if (module.hasMainTab() && module.isStartup()) {
				m_jtpTabbedPane.showTab(module.getModuleId());
			}
		}

		getContentPane().add(m_jtpTabbedPane, BorderLayout.CENTER);
		if (m_jtpTabbedPane.getTabCount() > 0) {
			m_jtpTabbedPane.setSelectedIndex(0);
		}

		getContentPane().add(getInfoPanel(), BorderLayout.SOUTH);

		setLocation(UserParameter.instance().hoMainFrame_PositionX,
				    UserParameter.instance().hoMainFrame_PositionY);
		setSize(UserParameter.instance().hoMainFrame_width,
				UserParameter.instance().hoMainFrame_height);
	}

	/**
	 * Initialize the menu.
	 */
	public void initMenue() {
		// No F10!
		((InputMap) UIManager.get("Table.ancestorInputMap")).remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));

		m_jmDownloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		m_jmDownloadItem.addActionListener(this);
		m_jmFile.add(m_jmDownloadItem);

		// Import HRF
		m_jmImportItem.addActionListener(this);
		m_jmFile.add(m_jmImportItem);
		m_jmFile.addSeparator();

		// Subksill recalculation
		m_jmSubksillFull.addActionListener(this);
		m_jmFile.add(m_jmSubksillFull);
		m_jmSubksillRecalc7.addActionListener(this);
		m_jmFile.add(m_jmSubksillRecalc7);

		m_jmFile.addSeparator();

		// Options
		m_jmOptionen.addActionListener(this);
		m_jmFile.add(m_jmOptionen);
		userAdministrationOptionsMenu.addActionListener(this);
		m_jmiDbCleanupTool.addActionListener(this);
		m_jmFile.add(userAdministrationOptionsMenu);
		m_jmFile.add(m_jmiDbCleanupTool);

		m_jmFile.addSeparator();

		// Toggle full screen mode
		if (FullScreen.instance().isFullScreenSupported(this)) {
			m_jmFullScreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11,
                                                                     KeyEvent.SHIFT_DOWN_MASK));
		} else {
			m_jmFullScreenItem.setEnabled(false);
		}
		m_jmFullScreenItem.addActionListener(this);
		m_jmFile.add(m_jmFullScreenItem);

		m_jmFile.addSeparator();

		// Quit
		m_jmBeendenItem.addActionListener(this);
		m_jmFile.add(m_jmBeendenItem);

		m_jmMenuBar.add(m_jmFile);

		// Modules
		IModule[] activeModules = ModuleManager.instance().getModules(true);
		for (IModule activeModule : activeModules) {
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

				m_jmFunctions.add(showTabMenuItem);
			}
			if (activeModule.hasMenu()) {
				m_jmModules.add(activeModule.getMenu());
			}
		}

		// Help =========================================================================
		m_jmHomepageItem.addActionListener(this);  //   Help | HomePage
		m_jmHelp.add(m_jmHomepageItem);

		m_jmWikiItem.addActionListener(this);     //   Help | Wiki
		m_jmHelp.add(m_jmWikiItem);

		m_jmReportAbug.addActionListener(this);    //   Help | Report a bug
		m_jmHelp.add(m_jmReportAbug);
		m_jmHelp.addSeparator();

		m_jmCheckUpdate.addActionListener(this);
		m_jmHelp.add(m_jmCheckUpdate);				// Help | check update
		m_jmChangelog.addActionListener(this);
		m_jmHelp.add(m_jmChangelog);				// Help | changelog
		m_jmHelp.addSeparator();

		m_jmAboutAbout.addActionListener(this);   // Help | About
		m_jmHelp.add(m_jmAboutAbout);

		// add Top Level Menus
		m_jmMenuBar.add(m_jmFunctions);
		m_jmMenuBar.add(new ToolManager().getToolMenu());
		m_jmMenuBar.add(m_jmModules);

		m_jmMenuBar.add(m_jmHelp);

		if (!HO.isRelease()) {
			m_jmMenuBar.add(DebugMode.getDeveloperMenu());
		}

		SwingUtilities.updateComponentTreeUI(m_jmMenuBar);
		this.setJMenuBar(m_jmMenuBar);
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
				UserParameter.instance().FXrate = faktorgeld;
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
		m_jtpTabbedPane.showTab(IModule.MATCHES);
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
	 * @param moduleId
	 *            ID of the module whose tab should be shown.
	 */
	public void showTab(int moduleId) {
		m_jtpTabbedPane.showTab(moduleId);
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
				getToolkit().getScreenSize().width-parameter.hoMainFrame_PositionX+currentDevice.getDefaultConfiguration().getBounds().x);
		parameter.hoMainFrame_height = Math.min(getSize().height,
				getToolkit().getScreenSize().height-parameter.hoMainFrame_PositionY+currentDevice.getDefaultConfiguration().getBounds().y);

		final var lineupPanel = getLineupPanel();

		assert lineupPanel != null;
		parameter.aufstellungsAssistentPanel_gruppe = lineupPanel.getAssistantGroup();
		parameter.aufstellungsAssistentPanel_reihenfolge = lineupPanel.getAssistantOrder();
		parameter.lineupAssistentPanel_include_group = !lineupPanel.isAssistantSelectedGroupExcluded();
		parameter.aufstellungsAssistentPanel_cbfilter = lineupPanel.isAssistantGroupFilter();
		parameter.aufstellungsAssistentPanel_idealPosition = lineupPanel.isAssistantBestPositionFirst();
		parameter.aufstellungsAssistentPanel_form = lineupPanel.isAssistantConsiderForm();
		parameter.aufstellungsAssistentPanel_verletzt = lineupPanel.isAssistantIgnoreInjured();
		parameter.aufstellungsAssistentPanel_gesperrt = lineupPanel.isAssistantIgnoreSuspended();
		parameter.aufstellungsAssistentPanel_notLast = lineupPanel.isAssistantExcludeLastMatch();

		// PlayerOverviewPanel
		if (getTabbedPane().isModuleTabVisible(IModule.PLAYEROVERVIEW)) {
			final int[] sup = getSpielerUebersichtPanel().getDividerLocations();
			parameter.spielerUebersichtsPanel_horizontalLeftSplitPane = sup[0];
			parameter.spielerUebersichtsPanel_horizontalRightSplitPane = sup[1];
			parameter.spielerUebersichtsPanel_verticalSplitPane = sup[2];
			getSpielerUebersichtPanel().saveColumnOrder();
		}

		// Lineup Panel
		if (getTabbedPane().isModuleTabVisible(IModule.LINEUP)) {
			final int[] ap = getLineupPanel().getDividerLocations();
			parameter.lineupPanel_verticalSplitLocation = ap[0];
			parameter.lineupPanel_horizontalSplitLocation = ap[1];
			getLineupPanel().saveColumnOrder();
		}

		// TransferScoutPanel
		if (getTabbedPane().isModuleTabVisible(IModule.TRANSFERS)) {
			parameter.transferScoutPanel_horizontalSplitPane = getTransferScoutPanel().getScoutPanel().getDividerLocation();
		}

		for ( var module : ModuleManager.instance().getModules(true)){
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
				HOLogger.instance().info(getClass(), "exiting..." );
				if (isAppTerminated.get()) {
					System.exit(0);
				} else {
					HOLogger.instance().warning(getClass(), "The app is not fully terminated yet." );
				}
			}

			/**
			 * Close HO window.
			 */
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				HOLogger.instance().info(getClass(), "shutting down HO... ");
				shutdown();
			}

		});
	}
}
