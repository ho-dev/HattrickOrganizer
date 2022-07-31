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

	private PreparedStatement saveHRFStatement;
	private PreparedStatement getSaveHRFStatement(){
		if ( saveHRFStatement==null){
			saveHRFStatement=createInsertStatement();
		}
		return saveHRFStatement;
	}
	/**
	 * Save hattrick resource file information
	 */
	void saveHRF(int hrfId, HODateTime datum) {
		adapter.executePreparedUpdate(getSaveHRFStatement(), hrfId, datum.toDbTimestamp());
		if (hrfId > getMaxHrf().getHrfId()) {
			maxHrf = new HRF(hrfId,  datum);
		}

		// reimport of latest hrf file has to set latestHrf to a new value
		if (!datum.isBefore(getLatestHrf().getDatum()) && hrfId != latestHrf.getHrfId()) {
			latestHrf = new HRF(hrfId, datum);
		}
	}

	PreparedStatement getHrfID4DateStatement;
	private PreparedStatement getGetHrfID4DateStatement(){
		if ( getHrfID4DateStatement== null){
			getHrfID4DateStatement = adapter.createPreparedStatement("SELECT HRF_ID FROM " + getTableName() + " WHERE Datum<=? ORDER BY Datum DESC LIMIT 1");
		}
		return getHrfID4DateStatement;
	}
	PreparedStatement getHrfID4DateStatement2;
	private PreparedStatement getGetHrfID4DateStatement2(){
		if ( getHrfID4DateStatement2== null){
			getHrfID4DateStatement2 = adapter.createPreparedStatement("SELECT HRF_ID FROM " + getTableName() + " WHERE Datum>? ORDER BY Datum LIMIT 1");
		}
		return getHrfID4DateStatement2;
	}

	/**
	 * Load id of latest hrf downloaded before time if available, otherwise the first after time
	 */
	int getHrfId4Date(Timestamp time) {
		int hrfID = 0;
		var rs = adapter.executePreparedQuery(getGetHrfID4DateStatement(), time);
		try {
			if (rs != null) {
				if (rs.first()) {
					// HRF available?
					hrfID = rs.getInt("HRF_ID");
				}
				else {
					rs = adapter.executePreparedQuery(getGetHrfID4DateStatement2(), time);
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
		rs = adapter._executeQuery(sql);

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

	PreparedStatement getHRFsSinceStatement;
	private PreparedStatement getGetHRFsSinceStatement(){
		if ( getHRFsSinceStatement==null){
			getHRFsSinceStatement=adapter.createPreparedStatement("SELECT * FROM " + getTableName() + " WHERE Datum>=? ORDER BY Datum ASC");
		}
		return getHRFsSinceStatement;
	}
	public List<HRF> getHRFsSince(Timestamp from) {
		var liste = new ArrayList<HRF>();

		var rs = adapter.executePreparedQuery(getGetHRFsSinceStatement(), from);
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

	private PreparedStatement loadLatestHRFDownloadedBeforeStatement;
	private PreparedStatement getLoadLatestHRFDownloadedBeforeStatement(){
		if ( loadLatestHRFDownloadedBeforeStatement==null){
			loadLatestHRFDownloadedBeforeStatement=adapter.createPreparedStatement("select * from HRF where DATUM < ? order by DATUM desc LIMIT 1");
		}
		return loadLatestHRFDownloadedBeforeStatement;
	}
	public HRF loadLatestHRFDownloadedBefore(Timestamp fetchDate) {
		return loadHRF(getLoadLatestHRFDownloadedBeforeStatement(), fetchDate);
	}

	/**
	 * liefert die Maximal Vergebene Id eines HRF-Files
	 */
	private PreparedStatement loadMaxHrfStatement;
	private PreparedStatement getLoadMaxHrfStatement(){
		if ( loadMaxHrfStatement==null){
			loadMaxHrfStatement=adapter.createPreparedStatement("select * from HRF order by HRF_ID desc LIMIT 1");
		}
		return loadMaxHrfStatement;
	}
	private HRF loadMaxHrf() {
		return loadHRF(getLoadMaxHrfStatement());
	}

	private PreparedStatement loadHRFStatement;
	private PreparedStatement getLoadHRFStatement(){
		if ( loadHRFStatement==null){
			loadHRFStatement=adapter.createPreparedStatement("select * from HRF where HRF_ID = ?");
		}
		return loadHRFStatement;
	}
	public HRF loadHRF(int id){
		return loadHRF(getLoadHRFStatement(), id );
	}

	private PreparedStatement loadLatestDownloadedHRFStatement;
	private PreparedStatement getLoadLatestDownloadedHRFStatement(){
		if ( loadLatestDownloadedHRFStatement==null){
			loadLatestDownloadedHRFStatement=adapter.createPreparedStatement("select * from HRF order by DATUM desc LIMIT 1");
		}
		return loadLatestDownloadedHRFStatement;
	}
	public HRF loadLatestDownloadedHRF() {
		return loadHRF(getLoadLatestDownloadedHRFStatement());
	}

	private PreparedStatement loadHRFDownloadedAtStatement;
	private PreparedStatement getLoadHRFDownloadedAtStatement(){
		if ( loadHRFDownloadedAtStatement==null){
			loadHRFDownloadedAtStatement=adapter.createPreparedStatement("select * from HRF where DATUM =?");
		}
		return loadHRFDownloadedAtStatement;
	}
	public HRF loadHRFDownloadedAt(Timestamp fetchDate){
		return loadHRF(getLoadHRFDownloadedAtStatement(), fetchDate);
	}

	private HRF loadHRF(PreparedStatement preparedStatement, Object ... params) {
		final ResultSet rs = adapter.executePreparedQuery(preparedStatement,params);
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

		final ResultSet rs = adapter._executeQuery(sql);
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
