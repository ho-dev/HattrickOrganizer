package core.db;

import core.model.FactorObject;
import core.model.FormulaFactors;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Types;


public final class FaktorenTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "FAKTOREN";
	
	protected FaktorenTable(JDBCAdapter  adapter){
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[8];
		columns[0]= new ColumnDescriptor("HOPosition",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("Torwart",Types.REAL,false);
		columns[2]= new ColumnDescriptor("Verteidigung",Types.REAL,false);
		columns[3]= new ColumnDescriptor("Fluegel",Types.REAL,false);
		columns[4]= new ColumnDescriptor("Passpiel",Types.REAL,false);
		columns[5]= new ColumnDescriptor("Standards",Types.REAL,false);
		columns[6]= new ColumnDescriptor("Torschuss",Types.REAL,false);
		columns[7]= new ColumnDescriptor("Spielaufbau",Types.REAL,false);
		
		
	}

	protected void setFaktorenFromDB(FactorObject fo) {
		if (fo != null) {
			String statement = null;
			final String[] awhereS = { "HOPosition" };
			final String[] awhereV = { "" + fo.getPosition()};

			//erst Vorhandene Aufstellung löschen
			delete( awhereS, awhereV );

			//insert vorbereiten
			statement = "INSERT INTO "+getTableName()+" ( HOPosition, Torwart, Verteidigung, Fluegel, Passpiel, Standards, Torschuss, Spielaufbau ) VALUES(";
			statement
				+= (""
					+ fo.getPosition()
					+ ","
					+ fo.getTorwart()
					+ ","
					+ fo.getVerteidigung()
					+ ","
					+ fo.getFluegelspiel()
					+ ","
					+ fo.getPasspiel()
					+ ","
					+ fo.getStandards()
					+ ","
					+ fo.getTorschuss()
					+ ","
					+ fo.getSpielaufbau()
					+ " )");
			adapter.executeUpdate(statement);
		}
	}
	
	///////////////////Faktoren holen//////////////////////////
	void getFaktorenFromDB() {
		final FormulaFactors factors = FormulaFactors.instance();
		final ResultSet rs = adapter.executeQuery("SELECT * FROM " + getTableName() + "");

		try {
			if (rs != null) {
				rs.beforeFirst();
				while (rs.next()) {
					factors.setPositionFactor(rs.getByte("HOPosition"), new FactorObject(rs));
				}
			} else {
				// use hardcoded values
				FormulaFactors.instance().importDefaults();
			}
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getFaktoren: " + e);
			FormulaFactors.instance().importDefaults(); // use hardcoded values
		}
	}
}
