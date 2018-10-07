package core.constants.player;
	// enum for speciality
	public enum Speciality {
		NO_SPECIALITY(0), TECHNICAL(1), QUICK(2), POWERFUL(3), UNPREDICTABLE(4), HEAD(5), REGAINER(6), SUPPORT(8);
		private final int value;

		Speciality(int spec) {
			value = spec;
		}
}