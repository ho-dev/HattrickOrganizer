// %888293640:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

import core.model.match.IMatchType;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.model.series.Paarung;
import core.util.HODateTime;
import module.teamAnalyzer.SystemManager;


/**
 * Match Value object that holds relevant information about the game
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class Match {
    //~ Instance fields ----------------------------------------------------------------------------

    /** The match playing date */
    private HODateTime matchDate;

    /** The away team name */
    private String awayTeam;

    /** The home team name */
    private String homeTeam;

    /** Goals scored by the home team */
    private int awayGoals;

    /** The away team id */
    private int awayId;

    /** Goals scored by the home team */
    private int homeGoals;

    /** The home team id */
    private int homeId;

    /** The match id */
    private int matchId;

    /** The match type */
    private IMatchType matchType;

    /** HT Season of the game */
    private int season;

    /** HT Week of the game */
    private int week;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new Match object.
     *
     * @param matchInfo MatchKurzInfo from which a match object has to be created
     */
    public Match(MatchKurzInfo matchInfo) {
        homeId = matchInfo.getHomeTeamID();
        awayId = matchInfo.getGuestTeamID();
        matchId = matchInfo.getMatchID();
        homeTeam = matchInfo.getHomeTeamName();
        awayTeam = matchInfo.getGuestTeamName();
        homeGoals = matchInfo.getHomeTeamGoals();
        awayGoals = matchInfo.getGuestTeamGoals();
        matchType = matchInfo.getMatchTypeExtended();
        matchDate = matchInfo.getMatchSchedule();

        var htweek = matchDate.toLocaleHTWeek();
        week = htweek.week;
        season = htweek.season;
    }

    /**
     * Creates a new Match object.
     *
     * @param matchInfo IPaarung from which a match object has to be created
     */
    public Match(Paarung matchInfo) {
        homeId = matchInfo.getHeimId();
        awayId = matchInfo.getGastId();
        matchId = matchInfo.getMatchId();
        homeTeam = matchInfo.getHeimName();
        awayTeam = matchInfo.getGastName();
        homeGoals = matchInfo.getToreHeim();
        awayGoals = matchInfo.getToreGast();
        season = matchInfo.getSaison();
        week = matchInfo.getSpieltag();
        matchType = MatchType.LEAGUE;
        matchDate = matchInfo.getDatum();
    }

    /**
     * Creates a new empty Match object.
     */
    public Match() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    public void setAwayGoals(int i) {
        awayGoals = i;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayId(int i) {
        awayId = i;
    }

    public int getAwayId() {
        return awayId;
    }

    public void setAwayTeam(String string) {
        awayTeam = string;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setHomeGoals(int i) {
        homeGoals = i;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeId(int i) {
        homeId = i;
    }

    public int getHomeId() {
        return homeId;
    }

    public void setHomeTeam(String string) {
        homeTeam = string;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setMatchDate(HODateTime date) {
        matchDate = date;
    }

    public HODateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchId(int i) {
        matchId = i;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchType(IMatchType i) {
        matchType = i;
    }

    public IMatchType getMatchType() {
        return matchType;
    }

    public void setSeason(int i) {
        season = i;
    }

    public int getSeason() {
        return season;
    }

    public void setWeek(int i) {
        week = i;
    }

    public int getWeek() {
        return week;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public String toString() {
        return "Match[" +
                "homeId = " + homeId +
                ", awayId = " + awayId +
                ", matchId = " + matchId +
                ", homeTeam = " + homeTeam +
                ", awayTeam = " + awayTeam +
                ", homeGoals = " + homeGoals +
                ", awayGoals = " + awayGoals +
                ", season = " + season +
                ", week = " + week +
                ", matchType = " + matchType +
                ", matchDate = " + matchDate +
                "]";
    }
    
    public boolean isHome() {
        return (getHomeId() == SystemManager.getActiveTeamId());
    }
}
