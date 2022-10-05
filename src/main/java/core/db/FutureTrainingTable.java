package core.db;

import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.util.HODateTime;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

public final class FutureTrainingTable extends AbstractTable {

	/**
	 * tablename
	 **/
	public final static String TABLENAME = "FUTURETRAINING";

	FutureTrainingTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_DATE").setGetter((p)->((TrainingPerWeek)p).getTrainingDate().toDbTimestamp()).setSetter((p, v)->((TrainingPerWeek)p).setTrainingDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_TYPE").setGetter((p)->((TrainingPerWeek)p).getTrainingType()).setSetter((p, v)->((TrainingPerWeek)p).setTrainingType((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_INTENSITY").setGetter((p)->((TrainingPerWeek)p).getTrainingIntensity()).setSetter((p, v)->((TrainingPerWeek)p).setTrainingIntensity((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("STAMINA_SHARE").setGetter((p)->((TrainingPerWeek)p).getStaminaShare()).setSetter((p, v)->((TrainingPerWeek)p).setStaminaShare((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COACH_LEVEL").setGetter((p)->((TrainingPerWeek)p).getCoachLevel()).setSetter((p, v)->((TrainingPerWeek)p).setCoachLevel((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_ASSISTANTS_LEVEL").setGetter((p)->((TrainingPerWeek)p).getTrainingAssistantsLevel()).setSetter((p, v)->((TrainingPerWeek)p).setTrainingAssistantLevel((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SOURCE").setGetter((p)->((TrainingPerWeek)p).getSource().getValue()).setSetter((p, v)->((TrainingPerWeek)p).setSource(DBDataSource.getCode((int) v))).setType(Types.INTEGER).isNullable(false).build(),
		};
	}

	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
		return new PreparedSelectStatementBuilder(this," ORDER BY TRAINING_DATE");
	}
	List<TrainingPerWeek> getFutureTrainingsVector() {
		return load(TrainingPerWeek.class);
	}

	private final DBManager.PreparedStatementBuilder loadFutureTrainingsStatementBuilder = new DBManager.PreparedStatementBuilder(
			"select TRAINING_TYPE from " + getTableName() + " where TRAINING_DATE=?");
	int loadFutureTrainings(Timestamp trainingDate) {
		ResultSet rs = adapter.executePreparedQuery(loadFutureTrainingsStatementBuilder.getStatement(), trainingDate);
		try {
			if (rs != null) {
				if (rs.next()) {
					return (rs.getInt("TYPE"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "loadFutureTrainings " + e);
		}
		return -1;
	}


	@Override
	protected PreparedUpdateStatementBuilder createPreparedUpdateStatementBuilder() {
		return new PreparedUpdateStatementBuilder(this,
				" set TRAINING_TYPE= ?, TRAINING_INTENSITY=?, STAMINA_SHARE=?, COACH_LEVEL=?, " +
						"TRAINING_ASSISTANTS_LEVEL=?, SOURCE=? WHERE TRAINING_DATE=?");
	}

	void storeFutureTraining(TrainingPerWeek training) {
		store(training);
	}

	void storeFutureTrainings(List<TrainingPerWeek> trainings){
		for (TrainingPerWeek futureTraining: trainings){
			storeFutureTraining(futureTraining);
		}
	}

	@Override
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this, " WHERE TRUE");
	}
	void clearFutureTrainingsTable(){
		executePreparedDelete();
		HOLogger.instance().debug(getClass(), "FutureTraining table has been cleared !");
	}
}
