package core.gui.comp.renderer

import core.gui.comp.entry.ColorLabelEntry
import core.gui.comp.entry.IHOTableEntry
import core.gui.theme.HOColorName
import core.gui.theme.ThemeManager
import java.awt.Component
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

/**
 * Renderer for tables with JLabels as table objects
 */
open class HODefaultTableCellRenderer : TableCellRenderer {
    //~ Methods ------------------------------------------------------------------------------------
    override fun getTableCellRendererComponent(
        table: JTable, value: Any,
        isSelected: Boolean,
        hasFocus: Boolean, row: Int,
        column: Int
    ): Component {
        return when (value) {
            is IHOTableEntry -> {
                val component = value.getComponent(isSelected)
                if (isSelected) {
                    component.setOpaque(true)
                }
                component
            }

            is JComponent -> {
                value.setOpaque(true)
                value.setBackground(if (isSelected) SELECTION_BG else ColorLabelEntry.BG_STANDARD)
                value.setForeground(if (isSelected) SELECTION_FG else ColorLabelEntry.FG_STANDARD)
                value
            }

            else -> {
                val component: JComponent = JLabel(value.toString())
                component.setOpaque(true)
                component.setBackground(if (isSelected) SELECTION_BG else ColorLabelEntry.BG_STANDARD)
                component.setForeground(if (isSelected) SELECTION_FG else ColorLabelEntry.FG_STANDARD)
                component
            }
        }
    }

    companion object {
        //~ Static fields/initializers -----------------------------------------------------------------
        @JvmField
        var SELECTION_BG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG)
        @JvmField
        var SELECTION_FG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_FG)
    }
}
