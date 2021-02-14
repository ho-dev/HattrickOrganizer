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
