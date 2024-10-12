package tool.dbencrypter.encrypt

import core.db.backup.HOZip
import core.db.user.UserManager
import core.model.HOVerwaltung
import tool.dbencrypter.FilePublicKeyRetriever
import tool.dbencrypter.PublicKeyRetriever
import java.io.File
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*


private val s = listOf("script", "data", "backup", "log", "properties")

class DbEncrypterManager(private val userManager: UserManager) {
	private val asymEncryptor: AsymEncryptor = RSAAsymEncryptor()
	private val publicKeyRetriever: PublicKeyRetriever = FilePublicKeyRetriever("export")
	private val fileSymEncryptor: SymFileEncryptor = AESSymFileEncryptor()

	fun createZipName():String {
		val sdf = SimpleDateFormat("yyyy-MM-dd")
		return sdf.format(Date())
	}

	fun encrypt() {
		val hoZip = zipDatabase()
		encryptFile(hoZip.path)
	}

	private fun zipDatabase():HOZip {
		val dbFolder = File(userManager.currentUser.dbFolder)
		val filesToZip = dbFolder.listFiles { file: File ->
			file.isFile && s.any { suffix -> file.extension == suffix }
		}

		val tempFile = File.createTempFile(createZipName(), ".zip")
		val hoZip = HOZip(tempFile.path)
		filesToZip?.map { file -> hoZip.addFile(file) }
		hoZip.closeArchive()

		return hoZip
	}

	private fun encryptFile(path:String):String {
		val randomSecret = encryptFileSymmetrically(path)
		encryptKeyAsymmetrically(randomSecret)
		return Base64.getEncoder().encodeToString(randomSecret.toByteArray())
	}

	private fun encryptKeyAsymmetrically(randomSecret: String) {
		val publicKey = publicKeyRetriever.retrievePublicKey("publickey")
		asymEncryptor.encryptSecret(randomSecret, publicKey)
	}

	private fun encryptFileSymmetrically(path: String): String {
		val key = ByteArray(16)
		SecureRandom.getInstanceStrong().nextBytes(key)
		val randomSecret = key.toString()
		fileSymEncryptor.encrypt(path, randomSecret)

		// Return random secret we just generated to encrypt
		return randomSecret
	}
}

fun main() {
	HOVerwaltung.instance().loadLatestHoModel()
	val encrypter = DbEncrypterManager(UserManager.instance())
	encrypter.encrypt()
}
