package core.gui.model;

import javax.swing.table.AbstractTableModel;

public class VAPTableModel extends AbstractTableModel {
    //~ Instance fields ----------------------------------------------------------------------------
	private static final long serialVersionUID = -8731149650305126908L;

    private String[] columnNames;
    private Object[][] data;
    private boolean editable;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new VAPTableModel object.
     */
    public VAPTableModel(String[] columnNames, Object[][] data) {
        this(columnNames, data, false);
    }

    /**
     * Creates a new VAPTableModel object.
     */
    public VAPTableModel(String[] columnNames, Object[][] data, boolean editable) {
        this.columnNames = columnNames;
        this.data = data;
        this.editable = editable;
    }

    //~ Methods ------------------------------------------------------------------------------------

    public final void setCellEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
	public final boolean isCellEditable(int row, int col) {
        return editable;
    }

    @Override
	public final Class<?> getColumnClass(int columnIndex) {
        final Object obj = getValueAt(0, columnIndex);

        return (obj != null) ? obj.getClass() : "".getClass();
    }

    public final int getColumnCount() {
        return ((data != null) && (data[0] != null)) ? data[0].length : 0;
    }

    @Override
	public final String getColumnName(int columnIndex) {
        if ((columnNames != null) && (columnNames.length > columnIndex)) {
            return columnNames[columnIndex];
        }

        return null;
    }

    public final int getRowCount() {
        return (data != null) ? data.length : 0;
    }

    public final Object getValue(int row, String columnName) {
        if ((columnNames != null) && (data != null)) {
            int i = 0;

            while ((i < columnNames.length) && !columnNames[i].equals(columnName)) {
                i++;
            }

            return data[row][i];
        }

        return null;
    }

    @Override
	public final void setValueAt(Object value, int row, int column) {
        data[row][column] = value;
    }

    public final Object getValueAt(int row, int column) {
        return (data != null) ? data[row][column] : null;
    }

    public final void setValues(String[] columNames, Object[][] data) {
        this.columnNames = columNames;
        this.data = data;
    }
}
