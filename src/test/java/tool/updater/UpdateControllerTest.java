package tool.updater;

import core.HO;
import core.util.HOBuild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class UpdateControllerTest {

    private static Stream<Arguments> versionComparisons() {
        return Stream.of(
            // Major version takes precedence over minor version and build number
            Arguments.of("10.99.999.2", "11.0.0.0", true),
            Arguments.of("11.0.0.0", "10.99.999.2", false),
            Arguments.of("11.0.999.2", "12.0.0.0", true),
            Arguments.of("12.0.0.0", "11.99.999.2", false),

            // Minor version takes precedence over build number
            Arguments.of("11.0.999.2", "11.1.0.0", true),
            Arguments.of("11.1.0.0", "11.0.999.2", false),
            Arguments.of("11.1.999.2", "11.2.0.0", true),
            Arguments.of("11.2.0.0", "11.1.999.2", false),

            // Multi-digit minor versions must be compared numerically
            Arguments.of("10.9.999.2", "10.10.0.0", true),
            Arguments.of("10.10.0.0", "10.9.999.2", false),
            Arguments.of("10.1.999.2", "10.10.0.0", true),
            Arguments.of("10.10.0.0", "10.1.999.2", false),
            Arguments.of("10.10.999.2", "10.11.0.0", true),
            Arguments.of("10.11.0.0", "10.10.999.2", false),

            // Build number determines order when major and minor are equal
            Arguments.of("11.0.123.0", "11.0.124.0", true),
            Arguments.of("11.0.124.0", "11.0.123.0", false),
            Arguments.of("11.0.123.0", "11.0.123.0", false),
            Arguments.of("11.0.0.0", "11.0.1.0", true),
            Arguments.of("11.0.1.0", "11.0.0.0", false),

            // A local build has build number -1
            Arguments.of("11.0.-1.0", "11.0.0.0", true),
            Arguments.of("11.0.0.0", "11.0.-1.0", false),
            Arguments.of("11.0.-1.0", "11.0.-1.0", false),

            // Stage does not affect the comparison
            Arguments.of("11.0.123.0", "11.0.123.1", false),
            Arguments.of("11.0.123.1", "11.0.123.2", false),
            Arguments.of("11.0.123.2", "11.0.123.0", false),
            Arguments.of("11.0.123.2", "11.0.124.0", true),
            Arguments.of("11.0.123.0", "11.0.124.2", true),
            Arguments.of("11.0.124.0", "11.0.123.2", false),

            // Zero versions and build boundaries
            Arguments.of("0.0.0.0", "0.0.1.0", true),
            Arguments.of("0.0.1.0", "0.0.0.0", false),
            Arguments.of("11.0.2147483646.2", "11.0.2147483647.2", true),
            Arguments.of("11.0.2147483647.2", "11.0.2147483646.2", false)
        );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @ParameterizedTest(name = "Current: {0}, available: {1}, newer: {2}")
    @MethodSource("versionComparisons")
    void compareToCurrentVersions(
        String currentVersion,
        String availableVersion,
        boolean expected
    ) {
        final var currentBuild = HOBuild.parseFromImplementationVersion(currentVersion);

        final var availableVersionInfo = new VersionInfo();
        availableVersionInfo.setAllButReleaseDate(availableVersion);

        try (MockedStatic<HO> mockedHO = mockStatic(HO.class)) {
            mockedHO.when(HO::getHoBuild).thenReturn(currentBuild);

            assertThat(UpdateController.compareToCurrentVersions(availableVersionInfo)).isEqualTo(expected);
        }
    }

    @Test
    void compareToCurrentVersions_nullVersion() {
        assertThat(UpdateController.compareToCurrentVersions(null)).isFalse();
    }
}
