package core.db;

import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.util.HODateTime;
import core.util.HOLogger;
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
		columns = new ColumnDescriptor[7];
		columns[0]= new ColumnDescriptor("TRAINING_DATE", Types.TIMESTAMP,false);
		columns[1]= new ColumnDescriptor("TRAINING_TYPE", Types.INTEGER,false);
		columns[2]= new ColumnDescriptor("TRAINING_INTENSITY",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("STAMINA_SHARE",Types.INTEGER,false);
		columns[4]= new ColumnDescriptor("COACH_LEVEL",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("TRAINING_ASSISTANTS_LEVEL",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("SOURCE",Types.INTEGER,false);
	}

	@Override
	protected PreparedUpdateStatementBuilder createPreparedUpdateStatementBuilder(){
		return new PreparedUpdateStatementBuilder(this, "SET TRAINING_TYPE=?, TRAINING_INTENSITY=?" +
				", STAMINA_SHARE=?, COACH_LEVEL=?, TRAINING_ASSISTANTS_LEVEL=?, SOURCE=? WHERE TRAINING_DATE = ?" );
	}

	/**
	 * save provided training in database (trainings still in the future will be skipped)
	 * @param training training to be saved
	 * @param force if true will replace the training if it exists, otherwise will do nothing
	 */
	void saveTraining(TrainingPerWeek training, HODateTime lastTrainingDate, boolean force) {

		if (training != null) {

			var trainingDate = training.getTrainingDate();
			if (trainingDate.isAfter(lastTrainingDate)) {
				return;
			}
			try {
				if (isTrainingDateInDB(trainingDate)) {

					if (force) {
						executePreparedUpdate(
								training.getTrainingType(),
								training.getTrainingIntensity(),
								training.getStaminaShare(),
								training.getCoachLevel(),
								training.getTrainingAssistantsLevel(),
								training.getSource().getValue(),
								trainingDate.toDbTimestamp()
						);
//					HOLogger.instance().debug(this.getClass(), trainingDate + " already in TRAININGS   =>    UPDATED");
					} else {
//					HOLogger.instance().debug(this.getClass(), trainingDate + " already in TRAININGS   =>    SKIPPED");
					}
				} else {
					executePreparedInsert(
							trainingDate.toDbTimestamp(),
							training.getTrainingType(),
							training.getTrainingIntensity(),
							training.getStaminaShare(),
							training.getCoachLevel(),
							training.getTrainingAssistantsLevel(),
							training.getSource().getValue()
					);
					//HOLogger.instance().debug(this.getClass(), trainingDate + "  =>    INSERTED");
				}

			} catch (Exception e) {
				HOLogger.instance().error(this.getClass(), "Error when executing TrainingsTable.saveTraining(): " + e);
			}
		}
	}

	/**
	 * apply the function saveTraining() to all elements of the provided vector
	 */
	void saveTrainings(List<TrainingPerWeek> trainings, HODateTime lastTrainingDate, boolean force) {
		for (var training:trainings) {
			saveTraining(training, lastTrainingDate, force);
		}
	}

	private TrainingPerWeek getTrainingPerWeek(ResultSet rs) throws SQLException {
		var trainingDate = HODateTime.fromDbTimestamp(rs.getTimestamp("TRAINING_DATE"));
		int trainingType = rs.getInt("TRAINING_TYPE");
		int trainingIntensity = rs.getInt("TRAINING_INTENSITY");
		int staminaShare = rs.getInt("STAMINA_SHARE");
		int trainingAssistantsLevel = rs.getInt("TRAINING_ASSISTANTS_LEVEL");
		int coachLevel = rs.getInt("COACH_LEVEL");
		DBDataSource source = DBDataSource.getCode(rs.getInt("SOURCE"));

		return new TrainingPerWeek(trainingDate,
				trainingType,
				trainingIntensity,
				staminaShare,
				trainingAssistantsLevel,
				coachLevel,
				source);
	}

	private final PreparedSelectStatementBuilder isTrainingDateInDBBuilder = new PreparedSelectStatementBuilder(this, "WHERE TRAINING_DATE = ? LIMIT 1");
	private boolean isTrainingDateInDB(HODateTime trainingDate) {
		try {
			final ResultSet rs = adapter.executePreparedQuery(isTrainingDateInDBBuilder.getStatement(), trainingDate.toDbTimestamp());
			if (rs != null) {
				if (rs.next()) {
					return true;
				}
			}
		} catch (SQLException e) {
			HOLogger.instance().error(this.getClass(), "Error when controlling if following entry was in Training table: " + trainingDate.toDbTimestamp() + ": " + e.getMessage());
			return false;
		}
		return false;
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
		final List<TrainingPerWeek> vTrainings = new ArrayList<>();
		try {
			var values = new ArrayList<>();
			var statement = getTrainingListStatement(fromDate, toDate, values);
			final ResultSet rs = adapter.executePreparedQuery(statement, values.toArray());
			if (rs != null) {
				while (rs.next()) {
					vTrainings.add(getTrainingPerWeek(rs));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().error(getClass(),"TrainingsTable.getTrainingList " + e.getMessage());
		}
		return vTrainings;
	}
}
