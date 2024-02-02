package tool.dbcleanup

import core.db.DBManager
import core.gui.RefreshManager
import core.model.HOVerwaltung
import core.model.enums.MatchType
import core.model.match.MatchKurzInfo
import core.util.HODateTime
import core.util.HOLogger
import java.sql.Timestamp
import java.time.temporal.ChronoUnit
import java.util.*
import javax.swing.JFrame

/**
 * HO Database Cleanup Tool
 * Removes old HRFs and old matches from the DB to speedup HO
 *
 * @author flattermann <HO@flattermann.net>
 */
class DBCleanupTool {

    companion object {
        var REMOVE_NONE: Int = -1
        var REMOVE_ALL: Int = 0
    }

    fun showDialog(owner: JFrame?) {
        DBCleanupDialog(owner, this)
    }

    /**
     * Remove old HRFs
     *
     * @param keepWeeks remove HRFs older than x weeks (-1=keep All, 0=remove All)
     * @param autoRemove if true, automatically remove all HRFs except the first per training week
     */
    fun cleanupHRFs(keepWeeks: Int, autoRemove: Boolean) {
        var removeDate: Timestamp? = null
        if (keepWeeks >= 0) {
            val hrfDateFrom = HODateTime.now().minus(keepWeeks * 7, ChronoUnit.DAYS)
            removeDate = hrfDateFrom.toDbTimestamp()
        }
        if (removeDate != null || autoRemove) {
            cleanupHRFs(removeDate, autoRemove)
        }
    }

    /**
     * Remove old HRFs
     *
     * @param removeDate remove HRFs older than x (null=keep all)
     * @param autoRemove if true, automatically remove all HRFs except the first per training week
     */
    private fun cleanupHRFs(removeDate: Timestamp?, autoRemove: Boolean) {
        HOLogger.instance().debug(
            javaClass,
            "Removing old HRFs: removeDate=$removeDate, autoRemove=$autoRemove"
        )
        val allHrfs = DBManager.instance().loadAllHRFs(true)
        val latestHrf = DBManager.instance().latestHRF
        var lastSeason = -1
        var lastWeek = -1
        var counter = 0

        for (curHrf in allHrfs) {
            val curId = curHrf.hrfId
            val curDate = curHrf.datum
            val htWeek = curDate.toTrainingWeek()
            val curHtSeasonTraining = htWeek.season
            val curHtWeekTraining = htWeek.week
            var remove = false
            if (removeDate != null && removeDate.after(curDate.toDbTimestamp())) {
                remove = true
            } else if (autoRemove) {
                if (lastSeason == curHtSeasonTraining && lastWeek == curHtWeekTraining) {
                    remove = true
                } else {
                    lastSeason = curHtSeasonTraining
                    lastWeek = curHtWeekTraining
                }
            }
            // Do not remove the latest HRF
            if (remove && curId != latestHrf.hrfId) {
                HOLogger.instance().debug(
                    javaClass,
                    "Removing Hrf: $curId @ $curDate ($curHtSeasonTraining/$curHtWeekTraining)"
                )
                DBManager.instance().deleteHRF(curId)
                counter++
            }
        }
        HOLogger.instance().debug(javaClass, "Removed $counter/${allHrfs.size} HRFs from DB!")
        if (counter > 0) {
            reInitHO()
        }
    }

    private fun calculateDateLimit(numWeeks: Int): Timestamp {
        val cal: Calendar = GregorianCalendar()
        cal.add(Calendar.WEEK_OF_YEAR, -numWeeks)
        return Timestamp(cal.timeInMillis)
    }

    /**
     * Remove old matches from DB (by date)
     *
     * @param cleanupDetails Parameters for cleanup
     */
    fun cleanupMatches(cleanupDetails: CleanupDetails) {
        HOLogger.instance().debug(
            javaClass,
            "Removing old matches: ownTeamMatchTypes=${cleanupDetails.ownTeamMatchTypes}, " +
                    "ownTeamWeeks=${cleanupDetails.ownTeamWeeks}, " +
                    "otherTeamMatchTypes=${cleanupDetails.otherTeamMatchTypes}, " +
                    "otherTeamWeeks=${cleanupDetails.otherTeamWeeks}"
        )

        var counter = 0
        val kurzInfos = DBManager.instance().getMatchesKurzInfo(-1)
        val myTeamId = HOVerwaltung.instance().model.basics.teamId

        for (curKurzInfo in kurzInfos) {
            val curMatchId = curKurzInfo.matchID
            var removeMatch = false

            if (checkDeleteMatch(myTeamId, curKurzInfo, cleanupDetails)) {
                HOLogger.instance().info(
                    javaClass,
                    "Match to be deleted: ${curKurzInfo.matchID}, " +
                            "matchType=${curKurzInfo.matchType}, matchDate=${curKurzInfo.matchSchedule}"
                )
                removeMatch = true
            }

            if (removeMatch) {
                // Remove match
                HOLogger.instance().debug(
                    javaClass,
                    "Removing match $curMatchId"
                )
                DBManager.instance().deleteMatch(curKurzInfo)
                counter++
            }
        }
        HOLogger.instance().debug(javaClass, "Removed $counter/${kurzInfos.size} matches from DB!")

        if (counter > 0) {
            reInitHO()
        }
    }

    private fun checkDeleteMatch(
        myTeamId: Int,
        curKurzInfo: MatchKurzInfo,
        cleanupDetails: CleanupDetails
    ): Boolean {
        val curMatchDate = curKurzInfo.matchSchedule.toDbTimestamp()
        val curMatchType = curKurzInfo.matchType
        val isMyMatch = (curKurzInfo.homeTeamID == myTeamId || curKurzInfo.guestTeamID == myTeamId)

        val ownMatchToDelete = (isMyMatch && cleanupDetails.ownTeamMatchTypes.contains(curMatchType)
                && calculateDateLimit(cleanupDetails.ownTeamWeeks).after(curMatchDate))
        val otherMatchToDelete = (!isMyMatch && cleanupDetails.otherTeamMatchTypes.contains(curMatchType)
                && calculateDateLimit(cleanupDetails.otherTeamWeeks).after(curMatchDate))

        return ownMatchToDelete || otherMatchToDelete

    }

    /**
     * Returns the number of matches stored in the database.
     *
     * @return Int – Number of matches stored in DB.
     */
    fun getMatchesCount(): Int {
        val kurzInfos = DBManager.instance().getMatchesKurzInfo(-1)
        return kurzInfos.size
    }

    /**
     * Returns the number of HRF entries stored in the database.
     *
     * @return Int – Number of HRF records in DB.
     */
    fun getHrfCount(): Int {
        val allHrfs = DBManager.instance().loadAllHRFs(true)
        return allHrfs.size
    }

    private fun reInitHO() {
       RefreshManager.instance().doReInit()
    }
}
//
//fun main() {
//    HOVerwaltung.instance().loadLatestHoModel()
//    val ho = HO::class.java
//    val field = ho.getDeclaredField("versionType")
//    field.isAccessible = true
//    field.set(ho, "DEV")
//
//    val frame = JFrame()
//    frame.title = "Test"
//    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//
//    DBCleanupDialog(frame, DBCleanupTool())
//}