package core.gui

import core.HO
import core.db.DBManager
import core.db.backup.BackupDialog
import core.file.hrf.HRFImport
import core.gui.comp.panel.ImagePanel
import core.gui.comp.tabbedPane.HOTabbedPane
import core.gui.theme.HOIconName
import core.gui.theme.ImageUtilities
import core.gui.theme.ThemeManager
import core.model.FormulaFactors
import core.model.HOVerwaltung
import core.model.UserParameter
import core.model.match.Weather
import core.model.player.Player
import core.module.IModule
import core.module.ModuleManager
import core.module.config.ModuleConfig
import core.net.DownloadDialog
import core.net.MyConnector
import core.net.login.ProxySettings
import core.option.OptionenDialog
import core.option.db.UserAdministrationDialog
import core.util.*
import module.lineup.LineupMasterView
import module.lineup.LineupPanel
import module.matches.MatchesPanel
import module.playerOverview.PlayerOverviewPanel
import module.playeranalysis.PlayerAnalysisModulePanel
import module.transfer.TransfersPanel
import tool.ToolManager
import tool.dbcleanup.DBCleanupTool
import tool.updater.UpdateController
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.net.URI
import java.sql.Date
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.*
import kotlin.math.max
import kotlin.math.min

/**
 * The Main HO window
 */
object HOMainFrame : JFrame(), Refreshable {
    var isHOMainFrame_initialized = false
        private set

    @JvmField
    var launching = AtomicBoolean(false)

    var weather: Weather = Weather.PARTIALLY_CLOUDY

    private val infoPanel: InfoPanel = InfoPanel()

    // Components
    var tabbedPane: HOTabbedPane? = null
        private set

    /**
     * Get all option panel names.
     */
    val optionPanelNames = Vector<String>()

    /**
     * Get all option panels.
     */
    val optionPanels = Vector<JPanel>()
    private val isAppTerminated = AtomicBoolean(false) // set when HO should be terminated
    private val applicationClosingListener: MutableList<ApplicationClosingListener> = ArrayList()

    // TODO: Should this go into theming?
    // Menu color depending of version
    private val c_beta = Color(162, 201, 255)
    private val c_dev = Color(235, 170, 170)

    // TODO: Fix this dependency on internal modules
    var selectedPlayer: Player? = null
        private set


    val playerAnalysisMainPanel: PlayerAnalysisModulePanel
        get() = tabbedPane!!.getModulePanel(IModule.PLAYERANALYSIS) as PlayerAnalysisModulePanel
    val playerOverviewPanel: PlayerOverviewPanel
        get() = tabbedPane!!.getModulePanel(IModule.PLAYEROVERVIEW) as PlayerOverviewPanel
    val transferScoutPanel: TransfersPanel
        /**
         * Get the transfer scout panel.
         */
        get() = tabbedPane!!.getModulePanel(IModule.TRANSFERS) as TransfersPanel

    val lineupPanel: LineupPanel?
        get() {
            val c: Container? = tabbedPane!!.getModulePanel(IModule.LINEUP)
            return if (c is LineupPanel) {
                c
            } else if (c != null) {
                (c as LineupMasterView).lineupPanel
            } else {
                null
            }
        }

    init {
        launching.set(true)
        if (OSUtils.isMac()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true")
            System.setProperty("apple.awt.showGroupBox", "true")
        }

        // Log HO! version
        HOLogger.instance().info(javaClass,
                "This is HO! version " + HO.getVersionString() + ", have fun!")

        // Log Operating System
        HOLogger.instance().info(
                javaClass,
                "Operating system found: " + System.getProperty("os.name") + " on "
                        + System.getProperty("os.arch") + " (" + System.getProperty("os.version")
                        + ")")

        // Log Java version
        HOLogger.instance().info(
                javaClass,
                "Using java: " + System.getProperty("java.version") + " ("
                        + System.getProperty("java.vendor") + ")")
        RefreshManager.instance().registerRefreshable(this)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        SwingUtilities.updateComponentTreeUI(this)
        setFrameTitle()
        setFrameIconImage()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
        addListeners()
        initProxy()
        initComponents()
        initMenu()
        RefreshManager.instance().doRefresh()

        val lineup = lineupPanel
        weather = if (lineup != null) lineup.weather else Weather.NULL

        launching.set(false)
    }

    private fun setFrameTitle() {
        val teamName = HOVerwaltung.instance().model.getBasics().teamName
        var frameTitle = if (StringUtils.isEmpty(teamName)) "" else teamName
        if (!HO.isRelease()) {
            frameTitle += " (${HOVerwaltung.instance().getLanguageString("ls.java.version")}: ${System.getProperty("java.version")})"
        }
        setTitle(frameTitle)
    }

    private fun setFrameIconImage() {
        var iconName = HOIconName.LOGO16_STABLE
        if (!HO.isRelease()) {
            iconName = "${HOIconName.LOGO16}_${HO.getVersionType().lowercase(Locale.getDefault())}"
        }
        var iconImage = ImageUtilities.iconToImage(ThemeManager.getIcon(iconName))
        if (OSUtils.isMac()) {
            try {
                val taskbar = Taskbar.getTaskbar()
                taskbar.setIconImage(iconImage)
            } catch (e: UnsupportedOperationException) {
                HOLogger.instance().error(HOMainFrame::class.java, "OS doesn't support operation: " + e.message)
            }
        }
    }

    fun addApplicationClosingListener(listener: ApplicationClosingListener) {
        if (!applicationClosingListener.contains(listener)) {
            applicationClosingListener.add(listener)
        }
    }

    private fun fireApplicationClosing() {
        for (listener in applicationClosingListener) {
            try {
                listener.applicationClosing()
            } catch (ex: Exception) {
                ExceptionDialog("Error", ex)
            }
        }
    }

    // TODO: Fix dependency on internal modules.
    fun selectPlayer(player: Player) {
        if (selectedPlayer !== player) {
            selectedPlayer = player
            val lineupPanel = lineupPanel
            lineupPanel?.setPlayer(player.playerID)
            playerOverviewPanel.setPlayer(player)
        }
    }

    fun setWaitInformation() = setInformation(HOVerwaltung.instance().getLanguageString("BitteWarten"), 1)

    fun resetInformation() = setInformation("", -100)

    fun setInformation(information: String) = setInformation(information, -100)

    fun setInformationCompleted() = setInformation(Helper.getTranslation("ls.update_status.complete"), 100)


    fun setInformation(information: String, progress: Int) {
        if (launching.get()) return
        infoPanel.setInformation(information, progress)
    }

    fun setInformation(information: String, color: Color) {
        if (launching.get()) return
        infoPanel.setInformation(information, color)
    }

    fun updateProgress(progress: Int) {
        if (launching.get()) return
        infoPanel.setProgressbarValue(progress)
    }

    private fun openURL(url: String) {
        try {
            BrowserLauncher.openURL(url)
        } catch (ex: Exception) {
            HOLogger.instance().log(HOMainFrame::class.java, ex)
        }
    }

    /**
     * closing HO
     */
    fun shutdown() {
        CursorToolkit.startWaitCursor(getRootPane())
        try {
            fireApplicationClosing()

            // TODO: instead of calling XY.instance().save() from here, those classes should register an ApplicationClosingListener
            HOLogger.instance().debug(javaClass, "Shutting down HO!")
            // save current UserParameter
            saveUserParameter()
            HOLogger.instance().debug(javaClass, "UserParameters saved")
            // Save scout list
            transferScoutPanel.scoutPanel.saveScoutListe()
            HOLogger.instance().debug(javaClass, "ScoutList saved")
            // Save formula factors
            FormulaFactors.instance().save()
            HOLogger.instance().debug(javaClass, "FormulaFactors saved")
            // Save module configs
            ModuleConfig.instance().save()
            HOLogger.instance().debug(javaClass, "Module configurations saved")
            // Disconnect
            DBManager.instance().disconnect()
            HOLogger.instance().debug(javaClass, "Disconnected")
            isAppTerminated.set(true) // enable System.exit in windowClosed()
            HOLogger.instance().info(javaClass, "Shutdown complete! isAppTerminated: " + isAppTerminated.get())

            // Dispose makes frame windowClosed as soon as all modules windowClosing all complete.
            try {
                dispose()
            } catch (ignored: Exception) {
            }
        } catch (ee: Exception) {
            HOLogger.instance().error(javaClass, ee)
        } finally {
            CursorToolkit.stopWaitCursor(getRootPane())
        }
    }

    /**
     * Builds frame.
     */
    fun initComponents() {
        ToolTipManager.sharedInstance().dismissDelay = 5000
        if (HO.isDevelopment()) {
            contentPane.setBackground(c_dev)
        } else if (HO.isBeta()) {
            contentPane.setBackground(c_beta)
        } else {
            contentPane = ImagePanel()
        }
        contentPane.setLayout(BorderLayout())
        tabbedPane = HOTabbedPane()
        val activeModules = ModuleManager.instance().getModules(true)
        for (module in activeModules) {
            if (module.hasMainTab() && module.isStartup()) {
                tabbedPane!!.showTab(module.getModuleId())
            }
        }
        contentPane.add(tabbedPane, BorderLayout.CENTER)
        if (tabbedPane!!.tabCount > 0) {
            tabbedPane!!.setSelectedIndex(0)
        }
        contentPane.add(infoPanel, BorderLayout.SOUTH)
        setLocation(UserParameter.instance().hoMainFrame_PositionX,
                UserParameter.instance().hoMainFrame_PositionY)
        setSize(UserParameter.instance().hoMainFrame_width,
                UserParameter.instance().hoMainFrame_height)
    }

    /**
     * Initialize the menu.
     */
    private fun initMenu() {
        // No F10!
        (UIManager.get("Table.ancestorInputMap") as InputMap).remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0))
        val jmFile = JMenu(HOVerwaltung.instance().getLanguageString("ls.menu.file")) //File
        val jmFunctions = JMenu(HOVerwaltung.instance().getLanguageString("ls.menu.functions")) //Functions
        val jmModules = JMenu(HOVerwaltung.instance().getLanguageString("ls.menu.modules")) //Modules
        val jmHelp = JMenu(HOVerwaltung.instance().getLanguageString("ls.menu.help")) //Help

        // Download CHPP data
        val jmDownloadItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.download"))
        jmDownloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0))
        jmDownloadItem.addActionListener { _ -> DownloadDialog.instance() }
        jmFile.add(jmDownloadItem)

        // Import HRF
        val jmImportItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.importfromhrf"))
        jmImportItem.addActionListener { _ -> HRFImport(this) }
        jmFile.add(jmImportItem)
        jmFile.addSeparator()
        val dbBackupMenu = JMenuItem("DB Backup")
        dbBackupMenu.addActionListener { _ ->
            BackupDialog().isVisible = true
        }
        jmFile.add(dbBackupMenu)
        jmFile.addSeparator()

        // Subskill recalculation
        val subSkillRecalcFullItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation"))
        subSkillRecalcFullItem.addActionListener { _->
            val from = HODateTime.now().minus(64 * 7, ChronoUnit.DAYS)
            if (JOptionPane.showConfirmDialog(this, Helper.getTranslation("SubskillRecalcFull"),
                            Helper.getTranslation("ls.menu.file.subskillrecalculation"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
                HOVerwaltung.instance().recalcSubskills(true, from.toDbTimestamp())
            }
        }
        jmFile.add(subSkillRecalcFullItem)
        val subSkillRecalc7WeeksItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation7weeks"))
        subSkillRecalc7WeeksItem.addActionListener { _ ->
            val from = HODateTime.now().minus(7 * 7, ChronoUnit.DAYS)
            if (JOptionPane.showConfirmDialog(this, Helper.getTranslation("subskillRecalc7w", Date.from(from.instant)),
                            Helper.getTranslation("ls.menu.file.subskillrecalculation7weeks"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
                HOVerwaltung.instance().recalcSubskills(true, from.toDbTimestamp())
            }
        }
        jmFile.add(subSkillRecalc7WeeksItem)
        jmFile.addSeparator()

        // Options
        val jmOptionen = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.preferences"))
        jmOptionen.addActionListener { _ -> OptionenDialog(this).isVisible = true }
        jmFile.add(jmOptionen)
        val userAdministrationOptionsItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.database.dbuseradministration"))
        userAdministrationOptionsItem.addActionListener { _ -> UserAdministrationDialog(this).isVisible = true }
        val jmDbCleanupTool = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.database.databasecleanup"))
        jmDbCleanupTool.addActionListener { _ -> DBCleanupTool().showDialog(this) }
        jmFile.add(userAdministrationOptionsItem)
        jmFile.add(jmDbCleanupTool)
        jmFile.addSeparator()

        // Toggle full screen mode
        val jmFullScreenItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.fullscreen"))
        if (FullScreen.instance().isFullScreenSupported(this)) {
            jmFullScreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11,
                    KeyEvent.SHIFT_DOWN_MASK))
        } else {
            jmFullScreenItem.setEnabled(false)
        }
        jmFullScreenItem.addActionListener { _ -> FullScreen.instance().toggle(this) }
        jmFile.add(jmFullScreenItem)
        jmFile.addSeparator()

        // Quit
        val jmQuitItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.quit"))
        jmQuitItem.addActionListener { _ ->
            // Restore normal window mode (i.e. leave full screen)
            FullScreen.instance().restoreNormalMode(this)
            // Fire CloseEvent, so all Modules get informed
            processWindowEvent(WindowEvent(this, WindowEvent.WINDOW_CLOSING))
        }
        jmFile.add(jmQuitItem)

        // Modules
        val activeModules = ModuleManager.instance().getModules(true)
        for (activeModule in activeModules) {
            if (activeModule.hasMainTab()) {
                val showTabMenuItem = JMenuItem(activeModule.getDescription())
                showTabMenuItem.setAccelerator(activeModule.getKeyStroke())
                showTabMenuItem.putClientProperty("MODULE", activeModule)
                showTabMenuItem.addActionListener { e:ActionEvent ->
                    val item = e.source as JMenuItem
                    val module = item.getClientProperty("MODULE") as IModule
                    tabbedPane!!.showTab(module.getModuleId())
                    RefreshManager.instance().doRefresh()
                }
                jmFunctions.add(showTabMenuItem)
            }
            if (activeModule.hasMenu()) {
                jmModules.add(activeModule.getMenu())
            }
        }

        // Help =========================================================================
        val jmHomepageItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.help.projecthomepage"))
        jmHomepageItem.addActionListener { _ -> openURL("https://ho-dev.github.io/HattrickOrganizer/") } //   Help | HomePage
        jmHelp.add(jmHomepageItem)
        val jmWikiItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.help.help"))
        jmWikiItem.addActionListener { _ -> openURL("https://ho.readthedocs.io/") } //   Help | Wiki
        jmHelp.add(jmWikiItem)
        val jmReportABugItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.help.reportabug"))
        jmReportABugItem.addActionListener { _ -> openURL("https://github.com/ho-dev/HattrickOrganizer/issues/new/choose") } //   Help | Report a bug
        jmHelp.add(jmReportABugItem)
        jmHelp.addSeparator()
        val jmCheckUpdateItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.file.update.ho"))
        jmCheckUpdateItem.addActionListener { _ -> UpdateController.check4update(true) }
        jmHelp.add(jmCheckUpdateItem) // Help | check update
        val jmChangelogItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.help.changelog"))
        jmChangelogItem.addActionListener { _ ->
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    val jarFile = File(this.javaClass.getProtectionDomain().codeSource.location.toURI())
                    val logFile: URI = if (!OSUtils.isMac()) {
                        jarFile.getParentFile().toPath().resolve("changelog.html").toUri()
                    } else {
                        jarFile.getParentFile().getParentFile().getParentFile().toPath().resolve("changelog.html").toUri()
                    }
                    Desktop.getDesktop().browse(logFile)
                } catch (ee: Exception) {
                    JOptionPane.showMessageDialog(this,
                            HOVerwaltung.instance().getLanguageString("Changelog.error"),
                            HOVerwaltung.instance().getLanguageString("Fehler"),
                            JOptionPane.ERROR_MESSAGE
                    )
                    HOLogger.instance().error(HOMainFrame::class.java, "Error opening changelog: " + ee.message)
                }
            }
        }
        jmHelp.add(jmChangelogItem) // Help | changelog
        jmHelp.addSeparator()
        val jmAboutItem = JMenuItem(HOVerwaltung.instance().getLanguageString("ls.menu.help.about"))
        jmAboutItem.addActionListener { _ -> Credits.showCredits(this) } // Help | About
        jmHelp.add(jmAboutItem)

        // add Top Level Menus
        val jmMenuBar = JMenuBar()
        jmMenuBar.add(jmFile)
        jmMenuBar.add(jmFunctions)
        jmMenuBar.add(ToolManager().getToolMenu())
        jmMenuBar.add(jmModules)
        jmMenuBar.add(jmHelp)
        if (!HO.isRelease()) {
            jmMenuBar.add(DebugMode.getDeveloperMenu())
        }
        SwingUtilities.updateComponentTreeUI(jmMenuBar)
        this.jMenuBar = jmMenuBar
    }

    /**
     * Proxy setup.
     */
    private fun initProxy() {
        if (UserParameter.instance().ProxyAktiv) {
            val settings = ProxySettings()
            settings.proxyHost = UserParameter.instance().ProxyHost
            settings.isUseProxy = UserParameter.instance().ProxyAktiv
            if (!StringUtils.isEmpty(UserParameter.instance().ProxyPort)) {
                settings.proxyPort = UserParameter.instance().ProxyPort.toInt()
            }
            settings.isAuthenticationNeeded = UserParameter.instance().ProxyAuthAktiv
            settings.username = UserParameter.instance().ProxyAuthName
            settings.password = UserParameter.instance().ProxyAuthPassword
            MyConnector.instance().enableProxy(settings)
        }
    }

    /**
     * Reinit, set currency.
     */
    override fun reInit() {
        // Set the currency from the HRF file.
        try {
            val xtra = HOVerwaltung.instance().model.getXtraDaten() ?: return
            val fxRate = xtra.currencyRate.toFloat()
            if (fxRate > -1) {
                UserParameter.instance().FXrate = fxRate
            }
        } catch (e: Exception) {
            HOLogger.instance().log(HOMainFrame::class.java, "Currency changed failed! " + e.message)
        }
    }

    /**
     * Called when data is changed.
     */
    override fun refresh() {
        // nix?
    }

    // --------------------------------------------------------------
    fun showMatch(matchId: Int) {
        tabbedPane!!.showTab(IModule.MATCHES)
        val matchesPanel = tabbedPane!!.getModulePanel(IModule.MATCHES) as MatchesPanel
        SwingUtilities.invokeLater {
            matchesPanel.showMatch(matchId)
        }
    }
    // ----------------Helper methods---------------------------------
    /**
     * Shows the tab associated with module with ID `moduleId`.
     *
     * @param moduleId
     * ID of the module whose tab should be shown.
     */
    fun showTab(moduleId: Int) {
        tabbedPane!!.showTab(moduleId)
    }

    /**
     * Gets the current user parameters, and stores them in the database.
     */
    private fun saveUserParameter() {
        val parameter = UserParameter.instance()
        parameter.hoMainFrame_PositionX = max(location.x.toDouble(), 0.0).toInt()
        parameter.hoMainFrame_PositionY = max(location.y.toDouble(), 0.0).toInt()
        val currentDevice = this.graphicsConfiguration.device
        parameter.hoMainFrame_width = min(size.width.toDouble(),
                (toolkit.screenSize.width - parameter.hoMainFrame_PositionX + currentDevice.defaultConfiguration.bounds.x).toDouble()).toInt()
        parameter.hoMainFrame_height = min(size.height.toDouble(),
                (toolkit.screenSize.height - parameter.hoMainFrame_PositionY + currentDevice.defaultConfiguration.bounds.y).toDouble()).toInt()
        val lineupPanel = lineupPanel!!
        parameter.aufstellungsAssistentPanel_gruppe = lineupPanel.assistantGroup
        parameter.aufstellungsAssistentPanel_reihenfolge = lineupPanel.assistantOrder
        parameter.lineupAssistentPanel_include_group = !lineupPanel.isAssistantSelectedGroupExcluded
        parameter.aufstellungsAssistentPanel_cbfilter = lineupPanel.isAssistantGroupFilter
        parameter.aufstellungsAssistentPanel_idealPosition = lineupPanel.isAssistantBestPositionFirst
        parameter.aufstellungsAssistentPanel_form = lineupPanel.isAssistantConsiderForm
        parameter.aufstellungsAssistentPanel_verletzt = lineupPanel.isAssistantIgnoreInjured
        parameter.aufstellungsAssistentPanel_gesperrt = lineupPanel.isAssistantIgnoreSuspended
        parameter.aufstellungsAssistentPanel_notLast = lineupPanel.isAssistantExcludeLastMatch

        // PlayerOverviewPanel
        if (tabbedPane!!.isModuleTabVisible(IModule.PLAYEROVERVIEW)) {
            val sup = playerOverviewPanel.getDividerLocations()
            parameter.spielerUebersichtsPanel_horizontalLeftSplitPane = sup[0]
            parameter.spielerUebersichtsPanel_horizontalRightSplitPane = sup[1]
            parameter.spielerUebersichtsPanel_verticalSplitPane = sup[2]
            playerOverviewPanel.saveColumnOrder()
        }

        // Lineup Panel
        if (tabbedPane!!.isModuleTabVisible(IModule.LINEUP)) {
            val ap = this.lineupPanel!!.getDividerLocations()
            parameter.lineupPanel_verticalSplitLocation = ap[0]
            parameter.lineupPanel_horizontalSplitLocation = ap[1]
            this.lineupPanel!!.saveColumnOrder()
        }

        // TransferScoutPanel
        if (tabbedPane!!.isModuleTabVisible(IModule.TRANSFERS)) {
            parameter.transferScoutPanel_horizontalSplitPane = transferScoutPanel.scoutPanel.dividerLocation
        }
        for (module in ModuleManager.instance().getModules(true)) {
            module.storeUserSettings()
        }
        DBManager.instance().saveUserParameter()
    }

    private fun addListeners() {
        addWindowListener(object : WindowAdapter() {
            /**
             * Finally shutting down the application when the main window is
             * closed. This is initiated through the call to dispose().
             * System.exit is called only in the case when @see shutdown() is
             * called in advance. This event is called when switching into full
             * screen mode, too.
             *
             * @param windowEvent
             * is ignored
             */
            override fun windowClosed(windowEvent: WindowEvent) {
                HOLogger.instance().info(javaClass, "exiting...")
                if (isAppTerminated.get()) {
                    System.exit(0)
                } else {
                    HOLogger.instance().warning(javaClass, "The app is not fully terminated yet.")
                }
            }

            /**
             * Close HO window.
             */
            override fun windowClosing(windowEvent: WindowEvent) {
                HOLogger.instance().info(javaClass, "shutting down HO... ")
                shutdown()
            }
        })
    }
}
