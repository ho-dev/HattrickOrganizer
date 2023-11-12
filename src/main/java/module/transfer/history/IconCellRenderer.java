package module.transfer.history;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import module.transfer.PlayerTransfer;

import java.awt.*;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * Cell renderer to show an icon for the type of transfer (in or out).
 */
class IconCellRenderer extends DefaultTableCellRenderer {

    public static Color SELECTION_BG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG);

    public static Color SELECTION_FG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_FG);

    @Override
	public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        var comp = super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);

        var  type = (ColorLabelEntry) value;

        if (Objects.equals(type.getText(), String.valueOf(PlayerTransfer.BUY))){
            this.setIcon(ImageUtilities.getTransferInIcon());
        }
        else {
            this.setIcon(ImageUtilities.getTransferOutIcon());
        }
        comp.setBackground(isSelected ? SELECTION_BG : ColorLabelEntry.BG_STANDARD);
        comp.setForeground(isSelected ? SELECTION_FG : ColorLabelEntry.FG_STANDARD);

        return comp;
    }
}
