package core.db;

import core.file.hrf.HRF;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.PreparedStatement;
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
	 * Save hattrick resource file information
	 */
	void saveHRF(int hrfId, HODateTime datum) {
		executePreparedInsert(
				hrfId,
				datum.toDbTimestamp());
		if (hrfId > getMaxHrf().getHrfId()) {
			maxHrf = new HRF(hrfId,  datum);
		}

		// reimport of latest hrf file has to set latestHrf to a new value
		if (!datum.isBefore(getLatestHrf().getDatum()) && hrfId != latestHrf.getHrfId()) {
			latestHrf = new HRF(hrfId, datum);
		}
	}

	private PreparedSelectStatementBuilder getGetHrfID4DateStatementBeforeBuilder = new PreparedSelectStatementBuilder(this, " WHERE Datum<=? ORDER BY Datum DESC LIMIT 1");
	private PreparedSelectStatementBuilder getGetHrfID4DateStatementAfterBuilder = new PreparedSelectStatementBuilder(this, " WHERE Datum>? ORDER BY Datum LIMIT 1");

	/**
	 * Load id of latest hrf downloaded before time if available, otherwise the first after time
	 */
	int getHrfIdNearDate(Timestamp time) {
		int hrfID = 0;
		var rs = adapter.executePreparedQuery(getGetHrfID4DateStatementBeforeBuilder.getStatement(), time);
		try {
			if (rs != null) {
				if (rs.next()) {
					// HRF available?
					hrfID = rs.getInt("HRF_ID");
				}
				else {
					rs = adapter.executePreparedQuery(getGetHrfID4DateStatementAfterBuilder.getStatement(), time);
					assert rs != null;
					if (rs.next()) {
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

	private PreparedSelectStatementBuilder loadHRFOrderedStatementBuilder = new PreparedSelectStatementBuilder(this, " WHERE Datum>=? ORDER BY Datum ASC");

	public List<HRF> getHRFsSince(Timestamp from) {
		var liste = new ArrayList<HRF>();
		var rs = this.adapter.executePreparedQuery(loadHRFOrderedStatementBuilder.getStatement(), from);
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

	private PreparedSelectStatementBuilder loadLatestHRFDownloadedBeforeStatementBuilder = new PreparedSelectStatementBuilder(this, "where DATUM < ? order by DATUM desc LIMIT 1");
	public HRF loadLatestHRFDownloadedBefore(Timestamp fetchDate) {
		return loadHRF(loadLatestHRFDownloadedBeforeStatementBuilder.getStatement(), fetchDate);
	}

	/**
	 * liefert die Maximal Vergebene Id eines HRF-Files
	 */
	private PreparedSelectStatementBuilder loadMaxHrfStatementBuilder = new PreparedSelectStatementBuilder(this, "order by HRF_ID desc LIMIT 1");

	private HRF loadMaxHrf() {
		return loadHRF(loadMaxHrfStatementBuilder.getStatement());
	}

	public HRF loadHRF(int id){
		return loadHRF(getPreparedSelectStatement(), id );
	}

	private PreparedSelectStatementBuilder loadLatestDownloadedHRFStatementBuilder = new PreparedSelectStatementBuilder(this, "order by DATUM desc LIMIT 1");
	public HRF loadLatestDownloadedHRF() {
		return loadHRF(loadLatestDownloadedHRFStatementBuilder.getStatement());
	}

	private PreparedSelectStatementBuilder loadHRFDownloadedAtStatementBuilder = new PreparedSelectStatementBuilder(this, "where DATUM =?");
	public HRF loadHRFDownloadedAt(Timestamp fetchDate){
		return loadHRF(loadHRFDownloadedAtStatementBuilder.getStatement(), fetchDate);
	}

	private HRF loadHRF(PreparedStatement preparedStatement, Object ... params) {
		final ResultSet rs = adapter.executePreparedQuery(preparedStatement,params);
		try {
			if (rs != null) {
				if (rs.next()) {
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

	public List<Integer> getHrfIdPerWeekList(int nWeeks) {
		var sql = "select min(hrf_id) as id from " +
				getTableName() +
				" group by unix_timestamp(datum)/7/86400 order by id desc limit " +
				nWeeks;

		var ret = new ArrayList<Integer>();
		final ResultSet rs = adapter.executeQuery(sql);
		try {
			if (rs != null) {
				while (rs.next()) {
					ret.add(rs.getInt("ID"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getAllHRFs: " + e);
		}
		return ret;
	}
}
