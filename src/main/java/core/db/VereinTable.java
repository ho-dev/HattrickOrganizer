package core.db;

import core.model.misc.Verein;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Types;

final class VereinTable extends AbstractTable {
	final static String TABLENAME = "VEREIN";

	protected VereinTable(JDBCAdapter  adapter){
		super( TABLENAME, adapter );
	}

	@Override
	protected void initColumns() {
		columns 	= new ColumnDescriptor[14];
		columns[0]	= new ColumnDescriptor( "HRF_ID",		Types.INTEGER,false, true );
		columns[1]	= new ColumnDescriptor( "TWTrainer",	Types.INTEGER,false );
		columns[2]	= new ColumnDescriptor( "COTrainer",	Types.INTEGER,false );
		columns[3]	= new ColumnDescriptor( "Physiologen",	Types.INTEGER,false );
		columns[4]	= new ColumnDescriptor( "Pschyologen",	Types.INTEGER,false );
		columns[5]	= new ColumnDescriptor( "Finanzberater",Types.INTEGER,false );
		columns[6]	= new ColumnDescriptor( "PRManager",	Types.INTEGER,false );
		columns[7]	= new ColumnDescriptor( "Aerzte",		Types.INTEGER,false );
		columns[8]	= new ColumnDescriptor( "Jugend",		Types.INTEGER,false );
		columns[9]	= new ColumnDescriptor( "Siege",		Types.INTEGER,false );
		columns[10]	= new ColumnDescriptor( "Ungeschlagen",	Types.INTEGER,false );
		columns[11]	= new ColumnDescriptor( "Fans",			Types.INTEGER,false );
		columns[12]	= new ColumnDescriptor( "TacticAssist",	Types.INTEGER,false );
		columns[13]	= new ColumnDescriptor( "FormAssist",	Types.INTEGER,false );
	}

	@Override
	protected String[] getCreateIndizeStatements() {
		return new String[] {
			"CREATE INDEX IVEREIN_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")"};
	}
	/**
	 * speichert das Verein
	 */
	void saveVerein(int hrfId, Verein verein) {
		String statement = null;
		final String[] awhereS = { "HRF_ID" };
		final String[] awhereV = { "" + hrfId };

		if (verein != null) {
			//erst Vorhandene Aufstellung löschen
			delete( awhereS, awhereV );

			//insert vorbereiten
			statement = "INSERT INTO "+getTableName()+" ( TWTrainer , COTrainer , Physiologen , Pschyologen , Finanzberater , PRManager , Aerzte , Jugend , Siege , Ungeschlagen , Fans , TacticAssist , FormAssist, HRF_ID ) VALUES(";
			statement
				+= (""
					+ 0
					+ ","
					+ verein.getCoTrainer()
					+ ","
					+ verein.getMasseure()
					+ ","
					+ verein.getPsychologen()
					+ ","
					+ verein.getFinancialDirectorLevels()
					+ ","
					+ verein.getPRManager()
					+ ","
					+ verein.getAerzte()
					+ ","
					+ verein.getJugend()
					+ ","
					+ verein.getSiege()
					+ ","
					+ verein.getUngeschlagen()
					+ ","
					+ verein.getFans()
					+ ","
					+ verein.getTacticalAssistantLevels()
					+ ","
					+ verein.getFormCoachLevels()
					+ ","
					+ hrfId
					+ " )");
			adapter.executeUpdate(statement);
		}
	}

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 */
	Verein getVerein(int hrfID) {
		ResultSet rs = null;
		Verein verein = null;
		String sql = null;

		sql = "SELECT * FROM "+getTableName()+" WHERE HRF_ID = " + hrfID;
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				rs.first();
				verein = new Verein(rs);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getTeam: " + e);
		}

		return verein;
	}

}
