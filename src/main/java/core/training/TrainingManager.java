package core.training;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.util.HOLogger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

/**
 * Singleton class that holds training information.
 * The class is initilized during startup procedure
 */
public class TrainingManager implements PropertyChangeListener {

	// singleton class
	private static TrainingManager m_clInstance;

	private TrainingPerWeek nextWeekTraining;        // used to determine training bar, include upcoming game   => Created at initilization
    private TrainingWeekManager recentTrainings;     // trainings that took place (if any null otherwise) since last entry in Training table  => Created at initilization
	private List<TrainingPerWeek> historicalTrainings;         // used to populate training history, no match information => Created at initilization


	public static final boolean TRAININGDEBUG = false;


	public void propertyChange(PropertyChangeEvent evt) {
		HOLogger.instance().debug(this.getClass(), "HOVerwaltung model changed => TrainingManager and TrainingWeekManager are reinitialized");
		m_clInstance = null;
		TrainingWeekManager.reset();
	}

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

			// Load next week training
			nextWeekTraining = TrainingWeekManager.getNextWeekTraining();
		}

		HOVerwaltung.instance().addPropertyChangeListener(this);
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

    public List<TrainingPerWeek> getRecentTrainings(Timestamp since, Timestamp before) {
        //return recentTrainings.getTrainingList();
		return historicalTrainings.stream()
				.filter(t->t.getTrainingDate().isBefore(before.toInstant()) && !since.toInstant().isAfter(t.getTrainingDate()))
				.collect(Collectors.toList());
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
