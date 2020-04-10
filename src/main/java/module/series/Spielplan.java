package module.series;

import core.model.series.*;
import core.util.HOLogger;
import core.util.Helper;

import java.sql.Timestamp;
import java.util.Vector;


public class Spielplan  {
    //~ Instance fields ----------------------------------------------------------------------------

    /*LigaTabellen Member für Schnellzugriff nach Berechnung*/
    protected LigaTabelle m_clTabelle;
    protected String m_sLigaName = "";
    protected Tabellenverlauf m_clVerlauf;
    protected Timestamp m_clFetchDate;
    protected Vector<Paarung> m_vEintraege = new Vector<Paarung>();
    protected int m_iLigaId = -1;
    protected int m_iSaison = -1;
    //~ Constructors -------------------------------------------------------------------------------
    //In DB immer nur ein einzigen Eintrag zu Spielplan pro Saison halten da alte Daten immer enthalten bleiben in neuem Plan
    /**
     * Creates a new instance of Spielplan
     */
    public Spielplan() {
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final Vector<Paarung> getEintraege() {
        return m_vEintraege;
    }

    /**
     * Setter for property m_clFetchDate.
     *
     * @param m_clFetchDate New value of property m_clFetchDate.
     */
    public final void setFetchDate(java.sql.Timestamp m_clFetchDate) {
        this.m_clFetchDate = m_clFetchDate;
    }

    /**
     * Getter for property m_clFetchDate.
     *
     * @return Value of property m_clFetchDate.
     */
    public final java.sql.Timestamp getFetchDate() {
        return m_clFetchDate;
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
     * liefert die Spiele zu einem Spieltag
     */
    public final Vector<Paarung> getPaarungenBySpieltag(int spieltag) {
        final Vector<Paarung> spiele = new Vector<Paarung>();
        Paarung tmp = null;

        for (int i = 0; i < m_vEintraege.size(); i++) {
            tmp = (Paarung) m_vEintraege.elementAt(i);

            if (tmp.getSpieltag() == spieltag) {
                spiele.add(tmp);
            }
        }

        return spiele;
    }

    /////////////////////////////////////////////////////////////////////////////////7
    //Logik
    ///////////////////////////////////////////////////////////////////////////////7

    /**
     * liefert die Spiele eines bestimmten Teams, sortiert nach Spieltagen
     */
    public final Paarung[] getPaarungenByTeamId(int id) {
        Paarung tmp = null;
        final Vector<Paarung> spiele = new Vector<Paarung>();
        Paarung[] aSpiele = null;

        for (int i = 0; i < m_vEintraege.size(); i++) {
            tmp = (Paarung) m_vEintraege.elementAt(i);

            if ((tmp.getHeimId() == id) || (tmp.getGastId() == id)) {
                spiele.add(tmp);
            }
        }

        aSpiele = new Paarung[spiele.size()];
        Helper.copyVector2Array(spiele, aSpiele);
        java.util.Arrays.sort(aSpiele);

        return aSpiele;
    }

    /**
     * Setter for property m_iSaison.
     *
     * @param m_iSaison New value of property m_iSaison.
     */
    public final void setSaison(int m_iSaison) {
        this.m_iSaison = m_iSaison;
    }

    /**
     * Getter for property m_iSaison.
     *
     * @return Value of property m_iSaison.
     */
    public final int getSaison() {
        return m_iSaison;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //Liga Tabelle
    ////////////////////////////////////////////////////////////////////////////////
    public final LigaTabelle getTabelle() {
        if (m_clTabelle == null) {
            m_clTabelle = berechneTabelle(14);
        }

        return m_clTabelle;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //Tabellenverlauf
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for property m_clVerlauf.
     *
     * @return Value of property m_clVerlauf.
     */
    public final Tabellenverlauf getVerlauf() {
        if (m_clVerlauf == null) {
            m_clVerlauf = generateTabellenVerlauf();
        }

        return m_clVerlauf;
    }

    public final void addEintrag(Paarung spiel) {
        if ((spiel != null) && (!m_vEintraege.contains(spiel))) {
            m_vEintraege.add(spiel);
        }
    }

    @Override
	public final boolean equals(Object o) {
        if (o instanceof Spielplan) {
            if ((m_iLigaId == ((Spielplan) o).getLigaId())
                && (m_iSaison == ((Spielplan) o).getSaison())) {
                return true;
            }
        }

        return false;
    }

    @Override
	public final String toString() {
        return core.model.HOVerwaltung.instance().getLanguageString("Season")
               + " " + getSaison() + " "
               + core.model.HOVerwaltung.instance().getLanguageString("Liga")
               + " " + getLigaName() + " (" + getLigaId() + ")";
    }

    /**
     * berechnet die Positionierung vom SPieltag zuvor
     */
    protected final void berechneAltePositionen(LigaTabelle tabelle) {
        LigaTabelle compare = null;
        LigaTabellenEintrag tmp = null;
        LigaTabellenEintrag tmp2 = null;

        int spieltag = 1;

        if (tabelle.getEintraege().size() <= 0) {
            return;
        }

        spieltag = ((LigaTabellenEintrag) tabelle.getEintraege().elementAt(0)).getAnzSpiele() - 1;

        if (spieltag > 0) {
            compare = berechneTabelle(spieltag);
            compare.sort();

            for (int i = 0; i < tabelle.getEintraege().size(); i++) {
                tmp = (LigaTabellenEintrag) tabelle.getEintraege().elementAt(i);
                tmp2 = compare.getEintragByTeamId(tmp.getTeamId());

                if (tmp2 != null) {
                    tmp.setAltePosition(tmp2.getPosition());
                }
            }
        }
    }

    /**
     * berechnet die Tabelle anhand des Spielplans
     *
     * @param maxSpieltag gibt an bis zu welchem Spieltag gerechnet werden soll (inklusive)
     */
    protected final LigaTabelle berechneTabelle(int maxSpieltag) {
        final LigaTabelle tmp = new LigaTabelle();
        final Vector<Paarung> spieltag = getPaarungenBySpieltag(maxSpieltag);

        //vorabInfos
        tmp.setLigaId(m_iLigaId);
        tmp.setLigaName(m_sLigaName);

        for (int i = 0; i < spieltag.size(); i++) {
            //jeweils für Heim und Gast TabellenEinträge erstellen
            tmp.addEintrag(berechneTabellenEintrag(getPaarungenByTeamId(((Paarung) spieltag
                                                                         .elementAt(i)).getHeimId()),
                                                   ((Paarung) spieltag.elementAt(i)).getHeimId(),
                                                   ((Paarung) spieltag.elementAt(i)).getHeimName(),
                                                   maxSpieltag));
            tmp.addEintrag(berechneTabellenEintrag(getPaarungenByTeamId(((Paarung) spieltag
                                                                         .elementAt(i)).getGastId()),
                                                   ((Paarung) spieltag.elementAt(i)).getGastId(),
                                                   ((Paarung) spieltag.elementAt(i)).getGastName(),
                                                   maxSpieltag));
        }

        tmp.sort();
        berechneAltePositionen(tmp);

        return tmp;
    }

    /**
     * Erstellt einen TabellenEintrag aus den Spielen eines Vereins
     * @param maxSpieltag gibt an bis zu welchem Spieltag die Tabelle berechnet werden soll ( 1-14 )
     */
    protected final LigaTabellenEintrag berechneTabellenEintrag(Paarung[] spiele, int teamId,
                                                                String name, int maxSpieltag) {
        final LigaTabellenEintrag eintrag = new LigaTabellenEintrag();
        int anzSpiele = 0;
        int hSieg = 0;
        int hUn = 0;
        int hNied = 0;
        int aSieg = 0;
        int aUn = 0;
        int aNied = 0;
        int hToreFuer = 0;
        int hToreGegen = 0;
        int aToreFuer = 0;
        int aToreGegen = 0;
        int hPunkte = 0;
        int aPunkte = 0;

        eintrag.setTeamId(teamId);
        eintrag.setTeamName(name);

        for (int i = 0; (i < spiele.length) && (i < maxSpieltag); i++) {
            //Spiel schon gespielt
            if (spiele[i].getToreHeim() > -1) {
                //Anz Spiel erhöhen
                anzSpiele++;

                //Heimspiel ?
                if (spiele[i].getHeimId() == teamId) {
                    //Sieg
                    if (spiele[i].getToreHeim() > spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 LigaTabellenEintrag.H_SIEG);
                        hPunkte += 3;
                        hSieg += 1;
                        hToreGegen += spiele[i].getToreGast();
                        hToreFuer += spiele[i].getToreHeim();
                    }
                    //Unentschieden
                    else if (spiele[i].getToreHeim() == spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 LigaTabellenEintrag.H_UN);
                        hPunkte += 1;
                        hUn += 1;
                        hToreGegen += spiele[i].getToreGast();
                        hToreFuer += spiele[i].getToreHeim();
                    }
                    //Niederlage
                    else if (spiele[i].getToreHeim() < spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 LigaTabellenEintrag.H_NIED);
                        hNied += 1;
                        hToreGegen += spiele[i].getToreGast();
                        hToreFuer += spiele[i].getToreHeim();
                    }
                }
                //Auswärts
                else {
                    //Niederlage
                    if (spiele[i].getToreHeim() > spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 LigaTabellenEintrag.A_NIED);

                        aNied += 1;
                        aToreGegen += spiele[i].getToreHeim();
                        aToreFuer += spiele[i].getToreGast();
                    }
                    //Unentschieden
                    else if (spiele[i].getToreHeim() == spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 LigaTabellenEintrag.A_UN);

                        aPunkte += 1;
                        aUn += 1;
                        aToreGegen += spiele[i].getToreHeim();
                        aToreFuer += spiele[i].getToreGast();
                    }
                    //Sieg
                    else if (spiele[i].getToreHeim() < spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 LigaTabellenEintrag.A_SIEG);

                        aPunkte += 3;
                        aSieg += 1;
                        aToreGegen += spiele[i].getToreHeim();
                        aToreFuer += spiele[i].getToreGast();
                    }
                }
            }
        }

        //Eintrag füllen
        eintrag.setAnzSpiele(anzSpiele);

        //home
        eintrag.setH_Nied(hNied);
        eintrag.setH_Siege(hSieg);
        eintrag.setH_Un(hUn);
        eintrag.setH_Punkte(hPunkte);
        eintrag.setH_ToreFuer(hToreFuer);
        eintrag.setH_ToreGegen(hToreGegen);

        //Away
        eintrag.setA_Nied(aNied);
        eintrag.setA_Siege(aSieg);
        eintrag.setA_Un(aUn);
        eintrag.setA_Punkte(aPunkte);
        eintrag.setA_ToreFuer(aToreFuer);
        eintrag.setA_ToreGegen(aToreGegen);

        //Gesamt
        eintrag.setPunkte(aPunkte + hPunkte);
        eintrag.setToreFuer(aToreFuer + hToreFuer);
        eintrag.setToreGegen(aToreGegen + hToreGegen);
        eintrag.setG_Nied(aNied + hNied);
        eintrag.setG_Siege(aSieg + hSieg);
        eintrag.setG_Un(aUn + hUn);

        return eintrag;
    }

    /**
     * erzeugt den Tabellenverlauf
     */
    protected final Tabellenverlauf generateTabellenVerlauf() {
        int spieltag = -1;
        LigaTabelle[] tabelle = null;
        final Tabellenverlauf verlauf = new Tabellenverlauf();
        TabellenVerlaufEintrag[] eintraege = null;

        try {
        	spieltag = ((LigaTabellenEintrag) getTabelle().getEintraege().elementAt(0)).getAnzSpiele();
            tabelle = new LigaTabelle[spieltag];

            for (int i = spieltag; i > 0; i--) {
                //i-1 wegen Array
                tabelle[i - 1] = berechneTabelle(i);
            }

            //VerlaufEinträge erstellen
            if (tabelle.length > 0) {
                LigaTabellenEintrag tmp = null;

                eintraege = new TabellenVerlaufEintrag[tabelle[spieltag - 1].getEintraege().size()];

                //Für jeden Eintrag verlaufeintrag erstellen
                for (int j = 0; j < tabelle[spieltag - 1].getEintraege().size(); j++) {
                    //Platzierungen
                    final int[] positionen = new int[tabelle.length];

                    eintraege[j] = new TabellenVerlaufEintrag();
                    eintraege[j].setTeamId(((LigaTabellenEintrag) tabelle[spieltag - 1].getEintraege()
                                                                                       .elementAt(j))
                                           .getTeamId());
                    eintraege[j].setTeamName(((LigaTabellenEintrag) tabelle[spieltag - 1].getEintraege()
                                                                                         .elementAt(j))
                                             .getTeamName());

                    for (int i = 0; i < tabelle.length; i++) {
                        tmp = tabelle[i].getEintragByTeamId(eintraege[j].getTeamId());

                        if (tmp != null) {
                            positionen[i] = tmp.getPosition();
                        } else {
                            positionen[i] = -1;
                        }
                    }

                    eintraege[j].setPlatzierungen(positionen);
                }
            }

            verlauf.setEintraege(eintraege);
            return verlauf;
        } catch (Exception e) {
        	HOLogger.instance().error(getClass(), "Error(generateTabellenVerlauf):" + e);
            return new Tabellenverlauf();
        }
    }
}
