package module.evilcard.gui;


import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


class DetailsTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -640090984809074407L;

    private Color WARNINGS_TYPE1_COLOR = new Color(255, 255, 200);
//    private Color WARNINGS_TYPE2_COLOR = new Color(255, 227, 200);
//    private Color DIRECT_RED_COLOR = new Color(255, 200, 200);

     DetailsTableCellRenderer() {
        super();
        this.setVerticalAlignment(SwingConstants.CENTER);
    }

 
    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        switch (column) {
            case DetailsTableModel.COL_DIRECT_RED_CARDS:
                this.setBackground(ThemeManager.getColor(HOColorName.LEAGUE_DEMOTED_BG));
                this.setHorizontalAlignment(SwingConstants.CENTER);
                break;

            case DetailsTableModel.COL_WARNINGS_TYPE1:
            case DetailsTableModel.COL_WARNINGS_TYPE3:
                this.setBackground(ThemeManager.getColor("lightYellow"));
                this.setHorizontalAlignment(SwingConstants.CENTER);
                break;

            case DetailsTableModel.COL_WARNINGS_TYPE2:
            case DetailsTableModel.COL_WARNINGS_TYPE4:
                this.setBackground(WARNINGS_TYPE1_COLOR);
                this.setHorizontalAlignment(SwingConstants.CENTER);
                break;

            case DetailsTableModel.COL_MATCH_RESULT:
                this.setHorizontalAlignment(SwingConstants.CENTER);
                break;

            default:
                this.setBackground(table.getBackground());
                this.setHorizontalAlignment(SwingConstants.LEFT);
                break;
        }

        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        }

        return this;
    }
}
