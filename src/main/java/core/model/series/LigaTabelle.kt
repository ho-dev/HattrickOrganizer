package core.model.series;

import core.util.Helper;

import java.util.Vector;

public class LigaTabelle  {
    //~ Instance fields ----------------------------------------------------------------------------
    protected String m_sLigaLandName = "";
    protected String m_sLigaName = "";
    protected Vector<SerieTableEntry> m_vEintraege = new Vector<SerieTableEntry>();
    protected int m_iLigaId = -1;
    protected int m_iLigaLandId = -1;
    /** Maximale ANzahl an Spielklassen */
    protected int m_iMaxAnzahlSpielklassen = -1;
    /*Wie hoch ist die Liga 1== hoch 6,7 == unten*/
    protected int m_iSpielklasse = -1;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of LigaTabelle
     */
    public LigaTabelle() {
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final Vector<SerieTableEntry> getEntries() {
        return m_vEintraege;
    }

    /**
     * liefert den Eintrag zu einem Team
     */
    public final SerieTableEntry getEintragByTeamId(int teamId) {
        SerieTableEntry tmp = null;

        for (int i = 0; (teamId >= 0) && (i < m_vEintraege.size()); i++) {
            tmp = m_vEintraege.elementAt(i);

            if (tmp.getTeamId() == teamId) {
                return tmp;
            }
        }

        return null;
    }

    /**
     * Setter for property m_iLigaId.
     *
     * @param m_iLigaId New value of property m_iLigaId.
     */
    public final void setLigaId(int m_iLigaId) {
        this.m_iLigaId = m_iLigaId;
    }

    /**
     * Getter for property m_iLigaId.
     *
     * @return Value of property m_iLigaId.
     */
    public final int getLigaId() {
        return m_iLigaId;
    }

    /**
     * Setter for property m_iLigaLandId.
     *
     * @param m_iLigaLandId New value of property m_iLigaLandId.
     */
    public final void setLigaLandId(int m_iLigaLandId) {
        this.m_iLigaLandId = m_iLigaLandId;
    }

    /**
     * Getter for property m_iLigaLandId.
     *
     * @return Value of property m_iLigaLandId.
     */
    public final int getLigaLandId() {
        return m_iLigaLandId;
    }

    /**
     * Setter for property m_sLigaLandName.
     *
     * @param m_sLigaLandName New value of property m_sLigaLandName.
     */
    public final void setLigaLandName(java.lang.String m_sLigaLandName) {
        this.m_sLigaLandName = m_sLigaLandName;
    }

    /**
     * Getter for property m_sLigaLandName.
     *
     * @return Value of property m_sLigaLandName.
     */
    public final java.lang.String getLigaLandName() {
        return m_sLigaLandName;
    }

    /**
     * Setter for property m_sLigaName.
     *
     * @param m_sLigaName New value of property m_sLigaName.
     */
    public final void setLigaName(java.lang.String m_sLigaName) {
        this.m_sLigaName = m_sLigaName;
    }

    /**
     * Getter for property m_sLigaName.
     *
     * @return Value of property m_sLigaName.
     */
    public final java.lang.String getLigaName() {
        return m_sLigaName;
    }

    /**
     * Setter for property m_iMaxAnzahlSpielklassen.
     *
     * @param m_iMaxAnzahlSpielklassen New value of property m_iMaxAnzahlSpielklassen.
     */
    public final void setMaxAnzahlSpielklassen(int m_iMaxAnzahlSpielklassen) {
        this.m_iMaxAnzahlSpielklassen = m_iMaxAnzahlSpielklassen;
    }

    /**
     * Getter for property m_iMaxAnzahlSpielklassen.
     *
     * @return Value of property m_iMaxAnzahlSpielklassen.
     */
    public final int getMaxAnzahlSpielklassen() {
        return m_iMaxAnzahlSpielklassen;
    }

    /**
     * Setter for property m_iSpielklasse.
     *
     * @param m_iSpielklasse New value of property m_iSpielklasse.
     */
    public final void setSpielklasse(int m_iSpielklasse) {
        this.m_iSpielklasse = m_iSpielklasse;
    }

    /**
     * Getter for property m_iSpielklasse.
     *
     * @return Value of property m_iSpielklasse.
     */
    public final int getSpielklasse() {
        return m_iSpielklasse;
    }

    ////////////////////////////////////////////////////////////////////////////////    
    //Logic
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * liefert tendenz zur Platzierung des Teams im Vergleich mit der angegebenen Tabelle -1 ==
     * abgerutscht, 0 = gleich, 1 == aufgestiegen
     */
    public final byte getTeamPlatzTendenz(int teamId, LigaTabelle compare) {
        SerieTableEntry aktu = null;
        SerieTableEntry vergleich = null;

        if (compare != null) {
            aktu = getEintragByTeamId(teamId);
            vergleich = compare.getEintragByTeamId(teamId);

            if ((aktu != null) && (vergleich != null)) {
                if (aktu.getPosition() < vergleich.getPosition()) {
                    return (byte) 1;
                } else if (aktu.getPosition() > vergleich.getPosition()) {
                    return (byte) -1;
                }
            }
        }

        //default
        return (byte) 0;
    }

    public final void addEintrag(SerieTableEntry lte) {
        if ((lte != null) && (!m_vEintraege.contains(lte))) {
            m_vEintraege.add(lte);
        }
    }

    /*
     *Sortiert die Einträge
     */
    public final void sort() {
        SerieTableEntry[] list = new SerieTableEntry[m_vEintraege.size()];
        Helper.copyVector2Array(m_vEintraege, list);

        java.util.Arrays.sort(list);

        //Positionen setzen
        for (int i = 0; i < list.length; i++) {
            list[i].setPosition(i + 1);
        }

        //aufräumen
        m_vEintraege.clear();

        //zurückkopieren
        Helper.copyArray2Vector(list, m_vEintraege);
        list = null;
    }
}
