package core.db;

import core.training.TrainingPerWeek;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Types;
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

	// TODO: repair this function to follow new TrainingPerWeek structure
	List<TrainingPerWeek> getTrainingList() {
		final List<TrainingPerWeek> vTrainings = new ArrayList<>();

		final String statement = "SELECT * FROM " + getTableName() + " ORDER BY year, week ASC";

		final ResultSet rs = adapter.executeQuery(statement);

		try {
			if (rs != null) {
				rs.beforeFirst();

				while (rs.next()) {
					vTrainings.add(new TrainingPerWeek(rs.getInt("week"), rs.getInt("year"), rs.getInt("Typ"), rs.getInt("Intensity"), rs.getInt("StaminaTrainingPart")));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getTraining " + e);
		}

		return vTrainings;
	}

	
}
