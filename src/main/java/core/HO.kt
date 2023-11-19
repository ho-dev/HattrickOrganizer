package core

import core.db.DBManager.firstStart
import core.db.DBManager.loadUserParameter
import core.db.DBManager.updateConfig
import core.db.backup.BackupHelper.backup
import core.db.user.UserManager.getCurrentUser
import core.db.user.UserManager.index
import core.db.user.UserManager.isSingleUser
import core.db.user.UserManager.users
import core.gui.HOMainFrame
import core.gui.SplashFrame
import core.gui.model.UserColumnController
import core.gui.theme.ImageUtilities
import core.gui.theme.ThemeManager
import core.model.HOVerwaltung
import core.model.UserParameter
import core.option.InitOptionsDialog
import core.training.TrainingManager
import core.util.ExceptionHandler
import core.util.HOLogger
import core.util.OSUtils
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.io.File
import java.text.NumberFormat
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*

object HO {
    @JvmField
	var VERSION = 0.0 // Version is set in build.gradle and exposed to HO via the manifest
    var revisionNumber = 0
    @JvmStatic
	var versionType: String? = null
        private set
    var platform: OSUtils.OS? = null
        private set
    var isPortableVersion = false // Used to determine the location of the DB

    @JvmStatic
	val development: Boolean
        get() = "DEV".equals(versionType, ignoreCase = true)
    @JvmStatic
	val beta: Boolean
        get() = "BETA".equals(versionType, ignoreCase = true)
    val release: Boolean
        get() = "RELEASE".equals(versionType, ignoreCase = true)


	fun getVersionString(): String {
            val nf = NumberFormat.getInstance(Locale.US)
            nf.setMinimumFractionDigits(1)
            var txt = nf.format(VERSION)
            if (beta) {
                txt += " BETA (r$revisionNumber)"
            } else if (development) {
                txt += " DEV (r$revisionNumber)"
            }
            return txt
        }

    /**
     * HO entry point
     */
    @JvmStatic
    fun main(args: Array<String>) {
        var tmpArgs: Array<String>? = args
        isPortableVersion = true
        platform = OSUtils.getOS()
        if (platform == OSUtils.OS.MAC) {
            System.setProperty("apple.laf.useScreenMenuBar", "true")
            System.setProperty("apple.awt.showGroupBox", "true")
            System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS")
        }
        System.setProperty("sun.awt.exception.handler", ExceptionHandler::class.java.getName())
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler())
        if (tmpArgs != null) {
            tmpArgs = tmpArgs.map { s -> s.uppercase(Locale.getDefault()) }.toTypedArray()
            if (Arrays.asList(*tmpArgs).contains("INSTALLED")) {
                isPortableVersion = false
            }
            var arg: String
            for (_arg in tmpArgs) {
                arg = _arg.trim { it <= ' ' }.uppercase(Locale.getDefault())
                when (arg) {
                    "INFO" -> HOLogger.instance().logLevel = HOLogger.INFORMATION
                    "DEBUG" -> HOLogger.instance().logLevel = HOLogger.DEBUG
                    "WARNING" -> HOLogger.instance().logLevel = HOLogger.WARNING
                    "ERROR" -> HOLogger.instance().logLevel = HOLogger.ERROR
                }
            }
        }

        // Get HO version from manifest
        val sVERSION = HO::class.java.getPackage().implementationVersion
        if (sVERSION != null) {
            val aVersion = sVERSION.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            VERSION = (aVersion[0] + "." + aVersion[1]).toDouble()
            revisionNumber = aVersion[2].toInt()
            versionType = when (aVersion[3]) {
                "0" -> "DEV"
                "1" -> "BETA"
                else -> "RELEASE"
            }
            HOLogger.instance().info(
                HO::class.java,
                "VERSION: $VERSION   versionType:  $versionType   RevisionNumber: $revisionNumber"
            )
        } else {
            HOLogger.instance().error(HO::class.java, "Launched from IDE otherwise there is a bug !")
            VERSION = 0.0
            versionType = "DEV"
        }

        // Login selection in case of multi-users DB
        try {
            if (!isSingleUser()) {
                val options = createOptionsArray()
                val choice = JOptionPane.showOptionDialog(
                    null,
                    "",
                    "Login",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    null
                )
                if (choice != JOptionPane.CLOSED_OPTION) {
                    index = choice
                } else {
                    System.exit(0)
                }
            }
        } catch (ex: Exception) {
            HOLogger.instance().log(HO::class.java, ex)
        }

        // start display splash image
        val interruptionsWindow = SplashFrame()

        // Backup
        interruptionsWindow.setInfoText(1, "Backup Database")
        backup(File(getCurrentUser().dbFolder))


        // Load user parameters from the DB
        interruptionsWindow.setInfoText(2, "Initialize Database")
        loadUserParameter()

        // init Theme
        try {
            ThemeManager.instance().setCurrentTheme()
        } catch (e: Exception) {
            HOLogger.instance().log(HO::class.java, "Can´t load Theme:" + UserParameter.instance().theme)
            JOptionPane.showMessageDialog(
                null, e.message, "Can´t load Theme: "
                        + UserParameter.instance().theme, JOptionPane.WARNING_MESSAGE
            )
        }
        // Init!
        interruptionsWindow.setInfoText(3, "Initialize Data-Administration")

        // Ask for language at first start
        if (firstStart) {
            interruptionsWindow.isVisible = false
            InitOptionsDialog()
            interruptionsWindow.isVisible = true
        }

        // Check if language file available
        interruptionsWindow.setInfoText(4, "Check Languagefiles")
        HOVerwaltung.checkLanguageFile(UserParameter.instance().sprachDatei)
        HOVerwaltung.instance().setResource(UserParameter.instance().sprachDatei)
        if (firstStart) {
            interruptionsWindow.isVisible = false
            JOptionPane.showMessageDialog(
                null,
                HOVerwaltung.instance().getLanguageString("firststartup.infowinmessage"),
                HOVerwaltung.instance().getLanguageString("firststartup.infowinmessage.title"),
                JOptionPane.INFORMATION_MESSAGE
            )
            interruptionsWindow.isVisible = true
        }
        interruptionsWindow.setInfoText(5, "Load latest Data")
        HOVerwaltung.instance().loadLatestHoModel()
        interruptionsWindow.setInfoText(6, "Load  XtraDaten")

        // Load table columns information
        UserColumnController.instance().load()

        // Set the currency from HRF
        val model = HOVerwaltung.instance().model
        if (model != null) {
            val xtra = HOVerwaltung.instance().model.getXtraDaten()
            if (xtra != null) {
                val fxRate = xtra.currencyRate.toFloat()
                if (fxRate > -1) UserParameter.instance().FXrate = fxRate
            }
        }
        // Upgrade database configuration
        if (!firstStart) {
            interruptionsWindow.setInfoText(7, "Upgrade DB configuration")
            updateConfig()
        }


        // Training
        interruptionsWindow.setInfoText(8, "Initialize Training")

        // Training estimation calculated on DB manual entries
        TrainingManager.instance()
        interruptionsWindow.setInfoText(9, "Prepare to show")
        SwingUtilities.invokeLater {
            HOMainFrame.isVisible = true

            // stop display splash image
            interruptionsWindow.isVisible = false
            interruptionsWindow.dispose()
        }
    }

    private fun createOptionsArray(): Array<Any> {
        val buttons = ArrayList<JButton>()
        var keyEvent = KeyEvent.VK_1
        for (user in users) {
            buttons.add(createIconButton(user.teamName, user.clubLogo, keyEvent++))
        }
        return buttons.toTypedArray()
    }

    private fun createIconButton(teamName: String?, iconPath: String?, keyEvent: Int): JButton {
        val width = 210
        val height = (width * 26.0 / 21.0).toInt()
        val scaledIcon: Icon? = try {
            val buttonIcon = ImageIO.read(File(iconPath))
            val icon = ImageIcon(buttonIcon)
            ImageUtilities.getScaledIcon(icon, width, height)
        } catch (exception: Exception) {
            ImageIcon()
        }
        val ret = JButton(teamName, scaledIcon)
        ret.setVerticalTextPosition(AbstractButton.BOTTOM)
        ret.setHorizontalTextPosition(AbstractButton.CENTER)
        ret.setMnemonic(keyEvent)
        ret.addActionListener { evt: ActionEvent ->
            val pane = getOptionPane(evt.source as JComponent)
            // set the value of the option pane
            pane.setValue(ret)
            val w = SwingUtilities.getWindowAncestor(ret)
            if (w != null) {
                w.isVisible = false
            }
        }
        return ret
    }

    private fun getOptionPane(source: JComponent): JOptionPane {
        var ret = source
        while (ret !is JOptionPane) {
            ret = ret.parent as JComponent
        }
        return ret
    }
}
