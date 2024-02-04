package core.model.match;

import core.db.AbstractTable;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.player.Specialty;
import core.util.HODateTime;
import core.util.HOLogger;
import javax.swing.*;
import java.util.*;
import java.util.stream.IntStream;

import static core.model.match.MatchEvent.MatchEventID.SPECTATORS_OR_VENUE_RAIN;

public class MatchEvent extends AbstractTable.Storable {

    private String m_sEventText = "";

    private String m_sGehilfeName = "";

    private String m_sSpielerName = "";

    private boolean m_sGehilfeHeim = true;

    private boolean m_sSpielerHeim = true;

    private int m_iGehilfeID;
    private int m_iMatchEventID;
    private Integer eventVariation;
    private HODateTime matchDate;

    public int getMatchEventIndex() {
        return m_iMatchEventIndex;
    }

    public void setMatchEventIndex(Integer index) {
        if (index != null) this.m_iMatchEventIndex = index;
    }

    private int m_iMatchEventIndex;

    private MatchEventID m_matchEventID;

    private MatchPartId matchPartId;

    private int m_iMinute;

    private int m_iSpielerID;

    private int m_iTeamID;

    private int matchId;
    private MatchType matchType;

    public MatchPartId getMatchPartId() {
        return matchPartId;
    }

    public void setMatchPartId(MatchPartId matchPartId) {
        this.matchPartId = matchPartId;
    }

    public void setEventVariation(Integer iEventVariation) {
        this.eventVariation = iEventVariation;
    }

    public Integer getEventVariation() {
        return eventVariation;
    }

    public boolean isEndOfMatchEvent() {
        return m_iMatchEventID >= 599 && m_iMatchEventID <= 606;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public HODateTime getMatchDate() {
        return this.matchDate;
    }

    public void setMatchDate(HODateTime matchDate) {
        this.matchDate = matchDate;
    }


    public enum MatchEventID {
        UNKNOWN_MATCHEVENT(-1),
        PLAYERS_ENTER_THE_FIELD(19), TACTICAL_DISPOSITION(20), PLAYER_NAMES_IN_LINEUP(21), PLAYERS_FROM_NEIGHBORHOOD_USED(22), SAME_FORMATION_BOTH_TEAMS(23), TEAM_FORMATIONS_DIFFERENT(24),
        REGIONAL_DERBY(25), NEUTRAL_GROUND(26), AWAY_IS_ACTUALLY_HOME(27), SPECTATORS_OR_VENUE_RAIN(30), SPECTATORS_OR_VENUE_CLOUDY(31), SPECTATORS_OR_VENUE_FAIR_WEATHER(32),
        SPECTATORS_OR_VENUE_SUNNY(33), ARENA_EXTENDED_WITH_TEMPORARY_SEATS(35), ONLY_VENUE_RAIN(36), ONLY_VENUE_CLOUDY(37), ONLY_VENUE_FAIR_WEATHER(38), ONLY_VENUE_SUNNY(39),
        DOMINATED(40), BEST_PLAYER(41), WORST_PLAYER(42), HALF_TIME_RESULTS(45), HATTRICK_COMMENT(46), NO_TEAM_DOMINATED(47), PENALTY_CONTEST_GOAL_BY_TECHNICAL_NO_NERVES(55),
        PENALTY_CONTEST_GOAL_NO_NERVES(56), PENALTY_CONTEST_GOAL_IN_SPITE_OF_NERVES(57), PENALTY_CONTEST_NO_GOAL_BECAUSE_OF_NERVES(58), PENALTY_CONTEST_NO_GOAL_IN_SPITE_OF_NO_NERVES(59),
        UNDERESTIMATION(60), ORGANIZATION_BREAKS(61), WITHDRAW(62), REMOVE_UNDERESTIMATION_AT_PAUSE(63), REORGANIZE(64), NERVES_IN_IMPORTANT_THRILLING_GAME(65),
        REMOVE_UNDERESTIMATION_AT_PAUSE_GOALDIFF_IS_0(66), REMOVE_UNDERESTIMATION_AT_PAUSE_GOALDIFF_IS_1(67), SUCCESSFUL_PRESSING(68), REMOVE_UNDERESTIMATION(69), EXTENSION(70),
        PENALTY_CONTEST_AFTER_EXTENSION(71), EXTENSION_DECIDED(72), AFTER_22_PENALTIES_TOSSING_COIN(73), ADDED_TIME(75), NO_ADDED_TIME(76), NEW_CAPTAIN(80),
        NEW_SET_PIECES_TAKER(81), INJURED_BUT_KEEPS_PLAYING(90), MODERATELY_INJURED_LEAVES_FIELD(91), BADLY_INJURED_LEAVES_FIELD(92), INJURED_AND_NO_REPLACEMENT_EXISTED(93),
        INJURED_AFTER_FOUL_BUT_CONTINUES(94), INJURED_AFTER_FOUL_AND_EXITS(95), INJURED_AFTER_FOUL_AND_NO_REPLACEMENT_EXISTED(96), KEEPER_INJURED_FIELD_PLAYER_HAS_TO_TAKE_HIS_PLACE(97),
        REDUCING_GOAL_HOME_TEAM_FREE_KICK(100), REDUCING_GOAL_HOME_TEAM_MIDDLE(101), REDUCING_GOAL_HOME_TEAM_LEFT_WING(102), REDUCING_GOAL_HOME_TEAM_RIGHT_WING(103),
        REDUCING_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL(104), SE_GOAL_UNPREDICTABLE_LONG_PASS(105), SE_GOAL_UNPREDICTABLE_SCORES_ON_HIS_OWN(106), GOAL_LONG_SHOT_NO_TACTIC(107),
        SE_GOAL_UNPREDICTABLE_SPECIAL_ACTION(108), SE_GOAL_UNPREDICTABLE_MISTAKE(109), EQUALIZER_GOAL_HOME_TEAM_FREE_KICK(110), EQUALIZER_GOAL_HOME_TEAM_MIDDLE(111),
        EQUALIZER_GOAL_HOME_TEAM_LEFT_WING(112), EQUALIZER_GOAL_HOME_TEAM_RIGHT_WING(113), EQUALIZER_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL(114), SE_QUICK_SCORES_AFTER_RUSH(115),
        SE_QUICK_RUSHES_PASSES_AND_RECEIVER_SCORES(116), SE_TIRED_DEFENDER_MISTAKE_STRIKER_SCORES(117), SE_GOAL_CORNER_TO_ANYONE(118), SE_GOAL_CORNER_HEAD_SPECIALIST(119),
        GOAL_TO_TAKE_LEAD_HOME_TEAM_FREE_KICK(120), GOAL_TO_TAKE_LEAD_HOME_TEAM_MIDDLE(121), GOAL_TO_TAKE_LEAD_HOME_TEAM_LEFT_WING(122), GOAL_TO_TAKE_LEAD_HOME_TEAM_RIGHT_WING(123),
        GOAL_TO_TAKE_LEAD_HOME_TEAM_PENALTY_KICK_NORMAL(124), SE_GOAL_UNPREDICTABLE_OWN_GOAL(125), INCREASE_GOAL_HOME_TEAM_FREE_KICK(130), INCREASE_GOAL_HOME_TEAM_MIDDLE(131),
        INCREASE_GOAL_HOME_TEAM_LEFT_WING(132), INCREASE_GOAL_HOME_TEAM_RIGHT_WING(133), INCREASE_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL(134), SE_EXPERIENCED_FORWARD_SCORES(135),
        SE_INEXPERIENCED_DEFENDER_CAUSES_GOAL(136), SE_WINGER_TO_HEAD_SPEC_SCORES(137), SE_WINGER_TO_ANYONE_SCORES(138), SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER(139),
        COUNTER_ATTACK_GOAL_FREE_KICK(140), COUNTER_ATTACK_GOAL_MIDDLE(141), COUNTER_ATTACK_GOAL_LEFT(142), COUNTER_ATTACK_GOAL_RIGHT(143), REDUCING_GOAL_AWAY_TEAM_FREE_KICK(150),
        REDUCING_GOAL_AWAY_TEAM_MIDDLE(151), REDUCING_GOAL_AWAY_TEAM_LEFT_WING(152), REDUCING_GOAL_AWAY_TEAM_RIGHT_WING(153), REDUCING_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL(154),
        EQUALIZER_GOAL_AWAY_TEAM_FREE_KICK(160), EQUALIZER_GOAL_AWAY_TEAM_MIDDLE(161), EQUALIZER_GOAL_AWAY_TEAM_LEFT_WING(162), EQUALIZER_GOAL_AWAY_TEAM_RIGHT_WING(163),
        EQUALIZER_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL(164), GOAL_TO_TAKE_LEAD_AWAY_TEAM_FREE_KICK(170), GOAL_TO_TAKE_LEAD_AWAY_TEAM_MIDDLE(171), GOAL_TO_TAKE_LEAD_AWAY_TEAM_LEFT_WING(172),
        GOAL_TO_TAKE_LEAD_AWAY_TEAM_RIGHT_WING(173), GOAL_TO_TAKE_LEAD_AWAY_TEAM_PENALTY_KICK_NORMAL(174), INCREASE_GOAL_AWAY_TEAM_FREE_KICK(180), INCREASE_GOAL_AWAY_TEAM_MIDDLE(181),
        INCREASE_GOAL_AWAY_TEAM_LEFT_WING(182), INCREASE_GOAL_AWAY_TEAM_RIGHT_WING(183), INCREASE_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL(184), GOAL_INDIRECT_FREE_KICK(185),
        COUNTER_ATTACK_GOAL_INDIRECT_FREE_KICK(186), GOAL_LONG_SHOT(187), SE_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE(190), NO_REDUCING_GOAL_HOME_TEAM_FREE_KICK(200),
        NO_REDUCING_GOAL_HOME_TEAM_MIDDLE(201), NO_REDUCING_GOAL_HOME_TEAM_LEFT_WING(202), NO_REDUCING_GOAL_HOME_TEAM_RIGHT_WING(203), NO_REDUCING_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL(204),
        SE_NO_GOAL_UNPREDICTABLE_LONG_PASS(205), SE_NO_GOAL_UNPREDICTABLE_ALMOST_SCORES(206), NO_GOAL_LONG_SHOT_NO_TACTIC(207), SE_NO_GOAL_UNPREDICTABLE_SPECIAL_ACTION(208),
        SE_NO_GOAL_UNPREDICTABLE_MISTAKE(209), NO_EQUALIZER_GOAL_HOME_TEAM_FREE_KICK(210), NO_EQUALIZER_GOAL_HOME_TEAM_MIDDLE(211), NO_EQUALIZER_GOAL_HOME_TEAM_LEFT_WING(212),
        NO_EQUALIZER_GOAL_HOME_TEAM_RIGHT_WING(213), NO_EQUALIZER_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL(214), SE_SPEEDY_MISSES_AFTER_RUSH(215), SE_QUICK_RUSHES_PASSES_BUT_RECEIVER_FAILS(216),
        SE_TIRED_DEFENDER_MISTAKE_BUT_NO_GOAL(217), SE_NO_GOAL_CORNER_TO_ANYONE(218), SE_NO_GOAL_CORNER_HEAD_SPECIALIST(219), NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_FREE_KICK(220),
        NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_MIDDLE(221), NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_LEFT_WING(222), NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_RIGHT_WING(223), NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_PENALTY_KICK_NORMAL(224),
        SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST(225), NO_INCREASE_GOAL_HOME_TEAM_FREE_KICK(230), NO_INCREASE_GOAL_HOME_TEAM_MIDDLE(231), NO_INCREASE_GOAL_HOME_TEAM_LEFT_WING(232),
        NO_INCREASE_GOAL_HOME_TEAM_RIGHT_WING(233), NO_INCREASE_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL(234), SE_EXPERIENCED_FORWARD_FAILS_TO_SCORE(235), SE_INEXPERIENCED_DEFENDER_ALMOST_CAUSES_GOAL(236),
        SE_WINGER_TO_SOMEONE_NO_GOAL(237), SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER_NO_GOAL(239), COUNTER_ATTACK_NO_GOAL_FREE_KICK(240), COUNTER_ATTACK_NO_GOAL_MIDDLE(241), COUNTER_ATTACK_NO_GOAL_LEFT(242),
        COUNTER_ATTACK_NO_GOAL_RIGHT(243), NO_REDUCING_GOAL_AWAY_TEAM_FREE_KICK(250), NO_REDUCING_GOAL_AWAY_TEAM_MIDDLE(251), NO_REDUCING_GOAL_AWAY_TEAM_LEFT_WING(252),
        NO_REDUCING_GOAL_AWAY_TEAM_RIGHT_WING(253), NO_REDUCING_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL(254), NO_EQUALIZER_GOAL_AWAY_TEAM_FREE_KICK(260), NO_EQUALIZER_GOAL_AWAY_TEAM_MIDDLE(261),
        NO_EQUALIZER_GOAL_AWAY_TEAM_LEFT_WING(262), NO_EQUALIZER_GOAL_AWAY_TEAM_RIGHT_WING(263), NO_EQUALIZER_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL(264), NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_FREE_KICK(270),
        NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_MIDDLE(271), NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_LEFT_WING(272), NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_RIGHT_WING(273), NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_PENALTY_KICK_NORMAL(274),
        NO_INCREASE_GOAL_AWAY_TEAM_FREE_KICK(280), NO_INCREASE_GOAL_AWAY_TEAM_MIDDLE(281), NO_INCREASE_GOAL_AWAY_TEAM_LEFT_WING(282), NO_INCREASE_GOAL_AWAY_TEAM_RIGHT_WING(283),
        NO_INCREASE_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL(284), NO_GOAL_INDIRECT_FREE_KICK(285), COUNTER_ATTACK_NO_GOAL_INDIRECT_FREE_KICK(286), NO_GOAL_LONG_SHOT(287), NO_GOAL_LONG_SHOT_DEFENDED(288),
        SE_QUICK_RUSHES_STOPPED_BY_QUICK_DEFENDER(289), SE_NO_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE(290), SE_TECHNICAL_SUFFERS_FROM_RAIN(301), SE_POWERFUL_THRIVES_IN_RAIN(302),
        SE_TECHNICAL_THRIVES_IN_SUN(303), SE_POWERFUL_SUFFERS_FROM_SUN(304), SE_QUICK_LOSES_IN_RAIN(305), SE_QUICK_LOSES_IN_SUN(306), SE_SUPPORT_PLAYER_BOOST_SUCCEEDED(307),
        SE_SUPPORT_PLAYER_BOOST_FAILED_AND_ORGANIZATION_DROPPED(308), SE_SUPPORT_PLAYER_BOOST_FAILED(309), SE_POWERFUL_DEFENSIVE_INNER_PRESSES_CHANCE(310), COUNTER_ATTACK_TRIGGERED_BY_TECHNICAL_DEFENDER(311), TACTIC_TYPE_PRESSING(331),
        TACTIC_TYPE_COUNTER_ATTACKING(332), TACTIC_TYPE_ATTACK_IN_MIDDLE(333), TACTIC_TYPE_ATTACK_ON_WINGS(334), TACTIC_TYPE_PLAY_CREATIVELY(335), TACTIC_TYPE_LONG_SHOTS(336),
        TACTIC_ATTACK_IN_MIDDLE_USED(343), TACTIC_ATTACK_ON_WINGS_USED(344), PLAYER_SUBSTITUTION_TEAM_IS_BEHIND(350), PLAYER_SUBSTITUTION_TEAM_IS_AHEAD(351), PLAYER_SUBSTITUTION_MINUTE(352),
        CHANGE_OF_TACTIC_TEAM_IS_BEHIND(360), CHANGE_OF_TACTIC_TEAM_IS_AHEAD(361), CHANGE_OF_TACTIC_MINUTE(362), PLAYER_POSITION_SWAP_TEAM_IS_BEHIND(370), PLAYER_POSITION_SWAP_TEAM_IS_AHEAD(371),
        PLAYER_POSITION_SWAP_MINUTE(372), MAN_MARKING_SUCCESS_SHORT_DISTANCE(380), MAN_MARKING_SUCCESS_LONG_DISTANCE(381), MAN_MARKED_CHANGED_FROM_SHORT_TO_LONG_DISTANCE(382),
        MAN_MARKED_CHANGED_FROM_LONG_TO_SHORT_DISTANCE(383), MAN_MARKER_PENALTY_NO_MAN_MARKED_ON_THE_FIELD(384), MAN_MARKER_CHANGED_FROM_SHORT_TO_LONG_DISTANCE(385), MAN_MARKER_CHANGED_FROM_LONG_TO_SHORT_DISTANCE(386),
        MAN_MARKER_PENALTY_MAN_MARKED_NOT_IN_MARKING_POSITION(387), MAN_MARKER_PENALTY_MAN_MARKER_NOT_IN_MARKING_POSITION(388), MAN_MARKER_PENALTY_NO_MAN_MARKED_IN_OPPONENT_TEAM(389), RAINY_WEATHER_MANY_PLAYERS_AFFECTED(390),
        SUNNY_WEATHER_MANY_PLAYERS_AFFECTED(391), INJURY_KNEE_LEFT(401), INJURY_KNEE_RIGHT(402), INJURY_THIGH_LEFT(403), INJURY_THIGH_RIGHT(404), INJURY_FOOT_LEFT(405), INJURY_FOOT_RIGHT(406),
        INJURY_ANKLE_LEFT(407), INJURY_ANKLE_RIGHT(408), INJURY_CALF_LEFT(409), INJURY_CALF_RIGHT(410), INJURY_GROIN_LEFT(411), INJURY_GROIN_RIGHT(412), INJURY_COLLARBONE(413), INJURY_BACK(414),
        INJURY_HAND_LEFT(415), INJURY_HAND_RIGHT(416), INJURY_ARM_LEFT(417), INJURY_ARM_RIGHT(418), INJURY_SHOULDER_LEFT(419), INJURY_SHOULDER_RIGHT(420), INJURY_RIB(421), INJURY_HEAD(422),
        INJURED_BY_FOUL(423), INJURED_PLAYER_REPLACED(424), NO_REPLACEMENT_FOR_INJURED_PLAYER(425), FIELD_PLAYER_HAS_TO_TAKE_INJURED_KEEPERS_PLACE(426), PLAYER_INJURED_WAS_REGAINER_SO_GOT_BRUISED_INSTEAD(427),
        PLAYER_GOT_THIRD_YELLOW_CARD_MISSES_NEXT_MATCH(450), WITH_THIS_STANDING_TEAM_X_WILL_RELEGATE_TO_CUP_Y(451), PLAYER_CURRENT_TEAM_MATCHES_100S_ANNIVERSARY(452), PLAYER_POSSIBLY_THE_LAST_GAME_IN_THIS_TEAM(453),
        DOCTOR_REPORT_OF_INJURY_LENGTH(454), NEW_STAR_PLAYER_OF_THE_TEAM(455), PLAYER_CAREER_GOALS_MULTIPLE_OF_50(456), PLAYER_LEAGUE_GOALS_THIS_SEASON(457), PLAYER_CUP_GOALS_THIS_SEASON(458), BENCH_PLAYER_WARMING_UP(459),
        FANS_SHOCKED_BY_LOSING(460), FANS_UPSET_BY_LOSING(461), FANS_SURPRISED_BY_WINNING(462), FANS_EXCITED_BY_WINNING(463), EXACT_NUMBER_OF_SPECTATORS(464), TEAM_SHOULD_WIN_MATCH_TO_SECURE_WINNING_THE_LEAGUE(465),
        TEAM_SHOULD_WIN_MATCH_TO_HAVE_CHANCE_OF_WINNING_LEAGUE(466), THE_WINNER_OF_THIS_MATCH_IF_THERE_IS_ONE_CAN_HAVE_A_CHANCE_OF_WINNING_THE_LEAGUE(467), TEAM_SHOULD_WIN_MATCH_TO_MAKE_SURE_THEY_DONT_DEMOTE(468),
        TEAM_SHOULD_WIN_MATCH_TO_HAVE_A_CHANCE_OF_NOT_DEMOTING(469), THE_LOSER_OF_THIS_MATCH_WILL_DEMOTE(470), HOMETEAM_OR_AWAYTEAM_HAS_MOST_POSSESSION_IN_BEGINNING_OF_MATCH(471), EQUAL_POSSESSION_IN_BEGINNING_OF_MATCH(472),
        CAREER_ENDING_INJURY(473), POSSESSION_SHIFTED(474), LOW_ATTENDANCE_BECAUSE_OF_FAN_MOOD(475), EXTRA_SECURITY_BECAUSE_OF_FAN_MOOD(476), BOTH_TEAMS_FANS_ARE_ANGRY(477), TEAM_WILL_HAVE_BEST_CUP_RUN_IF_WIN(478),
        BOTH_TEAMS_COULD_HAVE_BEST_CUP_RUN_IF_WIN_COMPETING(479), CURRENT_ROUND_IS_TEAMS_BEST_CUP_RUN(480), NEW_FORMATION_TODAY(481), TEAMS_USING_THE_SAME_STYLE_OF_PLAY(482), TEAMS_USING_DIFFERENT_STYLES_OF_PLAY(483),
        ONE_TEAMS_STYLE_OF_PLAY(484), TEAM_OF_OLDIES(485), TEAM_IS_AGGRESSIVE(486), TEAM_HAS_ONLY_HOMEGROWN_PLAYERS(487), TEAM_HAS_ALL_PLAYERS_FROM_SAME_COUNTRY(488), COMEBACK_AFTER_A_LONG_INJURY(489),
        PREVIOUS_MATCH_CUP_SIMILAR_OUTCOME(490), PREVIOUS_MATCH_CUP_DIFFERENT_OUTCOME(491), PREVIOUS_MATCH_LEAGUE_SIMILAR_OUTCOME(492), PREVIOUS_MATCH_LEAGUE_DIFFERENT_OUTCOME(493), TEAM_HAS_THE_BALL_BUT_IS_NOT_ATTACKING(494),
        TEAM_HAS_THE_BALL_AND_HAS_STARTED_ATTACKING(495), TEAM_IS_STILL_IN_THE_CUP_FOR_LEAGUE_MATCHES(496), BOTH_TEAMS_ARE_STILL_IN_THE_CUP_FOR_LEAGUE_MATCHES(497), TEAM_IS_LOOKING_TIRED_LOW_AVG_STAMINA(498),
        BOTH_TEAMS_WALKOVER(500), HOME_TEAM_WALKOVER(501), AWAY_TEAM_WALKOVER(502), BOTH_TEAMS_BREAK_GAME_2_PLAYERS_REMAINING(503), HOME_TEAM_BREAKS_GAME_2_PLAYERS_REMAINING(504),
        AWAY_TEAM_BREAKS_GAME_2_PLAYERS_REMAINING(505), YELLOW_CARD_NASTY_PLAY(510), YELLOW_CARD_CHEATING(511), RED_CARD_2ND_WARNING_NASTY_PLAY(512), RED_CARD_2ND_WARNING_CHEATING(513), RED_CARD_WITHOUT_WARNING(514),
        EXTRA_TIME_STARTED_THIRD_HALF(596), SECOND_HALF_STARTED(597), MATCH_STARTED(598), MATCH_FINISHED(599), CONGRATULATIONS_TO_THE_WINNER(601), WINNER_ADVANCES_TO_NEXT_CUP_ROUND_NO_RELEGATION_CUP_FOR_LOSER(602),
        WINNER_ADVANCES_TO_NEXT_CUP_ROUND_AND_LOSER_RELEGATES_TO_CUP_X(603), MATCH_ENDED_IN_A_TIE(604), END_OF_MATCH_CONGRATULATIONS_TEAM_WON_THE_LEAGUE(605), END_OF_MATCH_SAD_THAT_TEAM_WILL_DEMOTE_DIRECTLY(606),
        HATTRICK_ANNIVERSARY(650), TEAM_ANNIVERSARY(651), EVENTOMATIC_MANAGER_TAUNTS_OPPONENT(700), EVENTOMATIC_MANAGER_PRAISES_OPPONENT(701), EVENTOMATIC_MANAGER_ASKS_FANS_FOR_SUPPORT(702),
        EVENTOMATIC_MANAGER_EXPECTS_GREAT_SHOW(703), EVENTOMATIC_MANAGER_HONOURS_CLUB_LEGACY(704), STAR_PLAYER_MISSED_MATCH_BECAUSE_OF_RED_CARD(800), STAR_PLAYER_MISSED_MATCH_BECAUSE_OF_INJURY(801),
        TEAM_IS_ON_WINNING_STREAK(802), BOTH_TEAMS_ARE_ON_WINNING_STREAK(803), TEAM_WILL_BREAK_WINNING_STREAK(804), WEAKEST_TEAM_HTRATING_IS_WINNING(805),
        POSSESSION_SHIFT_BECAUSE_OF_RED_CARD(806), POSSESSION_SHIFT_BECAUSE_OF_SUBSTITUTION_LOST(807), POSSESSION_SHIFT_BECAUSE_OF_SUBSTITUTION_GAINED(808),
        POSSESSION_SHIFT_OTHER_REASON(809), PREVIOUS_MATCH_WINNER(810), PREVIOUS_MATCH_WAS_A_TIE(811), PLAYER_BIRTHDAY(812), NEW_MATCH_KIT(813),
        TEAM_UNDERPERFORMING_COMPARED_TO_LAST_LEAGUE_MATCH_HT_RATING(814), TEAM_OVERPERFORMING_COMPARED_TO_LAST_LEAGUE_MATCH_HT_RATING(815),
        POSSESSION_SHIFT_BECAUSE_OF_TEAM_CONFUSION(816), POSSESSION_SHIFT_BECAUSE_OF_TEAM_NERVES(817),
        BROTHERS_PLAY_FOR_SAME_TEAM(818), FATHER_SON_PLAY_FOR_SAME_TEAM(819), FAMILY_MEMBERS_PLAY_AGAINST_EACH_OTHER(820), Family_members_assist_goal(821), FAMILY_MEMBERS_FACING_EACH_OTHER_IN_A_PENALTY(822);

        private final int value;

        MatchEventID(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }

        // Reverse-lookup map for getting a MatchEvent from its value
        private static final HashMap<Integer, MatchEventID> lookup = new HashMap<>();

        static {
            for (MatchEventID me : MatchEventID.values()) {
                lookup.put(me.getValue(), me);
            }
        }

        public static MatchEventID fromMatchEventID(int iMatchEventID) {
            MatchEventID ret = lookup.get(iMatchEventID);
            if (ret == null) {
                ret = UNKNOWN_MATCHEVENT;
                HOLogger.instance().log(MatchEventID.class, "UNKNOWN_MATCHEVENT: " + iMatchEventID);
            }
            return ret;
        }

    }

    public enum MatchPartId {
        BEFORE_THE_MATCH_STARTED(0),
        FIRST_HALF(1),
        SECOND_HALF(2),
        OVERTIME(3),
        PENALTY_CONTEST(4);

        private final int value;

        MatchPartId(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }

        // Reverse-lookup map for getting a MatchEvent from its value
        private static final HashMap<Integer, MatchPartId> lookup = new HashMap<>();

        static {
            for (MatchPartId me : MatchPartId.values()) {
                lookup.put(me.getValue(), me);
            }
        }

        public static MatchPartId fromMatchPartId(Integer iMatchPartId) {
            if (iMatchPartId == null) return null;
            MatchPartId ret = lookup.get(iMatchPartId);
            if (ret == null) {
                HOLogger.instance().log(MatchPartId.class, "UNKNOWN_MATCHPART: " + iMatchPartId);
            }
            return ret;
        }

        public static Integer toInteger(MatchPartId id) {
            if (id == null) return null;
            return id.value;
        }
    }

    public Matchdetails.eInjuryType getM_eInjuryType() {
        return m_eInjuryType;
    }

    public void setM_eInjuryType(Matchdetails.eInjuryType m_eInjuryType) {
        this.m_eInjuryType = m_eInjuryType;
    }

    public void setM_eInjuryType(Integer i_InjuryType) {
        this.m_eInjuryType = Matchdetails.eInjuryType.fromInteger(i_InjuryType);
    }

    public Matchdetails.eInjuryType m_eInjuryType;

    /**
     * Creates a new instance of MatchHighlight
     */
    public MatchEvent() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    public boolean isBruisedOrInjured() {
        return (isBruised() || isInjured());
    }

    public boolean isBruised() {
        return m_eInjuryType == Matchdetails.eInjuryType.BRUISE;
    }

    public boolean isInjured() {
        return m_eInjuryType == Matchdetails.eInjuryType.INJURY;
    }

    public boolean isBooked() {
        return (isYellowCard() || isRedCard());
    }

    public boolean isPenaltyContestGoalEvent() {
        return ((this.m_matchEventID == MatchEventID.PENALTY_CONTEST_GOAL_BY_TECHNICAL_NO_NERVES) || (this.m_matchEventID == MatchEventID.PENALTY_CONTEST_GOAL_NO_NERVES) ||
                (this.m_matchEventID == MatchEventID.PENALTY_CONTEST_GOAL_IN_SPITE_OF_NERVES));
    }

    public boolean isPenaltyContestNoGoalEvent() {
        return ((this.m_matchEventID == MatchEventID.PENALTY_CONTEST_NO_GOAL_BECAUSE_OF_NERVES) || (this.m_matchEventID == MatchEventID.PENALTY_CONTEST_NO_GOAL_IN_SPITE_OF_NO_NERVES));
    }

    public boolean isChangeOfTactic() {
        return ((this.m_matchEventID == MatchEventID.CHANGE_OF_TACTIC_TEAM_IS_BEHIND) || (this.m_matchEventID == MatchEventID.CHANGE_OF_TACTIC_TEAM_IS_AHEAD) || (this.m_matchEventID == MatchEventID.CHANGE_OF_TACTIC_MINUTE));
    }

    public boolean isGoalEvent() {
        return isGoalEvent(m_iMatchEventID);
    }

    public static boolean isGoalEvent(int iMatchEventID) {
        return ((iMatchEventID >= 100) && (iMatchEventID < 200));
    }

    public boolean isNonGoalEvent() {
        return ((this.m_iMatchEventID >= 200) && (this.m_iMatchEventID < 300));
    }

    public boolean isNeutralEvent() {
        int id = this.m_iMatchEventID;
        return ((id == 23) || (id == 24) || (id == 25) || (id == 27) ||
                ((id >= 30) && (id <= 33)) || (id == 35) ||
                (id == 68) || (id == 75) ||
                (id == 451) || (id == 454) || (id == 456) || (id == 457) ||
                (id == 458) || (id == 464) || (id == 465) || (id == 466) ||
                (id == 468) || (id == 469));
    }

    public boolean isSubstitution() {
        return ((this.m_matchEventID == MatchEventID.PLAYER_SUBSTITUTION_TEAM_IS_BEHIND) || (this.m_matchEventID == MatchEventID.PLAYER_SUBSTITUTION_TEAM_IS_AHEAD) ||
                (this.m_matchEventID == MatchEventID.PLAYER_SUBSTITUTION_MINUTE) || (this.m_matchEventID == MatchEventID.INJURED_PLAYER_REPLACED));
    }

    /**
     * Setter for property m_sEventText.
     *
     * @param m_sEventText New value of property m_sEventText.
     */
    public final void setEventText(String m_sEventText) {
        this.m_sEventText = m_sEventText;
    }

    public final int getMatchId() {
        return matchId;
    }

    public final void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    /**
     * Getter for property m_sEventText.
     *
     * @return Value of property m_sEventText.
     */
    public final String getEventText() {
        return m_sEventText;
    }

    /**
     * Setter for property m_sGehilfeHeim.
     *
     * @param m_sGehilfeHeim New value of property m_sGehilfeHeim.
     */
    public final void setGehilfeHeim(boolean m_sGehilfeHeim) {
        this.m_sGehilfeHeim = m_sGehilfeHeim;
    }

    /**
     * Getter for property m_sGehilfeHeim.
     *
     * @return Value of property m_sGehilfeHeim.
     */
    public final boolean getGehilfeHeim() {
        return m_sGehilfeHeim;
    }

    /**
     * Setter for property m_iGehilfeID.
     *
     * @param m_iGehilfeID New value of property m_iGehilfeID.
     */
    public final void setAssistingPlayerId(int m_iGehilfeID) {
        this.m_iGehilfeID = m_iGehilfeID;
    }

    /**
     * Getter for property m_iGehilfeID.
     *
     * @return Value of property m_iGehilfeID.
     */
    public final int getAssistingPlayerId() {
        return m_iGehilfeID;
    }

    /**
     * Setter for property m_sGehilfeName.
     *
     * @param m_sGehilfeName New value of property m_sGehilfeName.
     */
    public final void setAssistingPlayerName(String m_sGehilfeName) {
        this.m_sGehilfeName = m_sGehilfeName;
    }

    /**
     * Getter for property m_sGehilfeName.
     *
     * @return Value of property m_sGehilfeName.
     */
    public final String getAssistingPlayerName() {
        return m_sGehilfeName;
    }

    public final void setMatchEventID(int m_iMatchEventID) {
        this.m_iMatchEventID = m_iMatchEventID;
        this.m_matchEventID = MatchEventID.fromMatchEventID(m_iMatchEventID);
    }

    public final int getiMatchEventID() {
        return m_iMatchEventID;
    }

    public final MatchEventID getMatchEventID() {
        return this.m_matchEventID;
    }

    /**
     * Setter for property m_iMinute.
     *
     * @param m_iMinute New value of property m_iMinute.
     */
    public final void setMinute(int m_iMinute) {
        this.m_iMinute = m_iMinute;
    }

    /**
     * Getter for property m_iMinute.
     *
     * @return Value of property m_iMinute.
     */
    public final int getMinute() {
        return m_iMinute;
    }

    /**
     * Setter for property m_sSpielerHeim.
     *
     * @param m_sSpielerHeim New value of property m_sSpielerHeim.
     */
    public final void setSpielerHeim(boolean m_sSpielerHeim) {
        this.m_sSpielerHeim = m_sSpielerHeim;
    }

    /**
     * Getter for property m_sSpielerHeim.
     *
     * @return Value of property m_sSpielerHeim.
     */
    public final boolean getSpielerHeim() {
        return m_sSpielerHeim;
    }

    /**
     * Setter for property m_iSpielerID.
     *
     * @param m_iSpielerID New value of property m_iSpielerID.
     */
    public final void setPlayerId(int m_iSpielerID) {
        this.m_iSpielerID = m_iSpielerID;
    }

    /**
     * Getter for property m_iSpielerID.
     *
     * @return Value of property m_iSpielerID.
     */
    public final int getPlayerId() {
        return m_iSpielerID;
    }

    /**
     * Setter for property m_sSpielerName.
     *
     * @param m_sSpielerName New value of property m_sSpielerName.
     */
    public final void setPlayerName(String m_sSpielerName) {
        this.m_sSpielerName = m_sSpielerName;
    }

    /**
     * Getter for property m_sSpielerName.
     *
     * @return Value of property m_sSpielerName.
     */
    public final String getPlayerName() {
        return m_sSpielerName;
    }

    /**
     * Setter for property m_iTeamID.
     *
     * @param m_iTeamID New value of property m_iTeamID.
     */
    public final void setTeamID(int m_iTeamID) {
        this.m_iTeamID = m_iTeamID;
    }

    /**
     * Getter for property m_iTeamID.
     *
     * @return Value of property m_iTeamID.
     */
    public final int getTeamID() {
        return m_iTeamID;
    }


    public boolean isYellowCard() {
        return yellowCardME.contains(this.m_matchEventID);
    }

    public static List<MatchEventID> yellowCardME = Arrays.asList(
            MatchEventID.YELLOW_CARD_NASTY_PLAY,                // #510
            MatchEventID.YELLOW_CARD_CHEATING);              // #511

    public boolean isRedCard() {
        return redCardME.contains(this.m_matchEventID);
    }

    public static List<MatchEventID> redCardME = Arrays.asList(
            MatchEventID.RED_CARD_2ND_WARNING_NASTY_PLAY,          // #512
            MatchEventID.RED_CARD_2ND_WARNING_CHEATING,            // #513
            MatchEventID.RED_CARD_WITHOUT_WARNING);              // #514


    public static List<MatchEventID> specialME = Arrays.asList(
            MatchEventID.SE_GOAL_UNPREDICTABLE_LONG_PASS,                                         // #105
            MatchEventID.SE_GOAL_UNPREDICTABLE_SCORES_ON_HIS_OWN,                                 // #106
            MatchEventID.SE_GOAL_UNPREDICTABLE_SPECIAL_ACTION,                                    // #108
            MatchEventID.SE_GOAL_UNPREDICTABLE_MISTAKE,                                           // #109
            MatchEventID.SE_QUICK_SCORES_AFTER_RUSH,                                              // #115
            MatchEventID.SE_QUICK_RUSHES_PASSES_AND_RECEIVER_SCORES,                              // #116
            MatchEventID.SE_TIRED_DEFENDER_MISTAKE_STRIKER_SCORES,                                // #117
            MatchEventID.SE_GOAL_CORNER_TO_ANYONE,                                                // #118
            MatchEventID.SE_GOAL_CORNER_HEAD_SPECIALIST,                                          // #119
            MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL,                                          // #125
            MatchEventID.SE_EXPERIENCED_FORWARD_SCORES,                                           // #135
            MatchEventID.SE_INEXPERIENCED_DEFENDER_CAUSES_GOAL,                                   // #136
            MatchEventID.SE_WINGER_TO_HEAD_SPEC_SCORES,                                           // #137
            MatchEventID.SE_WINGER_TO_ANYONE_SCORES,                                              // #138
            MatchEventID.SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER,                                    // #139
            MatchEventID.SE_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE,                  // #190
            MatchEventID.SE_NO_GOAL_UNPREDICTABLE_LONG_PASS,                                      // #205
            MatchEventID.SE_NO_GOAL_UNPREDICTABLE_ALMOST_SCORES,                                  // #206
            MatchEventID.SE_NO_GOAL_UNPREDICTABLE_SPECIAL_ACTION,                                 // #208
            MatchEventID.SE_NO_GOAL_UNPREDICTABLE_MISTAKE,                                        // #209
            MatchEventID.SE_SPEEDY_MISSES_AFTER_RUSH,                                             // #215
            MatchEventID.SE_QUICK_RUSHES_PASSES_BUT_RECEIVER_FAILS,                               // #216
            MatchEventID.SE_TIRED_DEFENDER_MISTAKE_BUT_NO_GOAL,                                   // #217
            MatchEventID.SE_NO_GOAL_CORNER_TO_ANYONE,                                             // #218
            MatchEventID.SE_NO_GOAL_CORNER_HEAD_SPECIALIST,                                       // #219
            MatchEventID.SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST,                                // #225
            MatchEventID.SE_EXPERIENCED_FORWARD_FAILS_TO_SCORE,                                   // #235
            MatchEventID.SE_INEXPERIENCED_DEFENDER_ALMOST_CAUSES_GOAL,                            // #236
            MatchEventID.SE_WINGER_TO_SOMEONE_NO_GOAL,                                            // #237
            MatchEventID.SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER_NO_GOAL,                            // #239
            MatchEventID.SE_QUICK_RUSHES_STOPPED_BY_QUICK_DEFENDER,                               // #289
            MatchEventID.SE_NO_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE);            // #290

    public boolean isIFK() {
        return IFKME.contains(this.m_matchEventID);
    }

    public static List<MatchEventID> IFKME = Arrays.asList(
            MatchEventID.GOAL_INDIRECT_FREE_KICK,                // #185
            MatchEventID.NO_GOAL_INDIRECT_FREE_KICK);          // #285


    public boolean isLS() {
        return LSME.contains(this.m_matchEventID);
    }

    public static List<MatchEventID> LSME = Arrays.asList(
            MatchEventID.GOAL_LONG_SHOT_NO_TACTIC,                // #107
            MatchEventID.GOAL_LONG_SHOT,                          // #187
            MatchEventID.NO_GOAL_LONG_SHOT_NO_TACTIC,            // #207
            MatchEventID.NO_GOAL_LONG_SHOT_NO_TACTIC);          // #287)

    /**
     * Check, if it is a Counter Attack event
     */
    public boolean isCounterAttack() {
        return CounterAttackME.contains(this.m_matchEventID);
    }

    public static List<MatchEventID> CounterAttackME = Arrays.asList(
            MatchEventID.COUNTER_ATTACK_GOAL_FREE_KICK,                 // #140
            MatchEventID.COUNTER_ATTACK_GOAL_MIDDLE,                    // #141
            MatchEventID.COUNTER_ATTACK_GOAL_LEFT,                      // #142
            MatchEventID.COUNTER_ATTACK_GOAL_RIGHT,                     // #143
            MatchEventID.COUNTER_ATTACK_GOAL_INDIRECT_FREE_KICK,        // #186
            MatchEventID.COUNTER_ATTACK_NO_GOAL_FREE_KICK,              // #240
            MatchEventID.COUNTER_ATTACK_NO_GOAL_MIDDLE,                 // #241
            MatchEventID.COUNTER_ATTACK_NO_GOAL_LEFT,                   // #242
            MatchEventID.COUNTER_ATTACK_NO_GOAL_RIGHT,                  // #243
            MatchEventID.COUNTER_ATTACK_NO_GOAL_INDIRECT_FREE_KICK);    // #286


    public static List<MatchEventID> CentralAttackME = Arrays.asList(
            MatchEventID.REDUCING_GOAL_HOME_TEAM_MIDDLE,                // #101
            MatchEventID.EQUALIZER_GOAL_HOME_TEAM_MIDDLE,               // #111
            MatchEventID.GOAL_TO_TAKE_LEAD_HOME_TEAM_MIDDLE,            // #121
            MatchEventID.INCREASE_GOAL_HOME_TEAM_MIDDLE,                // #131
            MatchEventID.REDUCING_GOAL_AWAY_TEAM_MIDDLE,                // #151
            MatchEventID.EQUALIZER_GOAL_AWAY_TEAM_MIDDLE,               // #161
            MatchEventID.GOAL_TO_TAKE_LEAD_AWAY_TEAM_MIDDLE,            // #171
            MatchEventID.INCREASE_GOAL_AWAY_TEAM_MIDDLE,                // #181
            MatchEventID.NO_REDUCING_GOAL_HOME_TEAM_MIDDLE,             // #201
            MatchEventID.NO_EQUALIZER_GOAL_HOME_TEAM_MIDDLE,            // #211
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_MIDDLE,         // #221
            MatchEventID.NO_INCREASE_GOAL_HOME_TEAM_MIDDLE,             // #231
            MatchEventID.NO_REDUCING_GOAL_AWAY_TEAM_MIDDLE,             // #251
            MatchEventID.NO_EQUALIZER_GOAL_AWAY_TEAM_MIDDLE,            // #261
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_MIDDLE,         // #271
            MatchEventID.NO_INCREASE_GOAL_AWAY_TEAM_MIDDLE);           // #281)


    public static List<MatchEventID> RightAttackME = Arrays.asList(
            MatchEventID.REDUCING_GOAL_HOME_TEAM_RIGHT_WING,                // #103
            MatchEventID.EQUALIZER_GOAL_HOME_TEAM_RIGHT_WING,               // #113
            MatchEventID.GOAL_TO_TAKE_LEAD_HOME_TEAM_RIGHT_WING,            // #123
            MatchEventID.INCREASE_GOAL_HOME_TEAM_RIGHT_WING,                // #133
            MatchEventID.REDUCING_GOAL_AWAY_TEAM_RIGHT_WING,                // #153
            MatchEventID.EQUALIZER_GOAL_AWAY_TEAM_RIGHT_WING,               // #163
            MatchEventID.GOAL_TO_TAKE_LEAD_AWAY_TEAM_RIGHT_WING,            // #173
            MatchEventID.INCREASE_GOAL_AWAY_TEAM_RIGHT_WING,                // #183
            MatchEventID.NO_REDUCING_GOAL_HOME_TEAM_RIGHT_WING,             // #203
            MatchEventID.NO_EQUALIZER_GOAL_HOME_TEAM_RIGHT_WING,            // #213
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_RIGHT_WING,         // #223
            MatchEventID.NO_INCREASE_GOAL_HOME_TEAM_RIGHT_WING,             // #233
            MatchEventID.NO_REDUCING_GOAL_AWAY_TEAM_RIGHT_WING,             // #253
            MatchEventID.NO_EQUALIZER_GOAL_AWAY_TEAM_RIGHT_WING,            // #263
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_RIGHT_WING,         // #273
            MatchEventID.NO_INCREASE_GOAL_AWAY_TEAM_RIGHT_WING);           // #283)

    public static List<MatchEventID> leftAttackME = Arrays.asList(
            MatchEventID.REDUCING_GOAL_HOME_TEAM_LEFT_WING,                // #102
            MatchEventID.EQUALIZER_GOAL_HOME_TEAM_LEFT_WING,               // #112
            MatchEventID.GOAL_TO_TAKE_LEAD_HOME_TEAM_LEFT_WING,            // #122
            MatchEventID.INCREASE_GOAL_HOME_TEAM_LEFT_WING,                // #132
            MatchEventID.REDUCING_GOAL_AWAY_TEAM_LEFT_WING,                // #152
            MatchEventID.EQUALIZER_GOAL_AWAY_TEAM_LEFT_WING,               // #162
            MatchEventID.GOAL_TO_TAKE_LEAD_AWAY_TEAM_LEFT_WING,            // #172
            MatchEventID.INCREASE_GOAL_AWAY_TEAM_LEFT_WING,                // #182
            MatchEventID.NO_REDUCING_GOAL_HOME_TEAM_LEFT_WING,             // #202
            MatchEventID.NO_EQUALIZER_GOAL_HOME_TEAM_LEFT_WING,            // #212
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_LEFT_WING,         // #222
            MatchEventID.NO_INCREASE_GOAL_HOME_TEAM_LEFT_WING,             // #232
            MatchEventID.NO_REDUCING_GOAL_AWAY_TEAM_LEFT_WING,             // #252
            MatchEventID.NO_EQUALIZER_GOAL_AWAY_TEAM_LEFT_WING,            // #262
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_LEFT_WING,         // #272
            MatchEventID.NO_INCREASE_GOAL_AWAY_TEAM_LEFT_WING);           // #282)

    private static final List<Integer> manMarkingMatchEventTypes = IntStream.range(
            MatchEventID.MAN_MARKING_SUCCESS_SHORT_DISTANCE.value,
            MatchEventID.MAN_MARKER_PENALTY_NO_MAN_MARKED_IN_OPPONENT_TEAM.value).boxed().toList();

    /**
     * Check, if it is a man marking  event
     */
    public boolean isManMarking() {
        return manMarkingMatchEventTypes.contains(m_matchEventID.value);
    }


    /**
     * Check, if it is a free kick event
     */
    public boolean isFreeKick() {
        return freekickME.contains(this.m_matchEventID);
    }

    public static List<MatchEventID> freekickME = Arrays.asList(
            MatchEventID.REDUCING_GOAL_HOME_TEAM_FREE_KICK,                // #100
            MatchEventID.EQUALIZER_GOAL_HOME_TEAM_FREE_KICK,               // #110
            MatchEventID.GOAL_TO_TAKE_LEAD_HOME_TEAM_FREE_KICK,            // #120
            MatchEventID.INCREASE_GOAL_HOME_TEAM_FREE_KICK,                // #130
            MatchEventID.REDUCING_GOAL_AWAY_TEAM_FREE_KICK,                // #150
            MatchEventID.EQUALIZER_GOAL_AWAY_TEAM_FREE_KICK,               // #160
            MatchEventID.GOAL_TO_TAKE_LEAD_AWAY_TEAM_FREE_KICK,            // #170
            MatchEventID.INCREASE_GOAL_AWAY_TEAM_FREE_KICK,                // #180
            MatchEventID.NO_REDUCING_GOAL_HOME_TEAM_FREE_KICK,             // #200
            MatchEventID.NO_EQUALIZER_GOAL_HOME_TEAM_FREE_KICK,            // #210
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_FREE_KICK,         // #220
            MatchEventID.NO_INCREASE_GOAL_HOME_TEAM_FREE_KICK,             // #230
            MatchEventID.NO_REDUCING_GOAL_AWAY_TEAM_FREE_KICK,             // #250
            MatchEventID.NO_EQUALIZER_GOAL_AWAY_TEAM_FREE_KICK,            // #260
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_FREE_KICK,         // #270
            MatchEventID.NO_INCREASE_GOAL_AWAY_TEAM_FREE_KICK);           // #280)

    /**
     * Check, if it is a penalty event
     */
    public boolean isPenalty() {
        return penaltyME.contains(this.m_matchEventID);
    }

    public static List<MatchEventID> penaltyME = Arrays.asList(
            MatchEventID.REDUCING_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,                // #104
            MatchEventID.EQUALIZER_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,               // #114
            MatchEventID.GOAL_TO_TAKE_LEAD_HOME_TEAM_PENALTY_KICK_NORMAL,            // #124
            MatchEventID.INCREASE_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,                // #134
            MatchEventID.REDUCING_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,                // #154
            MatchEventID.EQUALIZER_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,               // #164
            MatchEventID.GOAL_TO_TAKE_LEAD_AWAY_TEAM_PENALTY_KICK_NORMAL,            // #174
            MatchEventID.INCREASE_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,                // #184
            MatchEventID.NO_REDUCING_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,             // #204
            MatchEventID.NO_EQUALIZER_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,            // #214
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_PENALTY_KICK_NORMAL,         // #224
            MatchEventID.NO_INCREASE_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,             // #234
            MatchEventID.NO_REDUCING_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,             // #254
            MatchEventID.NO_EQUALIZER_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,            // #264
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_PENALTY_KICK_NORMAL,         // #274
            MatchEventID.NO_INCREASE_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL);           // #284)

    /**
     * Check, if it is a long shot event
     */
    public boolean isLongShot() {
        return
                (this.m_matchEventID == MatchEventID.GOAL_LONG_SHOT_NO_TACTIC ||     // #107
                        this.m_matchEventID == MatchEventID.GOAL_LONG_SHOT ||                 // #187
                        this.m_matchEventID == MatchEventID.NO_GOAL_LONG_SHOT_NO_TACTIC ||    // #207
                        this.m_matchEventID == MatchEventID.NO_GOAL_LONG_SHOT ||              // #287
                        this.m_matchEventID == MatchEventID.NO_GOAL_LONG_SHOT_DEFENDED);     // #288
    }


    /**
     * Check, if it is a Special Event
     */
    public boolean isSE() {
        return this.m_matchEventID.name().startsWith("SE_");
    }

    /**
     * Check, if it is a SE not related to specialty (SetPieces, stamina and XP)
     */
    public boolean isOtherSE() {
        return
                (this.m_matchEventID == MatchEventID.SE_TIRED_DEFENDER_MISTAKE_STRIKER_SCORES ||
                        this.m_matchEventID == MatchEventID.SE_TIRED_DEFENDER_MISTAKE_BUT_NO_GOAL ||
                        this.m_matchEventID == MatchEventID.SE_GOAL_CORNER_TO_ANYONE ||
                        this.m_matchEventID == MatchEventID.SE_NO_GOAL_CORNER_TO_ANYONE ||
                        this.m_matchEventID == MatchEventID.SE_EXPERIENCED_FORWARD_SCORES ||
                        this.m_matchEventID == MatchEventID.SE_EXPERIENCED_FORWARD_FAILS_TO_SCORE ||
                        this.m_matchEventID == MatchEventID.SE_INEXPERIENCED_DEFENDER_CAUSES_GOAL ||
                        this.m_matchEventID == MatchEventID.SE_INEXPERIENCED_DEFENDER_ALMOST_CAUSES_GOAL);
    }

    /**
     * Check, if it is a Specialty Special Event, i.e SE but not weather
     */
    public boolean isSpecialtyNonWeatherSE() {
        return (this.isSE() && (!this.isSpecialtyWeatherSE()));
    }


    /**
     * Check, if it is a SE related both to player specialty and weather
     */
    public boolean isSpecialtyWeatherSE() {
        return
                (this.m_matchEventID == MatchEventID.SE_TECHNICAL_SUFFERS_FROM_RAIN ||
                        this.m_matchEventID == MatchEventID.SE_POWERFUL_THRIVES_IN_RAIN ||
                        this.m_matchEventID == MatchEventID.SE_TECHNICAL_THRIVES_IN_SUN ||
                        this.m_matchEventID == MatchEventID.SE_POWERFUL_SUFFERS_FROM_SUN ||
                        this.m_matchEventID == MatchEventID.SE_QUICK_LOSES_IN_RAIN ||
                        this.m_matchEventID == MatchEventID.SE_QUICK_LOSES_IN_SUN ||
                        this.m_matchEventID == MatchEventID.RAINY_WEATHER_MANY_PLAYERS_AFFECTED ||
                        this.m_matchEventID == MatchEventID.SUNNY_WEATHER_MANY_PLAYERS_AFFECTED);
    }

    public String getEventTextDescription() {
        return getEventTextDescription(m_matchEventID.value);
    }

    public static String getEventTextDescription(int iMatchEventID) {
        return iMatchEventID + ": " + HOVerwaltung.instance().getLanguageString("MatchEvent_" + iMatchEventID);
    }

    public List<Icon> getIcons() {
        var ret = new ArrayList<Icon>();
        var id = getMatchEventID();
        if (id != null) {
            if (isBruised()) {
                ret.add(ImageUtilities.getSmallPlasterIcon());
            } else if (isInjured()) {
                ret.add(ImageUtilities.getSmallInjuryIcon());
            } else {
                switch (id) {
                    case TACTICAL_DISPOSITION, PLAYER_NAMES_IN_LINEUP -> ret.add(getIcon(HOIconName.FORMATION));
                    case SPECTATORS_OR_VENUE_RAIN, SPECTATORS_OR_VENUE_CLOUDY, SPECTATORS_OR_VENUE_FAIR_WEATHER, SPECTATORS_OR_VENUE_SUNNY ->
                            ret.add(getIcon(HOIconName.WEATHER[id.getValue() - SPECTATORS_OR_VENUE_RAIN.getValue()]));
                    case ONLY_VENUE_RAIN, ONLY_VENUE_CLOUDY, ONLY_VENUE_FAIR_WEATHER, ONLY_VENUE_SUNNY ->
                            ret.add(getIcon(HOIconName.WEATHER[id.getValue() - MatchEventID.ONLY_VENUE_RAIN.getValue()]));
                    case PENALTY_CONTEST_GOAL_BY_TECHNICAL_NO_NERVES -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getSpecialtyIcon(Specialty.Technical));
                    }
                    case PENALTY_CONTEST_GOAL_NO_NERVES, PENALTY_CONTEST_GOAL_IN_SPITE_OF_NERVES ->
                            ret.add(getIcon(HOIconName.GOAL));
                    case PENALTY_CONTEST_NO_GOAL_BECAUSE_OF_NERVES, PENALTY_CONTEST_NO_GOAL_IN_SPITE_OF_NO_NERVES ->
                            ret.add(getIcon(HOIconName.MISS));
                    case ORGANIZATION_BREAKS -> ret.add(getIcon(HOIconName.CONFUSION));
                    case REORGANIZE -> ret.add(getIcon(HOIconName.REORGANIZE));
                    case SUCCESSFUL_PRESSING -> ret.add(getIcon(HOIconName.TACTIC_PRESSING));
                    case NEW_CAPTAIN -> ret.add(getIcon(HOIconName.CAPTAIN, HOColorName.PLAYER_SPECIALTY_COLOR));
                    case NEW_SET_PIECES_TAKER -> ret.add(getIcon(HOIconName.PIECES));
                    case REDUCING_GOAL_HOME_TEAM_FREE_KICK,
                            EQUALIZER_GOAL_HOME_TEAM_FREE_KICK,
                            GOAL_TO_TAKE_LEAD_HOME_TEAM_FREE_KICK,
                            INCREASE_GOAL_HOME_TEAM_FREE_KICK,
                            REDUCING_GOAL_AWAY_TEAM_FREE_KICK,
                            EQUALIZER_GOAL_AWAY_TEAM_FREE_KICK,
                            GOAL_TO_TAKE_LEAD_AWAY_TEAM_FREE_KICK,
                            INCREASE_GOAL_AWAY_TEAM_FREE_KICK,
                            GOAL_INDIRECT_FREE_KICK -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getIcon(HOIconName.WHISTLE));
                    }
                    case GOAL_TO_TAKE_LEAD_HOME_TEAM_PENALTY_KICK_NORMAL,
                            INCREASE_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
                            REDUCING_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,
                            REDUCING_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
                            EQUALIZER_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
                            EQUALIZER_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,
                            GOAL_TO_TAKE_LEAD_AWAY_TEAM_PENALTY_KICK_NORMAL,
                            INCREASE_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getIcon(HOIconName.PENALTY));
                    }
                    case REDUCING_GOAL_HOME_TEAM_MIDDLE,
                            EQUALIZER_GOAL_HOME_TEAM_MIDDLE,
                            GOAL_TO_TAKE_LEAD_HOME_TEAM_MIDDLE,
                            INCREASE_GOAL_HOME_TEAM_MIDDLE,
                            REDUCING_GOAL_AWAY_TEAM_MIDDLE,
                            EQUALIZER_GOAL_AWAY_TEAM_MIDDLE,
                            GOAL_TO_TAKE_LEAD_AWAY_TEAM_MIDDLE,
                            INCREASE_GOAL_AWAY_TEAM_MIDDLE -> ret.add(getIcon(HOIconName.GOAL_MID));
                    case REDUCING_GOAL_HOME_TEAM_LEFT_WING,
                            EQUALIZER_GOAL_HOME_TEAM_LEFT_WING,
                            GOAL_TO_TAKE_LEAD_HOME_TEAM_LEFT_WING,
                            INCREASE_GOAL_HOME_TEAM_LEFT_WING,
                            REDUCING_GOAL_AWAY_TEAM_LEFT_WING,
                            EQUALIZER_GOAL_AWAY_TEAM_LEFT_WING,
                            GOAL_TO_TAKE_LEAD_AWAY_TEAM_LEFT_WING,
                            INCREASE_GOAL_AWAY_TEAM_LEFT_WING -> ret.add(getIcon(HOIconName.GOAL_LEFT));
                    case REDUCING_GOAL_HOME_TEAM_RIGHT_WING,
                            EQUALIZER_GOAL_HOME_TEAM_RIGHT_WING,
                            GOAL_TO_TAKE_LEAD_HOME_TEAM_RIGHT_WING,
                            INCREASE_GOAL_HOME_TEAM_RIGHT_WING,
                            REDUCING_GOAL_AWAY_TEAM_RIGHT_WING,
                            EQUALIZER_GOAL_AWAY_TEAM_RIGHT_WING,
                            GOAL_TO_TAKE_LEAD_AWAY_TEAM_RIGHT_WING,
                            INCREASE_GOAL_AWAY_TEAM_RIGHT_WING -> ret.add(getIcon(HOIconName.GOAL_RIGHT));
                    case SE_GOAL_UNPREDICTABLE_LONG_PASS, SE_GOAL_UNPREDICTABLE_SCORES_ON_HIS_OWN, SE_GOAL_UNPREDICTABLE_SPECIAL_ACTION -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getSpecialtyIcon(Specialty.Unpredictable));
                    }
                    case SE_GOAL_UNPREDICTABLE_MISTAKE -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getSpecialtyFaultIcon(Specialty.Unpredictable));
                    }
                    case GOAL_LONG_SHOT_NO_TACTIC, GOAL_LONG_SHOT -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getIcon(HOIconName.TACTIC_LONG_SHOTS));
                    }
                    case SE_QUICK_SCORES_AFTER_RUSH, SE_QUICK_RUSHES_PASSES_AND_RECEIVER_SCORES -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getSpecialtyIcon(Specialty.Quick));
                    }
                    case SE_TIRED_DEFENDER_MISTAKE_STRIKER_SCORES -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getIcon(HOIconName.TIRED));
                    }
                    case SE_GOAL_CORNER_TO_ANYONE -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getIcon(HOIconName.CORNER));
                    }
                    case SE_GOAL_CORNER_HEAD_SPECIALIST -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getSpecialtyIcon(Specialty.Head));
                    }
                    case SE_EXPERIENCED_FORWARD_SCORES,
                            SE_INEXPERIENCED_DEFENDER_CAUSES_GOAL -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getIcon(HOIconName.EXPERIENCE));
                    }
                    case SE_WINGER_TO_HEAD_SPEC_SCORES -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getIcon(HOIconName.WINGER, HOColorName.PLAYER_SPECIALTY_COLOR));
                        ret.add(getSpecialtyIcon(Specialty.Head));
                    }
                    case SE_WINGER_TO_ANYONE_SCORES -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getIcon(HOIconName.WINGER, HOColorName.PLAYER_SPECIALTY_COLOR));
                    }
                    case SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getSpecialtyIcon(Specialty.Technical));
                        ret.add(getSpecialtyFaultIcon(Specialty.Head));
                    }
                    case COUNTER_ATTACK_GOAL_FREE_KICK,
                            COUNTER_ATTACK_GOAL_INDIRECT_FREE_KICK -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getIcon(HOIconName.TACTIC_COUNTER_ATTACKING));
                        ret.add(getIcon(HOIconName.WHISTLE));
                    }
                    case COUNTER_ATTACK_GOAL_MIDDLE -> {
                        ret.add(getIcon(HOIconName.GOAL_MID));
                        ret.add(getIcon(HOIconName.TACTIC_COUNTER_ATTACKING));
                    }
                    case COUNTER_ATTACK_GOAL_LEFT -> {
                        ret.add(getIcon(HOIconName.GOAL_LEFT));
                        ret.add(getIcon(HOIconName.TACTIC_COUNTER_ATTACKING));
                    }
                    case COUNTER_ATTACK_GOAL_RIGHT -> {
                        ret.add(getIcon(HOIconName.GOAL_RIGHT));
                        ret.add(getIcon(HOIconName.TACTIC_COUNTER_ATTACKING));
                    }
                    case SE_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE -> {
                        ret.add(getIcon(HOIconName.GOAL));
                        ret.add(getSpecialtyIcon(Specialty.Powerful));
                    }
                    case NO_REDUCING_GOAL_HOME_TEAM_FREE_KICK,
                            NO_EQUALIZER_GOAL_HOME_TEAM_FREE_KICK,
                            NO_INCREASE_GOAL_HOME_TEAM_FREE_KICK,
                            NO_REDUCING_GOAL_AWAY_TEAM_FREE_KICK,
                            NO_EQUALIZER_GOAL_AWAY_TEAM_FREE_KICK,
                            NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_FREE_KICK,
                            NO_INCREASE_GOAL_AWAY_TEAM_FREE_KICK,
                            NO_GOAL_INDIRECT_FREE_KICK,
                            NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_FREE_KICK -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getIcon(HOIconName.WHISTLE));
                    }
                    case NO_REDUCING_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
                            NO_EQUALIZER_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
                            NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_PENALTY_KICK_NORMAL,
                            NO_INCREASE_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
                            NO_REDUCING_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,
                            NO_EQUALIZER_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,
                            NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_PENALTY_KICK_NORMAL,
                            NO_INCREASE_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getIcon(HOIconName.PENALTY));
                    }
                    case NO_REDUCING_GOAL_HOME_TEAM_MIDDLE,
                            NO_EQUALIZER_GOAL_HOME_TEAM_MIDDLE,
                            NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_MIDDLE,
                            NO_INCREASE_GOAL_HOME_TEAM_MIDDLE,
                            NO_REDUCING_GOAL_AWAY_TEAM_MIDDLE,
                            NO_EQUALIZER_GOAL_AWAY_TEAM_MIDDLE,
                            NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_MIDDLE,
                            NO_INCREASE_GOAL_AWAY_TEAM_MIDDLE -> ret.add(getIcon(HOIconName.NO_GOAL_MID));
                    case NO_REDUCING_GOAL_HOME_TEAM_LEFT_WING,
                            NO_EQUALIZER_GOAL_HOME_TEAM_LEFT_WING,
                            NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_LEFT_WING,
                            NO_INCREASE_GOAL_HOME_TEAM_LEFT_WING,
                            NO_REDUCING_GOAL_AWAY_TEAM_LEFT_WING,
                            NO_EQUALIZER_GOAL_AWAY_TEAM_LEFT_WING,
                            NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_LEFT_WING,
                            NO_INCREASE_GOAL_AWAY_TEAM_LEFT_WING -> ret.add(getIcon(HOIconName.NO_GOAL_LEFT));
                    case NO_REDUCING_GOAL_HOME_TEAM_RIGHT_WING,
                            NO_EQUALIZER_GOAL_HOME_TEAM_RIGHT_WING,
                            NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_RIGHT_WING,
                            NO_INCREASE_GOAL_HOME_TEAM_RIGHT_WING,
                            NO_REDUCING_GOAL_AWAY_TEAM_RIGHT_WING,
                            NO_EQUALIZER_GOAL_AWAY_TEAM_RIGHT_WING,
                            NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_RIGHT_WING,
                            NO_INCREASE_GOAL_AWAY_TEAM_RIGHT_WING -> ret.add(getIcon(HOIconName.NO_GOAL_RIGHT));
                    case SE_NO_GOAL_UNPREDICTABLE_LONG_PASS,
                            SE_NO_GOAL_UNPREDICTABLE_ALMOST_SCORES,
                            SE_NO_GOAL_UNPREDICTABLE_SPECIAL_ACTION -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getSpecialtyIcon(Specialty.Unpredictable));
                    }
                    case NO_GOAL_LONG_SHOT_NO_TACTIC,
                            NO_GOAL_LONG_SHOT -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getIcon(HOIconName.TACTIC_LONG_SHOTS));
                    }
                    case SE_NO_GOAL_UNPREDICTABLE_MISTAKE -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getSpecialtyFaultIcon(Specialty.Unpredictable));
                    }
                    case SE_NO_GOAL_CORNER_HEAD_SPECIALIST -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getIcon(HOIconName.CORNER));
                        ret.add(getSpecialtyIcon(Specialty.Head));
                    }
                    case SE_SPEEDY_MISSES_AFTER_RUSH,
                            SE_QUICK_RUSHES_PASSES_BUT_RECEIVER_FAILS -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getSpecialtyIcon(Specialty.Quick));
                    }
                    case SE_WINGER_TO_SOMEONE_NO_GOAL -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getIcon(HOIconName.WINGER, HOColorName.PLAYER_SPECIALTY_COLOR));
                    }
                    case SE_TIRED_DEFENDER_MISTAKE_BUT_NO_GOAL -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getIcon(HOIconName.TIRED));
                    }
                    case SE_NO_GOAL_CORNER_TO_ANYONE -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getIcon(HOIconName.CORNER));
                    }
                    case SE_EXPERIENCED_FORWARD_FAILS_TO_SCORE,
                            SE_INEXPERIENCED_DEFENDER_ALMOST_CAUSES_GOAL -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getIcon(HOIconName.EXPERIENCE));
                    }
                    case SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER_NO_GOAL -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getSpecialtyIcon(Specialty.Technical));
                        ret.add(getSpecialtyFaultIcon(Specialty.Head));
                    }
                    case COUNTER_ATTACK_NO_GOAL_FREE_KICK,
                            COUNTER_ATTACK_NO_GOAL_INDIRECT_FREE_KICK -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getIcon(HOIconName.TACTIC_COUNTER_ATTACKING));
                        ret.add(getIcon(HOIconName.WHISTLE));
                    }
                    case COUNTER_ATTACK_NO_GOAL_MIDDLE -> {
                        ret.add(getIcon(HOIconName.NO_GOAL_MID));
                        ret.add(getIcon(HOIconName.TACTIC_COUNTER_ATTACKING));
                    }
                    case COUNTER_ATTACK_NO_GOAL_LEFT -> {
                        ret.add(getIcon(HOIconName.NO_GOAL_LEFT));
                        ret.add(getIcon(HOIconName.TACTIC_COUNTER_ATTACKING));
                    }
                    case COUNTER_ATTACK_NO_GOAL_RIGHT -> {
                        ret.add(getIcon(HOIconName.NO_GOAL_RIGHT));
                        ret.add(getIcon(HOIconName.TACTIC_COUNTER_ATTACKING));
                    }
                    case SE_QUICK_RUSHES_STOPPED_BY_QUICK_DEFENDER -> {
                        ret.add(getSpecialtyFaultIcon(Specialty.Quick));
                        ret.add(getSpecialtyIcon(Specialty.Quick));
                    }
                    case SE_NO_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE -> {
                        ret.add(getIcon(HOIconName.MISS));
                        ret.add(getSpecialtyIcon(Specialty.Powerful));
                    }
                    case SE_TECHNICAL_SUFFERS_FROM_RAIN ->
                            ret.add(getSpecialtyFaultIcon(Specialty.Technical));
                    case SE_POWERFUL_THRIVES_IN_RAIN ->
                            ret.add(getSpecialtyIcon(Specialty.Powerful));
                    case SE_TECHNICAL_THRIVES_IN_SUN ->
                            ret.add(getSpecialtyIcon(Specialty.Technical));
                    case SE_POWERFUL_SUFFERS_FROM_SUN ->
                            ret.add(getSpecialtyFaultIcon(Specialty.Powerful));
                    case SE_POWERFUL_DEFENSIVE_INNER_PRESSES_CHANCE ->
                            ret.add(getSpecialtyIcon(Specialty.Powerful));
                    case SE_QUICK_LOSES_IN_RAIN,
                            SE_QUICK_LOSES_IN_SUN ->
                            ret.add(getSpecialtyFaultIcon(Specialty.Quick));
                    case SE_SUPPORT_PLAYER_BOOST_FAILED,
                            SE_SUPPORT_PLAYER_BOOST_FAILED_AND_ORGANIZATION_DROPPED ->
                            ret.add(getSpecialtyFaultIcon(Specialty.Support));
                    case SE_SUPPORT_PLAYER_BOOST_SUCCEEDED ->
                            ret.add(getSpecialtyIcon(Specialty.Support));
                    case TACTIC_TYPE_PRESSING -> ret.add(getIcon(HOIconName.TACTIC_PRESSING));
                    case TACTIC_TYPE_COUNTER_ATTACKING -> ret.add(getIcon(HOIconName.TACTIC_COUNTER_ATTACKING));
                    case TACTIC_TYPE_ATTACK_IN_MIDDLE,
                            TACTIC_ATTACK_IN_MIDDLE_USED -> ret.add(getIcon(HOIconName.TACTIC_AIM));
                    case TACTIC_TYPE_ATTACK_ON_WINGS,
                            TACTIC_ATTACK_ON_WINGS_USED -> ret.add(getIcon(HOIconName.TACTIC_AOW));
                    case TACTIC_TYPE_PLAY_CREATIVELY -> ret.add(getIcon(HOIconName.TACTIC_PLAY_CREATIVELY));
                    case TACTIC_TYPE_LONG_SHOTS -> ret.add(getIcon(HOIconName.TACTIC_LONG_SHOTS));
                    case PLAYER_SUBSTITUTION_TEAM_IS_BEHIND,
                            PLAYER_SUBSTITUTION_TEAM_IS_AHEAD,
                            PLAYER_SUBSTITUTION_MINUTE,
                            INJURED_PLAYER_REPLACED -> ret.add(getIcon(HOIconName.REPLACEMENT));
                    case CHANGE_OF_TACTIC_TEAM_IS_BEHIND,
                            CHANGE_OF_TACTIC_TEAM_IS_AHEAD,
                            CHANGE_OF_TACTIC_MINUTE -> ret.add(getIcon(HOIconName.ROTATE));
                    case PLAYER_POSITION_SWAP_MINUTE -> ret.add(getIcon(HOIconName.SWAP));
                    case MAN_MARKING_SUCCESS_SHORT_DISTANCE,
                            MAN_MARKING_SUCCESS_LONG_DISTANCE -> ret.add(getIcon(HOIconName.ME_MAN_MARKING));
                    case YELLOW_CARD_NASTY_PLAY,
                            YELLOW_CARD_CHEATING -> ret.add(getIcon(HOIconName.YELLOWCARD));
                    case RED_CARD_2ND_WARNING_NASTY_PLAY,
                            RED_CARD_2ND_WARNING_CHEATING -> ret.add(getIcon(HOIconName.ME_YELLOW_THEN_RED));
                    case RED_CARD_WITHOUT_WARNING -> ret.add(getIcon(HOIconName.REDCARD));
                    case SE_GOAL_UNPREDICTABLE_OWN_GOAL -> {
                        // TODO: color mapping does not work
                        ret.add(getIcon(HOIconName.GOAL, HOColorName.RED));
                        ret.add(getSpecialtyFaultIcon(Specialty.Unpredictable));
                    }
                    case SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST -> {
                        // TODO: color mapping does not work
                        ret.add(getIcon(HOIconName.MISS, HOColorName.PINK));
                        ret.add(getSpecialtyFaultIcon(Specialty.Unpredictable));
                    }
                }
            }
        }
        return ret;
    }

    private Icon getSpecialtyFaultIcon(Specialty specialty) {
        return getIcon(HOIconName.SPECIALTIES[specialty.getValue()]);
    }
    private Icon getSpecialtyIcon(Specialty specialty) {
        return getIcon(HOIconName.SPECIALTIES[specialty.getValue()], HOColorName.PLAYER_SPECIALTY_COLOR);
    }

    private Icon getIcon(String key, HOColorName color) {
        Map<Object, Object> colorMap = Map.of("lineColor", ThemeManager.getColor(color));
        return ImageUtilities.getSvgIcon(key, colorMap, 15, 15);
    }

    private Icon getIcon(String key) {
        return ImageUtilities.getSvgIcon(key, 15, 15);
    }
}