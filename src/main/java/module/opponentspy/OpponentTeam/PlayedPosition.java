package module.opponentspy.OpponentTeam;

import core.model.match.IMatchType;

public class PlayedPosition {
	
	public PlayedPosition(int positionId, byte tacticId, IMatchType matchType, double ratingStart, double ratingEnd) {
		this.positionId = positionId;
		this.tacticId = tacticId;
		this.matchType = matchType;
		this.ratingEnd = ratingEnd;
		this.ratingStart = ratingStart;
	}
	
	public int positionId;
	public byte tacticId;
	public IMatchType matchType;
	public double ratingStart;
	public double ratingEnd;
}
