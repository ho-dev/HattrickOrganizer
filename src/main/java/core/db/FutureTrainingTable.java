package core.db;

import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.training.HattrickDate;
import core.training.TrainingPerWeek;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public final class FutureTrainingTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "FUTURETRAINING";
	
	protected FutureTrainingTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[5];
		columns[0]= new ColumnDescriptor("TYPE",Types.INTEGER,false);
		columns[1]= new ColumnDescriptor("INTENSITY",Types.INTEGER,false);
		columns[2]= new ColumnDescriptor("WEEK",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("SEASON",Types.INTEGER,false);
		columns[4] = new ColumnDescriptor("STAMINATRAININGPART",Types.INTEGER,false);
	}
	
	List<TrainingPerWeek> getFutureTrainingsVector() {
		final Vector<TrainingPerWeek> vTrainings = new Vector<TrainingPerWeek>();
		String query = "select * from "+getTableName();
		ResultSet rs = adapter.executeQuery(query);

		try {
			if (rs != null) {
				rs.beforeFirst();

				while (rs.next()) {
					TrainingPerWeek train = new TrainingPerWeek();			
					train.setTrainingType(rs.getInt("TYPE")); 
					train.setTrainingIntensity(rs.getInt("INTENSITY")); 
					var week = rs.getInt("WEEK");
					var season=rs.getInt("SEASON");
					train.setHattrickDate(new HattrickDate(season, week));
					train.setStaminaPart(rs.getInt("STAMINATRAININGPART"));
					vTrainings.add(train);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getTraining " + e);
		}
		
		List<TrainingPerWeek> futures = new ArrayList<TrainingPerWeek>();

		int _actualSeason = HOVerwaltung.instance().getModel().getBasics().getSeason();
		int _actualWeek = HOVerwaltung.instance().getModel().getBasics().getSpieltag();

		var actualDate = new HattrickDate(_actualSeason, _actualWeek);

		// We are in the middle where season has not been updated!
		try {
			if (HOVerwaltung.instance().getModel().getXtraDaten().getTrainingDate().after(HOVerwaltung.instance().getModel()
																				 .getXtraDaten()
																				 .getSeriesMatchDate())) {
				actualDate.addWeeks(1);
			}
		} catch (Exception e1) {
			// Null when first time HO is launched		
		}

		for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
			// load the training from DB
			TrainingPerWeek train = null;

			for ( var t : vTrainings){
			//for (Iterator<TrainingPerWeek> iter = vTrainings.iterator(); iter.hasNext();) {
			//	TrainingPerWeek tmp = (TrainingPerWeek) iter.next();
				if (t.getHattrickDate().equals(actualDate)) {
					train = t;
					break;
				}
			}

			// if not found create it and saves it
			if (train == null) {
				train = new TrainingPerWeek();
				train.setHattrickDate(actualDate);
				train.setTrainingIntensity(-1);
				train.setStaminaPart(-1);
				train.setTrainingType(-1);
				saveFutureTraining(train);				
			}
			futures.add(train);
			actualDate.addWeeks(1);
		}		

		return futures;
	}
	
	int getFutureTrainings(int saison, int week) {
		String query = "select TYPE from "+getTableName()+" where SEASON="+saison+" and WEEK="+week;
		ResultSet rs = adapter.executeQuery(query);

		try {
			if (rs != null) {
				rs.beforeFirst();

				if (rs.next()) {
					return (rs.getInt("TYPE"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getTraining " + e);
		}
		return -1;
	}

	void saveFutureTraining(TrainingPerWeek training) {
		if (training != null) {
			String statement =
				"update "+getTableName()+" set TYPE= " + training.getTrainingType() + ", INTENSITY=" + training.getTrainingIntensity() + ", STAMINATRAININGPART=" + training.getStaminaPart() + " WHERE WEEK=" + training.getHattrickDate().getWeek() + " AND SEASON=" + training.getHattrickDate().getSeason();
			int count = adapter.executeUpdate(statement);

			if (count == 0) {
				adapter.executeUpdate("insert into "+getTableName()+" (TYPE, INTENSITY, WEEK, SEASON, STAMINATRAININGPART) values (" //$NON-NLS-1$
				+training.getTrainingType() + ", " + training.getTrainingIntensity() + ", " + training.getHattrickDate().getWeek() + ", " + training.getHattrickDate().getSeason() + "," + training.getStaminaPart() + ")");
			}

		}
	}

}
