package module.specialEvents.table;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.IMatchHighlight;
import core.model.match.MatchHighlight;
import module.specialEvents.MatchRow;
import module.specialEvents.SpecialEventsDM;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ChanceTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -7036262083065436L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {

		JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
				hasFocus, row, column);

		Icon icon = null;
		MatchRow matchRow = (MatchRow) value;
		MatchHighlight highlight = matchRow.getMatchHighlight();
		if (highlight != null) {
			if (highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_ERFOLGREICH) {
				icon = ThemeManager.getIcon(HOIconName.GOAL);
			} else if (highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_FEHLGESCHLAGEN) {
				icon = ThemeManager.getIcon(HOIconName.NOGOAL);
			} else if (SpecialEventsDM.isWeatherSE(highlight)) {
				icon = ThemeManager.getIcon(HOIconName.WEATHER[matchRow.getMatch().getWeather()
						.getId()]);
			}
		}
		component.setIcon(icon);
		RowColorDecorator.decorate(table, row, component, isSelected);
		return component;
	}

}
