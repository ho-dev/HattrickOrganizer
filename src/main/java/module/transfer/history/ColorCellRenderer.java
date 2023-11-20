// %1126721330135:hoplugins.transfers.ui%
package module.transfer.history;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;

/**
 * Cell reneder to show an icon for the type of transfer (in or out).
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
class ColorCellRenderer extends HODefaultTableCellRenderer {

	public static final Color WHITE = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    public static final Color GREEN = ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);//new Color(220, 255, 220);
    public static final Color YELLOW = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);//new Color(255, 255, 200);


    private final Color color;


    /**
     * Creates an instance of ColorcellRenderer.
     * 
     * @param color Color tto use
     */
    ColorCellRenderer(Color color) {
        super();
        this.color = color;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
	public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        var comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            comp.setBackground(this.color);
        }

//        final int intVal = ((Integer) value).intValue();
//
//        if (intVal == -1) {
//            setText("---");
//        }

        return comp;
    }
}
