package tool.dbencrypter

import core.util.HOLogger
import java.io.File
import java.io.FileNotFoundException

class FilePublicKeyRetriever(private val location:String) : PublicKeyRetriever {
	override fun retrievePublicKey(keyName: String): String {
		val fileName = "${location}/${keyName}.pem"
		val resource = javaClass.classLoader.getResource(fileName)
		if (resource != null) {
			val file = File(resource.file)
			return file.readText()
		} else {
			HOLogger.instance().error(javaClass,"Cannot find key file: $fileName")
			throw FileNotFoundException(fileName)
		}
	}
}
