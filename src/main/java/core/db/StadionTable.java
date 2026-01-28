package core.db;

import core.util.AmountOfMoney;
import core.util.HODateTime;
import tool.arenasizer.Stadium;

import java.sql.Types;

final class StadionTable extends AbstractTable {
	static final String TABLENAME = "STADION";

	StadionTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
			ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter(p -> ((Stadium) p).getHrfId()).setSetter((p, v) -> ((Stadium) p).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("StadionName").setGetter(p -> ((Stadium) p).getStadiumName()).setSetter((p, v) -> ((Stadium) p).setStadiumName((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("AnzSteh").setGetter(p -> ((Stadium) p).getTerraces()).setSetter((p, v) -> ((Stadium) p).setTerraces((int) v)).setType(Types.INTEGER).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("AnzSitz").setGetter(p -> ((Stadium) p).getBasicSeating()).setSetter((p, v) -> ((Stadium) p).setBasicSeating((int) v)).setType(Types.INTEGER).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("AnzDach").setGetter(p -> ((Stadium) p).getUnderRoofSeating()).setSetter((p, v) -> ((Stadium) p).setUnderRoofSeating((int) v)).setType(Types.INTEGER).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("AnzLogen").setGetter(p -> ((Stadium) p).getVipBox()).setSetter((p, v) -> ((Stadium) p).setVipBox((int) v)).setType(Types.INTEGER).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("AusbauSteh").setGetter(p -> ((Stadium) p).getTerracesUnderConstruction()).setSetter((p, v) -> ((Stadium) p).setTerracesUnderConstruction((int) v)).setType(Types.INTEGER).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("AusbauSitz").setGetter(p -> ((Stadium) p).getBasicSeatingUnderConstruction()).setSetter((p, v) -> ((Stadium) p).setBasicSeatingUnderConstruction((int) v)).setType(Types.INTEGER).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("AusbauDach").setGetter(p -> ((Stadium) p).getUnderRoofSeatingUnderConstruction()).setSetter((p, v) -> ((Stadium) p).setUnderRoofSeatingUnderConstruction((int) v)).setType(Types.INTEGER).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("AusbauLogen").setGetter(p -> ((Stadium) p).getVipBoxUnderConstruction()).setSetter((p, v) -> ((Stadium) p).setVipBoxUnderConstruction((int) v)).setType(Types.INTEGER).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("Ausbau").setGetter(p -> ((Stadium) p).isUnderConstruction()).setSetter((p, v) -> ((Stadium) p).setUnderConstruction(0 != (int) v)).setType(Types.INTEGER).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("AusbauKosten").setGetter(p -> ((Stadium) p).getExpansionCostsInSwedishKrona()).setSetter((p, v) -> ((Stadium) p).setExpansionCosts((AmountOfMoney) v)).setType(Types.DECIMAL).isNullable(true).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("ArenaID").setGetter(p -> ((Stadium) p).getArenaId()).setSetter((p, v) -> ((Stadium) p).setArenaId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("REBUILT_DATE").setGetter(p -> HODateTime.toDbTimestamp(((Stadium) p).getRebuiltDate())).setSetter((p, v) -> ((Stadium) p).setRebuiltDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
			ColumnDescriptor.Builder.newInstance().setColumnName("EXPANSION_DATE").setGetter(p -> HODateTime.toDbTimestamp(((Stadium) p).getExpansionDate())).setSetter((p, v) -> ((Stadium) p).setExpansionDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
            ColumnDescriptor.Builder.newInstance().setColumnName("ARENA_IMAGE").setGetter(p -> ((Stadium) p).getArenaImage()).setSetter((p, v) -> ((Stadium) p).setArenaImage((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.newInstance().setColumnName("ARENA_FALLBACK_IMAGE").setGetter(p -> ((Stadium) p).getArenaFallbackImage()).setSetter((p, v) -> ((Stadium) p).setArenaFallbackImage((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
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
