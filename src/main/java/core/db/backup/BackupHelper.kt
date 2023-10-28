package core.db.backup

import core.db.user.UserManager.getCurrentUser
import core.file.ExampleFileFilter
import core.util.HOLogger
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * HSQL DB zipper
 * @author Thorsten Dietz
 */
object BackupHelper {
    // zip and delete db
	@JvmStatic
	fun backup(dbDirectory: File) {
        if (!dbDirectory.exists()) {
            return
        }
        val filesToBackup = getFilesToBackup(dbDirectory)
        if (filesToBackup != null) {
            if (filesToBackup.isEmpty()) {
                return
            }
        }
        val zOut: HOZip
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            zOut = HOZip("$dbDirectory${File.separator}db_${getCurrentUser().teamName}-${sdf.format(Date())}.zip")
            if (filesToBackup != null) {
                for (file in filesToBackup) {
                    zOut.addFile(file)
                }
            }
            zOut.closeArchive()
        } catch (e: Exception) {
            HOLogger.instance().log(BackupHelper::class.java, e)
        }
        deleteOldFiles(dbDirectory)
    }

    /**
     * delete old zip files, which are out of backuplevel
     */
    private fun deleteOldFiles(dbDirectory: File) {
        var toDelete: File? = null
        val filter = ExampleFileFilter("zip")
        filter.isIgnoreDirectories = true
        val files = dbDirectory.listFiles(filter)
        if (files != null && files.size > getCurrentUser().numberOfBackups) {
            for (i in files.indices) {
                if (i == 0 || toDelete != null && toDelete.lastModified() > files[i].lastModified()) {
                    toDelete = files[i]
                }
            }
            toDelete?.delete()
        }
    }

    private fun getFilesToBackup(dbDirectory: File): Array<out File>? {
        return dbDirectory.listFiles { file: File ->
            file.getName().endsWith(".script") ||
                    file.getName().endsWith(".data") ||
                    file.getName().endsWith(".backup") ||
                    file.getName().endsWith(".log") ||
                    file.getName().endsWith(".properties")
        }
    }
}
