package core.db;

import core.model.XtraData;
import core.util.HODateTime;
import java.sql.Types;

final class XtraDataTable extends AbstractTable {
	final static String TABLENAME = "XTRADATA";

	XtraDataTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((p) -> ((XtraData) p).getHrfId()).setSetter((p, v) -> ((XtraData) p).setHrfId((int) v)).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CurrencyRate").setGetter((p) -> ((XtraData) p).getCurrencyRate()).setSetter((p, v) -> ((XtraData) p).setCurrencyRate( (float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HasPromoted").setGetter((p) -> ((XtraData) p).isHasPromoted()).setSetter((p, v) -> ((XtraData) p).setHasPromoted((boolean) v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LogoURL").setGetter((p) -> ((XtraData) p).getLogoURL()).setSetter((p, v) -> ((XtraData) p).setLogoURL((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SeriesMatchDate").setGetter((p) -> ((XtraData) p).getSeriesMatchDate().toDbTimestamp()).setSetter((p, v) -> ((XtraData) p).setSeriesMatchDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TrainingDate").setGetter((p) -> ((XtraData) p).getNextTrainingDate().toDbTimestamp()).setSetter((p, v) -> ((XtraData) p).setTrainingDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("EconomyDate").setGetter((p) -> ((XtraData) p).getEconomyDate().toDbTimestamp()).setSetter((p, v) -> ((XtraData) p).setEconomyDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LeagueLevelUnitID").setGetter((p) -> ((XtraData) p).getLeagueLevelUnitID()).setSetter((p, v) -> ((XtraData) p).setLeagueLevelUnitID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CountryId").setGetter((p) -> ((XtraData) p).getCountryId()).setSetter((p, v) -> ((XtraData) p).setCountryId((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
		};
	}

	/**
	 * load Xtra data
	 */
	XtraData loadXtraData(int hrfID) {
		return loadOne(XtraData.class, hrfID);
	}

	/**
	 * speichert das Team
	 */
	void saveXtraDaten(int hrfId, XtraData xtra) {
		if (xtra != null) {
			xtra.setHrfId(hrfId);
			store(xtra);
		}
	}
}
