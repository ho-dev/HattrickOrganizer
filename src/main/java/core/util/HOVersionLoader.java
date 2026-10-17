package core.util;

import core.BuildInfo;
import core.HO;
import lombok.Getter;

import java.util.Optional;

public final class HOVersionLoader {

    private static final HOLogger log = HOLogger.instance();

    @Getter
    private static final HOVersion version = createHoVersion();

    private HOVersionLoader() {
    }

    private static HOVersion createHoVersion() {
        try {
            return Optional.ofNullable(HO.class.getPackage().getImplementationVersion())
                .map(HOVersionLoader::createHoVersionFromImplementationVersion)
                .orElseGet(HOVersionLoader::createHoVersionFromBuildInfo);
        } catch (RuntimeException e) {
            log.error(HOVersionLoader.class, "Cannot create HOVersion from implementation version and build info: " + e);
            return new HOVersion(0, 0, 0, HOVersion.Stage.DEV);
        }
    }

    private static HOVersion createHoVersionFromImplementationVersion(String implementationVersion) {
        if (implementationVersion.matches("\\d+\\.\\d+")) {
            final var newImplementationVersion = createVersionStringForLocalBuild(implementationVersion);
            log.debug(HOVersionLoader.class,
                "Adjusted implementation version '%s' to '%s'.".formatted(implementationVersion, newImplementationVersion));
            return HOVersion.parseFromImplementationVersion(newImplementationVersion);
        }
        return HOVersion.parseFromImplementationVersion(implementationVersion);
    }

    private static String createVersionStringForLocalBuild(String majorMinor) {
        var localBuildNumber = -1;
        return "%s.%d.%s".formatted(majorMinor, localBuildNumber, BuildInfo.devStageStr());
    }

    private static HOVersion createHoVersionFromBuildInfo() {
        final String syntheticImplementationVersion = createVersionStringForLocalBuild(BuildInfo.versionStr());
        log.warning(HOVersionLoader.class, "Implementation-Version not found in manifest. Expected when launched from an IDE; otherwise, check build configuration.");
        return HOVersion.parseFromImplementationVersion(syntheticImplementationVersion);
    }
}
