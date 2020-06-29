package module.matches;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.match.MatchEvent;

import java.awt.Color;

import javax.swing.ImageIcon;

public class MatchesHelper {

	private MatchesHelper() {
	}

	/**
	 * Get the color for the given highlight type and subtype.
	 */
	public static Color getColor4SpielHighlight(MatchEvent me) {
		    MatchEvent.MatchEventID meID =  me.getMatchEventID();
			if ((meID == MatchEvent.MatchEventID.YELLOW_CARD_NASTY_PLAY)   // #510
					|| (meID == MatchEvent.MatchEventID.YELLOW_CARD_CHEATING))  {  // #511
 				return core.model.UserParameter.instance().FG_TWO_YELLOW_CARDS;
			}
			else if ((meID == MatchEvent.MatchEventID.RED_CARD_2ND_WARNING_NASTY_PLAY)   //#512
					|| (meID == MatchEvent.MatchEventID.RED_CARD_2ND_WARNING_CHEATING)  // #513
					|| (meID == MatchEvent.MatchEventID.RED_CARD_WITHOUT_WARNING)) {    // #514
				return core.model.UserParameter.instance().FG_RED_CARD;

		} else if (me.isGoalEvent()) {return ThemeManager.getColor(HOColorName.LABEL_FG);
		} else if (me.isNonGoalEvent()) {return ThemeManager.getColor(HOColorName.MATCHHIGHLIGHT_FAILED_FG);
		} else if ((meID == MatchEvent.MatchEventID.INJURED_BUT_KEEPS_PLAYING)  // #90
					|| (meID == MatchEvent.MatchEventID.INJURED_AFTER_FOUL_BUT_CONTINUES)) { // #94
				return core.model.UserParameter.instance().FG_BRUISED;
			}
			else if ((meID == MatchEvent.MatchEventID.MODERATELY_INJURED_LEAVES_FIELD)  // #91
					|| (meID == MatchEvent.MatchEventID.BADLY_INJURED_LEAVES_FIELD)   // #92
					|| (meID == MatchEvent.MatchEventID.INJURED_AND_NO_REPLACEMENT_EXISTED)  // #93
					|| (meID == MatchEvent.MatchEventID.INJURED_AFTER_FOUL_AND_EXITS)   // #95
					|| (meID == MatchEvent.MatchEventID.INJURED_AFTER_FOUL_AND_NO_REPLACEMENT_EXISTED)  // #96
					|| (meID == MatchEvent.MatchEventID.KEEPER_INJURED_FIELD_PLAYER_HAS_TO_TAKE_HIS_PLACE)) { // #97
				return core.model.UserParameter.instance().FG_INJURED;
			}
		return ThemeManager.getColor(HOColorName.LABEL_FG);
	}

}
