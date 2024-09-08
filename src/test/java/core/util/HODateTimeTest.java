package core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.data.Offset.offset;

class HODateTimeTest {

    private static final HODateTime HO_DATE_TIME = HODateTime.fromHT("2024-01-01 00:00:00");

        @Test
        void test() {
            var nextTraining = HODateTime.fromHT("2022-03-31 08:30:00");
            var localDateTime = nextTraining.toLocaleDateTime();
            var previousTraining = nextTraining.plusDaysAtSameLocalTime(-7);
            var localPrevious = previousTraining.toLocaleDateTime();

            var fetchedDate = HODateTime.fromHT("2022-01-08 14:33:58");

            Assertions.assertEquals("2022-01-08 14:33:58", fetchedDate.toHT());

//            Assertions.assertEquals("08.01.2022", fetchedDate.toLocaleDate());
//            Assertions.assertEquals("08.01.2022, 14:33:58", fetchedDate.toLocaleDateTime());

            var ts = fetchedDate.toDbTimestamp();
            Assertions.assertEquals("2022-01-08 14:33:58", HODateTime.fromDbTimestamp(ts).toHT());


            var dti = HODateTime.fromHT("2022-02-19 23:11:00");
            Assertions.assertEquals(dti.toHTWeek().season, 80);
            Assertions.assertEquals(dti.toHTWeek().week, 10);

            dti = HODateTime.fromHT("2021-02-14 23:11:00");
            Assertions.assertEquals(dti.toHTWeek().season, 77);
            Assertions.assertEquals(dti.toHTWeek().week, 6);

            dti = HODateTime.fromHT("2020-06-27 00:00:00");
            Assertions.assertEquals(dti.toHTWeek().season, 75);
            Assertions.assertEquals(dti.toHTWeek().week, 4);

            dti = HODateTime.fromHT("2018-05-10 00:00:00");
            Assertions.assertEquals(dti.toHTWeek().season, 68);
            Assertions.assertEquals(dti.toHTWeek().week, 5);

            dti = HODateTime.fromHT("2009-05-28 00:00:00");
            Assertions.assertEquals(dti.toHTWeek().season, 39);
            Assertions.assertEquals(dti.toHTWeek().week, 2);

            dti = HODateTime.fromHT("2020-09-07 00:00:00");
            Assertions.assertEquals(dti.toHTWeek().season, 75);
            Assertions.assertEquals(dti.toHTWeek().week, 15);

            dti = HODateTime.fromHT("2020-09-14 00:00:00");
            Assertions.assertEquals(dti.toHTWeek().season, 75);
            Assertions.assertEquals(dti.toHTWeek().week, 16);

            dti = HODateTime.fromHT("2020-09-21 01:30:00");
            Assertions.assertEquals(dti.toHTWeek().season, 76);
            Assertions.assertEquals(dti.toHTWeek().week, 1);

        }

    static Stream<Arguments> equals() {
        return Stream.of(
                Arguments.of(HODateTime.fromHT("2024-10-30 20:00:00"), HODateTime.fromHT("2025-10-30 20:00:00"), false),
                Arguments.of(HODateTime.fromHT("2024-10-30 20:00:00"), HODateTime.fromHT("2024-11-30 20:00:00"), false),
                Arguments.of(HODateTime.fromHT("2024-10-30 20:00:00"), HODateTime.fromHT("2024-10-31 20:00:00"), false),
                Arguments.of(HODateTime.fromHT("2024-10-30 20:00:00"), HODateTime.fromHT("2024-10-30 21:00:00"), false),
                Arguments.of(HODateTime.fromHT("2024-10-30 20:00:00"), HODateTime.fromHT("2024-10-30 20:01:00"), false),
                Arguments.of(HODateTime.fromHT("2024-10-30 20:00:00"), HODateTime.fromHT("2024-10-30 20:00:01"), false),
                Arguments.of(HO_DATE_TIME, HO_DATE_TIME, true),
                Arguments.of(HODateTime.fromHT("2024-10-30 20:00:00"), HODateTime.fromHT("2024-10-30 20:00:00"), true),
                Arguments.of(HO_DATE_TIME, null, false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void equals(HODateTime lhs, HODateTime rhs, boolean result) {
        assertThat(lhs.equals(rhs)).isEqualTo(result);
    }


    @Test
    void testHashCode_consistencyOnTwoCalls() {
        // given
        final var now = HODateTime.now();

        // when
        final var hashCode1 = now.hashCode();
        final var hashCode2 = now.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void testHashCode_twoEqualObjectsResultsEqualHashCodes() {
        // given
        final var htTimeString = "2024-12-31 23:59:59";
        final var hoDateTime1 = HODateTime.fromHT(htTimeString);
        final var hoDateTime2 = HODateTime.fromHT(htTimeString);

        // when
        final var hashCode1 = hoDateTime1.hashCode();
        final var hashCode2 = hoDateTime2.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void testHashCode_givenMultipleObjects_whenTestingHashCodeDistribution_thenEvenDistributionOfHashCodes() {
        // given
        final var localDateTime = LocalDateTime.now();
        final var objects = IntStream.range(0, 1000).mapToObj(i -> generate(i, localDateTime)).toList();

        // when
        final Set<Integer> hashCodes = objects.stream().map(Objects::hashCode).collect(Collectors.toSet());

        // then
        assertThat(hashCodes.size()).isCloseTo( objects.size(), offset(10));
    }

    private static HODateTime generate(int i, LocalDateTime localDateTime) {
        return fromLocalDateTime(localDateTime.plusDays(i));
    }

    private static HODateTime fromLocalDateTime(LocalDateTime localDateTime) {
        final var zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        zonedDateTime.withZoneSameInstant(HODateTime.DEFAULT_TIMEZONE);
        return new HODateTime(zonedDateTime.toInstant());
    }
}
