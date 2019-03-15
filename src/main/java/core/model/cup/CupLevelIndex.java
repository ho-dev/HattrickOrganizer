package core.model.cup;

public enum CupLevelIndex {

    NONE(0), // i.e MatchType is not 3.
    EMERALD(1),  // EMERALD, National or Divisional cup
    RUBY(2), // Ruby cup
    SAPPHIRE(3);  // Sapphire cup

    private final int id;

    CupLevelIndex(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}