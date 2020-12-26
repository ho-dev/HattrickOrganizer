package core.model.match;

import core.db.DBManager;
import core.util.HOLogger;

public class MatchLineup {

    protected int homeTeamId = -1;
    protected int matchId = -1;
    protected MatchType m_MatchTyp = MatchType.NONE;
    MatchLineupTeam guestTeam;
    MatchLineupTeam homeTeam;
    private String arenaName = "";
    private String downloadDate = "";
    private String guestTeamName = "";
    private String homeTeamName = "";
    private String matchDate = "";
    private int arenaId = -1;
    private int guestTeamId = -1;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of MatchLineup
     */
    public MatchLineup() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_iArenaID.
     *
     * @param m_iArenaID New value of property m_iArenaID.
     */
    public final void setArenaID(int m_iArenaID) {
        this.arenaId = m_iArenaID;
    }

    /**
     * Getter for property m_iArenaID.
     *
     * @return Value of property m_iArenaID.
     */
    public final int getArenaID() {
        return arenaId;
    }

    /**
     * Setter for property m_sArenaName.
     *
     * @param m_sArenaName New value of property m_sArenaName.
     */
    public final void setArenaName(java.lang.String m_sArenaName) {
        this.arenaName = m_sArenaName;
    }

    /**
     * Getter for property m_sArenaName.
     *
     * @return Value of property m_sArenaName.
     */
    public final java.lang.String getArenaName() {
        return arenaName;
    }

    /**
     * Setter for property m_lDatum.
     *
     * @param date New value of property m_lDatum.
     */
    public final void setDownloadDate(String date) {
        if (date != null) {
            downloadDate = date;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //Accessor
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for property m_lDatum.
     *
     * @return Value of property m_lDatum.
     */
    public final java.sql.Timestamp getDownloadDate() {
        try {
            //Hattrick
            final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                                                                           java.util.Locale.GERMANY);

            return new java.sql.Timestamp(simpleFormat.parse(downloadDate).getTime());
        } catch (Exception e) {
            try {
                //Hattrick
                final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd",
                                                                                               java.util.Locale.GERMANY);

                return new java.sql.Timestamp(simpleFormat.parse(downloadDate).getTime());
            } catch (Exception ex) {
                HOLogger.instance().log(getClass(),ex);
            }
        }

        return null;
    }

    /**
     * Setter for property m_clGast.
     *
     * @param m_clGast New value of property m_clGast.
     */
    public final void setGuestTeamId(MatchLineupTeam m_clGast) {
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
            guestTeam = DBManager.instance().getMatchLineupTeam(this.getSourceSystem().getValue(), this.matchId, this.guestTeamId);
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
            homeTeam = DBManager.instance().getMatchLineupTeam(this.getSourceSystem().getValue(), this.matchId, this.getHomeTeamId());
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
        return homeTeamId;
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
     * Setter for property m_lDatum.
     *
     * @param date New value of property m_lDatum.
     */
    public final void setMatchDate(String date) {
        if (date != null) {
            matchDate = date;
        }
    }

    /**
     * Getter for property m_lDatum.
     *
     * @return Value of property m_lDatum.
     */
    public final java.sql.Timestamp getMatchDate() {
        try {
            //Hattrick
            final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                                                                           java.util.Locale.GERMANY);

            return new java.sql.Timestamp(simpleFormat.parse(matchDate).getTime());
        } catch (Exception e) {
            try {
                //Hattrick
                final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd",
                                                                                               java.util.Locale.GERMANY);

                return new java.sql.Timestamp(simpleFormat.parse(matchDate).getTime());
            } catch (Exception ex) {
                HOLogger.instance().log(getClass(),ex);
            }
        }

        return null;
    }

    public final String getStringDownloadDate() {
        return downloadDate;
    }

    public final String getStringMatchDate() {
        return matchDate;
    }

    public SourceSystem getSourceSystem() {
        return this.getMatchTyp().getSourceSystem();
    }

    public MatchLineupTeam getTeam(Integer teamId) {
        if ( teamId == this.homeTeamId){
            return this.getHomeTeam();
        }
        else if ( teamId == this.guestTeamId){
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
}
