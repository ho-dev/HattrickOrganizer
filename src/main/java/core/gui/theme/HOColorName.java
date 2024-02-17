package core.gui.theme;


import core.model.HOVerwaltung;

import java.awt.*;
import java.util.ArrayList;

/**
 * Constants for Colors used in HO.
 * Modules can use them too.
 */
public enum HOColorName {

	// DEFAULT COLOR
	BLACK,
	WHITE,
	RED,
	PINK,
	BLUE,
	GREEN,
	ORANGE,
	DARK_GRAY,
	LIGHT_GRAY,
	LIGHTGREEN,
	LIGHTYELLOW,
	HO_GRAY1,
	FOREST_GREEN,
	GRAY,
	YELLOW,
	URL_PANEL_BG,
	PANEL_BG,
	PANEL_BORDER,
	PLAYER_POSITION_PANEL_BORDER,
	LABEL_FG,
	LABEL_ERROR_FG,
	LIST_FG,
	LIST_SELECTION_BG,
	BUTTON_BG,
	BUTTON_ASSIST_CANCEL_BG,
	BUTTON_ASSIST_OK_BG,
	TABLE_SELECTION_BG,
	TABLE_SELECTION_FG,
	TABLEENTRY_BG,
	TABLEENTRY_FG,
	BACKGROUND_CONTAINER,
	TABLEENTRY_IMPROVEMENT_FG,
	TABLEENTRY_DECLINE_FG,
	RATING_BORDER_BELOW_LIMIT,
	RATING_BORDER_ABOVE_LIMIT,
	SKILLENTRY2_BG,
	PLAYER_POS_BG,
	PLAYER_SUBPOS_BG,
	PLAYER_OLD_FG,
	PLAYER_SKILL_BG,
	PLAYER_SKILL_SPECIAL_BG,
	HOME_TEAM_FG,
	SELECTED_TEAM_FG,
	LINK_LABEL_FG,
	MATCHTYPE_BG,
	MATCHTYPE_LEAGUE_BG,
	MATCHTYPE_QUALIFIKATION_BG,
	MATCHTYPE_CUP_BG,
	MATCHTYPE_FRIENDLY_BG,
	MATCHTYPE_MASTERS_BG,
	MATCHTYPE_INTFRIENDLY_BG,
	MATCHTYPE_NATIONAL_BG,
	MATCHTYPE_TOURNAMENT_GROUP_BG,
	MATCHTYPE_TOURNAMENT_FINALS_BG,
	MATCHTYPE_DIVISIONBATTLE_BG,
	LEAGUEHISTORY_CROSS_FG,
	LEAGUE_TITLE_BG,
	LEAGUE_BG,
	LEAGUE_FG,
	SHIRT_KEEPER,
	SHIRT_CENTRALDEFENCE,
	SHIRT_WINGBACK,
	SHIRT_MIDFIELD,
	SHIRT_WING,
	SHIRT_FORWARD,
	SHIRT_SUBKEEPER,
	SHIRT_SUBDEFENCE,
	SHIRT_SUBMIDFIELD,
	SHIRT_SUBWING,
	SHIRT_SUBFORWARD,
	SHIRT,
	SMILEYS_COLOR,
	PLAYER_SPECIALTY_COLOR,
	PLAYER_STAR_COLOR,

	// Statistics
	STAT_PANEL_BG,
	STAT_PANEL_FG,
	LEAGUE_PANEL_BG,
	FORM_STREAK_WIN,
	FORM_STREAK_DRAW,
	FORM_STREAK_DEFEAT,
	FORM_STREAK_UNKNOWN,
	TABLE_LEAGUE_EVEN,
	TABLE_LEAGUE_ODD,

	// transfer module
	TRANSFER_IN_COLOR,
	TRANSFER_OUT_COLOR,

	//lineup
	SEL_OVERLAY_SELECTION_BG,
	SEL_OVERLAY_BG,
	SUBST_CHANGED_VALUE_BG,
	SWAP_COLOR,
	RESET_COLOR,
	SWAP_COLOR_PRESSED,
	LINEUP_PARTIAL_TRAINING,
	LINEUP_FULL_TRAINING,
	LINEUP_PLAYER_SELECTED,
	LINEUP_PLAYER_SUB,
	WARNING_ICON_CB_COLOR,

	// Training
	TRAINING_BIRTHDAY_BG,
	TRAINING_FULL_BG,
	TRAINING_PARTIAL_BG,
	TRAINING_OSMOSIS_BG,
	TRAINING_ICON_COLOR_1,
	TRAINING_ICON_COLOR_2,

	// TS Forecast
	TSFORECAST_ALT_COLOR,

	// HRF Explorer
	HRF_GREEN_BG,
	HRF_LIGHTBLUE_BG,
	HRF_DARKBLUE_BG,
	HRF_RED_BG,

	// Player State colours
	FG_INJURED,
	INJURY,
	PLASTER,
	FG_PROMOTION_INFO,

	// colors palettes used for graphics
	PALETTE13_0,
	PALETTE13_1,
	PALETTE13_2,
	PALETTE13_3,
	PALETTE13_4,
	PALETTE13_5,
	PALETTE13_6,
	PALETTE13_7,
	PALETTE13_8,
	PALETTE13_9,
	PALETTE13_10,
	PALETTE13_11,
	PALETTE13_12,

	// colors for training preview
	FULL_TRAINING_DONE,
	PARTIAL_TRAINING_DONE,
	FULL_TRAINING_PLANNED,
	FULL_STAMINA_DONE,
	STAMINA_PLANNED,
	PARTIAL_TRAINING_PLANNED,

	// colors for league details
	SHOW_MATCH,
	DOWNLOAD_MATCH,

	// Lineup module
    START_ASSISTANT,
	CLEAR_LINEUP,
	LINEUP_COLOR,
	LINEUP_HIGHLIGHT_FG,
	ORDERS_LINEUP,
	ORDERS_TICK,
	ORDERS_PEN,

	//Matches module
	HOME_ACTION,
	GUEST_ACTION,
	NEUTRAL_ACTION,
	BORDER_RATING_BAR,

	//Player details
	PLAYER_DETAILS_BAR_BORDER_COLOR,
	PLAYER_DETAILS_BAR_FILL_GREEN,
	PLAYER_DETAILS_STARS_FILL;

	@Override
	public String toString(){
		return HOVerwaltung.instance().getLanguageString("ls.color." + super.toString().toLowerCase());
	}
}
