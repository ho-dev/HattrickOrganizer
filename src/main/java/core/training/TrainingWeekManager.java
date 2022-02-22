package core.training;

import core.db.DBManager;
import core.db.JDBCAdapter;
import core.model.HOVerwaltung;
import core.model.enums.DBDataSource;
import core.util.DateTimeUtils;
import core.util.HOLogger;
import module.transfer.test.HTWeek;

import java.sql.ResultSet;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


/**
 * Class that create a list of TrainingPerWeek objects between 2 dates.
 *
 * FIXME DB logic should be pushed down into the DB layer, there is a lot of code duplication in this class
 */
public class TrainingWeekManager {

	// cl_NextTrainingDate is seen not as of now but as of LastUpdateDate
    // TODO This belongs in TrainingManager.
	private Instant nextTrainingDate;
	private Instant lastUpdateDate;

    private List<TrainingPerWeek> m_Trainings;
    private Instant m_StartDate;
	private Boolean m_IncludeUpcomingTrainings;

	/**
	 * Construct a list of TrainingPerWeek since provided initial training Date
	 * @param startDate initial training Date
	 * @param includeUpcomingTrainings whether or not the TrainingPerWeek objects will contain upcoming match information
	 * @param includeMatches whether or not the TrainingPerWeek objects will contain match information
	 */
	public TrainingWeekManager(Instant startDate, boolean includeUpcomingTrainings, boolean includeMatches) {
		if (HOVerwaltung.instance().getModel() == null) {
			HOLogger.instance().error(this.getClass(), "model not yet initialized");
		} else {
			getNextTrainingDate();
		}

		m_StartDate = startDate;
		m_IncludeUpcomingTrainings = includeUpcomingTrainings;
		m_Trainings = createTrainingListFromHRF(includeMatches);
	}

	public TrainingWeekManager(Instant startDate, boolean includeUpcomingTrainings) {
		this(startDate, includeUpcomingTrainings, false);
	}

	public void reset() {
		nextTrainingDate = null;
	}

	public TrainingPerWeek getNextWeekTraining() {

		var nextTrainingDate = getNextTrainingDate();
		if (nextTrainingDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(DateTimeUtils.DEFAULT_TIMEZONE);
			String refDate = formatter.format(nextTrainingDate);

			String sql = String.format("""
					SELECT TRAININGDATE, TRAININGSART, TRAININGSINTENSITAET, STAMINATRAININGPART, COTRAINER, TRAINER
					FROM XTRADATA
					INNER JOIN TEAM on XTRADATA.HRF_ID = TEAM.HRF_ID
					INNER JOIN VEREIN on XTRADATA.HRF_ID = VEREIN.HRF_ID
					INNER JOIN SPIELER on XTRADATA.HRF_ID = SPIELER.HRF_ID AND SPIELER.TRAINER > 0
					INNER JOIN (
					     SELECT TRAININGDATE, max(HRF_ID) MAX_HR_ID FROM XTRADATA GROUP BY TRAININGDATE
					) IJ1 ON XTRADATA.HRF_ID = IJ1.MAX_HR_ID
					WHERE XTRADATA.TRAININGDATE > '%s'""", refDate);

			HOLogger.instance().info(TrainingWeekManager.class, "Next week training date: " + nextTrainingDate);

			try {

				final JDBCAdapter ijdbca = DBManager.instance().getAdapter();
				final ResultSet rs = ijdbca.executeQuery(sql);
				if (rs != null) {
					if (!rs.last()) {
						HOLogger.instance().error(TrainingWeekManager.class, "Error while performing getNextWeekTraining(): empty result set");
						return null;
					}

					int numRows = rs.getRow();

					if (numRows != 1) {
						HOLogger.instance().error(TrainingWeekManager.class, "Error while performing getNextWeekTraining(): numRows: " + numRows);
						return null;
					}

					rs.beforeFirst();

					if (rs.next()) {
						int trainType = rs.getInt("TRAININGSART");
						int trainIntensity = rs.getInt("TRAININGSINTENSITAET");
						int trainStaminaPart = rs.getInt("STAMINATRAININGPART");
						Instant trainingDate = rs.getTimestamp("TRAININGDATE").toInstant();
						int coachLevel = rs.getInt("TRAINER");
						int trainingAssistantLevel = rs.getInt("COTRAINER");
						return new TrainingPerWeek(trainingDate,
								trainType,
								trainIntensity,
								trainStaminaPart,
								trainingAssistantLevel,
								coachLevel,
								DBDataSource.HRF,
								true);
					}
				}
			} catch (Exception e) {
				HOLogger.instance().error(TrainingWeekManager.class, "Error while performing getNextWeekTraining():  " + e);
			}
		}
		return null;
	}

	private Instant getNextTrainingDate() {
		if (nextTrainingDate == null) {
			nextTrainingDate = HOVerwaltung.instance().getModel().getXtraDaten().getNextTrainingDateAsInstant();
			lastUpdateDate = DBManager.instance().getMaxHrf().getDatum().toInstant();
		}
		return nextTrainingDate;
	}

	/**
	 * Create the list of trainings from DB but excluding 'Trainings' table
	 *  missing weeks are created by duplicating previous entry
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

		long nbWeeks = ChronoUnit.DAYS.between(m_StartDate, nextTrainingDate) / 7;


		HTDatetime dtiTrainingDate = new HTDatetime(nextTrainingDate);

		ZonedDateTime zdtCurrDate = dtiTrainingDate.getHattrickTime().minus(nbWeeks * 7, ChronoUnit.DAYS);

		Instant currDate = zdtCurrDate.toInstant();

		while (!currDate.isAfter(nextTrainingDate)) {

			if ((!m_IncludeUpcomingTrainings) && (HTDatetime.isAfterLastUpdate(zdtCurrDate))) {
				break;
			}

			// when daylight saving time changes, the interval between two trainings is not exactly 7 days,
			// so we need to calculate a ht week instance
			var training = trainingsInDB.get(new HTWeek(currDate).weekSinceOrigin());
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

			zdtCurrDate = zdtCurrDate.plus(7, ChronoUnit.DAYS);
			currDate = zdtCurrDate.toInstant();
		}

		return trainings;
	}

	/**
	 * Fetch trainings information from database (excl. Training table)
	 * This excludes manual entries set per user
	 */
	private HashMap<Long, TrainingPerWeek> createTPWfromDBentries() {

		HashMap<Long, TrainingPerWeek> output = new HashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC));

		// for past trainings the first hrf after training date is the best guess
		// - add one week to next training date of the week
		// - use min(hrf) here
		String startDate = formatter.format(m_StartDate.plus(Duration.ofDays(7)));
		String sql = String.format("""
					SELECT TRAININGDATE, TRAININGSART, TRAININGSINTENSITAET, STAMINATRAININGPART, COTRAINER, TRAINER
					FROM XTRADATA
					INNER JOIN TEAM on XTRADATA.HRF_ID = TEAM.HRF_ID
					INNER JOIN VEREIN on XTRADATA.HRF_ID = VEREIN.HRF_ID
					INNER JOIN SPIELER on XTRADATA.HRF_ID = SPIELER.HRF_ID AND SPIELER.TRAINER > 0
					INNER JOIN (
					     SELECT TRAININGDATE, min(HRF_ID) MIN_HRF_ID FROM XTRADATA GROUP BY TRAININGDATE
					) IJ1 ON XTRADATA.HRF_ID = IJ1.MIN_HRF_ID
					WHERE XTRADATA.TRAININGDATE >= '%s'""",startDate);

		try {

			final JDBCAdapter ijdbca = DBManager.instance().getAdapter();
			final ResultSet rs = ijdbca.executeQuery(sql);
			if ( rs != null ) {
				rs.beforeFirst();
				while (rs.next()) {
					int trainType = rs.getInt("TRAININGSART");
					int trainIntensity = rs.getInt("TRAININGSINTENSITAET");
					int trainStaminaPart = rs.getInt("STAMINATRAININGPART");
					// subtract one week from next training date to get the past week training date
					Instant trainingDate = rs.getTimestamp("TRAININGDATE").toInstant().minus(Duration.ofDays(7));
					int coachLevel = rs.getInt("TRAINER");
					int trainingAssistantLevel = rs.getInt("COTRAINER");
					TrainingPerWeek tpw = new TrainingPerWeek(trainingDate,
							trainType,
							trainIntensity,
							trainStaminaPart,
							trainingAssistantLevel,
							coachLevel,
							DBDataSource.HRF);
					output.put(new HTWeek(trainingDate).weekSinceOrigin(), tpw);
				}
			}
		}
		catch (Exception e) {
			HOLogger.instance().error(this.getClass(), "Error while performing fetchTrainingListFromDB():  " + e);
		}

		return output;
	}

    public List<TrainingPerWeek> getTrainingList() {
    	return m_Trainings;
    }


	public Instant getLastUpdateDate() {
		return lastUpdateDate;
	}

	/**
	 * The function push elements of m_Trainings into Training table but not replacing existing entries
	 *
	 */
	public void push2TrainingsTable(){
		DBManager.instance().saveTrainings(m_Trainings, lastUpdateDate,  false);
	}

	/**
	 * The function push elements of m_Trainings into Training table but not replacing existing entries
	 *
	 */
	public void pushPastTrainings2TrainingsTable(){
		List<TrainingPerWeek> pastTrainingsSinceLastUpdate = new ArrayList<>();
		for (var training: m_Trainings) {
			if (training.getTrainingDate().isBefore(nextTrainingDate)) {
				pastTrainingsSinceLastUpdate.add(training);
			}
		}
		DBManager.instance().saveTrainings(pastTrainingsSinceLastUpdate, lastUpdateDate, false);
	}

}
