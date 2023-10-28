package core.gui.comp.renderer;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;



/**
 * Renderer for tables with JLabels as table objects
 */
public class HODefaultTableCellRenderer implements javax.swing.table.TableCellRenderer {
    //~ Static fields/initializers -----------------------------------------------------------------

    public static Color SELECTION_BG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG);
    public static Color SELECTION_FG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_FG);
    //~ Methods ------------------------------------------------------------------------------------

    public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                            boolean isSelected,
                                                            boolean hasFocus, int row,
                                                            int column) {

        if (value instanceof IHOTableEntry) {
            final JComponent component = ((IHOTableEntry) value).getComponent(isSelected);

            if (isSelected) {
                component.setOpaque(true);
            }

            return component;
        }  else if (value instanceof JComponent) {
            final JComponent component = (JComponent) value;
            component.setOpaque(true);
            component.setBackground(isSelected?SELECTION_BG:ColorLabelEntry.BG_STANDARD);
            component.setForeground(isSelected?SELECTION_FG:ColorLabelEntry.FG_STANDARD);
            return component;
        }
        else {

            JComponent component = new JLabel(value!=null?value.toString():"");
            component.setOpaque(true);
            component.setBackground(isSelected?SELECTION_BG:ColorLabelEntry.BG_STANDARD);
            component.setForeground(isSelected?SELECTION_FG:ColorLabelEntry.FG_STANDARD);
            
            return component;
        }
    }
}
