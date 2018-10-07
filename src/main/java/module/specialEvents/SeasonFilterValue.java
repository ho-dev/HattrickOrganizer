package module.specialEvents;

public enum SeasonFilterValue {

	CURRENT_SEASON(3), LAST_TWO_SEASONS(2), ALL_SEASONS(1);

	private int id;

	private SeasonFilterValue(int id) {
		this.id = id;
	}

	public static SeasonFilterValue getById(int id) {
		switch (id) {
		case 3:
			return CURRENT_SEASON;
		case 2:
			return LAST_TWO_SEASONS;
		case 1:
			return ALL_SEASONS;
		default:
			return null;
		}
	}

	public int getId() {
		return this.id;
	}
}
