package module.lineup.substitution.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * A class holding information about substitutions and order changes
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
	private MatchOrderType orderType = MatchOrderType.SUBSTITUTION;
	@SerializedName("min")
	@Expose
	private int matchMinuteCriteria = -1;
	@SerializedName("pos")
	@Expose
	private byte positionIndex = -1;
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
			MatchOrderType orderType, int matchMinuteCriteria, byte pos, byte behaviour,
			RedCardCriteria card, GoalDiffCriteria standing) {
		this.playerOrderID = playerOrderID;
		this.objectPlayerID = playerIn;
		this.subjectPlayerID = subjectPlayerID;
		this.orderType = orderType;
		this.matchMinuteCriteria = matchMinuteCriteria;
		this.positionIndex = pos;
		this.behaviour = behaviour;
		this.card = card;
		this.standing = standing;
	}

	public Substitution() {
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
			this.objectPlayerID = subjectPlayerID;
		}
	}

	public MatchOrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(MatchOrderType orderType) {
		this.orderType = orderType;
	}

	public int getMatchMinuteCriteria() {
		return matchMinuteCriteria;
	}

	public void setMatchMinuteCriteria(int matchMinuteCriteria) {
		this.matchMinuteCriteria = matchMinuteCriteria;
	}

	public int getRoleId() {
		return positionIndex+100;
	}

	public void setRoleId(int roleId) {
		this.positionIndex = (byte) (roleId-100);
	}

	public byte getPositionIndex() {
		return positionIndex;
	}

	public void setPositionIndex(byte positionIndex) { this.positionIndex = positionIndex; }

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

	/**
	 * Merges the data from the given <code>Substitution</code> into this
	 * <code>Substitution</code>. This method should be used e.g. when a model
	 * has to be updated with data from a different <code>Substitution</code>
	 * instance but and object identity has to be preserved.
	 * 
	 * @param other
	 *            the <code>Substitution</code> to get the data from.
	 */
	public void merge(Substitution other) {
		setBehaviour(other.getBehaviour());
		setRedCardCriteria(other.getRedCardCriteria());
		setMatchMinuteCriteria(other.getMatchMinuteCriteria());
		setOrderType(other.getOrderType());
		setObjectPlayerID(other.getObjectPlayerID());
		setPlayerOrderId(other.getPlayerOrderId());
		setSubjectPlayerID(other.getSubjectPlayerID());
		setRoleId(other.getRoleId());
		setStanding(other.getStanding());
	}

}
