// %3852537837:de.hattrickorganizer.gui%
package core.gui;

import core.HO;
import core.db.DBManager;
import core.file.hrf.HRFImport;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.tabbedPane.HOTabbedPane;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.gui.theme.ho.HOTheme;
import core.gui.theme.jgoodies.JGoodiesTheme;
import core.gui.theme.nimbus.NimbusTheme;
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
import core.option.db.DatabaseOptionsDialog;
import core.util.BrowserLauncher;
import core.util.HOLogger;
import core.util.StringUtils;
import module.lineup.AufstellungsAssistentPanelNew;
import module.lineup.IAufstellungsAssistentPanel;
import module.lineup.LineupMasterView;
import module.lineup.LineupPanel;
import module.matches.SpielePanel;
import module.playerOverview.SpielerUebersichtsPanel;
import module.playeranalysis.PlayerAnalysisModulePanel;
import module.transfer.TransfersPanel;
import tool.ToolManager;
import tool.dbcleanup.DBCleanupTool;
import tool.updater.UpdateController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * The Main HO window
 */
public final class HOMainFrame extends JFrame implements Refreshable, ActionListener {

	public static final int BUSY = 0;
	public static final int READY = 1;
	private static final long serialVersionUID = -6333275250973872365L;
	private static HOMainFrame m_clHOMainFrame;
	private static int status = READY;
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
	private final JMenuItem m_jmTraining = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation"));
	private final JMenuItem m_jmTraining2 = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation7weeks"));
	private final JMenuItem m_jmOptionen = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.preferences"));
	private final JMenu databaseMenu = new JMenu(HOVerwaltung.instance().getLanguageString("ls.menu.file.database"));
	private final JMenuItem databaseOptionsMenu = new JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.database.dbuseradministration"));
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


	// Components
	private HOTabbedPane m_jtpTabbedPane;

	private Vector<String> m_vOptionPanelNames = new Vector<String>();
	private Vector<JPanel> m_vOptionPanels = new Vector<JPanel>();

	private boolean isAppTerminated = false; // set when HO should be terminated
	private List<ApplicationClosingListener> applicationClosingListener = new ArrayList<ApplicationClosingListener>();

	// Menu color depending of version
	private final Color c_beta = new Color(162, 201, 255);
	private final Color c_dev = new Color(235, 170, 170);

	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Singleton
	 */
	private HOMainFrame() {

		// Log HO! version
		HOLogger.instance().info(getClass(),
				"This is HO! version " + getVersionString() + ", have fun!");

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

		setDefaultFont(UserParameter.instance().schriftGroesse);

		
		String teamName = DBManager.instance().getBasics(DBManager.instance().getLatestHrfId()).getTeamName();

		if(teamName.equals("")){
		setTitle("HO! - Hattrick Organizer " + getVersionString());}
		else{
			setTitle("HO! - Hattrick Organizer " + getVersionString() + " - " + teamName);}

		if (HO.isDevelopment()) {
			this.setIconImage(ThemeManager.getIcon(HOIconName.LOGO16_DEV).getImage());
		}
		else if (HO.isBeta()) {
			this.setIconImage(ThemeManager.getIcon(HOIconName.LOGO16_BETA).getImage());
		}
		else {
			this.setIconImage(ThemeManager.getIcon(HOIconName.LOGO16_STABLE).getImage());
		}

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addListeners();

		initProxy();
		initComponents();
		initMenue();

		RefreshManager.instance().doRefresh();
	}

	final public static boolean isMac() {
		return (System.getProperty("os.name").toLowerCase(java.util.Locale.ENGLISH).indexOf("mac") != -1);
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * This method creates a MacOS specific listener for the quit operation
	 * ("Command-Q")
	 * 
	 * We need to use reflections here, because the com.apple.eawt.* classes are
	 * Apple specific
	 * 
	 * @author flattermann <flattermannHO@gmail.com>
	 */
	private void addMacOSListener() {
		HOLogger.instance().debug(getClass(), "Mac OS detected. Activating specific listeners...");
		try {
			// Create the Application
			Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
			Object appleApp = applicationClass.newInstance();

			// Create the ApplicationListener
			Class<?> applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
			Object appleListener = Proxy.newProxyInstance(getClass().getClassLoader(),
					new Class[] { applicationListenerClass }, new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) {
							if (method.getName().equals("handleQuit")) {
								HOLogger.instance()
										.debug(getClass(),
												"ApplicationListener.handleQuit() fired! Quitting MacOS Application!");
								shutdown();
							}
							return null;
						}
					});

			// Register the ApplicationListener
			Method addApplicationListenerMethod = applicationClass.getDeclaredMethod(
					"addApplicationListener", new Class[] { applicationListenerClass });
			addApplicationListenerMethod.invoke(appleApp, new Object[] { appleListener });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addApplicationClosingListener(ApplicationClosingListener listener) {
		if (!this.applicationClosingListener.contains(listener)) {
			this.applicationClosingListener.add(listener);
		}
	}

	public void removeApplicationClosingListener(ApplicationClosingListener listener) {
		this.applicationClosingListener.remove(listener);
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

	public static String getVersionString() {
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMinimumFractionDigits(1);
		String txt = nf.format(HO.VERSION);

		if (!HO.isRelease()) {
			final int r = HO.getRevisionNumber();
			if (r >= 1) {
				txt += " r" + HO.getRevisionNumber();
			}
		}

		return txt;
	}

	/**
	 * Getter for the singleton HOMainFrame instance.
	 */
	public static HOMainFrame instance() {
		if (m_clHOMainFrame == null) {
			m_clHOMainFrame = new HOMainFrame();
		}

		return m_clHOMainFrame;
	}

	public void setActualSpieler(Player player) {
		getAufstellungsPanel().setPlayer(player.getSpielerID());
		getSpielerUebersichtPanel().setPlayer(player);
	}

	public LineupPanel getAufstellungsPanel() {
		Container c = getTabbedPane().getModulePanel(IModule.LINEUP);
		if (c instanceof LineupPanel) {
			return (LineupPanel) c;
		} else {
			return ((LineupMasterView) c).getLineupPanel();
		}
	}

	public InfoPanel getInfoPanel() {
		if (m_jpInfoPanel == null)
			m_jpInfoPanel = new InfoPanel();
		return m_jpInfoPanel;
	}

	public PlayerAnalysisModulePanel getSpielerAnalyseMainPanel() {
		return ((PlayerAnalysisModulePanel) getTabbedPane().getModulePanel(IModule.PLAYERANALYSIS));
	}

	public SpielerUebersichtsPanel getSpielerUebersichtPanel() {
		return ((SpielerUebersichtsPanel) getTabbedPane().getModulePanel(IModule.PLAYEROVERVIEW));
	}

	public HOTabbedPane getTabbedPane() {
		return m_jtpTabbedPane;
	}

	/**
	 * Get the current weather.
	 */
	public static Weather getWetter() {
		if (m_clHOMainFrame == null) {
			return Weather.PARTIALLY_CLOUDY;
		}
		return instance().getAufstellungsPanel().getAufstellungsAssistentPanel().getWeather();
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
		HOMainFrame.setHOStatus(HOMainFrame.BUSY);
		final Object source = actionEvent.getSource();

		if (source.equals(m_jmImportItem)) { // HRF Import
			new HRFImport(this);
		} else if (source.equals(m_jmDownloadItem)) { // HRF Download
			new DownloadDialog();
		} else if (source.equals(m_jmOptionen)) { // Options
			new OptionenDialog(this).setVisible(true);
		} else if (source.equals(databaseOptionsMenu)) {
			new DatabaseOptionsDialog(this).setVisible(true);
		} else if (source.equals(m_jmiDbCleanupTool)) {
			DBCleanupTool dbCleanupTool = new DBCleanupTool();
			dbCleanupTool.showDialog(HOMainFrame.instance());
		} else if (source.equals(m_jmTraining)) { // recalc training
			if (JOptionPane.showConfirmDialog(this,
					HOVerwaltung.instance().getLanguageString("SubskillRecalcFull"), HOVerwaltung
							.instance().getLanguageString("ls.menu.file.subskillrecalculation"),
					JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
				HOVerwaltung.instance().recalcSubskills(true, null);
			}
		} else if (source.equals(m_jmTraining2)) { // recalc training (7 weeks)
			Calendar cal = Calendar.getInstance();
			cal.setLenient(true);
			cal.add(Calendar.WEEK_OF_YEAR, -7); // half season
			if (JOptionPane.showConfirmDialog(
					this,
					HOVerwaltung.instance().getLanguageString(
							"subskillRecalc7w",
							new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(cal
									.getTime())),
					HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation7weeks"),
					JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
				Timestamp from = new Timestamp(cal.getTimeInMillis());
				HOVerwaltung.instance().recalcSubskills(true, from);
			}
		} else if (source.equals(m_jmFullScreenItem)) { // Toggle full screen
														// mode
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
			openURL("https://github.com/akasolace/HO/wiki");
		} else if (source.equals(m_jmReportAbug)){ // Report a bug
			openURL("https://github.com/akasolace/HO/issues");
		}

		else if (source.equals(m_jmCheckUpdate)) {
			if(isMac()) {
				UpdateController.check4update(true);
			}
			else
			{
				UpdateController.check4update(false);
			}
		}
		else if (source.equals(m_jmChangelog)) {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				try {
					File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
					URI logFile;
					if (!isMac()) {logFile = jarFile.getParentFile().toPath().resolve("changelog.html").toUri();}
					else {logFile = jarFile.getParentFile().getParentFile().getParentFile().toPath().resolve("changelog.html").toUri();}
					Desktop.getDesktop().browse(logFile);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, HOVerwaltung.instance().getLanguageString("Changelog.error"), HOVerwaltung.instance().getLanguageString("Fehler"), JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}

		HOMainFrame.setHOStatus(HOMainFrame.READY);
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
			// aktuelle UserParameter speichern
			saveUserParameter();
			HOLogger.instance().debug(getClass(), "UserParameters saved");
			// Scoutliste speichern
			getTransferScoutPanel().getScoutPanel().saveScoutListe();
			HOLogger.instance().debug(getClass(), "ScoutList saved");
			// Faktoren saven
			FormulaFactors.instance().save();
			HOLogger.instance().debug(getClass(), "FormulaFactors saved");
			// Save module configs
			ModuleConfig.instance().save();
			HOLogger.instance().debug(getClass(), "Module configurations saved");
			// Disconnect
			DBManager.instance().disconnect();
			HOLogger.instance().debug(getClass(), "Disconnected");
			HOLogger.instance().debug(getClass(), "Shutdown complete!");
			// Dispose führt zu einem windowClosed, sobald alle windowClosing
			// (Modules) durch sind
			isAppTerminated = true; // enable System.exit in windowClosed()
			try {
				dispose();
			} catch (Exception e) {
			}
		} finally {
			CursorToolkit.stopWaitCursor(getRootPane());
		}
	}

	/**
	 * Frame aufbauen
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
		// Kein F10!
		((InputMap) UIManager.get("Table.ancestorInputMap")).remove(KeyStroke.getKeyStroke(
				KeyEvent.VK_F2, 0));

		m_jmDownloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		m_jmDownloadItem.addActionListener(this);
		m_jmFile.add(m_jmDownloadItem);

		// Import HRF
		m_jmImportItem.addActionListener(this);
		m_jmFile.add(m_jmImportItem);
		m_jmFile.addSeparator();

		// Training
		m_jmTraining.addActionListener(this);
		m_jmFile.add(m_jmTraining);
		m_jmTraining2.addActionListener(this);
		m_jmFile.add(m_jmTraining2);

		m_jmFile.addSeparator();

		// Optionen
		m_jmOptionen.addActionListener(this);
		m_jmFile.add(m_jmOptionen);
		databaseMenu.add(databaseOptionsMenu);
		databaseOptionsMenu.addActionListener(this);
		databaseMenu.add(m_jmiDbCleanupTool);
		m_jmiDbCleanupTool.addActionListener(this);
		m_jmFile.add(databaseMenu);

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

		// Beenden
		m_jmBeendenItem.addActionListener(this);
		m_jmFile.add(m_jmBeendenItem);

		m_jmMenuBar.add(m_jmFile);

		// Modules
		IModule[] activeModules = ModuleManager.instance().getModules(true);
		for (int i = 0; i < activeModules.length; i++) {
			if (activeModules[i].hasMainTab()) {
				JMenuItem showTabMenuItem = new JMenuItem(activeModules[i].getDescription());
				showTabMenuItem.setAccelerator(activeModules[i].getKeyStroke());
				showTabMenuItem.putClientProperty("MODULE", activeModules[i]);
				showTabMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						JMenuItem item = (JMenuItem) e.getSource();
						IModule module = (IModule) item.getClientProperty("MODULE");
						getTabbedPane().showTab(module.getModuleId());

					}
				});
				m_jmFunctions.add(showTabMenuItem);
			}
			if (activeModules[i].hasMenu()) {
				m_jmModules.add(activeModules[i].getMenu());
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

		// Adden
		this.setJMenuBar(m_jmMenuBar);
	}

	/**
	 * Proxyeinstellungen
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
	 * OptionsPanels for Modules
	 */
	public void addOptionPanel(String name, JPanel optionpanel) {
		m_vOptionPanels.add(optionpanel);
		m_vOptionPanelNames.add(name);
	}

	/**
	 * Reinit, set currency.
	 */
	@Override
	public void reInit() {
		// Die Währung auf die aus dem HRF setzen
		try {
			float faktorgeld = (float) HOVerwaltung.instance().getModel().getXtraDaten()
					.getCurrencyRate();

			if (faktorgeld > -1) {
				UserParameter.instance().faktorGeld = faktorgeld;
			}
		} catch (Exception e) {
			HOLogger.instance().log(HOMainFrame.class, "Währungsanpassung gescheitert!");
		}
	}

	// ------Refreshfunktionen-------------------------------

	/**
	 * Wird bei einer Datenänderung aufgerufen
	 */
	@Override
	public void refresh() {
		// nix?
	}

	// --------------------------------------------------------------
	public void showMatch(final int matchid) {
		m_jtpTabbedPane.showTab(IModule.MATCHES);
		final SpielePanel matchesPanel = (SpielePanel) getTabbedPane().getModulePanel(
				IModule.MATCHES);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				matchesPanel.showMatch(matchid);
			}
		});
	}

	// ----------------Hilfsmethoden---------------------------------

	/**
	 * Zeigt das Tab an (Nicht Index, sondern Konstante benutzen!
	 * 
	 * @param tabnumber
	 *            number of the tab to show
	 */
	public void showTab(int tabnumber) {
		m_jtpTabbedPane.showTab(tabnumber);
	}

	/**
	 * Set the default font size.
	 */
	private void setDefaultFont(int size) {
		try {
			boolean succ = false;
			if (UserParameter.instance().skin != null
					&& UserParameter.instance().skin.startsWith("JGoodies")) {
				succ = JGoodiesTheme.enableJGoodiesTheme(UserParameter.instance().skin, size);
			} else if ("System".equalsIgnoreCase(UserParameter.instance().skin)) {
				try {
					LookAndFeelInfo win = null;
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						if ("Windows".equals(info.getName())) {
							win = info;
							break;
						}
					}
					if (win != null) {
						HOLogger.instance().log(getClass(), "Use " + win.getName() + " l&f");
						UIManager.setLookAndFeel(win.getClassName());
					} else {
						HOLogger.instance().log(getClass(), "Use System l&f...");
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
					// TODO: font size
					SwingUtilities.updateComponentTreeUI(this);
					succ = true;
				} catch (Exception e) {
					succ = false;
				}
			} else if (!"Classic".equalsIgnoreCase(UserParameter.instance().skin)) {
				// Nimbus is the default theme
				succ = NimbusTheme.enableNimbusTheme(size);
			}
			if (!succ) {
				final MetalLookAndFeel laf = new MetalLookAndFeel();
				MetalLookAndFeel.setCurrentTheme(new HOTheme(
						UserParameter.instance().schriftGroesse));

				// Um die systemweite MenuBar von Mac OS X zu verwenden
				// http://www.pushing-pixels.org/?p=366
				if (System.getProperty("os.name").toLowerCase(java.util.Locale.ENGLISH)
						.startsWith("mac")) {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					Object mbUI = UIManager.get("MenuBarUI");
					Object mUI = UIManager.get("MenuUI");
					Object cbmiUI = UIManager.get("CheckBoxMenuItemUI");
					Object rbmiUI = UIManager.get("RadioButtonMenuItemUI");
					Object pmUI = UIManager.get("PopupMenuUI");

					UIManager.setLookAndFeel(laf);

					UIManager.put("MenuBarUI", mbUI);
					UIManager.put("MenuUI", mUI);
					UIManager.put("CheckBoxMenuItemUI", cbmiUI);
					UIManager.put("RadioButtonMenuItemUI", rbmiUI);
					UIManager.put("PopupMenuUI", pmUI);
				} else {
					UIManager.setLookAndFeel(laf);
				}
			}
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			HOLogger.instance().log(HOMainFrame.class, e);
		}
	}

	/**
	 * Holt die Parameter aus den Dialogen und speichert sie in der DB
	 */
	@SuppressWarnings("deprecation")
	private void saveUserParameter() {
		UserParameter parameter = UserParameter.instance();

		parameter.hoMainFrame_PositionX = Math.max(getLocation().x, 0);
		parameter.hoMainFrame_PositionY = Math.max(getLocation().y, 0);
		parameter.hoMainFrame_width = Math.min(getSize().width, getToolkit().getScreenSize().width
				- parameter.hoMainFrame_PositionX);
		parameter.hoMainFrame_height = Math.min(getSize().height,
				getToolkit().getScreenSize().height - parameter.hoMainFrame_PositionY);

		final IAufstellungsAssistentPanel aap = getAufstellungsPanel()
				.getAufstellungsAssistentPanel();

		parameter.bestPostWidth = Math.max(getSpielerUebersichtPanel().getBestPosWidth(),
				getAufstellungsPanel().getBestPosWidth());

		parameter.aufstellungsAssistentPanel_gruppe = AufstellungsAssistentPanelNew.asString(aap
				.getGroups());
		parameter.aufstellungsAssistentPanel_reihenfolge = aap.getOrder();
		parameter.aufstellungsAssistentPanel_not = aap.isNotGroup();
		parameter.aufstellungsAssistentPanel_cbfilter = aap.isGroupFilter();
		parameter.aufstellungsAssistentPanel_idealPosition = aap.isIdealPositionZuerst();
		parameter.aufstellungsAssistentPanel_form = aap.isConsiderForm();
		parameter.aufstellungsAssistentPanel_verletzt = aap.isIgnoreInjured();
		parameter.aufstellungsAssistentPanel_gesperrt = aap.isIgnoreSuspended();
		parameter.aufstellungsAssistentPanel_notLast = aap.isExcludeLastMatch();

		// SpielerÜbersichtsPanel
		if (getTabbedPane().isModuleTabVisible(IModule.PLAYEROVERVIEW)) {
			final int[] sup = getSpielerUebersichtPanel().getDividerLocations();
			parameter.spielerUebersichtsPanel_horizontalLeftSplitPane = sup[0];
			parameter.spielerUebersichtsPanel_horizontalRightSplitPane = sup[1];
			parameter.spielerUebersichtsPanel_verticalSplitPane = sup[2];
			getSpielerUebersichtPanel().saveColumnOrder();
		}

		// AufstellungsPanel
		if (getTabbedPane().isModuleTabVisible(IModule.LINEUP)) {
			final int[] ap = getAufstellungsPanel().getDividerLocations();
			parameter.aufstellungsPanel_verticalSplitPaneLow = ap[0];
			parameter.aufstellungsPanel_horizontalLeftSplitPane = ap[1];
			parameter.aufstellungsPanel_horizontalRightSplitPane = ap[2];
			parameter.aufstellungsPanel_verticalSplitPane = ap[3];
			getAufstellungsPanel().saveColumnOrder();
		}

		// TransferScoutPanel
		if (getTabbedPane().isModuleTabVisible(IModule.TRANSFERS)) {
			final int tsp = getTransferScoutPanel().getScoutPanel().getDividerLocation();
			parameter.transferScoutPanel_horizontalSplitPane = tsp;
		}

		DBManager.instance().saveUserParameter();
	}


	public static void setHOStatus(int i) {
		status = i;
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
				if (isAppTerminated) {
					System.exit(0);
				}
			}

			/**
			 * Close HO window.
			 */
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				shutdown();
			}

		});

		// Catch Apple-Q for MacOS
		if (isMac()) {
			addMacOSListener();
		}
	}
}
