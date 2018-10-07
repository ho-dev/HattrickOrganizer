// %793684749:de.hattrickorganizer.gui.model%
package core.gui.model;

import core.model.match.MatchType;
import core.model.match.Matchdetails;
import core.model.player.Spieler;


/**
 * Container für die SpielerMatchTable. Enthält die Daten des Spielers und des zugehörigen Matches
 */
public class SpielerMatchCBItem {
    //~ Instance fields ----------------------------------------------------------------------------

    private Matchdetails m_clMatchdetails;
    private Spieler m_clSpieler;
    private String m_clGastteam;
    private String m_clHeimteam;
    private String m_clMatchdate;
    private String m_sSelbstvertrauen;
    private String m_sStimmung;
    private float m_fRating;
    private int m_iGastID;
    private int m_iHeimID;
    private int m_iMatchID;
    private MatchType m_mtMatchTyp;
    private int m_iPosition;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerMatchCBItem object.
     */
    public SpielerMatchCBItem(Spieler spieler, int matchid, float rating, int positionsid,
                              String matchdate, String heimteam, int heimid, String gastteam,
                              int gastid, MatchType matchtyp, Matchdetails matchdetails,
                              String selbstvertrauen, String stimmung) {
        m_clSpieler = spieler;
        m_iMatchID = matchid;
        m_fRating = rating;
        m_iPosition = positionsid;
        m_clMatchdate = matchdate;
        m_clHeimteam = heimteam;
        m_iHeimID = heimid;
        m_clGastteam = gastteam;
        m_iGastID = gastid;
        m_clMatchdetails = matchdetails;
        m_sSelbstvertrauen = selbstvertrauen;
        m_sStimmung = stimmung;
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
    public final java.lang.String getGastteam() {
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
    public final java.lang.String getHeimteam() {
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
     * @param m_iMatchTyp New value of property m_iMatchTyp.
     */
    public final void setMatchTyp(MatchType m_mtMatchTyp) {
        this.m_mtMatchTyp = m_mtMatchTyp;
    }

    /**
     * Getter for property m_iMatchTyp.
     *
     * @return Value of property m_iMatchTyp.
     */
    public final MatchType getMatchTyp() {
        return m_mtMatchTyp;
    }

    /**
     * Setter for property m_clMatchdate.
     *
     * @param m_clMatchdate New value of property m_clMatchdate.
     */
    public final void setMatchdate(String m_clMatchdate) {
        this.m_clMatchdate = m_clMatchdate;
    }

    /**
     * Getter for property m_clMatchdate.
     *
     * @return Value of property m_clMatchdate.
     */
    public final String getMatchdate() {
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
     * Setter for property m_fRating.
     *
     * @param m_fRating New value of property m_fRating.
     */
    public final void setRating(float m_fRating) {
        this.m_fRating = m_fRating;
    }

    /**
     * Getter for property m_fRating.
     *
     * @return Value of property m_fRating.
     */
    public final float getRating() {
        return m_fRating;
    }

    /**
     * Setter for property m_sSelbstvertrauen.
     *
     * @param m_sSelbstvertrauen New value of property m_sSelbstvertrauen.
     */
    public final void setSelbstvertrauen(java.lang.String m_sSelbstvertrauen) {
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
     * Setter for property m_clSpieler.
     *
     * @param m_clSpieler New value of property m_clSpieler.
     */
    public final void setSpieler(core.model.player.Spieler m_clSpieler) {
        this.m_clSpieler = m_clSpieler;
    }

    /**
     * Getter for property m_clSpieler.
     *
     * @return Value of property m_clSpieler.
     */
    public final core.model.player.Spieler getSpieler() {
        return m_clSpieler;
    }

    /**
     * Setter for property m_sStimmung.
     *
     * @param m_sStimmung New value of property m_sStimmung.
     */
    public final void setStimmung(java.lang.String m_sStimmung) {
        this.m_sStimmung = m_sStimmung;
    }

    /**
     * Getter for property m_sStimmung.
     *
     * @return Value of property m_sStimmung.
     */
    public final java.lang.String getStimmung() {
        return m_sStimmung;
    }
}
