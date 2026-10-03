package core.model.match;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MatchEventTest {

    private static final Set<MatchEvent.MatchEventID> OWN_GOAL_GOAL_EVENTS =
        Set.of(MatchEvent.MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL);

    private static final Set<MatchEvent.MatchEventID> OWN_GOAL_NO_GOAL_EVENTS =
        Set.of(MatchEvent.MatchEventID.SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST);

    private static final Set<MatchEvent.MatchEventID> OWN_GOAL_EVENTS =
        Stream.concat(OWN_GOAL_GOAL_EVENTS.stream(), OWN_GOAL_NO_GOAL_EVENTS.stream()).collect(Collectors.toSet());

    private static final Set<MatchEvent.MatchEventID> PENALTY_CONTEXT_GOAL_EVENTS = Set.of(
        MatchEvent.MatchEventID.PENALTY_CONTEST_GOAL_BY_TECHNICAL_NO_NERVES,
        MatchEvent.MatchEventID.PENALTY_CONTEST_GOAL_NO_NERVES,
        MatchEvent.MatchEventID.PENALTY_CONTEST_GOAL_IN_SPITE_OF_NERVES
    );

    private static final Set<MatchEvent.MatchEventID> PENALTY_CONTEXT_NO_GOAL_EVENTS = Set.of(
        MatchEvent.MatchEventID.PENALTY_CONTEST_NO_GOAL_BECAUSE_OF_NERVES,
        MatchEvent.MatchEventID.PENALTY_CONTEST_NO_GOAL_IN_SPITE_OF_NO_NERVES
    );

    private static final Set<MatchEvent.MatchEventID> PENALTY_CONTEXT_GOAL_AND_NO_GOAL_EVENTS =
        Stream.concat(PENALTY_CONTEXT_GOAL_EVENTS.stream(), PENALTY_CONTEXT_NO_GOAL_EVENTS.stream()).collect(Collectors.toSet());

    private static final Set<MatchEvent.MatchEventID> PENALTY_CONTEXT_ADDITIONAL_EVENTS =
        Set.of(MatchEvent.MatchEventID.PENALTY_CONTEST_AFTER_EXTENSION, MatchEvent.MatchEventID.AFTER_22_PENALTIES_TOSSING_COIN);

    private static final Set<MatchEvent.MatchEventID> PENALTY_CONTEXT_EVENTS =
        Stream.concat(PENALTY_CONTEXT_GOAL_AND_NO_GOAL_EVENTS.stream(), PENALTY_CONTEXT_ADDITIONAL_EVENTS.stream()).collect(Collectors.toSet());

    private static Stream<Arguments> validMatchEventIds() {
        return Stream.of(MatchEvent.MatchEventID.values())
            .map(MatchEvent.MatchEventID::getValue)
            .map(Arguments::of);
    }

    private static Stream<Arguments> invalidMatchEventIds() {
        final var validIdSet = Stream.of(MatchEvent.MatchEventID.values()).map(MatchEvent.MatchEventID::getValue)
            .collect(Collectors.toSet());
        return IntStream.rangeClosed(-10, 1000)
            .filter(i -> !validIdSet.contains(i))
            .mapToObj(Arguments::of);
    }

    private static Stream<Arguments> test_isOwnGoalEvent() {
        return Stream.of(MatchEvent.MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, OWN_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isOwnGoalEvent(MatchEvent.MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isOwnGoalEvent()).isEqualTo(expected);
        assertThat(MatchEvent.isOwnGoalEvent(matchEvent.getiMatchEventID())).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isOwnGoalGoalEvent() {
        return Stream.of(MatchEvent.MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, OWN_GOAL_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isOwnGoalGoalEvent(MatchEvent.MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isOwnGoalGoalEvent()).isEqualTo(expected);
        assertThat(MatchEvent.isOwnGoalGoalEvent(matchEvent.getiMatchEventID())).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isOwnGoalNoGoalEvent() {
        return Stream.of(MatchEvent.MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, OWN_GOAL_NO_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isOwnGoalNoGoalEvent(MatchEvent.MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isOwnGoalNoGoalEvent()).isEqualTo(expected);
        assertThat(MatchEvent.isOwnGoalNoGoalEvent(matchEvent.getiMatchEventID())).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("validMatchEventIds")
    void test_findMatchEventId(int value) {
        final var matchEventID = MatchEvent.MatchEventID.findMatchEventId(value);
        assertThat(matchEventID).isPresent();
        assertThat(matchEventID.get().getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @MethodSource("invalidMatchEventIds")
    void test_findMatchEventId_invalid(int value) {
        final var matchEventID = MatchEvent.MatchEventID.findMatchEventId(value);
        assertThat(matchEventID).isEmpty();
    }

    private static Stream<Arguments> test_isPenaltyContestGoalEvent() {
        return Stream.of(MatchEvent.MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEXT_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestGoalEvent(MatchEvent.MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isPenaltyContestGoalEvent()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isPenaltyContestNoGoalEvent() {
        return Stream.of(MatchEvent.MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEXT_NO_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestNoGoalEvent(MatchEvent.MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isPenaltyContestNoGoalEvent()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isPenaltyContestEventGoalAndNoGoalEvent() {
        return Stream.of(MatchEvent.MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEXT_GOAL_AND_NO_GOAL_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestEventGoalAndNoGoalEvent(MatchEvent.MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isPenaltyContestEventGoalAndNoGoalEvent()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_isPenaltyContestEvent() {
        return Stream.of(MatchEvent.MatchEventID.values())
            .map(matchEventID -> Arguments.of(matchEventID, PENALTY_CONTEXT_EVENTS.contains(matchEventID)));
    }

    @ParameterizedTest
    @MethodSource
    void test_isPenaltyContestEvent(MatchEvent.MatchEventID matchEventID, boolean expected) {
        final MatchEvent matchEvent = createMatchEvent(matchEventID);
        assertThat(matchEvent.isPenaltyContestEvent()).isEqualTo(expected);
    }

    private static MatchEvent createMatchEvent(MatchEvent.MatchEventID matchEventID) {
        return createMatchEventFromMatchEventId(matchEventID.getValue());
    }

    private static MatchEvent createMatchEventFromMatchEventId(int matchEventId) {
        MatchEvent matchEvent = new MatchEvent();
        matchEvent.setMatchEventID(matchEventId);
        return matchEvent;
    }
}
