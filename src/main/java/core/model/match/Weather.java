package core.model.match;

public enum Weather {

	RAINY(0), OVERCAST(1), PARTIALLY_CLOUDY(2), SUNNY(3), UNKNOWN(4), NULL(5);

	private final int id;

	private Weather(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public static Weather getById(int id) {
		switch (id) {
		case 0:
			return RAINY;
		case 1:
			return OVERCAST;
		case 2:
			return PARTIALLY_CLOUDY;
		case 3:
			return SUNNY;
		case 4:
			return UNKNOWN;
		case 5:
			return NULL;
		default:
			return null;
		}
	}

}
