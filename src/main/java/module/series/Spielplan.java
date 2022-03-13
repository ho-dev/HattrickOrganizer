package module.series;

import core.model.series.*;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spielplan represents a game schedule, i.e. a particular season in a series.
 */
public class Spielplan  {
    //~ Instance fields ----------------------------------------------------------------------------

    protected LigaTabelle m_clTabelle;
    protected String m_sLigaName = "";
    protected Tabellenverlauf m_clVerlauf;
    protected HODateTime m_clFetchDate;
    protected List<Paarung> m_vEintraege = new ArrayList<>();
    protected int m_iLigaId = -1;
    protected int m_iSaison = -1;
    //~ Constructors -------------------------------------------------------------------------------
    // Always keep a single entry per season in the db so old data is kept in the new schedule.
    /**
     * Creates a new instance of Spielplan
     */
    public Spielplan() {
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final List<Paarung> getMatches() {
        return m_vEintraege;
    }

    /**
     * Setter for property m_clFetchDate.
     *
     * @param m_clFetchDate New value of property m_clFetchDate.
     */
    public final void setFetchDate(HODateTime m_clFetchDate) {
        this.m_clFetchDate = m_clFetchDate;
    }

    /**
     * Getter for property m_clFetchDate.
     *
     * @return Value of property m_clFetchDate.
     */
    public final HODateTime getFetchDate() {
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
    public final void setLigaName(String m_sLigaName) {
        this.m_sLigaName = m_sLigaName;
    }

    /**
     * Getter for property m_sLigaName.
     *
     * @return Value of property m_sLigaName.
     */
    public final String getLigaName() {
        return m_sLigaName;
    }

    /**
     * Returns the list of fixtures for a match day.
     *
     * @param gameDay Day number.
     * @return List – List of fixtures for a given match day.
     */
    public final List<Paarung> getPaarungenBySpieltag(final int gameDay) {
        return m_vEintraege
                .stream()
                .filter(fixture -> fixture.getSpieltag() == gameDay)
                .collect(Collectors.toList());
    }


    /////////////////////////////////////////////////////////////////////////////////7
    //Logik
    ///////////////////////////////////////////////////////////////////////////////7

    /**
     * Returns the games of a given team, sorted by match day.
     *
     * @param teamId ID of the team for which the fixtures are retrieved.
     * @return Paarung[] – Array of fixtures of team, sorted by match day.
     */
    public final Paarung[] getPaarungenByTeamId(final int teamId) {
        return m_vEintraege.stream()
                .filter(fixture -> (fixture.getHeimId() == teamId) || (fixture.getGastId() == teamId))
                .sorted()
                .toArray(Paarung[]::new);
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
    public final LigaTabelle getTable() {
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
            return (m_iLigaId == ((Spielplan) o).getLigaId())
                    && (m_iSaison == ((Spielplan) o).getSaison());
        } else {
            return false;
        }
    }

    @Override
	public final String toString() {
        return core.model.HOVerwaltung.instance().getLanguageString("Season")
               + " " + getSaison() + " "
               + core.model.HOVerwaltung.instance().getLanguageString("Liga")
               + " " + getLigaName() + " (" + getLigaId() + ")";
    }

    /**
     * Retrieves the previous position in series table for each current position in <code>tabelle</code>.
     *
     * @param tabelle Current series table for which the previous positions are being set.
     */
    protected final void berechneAltePositionen(LigaTabelle tabelle) {

        if (tabelle.getEntries().size() <= 0) {
            return;
        }

        var spieltag = (tabelle.getEntries().elementAt(0)).getAnzSpiele() - 1;

        if (spieltag > 0) {
            var compare = berechneTabelle(spieltag);
            compare.sort();

            for (int i = 0; i < tabelle.getEntries().size(); i++) {
                var tmp = tabelle.getEntries().elementAt(i);
                var tmp2 = compare.getEintragByTeamId(tmp.getTeamId());

                if (tmp2 != null) {
                    tmp.setAltePosition(tmp2.getPosition());
                }
            }
        }
    }

    /**
     * Calculates the series table based on the games of a given match day.
     *
     * @param maxMatchDay Game day up to which series table is computed.
     * @return LigaTabelle – Computed series table.
     */
    protected final LigaTabelle berechneTabelle(int maxMatchDay) {
        final LigaTabelle tmp = new LigaTabelle();
        final List<Paarung> spieltag = getPaarungenBySpieltag(maxMatchDay);

        tmp.setLigaId(m_iLigaId);
        tmp.setLigaName(m_sLigaName);

        for (Paarung paarung : spieltag) {
            // Create table entries for home and away games.
            tmp.addEintrag(berechneTabellenEintrag(getPaarungenByTeamId(paarung.getHeimId()),
                    paarung.getHeimId(),
                    paarung.getHeimName(),
                    maxMatchDay));
            tmp.addEintrag(berechneTabellenEintrag(getPaarungenByTeamId(paarung.getGastId()),
                    paarung.getGastId(),
                    paarung.getGastName(),
                    maxMatchDay));
        }

        tmp.sort();
        berechneAltePositionen(tmp);

        return tmp;
    }

    /**
     * Creates a league table from the matches of a team.
     *
     * @param maxSpieltag Day until which the table is being calculated (1–14)
     */
    protected final SerieTableEntry berechneTabellenEintrag(Paarung[] spiele, int teamId,
                                                            String name, int maxSpieltag) {
        final SerieTableEntry eintrag = new SerieTableEntry();
        int gameNumber = 0;
        int homeVictories = 0;
        int homeDraws = 0;
        int homeDefeats = 0;
        int awayVictories = 0;
        int awayDraws = 0;
        int awayDefeats = 0;
        int homeGoalsFor = 0;
        int homeGoalsAgainst = 0;
        int awayGoalsFor = 0;
        int awayGoalsAgainst = 0;
        int homePoints = 0;
        int awayPoints = 0;

        eintrag.setTeamId(teamId);
        eintrag.setTeamName(name);

        for (int i = 0; (i < spiele.length) && (i < maxSpieltag); i++) {
            // Games already played
            if (spiele[i].getToreHeim() > -1) {
                gameNumber++;

                // Home game
                if (spiele[i].getHeimId() == teamId) {
                    // Win
                    if (spiele[i].getToreHeim() > spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 SerieTableEntry.H_SIEG);
                        homePoints += 3;
                        homeVictories += 1;
                        homeGoalsAgainst += spiele[i].getToreGast();
                        homeGoalsFor += spiele[i].getToreHeim();
                    }
                    // Draw
                    else if (spiele[i].getToreHeim() == spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 SerieTableEntry.H_UN);
                        homePoints += 1;
                        homeDraws += 1;
                        homeGoalsAgainst += spiele[i].getToreGast();
                        homeGoalsFor += spiele[i].getToreHeim();
                    }
                    // Defeat
                    else if (spiele[i].getToreHeim() < spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 SerieTableEntry.H_NIED);
                        homeDefeats += 1;
                        homeGoalsAgainst += spiele[i].getToreGast();
                        homeGoalsFor += spiele[i].getToreHeim();
                    }
                }
                // Away
                else {
                    // Defeat
                    if (spiele[i].getToreHeim() > spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 SerieTableEntry.A_NIED);

                        awayDefeats += 1;
                        awayGoalsAgainst += spiele[i].getToreHeim();
                        awayGoalsFor += spiele[i].getToreGast();
                    }
                    // Draw
                    else if (spiele[i].getToreHeim() == spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 SerieTableEntry.A_UN);

                        awayPoints += 1;
                        awayDraws += 1;
                        awayGoalsAgainst += spiele[i].getToreHeim();
                        awayGoalsFor += spiele[i].getToreGast();
                    }
                    // Win
                    else if (spiele[i].getToreHeim() < spiele[i].getToreGast()) {
                        eintrag.addSerienEintrag(spiele[i].getSpieltag() - 1,
                                                 SerieTableEntry.A_SIEG);

                        awayPoints += 3;
                        awayVictories += 1;
                        awayGoalsAgainst += spiele[i].getToreHeim();
                        awayGoalsFor += spiele[i].getToreGast();
                    }
                }
            }
        }

        eintrag.setAnzSpiele(gameNumber);

        //home
        eintrag.setH_Nied(homeDefeats);
        eintrag.setH_Siege(homeVictories);
        eintrag.setH_Un(homeDraws);
        eintrag.setH_Punkte(homePoints);
        eintrag.setH_ToreFuer(homeGoalsFor);
        eintrag.setH_ToreGegen(homeGoalsAgainst);

        //Away
        eintrag.setA_Nied(awayDefeats);
        eintrag.setA_Siege(awayVictories);
        eintrag.setA_Un(awayDraws);
        eintrag.setA_Punkte(awayPoints);
        eintrag.setA_ToreFuer(awayGoalsFor);
        eintrag.setA_ToreGegen(awayGoalsAgainst);

        // Total
        eintrag.setPunkte(awayPoints + homePoints);
        eintrag.setToreFuer(awayGoalsFor + homeGoalsFor);
        eintrag.setToreGegen(awayGoalsAgainst + homeGoalsAgainst);
        eintrag.setG_Nied(awayDefeats + homeDefeats);
        eintrag.setG_Siege(awayVictories + homeVictories);
        eintrag.setG_Un(awayDraws + homeDraws);

        return eintrag;
    }

    /**
     * Creates the table history.
     *
     * @return Tabellenverlauf – Table position history.
     */
    protected final Tabellenverlauf generateTabellenVerlauf() {

        final Tabellenverlauf verlauf = new Tabellenverlauf();
        TabellenVerlaufEintrag[] eintraege = null;

        try {
        	var spieltag = getTable().getEntries().elementAt(0).getAnzSpiele();
            var tabelle = new LigaTabelle[spieltag];

            for (int i = spieltag; i > 0; i--) {
                tabelle[i - 1] = berechneTabelle(i);
            }

            // Create history entries
            if (tabelle.length > 0) {
                eintraege = new TabellenVerlaufEintrag[tabelle[spieltag - 1].getEntries().size()];

                for (int j = 0; j < tabelle[spieltag - 1].getEntries().size(); j++) {
                    final int[] positionen = new int[tabelle.length];

                    eintraege[j] = new TabellenVerlaufEintrag();
                    eintraege[j].setTeamId(tabelle[spieltag - 1].getEntries().elementAt(j).getTeamId());
                    eintraege[j].setTeamName(tabelle[spieltag - 1].getEntries().elementAt(j).getTeamName());

                    for (int i = 0; i < tabelle.length; i++) {
                        var tmp = tabelle[i].getEintragByTeamId(eintraege[j].getTeamId());

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
