package core.db;

import core.HO;
import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.db.backup.BackupDialog;
import core.db.user.UserManager;
import core.file.hrf.HRF
import core.gui.comp.table.HOTableModel
import core.gui.model.ArenaStatistikTableModel
import core.gui.model.PlayerMatchCBItem;
import core.gui.theme.TeamLogoInfo;
import core.model.*;
import core.model.Tournament.TournamentDetails;
import core.model.enums.DBDataSource;
import core.model.enums.MatchType;
import core.model.match.*;
import core.model.misc.Basics
import core.model.misc.Economy
import core.model.misc.Verein
import core.model.player.Player;
import core.model.player.Skillup
import core.model.series.Liga
import core.model.series.Paarung
import core.training.FuturePlayerTraining
import core.util.HODateTime;
import core.training.TrainingPerWeek;
import core.util.HOLogger;
import core.util.ExceptionUtils;
import module.ifa.IfaMatch
import module.lineup.substitution.model.Substitution
import module.matches.MatchLocation
import module.nthrf.NtTeamDetails
import module.series.Spielplan
import module.teamAnalyzer.vo.PlayerInfo
import module.teamAnalyzer.vo.SquadInfo
import module.teamAnalyzer.vo.Team
import module.transfer.PlayerTransfer
import module.transfer.TransferType
import module.transfer.scout.ScoutEintrag
import module.youth.YouthPlayer
import module.youth.YouthTrainerComment
import module.youth.YouthTraining
import org.hsqldb.error.ErrorCode;
import tool.arenasizer.Stadium
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors
import javax.swing.JOptionPane

/**
 * The type Db manager.
 */
object DBManager {

	/** database versions */
    const val DBVersion :Int = 800 // HO 8.0 version

	/**
	 * Previous db version is used by development versions to ensure that db upgrade will rerun on each
	 * new installed preliminary version
	 */
	private const val previousDBVersion: Int = 701
	private const val DBConfigVersion: Double = 8.0 // HO 8.0 version

	/** 2004-06-14 11:00:00.0 */
	val TSIDATE: Timestamp = Timestamp(1_087_203_600_000L)

	// ~ Instance fields
	// ----------------------------------------------------------------------------

	/** DB-Adapter */
	 val jdbcAdapter: JDBCAdapter = JDBCAdapter()

	/** all Tables */
    private val tables = mutableMapOf<String, AbstractTable>()
    var firstStart: Boolean = false


    fun getVersion(): Int = if (HO.development) previousDBVersion else DBVersion

	/**
	 * Instance db manager.
	 *
	 * @return the db manager
	 */

    init {
        initialize()
    }

    private fun initialize() {
        var errorMsg:String? = null

        try {
            val currentUser = UserManager.getCurrentUser()
            val dbFolder = currentUser.dbFolder
            val dbfolder = File(dbFolder)

            if (!dbfolder.exists()) {
                val parentFolder = File(UserManager.dbParentFolder)
                var dbDirectoryCreated = false
                    if (!parentFolder.exists() || parentFolder.canWrite()) {
                        dbDirectoryCreated = dbfolder.mkdirs();
                    } else {
                        errorMsg = "Could not initialize the database folder."
                        errorMsg += "No writing rights to the following directory\n" + parentFolder.getAbsolutePath() + "\n";
                        errorMsg += "You can report this error by opening a new bug ticket on GitHub";
                    }
                    if (!dbDirectoryCreated) {
                        errorMsg = "Could not create the database folder: " + dbfolder.getAbsolutePath();
                    }
                }

            } catch (e: Exception) {
                errorMsg = "Error encountered during database initialization: \n" + UserManager.getCurrentUser().getDbURL()
                e.printStackTrace()
            }

            if (errorMsg != null) {
                javax.swing.JOptionPane.showMessageDialog(null, errorMsg, "Fatal DB Error", javax.swing.JOptionPane.ERROR_MESSAGE)
                System.exit(-1)
            }

            val dbUpdater = DBUpdater()

            initAllTables()
            // Try connecting to the DB
            try {
                connect()
//				dbUpdater.setDbManager(tempInstance)
            } catch (e: Exception) {

                var msg:String? = e.message
                var recover = true

                if (msg!!.contains("The database is already in use by another process") ||
                    checkLockFileFailure(e)) {
                    msg = if ((msg.contains("Permission denied"))
                        || msg.contains("system cannot find the path")) {
                        "Could not write to database. Make sure you have write access to the HO directory and its sub-directories.\n" +
                                "If under Windows make sure to stay out of Program Files or similar."
                    } else {
                        "The database is already in use. You have another HO running\n or the database is still closing. Wait and try again."
                    }
                    recover = false;
                } else {
                    msg = "Fatal database error. Exiting HO!\nYou should restore the db-folder from backup or delete that folder."
                }

                JOptionPane.showMessageDialog(null, msg, "Fatal DB Error", JOptionPane.ERROR_MESSAGE)

                if (recover) {
                    val dialog = BackupDialog()
                    dialog.isVisible = true
                    while (dialog.isVisible) {
                        // wait
                    }
                }

                HOLogger.instance().error(DBManager.javaClass, msg)
                System.exit(-1)
            }

            // Does DB already exists?
            val existsDB:Boolean = checkIfDBExists()

            // for startup
            firstStart = !existsDB

            // Do we need to create the database from scratch?
            if (!existsDB) {
                try {
                    createAllTables()
                } catch (e:SQLException) {
                    throw RuntimeException(e)
                }
                val configTable:UserConfigurationTable = this.getTable(UserConfigurationTable.TABLENAME) as UserConfigurationTable
                configTable.storeConfigurations(UserParameter.instance())
                configTable.storeConfigurations(HOParameter.instance())
            } else {
                // Check if there are any updates on the database to be done.
                dbUpdater.updateDB(DBVersion)
            }

            HOLogger.instance().info(javaClass,
				"instance ${UserManager.getCurrentUser().getDbURL()}; parent folder: ${UserManager.dbParentFolder}"
			)

    }

    private fun checkLockFileFailure(e: Exception): Boolean {
        if (e is SQLException) {
            return e.errorCode == ErrorCode.LOCK_FILE_ACQUISITION_FAILURE ||
                    e.errorCode == -ErrorCode.LOCK_FILE_ACQUISITION_FAILURE
        }
        return false
    }

	private val preparedStatements = mutableMapOf<String, PreparedStatement?>()

	fun getPreparedStatement(sql:String): PreparedStatement? {
		var ret = preparedStatements.get(sql)
		if (ret == null) {
			ret = jdbcAdapter.createPreparedStatement(sql)
			preparedStatements.put(sql, ret)
		}
		return ret
	}

	fun updateConfig() {
		DBConfigUpdater.updateDBConfig(DBConfigVersion);
	}


	private fun initAllTables() {
		val adapter = this.jdbcAdapter

        tables[BasicsTable.TABLENAME] = BasicsTable(adapter)
        tables[TeamTable.TABLENAME] = TeamTable(adapter)
        tables[NtTeamTable.TABLENAME] = NtTeamTable(adapter)
        tables[FaktorenTable.TABLENAME] = FaktorenTable(adapter)
        tables[HRFTable.TABLENAME] = HRFTable(adapter)
        tables[StadionTable.TABLENAME] = StadionTable(adapter)
        tables[VereinTable.TABLENAME] = VereinTable(adapter)
		tables[LigaTable.TABLENAME] = LigaTable(adapter)
		tables[SpielerTable.TABLENAME] = SpielerTable(adapter)
		tables[EconomyTable.TABLENAME] = EconomyTable(adapter)
		tables[YouthPlayerTable.TABLENAME] = YouthPlayerTable(adapter)
		tables[YouthScoutCommentTable.TABLENAME] = YouthScoutCommentTable(adapter)
		tables[YouthTrainingTable.TABLENAME] = YouthTrainingTable(adapter)
		tables[TeamsLogoTable.TABLENAME] = TeamsLogoTable(adapter)
		tables[ScoutTable.TABLENAME] = ScoutTable(adapter)
		tables[UserColumnsTable.TABLENAME] = UserColumnsTable(adapter)
		tables[SpielerNotizenTable.TABLENAME] = SpielerNotizenTable(adapter)
		tables[SpielplanTable.TABLENAME] = SpielplanTable(adapter)
		tables[PaarungTable.TABLENAME] = PaarungTable(adapter)
		tables[MatchLineupTeamTable.TABLENAME] = MatchLineupTeamTable(adapter)
		tables[MatchLineupTable.TABLENAME] = MatchLineupTable(adapter);
		tables[XtraDataTable.TABLENAME] = XtraDataTable(adapter);
		tables[MatchLineupPlayerTable.TABLENAME] = MatchLineupPlayerTable(adapter)
		tables[MatchesKurzInfoTable.TABLENAME] = MatchesKurzInfoTable(adapter)
		tables[MatchDetailsTable.TABLENAME] = MatchDetailsTable(adapter)
		tables[MatchHighlightsTable.TABLENAME] = MatchHighlightsTable(adapter)
		tables[TrainingsTable.TABLENAME] = TrainingsTable(adapter)
		tables[FutureTrainingTable.TABLENAME] = FutureTrainingTable(adapter)
		tables[UserConfigurationTable.TABLENAME] = UserConfigurationTable(adapter)
		tables[SpielerSkillupTable.TABLENAME] = SpielerSkillupTable(adapter)
		tables[StaffTable.TABLENAME] = StaffTable(adapter)
		tables[MatchSubstitutionTable.TABLENAME] = MatchSubstitutionTable(adapter)
		tables[TransferTable.TABLENAME] = TransferTable(adapter)
		tables[TransferTypeTable.TABLENAME] = TransferTypeTable(adapter)
		tables[ModuleConfigTable.TABLENAME] = ModuleConfigTable(adapter)
		tables[TAFavoriteTable.TABLENAME] = TAFavoriteTable(adapter)
		tables[TAPlayerTable.TABLENAME] = TAPlayerTable(adapter)
		tables[WorldDetailsTable.TABLENAME] = WorldDetailsTable(adapter)
		tables[IfaMatchTable.TABLENAME] = IfaMatchTable(adapter)
		tables[TournamentDetailsTable.TABLENAME] = TournamentDetailsTable(adapter)
        tables[FuturePlayerTrainingTable.TABLENAME] = FuturePlayerTrainingTable(adapter)
        tables[MatchTeamRatingTable.TABLENAME] = MatchTeamRatingTable(adapter)
		tables[SquadInfoTable.TABLENAME] = SquadInfoTable(adapter)
	}

	/**
	 * Gets table.
	 *
	 * @param tableName the table name
	 * @return the table
	 */
    fun getTable(tableName: String):AbstractTable? = tables[tableName]

	/**
	 * disconnect from database
	 */
	fun disconnect() {
        jdbcAdapter.disconnect()
    }

	/**
	 * connect to the database
	 */

	fun connect() {
        val currentUser = UserManager.getCurrentUser()
        jdbcAdapter.connect(currentUser.getDbURL(), currentUser.dbUsername, currentUser.dbPwd, UserManager.getDriver())
	}

	/**
	 * check if tables in DB exists
	 * 
	 * @return boolean
	 */
	private fun checkIfDBExists(): Boolean {
		val exists = try {
			val rs = jdbcAdapter.executeQuery("SELECT Count(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'")
			rs!!.next()
			rs.getInt(1) > 0
		} catch(e:Exception) {
			HOLogger.instance().error(javaClass, ExceptionUtils.getStackTrace(e))
			false
		}
		return exists;
	}


	/**
	 * get the date of the last level increase of given player
	 *
	 * @param skill     integer code for the skill
	 * @param playerId player ID
	 * @return [0] = Time of change  [1] = Boolean: false=no skill change found
	 */
	fun getLastLevelUp(skill:Int, playerId:Int): Skillup? {
		return (this.getTable(SpielerSkillupTable.TABLENAME) as SpielerSkillupTable).getLastLevelUp(skill, playerId)
	}

	/**
	 * liefert das Datum des letzen LevelAufstiegs für den angeforderten Skill
	 * Vector filled with Skillup Objects
	 *
	 * @param skill        the skill
	 * @param playerId the m i spieler id
	 * @return the all level up
	 */
	fun getAllLevelUp(skill: Int, playerId: Int): List<Skillup?>  {
		return (this.getTable(SpielerSkillupTable.TABLENAME) as SpielerSkillupTable).getAllLevelUp(skill, playerId)
	}

	/**
	 * Check skillup.
	 *
	 * @param homodel the homodel
	 */
	fun checkSkillup(homodel: HOModel) {
		(getTable(SpielerSkillupTable.TABLENAME) as SpielerSkillupTable).importNewSkillup(homodel)
	}

	fun storeSkillup(skillup: Skillup) {
		getTable(SpielerSkillupTable.TABLENAME)!!.store(skillup)
	}

	// ------------------------------- SpielerTable
	// -------------------------------------------------

	/**
	 * Returns all the players, including former players.
	 *
	 * @return
	 */
	fun loadAllPlayers(): List<Player?> {
		return (getTable(SpielerTable.TABLENAME) as SpielerTable).loadAllPlayers()
	}

	/**
	 * Gibt die letzte Bewertung für den Player zurück // HRF
	 *
	 * @param playerId the playerId
	 * @return the letzte bewertung 4 spieler
	 */
	fun getLetzteBewertung4Spieler(playerId: Int): Int {
		return (getTable(SpielerTable.TABLENAME) as SpielerTable).getLatestRatingOfPlayer(playerId)
	}

	/**
	 * lädt die Player zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the spieler
	 */
	fun getSpieler(hrfID: Int): List<Player?> {
		return (getTable(SpielerTable.TABLENAME) as SpielerTable).loadPlayers(hrfID)
	}

	/**
	 * store youth players
	 *
	 * @param hrfId  the hrf id
	 * @param youthPlayers the list of youth players
	 */
	fun storeYouthPlayers(hrfId: Int, youthPlayers: List<YouthPlayer>) {
		val youthPlayerTable = getTable(YouthPlayerTable.TABLENAME) as YouthPlayerTable
		youthPlayerTable.deleteYouthPlayers(hrfId)
		for (youthPlayer in youthPlayers) {
			youthPlayer.stored = false
			storeYouthPlayer(hrfId, youthPlayer)
		}
	}

	/**
	 * store youth players
	 *
	 * @param hrfId  the hrf id
	 * @param youthPlayer the youth player
	 */
	fun storeYouthPlayer(hrfId: Int, youthPlayer: YouthPlayer) {
		(getTable(YouthPlayerTable.TABLENAME) as YouthPlayerTable).storeYouthPlayer(hrfId, youthPlayer)
		val youthScoutCommentTable = getTable(YouthScoutCommentTable.TABLENAME) as YouthScoutCommentTable
		youthScoutCommentTable.storeYouthScoutComments(youthPlayer.id, youthPlayer.getScoutComments())
	}

	/**
	 * Load youth players list.
	 *
	 * @param hrfID the hrf id
	 * @return the list
	 */
	fun loadYouthPlayers(hrfID: Int): List<YouthPlayer?> {
		return (getTable(YouthPlayerTable.TABLENAME) as YouthPlayerTable).loadYouthPlayers(hrfID)
	}

	/**
	 * Load youth scout comments list.
	 *
	 * @param id the id
	 * @return the list
	 */
	fun loadYouthScoutComments(id: Int): List<YouthPlayer.ScoutComment?> {
		return (getTable(YouthScoutCommentTable.TABLENAME) as YouthScoutCommentTable).loadYouthScoutComments(id)
	}

	/**
	 * Load youth player of match date youth player.
	 *
	 * @param id   the id
	 * @param date the date
	 * @return the youth player
	 */
	fun loadYouthPlayerOfMatchDate(id: Int, date: Timestamp): YouthPlayer? {
		return (getTable(YouthPlayerTable.TABLENAME) as YouthPlayerTable).loadYouthPlayerOfMatchDate(id, date)
	}

	/**
	 * Returns a player at a date around Timestamp
	 *
	 * @param playerId the player ID
	 * @param time      the time
	 * @return the player at date
	 */
	fun getSpielerAtDate(playerId: Int, time: Timestamp?): Player? {
		return (getTable(SpielerTable.TABLENAME) as SpielerTable).getSpielerNearDate(playerId, time)
	}

	/**
	 * Gibt einen Player zurück aus dem ersten HRF
	 *
	 * @param playerId the playerId
	 * @return the spieler first hrf
	 */
	@JvmOverloads
	fun loadPlayerFirstHRF(playerId: Int, afterDate: HODateTime? = null): Player? {
		var after = afterDate
		if (after == null) {
			after = HODateTime.HT_START
		}
		return (getTable(SpielerTable.TABLENAME) as SpielerTable).getSpielerFirstHRF(playerId, after!!.toDbTimestamp())
	}

	/**
	 * Returns the trainer code for the specified hrf. -99 if error
	 *
	 * @param hrfID HRF for which to load TrainerType
	 * @return int trainer type
	 */
	fun getTrainerType(hrfID: Int): Int {
		return (getTable(SpielerTable.TABLENAME) as SpielerTable).getTrainerType(hrfID)
	}

	/**
	 * store list of Player
	 *
	 * @param player the player
	 */
	fun saveSpieler(player: List<Player?>?) {
		(getTable(SpielerTable.TABLENAME) as SpielerTable?)!!.store(player)
	}

	// ------------------------------- LigaTable
	// -------------------------------------------------
		/**
		 * Gibt alle bekannten Ligaids zurück
		 *
		 * @return the integer [ ]
		 */

	fun getAllLigaIDs(): Array<Int?>? {
		return (getTable(SpielplanTable.TABLENAME) as SpielplanTable).getAllLigaIDs()
	}
	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the liga
	 */
	fun getLiga(hrfID: Int): Liga? {
		return (getTable(LigaTable.TABLENAME) as LigaTable).getLiga(hrfID)
	}

	/**
	 * speichert die Basdics
	 *
	 * @param hrfId the hrf id
	 * @param liga  the liga
	 */
	fun saveLiga(hrfId: Int, liga: Liga) {
		(getTable(LigaTable.TABLENAME) as LigaTable?)!!.saveLiga(hrfId, liga)
	}
	// ------------------------------- SpielplanTable
	// -------------------------------------------------
	/**
	 * Gibt eine Ligaid zu einer Seasonid zurück, oder -1, wenn kein Eintrag in
	 * der DB gefunden wurde
	 *
	 * @param seasonId the seasonid
	 * @return the liga id 4 saison id
	 */
	fun getLigaID4SaisonID(seasonId: Int): Int {
		return (getTable(SpielplanTable.TABLENAME) as SpielplanTable).getLigaID4SaisonID(seasonId)
	}

	/**
	 * holt einen Spielplan aus der DB, -1 bei den params holt den zuletzt
	 * gesavten Spielplan
	 *
	 * @param ligaId Id der Liga
	 * @param saison die Saison
	 * @return the spielplan
	 */
	fun getSpielplan(ligaId: Int, saison: Int): Spielplan? {
		val ret = (getTable(SpielplanTable.TABLENAME) as SpielplanTable?)!!.getSpielplan(ligaId, saison)
		ret?.addFixtures(loadFixtures(ret))
		return ret
	}

	fun getLatestSpielPlan(): Spielplan? {
		val ret = (getTable(SpielplanTable.TABLENAME) as SpielplanTable).getLatestSpieplan()
		ret?.addFixtures(loadFixtures(ret))
		return ret
	}
	/**
	 * speichert einen Spielplan mitsamt Paarungen
	 *
	 * @param plan the plan
	 */
	fun storeSpielplan(plan: Spielplan) {
		(getTable(SpielplanTable.TABLENAME) as SpielplanTable).storeSpielplan(plan)
		storePaarung(plan.matches, plan.ligaId, plan.saison)
	}

	fun deleteSpielplanTabelle(saison: Int, ligaId: Int) {
		val table = getTable(SpielplanTable.TABLENAME) as SpielplanTable
		table.executePreparedDelete(saison, ligaId)
	}

	/**
	 * lädt alle Spielpläne aus der DB
	 *
	 * @param withFixtures inklusive der Paarungen ja/nein
	 * @return the spielplan [ ]
	 */
	fun getAllSpielplaene(withFixtures: Boolean): List<Spielplan?> {
		val ret = (getTable(SpielplanTable.TABLENAME) as SpielplanTable).getAllSpielplaene()
		if (withFixtures) {
			for (gameSchedule in ret) {
				gameSchedule!!.addFixtures(loadFixtures(gameSchedule))
			}
		}
		return ret
	}
	// ------------------------------- MatchLineupPlayerTable
	// -------------------------------------------------
	/**
	 * Returns a list of ratings the player has played on [Max, Min, Average, posid]
	 *
	 * @param playerId the playerId
	 * @return the alle bewertungen
	 */
	fun getAlleBewertungen(playerId: Int): Vector<FloatArray> {
		return (getTable(MatchLineupPlayerTable.TABLENAME) as MatchLineupPlayerTable)
			.getAllRatings(playerId)
	}

	/**
	 * Gibt die beste, schlechteste und durchschnittliche Bewertung für den
	 * Player, sowie die Anzahl der Bewertungen zurück // Match
	 *
	 * @param playerId the playerId
	 * @return the float [ ]
	 */
	fun getBewertungen4Player(playerId: Int): FloatArray {
		return (getTable(MatchLineupPlayerTable.TABLENAME) as MatchLineupPlayerTable).getBewertungen4Player(playerId)
	}

	/**
	 * Gibt die beste, schlechteste und durchschnittliche Bewertung für den
	 * Player, sowie die Anzahl der Bewertungen zurück // Match
	 *
	 * @param playerId playerId
	 * @param position  Usere positionscodierung mit taktik
	 * @return the float [ ]
	 */
	fun getBewertungen4PlayerUndPosition(playerId: Int, position: Byte): FloatArray {
		return (getTable(MatchLineupPlayerTable.TABLENAME) as MatchLineupPlayerTable)
			.getPlayerRatingForPosition(playerId, position.toInt())
	}

	/**
	 * Gets match lineup players.
	 *
	 * @param matchID the match id
	 * @param matchType MatchType
	 * @param teamID  the team id
	 * @return the match lineup players
	 */
	fun getMatchLineupPlayers(
		matchID: Int,
		matchType: MatchType, teamID: Int
	): List<MatchLineupPosition?> {
		return (getTable(MatchLineupPlayerTable.TABLENAME) as MatchLineupPlayerTable)
			.getMatchLineupPlayers(matchID, matchType, teamID)
	}

	/**
	 * Get match inserts of given Player
	 *
	 * @param objectPlayerID id of the player
	 * @return stored lineup positions of the player
	 */
	fun getMatchInserts(objectPlayerID: Int): List<MatchLineupPosition?> {
		return (getTable(MatchLineupPlayerTable.TABLENAME) as MatchLineupPlayerTable)
			.getMatchInserts(objectPlayerID)
	}

	/**
	 * Get the top or flop players of given lineup position in given matches
	 *
	 * @param position lineup position sector
	 * @param matches list of matches
	 * @param isBest true: the best player is listed first
	 * @return stored lineup positions
	 */
	fun loadTopFlopRatings(
		matches: List<Paarung>,
		position: Int,
		count: Int,
		isBest: Boolean
	): List<MatchLineupPosition?> {
		return (getTable(MatchLineupPlayerTable.TABLENAME) as MatchLineupPlayerTable)
			.loadTopFlopRatings(matches, position, count, isBest)
	}
	// ------------------------------- BasicsTable
	// -------------------------------------------------
	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the basics
	 */
	fun getBasics(hrfID: Int): Basics {
		return (getTable(BasicsTable.TABLENAME) as BasicsTable).loadBasics(hrfID)
	}

	/**
	 * Returns an HRF before the matchData and after previous TrainingTime
	 *
	 * @param matchTime matchData
	 * @return hrfId hrf id same training
	 */
	fun getHrfIDSameTraining(matchTime: Timestamp?): Int {
		return (getTable(BasicsTable.TABLENAME) as BasicsTable).getHrfIDSameTraining(matchTime)
	}

	/**
	 * speichert die Basdics
	 *
	 * @param hrfId  the hrf id
	 * @param basics the basics
	 */
	fun saveBasics(hrfId: Int, basics: Basics) {
		(getTable(BasicsTable.TABLENAME) as BasicsTable?)!!.saveBasics(hrfId, basics)
	}

	/**
	 * Sets faktoren from db.
	 *
	 * @param fo the fo
	 */
	// ------------------------------- FaktorenTable
	// -------------------------------------------------
	fun setFaktorenFromDB(fo: FactorObject?) {
		(getTable(FaktorenTable.TABLENAME) as FaktorenTable).pushFactorsIntoDB(fo)
	}

		/**
		 * Gets faktoren from db.
		 */

	fun getFaktorenFromDB() {
		return (getTable(FaktorenTable.TABLENAME) as FaktorenTable).getFaktorenFromDB()
	}

	/**
	 * Gets tournament details from db.
	 *
	 * @param tournamentId the tournament id
	 * @return the tournament details from db
	 */
	// Tournament Details
	fun getTournamentDetailsFromDB(tournamentId: Int): TournamentDetails? {
		return (getTable(TournamentDetailsTable.TABLENAME) as TournamentDetailsTable)
			.getTournamentDetails(tournamentId)
	}

	/**
	 * Store tournament details into db.
	 *
	 * @param oTournamentDetails the o tournament details
	 */
	fun storeTournamentDetailsIntoDB(oTournamentDetails: TournamentDetails) {
		(getTable(TournamentDetailsTable.TABLENAME) as TournamentDetailsTable)
			.storeTournamentDetails(oTournamentDetails)
	}
	// ------------------------------- FinanzenTable
	// -------------------------------------------------
	/**
	 * fetch the Economy table from the DB for the specified HRF ID
	 *
	 * @param hrfID the hrf id
	 * @return the economy
	 */
	fun getEconomy(hrfID: Int): Economy? {
		return (getTable(EconomyTable.TABLENAME) as EconomyTable).getEconomy(hrfID)
	}

	/**
	 * store the economy info in the database
	 *
	 * @param hrfId   the hrf id
	 * @param economy the economy
	 * @param date    the date
	 */
	fun saveEconomyInDB(hrfId: Int, economy: Economy?, date: HODateTime?) {
		(getTable(EconomyTable.TABLENAME) as EconomyTable?)!!.storeEconomyInfoIntoDB(hrfId, economy, date)
	}
	// ------------------------------- HRFTable
	// -------------------------------------------------
	/**
	 * Get a list of all HRFs
	 *
	 * @param asc   order ascending (descending otherwise)
	 * @return all matching HRFs
	 */
	fun loadAllHRFs(asc: Boolean): Array<HRF?> {
		return (getTable(HRFTable.TABLENAME) as HRFTable).loadAllHRFs(asc)
	}

	/**
	 * get the latest imported hrf
	 * this does not have to be the latest downloaded, if the user imported hrf files in any order from files
	 * @return HRF object
	 */
	fun getMaxIdHrf():HRF = (getTable(HRFTable.TABLENAME) as HRFTable).getMaxHrf()

	/**
	 * get the latest downloaded hrf
	 * @return HRF object
	 */
	fun getLatestHRF(): HRF = (getTable(HRFTable.TABLENAME) as HRFTable).getLatestHrf()

	fun loadHRF(id: Int): HRF? {
		return (getTable(HRFTable.TABLENAME) as HRFTable).loadHRF(id)
	}

	/**
	 * save the HRF info
	 */
	fun saveHRF(hrf: HRF) {
		(getTable(HRFTable.TABLENAME) as HRFTable).saveHRF(hrf)
	}

	/**
	 * Gets hrfid 4 date.
	 *
	 * @param time the time
	 * @return the hrfid 4 date
	 */
	fun getHRFID4Date(time: Timestamp?): Int {
		return (getTable(HRFTable.TABLENAME) as HRFTable?)!!.getHrfIdNearDate(time)
	}

	/**
	 * is there is an HRFFile in the database with the same date?
	 *
	 * @param fetchDate the date
	 * @return The date of the file to which the file was imported or zero if no suitable file is available
	 */
	fun loadHRFDownloadedAt(fetchDate: Timestamp?): HRF? {
		return (getTable(HRFTable.TABLENAME) as HRFTable).loadHRFDownloadedAt(fetchDate)
	}

	fun loadLatestHRFDownloadedBefore(fetchDate: Timestamp?): HRF? {
		return (getTable(HRFTable.TABLENAME) as HRFTable).loadLatestHRFDownloadedBefore(fetchDate)
	}

	// ------------------------------- SpielerNotizenTable
	// -------------------------------------------------
	fun storePlayerNotes(notes: Player.Notes) {
		(getTable(SpielerNotizenTable.TABLENAME) as SpielerNotizenTable).storeNotes(notes)
	}

	fun loadPlayerNotes(playerId: Int): Player.Notes {
		return (getTable(SpielerNotizenTable.TABLENAME) as SpielerNotizenTable).load(playerId)
	}
	// ------------------------------- MatchLineupTable
	// -------------------------------------------------
	/**
	 * Load match lineup match lineup.
	 *
	 * @param iMatchType the source system
	 * @param matchID      the match id
	 * @return the match lineup
	 */
	fun loadMatchLineup(iMatchType: Int, matchID: Int): MatchLineup? {
		val ret = (getTable(MatchLineupTable.TABLENAME) as MatchLineupTable?)!!.loadMatchLineup(iMatchType, matchID)
		if (ret != null) {
			val match = loadMatchDetails(iMatchType, matchID)
			ret.homeTeam = loadMatchLineupTeam(iMatchType, matchID, match.homeTeamId)
			ret.guestTeam = loadMatchLineupTeam(iMatchType, matchID, match.guestTeamId)
		}
		return ret
	}

	/**
	 * Is the match already in the database?
	 *
	 * @param iMatchType the source system
	 * @param matchid      the matchid
	 * @return the boolean
	 */
	fun matchLineupIsNotStored(iMatchType: MatchType, matchid: Int): Boolean {
		return !getTable(MatchLineupTable.TABLENAME)!!.isStored(matchid, iMatchType.id)
	}

	/**
	 * Is match ifk rating in db boolean.
	 *
	 * @param matchid the matchid
	 * @return the boolean
	 */
	fun isMatchIFKRatingInDB(matchid: Int): Boolean {
		return (getTable(MatchDetailsTable.TABLENAME) as MatchDetailsTable).isMatchIFKRatingAvailable(matchid)
	}

	/**
	 * Has unsure weather forecast boolean.
	 *
	 * @param matchId the match id
	 * @return the boolean
	 */
	fun hasUnsureWeatherForecast(matchId: Int): Boolean {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable?)!!.hasUnsureWeatherForecast(matchId)
	}
	// ------------------------------- MatchesKurzInfoTable
	// -------------------------------------------------
	/**
	 * Check if match is available
	 *
	 * @param matchid the matchid
	 * @param matchType type of the match
	 * @return the boolean
	 */
	fun isMatchInDB(matchid: Int, matchType: MatchType): Boolean {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable).isMatchInDB(matchid, matchType)
	}

	/**
	 * Returns the MatchKurzInfo for the match. Returns null if not found.
	 *
	 * @param matchid The ID for the match
	 * @param matchType type of the match
	 * @return The kurz info object or null
	 */
	fun getMatchesKurzInfoByMatchID(matchid: Int, matchType: MatchType?): MatchKurzInfo? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable)
			.getMatchesKurzInfoByMatchID(matchid, matchType)
	}

	/**
	 * Get all matches for the given team from the database.
	 *
	 * @param teamId the teamid or -1 for all matches
	 * @return the match kurz info [ ]
	 */
	fun getMatchesKurzInfo(teamId: Int): List<MatchKurzInfo?>? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable)
			.getMatchesKurzInfo(teamId)
	}

	fun getMatchesKurzInfo(where: String, vararg values: Any?): List<MatchKurzInfo?>? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable)
			.loadMatchesKurzInfo(where, *values)
	}

	/**
	 * Get last matches kurz info match kurz info.
	 *
	 * @param teamId the team id
	 * @return the match kurz info
	 */
	private fun getLastMatchesKurzInfo(teamId: Int): MatchKurzInfo? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable)
			.loadLastMatchesKurzInfo(teamId)
	}

	private fun getNextMatchesKurzInfo(teamId: Int): MatchKurzInfo? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable)
			.loadNextMatchesKurzInfo(teamId)
	}

	fun getLastMatchWithMatchId(matchId: Int): MatchKurzInfo? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable)
			.getLastMatchWithMatchId(matchId)
	}

	/**
	 * function that fetch info of match played related to the TrainingPerWeek instance
	 * @return MatchKurzInfo[] related to this TrainingPerWeek instance
	 */
	fun loadOfficialMatchesBetween(
		teamId: Int,
		firstMatchDate: HODateTime,
		lastMatchDate: HODateTime
	): List<MatchKurzInfo?>? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable).getMatchesKurzInfo(
			teamId,
			firstMatchDate.toDbTimestamp(),
			lastMatchDate.toDbTimestamp(),
			MatchType.getOfficialMatchTypes()
		)
	}

	/**
	 * function that fetch info of NT match played related to the TrainingPerWeek instance
	 * @return MatchKurzInfo[] related to this TrainingPerWeek instance
	 */
	fun loadNTMatchesBetween(teamId: Int, firstMatchDate: HODateTime, lastMatchDate: HODateTime): List<MatchKurzInfo?>? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable).getMatchesKurzInfo(
			teamId,
			firstMatchDate.toDbTimestamp(),
			lastMatchDate.toDbTimestamp(),
			MatchType.getNTMatchType()
		)
	}

	/**
	 * Get all matches with a certain status for the given team from the
	 * database.
	 *
	 * @param teamId      the teamid or -1 for all matches
	 * @param matchStatus the match status
	 * @return the match kurz info [ ]
	 */
	fun getMatchesKurzInfo(teamId: Int, matchStatus: Int): List<MatchKurzInfo?>? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable)
			.getMatchesKurzInfo(teamId, matchStatus)
	}

	/**
	 * Gets first upcoming match with team id.
	 *
	 * @param teamId the team id
	 * @return the first upcoming match with team id
	 */
	fun getFirstUpcomingMatchWithTeamId(teamId: Int): MatchKurzInfo? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable)
			.getFirstUpcomingMatchWithTeamId(teamId)
	}

	/**
	 * Get played match info array list (own team Only)
	 *
	 * @param iNbGames           the nb games
	 * @param bOfficialGamesOnly the b official games only
	 * @return the array list
	 */
	fun getOwnPlayedMatchInfo(iNbGames: Int?, bOfficialGamesOnly: Boolean): List<MatchKurzInfo?>? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable).getPlayedMatchInfo(
			iNbGames,
			bOfficialGamesOnly,
			true
		)
	}

	/**
	 * Get played match info array list (own team Only)
	 *
	 * @param iNbGames           the nb games
	 * @param bOfficialGamesOnly the b official games only
	 * @return the array list
	 */
	fun getPlayedMatchInfo(iNbGames: Int?, bOfficialGamesOnly: Boolean, ownTeam: Boolean): List<MatchKurzInfo?>? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable).getPlayedMatchInfo(
			iNbGames,
			bOfficialGamesOnly,
			ownTeam
		)
	}

	/**
	 * Returns an array of [MatchKurzInfo] for the team with ID `teamId`,
	 * and of type `matchtyp`.
	 * Important: if teamId is -1, `matchtype` must be set to
	 * `MatchesPanel.ALL_MATCHS`.
	 * @param teamId   The ID of the team, or -1 for all.
	 * @param iMatchType Type of match, as defined in [module.matches.MatchesPanel]
	 * @param matchLocation Home, Away, Neutral
	 *
	 * @return MatchKurzInfo[] – Array of match info.
	 */
	fun getMatchesKurzInfo(teamId: Int, iMatchType: Int, matchLocation: MatchLocation?): List<MatchKurzInfo?>? {
		return getMatchesKurzInfo(teamId, iMatchType, matchLocation, HODateTime.HT_START.toDbTimestamp(), true)
	}

	/**
	 * Returns an array of [MatchKurzInfo] for the team with ID `teamId`,
	 * and of type `matchtyp`.
	 * Important: if teamId is -1, `matchtype` must be set to
	 * `MatchesPanel.ALL_MATCHS`.
	 *
	 * @param teamId   The ID of the team, or -1 for all.
	 * @param iMatchType Type of match, as defined in [module.matches.MatchesPanel]
	 * @param matchLocation Home, Away, Neutral
	 * @param from filter match schedule date
	 * @param includeUpcoming if false filter finished matches only
	 * @return MatchKurzInfo[] – Array of match info.
	 */
	fun getMatchesKurzInfo(
		teamId: Int,
		iMatchType: Int,
		matchLocation: MatchLocation?,
		from: Timestamp?,
		includeUpcoming: Boolean
	): List<MatchKurzInfo?>? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable).getMatchesKurzInfo(
			teamId,
			iMatchType,
			matchLocation,
			from,
			includeUpcoming
		)
	}

	/**
	 * Get matches kurz info up coming match kurz info [ ].
	 *
	 * @param teamId the team id
	 * @return the match kurz info [ ]
	 */
	fun getMatchesKurzInfoUpComing(teamId: Int): List<MatchKurzInfo?>? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable)
			.getMatchesKurzInfoUpComing(teamId)
	}

	/**
	 * Gets matches kurz info.
	 *
	 * @param teamId    the team id
	 * @param matchType  the matchtyp
	 * @param statistic the statistic
	 * @param home      the home
	 * @return the matches kurz info
	 */
	fun getMatchesKurzInfo(
		teamId: Int,
		matchType: Int,
		statistic: Int, home: Boolean
	): MatchKurzInfo? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable)
			.getMatchesKurzInfo(teamId, matchType, statistic, home)
	}

	/**
	 * Gets matches kurz info statistics count.
	 *
	 * @param teamId    the team id
	 * @param matchtype the matchtype
	 * @param statistic the statistic
	 * @return the matches kurz info statistics count
	 */
	fun getMatchesKurzInfoStatisticsCount(
		teamId: Int, matchtype: Int,
		statistic: Int
	): Int {
		return MatchesOverviewQuery.getMatchesKurzInfoStatisticsCount(
			teamId,
			matchtype, statistic
		)
	}

	/**
	 * speichert die Matches
	 *
	 * @param matches the matches
	 */
	fun storeMatchKurzInfos(matches: List<MatchKurzInfo>) {
		(getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable).storeMatchKurzInfos(matches)
	}

	// ------------------------------- ScoutTable
	// -------------------------------------------------
		/**
		 * Load player list for insertion into TransferScout
		 *
		 * @return the scout list
		 */

	fun getScoutList(): Vector<ScoutEintrag?>? {
		return (getTable(ScoutTable.TABLENAME) as ScoutTable).scoutList?.let { Vector(it) }
	}
	/**
	 * Save players from TransferScout
	 *
	 * @param list the list
	 */
	fun saveScoutList(list: Vector<ScoutEintrag>) {
		(getTable(ScoutTable.TABLENAME) as ScoutTable).saveScoutList(list)
	}
	// ------------------------------- StadionTable
	// -------------------------------------------------
	/**
	 * lädt die Finanzen zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the stadion
	 */
	fun getStadion(hrfID: Int): Stadium? {
		return (getTable(StadionTable.TABLENAME) as StadionTable).getStadion(hrfID)
	}

	/**
	 * speichert die Finanzen
	 *
	 * @param hrfId   the hrf id
	 * @param stadion the stadion
	 */
	fun saveStadion(hrfId: Int, stadion: Stadium?) {
		(getTable(StadionTable.TABLENAME) as StadionTable?)!!.saveStadion(hrfId, stadion)
	}
	// ------------------------------- StaffTable
	// -------------------------------------------------
	/**
	 * Fetch a list of staff store din a hrf
	 *
	 * @param hrfId the hrf id
	 * @return A list of StaffMembers belonging to the given hrf
	 */
	fun getStaffByHrfId(hrfId: Int): List<StaffMember?>? {
		return (getTable(StaffTable.TABLENAME) as StaffTable).getStaffByHrfId(hrfId)
	}

	/**
	 * Stores a list of StaffMembers
	 *
	 * @param hrfId The hrfId
	 * @param list  The staff objects
	 */
	fun saveStaff(hrfId: Int, list: List<StaffMember>) {
		(getTable(StaffTable.TABLENAME) as StaffTable?)!!.storeStaff(hrfId, list)
	}
	// ------------------------------- MatchSubstitutionTable
	// -------------------------------------------------
	/**
	 * Returns an array with substitution belonging to the match-team.
	 *
	 * @param matchType  match type
	 * @param teamId       The teamId for the team in question
	 * @param matchId      The matchId for the match in question
	 * @return the match substitutions by match team
	 */
	fun getMatchSubstitutionsByMatchTeam(matchId: Int, matchType: MatchType, teamId: Int): List<Substitution?> {
		return (getTable(MatchSubstitutionTable.TABLENAME) as MatchSubstitutionTable)
			.getMatchSubstitutionsByMatchTeam(matchType.id, teamId, matchId)
	}

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the team
	 */
	fun getTeam(hrfID: Int): core.model.Team {
		return (getTable(TeamTable.TABLENAME) as TeamTable?)!!.getTeam(hrfID)
	}

	/**
	 * speichert das Team
	 *
	 * @param hrfId the hrf id
	 * @param team  the team
	 */
	fun saveTeam(hrfId: Int, team: core.model.Team?) {
		(getTable(TeamTable.TABLENAME) as TeamTable?)!!.saveTeam(hrfId, team)
	}

		/**
		 * Gets the content of TrainingsTable as a vector of TrainingPerWeek objects
		 */
	fun getTrainingList(): List<TrainingPerWeek?> {
		return (getTable(TrainingsTable.TABLENAME) as TrainingsTable).getTrainingList()
	}

	fun getTrainingList(fromDate: Timestamp?, toDate: Timestamp?): List<TrainingPerWeek?> {
		return (getTable(TrainingsTable.TABLENAME) as TrainingsTable).getTrainingList(fromDate, toDate)
	}

	fun saveTraining(training: TrainingPerWeek?, lastTrainingDate: HODateTime?) {
		(getTable(TrainingsTable.TABLENAME) as TrainingsTable).saveTraining(training, lastTrainingDate)
	}

	fun saveTrainings(trainings: List<TrainingPerWeek>, lastTrainingDate: HODateTime?) {
		(getTable(TrainingsTable.TABLENAME) as TrainingsTable).saveTrainings(trainings, lastTrainingDate)
	}

	// ------------------------------- FutureTrainingTable
	// -------------------------------------------------
		/**
		 * Gets future trainings vector.
		 *
		 * @return the future trainings vector
		 */

	fun getFutureTrainingsVector(): List<TrainingPerWeek?>? {
		return (getTable(FutureTrainingTable.TABLENAME) as FutureTrainingTable).getFutureTrainingsVector()
	}
	/**
	 * Save future training.
	 *
	 * @param training the training
	 */
	fun saveFutureTraining(training: TrainingPerWeek) {
		(getTable(FutureTrainingTable.TABLENAME) as FutureTrainingTable).storeFutureTraining(training)
	}

	fun saveFutureTrainings(trainings: List<TrainingPerWeek>) {
		(getTable(FutureTrainingTable.TABLENAME) as FutureTrainingTable).storeFutureTrainings(trainings)
	}

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the verein
	 */
	fun getVerein(hrfID: Int): Verein {
		return (getTable(VereinTable.TABLENAME) as VereinTable).loadVerein(hrfID)
	}

	/**
	 * speichert das Verein
	 *
	 * @param hrfId  the hrf id
	 * @param verein the verein
	 */
	fun saveVerein(hrfId: Int, verein: Verein?) {
		(getTable(VereinTable.TABLENAME) as VereinTable?)!!.saveVerein(
			hrfId,
			verein
		)
	}

	/**
	 * Gets futur training.
	 *
	 * @param trainingDate the saison
	 * @return the futur training type
	 */
	// ------------------------------- FutureTraining
	// -------------------------------------------------
	fun getFuturTraining(trainingDate: Timestamp?): TrainingPerWeek? {
		return (getTable(FutureTrainingTable.TABLENAME) as FutureTrainingTable).loadFutureTrainings(trainingDate)
	}
	// ------------------------------- XtraDataTable
	// -------------------------------------------------
	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the xtra daten
	 */
	fun getXtraDaten(hrfID: Int): XtraData? {
		return (getTable(XtraDataTable.TABLENAME) as XtraDataTable).loadXtraData(hrfID)
	}

	/**
	 * speichert das Team
	 *
	 * @param hrfId the hrf id
	 * @param xtra  the xtra
	 */
	fun saveXtraDaten(hrfId: Int, xtra: XtraData?) {
		(getTable(XtraDataTable.TABLENAME) as XtraDataTable?)!!.saveXtraDaten(
			hrfId, xtra
		)
	}
	// ------------------------------- UserParameterTable
	// -------------------------------------------------
	/**
	 * Lädt die UserParameter direkt in das UserParameter-SingeltonObjekt
	 */
	fun loadUserParameter() {
		val table = getTable(UserConfigurationTable.TABLENAME) as UserConfigurationTable?
		table!!.loadConfigurations(UserParameter.instance())
		table.loadConfigurations(HOParameter.instance())
	}

	/**
	 * Saves the user parameters in the database.
	 */
	fun saveUserParameter() {
		val table = getTable(UserConfigurationTable.TABLENAME) as UserConfigurationTable?
		table!!.storeConfigurations(UserParameter.instance())
		table.storeConfigurations(HOParameter.instance())
	}
	// ------------------------------- PaarungTable
	// -------------------------------------------------
	/**
	 * Gets the fixtures for the given `plan` from the DB, and add them to that plan.
	 *
	 * @param plan Schedule for which the fixtures are retrieved, and to which they are added.
	 */
	fun loadFixtures(plan: Spielplan): List<Paarung?>? {
		return (getTable(PaarungTable.TABLENAME) as PaarungTable).loadFixtures(plan.ligaId, plan.saison)
	}

	/**
	 * Saves the fixtures to an existing game schedule ([Spielplan]).
	 *
	 * @param fixtures the fixtures
	 * @param ligaId   the liga id
	 * @param saison   the saison
	 */
	fun storePaarung(fixtures: List<Paarung>?, ligaId: Int, saison: Int) {
		(getTable(PaarungTable.TABLENAME) as PaarungTable).storePaarung(fixtures, ligaId, saison)
	}

	fun deletePaarungTabelle(saison: Int, ligaId: Int) {
		val table = getTable(PaarungTable.TABLENAME)
		table!!.executePreparedDelete(saison, ligaId)
	}
	// ------------------------------- MatchDetailsTable
	// -------------------------------------------------
	/**
	 * Gibt die MatchDetails zu einem Match zurück
	 *
	 * @param iMatchType the sourcesystem
	 * @param matchId      the match id
	 * @return the matchdetails
	 */
	fun loadMatchDetails(iMatchType: Int, matchId: Int): Matchdetails {
		return (getTable(MatchDetailsTable.TABLENAME) as MatchDetailsTable)
			.loadMatchDetails(iMatchType, matchId)
	}

	/**
	 * Return match statistics (Count,Win,Draw,Loss,Goals)
	 *
	 * @param matchtype the matchtype
	 * @return matches overview row [ ]
	 */
	fun getMatchesOverviewValues(matchtype: Int, matchLocation: MatchLocation): Array<MatchesOverviewRow> {
		return MatchesOverviewQuery.getMatchesOverviewValues(matchtype, matchLocation)
	}
	// ------------------------------- MatchHighlightsTable
	// -------------------------------------------------
	/**
	 * Gibt die MatchHighlights zu einem Match zurück
	 *
	 * @param iMatchType the source system
	 * @param matchId      the match id
	 * @return the match highlights
	 */
	fun getMatchHighlights(iMatchType: Int, matchId: Int): List<MatchEvent?>? {
		return (getTable(MatchHighlightsTable.TABLENAME) as MatchHighlightsTable)
			.getMatchHighlights(iMatchType, matchId)
	}

	/**
	 * Get chances stat matches highlights stat [ ].
	 *
	 * @param ownTeam   the own team
	 * @param iMatchType the matchtype
	 * @param matchLocation Home, Away, Neutral
	 * @return the matches highlights stat [ ]
	 */
	fun getGoalsByActionType(
		ownTeam: Boolean,
		iMatchType: Int,
		matchLocation: MatchLocation?
	): Array<MatchesHighlightsStat?> {
		return MatchesOverviewQuery.getGoalsByActionType(ownTeam, iMatchType, matchLocation)
	}

	/**
	 * Gets transfers.
	 *
	 * @param playerid     the playerid
	 * @param allTransfers the all transfers
	 * @return the transfers
	 */
	fun getTransfers(playerid: Int, allTransfers: Boolean): List<PlayerTransfer?> {
		return (getTable(TransferTable.TABLENAME) as TransferTable).getTransfers(playerid, allTransfers)
	}

	/**
	 * Gets transfers.
	 *
	 * @param season the season
	 * @param bought the bought
	 * @param sold   the sold
	 * @return the transfers
	 */
	fun getTransfers(season: Int, bought: Boolean, sold: Boolean): List<PlayerTransfer?> {
		return (getTable(TransferTable.TABLENAME) as TransferTable).getTransfers(season, bought, sold)
	}

	/**
	 * Remove transfer.
	 *
	 * @param transferId the transfer id
	 */
	fun removeTransfer(transferId: Int) {
		(getTable(TransferTable.TABLENAME) as TransferTable).removeTransfer(transferId)
	}

	/**
	 * Update player transfers.
	 *
	 * @param transfer PlayerTransfer
	 */
	fun storePlayerTransfer(transfer: PlayerTransfer) {
		(getTable(TransferTable.TABLENAME) as TransferTable).storeTransfer(transfer)
	}

	/**
	 * load one player transfer
	 * @param transferId int
	 * @return PlayerTransfer
	 */
	fun loadPlayerTransfer(transferId: Int): PlayerTransfer? {
		return (getTable(TransferTable.TABLENAME) as TransferTable).getTransfer(transferId)
	}

	/**
	 * Gets transfer type.
	 *
	 * @param playerId the player id
	 * @return the transfer type
	 */
	fun getTransferType(playerId: Int): TransferType? {
		return (getTable(TransferTypeTable.TABLENAME) as TransferTypeTable).loadTransferType(playerId)
	}

	/**
	 * Sets transfer type.
	 *
	 * @param type     the type
	 */
	fun setTransferType(type: TransferType) {
		(getTable(TransferTypeTable.TABLENAME) as TransferTypeTable).storeTransferType(type)
	}

		/**
		 * Get all world detail leagues world detail league [ ].
		 *
		 * @return the world detail league [ ]
		 */
	fun getAllWorldDetailLeagues(): List<WorldDetailLeague?> {
		return (getTable(WorldDetailsTable.TABLENAME) as WorldDetailsTable).getAllWorldDetailLeagues()
	}
	/**
	 * Save world detail leagues.
	 *
	 * @param leagues the leagues
	 */
	fun saveWorldDetailLeagues(leagues: List<WorldDetailLeague?>) {
		val table = getTable(WorldDetailsTable.TABLENAME) as WorldDetailsTable
		table.truncateTable()
		for (league in leagues) {
			table.insertWorldDetailsLeague(league)
		}
	}
	// --------------------------------------------------------------------------------
	// -------------------------------- Statistik Part
	// --------------------------------
	// --------------------------------------------------------------------------------
	/**
	 * Get spieler daten 4 statistik double [ ] [ ].
	 *
	 * @param playerId the spieler id
	 * @param anzahlHRF the anzahl hrf
	 * @return the double [ ] [ ]
	 */
	fun getSpielerDaten4Statistik(playerId: Int, anzahlHRF: Int): Array<DoubleArray> {
		return StatisticQuery.getSpielerDaten4Statistik(playerId, anzahlHRF)
	}

	/**
	 * Get data for club statistics panel double [ ] [ ].
	 *
	 * @param nbHRFs the nb hr fs
	 * @return the double [ ] [ ]
	 */
	fun getDataForClubStatisticsPanel(nbHRFs: Int): Array<DoubleArray> {
		return StatisticQuery.getDataForClubStatisticsPanel(nbHRFs)
	}

	/**
	 * Get data for finances statistics panel double [ ] [ ].
	 *
	 * @param nbHRF the nb hrf
	 * @return the double [ ] [ ]
	 */
	fun getDataForFinancesStatisticsPanel(nbHRF: Int): Array<DoubleArray> {
		return StatisticQuery.getDataForFinancesStatisticsPanel(nbHRF)
	}

	/**
	 * Gets arena statistik model.
	 *
	 * @param matchtyp the matchtyp
	 * @return the arena statistik model
	 */
	fun getArenaStatistikModel(matchtyp: Int): ArenaStatistikTableModel {
		return StatisticQuery.getArenaStatisticsModel(matchtyp)
	}

	/**
	 * Get data for team statistics panel double [ ] [ ].
	 *
	 * @param anzahlHRF the anzahl hrf
	 * @param group     the group
	 * @return the double [ ] [ ]
	 */
	fun getDataForTeamStatisticsPanel(
		anzahlHRF: Int,
		group: String
	): Array<DoubleArray> {
		return StatisticQuery.getDataForTeamStatisticsPanel(
			anzahlHRF, group
		)
	}

	/**
	 * Gets count of played matches.
	 *
	 * @param playerId the player id
	 * @param official the official
	 * @return the count of played matches
	 */
	fun getCountOfPlayedMatches(playerId: Int, official: Boolean): Int {
		val officialWhere = if (official) "<8" else ">7"
		val sqlStmt = "select count(MATCHESKURZINFO.matchid) as MatchNumber FROM MATCHLINEUPPLAYER " +
				"INNER JOIN MATCHESKURZINFO ON MATCHESKURZINFO.matchid = MATCHLINEUPPLAYER.matchid " +
				"where playerId = " + playerId +
				" and ROLEID BETWEEN 100 AND 113 and matchtyp " + officialWhere
		val rs = jdbcAdapter.executeQuery(sqlStmt) ?: return 0
		var count = 0
		try {
			while (rs.next()) {
				count = rs.getInt("MatchNumber")
			}
		} catch (ignored: SQLException) {
		}
		return count
	}

	/**
	 * Returns a list of PlayerMatchCBItems for given playerID
	 *
	 * @param playerID the player ID
	 */
	fun getPlayerMatchCBItems(playerID: Int): Vector<PlayerMatchCBItem> {
		return getPlayerMatchCBItems(playerID, false)
	}

	/**
	 * Returns a list of PlayerMatchCBItems for given playerID
	 *
	 * @param playerID the player ID
	 * @param officialOnly whether or not to select official game only
	 */
	fun getPlayerMatchCBItems(playerID: Int, officialOnly: Boolean): Vector<PlayerMatchCBItem> {
		if (playerID == -1) return Vector()
		val spielerMatchCBItems = Vector<PlayerMatchCBItem>()
		var sql = """
				SELECT DISTINCT MatchID, MatchDate, Rating, SpielDatum, HeimName, HeimID, GastName, GastID, HoPosCode, MatchTyp
				FROM MATCHLINEUPPLAYER
				INNER JOIN MATCHLINEUP ON (MATCHLINEUPPLAYER.MatchID=MATCHLINEUP.MatchID AND MATCHLINEUPPLAYER.MATCHTYP=MATCHLINEUP.MATCHTYP)
				INNER JOIN MATCHDETAILS ON (MATCHDETAILS.MatchID=MATCHLINEUP.MatchID AND MATCHDETAILS.MATCHTYP=MATCHLINEUP.MATCHTYP)
				INNER JOIN MATCHESKURZINFO ON (MATCHESKURZINFO.MATCHID=MATCHLINEUP.MatchID AND MATCHESKURZINFO.MATCHTYP=MATCHLINEUP.MATCHTYP)
				WHERE MATCHLINEUPPLAYER.SpielerId=? AND MATCHLINEUPPLAYER.Rating>0
				""".trimIndent()
		if (officialOnly) {
			val lMatchTypes =
				MatchType.fromSourceSystem(Objects.requireNonNull(SourceSystem.valueOf(SourceSystem.HATTRICK.value)))
			val inValues = lMatchTypes.stream().map { p: MatchType -> p.id.toString() }.collect(Collectors.joining(","))
			sql += " AND MATCHTYP IN ($inValues)"
		}
		sql += " ORDER BY MATCHDETAILS.SpielDatum DESC"

		// Get all data on the player
		try {
			val playerMatchCBItems = Vector<PlayerMatchCBItem>()
			val rs = jdbcAdapter.executePreparedQuery(getPreparedStatement(sql), playerID)
			var playerMatchCBItem: PlayerMatchCBItem
			assert(rs != null)
			// Get all data on the player
			while (rs!!.next()) {
				playerMatchCBItem = PlayerMatchCBItem(
					null,
					rs.getInt("MatchID"), (rs.getFloat("Rating") * 2).toInt(),
					rs.getInt("HoPosCode"),
					HODateTime.fromDbTimestamp(rs.getTimestamp("MatchDate")),
					rs.getString("HeimName"),
					rs.getInt("HeimID"),
					rs.getString("GastName"),
					rs.getInt("GastID"),
					MatchType.getById(rs.getInt("MatchTyp")),
					null,
					"",
					""
				)
				playerMatchCBItems.add(playerMatchCBItem)
			}
			var filter: Timestamp
			// Get the player data for the matches
			for (item in playerMatchCBItems) {
				filter = item.matchdate.toDbTimestamp()
				// Player
				val player = getSpielerAtDate(playerID, filter)
				// Matchdetails
				val details = loadMatchDetails(item.matchType.getMatchTypeId(), item.matchID)
				// Stimmung und Selbstvertrauen
				val team = getTeam(getHRFID4Date(filter))
				val sTSandConfidences = arrayOf(
					TeamSpirit.toString(team.teamSpiritLevel),
					TeamConfidence.toString(team.confidence)
				)
				//Only if player data has been found, pass it into the return vector
				if (player != null) {
					item.spieler = player
					item.matchdetails = details
					item.setTeamSpirit(sTSandConfidences[0])
					item.setConfidence(sTSandConfidences[1])
					spielerMatchCBItems.add(item)
				}
			}
		} catch (e: Exception) {
			HOLogger.instance().log(
				javaClass,
				"DatenbankZugriff.getSpieler4Matches : $e"
			)
		}
		return spielerMatchCBItems
	}

	/**
	 * Delete hrf.
	 *
	 * @param hrfid the hrfid
	 */
	fun deleteHRF(hrfid: Int) {
		getTable(StadionTable.TABLENAME)!!.executePreparedDelete(hrfid)
		getTable(HRFTable.TABLENAME)!!.executePreparedDelete(hrfid)
		getTable(LigaTable.TABLENAME)!!.executePreparedDelete(hrfid)
		getTable(VereinTable.TABLENAME)!!.executePreparedDelete(hrfid)
		getTable(TeamTable.TABLENAME)!!.executePreparedDelete(hrfid)
		getTable(EconomyTable.TABLENAME)!!.executePreparedDelete(hrfid)
		getTable(BasicsTable.TABLENAME)!!.executePreparedDelete(hrfid)
		getTable(SpielerTable.TABLENAME)!!.executePreparedDelete(hrfid)
		getTable(SpielerSkillupTable.TABLENAME)!!.executePreparedDelete(hrfid)
		getTable(XtraDataTable.TABLENAME)!!.executePreparedDelete(hrfid)
		getTable(StaffTable.TABLENAME)!!.executePreparedDelete(hrfid)
	}

	/**
	 * Deletes all data for the given match
	 *
	 * @param info MatchKurzInfo of the match to delete
	 */
	fun deleteMatch(info: MatchKurzInfo) {
		val matchid = info.matchID
		val matchType = info.getMatchType().id
		getTable(MatchDetailsTable.TABLENAME)!!.executePreparedDelete(matchid, matchType)
		getTable(MatchHighlightsTable.TABLENAME)!!.executePreparedDelete(matchid, matchType)
		getTable(MatchLineupTable.TABLENAME)!!.executePreparedDelete(matchid, matchType)
		getTable(MatchLineupTeamTable.TABLENAME)!!.executePreparedDelete(matchid, matchType, info.homeTeamID)
		getTable(MatchLineupTeamTable.TABLENAME)!!.executePreparedDelete(matchid, matchType, info.guestTeamID)
		getTable(MatchLineupPlayerTable.TABLENAME)!!.executePreparedDelete(matchid, matchType, info.homeTeamID)
		getTable(MatchLineupPlayerTable.TABLENAME)!!.executePreparedDelete(matchid, matchType, info.guestTeamID)
		getTable(MatchesKurzInfoTable.TABLENAME)!!.executePreparedDelete(matchid, matchType)
		getTable(MatchSubstitutionTable.TABLENAME)!!.executePreparedDelete(matchid, matchType, info.homeTeamID)
		getTable(MatchSubstitutionTable.TABLENAME)!!.executePreparedDelete(matchid, matchType, info.guestTeamID)
	}

	/**
	 * Stores the given match info. If info is missing, or the info are not for
	 * the same match, nothing is stored and false is returned. If the store is
	 * successful, true is returned.
	 *
	 *
	 * If status of the info is not FINISHED, nothing is stored, and false is
	 * also returned.
	 *
	 * @param info    The MatchKurzInfo for the match
	 * @param details The MatchDetails for the match
	 * @param lineup  The MatchLineup for the match
	 * @return true if the match is stored. False if not
	 */
	fun storeMatch(info: MatchKurzInfo?, details: Matchdetails?, lineup: MatchLineup?): Boolean {
		if (info == null || details == null || lineup == null) {
			return false
		}
		if (info.matchID == details.matchID && info.matchID == lineup.matchID && info.matchStatus == MatchKurzInfo.FINISHED) {
			val matches = ArrayList<MatchKurzInfo>()
			matches.add(info)
			(getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable).storeMatchKurzInfos(matches)
			storeMatchDetails(details)
			storeMatchLineup(lineup, null)
			return true
		}
		return false
	}

	/**
	 * Updates the given match in the database.
	 *
	 * @param match the match to update.
	 */
	fun updateMatchKurzInfo(match: MatchKurzInfo) {
		(getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable).update(match)
	}

	@Throws(SQLException::class)
	private fun createAllTables() {
		jdbcAdapter.executeUpdate("SET FILES SPACE TRUE")
		val allTables: Array<Any> = tables.values.toTypedArray()
		for (allTable in allTables) {
			val table = allTable as AbstractTable
			HOLogger.instance().info(javaClass, "Create table : " + table.tableName)
			table.createTable()
			val statements = table.createIndexStatement
			for (statement in statements) {
				jdbcAdapter.executeUpdate(statement)
			}
		}
	}

	/**
	 * Load module configs map.
	 *
	 * @return the map
	 */
	fun loadModuleConfigs(): Map<String, Any?> {
		return (getTable(ModuleConfigTable.TABLENAME) as ModuleConfigTable).findAll()
	}

	/**
	 * Save module configs.
	 *
	 * @param values the values
	 */
	fun saveModuleConfigs(values: Map<String?, Any?>) {
		(getTable(ModuleConfigTable.TABLENAME) as ModuleConfigTable).saveConfig(values)
	}

	/**
	 * Delete module configs key.
	 *
	 * @param key the key
	 */
	fun deleteModuleConfigsKey(key: String) {
		(getTable(ModuleConfigTable.TABLENAME) as ModuleConfigTable).deleteConfig(key)
	}

	/**
	 * Set a single UserParameter in the DB
	 *
	 * @param fieldName the name of the parameter to set
	 * @param value     the target value
	 */
	fun saveUserParameter(fieldName: String?, value: Int) {
		saveUserParameter(fieldName, value.toString())
	}

	/**
	 * Set a single UserParameter in the DB
	 *
	 * @param fieldName the name of the parameter to set
	 * @param value     the target value
	 */
	fun saveUserParameter(fieldName: String?, value: Double) {
		saveUserParameter(fieldName, value.toString())
	}

	/**
	 * Set a single UserParameter in the DB
	 *
	 * @param fieldName the name of the parameter to set
	 * @param value     the target value
	 */
	fun saveUserParameter(fieldName: String?, value: String?) {
		(getTable(UserConfigurationTable.TABLENAME) as UserConfigurationTable)
			.storeConfiguration(fieldName, value)
	}

	/**
	 * Save ho column model.
	 *
	 * @param model the model
	 */
	fun saveHOColumnModel(model: HOTableModel) {
		(getTable(UserColumnsTable.TABLENAME) as UserColumnsTable).saveModel(model)
	}

	/**
	 * Load ho colum model.
	 *
	 * @param model the model
	 */
	fun loadHOColumModel(model: HOTableModel) {
		(getTable(UserColumnsTable.TABLENAME) as UserColumnsTable).loadModel(model)
	}

	/**
	 * Remove ta favorite team.
	 *
	 * @param teamId the team id
	 */
	fun removeTAFavoriteTeam(teamId: Int) {
		(getTable(TAFavoriteTable.TABLENAME) as TAFavoriteTable).removeTeam(teamId)
	}

	/**
	 * Add ta favorite team.
	 *
	 * @param team the team
	 */
	fun addTAFavoriteTeam(team: Team?) {
		(getTable(TAFavoriteTable.TABLENAME) as TAFavoriteTable).addTeam(team)
	}

	/**
	 * Is ta favourite boolean.
	 *
	 * @param teamId the team id
	 * @return the boolean
	 */
	fun isTAFavourite(teamId: Int): Boolean {
		return (getTable(TAFavoriteTable.TABLENAME) as TAFavoriteTable).isTAFavourite(teamId)
	}

		/**
		 * Returns all favourite teams
		 *
		 * @return List of Teams Object
		 */

	fun getTAFavoriteTeams(): List<Team?> {
		return (getTable(TAFavoriteTable.TABLENAME) as TAFavoriteTable).getTAFavoriteTeams()
	}
	/**
	 * Gets ta player info.
	 *
	 * @param playerId the player id
	 * @param week     the week
	 * @param season   the season
	 * @return the ta player info
	 */
	fun getTAPlayerInfo(playerId: Int, week: Int, season: Int): PlayerInfo {
		return (getTable(TAPlayerTable.TABLENAME) as TAPlayerTable)
			.getPlayerInfo(playerId, week, season)
	}

	/**
	 * Gets ta latest player info.
	 *
	 * @param playerId the player id
	 * @return the ta latest player info
	 */
	fun getTALatestPlayerInfo(playerId: Int): PlayerInfo {
		return (getTable(TAPlayerTable.TABLENAME) as TAPlayerTable)
			.getLatestPlayerInfo(playerId)
	}

	/**
	 * Update ta player info.
	 *
	 * @param info the info
	 */
	fun storeTAPlayerInfo(info: PlayerInfo) {
		(getTable(TAPlayerTable.TABLENAME) as TAPlayerTable?)!!.storePlayer(info)
	}

	/**
	 * Is ifa matchin db boolean.
	 *
	 * @param matchId the match id
	 * @return the boolean
	 */
	fun isIFAMatchinDB(matchId: Int, matchType: Int): Boolean {
		return (getTable(IfaMatchTable.TABLENAME) as IfaMatchTable)
			.isMatchInDB(matchId, matchType)
	}

		/**
		 * Gets last ifa match date.
		 *
		 * @return the last ifa match date
		 */


	fun getLastIFAMatchDate(): Timestamp? {
		return (getTable(IfaMatchTable.TABLENAME) as IfaMatchTable).getLastMatchDate()
	}
	/**
	 * Get ifa matches ifa match [ ].
	 *
	 * @param home the home
	 * @return the ifa match [ ]
	 */
	fun getIFAMatches(home: Boolean): Array<IfaMatch?> {
		return (getTable(IfaMatchTable.TABLENAME) as IfaMatchTable).getMatches(home)
	}

	/**
	 * Insert ifa match.
	 *
	 * @param match the match
	 */
	fun insertIFAMatch(match: IfaMatch) {
		(getTable(IfaMatchTable.TABLENAME) as IfaMatchTable).insertMatch(match)
	}

	/**
	 * Deletes all the content of the IFA match table.
	 */
	fun deleteIFAMatches() {
		(getTable(IfaMatchTable.TABLENAME) as IfaMatchTable).deleteAllMatches()
	}

	/**
	 * Gets future player trainings.
	 *
	 * @param playerId the player id
	 * @return the future player trainings
	 */
	fun getFuturePlayerTrainings(playerId: Int): List<FuturePlayerTraining?>? {
		return (getTable(FuturePlayerTrainingTable.TABLENAME) as FuturePlayerTrainingTable)
			.getFuturePlayerTrainingPlan(playerId)
	}

	/**
	 * Store future player trainings.
	 *
	 * @param futurePlayerTrainings the future player trainings
	 */
	fun storeFuturePlayerTrainings(futurePlayerTrainings: List<FuturePlayerTraining?>) {
		(getTable(FuturePlayerTrainingTable.TABLENAME) as FuturePlayerTrainingTable)
			.storeFuturePlayerTrainings(futurePlayerTrainings)
	}

	/**
	 * Gets last youth match date.
	 *
	 * @return the last youth match date
	 */
	fun getLastYouthMatchDate(): Timestamp? {
		return (getTable(MatchDetailsTable.TABLENAME) as MatchDetailsTable).getLastYouthMatchDate()
	}



		/**
		 * Get min scouting date timestamp.
		 *
		 * @return the timestamp
		 */

	fun getMinScoutingDate(): Timestamp? {
		return (getTable(YouthPlayerTable.TABLENAME) as YouthPlayerTable).loadMinScoutingDate()
	}
	/**
	 * Store match lineup.
	 *
	 * @param lineup the lineup
	 * @param teamId the team id, if null both teams are stored
	 */
	fun storeMatchLineup(lineup: MatchLineup, teamId: Int?) {
		(getTable(MatchLineupTable.TABLENAME) as MatchLineupTable?)!!.storeMatchLineup(lineup)
		if (teamId == null || teamId == lineup.getHomeTeamId()) {
			storeMatchLineupTeam(lineup.getHomeTeam())
		}
		if (teamId == null || teamId == lineup.getGuestTeamId()) {
			storeMatchLineupTeam(lineup.getGuestTeam())
		}
	}

	/**
	 * Load youth trainer comments list.
	 *
	 * @param id the id
	 * @return the list
	 */
	fun loadYouthTrainerComments(id: Int): List<YouthTrainerComment?> {
		return (getTable(YouthTrainerCommentTable.TABLENAME) as YouthTrainerCommentTable).loadYouthTrainerComments(id)
	}

	fun getYouthMatchLineups(): List<MatchLineup?> {
		return (getTable(MatchLineupTable.TABLENAME) as MatchLineupTable).loadYouthMatchLineups()
	}

	/**
	 * Delete youth match data.
	 *
	 * @param before       the before
	 */
	fun deleteYouthMatchDataBefore(before: Timestamp?) {
		if (before != null) {
			(getTable(MatchHighlightsTable.TABLENAME) as MatchHighlightsTable?)!!.deleteYouthMatchHighlightsBefore(
				before
			)
			(getTable(MatchDetailsTable.TABLENAME) as MatchDetailsTable?)!!.deleteYouthMatchDetailsBefore(before)
			(getTable(MatchLineupTable.TABLENAME) as MatchLineupTable?)!!.deleteYouthMatchLineupsBefore(before)
		}
	}

	/**
	 * Load youth trainings list.
	 *
	 * @return the list
	 */
	fun loadYouthTrainings(): List<YouthTraining?> {
		return (getTable(YouthTrainingTable.TABLENAME) as YouthTrainingTable).loadYouthTrainings()
	}

	/**
	 * Store youth training.
	 *
	 * @param youthTraining the youth training
	 */
	fun storeYouthTraining(youthTraining: YouthTraining?) {
		(getTable(YouthTrainingTable.TABLENAME) as YouthTrainingTable?)!!.storeYouthTraining(youthTraining)
	}

	/**
	 * Store match details.
	 *
	 * @param details the details
	 */
	fun storeMatchDetails(details: Matchdetails?) {
		(getTable(MatchDetailsTable.TABLENAME) as MatchDetailsTable?)!!.storeMatchDetails(details)
		//Store Match Events
		(getTable(MatchHighlightsTable.TABLENAME) as MatchHighlightsTable?)!!.storeMatchHighlights(details)
	}

	/**
	 * Gets team logo file name BUT it will triggers download of the logo from internet if it is not yet available.
	 * It will also update LAST_ACCESS field
	 *
	 * @param teamID the team id
	 * @return the team logo file name
	 */
	fun loadTeamLogoInfo(teamID: Int): TeamLogoInfo? {
		return (getTable(TeamsLogoTable.TABLENAME) as TeamsLogoTable).loadTeamLogoInfo(teamID)
	}

	fun storeTeamLogoInfo(info: TeamLogoInfo) {
		(getTable(TeamsLogoTable.TABLENAME) as TeamsLogoTable).storeTeamLogoInfo(info)
	}

	/**
	 * Store team logo info.
	 *
	 * @param teamID     the team id
	 * @param logoURL    the logo url
	 * @param lastAccess the last access
	 */
	fun storeTeamLogoInfo(teamID: Int, logoURL: String?, lastAccess: HODateTime?) {
		val info = TeamLogoInfo()
		info.url = logoURL
		info.teamId = teamID
		info.lastAccess = lastAccess
		(getTable(TeamsLogoTable.TABLENAME) as TeamsLogoTable).storeTeamLogoInfo(info)
	}

	fun getHRFsSince(since: Timestamp?): List<HRF?> {
		return (getTable(HRFTable.TABLENAME) as HRFTable).getHRFsSince(since)
	}

	fun loadHrfIdPerWeekList(nWeeks: Int): List<Int> {
		return (getTable(HRFTable.TABLENAME) as HRFTable).getHrfIdPerWeekList(nWeeks)
	}

	fun storeTeamRatings(teamrating: MatchTeamRating?) {
		(getTable(MatchTeamRatingTable.TABLENAME) as MatchTeamRatingTable?)!!.storeTeamRating(teamrating)
	}

	fun loadMatchTeamRating(matchtype: Int, matchId: Int): List<MatchTeamRating?>? {
		return (getTable(MatchTeamRatingTable.TABLENAME) as MatchTeamRatingTable).load(matchId, matchtype)
	}

	// ------------------------------- MatchLineupTeamTable
	// -------------------------------------------------
	fun loadMatchLineupTeam(iMatchType: Int, matchID: Int, teamID: Int): MatchLineupTeam? {
		val ret = (getTable(MatchLineupTeamTable.TABLENAME) as MatchLineupTeamTable?)!!.loadMatchLineupTeam(
			iMatchType,
			matchID,
			teamID
		)
		ret?.loadLineup()
		return ret
	}

	fun loadPreviousMatchLineup(teamID: Int): MatchLineupTeam? {
		return loadLineup(getLastMatchesKurzInfo(teamID), teamID)
	}

	fun loadNextMatchLineup(teamID: Int): MatchLineupTeam? {
		return loadLineup(getNextMatchesKurzInfo(teamID), teamID)
	}

	private fun loadLineup(match: MatchKurzInfo?, teamID: Int): MatchLineupTeam? {
		return if (match != null) {
			loadMatchLineupTeam(match.getMatchType().id, match.matchID, teamID)
		} else null
	}

	fun storeMatchLineupTeam(matchLineupTeam: MatchLineupTeam) {
		(getTable(MatchLineupTeamTable.TABLENAME) as MatchLineupTeamTable?)!!.storeMatchLineupTeam(matchLineupTeam)

		//replace players
		val matchLineupPlayerTable = getTable(MatchLineupPlayerTable.TABLENAME) as MatchLineupPlayerTable
		matchLineupPlayerTable.storeMatchLineupPlayers(
			matchLineupTeam.getLineup().getAllPositions(),
			matchLineupTeam.getMatchType(),
			matchLineupTeam.matchId,
			matchLineupTeam.teamID
		)

		// replace Substitutions
		val matchSubstitutionTable = getTable(MatchSubstitutionTable.TABLENAME) as MatchSubstitutionTable
		matchSubstitutionTable.storeMatchSubstitutionsByMatchTeam(
			matchLineupTeam.getMatchType(),
			matchLineupTeam.matchId,
			matchLineupTeam.teamID,
			matchLineupTeam.substitutions
		)
	}

	fun deleteMatchLineupTeam(matchLineupTeam: MatchLineupTeam) {
		(getTable(MatchLineupTeamTable.TABLENAME) as MatchLineupTeamTable).deleteMatchLineupTeam(matchLineupTeam)
	}

	fun loadTemplateMatchLineupTeams(): List<MatchLineupTeam?> {
		return (getTable(MatchLineupTeamTable.TABLENAME) as MatchLineupTeamTable).getTemplateMatchLineupTeam()
	}

	fun getTemplateMatchLineupTeamNextNumber(): Int {
		return (getTable(MatchLineupTeamTable.TABLENAME) as MatchLineupTeamTable).getTemplateMatchLineupTeamNextNumber()
	}

	fun loadNtTeamDetails(teamId: Int, matchDate: Timestamp?): NtTeamDetails? {
		return (getTable(NtTeamTable.TABLENAME) as NtTeamTable).loadNTTeam(teamId, matchDate)
	}

	fun loadAllNtTeamDetails(): List<NtTeamDetails?>? {
		return (getTable(NtTeamTable.TABLENAME) as NtTeamTable).loadNTTeams(getLatestHRF().hrfId)
	}

	fun storeNtTeamDetails(details: NtTeamDetails?) {
		(getTable(NtTeamTable.TABLENAME) as NtTeamTable?)!!.storeNTTeam(details)
	}

	private val sql =
		"SELECT TRAININGDATE, TRAININGSART, TRAININGSINTENSITAET, STAMINATRAININGPART, COTRAINER, TRAINER" +
				" FROM XTRADATA INNER JOIN TEAM on XTRADATA.HRF_ID = TEAM.HRF_ID" +
				" INNER JOIN VEREIN on XTRADATA.HRF_ID = VEREIN.HRF_ID" +
				" INNER JOIN SPIELER on XTRADATA.HRF_ID = SPIELER.HRF_ID AND SPIELER.TRAINER > 0" +
				" INNER JOIN (SELECT TRAININGDATE, %s(HRF_ID) J_HRF_ID FROM XTRADATA GROUP BY TRAININGDATE) IJ1 ON XTRADATA.HRF_ID = IJ1.J_HRF_ID" +
				" WHERE XTRADATA.TRAININGDATE >= ?"
	private val loadTrainingPerWeekMaxStatement = PreparedStatementBuilder(String.format(sql, "max"))
	private val loadTrainingPerWeekMinStatement = PreparedStatementBuilder(String.format(sql, "min"))
	fun loadTrainingPerWeek(startDate: Timestamp?, all: Boolean): List<TrainingPerWeek>? {
		val ret = ArrayList<TrainingPerWeek>()
		try {
			val rs = jdbcAdapter.executePreparedQuery(
				if (all) loadTrainingPerWeekMaxStatement.getStatement() else loadTrainingPerWeekMinStatement.getStatement(),
				startDate
			)
			if (rs != null) {
				while (rs.next()) {
					val trainType = rs.getInt("TRAININGSART")
					val trainIntensity = rs.getInt("TRAININGSINTENSITAET")
					val trainStaminaPart = rs.getInt("STAMINATRAININGPART")
					// subtract one week from next training date to get the past week training date
					val nextTrainingDate = HODateTime.fromDbTimestamp(rs.getTimestamp("TRAININGDATE"))
					val trainingDate = nextTrainingDate.plusDaysAtSameLocalTime(-7)
					val coachLevel = rs.getInt("TRAINER")
					val trainingAssistantLevel = rs.getInt("COTRAINER")
					val tpw = TrainingPerWeek(
						trainingDate,
						trainType,
						trainIntensity,
						trainStaminaPart,
						trainingAssistantLevel,
						coachLevel,
						DBDataSource.HRF
					)
					ret.add(tpw)
				}
			}
			return ret
		} catch (e: Exception) {
			HOLogger.instance().error(this.javaClass, "Error while performing loadTrainingPerWeek():  $e")
		}
		return null
	}

	fun getMatchesKurzInfo(teamId: Int, status: Int, from: Timestamp?, matchTypes: List<Int?>): List<MatchKurzInfo?>? {
		return (getTable(MatchesKurzInfoTable.TABLENAME) as MatchesKurzInfoTable?)!!.getMatchesKurzInfo(
			teamId,
			status,
			from,
			matchTypes
		)
	}
	// ~ Constructors
	// -------------------------------------------------------------------------------
	/**
	 * Creates a new instance of DBZugriff
	 */

	fun loadLatestTSINotInjured(playerId: Int): String {
		return loadLatestTSI(preStatementBuilder, playerId)
	}

	fun loadLatestTSIInjured(playerId: Int): String {
		return loadLatestTSI(postStatementBuilder, playerId)
	}

	private fun loadLatestTSI(preparedStatementBuilder: PreparedStatementBuilder, playerId: Int): String {
		try {
			val rs = Objects.requireNonNull(jdbcAdapter).executePreparedQuery(
				preparedStatementBuilder.getStatement(), playerId
			)
			if (rs!!.next()) {
				return rs.getString("marktwert")
			}
		} catch (ignored: Exception) {
		}
		return ""
	}

	fun storeSquadInfo(squadInfo: SquadInfo) {
		(getTable(SquadInfoTable.TABLENAME) as SquadInfoTable?)!!.storeSquadInfo(squadInfo)
	}

	fun loadSquadInfo(teamId: Int): List<SquadInfo?> {
		return (getTable(SquadInfoTable.TABLENAME) as SquadInfoTable).loadSquadInfo(teamId)
	}

	 open class PreparedStatementBuilder(val sqlQuery:String) {

		 fun getStatement(): PreparedStatement? {
			 return jdbcAdapter.createPreparedStatement(sqlQuery)
		 }
	}

	private val preStatementBuilder = PreparedStatementBuilder(
		"select marktwert from SPIELER where spielerid=? and verletzt=-1 order by DATUM desc"
	)
	private val postStatementBuilder = PreparedStatementBuilder(
		"select marktwert from SPIELER where spielerid=? and verletzt>-1 order by DATUM desc"
	)

	fun getPlaceholders(count: Int): String {
		return java.lang.String.join(",", Collections.nCopies(count, "?"))
	}
}
