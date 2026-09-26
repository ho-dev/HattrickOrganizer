package core.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HOEnvironmentVariableTest {

    private static Stream<Arguments> parseBooleanReturnsExpectedValue() {
        return Stream.of(
            Arguments.of("true", true),
            Arguments.of("TRUE", true),
            Arguments.of("True", true),
            Arguments.of("tRuE", true),
            Arguments.of("1", true),
            Arguments.of(null, false),
            Arguments.of("", false),
            Arguments.of("false", false),
            Arguments.of("FALSE", false),
            Arguments.of("0", false),
            Arguments.of("yes", false),
            Arguments.of("Y", false),
            Arguments.of("T", false),
            Arguments.of("foo", false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void parseBooleanReturnsExpectedValue(String value, boolean expected) {
        assertThat(HOEnvironmentVariable.parseBoolean(value)).isEqualTo(expected);
    }
}
