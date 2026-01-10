package module.halloffame;

import core.model.TranslationFacility;

public enum ExpertType {
    REFEREE(1),
    AGENT(2),
    PLAYER_UNION_REPRESENTATIVE(3),
    TELEVISION_SHOW_HOST(4),
    ACTOR(5),
    SPORTS_JOURNALIST(6),
    MARKETING_DIRECTOR(7),
    RESTAURANT_OWNER(8),
    HEAD_EQUIPMENT_MANAGER(9),
    ENTREPENEUR(10),
    HOTDOG_VENDER(11),
    PUBLIC_RELATIONS_MANAGER(12),
    SALES_DIRECTOR(13),
    INFORMERCIAL_HOST(14),
    SECRETARY(15),
    UNEMPLOYED(16),
    MEDIC(17),
    FORMCOACH(18),
    ASSISTANT_TRAINER(19),
    SPOKESPERSON(20),
    PSYCHOLOGIST(21),
    FINANCIAL_DIRECTOR(22),
    YOUTH_TRAINER(23),
    YOUTH_SCOUT(24),
    TACTICAL_ASSISTANT(25),
    SPORTS_DIRECTOR(26),
    GOALKEEPERS_COACH(27),
    SUPPORTER_LIAISON_OFFICER(28),
    CHIEF_FINANCIAL_OFFICER(29),
    PRESS_MANAGER(30),
    HEAD_COACH(31);

    private final int id;

    ExpertType(int id) {
        this.id = id;
    }

    public int toInt() {
        return id;
    }

    /**
     * Convert integer to expert type object
     * @param i Integer
     * @return ExpertType
     */
    public static ExpertType fromInteger(Integer i) {
        if (i != null) {
            for (var s : ExpertType.values()) {
                if (s.id == i) {
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Get translated expert type string
     * @return String
     */
    public String getLanguageString() {
        return TranslationFacility.tr("ls.hof.experttype." + toString().toLowerCase());
    }
}
