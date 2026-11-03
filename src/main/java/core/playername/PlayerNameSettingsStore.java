package core.playername;

import core.model.HOConfigurationStringParameter;

import java.util.EnumMap;
import java.util.Optional;
import java.util.stream.Stream;

public final class PlayerNameSettingsStore {

    private static final String PREFIX = "playerNames.";
    private static final String INHERIT = "INHERIT";

    private final HOConfigurationStringParameter defaultFormatParameter;

    private final EnumMap<Location, HOConfigurationStringParameter> overrideParameters = new EnumMap<>(Location.class);

    public PlayerNameSettingsStore() {
        final var defaults = PlayerNameSettings.defaults();

        defaultFormatParameter = new HOConfigurationStringParameter(PREFIX + "defaultFormat", defaults.defaultFormat().name());

        Stream.of(Location.values()).forEach(location -> {
            final String defaultValue = defaults.findOverride(location).map(Enum::name).orElse(INHERIT);
            overrideParameters.put(location, new HOConfigurationStringParameter(PREFIX + location.name(), defaultValue));
        });
    }

    public PlayerNameSettings load() {
        PlayerNameFormat defaultFormat = parseFormat(defaultFormatParameter.getValue())
            .orElseGet(() -> PlayerNameSettings.defaults().defaultFormat());

        EnumMap<Location, PlayerNameFormat> overrides = new EnumMap<>(Location.class);

        overrideParameters.forEach((location, parameter) -> {
            String value = parameter.getValue();

            if (value != null && !INHERIT.equals(value)) {
                parseFormat(value).ifPresent(format -> overrides.put(location, format));
            }
        });

        return new PlayerNameSettings(defaultFormat, overrides);
    }

    public void save(PlayerNameSettings settings) {
        defaultFormatParameter.setValue(settings.defaultFormat().name());

        overrideParameters.forEach((location, parameter) ->
            parameter.setValue(settings.findOverride(location).map(Enum::name).orElse(INHERIT))
        );
    }

    private static Optional<PlayerNameFormat> parseFormat(String value) {
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(PlayerNameFormat.valueOf(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
