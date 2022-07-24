package core.db;

import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.util.DateTimeUtils;
import core.util.HODateTime;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
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
				new ColumnDescriptor("TRAINING_ASSISTANTS_LEVEL", Types.INTEGER, false),
				new ColumnDescriptor("SOURCE", Types.INTEGER, false)
		};
	}

	List<TrainingPerWeek> getFutureTrainingsVector() {
		var vTrainings = new ArrayList<TrainingPerWeek>();
		String query = "select * from " + getTableName() + " ORDER BY TRAINING_DATE";
		ResultSet rs = adapter.executePreparedQuery(query);

		try {
			if (rs != null) {
				rs.beforeFirst();

				while (rs.next()) {
					var trainingDate = rs.getTimestamp("TRAINING_DATE");
					var training_type = rs.getInt("TRAINING_TYPE");
					var training_intensity = rs.getInt("TRAINING_INTENSITY");
					var staminaShare = rs.getInt("STAMINA_SHARE");
					var trainingAssistantsLevel = rs.getInt("TRAINING_ASSISTANTS_LEVEL");
					var coachLevel = rs.getInt("COACH_LEVEL");
					var source = DBDataSource.getCode(rs.getInt("SOURCE"));

					var tpw = new TrainingPerWeek(HODateTime.fromDbTimestamp(trainingDate), training_type, training_intensity, staminaShare, trainingAssistantsLevel,
							coachLevel, source);

					vTrainings.add(tpw);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "Error when calling getFutureTrainingsVector():  " + e);
		}
		return vTrainings;
	}

	int loadFutureTrainings(Timestamp trainingDate) {
		String query = "select TRAINING_TYPE from " + getTableName() + " where TRAINING_DATE=?";
		ResultSet rs = adapter.executePreparedQuery(query, trainingDate);
		try {
			if (rs != null) {
				rs.beforeFirst();
				if (rs.next()) {
					return (rs.getInt("TYPE"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "loadFutureTrainings " + e);
		}
		return -1;
	}

	void storeFutureTraining(TrainingPerWeek training) {
		if (training != null) {

			String trainingDate = training.getTrainingDate().toDbTimestamp().toString();

			String statement = "update " + getTableName() +
					" set TRAINING_TYPE= ?, TRAINING_INTENSITY=?, STAMINA_SHARE=?, COACH_LEVEL=?, TRAINING_ASSISTANTS_LEVEL=?, SOURCE=? WHERE TRAINING_DATE=?";

			int count = adapter.executePreparedUpdate(statement,
					training.getTrainingType(),
					training.getTrainingIntensity(),
					training.getStaminaShare(),
					training.getCoachLevel(),
					training.getTrainingAssistantsLevel(),
					training.getSourceAsInt(),
					trainingDate
			);

			if (count == 0) {
				statement = createInsertStatement();
				adapter.executePreparedUpdate(statement,
						trainingDate,
						training.getTrainingIntensity(),
						training.getStaminaShare(),
						training.getCoachLevel(),
						training.getTrainingAssistantsLevel(),
						training.getSource()
				);
			}
		}
	}
	void storeFutureTrainings(List<TrainingPerWeek> trainings){
		for (TrainingPerWeek futureTraining: trainings){
			storeFutureTraining(futureTraining);
		}
	}

	void clearFutureTrainingsTable(){
		String sql = "DELETE FROM " + getTableName() + " WHERE TRUE";
		adapter.executePreparedUpdate(sql);
		HOLogger.instance().debug(getClass(), "FutureTraining table has been cleared !");
	}
}
