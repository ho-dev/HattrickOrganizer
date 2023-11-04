package core.file

import core.util.HOLogger

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile


/**
 * Utility class for handling ZipFiles.
 */
object ZipHelper {

	/**
	 * Extracts the file with the given entryName to the specified directory. If
	 * not existing, the destination directory is created.
	 * 
	 * @param zipFile
	 *            the zip file to extract a file from.
	 * @param entryName
	 *            the name of the entry to extract.
	 * @param destDir
	 *            the destination directory.
	 * @throws IOException
	 *             if an io error occurs while extracting.
	 */
	@Throws(IOException::class)
	fun extractFile(zipFile: ZipFile, entryName: String, destDir: String) {

		val file = File(destDir)
		file.mkdirs()

		for (entry in zipFile.entries()) {
			val fileName = destDir + File.separatorChar + entry.name
			if (fileName.uppercase(Locale.ENGLISH).endsWith(entryName.uppercase(Locale.ENGLISH))) {
				extractEntry(zipFile, entry, fileName)
			}
		}
	}

	/**
	 * Closes a zip file. This method is null safe, if the given zipFile is
	 * null, this method does nothing. The method will not throw an exception.
	 * If an exception occurs, it will be logged by HOLogger.
	 * 
	 * @param zipFile
	 *            the zip file to close.
	 */
	fun close(zipFile: ZipFile?) {
		try {
			zipFile?.close()
		} catch (ex: Exception) {
			HOLogger.instance().error(ZipHelper.javaClass, ex)
		}
	}

	/**
	 * Extracts a zip file to a directory. If the destination directory does not
	 * exist, it will be created.
	 * 
	 * @param file
	 *            the file to extract.
	 * @param destDir
	 *            the destination directory.
	 * @throws ZipException
	 *             if a ZIP error has occurred
	 * @throws IOException
	 *             if an I/O error has occurred
	 */
	@Throws(ZipException::class, IOException::class)
	fun unzip(file: File, destDir: File) {
		destDir.mkdirs()
		var zipFile:ZipFile? = null
		try {
			zipFile = ZipFile(file)
			val destDirStr = destDir.absolutePath + File.separatorChar
			for (entry in zipFile.entries()) {
				val fileName = destDirStr + entry.name
				if (HOLogger.instance().logLevel == HOLogger.DEBUG) {
					HOLogger.instance().debug(ZipHelper.javaClass,
						"${zipFile.name}: extracting ${entry.name} to $fileName"
					)
				}
				extractEntry(zipFile, entry, fileName)
			}
		} finally {
			close(zipFile)
		}
	}

	@Throws(FileNotFoundException::class, IOException::class)
	private fun extractEntry(zipFile: ZipFile, entry: ZipEntry, fileName: String) {
		val targetLocation = File(getSystemIndependentPath(fileName))

		if (!targetLocation.getParentFile().exists()) {
			targetLocation.getParentFile().mkdirs()
		}

		if (entry.isDirectory) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir()
			}
			return
		}

		val inputStream = zipFile.getInputStream(entry)
		inputStream.use {
			targetLocation.writeBytes(it.readAllBytes())
		}

		HOLogger.instance().debug(ZipHelper.javaClass, "Entry $fileName successfully extracted")
	}

	private fun getSystemIndependentPath(str: String): String {
		return str.replace('\\', '/')
	}
}
