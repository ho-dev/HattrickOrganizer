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
    private const val DAYS_PER_SEASON = 112

	// zip and delete db
	@JvmStatic
	fun backup(dbDirectory: File) {
        if (!dbDirectory.isDirectory) {
            HOLogger.instance().error(
                BackupHelper::class.java,
                "Backup directory does not exist or is not a directory: $dbDirectory"
            )
            return
        }

        val numberOfBackups = UserManager.instance().currentUser.numberOfBackups
        if (numberOfBackups < 1) {
            HOLogger.instance().warning(
                BackupHelper::class.java,
                "Number of backups is set to 0. No backup of HO database will be created"
            )
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
            deleteOldFiles(dbDirectory, numberOfBackups)
        } catch (e: Exception) {
            HOLogger.instance().log(BackupHelper::class.java, e)
        }
    }

	/**
	 * Deletes old zip files in the directory <code>dbDirectory</code>.
	 *
	 * @param dbDirectory Directory where to find the zip files to be deleted.
	 */
	private fun deleteOldFiles(dbDirectory: File, numberOfBackups: Int) {
        val files = dbDirectory.listFiles { file: File ->
            file.isFile && file.extension == HOZip.zipExt
        }
        if (files == null) {
            HOLogger.instance().error(this.javaClass, "Cannot access backup directory $dbDirectory")
        } else if (files.isNotEmpty()) {
            val fileList = files.toList()
            val deleteBackupList = getBackupFilesToDelete(fileList, numberOfBackups)
            deleteBackupList.forEach { file ->
                if (file.delete()) {
                    HOLogger.instance().info(this.javaClass, "Deleted old backup file: ${file.name}")
                } else {
                    HOLogger.instance().error(this.javaClass, "Failed to delete old backup file: ${file.name}")
                }
            }
        }
    }

    /**
     * Filter backup files from list that should be deleted
     *  - The configured count of youngest backups are not included to the result (not deleted)
     *  - If the backup file is younger than 112 days the latest file of each week is not included
     *  - If the backup file is older than 112 days the latest file of each season is not included
     *
     * When the returned files are deleted, the user has a backup file for each previous season
     * and also a file for each week of the last 16 weeks, plus the configured number of the most recent backups.
     */
    private fun getBackupFilesToDelete(files: List<File>, numberOfBackups : Int) : List<File> {
        require(numberOfBackups > 0) { "numberOfBackups must be greater than 0" }
        val ret = mutableListOf<File>()
        var keptBackupFileLastModifiedWeek: HODateTime.HTWeek? = null
        var keptFiles = 0
        val currentTimestamp = HODateTime.now()
        files.sortedByDescending { file -> file.lastModified() }
            .forEach { file ->
                val lastModified = HODateTime(Instant.ofEpochMilli(file.lastModified()))
                val lastModifiedWeek = lastModified.toHTWeek()
                val fileAgeInDays = HODateTime.between(lastModified, currentTimestamp).toDays()
                if (keptFiles < numberOfBackups ||
                    keptBackupFileLastModifiedWeek == null ||
                    fileAgeInDays < DAYS_PER_SEASON && !lastModifiedWeek.equals(keptBackupFileLastModifiedWeek) ||
                    fileAgeInDays >= DAYS_PER_SEASON && lastModifiedWeek.season != keptBackupFileLastModifiedWeek.season
                ) {
                    keptBackupFileLastModifiedWeek = lastModifiedWeek
                    keptFiles++
                } else {
                    ret.add(file)
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
