package core.db;

import core.util.HODateTime;
import core.util.HOLogger;
import module.series.Spielplan;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Vector;

final class SpielplanTable extends AbstractTable {
	final static String TABLENAME = "SPIELPLAN";
	
	SpielplanTable(JDBCAdapter adapter){
		super(TABLENAME,adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("LigaID").setGetter((p) -> ((Spielplan) p).getLigaId()).setSetter((p, v) -> ((Spielplan) p).setLigaId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Saison").setGetter((p) -> ((Spielplan) p).getSaison()).setSetter((p, v) -> ((Spielplan) p).setSaison((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LigaName").setGetter((p) -> ((Spielplan) p).getLigaName()).setSetter((p, v) -> ((Spielplan) p).setLigaName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("FetchDate").setGetter((p) -> ((Spielplan) p).getFetchDate().toDbTimestamp()).setSetter((p, v) -> ((Spielplan) p).setFetchDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build()
		};
	}

	private final PreparedSelectStatementBuilder getAllSpielplaeneStatementBuilder = new PreparedSelectStatementBuilder(this, "ORDER BY Saison DESC");

	/**
	 * Returns all the game schedules from the database.
	 */
	List<Spielplan> getAllSpielplaene() {
		return load(Spielplan.class, adapter.executePreparedQuery(getAllSpielplaeneStatementBuilder.getStatement()));
	}

	/**
	 * Gets a game schedule from the database; returns the latest if either param is -1.
	 *
	 * @param ligaId ID of the series.
	 * @param saison Season number.
	 */
	Spielplan getSpielplan(int ligaId, int saison) {
		return loadOne(Spielplan.class, ligaId, saison);
	}

	private final DBManager.PreparedStatementBuilder getLigaID4SaisonIDStatementBuilder=new DBManager.PreparedStatementBuilder(
			"SELECT LigaID FROM "+getTableName()+" WHERE Saison=? ORDER BY FETCHDATE DESC LIMIT 1");

	/**
	 * Gibt eine Ligaid zu einer Seasonid zurück, oder -1, wenn kein Eintrag in der DB gefunden
	 * wurde
	 */
	int getLigaID4SaisonID(int seasonid) {
		int ligaid = -1;

		try {
			final ResultSet rs = adapter.executePreparedQuery(getLigaID4SaisonIDStatementBuilder.getStatement(), seasonid);
			assert rs != null;
			if (rs.next()) {
				ligaid = rs.getInt("LigaID");
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getLigaID4SeasonID : " + e);
		}
		return ligaid;
	}
	
	/**
	 * Saves a game schedule ({@link Spielplan}) with its associated fixtures.
	 *
	 * @param plan Spielplan to save.
	 */
	void storeSpielplan(Spielplan plan) {
		if (plan != null) {
			plan.setIsStored(isStored(plan.getLigaId(), plan.getSaison()));
			store(plan);
		}
	}

	private final PreparedSelectStatementBuilder loadLatestSpielplanStatementBuilder = new PreparedSelectStatementBuilder(this,
			" ORDER BY FetchDate DESC LIMIT 1");

	public Spielplan getLatestSpielplan() {
		return loadOne(Spielplan.class, this.adapter.executePreparedQuery(loadLatestSpielplanStatementBuilder.getStatement()));
	}

	/**
	 * load all league ids
	 */
	Integer[] getAllLigaIDs() {
		final Vector<Integer> vligaids = new Vector<>();
		Integer[] ligaids = null;

		try {
			final String sql = "SELECT DISTINCT LigaID FROM SPIELPLAN";
			final ResultSet rs = adapter.executeQuery(sql);
			while (rs != null && rs.next()) {
				vligaids.add(rs.getInt("LigaID"));
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

}
