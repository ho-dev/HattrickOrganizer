package core.db;

import core.model.match.MatchLineup;
import core.model.match.MatchType;
import core.model.match.SourceSystem;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;


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
	protected String[] getCreateIndizeStatements() {
		return new String[] {
			"CREATE INDEX IMATCHLINEUP_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")"};
	}	

	MatchLineup getMatchLineup(int sourceSystem, int matchID) {
		MatchLineup lineup;
		try {
			var sql = "SELECT * FROM "+getTableName()+" WHERE SourceSystem=" + sourceSystem + " AND MatchID = " + matchID;

			var rs = adapter.executeQuery(sql);

			rs.first();

			// Plan auslesen
			lineup = new MatchLineup();
			lineup.setArenaID(rs.getInt("ArenaID"));
			lineup.setArenaName(DBManager.deleteEscapeSequences(rs.getString("ArenaName")));
			lineup.setFetchDatum(rs.getString("FetchDate"));
			lineup.setGastId(rs.getInt("GastID"));
			lineup.setGastName(DBManager.deleteEscapeSequences(rs.getString("GastName")));
			lineup.setHeimId(rs.getInt("HeimID"));
			lineup.setHeimName(DBManager.deleteEscapeSequences(rs.getString("HeimName")));
			lineup.setMatchID(matchID);
			lineup.setMatchTyp(MatchType.getById(rs.getInt("MatchTyp")));
			lineup.setSpielDatum(rs.getString("MatchDate"));

			lineup.setHeim(DBManager.instance().getMatchLineupTeam(sourceSystem, matchID, lineup.getHeimId()));
			lineup.setGast(DBManager.instance().getMatchLineupTeam(sourceSystem, matchID, lineup.getGastId()));
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchLineup Error" + e);

			//HOLogger.instance().log(getClass(),e);
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
			final String[] where = { "SourceSystem", "MatchID" };
			final String[] werte = { "" + lineup.getSourceSystem().getId(), "" + lineup.getMatchID()};
			delete(where, werte);

			//saven
			try {
				//insert vorbereiten
				var sql = "INSERT INTO "+getTableName()+" (SourceSystem,MatchID,MatchTyp,HeimName,HeimID,GastName," +
						"GastID,FetchDate,MatchDate,ArenaID,ArenaName) VALUES("+
						lineup.getSourceSystem().getId() + "," +
						lineup.getMatchID() + "," +
						lineup.getMatchTyp().getId() + ", '" +
						DBManager.insertEscapeSequences(lineup.getHeimName()) + "'," +
						lineup.getHeimId() + ",'" +
						DBManager.insertEscapeSequences(lineup.getGastName()) + "', " +
						lineup.getGastId() + ", '" +
						lineup.getStringFetchDate()	+ "', '"+
						lineup.getStringSpielDate() + "', " +
						lineup.getArenaID() + ", '" +
						DBManager.insertEscapeSequences(lineup.getArenaName()) + "' )";
				adapter.executeUpdate(sql);


				if ( teamId == null || teamId == lineup.getHeimId()){
					((MatchLineupTeamTable) DBManager.instance().getTable(MatchLineupTeamTable.TABLENAME))
							.storeMatchLineupTeam(lineup.getHeim(),	lineup.getMatchID());
				}
				if ( teamId == null || teamId == lineup.getGastId()) {
					((MatchLineupTeamTable) DBManager.instance().getTable(MatchLineupTeamTable.TABLENAME))
							.storeMatchLineupTeam(lineup.getGast(),	lineup.getMatchID());
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"DB.storeMatchLineup Error" + e);
				HOLogger.instance().log(getClass(),e);
			}
		}
	}

	public Timestamp getLastYouthMatchDate() {
		var sql = "select max(MatchDate) from " + getTableName() + " where SourceSystem=" + SourceSystem.YOUTH.getId();
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
}
