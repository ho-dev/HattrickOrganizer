package module.nthrf;

/**
 * Simple data class for position related information of a player.
 */
class NtPlayerPosition {

	private long playerId;
	private String name;

	private int roleId = 0;
	private int positionCode = 1;
	private int behaviour = 0;

	private float ratingStars = 0;

	long getPlayerId() {
		return playerId;
	}
	void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	int getRoleId() {
		return roleId;
	}
	int getPositionCode() {
		return positionCode;
	}
	int getBehaviour() {
		return behaviour;
	}
	float getRatingStars() {
		return ratingStars;
	}
	void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	void setPositionCode(int positionCode) {
		this.positionCode = positionCode;
	}
	void setBehaviour(int behaviour) {
		this.behaviour = behaviour;
	}
	void setRatingStars(float ratingStars) {
		this.ratingStars = ratingStars;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("--- NtPlayerPosition - " + name + " (" + playerId + ") ---");
		sb.append("\n\trole / posi / behaviour: " + roleId + " / " + positionCode + " / " + behaviour);
		sb.append("\n\tRatingstars: " + ratingStars);
		return sb.toString();
	}
}
