package core.db;

import core.constants.player.PlayerSkill;
import core.model.enums.MatchType;
import core.model.player.Player;
import core.model.player.PlayerCategory;
import core.model.player.TrainerType;
import core.util.HODateTime;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

final class SpielerTable extends AbstractTable {

	/** tablename **/
	final static String TABLENAME = "SPIELER";

	SpielerTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{

				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((p)->((Player)p).getHrfId()).setSetter((p,v)->((Player)p).setHrfId((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SpielerID").setGetter((p)->((Player)p).getPlayerID()).setSetter((p,v)->((Player)p).setPlayerID((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Datum").setGetter((p)->((Player)p).getHrfDate().toDbTimestamp()).setSetter((p,v)->((Player)p).setHrfDate((HODateTime)v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GelbeKarten").setGetter((p)->((Player)p).getCards()).setSetter((p,v)->((Player)p).setGelbeKarten((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("FirstName").setGetter((p)->((Player)p).getFirstName()).setSetter((p,v)->((Player)p).setFirstName((String)v)).setType(Types.VARCHAR).isNullable(false).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NickName").setGetter((p)->((Player)p).getNickName()).setSetter((p,v)->((Player)p).setNickName((String)v)).setType(Types.VARCHAR).isNullable(false).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastName").setGetter((p)->((Player)p).getLastName()).setSetter((p,v)->((Player)p).setLastName((String)v)).setType(Types.VARCHAR).isNullable(false).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Age").setGetter((p)->((Player)p).getAlter()).setSetter((p,v)->((Player)p).setAge((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Kondition").setGetter((p)->((Player)p).getStamina()).setSetter((p,v)->((Player)p).setStamina((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Form").setGetter((p)->((Player)p).getForm()).setSetter((p,v)->((Player)p).setForm((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Torwart").setGetter((p)->((Player)p).getGKskill()).setSetter((p,v)->((Player)p).setTorwart((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Verteidigung").setGetter((p)->((Player)p).getDEFskill()).setSetter((p,v)->((Player)p).setVerteidigung((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Spielaufbau").setGetter((p)->((Player)p).getPMskill()).setSetter((p,v)->((Player)p).setSpielaufbau((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Fluegel").setGetter((p)->((Player)p).getWIskill()).setSetter((p,v)->((Player)p).setFluegelspiel((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Torschuss").setGetter((p)->((Player)p).getSCskill()).setSetter((p,v)->((Player)p).setTorschuss((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Passpiel").setGetter((p)->((Player)p).getPSskill()).setSetter((p,v)->((Player)p).setPasspiel((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Standards").setGetter((p)->((Player)p).getSPskill()).setSetter((p,v)->((Player)p).setStandards((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubTorwart").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.KEEPER)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.KEEPER,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubVerteidigung").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.DEFENDING)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.DEFENDING,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubSpielaufbau").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.PLAYMAKING)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.PLAYMAKING,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubFluegel").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.WINGER)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.WINGER,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubTorschuss").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.SCORING)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.SCORING,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubPasspiel").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.PASSING)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.PASSING,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubStandards").setGetter((p)->((Player)p).getSub4Skill(PlayerSkill.SET_PIECES)).setSetter((p, v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.SET_PIECES,(float)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iSpezialitaet").setGetter((p)->((Player)p).getPlayerSpecialty()).setSetter((p,v)->((Player)p).setPlayerSpecialty((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iCharakter").setGetter((p)->((Player)p).getCharakter()).setSetter((p,v)->((Player)p).setCharakter((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iAnsehen").setGetter((p)->((Player)p).getAnsehen()).setSetter((p,v)->((Player)p).setAnsehen((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iAgressivitaet").setGetter((p)->((Player)p).getAgressivitaet()).setSetter((p,v)->((Player)p).setAgressivitaet((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Fuehrung").setGetter((p)->((Player)p).getLeadership()).setSetter((p,v)->((Player)p).setLeadership((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Erfahrung").setGetter((p)->((Player)p).getExperience()).setSetter((p,v)->((Player)p).setExperience((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Gehalt").setGetter((p)->((Player)p).getSalary()).setSetter((p,v)->((Player)p).setGehalt((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Land").setGetter((p)->((Player)p).getNationalityAsInt()).setSetter((p,v)->((Player)p).setNationalityAsInt((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Marktwert").setGetter((p)->((Player)p).getMarktwert()).setSetter((p,v)->((Player)p).setTSI((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Verletzt").setGetter((p)->((Player)p).getInjuryWeeks()).setSetter((p,v)->((Player)p).setInjuryWeeks((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ToreFreund").setGetter((p)->((Player)p).getToreFreund()).setSetter((p,v)->((Player)p).setToreFreund((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ToreLiga").setGetter((p)->((Player)p).getSeasonSeriesGoal()).setSetter((p,v)->((Player)p).setToreLiga((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TorePokal").setGetter((p)->((Player)p).getSeasonCupGoal()).setSetter((p,v)->((Player)p).setTorePokal((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ToreGesamt").setGetter((p)->((Player)p).getAllOfficialGoals()).setSetter((p,v)->((Player)p).setAllOfficialGoals((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Hattrick").setGetter((p)->((Player)p).getHattrick()).setSetter((p,v)->((Player)p).setHattrick((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Bewertung").setGetter((p)->((Player)p).getRating()).setSetter((p,v)->((Player)p).setBewertung((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TrainerTyp").setGetter((p)->TrainerType.toInt(((Player)p).getTrainerTyp())).setSetter((p,v)->((Player)p).setTrainerTyp(TrainerType.fromInt((int)v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Trainer").setGetter((p)->((Player)p).getTrainerSkill()).setSetter((p,v)->((Player)p).setTrainerSkill((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PlayerNumber").setGetter((p)->((Player)p).getTrikotnummer()).setSetter((p,v)->((Player)p).setShirtNumber((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TransferListed").setGetter((p)->((Player)p).getTransferlisted()).setSetter((p,v)->((Player)p).setTransferlisted((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Caps").setGetter((p)->((Player)p).getLaenderspiele()).setSetter((p,v)->((Player)p).setLaenderspiele((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("CapsU20").setGetter((p)->((Player)p).getU20Laenderspiele()).setSetter((p,v)->((Player)p).setU20Laenderspiele((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AgeDays").setGetter((p)->((Player)p).getAgeDays()).setSetter((p,v)->((Player)p).setAgeDays((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TrainingBlock").setGetter((p)->((Player)p).hasTrainingBlock()).setSetter((p,v)->((Player)p).setTrainingBlock((boolean)v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Loyalty").setGetter((p)->((Player)p).getLoyalty()).setSetter((p,v)->((Player)p).setLoyalty((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HomeGrown").setGetter((p)->((Player)p).isHomeGrown()).setSetter((p,v)->((Player)p).setHomeGrown((boolean)v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("NationalTeamID").setGetter((p)->((Player)p).getNationalTeamID()).setSetter((p,v)->((Player)p).setNationalTeamId((Integer)v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubExperience").setGetter((p)->((Player)p).getSubExperience()).setSetter((p,v)->((Player)p).setSubExperience((Double)v)).setType(Types.DOUBLE).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatchDate").setGetter((p)->((Player)p).getLastMatchDate()).setSetter((p,v)->((Player)p).setLastMatchDate((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatchRating").setGetter((p)->((Player)p).getLastMatchRating()).setSetter((p,v)->((Player)p).setLastMatchRating((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatchId").setGetter((p)->((Player)p).getLastMatchId()).setSetter((p,v)->((Player)p).setLastMatchId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LAST_MATCH_TYPE").setGetter((p)->((Player)p).getLastMatchType().getId()).setSetter((p,v)->((Player)p).setLastMatchType(MatchType.getById((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ArrivalDate").setGetter((p)->((Player)p).getArrivalDate()).setSetter((p,v)->((Player)p).setArrivalDate((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GoalsCurrentTeam").setGetter((p)->((Player)p).getGoalsCurrentTeam()).setSetter((p,v)->((Player)p).setGoalsCurrentTeam((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PlayerCategory").setGetter((p)->(PlayerCategory.idOf(((Player)p).getPlayerCategory()))).setSetter((p,v)->((Player)p).setPlayerCategory(PlayerCategory.valueOf((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Statement").setGetter((p)->((Player)p).getPlayerStatement()).setSetter((p,v)->((Player)p).setPlayerStatement((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(255).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("OwnerNotes").setGetter((p)->((Player)p).getOwnerNotes()).setSetter((p,v)->((Player)p).setOwnerNotes((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(255).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatch_PlayedMinutes").setGetter((p)->((Player)p).getLastMatchMinutes()).setSetter((p,v)->((Player)p).setLastMatchMinutes((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatch_PositionCode").setGetter((p)->((Player)p).getLastMatchPosition()).setSetter((p,v)->((Player)p).setLastMatchPosition((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatch_RatingEndOfGame").setGetter((p)->((Player)p).getLastMatchRatingEndOfGame()).setSetter((p,v)->((Player)p).setLastMatchRatingEndOfGame((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MotherclubId").setGetter((p)->((Player)p).getMotherclubId()).setSetter((p,v)->((Player)p).setMotherClubId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MotherclubName").setGetter((p)->((Player)p).getMotherclubName()).setSetter((p,v)->((Player)p).setMotherClubName((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(255).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MatchesCurrentTeam").setGetter((p)->((Player)p).getMatchesCurrentTeam()).setSetter((p,v)->((Player)p).setMatchesCurrentTeam((Integer) v)).setType(Types.INTEGER).isNullable(true).build()
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX iSpieler_1 ON " + getTableName() + "(" + columns[1].getColumnName() + "," + columns[2].getColumnName() + ")",
			"CREATE INDEX iSpieler_2 ON " + getTableName() + "(" + columns[0].getColumnName() + ")" };
	}


	/**
	 * store a list of records
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
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this, "WHERE HRF_ID=?");
	}

	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
		return new PreparedSelectStatementBuilder(this, "WHERE HRF_ID=?");
	}

	/**
	 * load players of a hrf (download)
	 * @param hrfID id of hrf
	 * @return list of pLayers
	 */
	List<Player> loadPlayers(int hrfID) {
		return load(Player.class, hrfID);
	}

	private final PreparedSelectStatementBuilder loadAllPlayersStatementBuilder = new PreparedSelectStatementBuilder(this, " t inner join (" +
			"    select SPIELERID, max(DATUM) as MaxDate from " +
			getTableName() +
			"    group by SPIELERID" +
			") tm on t.SPIELERID = tm.SPIELERID and t.DATUM = tm.MaxDate");

	/**
	 * load all players of database
	 * @return List of latest records stored in database of all players.
	 */
	List<Player> loadAllPlayers() {
		return load(Player.class, adapter.executePreparedQuery(loadAllPlayersStatementBuilder.getStatement()),-1);
	}

	private final DBManager.PreparedStatementBuilder getLetzteBewertung4SpielerStatementBuilder = new DBManager.PreparedStatementBuilder(
			"SELECT Bewertung from "+getTableName()+" WHERE SpielerID=? AND Bewertung>0 ORDER BY Datum DESC  LIMIT 1" );

	/**
	 * Gibt die letzte Bewertung für den Player zurück // HRF
	 */
	int getLetzteBewertung4Spieler(int spielerid) {
		int bewertung = 0;

		try {
			final ResultSet rs = adapter.executePreparedQuery(getLetzteBewertung4SpielerStatementBuilder.getStatement(), spielerid);
			if ((rs != null) && rs.next()) {
				bewertung = rs.getInt("Bewertung");
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getLetzteBewertung4Spieler : " + spielerid + " : " + e);
		}
		return bewertung;
	}

	private final PreparedSelectStatementBuilder getSpielerNearDateBeforeStatementBuilder = new PreparedSelectStatementBuilder(this, "WHERE Datum<=? AND Datum>=? AND SpielerID=? ORDER BY Datum DESC LIMIT 1");
	private final PreparedSelectStatementBuilder getSpielerNearDateAfterStatementBuilder = new PreparedSelectStatementBuilder(this, "WHERE Datum>=? AND SpielerID=? ORDER BY Datum LIMIT 1");

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
		player = loadOne(Player.class, adapter.executePreparedQuery(getSpielerNearDateBeforeStatementBuilder.getStatement(), time, time2, spielerid));

		//--- Dann ein HRF später versuchen, Dort muss er dann eigenlich vorhanden sein! ---
		if (player == null) {
			player = loadOne(Player.class, adapter.executePreparedQuery(getSpielerNearDateAfterStatementBuilder.getStatement(), time, spielerid));
		}

		//----Dann noch die dopplete Spanne vor der Spanne suchen---------------
		if (player == null) {
			//x Tage vorher
			final Timestamp time3 = new Timestamp(time2.getTime() - (spanne * 2));
			player = loadOne(Player.class, adapter.executePreparedQuery(getSpielerNearDateBeforeStatementBuilder.getStatement(), time2, time3, spielerid));
		}

		return player;
	}

	//------------------------------------------------------------------------------

	private final PreparedSelectStatementBuilder getSpielerFirstHRFStatementBuilder = new PreparedSelectStatementBuilder(this," WHERE SpielerID=? AND Datum>? ORDER BY Datum ASC LIMIT 1");
	/**
	 * load first player appearance
	 */
	Player getSpielerFirstHRF(int spielerid, Timestamp after) {
		var ret = loadOne(Player.class, adapter.executePreparedQuery(getSpielerFirstHRFStatementBuilder.getStatement(), spielerid, after));
		if ( ret != null){
			ret.setOld(true);
		}
		return ret;
	}

	private final PreparedSelectStatementBuilder getTrainerTypeStatementBuilder = new PreparedSelectStatementBuilder(this, " WHERE HRF_ID=? AND TrainerTyp >=0 AND Trainer >0 order by Trainer desc");
	int getTrainerType(int hrfID) {
		ResultSet rs;
		rs = adapter.executePreparedQuery(getTrainerTypeStatementBuilder.getStatement(), hrfID);
		try {
			if (rs != null) {
				if (rs.next()) {
					return rs.getInt("TrainerTyp");
				}
			}
		} catch (Exception ignored) {
		}

		return -99;
	}
}