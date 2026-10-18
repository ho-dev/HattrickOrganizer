package core.playername;

import core.model.player.Player;

import java.util.List;

public final class PlayerNameUtils {

    private PlayerNameUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static PlayerNameFormatter createPlayerNameFormatter(List<Player> players) {
        return new PlayerNameFormatter(players.stream().map(PlayerNameUtils::createPlayerName).toList());
    }

    public static PlayerName createPlayerName(Player player) {
        return new PlayerName() {
            @Override
            public int getPlayerId() {
                return player.getPlayerId();
            }

            @Override
            public String getFirstName() {
                return player.getFirstName();
            }

            @Override
            public String getNickName() {
                return player.getNickName();
            }

            @Override
            public String getLastName() {
                return player.getLastName();
            }
        };
    }
}
