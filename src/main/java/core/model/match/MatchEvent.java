package core.model.match;

import core.db.AbstractTable;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;
import core.model.enums.MatchType;
import core.model.player.Specialty;
import core.util.HODateTime;
import core.util.HOLogger;
import core.util.StreamUtils;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static core.model.match.MatchEventID.SPECTATORS_OR_VENUE_RAIN;

public class MatchEvent extends AbstractTable.Storable {

    private static final Set<MatchEventID> OWN_GOAL_GOAL_EVENTS =
        Set.of(MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL);

    private static final Set<MatchEventID> OWN_GOAL_NO_GOAL_EVENTS =
        Set.of(MatchEventID.SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST);

    private static final Set<MatchEventID> OWN_GOAL_EVENTS =
        StreamUtils.concat(OWN_GOAL_GOAL_EVENTS.stream(), OWN_GOAL_NO_GOAL_EVENTS.stream()).collect(Collectors.toSet());

    private static final Set<MatchEventID> PENALTY_CONTEXT_ADDITIONAL_EVENTS =
        Set.of(MatchEventID.PENALTY_CONTEST_AFTER_EXTENSION, MatchEventID.AFTER_22_PENALTIES_TOSSING_COIN);

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
     * Creates a new instance of MatchEvent
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

    /**
     * Identifies events that are events during a penalty contest.
     *
     * @return {@code true} when event for that case, otherwise {@code false}
     */
    public boolean isPenaltyContestEvent() {
        return isPenaltyContestEventGoalAndNoGoalEvent() || PENALTY_CONTEXT_ADDITIONAL_EVENTS.contains(this.m_matchEventID);
    }

    /**
     * Identifies events that are events during a penalty contest and can result in a goal.
     *
     * @return {@code true} when event for that case, otherwise {@code false}
     */
    public boolean isPenaltyContestEventGoalAndNoGoalEvent() {
        return isPenaltyContestGoalEvent() || isPenaltyContestNoGoalEvent();
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

    public boolean isOwnGoalEvent() {
        return isOwnGoalEvent(m_iMatchEventID);
    }

    public static boolean isOwnGoalEvent(int matchEventID) {
        return MatchEventID.findMatchEventId(matchEventID).map(OWN_GOAL_EVENTS::contains).orElse(false);
    }

    public boolean isOwnGoalGoalEvent() {
        return isOwnGoalGoalEvent(m_iMatchEventID);
    }

    public static boolean isOwnGoalGoalEvent(int matchEventID) {
        return MatchEventID.findMatchEventId(matchEventID).map(OWN_GOAL_GOAL_EVENTS::contains).orElse(false);
    }

    public boolean isOwnGoalNoGoalEvent() {
        return isOwnGoalNoGoalEvent(m_iMatchEventID);
    }

    public static boolean isOwnGoalNoGoalEvent(int matchEventID) {
        return MatchEventID.findMatchEventId(matchEventID).map(OWN_GOAL_NO_GOAL_EVENTS::contains).orElse(false);
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
        MatchEventID.MAN_MARKING_SUCCESS_SHORT_DISTANCE.getValue(),
        MatchEventID.MAN_MARKER_PENALTY_NO_MAN_MARKED_IN_OPPONENT_TEAM.getValue()).boxed().toList();

    /**
     * Check, if it is a man marking  event
     */
    public boolean isManMarking() {
        return manMarkingMatchEventTypes.contains(m_matchEventID.getValue());
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
        return getEventTextDescription(m_matchEventID.getValue());
    }

    public static String getEventTextDescription(MatchEventID matchEventID) {
        return getEventTextDescription(matchEventID.getValue());
    }

    public static String getEventTextDescription(int iMatchEventID) {
        return iMatchEventID + ": " + TranslationFacility.tr("MatchEvent_" + iMatchEventID);
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
