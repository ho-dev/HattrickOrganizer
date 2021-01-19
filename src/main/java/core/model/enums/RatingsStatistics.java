package core.model.enums;

import java.util.Locale;

public enum RatingsStatistics {
    POWER_RATINGS(1),
    HATSTATS_TOTAL_MAX(2),
    HATSTATS_DEF_MAX(3),
    HATSTATS_MID_MAX(4),
    HATSTATS_OFF_MAX(5),
    HATSTATS_TOTAL_AVG(6),
    HATSTATS_DEF_AVG(7),
    HATSTATS_MID_AVG(8),
    HATSTATS_OFF_AVG(9);

    private int value;

    RatingsStatistics(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RatingsStatistics getCode(String sector, String stat){

        if (stat.equalsIgnoreCase("max"))
        return switch (sector.toLowerCase()){
            case "total" -> HATSTATS_TOTAL_MAX;
            case "def" -> HATSTATS_DEF_MAX;
            case "mid" -> HATSTATS_MID_MAX;
            case "off" -> HATSTATS_OFF_MAX;
            default -> throw new IllegalStateException("Unexpected value: " + sector.toLowerCase());
        };
        else if (stat.equalsIgnoreCase("avg")){
            return switch (sector.toLowerCase()){
                case "total" -> HATSTATS_TOTAL_AVG;
                case "def" -> HATSTATS_DEF_AVG;
                case "mid" -> HATSTATS_MID_AVG;
                case "off" -> HATSTATS_OFF_AVG;
                default -> throw new IllegalStateException("Unexpected value: " + sector.toLowerCase());
            };
        }

        throw new IllegalStateException("Unexpected value: " + sector.toLowerCase());
    }
}
