package module.specialEvents.table;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import module.specialEvents.SpecialEventsTableModel;
import java.awt.Component;
import javax.swing.JTable;

public class RowColorDecorator {

	static void decorate(JTable table, int row, Component component, boolean isSelected) {
		if (!isSelected) {
			SpecialEventsTableModel model = (SpecialEventsTableModel) table.getModel();
			var matchIndex = model.getMatchCount(row);
			if (matchIndex % 2 == 0) {
				component.setBackground(ThemeManager.getColor(HOColorName.PLAYER_SUBPOS_BG));
			} else {
				component.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));
			}
		} else {
			component.setBackground(table.getSelectionBackground());
		}
	}

}
