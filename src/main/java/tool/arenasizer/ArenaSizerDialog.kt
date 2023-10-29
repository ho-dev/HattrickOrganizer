package tool.arenasizer

import core.model.HOVerwaltung
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

class ArenaSizerDialog(owner: JFrame?) : JDialog(owner, true), ActionListener {
    private var tabbedPane: JTabbedPane? = null
    private var panel: ArenaPanel? = null

    private var historyPanel: DistributionStatisticsPanel = DistributionStatisticsPanel()
    private var infoPanel: ArenaPanel = ArenaPanel()
    private var controlPanel: ControlPanel = ControlPanel()

    private var toolbar: JPanel? = null
    private val refreshButton = JButton(HOVerwaltung.instance().getLanguageString("ls.button.apply"))

    init {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        initialize()
    }

    private fun initialize() {
        setSize(900, 430)
        layout = BorderLayout()
        setTitle(HOVerwaltung.instance().getLanguageString("ArenaSizer"))
        add(getToolbar(), BorderLayout.NORTH)
        val centerPanel = JPanel(BorderLayout())
        val panelC = JPanel(FlowLayout(FlowLayout.LEADING))
        panelC.add(controlPanel)
        centerPanel.add(panelC, BorderLayout.NORTH)
        centerPanel.add(getTabbedPane(), BorderLayout.CENTER)
        add(centerPanel, BorderLayout.CENTER)
    }

    private fun getToolbar(): JPanel {
        if (toolbar == null) {
            toolbar = JPanel(FlowLayout(FlowLayout.LEADING))
            toolbar!!.add(refreshButton)
            refreshButton.addActionListener(this)
            // reset
            // save
        }
        return toolbar!!
    }

    private val arenaPanel: ArenaPanel
        get() {
            if (panel == null) {
                panel = ArenaPanel()
            }
            return panel!!
        }

    private fun getTabbedPane(): JTabbedPane {
        if (tabbedPane == null) {
            tabbedPane = JTabbedPane()
            val hoV = HOVerwaltung.instance()
            tabbedPane!!.addTab(hoV.getLanguageString("Stadion"), arenaPanel)
            tabbedPane!!.addTab(hoV.model.getStadium().name, infoPanel)
            tabbedPane!!.addTab(hoV.getLanguageString("Statistik"), historyPanel)
        }
        return tabbedPane!!
    }

    override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)
        val screenSize = parent.size
        val x = (screenSize.width - getWidth()) / 2
        val y = (screenSize.height - getHeight()) / 2
        setLocation(parent.x + x, parent.y + y)
    }

    override fun actionPerformed(e: ActionEvent) {
        if (e.source === refreshButton) {
            val stadium = controlPanel.stadium
            val supporter = controlPanel.modifiedSupporter
            arenaPanel.reinitArena(stadium, supporter[0], supporter[1], supporter[2])
            infoPanel.reinitArena(
                HOVerwaltung.instance().model.getStadium(),
                supporter[0],
                supporter[1],
                supporter[2]
            )
        }
    }
}
