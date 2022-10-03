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
				ColumnDescriptor.Builder.newInstance().setColumnName("Datum").setGetter((p)->((Player)p).getHrfDate().toDbTimestamp()).setSetter((p,v)->((Player)p).setHrfDate(HODateTime.fromDbTimestamp((Timestamp) v))).setType(Types.INTEGER).isNullable(false).build(),
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
				ColumnDescriptor.Builder.newInstance().setColumnName("SubTorwart").setGetter((p)->((Player)p).getSub4SkillAccurate(PlayerSkill.KEEPER)).setSetter((p,v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.KEEPER,(int)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubVerteidigung").setGetter((p)->((Player)p).getSub4SkillAccurate(PlayerSkill.DEFENDING)).setSetter((p,v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.DEFENDING,(int)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubSpielaufbau").setGetter((p)->((Player)p).getSub4SkillAccurate(PlayerSkill.PLAYMAKING)).setSetter((p,v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.PLAYMAKING,(int)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubFluegel").setGetter((p)->((Player)p).getSub4SkillAccurate(PlayerSkill.WINGER)).setSetter((p,v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.WINGER,(int)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubTorschuss").setGetter((p)->((Player)p).getSub4SkillAccurate(PlayerSkill.SCORING)).setSetter((p,v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.SCORING,(int)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubPasspiel").setGetter((p)->((Player)p).getSub4SkillAccurate(PlayerSkill.PASSING)).setSetter((p,v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.PASSING,(int)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubStandards").setGetter((p)->((Player)p).getSub4SkillAccurate(PlayerSkill.SET_PIECES)).setSetter((p,v)->((Player)p).setSubskill4PlayerSkill(PlayerSkill.SET_PIECES,(int)v)).setType(Types.REAL).isNullable(false).build(),
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
				ColumnDescriptor.Builder.newInstance().setColumnName("NationalTeamID").setGetter((p)->((Player)p).getNationalTeamID()).setSetter((p,v)->((Player)p).setNationalTeamId((int)v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("SubExperience").setGetter((p)->((Player)p).getSubExperience()).setSetter((p,v)->((Player)p).setSubExperience((int)v)).setType(Types.REAL).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatchDate").setGetter((p)->((Player)p).getLastMatchDate()).setSetter((p,v)->((Player)p).setLastMatchDate((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatchRating").setGetter((p)->((Player)p).getLastMatchRating()).setSetter((p,v)->((Player)p).setLastMatchRating((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LastMatchId").setGetter((p)->((Player)p).getLastMatchId()).setSetter((p,v)->((Player)p).setLastMatchId((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LAST_MATCH_TYPE").setGetter((p)->((Player)p).getLastMatchType().getId()).setSetter((p,v)->((Player)p).setLastMatchType(MatchType.getById((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ArrivalDate").setGetter((p)->((Player)p).getArrivalDate()).setSetter((p,v)->((Player)p).setArrivalDate((String)v)).setType(Types.VARCHAR).isNullable(true).setLength(100).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("GoalsCurrentTeam").setGetter((p)->((Player)p).getGoalsCurrentTeam()).setSetter((p,v)->((Player)p).setGoalsCurrentTeam((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("PlayerCategory").setGetter((p)->((Player)p).getPlayerCategory().getId()).setSetter((p,v)->((Player)p).setPlayerCategory(PlayerCategory.valueOf((Integer) v))).setType(Types.INTEGER).isNullable(true).build(),
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

	/**
	 * load players of a hrf (download)
	 * @param hrfID id of hrf
	 * @return list of pLayers
	 */
	List<Player> loadPlayers(int hrfID) {
		final ArrayList<Player> ret = new ArrayList<>();
		if ( hrfID > -1) {
			var rs = executePreparedSelect(hrfID);
			try {
				if (rs != null) {
					while (rs.next()) {
						var player = createObject(rs);
						ret.add(player);
					}
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "loadPlayers: " + e);
			}
		}
		return ret;
	}

	private final PreparedSelectStatementBuilder getAllSpielerStatementBuilder = new PreparedSelectStatementBuilder(this, " t inner join (" +
			"    select SPIELERID, max(DATUM) as MaxDate from " +
			getTableName() +
			"    group by SPIELERID" +
			") tm on t.SPIELERID = tm.SPIELERID and t.DATUM = tm.MaxDate");

	/**
	 * load all players of database
	 * @return List of latest records stored in database of all players.
	 */
	List<Player> loadAllPlayers() {
		ResultSet rs;
		Player player;
		final List<Player> ret = new ArrayList<>();
		try {
			rs = adapter.executePreparedQuery(getAllSpielerStatementBuilder.getStatement());
			if (rs != null) {
				while (rs.next()) {
					player = createObject(rs);
					ret.add(player);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "DatenbankZugriff.getPlayer: " + e);
		}
		return ret;
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

	/**
	 * Gibt einen Player zurück mit den Daten kurz vor dem Timestamp
	 */
	Player getSpielerNearDate(int spielerid, Timestamp time) {
		ResultSet rs;
		Player player = null;

		//6 Tage   //1209600000  //14 Tage vorher
		final int spanne = 518400000;

		if (time == null) {
			return null;
		}

		//--- Zuerst x Tage vor dem Datum suchen -------------------------------
		//x Tage vorher
		final Timestamp time2 = new Timestamp(time.getTime() - spanne);
		rs = adapter.executePreparedQuery(getSpielerNearDateBeforeStatementBuilder.getStatement(), time, time2, spielerid);

		try {
			if (rs != null) {
				if (rs.next()) {
					player = createObject(rs);}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"1. Player nicht gefunden für Datum " + time + " und SpielerID " + spielerid);
		}

		//--- Dann ein HRF später versuchen, Dort muss er dann eigenlich vorhanden sein! ---
		if (player == null) {
			rs = adapter.executePreparedQuery(getSpielerNearDateAfterStatementBuilder.getStatement(), time, spielerid);

			try {
				if (rs != null) {
					if (rs.next()) {
						player = createObject(rs);
					}
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"2. Player nicht gefunden für Datum " + time + " und SpielerID " + spielerid);
			}
		}

		//----Dann noch die dopplete Spanne vor der Spanne suchen---------------
		if (player == null) {
			//x Tage vorher
			final Timestamp time3 = new Timestamp(time2.getTime() - (spanne * 2));
			rs = adapter.executePreparedQuery(getSpielerNearDateBeforeStatementBuilder.getStatement(), time2, time3, spielerid);

			try {
				if (rs != null) {
					if (rs.next()) {
						player = createObject(rs);
					}
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"3. Player nicht gefunden für Datum " + time + " und SpielerID " + spielerid);
			}
		}

		return player;
	}

	//------------------------------------------------------------------------------

	private final PreparedSelectStatementBuilder getSpielerFirstHRFStatementBuilder = new PreparedSelectStatementBuilder(this," WHERE SpielerID=? ORDER BY Datum ASC LIMIT 1");
	/**
	 * Gibt einen Player zurück aus dem ersten HRF
	 */
	Player getSpielerFirstHRF(int spielerid) {
		ResultSet rs;
		Player player = null;
		rs = adapter.executePreparedQuery(getSpielerFirstHRFStatementBuilder.getStatement(), spielerid);

		try {
			if (rs != null) {
				if (rs.next()) {
					player = createObject(rs);
					//Info, da der Player für den Vergleich in der Spielerübersicht benutzt wird
					player.setOld(true);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"Nicht gefunden SpielerID " + spielerid);
		}

		return player;
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

    /**
     * Creates a {@link Player} instance populated with values from the db.
     */
    private Player createObject(ResultSet rs) {
    	Player player = new Player();
        try {
			player.setIsStored(true);
			player.setHrfId(rs.getInt("HRF_ID"));
        	player.setPlayerID(rs.getInt("SpielerID"));
            player.setFirstName(rs.getString("FirstName"));
			player.setNickName(rs.getString("NickName"));
			player.setLastName(rs.getString("LastName"));
			player.setArrivalDate(rs.getString("ArrivalDate"));
            player.setAge(rs.getInt("Age"));
            player.setAgeDays(rs.getInt("AgeDays"));
            player.setStamina(rs.getInt("Kondition"));
            player.setForm(rs.getInt("Form"));
            player.setTorwart(rs.getInt("Torwart"));
            player.setVerteidigung(rs.getInt("Verteidigung"));
            player.setSpielaufbau(rs.getInt("Spielaufbau"));
            player.setPasspiel(rs.getInt("Passpiel"));
            player.setFluegelspiel(rs.getInt("Fluegel"));
            player.setTorschuss(rs.getInt("Torschuss"));
            player.setStandards(rs.getInt("Standards"));
            player.setPlayerSpecialty(rs.getInt("iSpezialitaet"));
            player.setCharakter(rs.getInt("iCharakter"));
            player.setAnsehen(rs.getInt("iAnsehen"));
            player.setAgressivitaet(rs.getInt("iAgressivitaet"));
            player.setExperience(rs.getInt("Erfahrung"));
            player.setLoyalty(rs.getInt("Loyalty"));
            player.setHomeGrown(rs.getBoolean("HomeGrown"));
            player.setLeadership(rs.getInt("Fuehrung"));
            player.setGehalt(rs.getInt("Gehalt"));
            player.setNationalityAsInt(rs.getInt("Land"));
            player.setTSI(rs.getInt("Marktwert"));

            //TSI, values stored before TSIDATE needs to be divided by 1000
			var tsidate = rs.getTimestamp("Datum");
            player.setHrfDate(HODateTime.fromDbTimestamp(tsidate));
            if (tsidate.before(DBManager.TSIDATE)) {
                player.setTSI(player.getTSI()/1000);
            }

            //Subskills
            player.setSubskill4PlayerSkill(PlayerSkill.KEEPER,rs.getFloat("SubTorwart"));
            player.setSubskill4PlayerSkill(PlayerSkill.DEFENDING,rs.getFloat("SubVerteidigung"));
            player.setSubskill4PlayerSkill(PlayerSkill.PLAYMAKING,rs.getFloat("SubSpielaufbau"));
            player.setSubskill4PlayerSkill(PlayerSkill.PASSING,rs.getFloat("SubPasspiel"));
            player.setSubskill4PlayerSkill(PlayerSkill.WINGER,rs.getFloat("SubFluegel"));
            player.setSubskill4PlayerSkill(PlayerSkill.SCORING,rs.getFloat("SubTorschuss"));
			player.setSubskill4PlayerSkill(PlayerSkill.SET_PIECES,rs.getFloat("SubStandards"));

			player.setSubExperience(rs.getDouble("SubExperience"));
			player.setNationalTeamId(rs.getInt("NationalTeamID"));

            player.setGelbeKarten(rs.getInt("GelbeKarten"));
            player.setInjuryWeeks(rs.getInt("Verletzt"));
            player.setToreFreund(rs.getInt("ToreFreund"));
            player.setToreLiga(rs.getInt("ToreLiga"));
            player.setTorePokal(rs.getInt("TorePokal"));
            player.setAllOfficialGoals(rs.getInt("ToreGesamt"));
            player.setHattrick(rs.getInt("Hattrick"));
			player.setGoalsCurrentTeam(rs.getInt("GoalsCurrentTeam"));
            player.setBewertung(rs.getInt("Bewertung"));
            player.setTrainerTyp(TrainerType.fromInt(rs.getInt("TrainerTyp")));
            player.setTrainerSkill(rs.getInt("Trainer"));
            player.setShirtNumber(rs.getInt("PlayerNumber"));
            player.setTransferlisted(rs.getInt("TransferListed"));
            player.setLaenderspiele(rs.getInt("Caps"));
            player.setU20Laenderspiele(rs.getInt("CapsU20"));

            // Training block
			player.setTrainingBlock(rs.getBoolean("TrainingBlock"));

			// LastMatch
			try {
				player.setLastMatchDetails(
						rs.getString("LastMatchDate"),
						DBManager.getInteger(rs,"LastMatchRating"),
						DBManager.getInteger(rs,"LastMatchId")
				);

				player.setLastMatchMinutes(DBManager.getInteger(rs, "LastMatch_PlayedMinutes"));
				player.setLastMatchPosition(DBManager.getInteger(rs, "LastMatch_PositionCode"));
				player.setLastMatchRatingEndOfGame(DBManager.getInteger(rs, "LastMatch_RatingEndOfGame"));

			} catch (Exception e) {
				HOLogger.instance().error(getClass(), "Error retrieving last match details: " + e);
			}

            player.setLastMatchType(MatchType.getById(rs.getInt("LAST_MATCH_TYPE")));
			player.setPlayerCategory(PlayerCategory.valueOf(DBManager.getInteger(rs, "PlayerCategory")));
			player.setPlayerStatement(rs.getString("Statement"));
			player.setOwnerNotes(rs.getString("OwnerNotes"));

			player.setMotherClubId(DBManager.getInteger(rs, "MotherclubID"));
			player.setMotherClubName(rs.getString("MotherClubName"));
			player.setMatchesCurrentTeam(DBManager.getInteger(rs,"MatchesCurrentTeam"));

		} catch (Exception e) {
            HOLogger.instance().log(getClass(),e);
        }
        return player;
    }

	private final PreparedSelectStatementBuilder loadPlayerHistoryStatementBuilder = new PreparedSelectStatementBuilder(this, "WHERE SpielerID=? Order By Datum ASC");
	public List<Player> loadPlayerHistory(int spielerId) {
		var ret = new ArrayList<Player>();
		try {
			var rs = executePreparedSelect(loadPlayerHistoryStatementBuilder.getStatement(), spielerId);
			if (rs != null) {
				if (rs.next()) {
					var player = createObject(rs);
					ret.add(player);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),e);
		}
		return ret;
	}
}