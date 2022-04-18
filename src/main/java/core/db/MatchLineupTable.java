package core.db;

import core.model.match.MatchLineup;
import core.model.enums.MatchType;
import core.model.match.SourceSystem;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


public final class MatchLineupTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "MATCHLINEUP";
	
	protected MatchLineupTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("MatchID", Types.INTEGER, false),
				new ColumnDescriptor("MatchTyp", Types.INTEGER, false)
		};
	}

	@Override
	protected String[] getConstraintStatements() {
		return new String[] {" PRIMARY KEY (MATCHID, MATCHTYP)"};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX IMATCHLINEUP_1 ON " + getTableName() + "(MatchID)"};
	}	

	MatchLineup loadMatchLineup(int iMatchType, int matchID) {
		MatchLineup lineup;
		try {
			var sql = "SELECT * FROM "+getTableName()+" WHERE MatchTyp=" + iMatchType + " AND MatchID = " + matchID;
			var rs = adapter.executeQuery(sql);
			rs.first();
			lineup = createMatchLineup(rs);

			var match = DBManager.instance().loadMatchDetails(iMatchType, matchID);
			lineup.setHomeTeam(DBManager.instance().loadMatchLineupTeam(iMatchType, matchID, match.getHomeTeamId()));
			lineup.setGuestTeam(DBManager.instance().loadMatchLineupTeam(iMatchType, matchID, match.getGuestTeamId()));

		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchLineup Error " + e);
			lineup = null;
		}

		return lineup;
	}

	/**
	 * Ist das Match schon in der Datenbank vorhanden?
	 */
	boolean isMatchLineupInDB(MatchType matchType, int matchid) {
		boolean vorhanden = false;

		try {
			final String sql = "SELECT MatchId FROM "+getTableName()+" WHERE MATCHTYP=" + matchType.getId() + " AND MatchId=" + matchid;
			final ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();

			if (rs.next()) {
				vorhanden = true;
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"isMatchLineupInDB() : " + e);
		}

		return vorhanden;
	}

	/**
	 * store match lineup including team and player information
	 */
	void storeMatchLineup(MatchLineup lineup) {
		storeMatchLineup(lineup, null);
	}

	void storeMatchLineup(MatchLineup lineup, Integer teamId) {
		if (lineup != null) {
			//There should never be anything to delete, but...
			final String[] where = {"MatchTyp", "MatchID"};
			final String[] werte = {"" + lineup.getMatchType().getId(), "" + lineup.getMatchID()};
			delete(where, werte);
			try {
				//insert
				var sql = "INSERT INTO " + getTableName() + " (MatchID, MatchTyp) VALUES(" +
						lineup.getMatchID() + "," +
						lineup.getMatchTyp().getId() + ")";
				adapter.executeUpdate(sql);

				if (teamId == null || teamId == lineup.getHomeTeamId()) {
					DBManager.instance().storeMatchLineupTeam(lineup.getHomeTeam());
				}
				if (teamId == null || teamId == lineup.getGuestTeamId()) {
					DBManager.instance().storeMatchLineupTeam(lineup.getGuestTeam());
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DB.storeMatchLineup Error" + e);
				HOLogger.instance().log(getClass(), e);
			}
		}
	}

	public List<MatchLineup> loadMatchLineups(int sourceSystem) {

		var lineups = new ArrayList<MatchLineup>();
		try {
			var sql = "SELECT * FROM " + getTableName() + " WHERE MATCHTYP IN " + MatchType.getWhereClauseFromSourceSystem(sourceSystem);

			var rs = adapter.executeQuery(sql);
			rs.beforeFirst();
			while (rs.next()) {
				var lineup = createMatchLineup(rs);
				lineups.add(lineup);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.loadMatchLineups Error" + e);
		}
		return lineups;
	}

	public void deleteYouthMatchLineupsBefore(Timestamp before) {
		var sql = "DELETE FROM " +
				getTableName() +
				" WHERE MATCHTYP IN " + MatchType.getWhereClauseFromSourceSystem(SourceSystem.YOUTH.getValue()) +
				" AND MATCHID IN (SELECT MATCHID FROM  MATCHDETAILS WHERE SpielDatum<'" + before.toString() +
									"' AND MATCHTYP IN "+ MatchType.getWhereClauseFromSourceSystem(SourceSystem.YOUTH.getValue()) + ")";
		try {
			adapter.executeUpdate(sql);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupsBefore Error" + e);
		}
	}

	private MatchLineup createMatchLineup(ResultSet rs) throws SQLException {
		var lineup = new MatchLineup();
		lineup.setMatchID(rs.getInt("MatchID"));
		lineup.setMatchTyp(MatchType.getById(rs.getInt("MatchTyp")));
		return lineup;
	}
}
