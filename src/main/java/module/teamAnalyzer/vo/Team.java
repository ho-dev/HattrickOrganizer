// %193722072:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

import core.db.AbstractTable;
import core.model.enums.MatchType;
import core.model.match.IMatchType;
import core.util.HODateTime;

/**
 * Team Object Class
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class Team extends AbstractTable.Storable implements Comparable<Team> {

    public Team(){}

    /** Name of the team */
    private String name;

    /** Team id */
    private int teamId;

    private int iMatchID;
 
    // A hack for custom coloring of tournament teams in a renderer
    private boolean tournament = false;

    private IMatchType matchType;

    // Timestamp when next match is played
    private HODateTime time;

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

    public void setTime(HODateTime t) {
        time = t;
    }

    public HODateTime getTime() {
        return time;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setMatchType(IMatchType matchType) {
        this.matchType = matchType;
    }

    public IMatchType getMatchType() {
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

    public boolean isTemplate() {
        return teamId < 0 && matchType == MatchType.NONE && iMatchID == -1;
    }
}
