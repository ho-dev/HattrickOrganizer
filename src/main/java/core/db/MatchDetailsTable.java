package core.db;

import core.model.match.MatchEvent;
import core.model.match.Matchdetails;
import core.model.match.SourceSystem;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

import static core.db.DbUtil.getNullableInt;

final class MatchDetailsTable extends AbstractTable {

	public final static String TABLENAME = "MATCHDETAILS";

	protected MatchDetailsTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}
	
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[] {
				new ColumnDescriptor("MatchID", Types.INTEGER, false, true),
				new ColumnDescriptor("SourceSystem", Types.INTEGER, false),
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
				new ColumnDescriptor("GuestGoal4", Types.INTEGER, true)
		};
	}
	
	@Override
	protected String[] getCreateIndizeStatements() {
		return new String[] {
				"CREATE INDEX IMATCHDETAILS_1 ON " + getTableName() + "(" + columns[0].getColumnName() + ")",
				"CREATE INDEX matchdetails_heimid_idx ON " + getTableName() + " (" + columns[19].getColumnName() + ")",
				"CREATE INDEX matchdetails_gastid_idx ON " + getTableName() + " (" + columns[5].getColumnName() + ")"
		};
	}
	
	/**
	 * Gibt die MatchDetails zu einem Match zurück
	 */
	Matchdetails getMatchDetails(int matchId) {
		final Matchdetails details = new Matchdetails();

		try {
			String sql = "SELECT * FROM "+getTableName()+" WHERE MatchID=" + matchId;
			ResultSet rs = adapter.executeQuery(sql);

			if (rs.first()) {
				details.setSourceSystem(SourceSystem.getById(rs.getInt("SourceSystem")));
				details.setArenaID(rs.getInt("ArenaId"));
				details.setArenaName(core.db.DBManager.deleteEscapeSequences(rs.getString("ArenaName")));
				details.setRegionId(rs.getInt("RegionID"));
				details.setFetchDatum(rs.getTimestamp("Fetchdatum"));
				details.setGastId(rs.getInt("GastId"));
				details.setGastName(core.db.DBManager.deleteEscapeSequences(rs.getString("GastName")));
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
				details.setHeimName(core.db.DBManager.deleteEscapeSequences(rs.getString("HeimName")));
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
				details.setSpielDatum(rs.getTimestamp("SpielDatum"));
				details.setWetterId(rs.getInt("WetterId"));
				details.setZuschauer(rs.getInt("Zuschauer"));
				details.setSoldTerraces(rs.getInt("soldTerraces"));
				details.setSoldBasic(rs.getInt("soldBasic"));
				details.setSoldRoof(rs.getInt("soldRoof"));
				details.setSoldVIP(rs.getInt("soldVIP"));
				details.setMatchreport(DBManager.deleteEscapeSequences(rs.getString("Matchreport")));
				details.setRatingIndirectSetPiecesAtt(rs.getInt("RatingIndirectSetPiecesAtt"));
				details.setRatingIndirectSetPiecesDef(rs.getInt("RatingIndirectSetPiecesDef"));
				var homeGoalsInPart = new Integer[]{
						getNullableInt(rs, "HomeGoal0"),
						getNullableInt(rs, "HomeGoal1"),
						getNullableInt(rs, "HomeGoal2"),
						getNullableInt(rs, "HomeGoal3"),
						getNullableInt(rs, "HomeGoal4")
				};
				var guestGoalsInPart = new Integer[]{
						getNullableInt(rs, "GuestGoal0"),
						getNullableInt(rs, "GuestGoal1"),
						getNullableInt(rs, "GuestGoal2"),
						getNullableInt(rs, "GuestGoal3"),
						getNullableInt(rs, "GuestGoal4")
				};
				if ( hasValues(homeGoalsInPart)){
					details.setHomeGoalsInPart(homeGoalsInPart);
				}
				else {
					details.setHomeGoalsInPart(null);
				}
				if ( hasValues(guestGoalsInPart)){
					details.setGuestGoalsInPart(guestGoalsInPart);
				}
				else {
					details.setGuestGoalsInPart(null);
				}
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

			final String[] where = { "MatchID" };
			final String[] werte = { "" + details.getMatchID()};

			//Remove existing entries
			delete(where, werte);

			String sql;

			try {
				sql =
					"INSERT INTO "+getTableName()+" ( MatchID, SourceSystem, ArenaId, ArenaName, Fetchdatum, GastId, GastName, GastEinstellung, GastTore, "
						+ "GastLeftAtt, GastLeftDef, GastMidAtt, GastMidDef, GastMidfield, GastRightAtt, GastRightDef, GASTHATSTATS, GastTacticSkill, GastTacticType, "
						+ "HeimId, HeimName, HeimEinstellung, HeimTore, HeimLeftAtt, HeimLeftDef, HeimMidAtt, HeimMidDef, HeimMidfield, HeimRightAtt, HeimRightDef, HEIMHATSTATS, "
						+ "HeimTacticSkill, HeimTacticType, SpielDatum, WetterId, Zuschauer, "
						+ "Matchreport, RegionID, soldTerraces, soldBasic, soldRoof, soldVIP, "
						+ "RatingIndirectSetPiecesAtt, RatingIndirectSetPiecesDef, "
						+ "HomeGoal0, HomeGoal1, HomeGoal2, HomeGoal3, HomeGoal4, "
						+ "GuestGoal0, GuestGoal1, GuestGoal2, GuestGoal3, GuestGoal4 "
						+ ") VALUES ("
						+ details.getMatchID()
						+ ","
						+ details.getSourceSystem().getId()
						+ ","
						+ details.getArenaID()
						+ ",'"
						+ DBManager.insertEscapeSequences(details.getArenaName())
						+ "','"
						+ details.getFetchDatum().toString()
						+ "',"
						+ details.getGastId()
						+ ",'"
						+ DBManager.insertEscapeSequences(details.getGastName())
						+ "',"
						+ details.getGuestEinstellung()
						+ ","
						+ details.getGuestGoals()
						+ ","
						+ details.getGuestLeftAtt()
						+ ","
						+ details.getGuestLeftDef()
						+ ","
						+ details.getGuestMidAtt()
						+ ","
						+ details.getGuestMidDef()
						+ ","
						+ details.getGuestMidfield()
						+ ","
						+ details.getGuestRightAtt()
						+ ","
						+ details.getGuestRightDef()
						+ ","
						+ details.getGuestHatStats()
						+ ","
						+ details.getGuestTacticSkill()
						+ ","
						+ details.getGuestTacticType()
						+ ","
						+ details.getHeimId()
						+ ",'"
						+ DBManager.insertEscapeSequences(details.getHeimName())
						+ "',"
						+ details.getHomeEinstellung()
						+ ","
						+ details.getHomeGoals()
						+ ","
						+ details.getHomeLeftAtt()
						+ ","
						+ details.getHomeLeftDef()
						+ ","
						+ details.getHomeMidAtt()
						+ ","
						+ details.getHomeMidDef()
						+ ","
						+ details.getHomeMidfield()
						+ ","
						+ details.getHomeRightAtt()
						+ ","
						+ details.getHomeRightDef()
						+ ","
						+ details.getHomeHatStats()
						+ ","
						+ details.getHomeTacticSkill()
						+ ","
						+ details.getHomeTacticType()
						+ ",'"
						+ details.getSpielDatum().toString()
						+ "',"
						+ details.getWetterId()
						+ ","
						+ details.getZuschauer()
						+ ",'"
						+ DBManager.insertEscapeSequences(details.getMatchreport())
						+ "',"
						+ details.getRegionId()
						+ ","
						+ details.getSoldTerraces()
						+ ","
						+ details.getSoldBasic()
						+ ","
						+ details.getSoldRoof()
						+ ","
						+ details.getSoldVIP()
						+ ","
						+ details.getRatingIndirectSetPiecesAtt()
						+ ","
						+ details.getRatingIndirectSetPiecesDef()
						+ ","
						+ details.getHomeGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED)
						+ ","
						+ details.getHomeGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF)
						+ ","
						+ details.getHomeGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF)
						+ ","
						+ details.getHomeGoalsInPart(MatchEvent.MatchPartId.OVERTIME)
						+ ","
						+ details.getHomeGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST)
						+ ","
						+ details.getGuestGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED)
						+ ","
						+ details.getGuestGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF)
						+ ","
						+ details.getGuestGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF)
						+ ","
						+ details.getGuestGoalsInPart(MatchEvent.MatchPartId.OVERTIME)
						+ ","
						+ details.getGuestGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST)
						+ ")";

				adapter.executeUpdate(sql);

				//Store Match Events
				((MatchHighlightsTable) DBManager.instance().getTable(MatchHighlightsTable.TABLENAME))
											.storeMatchHighlights(details);

				//Workaround if the game is not set to Finished in MatchKurzInfos
				if (details.getZuschauer() > 0) {
					//Game is definitely done!
					sql = "UPDATE MATCHESKURZINFO SET Status=1, HeimTore=" + details.getHomeGoals() + " , GastTore=" + details.getGuestGoals() + "  WHERE MatchID=" + details.getMatchID();
					adapter.executeUpdate(sql);
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"DB.storeMatchDetails Error" + e);
				HOLogger.instance().log(getClass(),e);
			}
		}
	}

	public boolean isMatchIFKRatingAvailable(int matchId){
		try {
			final String sql = "SELECT RatingIndirectSetPiecesDef FROM " + getTableName() + " WHERE MatchId=" + matchId;
			final ResultSet rs = adapter.executeQuery(sql);
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


}
