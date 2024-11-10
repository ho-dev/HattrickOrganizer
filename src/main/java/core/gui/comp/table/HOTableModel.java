package core.gui.comp.table;

import core.db.DBManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.UserColumnController;
import core.model.TranslationFacility;
import core.util.HOLogger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
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
	protected JTable table;

	int selectedRow = -1;
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
		for (int i = 0; i < getDisplayedColumns().length; i++) columnNames[i] = getDisplayedColumns()[i].getColumnName();
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

	@Override
	public boolean isCellEditable(int row, int column) {
		return columns[column].isEditable();
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
		fireTableCellUpdated(table.convertRowIndexToView(row),table.convertColumnIndexToView(column));
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
	 *
	 * @param table Table
	 */
	private void getUserColumnSettings(JTable table) {
		// Restore column order and width settings
		Arrays.stream(getDisplayedColumns())
				.sorted(Comparator.comparingInt(UserColumn::getIndex))
				.forEach(i -> getColumnSettings(i, table));
	}

	/**
	 * Get column order and width from user column
	 *
	 * @param userColumn User column holding user's settings
	 * @param table      Table object
	 */
	private void getColumnSettings(UserColumn userColumn, JTable table) {
		var viewColumn = table.getColumn(userColumn.getId());
		viewColumn.setPreferredWidth(userColumn.getPreferredWidth());
		moveColumn(table, userColumn);
	}

	private void moveColumn(JTable table, UserColumn userColumn) {
		if (table instanceof FixedColumnsTable fixedColumnsTable) {
			var targetIndex = userColumn.getIndex() - fixedColumnsTable.getFixedColumnsCount();
			if (targetIndex > 0) {
				try {
					var index = fixedColumnsTable.getColumnModel().getColumnIndex(userColumn.getId());
					if (index != targetIndex) {
						table.moveColumn(index, targetIndex);
					}
				}
				catch (IllegalArgumentException e) {
					HOLogger.instance().info(this.getClass(), "Cannot move column to stored index " + userColumn.id + " " + userColumn.getColumnName() +  " index=" + userColumn.getIndex() + ": " + e.getMessage());
				}
			}
		} else {
			var index = table.getColumnModel().getColumnIndex(userColumn.getId());
			if (index != userColumn.getIndex()) {
				table.moveColumn(index, max(0, userColumn.getIndex()));
			}
		}
	}

	/**
	 * Set user column settings from the table instance
	 * @param table Table object
	 * @return True if one user setting is changed
	 * 		   False, if no user settings are changed
	 */
	private boolean setUserColumnSettings(JTable table) {
		boolean changed = false;
		for (int index = 0; index < table.getColumnCount(); index++) {
			var tableColumn = getTableColumn(table, index);
			var modelColumn = this.columns[tableColumn.getModelIndex()];

			if (!modelColumn.isDisplay()) {
				modelColumn.setDisplay(true);
				changed = true;
			}
			if (modelColumn.getIndex() != index) {
				changed = true;
				modelColumn.setIndex(index);
			}

			var tableColumnWidth = tableColumn.getWidth();
			if (modelColumn.getPreferredWidth() != tableColumnWidth) {
				changed = true;
				modelColumn.setPreferredWidth(tableColumnWidth);
			}
		}
		return changed;
	}

	/**
	 * User can disable columns
	 * @return boolean
	 */
	public boolean userCanDisableColumns() {
		return true;
	}

	/**
	 * Initialize the table object with data from the model
	 * Todo: Think about making HOTableModel supporting only FixedColumnsTable (JTable==FixedColumnsTable(0 fixed columns))
	 * @param table Table object
	 */
	public void initTable(JTable table) {
		this.table = table;
		if ( !(table instanceof FixedColumnsTable)) {
			var columnModel = table.getColumnModel();
			ToolTipHeader header = new ToolTipHeader(columnModel);
			header.setToolTipStrings(getTooltips());
			header.setToolTipText("");
			table.setTableHeader(header);
			table.setModel(this);
		}

		// Copy user columns' identifiers to table's columns
		var displayedColumns = getDisplayedColumns();
		for (int i=0; i<displayedColumnsCount; i++){
			var userColumn = displayedColumns[i];
			var tableColumn = getTableColumn(table, i);
			tableColumn.setIdentifier(userColumn.getId());
		}
		getUserColumnSettings(table);

		var rowSorter = new TableRowSorter<>(this);
		rowSorter.addRowSorterListener(e -> {
			// Restore the previous selection when table rows were sorted
            // Sorting changed
			switch (e.getType()){
				case SORT_ORDER_CHANGED ->  selectedRow = table.getSelectedRow();
                case SORTED -> {
                    if ( selectedRow > -1)  {
						var modelIndex = e.convertPreviousRowIndexToModel(selectedRow);
						if ( modelIndex > -1) {
							var newSelectedRow = table.convertRowIndexToView(modelIndex);
							table.setRowSelectionInterval(newSelectedRow, newSelectedRow);
						}
					}
                }
            }
        });
		getRowOrderSettings(rowSorter);
		table.setRowSorter(rowSorter);
		table.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
	}

	private TableColumn getTableColumn(JTable table, int i) {
		if ( table instanceof FixedColumnsTable fixedColumnstable) { return fixedColumnstable.getTableColumn(i); }
		return table.getColumnModel().getColumn(i);
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
}