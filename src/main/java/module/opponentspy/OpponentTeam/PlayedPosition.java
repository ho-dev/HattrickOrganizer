package module.opponentspy.OpponentTeam;

import core.model.match.MatchType;

public class PlayedPosition {
	
	public PlayedPosition(int positionId, byte tacticId, MatchType matchType, double ratingStart, double ratingEnd) {
		this.positionId = positionId;
		this.tacticId = tacticId;
		this.matchType = matchType;
		this.ratingEnd = ratingEnd;
		this.ratingStart = ratingStart;
	}
	
	public int positionId;
	public byte tacticId;
	public MatchType matchType;
	public double ratingStart;
	public double ratingEnd;
}
