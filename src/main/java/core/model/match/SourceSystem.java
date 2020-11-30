package core.model.match;

public enum SourceSystem {

    HATTRICK((int) 0),
    YOUTH((int) 1),  // youth match
    HTOINTEGRATED((int) 2); //integrated match

    private final int id;

    SourceSystem(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static SourceSystem getById(int id) {
        for (SourceSystem source : SourceSystem.values()) {
            if (source.getId() == id) {
                return source;
            }
        }
        return null;
    }

    public String to_string() {
        return switch (this.id) {
            case 0 -> "hattrick";
            case 1 -> "youth";
            case 2 -> "htointegrated";
            default -> "invalid source system";
        };
    }

}
