package core.gui.comp.table

import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.model.HOConfigurationIntParameter
import java.awt.Component
import java.awt.Dimension
import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import java.beans.PropertyChangeEvent
import javax.swing.*
import javax.swing.event.ListSelectionListener
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.table.TableModel

/**
 * Table with fixed columns on the left hand side
 * The other columns can be sorted or disabled by the user
 */
open class FixedColumnsTable @JvmOverloads constructor(tableModel: HOTableModel, fixedColumns: Int = 1) :
    JTable(tableModel) {
    /**
     * Return the number of fixed columns
     * @return int
     */
    /**
     * Number of fixed columns in table
     */
    val fixedColumnsCount: Int

    /**
     * Position of the divider between fixed and scrollable tables
     */
    private var dividerLocation: HOConfigurationIntParameter? = null

    /**
     * Fixed table part (left hand side)
     */
    private var fixed: JTable? = null

    /**
     * Container component for split pane of fixed and scrollable tables
     */
    private var scrollPane: JScrollPane? = null


    /**
     * Create a fixed columns table
     * Columns and header tooltips are taken from table model.
     * Column settings are restored from database.
     * Internally two tables are created, "fixed" for the left hand side, "scroll" for the right hand side
     *
     * @param tableModel Table model
     * @param fixedColumns fixed columns count
     */
    /**
     * Constructor of table with one fixed columns
     * @param tableModel Table model
     */
    init {
        tableModel.table = this
        this.fixedColumnsCount = fixedColumns

        // Handle tool tips
        val header = getTableHeader().defaultRenderer
        getTableHeader().defaultRenderer =
            TableCellRenderer { table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int ->
                val tableCellRendererComponent =
                    header.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                val tableColumn = table.columnModel.getColumn(column)
                val model = table.model as HOTableModel
                // Set header tool tip
                val tooltipString = model.getDisplayedColumns()[tableColumn.modelIndex].getTooltip()
                (tableCellRendererComponent as JComponent).toolTipText = tooltipString
                tableCellRendererComponent
            }

        setAutoResizeMode(AUTO_RESIZE_OFF)
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG)

        if (fixedColumns > 0) {
            val fixed = JTable(model)
            fixed.isFocusable = false
            fixed.selectionModel = getSelectionModel()
            fixed.tableHeader.reorderingAllowed = false
            fixed.selectionModel = getSelectionModel()
            //  Remove the non-fixed columns from the fixed table
            while (fixed.columnCount > fixedColumns) {
                val _columnModel = fixed.columnModel
                _columnModel.removeColumn(_columnModel.getColumn(fixedColumns))
            }
            this.fixed = fixed

            //  Remove the fixed columns from the main table
            var width = 0
            var i = 0
            while (i < fixedColumns) {
                val _columnModel = getColumnModel()
                val column = _columnModel.getColumn(0)
                width += column.preferredWidth
                _columnModel.removeColumn(column)
                i++
            }

            // Sync scroll bars of both tables
            val fixedScrollPane = JScrollPane(fixed)
            val rightScrollPane = JScrollPane(this)
            fixedScrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
            rightScrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
            val fixedScrollBar = fixedScrollPane.verticalScrollBar
            val rightScrollBar = rightScrollPane.verticalScrollBar

            // setVisible(false) does not have an effect, so we set the size to
            // false. We can't disable the scrollbar with VERTICAL_SCROLLBAR_NEVER
            // because this will disable mouse wheel scrolling.
            fixedScrollBar.preferredSize = Dimension(0, 0)

            // Synchronize vertical scrolling
            val adjustmentListener = AdjustmentListener { e: AdjustmentEvent ->
                if (e.source === rightScrollBar) {
                    fixedScrollBar.value = e.value
                } else {
                    rightScrollBar.value = e.value
                }
            }
            fixedScrollBar.addAdjustmentListener(adjustmentListener)
            rightScrollBar.addAdjustmentListener(adjustmentListener)
            rightScrollPane.verticalScrollBar.model = fixedScrollPane.verticalScrollBar.model
            val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fixedScrollPane, rightScrollPane)
            if (width == 0) width = 60
            val dividerLocation = HOConfigurationIntParameter("TableDividerLocation_" + tableModel.id, width)
            splitPane.dividerLocation = dividerLocation.getIntValue()
            splitPane.addPropertyChangeListener { evt: PropertyChangeEvent ->
                val propertyName = evt.propertyName
                if (propertyName == "dividerLocation") {
                    val pane = evt.source as JSplitPane
                    dividerLocation.setIntValue(pane.dividerLocation)
                }
            }
            this.dividerLocation = dividerLocation
            val scrollPane = JScrollPane()
            scrollPane.setViewportView(splitPane)
            this.scrollPane = scrollPane
        } else {
            // No fixed columns
            fixed = null
            scrollPane = JScrollPane(this)
        }
    }

    /**
     * Set row selection interval of both tables synchronously
     * @param rowIndex0 one end of the interval
     * @param rowIndex1 the other end of the interval
     */
    override fun setRowSelectionInterval(rowIndex0: Int, rowIndex1: Int) {
        super.setRowSelectionInterval(rowIndex0, rowIndex1)
        fixed?.setRowSelectionInterval(rowIndex0, rowIndex1)
    }

    /**
     * The provided renderer is set to both internal tables
     * @param columnClass  set the default cell renderer for this columnClass
     * @param renderer default cell renderer to be used for this columnClass
     */
    override fun setDefaultRenderer(columnClass: Class<*>?, renderer: TableCellRenderer?) {
        super.setDefaultRenderer(columnClass, renderer)
        fixed?.setDefaultRenderer(columnClass, renderer)
    }

    /**
     * Add a list selection listener
     * @param listener ListSelectionListener
     */
    fun addListSelectionListener(listener: ListSelectionListener?) {
        val rowSM = getSelectionModel()
        rowSM.addListSelectionListener(listener)
    }

    /**
     * Set the row sorter to both internal tables
     * @param sorter Sorter
     */
    override fun setRowSorter(sorter: RowSorter<out TableModel?>) {
        super.setRowSorter(sorter)
        if (fixed != null) fixed!!.rowSorter = sorter
    }

    val containerComponent: Component?
        /**
         * Returns the outer container component of the fixed column table
         * @return Component
         */
        get() = this.scrollPane

    override fun getColumn(identifier: Any): TableColumn {
        return try {
            super.getColumn(identifier)
        } catch (e: IllegalArgumentException) {
            fixed!!.getColumn(identifier)
        }
    }

    /**
     * Return th table column of the fixed or right hand side table
     * @param i Column index
     * @return TableColumn
     */
    fun getTableColumn(i: Int): TableColumn {
        if (i < fixedColumnsCount) {
            return fixed!!.columnModel.getColumn(i)
        }
        return super.getColumnModel().getColumn(i - fixedColumnsCount)
    }
}
