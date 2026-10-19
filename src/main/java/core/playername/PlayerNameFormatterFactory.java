package core.playername;

import core.model.match.MatchLineup;
import core.model.match.MatchLineupPosition;
import core.model.player.Player;
import core.util.StreamUtils;

import java.util.List;

public final class PlayerNameFormatterFactory {

    private PlayerNameFormatterFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static PlayerNameFormatter createFromPlayers(List<Player> players) {
        return new PlayerNameFormatter(players.stream().map(PlayerNameUtils::createPlayerName).toList());
    }

    public static PlayerNameFormatter createFrom(MatchLineup matchLineup) {
        return createFromMatchLineupPositions(getAllMatchLineupPositions(matchLineup));
    }

    public static PlayerNameFormatter createFromMatchLineupPositions(List<MatchLineupPosition> matchLineupPositions) {
        return new PlayerNameFormatter(matchLineupPositions.stream().map(PlayerNameUtils::createPlayerName).toList());
    }

    private static List<MatchLineupPosition> getAllMatchLineupPositions(MatchLineup matchLineup) {
        return StreamUtils.concat(
                matchLineup.getHomeTeam().getLineup().getAllPositions().stream(),
                matchLineup.getGuestTeam().getLineup().getAllPositions().stream())
            .toList();
    }
}
