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

	public enum Forecast {
		HAPPENED(0), TODAY(1), TOMORROW(2), UNSURE(3), NULL(4);
		private final int id;

		private Forecast(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public static Forecast getById(int id) {
			switch (id) {
				case 0:
					return HAPPENED;
				case 1:
					return TODAY;
				case 2:
					return TOMORROW;
				case 3:
					return UNSURE;
				default:
					return NULL;
			}
		}

		public boolean isSure() {
			return this.id<TOMORROW.id;
		}
	}
}

