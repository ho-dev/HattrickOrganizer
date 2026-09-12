package module.ifa;

import core.db.AbstractTable;
import core.model.WorldDetailsManager;
import core.util.HODateTime;

import static core.model.WorldDetailLeague.HATTRICK_INTERNATIONAL_LEAGUE_ID;

public class IfaMatch extends AbstractTable.Storable {

    private int matchId;
    private int matchTyp;
    private HODateTime playedDate;
    private int homeTeamId;
    private int awayTeamId;
    private int homeLeagueId;
    private int awayLeagueId;
    private Integer homeCountryId;
    private Integer awayCountryId;
    private int awayTeamGoals;
    private int homeTeamGoals;

    /**
     * constructor is used by AbstractTable.load
     */
    public IfaMatch() {
    }

    public IfaMatch(int matchTyp) {
        this.matchTyp = matchTyp;
    }

    public int getMatchTyp() {
        return matchTyp;
    }

    public void setMatchTyp(int v) {
        this.matchTyp = v;
    }

    public final int getMatchId() {
        return matchId;
    }

    public final void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public HODateTime getPlayedDate() {
        return playedDate;
    }

    public void setPlayedDate(HODateTime playedDate) {
        // be defensive, java.util.Date is not immutable
        this.playedDate = playedDate;
    }

    public final int getHomeTeamId() {
        return homeTeamId;
    }

    public final void setHomeTeamId(int homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public final int getAwayTeamId() {
        return awayTeamId;
    }

    public final void setAwayTeamId(int awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public final int getHomeLeagueId() {
        return homeLeagueId;
    }

    public final void setHomeLeagueId(int homeLeagueId) {
        this.homeLeagueId = homeLeagueId;
    }

    public final int getAwayLeagueId() {
        return awayLeagueId;
    }

    public final void setAwayLeagueId(int awayLeagueId) {
        this.awayLeagueId = awayLeagueId;
    }

    public final Integer getHomeCountryId() {
        return getHomeCountryIdWithReload(false);
    }

    /**
     * Get the away team country id.
     * If the id is not available (not yet downloaded to database), the value is fetched from the national league
     * of the match. If requested the value is downloaded from hattrick if even the national league's country id is not available.
     * This may happen in international leagues like HATTRICK_INTERNATIONAL.
     * @param isReload True: Download missing value from hattrick. False: No download
     * @return Integer
     */
    public final Integer getAwayCountryIdWithReload(boolean isReload) {
        if (awayCountryId == null) {
            awayCountryId = getCountryIdFromLeague(awayLeagueId);
            if (awayCountryId == null && isReload) {
                downLoadMatch();
            }
        }
        return awayCountryId;
    }

    /**
     * Get the home team country id.
     * If the id is not available (not yet downloaded to database), the value is fetched from the national league
     * of the match. If requested the value is downloaded from hattrick if even the national league's country id is not available.
     * This may happen in international leagues like HATTRICK_INTERNATIONAL.
     * @param isReload True: Download missing value from hattrick. False: No download
     * @return Integer
     */
    public final Integer getHomeCountryIdWithReload(boolean isReload) {
        if (homeCountryId == null) {
            homeCountryId = getCountryIdFromLeague(homeLeagueId);
            if (homeCountryId == null && isReload) {
                downLoadMatch();
            }
        }
        return homeCountryId;
    }

    /**
     * Get country id from national leagues
     * @param leagueId League Id
     * @return Country id of the national league. Null in case of international leagues.
     */
    private Integer getCountryIdFromLeague(int leagueId) {
        if (leagueId < HATTRICK_INTERNATIONAL_LEAGUE_ID) {
            var league = WorldDetailsManager.instance().getWorldDetailLeagueByLeagueId(leagueId);
            if (league != null) {
                return league.getCountryId();
            }
        }
        return null;
    }

    private void downLoadMatch() {
        PluginIfaUtils.downloadMatch(this);
    }

    public final void setHomeCountryId(Integer id) {
        this.homeCountryId = id;
    }

    public final Integer getAwayCountryId() {
        return getAwayCountryIdWithReload(false);
    }

    public final void setAwayCountryId(Integer id) {
        this.awayCountryId = id;
    }

    public final int getAwayTeamGoals() {
        return awayTeamGoals;
    }

    public final void setAwayTeamGoals(int awayTeamGoals) {
        this.awayTeamGoals = awayTeamGoals;
    }

    public final int getHomeTeamGoals() {
        return homeTeamGoals;
    }

    public final void setHomeTeamGoals(int homeTeamGoals) {
        this.homeTeamGoals = homeTeamGoals;
    }
}
