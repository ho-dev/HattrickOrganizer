package core.model.cup;

import java.util.HashMap;
import java.util.Map;

public enum CupLevel {

    NONE(0),
    NATIONALorDIVISIONAL(1),  // National/Divisional cup
    CHALLENGER(2), // Challenger cup
    CONSOLATION(3);  // Consolation cup

    private final int id;
    private static Map map = new HashMap<>();

    CupLevel(int id) {
        this.id = id;
    }

    static {
        for (CupLevel oCupLevel : CupLevel.values()) {
            map.put(oCupLevel.id, oCupLevel);
        }
    }

    public static CupLevel fromInt(int iCupLevel) {
        return (CupLevel) map.get(iCupLevel);
    }

    public int getId() {
        return id;
    }
}
