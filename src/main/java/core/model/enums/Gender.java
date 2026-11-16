package core.model.enums;

import lombok.Getter;

/**
 * Hattrick's GenderID and LeagueSystemID, which share one value set (#2429): the gender of a team
 * or player, and the league system (men's or women's) a league belongs to.
 */
public enum Gender {
    MALE(1),
    FEMALE(2);

    /** Hattrick's id of this value, as stored in the database. */
    @Getter
    private final int id;

    Gender(int id) {
        this.id = id;
    }

    /**
     * Returns the value of a Hattrick id, or null for a missing id (downloads before the CHPP
     * versions that added it) or an id this version of HO does not know.
     */
    public static Gender fromId(Integer id) {
        if (id == null) {
            return null;
        }
        for (var gender : values()) {
            if (gender.id == id) {
                return gender;
            }
        }
        return null;
    }

    /** The database id of a value, or null for null. */
    public static Integer toId(Gender gender) {
        return gender == null ? null : gender.id;
    }
}
