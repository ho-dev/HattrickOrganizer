package core.db;

import core.model.HOVerwaltung;
import core.util.HODateTime;
import core.util.HOLogger;
import module.ifa.IfaMatch;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

public class IfaMatchTable extends AbstractTable {

	public final static String TABLENAME = "IFA_MATCHES";

	IfaMatchTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("MATCHID").setGetter((o) -> ((IfaMatch) o).getMatchId()).setSetter((o, v) -> ((IfaMatch) o).setMatchId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchTyp").setGetter((o) -> ((IfaMatch) o).getMatchTyp()).setSetter((o, v) -> ((IfaMatch) o).setMatchTyp((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PLAYEDDATE").setGetter((o) -> ((IfaMatch) o).getPlayedDate().toHT()).setSetter((o, v) -> ((IfaMatch) o).setPlayedDate(HODateTime.fromHT((String) v))).setType(Types.VARCHAR).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HOMETEAMID").setGetter((o) -> ((IfaMatch) o).getHomeTeamId()).setSetter((o, v) -> ((IfaMatch) o).setHomeTeamId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AWAYTEAMID").setGetter((o) -> ((IfaMatch) o).getAwayTeamId()).setSetter((o, v) -> ((IfaMatch) o).setAwayTeamId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HOMETEAMGOALS").setGetter((o) -> ((IfaMatch) o).getHomeTeamGoals()).setSetter((o, v) -> ((IfaMatch) o).setHomeTeamGoals((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AWAYTEAMGOALS").setGetter((o) -> ((IfaMatch) o).getAwayTeamGoals()).setSetter((o, v) -> ((IfaMatch) o).setAwayTeamGoals((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HOME_LEAGUEID").setGetter((o) -> ((IfaMatch) o).getHomeLeagueId()).setSetter((o, v) -> ((IfaMatch) o).setHomeLeagueId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AWAY_LEAGUEID").setGetter((o) -> ((IfaMatch) o).getAwayLeagueId()).setSetter((o, v) -> ((IfaMatch) o).setAwayLeagueId((int) v)).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	@Override
	protected String[] getConstraintStatements() {
		return new String[]{" PRIMARY KEY (MATCHID, MATCHTYP)"};
	}

	boolean isMatchInDB(int matchId, int matchTyp) {
		var match = loadOne(IfaMatch.class, matchId, matchTyp);
		return match != null;
	}

	Timestamp getLastMatchDate() {
		String select = "SELECT MAX(" + "PLAYEDDATE" + ") FROM " + getTableName();
		ResultSet rs = adapter.executeQuery(select);
		try {
			if ((rs != null) && (rs.next())) {
				return rs.getTimestamp(1);
			}
		} catch (Exception e) {
			HOLogger.instance().error(this.getClass(), e);
		}
		return null;
	}

	private final PreparedSelectStatementBuilder getHomeMatchesStatementBuilder = new PreparedSelectStatementBuilder(this, "WHERE HOMETEAMID=? ORDER BY AWAY_LEAGUEID ASC");
	private final PreparedSelectStatementBuilder getAwayMatchesStatementBuilder = new PreparedSelectStatementBuilder(this, "WHERE AWAYTEAMID=? ORDER BY HOME_LEAGUEID ASC");

	IfaMatch[] getMatches(boolean home) {
		var list = load(IfaMatch.class,
				adapter.executePreparedQuery(home?getHomeMatchesStatementBuilder.getStatement():getAwayMatchesStatementBuilder.getStatement(),
						HOVerwaltung.instance().getModel().getBasics().getTeamId()));
		return list.toArray(new IfaMatch[0]);
	}

	void insertMatch(IfaMatch match) {
		store(match);
	}

	@Override
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this, "");
	}
	void deleteAllMatches() {
		executePreparedDelete();
	}

}
