package module.lineup.substitution.model;

import com.google.gson.annotations.SerializedName;

public enum RedCardCriteria {

	@SerializedName("-1")
	IGNORE((byte) -1),
	@SerializedName("1")
	MY_PLAYER((byte) 1),
	@SerializedName("2")
	OPPONENT_PLAYER((byte) 2),
	@SerializedName("11")
	MY_CENTRAL_DEFENDER((byte) 11),
	@SerializedName("12")
	MY_MIDFIELDER((byte) 12),
	@SerializedName("13")
	MY_FORWARD((byte) 13),
	@SerializedName("14")
	MY_WING_BACK((byte) 14),
	@SerializedName("15")
	MY_WINGER((byte) 15),
	@SerializedName("21")
	OPPONENT_CENTRAL_DEFENDER((byte) 21),
	@SerializedName("22")
	OPPONENT_MIDFIELDER((byte) 22),
	@SerializedName("23")
	OPPONENT_FORAWARD((byte) 23),
	@SerializedName("24")
	OPPONENT_WING_BACK((byte) 24),
	@SerializedName("25")
	OPPONENT_WINGER((byte) 25);

	private final byte id;

	RedCardCriteria(byte id) {
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

	public static RedCardCriteria parse(String id){
		try {
			return getById(Byte.parseByte(id));
		}
		catch(Exception ignore){
			return IGNORE;
		}
	}
}
