package core.model.player;

import java.util.HashMap;

public enum Specialty {
    /*
    SpecialtyID
    Value	Description
0	No specialty
1	Technical
2	Quick
3	Powerful
4	Unpredictable
5	Head specialist
6	resilient
8	support
*/

    NoSpecialty(0),
    Technical(1),
    Quick(2),
    Powerful(3),
    Unpredictable(4),
    Head(5),        // renamed to fit to ls.player.speciality.head string
    Regainer(6),    // renamed to fit to ls.player.speciality.regainer string
    Not_used(7),
    Support(8);

    private int value;
    private static HashMap<Integer, Specialty> map = new HashMap<>();

    Specialty(int value) {
        this.value = value;
    }

    // Init mapping
    static {
        for (Specialty s : Specialty.values()) {
            map.put(s.value, s);
        }
    }

    public static Specialty valueOf(Integer s) {
        if ( s != null) {
            return map.get(s);
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
