package core.gui.comp.table

import core.db.DBManager
import core.gui.comp.entry.CheckBoxTableEntry
import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.gui.model.UserColumnController.ColumnModelId
import core.model.TranslationFacility
import core.util.HOLogger
import java.util.*
import javax.swing.JTable
import javax.swing.RowSorter
import javax.swing.SortOrder
import javax.swing.event.RowSorterEvent
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableColumn
import javax.swing.table.TableRowSorter
import kotlin.math.max

/**
 * Basic ColumnModel for all UserColumnModels
 *
 * @author Thorsten Dietz
 * @since 1.36
 */
abstract class HOTableModel protected constructor(
    id: ColumnModelId,
    /** Name of the column model, shows in OptionsPanel  */
    private val name: String
) : AbstractTableModel() {
    /**
     * Return model's id
     * @return id
     */
    /**
     * Identifier of the column model.
     * It is used for saving columns in db
     */
	@JvmField
	val id: Int = id.value

    /**
     * Return all columns of the model
     *
     * @return UserColumn[]
     */
    /** All columns of this model  */
    lateinit var columns: Array<UserColumn>
        protected set

    /** Only displayed columns  */
    protected var _displayedColumns: Array<UserColumn>? = null

    /** Data of table  */
    @JvmField
	protected var m_clData: Array<Array<Any?>>? = null


    // TODO: Check if a list of tables is necessary (See SpielerMatchesTable, which uses two instances of same table model type)
    /** Table component  */
    protected var tables: MutableList<JTable> = ArrayList()

    var selectedRow: Int = -1

    /**
     * Return the language dependent name of this model
     */
    override fun toString(): String {
        return TranslationFacility.tr(name)
    }

    val columnNames: Array<String?>
        /**
         * Return all column names of displayed columns
         *
         * @return String[]
         */
        get() {
            val columnNames =
                arrayOfNulls<String>(displayedColumnCount)
            for (i in getDisplayedColumns().indices) columnNames[i] = getDisplayedColumns()[i].getColumnName()
            return columnNames
        }

    val tooltips: Array<String?>
        /**
         * Return all tooltips of displayed columns
         *
         * @return String[]
         */
        get() {
            val tooltips = arrayOfNulls<String>(displayedColumnCount)
            for (i in getDisplayedColumns().indices) tooltips[i] = getDisplayedColumns()[i].getTooltip()
            return tooltips
        }

    /**
     * Return all displayed columns
     *
     * @return UserColumn[]
     */
    fun getDisplayedColumns(): Array<UserColumn> {
        if (_displayedColumns == null) {
            _displayedColumns = columns.filter { col -> col.isDisplay }.toTypedArray()
        }
        return _displayedColumns as Array<UserColumn>
    }

    private val displayedColumnCount: Int
        /**
         * Return count of displayed columns
         *
         * @return int
         */
        get() {
            return getDisplayedColumns().size
        }

    /**
     * Returns count of displayed columns redundant method, but this is
     * overwritten method from AbstractTableModel
     */
    override fun getColumnCount(): Int {
        return getDisplayedColumns().size
    }

    /**
     * Return value of one table cell
     *
     * @param row Row number
     * @param column Column number
     *
     * @return Object
     */
    override fun getValueAt(row: Int, column: Int): Any? {
        if (m_clData != null && m_clData!!.size > row && row > -1 && column > -1 && column < m_clData!![row].size ) {
            val ret =  m_clData!![row][column]
            if ( ret is CheckBoxTableEntry){
                return ret.value
            }
            return ret
        }
        return null
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        val cols = getDisplayedColumns()
        if (column > -1 && column < cols.size) {
            return cols[column].isEditable
        }
        return false
    }

    /**
     * Return row count
     * @return int
     */
    override fun getRowCount(): Int {
        return m_clData?.size ?: 0
    }

    /**
     * Return class of a table column
     * @param columnIndex  the column being queried
     * @return Class?>
     */
    override fun getColumnClass(columnIndex: Int): Class<*> {
        val obj = getValueAt(0, columnIndex)

        if (obj != null) {
            if ( obj is CheckBoxTableEntry) {
                return Boolean::class.java
            }
            return obj.javaClass
        }

        return "".javaClass
    }

    /**
     * Return the name of a table column
     * @param columnIndex  the column being queried
     * @return String
     */
    override fun getColumnName(columnIndex: Int): String? {
        if (displayedColumnCount > columnIndex && columnIndex > -1) {
            return getDisplayedColumns()[columnIndex].getColumnName()
        }
        return null
    }

    /**
     * Set the value of a table cell
     * @param value   value to assign to cell
     * @param row   row of cell
     * @param column  column of cell
     */
    override fun setValueAt(value: Any, row: Int, column: Int) {
        if (m_clData != null && m_clData!!.size > row && row > -1 && column > -1 && column < m_clData!![row].size ) {
            val ret =  m_clData!![row][column]
            if ( ret is CheckBoxTableEntry){
                ret.value = value as Boolean?
            }
            m_clData!![row][column] = value
        }
        for (table in tables) {
            fireTableCellUpdated(table.convertRowIndexToView(row), table.convertColumnIndexToView(column))
        }
    }

    /**
     * Abstract init data method has to be provided by subclass
     */
    protected abstract fun initData()

    /**
     * Return the array index from a Column id
     */
    fun getPositionInArray(searchId: Int): Int {
        val tmpColumns = getDisplayedColumns()
        for (i in tmpColumns.indices) {
            if (tmpColumns[i].getId() == searchId) return i
        }
        return -1
    }

    /**
     * Get the table column width and index from user column settings stored in the database
     *
     * @param table Table
     */
    private fun getUserColumnSettings(table: JTable) {
        // Restore column order and width settings
        Arrays.stream(getDisplayedColumns())
            .sorted(Comparator.comparingInt { obj: UserColumn? -> obj!!.getIndex() })
            .forEach { i: UserColumn -> getColumnSettings(i, table) }
    }

    /**
     * Get column order and width from user column
     *
     * @param userColumn User column holding user's settings
     * @param table      Table object
     */
    private fun getColumnSettings(userColumn: UserColumn, table: JTable) {
        val viewColumn = table.getColumn(userColumn.getId())
        viewColumn.preferredWidth = userColumn.getPreferredWidth()
        moveColumn(table, userColumn)
    }

    private fun moveColumn(table: JTable, userColumn: UserColumn) {
        if (table is FixedColumnsTable) {
            val targetIndex = userColumn.getIndex() - table.fixedColumnsCount
            if (targetIndex >= 0) {
                try {
                    val index = table.getColumnModel().getColumnIndex(userColumn.getId())
                    if (index != targetIndex) {
                        table.moveColumn(index, targetIndex)
                    }
                } catch (e: IllegalArgumentException) {
                    HOLogger.instance().info(
                        this.javaClass,
                        "Cannot move column to stored index " + userColumn.id + " " + userColumn.getColumnName() + " index=" + userColumn.getIndex() + ": " + e.message
                    )
                }
            }
        } else {
            val index = table.columnModel.getColumnIndex(userColumn.getId())
            if (index != userColumn.getIndex()) {
                table.moveColumn(index, max(0.0, userColumn.getIndex().toDouble()).toInt())
            }
        }
    }

    /**
     * Set user column settings from the table instance
     * @param table Table object
     * @return True if one user setting is changed
     * False, if no user settings are changed
     */
    private fun setUserColumnSettings(table: JTable): Boolean {
        var changed = false
        val displayedColumns = getDisplayedColumns()
        for (index in 0..<displayedColumns.size) {
            val tableColumn = getTableColumn(table, index)
            val modelColumn = displayedColumns[tableColumn.modelIndex]

            if (modelColumn.getIndex() != index) {
                changed = true
                modelColumn.setIndex(index)
            }

            val tableColumnWidth = tableColumn.width
            if (modelColumn.getPreferredWidth() != tableColumnWidth) {
                changed = true
                modelColumn.setPreferredWidth(tableColumnWidth)
            }
        }
        return changed
    }

    /**
     * User can disable columns
     * @return boolean
     */
    fun userCanDisableColumns(): Boolean {
        return true
    }

    /**
     * Initialize the table object with data from the model
     * Todo: Think about making HOTableModel supporting only FixedColumnsTable (JTable==FixedColumnsTable(0 fixed columns))
     * Then initTable could be part of FixedColumnsTable (HOTable)
     * @param table Table object
     */
    fun initTable(table: JTable) {
        tables.add(table)
        if (table !is FixedColumnsTable) {
            val columnModel = table.columnModel
            val header = ToolTipHeader(columnModel)
            header.setToolTipStrings(tooltips)
            header.toolTipText = ""
            table.tableHeader = header
            table.model = this
        }

        // Copy user columns' identifiers to table's columns
        val displayedColumns = getDisplayedColumns()
        var i=0
        for (userColumn in displayedColumns) {
            val tableColumn = getTableColumn(table, i++)
            tableColumn.identifier = userColumn.getId()
            if (userColumn.isHidden){
                tableColumn.preferredWidth=0
                tableColumn.minWidth=0
                tableColumn.maxWidth=0
            }
        }
        getUserColumnSettings(table)

        val rowSorter = TableRowSorter(this)
        rowSorter.addRowSorterListener { e: RowSorterEvent ->
            // Restore the previous selection when table rows were sorted
            // Sorting changed
            when (e.type) {
                RowSorterEvent.Type.SORT_ORDER_CHANGED -> selectedRow = table.selectedRow
                RowSorterEvent.Type.SORTED -> {
                    if (selectedRow > -1) {
                        val modelIndex = e.convertPreviousRowIndexToModel(selectedRow)
                        if (modelIndex > -1) {
                            val newSelectedRow = table.convertRowIndexToView(modelIndex)
                            table.setRowSelectionInterval(newSelectedRow, newSelectedRow)
                        }
                        selectedRow = -1
                    }
                }
            }
        }
        getRowOrderSettings(rowSorter)
        table.rowSorter = rowSorter
        table.setDefaultRenderer(Any::class.java, HODefaultTableCellRenderer())
    }

    private fun getTableColumn(table: JTable, i: Int): TableColumn {
        if (table is FixedColumnsTable) {
            return table.getTableColumn(i)
        }
        return table.columnModel.getColumn(i)
    }

    /**
     * Store user table settings in the database if they were changed by the user
     */
    fun storeUserSettings() {
        for (table in tables) {
            var changed = setUserColumnSettings(table)
            val sorter = table.rowSorter as RowSorter<HOTableModel>
            if (setRowOrderSettings(sorter)) {
                changed = true
            }
            if (changed) {
                DBManager.instance().saveHOColumnModel(this)
                break // do not override with next table's setting
                // if more than one table changes setting the first one is the winner
            }
        }
    }

    /**
     * Get row order from user columns and restore it to the given row sorter
     * @param rowSorter Row sorter
     */
    private fun getRowOrderSettings(rowSorter: RowSorter<HOTableModel>) {
        // Restore row order setting
        val sortKeys = ArrayList<RowSorter.SortKey>()
        val sortColumns = Arrays.stream(this.columns).filter { i: UserColumn -> i.sortPriority != null }
            .sorted(Comparator.comparingInt { obj: UserColumn -> obj.getSortPriority() }).toList()
        if (sortColumns.isNotEmpty()) {
            val userColumns = Arrays.stream(this.columns).toList()
            for (col in sortColumns) {
                val index = userColumns.indexOf(col)
                if (index > -1) {
                    val sortKey = RowSorter.SortKey(index, col.getSortOrder())
                    if (sortKey.column > -1 && sortKey.column < rowSorter.model.columnCount) {
                        sortKeys.add(sortKey)
                    }
                }
            }
        }
        rowSorter.sortKeys = sortKeys
    }

    /**
     * Set user columns sort priority and order from given row sorter
     * @param sorter Row sorter
     * @return True if one user setting is changed
     * False, if no user settings are changed
     */
    private fun setRowOrderSettings(sorter: RowSorter<HOTableModel>): Boolean {
        var changed = false
        val rowSortKeys = sorter.sortKeys
        for (i in columns.indices) {
            val finalI = i
            val rowSortKey = rowSortKeys.stream().filter { k: RowSorter.SortKey -> k.column == finalI }.findFirst()
            val userColumn = columns[i]
            if (rowSortKey.isPresent && rowSortKey.get().sortOrder != SortOrder.UNSORTED) {
                val k = rowSortKey.get()
                val priority = rowSortKeys.indexOf(k)
                if (userColumn.getSortPriority() == null || (userColumn.getSortPriority() != priority) || (userColumn.getSortOrder() != k.sortOrder)) {
                    userColumn.setSortOrder(k.sortOrder)
                    userColumn.setSortPriority(priority)
                    changed = true
                }
            } else if (userColumn.getSortPriority() != null) {
                userColumn.setSortPriority(null)
                userColumn.setSortOrder(null)
                changed = true
            }
        }
        return changed
    }

    val table: JTable?
        /**
         * Returns the primary table
         * @return JTable
         */
        get() {
            if (tables.isNotEmpty()) return tables[0]
            return null
        }
}