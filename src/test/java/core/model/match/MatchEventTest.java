package core.model.match;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MatchEventTest {

    private static final Set<MatchEventID> OWN_GOAL_GOAL_EVENTS =
        Set.of(MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL);

    private static final Set<MatchEventID> OWN_GOAL_NO_GOAL_EVENTS =
        Set.of(MatchEventID.SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST);

    private static final Set<MatchEventID> OWN_GOAL_EVENTS =
        Stream.concat(OWN_GOAL_GOAL_EVENTS.stream(), OWN_GOAL_NO_GOAL_EVENTS.stream()).collect(Collectors.toSet());

    private static final Set<MatchEventID> PENALTY_CONTEXT_GOAL_EVENTS = Set.of(
        MatchEventID.PENALTY_CONTEST_GOAL_BY_TECHNICAL_NO_NERVES,
        MatchEventID.PENALTY_CONTEST_GOAL_NO_NERVES,
        MatchEventID.PENALTY_CONTEST_GOAL_IN_SPITE_OF_NERVES
    );

    private static final Set<MatchEventID> PENALTY_CONTEXT_NO_GOAL_EVENTS = Set.of(
        MatchEventID.PENALTY_CONTEST_NO_GOAL_BECAUSE_OF_NERVES,
        MatchEventID.PENALTY_CONTEST_NO_GOAL_IN_SPITE_OF_NO_NERVES
    );

    private static final Set<MatchEventID> PENALTY_CONTEXT_GOAL_AND_NO_GOAL_EVENTS =
        Stream.concat(PENALTY_CONTEXT_GOAL_EVENTS.stream(), PENALTY_CONTEXT_NO_GOAL_EVENTS.stream()).collect(Collectors.toSet());

    private static final Set<MatchEventID> PENALTY_CONTEXT_ADDITIONAL_EVENTS =
        Set.of(MatchEventID.PENALTY_CONTEST_AFTER_EXTENSION, MatchEventID.AFTER_22_PENALTIES_TOSSING_COIN);

    private static final Set<MatchEventID> PENALTY_CONTEXT_EVENTS =
        Stream.concat(PENALTY_CONTEXT_GOAL_AND_NO_GOAL_EVENTS.stream(), PENALTY_CONTEXT_ADDITIONAL_EVENTS.stream()).collect(Collectors.toSet());


    private static Stream<Arguments> test_isOwnGoalEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, OWN_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isOwnGoalEvent(MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isOwnGoalEvent()).isEqualTo(expected);
        assertThat(MatchEvent.isOwnGoalEvent(matchEvent.getiMatchEventID())).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isOwnGoalGoalEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, OWN_GOAL_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isOwnGoalGoalEvent(MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isOwnGoalGoalEvent()).isEqualTo(expected);
        assertThat(MatchEvent.isOwnGoalGoalEvent(matchEvent.getiMatchEventID())).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isOwnGoalNoGoalEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, OWN_GOAL_NO_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isOwnGoalNoGoalEvent(MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isOwnGoalNoGoalEvent()).isEqualTo(expected);
        assertThat(MatchEvent.isOwnGoalNoGoalEvent(matchEvent.getiMatchEventID())).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isPenaltyContestGoalEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEXT_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestGoalEvent(MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isPenaltyContestGoalEvent()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isPenaltyContestNoGoalEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEXT_NO_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestNoGoalEvent(MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isPenaltyContestNoGoalEvent()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isPenaltyContestEventGoalAndNoGoalEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEXT_GOAL_AND_NO_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestEventGoalAndNoGoalEvent(MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isPenaltyContestEventGoalAndNoGoalEvent()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isPenaltyContestEvent() {
        return Stream.of(MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEXT_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestEvent(MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isPenaltyContestEvent()).isEqualTo(expected);
    }

    private static MatchEvent createMatchEvent(MatchEventID matchEventID) {
        return createMatchEventFromMatchEventId(matchEventID.getValue());
    }

    private static MatchEvent createMatchEventFromMatchEventId(int matchEventId) {
        MatchEvent matchEvent = new MatchEvent();
        matchEvent.setMatchEventID(matchEventId);
        return matchEvent;
    }
}
