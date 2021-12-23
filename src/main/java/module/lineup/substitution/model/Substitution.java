package module.lineup.substitution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.player.Player;


/**
 * A class holding information about match orders
 *  - substitutions,
 *  - position swap
 *  - behaviour change
 *  - man marking
 * 
 * @author blaghaid
 * 
 */
public class Substitution {
	private int playerOrderID = -1;
	@SerializedName("playerin")
	@Expose
	private int objectPlayerID = -1;
	@SerializedName("playerout")
	@Expose
	private int subjectPlayerID = -1;
	@SerializedName("orderType")
	@Expose
	private MatchOrderType orderType;
	@SerializedName("min")
	@Expose
	private byte matchMinuteCriteria = -1;
	/**
	 * pos
	 *
	 * the position the player should take after the substitution,
	 * 0-13, see positions above for the order.
	 * -1 means no change.
	 *
	 * Attention: intellij claims that pos is never used.
	 * this is wrong. serialization of the json order (should) use it.
	 */
	@SerializedName("pos")
	@Expose
	private byte pos = -1;			// json attribute 0-13
	private byte roleId = -1;		// 100-113
	@SerializedName("beh")
	@Expose
	private byte behaviour = -1;
	@SerializedName("card")
	@Expose
	private RedCardCriteria card = RedCardCriteria.IGNORE;
	@SerializedName("standing")
	@Expose
	private GoalDiffCriteria standing = GoalDiffCriteria.ANY_STANDING;

	public Substitution(int playerOrderID, int playerIn, int subjectPlayerID,
			byte orderType, byte matchMinuteCriteria, byte roleId, byte behaviour,
			RedCardCriteria card, GoalDiffCriteria standing) {
		this.playerOrderID = playerOrderID;
		this.objectPlayerID = playerIn;
		this.subjectPlayerID = subjectPlayerID;

		if (orderType == MatchOrderType.POSITION_SWAP.getId()) {
			this.orderType = MatchOrderType.POSITION_SWAP;
		}
		else if ( orderType == MatchOrderType.MAN_MARKING.getId() ){
			this.orderType = MatchOrderType.MAN_MARKING;
		}
		else {
			// NEW_BEHAVIOUR and SUBSTITUTION have the same id
			if (playerIn == subjectPlayerID) {
				this.orderType = MatchOrderType.NEW_BEHAVIOUR;
			} else if ((playerIn <= 0 || subjectPlayerID <= 0)) {
				// allows the correct retrieval of some cases
				this.orderType = MatchOrderType.NEW_BEHAVIOUR;
			} else {
				this.orderType = MatchOrderType.SUBSTITUTION;
			}
		}

		this.matchMinuteCriteria = matchMinuteCriteria;
		setRoleId(roleId);

		this.behaviour = behaviour;
		this.card = card;
		this.standing = standing;
	}

	public Substitution(MatchOrderType orderType) {
		this.orderType = orderType;
	}

	public int getPlayerOrderId() {
		return playerOrderID;
	}

	public void setPlayerOrderId(int id) {
		this.playerOrderID = id;
	}

	/**
	 * Gets the ID of the the player entering, or if position swap, the player
	 * to swap with.
	 * 
	 * @return the id of the player entering
	 */
	public int getObjectPlayerID() {
		return objectPlayerID;
	}

	public void setObjectPlayerID(int objectPlayerID) {
		this.objectPlayerID = objectPlayerID;
		// Clear object player name so it gets retrieved again.
		this.objectPlayerName = null;
	}

	/**
	 * Gets the ID of the affected player. If substitution: the player leaving.
	 * If behaviour change: the player changing his behaviour. If position swap:
	 * the first player that will change his position.
	 * 
	 * @return the id of the affected player.
	 */
	public int getSubjectPlayerID() {
		return subjectPlayerID;
	}

	public void setSubjectPlayerID(int subjectPlayerID) {
		this.subjectPlayerID = subjectPlayerID;
		// to get conform with CHPP API (playerout==playerin if its a 
		// behaviour change)
		if (this.orderType == MatchOrderType.NEW_BEHAVIOUR) {
			setObjectPlayerID(subjectPlayerID);
		}
		this.subjectPlayerName = null;
	}

	public MatchOrderType getOrderType() {
		return orderType;
	}

	public byte getMatchMinuteCriteria() {
		return matchMinuteCriteria;
	}

	public void setMatchMinuteCriteria(byte matchMinuteCriteria) {
		this.matchMinuteCriteria = matchMinuteCriteria;
	}

	public byte getRoleId() {
		return roleId;
	}

	public void setRoleId(byte roleId)
	{
		this.roleId = roleId;
		if ( roleId > 99){
			this.pos = (byte) (roleId-100);
		}
		else {
			this.pos = roleId;
		}
	}

	public byte getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(byte behaviour) {
		this.behaviour = behaviour;
	}

	public RedCardCriteria getRedCardCriteria() {
		return card;
	}

	public void setRedCardCriteria(RedCardCriteria card) {
		this.card = card;
	}

	public GoalDiffCriteria getStanding() {
		return standing;
	}

	public void setStanding(GoalDiffCriteria standing) {
		this.standing = standing;
	}

	String subjectPlayerName;
	public String getSubjectPlayerName(){
		if ( subjectPlayerName == null){
			subjectPlayerName = getCurrentPlayerName(this.getSubjectPlayerID());
		}
		return subjectPlayerName;
	}

	private String getCurrentPlayerName(int id) {
		Player p = HOVerwaltung.instance().getModel().getCurrentPlayer(id);
		if (p != null) return p.getFullName();
		return "";
	}

	String objectPlayerName;
	public String getObjectPlayerName() {
		if ( getObjectPlayerID() == getSubjectPlayerID()) return getSubjectPlayerName();
		if ( objectPlayerName == null){
			if ( getOrderType() != MatchOrderType.MAN_MARKING) {
				objectPlayerName = getCurrentPlayerName(this.getObjectPlayerID());
			}
			else {
				objectPlayerName = HOVerwaltung.instance().getModel().getOpponentPlayerName(this.getObjectPlayerID());
			}
		}
		return objectPlayerName;
	}
}
