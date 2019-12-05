package core.model.match;

public enum TournamentType {
    LEAGUE_WITH_PLAYOFFS((int) 3),
    CUP((int) 4),  // League match
    DIVISIONBATTLE((int) 10); //Qualification match

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
