package core.util;

import java.util.Optional;

public enum HOEnvironmentVariable {
    HO_SAVE_DOWNLOADED_XML,
    ;

    public Optional<String> value() {
        return Optional.ofNullable(System.getenv(name()));
    }

    public Optional<Boolean> valueAsBoolean() {
        return value().map(HOEnvironmentVariable::parseBoolean);
    }

    static boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value) || "1".equals(value);
    }
}
