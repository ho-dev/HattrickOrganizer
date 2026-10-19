package core.playername;

import core.model.match.MatchLineupPosition;
import core.model.player.Player;
import module.matches.MatchesModel;

import java.util.Optional;

public final class PlayerNameUtils {

    private PlayerNameUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
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

    public static PlayerName createPlayerName(MatchLineupPosition matchLineupPosition) {
        return new PlayerName() {
            @Override
            public int getPlayerId() {
                return matchLineupPosition.getPlayerId();
            }

            @Override
            public String getFirstName() {
                return matchLineupPosition.getFirstName();
            }

            @Override
            public String getNickName() {
                return matchLineupPosition.getNickName();
            }

            @Override
            public String getLastName() {
                return matchLineupPosition.getLastName();
            }
        };
    }
}
