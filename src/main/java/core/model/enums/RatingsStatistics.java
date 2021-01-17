package core.model.enums;

public enum RatingsStatistics {
    POWER_RATINGS(1),
    HATSTATS_DEF_MAX(2),
    HATSTATS_MID_MAX(3),
    HATSTATS_OFF_MAX(4),
    HATSTATS_DEF_AVG(5),
    HATSTATS_MID_AVG(6),
    HATSTATS_OFF_AVG(7);

    private int value;

    RatingsStatistics(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
