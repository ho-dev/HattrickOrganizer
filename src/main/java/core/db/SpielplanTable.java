package core.db;

import core.util.HODateTime;
import core.util.HOLogger;
import module.series.Spielplan;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


final class SpielplanTable extends AbstractTable {
	final static String TABLENAME = "SPIELPLAN";
	
	SpielplanTable(JDBCAdapter adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[4];
		columns[0]= new ColumnDescriptor("LigaID",Types.INTEGER,false);
		columns[1]= new ColumnDescriptor("LigaName",Types.VARCHAR,true,256);
		columns[2]= new ColumnDescriptor("Saison",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("FetchDate",Types.TIMESTAMP,false);
	}

	@Override
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this,"WHERE SAISON=? AND LigaId=?");
	}

	private final PreparedSelectStatementBuilder getAllSpielplaeneStatementBuilder = new PreparedSelectStatementBuilder(this, "ORDER BY Saison DESC");

	/**
	 * Returns all the game schedules from the database.
	 *
	 * @param withFixtures Includes the games part of the schedule if <code>true</code>.
	 */
	List<Spielplan> getAllSpielplaene(boolean withFixtures) {
		final List<Spielplan> gameSchedules = new ArrayList<>();

		try {
			var rs = adapter.executePreparedQuery(getAllSpielplaeneStatementBuilder.getStatement());
			assert rs != null;
			while (rs.next()) {
				// Plan auslesen
				var plan = new Spielplan();

				plan.setFetchDate(HODateTime.fromDbTimestamp(rs.getTimestamp("FetchDate")));
				plan.setLigaId(rs.getInt("LigaID"));
				plan.setLigaName(rs.getString("LigaName"));
				plan.setSaison(rs.getInt("Saison"));

				gameSchedules.add(plan);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getSpielplan Error" + e);
		}

		if (withFixtures) {
			for (Spielplan gameSchedule : gameSchedules) {
				DBManager.instance().getPaarungen(gameSchedule);
			}
		}

		return gameSchedules;
	}

	private final PreparedSelectStatementBuilder getSpielplanStatementBuilder = new PreparedSelectStatementBuilder(this, " WHERE LigaID = ? AND Saison = ? ORDER BY FetchDate DESC LIMIT 1");

	/**
	 * Gets a game schedule from the database; returns the latest if either param is -1.
	 *
	 * @param ligaId ID of the series.
	 * @param saison Season number.
	 */
	Spielplan getSpielplan(int ligaId, int saison) {
		try {
			var rs = adapter.executePreparedQuery(getSpielplanStatementBuilder.getStatement(), ligaId, saison);
			assert rs != null;
			if (rs.next()) {
				var plan = new Spielplan();

				plan.setFetchDate(HODateTime.fromDbTimestamp(rs.getTimestamp("FetchDate")));
				plan.setLigaId(rs.getInt("LigaID"));
				plan.setLigaName(rs.getString("LigaName"));
				plan.setSaison(rs.getInt("Saison"));

				DBManager.instance().getPaarungen(plan);
				return plan;
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getSpielplan Error" + e);

			HOLogger.instance().log(getClass(),e);
		}

		return null;
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
			try {

				executePreparedDelete(plan.getSaison(), plan.getLigaId());
				executePreparedInsert(
						plan.getLigaId(),
						plan.getLigaName(),
						plan.getSaison(),
						plan.getFetchDate().toDbTimestamp()
				);
				DBManager.instance().storePaarung(plan.getMatches(), plan.getLigaId(), plan.getSaison());
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"DB.storeSpielplan Error" + e);
				HOLogger.instance().log(getClass(),e);
			}
		}
	}

	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
		return new PreparedSelectStatementBuilder(this," ORDER BY FetchDate DESC LIMIT 1");
	}
	public Spielplan getLatestSpielplan() {
		try {
			var rs = executePreparedSelect();
			assert rs != null;
			if (rs.next()) {
				var plan = new Spielplan();

				plan.setFetchDate(HODateTime.fromDbTimestamp(rs.getTimestamp("FetchDate")));
				plan.setLigaId(rs.getInt("LigaID"));
				plan.setLigaName(rs.getString("LigaName"));
				plan.setSaison(rs.getInt("Saison"));

				DBManager.instance().getPaarungen(plan);
				return plan;
			}
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "DB.getSpielplan Error" + e);
		}
		return null;
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
