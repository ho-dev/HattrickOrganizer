package core.db.backup

import core.db.user.UserManager
import core.util.HODateTime
import core.util.HOLogger
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

/**
 * HSQL DB zipper
 * @author Thorsten Dietz
 */

object BackupHelper {
	private val sdf = SimpleDateFormat("yyyy-MM-dd")
	private val extensions = listOf("script", "data", "backup", "log", "properties")

	// zip and delete db
	@JvmStatic
	fun backup(dbDirectory: File) {
		if (!dbDirectory.exists()) {
			return
		}

		val filesToBackup = getFilesToBackup(dbDirectory)
		if (filesToBackup.isEmpty()) {
			return
		}

		val zOut: HOZip
		try {

			zOut = HOZip(
				"""$dbDirectory${File.separator}db_${UserManager.instance().currentUser.teamName}-${sdf.format(Date())}.${HOZip.zipExt}"""
			)

			for (file in filesToBackup) {
				zOut.addFile(file)
			}

			zOut.closeArchive()
		} catch (e: Exception) {
			HOLogger.instance().log(BackupHelper::class.java, e)
		}

		deleteOldFiles(dbDirectory)
	}

	/**
	 * Deletes old zip files in the directory <code>dbDirectory</code>.
	 *
	 * @param dbDirectory Directory where to find the zip files to be deleted.
	 */
	private fun deleteOldFiles(dbDirectory: File) {
        val files = dbDirectory.listFiles { file: File ->
            file.isFile && file.extension == HOZip.zipExt
        }?.toList()
        val deleteBackupList = getBackupFilesToDelete(files)
        deleteBackupList.forEach { f -> f.delete() }
    }

    /**
     * Filter backup files from list that should be deleted
     *  - The configured count of youngest backups are removed from the list (not deleted)
     *  - If the backup file is younger than 112 days the latest file of each week is removed
     *  - If the backup file is older than 112 days the latest file of each season is removed
     *
     *  At the end the user has one backup file for each previous season
     *  and additionally one file for each week for the last 16 weeks
     *  plus the configured count of latest backups
     */
    private fun getBackupFilesToDelete(files: List<File>?) : List<File> {
        val ret = mutableListOf<File>()
        if (files != null) {
            var keptBackupFileLastModifiedWeek: HODateTime.HTWeek? = null
            var keptFiles = 0
            val currentTimestamp = HODateTime.now()
            files.sortedByDescending { f -> f.lastModified() }
                .forEach { f ->
                    val lastModified = HODateTime(Instant.ofEpochMilli(f.lastModified()))
                    val lastModifiedWeek = lastModified.toHTWeek()
                    val fileAgeInDays = HODateTime.between(lastModified, currentTimestamp).toDays()
                    val DAYS_PER_SEASON = 112
                    if (keptFiles < UserManager.instance().currentUser.numberOfBackups ||
                        fileAgeInDays < DAYS_PER_SEASON && !lastModifiedWeek.equals(keptBackupFileLastModifiedWeek) ||
                        fileAgeInDays >= DAYS_PER_SEASON && !lastModifiedWeek.season.equals(keptBackupFileLastModifiedWeek!!.season)
                    ) {
                        keptBackupFileLastModifiedWeek = lastModifiedWeek
                        keptFiles++
                    } else {
                        ret.add(f)
                    }
                }
        }
        return ret
    }

    private fun getFilesToBackup(dbDirectory: File): Array<File> {
		return dbDirectory.listFiles { file: File ->
			file.isFile && extensions.any { suffix -> file.extension == suffix }
		} ?: arrayOf()
	}
}
