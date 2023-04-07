package tool.arenasizer;

import core.db.AbstractTable;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Enthält die Stadiendaten
 */
public class Stadium extends AbstractTable.Storable {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Stadienname */
    private String m_sStadienname = "";

    /** Stadium Id */
    private int m_iStadiumId;

    /** Wird ausgebaut? */
    private boolean m_bAusbau;

    /** Ausbaukosten */
    private int m_iAusbauKosten;

    /** Ausbau Logen */
    private int m_iAusbauLogen;

    /** Ausbau Sitzplätze */
    private int m_iAusbauSitzplaetze;

    /** Ausbau Stehplätze */
    private int m_iAusbauStehplaetze;

    /** Ausbau Überdachte Sitzplätze */
    private int m_iAusbauUeberdachteSitzplaetze;

    /** Logen */
    private int m_iLogen;

    /** Sitzplätze */
    private int m_iSitzplaetze;

    /** Stehplätze */
    private int m_iStehplaetze;

    /** Überdachte Sitzplätze */
    private int m_iUeberdachteSitzplaetze;
    private int hrfId;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new Stadium object.
     */
    public Stadium() {
    }

    /**
     * Creates a new Stadium object.
     */
    public Stadium(java.util.Properties properties) {
        m_sStadienname = properties.getProperty("arenaname", "");
        m_iStadiumId = NumberUtils.toInt(properties.getProperty("arenaid"),0);
        //m_iGesamtgroesse = NumberUtils.toInt(properties.getProperty("seattotal"),0);
        m_iStehplaetze = NumberUtils.toInt(properties.getProperty("antalstaplats"),0);
        m_iSitzplaetze = NumberUtils.toInt(properties.getProperty("antalsitt"),0);
        m_iUeberdachteSitzplaetze = NumberUtils.toInt(properties.getProperty("antaltak"),0);
        m_iLogen = NumberUtils.toInt(properties.getProperty("antalvip"),0);
        m_iAusbauStehplaetze = NumberUtils.toInt(properties.getProperty("expandingstaplats"),0);
        m_iAusbauSitzplaetze = NumberUtils.toInt(properties.getProperty("expandingsitt"),0);
        m_iAusbauUeberdachteSitzplaetze = NumberUtils.toInt(properties.getProperty("expandingtak"),0);
        m_iAusbauLogen = NumberUtils.toInt(properties.getProperty("expandingvip"),0);
        m_bAusbau = NumberUtils.toInt(properties.getProperty("isexpanding"), 0) > 0;
        if (m_bAusbau) {
            m_iAusbauKosten = NumberUtils.toInt(properties.getProperty("expandcost"),0);
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_bAusbau.
     *
     * @param m_bAusbau New value of property m_bAusbau.
     */
    public final void setAusbau(boolean m_bAusbau) {
        this.m_bAusbau = m_bAusbau;
    }

    /**
     * Getter for property m_bAusbau.
     *
     * @return Value of property m_bAusbau.
     */
    public final boolean isAusbau() {
        return m_bAusbau;
    }

    /**
     * Setter for property m_iAusbauKosten.
     *
     * @param m_iAusbauKosten New value of property m_iAusbauKosten.
     */
    public final void setAusbauKosten(int m_iAusbauKosten) {
        this.m_iAusbauKosten = m_iAusbauKosten;
    }

    /**
     * Getter for property m_iAusbauKosten.
     *
     * @return Value of property m_iAusbauKosten.
     */
    public final int getAusbauKosten() {
        return m_iAusbauKosten;
    }

    /**
     * Setter for property m_iAusbauLogen.
     *
     * @param m_iAusbauLogen New value of property m_iAusbauLogen.
     */
    public final void setAusbauLogen(int m_iAusbauLogen) {
        this.m_iAusbauLogen = m_iAusbauLogen;
    }

    /**
     * Getter for property m_iAusbauLogen.
     *
     * @return Value of property m_iAusbauLogen.
     */
    public final int getAusbauLogen() {
        return m_iAusbauLogen;
    }

    /**
     * Setter for property m_iAusbauSitzplaetze.
     *
     * @param m_iAusbauSitzplaetze New value of property m_iAusbauSitzplaetze.
     */
    public final void setAusbauSitzplaetze(int m_iAusbauSitzplaetze) {
        this.m_iAusbauSitzplaetze = m_iAusbauSitzplaetze;
    }

    /**
     * Getter for property m_iAusbauSitzplaetze.
     *
     * @return Value of property m_iAusbauSitzplaetze.
     */
    public final int getAusbauSitzplaetze() {
        return m_iAusbauSitzplaetze;
    }

    /**
     * Setter for property m_iAusbauStehplaetze.
     *
     * @param m_iAusbauStehplaetze New value of property m_iAusbauStehplaetze.
     */
    public final void setAusbauStehplaetze(int m_iAusbauStehplaetze) {
        this.m_iAusbauStehplaetze = m_iAusbauStehplaetze;
    }

    /**
     * Getter for property m_iAusbauStehplaetze.
     *
     * @return Value of property m_iAusbauStehplaetze.
     */
    public final int getAusbauStehplaetze() {
        return m_iAusbauStehplaetze;
    }

    /**
     * Setter for property m_iAusbauUeberdachteSitzplaetze.
     *
     * @param m_iAusbauUeberdachteSitzplaetze New value of property
     *        m_iAusbauUeberdachteSitzplaetze.
     */
    public final void setAusbauUeberdachteSitzplaetze(int m_iAusbauUeberdachteSitzplaetze) {
        this.m_iAusbauUeberdachteSitzplaetze = m_iAusbauUeberdachteSitzplaetze;
    }

    /**
     * Getter for property m_iAusbauUeberdachteSitzplaetze.
     *
     * @return Value of property m_iAusbauUeberdachteSitzplaetze.
     */
    public final int getAusbauUeberdachteSitzplaetze() {
        return m_iAusbauUeberdachteSitzplaetze;
    }


    ////////////////////////////Accessor////////////////////////////////////////    

    /**
     * Getter for property m_iGesamtgroesse.
     *
     * @return Value of property m_iGesamtgroesse.
     */
    public final int getGesamtgroesse() {
        return getStehplaetze() + getSitzplaetze() + getUeberdachteSitzplaetze() + getLogen();
    }

    /**
     * Setter for property m_iLogen.
     *
     * @param m_iLogen New value of property m_iLogen.
     */
    public final void setLogen(int m_iLogen) {
        this.m_iLogen = m_iLogen;
    }

    /**
     * Getter for property m_iLogen.
     *
     * @return Value of property m_iLogen.
     */
    public final int getLogen() {
        return m_iLogen;
    }

    /**
     * Setter for property m_iSitzplaetze.
     *
     * @param m_iSitzplaetze New value of property m_iSitzplaetze.
     */
    public final void setSitzplaetze(int m_iSitzplaetze) {
        this.m_iSitzplaetze = m_iSitzplaetze;
    }

    /**
     * Getter for property m_iSitzplaetze.
     *
     * @return Value of property m_iSitzplaetze.
     */
    public final int getSitzplaetze() {
        return m_iSitzplaetze;
    }

    /**
     * Setter for property m_sStadienname.
     *
     * @param m_sStadienname New value of property m_sStadienname.
     */
    public final void setStadienname(java.lang.String m_sStadienname) {
        this.m_sStadienname = m_sStadienname;
    }

    /**
     * Getter for property m_sStadienname.
     *
     * @return Value of property m_sStadienname.
     */
    public final java.lang.String getStadienname() {
        return m_sStadienname;
    }

    /**
     * Setter for property m_iStehplaetze.
     *
     * @param m_iStehplaetze New value of property m_iStehplaetze.
     */
    public final void setStehplaetze(int m_iStehplaetze) {
        this.m_iStehplaetze = m_iStehplaetze;
    }

    /**
     * Getter for property m_iStehplaetze.
     *
     * @return Value of property m_iStehplaetze.
     */
    public final int getStehplaetze() {
        return m_iStehplaetze;
    }

    /**
     * Setter for property m_iUeberdachteSitzplaetze.
     *
     * @param m_iUeberdachteSitzplaetze New value of property m_iUeberdachteSitzplaetze.
     */
    public final void setUeberdachteSitzplaetze(int m_iUeberdachteSitzplaetze) {
        this.m_iUeberdachteSitzplaetze = m_iUeberdachteSitzplaetze;
    }

    /**
     * Getter for property m_iUeberdachteSitzplaetze.
     *
     * @return Value of property m_iUeberdachteSitzplaetze.
     */
    public final int getUeberdachteSitzplaetze() {
        return m_iUeberdachteSitzplaetze;
    }

    /**
     * Get the Arena ID
     * @return	arenaId
     */
    public int getArenaId () {
    	return m_iStadiumId;
    }
    
    /**
     * Set the Arena ID
     * @param arenaId	the new value
     */
    public void setArenaId (int arenaId) {
    	this.m_iStadiumId = arenaId;
    }

    public int getHrfId() {
        return hrfId;
    }

    public void setHrfId(int hrfId) {
        this.hrfId = hrfId;
    }
}
