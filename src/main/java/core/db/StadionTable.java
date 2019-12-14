package core.db;

import core.util.HOLogger;
import tool.arenasizer.Arena;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


final class StadionTable extends AbstractTable {
	final static String TABLENAME = "STADION";
	
	protected StadionTable(JDBCAdapter  adapter){
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[14];
		columns[0]= new ColumnDescriptor("HRF_ID",Types.INTEGER,false,true); // is Primary Key
		columns[1]= new ColumnDescriptor("StadionName",Types.VARCHAR,false,127);
		columns[2]= new ColumnDescriptor("GesamtGr",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("AnzSteh",Types.INTEGER,false);
		columns[4]= new ColumnDescriptor("AnzSitz",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("AnzDach",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("AnzLogen",Types.INTEGER,false);
		columns[7]= new ColumnDescriptor("AusbauSteh",Types.INTEGER,false);
		columns[8]= new ColumnDescriptor("AusbauSitz",Types.INTEGER,false);
		columns[9]= new ColumnDescriptor("AusbauDach",Types.INTEGER,false);
		columns[10]= new ColumnDescriptor("AusbauLogen",Types.INTEGER,false);
		columns[11]= new ColumnDescriptor("Ausbau",Types.INTEGER,false);
		columns[12]= new ColumnDescriptor("AusbauKosten",Types.INTEGER,false);
		columns[13]= new ColumnDescriptor("ArenaID",Types.INTEGER,false);
	}

	/**
	 * save Arena
	 *
	 * @param hrfId 
	 * @param stadion 
	 */
	void saveStadion(int hrfId, Arena stadion) {
		StringBuilder statement = new StringBuilder(200);
		final String[] awhereS = { columns[0].getColumnName() };
		final String[] awhereV = { String.valueOf(hrfId) };

		if (stadion != null) {
			//erst Vorhandene Aufstellung löschen
			delete( awhereS, awhereV );
			//insert vorbereiten
			statement.append("INSERT INTO "+getTableName()+" ( HRF_ID, StadionName, GesamtGr, AnzSteh, AnzSitz , AnzDach , AnzLogen , AusbauSteh , AusbauSitz , AusbauDach , AusbauLogen , Ausbau ,  AusbauKosten , ArenaID ) VALUES(");
			statement.append(hrfId);
			statement.append(",'");
			statement.append(DBManager.insertEscapeSequences(stadion.getStadienname()));
			statement.append("',");
			statement.append(stadion.getGesamtgroesse());
			statement.append(",");
			statement.append(stadion.getStehplaetze());
			statement.append(",");
			statement.append(stadion.getSitzplaetze());
			statement.append(",");
			statement.append(stadion.getUeberdachteSitzplaetze());
			statement.append(",");
			statement.append(stadion.getLogen());
			statement.append(",");
			statement.append(stadion.getAusbauStehplaetze());
			statement.append(",");
			statement.append(stadion.getAusbauSitzplaetze());
			statement.append(",");
			statement.append(stadion.getAusbauUeberdachteSitzplaetze());
			statement.append(",");
			statement.append(stadion.getAusbauLogen());
			statement.append(",");
			statement.append(stadion.getAusbau());
			statement.append(",");
			statement.append(stadion.getAusbauKosten());
			statement.append(",");
			statement.append(stadion.getArenaId());
			statement.append(" )");
			adapter.executeUpdate(statement.toString());
		}
	}
	
	/**
	 * lädt die Finanzen zum angegeben HRF file ein
	 */
	Arena getStadion(int hrfID) {
		ResultSet rs = null;
		Arena stadion = null;
		String sql = null;

		sql = "SELECT * FROM "+getTableName()+" WHERE HRF_ID = " + hrfID;
		rs = adapter.executeQuery(sql);

		try {
			if (rs.next()) {
				stadion = createStadionObject(rs);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getStadion: " + e);
		}

		return stadion;
	}
	
	private Arena createStadionObject(ResultSet rs) throws SQLException {
		Arena arena = new Arena();
		arena.setStadienname(DBManager.deleteEscapeSequences(rs.getString("StadionName")));
		arena.setArenaId(rs.getInt("ArenaID"));
		arena.setSitzplaetze(rs.getInt("AnzSitz"));
		arena.setStehplaetze(rs.getInt("AnzSteh"));
		arena.setUeberdachteSitzplaetze(rs.getInt("AnzDach"));
        arena.setLogen(rs.getInt("AnzLogen"));
        arena.setAusbauStehplaetze(rs.getInt("AusbauSteh"));
        arena.setAusbauSitzplaetze(rs.getInt("AusbauSitz"));
        arena.setAusbauUeberdachteSitzplaetze(rs.getInt("AusbauDach"));
        arena.setAusbauLogen(rs.getInt("AusbauLogen"));
        arena.setAusbau(rs.getBoolean("Ausbau"));
        arena.setAusbauKosten(rs.getInt("AusbauKosten"));
        return arena;
	}
	
}
