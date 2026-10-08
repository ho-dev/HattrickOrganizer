package module.matches;

import core.model.match.MatchEvent;
import core.model.match.MatchEventID;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public record MatchEventFactory(int homeTeamId, int guestTeamId) {

    private static final Set<MatchEventID> OWN_GOAL_EVENTS = Set.of(
        MatchEventID.SE_GOAL_UNPREDICTABLE_OWN_GOAL,
        MatchEventID.SE_NO_GOAL_UNPREDICTABLE_OWN_GOAL_ALMOST
    );

    public static MatchEventFactory of(int homeTeamId, int guestTeamId) {
        return new MatchEventFactory(homeTeamId, guestTeamId);
    }

    public MatchEventFactory {
        assertThat(homeTeamId).as("homeTeamId and guestTeamId must be different").isNotEqualTo(guestTeamId);
    }

    public MatchEvent createOwnGoalEvent(MatchEventID matchEventID, Player jinxPlayer, Player assistingPlayer) {
        assertThat(matchEventID).isIn(OWN_GOAL_EVENTS);
        return createTwoPlayerEvent(matchEventID, jinxPlayer, assistingPlayer);
    }

    public MatchEvent createGoalEventAssistedByOpponent(MatchEventID matchEventID, Player player, Player assistingPlayer) {
        assertThat(assistingPlayer.teamId())
            .as("assistingPlayer must play for the opposing team")
            .isNotEqualTo(player.teamId());
        return createTwoPlayerEvent(matchEventID, player, assistingPlayer);
    }

    public MatchEvent createSinglePlayerEvent(MatchEventID matchEventID, Player player) {
        checkPlaysForHomeOrGuestTeam("player", player);

        final var matchEvent = createMatchEvent(matchEventID);
        matchEvent.setMinute(90);
        setMatchEventPlayer(matchEvent, player);
        clearMatchEventAssistingPlayer(matchEvent);
        return matchEvent;
    }

    private MatchEvent createTwoPlayerEvent(
        MatchEventID matchEventID,
        Player player,
        Player assistingPlayer) {

        assertThat(player.playerId())
            .as("playerId must be different to assisting playerId")
            .isNotEqualTo(assistingPlayer.playerId());

        checkPlaysForHomeOrGuestTeam("player", player);
        checkPlaysForHomeOrGuestTeam("assistingPlayer", assistingPlayer);

        assertThat(player.playerName())
            .as("playerName must be distinguishable from assistingPlayer.playerName")
            .isNotEqualTo(assistingPlayer.playerName());

        final var matchEvent = createMatchEvent(matchEventID);
        matchEvent.setMinute(90);
        setMatchEventPlayer(matchEvent, player);
        setMatchEventAssistingPlayer(matchEvent, assistingPlayer);
        return matchEvent;
    }

    private void checkPlaysForHomeOrGuestTeam(String context, Player player) {
        assertThat(player.teamId())
            .as("%s should play for home or guest team".formatted(context))
            .isIn(homeTeamId, guestTeamId);
    }

    private void setMatchEventPlayer(MatchEvent matchEvent, Player player) {
        matchEvent.setTeamID(player.teamId());
        matchEvent.setPlayerName(player.playerName());
        matchEvent.setPlayerId(player.playerId());
        matchEvent.setSpielerHeim(player.teamId() == homeTeamId);
    }

    private void setMatchEventAssistingPlayer(MatchEvent matchEvent, Player assistingPlayer) {
        matchEvent.setAssistingPlayerName(assistingPlayer.playerName());
        matchEvent.setAssistingPlayerId(assistingPlayer.playerId());
        matchEvent.setGehilfeHeim(assistingPlayer.teamId() == homeTeamId);
    }

    private static void clearMatchEventAssistingPlayer(MatchEvent matchEvent) {
        matchEvent.setAssistingPlayerName("");
        matchEvent.setAssistingPlayerId(0);
        matchEvent.setGehilfeHeim(true);
    }

    private static MatchEvent createMatchEvent(MatchEventID matchEventID) {
        MatchEvent matchEvent = new MatchEvent();
        matchEvent.setMatchEventID(matchEventID.getValue());
        return matchEvent;
    }
}
