// %2139958143:de.hattrickorganizer.model%
package core.model.series;

import core.db.AbstractTable;

/**
 * Enthält alle Ligadaten
 */
public final class Liga extends AbstractTable.Storable {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Liganame */
    private String m_sLiga = "";

    /** Plazierung */
    private int m_iPlatzierung;

    /** Punkte */
    private int m_iPunkte;

    /** Spieltag */
    private int m_iSpieltag;

    /** ToreFuer */
    private int m_iToreFuer;

    /** ToreGegen */
    private int m_iToreGegen;
    private int hrfId;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new Liga object.
     */
    public Liga(java.util.Properties properties) {
        m_sLiga = properties.getProperty("serie", "");

        try {
            m_iPunkte = Integer.parseInt(properties.getProperty("poang", "0"));
        } catch (Exception e) {
            m_iPunkte = 0;
        }

        try {
            m_iToreGegen = Integer.parseInt(properties.getProperty("inslappta", "0"));
        } catch (Exception e) {
            m_iToreGegen = 0;
        }

        try {
            m_iToreFuer = Integer.parseInt(properties.getProperty("gjorda", "0"));
        } catch (Exception e) {
            m_iToreFuer = 0;
        }

        try {
            m_iSpieltag = Integer.parseInt(properties.getProperty("spelade", "0"));
        } catch (Exception e) {
            m_iSpieltag = 0;
        }

        //.. fehlen noch Einträge, Bedeutung unklar
        try {
            m_iPlatzierung = Integer.parseInt(properties.getProperty("placering", "0"));
        } catch (Exception e) {
            m_iPlatzierung = 0;
        }
    }

    /**
     * Creates a new Liga object.
     */
    public Liga() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_sLiga.
     *
     * @param m_sLiga New value of property m_sLiga.
     */
    public void setLiga(java.lang.String m_sLiga) {
        this.m_sLiga = m_sLiga;
    }

    /**
     * Getter for property m_sLiga.
     *
     * @return Value of property m_sLiga.
     */
    public java.lang.String getLiga() {
        return m_sLiga;
    }

    /**
     * Setter for property m_iPlatzierung.
     *
     * @param m_iPlatzierung New value of property m_iPlatzierung.
     */
    public void setPlatzierung(int m_iPlatzierung) {
        this.m_iPlatzierung = m_iPlatzierung;
    }

    /**
     * Getter for property m_iPlatzierung.
     *
     * @return Value of property m_iPlatzierung.
     */
    public int getPlatzierung() {
        return m_iPlatzierung;
    }

    /**
     * Setter for property m_iPunkte.
     *
     * @param m_iPunkte New value of property m_iPunkte.
     */
    public void setPunkte(int m_iPunkte) {
        this.m_iPunkte = m_iPunkte;
    }

    /**
     * Getter for property m_iPunkte.
     *
     * @return Value of property m_iPunkte.
     */
    public int getPunkte() {
        return m_iPunkte;
    }

    /**
     * Setter for property m_iSpieltag.
     *
     * @param m_iSpieltag New value of property m_iSpieltag.
     */
    public void setSpieltag(int m_iSpieltag) {
        this.m_iSpieltag = m_iSpieltag;
    }

    /**
     * Getter for property m_iSpieltag.
     *
     * @return Value of property m_iSpieltag.
     */
    public int getSpieltag() {
        return m_iSpieltag;
    }

    /**
     * Setter for property m_iToreFuer.
     *
     * @param m_iToreFuer New value of property m_iToreFuer.
     */
    public void setToreFuer(int m_iToreFuer) {
        this.m_iToreFuer = m_iToreFuer;
    }

    /**
     * Getter for property m_iToreFuer.
     *
     * @return Value of property m_iToreFuer.
     */
    public int getToreFuer() {
        return m_iToreFuer;
    }

    /**
     * Setter for property m_iToreGegen.
     *
     * @param m_iToreGegen New value of property m_iToreGegen.
     */
    public void setToreGegen(int m_iToreGegen) {
        this.m_iToreGegen = m_iToreGegen;
    }

    /**
     * Getter for property m_iToreGegen.
     *
     * @return Value of property m_iToreGegen.
     */
    public int getToreGegen() {
        return m_iToreGegen;
    }

    public int getHrfId() {
        return hrfId;
    }

    public void setHrfId(int hrfId) {
        this.hrfId = hrfId;
    }
}
