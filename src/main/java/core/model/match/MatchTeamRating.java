package core.model.match;

import core.db.AbstractTable;
import core.model.enums.MatchType;
import core.util.HOLogger;
import java.util.Map;

public class MatchTeamRating extends AbstractTable.Storable {

    private int matchId;
    private MatchType matchTyp;
    private int teamId;
    private int fanclubSize;
    private int powerRating;
    private int globalRanking;
    private int leagueRanking;
    private int regionRanking;
    private int numberOfVictories;
    private int numberOfUndefeated;

    /**
     * constructor is needed by AbstractTable.load
     */
    public MatchTeamRating(){}

    public MatchTeamRating(int matchID, MatchType matchType, Map<String, String> download) {
        this.matchId=matchID;
        this.matchTyp=matchType;
        this.teamId = getInt( download, "TeamID");
        this.fanclubSize = getInt(download, "FanclubSize");
        this.powerRating = getInt(download, "PowerRating");
        this.globalRanking = getInt(download,  "GlobalRanking");
        this.leagueRanking = getInt(download,  "LeagueRanking");
        this.regionRanking = getInt(download,  "RegionRanking");
        this.numberOfUndefeated = getInt(download, "NumberOfUndefeated");
        this.numberOfVictories = getInt(download, "NumberOfVictories");
    }

    private int getInt(Map<String, String> download, String key) {
        try {
            var s = download.get(key);
            if (s != null && s.length() > 0) return Integer.parseInt(s);
        } catch (Exception e) {
            HOLogger.instance().warning(getClass(), "getInt: " + e);
        }
        return -1;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public MatchType getMatchTyp() {
        return matchTyp;
    }

    public void setMatchTyp(MatchType matchTyp) {
        this.matchTyp = matchTyp;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getFanclubSize() {
        return fanclubSize;
    }

    public void setFanclubSize(int fanclubSize) {
        this.fanclubSize = fanclubSize;
    }

    public int getPowerRating() {
        return powerRating;
    }

    public void setPowerRating(Integer powerRating) {
        if ( powerRating != null ) this.powerRating = powerRating;
    }

    public int getGlobalRanking() {
        return globalRanking;
    }

    public void setGlobalRanking(Integer globalRanking) {
        if ( globalRanking != null ) this.globalRanking = globalRanking;
    }

    public int getLeagueRanking() {
        return leagueRanking;
    }

    public void setLeagueRanking(Integer leagueRanking) {
        if ( leagueRanking!= null) this.leagueRanking = leagueRanking;
    }

    public int getRegionRanking() {
        return regionRanking;
    }

    public void setRegionRanking(Integer regionRanking) {
       if ( regionRanking != null)  this.regionRanking = regionRanking;
    }

    public int getNumberOfVictories() {
        return numberOfVictories;
    }

    public void setNumberOfVictories(Integer numberOfVictories) {
        if ( numberOfVictories != null ) this.numberOfVictories = numberOfVictories;
    }

    public int getNumberOfUndefeated() {
        return numberOfUndefeated;
    }

    public void setNumberOfUndefeated(Integer numberOfUndefeated) {
        if ( numberOfUndefeated != null ) this.numberOfUndefeated = numberOfUndefeated;
    }
}
