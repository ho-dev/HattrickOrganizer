package core.db;

import core.model.series.Liga;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;


public final class LigaTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "LIGA";
	
	protected LigaTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}
	
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[7];
		columns[0]= new ColumnDescriptor("HRF_ID",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("LigaName",Types.VARCHAR,false,127);
		columns[2]= new ColumnDescriptor("Punkte",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("ToreFuer",Types.INTEGER,false);
		columns[4]= new ColumnDescriptor("ToreGegen",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("Platz",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("Spieltag",Types.INTEGER,false);
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX ILIGA_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")"};
	}

	/**
	 * store league
	 */
	protected void saveLiga(int hrfId, Liga liga) {
		if (liga != null) {
			// delete existing league
			executePreparedDelete( hrfId );
			executePreparedInsert(
					hrfId,
					liga.getLiga(),
					liga.getPunkte(),
					liga.getToreFuer(),
					liga.getToreGegen(),
					liga.getPlatzierung(),
					liga.getSpieltag()
			);
		}
	}
	
	/**
	 * load all league ids
	 */
	Integer[] getAllLigaIDs() {
		final Vector<Integer> vligaids = new Vector<Integer>();
		Integer[] ligaids = null;

		try {
			final String sql = "SELECT DISTINCT LigaID FROM SPIELPLAN";
			final ResultSet rs = adapter.executeQuery(sql);
			while (rs.next()) {
				vligaids.add(Integer.valueOf(rs.getInt("LigaID")));
			}

			ligaids = new Integer[vligaids.size()];
			for (int i = 0; i < vligaids.size(); i++) {
				ligaids[i] = vligaids.get(i);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getAllLigaIDs : " + e);
		}

		return ligaids;
	}

	Liga getLiga(int hrfID) {
		Liga serie = new Liga();
		if(hrfID == -1){
			return serie;
		}
		else {
			ResultSet rs;
			rs = executePreparedSelect(hrfID);
			try {
				if (rs != null) {
					rs.next();
					serie = new Liga(rs);
					rs.close();
				}
			} catch (Exception e) {
				HOLogger.instance().error(getClass(), "Error while loding Serie model: " + e);
			}
		}
		return serie;
	}
	
}
