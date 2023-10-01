package core.constants.player;
	// enum for speciality
	public enum Specialty {
		NO_SPECIALITY(0), TECHNICAL(1), QUICK(2), POWERFUL(3), UNPREDICTABLE(4), HEAD(5), REGAINER(6), _UNUSED(7), SUPPORT(8);
		private final int value;

		Specialty(int spec) {
			value = spec;
		}
}