package module.specialEvents.table;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.MatchType;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MatchTypeTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -703626020830654L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
				hasFocus, row, column);

		MatchType matchType = (MatchType) value;
		if (matchType != null) {
			component.setIcon(ThemeManager.getIcon(HOIconName.MATCHICONS[matchType
					.getIconArrayIndex()]));
		} else {
			component.setIcon(null);
		}
		RowColorDecorator.decorate(table, row, component, isSelected);
		return component;
	}
}
