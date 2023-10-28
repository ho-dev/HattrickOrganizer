// %3331837056:de.hattrickorganizer.gui.model%
package core.gui.model;

import javax.swing.table.TableModel;


/**
 * TableModel, das nur eine Spalte anzeigt
 */
public class ReduzedTableModel implements TableModel {
    //~ Instance fields ----------------------------------------------------------------------------

    private TableModel m_clTablemodel;
    private int m_iSpaltenindex;

    //~ Constructors -------------------------------------------------------------------------------

    public ReduzedTableModel(TableModel tablemodel, int spaltenindex) {
        m_clTablemodel = tablemodel;
        m_iSpaltenindex = spaltenindex;
    }

    //~ Methods ------------------------------------------------------------------------------------

    public final boolean isCellEditable(int row, int col) {
        return false;
    }

    public final Class<?> getColumnClass(int param) {
        return m_clTablemodel.getColumnClass(m_iSpaltenindex);
    }

    /**
     * Nur eine Spalte
     */
    public final int getColumnCount() {
        return 1;
    }

    public final String getColumnName(int param) {
        return m_clTablemodel.getColumnName(m_iSpaltenindex);
    }

    public final int getRowCount() {
        return m_clTablemodel.getRowCount();
    }

    public final void setValueAt(Object obj, int param, int param2) {
        m_clTablemodel.setValueAt(obj, param, param2);
    }

    /**
     * Immer Wert der einen Spalte zurückgeben
     */
    public final Object getValueAt(int row, int col) {
        return m_clTablemodel.getValueAt(row, m_iSpaltenindex);
    }

    public final void addTableModelListener(javax.swing.event.TableModelListener tableModelListener) {
        m_clTablemodel.addTableModelListener(tableModelListener);
    }

    //Listener
    public final void removeTableModelListener(javax.swing.event.TableModelListener tableModelListener) {
        m_clTablemodel.removeTableModelListener(tableModelListener);
    }
}
