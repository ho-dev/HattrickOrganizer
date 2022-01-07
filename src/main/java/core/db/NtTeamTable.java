package core.db;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.model.Team;
import core.util.HOLogger;
import module.nthrf.NtTeamDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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
				new ColumnDescriptor("TRAINERID", Types.INTEGER, true),
				new ColumnDescriptor("TRAINERNAME", Types.VARCHAR, true, 127),
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

	void store(NtTeamDetails ntTeamDetails) {
		if (ntTeamDetails != null) {
			final String[] awhereS = {"HRF_ID", "TEAM_ID"};
			final String[] awhereV = {"" + ntTeamDetails.getHrfId(), "" + ntTeamDetails.getTeamId()};

			//delete existing record
			delete(awhereS, awhereV);
			//prepare insert statement
			var statement = "INSERT INTO " +
					getTableName() +
					" (HRF_ID,TEAM_ID,MORALE,SELFCONFIDENCE,xp253,xp343,xp352,xp433,xp442,xp451,xp523,xp532,xp541,xp550," +
					"NAME,SHORTNAME,COACHID,COACHNAME,LEAGUEID,LEAGUENAME,TRAINERID,TRAINERNAME,SUPPORTERPOPULARITY," +
					"RATING,FANCLUBSIZE,RANK,FETCHEDDATE) VALUES (" +
					ntTeamDetails.getHrfId() + "," +
					ntTeamDetails.getTeamId() + "," +
					ntTeamDetails.getMorale() + "," +
					ntTeamDetails.getSelfConfidence() + "," +
					ntTeamDetails.getXp253() + "," +
					ntTeamDetails.getXp343() + "," +
					ntTeamDetails.getXp352() + "," +
					ntTeamDetails.getXp433() + "," +
					ntTeamDetails.getXp442() + "," +
					ntTeamDetails.getXp451() + "," +
					ntTeamDetails.getXp532() + "," +
					ntTeamDetails.getXp541() + "," +
					ntTeamDetails.getXp550() + ",'" +
					ntTeamDetails.getTeamName() + "','" +
					ntTeamDetails.getTeamNameShort() + "'," +
					ntTeamDetails.getCoachId() + ",'" +
					ntTeamDetails.getCoachName() + "'," +
					ntTeamDetails.getLeagueId() + ",'" +
					ntTeamDetails.getLeagueName() + "'," +
					ntTeamDetails.getTrainerId() + ",'" +
					ntTeamDetails.getTrainerName() + "'," +
					ntTeamDetails.getSupportersPopularity() + "," +
					ntTeamDetails.getRatingScore() + "," +
					ntTeamDetails.getFanclubSize() + "," +
					ntTeamDetails.getRank() + ",'" +
					ntTeamDetails.getFetchedDate().toString() + "')";
			adapter.executeUpdate(statement);
		}
	}

	public NtTeamDetails load(int teamId, Timestamp matchDate) {
		try {
			var sql = new StringBuilder();
			sql.append("SELECT * FROM ").append(TABLENAME).append(" WHERE TEAM_ID=").append(teamId);
			if (matchDate != null) sql.append(" AND FETCHEDDATE<'").append(matchDate).append("'");
			sql.append(" ORDER BY HRF_ID DESC LIMIT 1");
			var rs = adapter.executeQuery(sql.toString());
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
		team.setXp532(DBManager.getInteger(rs, "XP532"));
		team.setXp541(DBManager.getInteger(rs, "XP541"));
		team.setXp550(DBManager.getInteger(rs, "XP550"));
		team.setTeamName(DBManager.getString(rs, "NAME"));
		team.setTeamNameShort(DBManager.getString(rs, "SHORTNAME"));
		team.setCoachId(DBManager.getInteger(rs, "COACHID"));
		team.setCoachName(DBManager.getString(rs, "COACHNAME"));
		team.setLeagueId(DBManager.getInteger(rs, "LEAGUEID"));
		team.setLeagueName(DBManager.getString(rs, "LEAGUENAME"));
		team.setTrainerId(DBManager.getInteger(rs, "TRAINERID"));
		team.setTrainerName(DBManager.getString(rs, "TRAINERNAME"));
		team.setSupportersPopularity(DBManager.getInteger(rs, "SUPPORTERPOPULARITY"));
		team.setRatingScore(DBManager.getInteger(rs, "RATING"));
		team.setFanclubSize(DBManager.getInteger(rs, "FANCLUBSIZE"));
		team.setRank(DBManager.getInteger(rs, "RANK"));
		team.setFetchedDate(DBManager.getTimestamp(rs, "FETCHEDDATE"));
		return team;
	}

	public List<NtTeamDetails> load(int hrfId) {
		var ret = new ArrayList<NtTeamDetails>();
		try {
			var rs = adapter.executeQuery("SELECT * FROM " + TABLENAME + " WHERE HRF_ID=" + hrfId);
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