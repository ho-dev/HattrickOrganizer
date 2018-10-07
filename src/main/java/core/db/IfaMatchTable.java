package core.db;

import core.model.HOVerwaltung;
import core.util.HOLogger;
import module.ifa.DateHelper;
import module.ifa.IfaMatch;

import java.sql.ResultSet;
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
		columns = new ColumnDescriptor[8];
		columns[0] = new ColumnDescriptor("MATCHID", Types.INTEGER, false, true);
		columns[1] = new ColumnDescriptor("PLAYEDDATE", Types.VARCHAR, false, 25);
		columns[2] = new ColumnDescriptor("HOMETEAMID", Types.INTEGER, false);
		columns[3] = new ColumnDescriptor("AWAYTEAMID", Types.INTEGER, false);
		columns[4] = new ColumnDescriptor("HOMETEAMGOALS", Types.INTEGER, false);
		columns[5] = new ColumnDescriptor("AWAYTEAMGOALS", Types.INTEGER, false);
		columns[6] = new ColumnDescriptor("HOME_LEAGUEID", Types.INTEGER, false);
		columns[7] = new ColumnDescriptor("AWAY_LEAGUEID", Types.INTEGER, false);
	}

	boolean isLeagueIDinDB(int leagueID, boolean homeAway) {
		StringBuffer select = new StringBuffer(100);
		select.append("SELECT * FROM ").append(getTableName());
		select.append(" WHERE ");
		select.append(homeAway ? "HOME_LEAGUEID" : "AWAY_LEAGUEID");
		select.append(" = ").append(leagueID);
		ResultSet rs = adapter.executeQuery(select.toString());
		try {
			if ((rs != null) && (rs.next()))
				return true;
		} catch (Exception localException) {
			return false;
		}
		return false;
	}

	boolean isMatchinDB(int matchId) {
		StringBuffer select = new StringBuffer(100);
		select.append("SELECT * FROM ").append(getTableName());
		select.append(" WHERE ").append("MATCHID");
		select.append(" = ").append(matchId);
		ResultSet rs = adapter.executeQuery(select.toString());
		try {
			if ((rs != null) && (rs.next()))
				return true;
		} catch (Exception localException) {
			return false;
		}
		return false;
	}

	String getLastMatchDate(String defaultValue) {
		StringBuffer select = new StringBuffer(100);
		select.append("SELECT MAX(").append("PLAYEDDATE").append(") FROM ");
		select.append(getTableName());
		ResultSet rs = adapter.executeQuery(select.toString());
		String s = defaultValue;
		try {
			if ((rs != null) && (rs.next())) {
				String tmp = rs.getString(1);
				if (tmp != null)
					s = DateHelper.getDateString(DateHelper.getDate(tmp));
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage() + "\ns=" + s);
			e.printStackTrace();
		}
		return s;
	}

	@SuppressWarnings("deprecation")
	IfaMatch[] getMatches(boolean home) {
		ArrayList<IfaMatch> list = new ArrayList<IfaMatch>();
		StringBuffer select = new StringBuffer(100);
		select.append("SELECT * FROM ").append(getTableName());
		select.append(" WHERE ").append(home ? "HOMETEAMID" : "AWAYTEAMID");
		select.append(" = ").append(HOVerwaltung.instance().getModel().getBasics().getTeamId());
		select.append(" ORDER BY ");
		select.append(home ? "AWAY_LEAGUEID" : "HOME_LEAGUEID");
		select.append(" ASC ");
		ResultSet rs = adapter.executeQuery(select.toString());

		if (rs == null) {
			return new IfaMatch[0];
		}
		try {
			SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			while (rs.next()) {
				IfaMatch tmp = new IfaMatch();
				tmp.setAwayLeagueId(rs.getInt("AWAY_LEAGUEID"));
				tmp.setHomeLeagueId(rs.getInt("HOME_LEAGUEID"));
				tmp.setPlayedDate(simpleFormat.parse(rs.getString("PLAYEDDATE")));
				tmp.setPlayedDateString(rs.getString("PLAYEDDATE"));
				tmp.setHomeTeamId(rs.getInt("HOMETEAMID"));
				tmp.setAwayTeamId(rs.getInt("AWAYTEAMID"));
				tmp.setHomeTeamGoals(rs.getInt("HOMETEAMGOALS"));
				tmp.setAwayTeamGoals(rs.getInt("AWAYTEAMGOALS"));
				list.add(tmp);
			}

		} catch (Exception e) {
			HOLogger.instance().error(this.getClass(), e);
		}
		return list.toArray(new IfaMatch[list.size()]);
	}

	@SuppressWarnings("deprecation")
	void insertMatch(IfaMatch match) {
		StringBuilder statement = new StringBuilder(100);
		statement.append("insert into ").append(getTableName()).append("(");
		for (int i = 0; i < columns.length; i++) {
			statement.append(columns[i].getColumnName());
			if (i < columns.length - 1)
				statement.append(",");
		}
		statement.append(") VALUES (");
		statement.append(match.getMatchId()).append(",'");
		statement.append(match.getPlayedDateString()).append("',");
		statement.append(match.getHomeTeamId()).append(",");
		statement.append(match.getAwayTeamId()).append(",");
		statement.append(match.getHomeTeamGoals()).append(",");
		statement.append(match.getAwayTeamGoals()).append(",");
		statement.append(match.getHomeLeagueId()).append(",");
		statement.append(match.getAwayLeagueId()).append(")");
		adapter.executeUpdate(statement.toString());
	}
	
	void deleteAllMatches() {
		String sql = "delete from " + getTableName();
		adapter.executeUpdate(sql);
	}
	
}
