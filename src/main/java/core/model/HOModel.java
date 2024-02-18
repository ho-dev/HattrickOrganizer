package core.model;

import core.db.DBManager;
import core.db.PersistenceManager;
import core.file.hrf.HRF;
import core.model.enums.DBDataSource;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupTeam;
import core.model.misc.Basics;
import core.model.misc.Economy;
import core.model.misc.Verein;
import core.model.player.Player;
import core.model.player.TrainerType;
import core.rating.RatingPredictionManager;
import core.rating.RatingPredictionModel;
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
    private HRF previousHrf;
    private HRF hrf;
    private MatchLineupTeam aufstellung;
    private MatchLineupTeam lastAufstellung;
    private Basics basics;
    private Economy economy;
    private Liga liga;
    private Spielplan spielplan;
    private Stadium stadium;
    private Team team;
    private static List<Player> oldPlayers;
    private List<Player> players;
    private Verein verein;
    private XtraData xtraData;
    private List<StaffMember> staff;
    private List<YouthPlayer> youthPlayers;
    private List<MatchLineup> youthMatchLineups;
    private List<YouthTraining> youthTrainings;
    private RatingPredictionManager ratingPredictionManager;

    private PersistenceManager persistenceManager;


    /**
     * Returns the current persistence manager.  If none is set, use the {@link DBManager}
     * current instance.
     *
     * @return PersistenceManager – persistence manager to use.
     */
    public PersistenceManager getPersistenceManager() {
        if (persistenceManager == null) {
            setPersistenceManager(DBManager.instance());
        }

        return persistenceManager;
    }

    /**
     * Sets the persistence manager.  This is intended for unit tests only at this point.
     *
     * @param persistenceManager Persistence manager to set.
     */
    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    //~ Constructors -------------------------------------------------------------------------------
    public HOModel(HODateTime fetchDate) {
        PersistenceManager dbManager = getPersistenceManager();
        try {
            var latestImport = dbManager.getMaxIdHrf();
            int hrfId;
            if (latestImport != null) {
                hrfId = latestImport.getHrfId() + 1;
            } else {
                hrfId = 0;
            }
            hrf = new HRF(hrfId, fetchDate);
            previousHrf = dbManager.loadLatestHRFDownloadedBefore(hrf.getDatum().toDbTimestamp());
        } catch (Exception e) {
            HOLogger.instance().error(this.getClass(), "Error when trying to determine latest HRF_ID");
        }
    }

    public HOModel(int id) {
        PersistenceManager dbManager = getPersistenceManager();

        hrf = dbManager.loadHRF(id);
        if (hrf == null) {
            hrf = new HRF(id, HODateTime.now()); // initial start
        } else {
            previousHrf = dbManager.loadLatestHRFDownloadedBefore(hrf.getDatum().toDbTimestamp());
        }

        setClub(dbManager.getVerein(id));
        setCurrentPlayers(dbManager.getSpieler(id));
        setFormerPlayers(dbManager.loadAllPlayers());
        setTeam(dbManager.getTeam(id));
        setBasics(dbManager.getBasics(id));

        var teamId = getBasics().getTeamId();
        setLineup(dbManager.loadNextMatchLineup(teamId));
        setPreviousLineup(dbManager.loadPreviousMatchLineup(teamId));

        setEconomy(dbManager.getEconomy(id));
        setLeague(dbManager.getLiga(id));
        setStadium(dbManager.getStadion(id));
        setFixtures(dbManager.getLatestSpielplan());
        setXtraDaten(dbManager.getXtraDaten(id));
        setStaff(dbManager.getStaffByHrfId(id));
    }

    /**
     * Model created for subskill calculation
     * Only the HRFs needs to be initialized. Calculator inits what is necessary.
     *
     * @param hrf      current download information
     * @param previous previous download information. If not given, the previous HRF is loaded from the database.
     */
    public HOModel(HRF hrf, HRF previous) {
        this.hrf = hrf;
        this.previousHrf = previous;
        if (previousHrf == null) {
            previousHrf = getPersistenceManager().loadLatestHRFDownloadedBefore(hrf.getDatum().toDbTimestamp());
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Sets the list of former players of the club
     * current players are removed from the list
     */
    public final void setFormerPlayers(List<Player> playerVector) {
        oldPlayers = new ArrayList<>();
        for (var old : playerVector) {
            var isCurrent = this.players.stream().anyMatch(i -> i.getPlayerId() == old.getPlayerId());
            if (!isCurrent) {
                old.setGoner(true);
                oldPlayers.add(old);
            }
        }
    }

    /**
     * Returns former players of the club
     */
    public final List<Player> getFormerPlayers() {
        if (oldPlayers == null) {
            oldPlayers = getPersistenceManager().loadAllPlayers();
        }
        return oldPlayers;
    }

    //---------Player--------------------------------------

    /**
     * Returns all current players
     * (including the lineup disabled ones)
     */
    public final List<Player> getCurrentPlayers() {
        if (players == null) {
            players = getPersistenceManager().getSpieler(this.hrf.getHrfId());
        }
        return players;
    }

    /**
     * Returns all current youth players
     */
    public final List<YouthPlayer> getCurrentYouthPlayers() {
        if (this.youthPlayers == null) {
            this.youthPlayers = DBManager.instance().loadYouthPlayers(this.hrf.getHrfId());
        }
        return this.youthPlayers;
    }

    /**
     * Set a new lineup
     */
    public final void setLineup(@Nullable MatchLineupTeam lineup) {
        if (lineup != null) {
            if (lineup.getTeamID() < 0) lineup.setTeamID(getBasics().getTeamId());
            if (lineup.getTeamName().isEmpty()) lineup.setTeamName(getBasics().getTeamName());
            calcStyleOfPlay();
        }
        aufstellung = lineup;
    }

    public void storeLineup(MatchLineupTeam matchLineupTeam) {
        setLineup(matchLineupTeam);
        DBManager.instance().storeMatchLineupTeam(matchLineupTeam);
    }

    private void calcStyleOfPlay() {
        if (aufstellung != null) {
            var trainerType = getTrainer().getTrainerType();
            var tacticAssistants = getClub().getTacticalAssistantLevels();
            aufstellung.calcStyleOfPlay(trainerType, tacticAssistants);
        }
    }

    /**
     * returns the lineup (setRatings is NOT called)
     */
    public final @NotNull MatchLineupTeam getCurrentLineupTeam() {
        if (aufstellung == null) {
            aufstellung = getPersistenceManager().loadNextMatchLineup(HOVerwaltung.instance().getModel().getBasics().getTeamId());
            if (aufstellung != null) {
                calcStyleOfPlay();
            }
            else {
                // create an empty lineup
                aufstellung = new MatchLineupTeam();
                aufstellung.setLineup(new Lineup());
            }
        }
        return aufstellung;
    }

    /**
     * returns the lineup
     */
    public final @NotNull Lineup getCurrentLineup() {
        var team = getCurrentLineupTeam();
        return team.getLineup();
    }

    public final @NotNull RatingPredictionModel getRatingPredictionModel(){
        var ret = getRatingPredictionManager().getRatingPredictionModel();
        if ( ret == null){
            ret = getRatingPredictionManager().getRatingPredictionModel("default", getTeam());
        }
        return ret;
    }

    /**
     * Set Basics information
     */
    public final void setBasics(Basics basics) {
        this.basics = basics;
    }

    //----------Basics----------------------------------------

    /**
     * Returns Basics information
     */
    public final Basics getBasics() {
        if (basics == null) {
            basics = getPersistenceManager().getBasics(this.hrf.getHrfId());
        }
        return basics;
    }

    /**
     * Set economy information
     */
    public final void setEconomy(Economy economy) {
        this.economy = economy;
    }

    //------- finance ---------------------------------------

    /**
     * Returns economy information
     */
    public final Economy getEconomy() {
        if (economy == null) {
            economy = getPersistenceManager().getEconomy(this.hrf.getHrfId());
        }
        return economy;
    }

    //------ID-------------------------

    /**
     * Getter for property m_iID.
     *
     * @return Value of property m_iID.
     */
    public final int getHrfId() {
        return hrf.getHrfId();
    }

    public int getPreviousID() {
        return previousHrf != null ? previousHrf.getHrfId() : -1;
    }

    /**
     * Set the previous lineup
     */
    public final void setPreviousLineup(MatchLineupTeam aufstellung) {
        lastAufstellung = aufstellung;
    }

    /**
     * Returns previous lineup
     */
    public final MatchLineupTeam getPreviousLineup() {
        if (lastAufstellung == null) {
            lastAufstellung = getPersistenceManager().loadPreviousMatchLineup(HOVerwaltung.instance().getModel().getClub().getTeamID());
        }
        return lastAufstellung;
    }

    /**
     * Set league
     */
    public final void setLeague(Liga liga) {
        this.liga = liga;
    }

    //----------Liga----------------------------------------

    /**
     * Returns league information
     */
    public final Liga getLeague() {
        if (liga == null) {
            liga = getPersistenceManager().getLiga(this.getHrfId());
        }
        return liga;
    }

    /**
     * Set Player list of the current team
     */
    public final void setCurrentPlayers(List<Player> playerVector) {
        players = playerVector;
    }

    /**
     * Returns Player of the current team with given Id
     */
    public final Player getCurrentPlayer(int id) {
        for (Player p : getCurrentPlayers()) {
            if (p.getPlayerId() == id)
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
    public final void setFixtures(Spielplan m_clSpielplan) {
        this.spielplan = m_clSpielplan;
    }

    //-----------------------Spielplan----------------------------------------//

    /**
     * Get the match schedule.
     *
     * @return Value of property m_clSpielplan.
     */
    public final Spielplan getFixtures() {
        if (spielplan == null) {
            spielplan = getPersistenceManager().getLatestSpielplan(); // valid only for the current Model
        }
        return spielplan;
    }

    /**
     * Set Stadium
     */
    public final void setStadium(Stadium stadium) {
        this.stadium = stadium;
    }

    //--------Stadium----------------------------------------

    /**
     * Returns stadium information
     */
    public final Stadium getStadium() {
        if (stadium == null) {
            stadium = getPersistenceManager().getStadion(this.getHrfId());
        }
        return stadium;
    }

    // ---------------- Staff -----------------------------

    /**
     * Sets the staff list
     */
    public final void setStaff(List<StaffMember> staff) {
        this.staff = staff;
    }

    /**
     * Returns the staff list
     */
    public List<StaffMember> getStaff() {
        if (staff == null) {
            staff = getPersistenceManager().getStaffByHrfId(this.getHrfId());
        }
        return staff;
    }

    /**
     * Sets Team
     */
    public final void setTeam(Team team) {
        this.team = team;
    }

    //----------Team----------------------------------------

    /**
     * Returns Team information
     */
    public final Team getTeam() {
        if (team == null) {
            team = getPersistenceManager().getTeam(this.getHrfId());
        }
        return team;
    }

    /**
     * Returns Trainer information
     */
    public final Player getTrainer() {
        Player trainer = null;
        for (Player p : getCurrentPlayers()) {
            if (p.isCoach()) {
                trainer = p;
                break;
            }
        }

        // Nt team protection, they may have no coach:
        if (trainer == null) {
            trainer = new Player();
            trainer.setCoachSkill(7);
            trainer.setTrainerType(TrainerType.Balanced); // neutral;
        }

        return trainer;
    }

    /**
     * Sets club information
     */
    public final void setClub(Verein verein) {
        this.verein = verein;
    }

    //----------Verein----------------------------------------

    /**
     * Returns club information
     */
    public final Verein getClub() {
        if (verein == null) {
            verein = getPersistenceManager().getVerein(this.getHrfId());
        }
        return verein;
    }

    /**
     * Setter for property m_clXtraDaten.
     *
     * @param m_clXtraDaten New value of property m_clXtraDaten.
     */
    public final void setXtraDaten(XtraData m_clXtraDaten) {
        this.xtraData = m_clXtraDaten;
    }

    /**
     * Getter for property m_clXtraDaten.
     *
     * @return Value of property m_clXtraDaten.
     */
    public final core.model.XtraData getXtraDaten() {
        if (xtraData == null) {
            xtraData = getPersistenceManager().getXtraDaten(this.getHrfId());
        }
        return xtraData;
    }

    /**
     * Add a player to the current player list
     */
    public final void addPlayer(Player player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        players.add(player);
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
            player.calcSubSkills(this.getPreviousID(), trainingWeeks);
        }
        // store new values of current players
        DBManager.instance().saveSpieler(getCurrentPlayers());
    }

    /**
     * Determine the list of training weeks since previous download
     *
     * @return list of training weeks between previous and current download (may be empty)
     */
    private List<TrainingPerWeek> getTrainingWeeksSincePreviousDownload() {
        Timestamp from = null;
        if (previousHrf != null) {
            from = previousHrf.getDatum().toDbTimestamp();
        }
        return DBManager.instance().getTrainingList(from, hrf.getDatum().toDbTimestamp());
    }

    /**
     * Calculates the subskills of each player based on trainings that took place during a given period
     */
    public final void calcSubskills(HODateTime from, HODateTime to) {
        var trainingWeeks = TrainingManager.instance().getHistoricalTrainingsBetweenDates(from, to);
        for (var player : this.getCurrentPlayers()) {
            player.calcSubSkills(this.getPreviousID(), trainingWeeks);
        }
        // store new values of current players
        DBManager.instance().saveSpieler(getCurrentPlayers());

        // push recent training to historical training table
        TrainingManager.instance().updateHistoricalTrainings();
    }

    /**
     * Remove a Player
     */
    public final void removePlayer(Player player) {
        if (players != null) {
            players.remove(player);
        }
    }

    /**
     * save the model in the database
     */
    public final synchronized void saveHRF() {
        var time = getBasics().getDatum();
        DBManager.instance().saveHRF(this.hrf);
        DBManager.instance().saveBasics(getHrfId(), getBasics());
        DBManager.instance().saveVerein(getHrfId(), getClub());
        DBManager.instance().saveTeam(getHrfId(), getTeam());
        DBManager.instance().saveEconomyInDB(getHrfId(), getEconomy(), time);
        DBManager.instance().saveStadion(getHrfId(), getStadium());
        DBManager.instance().saveLiga(getHrfId(), getLeague());
        DBManager.instance().saveXtraDaten(getHrfId(), getXtraDaten());
        DBManager.instance().saveSpieler(getCurrentPlayers());
        DBManager.instance().storeYouthPlayers(getHrfId(), getCurrentYouthPlayers());
        DBManager.instance().saveStaff(getHrfId(), getStaff());
    }

    /**
     * Save match schedule in database
     *
     * @param fixtures Spielplan
     */
    public final synchronized void saveFixtures(Spielplan fixtures) {
        setFixtures(fixtures);
        if (spielplan != null)
            DBManager.instance().storeSpielplan(spielplan);
    }

    public YouthPlayer getCurrentYouthPlayer(int youthplayerID) {
        return this.getCurrentYouthPlayers().stream()
                .filter(player -> player.getId() == youthplayerID)
                .findAny()
                .orElse(null);
    }

    public void addYouthMatchLineup(MatchLineup lineup) {
        getYouthMatchLineups().add(lineup);
    }

    public List<MatchLineup> getYouthMatchLineups() {
        if (this.youthMatchLineups == null) {
            youthMatchLineups = DBManager.instance().getYouthMatchLineups();
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
                    .filter(t -> t.getYouthMatchId() == lineup.getMatchID())
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
        var xtra = this.getXtraDaten();
        if (xtra != null) {
            var team = this.getTeam();
            return new TrainingPerWeek(
                    xtra.getTrainingDateAfterWeeks(-1), // previous training
                    team.getTrainingsArtAsInt(),
                    team.getTrainingslevel(),
                    team.getStaminaTrainingPart(),
                    this.getClub().getCoTrainer(),
                    this.getTrainer().getCoachSkill(),
                    DBDataSource.HRF);
        }
        return null;
    }

    public RatingPredictionManager getRatingPredictionManager() {
        if (ratingPredictionManager == null) {
            ratingPredictionManager = new RatingPredictionManager();
        }
        return ratingPredictionManager;
    }
}