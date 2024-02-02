package tool.dbcleanup

import core.gui.HOMainFrame
import core.model.HOVerwaltung
import java.awt.*
import javax.swing.*

/**
 * Database Cleanup Dialog
 *
 * @author flattermann <HO@flattermann.net>
 */
internal class DBCleanupDialog(owner: JFrame?, private val cleanupTool: DBCleanupTool) : JDialog(
    owner,
    HOVerwaltung.instance().getLanguageString("ls.menu.file.database.databasecleanup"),
    true
) {
    private val mainPanelOwnMatches = WeekSelectionPanel(DBCleanupTool.REMOVE_NONE)
    private val mainPanelOtherMatches = WeekSelectionPanel(16)

    private val ownMatchesTypeSelectionPanel = MatchTypeSelectionPanel()
    private val othersMatchesTypeSelectionPanel = MatchTypeSelectionPanel()

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new DBCleanupDialog object.
     */
    init {
        defaultCloseOperation = DISPOSE_ON_CLOSE
        initComponents()
    }

    //~ Methods ------------------------------------------------------------------------------------
    private fun initComponents() {
        val mainPanel = JPanel()
        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.fill = GridBagConstraints.BOTH
        gbc.ipady = 5
        gbc.ipadx = 10

        mainPanel.layout = GridBagLayout()
        mainPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        gbc.gridwidth = 2
        val textIntro = JLabel(
            "<html>" + HOVerwaltung.instance().getLanguageString("dbcleanup.intro")
                .replace("\n", "<br>") + "</html>"
        )
        mainPanel.add(textIntro, gbc)

        gbc.gridwidth = 1
        val labelOwnMatches = JLabel(HOVerwaltung.instance().getLanguageString("dbcleanup.yourMatches"))
        labelOwnMatches.font = labelOwnMatches.font.deriveFont(Font.BOLD)

        val labelOtherMatches = JLabel(HOVerwaltung.instance().getLanguageString("dbcleanup.otherTeamsMatches"))
        labelOtherMatches.font = labelOtherMatches.font.deriveFont(Font.BOLD)

        val labelHrf = JLabel(HOVerwaltung.instance().getLanguageString("dbcleanup.hrf"))
        labelHrf.font = labelHrf.font.deriveFont(Font.BOLD)

        val hrfAutoRemove = JCheckBox(HOVerwaltung.instance().getLanguageString("dbcleanup.hrfSmartRemove"))
        hrfAutoRemove.isSelected = true

        gbc.gridx = 0
        gbc.gridy = 1
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.BOTH
        gbc.anchor = GridBagConstraints.LINE_START

        mainPanel.add(labelOwnMatches, gbc)
        gbc.gridy = 3
        mainPanel.add(labelOtherMatches, gbc)
        gbc.gridy = 5
        mainPanel.add(labelHrf, gbc)

        gbc.gridx = 1
        gbc.gridy = 1
        mainPanel.add(mainPanelOwnMatches, gbc)
        gbc.gridy++
        mainPanel.add(ownMatchesTypeSelectionPanel, gbc)
        gbc.gridy++
        mainPanel.add(mainPanelOtherMatches, gbc)
        gbc.gridy++
        mainPanel.add(othersMatchesTypeSelectionPanel, gbc)
        gbc.gridy++
        mainPanel.add(hrfAutoRemove, gbc)

       addNewStrut(gbc, mainPanel)

        // Add current statistics on DB Records
        gbc.gridy++
        gbc.gridx = 0
        gbc.anchor = GridBagConstraints.WEST
        gbc.weightx = 1.0
        mainPanel.add(JLabel(HOVerwaltung.instance().getLanguageString("dbcleanup.numMatches")), gbc)

        gbc.gridx = 1
        gbc.anchor = GridBagConstraints.LINE_END
        gbc.weightx = 3.0
        mainPanel.add(JLabel(cleanupTool.getMatchesCount().toString()), gbc)

        gbc.gridx = 0
        gbc.gridy++
        gbc.anchor = GridBagConstraints.LINE_START
        mainPanel.add(JLabel(HOVerwaltung.instance().getLanguageString("dbcleanup.numHrfs")), gbc)
        gbc.gridx = 1
        mainPanel.add(JLabel(cleanupTool.getHrfCount().toString()), gbc)
        gbc.gridy++

        addNewStrut(gbc, mainPanel)

        gbc.gridwidth = 2

        // Add Buttons
        val buttonPanel = JPanel()
        val cleanupNowButton = JButton(HOVerwaltung.instance().getLanguageString("dbcleanup.cleanupnow"))
        cleanupNowButton.font = cleanupNowButton.font.deriveFont(Font.BOLD)
        cleanupNowButton.addActionListener {
            cleanupTool.cleanupMatches(
                CleanupDetails(
                    ownMatchesTypeSelectionPanel.getSelectedMatchTypes(),
                    othersMatchesTypeSelectionPanel.getSelectedMatchTypes(),
                    mainPanelOwnMatches.getWeeks(),
                    mainPanelOtherMatches.getWeeks())
            )
            cleanupTool.cleanupHRFs(DBCleanupTool.REMOVE_NONE, hrfAutoRemove.isSelected)
            isVisible = false
        }

        val cancelButton = JButton(HOVerwaltung.instance().getLanguageString("ls.button.cancel"))
        cancelButton.addActionListener {
            isVisible = false
        }
        buttonPanel.add(cleanupNowButton)
        buttonPanel.add(cancelButton)

        mainPanel.add(buttonPanel, gbc)

        contentPane.layout = BorderLayout()
        contentPane.add(mainPanel, BorderLayout.CENTER)

        pack()

        val screenSize = HOMainFrame.instance().toolkit.screenSize
        if (screenSize.width > size.width) {
            // Place in the middle
            this.setLocation(
                (screenSize.width / 2) - (size.width / 2),
                (screenSize.height / 2) - (size.height / 2)
            )
        }

        isVisible = true
    }

    private fun addNewStrut(gbc: GridBagConstraints, mainPanel: JPanel, height: Int = 20) {
        gbc.gridy++
        gbc.gridwidth = 2
        gbc.gridx = 0

        val strut = JPanel()
        strut.preferredSize = Dimension(20, height)
        mainPanel.add(strut, gbc)

        gbc.gridwidth = 1
        gbc.gridy++
    }
}

