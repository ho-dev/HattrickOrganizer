package module.youth;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

class YouthPlayerTest {

    @Test
    void constant_PLAYER_NUMBER_NOT_SET_STRING() {
        assertThat(YouthPlayer.PLAYER_NUMBER_NOT_SET_STRING).isEqualTo("100");
    }

    @Test
    void constant_MIN_PLAYER_NUMBER() {
        assertThat(YouthPlayer.MIN_PLAYER_NUMBER).isEqualTo(1);
    }

    @Test
    void constant_MAX_PLAYER_NUMBER() {
        assertThat(YouthPlayer.MAX_PLAYER_NUMBER).isEqualTo(99);
    }

    private static Stream<Arguments> getPlayerNumberAsInt() {
        return Stream.of(
            of(null, null),
            of(StringUtils.EMPTY, null),
            of(StringUtils.SPACE, null),
            of("A", null),
            of("z", null),
            of("-1", null),
            of("0", null),
            of("1 ", null),
            of(" 1", null),
            of(" 1 ", null),
            of("1", 1),
            of("10", 10),
            of("99", 99),
            of("100", null),
            of("101", null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void getPlayerNumberAsInt(String playerNumberAsString, Integer expectedResult) {
        // given
        final YouthPlayer player = new YouthPlayer();
        player.setPlayerNumber(playerNumberAsString);
        final var expectedOptionalResult = Optional.ofNullable(expectedResult);

        // when
        final var optionalResult = player.getPlayerNumberAsInt();

        // then
        assertThat(optionalResult).isEqualTo(expectedOptionalResult);
    }
}
