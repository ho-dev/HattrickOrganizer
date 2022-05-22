package module.training.ui.renderer;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import module.training.ui.comp.PlayerNameCell;
import module.training.ui.comp.VerticalIndicator;
import module.training.ui.model.OutputTableModel;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * TableCellRenderer for the training results table in the Training tab.
 */
public class OutputTableRenderer extends DefaultTableCellRenderer {
    //~ Methods ------------------------------------------------------------------------------------

    private boolean isFixed;

    private static final Color TABLE_BG = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    private static final Color SELECTION_BG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG);
    private static final Color TABLE_FG = ThemeManager.getColor(HOColorName.TABLEENTRY_FG);

    public OutputTableRenderer(boolean isFixed){
        this.isFixed=isFixed;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {


        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        var tableModel = (OutputTableModel)table.getModel();

        // Reset default values
        if (isSelected) {
            this.setBackground(SELECTION_BG);
        } else {
            this.setBackground(TABLE_BG);
        }
        this.setForeground(TABLE_FG);

        if (column < 2) {
            if (isSelected) {
                if (isFixed && column == 0) {
                    PlayerNameCell pnc = (PlayerNameCell) value;
                    pnc.setBackground(SELECTION_BG);
                    return pnc;
                } else {
                    setBackground(SELECTION_BG);
                    return this;
                }
            } else {
                var modelRow = table.convertRowIndexToModel(row);
                var playerCol = (PlayerNameCell)tableModel.getValueAt(modelRow,0);
                var speed = playerCol.getSpeed();
                Color bgColor;

                // Speed range is 16 to 125
                if (speed > (125 + 50) / 2) {
                    bgColor = ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
                } else if (speed > (50 + 16) / 2) {
                    bgColor = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
                } else {
                    bgColor = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
                }

                if (isFixed && column == 0) {
                    PlayerNameCell pnc = (PlayerNameCell) value;
                    // Reset default values
                    pnc.setForeground(ThemeManager.getColor(HOColorName.TABLEENTRY_FG));
                    pnc.setBackground(bgColor);
                    return pnc;
                } else {
                    setBackground(bgColor);
                }

            }
        } else if (column < 12) {
            VerticalIndicator vi = (VerticalIndicator) value;

            if (isSelected) {
                vi.setBackground(SELECTION_BG);
            } else {
                vi.setBackground(TABLE_BG.brighter());
            }
            vi.setOpaque(true);

            return vi;
        }

        return cell;
    }
}
