package core.db;

import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.util.DateTimeUtils;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.PreparedStatement;
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

	private PreparedStatement getFutureTrainingsVectorStatement;
	private PreparedStatement getGetFutureTrainingsVectorStatement(){
		if (getFutureTrainingsVectorStatement==null ){
			getFutureTrainingsVectorStatement = adapter.createPreparedStatement("select * from " + getTableName() + " ORDER BY TRAINING_DATE");
		}
		return getFutureTrainingsVectorStatement;
	}
	List<TrainingPerWeek> getFutureTrainingsVector() {
		var vTrainings = new ArrayList<TrainingPerWeek>();
		ResultSet rs = adapter.executePreparedQuery(getGetFutureTrainingsVectorStatement());
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

	private PreparedStatement loadFutureTrainingsStatement;
	private PreparedStatement getLoadFutureTrainingsStatement(){
		if(loadFutureTrainingsStatement==null){
			loadFutureTrainingsStatement=adapter.createPreparedStatement("select TRAINING_TYPE from " + getTableName() + " where TRAINING_DATE=?");
		}
		return loadFutureTrainingsStatement;
	}
	int loadFutureTrainings(Timestamp trainingDate) {
		ResultSet rs = adapter.executePreparedQuery(getLoadFutureTrainingsStatement(), trainingDate);
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

	private PreparedStatement updateFutureTrainingStatement;
	private PreparedStatement getUpdateFutureTrainingStatement(){
		if (updateFutureTrainingStatement==null){
			updateFutureTrainingStatement=adapter.createPreparedStatement( "update " + getTableName() +
					" set TRAINING_TYPE= ?, TRAINING_INTENSITY=?, STAMINA_SHARE=?, COACH_LEVEL=?, " +
					"TRAINING_ASSISTANTS_LEVEL=?, SOURCE=? WHERE TRAINING_DATE=?");
		}
		return updateFutureTrainingStatement;
	}
	void storeFutureTraining(TrainingPerWeek training) {
		if (training != null) {

			var trainingDate = training.getTrainingDate().toDbTimestamp();
			int count = adapter.executePreparedUpdate(getUpdateFutureTrainingStatement(),
					training.getTrainingType(),
					training.getTrainingIntensity(),
					training.getStaminaShare(),
					training.getCoachLevel(),
					training.getTrainingAssistantsLevel(),
					training.getSourceAsInt(),
					trainingDate
			);

			if (count == 0) {
				adapter.executePreparedUpdate(getInsertStatement(),
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

	private PreparedStatement clearFutureTrainingsTableStatement;
	private PreparedStatement getClearFutureTrainingsTableStatement(){
		if ( clearFutureTrainingsTableStatement==null){
			clearFutureTrainingsTableStatement=adapter.createPreparedStatement("DELETE FROM " + getTableName()+ " WHERE TRUE");
		}
		return clearFutureTrainingsTableStatement;
	}
	void clearFutureTrainingsTable(){
		adapter.executePreparedUpdate(getClearFutureTrainingsTableStatement());
		HOLogger.instance().debug(getClass(), "FutureTraining table has been cleared !");
	}
}
