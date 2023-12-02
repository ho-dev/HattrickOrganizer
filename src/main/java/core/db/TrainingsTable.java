package core.db;

import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * This table is different from others because it does not hold data from XML/HRFs but is a mixed of computed data and data entered
 * directly by Users. Hence, there is a method recalculateEntries() that will force refresh of entries.
 * This method will be called automatically after table creation and during upgrade to v5.0
 */
final class TrainingsTable extends AbstractTable {
	final static String TABLENAME = "TRAINING";
	
	TrainingsTable(ConnectionManager adapter){
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_DATE").setGetter((p) -> ((TrainingPerWeek) p).getTrainingDate().toDbTimestamp()).setSetter((p, v) -> ((TrainingPerWeek) p).setTrainingDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_TYPE").setGetter((p) -> ((TrainingPerWeek) p).getTrainingType()).setSetter((p, v) -> ((TrainingPerWeek) p).setTrainingType((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_INTENSITY").setGetter((p) -> ((TrainingPerWeek) p).getTrainingIntensity()).setSetter((p, v) -> ((TrainingPerWeek) p).setTrainingIntensity((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("STAMINA_SHARE").setGetter((p) -> ((TrainingPerWeek) p).getStaminaShare()).setSetter((p, v) -> ((TrainingPerWeek) p).setStaminaShare((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COACH_LEVEL").setGetter((p) -> ((TrainingPerWeek) p).getCoachLevel()).setSetter((p, v) -> ((TrainingPerWeek) p).setCoachLevel((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_ASSISTANTS_LEVEL").setGetter((p) -> ((TrainingPerWeek) p).getTrainingAssistantsLevel()).setSetter((p, v) -> ((TrainingPerWeek) p).setTrainingAssistantLevel((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SOURCE").setGetter((p) -> ((TrainingPerWeek) p).getSource().getValue()).setSetter((p, v) -> ((TrainingPerWeek) p).setSource(DBDataSource.getCode((int) v))).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	/**
	 * save provided training in database (trainings still in the future will be skipped)
	 * @param training training to be saved
	 */
	void saveTraining(TrainingPerWeek training, HODateTime lastTrainingDate) {
		if (training != null) {
			var trainingDate = training.getTrainingDate();
			if (trainingDate.isAfter(lastTrainingDate)) {
				return;
			}
			store(training);
		}
	}

	/**
	 * apply the function saveTraining() to all elements of the provided vector
	 */
	void saveTrainings(List<TrainingPerWeek> trainings, HODateTime lastTrainingDate) {
		for (var training:trainings) {
			saveTraining(training, lastTrainingDate);
		}
	}

	private String getTrainingListStatement(Timestamp from, Timestamp to) {
		if (from == null && to == null) {
			return createSelectStatement(" ORDER  BY TRAINING_DATE DESC");
		} else if (from == null) {
			return createSelectStatement(" WHERE TRAINING_DATE<?");
		} else if (to == null) {
			return createSelectStatement(" WHERE TRAINING_DATE>=?");
		} else {
			return createSelectStatement(" WHERE TRAINING_DATE>=? AND TRAINING_DATE<?");
		}
	}
	List<TrainingPerWeek> getTrainingList() {
		return getTrainingList(null, null);
	}

	public List<TrainingPerWeek> getTrainingList(Timestamp fromDate, Timestamp toDate) {
		var values = Stream.of(fromDate, toDate).filter(Objects::nonNull).toArray();
		var statement = getTrainingListStatement(fromDate, toDate);
		return load(TrainingPerWeek.class, connectionManager.executePreparedQuery(statement, values));
	}

	private final String trainingPerWeekSql = "SELECT TRAININGDATE, TRAININGSART, TRAININGSINTENSITAET, STAMINATRAININGPART, COTRAINER, TRAINER" +
			" FROM XTRADATA INNER JOIN TEAM on XTRADATA.HRF_ID = TEAM.HRF_ID" +
			" INNER JOIN VEREIN on XTRADATA.HRF_ID = VEREIN.HRF_ID" +
			" INNER JOIN SPIELER on XTRADATA.HRF_ID = SPIELER.HRF_ID AND SPIELER.TRAINER > 0" +
			" INNER JOIN (SELECT TRAININGDATE, %s(HRF_ID) J_HRF_ID FROM XTRADATA GROUP BY TRAININGDATE) IJ1 ON XTRADATA.HRF_ID = IJ1.J_HRF_ID" +
			" WHERE XTRADATA.TRAININGDATE >= ?";

	public List<TrainingPerWeek> loadTrainingPerWeek(Timestamp startDate, boolean all) {
		var ret = new ArrayList<TrainingPerWeek>();
		String sqlQuery = all? String.format(trainingPerWeekSql, "max"): String.format(trainingPerWeekSql, "min");
		try (final ResultSet rs = connectionManager.executePreparedQuery(sqlQuery, startDate)) {
			if ( rs != null ) {
				while (rs.next()) {
					int trainType = rs.getInt("TRAININGSART");
					int trainIntensity = rs.getInt("TRAININGSINTENSITAET");
					int trainStaminaPart = rs.getInt("STAMINATRAININGPART");
					// subtract one week from next training date to get the past week training date
					var nextTrainingDate = HODateTime.fromDbTimestamp(rs.getTimestamp("TRAININGDATE"));
					var trainingDate = nextTrainingDate.plusDaysAtSameLocalTime(-7);
					int coachLevel = rs.getInt("TRAINER");
					int trainingAssistantLevel = rs.getInt("COTRAINER");
					TrainingPerWeek tpw = new TrainingPerWeek(trainingDate,
							trainType,
							trainIntensity,
							trainStaminaPart,
							trainingAssistantLevel,
							coachLevel,
							DBDataSource.HRF);
					ret.add( tpw);
				}
			}
			return ret;
		} catch (Exception e) {
			HOLogger.instance().error(this.getClass(), "Error while performing loadTrainingPerWeek():  " + e);
		}
		return ret;
	}
}
