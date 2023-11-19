package core.gui.comp.table;

import core.db.DBManager;
import core.gui.model.UserColumnController.ColumnModelId
import core.model.HOVerwaltung;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Basic ColumnModel for all UserColumnModels
 * 
 * @author Thorsten Dietz
 * @since 1.36
 */
abstract class HOTableModel protected constructor(
		val id: ColumnModelId,
		/** name of ColumnModell, shows in OptionsPanel  */
		private val name: String
) : AbstractTableModel() {

	/** id from ColumnModell, important for saving columns in db */
//	private val id: Int = 0

	/** name of ColumnModell, shows in OptionsPanel **/
//	private val name: String? = null

	/** count of displayed column **/
	private var displayedColumnsCount: Int = 0
		get () {
			if (field == 0) {
				if (columns != null) {
					for (column in columns!!) {
						if (column.isDisplay())
							field++
					}
				}
			}
			return field
		}

	/** all columns from this model **/
	var columns: Array<UserColumn>? = null

	/** only displayed columns **/
	var displayedColumns: Array<UserColumn>? = null
		get() {
			if (field == null) {

				val tmpList = mutableListOf<UserColumn>()
				if (columns != null) {
					for (col in columns!!) {
						if (col.isDisplay()) {
							if (col.index >= columnCount) {
								col.index = columnCount - 1
							}
							tmpList.add(col)
						}
					}
				}

				field = tmpList.toTypedArray()
			}
			return field
		}

	/** data of table **/
	protected var m_clData:Array<Array<Any>>? = null

	/** instance of the same class **/
	protected var instance: Int = 0

	/**
	 * return the language dependent name of this model
	 */
	override fun toString(): String {
		val tmp = HOVerwaltung.instance().getLanguageString(name)
		return if (instance == 0) tmp else (tmp + instance)
	}

	/**
	 * return all columnNames of displayed columns
	 * 
	 * @return String[]
	 */
	fun getColumnNames():Array<String> {
		return displayedColumns?.map { userColumn -> userColumn.columnName ?: "" }?.toTypedArray() ?: arrayOf()
	}

	/**
	 * return all tooltips of displayed columns
	 * 
	 * @return String[]
	 */
	fun getTooltips(): Array<String> {
		return displayedColumns?.map { userColumn -> userColumn.tooltip ?: "" }?.toTypedArray() ?: arrayOf()
	}

	/**
	 * Returns count of displayed columns redundant method, but this is
	 * overwritten method from AbstractTableModel
	 */
	 override fun getColumnCount():Int {
		return displayedColumnsCount
	}

	/**
	 * return value
	 *
	 * @param row
	 * @param column
	 *
	 * @return Object
	 */
	override fun getValueAt(row: Int, column: Int):Any? {
		if (m_clData != null) {
			return m_clData!![row][column]
		}

		return null
	}

	override fun getRowCount(): Int = m_clData?.size ?: 0

	override fun isCellEditable(row: Int, col: Int): Boolean = false

	override fun getColumnClass(columnIndex: Int): Class<*> {
		val obj = getValueAt(0, columnIndex)
		return obj?.javaClass ?: "".javaClass
	}

	override fun getColumnName(columnIndex: Int): String? {
		val columnNames = getColumnNames()
		return if (displayedColumnsCount > columnIndex) {
			columnNames[columnIndex]
		} else null
	}

	fun getValue(row: Int, columnName: String): Any? {
		val columnNames = getColumnNames()
		if (m_clData != null) {
			var i = 0
			while (i < columnNames.size && columnNames[i] != columnName) {
				i++
			}
			return m_clData!![row][i]
		}
		return null
	}

	@Override
	override fun setValueAt(value:Any, row: Int, column:Int) {
		m_clData!![row][column] = value;
	}

	/**
	 * 
	 * @param searchId
	 * @return
	 */
	protected open fun getColumnIndexOfDisplayedColumn(searchId: Int): Int {
		val tmp = displayedColumns
		if (tmp != null) {
			for (i in tmp.indices) {
				if (tmp[i].id == searchId) return i
			}
		}
		return -1
	}

	/**
	 * return the order of the column like old method getSpaltenreihenfolge
	 * 
	 * @return
	 */
	fun getColumnOrder():Array<IntArray> {
		val tmp = displayedColumns
		return if (tmp != null) {
			val order = Array(tmp.size) { IntArray(2) }
			for (i in order.indices) {
				order[i][0] = i
				order[i][1] = tmp[i].index
			}
			order
		} else {
			emptyArray()
		}
	}

	/**
	 * sets size in JTable
	 * 
	 * @param tableColumnModel
	 */
	fun setColumnsSize(tableColumnModel: TableColumnModel) {
		val tmpColumns = displayedColumns
		if (tmpColumns != null) {
			for (i in tmpColumns.indices) {
				tmpColumns[i].setSize(
					tableColumnModel.getColumn(tableColumnModel.getColumnIndex(i))
				)
			}
		}
	}

	protected abstract fun initData()

	/**
	 * return the array index from a Column id
	 * 
	 * @param searchId
	 * @return
	 */
	fun getPositionInArray(searchId: Int): Int {
		val tmpColumns = displayedColumns
		if (tmpColumns != null) {
			for (i in tmpColumns.indices) {
				if (tmpColumns[i].id == searchId) return i
			}
		}
		return -1
	}

	fun setCurrentValueToColumns(tmpColumns: Array<UserColumn>) {
		for (tmpColumn in tmpColumns) {
			if (columns != null) {
				for (column in columns!!) {
					if (column.id == tmpColumn.id) {
						column.index = tmpColumn.index
						column.preferredWidth = tmpColumn.preferredWidth
						break
					}
				}
			}
		}
	}

	@JvmOverloads
	fun restoreUserSettings(table: JTable, offset: Int = 0) {
		for (i in 0 until table.columnCount) {
			table.columnModel.getColumn(i).setIdentifier(i + offset)
		}
		Arrays.stream(columns)
			.skip(offset.toLong())
			.limit(table.columnCount.toLong())
			.filter { obj: UserColumn -> obj.isDisplay() }
			.sorted(Comparator.comparingInt { obj: UserColumn -> obj.index })
			.forEach { i: UserColumn -> setColumnSettings(i, table, offset) }
	}

	fun restoreUserSettings(table: FixedColumnsTable) {
		restoreUserSettings(table.fixedTable, 0)
		restoreUserSettings(table.scrollTable, table.fixedColumnsCount)
	}

	/**
	 * Set column order and width
	 *
	 * @param userColumn user column holding user's settings
	 * @param table      the table object
	 */
	private fun setColumnSettings(userColumn: UserColumn, table: JTable, offset: Int) {
		val column = table.getColumn(userColumn.id)
		column.setPreferredWidth(userColumn.preferredWidth)
		val index = table.columnModel.getColumnIndex(userColumn.id)
		if (index != userColumn.index -offset) {
			table.moveColumn(index, userColumn.index -offset)
		}
	}

	/**
	 * Save the user settings of the table. User selected width and column indexes are saved in user column model
	 * which is stored in database table UserColumnTable
	 *
	 * @param table table object
	 */
	fun storeUserSettings(table: JTable) {
		val changed = storeUserSettings(table, 0)
		if (changed) {
			DBManager.saveHOColumnModel(this)
		}
	}

	private fun storeUserSettings(table: JTable, offset: Int): Boolean {
		var changed = false
		// column order and width
		val tableColumnModel = table.columnModel
		val modelColumnCount = this.columnCount
		for (i in 0 until modelColumnCount) {
			if (i < offset) continue  // skip fixed columns in case of scroll table
			if (offset == 0 && i >= table.columnCount) break // fixed columns exceeded
			val column = columns?.get(i)
			val index = table.convertColumnIndexToView(i)
			if (column != null) {
				if (column.isDisplay()) {
					if (column.index != index + offset) {
						changed = true
						column.index = (index + offset)
					}
					if (column.preferredWidth != tableColumnModel.getColumn(index).width) {
						changed = true
						column.preferredWidth = tableColumnModel.getColumn(index).width
					}
				}
			}
		}
		return changed
	}

	fun storeUserSettings(table: FixedColumnsTable) {
		var changed = storeUserSettings(table.fixedTable, 0)
		changed = changed || storeUserSettings(table.scrollTable, table.fixedColumnsCount)
		if (changed) {
			DBManager.saveHOColumnModel(this)
		}
	}

	open fun userCanDisableColumns(): Boolean = false
}
