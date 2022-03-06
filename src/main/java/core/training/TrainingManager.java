package core.training;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HODateTime;
import core.util.HOLogger;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

/**
 * Singleton class that holds training information.
 * The class is initialized during startup procedure
 */
public class TrainingManager implements PropertyChangeListener {

	// singleton class
	private static TrainingManager m_clInstance;

	private TrainingPerWeek nextWeekTraining;        						// used to determine training bar, include upcoming game   => Created at initialization
    private TrainingWeekManager recentTrainings;     						// trainings that took place (if any null otherwise) since last entry in Training table  => Created at initialization
	private List<TrainingPerWeek> historicalTrainings;          			// used to populate training history, no match information => Created at initialization

	private HODateTime lastTrainingDate;

	public static final boolean TRAINING_DEBUG = false;


	public void propertyChange(PropertyChangeEvent evt) {
		HOLogger.instance().debug(this.getClass(), "HOVerwaltung model changed => TrainingManager and TrainingWeekManager are reinitialized");
		m_clInstance = null;
		if (recentTrainings != null) {
			recentTrainings.reset();
		}
	}

    /**
     * Creates a new instance of TrainingsManager
     */
    private TrainingManager() {

		if (HOVerwaltung.instance().getModel().getBasics().isNationalTeam()) {
			historicalTrainings = null;
			nextWeekTraining = null;
			recentTrainings = null;
			return;
		}

    	// Load historical trainings from 'trainings' table
		historicalTrainings = DBManager.instance().getTrainingList();

		// Create recent training history from other tables in database
		if (!historicalTrainings.isEmpty()) {
			HODateTime previousTrainingDate = historicalTrainings.stream()
					.map(TrainingPerWeek::getTrainingDate)
					.max(HODateTime::compareTo).get();
			recentTrainings = new TrainingWeekManager(previousTrainingDate.plus(1, ChronoUnit.DAYS), false, true);
		}
		else {
			var startDate = HOVerwaltung.instance().getModel().getBasics().getActivationDate();
			if ( startDate != null ) {
				recentTrainings = new TrainingWeekManager(startDate, false, true);
			}
		}
		// Load next week training
		nextWeekTraining = recentTrainings.getNextWeekTraining();
		lastTrainingDate = recentTrainings.getLastUpdateDate();
		HOVerwaltung.instance().addPropertyChangeListener(this);
    }

	/**
	 * compute vector of trainingPerWeeks Vector between 2 dates to be used for subskill recalculation
	 */
    public List<TrainingPerWeek> getHistoricalTrainingsBetweenDates(HODateTime startDate, HODateTime endDate){
		List<TrainingPerWeek> result = historicalTrainings.stream()
				.filter(t->t.getTrainingDate().isBefore(endDate) && !startDate.isAfter(t.getTrainingDate()))
				.collect(Collectors.toList());

		result.forEach(TrainingPerWeek::loadMatches);

    	return result;
	}

    public void updateHistoricalTrainings(){
    	// push trainings that took place since last update into Trainings table
		recentTrainings.pushPastTrainings2TrainingsTable();
		
		// update historical trainings
		historicalTrainings = DBManager.instance().getTrainingList();
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

	public HODateTime getLastTrainingDate() {
		return lastTrainingDate;
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
			HOLogger.instance().debug(getClass(), playerID + "|" + playerName + "|" + age + "|" + changes);
		}
	}


}
