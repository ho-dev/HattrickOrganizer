package core.gui.comp.renderer

import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer

class TableHeaderRenderer1(table: JTable) : TableCellRenderer {
    var renderer: DefaultTableCellRenderer

    init {
        renderer = table.tableHeader.defaultRenderer as DefaultTableCellRenderer
        renderer.setHorizontalAlignment(JLabel.CENTER)
    }

    override fun getTableCellRendererComponent(
        table: JTable, value: Any, isSelected: Boolean,
        hasFocus: Boolean, row: Int, col: Int
    ): Component {
        return renderer.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, col
        )
    }
}
