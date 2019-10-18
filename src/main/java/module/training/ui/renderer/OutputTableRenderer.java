package module.training.ui.renderer;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import module.training.ui.comp.VerticalIndicator;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class OutputTableRenderer extends DefaultTableCellRenderer {
    private static final Color SELECTION_BG = new java.awt.Color(210, 210, 210);
    //~ Methods ------------------------------------------------------------------------------------

    private static final long serialVersionUID = 7179773036740605371L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);

        Color bg_color;

        if (column == 0) {
            JLabel jl = (JLabel) value;
            return jl;
        }

        if (column < 3 && isSelected) {
            return this;
        }

        // Reset default values
        this.setForeground(Color.BLACK);
        if (isSelected)
            this.setBackground(SELECTION_BG);
        else
            this.setBackground(Color.WHITE);

        if ((column > 2) && (column < 11)) {
            VerticalIndicator vi = (VerticalIndicator) value;

            // Set background and make it visible.
            vi.setBackground(cell.getBackground());
            vi.setOpaque(true);

            return vi;
        }

        if (column < 3 && !isSelected) {
            int speed = Integer.parseInt((String) table.getValueAt(row, 12));

            // Speed range is 16 to 125
            if (speed > (125 + 50) / 2) {
                bg_color = ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
            } else if (speed > (50 + 16) / 2) {
                bg_color = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
            } else {
                bg_color = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
            }

            setBackground(bg_color);
        }

        return cell;
    }
}
