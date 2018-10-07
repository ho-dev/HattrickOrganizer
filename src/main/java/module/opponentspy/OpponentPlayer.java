package module.opponentspy;


import core.model.match.MatchLineupPlayer;
import core.model.player.Spieler;
//import module.opponentspy.OpponentTeam.PlayedPosition;

import module.opponentspy.OpponentTeam.PlayedPosition;

import java.util.ArrayList;
import java.util.List;


public class OpponentPlayer extends Spieler {
	
	private int position;
	private List<MatchLineupPlayer> matchesPlayed;
	private List<PlayedPosition> playedPositions;
	private boolean playingAbroad;
	private int calculationRole;
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition (int position) {
		this.position = position;
	}
	
	public void addPlayedPosition(PlayedPosition position) {
		if (playedPositions == null)
			playedPositions = new ArrayList<PlayedPosition>();
		
		playedPositions.add(position);
	}

	public List<PlayedPosition> getPlayedPositions() {
		return playedPositions;
	}

	public boolean isPlayingAbroad() {
		return playingAbroad;
	}

	public void setPlayingAbroad(boolean playsAbroad) {
		this.playingAbroad = playsAbroad;
	}

	public int getCalculationRole() {
		return calculationRole;
	}

	public void setCalculationRole(int calculationRole) {
		this.calculationRole = calculationRole;
	}
	
	
	
}