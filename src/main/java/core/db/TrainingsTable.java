package core.db;

import core.model.enums.DBDataSource;
import core.training.TrainingPerWeek;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


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

	// TODO: repair this function and delete error message when this is done
	void saveTraining(TrainingPerWeek training) {
//		if (training != null) {
//			final String[] awhereS = { "Week", "Year" };
//			final String[] awhereV = { "" + training.getWeek(), "" + training.getYear()};
//
//			delete( awhereS, awhereV );
//
//			String statement = "INSERT INTO "+getTableName()+" ( Week, Year, Typ, Intensity, StaminaTrainingPart ) VALUES ( ";
//			statement += (training.getWeek() + ", " + training.getYear() + ", " + training.getTrainingType() + ", " + training.getTrainingIntensity() + ", " + training.getStaminaPart() + " )");
//
//			adapter.executeUpdate(statement);
//		}
		HOLogger.instance().error(this.getClass(), "TrainingsTable.saveTraining() is currently broken");
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
