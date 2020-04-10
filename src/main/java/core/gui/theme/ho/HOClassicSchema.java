/**
 *
 */
package core.gui.theme.ho;

import core.gui.theme.HOBooleanName;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.Schema;
import core.util.HOLogger;

import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;

public class HOClassicSchema extends Schema implements HOIconName, HOColorName, HOBooleanName {

	public HOClassicSchema() {
		initialize();
	}

	private void initialize() {
		setName("Classic");
		initCachedColors();
		initColors();
		initBooleans();
		initIcons();
	}

	private void initIcons() {
		put(MATCHICONS[0], "gui/bilder/match_types/matchtype-League.png");
		put(MATCHICONS[1], "gui/bilder/match_types/matchtype-qualification.png");
		put(MATCHICONS[2], "gui/bilder/match_types/matchtype-Friendly.png");
		put(MATCHICONS[3], "gui/bilder/match_types/matchtype-matchCupA.png");  //national
		put(MATCHICONS[4], "gui/bilder/match_types/matchtype-matchCupB1.png");  //emerald
		put(MATCHICONS[5], "gui/bilder/match_types/matchtype-matchCupB2.png");  //ruby
		put(MATCHICONS[6], "gui/bilder/match_types/matchtype-matchCupB3.png");  //sapphir
		put(MATCHICONS[7], "gui/bilder/match_types/matchtype-tournament-ladder.png");
		put(MATCHICONS[8], "gui/bilder/match_types/matchtype-tournament.png");
		put(MATCHICONS[9], "gui/bilder/match_types/matchtype-single-match.png");
		put(MATCHICONS[10], "gui/bilder/match_types/matchtype-matchMasters.png");
		put(MATCHICONS[11], "gui/bilder/default_match_icon.gif");
		put(MATCHICONS[12], "gui/bilder/match_types/matchtype-matchCupC.png");  //consolante
		put(MATCHICONS[13], "gui/bilder/match_types/matchtype-battle.png");

		put(MANUELLSMILIES[1], "gui/bilder/smilies/1bigsmile.gif");
		put(MANUELLSMILIES[2], "gui/bilder/smilies/2smile.gif");
		put(MANUELLSMILIES[3], "gui/bilder/smilies/3normal.gif");
		put(MANUELLSMILIES[4], "gui/bilder/smilies/4sad.gif");
		put(MANUELLSMILIES[5], "gui/bilder/smilies/5verysad.gif");
		put(MANUELLSMILIES[6], "gui/bilder/smilies/6clown.gif");
		put(MANUELLSMILIES[7], "gui/bilder/smilies/7trainer.gif");
		put(MANUELLSMILIES[8], "gui/bilder/smilies/8dollar.gif");
		put(MANUELLSMILIES[9], "gui/bilder/smilies/9coach.gif");

		put(NO_TEAM, "gui/bilder/smilies/No-Team.png");
		put(TEAMSMILIES[1], "gui/bilder/smilies/A-Team.png");
		put(TEAMSMILIES[2], "gui/bilder/smilies/B-Team.png");
		put(TEAMSMILIES[3], "gui/bilder/smilies/C-Team.png");
		put(TEAMSMILIES[4], "gui/bilder/smilies/D-Team.png");
		put(TEAMSMILIES[5], "gui/bilder/smilies/E-Team.png");
		put(TEAMSMILIES[6], "gui/bilder/smilies/F-Team.png");

		put(WEATHER[0], "gui/bilder/match_events/weather0.png");
		put(WEATHER[1], "gui/bilder/match_events/weather1.png");
		put(WEATHER[2], "gui/bilder/match_events/weather2.png");
		put(WEATHER[3], "gui/bilder/match_events/weather3.png");
		put(WEATHER_EFFECT_GOOD, "gui/bilder/Fugue/tick-small.png");
		put(WEATHER_EFFECT_BAD, "gui/bilder/Fugue/cross-small.png");
		put(WEATHER_RAIN_POS, "gui/bilder/Fugue/weather-rain-pos-se.png");
		put(WEATHER_RAIN_NEG, "gui/bilder/Fugue/weather-rain-neg-se.png");
		put(WEATHER_SUN_POS, "gui/bilder/Fugue/weather-sun-pos-se.png");
		put(WEATHER_SUN_NEG, "gui/bilder/Fugue/weather-sun-neg-se.png");

		put(SPECIALTIES[1], "gui/bilder/match_events/speciality-1.png");
		put(SPECIALTIES[2], "gui/bilder/match_events/speciality-2.png");
		put(SPECIALTIES[3], "gui/bilder/match_events/speciality-3.png");
		put(SPECIALTIES[4], "gui/bilder/match_events/speciality-4.png");
		put(SPECIALTIES[5], "gui/bilder/match_events/speciality-5.png");
		put(SPECIALTIES[6], "gui/bilder/match_events/speciality-6.png");
		put(SPECIALTIES[8], "gui/bilder/match_events/speciality-8.png");
		put(SPECIALTIES_SMALL[1], "gui/bilder/player overview/speciality-1-small.png");
		put(SPECIALTIES_SMALL[2], "gui/bilder/player overview/speciality-2-small.png");
		put(SPECIALTIES_SMALL[3], "gui/bilder/player overview/speciality-3-small.png");
		put(SPECIALTIES_SMALL[4], "gui/bilder/player overview/speciality-4-small.png");
		put(SPECIALTIES_SMALL[5], "gui/bilder/player overview/speciality-5-small.png");
		put(SPECIALTIES_SMALL[6], "gui/bilder/player overview/speciality-6-small.png");
		put(SPECIALTIES_SMALL[8], "gui/bilder/player overview/speciality-8-small.png");

		put(TOOTHEDWHEEL, "gui/bilder/zahnrad.png");
		put(HAND, "gui/bilder/Manuell.png");

		put(NO_MATCH, "gui/bilder/NoMatch.gif");
		put(SHOW_MATCH, "gui/bilder/ShowMatch.gif");
		put(DOWNLOAD_MATCH, "gui/bilder/DownloadMatch.gif");

		put(PRINTER, "gui/bilder/Drucken.png");
		put(DISK, "gui/bilder/disk.png");
		put(LOCKED, "gui/bilder/Locked.gif");
		put(EMPTY, "gui/bilder/empty.gif");
		put(INFO, "gui/bilder/info.gif");
		put(FEEDBACK, "gui/bilder/feedback.gif");
		put(GOTOANALYSETOP, "gui/bilder/gotoAnalyseTop.png");
		put(GOTOANALYSEBOTTOM, "gui/bilder/gotoAnalyseBottom.png");
		put(OFFSET, "gui/bilder/offset.png");
		put(GOTOSTATISTIK, "gui/bilder/gotoStatistik.png");
		put(TRAININGBLOCK, "gui/bilder/trainingblock.png");

		put(MAXLINEUP, "gui/bilder/MaxAufstellung.png");
		put(RELOAD, "gui/bilder/Reload.png");
		put(SIMULATEMATCH, "gui/bilder/simulate_match.png");
		put(GETLINEUP, "gui/bilder/AufstellungUebernehmen.png");
		put(SWAP, "gui/bilder/swap.png");
		put(SWAPPRESSED, "gui/bilder/swap-pressed.png");
		put(TURN, "gui/bilder/drehen.png");

		put(CLEARASSIST, "gui/bilder/Assist_leeren.png");
		put(STARTASSIST, "gui/bilder/Assist_start.png");
		put(CLEARRESERVE, "gui/bilder/Assist_reserveleeren.png");
		put(CLEARPOSORDERS, "gui/bilder/Assist_clearPosOrders.png");

		put(LOGO16_STABLE, "gui/bilder/Logo-16px_stable.png");
		put(LOGO16_BETA, "gui/bilder/Logo-16px_beta.png");
		put(LOGO16_DEV, "gui/bilder/Logo-16px_dev.png");
		put(TRICKOT, "gui/bilder/Trickot.png");

		// Player Overview
		put(INJURED_SMALL, "gui/bilder/player overview/injured.png");
		put(BRUISED_SMALL, "gui/bilder/player overview/plaster-small.png");
		put(YELLOWCARD_SMALL, "gui/bilder/player overview/yellow-card(yellow)-small.png");
		put(TWOYELLOWCARDS_SMALL, "gui/bilder/player overview/yellow-card-x2(yellow)-small.png");
		put(REDCARD_SMALL, "gui/bilder/player overview/red-card(red)-small.png");

		// Highlights

		put(YELLOWCARD, "gui/bilder/match_events/yellow-card(yellow).png");
		put(REDCARD, "gui/bilder/match_events/red-card(red).png");

		put(FORMATION, "gui/bilder/match_events/mo-select(green).png");


		put(BRUISED, "gui/bilder/match_events/plaster.png");
		put(INJURED, "gui/bilder/match_events/injury(red).png");
		put(REPLACEMENT, "gui/bilder/match_events/in-out.png");
		put(ROTATE, "gui/bilder/match_events/rotate.png");

		put(GOAL, "gui/bilder/match_events/goal.png");
		put(MISS, "gui/bilder/match_events/miss.png");
		put(GOAL_MID, "gui/bilder/match_events/goal_C.png");
		put(GOAL_LEFT, "gui/bilder/match_events/goal_L.png");
		put(GOAL_RIGHT, "gui/bilder/match_events/goal_R.png");
		put(CONVERTED_PENALTY, "gui/bilder/match_events/converted_penalty.png");
		put(MISSED_PENALTY, "gui/bilder/match_events/missed_penalty.png");
		put(NO_GOAL_MID, "gui/bilder/match_events/miss_C.png");
		put(NO_GOAL_LEFT, "gui/bilder/match_events/miss_L.png");
		put(NO_GOAL_RIGHT, "gui/bilder/match_events/miss_R.png");

		put(TACTIC_PRESSING, "gui/bilder/match_events/tactic_pressing(green).png");
		put(TACTIC_COUNTER_ATTACKING, "gui/bilder/match_events/tactic_counter_attack.png");
		put(TACTIC_AIM, "gui/bilder/match_events/tactic_aim.png");
		put(TACTIC_AOW, "gui/bilder/match_events/tactic_aow.png");
		put(TACTIC_PLAY_CREATIVELY, "gui/bilder/match_events/tactic_play_creatively.png");
		put(TACTIC_LONG_SHOTS, "gui/bilder/match_events/tactic_long_shots.png");

		put(ME_GOAL_AND_QUICK, "gui/bilder/match_events/me_goal_and_quick.png");
		put(ME_GOAL_AND_HEAD_SPEC, "gui/bilder/match_events/me_goal_and_head_spec.png");
		put(ME_GOAL_AND_TECHNICAL, "gui/bilder/match_events/me_goal_and_technical.png");
		put(ME_GOAL_AND_UNPREDICTABLE, "gui/bilder/match_events/me_goal_and_unpredictable.png");
		put(PIECES, "gui/bilder/match_events/pieces.png");
		put(CONFUSION, "gui/bilder/match_events/confusion.png");
		put(REORGANIZE, "gui/bilder/match_events/reorganize.png");
		put(CAPTAIN, "gui/bilder/match_events/captain.png");
		put(ME_109, "gui/bilder/match_events/me_109.png");
		put(ME_117, "gui/bilder/match_events/me_117.png");
		put(ME_118, "gui/bilder/match_events/me_118.png");
		put(ME_135, "gui/bilder/match_events/me_135.png");
		put(ME_136, "gui/bilder/match_events/me_136.png");
		put(ME_137, "gui/bilder/match_events/me_137.png");
		put(ME_138, "gui/bilder/match_events/me_138.png");
        put(ME_139, "gui/bilder/match_events/me_139.png");
		put(ME_140, "gui/bilder/match_events/me_140.png");
		put(ME_141, "gui/bilder/match_events/me_141.png");
		put(ME_142, "gui/bilder/match_events/me_142.png");
		put(ME_143, "gui/bilder/match_events/me_143.png");
		put(ME_GOAL_AND_WHISTLE, "gui/bilder/match_events/me_goal_and_whistle.png");
		put(ME_184, "gui/bilder/match_events/me_184.png");
		put(ME_185, "gui/bilder/match_events/me_185.png");
		put(ME_NO_GOAL_UNPREDICTABLE, "gui/bilder/match_events/me_no_goal_unpredictable.png");
		put(ME_GOAL_LONGSHOT_PNG, "gui/bilder/match_events/me_goal_longshot.png");
		put(ME_MISS_LONGSHOT_PNG, "gui/bilder/match_events/me_miss_longshot.png");
		put(ME_209, "gui/bilder/match_events/me_209.png");
		put(ME_217, "gui/bilder/match_events/me_217.png");
		put(ME_NO_GOAL_AND_HEAD_SPEC, "gui/bilder/match_events/me_no_goal_and_head_spec.png");
		put(ME_ME_MISS_AND_QUICK, "gui/bilder/match_events/me_miss_and_quick.png");
		put(ME_MISSED_CORNER, "gui/bilder/match_events/missed_corner.png");
		put(ME_235, "gui/bilder/match_events/me_235.png");
        put(ME_236, "gui/bilder/match_events/me_236.png");
		put(ME_237, "gui/bilder/match_events/me_237.png");
        put(ME_239, "gui/bilder/match_events/me_239.png");
		put(ME_240, "gui/bilder/match_events/me_240.png");
		put(ME_241, "gui/bilder/match_events/me_241.png");
		put(ME_242, "gui/bilder/match_events/me_242.png");
		put(ME_243, "gui/bilder/match_events/me_243.png");
		put(ME_MISS_AND_WHISTLE, "gui/bilder/match_events/me_miss_and_whistle.png");
		put(ME_285, "gui/bilder/match_events/me_285.png");
		put(ME_186, "gui/bilder/match_events/me_186.png");
		put(ME_286, "gui/bilder/match_events/me_286.png");
		put(ME_289, "gui/bilder/match_events/me_289.png");
		put(ME_YELLOW_THEN_RED, "gui/bilder/match_events/me_YellowThenRed.png");

		put(STAR, "gui/bilder/star.gif");
		put(STAR_GRAY, "gui/bilder/star_grey.png");

		put(HOMEGROWN, "gui/bilder/motherclub.png");
		put(IMAGEPANEL_BACKGROUND, "gui/bilder/Background.jpg");
		put(GRASSPANEL_BACKGROUND, "gui/bilder/Rasen_mit_Streifen.jpg");
		put(RATINGCOMPARISON_BACKGROUND, "gui/bilder/field_cut.png");

		// 1.431
		put(REMOVE, "gui/bilder/remove.png");
		put(BALL, "gui/bilder/Ball.png");
		put(TRANSFER_IN, "gui/bilder/in.gif");
		put(TRANSFER_OUT, "gui/bilder/out.gif");

		put(TABBEDPANE_CLOSE, "gui/bilder/closetab.png");

		put(ARROW_LEFT1, "gui/bilder/arrows/leftArrow1.gif");
		put(ARROW_LEFT2, "gui/bilder/arrows/leftArrow2.gif");
		put(ARROW_RIGHT1, "gui/bilder/arrows/rightArrow1.gif");
		put(ARROW_RIGHT2, "gui/bilder/arrows/rightArrow2.gif");

		put(ARROW_UP, "gui/bilder/arrows/ArrowUp.gif");
		put(ARROW_DOWN, "gui/bilder/arrows/ArrowDown.gif");

		put(EXCLAMATION, "gui/bilder/Fugue/exclamation.png");
		put(EXCLAMATION_RED, "gui/bilder/Fugue/exclamation-red.png");
		put(CONTROL_DOUBLE_090, "gui/bilder/Fugue/control-double-090.png");
		put(CONTROL_DOUBLE_270, "gui/bilder/Fugue/control-double-270.png");
		put(ORDER_SET, "gui/bilder/Fugue/tick.png");
		put(PLAYS_AT_BEGINNING, "gui/bilder/Fugue/status.png");
		put(IS_RESERVE, "gui/bilder/Fugue/status-away.png");
		put(NOT_IN_LINEUP, "gui/bilder/Fugue/status-offline.png");
		put(MOVE_UP, "gui/bilder/Fugue/arrow-090-medium.png");
		put(MOVE_DOWN, "gui/bilder/Fugue/arrow-270-medium.png");
		put(MOVE_LEFT, "gui/bilder/Fugue/arrow-180-medium.png");
		put(MOVE_RIGHT, "gui/bilder/Fugue/arrow-000-medium.png");
		put(ARROW_CIRCLE, "gui/bilder/Fugue/arrow-circle-double-135.png");
		put(ARROW_MOVE, "gui/bilder/Fugue/arrow-move-recoloured.png");
		put(SUBSTITUTION, "gui/bilder/Fugue/arrow-circle-double-135-recoloured.png");
		put(IFA_VISITED, "gui/bilder/Fugue/status.png");
		put(INFORMATION, "gui/bilder/Fugue/information-white.png");
		put(CHPP, "gui/bilder/chpp.png");
		
		put(HOME, "gui/bilder/home.png");
		put(AWAY, "gui/bilder/away.png");
		
		put(PIC, "gui/bilder/pic.png");
		put(MOTS, "gui/bilder/mots.png");

		// Train bar
		put(TRAINBAR_FT, "gui/bilder/trainpreview/trainpre_ft.png");
		put(TRAINBAR_PT, "gui/bilder/trainpreview/trainpre_pt.png");
		put(TRAINBAR_FFT, "gui/bilder/trainpreview/trainpre_fft.png");
		put(TRAINBAR_FPT, "gui/bilder/trainpreview/trainpre_fpt.png");
		put(TRAINBAR_FT_FFT, "gui/bilder/trainpreview/trainpre_ft_fft.png");
		put(TRAINBAR_FT_FPT, "gui/bilder/trainpreview/trainpre_ft_fpt.png");
		put(TRAINBAR_PT_FPT, "gui/bilder/trainpreview/trainpre_pt_fpt.png");
		put(TRAINBAR_FT_PT, "gui/bilder/trainpreview/trainpre_ft_pt.png");
		put(TRAINBAR_FT_PT_E, "gui/bilder/trainpreview/trainpre_ft_pt_e.png");
		put(TRAINBAR_FT_E, "gui/bilder/trainpreview/trainpre_ft_e.png");
		put(TRAINBAR_PT_E, "gui/bilder/trainpreview/trainpre_pt_e.png");
		put(STAMINABAR_FT, "gui/bilder/trainpreview/stamina_ft.png");
		put(STAMINABAR_FFT, "gui/bilder/trainpreview/stamina_fft.png");
		put(STAMINABAR_FT_E, "gui/bilder/trainpreview/stamina_ft_e.png");
		put(TRAINBAR_EMPTY, "gui/bilder/trainpreview/trainpre_empty.png");

		put(GREEN_WHITE_CLOCK, "gui/bilder/green-white-clock.png");
		put(WHITE_GREEN_CLOCK, "gui/bilder/white-green-clock.png");
		put(RED_WHITE_CLOCK, "gui/bilder/red-white-clock.png");
		put(WHITE_RED_CLOCK, "gui/bilder/white-red-clock.png");

		put(RATING_GRAPH, "gui/bilder/rating-graph.png");

		put(SPINNER, "gui/bilder/spinner.gif");

		put(UNKOWN, "gui/bilder/unknown.png");
	}

	private void initBooleans() {
		put(IMAGEPANEL_BG_PAINTED, Boolean.TRUE);

	}

	private void initCachedColors() {
		// don´t use UIManager keys !!
		put("black", Color.BLACK);
		put("white", Color.WHITE);
		put("gray", Color.GRAY);
		put("green", Color.GREEN);
		put("yellow", Color.YELLOW);
		put("dark_gray", Color.DARK_GRAY);
		put("light_gray", Color.LIGHT_GRAY);
		put("lightGreen", new Color(220, 255, 220));
		put("lightYellow", new Color(255, 255, 200));
		put("ho_gray1", new Color(230, 230, 230));
	}

	/**
	 * key-Syntax => javaComponent.[hoComponent].property || name
	 *
	 */
	private void initColors() {
		put(PANEL_BG, "white");
		put(PANEL_BORDER, "dark_gray");
		put(BUTTON_BG, "white");
		put(BUTTON_ASSIST_CANCEL_BG,  new Color(226, 31, 31));
		put(BUTTON_ASSIST_OK_BG,  new Color(34, 225, 36));
		put(LABEL_ERROR_FG, Color.RED);
		put(LABEL_SUCCESS_FG, "green");
		put(LABEL_ONGREEN_FG, "white");
		put(LABEL_FG, "black");
		put(LIST_FG, "black");
		put(LIST_CURRENT_FG, new Color(0, 0, 150));
		put(TABLE_SELECTION_BG, new Color(235, 235, 235));
		put(TABLE_SELECTION_FG, LABEL_FG);
		put(LIST_SELECTION_BG, new Color(220, 220, 255));

		put(MATCHHIGHLIGHT_FAILED_FG, "gray");

		// player
		put(PLAYER_SKILL_SPECIAL_BG, "lightGreen");
		put(PLAYER_SKILL_BG, "lightYellow");
		put(TABLEENTRY_BG, "white");
		put(TABLEENTRY_FG, "black");
		put(PLAYER_POS_BG, new Color(220, 220, 255));
		put(PLAYER_SUBPOS_BG, new Color(235, 235, 255));
		put(PLAYER_OLD_FG, "gray");
		put(TABLEENTRY_IMPROVEMENT_FG, new Color(0, 200, 0));
		put(TABLEENTRY_DECLINE_FG, new Color(200, 0, 0));
		put(SKILLENTRY2_BG, "gray");

		// league Table
		put(TEAM_FG, new Color(50, 50, 150));
		put(LEAGUE_TITLE_BG, "ho_gray1");
		put(LEAGUE_PROMOTED_BG, "lightGreen");
		put(LEAGUE_RELEGATION_BG, "lightYellow");
		put(LEAGUE_DEMOTED_BG, new Color(255, 220, 220));
		put(LEAGUE_BG, "white");
		put(LEAGUE_FG, "black");

		// league history panel
		put(LEAGUEHISTORY_LINE1_FG, Color.GREEN);
		put(LEAGUEHISTORY_LINE2_FG, Color.CYAN);
		put(LEAGUEHISTORY_LINE3_FG, Color.GRAY);
		put(LEAGUEHISTORY_LINE4_FG, "black");
		put(LEAGUEHISTORY_LINE5_FG, Color.ORANGE);
		put(LEAGUEHISTORY_LINE6_FG, Color.PINK);
		put(LEAGUEHISTORY_LINE7_FG, Color.RED);
		put(LEAGUEHISTORY_LINE8_FG, Color.MAGENTA);
		put(LEAGUEHISTORY_CROSS_FG, Color.DARK_GRAY);
		put(LEAGUEHISTORY_GRID_FG, "light_gray");

		// lineup

		put(SEL_OVERLAY_SELECTION_BG, new Color(10, 255, 10, 40));
		put(SEL_OVERLAY_BG, new Color(255, 10, 10, 40));
		put(LINEUP_POS_MIN_BG, PANEL_BG);
		put(LINEUP_POS_MIN_BORDER, "light_gray");
		put(SUBST_CHANGED_VALUE_BG, "lightGreen");

		// shirts
		put(SHIRT_KEEPER, "black");
		put(SHIRT_CENTRALDEFENCE, new Color(0, 0, 220));
		put(SHIRT_WINGBACK, new Color(0, 220, 0));
		put(SHIRT_MIDFIELD, new Color(220, 220, 0));
		put(SHIRT_WING, new Color(220, 140, 0));
		put(SHIRT_FORWARD, new Color(220, 0, 0));
		put(SHIRT_SUBKEEPER, new Color(200, 200, 200));
		put(SHIRT_SUBDEFENCE, new Color(200, 200, 255));
		put(SHIRT_SUBMIDFIELD, new Color(255, 255, 180));
		put(SHIRT_SUBWING, new Color(255, 225, 180));
		put(SHIRT_SUBFORWARD, new Color(255, 200, 200));
		put(SHIRT, "ho_gray1");

		put(STAT_LEADERSHIP, Color.GRAY);
		put(STAT_EXPERIENCE, Color.DARK_GRAY);
		put(STAT_FORM, Color.PINK);
		put(STAT_STAMINA, Color.MAGENTA);
		put(STAT_LOYALTY, new Color(180, 180, 0));
		put(STAT_KEEPER, "black");
		put(STAT_DEFENDING, Color.BLUE);
		put(STAT_PLAYMAKING, "yellow");
		put(STAT_PASSING, Color.GREEN);
		put(STAT_WINGER, Color.ORANGE);
		put(STAT_SCORING, Color.RED);
		put(STAT_SET_PIECES, Color.CYAN);

		put(STAT_CASH, "black");
		put(STAT_WINLOST, Color.GRAY);
		put(STAT_INCOMESUM, Color.GREEN);
		put(STAT_COSTSUM, Color.RED);
		put(STAT_INCOMESPECTATORS, new Color(0, 180, 0));
		put(STAT_INCOMESPONSORS, new Color(0, 120, 60));
		put(STAT_INCOMEFINANCIAL, new Color(0, 60, 120));
		put(STAT_INCOMETEMPORARY, new Color(0, 0, 180));
		put(STAT_COSTARENA, new Color(180, 0, 0));
		put(STAT_COSTSPLAYERS, new Color(180, 36, 0));
		put(STAT_COSTFINANCIAL, new Color(180, 72, 0));
		put(STAT_COSTTEMPORARY, new Color(180, 108, 0));
		put(STAT_COSTSTAFF, new Color(180, 144, 0));
		put(STAT_COSTSYOUTH, new Color(180, 180, 0));
		put(STAT_FANS, Color.CYAN);
		put(STAT_MARKETVALUE, Color.BLUE);
		put(STAT_RATING, new Color(100, 200, 0));
		put(STAT_WAGE, new Color(150, 20, 20));
		put(STAT_RATING2, "black");
		put(STAT_TOTAL, Color.GRAY);
		put(STAT_MOOD, Color.PINK);
		put(STAT_CONFIDENCE, Color.CYAN);
		put(STAT_HATSTATS, Color.YELLOW);
		put(STAT_LODDAR, new Color(150, 20, 20));

		// matchtypes
		put(MATCHTYPE_BG, "white");
		put(MATCHTYPE_LEAGUE_BG, "lightYellow");
		put(MATCHTYPE_QUALIFIKATION_BG, new Color(255, 200, 200));
		put(MATCHTYPE_CUP_BG, new Color(200, 255, 200));
		put(MATCHTYPE_FRIENDLY_BG, Color.WHITE);
		put(MATCHTYPE_INT_BG, "light_gray");
		put(MATCHTYPE_MASTERS_BG, new Color(255, 215, 120));
		put(MATCHTYPE_INTFRIENDLY_BG, "white");
		put(MATCHTYPE_NATIONAL_BG, new Color(240, 220, 255));
		put(MATCHTYPE_TOURNAMENT_GROUP_BG, new Color(218, 237, 247));
		put(MATCHTYPE_TOURNAMENT_FINALS_BG, new Color(218, 237, 247));
		put(MATCHTYPE_DIVISIONBATTLE_BG, new Color(200, 210, 247));

		// 1.431
		put(MATCHDETAILS_PROGRESSBAR_GREEN, new Color(0, 124, 0));
		put(MATCHDETAILS_PROGRESSBAR_RED, new Color(124, 0, 0));

		// 1.432

		// Team Analyzer
		put(TA_TEAM_LEAGUE_NEXT, Color.RED);
		put(TA_TEAM_CUP_NEXT, Color.GREEN);
		put(TA_TEAM_TOURNAMENT_NEXT, new Color(0, 51, 255)); // Darkish blue
		put(TA_TEAM_TOURNAMENT, new Color(0, 179, 255)); // Lightish blue
		
		// Matches Analyzer
		put(MATCHESANALYZER_DEFAULT_FONT,			"black");
		put(MATCHESANALYZER_DEFAULT_BG,				"white");
		put(MATCHESANALYZER_MATCH_BG,				new Color(220, 220, 255));
		put(MATCHESANALYZER_LINEUP_BG,				"lightGreen");
		put(MATCHESANALYZER_POSITIVE_BAR_BG,		new Color(34, 139, 34));
		put(MATCHESANALYZER_NEGATIVE_BAR_BG,		new Color(246, 34, 23));
		put(MATCHESANALYZER_STATS_BG,				"lightYellow");
		put(MATCHESANALYZER_OVERALL_BG,				"light_gray");
		put(MATCHESANALYZER_PANELS_BORDER,			"light_gray");
		put(MATCHESANALYZER_FIELD_LINES,			"white");
		put(MATCHESANALYZER_FILED_GRASS,			new Color(64, 130, 61));
		put(MATCHESANALYZER_TEAM_LEAGUE_NEXT,		Color.RED);
		put(MATCHESANALYZER_TEAM_CUP_NEXT,			Color.GREEN);
		put(MATCHESANALYZER_TEAM_TOURNAMENT_NEXT,	new Color(0, 51, 255));
		put(MATCHESANALYZER_TEAM_TOURNAMENT,		new Color(0, 179, 255));
		put(MATCHESANALYZER_TEAM_MYTEAM,			"black");
	}

	public Color getDefaultColor(String key) {
		return key.contains("fg") ? Color.BLACK : Color.WHITE;
	}

	public ImageIcon getThemeIcon(String key) {
		return (ImageIcon) cache.get(key);
	}

	@Override
	public ImageIcon loadImageIcon(String path) {
		ImageIcon image = null;

		image = (ImageIcon) cache.get(path);
		if (image == null) {
			try {
				URL resource = HOClassicSchema.class.getClassLoader().getResource(path);
				if (resource == null) {
					HOLogger.instance().log(Schema.class, path + " Not Found!!!");
					return loadImageIcon("gui/bilder/Unknownflag.png");
				}

				image = new ImageIcon(resource);
				cache.put(path, image);

				return image;
			} catch (Throwable e) {
				HOLogger.instance().log(Schema.class, e);
			}
		}
		return image;
	}
}
