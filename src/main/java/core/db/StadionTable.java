package core.db;

import tool.arenasizer.Stadium;
import java.sql.Types;

final class StadionTable extends AbstractTable {
	final static String TABLENAME = "STADION";

	StadionTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((p) -> ((Stadium) p).getHrfId()).setSetter((p, v) -> ((Stadium) p).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("StadionName").setGetter((p) -> ((Stadium) p).getStadienname()).setSetter((p, v) -> ((Stadium) p).setStadienname((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AnzSteh").setGetter((p) -> ((Stadium) p).getStehplaetze()).setSetter((p, v) -> ((Stadium) p).setStehplaetze((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AnzSitz").setGetter((p) -> ((Stadium) p).getSitzplaetze()).setSetter((p, v) -> ((Stadium) p).setSitzplaetze((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AnzDach").setGetter((p) -> ((Stadium) p).getUeberdachteSitzplaetze()).setSetter((p, v) -> ((Stadium) p).setUeberdachteSitzplaetze((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AnzLogen").setGetter((p) -> ((Stadium) p).getLogen()).setSetter((p, v) -> ((Stadium) p).setLogen((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AusbauSteh").setGetter((p) -> ((Stadium) p).getAusbauStehplaetze()).setSetter((p, v) -> ((Stadium) p).setAusbauStehplaetze((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AusbauSitz").setGetter((p) -> ((Stadium) p).getAusbauSitzplaetze()).setSetter((p, v) -> ((Stadium) p).setAusbauSitzplaetze((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AusbauDach").setGetter((p) -> ((Stadium) p).getAusbauUeberdachteSitzplaetze()).setSetter((p, v) -> ((Stadium) p).setAusbauUeberdachteSitzplaetze((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AusbauLogen").setGetter((p) -> ((Stadium) p).getAusbauLogen()).setSetter((p, v) -> ((Stadium) p).setAusbauLogen((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Ausbau").setGetter((p) -> ((Stadium) p).isAusbau()).setSetter((p, v) -> ((Stadium) p).setAusbau(0 != (int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AusbauKosten").setGetter((p) -> ((Stadium) p).getAusbauKosten()).setSetter((p, v) -> ((Stadium) p).setAusbauKosten((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ArenaID").setGetter((p) -> ((Stadium) p).getArenaId()).setSetter((p, v) -> ((Stadium) p).setArenaId((int) v)).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	/**
	 * save Arena
	 *
	 * @param hrfId   foreign key of status info
	 * @param stadion stadium to store
	 */
	void saveStadion(int hrfId, Stadium stadion) {
		if (stadion != null) {
			stadion.setHrfId(hrfId);
			store(stadion);
		}
	}

	Stadium getStadion(int hrfID) {
		return loadOne(Stadium.class, hrfID);
	}
}