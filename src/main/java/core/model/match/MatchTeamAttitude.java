package core.model.match;

public enum MatchTeamAttitude {

    PlayItCool(-1),
	Normal(0),
	MatchOfTheSeason(1);

    private final int id;

    MatchTeamAttitude(int i) {
        this.id=i;
    }

    static public MatchTeamAttitude fromInt(Integer i) {
        if (i == null) return null;
        return switch (i) {
            case 1 -> MatchOfTheSeason;
            case -1 -> PlayItCool;
            default -> Normal;
        };
    }

    public int toInt(){
        return this.id;
    }
}
