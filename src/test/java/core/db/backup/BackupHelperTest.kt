package core.db.backup

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.FileTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.ZipFile


internal class BackupHelperTest {

	private val testResourcesDir = File("./src/test/resources")
	private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

	private fun listZipInDir(path: String): Array<out File> {
		val dir = File(path)
		val output = dir.listFiles { _, fileName ->
			fileName.endsWith(".zip")
		}
		return output ?: arrayOf<File>()
	}

	private fun zipFileName() = "db_user-${formatter.format(LocalDate.now())}.zip"

	private fun listFilesInZip(zipPath: String): List<String> {
		return ZipFile(zipPath)
			.entries()
			.toList().map { e -> e.name }
	}

	@Test
	fun testBackupDbDoesNothingIfDirDoesntExist() {
		val noDir = File(testResourcesDir, "none")
		BackupHelper.backup(noDir)

		val zips = listZipInDir(noDir.absolutePath)
		Assertions.assertNotNull(zips)
		listZipInDir(noDir.absolutePath).let { Assertions.assertTrue(it.isEmpty()) }
	}

	@Test
	fun testBackupDbDoesNothingIfNoMatchingFiles() {
		val exportDir = File(testResourcesDir, "export")
		BackupHelper.backup(exportDir)

		val zips = listZipInDir(exportDir.absolutePath)
		Assertions.assertNotNull(zips)
		listZipInDir(exportDir.absolutePath).let { Assertions.assertTrue(it.isEmpty()) }
	}

	@Test
	fun testBackupIncludesRelevantFiles() {
		val dbDir = File(testResourcesDir, "db")
		BackupHelper.backup(dbDir)

		val zips = listZipInDir(dbDir.absolutePath)
		Assertions.assertNotNull(zips)
		Assertions.assertEquals(1, zips.size)

		val entries = listFilesInZip(zips.first().absolutePath)
		Assertions.assertEquals(3, entries.size)
		Assertions.assertEquals(zipFileName(), zips.first().name)
	}

	@Test
	fun testBackupOnlyKeepsMaxNumber() {
		val dbDir = File(testResourcesDir, "db")

		val currentDate = LocalDateTime.now()
		(1..5).forEach { i ->
			val date = currentDate.minusDays(i.toLong())
			val f = File(testResourcesDir, "db/db_user-${formatter.format(date)}.zip")
			Assertions.assertDoesNotThrow { f.createNewFile() }
			Files.setLastModifiedTime(f.toPath(), FileTime.from(date.toInstant(ZoneOffset.UTC)))
		}

		BackupHelper.backup(dbDir)

		val zips = listZipInDir(dbDir.absolutePath)
		Assertions.assertNotNull(zips)
		Assertions.assertEquals(3, zips.size)
	}

	@AfterEach
	fun cleanup() {
		File(testResourcesDir, "db").listFiles()
			?.forEach { f ->
				if (f.extension == "zip") {
					f.delete()
				}
			}
	}
}
