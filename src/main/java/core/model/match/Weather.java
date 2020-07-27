package core.model.match;

public enum Weather {

	RAINY(0), OVERCAST(1), PARTIALLY_CLOUDY(2), SUNNY(3), UNKNOWN(4), NULL(5);

	private final int id;

	Weather(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public static Weather getById(Integer id) {
		if (id != null) return switch (id) {
			case 0 -> RAINY;
			case 1 -> OVERCAST;
			case 2 -> PARTIALLY_CLOUDY;
			case 3 -> SUNNY;
			case 4 -> UNKNOWN;
			case 5 -> NULL;
			default -> null;
		};
		return null;
	}

	public enum Forecast {
		HAPPENED(0), TODAY(1), TOMORROW(2), UNSURE(3), NULL(4);
		private final int id;

		Forecast(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public static Forecast getById(Integer id) {
			if ( id != null) {
				return switch (id) {
					case 0 -> HAPPENED;
					case 1 -> TODAY;
					case 2 -> TOMORROW;
					case 3 -> UNSURE;
					default -> NULL;
				};
			}
			return null;
		}

		public boolean isSure() {
			return this.id<TOMORROW.id;
		}
	}
}

