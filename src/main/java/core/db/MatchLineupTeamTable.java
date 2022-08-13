package core.db;

import core.model.HOVerwaltung;
import core.model.match.*;
import core.model.enums.MatchType;
import core.util.HOLogger;
import module.lineup.Lineup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;


public final class MatchLineupTeamTable extends AbstractTable {

	/**
	 * tablename
	 **/
	public final static String TABLENAME = "MATCHLINEUPTEAM";

	protected MatchLineupTeamTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("MatchID", Types.INTEGER, false),
				new ColumnDescriptor("TeamID", Types.INTEGER, false),
				new ColumnDescriptor("MatchTyp", Types.INTEGER, false),
				new ColumnDescriptor("Erfahrung", Types.INTEGER, false),
				new ColumnDescriptor("TeamName", Types.VARCHAR, false, 256),
				new ColumnDescriptor("StyleOfPlay", Types.INTEGER, false),
				new ColumnDescriptor("Attitude", Types.INTEGER, true),
				new ColumnDescriptor("Tactic", Types.INTEGER, true)
		};
	}

	@Override
	protected String[] getConstraintStatements() {
		return new String[]{
				"  PRIMARY KEY (" + columns[0].getColumnName() + "," + columns[1].getColumnName() + "," + columns[2].getColumnName() + ")"
		};
	}
	@Override
	protected PreparedStatement createDeleteStatement(){
		return createDeleteStatement("WHERE MATCHTYP=? AND MATCHID=?");
	}

	@Override
	protected PreparedStatement createSelectStatement(){
		return createSelectStatement("WHERE MatchTyp = ? AND MatchID = ? AND TeamID = ?");
	}

	MatchLineupTeam getMatchLineupTeam(int iMatchType, int matchID, int teamID) {
		try {
			var sql = "SELECT * FROM " + getTableName() + " WHERE MatchTyp = " + iMatchType + " AND MatchID = " + matchID + " AND TeamID = " + teamID;
			var rs = executePreparedSelect(iMatchType, matchID, teamID);
			if (rs != null) {
				if (rs.first()) {
					var team = new MatchLineupTeam(MatchType.getById(iMatchType),
							matchID,
							rs.getString("TeamName"),
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

	private PreparedStatement deleteMatchLineupTeamStatement;
	private PreparedStatement getDeleteMatchLineupTeamStatement(){
		if ( deleteMatchLineupTeamStatement==null){
			deleteMatchLineupTeamStatement=createDeleteStatement("WHERE MatchTyp=? AND MatchID=? AND TeamID=?");
		}
		return deleteMatchLineupTeamStatement;
	}
	void deleteMatchLineupTeam(MatchLineupTeam team) {
		try {
			if (team != null) {
				executePreparedDelete(getDeleteMatchLineupTeamStatement(),
						team.getMatchType().getId(),
						team.getMatchId(),
						team.getTeamID());
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupTeam Error" + e);
			HOLogger.instance().log(getClass(), e);
		}
	}

	void storeMatchLineupTeam(MatchLineupTeam team) {
		if (team != null) {
			deleteMatchLineupTeam(team);
			try {
				var matchID = team.getMatchId();
				executePreparedInsert(
						matchID,
						team.getTeamID(),
						team.getMatchType().getId(),
						team.getExperience(),
						team.getTeamName(),
						StyleOfPlay.toInt(team.getStyleOfPlay()),
						MatchTeamAttitude.toInt(team.getMatchTeamAttitude()),
						MatchTacticType.toInt(team.getMatchTacticType())
						);

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

			var rs = adapter._executeQuery(sql);
			if ( rs != null) {
				rs.beforeFirst();
				while (rs.next()) {
					var team = new MatchLineupTeam(
							MatchType.getById(rs.getInt("MATCHTYP")),
							rs.getInt("MATCHID"),
							rs.getString("TeamName"),
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
			var rs = adapter._executeQuery(sql);
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