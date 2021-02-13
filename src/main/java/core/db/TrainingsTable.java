package core.db;

import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.util.DateTimeInfo;
import core.util.DateTimeUtils;
import core.util.HOLogger;
import module.transfer.PlayerTransfer;

import java.sql.ResultSet;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * This table is different than others because it does not hold data from XML/HRFs but is a mixed of computed data and data entered
 * directly by Users. Hence, there is a method recalculateEntries() that will force refresh of entries.
 * This method will be called automatically after table creation and during upgrade to v5.0
 * TODO: decide whehter or not to expose that method to users
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
	void saveTraining(TrainingPerWeek training, boolean force) {

		if (training != null) {

			DateTimeInfo trainingDateAsDTI = new DateTimeInfo(training.getTrainingDate());
			String trainingDate = DateTimeUtils.InstantToSQLtimeStamp(training.getTrainingDate());

			if (trainingDateAsDTI.isInTheFuture()){
				HOLogger.instance().debug(this.getClass(), trainingDate + " in the future   =>    SKIPPED");
				return;
			}


			if(isTrainingDateInDB(trainingDate)){

				if (force){
					delete(new String[]{"TRAINING_DATE"}, new String[]{trainingDate});
					HOLogger.instance().debug(this.getClass(), trainingDate + " already in TRAININGS   =>    DELETED");
				}
				else{
					HOLogger.instance().debug(this.getClass(), trainingDate + " already in TRAININGS   =>    SKIPPED");
					return;
				}
			}

			String statement = "INSERT INTO " + getTableName() + " (TRAINING_DATE, TRAINING_TYPE, TRAINING_INTENSITY, STAMINA_SHARE, COACH_LEVEL, TRAINING_ASSISTANTS_LEVEL, SOURCE) VALUES (";
			statement += trainingDate + ", ";
			statement += training.getTrainingType() + ", ";
			statement += training.getTrainingIntensity() + ", ";
			statement += training.getStaminaPart() + ", ";
			statement += training.getCoachLevel() + ", ";
			statement += training.getTrainingAssistantsLevel() + ", ";
			statement += training.getSource().getValue() + ")";

			try {
				adapter.executeUpdate(statement);
				HOLogger.instance().debug(this.getClass(), trainingDate + "  =>    INSERTED");
			}

			catch (Exception e) {
			HOLogger.instance().error(this.getClass(), "Error when executing TrainingsTable.saveTraining(): " +e);
			}
		}
	}


	/**
	 * apply the function saveTraining() to all elements of the provided vector
	 */
	void saveTrainings(List<TrainingPerWeek> trainings, boolean force) {
		for (var training:trainings){
			saveTraining(training, force);
		}
	}

	private boolean isTrainingDateInDB(String trainingDate){
		String sql = String.format("SELECT 1 FROM XTRADATA WHERE TRAININGDATE = '%s' LIMIT 1", trainingDate);
		ResultSet rs = adapter.executeQuery(sql);
		if (rs == null) {
			return false;
		}
		else{
			return true;
		}
	}


	List<TrainingPerWeek> getTrainingList() {
		final List<TrainingPerWeek> vTrainings = new ArrayList<>();

		final String statement = "SELECT * FROM " + getTableName() + " ORDER BY year, week ASC";

		try {
			final ResultSet rs = adapter.executeQuery(statement);
			TrainingPerWeek tpw;
			Instant trainingDate;
			Integer training_type, training_intensity, staminaShare, trainingAssistantsLevel, coachLevel;
			DBDataSource source;

			if (rs != null) {
				rs.beforeFirst();

				while (rs.next()) {
					trainingDate = rs.getTimestamp("TRAINING_DATE").toInstant();
					training_type = rs.getInt("TRAINING_TYPE");
					training_intensity = rs.getInt("TRAINING_INTENSITY");
					staminaShare = rs.getInt("STAMINA_SHARE");
					trainingAssistantsLevel = rs.getInt("TRAINING_ASSISTANTS_LEVEL");
					coachLevel = rs.getInt("COACH_LEVEL");
					source = DBDataSource.getCode(rs.getInt("SOURCE"));

					tpw = new TrainingPerWeek(trainingDate, training_type, training_intensity, staminaShare, trainingAssistantsLevel,
							coachLevel, false, false, source);

					vTrainings.add(tpw);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getTraining " + e);
		}

		return vTrainings;
	}
	
}
