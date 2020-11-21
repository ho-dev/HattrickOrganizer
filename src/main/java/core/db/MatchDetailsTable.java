package core.db;

import core.model.match.MatchEvent;
import core.model.match.Matchdetails;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
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
		columns = new ColumnDescriptor[53];
		columns[0]= new ColumnDescriptor("MatchID",Types.INTEGER,false,true);
		columns[1]= new ColumnDescriptor("ArenaId",Types.INTEGER,false);
		columns[2]= new ColumnDescriptor("ArenaName",Types.VARCHAR,false,256);
		columns[3]= new ColumnDescriptor("Fetchdatum",Types.TIMESTAMP,false);
		columns[4]= new ColumnDescriptor("GastName",Types.VARCHAR,false,256);
		columns[5]= new ColumnDescriptor("GastID",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("GastEinstellung",Types.INTEGER,false);
		columns[7]= new ColumnDescriptor("GastTore",Types.INTEGER,false);
		columns[8]= new ColumnDescriptor("GastLeftAtt",Types.INTEGER,false);
		columns[9]= new ColumnDescriptor("GastLeftDef",Types.INTEGER,false);
		columns[10]= new ColumnDescriptor("GastMidAtt",Types.INTEGER,false);
		columns[11]= new ColumnDescriptor("GastMidDef",Types.INTEGER,false);
		columns[12]= new ColumnDescriptor("GastMidfield",Types.INTEGER,false);
		columns[13]= new ColumnDescriptor("GastRightAtt",Types.INTEGER,false);
		columns[14]= new ColumnDescriptor("GastRightDef",Types.INTEGER,false);
		columns[15]= new ColumnDescriptor("GastTacticSkill",Types.INTEGER,false);
		columns[16]= new ColumnDescriptor("GastTacticType",Types.INTEGER,false);
		columns[17]= new ColumnDescriptor("GASTHATSTATS",Types.INTEGER,false);
		columns[18]= new ColumnDescriptor("HeimName",Types.VARCHAR,false,256);
		columns[19]= new ColumnDescriptor("HeimId",Types.INTEGER,false);
		columns[20]= new ColumnDescriptor("HeimEinstellung",Types.INTEGER,false);
		columns[21]= new ColumnDescriptor("HeimTore",Types.INTEGER,false);
		columns[22]= new ColumnDescriptor("HeimLeftAtt",Types.INTEGER,false);
		columns[23]= new ColumnDescriptor("HeimLeftDef",Types.INTEGER,false);
		columns[24]= new ColumnDescriptor("HeimMidAtt",Types.INTEGER,false);
		columns[25]= new ColumnDescriptor("HeimMidDef",Types.INTEGER,false);
		columns[26]= new ColumnDescriptor("HeimMidfield",Types.INTEGER,false);
		columns[27]= new ColumnDescriptor("HeimRightAtt",Types.INTEGER,false);
		columns[28]= new ColumnDescriptor("HeimRightDef",Types.INTEGER,false);
		columns[29]= new ColumnDescriptor("HeimTacticSkill",Types.INTEGER,false);
		columns[30]= new ColumnDescriptor("HeimTacticType",Types.INTEGER,false);
		columns[31]= new ColumnDescriptor("HEIMHATSTATS",Types.INTEGER,false);
		columns[32]= new ColumnDescriptor("SpielDatum",Types.TIMESTAMP,false);
		columns[33]= new ColumnDescriptor("WetterId",Types.INTEGER,false);
		columns[34]= new ColumnDescriptor("Zuschauer",Types.INTEGER,false);
		columns[35]= new ColumnDescriptor("Matchreport",Types.VARCHAR,false,20000);
		columns[36]= new ColumnDescriptor("RegionID",Types.INTEGER,false);
		columns[37]= new ColumnDescriptor("soldTerraces",Types.INTEGER,false);
		columns[38]= new ColumnDescriptor("soldBasic",Types.INTEGER,false);
		columns[39]= new ColumnDescriptor("soldRoof",Types.INTEGER,false);
		columns[40]= new ColumnDescriptor("soldVIP",Types.INTEGER,false);
		columns[41]= new ColumnDescriptor("RatingIndirectSetPiecesDef",Types.INTEGER,true);
		columns[42]= new ColumnDescriptor("RatingIndirectSetPiecesAtt",Types.INTEGER,true);
		columns[43]= new ColumnDescriptor("HomeGoal0",Types.INTEGER,true);
		columns[44]= new ColumnDescriptor("HomeGoal1",Types.INTEGER,true);
		columns[45]= new ColumnDescriptor("HomeGoal2",Types.INTEGER,true);
		columns[46]= new ColumnDescriptor("HomeGoal3",Types.INTEGER,true);
		columns[47]= new ColumnDescriptor("HomeGoal4",Types.INTEGER,true);
		columns[48]= new ColumnDescriptor("GuestGoal0",Types.INTEGER,true);
		columns[49]= new ColumnDescriptor("GuestGoal1",Types.INTEGER,true);
		columns[50]= new ColumnDescriptor("GuestGoal2",Types.INTEGER,true);
		columns[51]= new ColumnDescriptor("GuestGoal3",Types.INTEGER,true);
		columns[52]= new ColumnDescriptor("GuestGoal4",Types.INTEGER,true);
	}
	
	@Override
	protected String[] getCreateIndexStatement() {
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
					"INSERT INTO "+getTableName()+" ( MatchID, ArenaId, ArenaName, Fetchdatum, GastId, GastName, GastEinstellung, GastTore, "
						+ "GastLeftAtt, GastLeftDef, GastMidAtt, GastMidDef, GastMidfield, GastRightAtt, GastRightDef, GASTHATSTATS, GastTacticSkill, GastTacticType, "
						+ "HeimId, HeimName, HeimEinstellung, HeimTore, HeimLeftAtt, HeimLeftDef, HeimMidAtt, HeimMidDef, HeimMidfield, HeimRightAtt, HeimRightDef, HEIMHATSTATS, "
						+ "HeimTacticSkill, HeimTacticType, SpielDatum, WetterId, Zuschauer, "
						+ "Matchreport, RegionID, soldTerraces, soldBasic, soldRoof, soldVIP, "
						+ "RatingIndirectSetPiecesAtt, RatingIndirectSetPiecesDef, "
						+ "HomeGoal0, HomeGoal1, HomeGoal2, HomeGoal3, HomeGoal4, "
						+ "GuestGoal0, GuestGoal1, GuestGoal2, GuestGoal3, GuestGoal4 "
						+ ") VALUES ("
						+ details.getMatchID()
						+ ", "
						+ details.getArenaID()
						+ ", '"
						+ DBManager.insertEscapeSequences(details.getArenaName())
						+ "', '"
						+ details.getFetchDatum().toString()
						+ "', "
						+ details.getGastId()
						+ ", '"
						+ DBManager.insertEscapeSequences(details.getGastName())
						+ "', "
						+ details.getGuestEinstellung()
						+ ", "
						+ details.getGuestGoals()
						+ ", "
						+ details.getGuestLeftAtt()
						+ ", "
						+ details.getGuestLeftDef()
						+ ", "
						+ details.getGuestMidAtt()
						+ ", "
						+ details.getGuestMidDef()
						+ ", "
						+ details.getGuestMidfield()
						+ ", "
						+ details.getGuestRightAtt()
						+ ", "
						+ details.getGuestRightDef()
						+ ", "
						+ details.getGuestHatStats()
						+ ", "
						+ details.getGuestTacticSkill()
						+ ", "
						+ details.getGuestTacticType()
						+ ", "
						+ details.getHeimId()
						+ ", '"
						+ DBManager.insertEscapeSequences(details.getHeimName())
						+ "', "
						+ details.getHomeEinstellung()
						+ ", "
						+ details.getHomeGoals()
						+ ", "
						+ details.getHomeLeftAtt()
						+ ", "
						+ details.getHomeLeftDef()
						+ ", "
						+ details.getHomeMidAtt()
						+ ", "
						+ details.getHomeMidDef()
						+ ", "
						+ details.getHomeMidfield()
						+ ", "
						+ details.getHomeRightAtt()
						+ ", "
						+ details.getHomeRightDef()
						+ ", "
						+ details.getHomeHatStats()
						+ ", "
						+ details.getHomeTacticSkill()
						+ ", "
						+ details.getHomeTacticType()
						+ ", '"
						+ details.getSpielDatum().toString()
						+ "', "
						+ details.getWetterId()
						+ ", "
						+ details.getZuschauer()
						+ ", '"
						+ DBManager.insertEscapeSequences(details.getMatchreport())
						+ "', "
						+ details.getRegionId()
						+ ", "
						+ details.getSoldTerraces()
						+ ", "
						+ details.getSoldBasic()
						+ ", "
						+ details.getSoldRoof()
						+ ", "
						+ details.getSoldVIP()
						+ ", "
						+ details.getRatingIndirectSetPiecesAtt()
						+ ", "
						+ details.getRatingIndirectSetPiecesDef()
						+ ", "
						+ details.getHomeGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED)
						+ ", "
						+ details.getHomeGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF)
						+ ", "
						+ details.getHomeGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF)
						+ ", "
						+ details.getHomeGoalsInPart(MatchEvent.MatchPartId.OVERTIME)
						+ ", "
						+ details.getHomeGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST)
						+ ", "
						+ details.getGuestGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED)
						+ ", "
						+ details.getGuestGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF)
						+ ", "
						+ details.getGuestGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF)
						+ ", "
						+ details.getGuestGoalsInPart(MatchEvent.MatchPartId.OVERTIME)
						+ ", "
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
