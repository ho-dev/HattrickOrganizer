package core.model.match;

public enum TournamentType {
    NONE(0),
    LEAGUE_WITH_PLAYOFFS(3),
    CUP(4),  // League match
    DIVISIONBATTLE(10); //Qualification match

    private final int id;

    TournamentType(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public static TournamentType getById(int id) {
        for (TournamentType oTournamentType : TournamentType.values()) {
            if (oTournamentType.getId() == id) {
                return oTournamentType;
            }
        }
        return null;
    }

}
