package core.db;

import core.HO;
import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.training.TrainingWeekManager;
import core.util.HOLogger;
import core.util.HTDatetime;

import javax.swing.*;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


final class DBConfigUpdater {
	final static DBManager dbManager = DBManager.instance();
	final static JDBCAdapter m_clJDBCAdapter = dbManager.getAdapter();


	public static void updateDBConfig(double configVersion) {

		double currentConfigVersion = ((UserConfigurationTable) dbManager.getTable(UserConfigurationTable.TABLENAME)).getLastConfUpdate();

		/*
		 * We have to use separate 'if-then' clauses for each conf version  (ascending order) because a user might have skipped some HO releases
		 * DO NOT use 'if-then-else' here, as this would ignores some updates!
		 */

		if (currentConfigVersion < 1.436) {
			HOLogger.instance().log(DBConfigUpdater.class, "DB config version " + currentConfigVersion + " is too old");
			try {
				JOptionPane.showMessageDialog(null,
						"DB is too old.\nPlease update first to HO! 3.0", "Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				HOLogger.instance().log(DBConfigUpdater.class, e);
			}
			System.exit(0);
		}

		if ((currentConfigVersion < 5) || (HO.isDevelopment() && (currentConfigVersion == 5))) {
			HOLogger.instance().log(DBConfigUpdater.class, "Updating configuration to version 5.0  ...");
			updateDBConfig5(configVersion, HO.isDevelopment() && (currentConfigVersion == 5));
		}

	}

	private static void updateDBConfig5(double configVersion, boolean alreadyApplied){

		if (!alreadyApplied) {

			TrainingWeekManager twm=null;

			// Creating entries into TRAININGS table ===========================================================
			try {
				String sql = "SELECT TRAININGDATE FROM XTRADATA ORDER BY TRAININGDATE ASC LIMIT 1";
				ResultSet rs = m_clJDBCAdapter.executeQuery(sql);
				rs.next();
				Instant firstTrainingDate = rs.getTimestamp("TRAININGDATE").toInstant();
				twm = new TrainingWeekManager(firstTrainingDate, false);
				twm.push2TrainingsTable();
			} catch (Exception e) {
				HOLogger.instance().error(DBConfigUpdater.class, "Error when trying to create entries inside TRAINING table");
			}

			// Creating entries into FUTURETRAININGS table ===========================================================
			try {
				var trainingList = twm.getTrainingList();
				Optional<TrainingPerWeek> optionallastTraining = trainingList.stream().max(Comparator.comparing(TrainingPerWeek::getTrainingDate));

				if(optionallastTraining.isEmpty()){

					throw new Exception("Can't determine last training");
				}

				// determine parameter of last training
				TrainingPerWeek latestTraining = optionallastTraining.get();
				int assistantLevel = latestTraining.getTrainingAssistantsLevel();
				int coachLevel = latestTraining.getCoachLevel();
				Instant lastTrainnig = latestTraining.getTrainingDate();

				HTDatetime oTrainingDate = new HTDatetime(lastTrainnig);
				ZonedDateTime zdtLastTraining =  oTrainingDate.getHattrickTime();
				int latestTrainingWeek = oTrainingDate.getHTWeekLocalized();
				int latestTrainingSeason = oTrainingDate.getHTSeasonLocalized();

				// iterate through entries of Future Training table and migrate data
				List<TrainingPerWeek> futureTrainings = new ArrayList<>();
				List<TrainingPerWeek> futureTrainingsInDB = DBManager.instance().getFutureTrainingsVector();

				ZonedDateTime zdtFutureTrainingDate;
				int iWeekNumber, iSeasonNumber, nbDays;
				TrainingPerWeek futureTraining;

				for(TrainingPerWeek futureTrainingDB : futureTrainingsInDB){

					iWeekNumber = futureTrainingDB.getTrainingAssistantsLevel();
					iSeasonNumber = futureTrainingDB.getCoachLevel();
					nbDays = ((iSeasonNumber - latestTrainingSeason) * 16 + (iWeekNumber - latestTrainingWeek)) * 7 ;
					if(nbDays <= 0){
						continue;
					}

					zdtFutureTrainingDate = zdtLastTraining.plus(nbDays, ChronoUnit.DAYS);
					futureTraining = new TrainingPerWeek(zdtFutureTrainingDate.toInstant(), futureTrainingDB.getTrainingType(), futureTrainingDB.getTrainingIntensity(),
							futureTrainingDB.getStaminaShare(), assistantLevel, coachLevel, DBDataSource.MANUAL);
					futureTrainings.add(futureTraining);
				}

				// store futureTrainings in database
				DBManager.instance().clearFutureTrainingsTable();
				DBManager.instance().saveFutureTrainings(futureTrainings);

			} catch (Exception e) {
				HOLogger.instance().error(DBConfigUpdater.class, "Error when trying to create entries inside TRAINING table");
			}

			updateDBConfigVersion(configVersion, 5d);
		}
	}


	private static void updateDBConfigVersion(double DBConfigVersion, double version) {
		if (version < DBConfigVersion) {
				HOLogger.instance().debug(DBConfigUpdater.class, "Update to config " + version + " done. Updating DBConfigVersion");
				dbManager.saveUserParameter("LastConfUpdate", version);
		}
		else if (version == DBConfigVersion){
				HOLogger.instance().debug(DBConfigUpdater.class, "DB config update complete, setting DBConfigVersion to " + version);
				dbManager.saveUserParameter("LastConfUpdate", version);
		}
		else {
			HOLogger.instance().error(DBConfigUpdater.class,
					"Error trying to set DBConfigVersion to unidentified value:  " + version + " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}



}
