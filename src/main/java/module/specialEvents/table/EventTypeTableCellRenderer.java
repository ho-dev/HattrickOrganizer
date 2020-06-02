package module.specialEvents.table;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.MatchEvent;
import module.matches.MatchesHelper;
import module.specialEvents.SpecialEventsDM;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class EventTypeTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -70362602083065436L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		MatchEvent matchHighlight = (MatchEvent) value;
		Icon icon = null;
		String eventText = null;

		if (matchHighlight != null) {
			icon = matchHighlight.getIcon();
			eventText = " " + SpecialEventsDM.getSEText(matchHighlight);
		}

		JLabel component = (JLabel) super.getTableCellRendererComponent(table, eventText, isSelected, hasFocus, row, column);
		component.setIcon(icon);

		RowColorDecorator.decorate(table, row, component, isSelected);

		return component;
	}

}
