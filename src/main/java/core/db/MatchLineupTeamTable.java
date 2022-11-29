package core.db;

import core.model.match.*;
import core.model.enums.MatchType;
import core.util.HOLogger;
import java.sql.Types;
import java.util.List;

public final class MatchLineupTeamTable extends AbstractTable {

	/**
	 * tablename
	 **/
	public final static String TABLENAME = "MATCHLINEUPTEAM";

	MatchLineupTeamTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
		idColumns = 3;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchID").setGetter((o) -> ((MatchLineupTeam) o).getMatchId()).setSetter((o, v) -> ((MatchLineupTeam) o).setMatchId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TeamID").setGetter((o) -> ((MatchLineupTeam) o).getTeamID()).setSetter((o, v) -> ((MatchLineupTeam) o).setTeamID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchTyp").setGetter((o) -> ((MatchLineupTeam) o).getMatchType().getId()).setSetter((o, v) -> ((MatchLineupTeam) o).setMatchType(MatchType.getById((int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Erfahrung").setGetter((o) -> ((MatchLineupTeam) o).getExperience()).setSetter((o, v) -> ((MatchLineupTeam) o).setExperience((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TeamName").setGetter((o) -> ((MatchLineupTeam) o).getTeamName()).setSetter((o, v) -> ((MatchLineupTeam) o).setTeamName((String) v)).setType(Types.VARCHAR).setLength(265).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("StyleOfPlay").setGetter((o) -> (StyleOfPlay.toInt(((MatchLineupTeam) o).getStyleOfPlay()))).setSetter((o, v) -> ((MatchLineupTeam) o).setStyleOfPlay(StyleOfPlay.fromInt((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Attitude").setGetter((o) -> (MatchTeamAttitude.toInt(((MatchLineupTeam) o).getMatchTeamAttitude()))).setSetter((o, v) -> ((MatchLineupTeam) o).setMatchTeamAttitude(MatchTeamAttitude.fromInt((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Tactic").setGetter((o) -> (MatchTacticType.toInt(((MatchLineupTeam) o).getMatchTacticType()))).setSetter((o, v) -> ((MatchLineupTeam) o).setMatchTacticType(MatchTacticType.fromInt((Integer) v))).setType(Types.INTEGER).isNullable(true).build()
		};
	}

	@Override
	protected String[] getConstraintStatements() {
		return new String[]{
				"  PRIMARY KEY (" + columns[0].getColumnName() + "," + columns[1].getColumnName() + "," + columns[2].getColumnName() + ")"
		};
	}

	MatchLineupTeam loadMatchLineupTeam(int iMatchType, int matchID, int teamID) {
		return loadOne(MatchLineupTeam.class, matchID, teamID, iMatchType);
	}

	void deleteMatchLineupTeam(MatchLineupTeam team) {
		executePreparedDelete(team.getMatchId(), team.getTeamID(), team.getMatchType().getId());
	}

	void storeMatchLineupTeam(MatchLineupTeam team) {
		if (team != null) {
			team.setIsStored(isStored(team.getMatchId(), team.getTeamID(), team.getMatchType().getId()));
			store(team);
		}
	}

	private final PreparedSelectStatementBuilder loadTemplateStatementBuilder = new PreparedSelectStatementBuilder(this,
			" WHERE TeamID<0 AND MATCHTYP=0 AND MATCHID=-1");

	public List<MatchLineupTeam> getTemplateMatchLineupTeams() {
		return load(MatchLineupTeam.class, adapter.executePreparedQuery(loadTemplateStatementBuilder.getStatement()));
	}

	public int getTemplateMatchLineupTeamNextNumber() {
		try {
			var sql = "SELECT MIN(TEAMID) FROM " + getTableName() + " WHERE MatchTyp=0 AND MATCHID=-1";
			var rs = adapter.executeQuery(sql);
			if (rs != null) {
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