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
    static final public boolean TRAININGDEBUG = true;

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

	/**
	 * Change the trained skills of the current players
	 *
	 * @param nextTrainingDate next training date of the current download. It is used to detect previous weeks if more
	 *                         than one week is calculated
	 * @param trainingsWeeksList list of the training weeks incl. training parameters
	 * @param currentPlayers status of the current players
	 * @param previousPlayers status of the players of the last download
	 * @param trainerSkill skill level of the trainer
	 * @param staff	is used to extract the assistant trainer level
	 */
	public void calculateTraining(Timestamp nextTrainingDate,
								  List<TrainingPerWeek> trainingsWeeksList,
								  List<Player> currentPlayers,
								  List<Player> previousPlayers,
								  int trainerSkill,
								  List<StaffMember> staff) {

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

				if (trainingsWeeksList.size() > 0) {
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
					Iterator<TrainingPerWeek> iter = trainingsWeeksList.iterator();
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
								tpw.getTrainingIntensity(),
								tpw.getStaminaPart(),
								tpw,
								staff);

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

					if (trainingsWeeksList.size() > 0)
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

	public TrainingPerWeek getLastTrainingWeek() {
		return this._WeekManager.getLastTrainingWeek();
	}
}
