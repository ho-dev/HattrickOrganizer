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
import core.util.HTCalendar;
import core.util.HTCalendarFactory;
import core.util.HelperWrapper;

import java.sql.Timestamp;
import java.util.*;

import javax.swing.JOptionPane;

import static java.lang.Integer.min;

/**
 * Class that extract data from Database and calculates TrainingWeek and TrainingPoints earned from
 * players
 *
 * @author humorlos, Dragettho, thetom
 */
public class TrainingManager {
    //~ Static fields/initializers -----------------------------------------------------------------

	private static TrainingManager m_clInstance;

    //~ Instance fields ----------------------------------------------------------------------------
    private TrainingWeekManager _WeekManager;
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
     * @param timestamp if not null, calculate training for this training date only
     *
     * @return TrainingPerPlayer
     */
    public TrainingPerPlayer calculateWeeklyTrainingForPlayer(Player inputPlayer,
                                                              TrainingPerWeek train, Timestamp timestamp) {
        //playerID HIER SETZEN
		final int playerID = inputPlayer.getSpielerID();

        TrainingPerPlayer output = new TrainingPerPlayer(inputPlayer);
        if (timestamp != null)
        	output.setTimestamp(timestamp);
        if (train == null || train.getTrainingType() < 0) {
            return output;
        }
        if (TRAININGDEBUG) {
        	HTCalendar htc1 = HTCalendarFactory.createTrainingCalendar();
        	HTCalendar htc2 = HTCalendarFactory.createTrainingCalendar();
        	String c1s = "";
			if (timestamp != null) {
        		htc1.setTime(timestamp);
        		c1s = " ("+htc1.getHTSeason()+"."+htc1.getHTWeek()+")";
        	}
        	htc2.setTime(train.getTrainingDate());
			String c2s = " (" + htc2.getHTSeason() + "." + htc2.getHTWeek() + ")";

        	HOLogger.instance().debug(getClass(),
        			"Start calcWeeklyTraining for "+ inputPlayer.getFullName()+", zeitpunkt="+((timestamp!=null)?timestamp.toString()+c1s:"")
        			+ ", trainDate="+train.getTrainingDate().toString()+c2s);
        }

        Calendar trainingDate = Calendar.getInstance(Locale.UK);
        trainingDate.setTime(train.getTrainingDate());
        WeeklyTrainingType wt = WeeklyTrainingType.instance(train.getTrainingType());
        if (wt != null) {
	        try {
	        	//List<Integer> matches = getOwnMatchesForTraining(trainingDate);
	        	var matches = train.getMatches();
	        	int myID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
	        	TrainingWeekPlayer tp = new TrainingWeekPlayer(inputPlayer);
	            int minutes=0;
	        	for (var match : matches) {
	                //Get the MatchLineup by id
	                MatchLineupTeam mlt = DBManager.instance().getMatchLineupTeam(match.getMatchID(), myID);
	                MatchStatistics ms = new MatchStatistics(match, mlt);
					MatchType type = mlt.getMatchType();
					if ( type != MatchType.MASTERS) { // MASTERS counts only for experience
						tp.addPrimarySkillPositionMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getTrainingSkillPositions()));
						tp.addPrimarySkillBonusPositionMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getTrainingSkillBonusPositions()));
						tp.addPrimarySkillSecondaryPositionMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getTrainingSkillSecondaryTrainingPositions()));
						tp.addPrimarySkillOsmosisPositionMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getTrainingSkillOsmosisTrainingPositions()));
						tp.addSecondarySkillMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getTrainingSkillPositions()));
						tp.addSecondarySkillBonusMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getTrainingSkillBonusPositions()));
						tp.addSecondarySkillSecondaryPositionMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getTrainingSkillSecondaryTrainingPositions()));
						tp.addSecondarySkillOsmosisTrainingMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getTrainingSkillOsmosisTrainingPositions()));
					}
					tp.addTotalMinutesPlayed(ms.getTrainMinutesPlayedInPositions(playerID, null));
					output.addExperienceIncrease(min(90,tp.getTotalMinutesPlayed() - minutes), type );
	                minutes = tp.getTotalMinutesPlayed();
				}
	            TrainingPoints trp = new TrainingPoints(wt.getPrimaryTraining(tp), wt.getSecondaryTraining(tp));

	        	// get experience increase of national matches
				if  ( inputPlayer.getNationalTeamID() != 0 && inputPlayer.getNationalTeamID() != myID){
					// TODO check if national matches are stored in database
					var nationalMatches = train.getMatches(inputPlayer.getNationalTeamID());
					for (var match : nationalMatches){
						MatchLineupTeam mlt = DBManager.instance().getMatchLineupTeam(match.getMatchID(), inputPlayer.getNationalTeamID());
						MatchStatistics ms = new MatchStatistics(match, mlt);
						minutes = ms.getStaminaMinutesPlayedInPositions(playerID);
						if ( minutes > 0 ) {
							output.addExperienceIncrease(min(90,minutes), mlt.getMatchType());
						}
					}
				}

	    		if (TrainingManager.TRAININGDEBUG) {
					HOLogger.instance().debug(getClass(), "Week " + train.getHattrickDate().getWeek()
	            		+": Player " + inputPlayer.getFullName() + " (" + playerID + ")"
	            		+" played total " + tp.getTotalMinutesPlayed() + " mins for training purposes and got "
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
								  int trainerSkill,
								  List<StaffMember> staff) {

		// Generate a map of players from the previous hrf.
		final Map<Integer, Player> players = new HashMap<>();
		for (Player p : previousPlayers) {
			players.put(p.getSpielerID(), p);
		}

		// Train each player
		for (Player player : currentPlayers) {
			try {

				// The version of the player from last hrf
				Player old = players.get(player.getSpielerID());
				if (old == null) {
					if (TrainingManager.TRAININGDEBUG) {
						HOLogger.instance().debug(HOModel.class, "Old player for id " + player.getSpielerID() + " = null");
					}
					// Player appears the first time
					// - was bought new
					// - promoted from youth
					// - it is the first hrf ever loaded
					old = new Player();
					old.setSpielerID(player.getSpielerID());
					old.copySkills(player);
					if (HOVerwaltung.instance().getModel().getCurrentPlayer(player.getSpielerID()) != null) {
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
					tmpOld.setSpielerID(old.getSpielerID());
					tmpOld.setAlter(old.getAlter());

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
							calculationPlayer.setSpielerID(tmpOld.getSpielerID());
							calculationPlayer.setAlter(tmpOld.getAlter());
						}

						calculationPlayer.calcIncrementalSubskills(tmpOld, tpw.getAssistants(),
								trainerSkill,
								tpw.getTrainingIntensity(),
								tpw.getStaminaPart(),
								tpw,
								staff);

						if (iter.hasNext()) {
							// Use calculated skills and subskills as "old" if there is another week in line...
							tmpOld = new Player();
							tmpOld.copySkills(calculationPlayer);
							tmpOld.copySubSkills(calculationPlayer);
							tmpOld.setSpielerID(calculationPlayer.getSpielerID());
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

		int playerID = after.getSpielerID();
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

	public TrainingPerWeek getLastTrainingWeek() {
		return this._WeekManager.getLastTrainingWeek();
	}
}
