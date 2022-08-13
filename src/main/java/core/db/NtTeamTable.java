package core.db;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.model.Team;
import core.util.HODateTime;
import core.util.HOLogger;
import module.nthrf.NtTeamDetails;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

final class NtTeamTable extends AbstractTable {
	public final static String TABLENAME = "NTTEAM";

	NtTeamTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("HRF_ID", Types.INTEGER, false),
				new ColumnDescriptor("TEAM_ID", Types.INTEGER, false),
				new ColumnDescriptor("MORALE", Types.INTEGER, true),
				new ColumnDescriptor("SELFCONFIDENCE", Types.INTEGER, true),
				new ColumnDescriptor("xp253", Types.INTEGER, false),
				new ColumnDescriptor("xp343", Types.INTEGER, false),
				new ColumnDescriptor("xp352", Types.INTEGER, false),
				new ColumnDescriptor("xp433", Types.INTEGER, false),
				new ColumnDescriptor("xp442", Types.INTEGER, false),
				new ColumnDescriptor("xp451", Types.INTEGER, false),
				new ColumnDescriptor("xp523", Types.INTEGER, false),
				new ColumnDescriptor("xp532", Types.INTEGER, false),
				new ColumnDescriptor("xp541", Types.INTEGER, false),
				new ColumnDescriptor("xp550", Types.INTEGER, false),
				new ColumnDescriptor("NAME", Types.VARCHAR, true, 127),
				new ColumnDescriptor("SHORTNAME", Types.VARCHAR, true, 127),
				new ColumnDescriptor("COACHID", Types.INTEGER, true),
				new ColumnDescriptor("COACHNAME", Types.VARCHAR, true, 127),
				new ColumnDescriptor("LEAGUEID", Types.INTEGER, true),
				new ColumnDescriptor("LEAGUENAME", Types.VARCHAR, true, 127),
				new ColumnDescriptor("SUPPORTERPOPULARITY", Types.INTEGER, true),
				new ColumnDescriptor("RATING", Types.INTEGER, true),
				new ColumnDescriptor("FANCLUBSIZE", Types.INTEGER, true),
				new ColumnDescriptor("RANK", Types.INTEGER, true),
				new ColumnDescriptor("FETCHEDDATE", Types.TIMESTAMP, true)
		};
	}

	@Override
	protected String[] getConstraintStatements() {
		return new String[]{
				"  PRIMARY KEY (HRF_ID, TEAM_ID)"
		};
	}

	@Override
	protected PreparedStatement createDeleteStatement(){
		return createDeleteStatement("WHERE HRF_ID=? AND TEAM_ID=?");
	}
	void store(NtTeamDetails ntTeamDetails) {
		if (ntTeamDetails != null) {
			executePreparedDelete(ntTeamDetails.getHrfId(), ntTeamDetails.getTeamId());
			executePreparedInsert(
					ntTeamDetails.getHrfId(),
					ntTeamDetails.getTeamId(),
					ntTeamDetails.getMorale(),
					ntTeamDetails.getSelfConfidence(),
					ntTeamDetails.getXp253(),
					ntTeamDetails.getXp343(),
					ntTeamDetails.getXp352(),
					ntTeamDetails.getXp433() ,
					ntTeamDetails.getXp442() ,
					ntTeamDetails.getXp451() ,
					ntTeamDetails.getXp523() ,
					ntTeamDetails.getXp532() ,
					ntTeamDetails.getXp541(),
					ntTeamDetails.getXp550(),
					ntTeamDetails.getTeamName(),
					ntTeamDetails.getTeamNameShort(),
					ntTeamDetails.getCoachId() ,
					ntTeamDetails.getCoachName(),
					ntTeamDetails.getLeagueId(),
					ntTeamDetails.getLeagueName(),
					ntTeamDetails.getSupportersPopularity(),
					ntTeamDetails.getRatingScore(),
					ntTeamDetails.getFanclubSize(),
					ntTeamDetails.getRank() ,
					ntTeamDetails.getFetchedDate()
			);
		}
	}

	private  PreparedStatement selectBeforeStatement;
	protected PreparedStatement getSelectBeforeStatement(){
		if ( selectBeforeStatement==null){
			selectBeforeStatement=createSelectStatement("WHERE TEAM_ID=? AND MORALE IS NOT NULL AND FETCHEDDATE<? ORDER BY HRF_ID DESC LIMIT 1");
		}
		return selectBeforeStatement;
	}
	private  PreparedStatement selectTeamStatement;
	protected PreparedStatement getSelectTeamStatement(){
		if ( selectTeamStatement==null){
			selectTeamStatement=createSelectStatement("WHERE TEAM_ID=? AND MORALE IS NOT NULL AND FETCHEDDATE<? ORDER BY HRF_ID DESC LIMIT 1");
		}
		return selectTeamStatement;
	}
	public NtTeamDetails load(int teamId, Timestamp matchDate) {
		try {
			ResultSet rs;
			if ( matchDate!=null){
				rs = executePreparedSelect(getSelectBeforeStatement(), teamId, matchDate);
			}
			else {
				rs = executePreparedSelect(getSelectTeamStatement(), teamId);
			}
			if (rs != null) {
				rs.first();
				var team = createNtTeamDetails(rs);
				rs.close();
				return team;
			}
		} catch (SQLException e) {
			HOLogger.instance().error(getClass(), "Error while loading NT team details: " + e);
		}
		return null;
	}

	private NtTeamDetails createNtTeamDetails(ResultSet rs) {
		var team = new NtTeamDetails();
		team.setTeamId(DBManager.getInteger(rs, "TEAM_ID"));
		team.setHrfId(DBManager.getInteger(rs, "HRF_ID"));
		team.setMorale(DBManager.getInteger(rs, "MORALE"));
		team.setSelfConfidence(DBManager.getInteger(rs, "SELFCONFIDENCE"));
		team.setXp253(DBManager.getInteger(rs, "XP253"));
		team.setXp343(DBManager.getInteger(rs, "XP343"));
		team.setXp352(DBManager.getInteger(rs, "XP352"));
		team.setXp433(DBManager.getInteger(rs, "XP433"));
		team.setXp442(DBManager.getInteger(rs, "XP442"));
		team.setXp451(DBManager.getInteger(rs, "XP451"));
		team.setXp523(DBManager.getInteger(rs, "XP523"));
		team.setXp532(DBManager.getInteger(rs, "XP532"));
		team.setXp541(DBManager.getInteger(rs, "XP541"));
		team.setXp550(DBManager.getInteger(rs, "XP550"));
		team.setTeamName(DBManager.getString(rs, "NAME"));
		team.setTeamNameShort(DBManager.getString(rs, "SHORTNAME"));
		team.setCoachId(DBManager.getInteger(rs, "COACHID"));
		team.setCoachName(DBManager.getString(rs, "COACHNAME"));
		team.setLeagueId(DBManager.getInteger(rs, "LEAGUEID"));
		team.setLeagueName(DBManager.getString(rs, "LEAGUENAME"));
		team.setSupportersPopularity(DBManager.getInteger(rs, "SUPPORTERPOPULARITY"));
		team.setRatingScore(DBManager.getInteger(rs, "RATING"));
		team.setFanclubSize(DBManager.getInteger(rs, "FANCLUBSIZE"));
		team.setRank(DBManager.getInteger(rs, "RANK"));
		team.setFetchedDate(HODateTime.fromDbTimestamp(DBManager.getTimestamp(rs, "FETCHEDDATE")));
		return team;
	}

	@Override
	protected PreparedStatement createSelectStatement(){
		return createSelectStatement(" WHERE HRF_ID=?");
	}
	public List<NtTeamDetails> load(int hrfId) {
		var ret = new ArrayList<NtTeamDetails>();
		try {
			var rs = executePreparedSelect(hrfId);
			if ( rs!=null) {
				rs.beforeFirst();
				while (rs.next()) {
					var ntTeamDetails = createNtTeamDetails(rs);
					ret.add(ntTeamDetails);
				}
			}
		} catch (SQLException e) {
			HOLogger.instance().error(getClass(), "Error while loading NT team details: " + e);
			return null;
		}
		return ret;
	}
}