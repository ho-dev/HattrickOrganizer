package core.db;

import core.model.misc.Basics;
import core.util.HODateTime;
import java.sql.Types;

final class BasicsTable extends AbstractTable {
	static final String TABLENAME = "BASICS";

	BasicsTable(ConnectionManager connectionManager) {
		super(TABLENAME, connectionManager);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((b)->((Basics)b).getHrfId()).setSetter((b, v)->((Basics)b).setHrfId((int)v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Manager").setGetter((b)->((Basics)b).getManager()).setSetter((b, v)->((Basics)b).setManager((String)v)).setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TeamID").setGetter((b)->((Basics)b).getTeamId()).setSetter((b, v)->((Basics)b).setTeamId((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TeamName").setGetter((b)->((Basics)b).getTeamName()).setSetter((b, v)->((Basics)b).setTeamName((String)v)).setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Land").setGetter((b)->((Basics)b).getCountryId()).setSetter((b, v)->((Basics)b).setCountryId((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Liga").setGetter((b)->((Basics)b).getLiga()).setSetter((b, v)->((Basics)b).setLiga((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Saison").setGetter((b)->((Basics)b).getSeason()).setSetter((b, v)->((Basics)b).setSeason((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Spieltag").setGetter((b)->((Basics)b).getSpieltag()).setSetter((b, v)->((Basics)b).setSpieltag((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Datum").setGetter((b)->((Basics)b).getDatum().toDbTimestamp()).setSetter((b, v)->((Basics)b).setDatum((HODateTime)v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Region").setGetter((b)->((Basics)b).getRegionId()).setSetter((b, v)->((Basics)b).setRegionId((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HasSupporter").setGetter((b)->((Basics)b).isHasSupporter()).setSetter((b, v)->((Basics)b).setHasSupporter((boolean)v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ActivationDate").setGetter((b)-> HODateTime.toDbTimestamp (((Basics)b).getActivationDate())).setSetter((b, v)->((Basics)b).setActivationDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SeasonOffset").setGetter(b->((Basics)b).getStoredSeasonOffset()).setSetter((b, v)->((Basics)b).setSeasonOffset((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("YouthTeamName").setGetter((b)->((Basics)b).getYouthTeamName()).setSetter((b, v)->((Basics)b).setYouthTeamName((String)v)).setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("YouthTeamID").setGetter((b)->((Basics)b).getYouthTeamId()).setSetter((b, v)->((Basics)b).setYouthTeamId((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[]{
				"CREATE INDEX IBASICS_2 ON " + getTableName() + "(Datum)"
		};
	}

	/**
	 * save Basics
	 */
	void saveBasics(int hrfId, Basics basics) {
		basics.setHrfId(hrfId);
		store(basics);
	}

	/**
	 * Loads the {@link Basics} instance for the given HRF ID <code>hrfId</code>.
	 *
	 * @return Basics – {@link Basics} instance for HRF ID, empty object otherwise.
	 */
	Basics loadBasics(int hrfId) {
		var ret = loadOne(Basics.class, hrfId);
		if (ret == null) return new Basics();
		deriveMissingSeasonOffset(ret);
		return ret;
	}

	/**
	 * Rows without a stored season offset (NULL, from before Hattrick reported it) get one
	 * derived from the download date. A stored 0 is a real offset and stays: deriving it
	 * from the date is off by one when the download falls between the league's season change
	 * and HO's fixed Monday changeover (#2587).
	 */
	static void deriveMissingSeasonOffset(Basics basics) {
		if (basics.getStoredSeasonOffset() != null) return;
		var season0 = basics.getDatum().toHTWeek().season();
		basics.setSeasonOffset(basics.getSeason() - season0);
	}
}
