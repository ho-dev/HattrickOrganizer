package core.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HOVersionTest {

    private static final class CompareResult {
        public static final int SMALLER = -1;
        public static final int EQUAL = 0;
        public static final int GREATER = 1;
    }

    private static Stream<Arguments> versionComparisons() {
        return Stream.of(
            Arguments.of(new HOVersion(10, 9), new HOVersion(10, 10), CompareResult.SMALLER),
            Arguments.of(new HOVersion(10, 10), new HOVersion(10, 9), CompareResult.GREATER),
            Arguments.of(new HOVersion(10, 10), new HOVersion(10, 10), CompareResult.EQUAL),
            Arguments.of(new HOVersion(10, 99), new HOVersion(11, 0), CompareResult.SMALLER),
            Arguments.of(new HOVersion(11, 0), new HOVersion(10, 99), CompareResult.GREATER),
            Arguments.of(new HOVersion(11, 0), new HOVersion(11, 1), CompareResult.SMALLER),
            Arguments.of(new HOVersion(11, 1), new HOVersion(11, 0), CompareResult.GREATER),
            Arguments.of(new HOVersion(0, 0), new HOVersion(0, 0), CompareResult.EQUAL)
        );
    }

    @ParameterizedTest
    @MethodSource("versionComparisons")
    void compareTo(HOVersion version, HOVersion other, int expected) {
        assertThat(Integer.signum(version.compareTo(other))).isEqualTo(expected);
    }

    private static Stream<Arguments> invalidVersions() {
        return Stream.of(
            Arguments.of(-1, 0),
            Arguments.of(0, -1),
            Arguments.of(-1, -1),
            Arguments.of(Integer.MIN_VALUE, 0),
            Arguments.of(0, Integer.MIN_VALUE)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidVersions")
    void rejectsInvalidVersions(int major, int minor) {
        assertThatThrownBy(() -> new HOVersion(major, minor)).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> versionStrings() {
        return Stream.of(
            Arguments.of(new HOVersion(0, 0), "0.0"),
            Arguments.of(new HOVersion(10, 9), "10.9"),
            Arguments.of(new HOVersion(10, 10), "10.10"),
            Arguments.of(new HOVersion(11, 0), "11.0"),
            Arguments.of(new HOVersion(11, 123), "11.123")
        );
    }

    @ParameterizedTest
    @MethodSource("versionStrings")
    void getVersionStr(HOVersion version, String expected) {
        assertThat(version.getVersionStr()).isEqualTo(expected);
    }
}
