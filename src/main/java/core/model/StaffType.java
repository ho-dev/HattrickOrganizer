package core.model;

public enum StaffType {

	NONE(0),
	ASSISTANTTRAINER(1),
	MEDIC(2),
	SPOKESPERSON(3),
	SPORTPSYCHOLOGIST(4),
	FORMCOACH(5),
	FINANCIALDIRECTOR(6),
	TACTICALASSISTANT(7);
		
	private final int id;

	StaffType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}


		public static StaffType getById(int id) {
			for (StaffType staffRole : StaffType.values()) {
				if (staffRole.getId() == id) {
					return staffRole;
				}
			}
			return null;
		}

		public String getName() {
            return switch (this) {
                case ASSISTANTTRAINER -> HOVerwaltung.instance().getLanguageString("ls.club.staff.assistantcoach");
                case MEDIC -> HOVerwaltung.instance().getLanguageString("ls.club.staff.medic");
                case SPOKESPERSON -> HOVerwaltung.instance().getLanguageString("ls.club.staff.spokesperson");
                case SPORTPSYCHOLOGIST -> HOVerwaltung.instance().getLanguageString("ls.club.staff.sportspsychologist");
                case FORMCOACH -> HOVerwaltung.instance().getLanguageString("ls.club.staff.formcoach");
                case FINANCIALDIRECTOR -> HOVerwaltung.instance().getLanguageString("ls.club.staff.financialdirector");
                case TACTICALASSISTANT -> HOVerwaltung.instance().getLanguageString("ls.club.staff.tacticalassistant");

                //Error?
                default -> "unknown";
            };
		}
}
