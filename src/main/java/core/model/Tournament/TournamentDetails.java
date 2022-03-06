package core.model.Tournament;

import core.util.HODateTime;

import java.util.Date;

public class TournamentDetails {

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(int tournamentType) {
        this.tournamentType = tournamentType;
    }

    public short getSeason() {
        return season;
    }

    public void setSeason(short season) {
        this.season = season;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int getTrophyType() {
        return trophyType;
    }

    public void setTrophyType(int trophyType) {
        this.trophyType = trophyType;
    }

    public int getNumberOfTeams() {
        return NumberOfTeams;
    }

    public void setNumberOfTeams(int numberOfTeams) {
        NumberOfTeams = numberOfTeams;
    }

    public int getNumberOfGroups() {
        return NumberOfGroups;
    }

    public void setNumberOfGroups(int numberOfGroups) {
        NumberOfGroups = numberOfGroups;
    }

    public short getLastMatchRound() {
        return lastMatchRound;
    }

    public void setLastMatchRound(short lastMatchRound) {
        this.lastMatchRound = lastMatchRound;
    }

    public HODateTime getFirstMatchRoundDate() {
        return firstMatchRoundDate;
    }

    public void setFirstMatchRoundDate(HODateTime firstMatchRoundDate) {
        this.firstMatchRoundDate = firstMatchRoundDate;
    }

    public HODateTime getNextMatchRoundDate() {
        return nextMatchRoundDate;
    }

    public void setNextMatchRoundDate(HODateTime nextMatchRoundDate) {
        this.nextMatchRoundDate = nextMatchRoundDate;
    }

    public Boolean getMatchesOngoing() {
        return isMatchesOngoing;
    }

    public void setMatchesOngoing(Boolean matchesOngoing) {
        isMatchesOngoing = matchesOngoing;
    }

    public int getCreator_UserId() {
        return creator_UserId;
    }

    public void setCreator_UserId(int creator_UserId) {
        this.creator_UserId = creator_UserId;
    }

    public String getCreator_Loginname() {
        return creator_Loginname;
    }

    public void setCreator_Loginname(String creator_Loginname) {
        this.creator_Loginname = creator_Loginname;
    }

    private int tournamentId; // The globally unique id for the tournament
    private String name; // The name of the tournament
    private int tournamentType;  // The type of tournament
    private short season; // The current Season number
    private String logoUrl; // The url to the logo.
    private int trophyType; // The type of trophy.
    private int NumberOfTeams; // The total number of teams.
    private int NumberOfGroups; // The total number of groups.
    private short lastMatchRound; // Last finished match round.
    private HODateTime firstMatchRoundDate; // The date of the first match round.
    private HODateTime nextMatchRoundDate; // The date of the next match round. During matches this will be the Next match round.
    private Boolean isMatchesOngoing; // Whether or not the match is currently being played.
    private int creator_UserId; // The globally unique user id of the tournament creator
    private String creator_Loginname; //The 'username' or 'nickname' of the tournament creator


}
