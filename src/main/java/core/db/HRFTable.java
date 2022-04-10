package core.db;

import core.file.hrf.HRF;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public final class HRFTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "HRF";

	private HRF maxHrf = new HRF();
	private HRF latestHrf = new HRF();

	protected HRFTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[3];
		columns[0] = new ColumnDescriptor("HRF_ID", Types.INTEGER, false, true);
		columns[1] = new ColumnDescriptor("Name", Types.VARCHAR, false, 256);
		columns[2] = new ColumnDescriptor("Datum", Types.TIMESTAMP, false);
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] { "CREATE INDEX iHRF_1 ON " + getTableName() + "("
				+ columns[2].getColumnName() + ")" };
	}

	HRF getLatestHrf() {
		if (latestHrf.getHrfId() == -1) {
			latestHrf = loadLatestDownloadedHrf();
		}
		return latestHrf;
	}

	HRF getMaxHrf() {
		if (maxHrf.getHrfId() == -1) {
			maxHrf = loadMaxHrf();
		}
		return maxHrf;
	}

	/**
	 * load the id of latest downloaded hrf file (maximum fetch date)
	 * (there maybe hrfs with greater ids imported later, if user reimported old hrf files)
	 */
	private HRF loadLatestDownloadedHrf() {
		ResultSet rs;

		rs = adapter.executeQuery("SELECT HRF_ID FROM " + getTableName() + " Order By Datum DESC");

		try {
			if ((rs != null) && rs.first()) {
//				HOLogger.instance().log(getClass(), "Max( HRF_ID )" + rs.getInt(1));
				return getHRF(rs.getInt(1));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DBZugriff.loadLatestHrf: " + e);
		}

		return new HRF();
	}

	/**
	 * liefert die Maximal Vergebene Id eines HRF-Files
	 */
	private HRF loadMaxHrf() {
		ResultSet rs = adapter.executeQuery("SELECT Max( HRF_ID ) FROM " + getTableName() + "");
		try {
			if ((rs != null) && rs.first()) {
				return getHRF(rs.getInt(1));
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DBZugriff.loadMaxHrf: " + e);
		}
		return new HRF();
	}

	/**
	 * speichert das Verein
	 */
	void saveHRF(int hrfId, String name, HODateTime datum) {
		String statement;

		// insert vorbereiten
		statement = "INSERT INTO " + getTableName() + " ( HRF_ID, Name, Datum ) VALUES(";
		statement += ("" + hrfId + ",'" + name + "','" + datum.toDbTimestamp() + "' )");
		adapter.executeUpdate(statement);

		if (hrfId > getMaxHrf().getHrfId()) {
			maxHrf = new HRF(hrfId, name, datum);
		}

		if (datum.isAfter(getLatestHrf().getDatum())) {
			latestHrf = new HRF(hrfId, name, datum);
		}
	}

	/**
	 * Gibt die HRFId vor dem Datum zurück, wenn möglich
	 */
	int getHrfId4Date(Timestamp time) {
		ResultSet rs;
		String sql;
		int hrfID = 0;

		// Die passende HRF-ID besorgen
		sql = "SELECT HRF_ID FROM " + getTableName() + " WHERE Datum<='" + time.toString()
				+ "' ORDER BY Datum DESC";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				// HRF vorher vorhanden?
				if (rs.first()) {
					hrfID = rs.getInt("HRF_ID");
				}
				// sonst HRF nach dem Datum nehmen
				else {
					sql = "SELECT HRF_ID FROM " + getTableName() + " WHERE Datum>'"
							+ time + "' ORDER BY Datum";
					rs = adapter.executeQuery(sql);

					assert rs != null;
					if (rs.first()) {
						hrfID = rs.getInt("HRF_ID");
					}
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getHRFID4Time: " + e);
		}

		return hrfID;
	}

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 */
	HRF getHRF(int hrfID) {
		return loadHRF("HRF_ID = " +	hrfID);
	}

	/**
	 * Get a list of all HRFs
	 * 
	 * @param minId
	 *            minimum HRF id (<0 for all)
	 * @param maxId
	 *            maximum HRF id (<0 for all)
	 * @param asc
	 *            order ascending (descending otherwise)
	 * 
	 * @return all matching HRFs
	 */
	HRF[] getAllHRFs(int minId, int maxId, boolean asc) {
		Vector<HRF> liste = new Vector<>();
		ResultSet rs;
		String sql;
		sql = "SELECT * FROM " + getTableName();
		sql += " WHERE 1=1";
		if (minId >= 0)
			sql += " AND HRF_ID >=" + minId;
		if (maxId >= 0)
			sql += " AND HRF_ID <=" + maxId;
		if (asc)
			sql += " ORDER BY Datum ASC";
		else
			sql += " ORDER BY Datum DESC";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				while (rs.next()) {
					HRF curHrf = new HRF(rs);
					liste.add(curHrf);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getAllHRFs: " + e);
		}

		// Convert to array
		return liste.toArray(new HRF[0]);
	}

	public List<HRF> getHRFsSince(Timestamp from) {
		var liste = new ArrayList<HRF>();
		ResultSet rs;
		String sql = "SELECT * FROM " + getTableName() + " WHERE Datum>='" + from + "' ORDER BY Datum ASC";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				while (rs.next()) {
					HRF curHrf = new HRF(rs);
					liste.add(curHrf);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getAllHRFs: " + e);
		}
		return liste;
	}

	public HRF getPreviousHRF(int hrfId) {
		return loadHRF(" HRF_ID < " + hrfId + " order by HRF_ID desc LIMIT 1");
	}

	public HRF loadHRFDownloadedAt(Timestamp fetchDate){
		return loadHRF("DATUM = '" + fetchDate + "'");
	}

	private HRF loadHRF(String where) {
		var sql="select * from HRF where " + where;
		final ResultSet rs = adapter.executeQuery(sql);
		try {
			if (rs != null) {
				if (rs.first()) {
					var ret = new HRF(rs);
					rs.close();
					return ret;
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "HRFTable.loadHRF: " + e);
		}
		return null;
	}

	public String getHrfIdPerWeekList(int nWeeks) {
		var sql = "select min(hrf_id) as id from " +
				getTableName() +
				" group by unix_timestamp(datum)/7/86400 order by id desc limit " +
				nWeeks;

		var ret = new StringBuilder();
		var separator = "";

		final ResultSet rs = adapter.executeQuery(sql);
		try {
			if (rs != null) {
				while (rs.next()) {
					ret.append(separator).append(rs.getInt("ID"));
					separator=",";
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getAllHRFs: " + e);
		}
		return ret.toString();
	}
}
