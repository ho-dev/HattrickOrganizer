// %390576536:de.hattrickorganizer.tools.updater%
package tool.updater;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;


/**
 * TableModel for the UpdaterDialogs
 *
 * @author Thorsten Dietz
 *
 * @since 1.35
 */
public final class TableModel extends DefaultTableModel {
    //~ Constructors -------------------------------------------------------------------------------

	private static final long serialVersionUID = -3901370941644100246L;

	/**
     * Creates a new TableModel object.
     */
    public TableModel(Object[][] daten, Object[] headers) {
        super(daten, headers);
    }

    //~ Methods ------------------------------------------------------------------------------------

    @Override
	public boolean isCellEditable(int row, int col) {
        return getValueAt(row, col) instanceof JCheckBox || getValueAt(row, col) instanceof JButton || getValueAt(row, col) instanceof JComboBox ;
    }
}