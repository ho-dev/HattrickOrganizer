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
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("HRF_ID", Types.INTEGER, false, true),
				new ColumnDescriptor("Datum", Types.TIMESTAMP, false)
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] { "CREATE INDEX iHRF_1 ON " + getTableName() + "("
				+ columns[1].getColumnName() + ")" };
	}

	HRF getLatestHrf() {
		if (latestHrf.getHrfId() == -1) {
			var hrf =  loadLatestDownloadedHRF();
			if ( hrf != null){
				latestHrf = hrf;
			}
		}
		return latestHrf;
	}

	HRF getMaxHrf() {
		if (maxHrf.getHrfId() == -1) {
			var hrf = loadMaxHrf();
			if ( hrf != null){
				maxHrf = hrf;
			}
		}
		return maxHrf;
	}


	/**
	 * speichert das Verein
	 */
	void saveHRF(int hrfId, HODateTime datum) {
		String statement = "INSERT INTO " + getTableName()
				+ " ( HRF_ID, Datum ) VALUES("
				+ hrfId +  ",'"
				+ datum.toDbTimestamp() + "' )"
				;
		adapter.executeUpdate(statement);

		if (hrfId > getMaxHrf().getHrfId()) {
			maxHrf = new HRF(hrfId,  datum);
		}

		// reimport of latest hrf file has to set latestHrf to a new value
		if (!datum.isBefore(getLatestHrf().getDatum()) && hrfId != latestHrf.getHrfId()) {
			latestHrf = new HRF(hrfId, datum);
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
		sql = "SELECT HRF_ID FROM " + getTableName() + " WHERE Datum<='" + time
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
	 * Get a list of all HRFs
	 * 
	 * @param asc
	 *            order ascending (descending otherwise)
	 * 
	 * @return all matching HRFs
	 */
	HRF[] loadAllHRFs( boolean asc) {
		Vector<HRF> liste = new Vector<>();
		ResultSet rs;
		String sql;
		sql = "SELECT * FROM " + getTableName();
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

	public HRF loadLatestHRFDownloadedBefore(Timestamp fetchDate) {
		return loadHRF(" where DATUM < '" + fetchDate + "' order by DATUM desc LIMIT 1");
	}

	/**
	 * liefert die Maximal Vergebene Id eines HRF-Files
	 */
	private HRF loadMaxHrf() {
		return loadHRF(" order by HRF_ID desc LIMIT 1");
	}

	public HRF loadHRF(int id){
		return loadHRF(" where HRF_ID =" + id );
	}

	public HRF loadLatestDownloadedHRF() {
		return loadHRF(" order by DATUM desc LIMIT 1");
	}

	public HRF loadHRFDownloadedAt(Timestamp fetchDate){
		return loadHRF(" where DATUM = '" + fetchDate + "'");
	}

	private HRF loadHRF(String where) {
		var sql="select * from HRF" + where;
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
