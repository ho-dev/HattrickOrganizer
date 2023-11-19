package core.gui.comp.table

import module.transfer.ui.sorter.DefaultTableSorter
import java.awt.Dimension
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionListener
import javax.swing.table.TableCellRenderer

class FixedColumnsTable(
    /**
     * Number of fixed columns in table
     */
    val fixedColumnsCount: Int, tableModel: HOTableModel
) : JScrollPane() {
    /**
     * Return the number of fixed columns
     * @return int
     */
    /**
     * Return the created table sorter
     * @return DefaultTableSorter
     */
    /**
     * Table sorter
     */
    val tableSorter: DefaultTableSorter
    /**
     * Returns the Locked LeftTable
     *
     * @return Jtable
     */
    /**
     * Fixed table part (left hand side)
     */
    val fixedTable: JTable
    /**
     * Returns the Scrollable RightTable
     *
     * @return Jtable
     */
    /**
     * Scrollable table part (right hand side)
     */
    val scrollTable: JTable

    /**
     * Create a fixed columns table
     * Columns and Header tooltips are taken from table model.
     * Column settings are restored from database.
     * Internally two tables are created, "fixed" for the left hand side, "scroll" for the right hand side
     *
     * @param fixedColumns number of fixed columnms
     * @param tableModel table model
     */
    init {
        tableSorter = DefaultTableSorter(tableModel)
        val table = JTable(tableSorter)
        setTooltipHeader(table, tableModel.getTooltips())
        table.autoResizeMode = JTable.AUTO_RESIZE_OFF
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        setViewportView(table)
        scrollTable = table
        for (i in 0 until scrollTable.columnCount) {
            val tm = tableModel.columns?.get(i)
            val cm = scrollTable.columnModel.getColumn(i)
            cm.setMinWidth(tm?.minWidth ?: 20)
        }
        fixedTable = JTable(scrollTable.model)
        fixedTable.setFocusable(false)
        fixedTable.setSelectionModel(scrollTable.selectionModel)
        fixedTable.tableHeader.setReorderingAllowed(false)


        //  Remove the fixed columns from the main table
        var width = 0
        var i = 0
        while (i < fixedColumnsCount) {
            val _columnModel = scrollTable.columnModel
            val column = _columnModel.getColumn(0)
            width += column.minWidth
            _columnModel.removeColumn(column)
            i++
        }
        scrollTable.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        fixedTable.setSelectionModel(scrollTable.selectionModel)

        //  Remove the non-fixed columns from the fixed table
        while (fixedTable.columnCount > fixedColumnsCount) {
            val _columnModel = fixedTable.columnModel
            _columnModel.removeColumn(_columnModel.getColumn(fixedColumnsCount))
        }

        //  Add the fixed table to the scroll pane
        fixedTable.preferredScrollableViewportSize = Dimension(width, 0)
        setRowHeaderView(fixedTable)
        setCorner(UPPER_LEFT_CORNER, fixedTable.tableHeader)
        tableModel.restoreUserSettings(this)
    }

    private fun setTooltipHeader(table: JTable, tooltips: Array<String>) {
        val header = ToolTipHeader(table.columnModel)
        header.setToolTipStrings(tooltips)
        header.setToolTipText("")
        table.tableHeader = header
        tableSorter.setTableHeader(table.tableHeader)
    }
    //~ Methods ------------------------------------------------------------------------------------
    /**
     * The provided renderer is set to both internal tables
     * @param columnClass  set the default cell renderer for this columnClass
     * @param renderer default cell renderer to be used for this columnClass
     */
    fun setDefaultRenderer(columnClass: Class<*>?, renderer: TableCellRenderer?) {
        fixedTable.setDefaultRenderer(columnClass, renderer)
        scrollTable.setDefaultRenderer(columnClass, renderer)
    }

    /**
     * Add a list selection listener
     * @param listener ListSelectionListener
     */
    fun addListSelectionListener(listener: ListSelectionListener?) {
        val rowSM = scrollTable.selectionModel
        rowSM.addListSelectionListener(listener)
    }
}
