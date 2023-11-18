package core.gui.comp.renderer

import core.datatype.CBItem
import core.gui.comp.entry.ColorLabelEntry
import core.gui.theme.HOColorName
import core.gui.theme.HOIconName
import core.gui.theme.ImageUtilities
import core.gui.theme.ThemeManager
import java.awt.Component
import javax.swing.*

/**
 * Renderer for Weather CB items
 */
class WeatherListCellRenderer : ListCellRenderer<Any?> {
    var m_clEntry = ColorLabelEntry(
        "",
        ColorLabelEntry.FG_STANDARD,
        ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER),
        SwingConstants.LEFT
    )
    var m_jlLeer = JLabel(" ")
    override fun getListCellRendererComponent(
        jList: JList<*>?,
        obj: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        if (obj is CBItem) {
            val id = obj.id
            val icon: Icon = if (id == 4) {
                ImageUtilities.getSvgIcon(HOIconName.WEATHER[id], 30, 18)
            } else {
                ThemeManager.getIcon(HOIconName.WEATHER[id])
            }
            m_clEntry.setIcon(icon)
            return m_clEntry.getComponent(isSelected)
        }
        m_jlLeer.setOpaque(true)
        if (isSelected) {
            m_jlLeer.setBackground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG))
        } else {
            m_jlLeer.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG))
        }
        return m_jlLeer
    }
}
