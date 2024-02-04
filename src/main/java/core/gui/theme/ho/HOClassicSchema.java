package core.gui.theme.ho;

import core.gui.theme.*;
import core.util.HOLogger;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class HOClassicSchema extends Schema implements HOIconName, HOBooleanName {

	public HOClassicSchema() {
		initialize();
	}

	private void initialize() {
		setName("Classic");
//		initCachedColors();
//		initColors();
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

		put(SMILEYS[1], "gui/bilder/smilies/smiley-coach.svg");
		put(SMILEYS[2], "gui/bilder/smilies/smiley-sale.svg");
		put(SMILEYS[3], "gui/bilder/smilies/smiley-happy.svg");
		put(SMILEYS[4], "gui/bilder/smilies/smiley-sad.svg");
		put(SMILEYS[5], "gui/bilder/smilies/smiley-neutral.svg");

		put(GROUP_TEAM, "gui/bilder/smilies/Group-Team.svg");
		put(GROUP_TEAM_CLEAN, "gui/bilder/group-team-clean.svg");
		put(GREYED_OUT, "gui/bilder/smilies/Group-Team-Greyed.svg");

		put(WEATHER[0], "gui/bilder/match_events/weather0.png");
		put(WEATHER[1], "gui/bilder/match_events/weather1.png");
		put(WEATHER[2], "gui/bilder/match_events/weather2.png");
		put(WEATHER[3], "gui/bilder/match_events/weather3.png");
		put(WEATHER[4], "gui/bilder/weather-too-early.svg");
		put(WEATHER_EFFECT_GOOD, "gui/bilder/Fugue/tick-small.png");
		put(WEATHER_EFFECT_BAD, "gui/bilder/Fugue/cross-small.png");
		put(WEATHER_RAIN_POS, "gui/bilder/Fugue/weather-rain-pos-se.png");
		put(WEATHER_RAIN_NEG, "gui/bilder/Fugue/weather-rain-neg-se.png");
		put(WEATHER_SUN_POS, "gui/bilder/Fugue/weather-sun-pos-se.png");
		put(WEATHER_SUN_NEG, "gui/bilder/Fugue/weather-sun-neg-se.png");

		put(SPECIALTIES[1], "gui/bilder/player overview/player_specialty_1.svg");
		put(SPECIALTIES[2], "gui/bilder/player overview/player_specialty_2.svg");
		put(SPECIALTIES[3], "gui/bilder/player overview/player_specialty_3.svg");
		put(SPECIALTIES[4], "gui/bilder/player overview/player_specialty_4.svg");
		put(SPECIALTIES[5], "gui/bilder/player overview/player_specialty_5.svg");
		put(SPECIALTIES[6], "gui/bilder/player overview/player_specialty_6.svg");
		put(SPECIALTIES[8], "gui/bilder/player overview/player_specialty_8.svg");

		put(TOOTHEDWHEEL, "gui/bilder/automatic.png");
		put(HAND, "gui/bilder/manual.png");

		put(NO_MATCH, "gui/bilder/NoMatch.gif");

		put(DISK, "gui/bilder/disk.png");
		put(LOCKED, "gui/bilder/Locked.gif");
		put(EMPTY, "gui/bilder/empty.gif");
		put(INFO, "gui/bilder/info.gif");
		put(UPLOAD, "gui/bilder/upload.svg");
		put(WARNING_ICON, "gui/bilder/warning.svg");
		put(GOTOANALYSETOP, "gui/bilder/gotoAnalyseTop.svg");
		put(GOTOANALYSEBOTTOM, "gui/bilder/gotoAnalyseBottom.svg");
		put(OFFSET, "gui/bilder/offset.svg");
		put(GOTOSTATISTIK, "gui/bilder/gotoStatistik.svg");
		put(NO_CLUB_LOGO, "gui/bilder/no-logo.png");

		put(RELOAD, "gui/bilder/Reload.png");
		put(SIMULATEMATCH, "gui/bilder/simulate_match.png");
		put(GETLINEUP, "gui/bilder/AufstellungUebernehmen.png");
		put(SWAP, "gui/bilder/swap.svg");
		put(RESET, "gui/bilder/reset.svg");
		put(TURN, "gui/bilder/rotate.svg");

		put(LOGO16_STABLE, "gui/bilder/Logo-16px_stable.png");
		put(LOGO16_BETA, "gui/bilder/Logo-16px_beta.png");
		put(LOGO16_DEV, "gui/bilder/Logo-16px_dev.png");
		put(TRICKOT, "gui/bilder/jerseys.svg");

		// Player Overview
		put(YELLOWCARD_SMALL, "gui/bilder/player overview/yellow-card(yellow)-small.png");
		put(TWOYELLOWCARDS_SMALL, "gui/bilder/player overview/yellow-card-x2(yellow)-small.png");
		put(REDCARD_SMALL, "gui/bilder/player overview/red-card(red)-small.png");
		put(TRANSFERLISTED_TINY, "gui/bilder/player overview/transferlisted.svg");
		put(SUSPENDED_TINY, "img/icons/made/red-card(red).svg");
		put(TWOYELLOW_TINY, "img/icons/made/yellow-card-x2(yellow).svg");
		put(ONEYELLOW_TINY, "img/icons/made/yellow-card(yellow).svg");

		// Highlights

		put(YELLOWCARD, "img/icons/made/yellow-card(yellow).svg");
		put(REDCARD, "gui/bilder//red-card.svg");
		put(FORMATION, "gui/bilder/mo-select.svg");
		put(REPLACEMENT, "gui/bilder/exchange.svg");
		put(ROTATE, "gui/bilder/rotate.svg");
		put(GOAL, "gui/bilder/goal.svg");
		put(MISS, "gui/bilder/miss.svg");
		put(PENALTY, "gui/bilder/penalty.svg");
		put(EXPERIENCE, "gui/bilder/experience.svg");
		put(WHISTLE, "gui/bilder/whistle.svg");
		put(CORNER, "gui/bilder/corner.svg");
		put(TIRED, "gui/bilder/tired.svg");
		put(WINGER, "gui/bilder/winger.svg");
		put(GOAL_MID, "gui/bilder/goal_C.svg");
		put(GOAL_LEFT, "gui/bilder/goal_L.svg");
		put(GOAL_RIGHT, "gui/bilder/goal_R.svg");
		put(NO_GOAL_MID, "gui/bilder/miss_C.svg");
		put(NO_GOAL_LEFT, "gui/bilder/miss_L.svg");
		put(NO_GOAL_RIGHT, "gui/bilder/miss_R.svg");
		put(TACTIC_PRESSING, "gui/bilder/pressing.svg");
		put(TACTIC_COUNTER_ATTACKING, "gui/bilder/counter_attack.svg");
		put(TACTIC_AIM, "gui/bilder/aim.svg");
		put(TACTIC_AOW, "gui/bilder/aow.svg");
		put(TACTIC_PLAY_CREATIVELY, "gui/bilder/creative.svg");
		put(TACTIC_LONG_SHOTS, "gui/bilder/longshot.svg");

		put(PIECES, "gui/bilder/set_pieces.svg");
		put(CONFUSION, "gui/bilder/confusion.svg");
		put(REORGANIZE, "gui/bilder/reorganize.svg");
		put(CAPTAIN, "gui/bilder/captain.svg");
		put(ME_YELLOW_THEN_RED, "gui/bilder/yellow-red-card.svg");
		put(ME_SWAP, "gui/bilder/swap.svg");
		put(ME_MAN_MARKING, "gui/bilder/manmark.svg");

		put(HOMEGROWN, "gui/bilder/motherclub.png");
		put(IMAGEPANEL_BACKGROUND, "gui/bilder/Background.jpg");
		put(GRASSPANEL_BACKGROUND, "gui/bilder/Rasen_mit_Streifen.jpg");
		put(RATINGCOMPARISON_BACKGROUND, "gui/bilder/field_cut.png");


		put(REMOVE, "gui/bilder/remove.png");
		put(BALL, "gui/bilder/Ball.png");

		put(TABBEDPANE_CLOSE, "gui/bilder/closetab.png");

		put(ARROW_LEFT1, "gui/bilder/arrows/leftArrow1.gif");
		put(ARROW_LEFT2, "gui/bilder/arrows/leftArrow2.gif");
		put(ARROW_LEFT_3, "gui/bilder/arrow_left3.svg");
		put(ARROW_RIGHT1, "gui/bilder/arrows/rightArrow1.gif");
		put(ARROW_RIGHT2, "gui/bilder/arrows/rightArrow2.gif");
		put(ARROW_RIGHT3, "gui/bilder/arrow_right3.svg");

		put(ARROW_UP, "gui/bilder/arrows/ArrowUp.gif");
		put(ARROW_DOWN, "gui/bilder/arrows/ArrowDown.gif");

		put(ORDERS_SENT, "gui/bilder/orders_sent.svg");
		put(ORDERS_MISSING, "gui/bilder/orders_missing.svg");

		put(EXCLAMATION, "gui/bilder/Fugue/exclamation.png");
		put(EXCLAMATION_RED, "gui/bilder/Fugue/exclamation-red.png");
		put(CONTROL_DOUBLE_090, "gui/bilder/Fugue/control-double-090.png");
		put(CONTROL_DOUBLE_270, "gui/bilder/Fugue/control-double-270.png");
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
		// TODO: create new icon for man marking
		put(MAN_MARKING, "img/icons/official/HTwebsite/svg/manmark.svg");
		put(IFA_VISITED, "gui/bilder/Fugue/status.png");
		put(INFORMATION, "gui/bilder/Fugue/information-white.png");
		put(CHPP, "gui/bilder/chpp.png");
		put(CHPP_WHITE_BG, "gui/bilder/chpp_white_bg.png");
		
		put(HOME, "gui/bilder/home.png");
		put(AWAY, "gui/bilder/away.png");

		// Train bar
		put(TRAINING_BAR, "gui/bilder/trainpreview/training_bar.svg");
		put(TRAINING_ICON, "gui/bilder/training.svg");


		put(GREEN_WHITE_CLOCK, "gui/bilder/green-white-clock.png");
		put(WHITE_GREEN_CLOCK, "gui/bilder/white-green-clock.png");
		put(RED_WHITE_CLOCK, "gui/bilder/red-white-clock.png");
		put(WHITE_RED_CLOCK, "gui/bilder/white-red-clock.png");

		put(RATING_GRAPH, "gui/bilder/rating-graph.png");

		put(SPINNER, "gui/bilder/spinner.gif");

		put(UNKOWN, "gui/bilder/unknown.png");

		// Empty icon to avoid exceptions
		put(EMPTY_SVG, "gui/bilder/empty.svg");
	}

	private void initBooleans() {
		put(IMAGEPANEL_BG_PAINTED, Boolean.FALSE);

	}

	@Override
	public ImageIcon loadImageIcon(String path) {
		ImageIcon image;

		image = (ImageIcon) cache.get(path);
		if (image == null) {
			try {
				URL resource = HOClassicSchema.class.getClassLoader().getResource(path);
				if (resource == null) {
					try {
						// This is a shameless hack to get resources to load from IntelliJ.
						resource = new File("./src/main/resources" + path).toURI().toURL();
						image = new ImageIcon(resource);
						cache.put(path, image);
						return image;
					}
					catch (MalformedURLException e) {
						// At this point this is hopeless.
						e.printStackTrace();
					}
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
