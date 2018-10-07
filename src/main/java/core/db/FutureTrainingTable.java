package core.db;

import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.training.TrainingPerWeek;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
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
					train.setHattrickWeek(rs.getInt("WEEK"));
					train.setHattrickSeason(rs.getInt("SEASON"));
					train.setStaminaPart(rs.getInt("STAMINATRAININGPART"));
					vTrainings.add(train);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getTraining " + e);
		}
		
		List<TrainingPerWeek> futures = new ArrayList<TrainingPerWeek>();

		int actualSeason = HOVerwaltung.instance().getModel().getBasics().getSeason();
		int actualWeek = HOVerwaltung.instance().getModel().getBasics().getSpieltag();

		// We are in the middle where season has not been updated!
		try {
			if (HOVerwaltung.instance().getModel().getXtraDaten().getTrainingDate().after(HOVerwaltung.instance().getModel()
																				 .getXtraDaten()
																				 .getSeriesMatchDate())) {
			actualWeek++;

				if (actualWeek == 17) {
					actualWeek = 1;
					actualSeason++;
				}
			}
		} catch (Exception e1) {
			// Null when first time HO is launched		
		}

		for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
			
			// calculate the week and season of the future training
			int week = (actualWeek + i) - 1;
			int season = actualSeason + (week / 16);
			week = (week % 16) + 1;
	
			// load the training from DB
			TrainingPerWeek train = null;

			for (Iterator<TrainingPerWeek> iter = vTrainings.iterator(); iter.hasNext();) {
				TrainingPerWeek tmp = (TrainingPerWeek) iter.next();
				if ((tmp.getHattrickWeek() == week) && (tmp.getHattrickSeason() == season)) {
					train = tmp;
					break;
				}
			}

			// if not found create it and saves it
			if (train == null) {
				train = new TrainingPerWeek();
				train.setHattrickWeek(week);
				train.setHattrickSeason(season);
				train.setTrainingIntensity(-1);
				train.setStaminaPart(-1);
				train.setTrainingType(-1);
				saveFutureTraining(train);				
			}
			futures.add(train);
		}		

		return futures;
	}
	
	void saveFutureTraining(TrainingPerWeek training) {
		if (training != null) {
			String statement =
				"update "+getTableName()+" set TYPE= " + training.getTrainingType() + ", INTENSITY=" + training.getTrainingIntensity() + ", STAMINATRAININGPART=" + training.getStaminaPart() + " WHERE WEEK=" + training.getHattrickWeek() + " AND SEASON=" + training.getHattrickSeason();
			int count = adapter.executeUpdate(statement);

			if (count == 0) {
				adapter.executeUpdate("insert into "+getTableName()+" (TYPE, INTENSITY, WEEK, SEASON, STAMINATRAININGPART) values (" //$NON-NLS-1$
				+training.getTrainingType() + ", " + training.getTrainingIntensity() + ", " + training.getHattrickWeek() + ", " + training.getHattrickSeason() + "," + training.getStaminaPart() + ")");
			}

		}
	}

}
