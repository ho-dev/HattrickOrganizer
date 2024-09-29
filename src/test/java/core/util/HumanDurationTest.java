package core.util;

import core.model.TranslationFacility;
import core.model.Translator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

class HumanDurationTest {

    private static Stream<Arguments> secondsAndHumanDurations() {
        return Stream.of(
                Arguments.of(3600L * 2L + 60L * 3L + 4L, HumanDuration.builder().hours(2).minutes(3).seconds(4).build()),
                Arguments.of(86400L + 60L * 3L + 4L, HumanDuration.builder().days(1).minutes(3).seconds(4).build()),
                Arguments.of(86400L + 3600L * 2L + 4L, HumanDuration.builder().days(1).hours(2).seconds(4).build()),
                Arguments.of(86400L + 3600L * 2L + 60L * 3L, HumanDuration.builder().days(1).hours(2).minutes(3).build()),
                Arguments.of(86400L + 3600L * 2L + 60L * 3L + 4L, HumanDuration.builder().days(1).hours(2).minutes(3).seconds(4).build()),
                Arguments.of(0L, HumanDuration.builder().build())
                );
    }

    private static Stream<Arguments> humanDurationToString() {
        return Stream.of(
                Arguments.of(HumanDuration.builder().hours(2).minutes(3).seconds(4).build(), "2h, 3m, 4s"),
                Arguments.of(HumanDuration.builder().days(1).minutes(3).seconds(4).build(), "1d, 3m, 4s"),
                Arguments.of(HumanDuration.builder().days(1).hours(2).seconds(4).build(), "1d, 2h, 4s"),
                Arguments.of(HumanDuration.builder().days(1).hours(2).minutes(3).build(), "1d, 2h, 3m"),
                Arguments.of(HumanDuration.builder().days(1).hours(2).minutes(3).seconds(4).build(), "1d, 2h, 3m, 4s"),
                Arguments.of(HumanDuration.builder().build(), EMPTY)
        );
    }

    @ParameterizedTest
    @MethodSource("secondsAndHumanDurations")
    void of(long durationInSeconds, HumanDuration expected) {
        // given
        final Duration duration = Duration.ofSeconds(durationInSeconds);

        // when
        final HumanDuration result = HumanDuration.of(duration);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("secondsAndHumanDurations")
    void fromSeconds(long durationInSeconds, HumanDuration expected) {
        // when
        final HumanDuration result = HumanDuration.fromSeconds(durationInSeconds);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("humanDurationToString")
    void toHumanString(HumanDuration humanDuration, String expected) {
        // given
        TranslationFacility.setTranslator(Translator.load(Translator.LANGUAGE_DEFAULT));

        // when
        final var result = humanDuration.toHumanString();

        // then
        assertThat(result).isEqualTo(expected);
    }
}