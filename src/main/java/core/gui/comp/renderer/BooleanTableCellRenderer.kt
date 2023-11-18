package core.gui.comp.renderer

import core.gui.theme.HOColorName
import core.gui.theme.ThemeManager
import java.awt.Component
import javax.swing.JCheckBox
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

class BooleanTableCellRenderer : JCheckBox(), TableCellRenderer {
    init {
        setOpaque(true)
        setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG))
        setHorizontalAlignment(CENTER)
    }

    override fun getTableCellRendererComponent(
        table: JTable, value: Any,
        isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
    ): Component {
        val boolValue = value as Boolean
        setSelected(boolValue)
        return this
    }
}
