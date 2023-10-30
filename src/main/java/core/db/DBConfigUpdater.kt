package core.db

import core.HO
import core.model.enums.DBDataSource
import core.training.TrainingPerWeek
import core.training.TrainingWeekManager
import core.util.HODateTime
import core.util.HOLogger
import java.time.temporal.ChronoUnit
import javax.swing.JOptionPane

internal object DBConfigUpdater {
    private val dbManager: DBManager = DBManager
    private val m_clJDBCAdapter = dbManager.jdbcAdapter

    fun updateDBConfig(configVersion: Double) {
        val currentConfigVersion =
            (dbManager.getTable(UserConfigurationTable.TABLENAME) as UserConfigurationTable).getLastConfUpdate()

        /*
		 * We have to use separate 'if-then' clauses for each conf version  (ascending order) because a user might have skipped some HO releases
		 * DO NOT use 'if-then-else' here, as this would ignores some updates!
		 */
        if (currentConfigVersion < 1.436) {
            HOLogger.instance().log(DBConfigUpdater::class.java, "DB config version $currentConfigVersion is too old")
            try {
                JOptionPane.showMessageDialog(
                    null,
                    "DB is too old.\nPlease update first to HO! 3.0", "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            } catch (e: Exception) {
                HOLogger.instance().log(DBConfigUpdater::class.java, e)
            }
            System.exit(0)
        }
        if (currentConfigVersion < 5 || HO.isDevelopment() && currentConfigVersion == 5.0) {
            HOLogger.instance().log(DBConfigUpdater::class.java, "Updating configuration to version 5.0  ...")
            updateDBConfig5(configVersion, HO.isDevelopment() && currentConfigVersion == 5.0)
        }
    }

    private fun updateDBConfig5(configVersion: Double, alreadyApplied: Boolean) {
        if (!alreadyApplied) {
            var twm: TrainingWeekManager? = null

            // Creating entries into TRAININGS table ===========================================================
            try {
                val sql = "SELECT TRAININGDATE FROM XTRADATA ORDER BY TRAININGDATE ASC LIMIT 1"
                val rs = m_clJDBCAdapter.executeQuery(sql)
                rs!!.next()
                val firstTrainingDate = HODateTime.fromDbTimestamp(rs.getTimestamp("TRAININGDATE"))
                twm = TrainingWeekManager(firstTrainingDate, false)
                twm.push2TrainingsTable()
            } catch (e: Exception) {
                HOLogger.instance()
                    .error(DBConfigUpdater::class.java, "Error when trying to create entries inside TRAINING table")
            }

            // Creating entries into FUTURETRAININGS table ===========================================================
            try {
                assert(twm != null)
                val trainingList = twm!!.trainingList
                val optionallastTraining =
                    trainingList.stream().max(Comparator.comparing { obj: TrainingPerWeek -> obj.trainingDate })
                if (optionallastTraining.isEmpty) {
                    throw Exception("Can't determine last training")
                }

                // determine parameter of last training
                val latestTraining = optionallastTraining.get()
                val assistantLevel = latestTraining.trainingAssistantsLevel
                val coachLevel = latestTraining.coachLevel
                val oTrainingDate = latestTraining.trainingDate
                val htWeek = oTrainingDate.toHTWeek()

                // iterate through entries of Future Training table and migrate data
                val futureTrainings: MutableList<TrainingPerWeek> = ArrayList()
                val futureTrainingsInDB: List<TrainingPerWeek?>? = DBManager.getFutureTrainingsVector()
                var nbDays: Int
                var futureTraining: TrainingPerWeek
                if (futureTrainingsInDB != null) {
                    for (futureTrainingDB in futureTrainingsInDB) {
                        val trainingWeek = futureTrainingDB!!.trainingDate.toHTWeek()
                        nbDays = ((trainingWeek.season - htWeek.season) * 16 + (trainingWeek.week - htWeek.week)) * 7
                        if (nbDays <= 0) {
                            continue
                        }
                        val futureTrainingDate = oTrainingDate.plus(nbDays, ChronoUnit.DAYS)
                        futureTraining = TrainingPerWeek(
                            futureTrainingDate, futureTrainingDB.trainingType, futureTrainingDB.trainingIntensity,
                            futureTrainingDB.staminaShare, assistantLevel, coachLevel, DBDataSource.MANUAL
                        )
                        futureTrainings.add(futureTraining)
                    }
                }

                // store futureTrainings in database
                DBManager.saveFutureTrainings(futureTrainings)
            } catch (e: Exception) {
                HOLogger.instance()
                    .error(DBConfigUpdater::class.java, "Error when trying to create entries inside TRAINING table")
            }
            updateDBConfigVersion(configVersion, 5.0)
        }
    }

    private fun updateDBConfigVersion(dbConfigVersion: Double, version: Double) {
        if (version < dbConfigVersion) {
            HOLogger.instance()
                .debug(DBConfigUpdater::class.java, "Update to config $version done. Updating DBConfigVersion")
            dbManager.saveUserParameter("LastConfUpdate", version)
        } else if (version == dbConfigVersion) {
            HOLogger.instance()
                .debug(DBConfigUpdater::class.java, "DB config update complete, setting DBConfigVersion to $version")
            dbManager.saveUserParameter("LastConfUpdate", version)
        } else {
            HOLogger.instance().error(
                DBConfigUpdater::class.java,
                "Error trying to set DBConfigVersion to unidentified value:  $version (isDevelopment=${HO.isDevelopment()})"
            )
        }
    }
}
