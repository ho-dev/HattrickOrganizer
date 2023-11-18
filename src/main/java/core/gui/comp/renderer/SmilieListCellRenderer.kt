package core.gui.comp.renderer

import core.gui.comp.entry.ColorLabelEntry
import core.gui.theme.*
import java.awt.Component
import java.util.*
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.SwingConstants

/**
 * Renderer für eine Combobox mit SpielerCBItems
 */
class SmilieListCellRenderer : ListCellRenderer<Any> {
    //~ Instance fields ----------------------------------------------------------------------------
    private val m_clEntry = ColorLabelEntry(
        "", ColorLabelEntry.FG_STANDARD,
        ThemeManager.getColor(HOColorName.TABLEENTRY_BG),
        SwingConstants.LEFT
    )
    private val m_jlLeer = JLabel(" ")

    //~ Methods ------------------------------------------------------------------------------------
    override fun getListCellRendererComponent(
        jList: JList<*>?,
        obj: Any, index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        if (obj is String && "" != obj) {
            if (Arrays.stream(HOIconName.SMILEYS).anyMatch { o: String -> o.equals(obj) }) {
                // smiley icon
                m_clEntry.setIcon(ImageUtilities.getSmileyIcon(obj.toString()))
                return m_clEntry.getComponent(isSelected)
            } else if (Arrays.stream(GroupTeamFactory.TEAMS_GROUPS).anyMatch { o: String -> o.equals(obj) }) {
                // jersey icon
                m_clEntry.setIcon(GroupTeamFactory.instance().getActiveGroupIcon(obj.toString()))
                return m_clEntry.getComponent(isSelected)
            }
        }
        m_jlLeer.setOpaque(true)
        if (isSelected) {
            m_jlLeer.setBackground(HODefaultTableCellRenderer.SELECTION_BG)
        } else {
            m_jlLeer.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG))
        }
        return m_jlLeer
    }
}
