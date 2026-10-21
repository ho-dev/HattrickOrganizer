package core.playername;

import java.util.Objects;

public record ConfiguredPlayerNameFormatter(PlayerNameFormatter formatter, NameFormat format) {

    public ConfiguredPlayerNameFormatter {
        Objects.requireNonNull(formatter);
        Objects.requireNonNull(format);
    }

    public String format(int playerId) {
        return formatter.format(playerId, format);
    }

    public String format(PlayerName player) {
        return formatter.format(player, format);
    }
}
