package core.db;

import core.util.HOLogger;
import tool.arenasizer.Stadium;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


final class StadionTable extends AbstractTable {
	final static String TABLENAME = "STADION";

	StadionTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[14];
		columns[0] = new ColumnDescriptor("HRF_ID", Types.INTEGER, false, true); // is Primary Key
		columns[1] = new ColumnDescriptor("StadionName", Types.VARCHAR, false, 127);
		columns[2] = new ColumnDescriptor("GesamtGr", Types.INTEGER, false);
		columns[3] = new ColumnDescriptor("AnzSteh", Types.INTEGER, false);
		columns[4] = new ColumnDescriptor("AnzSitz", Types.INTEGER, false);
		columns[5] = new ColumnDescriptor("AnzDach", Types.INTEGER, false);
		columns[6] = new ColumnDescriptor("AnzLogen", Types.INTEGER, false);
		columns[7] = new ColumnDescriptor("AusbauSteh", Types.INTEGER, false);
		columns[8] = new ColumnDescriptor("AusbauSitz", Types.INTEGER, false);
		columns[9] = new ColumnDescriptor("AusbauDach", Types.INTEGER, false);
		columns[10] = new ColumnDescriptor("AusbauLogen", Types.INTEGER, false);
		columns[11] = new ColumnDescriptor("Ausbau", Types.INTEGER, false);
		columns[12] = new ColumnDescriptor("AusbauKosten", Types.INTEGER, false);
		columns[13] = new ColumnDescriptor("ArenaID", Types.INTEGER, false);
	}

	/**
	 * save Arena
	 *
	 * @param hrfId   foreign key of status info
	 * @param stadion stadio indo to store
	 */
	void saveStadion(int hrfId, Stadium stadion) {
		if (stadion != null) {
			// delete existing record
			executePreparedDelete(hrfId);
			executePreparedInsert(
					hrfId,

					stadion.getStadienname(),
					stadion.getGesamtgroesse(),
					stadion.getStehplaetze(),
					stadion.getSitzplaetze(),
					stadion.getUeberdachteSitzplaetze(),
					stadion.getLogen(),
					stadion.getAusbauStehplaetze(),
					stadion.getAusbauSitzplaetze(),
					stadion.getAusbauUeberdachteSitzplaetze(),
					stadion.getAusbauLogen(),
					stadion.getAusbau(),
					stadion.getAusbauKosten(),
					stadion.getArenaId()
			);
		}
	}

	/**
	 * lädt die Finanzen zum angegeben HRF file ein
	 */
	Stadium getStadion(int hrfID) {
		Stadium stadion = null;
		if (hrfID > -1) {
			var rs = getSelectByHrfID(hrfID);
			if (rs != null) {
				try {
					if (rs.next()) {
						stadion = createStadionObject(rs);
					}
				} catch (Exception e) {
					HOLogger.instance().log(getClass(), "DatenbankZugriff.getStadion: " + e);
				}
			}
		}
		return stadion;
	}

	private Stadium createStadionObject(ResultSet rs) throws SQLException {
		Stadium arena = new Stadium();
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