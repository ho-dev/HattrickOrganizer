package core.db;

import core.model.match.MatchEvent;
import core.model.enums.MatchType;
import core.model.match.Matchdetails;
import core.util.HODateTime;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.stream.Collectors;

final class MatchDetailsTable extends AbstractTable {

	public final static String TABLENAME = "MATCHDETAILS";

	MatchDetailsTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
		idColumns = 2;
	}
	
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[] {
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchID").setGetter((o) -> ((Matchdetails) o).getMatchID()).setSetter((o, v) -> ((Matchdetails) o).setMatchID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchTyp").setGetter((o) -> ((Matchdetails) o).getMatchType().getId()).setSetter((o, v) -> ((Matchdetails) o).setMatchType(MatchType.getById((int) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ArenaId").setGetter((o) -> ((Matchdetails) o).getArenaID()).setSetter((o, v) -> ((Matchdetails) o).setArenaID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ArenaName").setGetter((o) -> ((Matchdetails) o).getArenaName()).setSetter((o, v) -> ((Matchdetails) o).setArenaName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Fetchdatum").setGetter((o) -> ((Matchdetails) o).getFetchDatum().toDbTimestamp()).setSetter((o, v) -> ((Matchdetails) o).setFetchDatum((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastName").setGetter((o) -> ((Matchdetails) o).getGuestTeamName()).setSetter((o, v) -> ((Matchdetails) o).setGastName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastID").setGetter((o) -> ((Matchdetails) o).getGuestTeamId()).setSetter((o, v) -> ((Matchdetails) o).setGastId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastEinstellung").setGetter((o) -> ((Matchdetails) o).getGuestEinstellung()).setSetter((o, v) -> ((Matchdetails) o).setGuestEinstellung((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastTore").setGetter((o) -> ((Matchdetails) o).getGuestGoals()).setSetter((o, v) -> ((Matchdetails) o).setGuestGoals((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastLeftAtt").setGetter((o) -> ((Matchdetails) o).getGuestLeftAtt()).setSetter((o, v) -> ((Matchdetails) o).setGuestLeftAtt((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastLeftDef").setGetter((o) -> ((Matchdetails) o).getGuestLeftDef()).setSetter((o, v) -> ((Matchdetails) o).setGuestLeftDef((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastMidAtt").setGetter((o) -> ((Matchdetails) o).getGuestMidAtt()).setSetter((o, v) -> ((Matchdetails) o).setGuestMidAtt((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastMidDef").setGetter((o) -> ((Matchdetails) o).getGuestMidDef()).setSetter((o, v) -> ((Matchdetails) o).setGuestMidDef((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastMidfield").setGetter((o) -> ((Matchdetails) o).getGuestMidfield()).setSetter((o, v) -> ((Matchdetails) o).setGuestMidfield((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastRightAtt").setGetter((o) -> ((Matchdetails) o).getGuestRightAtt()).setSetter((o, v) -> ((Matchdetails) o).setGuestRightAtt((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastRightDef").setGetter((o) -> ((Matchdetails) o).getGuestRightDef()).setSetter((o, v) -> ((Matchdetails) o).setGuestRightDef((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastTacticSkill").setGetter((o) -> ((Matchdetails) o).getGuestTacticSkill()).setSetter((o, v) -> ((Matchdetails) o).setGuestTacticSkill((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GastTacticType").setGetter((o) -> ((Matchdetails) o).getGuestTacticType()).setSetter((o, v) -> ((Matchdetails) o).setGuestTacticType((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GASTHATSTATS").setGetter((o) -> ((Matchdetails) o).getGuestHatStats()).setSetter((o, v) -> ((Matchdetails) o).setGuestHatStats((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimName").setGetter((o) -> ((Matchdetails) o).getHomeTeamName()).setSetter((o, v) -> ((Matchdetails) o).setHeimName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimId").setGetter((o) -> ((Matchdetails) o).getHomeTeamId()).setSetter((o, v) -> ((Matchdetails) o).setHeimId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimEinstellung").setGetter((o) -> ((Matchdetails) o).getHomeEinstellung()).setSetter((o, v) -> ((Matchdetails) o).setHomeEinstellung((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimTore").setGetter((o) -> ((Matchdetails) o).getHomeGoals()).setSetter((o, v) -> ((Matchdetails) o).setHomeGoals((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimLeftAtt").setGetter((o) -> ((Matchdetails) o).getHomeLeftAtt()).setSetter((o, v) -> ((Matchdetails) o).setHomeLeftAtt((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimLeftDef").setGetter((o) -> ((Matchdetails) o).getHomeLeftDef()).setSetter((o, v) -> ((Matchdetails) o).setHomeLeftDef((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimMidAtt").setGetter((o) -> ((Matchdetails) o).getHomeMidAtt()).setSetter((o, v) -> ((Matchdetails) o).setHomeMidAtt((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimMidDef").setGetter((o) -> ((Matchdetails) o).getHomeMidDef()).setSetter((o, v) -> ((Matchdetails) o).setHomeMidDef((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimMidfield").setGetter((o) -> ((Matchdetails) o).getHomeMidfield()).setSetter((o, v) -> ((Matchdetails) o).setHomeMidfield((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimRightAtt").setGetter((o) -> ((Matchdetails) o).getHomeRightAtt()).setSetter((o, v) -> ((Matchdetails) o).setHomeRightAtt((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimRightDef").setGetter((o) -> ((Matchdetails) o).getHomeRightDef()).setSetter((o, v) -> ((Matchdetails) o).setHomeRightDef((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimTacticSkill").setGetter((o) -> ((Matchdetails) o).getHomeTacticSkill()).setSetter((o, v) -> ((Matchdetails) o).setHomeTacticSkill((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HeimTacticType").setGetter((o) -> ((Matchdetails) o).getHomeTacticType()).setSetter((o, v) -> ((Matchdetails) o).setHomeTacticType((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HEIMHATSTATS").setGetter((o) -> ((Matchdetails) o).getHomeHatStats()).setSetter((o, v) -> ((Matchdetails) o).setHomeHatStats((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SpielDatum").setGetter((o) -> ((Matchdetails) o).getMatchDate().toDbTimestamp()).setSetter((o, v) -> ((Matchdetails) o).setSpielDatum((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("WetterId").setGetter((o) -> ((Matchdetails) o).getWetterId()).setSetter((o, v) -> ((Matchdetails) o).setWetterId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Zuschauer").setGetter((o) -> ((Matchdetails) o).getZuschauer()).setSetter((o, v) -> ((Matchdetails) o).setZuschauer((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Matchreport").setGetter((o) -> ((Matchdetails) o).getMatchreport()).setSetter((o, v) -> ((Matchdetails) o).setMatchreport((String) v)).setType(Types.VARCHAR).setLength(20000).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("RegionID").setGetter((o) -> ((Matchdetails) o).getRegionId()).setSetter((o, v) -> ((Matchdetails) o).setRegionId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("soldTerraces").setGetter((o) -> ((Matchdetails) o).getSoldTerraces()).setSetter((o, v) -> ((Matchdetails) o).setSoldTerraces((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("soldBasic").setGetter((o) -> ((Matchdetails) o).getSoldBasic()).setSetter((o, v) -> ((Matchdetails) o).setSoldBasic((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("soldRoof").setGetter((o) -> ((Matchdetails) o).getSoldRoof()).setSetter((o, v) -> ((Matchdetails) o).setSoldRoof((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("soldVIP").setGetter((o) -> ((Matchdetails) o).getSoldVIP()).setSetter((o, v) -> ((Matchdetails) o).setSoldVIP((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("RatingIndirectSetPiecesDef").setGetter((o) -> ((Matchdetails) o).getRatingIndirectSetPiecesDef()).setSetter((o, v) -> ((Matchdetails) o).setRatingIndirectSetPiecesDef((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("RatingIndirectSetPiecesAtt").setGetter((o) -> ((Matchdetails) o).getRatingIndirectSetPiecesAtt()).setSetter((o, v) -> ((Matchdetails) o).setRatingIndirectSetPiecesAtt((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HomeGoal0").setGetter((o) -> ((Matchdetails) o).getHomeGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED)).setSetter((o, v) -> ((Matchdetails) o).setHomeGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HomeGoal1").setGetter((o) -> ((Matchdetails) o).getHomeGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF)).setSetter((o, v) -> ((Matchdetails) o).setHomeGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HomeGoal2").setGetter((o) -> ((Matchdetails) o).getHomeGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF)).setSetter((o, v) -> ((Matchdetails) o).setHomeGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HomeGoal3").setGetter((o) -> ((Matchdetails) o).getHomeGoalsInPart(MatchEvent.MatchPartId.OVERTIME)).setSetter((o, v) -> ((Matchdetails) o).setHomeGoalsInPart(MatchEvent.MatchPartId.OVERTIME, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HomeGoal4").setGetter((o) -> ((Matchdetails) o).getHomeGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST)).setSetter((o, v) -> ((Matchdetails) o).setHomeGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GuestGoal0").setGetter((o) -> ((Matchdetails) o).getGuestGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED)).setSetter((o, v) -> ((Matchdetails) o).setGuestGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GuestGoal1").setGetter((o) -> ((Matchdetails) o).getGuestGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF)).setSetter((o, v) -> ((Matchdetails) o).setGuestGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GuestGoal2").setGetter((o) -> ((Matchdetails) o).getGuestGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF)).setSetter((o, v) -> ((Matchdetails) o).setGuestGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GuestGoal3").setGetter((o) -> ((Matchdetails) o).getGuestGoalsInPart(MatchEvent.MatchPartId.OVERTIME)).setSetter((o, v) -> ((Matchdetails) o).setGuestGoalsInPart(MatchEvent.MatchPartId.OVERTIME, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GuestGoal4").setGetter((o) -> ((Matchdetails) o).getGuestGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST)).setSetter((o, v) -> ((Matchdetails) o).setGuestGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST, (Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HomeFormation").setGetter((o) -> ((Matchdetails) o).getFormation(true)).setSetter((o, v) -> ((Matchdetails) o).setHomeFormation((String) v)).setType(Types.VARCHAR).setLength(5).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AwayFormation").setGetter((o) -> ((Matchdetails) o).getFormation(false)).setSetter((o, v) -> ((Matchdetails) o).setAwayFormation((String) v)).setType(Types.VARCHAR).setLength(5).isNullable(true).build()
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

	Matchdetails loadMatchDetails(int iMatchType, int matchId) {
		var ret =  loadOne(Matchdetails.class, matchId, iMatchType);
		if ( ret == null){
			ret = new Matchdetails();
		}
		return ret;
	}

	void storeMatchDetails(Matchdetails details) {
		if (details != null) {
			details.setIsStored(isStored(details.getMatchID(), details.getMatchType().getId()));
			store(details);
		}
	}

	private final String isMatchIFKRatingAvailableSql = "SELECT RatingIndirectSetPiecesDef FROM " + getTableName() + " WHERE MatchId=?";

	public boolean isMatchIFKRatingAvailable(int matchId){
		try (final ResultSet rs = connectionManager.executePreparedQuery(isMatchIFKRatingAvailableSql, matchId)) {
			assert rs != null;
			if (rs.next()) {
				rs.getInt(1);
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
			var placeholders =youthMatchTypes.stream().map(i->"?").collect(Collectors.joining(","));
			placeHolderYouthMatchTypes = "(" + placeholders + ")";
		}
		return placeHolderYouthMatchTypes;
	}

	private final String deleteYouthMatchDetailsBeforeSql = createDeleteStatement("WHERE MATCHTYP IN " + getPlaceHolderYouthMatchTypes() + " AND SPIELDATUM<?");
	public void deleteYouthMatchDetailsBefore(Timestamp before) {
		try {
			var params = new ArrayList<>();
			params.addAll(MatchType.getYouthMatchType().stream().map(MatchType::getId).toList());
			params.add(before);
			connectionManager.executePreparedUpdate(deleteYouthMatchDetailsBeforeSql, params.toArray());
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DB.deleteMatchLineupsBefore Error" + e);
		}
	}

	private final String getLastYouthMatchDateSql = createSelectStatement("max(SpielDatum)",
			" WHERE MATCHTYP IN " + getPlaceHolderYouthMatchTypes());
	public Timestamp getLastYouthMatchDate() {
		try (var rs = connectionManager.executePreparedQuery(getLastYouthMatchDateSql,
				MatchType.getYouthMatchType().stream().map(MatchType::getId).toArray())) {
			assert rs != null;
			if ( rs.next()){
				return rs.getTimestamp(1);
			}
		}
		catch (Exception ignored){
		}
		return null;
	}
}
