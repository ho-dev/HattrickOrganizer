package core.db.backup

import core.db.user.UserManager
import core.util.HOLogger
import java.io.File
import java.text.SimpleDateFormat
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

		if (files != null) {
			files.sortedByDescending { f -> f.lastModified() }
				.drop(UserManager.instance().currentUser.numberOfBackups)
				.forEach { f -> f.delete() }
		}
	}

	private fun getFilesToBackup(dbDirectory: File): Array<File> {
		return dbDirectory.listFiles { file: File ->
			file.isFile && extensions.any { suffix -> file.extension == suffix }
		} ?: arrayOf()
	}
}
