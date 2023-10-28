package module.specialEvents.table;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.IMatchDetails;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class TacticsTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -7036269102083065436L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		Icon icon = null;

		if (value != null) {
			int tacticId = (Integer) value;
			icon = switch (tacticId) {
				case IMatchDetails.TAKTIK_PRESSING -> ThemeManager.getIcon(HOIconName.TACTIC_PRESSING);
				case IMatchDetails.TAKTIK_KONTER -> ThemeManager.getIcon(HOIconName.TACTIC_COUNTER_ATTACKING);
				case IMatchDetails.TAKTIK_MIDDLE -> ThemeManager.getIcon(HOIconName.TACTIC_AIM);
				case IMatchDetails.TAKTIK_WINGS -> ThemeManager.getIcon(HOIconName.TACTIC_AOW);
				case IMatchDetails.TAKTIK_CREATIVE -> ThemeManager.getIcon(HOIconName.TACTIC_PLAY_CREATIVELY);
				case IMatchDetails.TAKTIK_LONGSHOTS -> ThemeManager.getIcon(HOIconName.TACTIC_LONG_SHOTS);
				default -> null;
			};

		}

		JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
		component.setIcon(icon);
		RowColorDecorator.decorate(table, row, component, isSelected);
		return component;
	}

}
