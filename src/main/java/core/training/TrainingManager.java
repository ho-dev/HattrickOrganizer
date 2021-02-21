package core.training;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HOLogger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * Singleton class that holds training information.
 * The class is initilized during startup procedure
 */
public class TrainingManager {

	// singleton class
	private static TrainingManager m_clInstance;

	private TrainingPerWeek nextWeekTraining;        // used to determine training bar, include upcoming game   => Created at initilization
    private TrainingWeekManager recentTrainings;     // trainings that took place (if any null otherwise) since last entry in Training table  => Created at initilization
	private List<TrainingPerWeek> historicalTrainings;         // used to populate training history, no match information => Created at initilization


	public static final boolean TRAININGDEBUG = false;



    /**
     * Creates a new instance of TrainingsManager
     */
    private TrainingManager() {

    	// Load historical trainings from 'trainings' table
		historicalTrainings =  DBManager.instance().getTrainingList();

		// Create recent training history from other tables in database
		if (!historicalTrainings.isEmpty()) {
			Instant previousTrainingDate = historicalTrainings.stream()
					.map(TrainingPerWeek::getTrainingDate)
					.max(Instant::compareTo).get();
			recentTrainings = new TrainingWeekManager(previousTrainingDate.plus(1, ChronoUnit.DAYS), false, true);
			//TODO: add entries in trainings from recentTrainings => should it be done from here ?
			//TODO: the function DBManager.instance().saveTrainings() should refresh table in training tab
			// DBManager.instance().saveTrainings(recentTrainings.getTrainingList(), false);

			// Load next week training
			nextWeekTraining = TrainingWeekManager.getNextWeekTraining(true);
		}
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

	public List<TrainingPerWeek> getHistoricalTrainings() {
		return historicalTrainings;
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
	private void logPlayerProgress (Player before, Player after) {

		if ((after == null) || (before == null)) {
			// crash due to non paranoid logging is too silly
			return;
		}

		int playerID = after.getPlayerID();
		String playerName = after.getFullName();

		int age = after.getAlter();

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
