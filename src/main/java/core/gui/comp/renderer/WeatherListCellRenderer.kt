package core.gui.comp.renderer;

import core.datatype.CBItem;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import javax.swing.*;


/**
 * Renderer for Weather CB items
 */
public class WeatherListCellRenderer implements ListCellRenderer<Object> {

    public ColorLabelEntry m_clEntry = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,	ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER), SwingConstants.LEFT);

    public javax.swing.JLabel m_jlLeer = new javax.swing.JLabel(" ");

    @Override
	public final java.awt.Component getListCellRendererComponent(JList jList, Object obj, int index, boolean isSelected, boolean cellHasFocus) {
        if (obj instanceof CBItem) {
        	int id = ((CBItem) obj).getId();
            Icon icon;
        	if (id==4){
                icon = ImageUtilities.getSvgIcon(HOIconName.WEATHER[id], 30, 18);
            }
            else{
                icon = ThemeManager.getIcon(HOIconName.WEATHER[id]);
            }
            m_clEntry.setIcon(icon);
            return m_clEntry.getComponent(isSelected);
        } 
        m_jlLeer.setOpaque(true);

       if (isSelected) {
              m_jlLeer.setBackground(ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG));
       } else {
              m_jlLeer.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));
       }
       return m_jlLeer;
    }

}
