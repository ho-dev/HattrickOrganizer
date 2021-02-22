package core.db;

import core.file.hrf.HRF;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
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
			latestHrf = loadLatestHrf();
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
	 * liefert die aktuelle Id des neuesten HRF-Files
	 */
	private HRF loadLatestHrf() {
		ResultSet rs = null;

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
	 * Sucht das letzte HRF zwischen dem angegebenen Datum und 6 Tagen davor
	 * Wird kein HRF gefunden wird -1 zurückgegeben
	 */
	int getPreviousHRFId(int hrfId) {
		String sql;
		int previousHrfId = -1;

		sql = "select TOP 1 HRF_ID from HRF where datum < (select DATUM from " + getTableName()
				+ " where HRF_ID=" + hrfId + ") order by datum desc";

		final ResultSet rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					previousHrfId = rs.getInt("HRF_ID");
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DBZugriff.getPreviousHRF: " + e.toString());
		}

		return previousHrfId;
	}

	/**
	 * speichert das Verein
	 */
	void saveHRF(int hrfId, String name, Timestamp datum) {
		String statement;

		// insert vorbereiten
		statement = "INSERT INTO " + getTableName() + " ( HRF_ID, Name, Datum ) VALUES(";
		statement += ("" + hrfId + ",'" + name + "','" + datum.toString() + "' )");
		adapter.executeUpdate(statement);

		if (hrfId > getMaxHrf().getHrfId()) {
			maxHrf = new HRF(hrfId, name, datum);
		}

		if (datum.after(getLatestHrf().getDatum())) {
			latestHrf = new HRF(hrfId, name, datum);
		}
	}

	/**
	 * gibt es ein HRFFile in der Datenbank mit dem gleichen Dateimodifieddatum
	 * schon?
	 * 
	 * @param date
	 *            der letzten Dateiänderung der zu vergleichenden Datei
	 * 
	 * @return Das Datum der Datei, an den die Datei importiert wurde oder null,
	 *         wenn keine passende Datei vorhanden ist
	 */
	String getHrfName4Date(Timestamp date) {
		ResultSet rs;
		final String statement = "select Name from " + getTableName() + " where Datum='"
				+ date.toString() + "'";

		try {
			rs = adapter.executeQuery(statement);

			if (rs != null) {
				rs.beforeFirst();

				if (rs.next()) {
					return rs.getString("Name");
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getName4Date " + e);
		}

		// Error or nothing found
		return null;
	}

	/**
	 * Gibt die HRFId vor dem Datum zurück, wenn möglich
	 */
	int getHrfId4Date(Timestamp time) {
		ResultSet rs = null;
		String sql = null;
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
							+ time.toString() + "' ORDER BY Datum";
					rs = adapter.executeQuery(sql);

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
		ResultSet rs = null;
		HRF hrf = null;

		rs = getSelectByHrfID(hrfID);

		try {
			if (rs != null) {
				rs.first();
				hrf = new HRF(rs);
				rs.close();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getHrf: " + e);
		}

		return hrf;
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
		Vector<HRF> liste = new Vector<HRF>();
		ResultSet rs = null;
		String sql = null;
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
		HRF[] allHrfs = liste.toArray(new HRF[0]);
		return allHrfs;
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
		var sql = "select * from HRF where HRF_ID < " + hrfId
				+ " order by HRF_ID desc LIMIT 1";

		final ResultSet rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					return new HRF(rs);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DBZugriff.getPreviousHRF: " + e.toString());
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
					ret.append(separator).append(rs.getInt(0));
					separator=",";
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getAllHRFs: " + e);
		}
		return ret.toString();
	}
}
