package core.db;

import core.constants.player.PlayerSkill;
import core.model.enums.MatchType;
import core.model.player.Player;
import core.model.player.PlayerCategory;
import core.model.player.TrainerType;
import core.util.HODateTime;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

final class SpielerTable extends AbstractTable {

	/** Table name **/
	final static String TABLENAME = "SPIELER";

	SpielerTable(ConnectionManager adapter) {
		super(TABLENAME, adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{

				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((p)->((Player)p).getHrfId()).setSetter((p,v)->((Player)p).setHrfId((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SpielerID").setGetter((p)->((Player)p).getPlayerId()).setSetter((p, v)->((Player)p).setPlayerId((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Datum").setGetter((p)->((Player)p).getHrfDate().toDbTimestamp()).setSetter((p,v)->((Player)p).setHrfDate((HODateTime)v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GelbeKarten").setGetter((p)->((Player)p).getTotalCards()).setSetter((p, v)->((Player)p).setTotalCards((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("FirstName").setGetter((p)->((Player)p).getFirstName()).setSetter((p,v)->((Player)p).setFirstName((String)v)).setType(Types.VARCHAR).isNullable(false).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NickName").setGetter((p)->((Player)p).getNickName()).setSetter((p,v)->((Player)p).setNickName((String)v)).setType(Types.VARCHAR).isNullable(false).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastName").setGetter((p)->((Player)p).getLastName()).setSetter((p,v)->((Player)p).setLastName((String)v)).setType(Types.VARCHAR).isNullable(false).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Age").setGetter((p)->((Player)p).getAge()).setSetter((p, v)->((Player)p).setAge((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Kondition").setGetter((p)->((Player)p).getStamina()).setSetter((p,v)->((Player)p).setStamina((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Form").setGetter((p)->((Player)p).getForm()).setSetter((p,v)->((Player)p).setForm((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Torwart").setGetter((p)->((Player)p).getGoalkeeperSkill()).setSetter((p, v)->((Player)p).setGoalkeeperSkill((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Verteidigung").setGetter((p)->((Player)p).getDefendingSkill()).setSetter((p, v)->((Player)p).setDefendingSkill((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Spielaufbau").setGetter((p)->((Player)p).getPlaymakingSkill()).setSetter((p, v)->((Player)p).setPlaymakingSkill((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Fluegel").setGetter((p)->((Player)p).getWingerSkill()).setSetter((p, v)->((Player)p).setWingerSkill((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Torschuss").setGetter((p)->((Player)p).getScoringSkill()).setSetter((p, v)->((Player)p).setScoringSkill((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Passpiel").setGetter((p)->((Player)p).getPassingSkill()).setSetter((p, v)->((Player)p).setPassingSkill((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Standards").setGetter((p)->((Player)p).getSetPiecesSkill()).setSetter((p, v)->((Player)p).setSetPiecesSkill((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubTorwart").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.KEEPER)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.KEEPER,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubVerteidigung").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.DEFENDING)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.DEFENDING,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubSpielaufbau").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.PLAYMAKING)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.PLAYMAKING,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubFluegel").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.WINGER)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.WINGER,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubTorschuss").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.SCORING)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.SCORING,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubPasspiel").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.PASSING)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.PASSING,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubStandards").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.SETPIECES)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.SETPIECES,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubStamina").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.STAMINA)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.STAMINA,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iSpezialitaet").setGetter((p)->((Player)p).getSpecialty()).setSetter((p, v)->((Player)p).setSpecialty((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iCharakter").setGetter((p)->((Player)p).getGentleness()).setSetter((p, v)->((Player)p).setGentleness((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iAnsehen").setGetter((p)->((Player)p).getHonesty()).setSetter((p, v)->((Player)p).setHonesty((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iAgressivitaet").setGetter((p)->((Player)p).getAggressivity()).setSetter((p, v)->((Player)p).setAggressivity((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Fuehrung").setGetter((p)->((Player)p).getLeadership()).setSetter((p,v)->((Player)p).setLeadership((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Erfahrung").setGetter((p)->((Player)p).getExperience()).setSetter((p,v)->((Player)p).setExperience((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Gehalt").setGetter((p)->((Player)p).getWage()).setSetter((p, v)->((Player)p).setWage((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Land").setGetter((p)->((Player)p).getNationalityId()).setSetter((p, v)->((Player)p).setNationalityId((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Marktwert").setGetter((p)->((Player)p).getTsi()).setSetter((p, v)->((Player)p).setTsi((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Verletzt").setGetter((p)->((Player)p).getInjuryWeeks()).setSetter((p,v)->((Player)p).setInjuryWeeks((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ToreFreund").setGetter((p)->((Player)p).getFriendlyGoals()).setSetter((p, v)->((Player)p).setFriendlyGoals((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ToreLiga").setGetter((p)->((Player)p).getLeagueGoals()).setSetter((p, v)->((Player)p).setLeagueGoals((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TorePokal").setGetter((p)->((Player)p).getCupGameGoals()).setSetter((p, v)->((Player)p).setCupGameGoals((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ToreGesamt").setGetter((p)->((Player)p).getTotalGoals()).setSetter((p, v)->((Player)p).setTotalGoals((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Hattrick").setGetter((p)->((Player)p).getHatTricks()).setSetter((p, v)->((Player)p).setHatTricks((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Bewertung").setGetter((p)->((Player)p).getRating()).setSetter((p,v)->((Player)p).setRating((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TrainerTyp").setGetter((p)->TrainerType.toInt(((Player)p).getTrainerType())).setSetter((p, v)->((Player)p).setTrainerType(TrainerType.fromInt((int)v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Trainer").setGetter((p)->((Player)p).getCoachSkill()).setSetter((p, v)->((Player)p).setCoachSkill((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PlayerNumber").setGetter((p)->((Player)p).getShirtNumber()).setSetter((p, v)->((Player)p).setShirtNumber((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TransferListed").setGetter((p)->((Player)p).getTransferListed()).setSetter((p, v)->((Player)p).setTransferListed((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Caps").setGetter((p)->((Player)p).getInternalMatches()).setSetter((p, v)->((Player)p).setInternationalMatches((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CapsU20").setGetter((p)->((Player)p).getU20InternationalMatches()).setSetter((p, v)->((Player)p).setU20InternationalMatches((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AgeDays").setGetter((p)->((Player)p).getAgeDays()).setSetter((p,v)->((Player)p).setAgeDays((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TrainingBlock").setGetter((p)->((Player)p).hasTrainingBlock()).setSetter((p,v)->((Player)p).setTrainingBlock((boolean)v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Loyalty").setGetter((p)->((Player)p).getLoyalty()).setSetter((p,v)->((Player)p).setLoyalty((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HomeGrown").setGetter((p)->((Player)p).isHomeGrown()).setSetter((p,v)->((Player)p).setHomeGrown((boolean)v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NationalTeamID").setGetter((p)->((Player)p).getNationalTeamId()).setSetter((p, v)->((Player)p).setNationalTeamId((Integer)v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubExperience").setGetter((p)->((Player)p).getSubExperience()).setSetter((p,v)->((Player)p).setSubExperience((Double)v)).setType(Types.DOUBLE).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatchDate").setGetter((p)->((Player)p).getLastMatchDate()).setSetter((p,v)->((Player)p).setLastMatchDate((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatchRating").setGetter((p)->((Player)p).getLastMatchRating()).setSetter((p,v)->((Player)p).setLastMatchRating((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatchId").setGetter((p)->((Player)p).getLastMatchId()).setSetter((p,v)->((Player)p).setLastMatchId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LAST_MATCH_TYPE").setGetter((p)->((Player)p).getLastMatchType().getId()).setSetter((p,v)->((Player)p).setLastMatchType(MatchType.getById((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ArrivalDate").setGetter((p)->((Player)p).getArrivalDate().toHT()).setSetter((p,v)->((Player)p).setArrivalDate(HODateTime.fromHT((String)v))).setType(Types.VARCHAR).isNullable(true).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GoalsCurrentTeam").setGetter((p)->((Player)p).getCurrentTeamGoals()).setSetter((p, v)->((Player)p).setCurrentTeamGoals((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PlayerCategory").setGetter((p)->(PlayerCategory.idOf(((Player)p).getPlayerCategory()))).setSetter((p,v)->((Player)p).setPlayerCategory(PlayerCategory.valueOf((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Statement").setGetter((p)->((Player)p).getPlayerStatement()).setSetter((p,v)->((Player)p).setPlayerStatement((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(255).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("OwnerNotes").setGetter((p)->((Player)p).getOwnerNotes()).setSetter((p,v)->((Player)p).setOwnerNotes((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(512).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatch_PlayedMinutes").setGetter((p)->((Player)p).getLastMatchMinutes()).setSetter((p,v)->((Player)p).setLastMatchMinutes((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatch_PositionCode").setGetter((p)->((Player)p).getLastMatchPosition()).setSetter((p,v)->((Player)p).setLastMatchPosition((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatch_RatingEndOfGame").setGetter((p)->((Player)p).getLastMatchRatingEndOfGame()).setSetter((p,v)->((Player)p).setLastMatchRatingEndOfGame((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MotherclubId").setGetter((p)->((Player)p).getMotherClubId()).setSetter((p, v)->((Player)p).setMotherClubId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MotherclubName").setGetter((p)->((Player)p).getMotherClubName()).setSetter((p, v)->((Player)p).setMotherClubName((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(255).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchesCurrentTeam").setGetter((p)->((Player)p).getCurrentTeamMatches()).setSetter((p, v)->((Player)p).setCurrentTeamMatches((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LineupDisabled").setGetter((p)->((Player)p).isLineupDisabled()).setSetter((p,v)->((Player)p).setLineupDisabled((Boolean) v)).setType(Types.BOOLEAN).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ContractDate").setGetter((p)->((Player)p).getContractDate()).setSetter((p,v)->((Player)p).setContractDate((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(100).build()
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[]{
				"CREATE INDEX iSpieler_1 ON " + getTableName() + "(SpielerID,Datum)",
				"CREATE INDEX iSpieler_2 ON " + getTableName() + "(HRF_ID)",
				"CREATE INDEX iSpieler_3 ON " + getTableName() + "(LASTNAME,FIRSTNAME,DATUM)"
		};
	}

	/**
	 * Store a list of records
	 * @param players list of players
	 */
	void store(List<Player> players) {
		if (players != null) {
			for (var p : players) {
				store(p);
			}
		}
	}

	@Override
	protected String createDeleteStatement() {
		return createDeleteStatement("WHERE HRF_ID=?");
	}

	@Override
	protected String createSelectStatement() {
		return createSelectStatement("WHERE HRF_ID=?");
	}

	/**
	 * load players of a hrf (download)
	 * @param hrfID id of hrf
	 * @return list of pLayers
	 */
	List<Player> loadPlayersBefore(int hrfID) {
		return load(Player.class, hrfID);
	}

	private final String loadAllPlayersSql = createSelectStatement(" t inner join (" +
			"    select SPIELERID, max(DATUM) as MaxDate from " +
			getTableName() +
			"    group by SPIELERID" +
			") tm on t.SPIELERID = tm.SPIELERID and t.DATUM = tm.MaxDate");

	/**
	 * load all players of database
	 * @return List of latest records stored in database of all players.
	 */
	List<Player> loadAllPlayers() {
		return load(Player.class, connectionManager.executePreparedQuery(loadAllPlayersSql),-1);
	}

	String loadPlayerHistorySql = createSelectStatement("WHERE SpielerID=? ORDER BY Datum");

	List<Player> loadPlayerHistory(int playerId){
		return load(Player.class, connectionManager.executePreparedQuery(loadPlayerHistorySql, playerId), -1);
	}

	private final String getLetzteBewertung4SpielerSql = "SELECT Bewertung from "+getTableName()+" WHERE SpielerID=? AND Bewertung>0 ORDER BY Datum DESC  LIMIT 1";

	/**
	 * Get latest rating of player
	 */
	int getLatestRatingOfPlayer(int playerId) {
		int bewertung = 0;

		try (final ResultSet rs = connectionManager.executePreparedQuery(getLetzteBewertung4SpielerSql, playerId)) {
			if ((rs != null) && rs.next()) {
				bewertung = rs.getInt("Bewertung");
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getLetzteBewertung4Spieler : " + playerId + " : " + e);
		}
		return bewertung;
	}

	private final String getSpielerNearDateBeforeSql = createSelectStatement("WHERE Datum<=? AND Datum>=? AND SpielerID=? ORDER BY Datum DESC LIMIT 1");
	private final String getSpielerNearDateAfterSql = createSelectStatement("WHERE Datum>=? AND SpielerID=? ORDER BY Datum LIMIT 1");

	Player getSpielerNearDate(int spielerid, Timestamp time) {
		Player player;

		//6 Tage   //1209600000  //14 Tage vorher
		final int spanne = 518400000;

		if (time == null) {
			return null;
		}

		//--- Zuerst x Tage vor dem Datum suchen -------------------------------
		//x Tage vorher
		final Timestamp time2 = new Timestamp(time.getTime() - spanne);
		player = loadOne(Player.class, connectionManager.executePreparedQuery(getSpielerNearDateBeforeSql, time, time2, spielerid));

		//--- Dann ein HRF später versuchen, Dort muss er dann eigenlich vorhanden sein! ---
		if (player == null) {
			player = loadOne(Player.class, connectionManager.executePreparedQuery(getSpielerNearDateAfterSql, time, spielerid));
		}

		//----Dann noch die dopplete Spanne vor der Spanne suchen---------------
		if (player == null) {
			//x Tage vorher
			final Timestamp time3 = new Timestamp(time2.getTime() - (spanne * 2));
			player = loadOne(Player.class, connectionManager.executePreparedQuery(getSpielerNearDateBeforeSql, time2, time3, spielerid));
		}

		return player;
	}

	//------------------------------------------------------------------------------

	private final String getSpielerFirstHRFSql = createSelectStatement(" WHERE SpielerID=? AND Datum>? ORDER BY Datum ASC LIMIT 1");
	/**
	 * load first player appearance
	 */
	Player getSpielerFirstHRF(int playerId, Timestamp after) {
		var ret = loadOne(Player.class, connectionManager.executePreparedQuery(getSpielerFirstHRFSql, playerId, after));
		if ( ret != null){
			ret.setGoner(true);
		}
		return ret;
	}

	private final String loadLatestPlayerInfoSql = createSelectStatement(" WHERE SpielerID=? ORDER BY Datum Desc LIMIT 1");
	public Player loadLatestPlayerInfo(int playerId) {
		return loadOne(Player.class, connectionManager.executePreparedQuery(loadLatestPlayerInfoSql, playerId));
	}

	private final String getTrainerTypeSql = createSelectStatement(" WHERE HRF_ID=? AND TrainerTyp >=0 AND Trainer >0 order by Trainer desc");
	int getTrainerType(int hrfID) {
		try (ResultSet rs = connectionManager.executePreparedQuery(getTrainerTypeSql, hrfID)) {
			if (rs != null) {
				if (rs.next()) {
					return rs.getInt("TrainerTyp");
				}
			}
		} catch (Exception ignored) {
		}

		return -99;
	}

	private final String loadPlayerBeforeSql = createSelectStatement(" WHERE SpielerID=? AND Datum<=? ORDER BY Datum DESC LIMIT 1");
	public Player loadPlayerBefore(int playerId, Timestamp before) {
		return loadOne(Player.class, connectionManager.executePreparedQuery(loadPlayerBeforeSql, playerId, before));
	}

	private final String loadPlayerAfterSql = createSelectStatement(" WHERE SpielerID=? AND Datum>=? ORDER BY Datum ASC LIMIT 1");
	public Player loadPlayerAfter(int playerId, Timestamp before) {
		return loadOne(Player.class, connectionManager.executePreparedQuery(loadPlayerAfterSql, playerId, before));
	}

	private final String loadPlayersBeforeSql = "SELECT S.* FROM SPIELER S INNER JOIN ( SELECT SPIELERID, MAX(DATUM) AS DATUM  FROM SPIELER WHERE DATUM<=? AND (FIRSTNAME IS NULL AND LASTNAME=? OR FIRSTNAME=? AND LASTNAME=?) GROUP BY SPIELERID ) IJ ON S.SPIELERID = IJ.SPIELERID AND S.DATUM = IJ.DATUM";
	public List<Player> loadPlayersBefore(String playerName, Timestamp before) {
		return loadPlayers(loadPlayersBeforeSql, playerName, before);
	}

	private final String loadPlayersAfterSql = "SELECT S.* FROM SPIELER S INNER JOIN ( SELECT SPIELERID, MIN(DATUM) AS DATUM  FROM SPIELER WHERE DATUM>=? AND (FIRSTNAME IS NULL AND LASTNAME=? OR FIRSTNAME=? AND LASTNAME=?) GROUP BY SPIELERID ) IJ ON S.SPIELERID = IJ.SPIELERID AND S.DATUM = IJ.DATUM";
	public List<Player> loadPlayersAfter(String playerName, Timestamp after) {
		return loadPlayers(loadPlayersAfterSql, playerName, after);
	}

	private List<Player> loadPlayers(String query, String playerName, Timestamp time) {
		var nameParts = playerName.split(" ");
		var lastName = nameParts[nameParts.length-1];
		var firstName = Arrays.stream(nameParts).limit(nameParts.length-1).collect(Collectors.joining(" "));
		return load(Player.class, connectionManager.executePreparedQuery(query, time, playerName, firstName, lastName));
	}

	private final String preSql = "select marktwert from SPIELER where spielerid=? and verletzt=-1 order by DATUM desc";

	private final String postSql = "select marktwert from SPIELER where spielerid=? and verletzt>-1 order by DATUM desc";

	public String loadLatestTSINotInjured(int playerId) {
		return loadLatestTSI(preSql, playerId);
	}
	public String loadLatestTSIInjured(int playerId) {
		return loadLatestTSI(postSql, playerId);
	}

	private String loadLatestTSI(String query, int playerId) {
		try (ResultSet rs = connectionManager.executePreparedQuery(query, playerId)) {
			if (rs.next()) {
				return rs.getString("markwert");
			}
		} catch (SQLException e) {
            HOLogger.instance().error(SpielerTable.class, "Error retrieving TSI: " + e);
        }
        return "";
	}

	public Map<Integer, Integer> loadWageHistory(int playerId) {
		Map<Integer, Integer> ret = new HashMap<>();
		String loadWageHistorySql = "select age, max(gehalt) from spieler where spielerid=? group by age";
		try (ResultSet rs = connectionManager.executePreparedQuery(loadWageHistorySql, playerId)) {
			while (rs.next()) {
				ret.put(rs.getInt(1), rs.getInt(2));
			}
		} catch (SQLException e) {
			HOLogger.instance().error(SpielerTable.class, "Error retrieving TSI: " + e);
		}
		return ret;
	}

}