package module.specialEvents.table;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.MatchEvent;
import module.specialEvents.MatchRow;
import module.specialEvents.SpecialEventsDM;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SETypeTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -70362602083065436L;
	private final boolean away;

	public SETypeTableCellRenderer(boolean away) {
		this.away = away;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
				hasFocus, row, column);

		MatchRow matchRow = (MatchRow) value;
		if (matchRow.getMatchHighlight() != null) {
			component.setIcon(getOwnerIcon(matchRow.getMatchHighlight(), !this.away, matchRow
					.getMatch().getHostingTeamId(), matchRow.getMatch().getVisitingTeamId()));
		} else {
			component.setIcon(null);
		}
		RowColorDecorator.decorate(table, row, component, isSelected);
		return component;
	}

	private ImageIcon getOwnerIcon(MatchEvent highlight, boolean home, int heimId, int gastId) {
		ImageIcon icon = null;
		if (home) {
			// Create home icon
			if (highlight.getTeamID() == heimId) {
				if (highlight.isPositiveSpecialtyWeatherSE()) {
					// Positive weather SE for home team
					return ThemeManager.getIcon(HOIconName.ARROW_RIGHT1);
				} else if (highlight.isPositiveSpecialtyWeatherSE()) {
					// Negative weather SE for home team
					return ThemeManager.getIcon(HOIconName.ARROW_RIGHT2);
				} else if (!SpecialEventsDM.isNegativeSE(highlight)) {
					// Positive non-weather SE for home
					return ThemeManager.getIcon(HOIconName.ARROW_RIGHT1);
				}
			} else {
				if (!highlight.isSpecialtyWeatherSE() && SpecialEventsDM.isNegativeSE(highlight)) {
					// Negative non-weather SE against home team
					return ThemeManager.getIcon(HOIconName.ARROW_RIGHT2);
				}
			}
		} else {
			// Create guest icon
			if (highlight.getTeamID() == gastId) {
				if (highlight.isPositiveSpecialtyWeatherSE()) {
					// Positive weather SE for guest team
					ThemeManager.getIcon(HOIconName.ARROW_LEFT1);
				} else if (highlight.isNegativeSpecialtyWeatherSE()) {
					// Negative weather SE for guest team
					return ThemeManager.getIcon(HOIconName.ARROW_LEFT2);
				} else if (!SpecialEventsDM.isNegativeSE(highlight)) {
					// Positive non-weather SE for guest
					return ThemeManager.getIcon(HOIconName.ARROW_LEFT1);
				}
			} else {
				if (!highlight.isSpecialtyWeatherSE() && SpecialEventsDM.isNegativeSE(highlight)) {
					// Negative non-weather SE against guest team
					return ThemeManager.getIcon(HOIconName.ARROW_LEFT2);
				}
			}
		}

		return null;
	}
}
