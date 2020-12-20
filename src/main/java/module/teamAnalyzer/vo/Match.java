// %888293640:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
import core.model.series.Paarung;
import core.util.HTCalendar;
import core.util.HTCalendarFactory;
import module.teamAnalyzer.SystemManager;

import java.util.Date;



/**
 * Match Value object that holds relevant information about the game
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class Match {
    //~ Instance fields ----------------------------------------------------------------------------

    /** The match playing date */
    private Date matchDate;

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
    private MatchType matchType;

    /** HT Season of the game */
    private int season;

    /** HT Week of the game */
    private int week;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new Match object.
     *
     * @param matchInfo IMatckKurtzInfo from which a match object has to be created
     */
    public Match(MatchKurzInfo matchInfo) {
        homeId = matchInfo.getHeimID();
        awayId = matchInfo.getGastID();
        matchId = matchInfo.getMatchID();
        homeTeam = matchInfo.getHeimName();
        awayTeam = matchInfo.getGastName();
        homeGoals = matchInfo.getHeimTore();
        awayGoals = matchInfo.getGastTore();
        matchType = matchInfo.getMatchType();
        matchDate = matchInfo.getMatchDateAsTimestamp();

        Date matchDate = HTCalendar.resetDay(matchInfo.getMatchDateAsTimestamp());        
       
        week = HTCalendarFactory.getHTWeek(matchDate);
        season = HTCalendarFactory.getHTSeason(matchDate);
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

    public void setMatchDate(Date date) {
        matchDate = date;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchId(int i) {
        matchId = i;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchType(MatchType i) {
        matchType = i;
    }

    public MatchType getMatchType() {
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
        StringBuffer buffer = new StringBuffer();

        buffer.append("Match[");
        buffer.append("homeId = " + homeId);
        buffer.append(", awayId = " + awayId);
        buffer.append(", matchId = " + matchId);
        buffer.append(", homeTeam = " + homeTeam);
        buffer.append(", awayTeam = " + awayTeam);
        buffer.append(", homeGoals = " + homeGoals);
        buffer.append(", awayGoals = " + awayGoals);
        buffer.append(", season = " + season);
        buffer.append(", week = " + week);
        buffer.append(", matchType = " + matchType);
        buffer.append(", matchDate = " + matchDate);
        buffer.append("]");

        return buffer.toString();
    }
    
    public boolean isHome() {
        return (getHomeId() == SystemManager.getActiveTeamId());
    }
}
