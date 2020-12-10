package core.gui.theme;


import javax.print.DocFlavor;

/**
 * Constants for Colors used in HO.
 * Modules can use them too.
 */
public interface HOColorName {

	// DEFAULT COLOR
    String RED 							= "default.red";
	String BLUE 						= "default.blue";

	String PANEL_BG 					= "panel.bg";
	String PANEL_BORDER					= "panel.border";
	String PLAYER_POSITION_PANEL_BORDER = "player_position_panel_border";
	String LABEL_FG 					= "label.fg";
	String LABEL_ERROR_FG 				= "label.error.fg";
	String LABEL_SUCCESS_FG 			= "label.success.fg";
	String LABEL_ONGREEN_FG 			= "label.onGreen.fg";
	String LIST_FG 						= "list.fg";
	String LIST_CURRENT_FG 				= "list.current.fg";
	String LIST_SELECTION_BG 			= "list.selection.bg";
	String BUTTON_BG 					= "button.bg";
	String BUTTON_ASSIST_CANCEL_BG		= "button.assist.cancel.bg";
	String BUTTON_ASSIST_OK_BG			= "button.assist.ok.bg";
	String TABLE_SELECTION_BG			= "table.selection.bg";
	String TABLE_SELECTION_FG			= "table.selection.fg";

	String TABLEENTRY_BG 				= "tableEntry.bg";
	String TABLEENTRY_FG 				= "tableEntry.fg";
	String TABLEENTRY_IMPROVEMENT_FG 	= "tableEntry.improvement.fg";
	String TABLEENTRY_DECLINE_FG 		= "tableEntry.decline.fg";
	String RATING_BORDER_BELOW_LIMIT    = "rating_border_below_limit";
	String RATING_BORDER_ABOVE_LIMIT    = "rating_border_above_limit";

	String SKILLENTRY2_BG 				= "skillEntry2.bg";

	String PLAYER_POS_BG 				= "player.pos.bg";
	String PLAYER_SUBPOS_BG 			= "player.subpos.bg";
	String PLAYER_OLD_FG 				= "player.old.fg";
	String PLAYER_SKILL_BG 				= "player.skill.bg";
	String PLAYER_SKILL_SPECIAL_BG 		= "player.skill.special.bg";
	String HOME_TEAM_FG 				= "team.fg";
	String SELECTED_TEAM_FG 			= "selected_team_fg";

	String MATCHTYPE_BG 				= "matchtype.bg";
	String MATCHTYPE_LEAGUE_BG 			= "matchtype.league.bg";
	String MATCHTYPE_QUALIFIKATION_BG 	= "matchtype.qualification.bg";
	String MATCHTYPE_CUP_BG				= "matchtype.cup.bg";
	String MATCHTYPE_FRIENDLY_BG 		= "matchtype.friendly.bg";
	String MATCHTYPE_INT_BG 			= "matchtype.int.normal.bg";
	String MATCHTYPE_MASTERS_BG 		= "matchtype.masters.bg";
	String MATCHTYPE_INTFRIENDLY_BG 	= "matchtype.intFriendly.bg";
	String MATCHTYPE_NATIONAL_BG 		= "matchtype.national.bg";
	String MATCHTYPE_TOURNAMENT_GROUP_BG		= "matchtype.tourneyGroup.bg";
	String MATCHTYPE_TOURNAMENT_FINALS_BG 		= "matchtype.tourneyFinals.bg";
	String MATCHTYPE_DIVISIONBATTLE_BG 			= "matchtype.divisionbattlr.bg";


	String LEAGUEHISTORY_LINE1_FG 	= "leaguehistory.line1.fg";
	String LEAGUEHISTORY_LINE2_FG 	= "leaguehistory.line2.fg";
	String LEAGUEHISTORY_LINE3_FG 	= "leaguehistory.line3.fg";
	String LEAGUEHISTORY_LINE4_FG 	= "leaguehistory.line4.fg";
	String LEAGUEHISTORY_LINE5_FG 	= "leaguehistory.line5.fg";
	String LEAGUEHISTORY_LINE6_FG 	= "leaguehistory.line6.fg";
	String LEAGUEHISTORY_LINE7_FG 	= "leaguehistory.line7.fg";
	String LEAGUEHISTORY_LINE8_FG 	= "leaguehistory.line8.fg";
	String LEAGUEHISTORY_CROSS_FG 	= "leaguehistory.cross.fg";
	String LEAGUEHISTORY_GRID_FG  	= "leaguehistory.grid.fg";

	String LEAGUE_TITLE_BG 			= "league.title.bg";
	String LEAGUE_PROMOTED_BG 		= "league.promoted.bg";
	String LEAGUE_RELEGATION_BG 	= "league.relegation.bg";
	String LEAGUE_DEMOTED_BG 		= "league.demoted.bg";
	String LEAGUE_BG 				= "league.bg";
	String LEAGUE_FG 				= "league.fg";

	String SHIRT_KEEPER 			= "shirt.kee";
	String SHIRT_CENTRALDEFENCE 	= "shirt.cd";
	String SHIRT_WINGBACK 			= "shirt.wb";
	String SHIRT_MIDFIELD 			= "shirt.mid";
	String SHIRT_WING 				= "shirt.win";
	String SHIRT_FORWARD 			= "shirt.for";
	String SHIRT_SUBKEEPER			= "shirt.subKee";
	String SHIRT_SUBDEFENCE 		= "shirt.subDef";
	String SHIRT_SUBMIDFIELD 		= "shirt.subMid";
	String SHIRT_SUBWING 			= "shirt.subWin";
	String SHIRT_SUBFORWARD 		= "shirt.subFor";
	String SHIRT 					= "shirt";

	String SMILEYS_COLOR = "smileys_color";
	String PLAYER_SPECIALTY_COLOR = "player_specialty_color";
	String PLAYER_SPECIALTY_NEGATIVE_COLOR = "player_specialty_negative_color";
	String PLAYER_STAR_COLOR = "player_star_color";

	// Statistics
	String STAT_CASH 				= "stat.cash";
	String STAT_WINLOST 			= "stat.winLost";
	String STAT_INCOMESUM 			= "stat.incomeSum";
	String STAT_COSTSUM 			= "stat.costSum";
	String STAT_INCOMESPECTATORS 	= "stat.incomeSpectators";
	String STAT_INCOMESPONSORS		= "stat.incomeSponsors";
	String STAT_INCOMEFINANCIAL		= "stat.incomeFinancial";
	String STAT_INCOMETEMPORARY		= "stat.incomeTemporary";
	String STAT_COSTARENA			= "stat.costArena";
	String STAT_COSTSPLAYERS		= "stat.costsPlayers";
	String STAT_COSTFINANCIAL		= "stat.costFinancial";
	String STAT_COSTTEMPORARY		= "stat.costTemporary";
	String STAT_COSTSTAFF			= "stat.costStaff";
	String STAT_COSTSYOUTH			= "stat.costsYouth";
	String STAT_FANS				= "stat.fans";
	String STAT_MARKETVALUE			= "stat.marketValue";
	String STAT_WAGE				= "stat.wage";
	String STAT_RATING2				= "stat.rating2";
	String STAT_TOTAL				= "stat.total";
	String STAT_MOOD				= "stat.mood";
	String STAT_CONFIDENCE			= "stat.confidence";
	String STAT_HATSTATS			= "stat.hatstats";
	String STAT_LODDAR				= "stat.loddar";
	String STAT_PANEL_BG 			= "stat.panel.bg";
	String STAT_PANEL_FG			 = "stat.panel.fg";
	String STAT_PANEL_FG_HELPING_LINES = "stat.panel.fg.helping.lines";
	String LEAGUE_PANEL_BG 				= "league_panel_bg";

	String MATCHHIGHLIGHT_FAILED_FG	= "matchHighlight.failed.fg";

	String FORM_STREAK_WIN = "form.streak.win";
	String FORM_STREAK_DRAW = "form.streak.draw";
	String FORM_STREAK_DEFEAT = "form.streak.defeat";
	String FORM_STREAK_UNKNOWN = "form.streak.unknown";

	String TABLE_LEAGUE_EVEN = "table.league.even";
	String TABLE_LEAGUE_ODD = "table.league.odd";

	//lineup

	String SEL_OVERLAY_SELECTION_BG	= "selectorOverlay.selected.bg";
	String SEL_OVERLAY_BG 			= "selectorOverlay.bg";
	String LINEUP_POS_MIN_BG 		= "lineup.pos.min.bg";
	String LINEUP_POS_MIN_BORDER	= "lineup.pos.min.border";
	String SUBST_CHANGED_VALUE_BG   = "substitution.changed.value.bg";
	String SWAP_COLOR 				= "swap.color";
	String SWAP_COLOR_PRESSED 		= "swap.color.pressed";
	String LINEUP_PARTIAL_TRAINING  = "lineup.partial.training.bg";
	String LINEUP_FULL_TRAINING     = "lineup.full.training.bg";
	String LINEUP_RATING_BORDER = "lineup.rating.border";
	String LINEUP_PLAYER_SELECTED = "lineup.player.selected";
	String LINEUP_PLAYER_SUB = "lineup.player.sub";
	String LINEUP_BG_BUTTONS = "lineup_bg_buttons";
	String WARNING_ICON_CB_COLOR = "warning_icon_cb_color";

	//1.431
	String MATCHDETAILS_PROGRESSBAR_GREEN = "matchdetails.progressbar.green";
	String MATCHDETAILS_PROGRESSBAR_RED = "matchdetails.progressbar.red";

	//1.432
	// Team Analyzer
	String TA_TEAM_LEAGUE_NEXT = "teamanalyzer.teamlist.league";
	String TA_TEAM_CUP_NEXT = "teamanalyzer.teamlist.cup";
	String TA_TEAM_TOURNAMENT_NEXT = "teamanalyzer.teamlist.nexttournament";
	String TA_TEAM_TOURNAMENT = "teamanalyzer.teamlist.tournament";

	//1.434
	// Matches Analyzer
//	String MATCHESANALYZER_DEFAULT_FONT = "matchesanalyzer.font.default";
//	String MATCHESANALYZER_DEFAULT_BG = "matchesanalyzer.bg.default";
//	String MATCHESANALYZER_MATCH_BG = "matchesanalyzer.bg.match";
//	String MATCHESANALYZER_LINEUP_BG = "matchesanalyzer.bg.lineup";
//	String MATCHESANALYZER_STATS_BG = "matchesanalyzer.bg.stats";
//	String MATCHESANALYZER_POSITIVE_BAR_BG = "matchesanalyzer.bg.positivebar";
//	String MATCHESANALYZER_NEGATIVE_BAR_BG = "matchesanalyzer.bg.negativebar";
//	String MATCHESANALYZER_OVERALL_BG = "matchesanalyzer.bg.overall";
//	String MATCHESANALYZER_PANELS_BORDER = "matchesanalyzer.border";
//	String MATCHESANALYZER_FIELD_LINES = "matchesanalyzer.field.line";
//	String MATCHESANALYZER_FILED_GRASS = "matchesanalyzer.field.grass";
//	String MATCHESANALYZER_TEAM_LEAGUE_NEXT = "matchesanalyzer.cbox.league";
//	String MATCHESANALYZER_TEAM_CUP_NEXT = "matchesanalyzer.cbox.cup";
//	String MATCHESANALYZER_TEAM_TOURNAMENT_NEXT = "matchesanalyzer.cbox.tournamentnext";
//	String MATCHESANALYZER_TEAM_TOURNAMENT = "matchesanalyzer.cbox.tournament";
//	String MATCHESANALYZER_TEAM_MYTEAM = "matchesanalyzer.cbox.myteam";

	// Training
	String TRAINING_BIRTHDAY_BG = "training.birthday.bg";
	String TRAINING_FULL_BG = "training.full.bg";
	String TRAINING_PARTIAL_BG = "training.partial.bg";
	String TRAINING_OSMOSIS_BG = "training.osmosis.bg";

	// TS Forecast
	String TSFORECAST_ALT_COLOR = "tsforecast.alt.color";

	// HRF Explorer
	String HRF_GREEN_BG = "hrf.green.bg";
	String HRF_LIGHTBLUE_BG = "hrf.lightblue.bg";
	String HRF_DARKBLUE_BG = "hrf.darkblue.bg";
	String HRF_RED_BG = "hrf.red.bg";

	// Player State colours
	String FG_STANDARD = "player.state.standard";
	String FG_TRANSFERLISTED = "player.state.transferlisted";
	String FG_INJURED = "player.state.injured";
	String FG_BRUISED = "player.state.bruised";
	String FG_TWO_YELLOW_CARDS = "player.state.two-yellow-cards";
	String FG_RED_CARD = "player.state.red-card";
	String INJURY = "injury_indicator";
	String PLASTER = "plaster";

	// Promotion
	String BG_PROMOTION_INFO = "promotion.info.bg";
	String FG_PROMOTION_INFO = "promotion.info.fg";

	// colors palettes used for graphics
	String[] PALETTE13	= {"PALETTE13_0", "PALETTE13_1", "PALETTE13_2", "PALETTE13_3", "PALETTE13_4", "PALETTE13_5", "PALETTE13_6", "PALETTE13_7", "PALETTE13_8", "PALETTE13_9", "PALETTE13_10", "PALETTE13_11", "PALETTE13_12"};

	// colors for training preview
	String FULL_TRAINING_DONE = "full_training_done";
	String PARTIAL_TRAINING_DONE = "partial_training_done";
	String FULL_TRAINING_PLANNED = "full_training_planned";
	String FULL_STAMINA_DONE = "full_stamina_done";
	String STAMINA_PLANNED = "stamina_planned";
	String PARTIAL_TRAINING_PLANNED = "partial_training_planned";

	// colors for league details
	String SHOW_MATCH = "show_match";
	String DOWNLOAD_MATCH = "download_match";

    String START_ASSISTANT = "start_assistant";
    String CLEAR_LINEUP = "clear_lineup";
    String LINEUP_COLOR = "lineup_color";
}
