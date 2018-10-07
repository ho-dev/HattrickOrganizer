package module.playerOverview;

import core.model.player.Spieler;

public interface PlayerTable {

	Spieler getSpieler(int row);
	void setSpieler(int spielerid);
	
}
