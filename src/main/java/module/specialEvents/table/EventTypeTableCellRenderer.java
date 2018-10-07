package module.specialEvents.table;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.IMatchHighlight;
import core.model.match.MatchHighlight;
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

		MatchHighlight matchHighlight = (MatchHighlight) value;
		if (matchHighlight != null) {
			component.setIcon(getEventTypIcon(matchHighlight));
		} else {
			component.setIcon(null);
		}
		RowColorDecorator.decorate(table, row, component, isSelected);

		return component;
	}

	private ImageIcon getEventTypIcon(MatchHighlight highlight) {
		if (SpecialEventsDM.isPositiveWeatherSE(highlight)) {
			return ThemeManager.getIcon(HOIconName.WEATHER_EFFECT_GOOD);
		} else if (SpecialEventsDM.isNegativeWeatherSE(highlight)) {
			return ThemeManager.getIcon(HOIconName.WEATHER_EFFECT_BAD);
		} else if (highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_ERFOLGREICH
				|| highlight.getHighlightTyp() == IMatchHighlight.HIGHLIGHT_FEHLGESCHLAGEN) {
			// Non-weather SE
			switch (highlight.getHighlightSubTyp()) {
			case IMatchHighlight.HIGHLIGHT_SUB_WEITSCHUSS_TOR:
				return ThemeManager.getIcon(HOIconName.GOAL_LONGSHOT);
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_VORLAGE_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_ABGEFANGEN_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALL_ERKAEMPFT_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALLVERLUST_TOR:
				return ThemeManager.getIcon(HOIconName.SPECIAL[4]);
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_PASS_TOR:
				return ThemeManager.getIcon(HOIconName.SPECIAL[2]);
			case IMatchHighlight.HIGHLIGHT_SUB_SCHLECHTE_KONDITION_BALLVERLUST_TOR:
				return ThemeManager.getIcon(HOIconName.GOAL_SPECIAL);
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_KOPFTOR:
				return ThemeManager.getIcon(HOIconName.GOAL_SPECIAL);
			case IMatchHighlight.HIGHLIGHT_SUB_ERFAHRENER_ANGREIFER_TOR:
				return ThemeManager.getIcon(HOIconName.GOAL_SPECIAL);
			case IMatchHighlight.HIGHLIGHT_SUB_UNERFAHREN_TOR:
				return ThemeManager.getIcon(HOIconName.GOAL_SPECIAL);
			case IMatchHighlight.HIGHLIGHT_SUB_QUERPASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_AUSSERGEWOEHNLICHER_PASS_TOR:
				return ThemeManager.getIcon(HOIconName.GOAL_RIGHT);
			case IMatchHighlight.HIGHLIGHT_SUB_TECHNIKER_ANGREIFER_TOR:
				return ThemeManager.getIcon(HOIconName.SPECIAL[1]);
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_EINS:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_ZWEI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_DREI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_VIER:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_FUENF:
				return ThemeManager.getIcon(HOIconName.GOAL_COUNTER);
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_8:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_2:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_3:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_4:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_5:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_6:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_7:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_8:
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_1:
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_2:
			case IMatchHighlight.HIGHLIGHT_SUB_LONGHSHOT_1:
			case IMatchHighlight.HIGHLIGHT_SUB_QUICK_RUSH_STOPPED_BY_DEF:
				// Always return the icon for "SUCCESS" because we only want the
				// chance type icon
				return MatchesHelper.getImageIcon4SpielHighlight(
						IMatchHighlight.HIGHLIGHT_ERFOLGREICH, highlight.getHighlightSubTyp());
			}
		}
		return null;
	}
}
