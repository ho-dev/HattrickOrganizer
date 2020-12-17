// %2449251406:de.hattrickorganizer.gui.model%
package core.gui.model;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


/**
 * Für 2 Markierungen
 */
public class AufstellungsListRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 7059514707568786835L;
	private static final Color bgColor = ThemeManager.getColor(HOColorName.LIST_SELECTION_BG);//new Color(220, 220, 255);
	private static Color angezeigtColor = ThemeManager.getColor(HOColorName.LIST_CURRENT_FG);//new Color(0, 0, 150);
	private static Color FG_COLOR = ThemeManager.getColor(HOColorName.LIST_FG);
	
	public final Component getListCellRendererComponent(final JList jList, final Object value, final int row,
			final boolean isSelected, final boolean hasFocus) {
		setText(value.toString());
		setOpaque(true);
		setForeground(FG_COLOR);
		if (isSelected) {
			setBackground(bgColor);
		} else {
			setOpaque(false);
			setBackground(jList.getBackground());
		}
		
//		if (value instanceof LineupCBItem) {
//			if (((LineupCBItem) value).isAngezeigt()) {
//				setForeground(angezeigtColor);
//			}
//		}

		return this;
	}
}
