package core.gui.comp.renderer;

import com.github.weisj.darklaf.icons.IconLoader;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.theme.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.Map;

import static core.gui.theme.HOIconName.SMILEYS;


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

            if (Arrays.stream(SMILEYS).anyMatch(obj::equals)) {
                // smiley icon
                m_clEntry.setIcon(ImageUtilities.getSmileyIcon(obj.toString()));
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
