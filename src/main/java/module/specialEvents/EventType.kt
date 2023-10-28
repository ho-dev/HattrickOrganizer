package module.specialEvents;

public enum EventType {

	SPECIALTY_NON_WEATHER_SE(0), SPECIALTY_WEATHER_SE(1), COUNTER_ATTACK(2), MANMARKING(3), LONGSHOT(4), FREEKICK(5),
	PENALTY(6), NON_SPECIALTY_SE(7);

	private final int id;

	EventType(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}
}
