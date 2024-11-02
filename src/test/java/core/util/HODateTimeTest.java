package core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.junit.jupiter.params.provider.Arguments.of;

class HODateTimeTest {

    private static final HODateTime HO_DATE_TIME = HODateTime.fromHT("2024-01-01 00:00:00");

    private static final LocalDateTime NEXT_LOCAL_DAY_1 = LocalDate.of(2024, 8, 30).atStartOfDay();
    private static final LocalDateTime NEXT_LOCAL_DAY_2 = LocalDate.of(2024, 8, 31).atStartOfDay();
    private static final LocalDateTime NEXT_LOCAL_DAY_3 = LocalDate.of(2024, 9, 1).atStartOfDay();

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
        return localDateTimeToHoDateTime(localDateTime.plusDays(i));
    }

    @Test
    void daysFromNow_future() {
        // given
        final var future = HODateTime.now().plus(1, ChronoUnit.DAYS);

        // when
        final var result = HODateTime.daysFromNow(future, 1);

        // then
        assertThat(result).isGreaterThanOrEqualTo(ONE.setScale(1, RoundingMode.HALF_DOWN));
    }

    @Test
    void daysFromNow_past_approx() {
        // given
        final var past = HODateTime.now().minus(43200, ChronoUnit.SECONDS);

        // when
        final var result = HODateTime.daysFromNow(past, 5);

        // then
        assertThat(result).isLessThan(BigDecimal.valueOf(-0.49999).setScale(5, RoundingMode.HALF_DOWN));
    }

    @Test
    void daysToNow_future() {
        // given
        final var future = HODateTime.now().plus(1, ChronoUnit.DAYS);

        // when
        final var result = HODateTime.daysToNow(future, 1);

        // then
        assertThat(result).isLessThanOrEqualTo(ONE.negate().setScale(1, RoundingMode.HALF_DOWN));
    }

    @Test
    void daysToNow_past_approx() {
        // given
        final var past = HODateTime.now().minus(43200, ChronoUnit.SECONDS);

        // when
        final var result = HODateTime.daysToNow(past, 5);

        // then
        assertThat(result).isGreaterThan(BigDecimal.valueOf(0.49999).setScale(5, RoundingMode.HALF_DOWN));
    }

    @Test
    void daysBetween_with_now_results_zero() {
        // given
        final var now = HODateTime.now();

        // when
        final var result = HODateTime.daysBetween(now, now, 1);

        // then
        assertThat(result).isEqualByComparingTo(ZERO);
    }

    @Test
    void calculateDistanceInDays_toOneDayInFuture_results_one() {
        // given
        final var from = HODateTime.now();
        final var to = from.plus(1, ChronoUnit.DAYS);

        // when
        final var result = HODateTime.daysBetween(from, to, 1);

        // then
        assertThat(result).isEqualTo(ONE.setScale(1, RoundingMode.HALF_DOWN));
    }

    @Test
    void calculateDistanceInDays_toOneDayInPast_results_minusOne() {
        // given
        final var from = HODateTime.now();
        final var to = from.minus(1, ChronoUnit.DAYS);

        // when
        final var result = HODateTime.daysBetween(from, to, 1);

        // then
        assertThat(result).isEqualTo(ONE.negate().setScale(1, RoundingMode.HALF_UP));
    }

    @Test
    void calculateDistanceInDays_with_one_day_in_past_results_one() {
        // given
        final var from = HODateTime.now();
        final var to = from.plus(46530, ChronoUnit.SECONDS);

        // when
        final var result = HODateTime.daysBetween(from, to, 1);

        // then
        assertThat(result).isEqualTo(BigDecimal.valueOf(0.5).setScale(1, RoundingMode.HALF_UP));
    }

    @Test
    void calculateDistanceInDays_with_82080_secs_in_future_results_one() {
        // given
        final var from = HODateTime.now();
        final var to = from.plus(82080, ChronoUnit.SECONDS);

        // when
        final var result = HODateTime.daysBetween(from, to, 1);

        // then
        assertThat(result).isEqualTo(ONE.setScale(1, RoundingMode.HALF_UP));
    }

    @Test
    void calculateDistanceInDays_with_81216_secs_in_future_results_zero_point_nine() {
        // given
        final var from = HODateTime.now();
        final var to = from.plus(81216, ChronoUnit.SECONDS);

        // when
        final var result = HODateTime.daysBetween(from, to, 1);

        // then
        assertThat(result).isEqualTo(BigDecimal.valueOf(0.9).setScale(1, RoundingMode.HALF_UP));
    }

    static Stream<Arguments> getLocalDateTime() {
        return Stream.of(
                of(LocalDateTime.of(2024, 8, 29, 23, 59, 59)),
                of(LocalDateTime.of(2024, 8, 30, 0, 1, 0)),
                of(LocalDateTime.of(2024, 8, 30, 1, 2, 0)),
                of(LocalDateTime.of(2024, 8, 30, 2, 3, 0)),
                of(LocalDateTime.of(2024, 8, 30, 3, 4, 0)),
                of(LocalDateTime.of(2024, 8, 30, 4, 5)),
                of(LocalDateTime.of(2024, 8, 30, 5, 6, 0)),
                of(LocalDateTime.of(2024, 8, 30, 6, 7, 0)),
                of(LocalDateTime.of(2024, 8, 30, 7, 8, 0)),
                of(LocalDateTime.of(2024, 8, 30, 8, 9, 0)),
                of(LocalDateTime.of(2024, 8, 30, 9, 10)),
                of(LocalDateTime.of(2024, 8, 30, 10, 11, 14)),
                of(LocalDateTime.of(2024, 8, 30, 11, 12, 13)),
                of(LocalDateTime.of(2024, 8, 30, 12, 13, 12)),
                of(LocalDateTime.of(2024, 8, 30, 13, 14, 11)),
                of(LocalDateTime.of(2024, 8, 30, 14, 15, 10)),
                of(LocalDateTime.of(2024, 8, 30, 15, 16, 9)),
                of(LocalDateTime.of(2024, 8, 30, 16, 17, 8)),
                of(LocalDateTime.of(2024, 8, 30, 17, 18, 7)),
                of(LocalDateTime.of(2024, 8, 30, 18, 19, 6)),
                of(LocalDateTime.of(2024, 8, 30, 19, 20, 5)),
                of(LocalDateTime.of(2024, 8, 30, 20, 21, 4)),
                of(LocalDateTime.of(2024, 8, 30, 21, 22, 3)),
                of(LocalDateTime.of(2024, 8, 30, 22, 23, 2)),
                of(LocalDateTime.of(2024, 8, 30, 23, 24, 1)),
                of(LocalDateTime.of(2024, 8, 30, 23, 59, 59)),
                of(LocalDateTime.of(2024, 8, 31, 0, 30, 30))
        );
    }

    @ParameterizedTest
    @MethodSource
    void getLocalDateTime(LocalDateTime localDateTime) {
        final var hoDateTime = localDateTimeToHoDateTime(localDateTime);
        assertThat(hoDateTime.getLocalDateTime()).isEqualTo(localDateTime);
    }

    static Stream<Arguments> nextLocalDay() {
        return Stream.of(
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 29, 23, 59, 59)), NEXT_LOCAL_DAY_1),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 0, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 1, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 2, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 3, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 4, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 5, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 6, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 7, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 8, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 9, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 10, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 11, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 12, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 13, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 14, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 15, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 16, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 17, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 18, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 19, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 20, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 21, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 22, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 23, 0, 0)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 30, 23, 59, 59)), NEXT_LOCAL_DAY_2),
                of(localDateTimeToHoDateTime(LocalDateTime.of(2024, 8, 31, 0, 0, 0)), NEXT_LOCAL_DAY_3)
        );
    }

    @ParameterizedTest
    @MethodSource
    void nextLocalDay(HODateTime hoDateTime, LocalDateTime expected) {
        assertThat(hoDateTime.nextLocalDay().getLocalDateTime()).isEqualTo(expected);
    }

    private static HODateTime localDateTimeToHoDateTime(LocalDateTime localDateTime) {
        final var zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        return new HODateTime(zonedDateTime.toInstant());
    }
}
