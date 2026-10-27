package core.util;

import core.BuildInfo;
import core.HO;
import lombok.Getter;

import java.util.Optional;

public final class HOBuildLoader {

    private static final HOLogger log = HOLogger.instance();

    @Getter
    private static final HOBuild build = createBuild();

    private HOBuildLoader() {
    }

    private static HOBuild createBuild() {
        try {
            return Optional.ofNullable(HO.class.getPackage().getImplementationVersion())
                .map(HOBuildLoader::createBuildFromImplementationVersion)
                .orElseGet(HOBuildLoader::createBuildFromBuildInfo);
        } catch (RuntimeException e) {
            log.error(HOBuildLoader.class, "Cannot create HOBuild from implementation version and build info: " + e);
            return new HOBuild(new HOVersion(0, 0), 0, HOBuild.Stage.DEV);
        }
    }

    private static HOBuild createBuildFromImplementationVersion(String implementationVersion) {
        if (implementationVersion.matches("\\d+\\.\\d+")) {
            final var newImplementationVersion = createImplementationVersionForLocalBuild(implementationVersion);
            log.debug(HOBuildLoader.class,
                "Adjusted implementation version '%s' to '%s'.".formatted(implementationVersion, newImplementationVersion));
            return HOBuild.parseFromImplementationVersion(newImplementationVersion);
        }
        return HOBuild.parseFromImplementationVersion(implementationVersion);
    }

    private static String createImplementationVersionForLocalBuild(String majorMinor) {
        var localBuildNumber = -1;
        return "%s.%d.%s".formatted(majorMinor, localBuildNumber, BuildInfo.devStageStr());
    }

    private static HOBuild createBuildFromBuildInfo() {
        final String syntheticImplementationVersion = createImplementationVersionForLocalBuild(BuildInfo.versionStr());
        log.warning(HOBuildLoader.class, "Implementation-Version not found in manifest. Expected when launched from an IDE; otherwise, check build configuration.");
        return HOBuild.parseFromImplementationVersion(syntheticImplementationVersion);
    }
}
