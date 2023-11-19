package core.gui

import core.HO.getVersionString
import core.db.user.UserManager.dbParentFolder
import core.db.user.UserManager.getCurrentUser
import core.gui.comp.HyperLinkLabel
import core.gui.theme.HOIconName
import core.gui.theme.ThemeManager
import core.model.HOVerwaltung
import core.util.BrowserLauncher
import core.util.HOLogger
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel

object Credits {
    fun showCredits(parent: Component?) {
        val creditsPanel = JPanel(GridBagLayout())
        var gbc = GridBagConstraints()
        gbc.anchor = GridBagConstraints.NORTHWEST
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 2
        creditsPanel.add(JLabel("Hattrick Organizer " + getVersionString()), gbc)
        gbc.gridy = 1
        creditsPanel.add(JLabel(" "), gbc)
        gbc.gridy = 2
        val text = HOVerwaltung.instance().getLanguageString("window.about.text").split("\\n".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (line in text) {
            creditsPanel.add(JLabel(line), gbc)
            gbc.gridy++
        }
        creditsPanel.add(JLabel(" "), gbc)
        gbc.gridy++
        val lines = gbc.gridy + 1
        gbc.gridy = 0
        gbc.gridx = 2
        gbc.gridheight = lines
        gbc.anchor = GridBagConstraints.NORTHEAST
        gbc.insets = Insets(0, 15, 0, 0)
        creditsPanel.add(JLabel(ThemeManager.getIcon(HOIconName.CHPP_WHITE_BG)), gbc)
        val hoPanel = JPanel(GridBagLayout())
        gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.anchor = GridBagConstraints.NORTHWEST
        val hoLabel = JLabel("HO website: ")
        hoPanel.add(hoLabel, gbc)
        gbc.gridx = 1
        val linkLabel: JLabel = HyperLinkLabel("https://github.com/ho-dev/HattrickOrganizer/")
        hoPanel.add(linkLabel, gbc)
        gbc.gridy = 4
        gbc.gridx = 0
        hoPanel.add(JLabel(HOVerwaltung.instance().getLanguageString("ls.about.database.folder") + ": "), gbc)
        gbc.gridx = 1
        hoPanel.add(HyperLinkLabel(File(dbParentFolder + "\\" + getCurrentUser().dbName).toURI().toString()), gbc)
        gbc.gridy++
        gbc.gridx = 0
        hoPanel.add(JLabel(HOVerwaltung.instance().getLanguageString("ls.about.logs.folder") + ": "), gbc)
        gbc.gridx = 1
        hoPanel.add(HyperLinkLabel(File(HOLogger.getLogsFolderName()).toURI().toString()), gbc)
        creditsPanel.add(hoPanel, gbc)
        val options1 = arrayOf<Any>(
            HOVerwaltung.instance().getLanguageString("window.about.licence"),
            HOVerwaltung.instance().getLanguageString("ls.button.ok")
        )
        val result = JOptionPane.showOptionDialog(
            parent,
            creditsPanel,
            HOVerwaltung.instance().getLanguageString("window.about.title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options1,
            null
        )
        if (result == JOptionPane.YES_OPTION) {
            try {
                BrowserLauncher.openURL("https://raw.githubusercontent.com/ho-dev/HattrickOrganizer/master/LICENSE")
            } catch (ex: Exception) {
                HOLogger.instance().log(HOMainFrame::class.java, ex)
            }
        }
    }
}
