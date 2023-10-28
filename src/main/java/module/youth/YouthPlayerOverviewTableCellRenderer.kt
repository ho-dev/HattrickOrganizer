package module.youth;

import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.renderer.HODefaultTableCellRenderer;

import javax.swing.*;

public class YouthPlayerOverviewTableCellRenderer extends HODefaultTableCellRenderer {

    public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                            boolean isSelected,
                                                            boolean hasFocus, int row,
                                                            int column) {
        if (value instanceof YouthSkillInfoColumn) {
            return super.getTableCellRendererComponent(table, value, false, hasFocus, row, column);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}