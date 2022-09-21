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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

final class SpielerTable extends AbstractTable {

	/** tablename **/
	final static String TABLENAME = "SPIELER";

	SpielerTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				new ColumnDescriptor("HRF_ID", Types.INTEGER, false),
				new ColumnDescriptor("Datum", Types.TIMESTAMP, false),
				new ColumnDescriptor("GelbeKarten", Types.INTEGER, false),
				new ColumnDescriptor("SpielerID", Types.INTEGER, false),
				new ColumnDescriptor("FirstName", Types.VARCHAR, true, 100),
				new ColumnDescriptor("NickName", Types.VARCHAR, true, 100),
				new ColumnDescriptor("LastName", Types.VARCHAR, true, 100),
				new ColumnDescriptor("Age", Types.INTEGER, false),
				new ColumnDescriptor("Kondition", Types.INTEGER, false),
				new ColumnDescriptor("Form", Types.INTEGER, false),
				new ColumnDescriptor("Torwart", Types.INTEGER, false),
				new ColumnDescriptor("Verteidigung", Types.INTEGER, false),
				new ColumnDescriptor("Spielaufbau", Types.INTEGER, false),
				new ColumnDescriptor("Fluegel", Types.INTEGER, false),
				new ColumnDescriptor("Torschuss", Types.INTEGER, false),
				new ColumnDescriptor("Passpiel", Types.INTEGER, false),
				new ColumnDescriptor("Standards", Types.INTEGER, false),
				new ColumnDescriptor("SubTorwart", Types.REAL, false),
				new ColumnDescriptor("SubVerteidigung", Types.REAL, false),
				new ColumnDescriptor("SubSpielaufbau", Types.REAL, false),
				new ColumnDescriptor("SubFluegel", Types.REAL, false),
				new ColumnDescriptor("SubTorschuss", Types.REAL, false),
				new ColumnDescriptor("SubPasspiel", Types.REAL, false),
				new ColumnDescriptor("SubStandards", Types.REAL, false),
				new ColumnDescriptor("OffsetTorwart", Types.REAL, false),
				new ColumnDescriptor("OffsetVerteidigung", Types.REAL, false),
				new ColumnDescriptor("OffsetSpielaufbau", Types.REAL, false),
				new ColumnDescriptor("OffsetFluegel", Types.REAL, false),
				new ColumnDescriptor("OffsetTorschuss", Types.REAL, false),
				new ColumnDescriptor("OffsetPasspiel", Types.REAL, false),
				new ColumnDescriptor("OffsetStandards", Types.REAL, false),
				new ColumnDescriptor("iSpezialitaet", Types.INTEGER, false),
				new ColumnDescriptor("iCharakter", Types.INTEGER, false),
				new ColumnDescriptor("iAnsehen", Types.INTEGER, false),
				new ColumnDescriptor("iAgressivitaet", Types.INTEGER, false),
				new ColumnDescriptor("Fuehrung", Types.INTEGER, false),
				new ColumnDescriptor("Erfahrung", Types.INTEGER, false),
				new ColumnDescriptor("Gehalt", Types.INTEGER, false),
				new ColumnDescriptor("Bonus", Types.INTEGER, false),
				new ColumnDescriptor("Land", Types.INTEGER, false),
				new ColumnDescriptor("Marktwert", Types.INTEGER, false),
				new ColumnDescriptor("Verletzt", Types.INTEGER, false),
				new ColumnDescriptor("ToreFreund", Types.INTEGER, false),
				new ColumnDescriptor("ToreLiga", Types.INTEGER, false),
				new ColumnDescriptor("TorePokal", Types.INTEGER, false),
				new ColumnDescriptor("ToreGesamt", Types.INTEGER, false),
				new ColumnDescriptor("Hattrick", Types.INTEGER, false),
				new ColumnDescriptor("Bewertung", Types.INTEGER, false),
				new ColumnDescriptor("TrainerTyp", Types.INTEGER, false),
				new ColumnDescriptor("Trainer", Types.INTEGER, false),
				new ColumnDescriptor("PlayerNumber", Types.INTEGER, false),
				new ColumnDescriptor("TransferListed", Types.INTEGER, false),
				new ColumnDescriptor("Caps", Types.INTEGER, false),
				new ColumnDescriptor("CapsU20", Types.INTEGER, false),
				new ColumnDescriptor("AgeDays", Types.INTEGER, false),
				new ColumnDescriptor("TrainingBlock", Types.BOOLEAN, false),
				new ColumnDescriptor("Loyalty", Types.INTEGER, false),
				new ColumnDescriptor("HomeGrown", Types.BOOLEAN, false),
				new ColumnDescriptor("NationalTeamID", Types.INTEGER, true),
				new ColumnDescriptor("SubExperience", Types.REAL, false),
				new ColumnDescriptor("LastMatchDate", Types.VARCHAR, true, 100),
				new ColumnDescriptor("LastMatchRating", Types.INTEGER, true),
				new ColumnDescriptor("LastMatchId", Types.INTEGER, true),
				new ColumnDescriptor("LAST_MATCH_TYPE", Types.INTEGER, true),
				new ColumnDescriptor("ArrivalDate", Types.VARCHAR, true, 100),
				new ColumnDescriptor("GoalsCurrentTeam", Types.INTEGER, true),
				new ColumnDescriptor("PlayerCategory", Types.INTEGER, true),
				new ColumnDescriptor("Statement", Types.VARCHAR, true, 255),
				new ColumnDescriptor("OwnerNotes", Types.VARCHAR, true, 255),
				new ColumnDescriptor("LastMatch_PlayedMinutes", Types.INTEGER, true),
				new ColumnDescriptor("LastMatch_PositionCode", Types.INTEGER, true),
				new ColumnDescriptor("LastMatch_RatingEndOfGame", Types.INTEGER, true),
				new ColumnDescriptor("MotherclubId", Types.INTEGER, true),
				new ColumnDescriptor("MotherclubName", Types.VARCHAR, true, 255),
				new ColumnDescriptor("MatchesCurrentTeam", Types.INTEGER, true)
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[] {
			"CREATE INDEX iSpieler_1 ON " + getTableName() + "(" + columns[3].getColumnName() + "," + columns[1].getColumnName() + ")",
			"CREATE INDEX iSpieler_2 ON " + getTableName() + "(" + columns[0].getColumnName() + ")" };
	}

	private final PreparedDeleteStatementBuilder deletePlayerStatementBuilder=new PreparedDeleteStatementBuilder(this, "WHERE HRF_ID=? AND SPIELERID=?");
	/**
	 * saves one player to the DB
	 *
	 * @param hrfId		hrf id
	 * @param player	the player to be saved
	 */

	void saveSpieler(int hrfId, Player player, Timestamp date) {
		this.adapter.executePreparedUpdate(deletePlayerStatementBuilder.getStatement(), hrfId, player.getPlayerID());
		executePreparedInsert(
				hrfId,
				date,
				player.getCards(),
				player.getPlayerID(),
				player.getFirstName(),
				player.getNickName(),
				player.getLastName(),
				player.getAlter(),
				player.getStamina(),
				player.getForm(),
				player.getGKskill(),
				player.getDEFskill(),
				player.getPMskill(),
				player.getWIskill(),
				player.getSCskill(),
				player.getPSskill(),
				player.getSPskill(),
				player.getSub4SkillAccurate(PlayerSkill.KEEPER),
				player.getSub4SkillAccurate(PlayerSkill.DEFENDING),
				player.getSub4SkillAccurate(PlayerSkill.PLAYMAKING),
				player.getSub4SkillAccurate(PlayerSkill.WINGER),
				player.getSub4SkillAccurate(PlayerSkill.SCORING),
				player.getSub4SkillAccurate(PlayerSkill.PASSING),
				player.getSub4SkillAccurate(PlayerSkill.SET_PIECES),
				// Training offsets below
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				player.getPlayerSpecialty(),
				player.getCharakter(),
				player.getAnsehen(),
				player.getAgressivitaet(),
				player.getLeadership(),
				player.getExperience(),
				player.getSalary(),
				player.getBonus(),
				player.getNationalityAsInt(),
				player.getMarktwert(),
				player.getInjuryWeeks(),
				player.getToreFreund(),
				player.getSeasonSeriesGoal(),
				player.getSeasonCupGoal(),
				player.getAllOfficialGoals(),
				player.getHattrick(),
				player.getRating(),
				TrainerType.toInt(player.getTrainerTyp()),
				player.getTrainerSkill(),
				player.getTrikotnummer(),
				player.getTransferlisted(),
				player.getLaenderspiele(),
				player.getU20Laenderspiele(),
				player.getAgeDays(),
				player.hasTrainingBlock(),
				player.getLoyalty(),
				player.isHomeGrown(),
				player.getNationalTeamID(),
				player.getSubExperience(),
				player.getLastMatchDate(),
				player.getLastMatchRating(),
				player.getLastMatchId(),
				player.getLastMatchType().getId(),
				player.getArrivalDate(),
				player.getGoalsCurrentTeam(),
				(player.getPlayerCategory()!=null?player.getPlayerCategory().getId():null),
				player.getPlayerStatement(),
				player.getOwnerNotes(),
				player.getLastMatchMinutes(),
				player.getLastMatchPosition(),
				player.getLastMatchRatingEndOfGame(),
				player.getMotherclubId(),
				player.getMotherclubName(),
				player.getMatchesCurrentTeam()
		);
	}


	/**
	 * Saves the players in the <code>spieler</code> list.
	 */
	void saveSpieler(int hrfId, List<Player> spieler, Timestamp date) {
		if (spieler != null) {
			// Delete old values
			executePreparedDelete(hrfId);
			for (Player p: spieler) {
				saveSpieler(hrfId, p, date);
			}
		}
	}


	private final PreparedSelectStatementBuilder selectStatementBuilder = new PreparedSelectStatementBuilder(this, " WHERE HRF_ID =? AND SpielerId=?");

	/**
	 * get a player from a specific HRF
	 *
	 * @param hrfID hrd id
	 * @param playerId player id
	 *
	 * @return player
	 */
	Player getSpielerFromHrf(int hrfID, int playerId) {
		var rs = this.adapter.executePreparedQuery(selectStatementBuilder.getStatement(), hrfID, playerId);

		try {
			if (rs != null) {
				if (rs.next()) {
					return  createObject(rs);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getSpielerFromHrf: " + e);
		}
		return null;
	}


	/**
	 * lädt die Player zum angegeben HRF file ein
	 */
	List<Player> getSpieler(int hrfID) {
		ResultSet rs;
		Player player;

		final ArrayList<Player> ret = new ArrayList<>();
		if ( hrfID > -1) {

			rs = executePreparedSelect(hrfID);

			try {
				if (rs != null) {
					while (rs.next()) {
						player = createObject(rs);

						//HOLogger.instance().log(getClass(), player.getSpielerID () );
						ret.add(player);
					}
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "DatenbankZugriff.getPlayer: " + e);
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
	 * gibt alle Player zurück, auch ehemalige
	 */
	Vector<Player> getAllSpieler() {
		ResultSet rs;
		Player player;
		final Vector<Player> ret = new Vector<>();
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

	private final DBManager.PreparedStatementBuilder getLetzteBewertung4SpielerStatementBuilder = new DBManager.PreparedStatementBuilder(this.adapter,"SELECT Bewertung from "+getTableName()+" WHERE SpielerID=? AND Bewertung>0 ORDER BY Datum DESC  LIMIT 1" );

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
