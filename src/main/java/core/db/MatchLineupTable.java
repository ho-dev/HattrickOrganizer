package core.db;

import core.model.match.MatchLineup;
import core.model.match.MatchType;
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
				new ColumnDescriptor("SourceSystem", Types.INTEGER, false),
				new ColumnDescriptor("MatchID", Types.INTEGER, false, true),
				new ColumnDescriptor("MatchTyp", Types.INTEGER, false),
				new ColumnDescriptor("HeimName", Types.VARCHAR, false, 256),
				new ColumnDescriptor("HeimID", Types.INTEGER, false),
				new ColumnDescriptor("GastName", Types.VARCHAR, false, 256),
				new ColumnDescriptor("GastID", Types.INTEGER, false),
				new ColumnDescriptor("FetchDate", Types.VARCHAR, false, 256),
				new ColumnDescriptor("MatchDate", Types.VARCHAR, false, 256),
				new ColumnDescriptor("ArenaID", Types.INTEGER, false),
				new ColumnDescriptor("ArenaName", Types.VARCHAR, false, 256)
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX IMATCHLINEUP_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")"};
	}	

	MatchLineup loadMatchLineup(int sourceSystem, int matchID) {
		MatchLineup lineup;
		try {
			var sql = "SELECT * FROM "+getTableName()+" WHERE SourceSystem=" + sourceSystem + " AND MatchID = " + matchID;
			var rs = adapter.executeQuery(sql);
			rs.first();
			lineup = createMatchLineup(rs);
			lineup.setHomeTeam(DBManager.instance().getMatchLineupTeam(sourceSystem, matchID, lineup.getHomeTeamId()));
			lineup.setGuestTeamId(DBManager.instance().getMatchLineupTeam(sourceSystem, matchID, lineup.getGuestTeamId()));
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchLineup Error" + e);
			lineup = null;
		}

		return lineup;
	}

	/**
	 * Ist das Match schon in der Datenbank vorhanden?
	 */
	boolean isMatchLineupVorhanden(int sourceSystem, int matchid) {
		boolean vorhanden = false;

		try {
			final String sql = "SELECT MatchId FROM "+getTableName()+" WHERE SourceSystem=" + sourceSystem + " AND MatchId=" + matchid;
			final ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();

			if (rs.next()) {
				vorhanden = true;
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.isMatchVorhanden : " + e);
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
			final String[] where = {"SourceSystem", "MatchID"};
			final String[] werte = {"" + lineup.getSourceSystem().getValue(), "" + lineup.getMatchID()};
			delete(where, werte);
			try {
				//insert
				var sql = "INSERT INTO " + getTableName() + " (SourceSystem,MatchID,MatchTyp,HeimName,HeimID,GastName," +
						"GastID,FetchDate,MatchDate,ArenaID,ArenaName) VALUES(" +
						lineup.getSourceSystem().getValue() + "," +
						lineup.getMatchID() + "," +
						lineup.getMatchTyp().getId() + ", '" +
						DBManager.insertEscapeSequences(lineup.getHomeTeamName()) + "'," +
						lineup.getHomeTeamId() + ",'" +
						DBManager.insertEscapeSequences(lineup.getGuestTeamName()) + "', " +
						lineup.getGuestTeamId() + ", '" +
						lineup.getStringDownloadDate() + "', '" +
						lineup.getStringMatchDate() + "', " +
						lineup.getArenaID() + ", '" +
						DBManager.insertEscapeSequences(lineup.getArenaName()) + "' )";
				adapter.executeUpdate(sql);

				if (teamId == null || teamId == lineup.getHomeTeamId()) {
					((MatchLineupTeamTable) DBManager.instance().getTable(MatchLineupTeamTable.TABLENAME))
							.storeMatchLineupTeam(lineup.getHomeTeam(), lineup.getMatchID());
				}
				if (teamId == null || teamId == lineup.getGuestTeamId()) {
					((MatchLineupTeamTable) DBManager.instance().getTable(MatchLineupTeamTable.TABLENAME))
							.storeMatchLineupTeam(lineup.getGuestTeam(), lineup.getMatchID());
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DB.storeMatchLineup Error" + e);
				HOLogger.instance().log(getClass(), e);
			}
		}
	}

	public Timestamp getLastYouthMatchDate() {
		var sql = "select max(MatchDate) from " + getTableName() + " where SourceSystem=" + SourceSystem.YOUTH.getValue();
		try {
			var rs = adapter.executeQuery(sql);
			rs.beforeFirst();
			if ( rs.next()){
				return rs.getTimestamp(1);
			}
		}
		catch (Exception ignored){

		}
		return null;
	}

	public List<MatchLineup> loadMatchLineups(int sourceSystem) {
		var lineups = new ArrayList<MatchLineup>();
		try {
			var sql = "SELECT * FROM " + getTableName() + " WHERE SourceSystem=" + sourceSystem;

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

	public void deleteMatchLineupsBefore(int sourceSystem, Timestamp before) {
		var sql = "DELETE FROM " +
				getTableName() +
				" WHERE SOURCESYSTEM=" +
				sourceSystem +
				" AND MATCHDATE<'" +
				before.toString() + "'";
		try {
			adapter.executeUpdate(sql);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupsBefore Error" + e);
		}
	}

	private MatchLineup createMatchLineup(ResultSet rs) throws SQLException {
		var lineup = new MatchLineup();
		lineup.setArenaID(rs.getInt("ArenaID"));
		lineup.setArenaName(DBManager.deleteEscapeSequences(rs.getString("ArenaName")));
		lineup.setDownloadDate(rs.getString("FetchDate"));
		lineup.setGuestTeamId(rs.getInt("GastID"));
		lineup.setGuestTeamName(DBManager.deleteEscapeSequences(rs.getString("GastName")));
		lineup.setHomeTeamId(rs.getInt("HeimID"));
		lineup.setHomeTeamName(DBManager.deleteEscapeSequences(rs.getString("HeimName")));
		lineup.setMatchID(rs.getInt("MatchID"));
		lineup.setMatchTyp(MatchType.getById(rs.getInt("MatchTyp")));
		lineup.setMatchDate(rs.getString("MatchDate"));
		return lineup;
	}
}
