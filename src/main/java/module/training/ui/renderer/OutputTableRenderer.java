package module.training.ui.renderer;

import module.training.ui.comp.VerticalIndicator;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class OutputTableRenderer extends DefaultTableCellRenderer {
    //~ Methods ------------------------------------------------------------------------------------

	private static final long serialVersionUID = 7179773036740605371L;

    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                                             row, column);

        if ((column > 2) && (column < 11)) {
            VerticalIndicator vi = (VerticalIndicator) value;

            // Set background and make it visible.
            vi.setBackground(cell.getBackground());
            vi.setOpaque(true);

            return vi;
        }

        return cell;
    }
}
