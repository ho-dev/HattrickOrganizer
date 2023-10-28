package core.prediction.engine;


class TeamGameData extends TeamData {

    private boolean isHome;
    private int actionAlreadyPlayed;
    private int actionNumber;
    private int counterAction;

    TeamGameData(int _action, double possess, double rchance, double lchance,
                        double mchance, double rrisk, double lrisk, double mrisk, int type,
                        int level) {
        super("", new TeamRatings(possess, lrisk, mrisk, rrisk, lchance, mchance, rchance), type,
              level);
        actionNumber = _action;
        actionAlreadyPlayed = 0;
        counterAction = 0;
    }

    public final int getActionAlreadyPlayed() {
        return actionAlreadyPlayed;
    }

    public final void setActionNumber(int d) {
        actionNumber = d;
    }

    public final int getActionNumber() {
        return actionNumber;
    }

    public final int getCounterAction() {
        return counterAction;
    }

    public final void setHome(boolean b) {
        isHome = b;
    }

    public final boolean isHome() {
        return isHome;
    }

    public final double getTotalChance() {
        return (getRatings().getRightAttack() + getRatings().getLeftAttack()
               + getRatings().getMiddleAttack()) / 3.0;
    }

    public final double getTotalRisk() {
        return (getRatings().getRightDef() + getRatings().getLeftDef()
               + getRatings().getMiddleDef()) / 3.0;
    }

    public final void addActionPlayed() {
        actionAlreadyPlayed++;
    }

    public final void addCounterActionPlayed() {
        counterAction++;
    }
}
