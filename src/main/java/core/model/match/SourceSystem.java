package core.model.match;

public enum SourceSystem {

    HATTRICK((int) 0),
    YOUTH((int) 1),  // youth match
    HTOINTEGRATED((int) 2); //integrated match

    private final int value;

    SourceSystem(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SourceSystem valueOf(int id) {
        for (SourceSystem source : SourceSystem.values()) {
            if (source.getValue() == id) {
                return source;
            }
        }
        return null;
    }

    public String to_string() {
        return switch (this.value) {
            case 0 -> "hattrick";
            case 1 -> "youth";
            case 2 -> "htointegrated";
            default -> "invalid source system";
        };
    }

}
