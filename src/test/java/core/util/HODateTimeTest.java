package core.util;

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
        final var fetchedDate = HODateTime.fromHT("2022-01-08 14:33:58");
        assertThat(fetchedDate.toHT()).isEqualTo("2022-01-08 14:33:58");

        final var ts = fetchedDate.toDbTimestamp();
        assertThat(HODateTime.fromDbTimestamp(ts).toHT()).isEqualTo("2022-01-08 14:33:58");


        var dti = HODateTime.fromHT("2022-02-19 23:11:00");
        assertThat(dti.toHTWeek().season).isEqualTo(80);
        assertThat(dti.toHTWeek().week).isEqualTo(10);

        dti = HODateTime.fromHT("2021-02-14 23:11:00");
        assertThat(dti.toHTWeek().season).isEqualTo(77);
        assertThat(dti.toHTWeek().week).isEqualTo(6);

        dti = HODateTime.fromHT("2020-06-27 00:00:00");
        assertThat(dti.toHTWeek().season).isEqualTo(75);
        assertThat(dti.toHTWeek().week).isEqualTo(4);

        dti = HODateTime.fromHT("2018-05-10 00:00:00");
        assertThat(dti.toHTWeek().season).isEqualTo(68);
        assertThat(dti.toHTWeek().week).isEqualTo(5);

        dti = HODateTime.fromHT("2009-05-28 00:00:00");
        assertThat(dti.toHTWeek().season).isEqualTo(39);
        assertThat(dti.toHTWeek().week).isEqualTo(2);

        dti = HODateTime.fromHT("2020-09-07 00:00:00");
        assertThat(dti.toHTWeek().season).isEqualTo(75);
        assertThat(dti.toHTWeek().week).isEqualTo(15);

        dti = HODateTime.fromHT("2020-09-14 00:00:00");
        assertThat(dti.toHTWeek().season).isEqualTo(75);
        assertThat(dti.toHTWeek().week).isEqualTo(16);

        dti = HODateTime.fromHT("2020-09-21 01:30:00");
        assertThat(dti.toHTWeek().season).isEqualTo(76);
        assertThat(dti.toHTWeek().week).isEqualTo(1);
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
        assertThat(hashCodes.size()).isCloseTo(objects.size(), offset(10));
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
