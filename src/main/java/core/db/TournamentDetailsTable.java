package core.db;

import core.model.Tournament.TournamentDetails;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Types;

import static core.util.DateTimeUtils.DateToSQLtimeStamp;

public final class TournamentDetailsTable extends AbstractTable {

	public final static String TABLENAME = "TOURNAMENTDETAILS";

	protected TournamentDetailsTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[14];
		columns[0]= new ColumnDescriptor("TournamentId",Types.INTEGER,false,true); //The globally unique identifier of the match
		columns[1]= new ColumnDescriptor("Name",Types.VARCHAR,false,256);
		columns[2]= new ColumnDescriptor("TournamentType",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("Season",Types.SMALLINT,false);  //Current season
		columns[4]= new ColumnDescriptor("LogoUrl",Types.VARCHAR,true,256); //The url to the logo
		columns[5]= new ColumnDescriptor("TrophyType",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("NumberOfTeams",Types.INTEGER,false);
		columns[7]= new ColumnDescriptor("NumberOfGroups",Types.INTEGER,false);
		columns[8]= new ColumnDescriptor("LastMatchRound",Types.TINYINT,false);
		columns[9]= new ColumnDescriptor("FirstMatchRoundDate",Types.TIMESTAMP,false);
		columns[10]= new ColumnDescriptor("NextMatchRoundDate",Types.TIMESTAMP,true); //The date of the next match round. During matches this will be the Next match round.
		columns[11]= new ColumnDescriptor("IsMatchesOngoing",Types.BOOLEAN,false);
		columns[12]= new ColumnDescriptor("Creator_UserID",Types.INTEGER,false);
		columns[13]= new ColumnDescriptor("Creator_Loginname",Types.VARCHAR,true, 256);
	}

	public TournamentDetails getTournamentDetails(int TournamentId)
	{
		TournamentDetails oTournamentDetails = null;
		StringBuilder sql = new StringBuilder(200);

        sql.append("SELECT * FROM ").append(getTableName());
		sql.append(" WHERE TOURNAMENTID = ").append(TournamentId);
		try {
			var rs = adapter.executeQuery(sql.toString());
			assert rs != null;
			rs.beforeFirst();
			if (rs.next()) {
				oTournamentDetails = new TournamentDetails();
				oTournamentDetails.setTournamentId(rs.getInt("TournamentId"));
				oTournamentDetails.setName(rs.getString("Name"));
				oTournamentDetails.setTournamentType(rs.getInt("TournamentType"));
				oTournamentDetails.setSeason((short)rs.getInt("Season"));
				oTournamentDetails.setLogoUrl(rs.getString("LogoUrl"));
				oTournamentDetails.setTrophyType(rs.getInt("TrophyType"));
				oTournamentDetails.setNumberOfTeams(rs.getInt("NumberOfTeams"));
				oTournamentDetails.setNumberOfGroups(rs.getInt("NumberOfGroups"));
				oTournamentDetails.setLastMatchRound((short)rs.getInt("LastMatchRound"));
				oTournamentDetails.setFirstMatchRoundDate(HODateTime.fromDbTimestamp(rs.getTimestamp("FirstMatchRoundDate")));
				oTournamentDetails.setFirstMatchRoundDate(HODateTime.fromDbTimestamp(rs.getTimestamp("NextMatchRoundDate")));
				oTournamentDetails.setMatchesOngoing(rs.getBoolean("IsMatchesOngoing"));
				oTournamentDetails.setCreator_UserId(rs.getInt("Creator_UserID"));
				oTournamentDetails.setCreator_Loginname(rs.getString("Creator_Loginname"));
			}
		}
		catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.getTournamentDetails Error" + e);
		}
		return oTournamentDetails;
	}


	/**
	 * Store Tournament Details into DB
	 */
	void storeTournamentDetails(TournamentDetails oTournamentDetails) {
		StringBuilder sql = new StringBuilder(200);

		try {
			sql.append("INSERT INTO ").append(getTableName());
			sql.append(" (TOURNAMENTID, NAME, TOURNAMENTTYPE, SEASON, LOGOURL, TROPHYTYPE, NUMBEROFTEAMS, NUMBEROFGROUPS, " +
					"LASTMATCHROUND, FIRSTMATCHROUNDDATE, NEXTMATCHROUNDDATE, ISMATCHESONGOING, CREATOR_USERID, CREATOR_LOGINNAME) VALUES (");
			sql.append(oTournamentDetails.getTournamentId()).append(", '");
			sql.append(oTournamentDetails.getName()).append("', ");
			sql.append(oTournamentDetails.getTournamentType()).append(", ");
			sql.append(oTournamentDetails.getSeason()).append(", ");
			if (oTournamentDetails.getLogoUrl() == null)
			{
				sql.append("null, ");
			}
			else
			{
				sql.append("'").append(oTournamentDetails.getLogoUrl()).append("', ");
			}
			sql.append(oTournamentDetails.getTrophyType()).append(", ");
			sql.append(oTournamentDetails.getNumberOfTeams()).append(", ");
			sql.append(oTournamentDetails.getNumberOfGroups()).append(", ");
			sql.append(oTournamentDetails.getLastMatchRound()).append(", '");
			sql.append(HODateTime.toDbTimestamp(oTournamentDetails.getFirstMatchRoundDate())).append("', '");
			sql.append(HODateTime.toDbTimestamp(oTournamentDetails.getNextMatchRoundDate())).append("', ");

			if (oTournamentDetails.getMatchesOngoing())
			{
				sql.append("true, ");
			}
			else
			{
				sql.append("false, ");
			}
			sql.append(oTournamentDetails.getCreator_UserId()).append(", ");

			if (oTournamentDetails.getCreator_Loginname() == null)
			{
				sql.append("null)");
			}
			else
			{
				sql.append("'").append(oTournamentDetails.getCreator_Loginname()).append("')");
			}
			adapter.executeUpdate(sql.toString());
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),
						"DB.storeMatchKurzInfos Error" + e);
				HOLogger.instance().log(getClass(), e);
			}

	}

}
