package module.series;

import core.db.AbstractTable;
import core.file.xml.TeamStats;
import core.model.TranslationFacility;
import core.model.series.*;
import core.net.OnlineWorker;
import core.util.HODateTime;
import core.util.HOLogger;
import org.javatuples.Pair;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Spielplan represents a game schedule, i.e. a particular season in a series.
 */
public class Spielplan  extends AbstractTable.Storable {
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
    public final List<Paarung> getFixturesOfMatchDay(final int gameDay) {
        return m_vEintraege
                .stream()
                .filter(fixture -> fixture.getSpieltag() == gameDay)
                .collect(Collectors.toList());
    }

    /**
     * Get all teams that played during the season
     * There might be teams replaced
     * @return Set of team ids (size >= 8)
     */
    public Set<Integer> getTeamsInSeries() {
        var ret = new HashSet<Integer>();
        for (var p : m_vEintraege) {
            ret.add(p.getHeimId());
            ret.add(p.getGastId());
        }
        return ret;
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
            m_clTabelle = calculateSeriesTable();
        }
        return m_clTabelle;
    }

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
        return TranslationFacility.tr("Season")
               + " " + getSaison() + " "
               + TranslationFacility.tr("Liga")
               + " " + getLigaName() + " (" + getLigaId() + ")";
    }

    /**
     * Retrieves the previous position in series table for each current position in <code>tabelle</code>.
     *
     * @param tabelle      Current series table for which the previous positions are being set.
     * @param currentTeams
     */
    protected final void calculatePreviousTablePositions(LigaTabelle tabelle, ArrayList<List<Integer>> currentTeams) {

        if (tabelle.getEntries().isEmpty()) {
            return;
        }

        var spieltag = (tabelle.getEntries().elementAt(0)).getAnzSpiele() - 1;

        if (spieltag > 0) {
            var compare = calculateSeriesTable(spieltag, currentTeams);
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
     * @return LigaTabelle – Computed series table.
     */
    private LigaTabelle calculateSeriesTable() {
        return calculateSeriesTable(14, GetCurrentTeams());
    }

    /**
     * Determine current teams of the series from last match day
     * Each entry is a list of team ids with
     * first entry specifying the id of the current team and
     * next optional entry specifying a team which was replaced by the current team during the series
     * @return List of Lists of team ids
     */
    private ArrayList<List<Integer>> GetCurrentTeams() {
        final List<Paarung> fixturesOfMatchDay = getFixturesOfMatchDay(14);
        var currentTeams = new ArrayList<List<Integer>>();
        for (var p : fixturesOfMatchDay) {
            currentTeams.add(new ArrayList<>(List.of(p.getHeimId())));
            currentTeams.add(new ArrayList<>(List.of(p.getGastId())));
        }

        // Determine if teams were replaced during series
        var teamsInSeries = getTeamsInSeries();
        if (teamsInSeries.size() != 8) {
            for (var t : teamsInSeries) {
                FindReplacementOfTeam(currentTeams, t);
            }
        }
        return currentTeams;
    }

    private LigaTabelle calculateSeriesTable(int maxMatchDay, ArrayList<List<Integer>> currentTeams) {
        final LigaTabelle ligaTabelle = new LigaTabelle();
        ligaTabelle.setLigaId(m_iLigaId);
        ligaTabelle.setLigaName(m_sLigaName);

        for( var ids : currentTeams) {
            ligaTabelle.addEintrag(calculateTableEntry(ids, maxMatchDay));
        }

        if(ligaTabelle.getEntries().get(0).getAnzSpiele() > 0) {
            ligaTabelle.sort();
            calculatePreviousTablePositions(ligaTabelle, currentTeams);
        }
        else {
            var seriesDetails = OnlineWorker.getSeriesDetails(this.getLigaId());
            for ( var t : ligaTabelle.getEntries()){
                var details = seriesDetails.get(String.valueOf(t.getTeamId()));
                var position = details.getPosition();
                t.setPosition(position);
                t.setAltePosition(position);
            }
            ligaTabelle.sortByPosition();
        }

        return ligaTabelle;
    }

    private List<Paarung> getMatchesByTeamIds(List<Integer> ids) {
        return m_vEintraege.stream()
                .filter(fixture -> (ids.contains(fixture.getHeimId()) || (ids.contains(fixture.getGastId()))))
                .sorted()
                .toList();
    }

    // TODO: Fix table history
    private void FindReplacementOfTeam(ArrayList<List<Integer>> currentTeams, Integer t) {
        for ( var ids : currentTeams) {
            if ( ids.contains(t)) {return;} // not replaced
        }

        // Find replacement of team
        List<Integer> replaces = new ArrayList<>();
        replaces.add(t);

        while ( !replaces.isEmpty() ) {
            var replaceTeam = replaces.get(0);
            for (var i = 0; i < 7; i++) {
                int finalI = i;
                var match = m_vEintraege.stream().filter(p -> p.getSpieltag() == 1 + finalI && (p.getHeimId() == replaceTeam || p.getGastId() == replaceTeam)).findAny();
                if ( match.isPresent()) {
                    // Find reverse match
                    if ( match.get().getHeimId() == replaceTeam) {
                        var opponentAtRound = match.get().getGastId();
                        var reverseMatch = m_vEintraege.stream().filter(p -> p.getSpieltag() == 14 - finalI && (p.getHeimId() == opponentAtRound)).findAny();
                        if ( reverseMatch.isPresent()) {
                            var replacement = reverseMatch.get().getGastId();
                            CurrentTeamsAddReplacement(currentTeams, replacement, replaceTeam);
                            replaces.remove(replaceTeam);
                            break;
                        }
                        else {
                            // opponent of this round is also replaced, try next round to find replacement of replaceTeam
                            replaces.add(opponentAtRound);
                        }
                    }
                    else {
                        var opponentAtRound = match.get().getHeimId();
                        var reverseMatch = m_vEintraege.stream().filter(p -> p.getSpieltag() == 14 - finalI && (p.getGastId() == opponentAtRound)).findAny();
                        if ( reverseMatch.isPresent()) {
                            var replacement = reverseMatch.get().getHeimId();
                            CurrentTeamsAddReplacement(currentTeams, replacement, replaceTeam);
                            replaces.remove(replaceTeam);
                            break;
                        }
                        else {
                            // opponent of this round is also replaced, try next round to find replacement of replaceTeam
                            replaces.add(opponentAtRound);
                        }
                    }
                }
            }
        }
    }

    private void CurrentTeamsAddReplacement(ArrayList<List<Integer>> currentTeams, int replacement, Integer replaceTeam) {
        for ( var ids : currentTeams) {
            if ( ids.contains(replacement)) {
                ids.add(replaceTeam);
                return;
            }
        }
    }

    /**
     * Creates a league table from the matches of a team.
     *
     * @param maxMatchDay Day until which the table is being calculated (1–14)
     */
    private SerieTableEntry calculateTableEntry(List<Integer> ids, int maxMatchDay) {
        var matches = getMatchesByTeamIds(ids);
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

        var teamId = ids.get(0); // First entry is the current existing teams

        eintrag.setTeamId(teamId);

        var name = "";

        for ( var match : matches) {
            if ( match.getSpieltag() > maxMatchDay) { break; }

            // Games already played
            if (match.getToreHeim() > -1) {
                gameNumber++;

                // Home game
                if (match.getHeimId() == teamId) {
                    name = match.getHeimName();
                    // Win
                    if (match.getToreHeim() > match.getToreGast()) {
                        eintrag.addSerienEintrag(match.getSpieltag() - 1, SerieTableEntry.H_SIEG);
                        homePoints += 3;
                        homeVictories += 1;
                        homeGoalsAgainst += match.getToreGast();
                        homeGoalsFor += match.getToreHeim();
                    }
                    // Draw
                    else if (match.getToreHeim() == match.getToreGast()) {
                        eintrag.addSerienEintrag(match.getSpieltag() - 1, SerieTableEntry.H_UN);
                        homePoints += 1;
                        homeDraws += 1;
                        homeGoalsAgainst += match.getToreGast();
                        homeGoalsFor += match.getToreHeim();
                    }
                    // Defeat
                    else {
                        eintrag.addSerienEintrag(match.getSpieltag() - 1, SerieTableEntry.H_NIED);
                        homeDefeats += 1;
                        homeGoalsAgainst += match.getToreGast();
                        homeGoalsFor += match.getToreHeim();
                    }
                }
                // Away
                else {
                    name = match.getGastName();
                    // Defeat
                    if (match.getToreHeim() > match.getToreGast()) {
                        eintrag.addSerienEintrag(match.getSpieltag() - 1, SerieTableEntry.A_NIED);
                        awayDefeats += 1;
                        awayGoalsAgainst += match.getToreHeim();
                        awayGoalsFor += match.getToreGast();
                    }
                    // Draw
                    else if (match.getToreHeim() == match.getToreGast()) {
                        eintrag.addSerienEintrag(match.getSpieltag() - 1, SerieTableEntry.A_UN);
                        awayPoints += 1;
                        awayDraws += 1;
                        awayGoalsAgainst += match.getToreHeim();
                        awayGoalsFor += match.getToreGast();
                    }
                    // Win
                    else {
                        eintrag.addSerienEintrag(match.getSpieltag() - 1, SerieTableEntry.A_SIEG);
                        awayPoints += 3;
                        awayVictories += 1;
                        awayGoalsAgainst += match.getToreHeim();
                        awayGoalsFor += match.getToreGast();
                    }
                }
            }
        }

        eintrag.setTeamName(name);
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

            var currentTeams = GetCurrentTeams();
            for (int i = spieltag; i > 0; i--) {
                tabelle[i - 1] = calculateSeriesTable(i, currentTeams);
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

    public void addFixtures(List<Paarung> fixtures) {
        m_vEintraege.addAll(fixtures);
    }

    private static final List<List<Pair<Integer, Integer>>> fixtureEntryIndices = List.of(
            List.of(new Pair<>(1,2), new Pair<>(3,4), new Pair<>(5,6), new Pair<>(7,8)),
            List.of(new Pair<>(4,1), new Pair<>(2,7), new Pair<>(6,3), new Pair<>(8,5)),
            List.of(new Pair<>(1,8), new Pair<>(3,5), new Pair<>(4,2), new Pair<>(7,6)),
            List.of(new Pair<>(6,1), new Pair<>(2,3), new Pair<>(5,7), new Pair<>(8,4)),
            List.of(new Pair<>(1,7), new Pair<>(4,5), new Pair<>(3,8), new Pair<>(2,6)),
            List.of(new Pair<>(5,1), new Pair<>(7,3), new Pair<>(6,4), new Pair<>(8,2)),
            List.of(new Pair<>(1,3), new Pair<>(2,5), new Pair<>(4,7), new Pair<>(6,8))
    );

    private static Paarung createFixture(HODateTime date, int round,  TeamStats team1, TeamStats team2) {
        var ret = new Paarung();
        ret.setDatum(date);
        ret.setHeimId(team1.getTeamId());
        ret.setGastId(team2.getTeamId());
        ret.setHeimName(team1.getTeamName());
        ret.setGastName(team2.getTeamName());
        ret.setSpieltag(round);
        return ret;
    }

    public static List<Paarung> createFixtures(HODateTime seriesStartDate, List<TeamStats> teams) {
        assert teams.size() == 8;
        var newFixtures = new ArrayList<Paarung>();
        var date = seriesStartDate;
        int roundNumber = 1;

        // First series half
        for (var round : fixtureEntryIndices){
            for ( var match  : round){
                newFixtures.add(createFixture(date, roundNumber, teams.get(match.getValue0()), teams.get(match.getValue1())));
            }
            roundNumber++;
            date = date.plusDaysAtSameLocalTime(7);
        }

        var copy = new ArrayList<>(fixtureEntryIndices);
        Collections.reverse(copy);
        // Second series half
        for (var round : copy){
            for ( var match  : round){
                newFixtures.add(createFixture(date, fixtureEntryIndices.indexOf(round)+1, teams.get(match.getValue1()), teams.get(match.getValue0())));
            }
            roundNumber++;
            date = date.plusDaysAtSameLocalTime(7);
        }
        return newFixtures;
    }
}
