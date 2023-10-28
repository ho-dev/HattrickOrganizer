package tool.hrfExplorer;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * @author KickMuck
 */
public class HrfTableModel extends DefaultTableModel {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4726662462776212169L;

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	@Override
	public Class getColumnClass(int columnIndex) {
		Object o = getValueAt(0, columnIndex);
		Vector v = (Vector) dataVector.elementAt(0);
		if (o == null) {
			return Object.class;
		}
		return v.elementAt(columnIndex).getClass();
	}

	public void removeAllRows() {
		while (dataVector.size() > 0) {
			this.removeRow(0);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addRow(Vector myRow) {
		try {
			dataVector.addElement(myRow);
		} catch (Exception e) {
			HrfExplorer.appendText("FEHLER iN addrow");
		}

	}
}
