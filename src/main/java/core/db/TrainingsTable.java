package core.db;

import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.util.HODateTime;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This table is different from others because it does not hold data from XML/HRFs but is a mixed of computed data and data entered
 * directly by Users. Hence, there is a method recalculateEntries() that will force refresh of entries.
 * This method will be called automatically after table creation and during upgrade to v5.0
 */
final class TrainingsTable extends AbstractTable {
	final static String TABLENAME = "TRAINING";
	
	TrainingsTable(JDBCAdapter adapter){
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_DATE").setGetter((p) -> ((TrainingPerWeek) p).getTrainingDate().toDbTimestamp()).setSetter((p, v) -> ((TrainingPerWeek) p).setTrainingDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_TYPE").setGetter((p) -> ((TrainingPerWeek) p).getTrainingType()).setSetter((p, v) -> ((TrainingPerWeek) p).setTrainingType((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TRAINING_INTENSITY").setGetter((p) -> ((TrainingPerWeek) p).getTrainingIntensity()).setSetter((p, v) -> ((TrainingPerWeek) p).setTrainingIntensity((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("STAMINA_SHARE").setGetter((p) -> ((TrainingPerWeek) p).getStaminaShare()).setSetter((p, v) -> ((TrainingPerWeek) p).setStaminaShare((int) v)).setType(Types.INTEGER).isNullable(false).build(),
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

	private final HashMap<Integer, PreparedStatement> getTrainingListStatements = new HashMap<>();
	private PreparedStatement getTrainingListStatement(Timestamp from, Timestamp to, List<Object> values){
		PreparedStatement ret;
		if ( from == null && to==null){
			ret = getTrainingListStatements.get(0);
			if ( ret == null){
				ret = new PreparedSelectStatementBuilder(this, "ORDER  BY TRAINING_DATE DESC").getStatement();
				getTrainingListStatements.put(0,ret);
			}
		}
		else if ( from==null ) {
			ret = getTrainingListStatements.get(1);
			if ( ret == null){
				ret = new PreparedSelectStatementBuilder(this, "WHERE TRAINING_DATE<?").getStatement();
				getTrainingListStatements.put(1,ret);
			}
			values.add(to);
		}
		else if ( to==null ){
			ret = getTrainingListStatements.get(2);
			if ( ret == null) {
				ret = new PreparedSelectStatementBuilder(this, "WHERE TRAINING_DATE>=?").getStatement();
				getTrainingListStatements.put(2, ret);
			}
			values.add(from);
		}
		else {
			ret = getTrainingListStatements.get(3);
			if ( ret == null){
				ret = new PreparedSelectStatementBuilder(this, "WHERE TRAINING_DATE>=? AND TRAINING_DATE<?").getStatement();
			}
			values.add(from);
			values.add(to);
		}
		return ret;
	}
	List<TrainingPerWeek> getTrainingList() {
		return getTrainingList(null, null);
	}

	public List<TrainingPerWeek> getTrainingList(Timestamp fromDate, Timestamp toDate) {
		var values = new ArrayList<>();
		var statement = getTrainingListStatement(fromDate, toDate, values);
		return load(TrainingPerWeek.class, adapter.executePreparedQuery(statement, values.toArray()));
	}
}
