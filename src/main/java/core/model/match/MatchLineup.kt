package core.model.match;

import core.db.AbstractTable;
import core.db.DBManager;
import core.model.enums.MatchType;
import core.util.HODateTime;

public class MatchLineup extends AbstractTable.Storable {

    protected int homeTeamId = -1;
    protected int matchId = -1;
    protected MatchType m_MatchTyp = MatchType.NONE;
    MatchLineupTeam guestTeam;
    MatchLineupTeam homeTeam;
    private String guestTeamName = null;
    private String homeTeamName = null;
    private HODateTime matchDate;
    private int guestTeamId = -1;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of MatchLineup
     */
    public MatchLineup() {
    }

    /**
     * Setter for property m_clGast.
     *
     * @param m_clGast New value of property m_clGast.
     */
    public final void setGuestTeam(MatchLineupTeam m_clGast) {
        this.guestTeam = m_clGast;
    }

    /**
     * Getter for property guest Team.
     * Team is loaded, if not done already
     *
     * @return guest team match lineup.
     */
    public MatchLineupTeam getGuestTeam() {
        if ( guestTeam == null){
            guestTeam = DBManager.instance().loadMatchLineupTeam(this.getMatchType().getId(), this.matchId, this.getGuestTeamId());
        }
        return guestTeam;
    }

    /**
     * Setter for property m_iGastId.
     *
     * @param m_iGastId New value of property m_iGastId.
     */
    public final void setGuestTeamId(int m_iGastId) {
        this.guestTeamId = m_iGastId;
    }

    /**
     * Getter for property m_iGastId.
     *
     * @return Value of property m_iGastId.
     */
    public final int getGuestTeamId() {
        if ( guestTeamId == -1) {
            init();
        }
        return guestTeamId;
    }

    /**
     * Setter for property m_sGastName.
     *
     * @param m_sGastName New value of property m_sGastName.
     */
    public final void setGuestTeamName(String m_sGastName) {
        this.guestTeamName = m_sGastName;
    }

    /**
     * Getter for property m_sGastName.
     *
     * @return Value of property m_sGastName.
     */
    public final String getGuestTeamName() {
        if (guestTeamName == null) {
            init();
        }
        return guestTeamName;
    }

    /**
     * Setter for property m_clHeim.
     *
     * @param m_clHeim New value of property m_clHeim.
     */
    public final void setHomeTeam(MatchLineupTeam m_clHeim) {
        this.homeTeam = m_clHeim;
    }

    /**
     * Get home team
     * team is loaded if not done already
     *
     * @return home team match lineup
     */
    public final MatchLineupTeam getHomeTeam() {
        if ( homeTeam == null){
            homeTeam = DBManager.instance().loadMatchLineupTeam(this.getMatchType().getId(), this.matchId, this.getHomeTeamId());
        }
        return homeTeam;
    }

    /**
     * Setter for property m_iHeimId.
     *
     * @param m_iHeimId New value of property m_iHeimId.
     */
    public final void setHomeTeamId(int m_iHeimId) {
        this.homeTeamId = m_iHeimId;
    }

    /**
     * Getter for property m_iHeimId.
     *
     * @return Value of property m_iHeimId.
     */
    public final int getHomeTeamId() {
        if ( homeTeamId == -1) {
            init();
        }
        return homeTeamId;
    }

    private void init() {
        var match = DBManager.instance().loadMatchDetails(this.m_MatchTyp.getId(), this.matchId);
        homeTeamId = match.getHomeTeamId();
        homeTeamName = match.getHomeTeamName();
        guestTeamId = match.getGuestTeamId();
        guestTeamName = match.getGuestTeamName();
        matchDate = match.getMatchDate();
    }

    /**
     * Setter for property m_sHeimName.
     *
     * @param m_sHeimName New value of property m_sHeimName.
     */
    public final void setHomeTeamName(String m_sHeimName) {
        this.homeTeamName = m_sHeimName;
    }

    /**
     * Getter for property m_sHeimName.
     *
     * @return Value of property m_sHeimName.
     */
    public final String getHomeTeamName() {
        if (homeTeamName == null) {
            init();
        }
        return homeTeamName;
    }

    /**
     * Setter for property m_iMatchID.
     *
     * @param m_iMatchID New value of property m_iMatchID.
     */
    public final void setMatchID(int m_iMatchID) {
        this.matchId = m_iMatchID;
    }

    /**
     * Getter for property m_iMatchID.
     *
     * @return Value of property m_iMatchID.
     */
    public final int getMatchID() {
        return matchId;
    }

    public final MatchType getMatchType() {
        return m_MatchTyp;
    }

    /**
     * Setter for property m_iMatchTyp.
     *
     * @param matchTyp New value of property m_iMatchTyp.
     */
    public final void setMatchTyp(MatchType matchTyp) {
        this.m_MatchTyp = matchTyp;
    }

    /**
     * Getter for property m_iMatchTyp.
     *
     * @return Value of property m_iMatchTyp.
     */
    public final MatchType getMatchTyp() {
        return m_MatchTyp;
    }

    /**
     * Getter for property m_lDatum.
     *
     * @return Value of property m_lDatum.
     */
    public HODateTime getMatchDate() {
        if ( this.matchDate == null){
            init();
        }
        return this.matchDate;
    }

    public MatchLineupTeam getTeam(Integer teamId) {
        if ( teamId == this.getHomeTeamId()){
            return this.getHomeTeam();
        }
        else if ( teamId == this.getGuestTeamId()){
            return this.getGuestTeam();
        }
        return null;
    }

    public void setMatchDetails(Matchdetails details) {
        if ( this.guestTeam != null){
            this.guestTeam.setMatchDetails(details);
        }
        if ( this.homeTeam != null){
            this.homeTeam.setMatchDetails(details);
        }
    }

    public boolean isHomeTeamNotLoaded() {
        return this.homeTeam == null;
    }

    public boolean isGuestTeamLoaded() {
        return this.guestTeam != null;
    }
}
