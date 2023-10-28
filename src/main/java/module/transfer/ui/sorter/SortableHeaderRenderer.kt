package module.transfer.ui.sorter;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;

class SortableHeaderRenderer implements TableCellRenderer {
    private final AbstractTableSorter sorter;
    private TableCellRenderer tableCellRenderer;

    /**
     * Creates a new SortableHeaderRenderer object.
     *
     * @param sorter
     * @param tableCellRenderer
     */
    public SortableHeaderRenderer(AbstractTableSorter sorter,
        TableCellRenderer tableCellRenderer) {
        this.tableCellRenderer = tableCellRenderer;
        this.sorter = sorter;
    }

    public TableCellRenderer getTableCellRenderer() {
        return tableCellRenderer;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = tableCellRenderer.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

        if (c instanceof JLabel) {
            JLabel l = (JLabel) c;

            l.setHorizontalTextPosition(SwingConstants.LEFT);

            int modelColumn = table.convertColumnIndexToModel(column);

            l.setIcon(this.sorter.getHeaderRendererIcon(modelColumn,
                    l.getFont().getSize()));
        }

        return c;
    }
}
