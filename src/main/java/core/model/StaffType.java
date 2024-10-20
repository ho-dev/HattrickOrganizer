package core.model;

public enum StaffType {

	NONE((int) 0),
	ASSISTANTTRAINER((int) 1),
	MEDIC((int) 2),
	SPOKESPERSON((int) 3),
	SPORTPSYCHOLOGIST((int) 4),
	FORMCOACH((int) 5),
	FINANCIALDIRECTOR((int) 6),
	TACTICALASSISTANT((int) 7);
		
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
			 switch (this) {
		         case ASSISTANTTRAINER:
		             return TranslationFacility.tr("ls.club.staff.assistantcoach");

		         case MEDIC:
		             return TranslationFacility.tr("ls.club.staff.medic");

		         case SPOKESPERSON:
		             return TranslationFacility.tr("ls.club.staff.spokesperson");

		         case SPORTPSYCHOLOGIST:
		             return TranslationFacility.tr("ls.club.staff.sportspsychologist");

		         case FORMCOACH:
		             return TranslationFacility.tr("ls.club.staff.formcoach");

		         case FINANCIALDIRECTOR:
		             return TranslationFacility.tr("ls.club.staff.financialdirector");
		         
		         case TACTICALASSISTANT:
		           return TranslationFacility.tr("ls.club.staff.tacticalassistant");
				 
		           //Error?
		         default:
		             return "unknown";

			 }
		}
}
