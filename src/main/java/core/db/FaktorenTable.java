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
		columns = new ColumnDescriptor[9];
		columns[0]= new ColumnDescriptor("PositionID",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("GKfactor",Types.REAL,false);
		columns[2]= new ColumnDescriptor("DEfactor",Types.REAL,false);
		columns[3]= new ColumnDescriptor("WIfactor",Types.REAL,false);
		columns[4]= new ColumnDescriptor("PSfactor",Types.REAL,false);
		columns[5]= new ColumnDescriptor("SPfactor",Types.REAL,false);
		columns[6]= new ColumnDescriptor("SCfactor",Types.REAL,false);
		columns[7]= new ColumnDescriptor("PMfactor",Types.REAL,false);
		columns[8]= new ColumnDescriptor("NormalisationFactor",Types.REAL,false);
	}

	protected void pushFactorsIntoDB(FactorObject fo) {
		if (fo != null) {
			String statement = null;
			final String[] awhereS = {"PositionID"};
			final String[] awhereV = {"" + fo.getPosition()};
			//delete  the existing entry
			delete(awhereS, awhereV);
			statement = createInsertStatement();
			adapter.executePreparedUpdate(statement,
					fo.getPosition(),
					fo.getGKfactor(),
					fo.getDEfactor(),
					fo.getWIfactor(),
					fo.getPSfactor(),
					fo.getSPfactor(),
					fo.getSCfactor(),
					fo.getPMfactor(),
					fo.getNormalizationFactor()
			);
		}
	}

	void getFaktorenFromDB() {
		final FormulaFactors factors = FormulaFactors.instance();
		final ResultSet rs = adapter.executePreparedQuery("SELECT * FROM " + getTableName() + "");

		try {
			if (rs != null) {
				rs.beforeFirst();
				while (rs.next()) {
					factors.setPositionFactor(rs.getByte("PositionID"), new FactorObject(rs));
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
