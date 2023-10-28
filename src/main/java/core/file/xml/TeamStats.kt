package core.file.xml;

public class TeamStats {

    private int teamId;
    private String teamName;

    private String leagueName;
    private int leagueRank;

    private int position;
    private int points;
    private int goalsFor;
    private int goalsAgainst;

    private int observedRank;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getLeagueRank() {
        return leagueRank;
    }

    public void setLeagueRank(int leagueRank) {
        this.leagueRank = leagueRank;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getGoalsDiff() {
        return getGoalsFor() - getGoalsAgainst();
    }

    public int getObservedRank() {
        return observedRank;
    }

    public void setObservedRank(int observedRank) {
        this.observedRank = observedRank;
    }

    /**
     * Calculates the ranking score for the current team.
     *
     * digit 1 = 10 - Division_Rank
     * digit 2 = 8 - position in Division
     * Digit 3-4 : nb points   (between 0 and 42)
     * digit 5-6-7:  500 + goals difference
     * digit 8-9-10: 500 + goal For
     * digit 11-12-13-14-15 initialized at 00000
     *
     * If no teams have duplicated score, we are done, otherwise for the team with duplicated score, we download teamDetails and
     * digits 11-12-13-14-15:  99 999 - visible rank* with
     * visible rank* = 99 999 if visible rank = 0  (bot team)
     *
     *
     * @return
     */
    public long rankingScore() {
        return (10-leagueRank) * 100_000_000_000_000L +
                (8-position) * 10_000_000_000_000L +
                points * 100_000_000_000L +
                (500 + getGoalsDiff()) * 100_000_000L +
                (500 + goalsFor) * 100_000L;
    }

    public String toString() {
        return "TeamStats[ " + teamName + " rank: " + getObservedRank() + " score: " + rankingScore() + " ]";
    }
}
