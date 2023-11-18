package core.gui.comp.renderer

import java.awt.Component
import java.text.NumberFormat
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

class DoubleTableCellRenderer(precision: Int) : DefaultTableCellRenderer() {
    private val format: NumberFormat = NumberFormat.getInstance()

    init {
        format.setMaximumFractionDigits(precision)
        format.setMinimumFractionDigits(precision)
        setHorizontalAlignment(RIGHT)
    }

    override fun getTableCellRendererComponent(
        table: JTable, value: Any, isSelected: Boolean,
        hasFocus: Boolean, row: Int, column: Int
    ): Component {
        val numberString = format.format(value as Number)
        return super.getTableCellRendererComponent(
            table, numberString, isSelected, hasFocus, row,
            column
        )
    }
}