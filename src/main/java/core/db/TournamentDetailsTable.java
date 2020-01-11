package core.db;

import core.model.Tournament.TournamentDetails;
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
		columns[13]= new ColumnDescriptor("Creator_Loginname",Types.INTEGER,true);
	}

	@Override
	protected String[] getCreateIndizeStatements() {
		return new String[] {
			"CREATE INDEX ITOURNAMENTDETAILS_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")"};
	}


	public TournamentDetails getTournamentDetails(int TournamentId)
	{
		TournamentDetails oTournamentDetails = null;
		StringBuilder sql = new StringBuilder(200);
		ResultSet rs = null;
        sql.append("SELECT * FROM ").append(getTableName());
		sql.append(" WHERE TOURNAMENTID = ").append(TournamentId);
		try {
			rs = adapter.executeQuery(sql.toString());
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
				oTournamentDetails.setFirstMatchRoundDate(rs.getTimestamp("FirstMatchRoundDate"));
				oTournamentDetails.setFirstMatchRoundDate(rs.getTimestamp("NextMatchRoundDate"));
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
			sql.append(oTournamentDetails.getTournamentId() + ", '");
			sql.append(oTournamentDetails.getName() + "', ");
			sql.append(oTournamentDetails.getTournamentType() +", ");
			sql.append(oTournamentDetails.getSeason() +", ");
			if (oTournamentDetails.getLogoUrl() == null)
			{
				sql.append("null, ");
			}
			else
			{
				sql.append(oTournamentDetails.getLogoUrl() +", ");
			}
			sql.append(oTournamentDetails.getTrophyType() +", ");
			sql.append(oTournamentDetails.getNumberOfTeams() +", ");
			sql.append(oTournamentDetails.getNumberOfGroups() +", ");
			sql.append(oTournamentDetails.getLastMatchRound() +", ");
			sql.append(DateToSQLtimeStamp(oTournamentDetails.getFirstMatchRoundDate())+", ");
			sql.append(DateToSQLtimeStamp(oTournamentDetails.getNextMatchRoundDate())+", ");

			if (oTournamentDetails.getMatchesOngoing())
			{
				sql.append("true, ");
			}
			else
			{
				sql.append("false, ");
			}
			sql.append(oTournamentDetails.getCreator_UserId() +", ");
			sql.append(oTournamentDetails.getCreator_Loginname());
			sql.append(")");
			adapter.executeUpdate(sql.toString());
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),
						"DB.storeMatchKurzInfos Error" + e);
				HOLogger.instance().log(getClass(), e);
			}

	}

}
