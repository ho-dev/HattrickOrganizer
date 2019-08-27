package module.lineup.substitution.model;

/**
 * GoalDiffCriteria for substitutions.
 * 
 * @author kruescho
 *
 */
public enum GoalDiffCriteria {
	
	ANY_STANDING((byte) -1),
	MATCH_IS_TIED((byte) 0),
	IN_THE_LEAD((byte) 1),
	DOWN((byte) 2),
	IN_THE_LEAD_BY_MORE_THAN_ONE((byte) 3),
	DOWN_BY_MORE_THAN_ONE((byte) 4),
	NOT_DOWN((byte) 5),
	NOT_IN_THE_LEAD((byte) 6),
	IN_THE_LEAD_BY_MORE_THAN_TWO((byte) 7),
	DOWN_BY_MORE_THAN_TWO((byte) 8),
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
