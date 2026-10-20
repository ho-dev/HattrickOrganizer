package core.playername;

import lombok.Getter;

import java.util.Objects;

public final class PlayerNameSettingsManager {

    private static volatile PlayerNameSettingsManager instance;

    private final PlayerNameSettingsStore store;

    @Getter
    private volatile PlayerNameSettings settings;

    private PlayerNameSettingsManager() {
        store = new PlayerNameSettingsStore();
        settings = store.load();
    }

    public static synchronized void initialize() {
        if (instance == null) {
            instance = new PlayerNameSettingsManager();
        }
    }

    public static PlayerNameSettingsManager instance() {
        if (instance == null) {
            throw new IllegalStateException(
                "PlayerNameSettingsManager has not been initialized."
            );
        }
        return instance;
    }

    public NameFormat getFormat(Location location) {
        return settings.effectiveFormat(location);
    }

    public void apply(PlayerNameSettings newSettings) {
        Objects.requireNonNull(newSettings);

        store.save(newSettings);
        settings = newSettings;
    }
}
