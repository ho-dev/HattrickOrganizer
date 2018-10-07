package module.matches;

import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.IMatchHighlight;
import core.model.match.MatchHighlight;

import java.awt.Color;

import javax.swing.ImageIcon;

public class MatchesHelper {

	private MatchesHelper() {
	}

	/**
	 * Get the color for the given highlight type and subtype.
	 */
	public static Color getColor4SpielHighlight(int typ, int subtyp) {
		if (typ == IMatchHighlight.HIGHLIGHT_KARTEN) {
			if ((subtyp == IMatchHighlight.HIGHLIGHT_SUB_GELB_HARTER_EINSATZ)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_GELB_UNFAIR)) {
				return core.model.UserParameter.instance().FG_ZWEIKARTEN;
			} else if ((subtyp == IMatchHighlight.HIGHLIGHT_SUB_ROT)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_GELB_ROT_HARTER_EINSATZ)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_GELB_ROT_UNFAIR)) {
				return core.model.UserParameter.instance().FG_GESPERRT;
			}
		} else if (typ == IMatchHighlight.HIGHLIGHT_ERFOLGREICH) {
			return ThemeManager.getColor(HOColorName.LABEL_FG);
		} else if (typ == IMatchHighlight.HIGHLIGHT_FEHLGESCHLAGEN) {
			return ThemeManager.getColor(HOColorName.MATCHHIGHLIGHT_FAILED_FG);
		} else if (typ == IMatchHighlight.HIGHLIGHT_INFORMATION) {
			if ((subtyp == IMatchHighlight.HIGHLIGHT_SUB_PFLASTER)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_PFLASTER_BEHANDLUNG)) {
				return core.model.UserParameter.instance().FG_ANGESCHLAGEN;
			} else if ((subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT_LEICHT)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT_SCHWER)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT_KEIN_ERSATZ_EINS)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT_KEIN_ERSATZ_ZWEI)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT_TORWART_FELDSPIELER)) {
				return core.model.UserParameter.instance().FG_VERLETZT;
			}
		}

		return ThemeManager.getColor(HOColorName.LABEL_FG);
	}

	public static ImageIcon getImageIcon4SpielHighlight(MatchHighlight highlight) {
		if (highlight != null) {
			return getImageIcon4SpielHighlight(highlight.getHighlightTyp(),
					highlight.getHighlightSubTyp());
		}
		return null;
	}

	public static ImageIcon getImageIcon4SpielHighlight(int typ, int subtyp) {
		ImageIcon icon = null;

		if (typ == IMatchHighlight.HIGHLIGHT_KARTEN) {
			if ((subtyp == IMatchHighlight.HIGHLIGHT_SUB_GELB_HARTER_EINSATZ)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_GELB_UNFAIR)) {
				icon = ThemeManager.getIcon(HOIconName.YELLOWCARD);
			} else if ((subtyp == IMatchHighlight.HIGHLIGHT_SUB_ROT)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_GELB_ROT_HARTER_EINSATZ)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_GELB_ROT_UNFAIR)) {
				icon = ThemeManager.getIcon(HOIconName.REDCARD);
			}
		} else if (typ == IMatchHighlight.HIGHLIGHT_ERFOLGREICH) {
			switch (subtyp) {
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_8: {
				icon = ThemeManager.getIcon(HOIconName.GOAL_FREEKICK);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_2:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_3:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_4:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_5:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_6:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_7:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_8: {
				icon = ThemeManager.getIcon(HOIconName.GOAL_MID);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_8: {
				icon = ThemeManager.getIcon(HOIconName.GOAL_LEFT);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_8: {
				icon = ThemeManager.getIcon(HOIconName.GOAL_RIGHT);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_2:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_3:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_4:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_5:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_6:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_7:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_8: {
				icon = ThemeManager.getIcon(HOIconName.GOAL_PENALTY);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_1:
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_2: {
				icon = ThemeManager.getIcon(HOIconName.GOAL_FREEKICK2);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_LONGHSHOT_1: {
				icon = ThemeManager.getIcon(HOIconName.GOAL_LONGSHOT);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_VORLAGE_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_ABGEFANGEN_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_WEITSCHUSS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALL_ERKAEMPFT_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALLVERLUST_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_PASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_SCHLECHTE_KONDITION_BALLVERLUST_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_KOPFTOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ERFAHRENER_ANGREIFER_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNERFAHREN_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_QUERPASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_AUSSERGEWOEHNLICHER_PASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_TECHNIKER_ANGREIFER_TOR: {
				icon = ThemeManager.getIcon(HOIconName.GOAL_SPECIAL);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_EINS:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_ZWEI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_DREI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_VIER:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_FUENF: {
				icon = ThemeManager.getIcon(HOIconName.GOAL_COUNTER);
				break;
			}

			default:
				icon = ThemeManager.getIcon(HOIconName.GOAL);
			}
		} else if (typ == IMatchHighlight.HIGHLIGHT_FEHLGESCHLAGEN) {
			switch (subtyp) {
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_FREISTOSS_8: {
				icon = ThemeManager.getIcon(HOIconName.NOGOAL_FREEKICK);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_2:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_3:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_4:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_5:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_6:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_7:
			case IMatchHighlight.HIGHLIGHT_SUB_DURCH_MITTE_8: {
				icon = ThemeManager.getIcon(HOIconName.NOGOAL_MID);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_LINKS_8: {
				icon = ThemeManager.getIcon(HOIconName.NOGOAL_LEFT);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_2:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_3:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_4:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_5:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_6:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_7:
			case IMatchHighlight.HIGHLIGHT_SUB_UEBER_RECHTS_8: {
				icon = ThemeManager.getIcon(HOIconName.NOGOAL_RIGHT);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_2:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_3:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_4:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_5:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_6:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_7:
			case IMatchHighlight.HIGHLIGHT_SUB_ELFMETER_8: {
				icon = ThemeManager.getIcon(HOIconName.NOGOAL_PENALTY);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_1:
			case IMatchHighlight.HIGHLIGHT_SUB_INDIRECT_FREEKICK_2: {
				icon = ThemeManager.getIcon(HOIconName.NOGOAL_FREEKICK2);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_LONGHSHOT_1: {
				icon = ThemeManager.getIcon(HOIconName.NOGOAL_LONGSHOT);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_VORLAGE_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_PASS_ABGEFANGEN_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_WEITSCHUSS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALL_ERKAEMPFT_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNVORHERSEHBAR_BALLVERLUST_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_SCHNELLER_ANGREIFER_PASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_SCHLECHTE_KONDITION_BALLVERLUST_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ECKBALL_KOPFTOR:
			case IMatchHighlight.HIGHLIGHT_SUB_ERFAHRENER_ANGREIFER_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_UNERFAHREN_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_QUERPASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_AUSSERGEWOEHNLICHER_PASS_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_TECHNIKER_ANGREIFER_TOR:
			case IMatchHighlight.HIGHLIGHT_SUB_QUICK_RUSH_STOPPED_BY_DEF: {
				icon = ThemeManager.getIcon(HOIconName.NOGOAL_SPECIAL);
				break;
			}

			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_EINS:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_ZWEI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_DREI:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_VIER:
			case IMatchHighlight.HIGHLIGHT_SUB_KONTERANGRIFF_FUENF: {
				icon = ThemeManager.getIcon(HOIconName.NOGOAL_COUNTER);
				break;
			}

			default:
				icon = ThemeManager.getIcon(HOIconName.NOGOAL);
			}
		} else if (typ == IMatchHighlight.HIGHLIGHT_INFORMATION) {
			if ((subtyp == IMatchHighlight.HIGHLIGHT_SUB_PFLASTER)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_PFLASTER_BEHANDLUNG)) {
				icon = ThemeManager.getIcon(HOIconName.PATCHSMALL);
			} else if ((subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT_LEICHT)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT_SCHWER)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT_KEIN_ERSATZ_EINS)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT)
					|| (subtyp == IMatchHighlight.HIGHLIGHT_SUB_VERLETZT_KEIN_ERSATZ_ZWEI)) {
				icon = ThemeManager.getIcon(HOIconName.INJUREDSMALL);
			}
		} else if (typ == IMatchHighlight.HIGHLIGHT_SPEZIAL) {
			switch (subtyp) {
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_POWERFUL_RAINY: // +
				icon = ThemeManager.getScaledIcon(HOIconName.WEATHER_RAIN_POS, 16, 10);
				break;
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_TECHNICAL_SUNNY: // +
				icon = ThemeManager.getScaledIcon(HOIconName.WEATHER_SUN_POS, 16, 10);
				break;
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_POWERFUL_SUNNY: // -
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_QUICK_SUNNY: // -
				icon = ThemeManager.getScaledIcon(HOIconName.WEATHER_SUN_NEG, 16, 10);
				break;
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_QUICK_RAINY: // -
			case IMatchHighlight.HIGHLIGHT_SUB_PLAYER_TECHNICAL_RAINY: // -
				icon = ThemeManager.getScaledIcon(HOIconName.WEATHER_RAIN_NEG, 16, 10);
				break;
			default:
				icon = null;
			}
		}

		return icon;
	}
}
