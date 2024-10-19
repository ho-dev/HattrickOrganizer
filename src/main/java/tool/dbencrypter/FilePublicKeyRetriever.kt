package tool.dbencrypter

import core.util.HOLogger
import java.io.File
import java.io.FileNotFoundException

private const val PEM_EXTENSION = ".pem"

/**
 * Retrieves a public key from a PEM file
 */
class FilePublicKeyRetriever(private val location: String) : PublicKeyRetriever {

	/**
	 * Returns the public key with name <code>keyName</code>.
	 *
	 * @param keyName Name of the key.
	 * @return String – Content of the key file in PEM format.
	 */
	override fun retrievePublicKey(keyName: String): String {
		val fileName = "${location}/${keyName}$PEM_EXTENSION"
		val resource = javaClass.classLoader.getResource(fileName)
		if (resource != null) {
			val file = File(resource.file)
			return file.readText()
		} else {
			HOLogger.instance().error(javaClass, "Cannot find key file: $fileName")
			throw FileNotFoundException(fileName)
		}
	}
}
