package core.gui.comp.entry

import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.gui.theme.HOColorName
import core.gui.theme.HOIconName
import core.gui.theme.ImageUtilities
import core.gui.theme.ThemeManager
import core.model.match.IMatchType
import core.util.HODateTime
import java.awt.Cursor
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.border.EmptyBorder

class MatchDateTableEntry(lastMatchDate: HODateTime?, matchType: IMatchType) : AbstractHOTableEntry() {
    private val bgColor = ThemeManager.getColor(HOColorName.TABLEENTRY_BG)
    val matchLink = JLabel("")
    private var matchDate: HODateTime? = null
    private var m_clComponent: JComponent = JPanel()
    override fun getComponent(isSelected: Boolean): JComponent {
        m_clComponent.setBackground(if (isSelected) HODefaultTableCellRenderer.SELECTION_BG else bgColor)
        return m_clComponent
    }

    init {
        createComponent()
        setMatchInfo(lastMatchDate, matchType)
    }

    private fun setMatchInfo(t: HODateTime?, matchType: IMatchType) {
        if (t != null) {
            matchDate = t
            matchLink.setText(matchDate!!.toLocaleDateTime())
            matchIcon = ThemeManager.getIcon(HOIconName.MATCHICONS[matchType.getIconArrayIndex()])
            matchLink.setIcon(matchIcon)
        }
        updateComponent()
        val layout = GridBagLayout()
        val constraints = GridBagConstraints()
        m_clComponent.setLayout(layout)
        m_clComponent.setBorder(EmptyBorder(0, 3, 0, 0))
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
        constraints.weighty = 0.0
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.anchor = GridBagConstraints.WEST
        matchLink.setCursor(Cursor(Cursor.HAND_CURSOR))
        layout.setConstraints(matchLink, constraints)
        m_clComponent.add(matchLink)
        m_clComponent.repaint()
    }

    override fun clear() {
        val constraints = GridBagConstraints()
        val layout = GridBagLayout()
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
        constraints.weighty = 0.0
        constraints.gridy = 0
        m_clComponent.removeAll()
        m_clComponent.setLayout(layout)
        val jlabel = JLabel(ImageUtilities.NOIMAGEICON)
        jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
        constraints.gridx = 0
        layout.setConstraints(jlabel, constraints)
        m_clComponent.add(jlabel)
    }

    override fun compareTo(other: IHOTableEntry): Int {
        if (matchDate == null) {
            return -1
        }
        return if (other is MatchDateTableEntry) {
            if (other.matchDate == null) {
                1
            } else matchDate!!.compareTo(other.matchDate!!)
        } else 0
    }

    override fun createComponent() {
        val renderer = JPanel()
        val layout = GridBagLayout()
        val constraints = GridBagConstraints()
        renderer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
        renderer.setLayout(layout)
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
        constraints.weighty = 0.0
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.anchor = GridBagConstraints.WEST
        m_clComponent = renderer
    }

    override fun updateComponent() {
        m_clComponent.removeAll()
        val layout = GridBagLayout()
        val constraints = GridBagConstraints()
        m_clComponent.setLayout(layout)
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
        constraints.weighty = 0.0
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.anchor = GridBagConstraints.WEST
        m_clComponent.repaint()
    }

    companion object {
        private var matchIcon: Icon? = null
    }
}
