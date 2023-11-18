package core.file.xml

import core.datatype.CBItem
import core.gui.HOMainFrame
import core.gui.comp.panel.ImagePanel
import core.model.HOVerwaltung
import java.awt.*
import javax.swing.*

class TeamSelectionDialog(mainFrame: HOMainFrame, private val infos: List<TeamInfo>) :
    JDialog(mainFrame, HOVerwaltung.instance().getLanguageString("teamSelect.header"), true) {
    private var teamComboBox: JComboBox<*>? = null
    private val m_jbOK = JButton()
    private val m_jbCancel = JButton()
    private val teamName = JLabel("")
    private val teamCountry = JLabel("")
    private val teamSeries = JLabel("")
    var cancel = false
        private set

    init {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        init(mainFrame)
        addListeners()
    }

    private fun init(mainFrame: HOMainFrame) {
        contentPane = ImagePanel(FlowLayout())
        val cbItems = ArrayList<CBItem>()
        var i = 0
        for (info in infos) {
            cbItems.add(CBItem(info.name!!, i++))
        }
        teamComboBox = JComboBox<Any?>(cbItems.toTypedArray())
        val mainPanel = JPanel()
        mainPanel.setOpaque(false)
        mainPanel.setLayout(BorderLayout())
        mainPanel.preferredSize = Dimension(300, 200)
        mainPanel.add(teamComboBox, BorderLayout.NORTH)
        val selectedPanel = JPanel()
        selectedPanel.setLayout(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.anchor = GridBagConstraints.WEST
        gbc.insets = Insets(2, 2, 2, 2)
        selectedPanel.add(JLabel(HOVerwaltung.instance().getLanguageString("teamSelect.teamName")), gbc)
        gbc.gridx = 1
        selectedPanel.add(teamName, gbc)
        gbc.gridx = 0
        gbc.gridy++
        selectedPanel.add(JLabel(HOVerwaltung.instance().getLanguageString("teamSelect.series")), gbc)
        gbc.gridx = 1
        selectedPanel.add(teamSeries, gbc)
        gbc.gridx = 0
        gbc.gridy++
        selectedPanel.add(JLabel(HOVerwaltung.instance().getLanguageString("teamSelect.country")), gbc)
        gbc.gridx = 1
        selectedPanel.add(teamCountry, gbc)
        initTeam(0)
        mainPanel.add(selectedPanel, BorderLayout.CENTER)
        val buttonPanel = JPanel()
        buttonPanel.setLayout(FlowLayout())
        m_jbOK.setText(HOVerwaltung.instance().getLanguageString("ls.button.ok"))
        buttonPanel.add(m_jbOK)
        m_jbCancel.setText(HOVerwaltung.instance().getLanguageString("ls.button.cancel"))
        buttonPanel.add(m_jbCancel)
        mainPanel.add(buttonPanel, BorderLayout.SOUTH)
        this.contentPane.add(mainPanel)
        val size = mainFrame.toolkit.screenSize
        if (size.width > size.width) { // open dialog in the middle of the screen
            this.setLocation(size.width / 2 - size.width / 2, size.height / 2 - size.height / 2)
        }
        pack()
    }

    private fun addListeners() {
        teamComboBox!!.addActionListener { _ ->
            // team changed
            initTeam(teamComboBox!!.getSelectedIndex())
        }
        m_jbOK.addActionListener { _ ->
            val i = teamComboBox!!.getSelectedIndex()
            if (i >= 0) {
                isVisible = false
            } else {
                JOptionPane.showMessageDialog(
                    null, HOVerwaltung.instance()
                        .getLanguageString("teamSelect.doChoose"), HOVerwaltung
                        .instance().getLanguageString("teamSelect.doChooseHeader"),
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        }
        m_jbCancel.addActionListener { _ ->
            cancel = true
            dispose()
        }
    }

    private fun initTeam(i: Int) {
        if (i < 0) return
        val info = infos[i]
        teamCountry.setText(info.country)
        teamSeries.setText(info.league)
        teamName.setText(info.name)
    }

    fun getSelectedTeam(): TeamInfo? {
            val i = teamComboBox!!.getSelectedIndex()
            return if (i >= 0) {
                infos[i]
            } else null
        }
}
