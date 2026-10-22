package core.model.misc;

import core.util.HODateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EconomyTest {

    private static final HODateTime HISTORICAL_DATE = HODateTime.fromHT("2008-02-24 23:59:59");

    private static final HODateTime CURRENT_DATE = HODateTime.fromHT("2008-02-25 00:00:00");

    @ParameterizedTest
    @MethodSource("historicalFanLevels")
    void mapsHistoricalFanLevelsToCurrentFanLevels(int historicalLevel, int expectedLevel) {
        assertThat(Economy.mapHistoricalLevelFansToLevelFans(historicalLevel, HISTORICAL_DATE)).isEqualTo(expectedLevel);
    }

    private static Stream<Arguments> historicalFanLevels() {
        return Stream.of(
            Arguments.of(Economy.LV_SPONSORS_SENDING_LOVE_POEMS_TO_YOU, Economy.LV_FANS_SENDING_LOVE_POEMS_TO_YOU),
            Arguments.of(Economy.LV_SPONSORS_DANCING_IN_THE_STREETS, Economy.LV_FANS_DANCING_IN_THE_STREETS),
            Arguments.of(Economy.LV_SPONSORS_HIGH_ON_LIFE, Economy.LV_FANS_HIGH_ON_LIFE),
            Arguments.of(Economy.LV_SPONSORS_DELIRIOUS, Economy.LV_FANS_DELIRIOUS),
            Arguments.of(Economy.LV_SPONSORS_SATISFIED, Economy.LV_FANS_SATISFIED),
            Arguments.of(Economy.LV_SPONSORS_CONTENT, Economy.LV_FANS_CONTENT),
            Arguments.of(Economy.LV_SPONSORS_CALM, Economy.LV_FANS_CALM),
            Arguments.of(Economy.LV_SPONSORS_IRRITATED, Economy.LV_FANS_IRRITATED),
            Arguments.of(Economy.LV_SPONSORS_FURIOUS, Economy.LV_FANS_FURIOUS),
            Arguments.of(Economy.LV_SPONSORS_MURDEROUS, Economy.LV_FANS_MURDEROUS)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "2008-02-24 23:59:59, 11",
        "2008-02-25 00:00:00, 9",
        "2008-02-25 00:00:01, 9"
    })
    void usesCorrectFanLevelScaleForDate(String date, int expectedLevel) {
        assertThat(Economy.mapHistoricalLevelFansToLevelFans(Economy.LV_SPONSORS_SENDING_LOVE_POEMS_TO_YOU, HODateTime.fromHT(date))).isEqualTo(expectedLevel);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 5, 9, 11})
    void returnsCurrentFanLevelsUnchanged(int level) {
        assertThat(Economy.mapHistoricalLevelFansToLevelFans(level, CURRENT_DATE)).isEqualTo(level);
    }

    @Test
    void returnsFanLevelUnchangedWhenDateIsNull() {
        assertThat(Economy.mapHistoricalLevelFansToLevelFans(Economy.LV_FANS_SENDING_LOVE_POEMS_TO_YOU, null)).isEqualTo(Economy.LV_FANS_SENDING_LOVE_POEMS_TO_YOU);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 10, 11, 12})
    void throwsExceptionForInvalidHistoricalFanLevels(int level) {
        assertThatThrownBy(() -> Economy.mapHistoricalLevelFansToLevelFans(level, HISTORICAL_DATE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No mapping for historical level of fans " + level);
    }
}
