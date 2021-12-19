package core.db;

import core.model.match.*;
import core.model.enums.MatchType;
import core.util.HOLogger;
import module.lineup.Lineup;

import java.sql.ResultSet;
import java.sql.Types;


public final class MatchLineupTeamTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "MATCHLINEUPTEAM";
	
	protected MatchLineupTeamTable(JDBCAdapter  adapter){
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("MatchID",Types.INTEGER,false),
				new ColumnDescriptor("TeamID",Types.INTEGER,false),
				new ColumnDescriptor("MatchTyp",Types.INTEGER,false),
				new ColumnDescriptor("Erfahrung",Types.INTEGER,false),
				new ColumnDescriptor("TeamName",Types.VARCHAR,false,256),
				new ColumnDescriptor("StyleOfPlay",Types.INTEGER,false),
				new ColumnDescriptor("Attitude", Types.INTEGER, true),
				new ColumnDescriptor("Tactic", Types.INTEGER, true)
		};
	}
	@Override
	protected String[] getCreateIndexStatement() {
		return new String[]{
				"CREATE INDEX MATCHLINEUPTEAM_IDX ON " + getTableName() + "(" + columns[0].getColumnName() + "," + columns[1].getColumnName() + "," + columns[2].getColumnName() + ")"
		};
	}

	MatchLineupTeam getMatchLineupTeam(int iMatchType, int matchID, int teamID) {
		MatchLineupTeam team;
		String sql;
		ResultSet rs;
		
		try {
			sql = "SELECT * FROM " + getTableName() + " WHERE MatchTyp = " + iMatchType + " AND MatchID = " + matchID + " AND TeamID = " + teamID;

			rs = adapter.executeQuery(sql);

			assert rs != null;
			rs.first();
			team = new MatchLineupTeam(MatchType.getById(iMatchType),
					matchID,
					DBManager.deleteEscapeSequences(rs.getString("TeamName")),
					teamID,
					rs.getInt("Erfahrung"));

			var styleOfPlay = StyleOfPlay.fromInt(rs.getInt("StyleOfPlay"));
			var matchTacticType = MatchTacticType.fromInt(DBManager.getInteger(rs, "MatchTacticType"));
			var matchTeamAttitude =	MatchTeamAttitude.fromInt(DBManager.getInteger(rs,"MatchTeamAttitude"));

			team.loadLineup();
			team.setStyleOfPlay(styleOfPlay);
			team.setMatchTacticType(matchTacticType);
			team.setMatchTeamAttitude(matchTeamAttitude);

		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchLineupTeam Error" + e);
			team = null;
		}

		return team;
	}

	void storeMatchLineupTeam(MatchLineupTeam team) {

		if (team != null) {
			var matchID = team.getMatchId();
			final String[] where = { "MatchTyp", "MatchID" , "TeamID"};
			final String[] werte = { "" + team.getMatchType().getId(), "" + matchID, "" +team.getTeamID()};
			delete(where, werte);

			String sql;
			//saven
			try {
				//insert vorbereiten
				sql = "INSERT INTO "+getTableName()+" ( MatchTyp, MatchID, Erfahrung, TeamName, TeamID, StyleOfPlay, Attitude, Tactic ) VALUES(";
				sql += (team.getMatchType().getId() + "," +
						matchID + "," +
						team.getExperience() + ", '" +
						DBManager.insertEscapeSequences(team.getTeamName()) + "'," +
						team.getTeamID() + "," +
						StyleOfPlay.toInt(team.getStyleOfPlay()) + "," +
						MatchTeamAttitude.toInt(team.getMatchTeamAttitude()) + "," +
						MatchTacticType.toInt(team.getMatchTacticType()) + " )");
				adapter.executeUpdate(sql);

				//Store players
				var matchLineupPlayerTable = (MatchLineupPlayerTable) DBManager.instance().getTable(MatchLineupPlayerTable.TABLENAME);
				for ( var p : team.getLineup().getAllPositions()) {
					matchLineupPlayerTable.storeMatchLineupPlayer(p, team.getMatchType(), matchID, team.getTeamID());
				}
				
				// Store Substitutions
				var matchSubstitutionTable = (MatchSubstitutionTable) DBManager.instance().getTable(MatchSubstitutionTable.TABLENAME);
				matchSubstitutionTable.storeMatchSubstitutionsByMatchTeam(team.getMatchType().getId(), matchID, team.getTeamID(), team.getSubstitutions());
				
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"DB.storeMatchLineupTeam Error" + e);
				HOLogger.instance().log(getClass(),e);
			}
		}
	}

}
