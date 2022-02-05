package core.db;

import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.util.HTDatetime;
import core.util.DateTimeUtils;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


/**
 * This table is different from others because it does not hold data from XML/HRFs but is a mixed of computed data and data entered
 * directly by Users. Hence, there is a method recalculateEntries() that will force refresh of entries.
 * This method will be called automatically after table creation and during upgrade to v5.0
 */
final class TrainingsTable extends AbstractTable {
	final static String TABLENAME = "TRAINING";
	
	protected TrainingsTable(JDBCAdapter  adapter){
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

	/**
	 * save provided training in database (trainings still in the future will be skipped)
	 * @param training training to be saved
	 * @param force if true will replace the training if it exists, otherwise will do nothing
	 */
	void saveTraining(TrainingPerWeek training, Instant lastTrainingDate, boolean force) {

		if (training != null) {

			HTDatetime trainingDateAsDTI = new HTDatetime(training.getTrainingDate());
			String trainingDate = DateTimeUtils.InstantToSQLtimeStamp(training.getTrainingDate());

			if (trainingDateAsDTI.isAfter(lastTrainingDate)) {
//				HOLogger.instance().debug(this.getClass(), trainingDate + " in the future   =>    SKIPPED");
				return;
			}

			String sql;
			if(isTrainingDateInDB(trainingDate)){

				if (force){
					sql = String.format("""
     				UPDATE TRAINING  SET TRAINING_TYPE=%s, TRAINING_INTENSITY=%s, STAMINA_SHARE=%s, COACH_LEVEL=%s, 
     				TRAINING_ASSISTANTS_LEVEL=%s, SOURCE=%s WHERE TRAINING_DATE = '%s'""", training.getTrainingType(), training.getTrainingIntensity(),
							training.getStaminaShare(), training.getCoachLevel(), training.getTrainingAssistantsLevel(), training.getSource().getValue(), trainingDate);
//					HOLogger.instance().debug(this.getClass(), trainingDate + " already in TRAININGS   =>    UPDATED");
				}
				else{
//					HOLogger.instance().debug(this.getClass(), trainingDate + " already in TRAININGS   =>    SKIPPED");
					return;
				}
			}
			else{
				sql = "INSERT INTO " + getTableName() + " (TRAINING_DATE, TRAINING_TYPE, TRAINING_INTENSITY, STAMINA_SHARE, COACH_LEVEL, TRAINING_ASSISTANTS_LEVEL, SOURCE) VALUES ('";
				sql += trainingDate + "', ";
				sql += training.getTrainingType() + ", ";
				sql += training.getTrainingIntensity() + ", ";
				sql += training.getStaminaShare() + ", ";
				sql += training.getCoachLevel() + ", ";
				sql += training.getTrainingAssistantsLevel() + ", ";
				sql += training.getSource().getValue() + ")";
				//HOLogger.instance().debug(this.getClass(), trainingDate + "  =>    INSERTED");
			}

			try {
				adapter.executeUpdate(sql);
			}

			catch (Exception e) {
				HOLogger.instance().error(this.getClass(), "Error when executing TrainingsTable.saveTraining(): " +e);
			}
		}
	}


	/**
	 * apply the function saveTraining() to all elements of the provided vector
	 */
	void saveTrainings(List<TrainingPerWeek> trainings, Instant lastTrainingDate, boolean force) {
		for (var training:trainings) {
			saveTraining(training, lastTrainingDate, force);
		}
	}

	private TrainingPerWeek getTrainingPerWeek(ResultSet rs) throws SQLException {
		Instant trainingDate = rs.getTimestamp("TRAINING_DATE").toInstant();
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

	private boolean isTrainingDateInDB(String trainingDate){
		String sql = String.format("SELECT 1 FROM " + getTableName() + " WHERE TRAINING_DATE = '%s' LIMIT 1", trainingDate);

		try {
			final ResultSet rs = adapter.executeQuery(sql);
			if (rs != null) {
				if (rs.next()) {
					return true;
				}
			}
		}
		catch (SQLException e) {
			HOLogger.instance().error(this.getClass(), "Error when controlling if following entry was in Training table: " + trainingDate + ": " + e.getMessage());
			return false;
		}
		return false;
	}

	// FIXME This method and the next one can be collapsed into one.
	List<TrainingPerWeek> getTrainingList() {
		final List<TrainingPerWeek> vTrainings = new ArrayList<>();

		final String statement = "SELECT * FROM " + getTableName() + " ORDER BY TRAINING_DATE ASC";

		try {
			final ResultSet rs = adapter.executeQuery(statement);

			if (rs != null) {
				rs.beforeFirst();
				while (rs.next()) {
					vTrainings.add(getTrainingPerWeek(rs));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().error(getClass(),"TrainingsTable.getTrainingList " + e.getMessage());
		}

		return vTrainings;
	}


	public List<TrainingPerWeek> getTrainingList(Timestamp fromDate, Timestamp toDate) {

		final List<TrainingPerWeek> vTrainings = new ArrayList<>();

		var statement = new StringBuilder ( "SELECT * FROM " )
				.append( getTableName() )
				.append(" WHERE TRAINING_DATE < '")
				.append(""+toDate);

				if (fromDate!=null) {
					statement.append("' AND TRAINING_DATE >= '")
							.append(""+fromDate);
				}
				statement.append("' ORDER BY TRAINING_DATE ASC");

		try {
			final ResultSet rs = adapter.executeQuery(statement.toString());

			if (rs != null) {
				rs.beforeFirst();
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
