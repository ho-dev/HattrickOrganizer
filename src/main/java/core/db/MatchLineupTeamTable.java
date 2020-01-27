package core.db;

import core.model.match.MatchLineupPlayer;
import core.model.match.MatchLineupTeam;
import core.util.HOLogger;
import module.lineup.substitution.model.Substitution;

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
		columns = new ColumnDescriptor[5];
		columns[0]= new ColumnDescriptor("MatchID",Types.INTEGER,false);
		columns[1]= new ColumnDescriptor("Erfahrung",Types.INTEGER,false);
		columns[2]= new ColumnDescriptor("TeamName",Types.VARCHAR,false,256);
		columns[3]= new ColumnDescriptor("TeamID",Types.INTEGER,false);
		columns[4]= new ColumnDescriptor("StyleOfPlay",Types.INTEGER,false);
	}

	MatchLineupTeam getMatchLineupTeam(int matchID, int teamID) {
		MatchLineupTeam team = null;
		String sql = null;
		ResultSet rs = null;
		
		try {
			sql = "SELECT * FROM "+getTableName()+" WHERE MatchID = " + matchID + " AND TeamID = " + teamID;

			rs = adapter.executeQuery(sql);

			rs.first();

			team = new MatchLineupTeam(matchID, DBManager.deleteEscapeSequences(rs.getString("TeamName")),
										teamID, rs.getInt("Erfahrung"), rs.getInt("StyleOfPlay"));
			team.setAufstellung(DBManager.instance().getMatchLineupPlayers(matchID, teamID));
			
			team.setSubstitutions(new ArrayList<Substitution>(DBManager.instance().getMatchSubstitutionsByMatchTeam(teamID, matchID)));
			
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchLineupTeam Error" + e);
			team = null;
		}

		return team;
	}

	void storeMatchLineupTeam(MatchLineupTeam team, int matchID) {
		if (team != null) {
			final String[] where = { "MatchID" , "TeamID"};
			final String[] werte = { "" + matchID, "" +team.getTeamID()};			
			delete(where, werte);

			String sql = null;
			//saven
			try {
				//insert vorbereiten
				sql = "INSERT INTO "+getTableName()+" ( MatchID, Erfahrung, TeamName, TeamID, StyleOfPlay ) VALUES(";
				sql += (matchID + "," + team.getErfahrung() + ", '" + DBManager.insertEscapeSequences(team.getTeamName()) + 
								"'," + team.getTeamID() + "," + team.getStyleOfPlay() + " )");
				adapter.executeUpdate(sql);

				//Store players
				for (int i = 0; i < team.getAufstellung().size(); i++) {
					
					((MatchLineupPlayerTable) DBManager.instance().getTable(
							MatchLineupPlayerTable.TABLENAME)).storeMatchLineupPlayer(
									(MatchLineupPlayer) team.getAufstellung().elementAt(i),
									matchID, team.getTeamID());
				}
				
				// Store Substitutions
				
				((MatchSubstitutionTable) DBManager.instance().getTable(MatchSubstitutionTable.TABLENAME))
						.storeMatchSubstitutionsByMatchTeam(matchID, team.getTeamID(), team.getSubstitutions());
				
				
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"DB.storeMatchLineupTeam Error" + e);
				HOLogger.instance().log(getClass(),e);
			}
		}
	}		
}
