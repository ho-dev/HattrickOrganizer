package module.playeroverview;

import core.model.player.Player;

public interface PlayerTable {

	Player getPlayer(int row);
	void setPlayer(int spielerid);
	
}
