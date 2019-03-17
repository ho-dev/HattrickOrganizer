package core.model.cup;

public enum CupLevel {

    NONE(0),
    NATIONALorDIVISIONAL(1),  // National/Divisional cup
    CHALLENGER(2), // Challenger cup
    CONSOLATION(3);  // Consolation cup

    private final int id;

    CupLevel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
