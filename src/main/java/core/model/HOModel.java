package core.model;

import core.db.DBManager;
import core.file.hrf.HRF;
import core.model.enums.DBDataSource;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupTeam;
import core.model.match.SourceSystem;
import core.model.misc.Basics;
import core.model.misc.Economy;
import core.model.misc.Verein;
import core.model.player.Player;
import core.model.player.TrainerType;
import core.util.HODateTime;
import core.training.TrainingPerWeek;
import module.youth.YouthPlayer;
import core.model.series.Liga;
import core.training.TrainingManager;
import core.util.HOLogger;
import module.lineup.Lineup;
import module.series.Spielplan;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ht.HattrickManager;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.youth.YouthTraining;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tool.arenasizer.Stadium;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class bundles all models that belong to an HRF file - the data can also come from the database
 */
public class HOModel {
    private HRF o_previousHRF;
    private HRF o_hrf;
    private MatchLineupTeam m_clAufstellung;
    private MatchLineupTeam m_clLastAufstellung;
    private Basics m_clBasics;
    private Economy m_clEconomy;
    private Liga m_clLiga;
    private Spielplan m_clSpielplan;
    private Stadium m_clStadium;
    private Team m_clTeam;
    private static List<Player> m_vOldPlayer;
    private List<Player> m_vPlayer;
    private Verein m_clVerein;
    private XtraData m_clXtraDaten;
    private List<StaffMember> m_clStaff;
    private List<YouthPlayer> youthPlayers;
    private List<MatchLineup> youthMatchLineups;
    private List<YouthTraining> youthTrainings;

    //~ Constructors -------------------------------------------------------------------------------
    public HOModel(HODateTime fetchDate) {
        try {
            var latestImport = DBManager.instance().getMaxIdHrf();
            int hrfId;
            if (latestImport != null) {
                hrfId = latestImport.getHrfId() + 1;
            } else {
                hrfId = 0;
            }
            o_hrf = new HRF(hrfId, fetchDate);
            o_previousHRF = DBManager.instance().loadLatestHRFDownloadedBefore(o_hrf.getDatum().toDbTimestamp());
        } catch (Exception e) {
            HOLogger.instance().error(this.getClass(), "Error when trying to determine latest HRF_ID");
        }
    }

    public HOModel(int id) {

        o_hrf = DBManager.instance().loadHRF(id);
        if (o_hrf == null) {
            o_hrf = new HRF(id, HODateTime.now()); // initial start
        } else {
            o_previousHRF = DBManager.instance().loadLatestHRFDownloadedBefore(o_hrf.getDatum().toDbTimestamp());
        }

        setClub(DBManager.instance().getVerein(id));
        setCurrentPlayers(DBManager.instance().getSpieler(id));
        setFormerPlayers(DBManager.instance().getAllSpieler());
        setTeam(DBManager.instance().getTeam(id));
        setLineup(DBManager.instance().loadNextMatchLineup(getClub().getTeamID()));
        setPreviousLineup(DBManager.instance().loadPreviousMatchLineup(getClub().getTeamID()));
        setBasics(DBManager.instance().getBasics(id));
        setEconomy(DBManager.instance().getEconomy(id));
        setLeague(DBManager.instance().getLiga(id));
        setStadium(DBManager.instance().getStadion(id));
        setFixtures(DBManager.instance().getSpielplan(-1, -1));
        setXtraDaten(DBManager.instance().getXtraDaten(id));
        setStaff(DBManager.instance().getStaffByHrfId(id));
    }

    /**
     * Model created for subskill calculation
     * Only the HRFs needs to be initialized. Calculator inits what is necessary.
     *
     * @param hrf      current download information
     * @param previous previous download information. If not given, the previous HRF is loaded from the database.
     */
    public HOModel(HRF hrf, HRF previous) {
        this.o_hrf = hrf;
        this.o_previousHRF = previous;
        if (o_previousHRF == null) {
            o_previousHRF = DBManager.instance().loadLatestHRFDownloadedBefore(hrf.getDatum().toDbTimestamp());
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Sets the list of former players of the club
     * current players are removed from the list
     */
    public final void setFormerPlayers(Vector<Player> playerVector) {
        for (int i = 0; i < playerVector.size(); i++) {
            //Auf alt setzen, die neuen werden gleich entfernt
            playerVector.get(i).setOld(true);

            for (Player player : m_vPlayer) {
                //Schon in den aktuellen Spielern vorhanden, dann überspringen
                if (playerVector.get(i).equals(player)) {
                    playerVector.remove(i);

                    //Index einen zurücksetzen, da ein wert gelöscht wurde
                    i--;
                    break;
                }
            }
        }
        m_vOldPlayer = playerVector;
    }

    /**
     * Returns former players of the club
     */
    public final List<Player> getFormerPlayers() {
        if (m_vOldPlayer == null) {
            m_vOldPlayer = DBManager.instance().getAllSpieler();
        }
        return m_vOldPlayer;
    }

    //---------Player--------------------------------------

    /**
     * Returns all current players
     */
    public final List<Player> getCurrentPlayers() {
        if (m_vPlayer == null) {
            m_vPlayer = DBManager.instance().getSpieler(this.o_hrf.getHrfId());
        }
        return m_vPlayer;
    }

    /**
     * Returns all current youth players
     */
    public final List<YouthPlayer> getCurrentYouthPlayers() {
        if (this.youthPlayers == null) {
            this.youthPlayers = DBManager.instance().loadYouthPlayers(this.o_hrf.getHrfId());
        }
        return this.youthPlayers;
    }

    /**
     * Set a new lineup
     */
    public final void setLineup(@Nullable MatchLineupTeam lineup) {
        if (lineup != null) {
            if (lineup.getTeamID() < 0) lineup.setTeamID(getBasics().getTeamId());
            if (lineup.getTeamName().equals("")) lineup.setTeamName(getBasics().getTeamName());
            lineup.calcStyleOfPlay();
        }
        m_clAufstellung = lineup;
    }

    public void storeLineup(MatchLineupTeam matchLineupTeam) {
        setLineup(matchLineupTeam);
        DBManager.instance().storeMatchLineupTeam(matchLineupTeam);
    }

    //--------- Lineup ----------------------------------

    /**
     * returns the lineup (setRatings is called)
     */
    public final MatchLineupTeam getCurrentLineupTeamRecalculated() {
        getCurrentLineupTeam();
        m_clAufstellung.getLineup().setRatings();
        return m_clAufstellung;
    }

    /**
     * returns the lineup (setRatings is NOT called)
     */
    public final @NotNull MatchLineupTeam getCurrentLineupTeam() {
        if (m_clAufstellung == null) {
            m_clAufstellung = DBManager.instance().loadNextMatchLineup(HOVerwaltung.instance().getModel().getBasics().getTeamId());
            if (m_clAufstellung != null) {
                m_clAufstellung.calcStyleOfPlay();
            }
            else {
                // create an empty lineup
                m_clAufstellung = new MatchLineupTeam();
                m_clAufstellung.setLineup(new Lineup());
            }
        }
        return m_clAufstellung;
    }

    /**
     * returns the lineup
     */
    public final @NotNull Lineup getLineupWithoutRatingRecalc() {
        var team = getCurrentLineupTeam();
        return team.getLineup();
    }

    /**
     * Set Basics information
     */
    public final void setBasics(Basics basics) {
        m_clBasics = basics;
    }

    //----------Basics----------------------------------------

    /**
     * Returns Basics information
     */
    public final Basics getBasics() {
        if (m_clBasics == null) {
            m_clBasics = DBManager.instance().getBasics(this.o_hrf.getHrfId());
        }
        return m_clBasics;
    }

    /**
     * Set economy information
     */
    public final void setEconomy(Economy economy) {
        m_clEconomy = economy;
    }

    //------- finance ---------------------------------------

    /**
     * Returns economy information
     */
    public final Economy getEconomy() {
        if (m_clEconomy == null) {
            m_clEconomy = DBManager.instance().getEconomy(this.o_hrf.getHrfId());
        }
        return m_clEconomy;
    }

    //------ID-------------------------

    /**
     * Getter for property m_iID.
     *
     * @return Value of property m_iID.
     */
    public final int getID() {
        return o_hrf.getHrfId();
    }

    public int getPreviousID() {
        return o_previousHRF != null ? o_previousHRF.getHrfId() : -1;
    }

    /**
     * Set the previous lineup
     */
    public final void setPreviousLineup(MatchLineupTeam aufstellung) {
        m_clLastAufstellung = aufstellung;
    }

    /**
     * Returns previous lineup
     */
    public final MatchLineupTeam getPreviousLineup() {
        if (m_clLastAufstellung == null) {
            m_clLastAufstellung = DBManager.instance().loadPreviousMatchLineup(HOVerwaltung.instance().getModel().getClub().getTeamID());
        }
        return m_clLastAufstellung;
    }

    /**
     * Set league
     */
    public final void setLeague(Liga liga) {
        m_clLiga = liga;
    }

    //----------Liga----------------------------------------

    /**
     * Returns league information
     */
    public final Liga getLeague() {
        if (m_clLiga == null) {
            m_clLiga = DBManager.instance().getLiga(this.getID());
        }
        return m_clLiga;
    }

    /**
     * Set Player list of the current team
     */
    public final void setCurrentPlayers(List<Player> playerVector) {
        m_vPlayer = playerVector;
    }

    /**
     * Returns Player of the current team with given Id
     */
    public final Player getCurrentPlayer(int id) {
        for (Player p : getCurrentPlayers()) {
            if (p.getPlayerID() == id)
                return p;
        }
        return null;
    }

    /**
     * Returns Player of opponent team (used for man marking)
     * if there are lineups stored of the opponent team, these are used to get the information.
     * Otherwise opponent teams players are downloaded.
     *
     * @param objectPlayerID the id of the opponent player
     * @return opponent player's name
     */
    public String getOpponentPlayerName(int objectPlayerID) {

        var lineupPos = DBManager.instance().getMatchInserts(objectPlayerID);
        for (var p : lineupPos) {
            return p.getSpielerName();
        }

        var teamId = SystemManager.getActiveTeamId();
        HattrickManager.downloadPlayers(teamId);
        var info = PlayerDataManager.getPlayerInfo(objectPlayerID);
        if (info != null) {
            return info.getName();
        }
        return null;
    }

    /**
     * Set the match schedule
     *
     * @param m_clSpielplan New value of property m_clSpielplan.
     */
    public final void setFixtures(module.series.Spielplan m_clSpielplan) {
        this.m_clSpielplan = m_clSpielplan;
    }

    //-----------------------Spielplan----------------------------------------//

    /**
     * Get the match schedule.
     *
     * @return Value of property m_clSpielplan.
     */
    public final module.series.Spielplan getFixtures() {
        if (m_clSpielplan == null) {
            m_clSpielplan = DBManager.instance().getSpielplan(-1, -1); // valid only for the current Model
        }
        return m_clSpielplan;
    }

    /**
     * Set Stadium
     */
    public final void setStadium(Stadium stadium) {
        m_clStadium = stadium;
    }

    //--------Stadium----------------------------------------

    /**
     * Returns stadium information
     */
    public final Stadium getStadium() {
        if (m_clStadium == null) {
            m_clStadium = DBManager.instance().getStadion(this.getID());
        }
        return m_clStadium;
    }

    // ---------------- Staff -----------------------------

    /**
     * Sets the staff list
     */
    public final void setStaff(List<StaffMember> staff) {
        m_clStaff = staff;
    }

    /**
     * Returns the staff list
     */
    public List<StaffMember> getStaff() {
        if (m_clStaff == null) {
            m_clStaff = DBManager.instance().getStaffByHrfId(this.getID());
        }
        return m_clStaff;
    }

    /**
     * Sets Team
     */
    public final void setTeam(Team team) {
        m_clTeam = team;
    }

    //----------Team----------------------------------------

    /**
     * Returns Team information
     */
    public final Team getTeam() {
        if (m_clTeam == null) {
            m_clTeam = DBManager.instance().getTeam(this.getID());
        }
        return m_clTeam;
    }

    /**
     * Returns Trainer information
     */
    public final Player getTrainer() {
        Player trainer = null;
        for (Player p : getCurrentPlayers()) {
            if (p.isTrainer()) {
                trainer = p;
                break;
            }
        }

        // Nt team protection, they may have no coach:
        if (trainer == null) {
            trainer = new Player();
            trainer.setTrainerSkill(7);
            trainer.setTrainerTyp(TrainerType.Balanced); // neutral;
        }

        return trainer;
    }

    /**
     * Sets club information
     */
    public final void setClub(Verein verein) {
        m_clVerein = verein;
    }

    //----------Verein----------------------------------------

    /**
     * Returns club information
     */
    public final Verein getClub() {
        if (m_clVerein == null) {
            m_clVerein = DBManager.instance().getVerein(this.getID());
        }
        return m_clVerein;
    }

    /**
     * Setter for property m_clXtraDaten.
     *
     * @param m_clXtraDaten New value of property m_clXtraDaten.
     */
    public final void setXtraDaten(core.model.XtraData m_clXtraDaten) {
        this.m_clXtraDaten = m_clXtraDaten;
    }

    /**
     * Getter for property m_clXtraDaten.
     *
     * @return Value of property m_clXtraDaten.
     */
    public final core.model.XtraData getXtraDaten() {
        if (m_clXtraDaten == null) {
            m_clXtraDaten = DBManager.instance().getXtraDaten(this.getID());
        }
        return m_clXtraDaten;
    }

    /**
     * Add a player to the current player list
     */
    public final void addPlayer(Player player) {
        if (m_vPlayer == null) {
            m_vPlayer = new ArrayList<>();
        }
        m_vPlayer.add(player);
    }

    public final void addYouthPlayer(YouthPlayer player) {
        if (youthPlayers == null) {
            youthPlayers = new ArrayList<>();
        }
        youthPlayers.add(player);
    }

    /**
     * Calculates the subskills of each player based on recent trainings
     */
    public final void calcSubskills() {
        // push recent training to historical training table
        TrainingManager.instance().updateHistoricalTrainings();
        var trainingWeeks = getTrainingWeeksSincePreviousDownload();
        for (var player : this.getCurrentPlayers()) {
            player.calcSubskills(this.getPreviousID(), trainingWeeks);
        }
        // store new values of current players
        DBManager.instance().saveSpieler(getID(), getCurrentPlayers(), getBasics().getDatum());
    }

    /**
     * Determine the list of training weeks since previous download
     *
     * @return list of training weeks between previous and current download (may be empty)
     */
    private List<TrainingPerWeek> getTrainingWeeksSincePreviousDownload() {
        Timestamp from = null;
        if (o_previousHRF != null) {
            from = o_previousHRF.getDatum().toDbTimestamp();
        }
        return DBManager.instance().getTrainingList(from, o_hrf.getDatum().toDbTimestamp());
    }

    /**
     * Calculates the subskills of each player based on trainings that took place during a given period
     */
    public final void calcSubskills(HODateTime from, HODateTime to) {

        var trainingWeeks = TrainingManager.instance().getHistoricalTrainingsBetweenDates(from, to);
        for (var player : this.getCurrentPlayers()) {
            player.calcSubskills(this.getPreviousID(), trainingWeeks);
        }
        // store new values of current players
        DBManager.instance().saveSpieler(getID(), getCurrentPlayers(), getBasics().getDatum());

        // push recent training to historical training table
        TrainingManager.instance().updateHistoricalTrainings();
    }

    /**
     * Remove a Player
     */
    public final void removePlayer(Player player) {
        if (m_vPlayer != null) {
            m_vPlayer.remove(player);
        }
    }

    /**
     * save the model in the database
     */
    public final synchronized void saveHRF() {
        var time = getBasics().getDatum();
        DBManager.instance().saveHRF(getID(), time);
        DBManager.instance().saveBasics(getID(), getBasics());
        DBManager.instance().saveVerein(getID(), getClub());
        DBManager.instance().saveTeam(getID(), getTeam());
        DBManager.instance().saveEconomyInDB(getID(), getEconomy(), time);
        DBManager.instance().saveStadion(getID(), getStadium());
        DBManager.instance().saveLiga(getID(), getLeague());
        DBManager.instance().saveXtraDaten(getID(), getXtraDaten());
        DBManager.instance().saveSpieler(getID(), getCurrentPlayers(), time);
        DBManager.instance().storeYouthPlayers(getID(), getCurrentYouthPlayers());
        DBManager.instance().saveStaff(getID(), getStaff());
    }

    /**
     * Save match schedule in database
     *
     * @param fixtures Spielplan
     */
    public final synchronized void saveFixtures(Spielplan fixtures) {
        setFixtures(fixtures);
        if (m_clSpielplan != null)
            DBManager.instance().storeSpielplan(m_clSpielplan);
    }

    public YouthPlayer getCurrentYouthPlayer(int youthplayerID) {
        return this.getCurrentYouthPlayers().stream().filter(player -> player.getId() == youthplayerID).findAny()
                .orElse(null);
    }

    public void addYouthMatchLineup(MatchLineup lineup) {
        getYouthMatchLineups().add(lineup);
    }

    public List<MatchLineup> getYouthMatchLineups() {
        if (this.youthMatchLineups == null) {
            youthMatchLineups = DBManager.instance().loadMatchLineups(SourceSystem.YOUTH.getValue());
        }
        return youthMatchLineups;
    }

    public List<YouthTraining> getYouthTrainings() {
        if (this.youthTrainings == null) {
            initYouthTrainings();
        }
        return youthTrainings;
    }

    private void initYouthTrainings() {
        this.youthTrainings = DBManager.instance().loadYouthTrainings();
        for (var lineup : this.getYouthMatchLineups()) {
            // create a youth trainings object for each lineup, if it does not exist already
            var youthTraining = youthTrainings.stream()
                    .filter(t -> t.getMatchId() == lineup.getMatchID())
                    .findFirst()
                    .orElse(null);
            if (youthTraining == null) {
                this.youthTrainings.add(new YouthTraining(lineup));
            }
        }
    }

    public List<YouthTraining> getYouthTrainingsAfter(HODateTime date) {
        return getYouthTrainings().stream()
                .filter(i -> i.getMatchDate() != null && i.getMatchDate().isAfter(date))
                .sorted(Comparator.comparing(YouthTraining::getMatchDate))
                .collect(Collectors.toList());
    }

    /**
     * League id of user's premier team
     *
     * @return league id of premier team, if available
     * otherwise of the current team
     */
    public int getLeagueIdPremierTeam() {
        var xtra = getXtraDaten();
        if (xtra != null) {
            var countryId = xtra.getCountryId();
            if (countryId != null) {
                var ret = getLeagueId(countryId);
                if (ret != null) return ret;
            }
        }
        return getBasics().getLiga(); // should no longer happen
    }

    /**
     * League id of country
     *
     * @param countryId country id
     * @return league id of the country
     * or null if not found
     */
    private Integer getLeagueId(int countryId) {
        var league = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(countryId);
        if (league != null) return league.getLeagueId();
        return null;
    }

    public TrainingPerWeek getTraining() {
        var team = this.getTeam();
        return new TrainingPerWeek(
                this.getXtraDaten().getTrainingDateAfterWeeks(-1), // previous training
                team.getTrainingsArtAsInt(),
                team.getTrainingslevel(),
                team.getStaminaTrainingPart(),
                this.getClub().getCoTrainer(),
                this.getTrainer().getTrainerSkill(),
                DBDataSource.HRF);
    }

}