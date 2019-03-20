package module.teamAnalyzer.ui.renderer;

import core.gui.theme.ImageUtilities;
import core.model.player.MatchRoleID;
import core.util.HelperWrapper;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PlayerPositionTableCellRenderer extends DefaultTableCellRenderer {
    //~ Static fields/initializers -----------------------------------------------------------------
    private static final long serialVersionUID = 3258412837305923127L;
    private static Map<Object,ImageIcon> map;


    //~ Constructors -------------------------------------------------------------------------------
    public PlayerPositionTableCellRenderer() {
        super();

        if (map == null) {
            map = new HashMap<Object,ImageIcon>();
        }

    }

    //~ Methods ------------------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int col) {
        if (value instanceof Integer) {
            super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, col);

            int pos = ((Integer) value).intValue();

            ImageIcon icon;

            // Check for cached icon first.
            if (map.containsKey(value)) {
                icon = map.get(value);
            } else {
                // Make new icon and cache it.
                icon = ImageUtilities.getImage4Position(HelperWrapper.instance().getPosition(pos),
                                                                        (byte) 0,0);
                map.put(value, icon);
            }

            this.setIcon(icon);
            this.setText(MatchRoleID.getNameForPosition((byte) pos));
        } else {
            this.setIcon(null);
            this.setText(null);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        }

        return this;
    }
}
