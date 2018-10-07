package module.specialEvents;

public enum EventType {

	SPECIALTYSE(0), WEATHERSE(1), COUNTER(2), IFK(3), LONGSHOT(4), FREEKICK(5), PENALTY(6);

	private final int id;

	private EventType(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}
}
