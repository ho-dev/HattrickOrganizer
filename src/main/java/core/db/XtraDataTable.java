package core.db;

import core.model.XtraData;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Types;


final class XtraDataTable extends AbstractTable {
	final static String TABLENAME = "XTRADATA";
	
	protected XtraDataTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}
	
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("HRF_ID", Types.INTEGER, false, true),
				new ColumnDescriptor("CurrencyRate", Types.REAL, false),
				new ColumnDescriptor("HasPromoted", Types.BOOLEAN, false),
				new ColumnDescriptor("LogoURL", Types.VARCHAR, false, 127),
				new ColumnDescriptor("SeriesMatchDate", Types.TIMESTAMP, false),
				new ColumnDescriptor("TrainingDate", Types.TIMESTAMP, false),
				new ColumnDescriptor("EconomyDate", Types.TIMESTAMP, false),
				new ColumnDescriptor("LeagueLevelUnitID", Types.INTEGER, false),
				new ColumnDescriptor("CountryId", Types.INTEGER, true)
		};
	}
	
	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX IXTRADATA_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")"};
	}	
	
	/**
	 * lädt die Basics zum angegeben HRF file ein
	 */
	XtraData getXtraDaten(int hrfID) {

		XtraData xtra = new XtraData();

		if (hrfID != -1) {

			String sql = "SELECT * FROM " + getTableName() + " WHERE HRF_ID = " + hrfID;
			ResultSet rs = adapter.executeQuery(sql);

			try {
				if (rs != null) {
					rs.first();
					xtra = new XtraData(rs);
				}
			} catch (Exception e) {
				HOLogger.instance().error(getClass(), "Error while loading XtraData model: " + e);
			}
		}
		return xtra;
	}

	/**
	 * speichert das Team
	 */
	void saveXtraDaten(int hrfId, XtraData xtra) {
		String statement;

		if (xtra != null) {
			int hasProm = 0;

			if (xtra.isHasPromoted()) {
				hasProm = 1;
			}

			//erst Vorhandene Aufstellung löschen
			deleteXtraDaten(hrfId);

			//insert vorbereiten
			statement = "INSERT INTO "+getTableName()+" ( HRF_ID , CurrencyRate, HasPromoted , LogoURL , SeriesMatchDate ,TrainingDate, EconomyDate, LeagueLevelUnitID, CountryId ) VALUES(";
			statement
				+= (""
					+ hrfId
					+ ","
					+ xtra.getCurrencyRate()
					+ ","
					+ hasProm
					+ ",'"
					+ core.db.DBManager.insertEscapeSequences(xtra.getLogoURL())
					+ "', '"
					+ xtra.getSeriesMatchDate()
					+ "', '"
					+ xtra.getNextTrainingDate()
					+ "', '"
					+ xtra.getEconomyDate()
					+ "', "
					+ xtra.getLeagueLevelUnitID()
					+ ", "
					+ xtra.getCountryId()
					+ " )");
			adapter.executeUpdate(statement);
		}
	}
	
	private void deleteXtraDaten(int hrfID) {
		final String[] where = { "HRF_ID" };
		final String[] value = { hrfID + "" };
		delete( where,value );
	}

}
