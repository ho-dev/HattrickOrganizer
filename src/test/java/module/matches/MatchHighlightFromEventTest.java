package module.matches;

import core.model.enums.MatchType;
import core.model.match.MatchEvent;
import core.model.match.MatchEvent.MatchEventID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MatchHighlightFromEventTest {

    private static final MatchHighlightFromEvent MATCH_HIGHLIGHT = createMatchHighlight();

    private static final Set<MatchEventID> GOAL_EVENTS = Stream.of(MatchEventID.values())
        .filter(matchEventID -> MatchEvent.isGoalEvent(matchEventID.getValue()))
        .collect(Collectors.toSet());

    private static final Set<MatchEventID> SUBSTITUTION_EVENTS = Stream.of(MatchEventID.values())
        .filter(matchEventID -> {
            final var matchEvent = createMatchEvent();
            matchEvent.setMatchEventID(matchEventID.getValue());
            return matchEvent.isSubstitution();
        })
        .collect(Collectors.toSet());

    private static final Set<MatchEventID> PENALTY_CONTEST_EVENTS = Stream.of(MatchEventID.values())
        .filter(matchEventID -> {
            final var matchEvent = createMatchEvent();
            matchEvent.setMatchEventID(matchEventID.getValue());
            return matchEvent.isPenaltyContestEvent();
        })
        .collect(Collectors.toSet());

    private static final Set<MatchEventID> PENALTY_CONTEST_GOAL_EVENTS = Stream.of(MatchEventID.values())
        .filter(matchEventID -> {
            final var matchEvent = createMatchEvent();
            matchEvent.setMatchEventID(matchEventID.getValue());
            return matchEvent.isPenaltyContestGoalEvent();
        })
        .collect(Collectors.toSet());

    private static final Set<MatchEventID> PENALTY_CONTEST_NO_GOAL_EVENTS = Stream.of(MatchEventID.values())
        .filter(matchEventID -> {
            final var matchEvent = createMatchEvent();
            matchEvent.setMatchEventID(matchEventID.getValue());
            return matchEvent.isPenaltyContestNoGoalEvent();
        })
        .collect(Collectors.toSet());

    @Test
    void test_homeTeamId() {
        assertThat(MATCH_HIGHLIGHT.homeTeamId()).isEqualTo(1);
    }

    @Test
    void test_score() {
        assertThat(MATCH_HIGHLIGHT.score()).isEqualTo(MatchScore.of(2, 1));
    }

    @Test
    void test_homeScored() {
        assertThat(MATCH_HIGHLIGHT.homeScored()).isFalse();
    }

    @Test
    void test_guestScored() {
        assertThat(MATCH_HIGHLIGHT.guestScored()).isFalse();
    }

    @Test
    void test_matchEvent() {
        final var matchEvent = createMatchEvent();
        final var matchHighlight = new MatchHighlightFromEvent(1, MatchScore.of(2, 1), false, false, matchEvent);
        assertThat(matchHighlight.matchEvent()).isSameAs(matchEvent);
    }

    @Test
     void test_getMatchEventID() {
        assertThat(MATCH_HIGHLIGHT.getMatchEventID()).isEqualTo(MatchEventID.PLAYERS_ENTER_THE_FIELD);
    }

    @Test
    void test_isHomeAction() {
        final var given = createMatchHighlight();
        assertThat(given.isHomeAction()).isTrue();
        given.matchEvent().setTeamID(2);
        assertThat(given.isHomeAction()).isFalse();
    }

    private static Stream<Arguments> test_isGoalEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isGoalEvent(MatchEventID matchEventID, boolean expected) {
        final var matchHighlightFromEvent = createMatchHighlight(matchEventID);
        assertThat(matchHighlightFromEvent.isGoalEvent()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isSubstitution() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, SUBSTITUTION_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isSubstitution(MatchEventID matchEventID, boolean expected) {
        final var matchHighlightFromEvent = createMatchHighlight(matchEventID);
        assertThat(matchHighlightFromEvent.isSubstitution()).isEqualTo(expected);
    }

    @Test
    void test_isOwnGoalGoalEvent() {
        final var given = createMatchHighlight();
        assertThat(given.isPenaltyContestStartEvent()).isFalse();
        given.matchEvent().setMatchEventID(MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL.getValue());
        assertThat(given.isHomeAction()).isTrue();
    }

    private static Stream<Arguments> test_isPenaltyContestEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEST_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestEvent(MatchEventID matchEventID, boolean expected) {
        final var matchHighlightFromEvent = createMatchHighlight(matchEventID);
        assertThat(matchHighlightFromEvent.isPenaltyContestEvent()).isEqualTo(expected);
    }

    @Test
    void test_isPenaltyContestStartEvent() {
        final var given = createMatchHighlight();
        assertThat(given.isPenaltyContestStartEvent()).isFalse();
        given.matchEvent().setMatchEventID(MatchEventID.PENALTY_CONTEST_AFTER_EXTENSION.getValue());
        assertThat(given.isHomeAction()).isTrue();
    }

    @Test
    void test_isPenaltyContestDecisionByCoinToss() {
        final var given = createMatchHighlight();
        assertThat(given.isPenaltyContestDecisionByCoinToss()).isFalse();
        given.matchEvent().setMatchEventID(MatchEventID.AFTER_22_PENALTIES_TOSSING_COIN.getValue());
        assertThat(given.isHomeAction()).isTrue();
    }

    private static Stream<Arguments> test_isPenaltyContestGoalEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEST_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestGoalEvent(MatchEventID matchEventID, boolean expected) {
        final var matchHighlightFromEvent = createMatchHighlight(matchEventID);
        assertThat(matchHighlightFromEvent.isPenaltyContestGoalEvent()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isPenaltyContestNoGoalEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEST_NO_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestNoGoalEvent(MatchEventID matchEventID, boolean expected) {
        final var matchHighlightFromEvent = createMatchHighlight(matchEventID);
        assertThat(matchHighlightFromEvent.isPenaltyContestNoGoalEvent()).isEqualTo(expected);
    }

    @Test
    void test_getIcons() {
        final var given = createMatchHighlight();
        assertThat(given.getIcons()).isEmpty();
        given.matchEvent().setMatchEventID(MatchEventID.TACTICAL_DISPOSITION.getValue());
        assertThat(given.getIcons()).isNotEmpty();
    }

    @Test
    void test_getPlayerName() {
        assertThat(MATCH_HIGHLIGHT.getPlayerName()).isEqualTo("PlayerName");
    }

    @Test
    void test_getAssistingPlayerName() {
        assertThat(MATCH_HIGHLIGHT.getAssistingPlayerName()).isEqualTo("AssistingPlayerName");
    }

    @Test
    void test_getMinute() {
        assertThat(MATCH_HIGHLIGHT.getMinute()).isEqualTo(3);
    }

    @Test
    void test_withHomeGoal() {
        final var given = createMatchHighlight().withGuestGoal(1);
        assertThat(given.score()).isEqualTo(MatchScore.of(2, 1));
        assertThat(given.homeScored()).isFalse();
        assertThat(given.guestScored()).isTrue();

        final var result = given.withHomeGoal(4);
        assertThat(result.score()).isEqualTo(MatchScore.of(4, 1));
        assertThat(result.homeScored()).isTrue();
        assertThat(result.guestScored()).isFalse();
    }

    @Test
    void test_withGuestGoal() {
        final var given = createMatchHighlight().withHomeGoal(2);
        assertThat(given.score()).isEqualTo(MatchScore.of(2, 1));
        assertThat(given.homeScored()).isTrue();
        assertThat(given.guestScored()).isFalse();

        final var result = given.withGuestGoal(3);
        assertThat(result.score()).isEqualTo(MatchScore.of(2, 3));
        assertThat(result.guestScored()).isTrue();
        assertThat(result.homeScored()).isFalse();
    }

    private static MatchHighlightFromEvent createMatchHighlight(MatchEventID matchEventID) {
        final var matchHighlightFromEvent = createMatchHighlight();
        matchHighlightFromEvent.matchEvent().setMatchEventID(matchEventID.getValue());
        return  matchHighlightFromEvent;
    }

    private static MatchHighlightFromEvent createMatchHighlight() {
        return new MatchHighlightFromEvent(1, MatchScore.of(2, 1), false, false, createMatchEvent());
    }

    private static MatchEvent createMatchEvent() {
        MatchEvent matchEvent = new MatchEvent();
        matchEvent.setTeamID(1);
        matchEvent.setMatchId(2);
        matchEvent.setMinute(3);
        matchEvent.setMatchEventID(MatchEventID.PLAYERS_ENTER_THE_FIELD.getValue());
        matchEvent.setGehilfeHeim(false);
        matchEvent.setSpielerHeim(false);
        matchEvent.setEventText("EventText");
        matchEvent.setPlayerId(5);
        matchEvent.setPlayerName("PlayerName");
        matchEvent.setAssistingPlayerId(6);
        matchEvent.setAssistingPlayerName("AssistingPlayerName");
        matchEvent.setMatchType(MatchType.FRIENDLYNORMAL);
        return matchEvent;
    }
}
