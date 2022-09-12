package core.db;

import core.model.match.MatchLineup;
import core.model.enums.MatchType;
import core.model.match.SourceSystem;
import core.util.HOLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public final class MatchLineupTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "MATCHLINEUP";
	
	MatchLineupTable(JDBCAdapter adapter){
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

	@Override
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this,"WHERE MATCHTYP=? AND MATCHID=?");
	}
	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
		return new PreparedSelectStatementBuilder(this,"WHERE MATCHTYP=? AND MATCHID=?");
	}

	MatchLineup loadMatchLineup(int iMatchType, int matchID) {
		try {
			var rs = executePreparedSelect(iMatchType, matchID);
			rs.next();
			var lineup = createMatchLineup(rs);
			var match = DBManager.instance().loadMatchDetails(iMatchType, matchID);
			lineup.setHomeTeam(DBManager.instance().loadMatchLineupTeam(iMatchType, matchID, match.getHomeTeamId()));
			lineup.setGuestTeam(DBManager.instance().loadMatchLineupTeam(iMatchType, matchID, match.getGuestTeamId()));
			return lineup;

		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchLineup Error " + e);
		}
		return null;
	}

	private final DBManager.PreparedStatementBuilder isMatchLineupInDBStatementBuilder = new DBManager.PreparedStatementBuilder(this.adapter, "SELECT MatchId FROM "+getTableName()+" WHERE MATCHTYP=? AND MatchId=?");

	/**
	 * Ist das Match schon in der Datenbank vorhanden?
	 */
	boolean isMatchLineupInDB(MatchType matchType, int matchid) {
		boolean vorhanden = false;
		try {
			final ResultSet rs = adapter.executePreparedQuery(isMatchLineupInDBStatementBuilder.getStatement(), matchType.getId(), matchid);
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
			try {
				executePreparedDelete( lineup.getMatchType().getId(), lineup.getMatchID());
				//insert
				executePreparedInsert(
						lineup.getMatchID(),
						lineup.getMatchTyp().getId());

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

	private final PreparedSelectStatementBuilder loadYouthMatchLineupsStatementBuilder = new PreparedSelectStatementBuilder(this, " WHERE MATCHTYP IN (" + getMatchTypeInValues() + ")");
	public List<MatchLineup> loadYouthMatchLineups() {

		var lineups = new ArrayList<MatchLineup>();
		try {
			var rs = adapter.executePreparedQuery(loadYouthMatchLineupsStatementBuilder.getStatement());
			while (rs.next()) {
				var lineup = createMatchLineup(rs);
				lineups.add(lineup);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.loadMatchLineups Error" + e);
		}
		return lineups;
	}

	private final PreparedDeleteStatementBuilder deleteYouthMatchLineupsBeforeStatementBuilder = new PreparedDeleteStatementBuilder(this, getDeleteYouthMatchLineupsBeforeStatementSQL());

	private String getDeleteYouthMatchLineupsBeforeStatementSQL() {
		var matchTypes = getMatchTypeInValues();
		return " WHERE MATCHTYP IN (" +
				matchTypes +
				") AND MATCHID IN (SELECT MATCHID FROM  MATCHDETAILS WHERE SpielDatum<? AND MATCHTYP IN (" +
				matchTypes + "))";
	}

	public void deleteYouthMatchLineupsBefore(Timestamp before) {
		try {
			this.adapter.executePreparedUpdate(deleteYouthMatchLineupsBeforeStatementBuilder.getStatement(), before);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupsBefore Error" + e);
		}
	}

	private String getMatchTypeInValues() {
		return MatchType.fromSourceSystem(SourceSystem.YOUTH).stream().map(i->String.valueOf(i.getId())).collect(Collectors.joining(","));
	}

	private MatchLineup createMatchLineup(ResultSet rs) throws SQLException {
		var lineup = new MatchLineup();
		lineup.setMatchID(rs.getInt("MatchID"));
		lineup.setMatchTyp(MatchType.getById(rs.getInt("MatchTyp")));
		return lineup;
	}
}
