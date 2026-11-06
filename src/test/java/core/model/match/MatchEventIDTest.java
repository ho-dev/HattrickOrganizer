package core.model.match;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class MatchEventIDTest {

    private static Stream<Arguments> validMatchEventIds() {
        return Stream.of(MatchEventID.values())
            .map(MatchEventID::getValue)
            .map(Arguments::of);
    }

    private static Stream<Arguments> invalidMatchEventIds() {
        final var validIdSet = Stream.of(MatchEventID.values()).map(MatchEventID::getValue)
            .collect(Collectors.toSet());
        return IntStream.rangeClosed(-10, 1000)
            .filter(i -> !validIdSet.contains(i))
            .mapToObj(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("validMatchEventIds")
    void test_findMatchEventId(int value) {
        final var matchEventID = MatchEventID.findMatchEventId(value);
        assertThat(matchEventID).isPresent();
        assertThat(matchEventID.get().getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @MethodSource("invalidMatchEventIds")
    void test_findMatchEventId_invalid(int value) {
        final var matchEventID = MatchEventID.findMatchEventId(value);
        assertThat(matchEventID).isEmpty();
    }
}
