package core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HOVersionTest {

    private static Stream<Arguments> invalidVersionNumbers() {
        return Stream.of(
            Arguments.of(-1, 0, 0, "major"),
            Arguments.of(0, -1, 0, "minor"),
            Arguments.of(0, 0, -2, "buildNumber")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidVersionNumbers")
    void constructorRejectsInvalidVersionNumbers(
        int major,
        int minor,
        int buildNumber,
        String expectedMessage
    ) {
        assertThatThrownBy(() ->
            new HOVersion(major, minor, buildNumber, HOVersion.Stage.DEV))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(expectedMessage);
    }

    @Test
    void constructorRejectsNullStage() {
        assertThatThrownBy(() -> new HOVersion(11, 0, 123, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("stage must not be null");
    }

    private static Stream<Arguments> parseFromImplementationVersion() {
        return Stream.of(
            Arguments.of("11.0.-1.0", new HOVersion(11, 0, -1, HOVersion.Stage.DEV)),
            Arguments.of("11.0.-1.1", new HOVersion(11, 0, -1, HOVersion.Stage.BETA)),
            Arguments.of("11.0.-1.2", new HOVersion(11, 0, -1, HOVersion.Stage.STABLE)),
            Arguments.of("10.0.0.0", new HOVersion(10, 0, 0, HOVersion.Stage.DEV)),
            Arguments.of("10.0.0.1", new HOVersion(10, 0, 0, HOVersion.Stage.BETA)),
            Arguments.of("10.0.0.2", new HOVersion(10, 0, 0, HOVersion.Stage.STABLE)),
            Arguments.of("10.0.1.0", new HOVersion(10, 0, 1, HOVersion.Stage.DEV)),
            Arguments.of("10.0.1.1", new HOVersion(10, 0, 1, HOVersion.Stage.BETA)),
            Arguments.of("10.0.1.2", new HOVersion(10, 0, 1, HOVersion.Stage.STABLE)),
            Arguments.of("10.2.0.0", new HOVersion(10, 2, 0, HOVersion.Stage.DEV)),
            Arguments.of("10.2.0.1", new HOVersion(10, 2, 0, HOVersion.Stage.BETA)),
            Arguments.of("10.2.0.2", new HOVersion(10, 2, 0, HOVersion.Stage.STABLE)),
            Arguments.of("10.2.3.0", new HOVersion(10, 2, 3, HOVersion.Stage.DEV)),
            Arguments.of("10.2.3.1", new HOVersion(10, 2, 3, HOVersion.Stage.BETA)),
            Arguments.of("10.2.3.2", new HOVersion(10, 2, 3, HOVersion.Stage.STABLE)),
            Arguments.of("1.4.3.2", new HOVersion(1, 4, 3, HOVersion.Stage.STABLE)),
            Arguments.of("0.0.0.0", new HOVersion(0, 0, 0, HOVersion.Stage.DEV))
        );
    }

    @ParameterizedTest
    @MethodSource
    void parseFromImplementationVersion(String implementationVersion, HOVersion expected) {
        final var hoVersion = HOVersion.parseFromImplementationVersion(implementationVersion);
        assertThat(hoVersion).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("parseFromImplementationVersion")
    void getImplementationVersionStr(String expected, HOVersion version) {
        assertThat(version.getImplementationVersionStr()).isEqualTo(expected);
    }

    private static Stream<Arguments> parseFromImplementationVersion_throwsIllegalArgumentException() {
        return Stream.of(
            Arguments.of("1..3.0", "Cannot parse integer from ''"),
            Arguments.of("1.2..0", "Cannot parse integer from ''"),
            Arguments.of("1.2.3.", "Cannot parse integer from ''"),
            Arguments.of("1.2.3.0.", "4 parts expected but got 5 from '1.2.3.0.'"),
            Arguments.of("", "4 parts expected but got 0 from ''"),
            Arguments.of(" ", "4 parts expected but got 1 from ' '"),
            Arguments.of("1", "4 parts expected but got 1 from '1'"),
            Arguments.of("1.2", "4 parts expected but got 2 from '1.2'"),
            Arguments.of("1.2.3", "4 parts expected but got 3 from '1.2.3'"),
            Arguments.of("1.2.3.4", "4 is not a valid id for a Stage"),
            Arguments.of("I.4.3.2", "Cannot parse integer from 'I'"),
            Arguments.of("1.2.3.4.5", "4 parts expected but got 5 from '1.2.3.4.5'"),
            Arguments.of(null, "null is not allowed for implementationVersion")
        );
    }

    @ParameterizedTest
    @MethodSource
    void parseFromImplementationVersion_throwsIllegalArgumentException(String implementationVersion, String expectedMessage) {
        assertThatThrownBy(() -> HOVersion.parseFromImplementationVersion(implementationVersion))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(expectedMessage);
    }

    private static Stream<Arguments> versionStrings() {
        return Stream.of(
            Arguments.of(new HOVersion(11, 0, -1, HOVersion.Stage.DEV), "11.0 DEV (local build)", "11.0 DEV (r-1)"),
            Arguments.of(new HOVersion(11, 0, -1, HOVersion.Stage.BETA), "11.0 BETA (local build)", "11.0 BETA (r-1)"),
            Arguments.of(new HOVersion(11, 0, -1, HOVersion.Stage.STABLE), "11.0 (local build)", "11.0"),
            Arguments.of(new HOVersion(11, 0, 0, HOVersion.Stage.DEV), "11.0 DEV (build 0)", "11.0 DEV (r0)"),
            Arguments.of(new HOVersion(11, 0, 0, HOVersion.Stage.BETA), "11.0 BETA (build 0)", "11.0 BETA (r0)"),
            Arguments.of(new HOVersion(11, 0, 0, HOVersion.Stage.STABLE), "11.0 (build 0)", "11.0"),
            Arguments.of(new HOVersion(11, 0, 123, HOVersion.Stage.DEV), "11.0 DEV (build 123)", "11.0 DEV (r123)"),
            Arguments.of(new HOVersion(11, 0, 123, HOVersion.Stage.BETA), "11.0 BETA (build 123)", "11.0 BETA (r123)"),
            Arguments.of(new HOVersion(11, 0, 123, HOVersion.Stage.STABLE), "11.0 (build 123)", "11.0")
        );
    }

    @ParameterizedTest
    @MethodSource("versionStrings")
    void getVersionString(HOVersion version, String expected, String ignored) {
        assertThat(version.getVersionString()).isEqualTo(expected);
    }

    @SuppressWarnings("deprecation")
    @ParameterizedTest
    @MethodSource("versionStrings")
    void getHistoricalVersionString(HOVersion version, String ignored, String expected) {
        assertThat(version.getHistoricalVersionString()).isEqualTo(expected);
    }

    private static Stream<Arguments> shortVersions() {
        return Stream.of(
            Arguments.of(new HOVersion(0, 0, -1, HOVersion.Stage.DEV), "0.0", 0.0),
            Arguments.of(new HOVersion(11, 0, 0, HOVersion.Stage.DEV), "11.0", 11.0),
            Arguments.of(new HOVersion(11, 2, 123, HOVersion.Stage.STABLE), "11.2", 11.2),
            Arguments.of(new HOVersion(1, 4, 3, HOVersion.Stage.BETA), "1.4", 1.4)
        );
    }

    @ParameterizedTest
    @MethodSource("shortVersions")
    void getVersionShort(HOVersion version, String expected, double ignored) {
        assertThat(version.getVersionShort()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shortVersions")
    void getVersionShortAsDouble(HOVersion version, String ignored, double expected) {
        assertThat(version.getVersionShortAsDouble()).isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(HOVersion.Stage.class)
    void stagePredicates(HOVersion.Stage stage) {
        final var version = new HOVersion(11, 0, 123, stage);

        assertThat(version.isDevelopment()).isEqualTo(stage == HOVersion.Stage.DEV);
        assertThat(version.isBeta()).isEqualTo(stage == HOVersion.Stage.BETA);
        assertThat(version.isRelease()).isEqualTo(stage == HOVersion.Stage.STABLE);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 123})
    void isLocalBuild(int buildNumber) {
        final var version = new HOVersion(11, 0, buildNumber, HOVersion.Stage.DEV);

        assertThat(version.isLocalBuild()).isEqualTo(buildNumber == -1);
    }

    private static Stream<Arguments> stageConstants() {
        return Stream.of(
            Arguments.of(HOVersion.Stage.DEV, 0, "DEV"),
            Arguments.of(HOVersion.Stage.BETA, 1, "BETA"),
            Arguments.of(HOVersion.Stage.STABLE, 2, "")
        );
    }

    @ParameterizedTest
    @MethodSource("stageConstants")
    void stageConstants(HOVersion.Stage stage, int expectedId, String expectedText) {
        assertThat(stage.getId()).isEqualTo(expectedId);
        assertThat(stage.getText()).isEqualTo(expectedText);
    }

    @ParameterizedTest
    @MethodSource("stageConstants")
    void stageFromId(HOVersion.Stage expected, int id, String ignored) {
        assertThat(HOVersion.Stage.fromId(id)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 3, 4, 100})
    void stageFromId_throwsIllegalArgumentException(int id) {
        assertThatThrownBy(() -> HOVersion.Stage.fromId(id))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("%d is not a valid id for a Stage".formatted(id));
    }

    @Test
    @SuppressWarnings("deprecation")
    void getRevisionNumber() {
        final var version = new HOVersion(11, 0, 123, HOVersion.Stage.DEV);

        assertThat(version.getRevisionNumber()).isEqualTo(123);
    }
}
