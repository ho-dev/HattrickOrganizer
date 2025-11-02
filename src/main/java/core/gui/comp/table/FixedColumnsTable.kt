package core.gui.comp.table

import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.model.HOConfigurationIntParameter
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.beans.PropertyChangeEvent
import javax.swing.*
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
import javax.swing.event.ListSelectionListener
import javax.swing.event.TableModelEvent
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.table.TableModel


/**
 * Table with fixed columns on the left hand side
 * The other columns can be sorted or disabled by the user
 */
open class FixedColumnsTable @JvmOverloads constructor(
    tableModel: HOTableModel,
    /**
     * Number of fixed columns in table
     */
    val fixedColumnsCount: Int = 1
) :
    JTable(tableModel) {
    /**
     * Return the number of fixed columns
     * @return int
     */

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
    private var containerComponent: JPanel

    /**
     * Create a fixed columns table
     * Columns and header tooltips are taken from table model.
     * Column settings are restored from database.
     * Internally two tables are created, "fixed" for the left hand side, "scroll" for the right hand side
     */
    init {
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

        if (fixedColumnsCount > 0) {
            fixed = JTable(model)
            fixed!!.setFocusable(false)
            fixed!!.setSelectionModel(getSelectionModel())
            fixed!!.getTableHeader().reorderingAllowed = false
            fixed!!.setSelectionModel(getSelectionModel())
            //  Remove the non-fixed columns from the fixed table
            while (fixed!!.getColumnCount() > fixedColumnsCount) {
                val _columnModel = fixed!!.getColumnModel()
                _columnModel.removeColumn(_columnModel.getColumn(fixedColumnsCount))
            }
            //  Remove the fixed columns from the main table
            var width = 0
            var i = 0
            while (i < fixedColumnsCount) {
                val _columnModel = getColumnModel()
                val column = _columnModel.getColumn(0)
                width += column.preferredWidth
                _columnModel.removeColumn(column)
                i++
            }

            // Sync scroll bars of both tables
            val fixedScrollPane = JScrollPane(fixed)
            val rightScrollPane = JScrollPane(this)

            // Synchronize horizontal scroll bars appearances
            // Initially set policy AS_NEEDED
            // If one scroll bar appears, and the other one is not visible, set ALWAYS
            // If one scroll bar disappears, and the other has policy ALWAYS, set AS_NEEDED
            val fixedHorizontalScrollbar = fixedScrollPane.horizontalScrollBar
            val rightHorizontalScrollbar = rightScrollPane.horizontalScrollBar
            fixedHorizontalScrollbar.addComponentListener(object : ComponentAdapter() {
                override fun componentShown(e: ComponentEvent?) {
                    showHorizontalScrollbar(rightScrollPane);
                }

                override fun componentHidden(e: ComponentEvent?) {
                    horizontalScrollbarHidden(fixedScrollPane, rightScrollPane);
                }
            })
            rightHorizontalScrollbar.addComponentListener(object : ComponentAdapter() {
                override fun componentShown(e: ComponentEvent?) {
                    showHorizontalScrollbar(fixedScrollPane);
                }

                override fun componentHidden(e: ComponentEvent?) {
                    horizontalScrollbarHidden(rightScrollPane, fixedScrollPane);
                }
            })

            val fixedVerticalScrollBar = fixedScrollPane.verticalScrollBar
            val rightVerticalScrollBar = rightScrollPane.verticalScrollBar

            // setVisible(false) does not have an effect, so we set the size to
            // false. We can't disable the scrollbar with VERTICAL_SCROLLBAR_NEVER
            // because this will disable mouse wheel scrolling.
            fixedVerticalScrollBar.preferredSize = Dimension(0, 0)

            // Synchronize vertical scrolling
            val verticalAdjustmentListener = AdjustmentListener { e: AdjustmentEvent ->
                if (e.source === rightVerticalScrollBar) {
                    fixedVerticalScrollBar.value = e.value
                } else {
                    rightVerticalScrollBar.value = e.value
                }
            }

            fixedVerticalScrollBar.addAdjustmentListener(verticalAdjustmentListener)
            rightVerticalScrollBar.addAdjustmentListener(verticalAdjustmentListener)
            rightScrollPane.verticalScrollBar.model = fixedScrollPane.verticalScrollBar.model
            val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fixedScrollPane, rightScrollPane)
            splitPane.dividerSize = 2
            if (width == 0) width = 60
            this.dividerLocation = HOConfigurationIntParameter("TableDividerLocation_" + tableModel.id, width)
            splitPane.dividerLocation = dividerLocation?.getIntValue()!!
            splitPane.addPropertyChangeListener { evt: PropertyChangeEvent ->
                val propertyName = evt.propertyName
                if (propertyName == "dividerLocation") {
                    val pane = evt.source as JSplitPane
                    dividerLocation!!.setIntValue(pane.dividerLocation)
                }
            }
            containerComponent = JPanel()
            containerComponent.setLayout(BorderLayout())
            containerComponent.add(splitPane, BorderLayout.CENTER)
            tableModel.initTable(this)
        } else {
            // No fixed columns
            fixed = null
            containerComponent = JPanel()
            containerComponent.setLayout(BorderLayout())
            containerComponent.add(JScrollPane(this))
        }
    }

    /**
     * Show the horizontal scroll bar of the scroll pane when the other horizontal scroll bar appeared in case of pane resizing
     * @param scrollPane The scroll pane that has to be synchronized with the other scroll pane
     */
    private fun showHorizontalScrollbar(scrollPane: JScrollPane) {
        if (scrollPane.horizontalScrollBar.isVisible) return // already visible, no action required
        if (scrollPane.horizontalScrollBarPolicy != HORIZONTAL_SCROLLBAR_ALWAYS){
            scrollPane.horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_ALWAYS
            scrollPane.revalidate();
            scrollPane.repaint();
        }
    }

    /**
     * Hide the horizontal scroll bar of the scroll pane when the other horizontal scroll bar disappeared in case of pane resizing
     * If the other scroll bar does not require a scroll bar, its scroll bar will be removed
     * otherwise the disappearance in hidden scroll bar will be reset.
     * @param hiddenScrollPane The scroll pane that removed the scroll bar
     * @param otherScrollPane The scroll pane that has to be adapted
     */
    private fun horizontalScrollbarHidden(hiddenScrollPane: JScrollPane, otherScrollPane: JScrollPane){
        val otherScrollbar = otherScrollPane.horizontalScrollBar
        if (otherScrollbar.maximum-otherScrollbar.visibleAmount == otherScrollbar.minimum){
            // Full size, no scroll bar needed
            otherScrollPane.horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_AS_NEEDED
            otherScrollPane.revalidate();
            otherScrollPane.repaint();
        }
        else {
            hiddenScrollPane.horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_ALWAYS
            hiddenScrollPane.revalidate();
            hiddenScrollPane.repaint();
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
     * Add row selection interval of both tables synchronously
     * @param rowIndex0 one end of the interval
     * @param rowIndex1 the other end of the interval
     */
    override fun addRowSelectionInterval(rowIndex0: Int, rowIndex1: Int) {
        super.addRowSelectionInterval(rowIndex0, rowIndex1)
        fixed?.addRowSelectionInterval(rowIndex0, rowIndex1)
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

    val selectedModelIndex: Int
        get() {
            val viewRowIndex = selectedRow
            if (viewRowIndex > -1) {
                return convertRowIndexToModel(viewRowIndex)
            }
            return -1
        }

    fun selectModelIndex(modelIndex: Int) {
        if (modelIndex > -1) {
            val viewRowIndex = convertRowIndexToView(modelIndex)
            setRowSelectionInterval(viewRowIndex, viewRowIndex)
        }
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
        fixed?.rowSorter = sorter
    }

    /**
     * Returns the outer container component of the fixed column table
     * @return Component
     */
    fun getContainerComponent(): Component {
        return this.containerComponent
    }

    override fun getColumn(identifier: Any): TableColumn {
        try {
            return super.getColumn(identifier)
        } catch (e: IllegalArgumentException) {
            if ( fixed != null ) return fixed!!.getColumn(identifier)
            throw e
        }
    }

    /**
     * Return th table column of the fixed or right hand side table
     * @param modelColumnIndex Model column index
     * @return TableColumn
     */
    fun getModelTableColumn(modelColumnIndex: Int): TableColumn {
        if (fixed != null && modelColumnIndex < fixedColumnsCount) {
            val i = fixed!!.convertColumnIndexToView(modelColumnIndex)
            return fixed!!.columnModel.getColumn(i)
        }
        // The registered model index values are not changed when column model is divided into fixed and not fixed part
        // So do not adapt the model column index here
        val i = super.convertColumnIndexToView(modelColumnIndex)
        return super.getColumnModel().getColumn(i)
    }

    /**
     * Return th table column of the fixed or right hand side table
     * @param i Column index
     * @return TableColumn
     */
    fun getTableColumn(i: Int): TableColumn {
        if (fixed != null && i < fixedColumnsCount) {
            return fixed!!.columnModel.getColumn(i)
        }
        return super.getColumnModel().getColumn(i - fixedColumnsCount)
    }

    /**
     * Return the user column of the event
     */
    fun getUserColumn(e: TableModelEvent): UserColumn? {
        if (e.column >= 0 && e.source.equals(this.model)){
            val modelIndex = convertColumnIndexToModel(e.column)
            if ( modelIndex > -1) {
                val hoTableModel = this.model as HOTableModel
                return hoTableModel.getDisplayedColumns()[modelIndex]
            }
        }
        return null
    }

}