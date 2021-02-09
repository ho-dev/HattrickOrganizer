package core.model;

import core.db.DBManager;
import core.model.match.MatchLineup;
import core.model.match.SourceSystem;
import core.model.misc.Basics;
import core.model.misc.Economy;
import core.model.misc.Verein;
import core.model.player.Player;
import module.youth.YouthPlayer;
import core.model.series.Liga;
import core.training.TrainingPerWeek;
import core.training.TrainingManager;
import core.util.HOLogger;
import core.util.HTCalendar;
import core.util.HTCalendarFactory;
import module.lineup.Lineup;
import module.series.Spielplan;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ht.HattrickManager;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.youth.YouthTraining;
import org.jetbrains.annotations.Nullable;
import tool.arenasizer.Stadium;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class bundles all models that belong to an HRF file - the data can also come from the database
 */
public class HOModel {
    //~ Instance fields ----------------------------------------------------------------------------

    private Lineup m_clAufstellung;
    private Lineup m_clLastAufstellung;
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
    private int m_iID = -1;
    private List<StaffMember> m_clStaff;
    private List<YouthPlayer> youthPlayers;
    private List<MatchLineup> youthMatchLineups;
    private List<YouthTraining> youthTrainings;

    //~ Constructors -------------------------------------------------------------------------------
	public HOModel() {

	    if (DBManager.instance().isFirstStart()){
	        m_iID = 0;
        }
	    else {

            try {
                m_iID = DBManager.instance().getMaxHrfId() + 1;
            }
            catch (Exception e) {
                HOLogger.instance().error(this.getClass(), "Error when trying to determine latest HRH_ID");
            }
        }
    }

	public HOModel(int id) {
		m_iID = id;
	}

	//~ Methods ------------------------------------------------------------------------------------


	public void setLineups(int id) {
		this.setLineup(DBManager.instance().getAufstellung(id, Lineup.DEFAULT_NAME));
		this.setPreviousLineup(DBManager.instance().getAufstellung(id, Lineup.DEFAULT_NAMELAST));
	}

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
    	if ( m_vOldPlayer == null){
    		m_vOldPlayer = DBManager.instance().getAllSpieler();
		}
        return m_vOldPlayer;
    }

    //---------Player--------------------------------------

    /**
     * Returns all current players
     */
    public final List<Player> getCurrentPlayers() {
        if ( m_vPlayer == null){
            m_vPlayer = DBManager.instance().getSpieler(this.m_iID);
        }
        return m_vPlayer;
    }

    /**
     * Returns all current youth players
     */
    public final List<YouthPlayer> getCurrentYouthPlayers() {
        if ( this.youthPlayers == null){
            this.youthPlayers = DBManager.instance().loadYouthPlayers(this.m_iID);
        }
        return this.youthPlayers;
    }

    /**
     * Set a new lineup
     */
    public final void setLineup(@Nullable Lineup lineup) {
        m_clAufstellung = lineup;
    }

    //--------- Lineup ----------------------------------

    /**
     * returns the lineup (setRatings is called)
     */
    public final Lineup getLineup() {
		if (m_clAufstellung == null){
			m_clAufstellung = DBManager.instance().getAufstellung(this.m_iID, Lineup.DEFAULT_NAME);
		}
		m_clAufstellung.setRatings();
        return m_clAufstellung;
    }

	/**
	 * returns the lineup (setRatings is NOT called)
	 */
	public final Lineup getCurrentLineup() {
    	if (m_clAufstellung == null){
    		m_clAufstellung = DBManager.instance().getAufstellung(this.m_iID, Lineup.DEFAULT_NAME);
		}
		return m_clAufstellung;
	}

	/**
	 * returns the lineup (redundant to getCurrentLineup)
	 */
	public final Lineup getLineupWithoutRatingRecalc() {
		return getCurrentLineup();
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
    	if ( m_clBasics == null){
    		m_clBasics = DBManager.instance().getBasics(this.m_iID);
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
		if ( m_clEconomy == null){
			m_clEconomy = DBManager.instance().getEconomy(this.m_iID);
		}
        return m_clEconomy;
    }

    /**
     * Setter for property m_iID.
     *
     * @param m_iID New value of property m_iID.
     */
    public final void setID(int m_iID) {
        this.m_iID = m_iID;
    }

    //------ID-------------------------

    /**
     * Getter for property m_iID.
     *
     * @return Value of property m_iID.
     */
    public final int getID() {
        return m_iID;
    }

    /**
     * Set the previous lineup
     */
    public final void setPreviousLineup(Lineup aufstellung) {
        m_clLastAufstellung = aufstellung;
    }

    /**
     * Returns previous lineup
     */
    public final Lineup getPreviousLineup() {
    	if ( m_clLastAufstellung == null){
    		m_clLastAufstellung = DBManager.instance().getAufstellung(this.m_iID, Lineup.DEFAULT_NAMELAST);
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
    	if  ( m_clLiga == null){
    		m_clLiga = DBManager.instance().getLiga(this.m_iID);
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
    	for ( Player p : getCurrentPlayers()){
    		if ( p.getPlayerID()==id)
    			return p;
		}
        return null;
    }

    /**
     * Returns Player of opponent team (used for man marking)
     * if there are lineups stored of the opponent team, these are used to get the information.
     * Otherwise opponent teams players are downloaded.
     * @param objectPlayerID the id of the opponent player
     * @return opponent player's name
     */
    public String getOpponentPlayerName(int objectPlayerID) {

        var lineupPos = DBManager.instance().getMatchInserts(objectPlayerID);
        for ( var p : lineupPos){
            return p.getSpielerName();
        }

        var teamId = SystemManager.getActiveTeamId();
        HattrickManager.downloadPlayers(teamId);
        var info = PlayerDataManager.getPlayerInfo(objectPlayerID);
        if ( info != null){
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
    	if ( m_clSpielplan == null){
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
    	if ( m_clStadium == null){
    		m_clStadium = DBManager.instance().getStadion(this.m_iID);
		}
        return m_clStadium;
    }

    // ---------------- Staff -----------------------------
    
    /**
     * Sets the staff list
     */
    public final void setStaff (List<StaffMember> staff) {
    	m_clStaff = staff;
    }
    
    /**
     * Returns the staff list
     */
    public List<StaffMember> getStaff() {
    	if ( m_clStaff == null){
    		m_clStaff = DBManager.instance().getStaffByHrfId(this.m_iID);
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
    	if ( m_clTeam == null){
    		m_clTeam = DBManager.instance().getTeam(this.m_iID);
		}
        return m_clTeam;
    }

    /**
     * Returns Trainer informatin
     */
    public final Player getTrainer() {
		Player trainer = null;
    	for ( Player p : getCurrentPlayers()){
			if ( p.isTrainer()){
				if (trainer == null || p.getTrainerSkill() > trainer.getTrainerSkill()){
					trainer = p;
				}
			}
		}

        // Nt team protection, they may have no coach:
        if (trainer == null)
        {
        	trainer = new Player();
        	trainer.setTrainerSkill(7);
        	trainer.setTrainerTyp(2); // neutral;
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
    	if  ( m_clVerein == null){
    		m_clVerein = DBManager.instance().getVerein(this.m_iID);
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
    	if ( m_clXtraDaten == null) {
    		m_clXtraDaten = DBManager.instance().getXtraDaten(this.m_iID);
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

    public final void addYouthPlayer(YouthPlayer player){
        if ( youthPlayers == null){
            youthPlayers = new ArrayList<>();
        }
        youthPlayers.add(player);
    }

    /**
     * Caclulates the subskill of each player, based on training and the previous hrf.
     * this method is either called from DownloadDialog to calculated the training effect of current homodel (hrf)
     * or by the recalcSubskills method, which examines the relevant hrfs (models) and calls each of them.
     */
    public final void calcSubskills() {

    	boolean doOnce = false;
    	
    	final List<Player> vPlayer = getCurrentPlayers();
    	final java.sql.Timestamp calcDate = getBasics().getDatum();
    	
    	final int previousHrfId = DBManager.instance().getPreviousHRF(m_iID);
    	
    	Timestamp trainingDateOfPreviousHRF;
    	Timestamp trainingDateOfCurrentHRF = getXtraDaten().getTrainingDate();
    	if (previousHrfId > -1) {
    		trainingDateOfPreviousHRF = DBManager.instance()
    									.getXtraDaten(previousHrfId)
    									.getTrainingDate();
    	}
    	else {
    		// handle the very first hrf download
			trainingDateOfPreviousHRF = new Timestamp(0); // fetch all trainings before the first hrf was loaded
		}
    	
    	if ((trainingDateOfPreviousHRF != null) && (trainingDateOfCurrentHRF != null)) {
    		// Training Happened

    		// Find TrainingPerWeeks that should be processed (those since last training).
    		List<TrainingPerWeek> rawTrainingList = TrainingManager.instance().getTrainingWeekList();
    		List<TrainingPerWeek> trainingList = new ArrayList<>();
    		for (TrainingPerWeek tpw : rawTrainingList) {
    			// We want to add all weeks with nextTraining after the previous date, and stop
    			// when we are after the current date.
    			
    			if (tpw.getNextTrainingDate().after(trainingDateOfCurrentHRF)) {
    				break;
    			}
    			
    			if (tpw.getNextTrainingDate().after(trainingDateOfPreviousHRF)) {
    			    if(TrainingManager.TRAININGDEBUG) {
                        HTCalendar htcP;
                        String htcPs;
                        htcP = HTCalendarFactory.createTrainingCalendar(new Date(trainingDateOfPreviousHRF.getTime()));
                        htcPs = " (" + htcP.getHTSeason() + "." + htcP.getHTWeek() + ")";
                        HTCalendar htcA = HTCalendarFactory.createTrainingCalendar(new Date((trainingDateOfCurrentHRF.getTime())));
                        String htcAs = " (" + htcA.getHTSeason() + "." + htcA.getHTWeek() + ")";
                        HTCalendar htcC = HTCalendarFactory.createTrainingCalendar(new Date((calcDate.getTime())));
                        String htcCs = " (" + htcC.getHTSeason() + "." + htcC.getHTWeek() + ")";

                        HOLogger.instance().info(HOModel.class,
                                "trArt=" + tpw.getTrainingType() + ", numPl=" + vPlayer.size() + ", calcDate=" + calcDate.toString() + htcCs + ", act=" + trainingDateOfCurrentHRF.toString() + htcAs + ", prev=" + (trainingDateOfPreviousHRF.toString() + htcPs) + " (" + previousHrfId + ")");
                    }

                    trainingList.add(tpw);
    			}
    		}
    		
    		TrainingManager.instance().calculateTraining(
    		        getXtraDaten().getTrainingDate(),
    				trainingList,
					getCurrentPlayers(),
					DBManager.instance().getSpieler(previousHrfId),
					getTrainer().getTrainerSkill(),
                    getStaff());

    		// store new values of current players
    		DBManager.instance().saveSpieler(m_iID, getCurrentPlayers(), getBasics().getDatum());
    	}
    }
    

    /**
     * Remove a Player
     */
    public final void removePlayer(Player player) {
    	if ( m_vPlayer != null) {
			m_vPlayer.remove(player);
		}
    }
   
    /**
     * save the model in the database
     */
    public final synchronized void saveHRF() {
        DBManager.instance().saveHRF(m_iID,
        		java.text.DateFormat.getDateTimeInstance().format(new java.util.Date(
        				System.currentTimeMillis())), getBasics().getDatum());

        //basics
        DBManager.instance().saveBasics(m_iID, getBasics());
        //Verein
        DBManager.instance().saveVerein(m_iID, getClub());
        //Team
        DBManager.instance().saveTeam(m_iID, getTeam());
        //Finanzen
        DBManager.instance().saveEconomyInDB(m_iID, getEconomy(), getBasics().getDatum());
        //Stadion
        DBManager.instance().saveStadion(m_iID, getStadium());
        //Liga
        DBManager.instance().saveLiga(m_iID, getLeague());
        //Aufstellung + aktu Sys als Standard saven
        DBManager.instance().saveAufstellung(SourceSystem.HATTRICK.getValue(), m_iID, getCurrentLineup(), Lineup.DEFAULT_NAME);
        //Aufstellung + aktu Sys als Standard saven
        DBManager.instance().saveAufstellung(SourceSystem.HATTRICK.getValue(), m_iID, getPreviousLineup(), Lineup.DEFAULT_NAMELAST);
        //Xtra Daten
        DBManager.instance().saveXtraDaten(m_iID, getXtraDaten());
        //Player
        DBManager.instance().saveSpieler(m_iID, getCurrentPlayers(), getBasics().getDatum());
        // Youth Player
        DBManager.instance().storeYouthPlayers(m_iID, getCurrentYouthPlayers());
        //Staff
        DBManager.instance().saveStaff(m_iID, getStaff());
    }

    /**
     * Save match schedule in database
     */
    public final synchronized void saveFixtures() {
        if (m_clSpielplan != null) 
            DBManager.instance().storeSpielplan(m_clSpielplan);
    }

    public YouthPlayer getCurrentYouthPlayer(int youthplayerID) {
        return this.getCurrentYouthPlayers().stream().filter(player -> player.getId()==youthplayerID).findAny()
                .orElse(null);
    }

    public void addYouthMatchLineup(MatchLineup lineup) {
        if ( this.youthMatchLineups == null){
            getYouthMatchLineups();
        }
        this.youthMatchLineups.add(lineup);
    }

    public List<MatchLineup> getYouthMatchLineups(){
        if ( this.youthMatchLineups == null){
            youthMatchLineups = DBManager.instance().loadMatchLineups(SourceSystem.YOUTH.getValue());
        }
        return youthMatchLineups;
    }

    public List<YouthTraining> getYouthTrainings() {
        if ( this.youthTrainings == null){
            initYouthTrainings();
        }
        return youthTrainings;
    }

    private void initYouthTrainings() {
        this.youthTrainings = DBManager.instance().loadYouthTrainings();
        for ( var lineup : this.getYouthMatchLineups()){
            // create a youth trainings object for each lineup, if it does not exist already
            var youthTraining = youthTrainings.stream()
                    .filter(t->t.getMatchId()==lineup.getMatchID())
                    .findFirst()
                    .orElse(null);
            if ( youthTraining==null ){
                this.youthTrainings.add(new YouthTraining(lineup));
            }
        }
    }

    public void setYouthTrainings(List<YouthTraining> youthTrainings) {
        this.youthTrainings = youthTrainings;
    }

    public List<YouthTraining> getYouthTrainingsAfter(Timestamp date) {
        return getYouthTrainings().stream()
                .filter(i->i.getMatchDate().after(date))
                .sorted(Comparator.comparing(YouthTraining::getMatchDate))
                .collect(Collectors.toList());
    }
}
