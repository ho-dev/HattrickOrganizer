package module.transfer.history;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.table.TableCellRenderer;

class ButtonCellRenderer implements TableCellRenderer {
    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        JButton button = (JButton)value;
        return button; 
    }
}
