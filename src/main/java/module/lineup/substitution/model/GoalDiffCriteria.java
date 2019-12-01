package module.lineup.substitution.model;

import com.google.gson.annotations.SerializedName;

/**
 * GoalDiffCriteria for substitutions.
 * 
 * @author kruescho
 *
 */
public enum GoalDiffCriteria {

	@SerializedName("-1")
	ANY_STANDING((byte) -1),
	@SerializedName("0")
	MATCH_IS_TIED((byte) 0),
	@SerializedName("1")
	IN_THE_LEAD((byte) 1),
	@SerializedName("2")
	DOWN((byte) 2),
	@SerializedName("3")
	IN_THE_LEAD_BY_MORE_THAN_ONE((byte) 3),
	@SerializedName("4")
	DOWN_BY_MORE_THAN_ONE((byte) 4),
	@SerializedName("5")
	NOT_DOWN((byte) 5),
	@SerializedName("6")
	NOT_IN_THE_LEAD((byte) 6),
	@SerializedName("7")
	IN_THE_LEAD_BY_MORE_THAN_TWO((byte) 7),
	@SerializedName("8")
	DOWN_BY_MORE_THAN_TWO((byte) 8),
	@SerializedName("9")
	MATCH_IS_NOT_TIED((byte) 9);

	private final byte id;

	private GoalDiffCriteria(byte id) {
		this.id = id;
	}

	public byte getId() {
		return this.id;
	}

	public static GoalDiffCriteria getById(byte id) {
		for (GoalDiffCriteria standing : GoalDiffCriteria.values()) {
			if (standing.getId() == id) {
				return standing;
			}
		}
		return null;
	}
}
