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

	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
		return new PreparedSelectStatementBuilder(this," WHERE TOURNAMENTID = ?" );
	}

	public TournamentDetails getTournamentDetails(int tournamentId)
	{
		TournamentDetails oTournamentDetails = null;
		try {
			var rs = executePreparedSelect(tournamentId);
			assert rs != null;
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
	void storeTournamentDetails(TournamentDetails details) {
		StringBuilder sql = new StringBuilder(200);

		try {
			executePreparedInsert(
					details.getTournamentId(),
					details.getName(),
					details.getTournamentType(),
					details.getSeason(),
					details.getLogoUrl(),
					details.getTrophyType(),
					details.getNumberOfTeams(),
					details.getNumberOfGroups(),
					details.getLastMatchRound(),
					HODateTime.toDbTimestamp(details.getFirstMatchRoundDate()),
					HODateTime.toDbTimestamp(details.getNextMatchRoundDate()),
					details.getMatchesOngoing(),
					details.getCreator_UserId(),
					details.getCreator_Loginname()
			);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DB.storeTournamentDetails Error" + e);
			HOLogger.instance().log(getClass(), e);
		}

	}

}
