package core.db;

import core.model.HOVerwaltung;
import core.model.match.*;
import core.model.enums.MatchType;
import core.util.HOLogger;
import module.lineup.Lineup;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;


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
	protected  String[] getConstraintStatements() {
		return new String[]{
				"  PRIMARY KEY (" + columns[0].getColumnName() + "," + columns[1].getColumnName() + "," + columns[2].getColumnName() + ")"
		};
	}

	MatchLineupTeam getMatchLineupTeam(int iMatchType, int matchID, int teamID) {
		try {
			var sql = "SELECT * FROM " + getTableName() + " WHERE MatchTyp = " + iMatchType + " AND MatchID = " + matchID + " AND TeamID = " + teamID;
			var rs = adapter.executeQuery(sql);
			if (rs != null) {
				if (rs.first()) {
					var team = new MatchLineupTeam(MatchType.getById(iMatchType),
							matchID,
							DBManager.deleteEscapeSequences(rs.getString("TeamName")),
							teamID,
							rs.getInt("Erfahrung"));
					var styleOfPlay = StyleOfPlay.fromInt(rs.getInt("StyleOfPlay"));
					var matchTacticType = MatchTacticType.fromInt(DBManager.getInteger(rs, "Tactic"));
					var matchTeamAttitude = MatchTeamAttitude.fromInt(DBManager.getInteger(rs, "Attitude"));
					team.loadLineup();
					team.setStyleOfPlay(styleOfPlay);
					team.setMatchTacticType(matchTacticType);
					team.setMatchTeamAttitude(matchTeamAttitude);
					return team;
				}
				rs.close();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.getMatchLineupTeam Error " + e);
		}
		return null;
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

	public ArrayList<MatchLineupTeam> getTemplateMatchLineupTeams() {
		ArrayList<MatchLineupTeam> ret = new ArrayList<>();
		try {
			String sql = "SELECT * FROM " + getTableName() + " WHERE TeamID<0 AND MATCHTYP=0 AND MATCHID=-1";

			var rs = adapter.executeQuery(sql);
			if ( rs != null) {
				rs.beforeFirst();
				while (rs.next()) {
					var team = new MatchLineupTeam(
							MatchType.getById(rs.getInt("MATCHTYP")),
							rs.getInt("MATCHID"),
							DBManager.deleteEscapeSequences(rs.getString("TeamName")),
							rs.getInt("TEAMID"),
							rs.getInt("Erfahrung"));

					var styleOfPlay = StyleOfPlay.fromInt(rs.getInt("StyleOfPlay"));
					var matchTacticType = MatchTacticType.fromInt(DBManager.getInteger(rs, "Tactic"));
					var matchTeamAttitude = MatchTeamAttitude.fromInt(DBManager.getInteger(rs, "Attitude"));
					team.setStyleOfPlay(styleOfPlay);
					team.setMatchTacticType(matchTacticType);
					team.setMatchTeamAttitude(matchTeamAttitude);
					ret.add(team);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchLineupTeam Error" + e);
		}
		return ret;
	}

	public int getTemplateMatchLineupTeamNextNumber() {
		try {
			var sql = "SELECT MIN(TEAMID) FROM " + getTableName() + " WHERE MatchTyp=0 AND MATCHID=-1";
			var rs = adapter.executeQuery(sql);
			if (rs != null) {
				rs.beforeFirst();
				if (rs.next()) {
					return Math.min(-1,rs.getInt(1)-1);
				}
			}
		}
		catch (Exception e){
			HOLogger.instance().log(getClass(),e);
		}
		return -1;
	}
}
