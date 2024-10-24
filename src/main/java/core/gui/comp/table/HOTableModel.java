package core.gui.comp.table;

import core.db.DBManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.UserColumnController;
import core.model.TranslationFacility;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static java.lang.Math.max;

/**
 * Basic ColumnModel for all UserColumnModels
 * 
 * @author Thorsten Dietz
 * @since 1.36
 */
public abstract class HOTableModel extends AbstractTableModel {

	@Serial
	private static final long serialVersionUID = -207230110294902139L;

	/**
	 * Identifier of the column model.
	 * It is used for saving columns in db
	 */
	private final int id;

	/** Name of the column model, shows in OptionsPanel **/
	private final String name;

	/** Count of displayed column **/
	private int displayedColumnsCount;

	/** All columns of this model **/
	protected UserColumn[] columns;

	/** Only displayed columns **/
	protected UserColumn[] displayedColumns;

	/** Data of table **/
	protected Object[][] m_clData;

	/** Table component **/
	private JTable table;

	/**
	 * Constructor
	 * 
	 * @param id Model id
	 * @param name Model name
	 */
	protected HOTableModel(UserColumnController.ColumnModelId id, String name) {
		this.id = id.getValue();
		this.name = name;
	}

	/**
	 * Return all columns of the model
	 * 
	 * @return UserColumn[]
	 */
	public final UserColumn[] getColumns() {
		return columns;
	}

	/**
	 * Return model's id
	 * @return id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Return the language dependent name of this model
	 */
	@Override
	public String toString() {
		return TranslationFacility.tr(name);
	}

	/**
	 * Return all column names of displayed columns
	 * 
	 * @return String[]
	 */
	public String[] getColumnNames() {
		final String[] columnNames = new String[getDisplayedColumnCount()];
		for (int i = 0; i < getDisplayedColumns().length; i++)
			columnNames[i] = getDisplayedColumns()[i].getColumnName();

		return columnNames;
	}

	/**
	 * Return all tooltips of displayed columns
	 * 
	 * @return String[]
	 */
	public String[] getTooltips() {
		final String[] tooltips = new String[getDisplayedColumnCount()];
		for (int i = 0; i < getDisplayedColumns().length; i++)
			tooltips[i] = getDisplayedColumns()[i].getTooltip();
		return tooltips;
	}

	/**
	 * Return all displayed columns
	 * 
	 * @return UserColumn[]
	 */
	public UserColumn[] getDisplayedColumns() {

		if (displayedColumns == null) {
			final int columncount = getDisplayedColumnCount();
			displayedColumns = new UserColumn[columncount];
			int currentIndex = 0;
			for (UserColumn column : columns) {

				if (column.isDisplay()) {
					displayedColumns[currentIndex] = column;

					if (column.getIndex() >= columncount)
						displayedColumns[currentIndex].setIndex(columncount - 1);
					currentIndex++;
				} // column is displayed
			} // for
		}

		return displayedColumns;
	}

	/**
	 * Return count of displayed columns
	 * 
	 * @return int
	 */
	private int getDisplayedColumnCount() {
		if (displayedColumnsCount == 0) {
			for (UserColumn column : columns) {
				if (column.isDisplay())
					displayedColumnsCount++;
			}
		}
		return displayedColumnsCount;
	}

	/**
	 * Returns count of displayed columns redundant method, but this is
	 * overwritten method from AbstractTableModel
	 */
	@Override
	public int getColumnCount() {
		return getDisplayedColumnCount();
	}

	/**
	 * Return value of one table cell
	 * 
	 * @param row Row number
	 * @param column Column number
	 * 
	 * @return Object
	 */
	@Override
	public final Object getValueAt(int row, int column) {
		if (m_clData != null && m_clData.length>row) {
			return m_clData[row][column];
		}

		return null;
	}

	/**
	 * Return row count
	 * @return int
	 */
	@Override
	public final int getRowCount() {
		return (m_clData != null) ? m_clData.length : 0;
	}

	/**
	 * Return class of a table column
	 * @param columnIndex  the column being queried
	 * @return Class</?>
	 */
    @Override
	public final Class<?> getColumnClass(int columnIndex) {
		final Object obj = getValueAt(0, columnIndex);

		if (obj != null) {
			return obj.getClass();
		}

		return "".getClass();
	}

	/**
	 * Return the name of a table column
	 * @param columnIndex  the column being queried
	 * @return String
	 */
	@Override
	public final String getColumnName(int columnIndex) {
		if (getDisplayedColumnCount() > columnIndex) {
			return getDisplayedColumns()[columnIndex].getColumnName();
		}

		return null;
	}

	/**
	 * Set the value of a table cell
	 * @param value   value to assign to cell
	 * @param row   row of cell
	 * @param column  column of cell
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {
		m_clData[row][column] = value;
		fireTableCellUpdated(row,column);
	}

	/**
	 * Abstract init data method has to be provided by subclass
	 */
	protected abstract void initData();

	/**
	 * Return the array index from a Column id
	 */
	public int getPositionInArray(int searchid) {
		final UserColumn[] tmpColumns = getDisplayedColumns();
		for (int i = 0; i < tmpColumns.length; i++) {
			if (tmpColumns[i].getId() == searchid)
				return i;
		}
		return -1;
	}

	/**
	 * Get the table column width and index from user column settings stored in the database
	 * @param table Table
	 * @param offset Column's offset in model (in case of FixedColumnTable)
	 */
	private void getUserColumnSettings(JTable table, int offset) {
		// Restore column order and width settings
		Arrays.stream(getDisplayedColumns())
				.skip(offset)
				.limit(table.getColumnCount())
				.sorted(Comparator.comparingInt(UserColumn::getIndex))
				.forEach(i -> getColumnSettings(i, table, offset));
	}

	/**
	 * Get column order and width from user column
	 *
	 * @param userColumn User column holding user's settings
	 * @param table      Table object
	 * @param offset 	 Column's offset in model (in case of FixedColumnTable)
	 */
	private void getColumnSettings(UserColumn userColumn, JTable table, int offset) {
		var column = table.getColumn(userColumn.getId());
		column.setPreferredWidth(userColumn.getPreferredWidth());
		var index = table.getColumnModel().getColumnIndex(userColumn.getId());
		if ( index != userColumn.getIndex()-offset) {
			table.moveColumn(index, max(0, userColumn.getIndex()-offset));
		}
	}

	/**
	 * Save the user settings of the table. User selected width and column indexes are saved in user column model
	 * which is stored in database table UserColumnTable
	 *
	 * @param table Table object
	 * @param offset 	 Column's offset in model (in case of FixedColumnTable)
	 */
	private boolean setUserColumnSettings(JTable table, int offset) {
		boolean changed = false;
		// column order and width
		var tableColumnModel = table.getColumnModel();
		var modelColumnCount = this.getColumnCount();
		for (int i = 0; i < modelColumnCount; i++) {
			if (i < offset) continue;                                // skip fixed columns in case of scroll table
			if (offset == 0 && i >= table.getColumnCount()) break;   // fixed columns exceeded

			var column = this.getColumns()[i];
			var index = table.convertColumnIndexToView(i);
			if (column.isDisplay()) {
				if (column.getIndex() != index + offset) {
					changed = true;
					column.setIndex(index + offset);
				}
				if (column.getPreferredWidth() != tableColumnModel.getColumn(index).getWidth()) {
					changed = true;
					column.setPreferredWidth(tableColumnModel.getColumn(index).getWidth());
				}
			}
		}
		return changed;
	}

	/**
	 * Set user column settings from the table instance
	 * @param table Table object
	 * @return True if one user setting is changed
	 * 		   False, if no user settings are changed
	 */
	private boolean setUserColumnSettings(JTable table) {
		if(table instanceof FixedColumnsTable fixedColumnstable) {
            var changed =  setUserColumnSettings(fixedColumnstable.getFixedTable(), 0);
			if (setUserColumnSettings(fixedColumnstable, fixedColumnstable.getFixedColumnsCount()) ){
				changed = true;
			}
			return  changed;
		}
		return setUserColumnSettings(table,0);
	}

	public boolean userCanDisableColumns() {
		return false;
	}

	/**
	 * Initialize the table object with data from the model
	 * @param table Table object
	 */
	public void initTable(JTable table) {
		this.table = table;
		var columnModel = table.getColumnModel();
		ToolTipHeader header = new ToolTipHeader(columnModel);
		header.setToolTipStrings(getTooltips());
		header.setToolTipText("");
		table.setTableHeader(header);
		table.setModel(this);

		var displayedColumns = getDisplayedColumns();
		if (table instanceof FixedColumnsTable fixedColumnstable) {
			for ( int i=0; i<fixedColumnstable.getFixedColumnsCount(); i++){
				var tm = displayedColumns[i];
				var cm = fixedColumnstable.getFixedTable().getColumnModel().getColumn(i);
				cm.setIdentifier(tm.getId());
				cm.setMinWidth(tm.minWidth);
			}
			for ( int i=fixedColumnstable.getFixedColumnsCount(); i < displayedColumnsCount;  i++){
				var tm = displayedColumns[i];
				var cm = fixedColumnstable.getColumnModel().getColumn(i-fixedColumnstable.getFixedColumnsCount());
				cm.setIdentifier(tm.getId());
				cm.setMinWidth(tm.minWidth);
			}
			getUserColumnSettings(fixedColumnstable.getFixedTable(), 0);
			getUserColumnSettings(table, fixedColumnstable.getFixedColumnsCount());
		}
		else {
			for (int i=0; i<displayedColumnsCount; i++){
				var tm = displayedColumns[i];
				var cm = columnModel.getColumn(i);
				cm.setIdentifier(tm.getId());
				cm.setMinWidth(tm.minWidth);
			}
			getUserColumnSettings(table,0);
		}
		var rowSorter = new TableRowSorter<>(this);
		getRowOrderSettings(rowSorter);
		table.setRowSorter(rowSorter);
		table.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
	}

	/**
	 * Store user table settings in the database if they were changed by the user
	 */
	public void storeUserSettings(){
		if (table == null) return;
		var changed = setUserColumnSettings(table);

		RowSorter<HOTableModel> sorter = (RowSorter<HOTableModel>) table.getRowSorter();
		if (sorter != null){
			if ( setRowOrderSettings(sorter) ) {
				changed = true;
			}
		}
		if (changed){
			DBManager.instance().saveHOColumnModel(this);
		}
	}

	/**
	 * Get row order from user columns and restore it to the given row sorter
	 *
	 * @param rowSorter Row sorter
	 */
	private void getRowOrderSettings(RowSorter<HOTableModel> rowSorter) {
		// Restore row order setting
		var sortKeys = new ArrayList<RowSorter.SortKey>();
		var sortColumns =  Arrays.stream(this.columns).filter(i->i.sortPriority != null).sorted(Comparator.comparingInt(UserColumn::getSortPriority)).toList();
		if (!sortColumns.isEmpty()) {
			var userColumns = Arrays.stream(this.columns).toList();
			for (var col : sortColumns) {
				var index = userColumns.indexOf(col);
				var sortKey = new RowSorter.SortKey(index, col.getSortOrder());
				sortKeys.add(sortKey);
			}
		}
		rowSorter.setSortKeys(sortKeys);
	}

	/**
	 * Set user columns sort priority and order from given row sorter
	 * @param sorter Row sorter
	 * @return True if one user setting is changed
	 * 		   False, if no user settings are changed
	 */
	private boolean setRowOrderSettings(RowSorter<HOTableModel> sorter) {
		var changed = false;
		var rowSortKeys = sorter.getSortKeys();
		for (int i = 0; i < this.columns.length; i++) {
			int finalI = i;
			var rowSortKey = rowSortKeys.stream().filter(k -> k.getColumn() == finalI).findFirst();
			var userColumn = this.columns[i];
			if (rowSortKey.isPresent() && rowSortKey.get().getSortOrder() != SortOrder.UNSORTED) {
				var k = rowSortKey.get();
				var priority = rowSortKeys.indexOf(k);
				if (userColumn.getSortPriority() == null || !userColumn.getSortPriority().equals(priority) ||
						!userColumn.getSortOrder().equals(k.getSortOrder())) {
					userColumn.setSortOrder(k.getSortOrder());
					userColumn.setSortPriority(priority);
					changed = true;
				}
			} else if (userColumn.getSortPriority() != null) {
				userColumn.setSortPriority(null);
				userColumn.setSortOrder(null);
				changed = true;
			}
		}
		return changed;
	}

	/**
	 * Convert model index to row number
	 * @param modelIndex Model index
	 * @return int, Row number index (-1, if model index is invalid)
	 */
	public int convertModelIndexToRow(int modelIndex) {
		if (modelIndex>=0 && modelIndex < this.getRowCount()){
			var rowSorter = (RowSorter<HOTableModel>)table.getRowSorter();
			if ( rowSorter != null ) {
				return rowSorter.convertRowIndexToView(modelIndex);
			}
			return modelIndex;
		}
		return -1;
	}

	/**
	 * Convert row number to model index
	 * @param rowIndex Table row number
	 * @return int, model index (-1, if row index is invalid)
	 */
	public int convertRowToModelIndex(int rowIndex) {
		if (rowIndex>=0 && rowIndex < this.getRowCount()){
			var rowSorter = (RowSorter<HOTableModel>)table.getRowSorter();
			if ( rowSorter != null ) {
				return rowSorter.convertRowIndexToModel(rowIndex);
			}
			return rowIndex;
		}
		return -1;
	}
}