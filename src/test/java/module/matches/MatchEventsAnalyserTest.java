package module.matches;

import core.model.match.MatchEvent;
import core.model.match.MatchEventID;
import core.util.StreamUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MatchEventsAnalyserTest {

    private static final Set<MatchEventID> VALID_HIGHLIGHT_EVENTS =
        Set.of(
            MatchEventID.REDUCING_GOAL_HOME_TEAM_FREE_KICK,
            MatchEventID.PENALTY_CONTEST_GOAL_BY_TECHNICAL_NO_NERVES,
            MatchEventID.PENALTY_CONTEST_GOAL_NO_NERVES,
            MatchEventID.PENALTY_CONTEST_NO_GOAL_BECAUSE_OF_NERVES,
            MatchEventID.PENALTY_CONTEST_AFTER_EXTENSION,
            MatchEventID.PENALTY_CONTEST_GOAL_IN_SPITE_OF_NERVES,
            MatchEventID.PENALTY_CONTEST_NO_GOAL_IN_SPITE_OF_NO_NERVES,
            MatchEventID.REDUCING_GOAL_HOME_TEAM_MIDDLE,
            MatchEventID.REDUCING_GOAL_HOME_TEAM_LEFT_WING,
            MatchEventID.REDUCING_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.REDUCING_GOAL_HOME_TEAM_RIGHT_WING,
            MatchEventID.SE_GOAL_UNPREDICTABLE_LONG_PASS,
            MatchEventID.SE_GOAL_UNPREDICTABLE_SCORES_ON_HIS_OWN,
            MatchEventID.GOAL_LONG_SHOT_NO_TACTIC,
            MatchEventID.SE_GOAL_UNPREDICTABLE_SPECIAL_ACTION,
            MatchEventID.SE_GOAL_UNPREDICTABLE_MISTAKE,
            MatchEventID.EQUALIZER_GOAL_HOME_TEAM_FREE_KICK,
            MatchEventID.EQUALIZER_GOAL_HOME_TEAM_MIDDLE,
            MatchEventID.EQUALIZER_GOAL_HOME_TEAM_LEFT_WING,
            MatchEventID.EQUALIZER_GOAL_HOME_TEAM_RIGHT_WING,
            MatchEventID.EQUALIZER_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.SE_QUICK_SCORES_AFTER_RUSH,
            MatchEventID.SE_QUICK_RUSHES_PASSES_AND_RECEIVER_SCORES,
            MatchEventID.SE_TIRED_DEFENDER_MISTAKE_STRIKER_SCORES,
            MatchEventID.SE_GOAL_CORNER_TO_ANYONE,
            MatchEventID.SE_GOAL_CORNER_HEAD_SPECIALIST,
            MatchEventID.GOAL_TO_TAKE_LEAD_HOME_TEAM_FREE_KICK,
            MatchEventID.GOAL_TO_TAKE_LEAD_HOME_TEAM_MIDDLE,
            MatchEventID.GOAL_TO_TAKE_LEAD_HOME_TEAM_LEFT_WING,
            MatchEventID.GOAL_TO_TAKE_LEAD_HOME_TEAM_RIGHT_WING,
            MatchEventID.GOAL_TO_TAKE_LEAD_HOME_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL,
            MatchEventID.INCREASE_GOAL_HOME_TEAM_FREE_KICK,
            MatchEventID.INCREASE_GOAL_HOME_TEAM_MIDDLE,
            MatchEventID.INCREASE_GOAL_HOME_TEAM_LEFT_WING,
            MatchEventID.INCREASE_GOAL_HOME_TEAM_RIGHT_WING,
            MatchEventID.INCREASE_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.SE_EXPERIENCED_FORWARD_SCORES,
            MatchEventID.SE_INEXPERIENCED_DEFENDER_CAUSES_GOAL,
            MatchEventID.SE_WINGER_TO_HEAD_SPEC_SCORES,
            MatchEventID.SE_WINGER_TO_ANYONE_SCORES,
            MatchEventID.SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER,
            MatchEventID.COUNTER_ATTACK_GOAL_FREE_KICK,
            MatchEventID.COUNTER_ATTACK_GOAL_MIDDLE,
            MatchEventID.COUNTER_ATTACK_GOAL_LEFT,
            MatchEventID.COUNTER_ATTACK_GOAL_RIGHT,
            MatchEventID.REDUCING_GOAL_AWAY_TEAM_FREE_KICK,
            MatchEventID.REDUCING_GOAL_AWAY_TEAM_MIDDLE,
            MatchEventID.REDUCING_GOAL_AWAY_TEAM_LEFT_WING,
            MatchEventID.REDUCING_GOAL_AWAY_TEAM_RIGHT_WING,
            MatchEventID.REDUCING_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.EQUALIZER_GOAL_AWAY_TEAM_FREE_KICK,
            MatchEventID.EQUALIZER_GOAL_AWAY_TEAM_MIDDLE,
            MatchEventID.EQUALIZER_GOAL_AWAY_TEAM_LEFT_WING,
            MatchEventID.EQUALIZER_GOAL_AWAY_TEAM_RIGHT_WING,
            MatchEventID.EQUALIZER_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.GOAL_TO_TAKE_LEAD_AWAY_TEAM_FREE_KICK,
            MatchEventID.GOAL_TO_TAKE_LEAD_AWAY_TEAM_MIDDLE,
            MatchEventID.GOAL_TO_TAKE_LEAD_AWAY_TEAM_LEFT_WING,
            MatchEventID.GOAL_TO_TAKE_LEAD_AWAY_TEAM_RIGHT_WING,
            MatchEventID.GOAL_TO_TAKE_LEAD_AWAY_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.INCREASE_GOAL_AWAY_TEAM_FREE_KICK,
            MatchEventID.INCREASE_GOAL_AWAY_TEAM_MIDDLE,
            MatchEventID.INCREASE_GOAL_AWAY_TEAM_LEFT_WING,
            MatchEventID.INCREASE_GOAL_AWAY_TEAM_RIGHT_WING,
            MatchEventID.INCREASE_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.GOAL_INDIRECT_FREE_KICK,
            MatchEventID.COUNTER_ATTACK_GOAL_INDIRECT_FREE_KICK,
            MatchEventID.GOAL_LONG_SHOT,
            MatchEventID.SE_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE,
            MatchEventID.NO_REDUCING_GOAL_HOME_TEAM_FREE_KICK,
            MatchEventID.NO_REDUCING_GOAL_HOME_TEAM_MIDDLE,
            MatchEventID.NO_REDUCING_GOAL_HOME_TEAM_LEFT_WING,
            MatchEventID.NO_REDUCING_GOAL_HOME_TEAM_RIGHT_WING,
            MatchEventID.SE_NO_GOAL_UNPREDICTABLE_LONG_PASS,
            MatchEventID.SE_NO_GOAL_UNPREDICTABLE_ALMOST_SCORES,
            MatchEventID.NO_GOAL_LONG_SHOT_NO_TACTIC,
            MatchEventID.SE_NO_GOAL_UNPREDICTABLE_SPECIAL_ACTION,
            MatchEventID.SE_NO_GOAL_UNPREDICTABLE_MISTAKE,
            MatchEventID.NO_EQUALIZER_GOAL_HOME_TEAM_FREE_KICK,
            MatchEventID.NO_REDUCING_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.NO_EQUALIZER_GOAL_HOME_TEAM_MIDDLE,
            MatchEventID.NO_EQUALIZER_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.SE_SPEEDY_MISSES_AFTER_RUSH,
            MatchEventID.SE_QUICK_RUSHES_PASSES_BUT_RECEIVER_FAILS,
            MatchEventID.SE_TIRED_DEFENDER_MISTAKE_BUT_NO_GOAL,
            MatchEventID.SE_NO_GOAL_CORNER_TO_ANYONE,
            MatchEventID.SE_NO_GOAL_CORNER_HEAD_SPECIALIST,
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_FREE_KICK,
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_MIDDLE,
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_LEFT_WING,
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST,
            MatchEventID.NO_INCREASE_GOAL_HOME_TEAM_FREE_KICK,
            MatchEventID.NO_INCREASE_GOAL_HOME_TEAM_MIDDLE,
            MatchEventID.NO_EQUALIZER_GOAL_HOME_TEAM_LEFT_WING,
            MatchEventID.NO_EQUALIZER_GOAL_HOME_TEAM_RIGHT_WING,
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_HOME_TEAM_RIGHT_WING,
            MatchEventID.NO_INCREASE_GOAL_HOME_TEAM_LEFT_WING,
            MatchEventID.NO_INCREASE_GOAL_HOME_TEAM_RIGHT_WING,
            MatchEventID.NO_INCREASE_GOAL_HOME_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.SE_EXPERIENCED_FORWARD_FAILS_TO_SCORE,
            MatchEventID.SE_INEXPERIENCED_DEFENDER_ALMOST_CAUSES_GOAL,
            MatchEventID.SE_WINGER_TO_SOMEONE_NO_GOAL,
            MatchEventID.SE_TECHNICAL_GOES_AROUND_HEAD_PLAYER_NO_GOAL,
            MatchEventID.COUNTER_ATTACK_NO_GOAL_FREE_KICK,
            MatchEventID.COUNTER_ATTACK_NO_GOAL_MIDDLE,
            MatchEventID.COUNTER_ATTACK_NO_GOAL_LEFT,
            MatchEventID.COUNTER_ATTACK_NO_GOAL_RIGHT,
            MatchEventID.NO_REDUCING_GOAL_AWAY_TEAM_FREE_KICK,
            MatchEventID.NO_REDUCING_GOAL_AWAY_TEAM_MIDDLE,
            MatchEventID.NO_REDUCING_GOAL_AWAY_TEAM_LEFT_WING,
            MatchEventID.NO_REDUCING_GOAL_AWAY_TEAM_RIGHT_WING,
            MatchEventID.NO_REDUCING_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.NO_EQUALIZER_GOAL_AWAY_TEAM_FREE_KICK,
            MatchEventID.NO_EQUALIZER_GOAL_AWAY_TEAM_MIDDLE,
            MatchEventID.NO_EQUALIZER_GOAL_AWAY_TEAM_LEFT_WING,
            MatchEventID.NO_EQUALIZER_GOAL_AWAY_TEAM_RIGHT_WING,
            MatchEventID.NO_EQUALIZER_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_FREE_KICK,
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_MIDDLE,
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_LEFT_WING,
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_RIGHT_WING,
            MatchEventID.NO_GOAL_TO_TAKE_LEAD_AWAY_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.NO_GOAL_INDIRECT_FREE_KICK,
            MatchEventID.COUNTER_ATTACK_NO_GOAL_INDIRECT_FREE_KICK,
            MatchEventID.NO_GOAL_LONG_SHOT,
            MatchEventID.NO_GOAL_LONG_SHOT_DEFENDED,
            MatchEventID.SE_QUICK_RUSHES_STOPPED_BY_QUICK_DEFENDER,
            MatchEventID.PLAYER_SUBSTITUTION_TEAM_IS_BEHIND,
            MatchEventID.PLAYER_SUBSTITUTION_TEAM_IS_AHEAD,
            MatchEventID.PLAYER_SUBSTITUTION_MINUTE,
            MatchEventID.CHANGE_OF_TACTIC_TEAM_IS_BEHIND,
            MatchEventID.CHANGE_OF_TACTIC_TEAM_IS_AHEAD,
            MatchEventID.NO_INCREASE_GOAL_AWAY_TEAM_FREE_KICK,
            MatchEventID.NO_INCREASE_GOAL_AWAY_TEAM_MIDDLE,
            MatchEventID.NO_INCREASE_GOAL_AWAY_TEAM_LEFT_WING,
            MatchEventID.NO_INCREASE_GOAL_AWAY_TEAM_RIGHT_WING,
            MatchEventID.NO_INCREASE_GOAL_AWAY_TEAM_PENALTY_KICK_NORMAL,
            MatchEventID.SE_NO_GOAL_POWERFUL_NORMAL_FORWARD_GENERATES_EXTRA_CHANCE,
            MatchEventID.CHANGE_OF_TACTIC_MINUTE,
            MatchEventID.INJURED_PLAYER_REPLACED,
            MatchEventID.YELLOW_CARD_NASTY_PLAY,
            MatchEventID.YELLOW_CARD_CHEATING,
            MatchEventID.RED_CARD_2ND_WARNING_NASTY_PLAY,
            MatchEventID.RED_CARD_2ND_WARNING_CHEATING,
            MatchEventID.RED_CARD_WITHOUT_WARNING,
            MatchEventID.AFTER_22_PENALTIES_TOSSING_COIN
        );

    private static final int HOME_TEAM_ID = 1;
    private static final int GUEST_TEAM_ID = 3;

    private static Stream<Arguments> validMatchEventIds() {
        return VALID_HIGHLIGHT_EVENTS.stream().map(Arguments::of);
    }

    private static Stream<Arguments> invalidMatchEventIds() {
        final var validHighlightEventIdIntSet = new HashSet<>(VALID_HIGHLIGHT_EVENTS);
        return Stream.of(MatchEventID.values())
            .filter(matchEventID -> !validHighlightEventIdIntSet.contains(matchEventID))
            .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("validMatchEventIds")
    void test_analyse_validMatchEventIds_resultInOneEvent(MatchEventID matchEventID) {
        final var matchEvent = createMatchEvent(matchEventID);

        final var matchHighlights = MatchEventsAnalyser.analyse(HOME_TEAM_ID, List.of(matchEvent));
        final var matchHighlight = findOneMatchHighlight(matchHighlights);
        assertThat(matchHighlight.isPresent()).isTrue();
        assertThat(matchHighlight.orElseThrow().getMatchEventID()).isEqualTo(matchEventID);
    }

    @ParameterizedTest
    @MethodSource("invalidMatchEventIds")
    void test_analyse_invalidMatchEventId_resultInNoEvents(MatchEventID matchEventID) {
        final var matchEvent = createMatchEvent(matchEventID);

        final var matchHighlights = MatchEventsAnalyser.analyse(HOME_TEAM_ID, List.of(matchEvent));
        final var highlightedEvents = extractHighlightedEvents(matchHighlights);
        assertThat(highlightedEvents.isEmpty()).isTrue();
    }

    private static Stream<Arguments> test_analyse_ownGoalCountedForOpposingTeam() {
        return Stream.of(
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL, new Player(GUEST_TEAM_ID, 5, "Owen Goal"), new Player(GUEST_TEAM_ID, 7, "Owen Assist"), MatchScore.of(1, 0), "Guest Player scores an own goal for the home team assisted by his teammate"),
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL, new Player(HOME_TEAM_ID, 5, "Owen Goal"), new Player(HOME_TEAM_ID, 7, "Owen Assist"), MatchScore.of(0, 1), "Home Player scores an own goal for the guest team assisted by his teammate"),
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL, new Player(GUEST_TEAM_ID, 5, "Owen Goal"), new Player(HOME_TEAM_ID, 7, "Owen Assist"), MatchScore.of(1, 0), "Guest Player scores an own goal for the home team assisted by player from home team"),
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL, new Player(HOME_TEAM_ID, 5, "Owen Goal"), new Player(GUEST_TEAM_ID, 7, "Owen Assist"), MatchScore.of(0, 1), "Home Player scores an own goal for the guest team assisted by player from guest team"),
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST, new Player(GUEST_TEAM_ID, 5, "Owen Goal"), new Player(GUEST_TEAM_ID, 7, "Owen Assist"), MatchScore.of(0, 0), "Guest Player scores ALMOST an own goal for the home team assisted by his teammate"),
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST, new Player(HOME_TEAM_ID, 5, "Owen Goal"), new Player(HOME_TEAM_ID, 7, "Owen Assist"), MatchScore.of(0, 0), "Home Player scores ALMOST an own goal for the guest team assisted by his teammate"),
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST, new Player(GUEST_TEAM_ID, 5, "Owen Goal"), new Player(HOME_TEAM_ID, 7, "Owen Assist"), MatchScore.of(0, 0), "Guest Player scores ALMOST an own goal for the home team assisted by player from home team"),
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST, new Player(HOME_TEAM_ID, 5, "Owen Goal"), new Player(GUEST_TEAM_ID, 7, "Owen Assist"), MatchScore.of(0, 0), "Home Player scores ALMOST an own goal for the guest team assisted by player from guest team")
        );
    }

    @ParameterizedTest(name = "[{index}] {6}: TeamId {0} vs {1}: {2}: Jinx={3}, Assist={4} = {5}")
    @MethodSource
    void test_analyse_ownGoalCountedForOpposingTeam(int homeTeamId, int guestTeamId,
                                                    MatchEventID matchEventID,
                                                    Player jinxPlayer, Player assistingPlayer,
                                                    MatchScore expectedResult,
                                                    String testDescription) {
        final var matchEvent = MatchEventFactory.of(homeTeamId, guestTeamId).createOwnGoalEvent(matchEventID, jinxPlayer, assistingPlayer);

        // when
        final var matchHighlights = MatchEventsAnalyser.analyse(homeTeamId, List.of(matchEvent));

        // then
        final var oneMatchHighlight = findOneMatchHighlight(matchHighlights);
        assertThat(oneMatchHighlight).isPresent();
        final var matchHighlight = oneMatchHighlight.orElseThrow();
        assertThat(matchHighlight.getMatchEventID()).isEqualTo(matchEventID);
        assertThat(matchHighlight.getMinute()).isEqualTo(matchEvent.getMinute());
        assertThat(matchHighlight.getPlayerName()).isEqualTo(jinxPlayer.playerName());
        assertThat(matchHighlight.getAssistingPlayerName()).isEqualTo(assistingPlayer.playerName());
        assertThat(matchHighlight.score()).isEqualTo(expectedResult);
        assertMatchHighlightPreservesMatchEvent(oneMatchHighlight.orElseThrow(), matchEvent);
    }

    private static Stream<Arguments> test_analyse_singlePlayerMatchEvent() {
        return Stream.of(
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_GOAL_UNPREDICTABLE_SCORES_ON_HIS_OWN, new Player(GUEST_TEAM_ID, 5, "Unpredictable scores on his own"), MatchScore.of(0, 1), "Guest Player scores for his own"),
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_GOAL_UNPREDICTABLE_SCORES_ON_HIS_OWN, new Player(HOME_TEAM_ID, 5, "Unpredictable scores on his own"), MatchScore.of(1, 0), "Home Player scores for his own")
        );
    }

    @ParameterizedTest(name = "[{index}] {5}: TeamId {0} vs {1}: {2}: Player={3} = {4}")
    @MethodSource
    void test_analyse_singlePlayerMatchEvent(int homeTeamId, int guestTeamId,
                                             MatchEventID matchEventID,
                                             Player player,
                                             MatchScore expectedResult,
                                             String testDescription) {
        // given
        final var matchEvent = MatchEventFactory.of(homeTeamId, guestTeamId).createSinglePlayerEvent(matchEventID, player);

        // when
        final var matchHighlights = MatchEventsAnalyser.analyse(homeTeamId, List.of(matchEvent));

        // then
        final var oneMatchHighlight = findOneMatchHighlight(matchHighlights);
        assertThat(oneMatchHighlight).isPresent();
        final var matchHighlight = oneMatchHighlight.orElseThrow();
        assertThat(matchHighlight.getMatchEventID()).isEqualTo(matchEventID);
        assertThat(matchHighlight.getMinute()).isEqualTo(matchEvent.getMinute());
        assertThat(matchHighlight.getPlayerName()).isEqualTo(player.playerName());
        assertThat(matchHighlight.getAssistingPlayerName()).isEqualTo("");
        assertThat(matchHighlight.score()).isEqualTo(expectedResult);
        assertMatchHighlightPreservesMatchEvent(oneMatchHighlight.orElseThrow(), matchEvent);
    }

    private static Stream<Arguments> test_analyse_playerAssistedByOpponent() {
        return Stream.of(
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_GOAL_UNPREDICTABLE_MISTAKE, new Player(HOME_TEAM_ID, 5, "Mr. Goal"), new Player(GUEST_TEAM_ID, 7, "Mr. Assist"), MatchScore.of(1, 0), "Home Player scores a goal for the home team assisted by opponent"),
            Arguments.of(HOME_TEAM_ID, GUEST_TEAM_ID, MatchEventID.SE_GOAL_UNPREDICTABLE_MISTAKE, new Player(GUEST_TEAM_ID, 5, "Mr. Goal"), new Player(HOME_TEAM_ID, 7, "Mr. Assist"), MatchScore.of(0, 1), "Guest Player scores a goal for the guest assisted by opponent")
        );
    }

    @ParameterizedTest(name = "[{index}] {6}: TeamId {0} vs {1}: {2}: Player={3}, Assist={4} = {5}")
    @MethodSource
    void test_analyse_playerAssistedByOpponent(int homeTeamId, int guestTeamId,
                                               MatchEventID matchEventID,
                                               Player player, Player assistingPlayer,
                                               MatchScore expectedResult,
                                               String testDescription) {
        final var matchEvent = MatchEventFactory.of(homeTeamId, guestTeamId).createGoalEventAssistedByOpponent(matchEventID, player, assistingPlayer);

        // when
        final var matchHighlights = MatchEventsAnalyser.analyse(homeTeamId, List.of(matchEvent));

        // then
        final var oneMatchHighlight = findOneMatchHighlight(matchHighlights);
        assertThat(oneMatchHighlight).isPresent();
        final var matchHighlight = oneMatchHighlight.orElseThrow();
        assertThat(matchHighlight.getMatchEventID()).isEqualTo(matchEventID);
        assertThat(matchHighlight.getMinute()).isEqualTo(matchEvent.getMinute());
        assertThat(matchHighlight.getPlayerName()).isEqualTo(player.playerName());
        assertThat(matchHighlight.getAssistingPlayerName()).isEqualTo(assistingPlayer.playerName());
        assertThat(matchHighlight.score()).isEqualTo(expectedResult);
        assertMatchHighlightPreservesMatchEvent(oneMatchHighlight.orElseThrow(), matchEvent);
    }

    private static void assertMatchHighlightPreservesMatchEvent(MatchHighlight matchHighlight, MatchEvent matchEvent) {
        final MatchHighlightFromEvent matchHighlightFromEvent = (MatchHighlightFromEvent) matchHighlight;
        assertThat(matchHighlightFromEvent.matchEvent()).isSameAs(matchEvent);
    }

    private static MatchEvent createMatchEvent(MatchEventID matchEventID) {
        MatchEvent matchEvent = new MatchEvent();
        matchEvent.setMatchEventID(matchEventID.getValue());
        return matchEvent;
    }

    private static Optional<MatchHighlight> findOneMatchHighlight(MatchHighlights matchHighlights) {
        return StreamUtils.findOne(extractHighlightedEvents(matchHighlights).stream());
    }

    private static List<MatchHighlight> extractHighlightedEvents(MatchHighlights matchHighlights) {
        return Stream.concat(matchHighlights.regularAndExtraTime().highlights().stream(),
                matchHighlights.getPenaltyContest()
                    .map(MatchHighlightPenaltyContest::highlights)
                    .stream()
                    .flatMap(Collection::stream))
            .toList();
    }
}
