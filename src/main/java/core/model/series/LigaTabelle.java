package core.model.series;

import core.util.Helper;

import java.util.Comparator;
import java.util.Vector;
import java.util.stream.Collectors;

public class LigaTabelle  {
    //~ Instance fields ----------------------------------------------------------------------------
    protected String m_sLigaLandName = "";
    protected String m_sLigaName = "";
    protected Vector<SerieTableEntry> m_vEintraege = new Vector<>();
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
        SerieTableEntry tmp;

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
     * Setter for property m_iLigaLandId.
     *
     * @param m_iLigaLandId New value of property m_iLigaLandId.
     */
    public final void setLigaLandId(int m_iLigaLandId) {
        this.m_iLigaLandId = m_iLigaLandId;
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
     * Setter for property m_sLigaName.
     *
     * @param m_sLigaName New value of property m_sLigaName.
     */
    public final void setLigaName(java.lang.String m_sLigaName) {
        this.m_sLigaName = m_sLigaName;
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
     * Setter for property m_iSpielklasse.
     *
     * @param m_iSpielklasse New value of property m_iSpielklasse.
     */
    public final void setSpielklasse(int m_iSpielklasse) {
        this.m_iSpielklasse = m_iSpielklasse;
    }

    public final void addEintrag(SerieTableEntry lte) {
        if ((lte != null) && (!m_vEintraege.contains(lte))) {
            m_vEintraege.add(lte);
        }
    }

    /*
     * Sort series table entries
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
    }

    public void sortByPosition() {
        m_vEintraege = m_vEintraege.stream().sorted(Comparator.comparing(SerieTableEntry::getPosition)).collect(Collectors.toCollection(Vector::new));
    }
}
