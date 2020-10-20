// %1885149141:de.hattrickorganizer.gui.model%
package core.gui.comp.renderer;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.theme.*;
import javax.swing.*;
import java.util.Arrays;


/**
 * Renderer für eine Combobox mit SpielerCBItems
 */
public final class SmilieListCellRenderer implements javax.swing.ListCellRenderer {
    //~ Instance fields ----------------------------------------------------------------------------

    private ColorLabelEntry m_clEntry = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
    														ThemeManager.getColor(HOColorName.TABLEENTRY_BG),
                                                            SwingConstants.LEFT);

    private javax.swing.JLabel m_jlLeer = new javax.swing.JLabel(" ");

    //~ Methods ------------------------------------------------------------------------------------

    public final java.awt.Component getListCellRendererComponent(javax.swing.JList jList,
                                                                 Object obj, int index,
                                                                 boolean isSelected,
                                                                 boolean cellHasFocus) {
        if (obj instanceof String && !"".equals(obj)) {

            if (Arrays.stream(HOIconName.SMILEYS).anyMatch(obj::equals)) {
                // smiley icon
                int size = 22;
                if (obj.equals("smiley-happy") || obj.equals("smiley-sad") || obj.equals("smiley-neutral")) size = 18;
                m_clEntry.setIcon(ImageUtilities.getSvgIcon(obj.toString(), size, size));
                return m_clEntry.getComponent(isSelected);
            } else if (Arrays.stream(GroupTeamFactory.TEAMSMILIES).anyMatch(obj::equals)) {
                // jersey icon
                m_clEntry.setIcon(GroupTeamFactory.instance().getActiveGroupIcon(obj.toString()));
                return m_clEntry.getComponent(isSelected);
            }
        }

        m_jlLeer.setOpaque(true);

        if (isSelected) {
            m_jlLeer.setBackground(HODefaultTableCellRenderer.SELECTION_BG);
        } else {
            m_jlLeer.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));
        }

        return m_jlLeer;
    }
}
