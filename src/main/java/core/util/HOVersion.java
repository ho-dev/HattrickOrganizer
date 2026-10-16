package core.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public record HOVersion(
    int major,
    int minor,
    int buildNumber,
    Stage stage
) {
    public HOVersion {
        Objects.requireNonNull(stage, "stage must not be null");

        if (major < 0 ) {
            throw new IllegalArgumentException("major version number must not be negative");
        }
        if (minor < 0 ) {
            throw new IllegalArgumentException("minor version number must not be negative");
        }
        if (buildNumber < -1) {
            throw new IllegalArgumentException("buildNumber must be -1 (local build) or non-negative");
        }
    }

    public boolean isDevelopment() {
        return stage == Stage.DEV;
    }

    public boolean isBeta() {
        return stage == Stage.BETA;
    }

    public boolean isRelease() {
        return stage == Stage.STABLE;
    }

    public boolean isLocalBuild() {
        return buildNumber == -1;
    }

    public String getVersionShort() {
        return "%s.%s".formatted(major, minor);
    }

    public double getVersionShortAsDouble() {
        return Double.parseDouble(getVersionShort());
    }

    @Deprecated(since = "10.0")
    public int getRevisionNumber() {
        return buildNumber;
    }

    public String getVersionString() {
        final String versionShort = getVersionShort();
        final String buildNumberStr = isLocalBuild() ? "(local build)" : "(build %d)".formatted(buildNumber);

        return switch (stage) {
            case DEV, BETA -> "%s %s %s".formatted(versionShort, stage.getText(), buildNumberStr);
            case STABLE -> "%s %s".formatted(versionShort, buildNumberStr);
        };
    }

    public String getImplementationVersionStr() {
        return "%d.%d.%d.%d".formatted(major, minor, buildNumber, stage.getId());
    }

    public static HOVersion parseFromImplementationVersion(String implementationVersion) {
        if (implementationVersion == null) {
            throw new IllegalArgumentException("null is not allowed for implementationVersion");
        }

        final var parts = StringUtils.splitPreserveAllTokens(implementationVersion, ".");
        if (parts.length != 4) {
            throw new IllegalArgumentException("4 parts expected but got %d from '%s'".formatted(parts.length, implementationVersion));
        }

        final int major = parseInt(parts[0]);
        final int minor = parseInt(parts[1]);
        final int buildNumber = parseInt(parts[2]);
        final int stageId = parseInt(parts[3]);
        final var stage = Stage.fromId(stageId);
        return new HOVersion(major, minor, buildNumber, stage);
    }

    private static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot parse integer from '%s'".formatted(str));
        }
    }

    @RequiredArgsConstructor
    @Getter
    public enum Stage {
        DEV(0, "DEV"),
        BETA(1, "BETA"),
        STABLE(2, "");

        private final int id;
        private final String text;

        private static final Map<Integer, Stage> MAP_ID_TO_STAGE =
            Stream.of(values()).collect(toMap(Stage::getId, Function.identity()));

        public static Stage fromId(int id) {
            return Optional.ofNullable(MAP_ID_TO_STAGE.get(id))
                .orElseThrow(() -> new IllegalArgumentException("%d is not a valid id for a Stage".formatted(id)));
        }
    }
}
