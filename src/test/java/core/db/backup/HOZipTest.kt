package core.db.backup

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File

internal class HOZipTest {
	private fun generateTempZipFileName():String {
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
}
