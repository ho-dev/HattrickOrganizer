// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31.10.2011 08:11:05
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DummyTableModel.java

package core.db.frontend;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

final class DummyTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -1521045917540294324L;

	protected DummyTableModel(Object daten[][], Object headers[]) {
		super(daten, headers);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<?> getColumnClass(int col) {
		Vector vector = (Vector) dataVector.elementAt(0);
		if (vector != null) {
			return vector.elementAt(col).getClass();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}