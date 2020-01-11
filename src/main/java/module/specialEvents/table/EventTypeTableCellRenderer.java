package module.specialEvents.table;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.MatchEvent;
import module.matches.MatchesHelper;
import module.specialEvents.SpecialEventsDM;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class EventTypeTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -70362602083065436L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
				hasFocus, row, column);

		MatchEvent matchHighlight = (MatchEvent) value;
		if (matchHighlight != null) {
			component.setIcon(matchHighlight.getIcon());
		} else {
			component.setIcon(null);
		}
		RowColorDecorator.decorate(table, row, component, isSelected);

		return component;
	}

}
