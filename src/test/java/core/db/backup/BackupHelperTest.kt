package core.db.backup

import core.db.user.UserManager
import core.util.HODateTime
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
import java.time.temporal.ChronoUnit
import java.util.zip.ZipFile
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


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

	private fun zipFileName() = "db_${UserManager.instance().currentUser.teamName}-${formatter.format(LocalDate.now())}.zip"

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

        var fileFromPreviousWeek = 0
        val currentDate = HODateTime.now()
        val currentWeek = currentDate.toHTWeek()
        (1..5).forEach { i ->
            val date = currentDate.minus(i, ChronoUnit.DAYS)
            val f = File(testResourcesDir, "db/db_user-${formatter.format(date.localDateTime)}.zip")
            Assertions.assertDoesNotThrow { f.createNewFile() }
            Files.setLastModifiedTime(f.toPath(), FileTime.from(date.instant))
            if (!date.toHTWeek().equals(currentWeek)) {fileFromPreviousWeek = 1}
        }

        BackupHelper.backup(dbDir)

        val zips = listZipInDir(dbDir.absolutePath)
        Assertions.assertNotNull(zips)
        Assertions.assertEquals(3 + fileFromPreviousWeek, zips.size)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testBackupStrategy() {
        val dbDir = File(testResourcesDir, "db")

        var expectedLastModifiedDates = mutableListOf<HODateTime>()
        val currentDate = HODateTime.now()
        val currentWeek = currentDate.toHTWeek()
        (1..3).forEach { i ->
            val date = currentDate.minus(i, ChronoUnit.DAYS)
            createBackupFile(date)
            expectedLastModifiedDates.add(date)
            if (!date.toHTWeek().equals(currentWeek)) {
                expectedLastModifiedDates.add(date)
            }
        }

        val oldFileDate = HODateTime.fromHT("2026-09-01 11:00:00")
        expectedLastModifiedDates.add(oldFileDate)
        createBackupFile(oldFileDate)
        createBackupFile(oldFileDate.minus(1, ChronoUnit.DAYS))

        repeat(16) { weeks -> createBackupFile(oldFileDate.minus(7 * weeks + 8, ChronoUnit.DAYS)) }
        repeat(2) { seasons -> createBackupFile(oldFileDate.minus(112 * seasons + 113, ChronoUnit.DAYS)) }

        BackupHelper.backup(dbDir)

        val zips = listZipInDir(dbDir.absolutePath)
        Assertions.assertNotNull(zips)
        Assertions.assertEquals(expectedLastModifiedDates.size, zips.size)

        repeat(expectedLastModifiedDates.size) { fileNumber -> Assertions.assertEquals(expectedLastModifiedDates[fileNumber],
            HODateTime.fromEpochSecond(zips[fileNumber].lastModified()/1000)) }
    }

    private fun createBackupFile(date: HODateTime) {
        val f = File(testResourcesDir, "db/db_user-${formatter.format(date.localDateTime)}.zip")
        Assertions.assertDoesNotThrow { f.createNewFile() }
        Files.setLastModifiedTime(f.toPath(), FileTime.from(date.instant))
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
