package core.db.backup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class HOZipTest {
	private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

	private fun generateTempZipFileName(): String {
		val customDir = System.getProperty("java.io.tmpdir")
		val tempFileName = System.currentTimeMillis()
		return "${customDir}${File.separator}${tempFileName}.zip"
	}

	@Test
	fun testAddFile() {
		val fileName = generateTempZipFileName()
		val hoZip = HOZip(fileName)

		val testFile = this.javaClass.classLoader.getResource("tools/sample.txt")
		assertTrue(testFile != null)
		hoZip.addFile(File(testFile!!.path))
		hoZip.closeArchive()
		assertTrue(File(fileName).exists())
		assertEquals(1, hoZip.fileCount)
	}

	@Test
	fun testCreateZipFileName() {
		assertEquals("test-" + formatter.format(LocalDateTime.now()) + ".zip", HOZip.createZipName("test-"))
	}
}
