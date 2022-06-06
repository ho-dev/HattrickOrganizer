// %793684749:de.hattrickorganizer.gui.model%
package core.gui.model;

import core.model.enums.MatchType;
import core.model.match.IMatchType;
import core.model.match.Matchdetails;
import core.model.player.Player;
import core.util.HODateTime;


/**
 * Container für die SpielerMatchTable. Enthält die Daten des Spielers und des zugehörigen Matches
 */
public class PlayerMatchCBItem {
    //~ Instance fields ----------------------------------------------------------------------------

    private Matchdetails m_clMatchdetails;
    private Player m_clPlayer;
    private String m_clGastteam;
    private String m_clHeimteam;
    private HODateTime m_clMatchdate;
    private String m_sSelbstvertrauen;
    private String m_sTeamSpirit;
    private Integer m_iRating; // number of half rating stars
    private int m_iGastID;
    private int m_iHeimID;
    private int m_iMatchID;
    private IMatchType m_mtMatchTyp;
    private int m_iPosition;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerMatchCBItem object.
     */
    public PlayerMatchCBItem(Player player, int matchid, Integer rating, int positionsid,
                             HODateTime matchdate, String heimteam, int heimid, String gastteam,
                             int gastid, MatchType matchtyp, Matchdetails matchdetails,
                             String selbstvertrauen, String stimmung) {
        m_clPlayer = player;
        m_iMatchID = matchid;
        m_iRating = rating;
        m_iPosition = positionsid;
        m_clMatchdate = matchdate;
        m_clHeimteam = heimteam;
        m_iHeimID = heimid;
        m_clGastteam = gastteam;
        m_iGastID = gastid;
        m_clMatchdetails = matchdetails;
        m_sSelbstvertrauen = selbstvertrauen;
        m_sTeamSpirit = stimmung;
        m_mtMatchTyp = matchtyp;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_iGastID.
     *
     * @param m_iGastID New value of property m_iGastID.
     */
    public final void setGastID(int m_iGastID) {
        this.m_iGastID = m_iGastID;
    }

    /**
     * Getter for property m_iGastID.
     *
     * @return Value of property m_iGastID.
     */
    public final int getGastID() {
        return m_iGastID;
    }

    /**
     * Setter for property m_clGastteam.
     *
     * @param m_clGastteam New value of property m_clGastteam.
     */
    public final void setGastteam(java.lang.String m_clGastteam) {
        this.m_clGastteam = m_clGastteam;
    }

    /**
     * Getter for property m_clGastteam.
     *
     * @return Value of property m_clGastteam.
     */
    public final java.lang.String getGuestTeamName() {
        return m_clGastteam;
    }

    /**
     * Setter for property m_iHeimID.
     *
     * @param m_iHeimID New value of property m_iHeimID.
     */
    public final void setHeimID(int m_iHeimID) {
        this.m_iHeimID = m_iHeimID;
    }

    /**
     * Getter for property m_iHeimID.
     *
     * @return Value of property m_iHeimID.
     */
    public final int getHeimID() {
        return m_iHeimID;
    }

    /**
     * Setter for property m_clHeimteam.
     *
     * @param m_clHeimteam New value of property m_clHeimteam.
     */
    public final void setHeimteam(java.lang.String m_clHeimteam) {
        this.m_clHeimteam = m_clHeimteam;
    }

    /**
     * Getter for property m_clHeimteam.
     *
     * @return Value of property m_clHeimteam.
     */
    public final java.lang.String getHomeTeamName() {
        return m_clHeimteam;
    }

    /**
     * Setter for property m_iMatchID.
     *
     * @param m_iMatchID New value of property m_iMatchID.
     */
    public final void setMatchID(int m_iMatchID) {
        this.m_iMatchID = m_iMatchID;
    }

    /**
     * Getter for property m_iMatchID.
     *
     * @return Value of property m_iMatchID.
     */
    public final int getMatchID() {
        return m_iMatchID;
    }

    /**
     * Setter for property m_iMatchTyp.
     *
     * @param m_mtMatchTyp New value of property m_iMatchTyp.
     */
    public final void setMatchTyp(IMatchType m_mtMatchTyp) {
        this.m_mtMatchTyp = m_mtMatchTyp;
    }

    /**
     * Getter for property m_iMatchTyp.
     *
     * @return Value of property m_iMatchTyp.
     */
    public final IMatchType getMatchType() {
        return m_mtMatchTyp;
    }

    /**
     * Setter for property m_clMatchdate.
     *
     * @param m_clMatchdate New value of property m_clMatchdate.
     */
    public final void setMatchdate(HODateTime m_clMatchdate) {
        this.m_clMatchdate = m_clMatchdate;
    }

    /**
     * Getter for property m_clMatchdate.
     *
     * @return Value of property m_clMatchdate.
     */
    public final HODateTime getMatchdate() {
        return m_clMatchdate;
    }

    /**
     * Setter for property m_clMatchdetails.
     *
     * @param m_clMatchdetails New value of property m_clMatchdetails.
     */
    public final void setMatchdetails(core.model.match.Matchdetails m_clMatchdetails) {
        this.m_clMatchdetails = m_clMatchdetails;
    }

    /**
     * Getter for property m_clMatchdetails.
     *
     * @return Value of property m_clMatchdetails.
     */
    public final core.model.match.Matchdetails getMatchdetails() {
        return m_clMatchdetails;
    }

    /**
     * Setter for property m_iPosition.
     *
     * @param m_iPosition New value of property m_iPosition.
     */
    public final void setPosition(int m_iPosition) {
        this.m_iPosition = m_iPosition;
    }

    /**
     * Getter for property m_iPosition.
     *
     * @return Value of property m_iPosition.
     */
    public final int getPosition() {
        return m_iPosition;
    }

    /**
     * Setter for property rating.
     *
     * @param rating number of half rating stars
     */
    public final void setRating(Integer rating) {
        this.m_iRating = rating;
    }

    /**
     * Getter for property m_iRating.
     *
     * @return Integer number of half rating stars
     */
    public final Integer getRating() {
        return m_iRating;
    }

    /**
     * Setter for property m_sSelbstvertrauen.
     *
     * @param m_sSelbstvertrauen New value of property m_sSelbstvertrauen.
     */
    public final void setConfidence(java.lang.String m_sSelbstvertrauen) {
        this.m_sSelbstvertrauen = m_sSelbstvertrauen;
    }

    /**
     * Getter for property m_sSelbstvertrauen.
     *
     * @return Value of property m_sSelbstvertrauen.
     */
    public final java.lang.String getSelbstvertrauen() {
        return m_sSelbstvertrauen;
    }

    /**
     * Setter for property m_clPlayer.
     *
     * @param m_clPlayer New value of property m_clPlayer.
     */
    public final void setSpieler(Player m_clPlayer) {
        this.m_clPlayer = m_clPlayer;
    }

    /**
     * Getter for property m_clPlayer.
     *
     * @return Value of property m_clPlayer.
     */
    public final Player getSpieler() {
        return m_clPlayer;
    }

    /**
     * Setter for property m_sStimmung.
     *
     * @param m_sStimmung New value of property m_sStimmung.
     */
    public final void setTeamSpirit(java.lang.String m_sStimmung) {
        this.m_sTeamSpirit = m_sStimmung;
    }

    /**
     * Getter for property m_sStimmung.
     *
     * @return Value of property m_sStimmung.
     */
    public final java.lang.String getStimmung() {
        return m_sTeamSpirit;
    }
}
