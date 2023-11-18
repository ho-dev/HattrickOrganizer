package core.gui.comp.renderer

import java.awt.Component
import java.text.DateFormat
import java.util.*
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

class DateTimeTableCellRenderer : DefaultTableCellRenderer() {
    private val format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    override fun getTableCellRendererComponent(
        table: JTable, value: Any, isSelected: Boolean,
        hasFocus: Boolean, row: Int, column: Int
    ): Component {
        var dateString: String? = ""
        dateString = format.format(value as Date)
        return super.getTableCellRendererComponent(
            table, dateString, isSelected, hasFocus, row,
            column
        )
    }

}