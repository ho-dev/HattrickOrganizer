package tool.dbencrypter

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

class FilePublicKeyRetrieverTest {

	private val key = "-----BEGIN PUBLIC KEY-----\n" +
		"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzv9MzlUZt8JtypjDX9MTHX6iA\n" +
		"5j96Dv9l0v+cdenspYqk2oBHHEw5FGbPaugsO77QeZGtjf1wODUtvy1YynK/VH9i\n" +
		"AaDbvz+oLAnVE1+YzZTtY5VQ+cVwqE0dVzHjT3p+Bo1iILNhjmDc86ugmsahvyjr\n" +
		"yjZZYR+pHdf08c/1KwIDAQAB\n" +
		"-----END PUBLIC KEY-----\n"

	@Test
	fun testRetrieverLoadsFile() {
		val retriever = FilePublicKeyRetriever("export")
		assertEquals(key, retriever.retrievePublicKey("test"))
	}

	@Test
	fun testRetrieverThrowsExceptionIfKeyNotFound() {
		val retriever = FilePublicKeyRetriever("export")
		org.junit.jupiter.api.assertThrows<FileNotFoundException> { retriever.retrievePublicKey("nonexistent") }
	}
}
