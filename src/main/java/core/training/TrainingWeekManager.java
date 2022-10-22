package core.training;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.enums.DBDataSource;
import core.util.HODateTime;
import core.util.HOLogger;

import java.time.temporal.ChronoUnit;
import java.util.*;


/**
 * Class that create a list of TrainingPerWeek objects between 2 dates.
 */
public class TrainingWeekManager {

	// cl_NextTrainingDate is seen not as of now but as of LastUpdateDate
    // TODO This belongs in TrainingManager.
	private HODateTime nextTrainingDate;
	private HODateTime lastUpdateDate;

	private final List<TrainingPerWeek> m_Trainings;
	private final HODateTime m_StartDate;
	private final Boolean m_IncludeUpcomingTrainings;

	/**
	 * Construct a list of TrainingPerWeek since provided initial training Date
	 *
	 * @param startDate                initial training Date
	 * @param includeUpcomingTrainings whether or not the TrainingPerWeek objects will contain upcoming match information
	 * @param includeMatches           whether or not the TrainingPerWeek objects will contain match information
	 */
	public TrainingWeekManager(HODateTime startDate, boolean includeUpcomingTrainings, boolean includeMatches) {
		if (HOVerwaltung.instance().getModel() == null) {
			HOLogger.instance().error(this.getClass(), "model not yet initialized");
		} else {
			getNextTrainingDate();
		}

		m_StartDate = startDate;
		m_IncludeUpcomingTrainings = includeUpcomingTrainings;
		m_Trainings = createTrainingListFromHRF(includeMatches);
	}

	public TrainingWeekManager(HODateTime startDate, boolean includeUpcomingTrainings) {
		this(startDate, includeUpcomingTrainings, false);
	}

	public void reset() {
		nextTrainingDate = null;
		lastUpdateDate = null;
	}

	public TrainingPerWeek getNextWeekTraining() {
		var nextTrainingDate = getNextTrainingDate();
		if (nextTrainingDate != null) {
			var trainings = DBManager.instance().loadTrainingPerWeek(nextTrainingDate.toDbTimestamp(), false);
			HOLogger.instance().info(TrainingWeekManager.class, "Next week training date: " + nextTrainingDate.toHT());

			int numRows = trainings.size();
			if (numRows == 0) {
				HOLogger.instance().error(TrainingWeekManager.class, "Error while performing getNextWeekTraining(): empty result set");
				return null;
			}

			if (numRows != 1) {
				HOLogger.instance().error(TrainingWeekManager.class, "Error while performing getNextWeekTraining(): numRows: " + numRows);
				// training date changed or hrf from another team loaded (select latest)
				var ret = trainings.stream().max(Comparator.comparing(TrainingPerWeek::getTrainingDate));
				return ret.get();
			}
			return trainings.get(0);

		}
		return null;
	}

	private HODateTime getNextTrainingDate() {
		if (nextTrainingDate == null) {
			var hoModel = HOVerwaltung.instance().getModel();
			if (hoModel != null) {
				var xtra = hoModel.getXtraDaten();
				if (xtra != null) {
					nextTrainingDate = xtra.getNextTrainingDate();
				}
			}
		}
		return nextTrainingDate;
	}

	/**
	 * Create the list of trainings from DB but excluding 'Trainings' table
	 * missing weeks are created by duplicating previous entry
	 */
	private List<TrainingPerWeek> createTrainingListFromHRF(boolean includeMatches) {

		List<TrainingPerWeek> trainings = new ArrayList<>();

		if (nextTrainingDate == null) return trainings;    // initial call (no data downloaded yet)

		HashMap<Long, TrainingPerWeek> trainingsInDB = createTPWfromDBentries();
		int trainingsSize;

		if (m_StartDate.isAfter(nextTrainingDate)) {
			HOLogger.instance().error(this.getClass(), "It was assumed that start date will always be before next training date in database");
			return trainings;
		}

		var nbWeeks = ChronoUnit.DAYS.between(m_StartDate.instant, nextTrainingDate.instant) / 7;
		//var currDate = nextTrainingDate.minus((int)nbWeeks * 7, ChronoUnit.DAYS);
		var currDate = nextTrainingDate.plusDaysAtSameLocalTime(-7 * (int) nbWeeks);
		while (!currDate.isAfter(nextTrainingDate)) {

			if ((!m_IncludeUpcomingTrainings) && (currDate.isAfter(getLastUpdateDate()))) {
				break;
			}

			// when daylight saving time changes, the interval between two trainings is not exactly 7 days,
			// so we need to calculate a ht week instance
			var htWeek = currDate.toLocaleHTWeek();
			var training = trainingsInDB.get(htWeek.sinceOrigin());
			if (training != null) {
				if (includeMatches) {
					training.loadMatches();
				}
				trainings.add(training);
			} else {
				trainingsSize = trainings.size();
				if (trainingsSize != 0) {
					var previousTraining = trainings.get(trainingsSize - 1);
					var tpw = new TrainingPerWeek(currDate,
							previousTraining.getTrainingType(),
							previousTraining.getTrainingIntensity(),
							previousTraining.getStaminaShare(),
							previousTraining.getTrainingAssistantsLevel(),
							previousTraining.getCoachLevel(),
							DBDataSource.GUESS,
							includeMatches);
					trainings.add(tpw);
				} else {
					var tpw = new TrainingPerWeek(currDate, -1, 0, 0, 0, 0,
							DBDataSource.GUESS, includeMatches);
					trainings.add(tpw);
				}
			}
			currDate = currDate.plusDaysAtSameLocalTime(7);
		}

		return trainings;
	}

	/**
	 * Fetch trainings information from database (excl. Training table)
	 * This excludes manual entries set per user
	 */
	private HashMap<Long, TrainingPerWeek> createTPWfromDBentries() {

		HashMap<Long, TrainingPerWeek> output = new HashMap<>();
		var startDate = m_StartDate.plus(7, ChronoUnit.DAYS);
		for (var trainingPerWeek : DBManager.instance().loadTrainingPerWeek(startDate.toDbTimestamp(), true)) {
			output.put(trainingPerWeek.getTrainingDate().toLocaleHTWeek().sinceOrigin(), trainingPerWeek);
		}
		return output;
	}

	public List<TrainingPerWeek> getTrainingList() {
		return m_Trainings;
	}

	public HODateTime getLastUpdateDate() {
		if (lastUpdateDate == null) {
			lastUpdateDate = DBManager.instance().getLatestHRF().getDatum();
		}
		return lastUpdateDate;
	}

	/**
	 * The function push elements of m_Trainings into Training table but not replacing existing entries
	 */
	public void push2TrainingsTable() {
		DBManager.instance().saveTrainings(m_Trainings, getLastUpdateDate());
	}

	/**
	 * The function push elements of m_Trainings into Training table but not replacing existing entries
	 */
	public void pushPastTrainings2TrainingsTable() {
		var nextTraining = getNextTrainingDate();
		if ( nextTraining == null) return;
		List<TrainingPerWeek> pastTrainingsSinceLastUpdate = new ArrayList<>();
		for (var training : m_Trainings) {
			if (training.getTrainingDate().isBefore(nextTraining)) {
				pastTrainingsSinceLastUpdate.add(training);
			}
		}
		DBManager.instance().saveTrainings(pastTrainingsSinceLastUpdate, getLastUpdateDate());
	}

}