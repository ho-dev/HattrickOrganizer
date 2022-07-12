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

	/**
	 * saves one player to the DB
	 *
	 * @param hrfId		hrf id
	 * @param player	the player to be saved
	 */

	void saveSpieler(int hrfId, Player player, Timestamp date) {
		final String[] awhereS = { "HRF_ID", "SpielerId" };
		final String[] awhereV = { "" + hrfId, "" + player.getPlayerID()};
		// Delete old values
		delete(awhereS, awhereV);

		//insert vorbereiten

		String statement = "INSERT INTO " + getTableName() +
				" ( GelbeKarten , SpielerID , ArrivalDate, FirstName , NickName, LastName , Age , AgeDays , " +
				"Kondition , Form , Torwart , Verteidigung , Spielaufbau , Fluegel , " +
				"Torschuss , Passpiel , Standards , SubTorwart , SubVerteidigung , " +
				"SubSpielaufbau , SubFluegel , SubTorschuss , SubPasspiel , SubStandards , " +
				"OffsetTorwart , OffsetVerteidigung , OffsetSpielaufbau , OffsetFluegel , " +
				"OffsetTorschuss , OffsetPasspiel , OffsetStandards , iSpezialitaet , " +
				"iCharakter , iAnsehen , iAgressivitaet , Fuehrung , Erfahrung , Gehalt , " +
				"Bonus , Land , Marktwert , Verletzt , ToreFreund , ToreLiga , TorePokal , GoalsCurrentTeam , " +
				"ToreGesamt , Hattrick , Bewertung , TrainerTyp, Trainer, HRF_ID, Datum, " +
				"PlayerNumber, TransferListed,  Caps, CapsU20, TrainingBlock, Loyalty, HomeGrown, " +
				"SubExperience, NationalTeamID, " +
				"LastMatchDate, LastMatchRating, LastMatchId, LAST_MATCH_TYPE, LastMatch_PositionCode, LastMatch_PlayedMinutes, LastMatch_RatingEndOfGame, " +
				"Statement, OwnerNotes, PlayerCategory, " +
				"MotherclubId, MotherclubName, MatchesCurrentTeam" +
		") VALUES(" +
				player.getCards() + "," +
				player.getPlayerID() + "," +
				"'" + DBManager.insertEscapeSequences(player.getArrivalDate()) + "'," +
				"'" + DBManager.insertEscapeSequences(player.getFirstName()) + "'," +
				"'" + DBManager.insertEscapeSequences(player.getNickName()) + "'," +
				"'" + DBManager.insertEscapeSequences(player.getLastName()) + "'," +
				player.getAlter() + "," +
				player.getAgeDays() + "," +
				player.getStamina() + "," +
				player.getForm() + "," +
				player.getGKskill() + "," +
				player.getDEFskill() + "," +
				player.getPMskill() + "," +
				player.getWIskill() + "," +
				player.getSCskill() + "," +
				player.getPSskill() + "," +
				player.getSPskill() + "," +
				player.getSub4SkillAccurate(PlayerSkill.KEEPER) + "," +
				player.getSub4SkillAccurate(PlayerSkill.DEFENDING) + "," +
				player.getSub4SkillAccurate(PlayerSkill.PLAYMAKING) + "," +
				player.getSub4SkillAccurate(PlayerSkill.WINGER) + "," +
				player.getSub4SkillAccurate(PlayerSkill.SCORING) + "," +
				player.getSub4SkillAccurate(PlayerSkill.PASSING) + "," +
				player.getSub4SkillAccurate(PlayerSkill.SET_PIECES) + "," +
				// Training offsets below
				"0," +
				"0," +
				"0," +
				"0," +
				"0," +
				"0," +
				"0," +
				player.getPlayerSpecialty() + "," +
				player.getCharakter() + "," +
				player.getAnsehen() + "," +
				player.getAgressivitaet() + "," +
				player.getLeadership() + "," +
				player.getExperience() + "," +
				player.getSalary() + "," +
				player.getBonus() + "," +
				player.getNationalityAsInt() + "," +
				player.getSaveMarktwert() + "," +
				player.getInjuryWeeks() + "," +
				player.getToreFreund() + "," +
				player.getSeasonSeriesGoal() + "," +
				player.getSeasonCupGoal() + "," +
				player.getGoalsCurrentTeam() + "," +
				player.getAllOfficialGoals() + "," +
				player.getHattrick() + "," +
				player.getRating() + "," +
				TrainerType.toInt(player.getTrainerTyp()) + "," +
				player.getTrainerSkill() + "," +
				hrfId + "," +
				"'" + date.toString() + "'," +
				player.getTrikotnummer() + "," +
				player.getTransferlisted() + "," +
				player.getLaenderspiele() + "," +
				player.getU20Laenderspiele() + "," +
				player.hasTrainingBlock() + "," +
				player.getLoyalty() + "," +
				player.isHomeGrown() + "," +
				player.getSubExperience() + "," +
				player.getNationalTeamID() + "," +
				"'" + player.getLastMatchDate() + "'," +
				player.getLastMatchRating() + "," +
				player.getLastMatchId() + "," +
				player.getLastMatchType().getId()  + "," +
				player.getLastMatchPosition() + "," +
				player.getLastMatchMinutes() + "," +
				player.getLastMatchRatingEndOfGame() + ",'"
				+ player.getPlayerStatement() + "', '"
				+ player.getOwnerNotes() + "', "
				+ (player.getPlayerCategory()!=null?player.getPlayerCategory().getId():null) + ", "
				+ player.getMotherclubId() + ", '"
				+ DBManager.insertEscapeSequences(player.getMotherclubName() ) + "', "
				+ player.getMatchesCurrentTeam()
				+ ")";
		adapter.executeUpdate(statement);
	}

	/**
	 * Saves the players in the <code>spieler</code> list.
	 */
	void saveSpieler(int hrfId, List<Player> spieler, Timestamp date) {
		final String[] awhereS = { "HRF_ID" };
		final String[] awhereV = { "" + hrfId };

		if (spieler != null) {
			// Delete old values
			delete(awhereS, awhereV);

			for (Player p: spieler) {
				saveSpieler(hrfId, p, date);
			}
		}
	}

	/**
	 * get a player from a specific HRF
	 *
	 * @param hrfID hrd id
	 * @param playerId player id
	 *
	 *
	 * @return player
	 */
	Player getSpielerFromHrf(int hrfID, int playerId) {
		ResultSet rs;
		Player player;
		String sql;

		sql = "SELECT * from "+getTableName()+" WHERE HRF_ID = " + hrfID + " AND SpielerId="+playerId;
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				rs.beforeFirst();

				if (rs.next()) {
					player = createObject(rs);
					return player;
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
		String sql;
		final ArrayList<Player> ret = new ArrayList<>();
		if ( hrfID > -1) {

			sql = "SELECT * from " + getTableName() + " WHERE HRF_ID = " + hrfID;
			rs = adapter.executeQuery(sql);

			try {
				if (rs != null) {
					rs.beforeFirst();

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

	/**
	 * gibt alle Player zurück, auch ehemalige
	 */
	Vector<Player> getAllSpieler() {
		ResultSet rs;
		Player player;
		String sql;
		final Vector<Player> ret = new Vector<>();

		sql = "SELECT DISTINCT SpielerID from "+getTableName()+"";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				final Vector<Integer> idVector = new Vector<>();
				rs.beforeFirst();

				while (rs.next()) {
					idVector.add(rs.getInt("SpielerID"));
				}

				for (Integer integer : idVector) {
					sql = "SELECT * from " + getTableName() + " WHERE SpielerID=" + integer + " ORDER BY Datum DESC";
					rs = adapter.executeQuery(sql);

					if (rs != null && rs.first()) {
						player = createObject(rs);

						//HOLogger.instance().log(getClass(), player.getSpielerID () );
						ret.add(player);
					}
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getPlayer: " + e);
		}

		return ret;
	}


	/**
	 * Gibt die letzte Bewertung für den Player zurück // HRF
	 */
	int getLetzteBewertung4Spieler(int spielerid) {
		int bewertung = 0;

		try {
			final String sql = "SELECT Bewertung from "+getTableName()+" WHERE SpielerID=" + spielerid + " AND Bewertung>0 ORDER BY Datum DESC";
			final ResultSet rs = adapter.executeQuery(sql);

			if ((rs != null) && rs.first()) {
				bewertung = rs.getInt("Bewertung");
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getLetzteBewertung4Spieler : " + spielerid + " : " + e);
		}

		return bewertung;
	}

	/**
	 * Gibt einen Player zurück mit den Daten kurz vor dem Timestamp
	 */
	Player getSpielerAtDate(int spielerid, Timestamp time) {
		ResultSet rs;
		Player player = null;
		String sql;

		//6 Tage   //1209600000  //14 Tage vorher
		final int spanne = 518400000;

		if (time == null) {
			return null;
		}

		//--- Zuerst x Tage vor dem Datum suchen -------------------------------
		//x Tage vorher
		final Timestamp time2 = new Timestamp(time.getTime() - spanne);

		//HOLogger.instance().log(getClass(),"Time : " + time + " : vor 14 Tage : " + time2 );
		sql = "SELECT * from "+getTableName()+" WHERE Datum<='" + time + "' AND Datum>='" + time2 + "' AND SpielerID=" + spielerid + " ORDER BY Datum DESC";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					player = createObject(rs);

					//HOLogger.instance().log(getClass(), "Player " + player.getName () + " vom " + rs.getTimestamp ( "Datum" ) );
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"1. Player nicht gefunden für Datum " + time + " und SpielerID " + spielerid);
		}

		//--- Dann ein HRF später versuchen, Dort muss er dann eigenlich vorhanden sein! ---
		if (player == null) {
			sql = "SELECT * from "+getTableName()+" WHERE Datum>'" + time + "' AND SpielerID=" + spielerid + " ORDER BY Datum";
			rs = adapter.executeQuery(sql);

			try {
				if (rs != null) {
					if (rs.first()) {
						player = createObject(rs);

						//HOLogger.instance().log(getClass(), "Player " + player.getName () + " vom " + rs.getTimestamp ( "Datum" ) );
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

			//HOLogger.instance().log(getClass(),"Time : " + time + " : vor 14 Tage : " + time2 );
			sql = "SELECT * from "+getTableName()+" WHERE Datum<='" + time2 + "' AND Datum>='" + time3 + "' AND SpielerID=" + spielerid + " ORDER BY Datum DESC";
			rs = adapter.executeQuery(sql);

			try {
				if (rs != null) {
					if (rs.first()) {
						player = createObject(rs);

						//HOLogger.instance().log(getClass(), "Player " + player.getName () + " vom " + rs.getTimestamp ( "Datum" ) );
					}
				}
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"3. Player nicht gefunden für Datum " + time + " und SpielerID " + spielerid);
			}
		}

		return player;
	}

	//------------------------------------------------------------------------------

	/**
	 * Gibt einen Player zurück aus dem ersten HRF
	 */
	Player getSpielerFirstHRF(int spielerid) {
		ResultSet rs;
		Player player = null;
		String sql;

		sql = "SELECT * from "+getTableName()+" WHERE SpielerID=" + spielerid + " ORDER BY Datum ASC";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
					player = createObject(rs);

					//Info, da der Player für den Vergleich in der Spielerübersicht benutzt wird
					player.setOld(true);
//					HOLogger.instance().log(getClass(),"Player " + player.getName() + " vom " + rs.getTimestamp("Datum"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"Nicht gefunden SpielerID " + spielerid);
		}

		return player;
	}

	/**
	 * Gibt das Datum des ersten HRFs zurück, in dem der Player aufgetaucht ist
	 */
	Timestamp getTimestamp4FirstPlayerHRF(int spielerid) {
		Timestamp time = null;

		try {
			final String sql = "SELECT Datum from "+getTableName()+" WHERE SpielerID=" + spielerid + " ORDER BY Datum";
			final ResultSet rs = adapter.executeQuery(sql);

			if ((rs != null) && rs.first()) {
				time = rs.getTimestamp("Datum");
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getLetzteBewertung4Spieler : " + spielerid + " : " + e);
		}

		return time;
	}

	/**
	 * Gibt einen Player zurï¿½ck mit den Daten kurz vor dem Timestamp
	 */
	int getTrainerType(int hrfID) {
		ResultSet rs;
		String sql;

		sql = "SELECT TrainerTyp FROM "+getTableName()+" WHERE HRF_ID=" + hrfID + " AND TrainerTyp >=0 AND Trainer >0 order by Trainer desc";
		rs = adapter.executeQuery(sql);

		try {
			if (rs != null) {
				if (rs.first()) {
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
        	player.setPlayerID(rs.getInt("SpielerID"));
            player.setFirstName(DBManager.deleteEscapeSequences(rs.getString("FirstName")));
			player.setNickName(DBManager.deleteEscapeSequences(rs.getString("NickName")));
			player.setLastName(DBManager.deleteEscapeSequences(rs.getString("LastName")));
			player.setArrivalDate(DBManager.deleteEscapeSequences(rs.getString("ArrivalDate")));
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
						DBManager.deleteEscapeSequences(rs.getString("LastMatchDate")),
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
			player.setMotherClubName(DBManager.deleteEscapeSequences(rs.getString("MotherClubName")));
			player.setMatchesCurrentTeam(DBManager.getInteger(rs,"MatchesCurrentTeam"));

		} catch (Exception e) {
            HOLogger.instance().log(getClass(),e);
        }
        return player;
    }
}
