package core.db.backup

import core.util.HOLogger
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

private const val COMPRESSION_LEVEL = 5
private const val COMPRESSION_METHOD = ZipOutputStream.DEFLATED

class HOZip(filename: String) : File(filename) {
	private val zOut: ZipOutputStream

	var fileCount: Int = 0
		private set

	/**
	 * Creates a new HOZip object.
	 */
	init {
		HOLogger.instance().info(javaClass, "Create Backup: $filename")
		zOut = ZipOutputStream(FileOutputStream(this))
		zOut.setMethod(COMPRESSION_METHOD)
		zOut.setLevel(COMPRESSION_LEVEL)
	}

	@Throws(Exception::class)
	fun addFile(file: File) {
		FileInputStream(file).use { fis ->
			zOut.putNextEntry(ZipEntry(file.name))
			fis.copyTo(zOut)
			zOut.closeEntry()
		}
		fileCount++
	}

	@Throws(Exception::class)
	fun closeArchive() {
		zOut.finish()
		zOut.close()
	}

	companion object {
		val zipExt = "zip"
		private val sdf = SimpleDateFormat("yyyy-MM-dd")
		fun createZipName(prefix: String): String = prefix + sdf.format(Date()) + ".${zipExt}"
	}
}
