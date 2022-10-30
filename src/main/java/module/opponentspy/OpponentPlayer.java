package module.opponentspy;


import core.model.player.Player;
import module.opponentspy.OpponentTeam.PlayedPosition;
import java.util.ArrayList;
import java.util.List;


public class OpponentPlayer extends Player {
	
	private int position;
	private List<PlayedPosition> playedPositions;

    public OpponentPlayer() {
        super();
    }


    public int getPosition() {
		return position;
	}
	
	public void setPosition (int position) {
		this.position = position;
	}
	
	public void addPlayedPosition(PlayedPosition position) {
		if (playedPositions == null)
			playedPositions = new ArrayList<>();
		
		playedPositions.add(position);
	}


	
}