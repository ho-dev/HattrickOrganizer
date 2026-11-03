package core.playername;

import java.util.*;

public record PlayerNameSettings(PlayerNameFormat defaultFormat, Map<Location, PlayerNameFormat> overrides) {

    private static final PlayerNameSettings DEFAULTS = new PlayerNameSettings(
        PlayerNameFormat.HATTRICK,
        Map.ofEntries(
            Map.entry(Location.MATCHES_PLAYER_DETAILS, PlayerNameFormat.FULL_LAST_FIRST)
        )
    );

    public PlayerNameSettings {
        Objects.requireNonNull(defaultFormat, "defaultFormat");
        Objects.requireNonNull(overrides, "overrides");

        EnumMap<Location, PlayerNameFormat> copy = new EnumMap<>(Location.class);

        overrides.forEach((location, format) ->
            copy.put(Objects.requireNonNull(location), Objects.requireNonNull(format)));

        overrides = Collections.unmodifiableMap(copy);
    }

    public static PlayerNameSettings defaults() {
        return DEFAULTS;
    }

    public PlayerNameFormat effectiveFormat(Location location) {
        return overrides.getOrDefault(Objects.requireNonNull(location), defaultFormat);
    }

    public Optional<PlayerNameFormat> findOverride(Location location) {
        return Optional.ofNullable(overrides.get(Objects.requireNonNull(location)));
    }
}
