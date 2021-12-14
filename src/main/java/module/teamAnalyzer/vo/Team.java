// %193722072:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

import core.model.enums.MatchType;

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

    private int iMatchID;
 
    // A hack for custom coloring of tournament teams in a renderer
    private boolean tournament = false;

    private MatchType matchType;

    // Timestamp when next match is played
    private java.sql.Timestamp time;

    private boolean isHomeMatch;

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

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public boolean isTournament() {
		return tournament;
	}

	public void setTournament(boolean tournament) {
		this.tournament = tournament;
	}

    public String desc() {
        return name + " " + teamId;
    }

    public boolean isHomeMatch() {return isHomeMatch;}

    public void setHomeMatch(boolean homeMatch) {isHomeMatch = homeMatch;}

    public int getMatchID() {return iMatchID;}

    public void setMatchID(int iMatchID) {this.iMatchID = iMatchID;}

    @Override
	public String toString() {
        return name;
    }

    @Override
    public int compareTo(Team team) {
        return this.getTime().compareTo(team.getTime());
    }
}
