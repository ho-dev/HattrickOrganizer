package core.db;

import core.file.hrf.HRF;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class HRFTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "HRF";

	private HRF maxHrf = new HRF();
	private HRF latestHrf = new HRF();

	HRFTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((o) -> ((HRF) o).getHrfId()).setSetter((o, v) -> ((HRF) o).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Datum").setGetter((o) -> ((HRF) o).getDatum().toDbTimestamp()).setSetter((o, v) -> ((HRF) o).setDatum((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build()
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
	void saveHRF(HRF hrf) {
		store(hrf);
		if (hrf.getHrfId() > getMaxHrf().getHrfId()) {
			maxHrf = hrf;
		}

		// reimport of latest hrf file has to set latestHrf to a new value
		if (!hrf.getDatum().isBefore(getLatestHrf().getDatum()) && hrf.getHrfId() != latestHrf.getHrfId()) {
			latestHrf = hrf;
		}
	}

	/**
	 * Load id of latest hrf downloaded before time if available, otherwise the first after time
	 */
	int getHrfIdNearDate(Timestamp time) {
		int hrfID = 0;
		String getGetHrfID4DateBeforeSql = createSelectStatement(" WHERE Datum<=? ORDER BY Datum DESC LIMIT 1");
		try (var rs = connectionManager.executePreparedQuery(getGetHrfID4DateBeforeSql, time)) {
			if (rs != null) {
				if (rs.next()) {
					// HRF available?
					hrfID = rs.getInt("HRF_ID");
				} else {
					String getGetHrfID4DateAfterSql = createSelectStatement(" WHERE Datum>? ORDER BY Datum LIMIT 1");
					try (ResultSet afterRs = connectionManager.executePreparedQuery(getGetHrfID4DateAfterSql, time)) {
						assert afterRs != null;
						if (afterRs.next()) {
							hrfID = afterRs.getInt("HRF_ID");
						}
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
	HRF[] loadAllHRFs(boolean asc) {
		List<HRF> list;
		if (asc){
			String loadAllHrfAscendingSql = createSelectStatement("ORDER BY DATUM ASC");
			list = load(HRF.class, connectionManager.executePreparedQuery(loadAllHrfAscendingSql));
		} else {
			String loadAllHrfDescendingSql = createSelectStatement("ORDER BY DATUM DESC");
			list = load(HRF.class, connectionManager.executePreparedQuery(loadAllHrfDescendingSql));
		}
		// Convert to array
		return list.toArray(new HRF[0]);
	}

	public List<HRF> getHRFsSince(Timestamp from) {
		String loadHRFOrderedSql = createSelectStatement(" WHERE Datum>=? ORDER BY Datum ASC");
		return load(HRF.class, connectionManager.executePreparedQuery(loadHRFOrderedSql, from));
	}

	public HRF loadLatestHRFDownloadedBefore(Timestamp fetchDate) {
		String loadLatestHRFDownloadedBeforeSql = createSelectStatement("where DATUM < ? order by DATUM desc LIMIT 1");
		return loadHRF(loadLatestHRFDownloadedBeforeSql, fetchDate);
	}

	private HRF loadMaxHrf() {
		/**
		 * liefert die Maximal Vergebene Id eines HRF-Files
		 */
		String loadMaxHrfSql = createSelectStatement("order by HRF_ID desc LIMIT 1");
		return loadHRF(loadMaxHrfSql);
	}

	public HRF loadHRF(int id) {
		return loadHRF(createSelectStatement(), id);
	}

	public HRF loadLatestDownloadedHRF() {
		String loadLatestDownloadedHRFSql = createSelectStatement("order by DATUM desc LIMIT 1");
		return loadHRF(loadLatestDownloadedHRFSql);
	}

	public HRF loadHRFDownloadedAt(Timestamp fetchDate) {
		String loadHRFDownloadedAtSql = createSelectStatement("where DATUM =?");
		return loadHRF(loadHRFDownloadedAtSql, fetchDate);
	}

	private HRF loadHRF(String query, Object... params) {
		return loadOne(HRF.class, connectionManager.executePreparedQuery(query, params));
	}

	public List<Integer> getHrfIdPerWeekList(int nWeeks) {
		var sql = "select min(hrf_id) as id from " +
				getTableName() +
				" group by unix_timestamp(datum)/7/86400 order by id desc limit " +
				nWeeks;

		var ret = new ArrayList<Integer>();

		try (final ResultSet rs = connectionManager.executeQuery(sql)) {
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
