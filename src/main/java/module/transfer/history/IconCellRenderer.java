package module.transfer.history;

import core.gui.theme.ImageUtilities;
import module.transfer.PlayerTransfer;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * Cell renderer to show an icon for the type of transfer (in or out).
 */
class IconCellRenderer extends DefaultTableCellRenderer {


    @Override
	public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);

        final int type = (Integer) value;

        this.setIcon(type == PlayerTransfer.BUY? ImageUtilities.getTransferInIcon():ImageUtilities.getTransferOutIcon());
        return this;
    }
}
