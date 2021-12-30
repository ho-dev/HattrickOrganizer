package core.db;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.model.Team;
import core.util.HOLogger;
import module.nthrf.NtTeamDetails;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

final class NtTeamTable extends AbstractTable {
	public final static String TABLENAME = "NTTEAM";

	protected NtTeamTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
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
				new ColumnDescriptor("NAME",Types.VARCHAR,true,127),
				new ColumnDescriptor("SHORTNAME",Types.VARCHAR,true,127),
				new ColumnDescriptor("COACHID",Types.INTEGER,true),
				new ColumnDescriptor("COACHNAME",Types.VARCHAR,true,127),
				new ColumnDescriptor("LEAGUEID",Types.INTEGER,true),
				new ColumnDescriptor("LEAGUENAME",Types.VARCHAR,true,127),
				new ColumnDescriptor("TRAINERID",Types.INTEGER,true),
				new ColumnDescriptor("TRAINERNAME",Types.VARCHAR,true,127),
				new ColumnDescriptor("SUPPORTERPOPULARITY",Types.INTEGER,true),
				new ColumnDescriptor("RATING",Types.INTEGER,true),
				new ColumnDescriptor("FANCLUBSIZE",Types.INTEGER,true),
				new ColumnDescriptor("RANK",Types.INTEGER,true),
				new ColumnDescriptor("FETCHEDDATE",Types.TIMESTAMP,true)
		};
	}

	@Override
	protected  String[] getConstraintStatements() {
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
			if ( matchDate != null ) sql.append(" AND FETCHEDDATE<'").append(matchDate).append("'");
			sql.append(" ORDER BY HRF_ID DESC LIMIT 1");
			var rs = adapter.executeQuery(sql.toString());
			if (rs != null) {
				rs.first();
				var team = new NtTeamDetails(rs);
				rs.close();
				return team;
			}
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "Error while loading NT team details: " + e);
		}
		return null;
	}
}
