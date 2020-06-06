// %193722072:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

/**
 * Team Object Class
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class Team implements Comparable<Team> {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Name of the team */
    private String name;

    /** Team id */
    private int teamId;
 
    // A hack for custom coloring of tournament teams in a renderer
    private boolean tournament = false;

    private int matchType = -1;
    // Timestamp when next match is played
    private java.sql.Timestamp time;

    //~ Methods ------------------------------------------------------------------------------------
    public void setName(String string) {
        name = string;
    }

    public String getName() {
        return name;
    }

    public void setTeamId(int i) {
        teamId = i;
    }

    public void setTime(java.sql.Timestamp t) {
        time = t;
    }

    public java.sql.Timestamp getTime() {
        return time;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }

    public int getMatchType() {
        return matchType;
    }

    public boolean isTournament() {
		return tournament;
	}

	public void setTournament(boolean tournament) {
		this.tournament = tournament;
	}

	/**
     * Return description for the team
     *
     * @return
     */
    public String desc() {
        return name + " " + teamId;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public String toString() {
        return name;
    }

    /**
     * Check if this team has match before team passed as parameter
     */
    public boolean isBefore(Team b) {
        if (b.getTeamId() == 0 && getTeamId() == 0)
            return false;

        if (getTeamId() == 0)
            return false;

        if (b.getTeamId() == 0)
            return true;

        if (time.getTime() < b.time.getTime())
            return true;

        return false;
    }

    @Override
    public int compareTo(Team team) {
        return this.getTime().compareTo(team.getTime());
    }
}
