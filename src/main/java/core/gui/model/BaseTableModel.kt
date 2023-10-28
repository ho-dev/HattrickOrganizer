// %1126721045432:hoplugins.commons.ui%
package core.gui.model;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * Base TableModel that creates a not editable table and manage the rendering
 * 
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class BaseTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -5658792494605688937L;

	/**
	 * Creates a new instance of BaseTableModel
	 */
	public BaseTableModel() {
		super();
	}

	/**
	 * Creates a new BaseTableModel object.
	 * 
	 * @param data
	 *            Vector with the data to be used to fill the table
	 * @param columnNames
	 *            Vector of column names
	 */
	public BaseTableModel(Vector<Vector<Object>> data, Vector<?> columnNames) {
		super(data, columnNames);
	}

	/**
	 * Method that returns if the cell if editable, false by default
	 * 
	 * @param row
	 *            the row index of the cell
	 * @param column
	 *            the column index of the cell
	 * 
	 * @return true if editable, false if not
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/**
	 * Method the return the column class type
	 * 
	 * @param column
	 *            the column we want to know the class type
	 * 
	 * @return Object is column is empty, or the type of object we have in the
	 *         column
	 */
	@Override
	public Class<?> getColumnClass(int column) {
		if (getValueAt(0, column) == null) {
			return Object.class;
		} else {
			return getValueAt(0, column).getClass();
		}
	}

	public void removeAllRows() {
		int rowCount = getDataVector().size();
		if (rowCount > 0) {
			getDataVector().clear();
			fireTableRowsDeleted(0, rowCount - 1);
		}
	}
}
