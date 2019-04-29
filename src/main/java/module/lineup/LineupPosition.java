package module.lineup;


import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;

import java.util.Vector;


/**
 * 
 * Position of players for the futur matchs: used by training preview
 * 
 * @author yaute
 */
public class LineupPosition {

	/** positions */
	private Vector<IMatchRoleID> m_vPositionen = new Vector<>();

	// ~ Methods
	// -------------------------------------------------------------------------------

	/**
	 * Add new playeurs with is role id
	 *
	 * @param roleId:	role id
	 * @param playerId:	player id
	 */
	public void addPosition(int roleId, int playerId) {
		m_vPositionen.add(new MatchRoleID(roleId, playerId, (byte) 0));
	}

	/**
	 * Get player role id from a his id
	 *
	 * @param playerid:	player id
	 * @return			role id
	 */
	public final MatchRoleID getPositionBySpielerId(int playerid) {
		for (IMatchRoleID position : m_vPositionen) {
			MatchRoleID spielerPosition = (MatchRoleID) position;
			if (spielerPosition.getSpielerId() == playerid) {
				return spielerPosition;
			}
		}
		return null;
	}
}
