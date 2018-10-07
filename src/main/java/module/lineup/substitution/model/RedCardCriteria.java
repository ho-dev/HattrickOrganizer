package module.lineup.substitution.model;

public enum RedCardCriteria {

	IGNORE((byte) -1),
	MY_PLAYER((byte) 1),
	OPPONENT_PLAYER((byte) 2),
	MY_CENTRAL_DEFENDER((byte) 11),
	MY_MIDFIELDER((byte) 12),
	MY_FORWARD((byte) 13),
	MY_WING_BACK((byte) 14),
	MY_WINGER((byte) 15),
	OPPONENT_CENTRAL_DEFENDER((byte) 21),
	OPPONENT_MIDFIELDER((byte) 22),
	OPPONENT_FORAWARD((byte) 23),
	OPPONENT_WING_BACK((byte) 24),
	OPPONENT_WINGER((byte) 25);

	private final byte id;

	private RedCardCriteria(byte id) {
		this.id = id;
	}

	public byte getId() {
		return this.id;
	}

	public static RedCardCriteria getById(byte id) {
		for (RedCardCriteria criteria : RedCardCriteria.values()) {
			if (criteria.getId() == id) {
				return criteria;
			}
		}
		return null;
	}
}
