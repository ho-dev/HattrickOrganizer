package tool.hrfexplorer;

import core.util.HOLogger;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * @author KickMuck
 */
public class HrfTableModel extends DefaultTableModel {

	public HrfTableModel(Vector columns, Vector rows) {
		dataVector = rows;
		columnIdentifiers = columns;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if (getValueAt(row, col).equals(Boolean.TRUE)
				|| getValueAt(row, col).equals(Boolean.FALSE)) {
			this.fireTableCellUpdated(row, col);
			return true;
		}
			return false;
	}

	@Override
	public Class getColumnClass(int columnIndex) {
		Object o = getValueAt(0, columnIndex);
		Vector v = dataVector.elementAt(0);
		if (o == null) {
			return Object.class;
		}
		return v.elementAt(columnIndex).getClass();
	}

	public void removeAllRows() {
		while (!dataVector.isEmpty()) {
			this.removeRow(0);
		}
	}

	@Override
	public void addRow(Vector myRow) {
		try {
			dataVector.addElement(myRow);
		} catch (Exception e) {
			HOLogger.instance().error(this.getClass(), "Error in addRow: " + e.getMessage());
		}

	}
}
