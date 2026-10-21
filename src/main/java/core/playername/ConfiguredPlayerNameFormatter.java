package core.playername;

import java.util.Objects;

public final class ConfiguredPlayerNameFormatter {

    private final PlayerNameFormatter formatter;
    private final NameFormat format;

    public ConfiguredPlayerNameFormatter(PlayerNameFormatter formatter, NameFormat format) {
        this.formatter = Objects.requireNonNull(formatter);
        this.format = Objects.requireNonNull(format);
    }

    public String format(int playerId) {
        return formatter.format(playerId, format);
    }

    public String format(PlayerName player) {
        return formatter.format(player, format);
    }
}
