package core.db

import core.HO
import core.db.DBManager.PreparedStatementBuilder
import core.db.user.User
import core.db.user.UserManager.save
import core.db.user.UserManager.users
import core.model.enums.DBDataSource
import core.util.HODateTime
import core.util.HOLogger
import module.youth.YouthPlayer
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import java.util.function.Consumer
import javax.swing.JOptionPane

internal class DBUpdater {
    var m_clJDBCAdapter: JDBCAdapter? = null
    var dbManager: DBManager? = null
    fun updateDB(DBVersion: Int) {
        // Just add new version cases in the switch..case part
        // and leave the old ones active, so also users which
        // have skipped a version get their database updated.
        dbManager = DBManager
        m_clJDBCAdapter = dbManager!!.jdbcAdapter
        val version = (dbManager!!.getTable(UserConfigurationTable.TABLENAME) as UserConfigurationTable).getDBVersion()
        if (version != DBVersion) {
            try {
                when (version) {
                    301 -> {
                        updateDBv300() // Bug#509 requires another update run of v300
                        updateDBv301(DBVersion)
                        updateDBv400(DBVersion)
                        updateDBv500(DBVersion)
                        updateDBv600(DBVersion)
                        updateDBv601(DBVersion)
                        updateDBv700(DBVersion)
                        updateDBv701(DBVersion)
                        updateDBv800(DBVersion)
                    }

                    302, 399 -> {
                        updateDBv400(DBVersion)
                        updateDBv500(DBVersion)
                        updateDBv600(DBVersion)
                        updateDBv601(DBVersion)
                        updateDBv700(DBVersion)
                        updateDBv701(DBVersion)
                        updateDBv800(DBVersion)
                    }

                    400, 499 -> {
                        updateDBv500(DBVersion)
                        updateDBv600(DBVersion)
                        updateDBv601(DBVersion)
                        updateDBv700(DBVersion)
                        updateDBv701(DBVersion)
                        updateDBv800(DBVersion)
                    }

                    500 -> {
                        updateDBv600(DBVersion)
                        updateDBv601(DBVersion)
                        updateDBv700(DBVersion)
                        updateDBv701(DBVersion)
                        updateDBv800(DBVersion)
                    }

                    600 -> {
                        updateDBv601(DBVersion)
                        updateDBv700(DBVersion)
                        updateDBv701(DBVersion)
                        updateDBv800(DBVersion)
                    }

                    601, 602 -> {
                        updateDBv700(DBVersion)
                        updateDBv701(DBVersion)
                        updateDBv800(DBVersion)
                    }

                    700 -> {
                        updateDBv701(DBVersion)
                        updateDBv800(DBVersion)
                    }

                    701 -> updateDBv800(DBVersion)
                    800 -> {}
                    else -> {
                        // Unsupported database version
                        // We upgrade database from version 300 (HO 3.0)
                        HOLogger.instance().log(javaClass, "DB version $version is too old")
                        try {
                            JOptionPane.showMessageDialog(
                                null,
                                "DB is too old.\nPlease update first to HO! 3.0", "Error",
                                JOptionPane.ERROR_MESSAGE
                            )
                        } catch (e: Exception) {
                            HOLogger.instance().log(javaClass, e)
                        }
                        System.exit(0)
                    }
                }
            } catch (e: Exception) {
                HOLogger.instance().error(javaClass, e)
            }
        } else {
            HOLogger.instance().log(javaClass, "No DB update necessary.")
        }
    }

    @Throws(SQLException::class)
    private fun updateDBv800(dbVersion: Int) {
        val playerTable = dbManager!!.getTable(SpielerTable.Companion.TABLENAME)
        playerTable!!.tryAddColumn("LineupDisabled", "BOOLEAN")
        playerTable.tryAddColumn("ContractDate", "VARCHAR(100)")
        playerTable.tryChangeColumn("OwnerNotes", "VARCHAR(512)")
        updateDBVersion(dbVersion, 800)
    }

    private val migrateStatements = HashMap<String, PreparedStatement?>()
    private fun getMigrateEscapesStatement(table: String, column: String, where: String): PreparedStatement? {
        val sql = "Update $table SET $column=REPLACE($column, ?, ?) $where"
        var ret = migrateStatements[sql]
        if (ret == null) {
            ret = PreparedStatementBuilder(sql).getStatement()
            migrateStatements[sql] = ret
        }
        return ret
    }

    private fun migrateEscapes(table: String, vararg columns: String) {
        migrateSelectedEscapes(table, "", *columns)
    }

    private fun migrateSelectedEscapes(table: String, where: String, vararg columns: String) {
        for (column in columns) {
            val now = HODateTime.now()
            HOLogger.instance().info(
                javaClass,
                "Migrating escapes in column " + column + " of table " + table + " at " + now.toLocaleDateTime()
            )
            m_clJDBCAdapter!!.executePreparedUpdate(getMigrateEscapesStatement(table, column, where), "§", "\\")
            val rows =
                m_clJDBCAdapter!!.executePreparedUpdate(getMigrateEscapesStatement(table, column, where), "#", "'")
            val finished = HODateTime.now()
            HOLogger.instance().info(
                javaClass,
                "Migrating escapes in column " + column + " of table " + table + " " + rows + " rows finished  at " + finished.toLocaleDateTime() + " duration: " + Duration.between(
                    now.instant,
                    finished.instant
                ).toSeconds() + "sec"
            )
        }
    }

    // fix https://github.com/akasolace/HO/issues/1817
    @Throws(SQLException::class)
    private fun updateDBv701(dbVersion: Int) {
        val teamTable = dbManager!!.getTable(TeamTable.Companion.TABLENAME)
        teamTable!!.tryDeleteColumn("sTrainingsArt")
        teamTable.tryDeleteColumn("sStimmung")
        teamTable.tryDeleteColumn("sSelbstvertrauen")
        val matchlineupplayerTable = dbManager!!.getTable(MatchLineupPlayerTable.Companion.TABLENAME)
        matchlineupplayerTable!!.tryDeleteColumn("PositionCode")
        matchlineupplayerTable.tryDeleteColumn("FIELDPOS")
        val mmatchSubstitutionTable = dbManager!!.getTable(MatchSubstitutionTable.Companion.TABLENAME)
        mmatchSubstitutionTable!!.tryDeleteColumn("HRFID")
        mmatchSubstitutionTable.tryDeleteColumn("LineupName")
        val stadiumTable = dbManager!!.getTable(StadionTable.Companion.TABLENAME)
        stadiumTable!!.tryDeleteColumn("GesamtGr")
        updateDBVersion(dbVersion, 701)
    }

    @Throws(SQLException::class)
    private fun updateDBv700(dbVersion: Int) {
        var isFixed = false
        val users: List<User> = users
        for (user in users) {
            if (user.numberOfBackups == 0) {    // repair backupLevel
                user.numberOfBackups = 3
                isFixed = true
            }
        }
        if (isFixed) {
            save()
        }
        val playerTable = dbManager!!.getTable(SpielerTable.Companion.TABLENAME)
        if (playerTable!!.tryAddColumn("LastMatch_PlayedMinutes", "INTEGER")) {
            playerTable.tryAddColumn("LastMatch_PositionCode", "INTEGER")
            playerTable.tryAddColumn("LastMatch_RatingEndOfGame", "INTEGER")
        }
        if (playerTable.tryAddColumn("MotherclubId", "INTEGER")) {
            playerTable.tryAddColumn("MotherclubName", "VARCHAR(255)")
            playerTable.tryAddColumn("MatchesCurrentTeam", "INTEGER")
        }
        if (playerTable.tryDeleteColumn("BONUS")) {
            playerTable.tryDeleteColumn("OffsetTorwart")
            playerTable.tryDeleteColumn("OffsetVerteidigung")
            playerTable.tryDeleteColumn("OffsetSpielaufbau")
            playerTable.tryDeleteColumn("OffsetFluegel")
            playerTable.tryDeleteColumn("OffsetTorschuss")
            playerTable.tryDeleteColumn("OffsetPasspiel")
            playerTable.tryDeleteColumn("OffsetStandards")
            val hrfTable = dbManager!!.getTable(HRFTable.Companion.TABLENAME)
            hrfTable!!.tryDeleteColumn("NAME")
            m_clJDBCAdapter!!.executeUpdate("DROP TABLE IF EXISTS MATCHLINEUPPENALTYTAKER")
            migrateEscapes("MATCHHIGHLIGHTS", "EventText", "SpielerName", "GehilfeName")
            migrateEscapes("BASICS", "Manager", "TeamName", "YouthTeamName")
            migrateEscapes("MATCHDETAILS", "ArenaName", "GastName", "HeimName", "Matchreport")
            migrateEscapes("MATCHLINEUPPLAYER", "VName", "NickName", "Name")
            migrateEscapes("MATCHLINEUPTEAM", "TeamName")
            migrateEscapes("MATCHESKURZINFO", "GastName", "HeimName")
            migrateEscapes("NTTEAM", "SHORTNAME", "COACHNAME", "LEAGUENAME", "NAME")
            migrateEscapes("PAARUNG", "GastName", "HeimName")
            migrateEscapes("SCOUT", "Name", "Info")
            migrateEscapes("SPIELERNOTIZ", "Notiz")
            migrateEscapes("SPIELER", "ArrivalDate", "MotherclubName", "NickName", "LastName", "FirstName")
            migrateEscapes("STADION", "StadionName")
            migrateEscapes("TA_PLAYER", "NAME")
            migrateEscapes("TRANSFER", "playername", "buyername", "sellername")
            migrateEscapes("HT_WORLDDETAILS", "COUNTRYNAME")
            migrateEscapes("XTRADATA", "LogoURL")
            migrateEscapes("YOUTHPLAYER", "ScoutName", "OwnerNotes", "Statement", "NickName", "LastName", "FirstName")
            migrateEscapes("YOUTHSCOUTCOMMENT", "Text")
            migrateSelectedEscapes("USERCONFIGURATION", "where CONFIG_KEY='hrfImport_HRFPath'", "CONFIG_VALUE")
        }
        val youthplayerTable = dbManager!!.getTable(YouthPlayerTable.Companion.TABLENAME)
        if (!youthplayerTable!!.primaryKeyExists()) {
            youthplayerTable.addPrimaryKey("HRF_ID,ID")
            youthplayerTable.tryChangeColumn("rating", "DOUBLE")
            val teamTable = dbManager!!.getTable(TeamTable.Companion.TABLENAME)
            teamTable!!.tryDeleteColumn("sTrainingsArt")
            teamTable.tryDeleteColumn("sStimmung")
            teamTable.tryDeleteColumn("sSelbstvertrauen")
            val matchlineupplayerTable = dbManager!!.getTable(MatchLineupPlayerTable.Companion.TABLENAME)
            matchlineupplayerTable!!.tryDeleteColumn("PositionCode")
            matchlineupplayerTable.tryDeleteColumn("FIELDPOS")
            val mmatchSubstitutionTable = dbManager!!.getTable(MatchSubstitutionTable.Companion.TABLENAME)
            mmatchSubstitutionTable!!.tryDeleteColumn("HRFID")
            mmatchSubstitutionTable.tryDeleteColumn("LineupName")
            val stadiumTable = dbManager!!.getTable(StadionTable.Companion.TABLENAME)
            stadiumTable!!.tryDeleteColumn("GesamtGr")
        }
        val matchSubstitutionTable = dbManager!!.getTable(MatchSubstitutionTable.Companion.TABLENAME)
        matchSubstitutionTable!!.tryAddIndex("IMATCHSUBSTITUTION_0", "MatchID,MatchTyp,TeamID")
        matchSubstitutionTable.tryDropIndex("IMATCHSUBSTITUTION_3")
        if (!tableExists(SquadInfoTable.Companion.TABLENAME)) {
            dbManager!!.getTable(SquadInfoTable.Companion.TABLENAME)!!.createTable()
        }
        updateDBVersion(dbVersion, 700)
    }

    @Throws(SQLException::class)
    private fun updateDBv601(dbVersion: Int) {
        val playerTable = dbManager!!.getTable(SpielerTable.Companion.TABLENAME)
        if (playerTable!!.tryAddColumn("Statement", "VARCHAR(255)")) {
            playerTable.tryAddColumn("OwnerNotes", "VARCHAR(255)")
            playerTable.tryAddColumn("PlayerCategory", "INTEGER")
        }
        updateDBVersion(dbVersion, 601)
    }

    @Throws(SQLException::class)
    private fun updateDBv600(dbVersion: Int) {
        // reduce data base file's disk space
        m_clJDBCAdapter!!.executeUpdate("CHECKPOINT DEFRAG")
        m_clJDBCAdapter!!.executeUpdate("SET FILES SPACE TRUE")
        m_clJDBCAdapter!!.executeUpdate("SET TABLE MATCHHIGHLIGHTS NEW SPACE")
        m_clJDBCAdapter!!.executeUpdate("SET TABLE MATCHLINEUPPLAYER NEW SPACE")
        m_clJDBCAdapter!!.executeUpdate("DROP TABLE AUFSTELLUNG IF EXISTS")
        m_clJDBCAdapter!!.executeUpdate("DROP TABLE MATCHORDER IF EXISTS")
        m_clJDBCAdapter!!.executeUpdate("DROP TABLE POSITIONEN IF EXISTS")
        val matchLineupTeamTable = dbManager!!.getTable(MatchLineupTeamTable.Companion.TABLENAME)
        if (!matchLineupTeamTable!!.primaryKeyExists()) {
            matchLineupTeamTable.tryAddColumn("ATTITUDE", "INTEGER")
            matchLineupTeamTable.tryAddColumn("TACTIC", "INTEGER")
            matchLineupTeamTable.tryDropIndex("MATCHLINEUPTEAM_IDX")
            matchLineupTeamTable.addPrimaryKey("MATCHID,TEAMID,MATCHTYP")
        }
        if (!tableExists(NtTeamTable.Companion.TABLENAME)) {
            dbManager!!.getTable(NtTeamTable.Companion.TABLENAME)!!.createTable()
        }

        // drop indexes where corresponding primary key exists
        dbManager!!.getTable(BasicsTable.Companion.TABLENAME)!!.tryDropIndex("IBASICS_1")
        dbManager!!.getTable(EconomyTable.Companion.TABLENAME)!!.tryDropIndex("ECONOMY_2")
        dbManager!!.getTable(LigaTable.Companion.TABLENAME)!!.tryDropIndex("ILIGA_1")
        dbManager!!.getTable(SpielerNotizenTable.Companion.TABLENAME)!!.tryDropIndex("ISPIELERNOTIZ_1")
        dbManager!!.getTable(TeamTable.Companion.TABLENAME)!!.tryDropIndex("ITEAM_1")
        dbManager!!.getTable(VereinTable.Companion.TABLENAME)!!.tryDropIndex("IVEREIN_1")
        dbManager!!.getTable(XtraDataTable.Companion.TABLENAME)!!.tryDropIndex("IXTRADATA_1")
        if (!columnExistsInTable("IncomeSponsorsBonus", EconomyTable.Companion.TABLENAME)) {
            try {
                m_clJDBCAdapter!!.executeUpdate("ALTER TABLE ECONOMY ADD COLUMN LastIncomeSponsorsBonus INTEGER DEFAULT 0")
                m_clJDBCAdapter!!.executeUpdate("ALTER TABLE ECONOMY ADD COLUMN IncomeSponsorsBonus INTEGER DEFAULT 0")
                HOLogger.instance().info(javaClass, "Sponsor Bonus columns have been added to Economy table")
            } catch (e: Exception) {
                HOLogger.instance().error(
                    javaClass,
                    "Error when trying to add Sponsor Bonus columns have been added to Economy table: $e"
                )
            }
        }
        if (!columnExistsInTable("GoalsCurrentTeam", SpielerTable.Companion.TABLENAME)) {
            try {
                m_clJDBCAdapter!!.executeUpdate("ALTER TABLE SPIELER ADD COLUMN GoalsCurrentTeam INTEGER DEFAULT 0")
                m_clJDBCAdapter!!.executeUpdate("ALTER TABLE SPIELER ADD COLUMN ArrivalDate VARCHAR (100)")
                HOLogger.instance().info(javaClass, "SPIELER table structure has been updated")
            } catch (e: Exception) {
                HOLogger.instance().error(
                    javaClass,
                    "Error when trying to add Sponsor Bonus columns have been added to Economy table: $e"
                )
            }
        }
        updateDBVersion(dbVersion, 600)
    }

    @Throws(SQLException::class)
    private fun updateDBv500(dbVersion: Int) {
        // Upgrade legacy FINANZEN table to new ECONOMY Table (since HO 5.0)
        if (!tableExists(EconomyTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Datum RENAME TO FetchedDate")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Supporter RENAME TO SupportersPopularity")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Sponsoren RENAME TO SponsorsPopularity")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN Finanzen RENAME TO Cash")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinSponsoren RENAME TO IncomeSponsors")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinZuschauer RENAME TO IncomeSpectators")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinZinsen RENAME TO IncomeFinancial")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinSonstiges RENAME TO IncomeTemporary")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN EinGesamt RENAME TO IncomeSum")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostSpieler RENAME TO CostsPlayers")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostTrainer RENAME TO CostsStaff")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostStadion RENAME TO CostsArena")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostJugend RENAME TO CostsYouth")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostZinsen RENAME TO CostsFinancial")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostSonstiges RENAME TO CostsTemporary")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN KostGesamt RENAME TO CostsSum")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN GewinnVerlust RENAME TO ExpectedWeeksTotal")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinSponsoren RENAME TO LastIncomeSponsors")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinZuschauer RENAME TO LastIncomeSpectators")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinZinsen RENAME TO LastIncomeFinancial")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinSonstiges RENAME TO LastIncomeTemporary")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteEinGesamt RENAME TO LastIncomeSum")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostSpieler RENAME TO LastCostsPlayers")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostTrainer RENAME TO LastCostsStaff")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostStadion RENAME TO LastCostsArena")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostJugend RENAME TO LastCostsYouth")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostZinsen RENAME TO LastCostsFinancial")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostSonstiges RENAME TO LastCostsTemporary")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteKostGesamt RENAME TO LastCostsSum")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ALTER COLUMN LetzteGewinnVerlust RENAME TO LastWeeksTotal")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN ExpectedCash INTEGER DEFAULT 0")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN IncomeSoldPlayers INTEGER DEFAULT 0")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN IncomeSoldPlayersCommission INTEGER DEFAULT 0")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN CostsBoughtPlayers INTEGER DEFAULT 0")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN CostsArenaBuilding INTEGER DEFAULT 0")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastIncomeSoldPlayers INTEGER DEFAULT 0")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastIncomeSoldPlayersCommission INTEGER DEFAULT 0")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastCostsBoughtPlayers INTEGER DEFAULT 0")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN ADD COLUMN LastCostsArenaBuilding INTEGER DEFAULT 0")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE FINANZEN RENAME TO ECONOMY")
        }
        val matchDetailsTable = dbManager!!.getTable(MatchDetailsTable.Companion.TABLENAME)
        matchDetailsTable!!.tryAddColumn("HomeFormation", "VARCHAR (5)")
        matchDetailsTable.tryAddColumn("AwayFormation", "VARCHAR (5)")
        val basicsTable = dbManager!!.getTable(BasicsTable.Companion.TABLENAME)
        basicsTable!!.tryAddColumn("YouthTeamName", "VARCHAR (127)")
        basicsTable.tryAddColumn("YouthTeamID", "INTEGER")
        val matchLineupPlayerTable = dbManager!!.getTable(MatchLineupPlayerTable.Companion.TABLENAME)
        matchLineupPlayerTable!!.tryAddColumn("StartSetPieces", "BOOLEAN")
        val matchHighlightsTable = dbManager!!.getTable(MatchHighlightsTable.Companion.TABLENAME)
        matchHighlightsTable!!.tryAddColumn("MatchDate", "TIMESTAMP")
        if (!tableExists(YouthTrainingTable.Companion.TABLENAME)) {
            dbManager!!.getTable(YouthTrainingTable.Companion.TABLENAME)!!.createTable()
            dbManager!!.getTable(YouthPlayerTable.Companion.TABLENAME)!!.createTable()
            dbManager!!.getTable(YouthScoutCommentTable.Companion.TABLENAME)!!.createTable()
        }
        val youthplayerTable = dbManager!!.getTable(YouthPlayerTable.Companion.TABLENAME)
        for (skill in YouthPlayer.skillIds) {
            youthplayerTable!!.tryAddColumn(skill.toString() + "Top3", "BOOLEAN")
        }
        if (!tableExists(TeamsLogoTable.Companion.TABLENAME)) {
            dbManager!!.getTable(TeamsLogoTable.Companion.TABLENAME)!!.createTable()
        }

        // Upgrade TRAINING table =================================================================================
        if (columnExistsInTable("COACH_LEVEL", TrainingsTable.Companion.TABLENAME)) {
            HOLogger.instance()
                .debug(javaClass, "Upgrade of training table was already performed ... process skipped !")
        } else {
            // Step 1. Add new columns in TRAINING table ===========================
            val trainingTable = dbManager!!.getTable(TrainingsTable.Companion.TABLENAME)
            trainingTable!!.tryAddColumn("COACH_LEVEL", "INTEGER")
            trainingTable.tryAddColumn("TRAINING_ASSISTANTS_LEVEL", "INTEGER")
            trainingTable.tryAddColumn("SOURCE", "INTEGER")
            trainingTable.tryAddColumn("TRAINING_DATE", "TIMESTAMP")

            // Step 2. Migrate existing entries ==================================================================
            val trainings = ArrayList<IntArray>()
            val statement = "SELECT * FROM " + TrainingsTable.Companion.TABLENAME
            var rs = m_clJDBCAdapter!!.executeQuery(statement)
            try {
                if (rs != null) {
                    while (rs.next()) {
                        val training = intArrayOf(
                            rs.getInt("week"),
                            rs.getInt("year")
                        )
                        trainings.add(training)
                    }
                }
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC))
                val preparedStatement = m_clJDBCAdapter!!.createPreparedStatement(
                    "select TRAININGDATE,COTRAINER,TRAINER FROM XTRADATA " +
                            "INNER JOIN VEREIN ON VEREIN.HRF_ID=XTRADATA.HRF_ID " +
                            "INNER JOIN SPIELER ON SPIELER.HRF_ID=XTRADATA.HRF_ID AND TRAINER>0 " +
                            "WHERE TRAININGDATE> ? LIMIT 1"
                )
                val updateStatement =
                    m_clJDBCAdapter!!.createPreparedStatement("update " + TrainingsTable.Companion.TABLENAME + " SET TRAINING_DATE=?, TRAINING_ASSISTANTS_LEVEL=?, COACH_LEVEL=?, SOURCE=? WHERE YEAR=? AND WEEK=?")
                for (training in trainings) {
                    // Convert year, week to Date
                    val dayOfWeek = 1 // 1-7, locale-dependent such as Sunday-Monday in US.
                    val weekFields = WeekFields.of(Locale.GERMANY)
                    val ld = LocalDate.now()
                        .withYear(training[1])
                        .with(weekFields.weekOfYear(), training[0].toLong())
                        .with(weekFields.dayOfWeek(), dayOfWeek.toLong())
                    val dateString = formatter.format(ld)

                    // find hrf of that training week
                    // COTrainer from VEREIN,HRF_ID
                    // TRAINER from SPIELER,HRF_ID && TRAINER>0
                    // TrainingDate from XTRA,HRF_ID
                    rs = m_clJDBCAdapter!!.executePreparedQuery(preparedStatement, dateString)
                    if (rs != null) {
                        rs.next()
                        val trainingDate = rs.getTimestamp("TRAININGDATE")
                        val coTrainer = rs.getInt("COTRAINER")
                        val trainer = rs.getInt("TRAINER")

                        // update new columns
                        m_clJDBCAdapter!!.executePreparedUpdate(
                            updateStatement,
                            trainingDate,
                            coTrainer,
                            trainer,
                            DBDataSource.MANUAL.value,
                            training[1],
                            training[0]
                        )
                    }
                }
            } catch (e: Exception) {
                HOLogger.instance()
                    .error(javaClass, "Error when trying to migrate existing entries of TRAININGS table: $e")
            }

            // Step 3. Finalize upgrade of Training table structure ===============================
            trainingTable.tryChangeColumn("COACH_LEVEL", "NOT NULL")
            trainingTable.tryChangeColumn("TRAINING_ASSISTANTS_LEVEL", "NOT NULL")
            trainingTable.tryChangeColumn("TRAINING_DATE", "NOT NULL")
            trainingTable.tryChangeColumn("SOURCE", "NOT NULL")
            trainingTable.tryRenameColumn("TYP", "TRAINING_TYPE")
            trainingTable.tryRenameColumn("INTENSITY", "TRAINING_INTENSITY")
            trainingTable.tryRenameColumn("STAMINATRAININGPART", "STAMINA_SHARE")
            trainingTable.tryDeleteColumn("YEAR")
            trainingTable.tryDeleteColumn("WEEK")
        }

        // Upgrade FutureTraining table ======================================================================================
        if (columnExistsInTable("COACH_LEVEL", FutureTrainingTable.Companion.TABLENAME)) {
            HOLogger.instance()
                .debug(javaClass, "Upgrade of FutureTraining table was already performed ... process skipped !")
        } else {
            // Step 1. Add new columns in FUTURETRAININGS table ===========================================================
            val futureTrainingTable = dbManager!!.getTable(FutureTrainingTable.Companion.TABLENAME)
            futureTrainingTable!!.tryAddColumn("COACH_LEVEL", "INTEGER")
            futureTrainingTable.tryAddColumn("TRAINING_ASSISTANTS_LEVEL", "INTEGER")
            futureTrainingTable.tryAddColumn("TRAINING_DATE", "TIMESTAMP")
            futureTrainingTable.tryAddColumn("SOURCE", "INTEGER")


            // Step 2: update columns with non-null values to ensure NOT NULL clauses can be called
            // we store week and season information for future treatment
            val sql = "UPDATE " + FutureTrainingTable.Companion.TABLENAME +
                    " SET TRAINING_DATE=timestamp('1900-01-01'), TRAINING_ASSISTANTS_LEVEL=WEEK, COACH_LEVEL=SEASON, SOURCE=" +
                    DBDataSource.MANUAL.value + " WHERE TRUE"
            m_clJDBCAdapter!!.executeUpdate(sql)

            // Step 3. Finalize upgrade of FUTURETRAININGS table structure ===============================
            futureTrainingTable.tryChangeColumn("COACH_LEVEL", "NOT NULL")
            futureTrainingTable.tryChangeColumn("TRAINING_ASSISTANTS_LEVEL", "NOT NULL")
            futureTrainingTable.tryChangeColumn("TRAINING_DATE", "NOT NULL")
            futureTrainingTable.tryChangeColumn("SOURCE", "NOT NULL")
            futureTrainingTable.tryRenameColumn("TYPE", "TRAINING_TYPE")
            futureTrainingTable.tryRenameColumn("INTENSITY", "TRAINING_INTENSITY")
            futureTrainingTable.tryRenameColumn("STAMINATRAININGPART", "STAMINA_SHARE")
            futureTrainingTable.tryDeleteColumn("SEASON")
            futureTrainingTable.tryDeleteColumn("WEEK")
        }

        // SourceSystem does NOT exist in HO4, but HeimName does
        if (columnExistsInTable("HeimName", MatchLineupTable.Companion.TABLENAME)) {
            HOLogger.instance().debug(javaClass, "Upgrading DB structure SourceSystem/MatchType .... ")
            val matchLineupTable = dbManager!!.getTable(MatchLineupTable.Companion.TABLENAME)
            val matchesKurzInfoTable = dbManager!!.getTable(MatchesKurzInfoTable.Companion.TABLENAME)
            val matchIFATable = dbManager!!.getTable(IfaMatchTable.Companion.TABLENAME)
            matchesKurzInfoTable!!.tryDropPrimaryKey()
            matchLineupTable!!.tryDropPrimaryKey()
            matchDetailsTable.tryDropPrimaryKey()
            matchIFATable!!.tryDropPrimaryKey()

            // Update primary key from matchID => (matchID, MATCHTYP) because doublons might otherwise exists
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD PRIMARY KEY (MATCHID, MATCHTYP)")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHLINEUP ADD PRIMARY KEY (MATCHID, MATCHTYP)")
            matchLineupTable.tryDeleteColumn("SourceSystem")
            matchLineupTable.tryDeleteColumn("HeimName")
            matchLineupTable.tryDeleteColumn("HeimID")
            matchLineupTable.tryDeleteColumn("GastName")
            matchLineupTable.tryDeleteColumn("GastID")
            matchLineupTable.tryDeleteColumn("FetchDate")
            matchLineupTable.tryDeleteColumn("MatchDate")
            matchLineupTable.tryDeleteColumn("ArenaID")
            matchLineupTable.tryDeleteColumn("ArenaName")
            dbManager!!.getTable(MatchHighlightsTable.Companion.TABLENAME)!!.tryDeleteColumn("SourceSystem")
            dbManager!!.getTable(MatchLineupTeamTable.Companion.TABLENAME)!!.tryDeleteColumn("SourceSystem")
            dbManager!!.getTable(MatchSubstitutionTable.Companion.TABLENAME)!!.tryDeleteColumn("SourceSystem")
            dbManager!!.getTable(MatchDetailsTable.Companion.TABLENAME)!!.tryDeleteColumn("SourceSystem")
            dbManager!!.getTable(MatchLineupPlayerTable.Companion.TABLENAME)!!.tryDeleteColumn("SourceSystem")
            dbManager!!.getTable(IfaMatchTable.Companion.TABLENAME)!!.tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0")
            dbManager!!.getTable(MatchDetailsTable.Companion.TABLENAME)!!.tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0")
            dbManager!!.getTable(MatchHighlightsTable.Companion.TABLENAME)!!
                .tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0")
            dbManager!!.getTable(MatchLineupTable.Companion.TABLENAME)!!.tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0")
            dbManager!!.getTable(MatchLineupPlayerTable.Companion.TABLENAME)!!
                .tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0")
            dbManager!!.getTable(MatchLineupTeamTable.Companion.TABLENAME)!!
                .tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0")
            dbManager!!.getTable(MatchSubstitutionTable.Companion.TABLENAME)!!
                .tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0")
            dbManager!!.getTable(YouthTrainingTable.Companion.TABLENAME)!!.tryAddColumn("MATCHTYP", "INTEGER DEFAULT 0")

            // Correct history of MATCHESKURZINFO  ==============================================
            val sql = """
					UPDATE MATCHESKURZINFO
					SET MATCHTYP =
					    CASE MATCHTYP
					        WHEN 1001 THEN 3
					        WHEN 1002 THEN 3
					        WHEN 1003 THEN 3
					        WHEN 1004 THEN 3
					        WHEN 1101 THEN 50
					        ELSE -1
					    END
					WHERE
					    MATCHTYP IN (1001, 1002, 1003, 1004, 1101)
					    """.trimIndent()
            m_clJDBCAdapter!!.executeUpdate(sql)

            // Set MatchType in all table but YouthTable from entry in MATCHESKURZINFO =============================
            // use match lineup table to fix match types, since the lineup table holds the youth matches too
            // the types in lineup table seems to be the correct one - at least in my database (ws) - no fake types of sapphire cup and co.
            //List<String> lTables = List.of("IFA_MATCHES", "MATCHDETAILS", "MATCHLINEUP", "MATCHHIGHLIGHTS", "MATCHLINEUPPLAYER", "MATCHLINEUPTEAM",
            //		"MATCHORDER", "MATCHSUBSTITUTION");
            copyMatchTypes("MATCHESKURZINFO", "IFA_MATCHES")
            if (tableExists("MATCHORDER") && dbVersion < 600) copyMatchTypes(
                "MATCHESKURZINFO",
                "MATCHORDER"
            ) // no lineup available yet for match orders
            copyMatchTypes("MATCHLINEUP", "MATCHDETAILS")
            copyMatchTypes("MATCHLINEUP", "MATCHHIGHLIGHTS")
            copyMatchTypes("MATCHLINEUP", "MATCHLINEUPPLAYER")
            copyMatchTypes("MATCHLINEUP", "MATCHLINEUPTEAM")
            copyMatchTypes("MATCHLINEUP", "MATCHSUBSTITUTION")
            copyMatchTypes("MATCHLINEUP", "YOUTHTRAINING")

            // Update primary key from matchID => (matchID, MATCHTYP) because doublons might otherwise exists
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE IFA_MATCHES ADD PRIMARY KEY (MATCHID, MATCHTYP)")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD PRIMARY KEY (MATCHID, MATCHTYP)")
            HOLogger.instance().debug(javaClass, "Upgrade of DB structure SourceSystem/MatchType is complete ! ")
        }
        if (!columnExistsInTable("LAST_MATCH_TYPE", "SPIELER")) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LAST_MATCH_TYPE INTEGER ")
        }

        // Delete corrupt entries (wrong week numbers) from TA_PLAYER table
        val hrfTable = dbManager!!.getTable(HRFTable.Companion.TABLENAME) as HRFTable
        val hrf = hrfTable.getLatestHrf()
        if (hrf.isOK) {
            m_clJDBCAdapter!!.executeUpdate(
                "DELETE FROM " + TAPlayerTable.Companion.TABLENAME
                        + " WHERE WEEK> (SELECT SAISON*16+SPIELTAG-1 FROM "
                        + basicsTable.tableName + " WHERE HRF_ID=" + hrf.hrfId + ")"
            )
        }
        dbManager!!.getTable(MatchesKurzInfoTable.Companion.TABLENAME)!!.tryAddColumn("isObsolete", "BOOLEAN")
        if (!tableExists(MatchTeamRatingTable.Companion.TABLENAME)) {
            dbManager!!.getTable(MatchTeamRatingTable.Companion.TABLENAME)!!.createTable()
        }
        dbManager!!.getTable(XtraDataTable.Companion.TABLENAME)!!.tryAddColumn("CountryId", "INTEGER")
        updateDBVersion(dbVersion, 500)
    }

    private fun copyMatchTypes(fromTable: String, toTable: String) {
        var sql =
            "UPDATE $toTable t1 SET MATCHTYP = (SELECT MK.MATCHTYP FROM $fromTable MK WHERE t1.MATCHID = MK.MATCHID)"
        m_clJDBCAdapter!!.executeUpdate(sql)
        sql = "UPDATE $toTable SET MATCHTYP = 0 WHERE MATCHTYP IS NULL"
        m_clJDBCAdapter!!.executeUpdate(sql)
    }

    @Throws(SQLException::class)
    private fun updateDBv400(dbVersion: Int) {
        // Delete existing values to provide sane defaults.
        m_clJDBCAdapter!!.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'spielerUebersichtsPanel_horizontalRightSplitPane'")
        m_clJDBCAdapter!!.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_verticalSplitPane'")
        m_clJDBCAdapter!!.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_horizontalRightSplitPane'")
        m_clJDBCAdapter!!.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_horizontalLeftSplitPane'")
        if (!columnExistsInTable("SeasonOffset", BasicsTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE BASICS ADD COLUMN SeasonOffset INTEGER")
        }
        if (!columnExistsInTable("Duration", MatchesKurzInfoTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN Duration INTEGER ")
        }
        if (!columnExistsInTable("MatchPart", MatchHighlightsTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN MatchPart INTEGER ")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EventVariation INTEGER ")
        }
        if (!columnExistsInTable("HomeGoal0", MatchDetailsTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal0 INTEGER ")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal1 INTEGER ")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal2 INTEGER ")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal3 INTEGER ")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal4 INTEGER ")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal0 INTEGER ")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal1 INTEGER ")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal2 INTEGER ")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal3 INTEGER ")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal4 INTEGER ")
        }
        if (!columnExistsInTable("NAME", TAPlayerTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN NAME VARCHAR (100) ")
        }

        // use defaults player formula from defaults.xml by resetting the value in the database
        try {
            val faktorenTab = dbManager!!.getTable(FaktorenTable.Companion.TABLENAME)
            if (faktorenTab != null) {
                faktorenTab.tryDropTable()
                faktorenTab.createTable()
            }
        } catch (throwables: SQLException) {
            HOLogger.instance().error(javaClass, "updateDBv400:  Faktoren table could not be reset")
            throwables.printStackTrace()
        }
        resetUserColumns()

        //create FuturePlayerTrainingTable
        if (!tableExists(FuturePlayerTrainingTable.Companion.TABLENAME)) {
            dbManager!!.getTable(FuturePlayerTrainingTable.Companion.TABLENAME)!!.createTable()
        }
        updateDBVersion(dbVersion, 400)
    }

    @Throws(SQLException::class)
    private fun updateDBv301(dbVersion: Int) {
        m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHESKURZINFO ALTER COLUMN isDerby SET DATA TYPE BOOLEAN")
        m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHESKURZINFO ALTER COLUMN isNeutral SET DATA TYPE BOOLEAN")
        if (!columnExistsInTable("EVENT_INDEX", MatchHighlightsTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EVENT_INDEX INTEGER")
        }
        if (!columnExistsInTable("INJURY_TYPE", MatchHighlightsTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN INJURY_TYPE TINYINT")
        }
        if (columnExistsInTable("TYP", MatchHighlightsTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("UPDATE MATCHHIGHLIGHTS SET MATCH_EVENT_ID = (TYP * 100) + SUBTYP WHERE MATCH_EVENT_ID IS NULL")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP TYP")
        }
        if (!columnExistsInTable("LastMatchDate", SpielerTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchDate VARCHAR (100)")
        }
        if (!columnExistsInTable("LastMatchRating", SpielerTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchRating INTEGER")
        }
        if (!columnExistsInTable("LastMatchId", SpielerTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchId INTEGER")
        }
        mutableListOf<String>("HEIMTORE", "GASTTORE", "SUBTYP").forEach(Consumer<String> { s: String ->
            try {
                if (columnExistsInTable(s, MatchHighlightsTable.Companion.TABLENAME)) {
                    m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP $s")
                }
            } catch (e: SQLException) {
                HOLogger.instance().log(javaClass, e)
            }
        })
        m_clJDBCAdapter!!.executeUpdate("CREATE INDEX IF NOT EXISTS matchdetails_heimid_idx ON MATCHDETAILS (HEIMID)")
        m_clJDBCAdapter!!.executeUpdate("CREATE INDEX IF NOT EXISTS matchdetails_gastid_idx ON MATCHDETAILS (GASTID)")
        m_clJDBCAdapter!!.executeUpdate("CREATE INDEX IF NOT EXISTS matchkurzinfo_heimid_idx ON MATCHESKURZINFO (HEIMID)")
        m_clJDBCAdapter!!.executeUpdate("CREATE INDEX IF NOT EXISTS matchkurzinfo_gastid_idx ON MATCHESKURZINFO (GASTID)")
        m_clJDBCAdapter!!.executeUpdate("CREATE INDEX IF NOT EXISTS matchhighlights_teamid_idx ON MATCHHIGHLIGHTS (TEAMID)")
        m_clJDBCAdapter!!.executeUpdate("CREATE INDEX IF NOT EXISTS matchhighlights_eventid_idx ON MATCHHIGHLIGHTS (MATCH_EVENT_ID)")
        mutableListOf<String>("GlobalRanking", "LeagueRanking", "RegionRanking", "PowerRating").forEach(
            Consumer<String> { s: String ->
                try {
                    if (!columnExistsInTable(s, VereinTable.Companion.TABLENAME)) {
                        m_clJDBCAdapter!!.executeUpdate(String.format("ALTER TABLE VEREIN ADD COLUMN %s INTEGER", s))
                    }
                } catch (e: SQLException) {
                    HOLogger.instance().log(javaClass, e)
                }
            })
        mutableListOf<String>("TWTrainer", "Physiologen").forEach(Consumer<String> { s: String ->
            try {
                if (columnExistsInTable(s, VereinTable.Companion.TABLENAME)) {
                    m_clJDBCAdapter!!.executeUpdate("ALTER TABLE VEREIN DROP $s")
                }
            } catch (e: SQLException) {
                HOLogger.instance().log(javaClass, e)
            }
        })
        updateDBVersion(dbVersion, 301)
    }

    @Throws(SQLException::class)
    private fun updateDBv300() {
        // HO 3.0

        // delete old divider locations
        m_clJDBCAdapter!!.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_LowerLefSplitPane'")
        m_clJDBCAdapter!!.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_UpperLeftSplitPane'")
        m_clJDBCAdapter!!.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_MainSplitPane'")

        //store ArenaId into MATCHESKURZINFO table
        if (!columnExistsInTable("ArenaId", MatchesKurzInfoTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN ArenaId INTEGER")
        }

        //store RegionId into MATCHESKURZINFO table
        if (!columnExistsInTable("RegionId", MatchesKurzInfoTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN RegionId INTEGER")
        }

        //store Weather into MATCHESKURZINFO table
        if (!columnExistsInTable("Weather", MatchesKurzInfoTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN Weather INTEGER")
        }

        //store WeatherForecast into MATCHESKURZINFO table
        if (!columnExistsInTable("WeatherForecast", MatchesKurzInfoTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN WeatherForecast INTEGER")
        }

        //store isDerby into MATCHESKURZINFO table
        if (!columnExistsInTable("isDerby", MatchesKurzInfoTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN isDerby BOOLEAN")
        }

        //store isNeutral into MATCHESKURZINFO table
        if (!columnExistsInTable("isNeutral", MatchesKurzInfoTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN isNeutral BOOLEAN")
        }

        //store Salary into TA_PLAYER table
        if (!columnExistsInTable("SALARY", TAPlayerTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN SALARY INTEGER")
        }

        //store Stamina  into TA_PLAYER table
        if (!columnExistsInTable("STAMINA", TAPlayerTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN STAMINA INTEGER")
        }

        //store MotherClubBonus  into TA_PLAYER table
        if (!columnExistsInTable("MOTHERCLUBBONUS", TAPlayerTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN MOTHERCLUBBONUS BOOLEAN")
        }

        //store Loyalty  into TA_PLAYER table
        if (!columnExistsInTable("LOYALTY", TAPlayerTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN LOYALTY INTEGER")
        }

        //store RATINGINDIRECTSETPIECESATT  into MATCHDETAILS table
        if (!columnExistsInTable("RATINGINDIRECTSETPIECESATT", MatchDetailsTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RATINGINDIRECTSETPIECESATT INTEGER")
        }

        //store RATINGINDIRECTSETPIECESDEF  into MATCHDETAILS table
        if (!columnExistsInTable("RATINGINDIRECTSETPIECESDEF", MatchDetailsTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RATINGINDIRECTSETPIECESDEF INTEGER")
        }

        //store FirstName, Nickname  into Playertable
        if (!columnExistsInTable("FirstName", SpielerTable.Companion.TABLENAME)) {
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE SPIELER ADD COLUMN FirstName VARCHAR (100)")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE SPIELER ADD COLUMN NickName VARCHAR (100)")
            m_clJDBCAdapter!!.executeUpdate("ALTER TABLE SPIELER ALTER COLUMN Name RENAME TO LastName")
        }

        // Delete league plans which are not of our own team
        try {
            // find own league plans
            val teamId = getTeamId()
            // select saison,ligaid from paarung where heimid=520472 group by saison,ligaid
            val ownLeaguePlans = HashMap<Int, Int>()
            var rs =
                m_clJDBCAdapter!!.executeQuery("select saison,ligaid from paarung where heimid=$teamId group by saison,ligaid")
            if (rs != null) {
                while (rs.next()) {
                    val saison = rs.getInt(1)
                    val league = rs.getInt(2)
                    ownLeaguePlans[saison] = league
                }
                rs.close()
            }
            // delete entries in SPIELPLAN and PAARUNG which are not from own team
            rs = m_clJDBCAdapter!!.executeQuery("select saison,ligaid from spielplan")
            if (rs != null) {
                while (rs.next()) {
                    val saison = rs.getInt(1)
                    val league = rs.getInt(2)
                    if (!ownLeaguePlans.containsKey(saison) || ownLeaguePlans[saison] != league) {
                        // league is not our own one
                        m_clJDBCAdapter!!.executeUpdate("DELETE FROM spielplan WHERE ligaid=$league and saison=$saison")
                        m_clJDBCAdapter!!.executeUpdate("DELETE FROM paarung WHERE ligaid=$league and saison=$saison")
                    }
                }
                rs.close()
            }
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, e)
        }
        HOLogger.instance().info(DBUpdater::class.java, "updateDBv300() successfully completed")
    }

    private fun updateDBVersion(dbVersion: Int, version: Int) {
        if (version < dbVersion) {
            if (!HO.isDevelopment()) {
                HOLogger.instance().info(DBUpdater::class.java, "Update to $version done. Updating DBVersion")
                dbManager!!.saveUserParameter("DBVersion", version)
            } else {
                HOLogger.instance().debug(
                    DBUpdater::class.java,
                    "Update to $version done but this is a development version so DBVersion will remain unchanged"
                )
            }
        } else if (version == dbVersion) {
            if (!HO.isDevelopment()) {
                HOLogger.instance().info(DBUpdater::class.java, "Update complete, setting DBVersion to $version")
                dbManager!!.saveUserParameter("DBVersion", version)
            } else {
                HOLogger.instance().debug(
                    DBUpdater::class.java,
                    "Update to $version complete but this is a development version so DBVersion will remain unchanged"
                )
            }
        } else {
            HOLogger.instance().error(
                DBUpdater::class.java, "Error trying to set DB version to unidentified value:  " + version
                        + " (isDevelopment=" + HO.isDevelopment() + ")"
            )
        }
    }

    private fun getTeamId(): Int {
        try {
            val rs = m_clJDBCAdapter!!.executeQuery("select teamid from basics limit 1")
            if (rs != null) {
                rs.next()
                val ret = rs.getInt(1)
                rs.close()
                return ret
            }
        } catch (e: SQLException) {
            HOLogger.instance().log(javaClass, e)
        }
        return 0
    }

    @Throws(SQLException::class)
    private fun columnExistsInTable(columnName: String, tableName: String): Boolean {
        val sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME = '" +
                tableName.uppercase(Locale.getDefault()) + "' AND COLUMN_NAME = '" + columnName.uppercase(Locale.getDefault()) + "'"
        val rs = m_clJDBCAdapter!!.executeQuery(sql)
        return rs?.next() ?: false
    }

    @Throws(SQLException::class)
    private fun tableExists(tableName: String): Boolean {
        val sql =
            "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '" + tableName.uppercase(Locale.getDefault()) + "'"
        val rs = m_clJDBCAdapter!!.executeQuery(sql)
        return rs?.next() ?: false
    }

    private fun resetUserColumns() {
        HOLogger.instance().debug(javaClass, "Resetting player overview rows.")
        var sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 2000 AND 3000"
        m_clJDBCAdapter!!.executeUpdate(sql)
        HOLogger.instance().debug(javaClass, "Resetting lineup overview rows.")
        sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 3000 AND 4000"
        m_clJDBCAdapter!!.executeUpdate(sql)
    }
}
