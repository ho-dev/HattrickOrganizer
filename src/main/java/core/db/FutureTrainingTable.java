package core.db;

import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.training.TrainingPerWeek;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class FutureTrainingTable extends AbstractTable {

	/**
	 * tablename
	 **/
	public final static String TABLENAME = "FUTURETRAINING";

	protected FutureTrainingTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("TRAINING_DATE", Types.TIMESTAMP, false),
				new ColumnDescriptor("TRAINING_TYPE", Types.INTEGER, false),
				new ColumnDescriptor("TRAINING_INTENSITY", Types.INTEGER, false),
				new ColumnDescriptor("STAMINA_SHARE", Types.INTEGER, false),
				new ColumnDescriptor("COACH_LEVEL", Types.INTEGER, false),
				new ColumnDescriptor("TRAINING_ASSISTANTS_LEVEL", Types.INTEGER, false)
		};
	}

	List<TrainingPerWeek> getFutureTrainingsVector() {
		var vTrainings = new ArrayList<TrainingPerWeek>();
		String query = "select * from " + getTableName();
		ResultSet rs = adapter.executeQuery(query);

		try {
			if (rs != null) {
				rs.beforeFirst();

				while (rs.next()) {
					var trainingDate = rs.getTimestamp("TRAINING_DATE").toInstant();
					var training_type = rs.getInt("TRAINING_TYPE");
					var training_intensity = rs.getInt("TRAINING_INTENSITY");
					var staminaShare = rs.getInt("STAMINA_SHARE");
					var trainingAssistantsLevel = rs.getInt("TRAINING_ASSISTANTS_LEVEL");
					var coachLevel = rs.getInt("COACH_LEVEL");

					var tpw = new TrainingPerWeek(trainingDate, training_type, training_intensity, staminaShare, trainingAssistantsLevel,
							coachLevel);

					vTrainings.add(tpw);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getTraining " + e);
		}

		var futures = new ArrayList<TrainingPerWeek>();
		var actualDate = HOVerwaltung.instance().getModel().getXtraDaten().getNextTrainingDate().toInstant();
		for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
			// load the training from DB
			TrainingPerWeek train = null;

			for (var t : vTrainings) {
				if (t.getTrainingDate().equals(actualDate)) {
					train = t;
					break;
				}
			}

			// if not found create it and saves it
			if (train == null) {
				train = new TrainingPerWeek(actualDate, -1, -1, -1, -1, -1);
				storeFutureTraining(train);
			}
			futures.add(train);
			actualDate.plus(Duration.ofDays(7));
		}

		return futures;
	}

	int loadFutureTrainings(Timestamp trainingDate) {
		String query = "select TRAINING_TYPE from " + getTableName() + " where TRAINING_DATE='" + trainingDate + "'";
		ResultSet rs = adapter.executeQuery(query);

		try {
			if (rs != null) {
				rs.beforeFirst();

				if (rs.next()) {
					return (rs.getInt("TYPE"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getTraining " + e);
		}
		return -1;
	}

	void storeFutureTraining(TrainingPerWeek training) {
		if (training != null) {
			HOLogger.instance().error(getClass(), "storeFutureTraining() disabled as it crashed HO!");
//			String statement =
//					"update " + getTableName() +
//							" set TRAINING_TYPE= " + training.getTrainingType() +
//							", TRAINING_INTENSITY=" + training.getTrainingIntensity() +
//							", STAMINA_SHARE=" + training.getStaminaShare() +
//							", COACH_LEVEL=" + training.getCoachLevel() +
//							", TRAINING_ASSISTANTS_LEVEL=" + training.getTrainingAssistantsLevel() +
//							" WHERE TRAINING_DATE='" + training.getTrainingDate() + "'";
//			int count = adapter.executeUpdate(statement);
//
//			if (count == 0) {
//				statement = "INSERT INTO " + getTableName() +
//						" (TRAINING_DATE, TRAINING_TYPE, TRAINING_INTENSITY, STAMINA_SHARE, COACH_LEVEL, TRAINING_ASSISTANTS_LEVEL) VALUES ('" +
//						training.getTrainingDate() + "'," +
//						training.getTrainingType() + "," +
//						training.getTrainingIntensity() + "," +
//						training.getStaminaShare() + "," +
//						training.getCoachLevel() + "," +
//						training.getTrainingAssistantsLevel();
//				adapter.executeUpdate(statement);
//			}
		}
	}
}
