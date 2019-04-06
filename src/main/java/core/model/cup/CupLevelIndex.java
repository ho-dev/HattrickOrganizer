package core.model.cup;

import java.util.HashMap;
import java.util.Map;

public enum CupLevelIndex {

    NONE(0), // i.e MatchType is not 3.
    EMERALD(1),  // EMERALD, National or Divisional cup
    RUBY(2), // Ruby cup
    SAPPHIRE(3);  // Sapphire cup

    private final int id;
    private static Map map = new HashMap<>();

    CupLevelIndex(int id) {
        this.id = id;
    }

    static {
        for (CupLevelIndex oCupLevelIndex : CupLevelIndex.values()) {
            map.put(oCupLevelIndex.id, oCupLevelIndex);
        }
    }

    public static CupLevelIndex fromInt(int iCupLevelIndex) {
        return (CupLevelIndex) map.get(iCupLevelIndex);
    }

    public int getId() {
        return id;
    }
}