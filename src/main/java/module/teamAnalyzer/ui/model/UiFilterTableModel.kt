// %511085336:hoplugins.teamAnalyzer.ui.model%
package module.teamAnalyzer.ui.model;

import core.gui.model.BaseTableModel;

import java.util.Vector;

import javax.swing.ImageIcon;



/**
 * Custom FilterTable model
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class UiFilterTableModel extends BaseTableModel {
    //~ Constructors -------------------------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = 327827808706196143L;

	/**
     * Creates a new instance of UiFilterTableModel
     */
    public UiFilterTableModel() {
        super();
    }

    /**
     * Creates a new UiFilterTableModel object.
     *
     * @param data Vector of table data
     * @param columnNames Vector of column names
     */
    public UiFilterTableModel(Vector<Vector<Object>> data, Vector<String> columnNames) {
        super(data, columnNames);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns true if the cell is editable
     *
     * @param row
     * @param column
     *
     * @return
     */
    @Override
	public boolean isCellEditable(int row, int column) {
        if (column != 0) {
            return false;
        }

        String available = (String) getValueAt(row, 6);

        if (available.equalsIgnoreCase("true")) {
            return true;
        }

        return false;
    }

    /**
     * Returns the column class type
     *
     * @param column
     *
     * @return
     */
    @Override
	public Class<?> getColumnClass(int column) {
        if (column == 2) {
            return ImageIcon.class;
        }

        return super.getColumnClass(column);
    }
}
