package core.model.enums;

public enum RatingsStatistics {
    POWER_RATINGS(1),
    HATSTATS_TOTAL(2),
    HATSTATS_DEF(3),
    HATSTATS_MID(4),
    HATSTATS_OFF(5);
    private int value;

    RatingsStatistics(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
