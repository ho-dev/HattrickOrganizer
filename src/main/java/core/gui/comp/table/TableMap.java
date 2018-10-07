// %3252003863:de.hattrickorganizer.gui.utils%
package core.gui.comp.table;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * TODO The purpose of this class is unclear to me (kruescho, 06/10/2012)
 * 
 */
class TableMap extends AbstractTableModel implements TableModelListener {
	
	private static final long serialVersionUID = 5022212679370349761L;
    private TableModel model;

    TableMap() {
    }

    @Override
	public final boolean isCellEditable(int i, int j) {
        return model.isCellEditable(i, j);
    }

    @Override
	public final Class<?> getColumnClass(int i) {
        return model.getColumnClass(i);
    }

    @Override
	public final int getColumnCount() {
        return (model != null) ? model.getColumnCount() : 0;
    }

    @Override
	public final String getColumnName(int i) {
        return model.getColumnName(i);
    }

    public void setModel(TableModel tablemodel) {
        model = tablemodel;
        tablemodel.addTableModelListener(this);
    }

    public final TableModel getModel() {
        return model;
    }

    @Override
	public final int getRowCount() {
        return (model != null) ? model.getRowCount() : 0;
    }

    @Override
	public void setValueAt(Object obj, int i, int j) {
        model.setValueAt(obj, i, j);
    }

    @Override
	public Object getValueAt(int i, int j) {
        return model.getValueAt(i, j);
    }

    @Override
	public void tableChanged(TableModelEvent tablemodelevent) {
        fireTableChanged(tablemodelevent);
    }
}
