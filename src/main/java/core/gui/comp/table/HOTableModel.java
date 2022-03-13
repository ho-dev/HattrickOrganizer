package core.gui.comp.table;

import core.db.DBManager;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.util.HODateTime;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Basic ColumnModel for all UserColumnModels
 * 
 * @author Thorsten Dietz
 * @since 1.36
 */
public abstract class HOTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -207230110294902139L;

	/** id from ColumnModell, important for saving columns in db */
	private int id;

	/** name of ColumnModell, shows in OptionsPanel **/
	private String name;

	/** count of displayed column **/
	private int displayedColumnsCount;

	/** all columns from this model **/
	protected UserColumn[] columns;

	/** only displayed columns **/
	protected UserColumn[] displayedColumns;

	/** data of table **/
	protected Object[][] m_clData;

	/** instance of the same class **/
	protected int instance;

	/**
	 * constructor
	 * 
	 * @param id
	 * @param name
	 */
	protected HOTableModel(UserColumnController.ColumnModelId id, String name) {
		this.id = id.getValue();
		this.name = name;
	}

	/**
	 * return all columns of the model
	 * 
	 * @return UserColumn[]
	 */
	public final UserColumn[] getColumns() {
		return columns;
	}

	/**
	 * 
	 * @return id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * return the language dependent name of this model
	 */
	@Override
	public String toString() {
		String tmp = HOVerwaltung.instance().getLanguageString(name);
		return (instance == 0) ? tmp : (tmp + instance);
	}

	/**
	 * return all columnNames of displayed columns
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
	 * return all tooltips of displayed columns
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
	 * return all displayed columns
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
	 * return count of displayed columns
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
	 * return return count of displayed columns redundant method, but this is
	 * overwritten method from AbstractTableModel
	 */
	@Override
	public int getColumnCount() {
		return getDisplayedColumnCount();
	}

	/**
	 * return value
	 * 
	 * @param row
	 * @param column
	 * 
	 * @return Object
	 */
	@Override
	public final Object getValueAt(int row, int column) {
		if (m_clData != null) {
			return m_clData[row][column];
		}

		return null;
	}

	@Override
	public final int getRowCount() {
		return (m_clData != null) ? m_clData.length : 0;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public final Class<?> getColumnClass(int columnIndex) {
		final Object obj = getValueAt(0, columnIndex);

		if (obj != null) {
			return obj.getClass();
		}

		return "".getClass();
	}

	@Override
	public final String getColumnName(int columnIndex) {
		if (getDisplayedColumnCount() > columnIndex) {
			return getColumnNames()[columnIndex];
		}

		return null;
	}

	public final Object getValue(int row, String columnName) {
		if (m_clData != null) {
			int i = 0;

			while ((i < getColumnNames().length) && !getColumnNames()[i].equals(columnName)) {
				i++;
			}

			return m_clData[row][i];
		}

		return null;
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		m_clData[row][column] = value;
	}

	/**
	 * 
	 * @param searchId
	 * @return
	 */
	protected int getColumnIndexOfDisplayedColumn(int searchId) {
		UserColumn[] tmp = getDisplayedColumns();
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].getId() == searchId)
				return i;
		}
		return -1;
	}

	/**
	 * return the order of the column like old method getSpaltenreihenfolge
	 * 
	 * @return
	 */
	public int[][] getColumnOrder() {
		UserColumn[] tmp = getDisplayedColumns();
		int[][] order = new int[tmp.length][2];
		for (int i = 0; i < order.length; i++) {
			order[i][0] = i;
			order[i][1] = tmp[i].getIndex();
		}
		return order;
	}

	/**
	 * sets size in JTable
	 * 
	 * @param tableColumnModel
	 */
	public void setColumnsSize(TableColumnModel tableColumnModel) {
		final UserColumn[] tmpColumns = getDisplayedColumns();
		for (int i = 0; i < tmpColumns.length; i++) {
			tmpColumns[i].setSize(tableColumnModel.getColumn(tableColumnModel
					.getColumnIndex(i)));
		}
	}

	protected abstract void initData();

	/**
	 * return the array index from a Column id
	 * 
	 * @param searchid
	 * @return
	 */
	public int getPositionInArray(int searchid) {
		final UserColumn[] tmpColumns = getDisplayedColumns();
		for (int i = 0; i < tmpColumns.length; i++) {
			if (tmpColumns[i].getId() == searchid)
				return i;
		}
		return -1;
	}

	public void setCurrentValueToColumns(UserColumn[] tmpColumns) {
		for (UserColumn tmpColumn : tmpColumns) {
			for (UserColumn column : columns) {
				if (column.getId() == tmpColumn.getId()) {
					column.setIndex(tmpColumn.getIndex());
					column.setPreferredWidth(tmpColumn.getPreferredWidth());
					break;
				}
			}
		}
	}

	/**
	 * stored user settings of table columns order and columns width are set to the table
	 *
	 * @param table the table object
	 */
	public void restoreUserSettings(JTable table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setIdentifier(i);
		}
		Arrays.stream(this.columns)
				.filter(UserColumn::isDisplay)
				.sorted(Comparator.comparingInt(UserColumn::getIndex))
				.forEach(i -> setColumnSettings(i, table));
	}

	/**
	 * Set column order and width
	 *
	 * @param userColumn user column holding user's settings
	 * @param table the table object
	 */
	private void setColumnSettings(UserColumn userColumn, JTable table) {
		var column = table.getColumn(userColumn.getId());
		column.setPreferredWidth(userColumn.getPreferredWidth());
		var index = table.getColumnModel().getColumnIndex(userColumn.getId());
		if ( index != userColumn.getIndex()) {
			table.moveColumn(index, userColumn.getIndex());
		}
	}

	/**
	 * Save the user settings of the table. User selected width and column indexes are saved in user column model
	 * which is stored in database table UserColumnTable
	 *
	 * @param table table object
	 */
	public void storeUserSettings(JTable table) {
		// column order and width
		var tableColumnModel = table.getColumnModel();

		boolean changed = false;
		int i=0;
		for ( var column : this.getColumns()){
			if ( column.isDisplay()) {
				var index = table.convertColumnIndexToView(i++);
				if ( column.getIndex() != index) {
					changed = true;
					column.setIndex(index);
				}
				if ( column.getPreferredWidth() != tableColumnModel.getColumn(index).getWidth()) {
					changed = true;
					column.setPreferredWidth(tableColumnModel.getColumn(index).getWidth());
				}
			}
		}
		if ( changed){
			DBManager.instance().saveHOColumnModel(this);
		}
	}


}
