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
        defaultFormatParameter = new HOConfigurationStringParameter(PREFIX + "defaultFormat", NameFormat.HATTRICK.name());

        Stream.of(Location.values()).forEach(location ->
            overrideParameters.put(location, new HOConfigurationStringParameter(PREFIX + location.name(), INHERIT))
        );
    }

    public PlayerNameSettings load() {
        NameFormat defaultFormat = parseFormat(defaultFormatParameter.getValue()).orElse(NameFormat.HATTRICK);

        EnumMap<Location, NameFormat> overrides = new EnumMap<>(Location.class);

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

        overrideParameters.forEach((location, parameter) -> {
            NameFormat format = settings.getOverride(location);

            parameter.setValue(
                format != null ? format.name() : INHERIT
            );
        });
    }

    private static Optional<NameFormat> parseFormat(String value) {
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(NameFormat.valueOf(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
