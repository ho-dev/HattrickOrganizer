package core.training;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.StaffMember;
import core.model.match.*;
import core.model.misc.TrainingEvent;
import core.model.player.Player;
import core.util.HOLogger;
import core.util.HelperWrapper;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.JOptionPane;

import static java.lang.Integer.min;

/**
 * Class that extract data from Database and calculates TrainingWeek and TrainingPoints earned from
 * players
 */
public class TrainingManager {
    //~ Static fields/initializers -----------------------------------------------------------------

	private static TrainingManager m_clInstance;

    //~ Instance fields ----------------------------------------------------------------------------
    private TrainingWeekManager _WeekManager;
    private TrainingPerWeek nextWeekTraining;
    static final public boolean TRAININGDEBUG = false;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of TrainingsManager
     */
    private TrainingManager() {
        _WeekManager = TrainingWeekManager.instance();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns a singleton TrainingManager object
     *
     * @return instance of TrainingManager
     */
    public static TrainingManager instance() {
        if (m_clInstance == null) {
            m_clInstance = new TrainingManager();
        }
        return m_clInstance;
    }

    public List<TrainingPerWeek> getTrainingWeekList() {
        return _WeekManager.getTrainingList();
    }

    public List<TrainingPerWeek> refreshTrainingWeeks() {
        return _WeekManager.refreshTrainingList();
    }

    /**
     * Training for given player for each skill
     *
     * @param inputPlayer Player to use
     * @param train preset Trainingweeks
     *
     * @return TrainingPerPlayer
     */
    public TrainingPerPlayer calculateWeeklyTrainingForPlayer(Player inputPlayer, TrainingPerWeek train) {
 		final int playerID = inputPlayer.getPlayerID();
        TrainingPerPlayer output = new TrainingPerPlayer(inputPlayer);
        if (train == null || train.getTrainingType() < 0) {
            return output;
        }

        WeeklyTrainingType wt = WeeklyTrainingType.instance(train.getTrainingType());
        if (wt != null) {
	        try {
	        	var matches = train.getMatches();
	        	int myID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
	        	TrainingWeekPlayer tp = new TrainingWeekPlayer(inputPlayer);
	            int minutes=0;
	        	for (var match : matches) {
	                //Get the MatchLineup by id
	                MatchLineupTeam mlt = DBManager.instance().getMatchLineupTeam(SourceSystem.HATTRICK.getValue(), match.getMatchID(), myID);
	                //MatchStatistics ms = new MatchStatistics(match, mlt);
					MatchType type = mlt.getMatchType();
					boolean walkoverWin = match.getMatchdetails().isWalkoverMatchWin(HOVerwaltung.instance().getModel().getBasics().getYouthTeamId());
					if ( type != MatchType.MASTERS) { // MASTERS counts only for experience
						tp.addFullTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getFullTrainingSectors(), walkoverWin));
						tp.addBonusTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getBonusTrainingSectors(), walkoverWin));
						tp.addPartlyTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getPartlyTrainingSectors(), walkoverWin));
						tp.addOsmosisTrainingMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, wt.getOsmosisTrainingSectors(), walkoverWin));
					}
					tp.addPlayedMinutes(mlt.getTrainingMinutesPlayedInSectors(playerID, null, walkoverWin));
					output.addExperienceIncrease(min(90,tp.getPlayedMinutes() - minutes), type );
	                minutes = tp.getPlayedMinutes();
				}
	            TrainingPoints trp = new TrainingPoints(wt, tp);

	        	// get experience increase of national matches
				if  ( inputPlayer.getNationalTeamID() != 0 && inputPlayer.getNationalTeamID() != myID){
					// TODO check if national matches are stored in database
					var nationalMatches = train.getNTmatches();
					for (var match : nationalMatches){
						MatchLineupTeam mlt = DBManager.instance().getMatchLineupTeam(SourceSystem.HATTRICK.getValue(), match.getMatchID(), inputPlayer.getNationalTeamID());
						minutes = mlt.getTrainingMinutesPlayedInSectors(playerID, null, false);
						if ( minutes > 0 ) {
							output.addExperienceIncrease(min(90,minutes), mlt.getMatchType());
						}
					}
				}

	    		if (TrainingManager.TRAININGDEBUG) {
					HOLogger.instance().debug(getClass(), "Week " + train.getHattrickDate().getWeek()
	            		+": Player " + inputPlayer.getFullName() + " (" + playerID + ")"
	            		+" played total " + tp.getPlayedMinutes() + " mins for training purposes and got "
	            		+ wt.getPrimaryTraining(tp) + " primary training points and "
	            		+ wt.getSecondaryTraining(tp) + " secondary training points");
	    		}
	            output.setTrainingPair(trp);
	            output.setTrainingWeek(train);
	        } catch (Exception e) {
	            HOLogger.instance().log(getClass(),e);
	        }
        }
        return output;
    }
    
	/*
     * Recalculates all sub skills for all players
     *
     * @param showBar show progress bar
     */
    public void recalcSubskills(boolean showBar) {
        if (JOptionPane.showConfirmDialog(HOMainFrame.instance(),
        		HOVerwaltung.instance().getLanguageString("SubskillRecalcFull"),
				HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
            HOVerwaltung.instance().recalcSubskills(showBar, null);
        }
    }

	public void calculateTraining(Timestamp nextTrainingDate,
								  List<TrainingPerWeek> trainingList,
								  List<Player> currentPlayers,
								  List<Player> previousPlayers,
								  int trainerSkill) {

		// Generate a map of players from the previous hrf.
		final Map<Integer, Player> playerOfPreviousDownload = new HashMap<>();
		for (Player p : previousPlayers) {
			playerOfPreviousDownload.put(p.getPlayerID(), p);
		}

		// Train each player
		for (Player player : currentPlayers) {
			try {

				// The version of the player from last hrf
				Player old = playerOfPreviousDownload.get(player.getPlayerID());
				if (old == null) {
					if (TrainingManager.TRAININGDEBUG) {
						HOLogger.instance().debug(HOModel.class, "Old player for id " + player.getPlayerID() + " = null");
					}
					// Player appears the first time
					// - was bought new
					// - promoted from youth
					// - it is the first hrf ever loaded
					old = new Player();
					old.setSpielerID(player.getPlayerID());
					old.copySkills(player);
					old.setLastName(player.getLastName());
					if (HOVerwaltung.instance().getModel().getCurrentPlayer(player.getPlayerID()) != null) {
						// PLayer is in current team (not an historical player)
						List<TrainingEvent> events = player.downloadTrainingEvents();
						if (events != null) {
							for (TrainingEvent event : events) {
								if (event.getEventDate().compareTo(player.getHrfDate()) <= 0) {
									old.setValue4Skill(event.getPlayerSkill(), event.getOldLevel());
								}
							}
						}
					}
				}

				// Always copy subskills as the first thing
				player.copySubSkills(old);

				// Always check skill drop if drop calculations are active.
				if (SkillDrops.instance().isActive()) {
					for (int skillType = 0; skillType < PlayerSkill.EXPERIENCE; skillType++) {
						if ((skillType == PlayerSkill.FORM) || (skillType == PlayerSkill.STAMINA)) {
							continue;
						}
						if (player.check4SkillDown(skillType, old)) {
							player.dropSubskills(skillType);
						}
					}
				}

				if (trainingList.size() > 0) {
					// Training happened

					// Perform training for all "untrained weeks"

					// An "old" player we can mess with.
					Player tmpOld = new Player();
					tmpOld.copySkills(old);
					tmpOld.copySubSkills(old);
					tmpOld.setSpielerID(old.getPlayerID());
					tmpOld.setAlter(old.getAlter());
					tmpOld.setLastName(old.getLastName());

					Player calculationPlayer = null;
					TrainingPerWeek tpw;
					Iterator<TrainingPerWeek> iter = trainingList.iterator();
					while (iter.hasNext()) {
						tpw = iter.next();

						if (tpw == null) {
							continue;
						}

						// The "player" is only the relevant Player for the current Hrf. All previous
						// training weeks (if any), should be calculated based on "old", and the result
						// of the previous week.

						if (nextTrainingDate.getTime() == tpw.getNextTrainingDate().getTime()) {
							// It is the same week as this model.

							if (calculationPlayer != null) {
								// We have run previous calculations because of missing training weeks.
								// Subskills may have changed, but no skillup can have happened. Copy subskills.

								player.copySubSkills(calculationPlayer);
							}
							calculationPlayer = player;

						} else {
							// An old week
							calculationPlayer = new Player();
							calculationPlayer.copySkills(tmpOld);
							calculationPlayer.copySubSkills(tmpOld);
							calculationPlayer.setSpielerID(tmpOld.getPlayerID());
							calculationPlayer.setAlter(tmpOld.getAlter());
							calculationPlayer.setLastName(tmpOld.getLastName());
						}

						calculationPlayer.calcIncrementalSubskills(tmpOld,
								trainerSkill,
								tpw);

						if (iter.hasNext()) {
							// Use calculated skills and subskills as "old" if there is another week in line...
							tmpOld = new Player();
							tmpOld.copySkills(calculationPlayer);
							tmpOld.copySubSkills(calculationPlayer);
							tmpOld.setSpielerID(calculationPlayer.getPlayerID());
							tmpOld.setAlter(calculationPlayer.getAlter());
						}
					}
				}

				/*
				 * Start of debug
				 */
				if (TrainingManager.TRAININGDEBUG) {
					HelperWrapper helper = HelperWrapper.instance();

					if (trainingList.size() > 0)
						logPlayerProgress(old, player);

				}
				/*
				 * End of debug
				 */

			} catch (Exception e) {
				HOLogger.instance().log(getClass(), e);
				HOLogger.instance().log(getClass(), "Model calcSubskill: " + e);
			}
		}
	}

	private void logPlayerProgress (Player before, Player after) {

		if ((after == null) || (before == null)) {
			// crash due to non paranoid logging is too silly
			return;
		}

		int playerID = after.getPlayerID();
		String playerName = after.getFullName();

		int age = after.getAlter();
		int skill = -1;
		int beforeSkill = 0;
		int afterSkill = 0;

		var changes= new StringBuilder();
		for ( var s = PlayerSkill.KEEPER; s <= PlayerSkill.EXPERIENCE; s++){
			var oldValue = before.getValue4Skill(s);
			var oldSub = before.getSub4Skill(s);
			var newValue = after.getValue4Skill(s);
			var newSub = after.getSub4Skill(s);
			if ( oldSub != newSub || oldValue != newValue){
				if ( changes.length() != 0 ) changes.append("; ");
				changes.append(PlayerSkill.toString(s)).append(": ").append(oldValue).append(".").append(oldSub)
						.append("->").append(newValue).append(".").append(newSub);
			}
		}
		if ( changes.length() > 0){
			HOLogger.instance().debug(getClass(), playerID + "|" + playerName + "|" + age + "|" + changes.toString());
		}
	}

	public TrainingPerWeek getNextWeekTraining() {
    	if (nextWeekTraining == null){
    		findLastTraining();
		}
		return nextWeekTraining;
	}

	private void findLastTraining(){

    	if (_WeekManager != null){

    		var nextTrainingDate = _WeekManager.getNextTrainingDate();

    		for(var training: _WeekManager.getTrainingList()){
    			if (training.getTrainingDate().equals(nextTrainingDate)){
					nextWeekTraining = training;
				}
			}

			TrainingWeekManager _trainingWeekManager = new TrainingWeekManager(nextTrainingDate.plus(-1, ChronoUnit.DAYS), nextTrainingDate.plus(1, ChronoUnit.DAYS), true);
    		if (_trainingWeekManager.getTrainingList().size() == 1){
				nextWeekTraining = _trainingWeekManager.getTrainingList().get(0);
			}
    		else{
    			HOLogger.instance().error(this.getClass(), "Last training could not be determined");
			}

		}

	}

}
