package core.training;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.misc.TrainingEvent;
import core.model.player.Player;
import core.util.HOLogger;
import core.util.HelperWrapper;
import java.sql.Timestamp;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * Singleton class that holds training information.
 * The class is initilized during startup procedure
 */
public class TrainingManager {

	// singleton class
	private static TrainingManager m_clInstance;

	private TrainingPerWeek nextWeekTraining;            // used to determine training bar, include upcoming game   => Created at initilization
    private TrainingWeekManager recentTrainings;         // trainings information since last HRF used in regular subskill calculation  => Created at initilization
	private List<TrainingPerWeek> trainings;             // used to populate training history, no match information => Created at initilization


	public static final boolean TRAININGDEBUG = false;



    /**
     * Creates a new instance of TrainingsManager
     */
    private TrainingManager() {

    	// Load data from 'trainings' table
		trainings =  DBManager.instance().getTrainingList();

		// Create recent training history from other tables in database
        recentTrainings = new TrainingWeekManager(2, true, false);

		// Save recent training history in 'trainings' table
		DBManager.instance().saveTrainings(recentTrainings.getTrainingList(), false);

		//TODO: add entries in trainings from recentTrainings

        nextWeekTraining = new TrainingWeekManager(1, true, true).getTrainingList().get(0);


    }


    public static TrainingManager instance() {
        if (m_clInstance == null) {
            m_clInstance = new TrainingManager();
        }
        return m_clInstance;
    }

    public List<TrainingPerWeek> getRecentTrainings() {
        return recentTrainings.getTrainingList();
    }

	public TrainingPerWeek getNextWeekTraining() {
		return nextWeekTraining;
	}

	public List<TrainingPerWeek> getAllTrainings() {
		return trainings;
	}

    @Deprecated
    public void recalcSubskills(boolean showBar) {
        if (JOptionPane.showConfirmDialog(HOMainFrame.instance(),
        		HOVerwaltung.instance().getLanguageString("SubskillRecalcFull"),
				HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
            HOVerwaltung.instance().recalcSubskills(showBar, null);
        }
    }


	@Deprecated
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

	@Deprecated
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


}
