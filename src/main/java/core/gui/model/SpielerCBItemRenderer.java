// %3053844359:de.hattrickorganizer.gui.model%
package core.gui.model;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.renderer.HODefaultTableCellRenderer;

/**
 * Renderer für eine Combobox mit SpielerCBItems
 */
public class SpielerCBItemRenderer implements javax.swing.ListCellRenderer {
    //~ Instance fields ----------------------------------------------------------------------------

    public javax.swing.JLabel m_jlLeer = new javax.swing.JLabel(" ");

    //~ Methods ------------------------------------------------------------------------------------

    public final java.awt.Component getListCellRendererComponent(javax.swing.JList jList,
                                                                 Object obj, int index,
                                                                 boolean isSelected,
                                                                 boolean cellHasFocus) {
        if (obj instanceof SpielerCBItem) {
            final SpielerCBItem spielerCBItem = ((SpielerCBItem) obj);
            return spielerCBItem.getListCellRendererComponent(jList, index, isSelected);
        }
            m_jlLeer.setOpaque(true);

            if (isSelected) {
                m_jlLeer.setBackground(HODefaultTableCellRenderer.SELECTION_BG);
            } else {
                m_jlLeer.setBackground(ColorLabelEntry.BG_STANDARD);
            }

            return m_jlLeer;
        
    }
}
