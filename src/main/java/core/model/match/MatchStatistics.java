package core.model.match;

import java.util.Vector;

public class MatchStatistics {

	private MatchLineupTeam teamLineup;
	private MatchKurzInfo match;

	public MatchStatistics(MatchKurzInfo match, MatchLineupTeam team) {
		this.match = match;
		teamLineup = team;

		if (isOldie()) {
			// Old match with no start lineup. Set start position like end
			// position for all players.
			var mlps = teamLineup.getLineup().getFieldPositions();
			for (var mlp : mlps) {
				var matchLineupPosition = (MatchLineupPosition)mlp;
				matchLineupPosition.setStartPosition(matchLineupPosition.getPosition());
			}
		}
	}

	/**
	 * Returns the minutes a player has played in the specified positions. For
	 * matches with no sub/start info, players get 90 minutes on the position
	 * they ended the match.
	 * 
	 * @param spielerId
	 *            the id of the player
	 * @param accepted
	 *            An array of integers specifying the positions which should be
	 *            accepted.
	 *            If null, all field positions are accepted. (used for experience increase)
	 * @return the number of minutes played in the specified positions.
	 * 			Special handling of walkover matches:
	 * 			 0 minutes is returned in case of walkover matches when all field positions are accepted
	 * 			(experience does not increase in such cases),
	 * 			90 minutes is returned if accepted field positions are given (training does increase)
	 */
	public int getTrainMinutesPlayedInPositions(int spielerId, int[] accepted) {
		return this.teamLineup.getTrainMinutesPlayedInPositions(spielerId, accepted, match.isWalkoverMatch());
	}

	// All positions are accepted.
	public int getStaminaMinutesPlayedInPositions(int spielerId) {
		return getTrainMinutesPlayedInPositions(spielerId, null);
	}

	/**
	 * Returns the last minute of the match. Usually 90, but there could be
	 * overtime.
	 * 
	 * @return the last minute or -0 if not found
	 */

	private boolean isOldie() {
		for (var mlp : teamLineup.getLineup().getFieldPositions()) {
			var matchLineupPosition = (MatchLineupPosition)mlp;
			if (matchLineupPosition.getStartPosition() > 0) {
				return false;
			}
		}
		return true;
	}
}
