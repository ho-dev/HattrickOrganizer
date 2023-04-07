package core.db;

import core.model.series.Liga;
import java.sql.Types;

public final class LigaTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "LIGA";
	
	LigaTable(JDBCAdapter adapter){
		super(TABLENAME,adapter);
	}
	
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((o) -> ((Liga) o).getHrfId()).setSetter((o, v) -> ((Liga) o).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LigaName").setGetter((o) -> ((Liga) o).getLiga()).setSetter((o, v) -> ((Liga) o).setLiga((String) v)).setType(Types.VARCHAR).isNullable(false).setLength(127).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Punkte").setGetter((o) -> ((Liga) o).getPunkte()).setSetter((o, v) -> ((Liga) o).setPunkte((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ToreFuer").setGetter((o) -> ((Liga) o).getToreFuer()).setSetter((o, v) -> ((Liga) o).setToreFuer((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ToreGegen").setGetter((o) -> ((Liga) o).getToreGegen()).setSetter((o, v) -> ((Liga) o).setToreGegen((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Platz").setGetter((o) -> ((Liga) o).getPlatzierung()).setSetter((o, v) -> ((Liga) o).setPlatzierung((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Spieltag").setGetter((o) -> ((Liga) o).getSpieltag()).setSetter((o, v) -> ((Liga) o).setSpieltag((int) v)).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX ILIGA_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")"};
	}

	/**
	 * store league
	 */
	void saveLiga(int hrfId, Liga liga) {
		if ( liga != null) {
			liga.setHrfId(hrfId);
			store(liga);
		}
	}

	Liga getLiga(int hrfID) {
		return loadOne(Liga.class, hrfID);
	}
	
}
