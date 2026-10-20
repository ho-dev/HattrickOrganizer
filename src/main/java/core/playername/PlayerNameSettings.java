package core.playername;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public record PlayerNameSettings(NameFormat defaultFormat, Map<Location, NameFormat> overrides) {
    public PlayerNameSettings {
        Objects.requireNonNull(defaultFormat, "defaultFormat");
        Objects.requireNonNull(overrides, "overrides");

        EnumMap<Location, NameFormat> copy = new EnumMap<>(Location.class);

        overrides.forEach((location, format) ->
            copy.put(Objects.requireNonNull(location), Objects.requireNonNull(format)));

        overrides = Collections.unmodifiableMap(copy);
    }

    public static PlayerNameSettings defaults() {
        return new PlayerNameSettings(NameFormat.HATTRICK, Map.of());
    }

    public NameFormat effectiveFormat(Location location) {
        return overrides.getOrDefault(Objects.requireNonNull(location), defaultFormat);
    }

    public NameFormat getOverride(Location location) {
        return overrides.get(Objects.requireNonNull(location));
    }
}
