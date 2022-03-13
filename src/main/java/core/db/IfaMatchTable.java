package core.db;

import core.model.HOVerwaltung;
import core.util.HODateTime;
import core.util.HOLogger;
import module.ifa.DateHelper;
import module.ifa.IfaMatch;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class IfaMatchTable extends AbstractTable {

	public final static String TABLENAME = "IFA_MATCHES";

	IfaMatchTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("MATCHID", Types.INTEGER, false),
				new ColumnDescriptor("MatchTyp", Types.INTEGER, false), //Integer defining the type of match
				new ColumnDescriptor("PLAYEDDATE", Types.VARCHAR, false, 25),
				new ColumnDescriptor("HOMETEAMID", Types.INTEGER, false),
				new ColumnDescriptor("AWAYTEAMID", Types.INTEGER, false),
				new ColumnDescriptor("HOMETEAMGOALS", Types.INTEGER, false),
				new ColumnDescriptor("AWAYTEAMGOALS", Types.INTEGER, false),
				new ColumnDescriptor("HOME_LEAGUEID", Types.INTEGER, false),
				new ColumnDescriptor("AWAY_LEAGUEID", Types.INTEGER, false)
		};
	}

	@Override
	protected String[] getConstraintStatements() {
		return new String[] {" PRIMARY KEY (MATCHID, MATCHTYP)"};
	}

	boolean isMatchInDB(int matchId) {
		String select = "SELECT * FROM " + getTableName() +
				" WHERE " + "MATCHID" +
				" = " + matchId;
		ResultSet rs = adapter.executeQuery(select);
		try {
			if ((rs != null) && (rs.next()))
				return true;
		} catch (Exception localException) {
			return false;
		}
		return false;
	}

	Timestamp getLastMatchDate() {
		String select = "SELECT MAX(" + "PLAYEDDATE" + ") FROM " + getTableName();
		ResultSet rs = adapter.executeQuery(select);
		try {
			if ((rs != null) && (rs.next())) {
				return rs.getTimestamp(1);
			}
		} catch (Exception e) {
			HOLogger.instance().error(this.getClass(),e);
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	IfaMatch[] getMatches(boolean home) {
		var list = new ArrayList<IfaMatch>();
		String select = "SELECT * FROM " + getTableName() +
				" WHERE " + (home ? "HOMETEAMID" : "AWAYTEAMID") +
				" = " + HOVerwaltung.instance().getModel().getBasics().getTeamId() +
				" ORDER BY " +
				(home ? "AWAY_LEAGUEID" : "HOME_LEAGUEID") +
				" ASC ";
		ResultSet rs = adapter.executeQuery(select);

		if (rs == null) {
			return new IfaMatch[0];
		}
		try {
			SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			while (rs.next()) {
				IfaMatch tmp = new IfaMatch(rs.getInt("MATCHTYP"));
				tmp.setAwayLeagueId(rs.getInt("AWAY_LEAGUEID"));
				tmp.setHomeLeagueId(rs.getInt("HOME_LEAGUEID"));
				tmp.setPlayedDate(HODateTime.fromDbTimestamp(rs.getTimestamp("PLAYEDDATE")));
				tmp.setHomeTeamId(rs.getInt("HOMETEAMID"));
				tmp.setAwayTeamId(rs.getInt("AWAYTEAMID"));
				tmp.setHomeTeamGoals(rs.getInt("HOMETEAMGOALS"));
				tmp.setAwayTeamGoals(rs.getInt("AWAYTEAMGOALS"));
				list.add(tmp);
			}

		} catch (Exception e) {
			HOLogger.instance().error(this.getClass(), e);
		}
		return list.toArray(new IfaMatch[0]);
	}

	@SuppressWarnings("deprecation")
	void insertMatch(IfaMatch match) {
		var sql = initInsertStatement();
		String statement = sql +
				match.getMatchId() + "," +
				match.getMatchTyp() + ",'" +
				HODateTime.toDbTimestamp(match.getPlayedDate()) + "'," +
				match.getHomeTeamId() + "," +
				match.getAwayTeamId() + "," +
				match.getHomeTeamGoals() + "," +
				match.getAwayTeamGoals() + "," +
				match.getHomeLeagueId() + "," +
				match.getAwayLeagueId() + ")";
		adapter.executeUpdate(statement);
	}

	static private String insertStatementPrefix;
	private String initInsertStatement() {
		if ( insertStatementPrefix == null) {
			var s = new StringBuilder();
			s.append("insert into ").append(getTableName()).append("(");
			for (int i = 0; i < columns.length; i++) {
				s.append(columns[i].getColumnName());
				if (i < columns.length - 1)
					s.append(",");
			}
			s.append(") VALUES (");
			insertStatementPrefix = s.toString();
		}
		return insertStatementPrefix;
	}

	void deleteAllMatches() {
		String sql = "delete from " + getTableName();
		adapter.executeUpdate(sql);
	}
	
}
