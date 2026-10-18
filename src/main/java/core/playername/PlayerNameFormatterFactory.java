package core.playername;

import core.model.match.MatchLineupPosition;
import core.model.player.Player;

import java.util.List;

public final class PlayerNameFormatterFactory {

    public static PlayerNameFormatter createFromPlayers(List<Player> players) {
        return new PlayerNameFormatter(players.stream().map(PlayerNameUtils::createPlayerName).toList());
    }

    public static PlayerNameFormatter createFromMatchLineupPositions(List<MatchLineupPosition> matchLineupPositions) {
        return new PlayerNameFormatter(matchLineupPositions.stream().map(PlayerNameUtils::createPlayerName).toList());
    }
}
