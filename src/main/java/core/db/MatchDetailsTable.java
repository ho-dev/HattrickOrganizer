package core.db;

import core.model.match.MatchEvent;
import core.model.enums.MatchType;
import core.model.match.Matchdetails;
import core.model.match.SourceSystem;
import core.util.HODateTime;
import core.util.HOLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

final class MatchDetailsTable extends AbstractTable {

	public final static String TABLENAME = "MATCHDETAILS";

	protected MatchDetailsTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}
	
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[] {
				new ColumnDescriptor("MatchID", Types.INTEGER, false),
				new ColumnDescriptor("MatchTyp", Types.INTEGER, false),
				new ColumnDescriptor("ArenaId", Types.INTEGER, false),
				new ColumnDescriptor("ArenaName", Types.VARCHAR, false, 256),
				new ColumnDescriptor("Fetchdatum", Types.TIMESTAMP, false),
				new ColumnDescriptor("GastName", Types.VARCHAR, false, 256),
				new ColumnDescriptor("GastID", Types.INTEGER, false),
				new ColumnDescriptor("GastEinstellung", Types.INTEGER, false),
				new ColumnDescriptor("GastTore", Types.INTEGER, false),
				new ColumnDescriptor("GastLeftAtt", Types.INTEGER, false),
				new ColumnDescriptor("GastLeftDef", Types.INTEGER, false),
				new ColumnDescriptor("GastMidAtt", Types.INTEGER, false),
				new ColumnDescriptor("GastMidDef", Types.INTEGER, false),
				new ColumnDescriptor("GastMidfield", Types.INTEGER, false),
				new ColumnDescriptor("GastRightAtt", Types.INTEGER, false),
				new ColumnDescriptor("GastRightDef", Types.INTEGER, false),
				new ColumnDescriptor("GastTacticSkill", Types.INTEGER, false),
				new ColumnDescriptor("GastTacticType", Types.INTEGER, false),
				new ColumnDescriptor("GASTHATSTATS", Types.INTEGER, false),
				new ColumnDescriptor("HeimName", Types.VARCHAR, false, 256),
				new ColumnDescriptor("HeimId", Types.INTEGER, false),
				new ColumnDescriptor("HeimEinstellung", Types.INTEGER, false),
				new ColumnDescriptor("HeimTore", Types.INTEGER, false),
				new ColumnDescriptor("HeimLeftAtt", Types.INTEGER, false),
				new ColumnDescriptor("HeimLeftDef", Types.INTEGER, false),
				new ColumnDescriptor("HeimMidAtt", Types.INTEGER, false),
				new ColumnDescriptor("HeimMidDef", Types.INTEGER, false),
				new ColumnDescriptor("HeimMidfield", Types.INTEGER, false),
				new ColumnDescriptor("HeimRightAtt", Types.INTEGER, false),
				new ColumnDescriptor("HeimRightDef", Types.INTEGER, false),
				new ColumnDescriptor("HeimTacticSkill", Types.INTEGER, false),
				new ColumnDescriptor("HeimTacticType", Types.INTEGER, false),
				new ColumnDescriptor("HEIMHATSTATS", Types.INTEGER, false),
				new ColumnDescriptor("SpielDatum", Types.TIMESTAMP, false),
				new ColumnDescriptor("WetterId", Types.INTEGER, false),
				new ColumnDescriptor("Zuschauer", Types.INTEGER, false),
				new ColumnDescriptor("Matchreport", Types.VARCHAR, false, 20000),
				new ColumnDescriptor("RegionID", Types.INTEGER, false),
				new ColumnDescriptor("soldTerraces", Types.INTEGER, false),
				new ColumnDescriptor("soldBasic", Types.INTEGER, false),
				new ColumnDescriptor("soldRoof", Types.INTEGER, false),
				new ColumnDescriptor("soldVIP", Types.INTEGER, false),
				new ColumnDescriptor("RatingIndirectSetPiecesDef", Types.INTEGER, true),
				new ColumnDescriptor("RatingIndirectSetPiecesAtt", Types.INTEGER, true),
				new ColumnDescriptor("HomeGoal0", Types.INTEGER, true),
				new ColumnDescriptor("HomeGoal1", Types.INTEGER, true),
				new ColumnDescriptor("HomeGoal2", Types.INTEGER, true),
				new ColumnDescriptor("HomeGoal3", Types.INTEGER, true),
				new ColumnDescriptor("HomeGoal4", Types.INTEGER, true),
				new ColumnDescriptor("GuestGoal0", Types.INTEGER, true),
				new ColumnDescriptor("GuestGoal1", Types.INTEGER, true),
				new ColumnDescriptor("GuestGoal2", Types.INTEGER, true),
				new ColumnDescriptor("GuestGoal3", Types.INTEGER, true),
				new ColumnDescriptor("GuestGoal4", Types.INTEGER, true),
				new ColumnDescriptor("HomeFormation", Types.VARCHAR, true, 5),
				new ColumnDescriptor("AwayFormation", Types.VARCHAR, true, 5)
		};
	}

	@Override
	protected  String[] getConstraintStatements() {
		return new String[]{
			"  PRIMARY KEY (MATCHID, MATCHTYP)"
		};
	}
	
	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
				"CREATE INDEX IMATCHDETAILS_1 ON " + getTableName() + "(MatchID)",
				"CREATE INDEX matchdetails_heimid_idx ON " + getTableName() + " (HeimId)",
				"CREATE INDEX matchdetails_gastid_idx ON " + getTableName() + " (GastID)"
		};
	}

	@Override
	protected PreparedStatement createDeleteStatement(){
		return createDeleteStatement("WHERE MATCHTYP=? AND MATCHID=?");
	}
	@Override
	protected  PreparedStatement createSelectStatement(){
		return createSelectStatement("WHERE MATCHTYP=? AND MATCHID=?");
	}

	/**
	 * Gibt die MatchDetails zu einem Match zurück
	 */
	Matchdetails loadMatchDetails(int iMatchType, int matchId) {
		final Matchdetails details = new Matchdetails();

		try {
			ResultSet rs = executePreparedSelect(iMatchType, matchId);
			assert rs != null;
			if (rs.first()) {
				details.setMatchType(MatchType.getById(rs.getInt("MATCHTYP")));
				details.setArenaID(rs.getInt("ArenaId"));
				details.setArenaName(rs.getString("ArenaName"));
				details.setRegionId(rs.getInt("RegionID"));
				details.setFetchDatum(HODateTime.fromDbTimestamp(rs.getTimestamp("Fetchdatum")));
				details.setGastId(rs.getInt("GastId"));
				details.setGastName(rs.getString("GastName"));
				details.setGuestEinstellung(rs.getInt("GastEinstellung"));
				details.setGuestGoals(rs.getInt("GastTore"));
				details.setGuestLeftAtt(rs.getInt("GastLeftAtt"));
				details.setGuestLeftDef(rs.getInt("GastLeftDef"));
				details.setGuestMidAtt(rs.getInt("GastMidAtt"));
				details.setGuestMidDef(rs.getInt("GastMidDef"));
				details.setGuestMidfield(rs.getInt("GastMidfield"));
				details.setGuestRightAtt(rs.getInt("GastRightAtt"));
				details.setGuestRightDef(rs.getInt("GastRightDef"));
				details.setGuestTacticSkill(rs.getInt("GastTacticSkill"));
				details.setGuestTacticType(rs.getInt("GastTacticType"));
				details.setHeimId(rs.getInt("HeimId"));
				details.setHeimName(rs.getString("HeimName"));
				details.setHomeEinstellung(rs.getInt("HeimEinstellung"));
				details.setHomeGoals(rs.getInt("HeimTore"));
				details.setHomeLeftAtt(rs.getInt("HeimLeftAtt"));
				details.setHomeLeftDef(rs.getInt("HeimLeftDef"));
				details.setHomeMidAtt(rs.getInt("HeimMidAtt"));
				details.setHomeMidDef(rs.getInt("HeimMidDef"));
				details.setHomeMidfield(rs.getInt("HeimMidfield"));
				details.setHomeRightAtt(rs.getInt("HeimRightAtt"));
				details.setHomeRightDef(rs.getInt("HeimRightDef"));
				details.setHomeTacticSkill(rs.getInt("HeimTacticSkill"));
				details.setHomeTacticType(rs.getInt("HeimTacticType"));
				details.setMatchID(matchId);
				details.setSpielDatum(HODateTime.fromDbTimestamp(rs.getTimestamp("SpielDatum")));
				details.setWetterId(rs.getInt("WetterId"));
				details.setZuschauer(rs.getInt("Zuschauer"));
				details.setSoldTerraces(rs.getInt("soldTerraces"));
				details.setSoldBasic(rs.getInt("soldBasic"));
				details.setSoldRoof(rs.getInt("soldRoof"));
				details.setSoldVIP(rs.getInt("soldVIP"));
				details.setMatchreport(rs.getString("Matchreport"));
				details.setRatingIndirectSetPiecesAtt(rs.getInt("RatingIndirectSetPiecesAtt"));
				details.setRatingIndirectSetPiecesDef(rs.getInt("RatingIndirectSetPiecesDef"));
				var homeGoalsInPart = new Integer[]{
						DBManager.getInteger(rs, "HomeGoal0"),
						DBManager.getInteger(rs, "HomeGoal1"),
						DBManager.getInteger(rs, "HomeGoal2"),
						DBManager.getInteger(rs, "HomeGoal3"),
						DBManager.getInteger(rs, "HomeGoal4")
				};
				var guestGoalsInPart = new Integer[]{
						DBManager.getInteger(rs, "GuestGoal0"),
						DBManager.getInteger(rs, "GuestGoal1"),
						DBManager.getInteger(rs, "GuestGoal2"),
						DBManager.getInteger(rs, "GuestGoal3"),
						DBManager.getInteger(rs, "GuestGoal4")
				};
				if (hasValues(homeGoalsInPart)) {
					details.setHomeGoalsInPart(homeGoalsInPart);
				} else {
					details.setHomeGoalsInPart(null);
				}
				if (hasValues(guestGoalsInPart)){
					details.setGuestGoalsInPart(guestGoalsInPart);
				} else {
					details.setGuestGoalsInPart(null);
				}

				details.setHomeFormation(rs.getString("HomeFormation"));
				details.setAwayFormation(rs.getString("AwayFormation"));
				details.setStatisics();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getMatchDetails : " + e);
			HOLogger.instance().log(getClass(),e);
		}

		return details;
	}

	private boolean hasValues(Integer[] goalsInPart) {
		for( var i : goalsInPart){
			if ( i != null) return true;
		}
		return false;
	}


	/**
	 * speichert die MatchDetails
	 */
	void storeMatchDetails(Matchdetails details) {
		if (details != null) {
			//Remove existing entries
			try {

				executePreparedDelete(details.getMatchType().getId(), details.getMatchID());
				executePreparedInsert(
						details.getMatchID(),
						details.getMatchType().getId(),
						details.getArenaID(),
						details.getArenaName(),
						details.getFetchDatum(),
						details.getGuestTeamName(),
						details.getGuestTeamId(),
						details.getGuestEinstellung(),
						details.getGuestGoals(),
						details.getGuestLeftAtt(),
						details.getGuestLeftDef(),
						details.getGuestMidAtt(),
						details.getGuestMidDef(),
						details.getGuestMidfield(),
						details.getGuestRightAtt(),
						details.getGuestRightDef(),
						details.getGuestTacticSkill(),
						details.getGuestTacticType(),
						details.getGuestHatStats(),
						details.getHomeTeamName(),
						details.getHomeTeamId(),
						details.getHomeEinstellung(),
						details.getHomeGoals(),
						details.getHomeLeftAtt(),
						details.getHomeLeftDef(),
						details.getHomeMidAtt(),
						details.getHomeMidDef(),
						details.getHomeMidfield(),
						details.getHomeRightAtt(),
						details.getHomeRightDef(),
						details.getHomeTacticSkill(),
						details.getHomeTacticType(),
						details.getHomeHatStats(),
						details.getMatchDate().toDbTimestamp(),
						details.getWetterId(),
						details.getZuschauer(),
						details.getMatchreport(),
						details.getRegionId(),
						details.getSoldTerraces(),
						details.getSoldBasic(),
						details.getSoldRoof(),
						details.getSoldVIP(),
						details.getRatingIndirectSetPiecesDef(),
						details.getRatingIndirectSetPiecesAtt(),
						details.getHomeGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED),
						details.getHomeGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF),
						details.getHomeGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF),
						details.getHomeGoalsInPart(MatchEvent.MatchPartId.OVERTIME),
						details.getHomeGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST),
						details.getGuestGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED),
						details.getGuestGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF),
						details.getGuestGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF),
						details.getGuestGoalsInPart(MatchEvent.MatchPartId.OVERTIME),
						details.getGuestGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST),
						details.getFormation(true),
						details.getFormation(false)
				);

				//Store Match Events
				((MatchHighlightsTable) DBManager.instance().getTable(MatchHighlightsTable.TABLENAME))
						.storeMatchHighlights(details);

				// MatchKurzInfo should be set correctly in OnlineWorker.downloadMatchData now
				// no workaround is necessary anymore

			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DB.storeMatchDetails Error" + e);
				HOLogger.instance().log(getClass(), e);
			}
		}
	}

	private PreparedStatement isMatchIFKRatingAvailableStatement;
	private PreparedStatement getIsMatchIFKRatingAvailableStatement(){
		if (isMatchIFKRatingAvailableStatement==null ){
			isMatchIFKRatingAvailableStatement = adapter.createPreparedStatement("SELECT RatingIndirectSetPiecesDef FROM " + getTableName() + " WHERE MatchId=?");
		}
		return isMatchIFKRatingAvailableStatement;
	}
	public boolean isMatchIFKRatingAvailable(int matchId){
		try {
			final ResultSet rs = adapter.executePreparedQuery(getIsMatchIFKRatingAvailableStatement(), matchId);
			assert rs != null;
			rs.beforeFirst();
			if (rs.next()) {
				int rating = rs.getInt(1);
				return !rs.wasNull();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DatenbankZugriff.isMatchIFKRatingAvailable : " + e);
		}
		return false;
	}

	static private String placeHolderYouthMatchTypes;
	static private String getPlaceHolderYouthMatchTypes(){
		if ( placeHolderYouthMatchTypes==null){
			var youthMatchTypes = MatchType.getYouthMatchType();
			var sep = "(";
			var placeHolders = new StringBuilder();
			for ( var t : youthMatchTypes){
				placeHolders.append(sep).append("?");
				sep=",";
			}
			placeHolders.append(")");
			placeHolderYouthMatchTypes = placeHolders.toString();
		}
		return placeHolderYouthMatchTypes;
	}

	private PreparedStatement deleteYouthMatchDetailsBeforeStatement;
	private PreparedStatement getDeleteYouthMatchDetailsBeforeStatement(){
		if(deleteYouthMatchDetailsBeforeStatement==null){
			deleteYouthMatchDetailsBeforeStatement = adapter.createPreparedStatement("DELETE FROM " + getTableName() + " WHERE MATCHTYP IN " + getPlaceHolderYouthMatchTypes() + " AND SPIELDATUM<?");
		}
		return deleteYouthMatchDetailsBeforeStatement;
	}
	public void deleteYouthMatchDetailsBefore(Timestamp before) {
		try {
			adapter.executePreparedUpdate(getDeleteYouthMatchDetailsBeforeStatement(), MatchType.getYouthMatchType().toArray(), before);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupsBefore Error" + e);
		}
	}

	private PreparedStatement getLastYouthMatchDateStatement;
	private PreparedStatement getGetLastYouthMatchDateStatement(){
		if ( getLastYouthMatchDateStatement==null){
			getLastYouthMatchDateStatement=adapter.createPreparedStatement("select max(SpielDatum) from " + getTableName() + " WHERE MATCHTYP IN " + getPlaceHolderYouthMatchTypes());
		}
		return getLastYouthMatchDateStatement;
	}
	public Timestamp getLastYouthMatchDate() {
		try {
			var rs = adapter.executePreparedQuery(getGetLastYouthMatchDateStatement(), MatchType.getYouthMatchType().toArray());
			assert rs != null;
			rs.beforeFirst();
			if ( rs.next()){
				return rs.getTimestamp(1);
			}
		}
		catch (Exception ignored){

		}
		return null;
	}
}
