package tool.dbencrypter

import core.db.backup.HOZip
import core.db.user.UserManager
import core.model.HOVerwaltung
import java.io.File
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.path.writeBytes
import kotlin.io.path.writeText


private val s = listOf("script", "data", "backup", "log", "properties")

class DbEncrypterManager(private val userManager: UserManager) {

	fun createZipName():String {
		val sdf = SimpleDateFormat("yyyy-MM-dd")
		return sdf.format(Date())
	}

	fun encrypt() {
		val hoZip = zipDatabase()
		createSecretKey(hoZip.path)
	}

	private fun zipDatabase():HOZip {
		val dbFolder = File("/home/sebastien/dev/HO-work/db")
		val filesToZip = dbFolder.listFiles { file: File ->
			file.isFile && s.any { suffix -> file.extension == suffix }
		}

		val tempFile = File.createTempFile(createZipName(), ".zip")
		val hoZip = HOZip(tempFile.path)
		filesToZip?.map { file -> hoZip.addFile(file) }
		hoZip.closeArchive()

		return hoZip
	}

	private fun createSecretKey(path:String):String {

		val secureRandom = SecureRandom()
		val iv = ByteArray(16)
		secureRandom.nextBytes(iv)
		val ivspec = IvParameterSpec(iv)

		val key = ByteArray(16)
		SecureRandom.getInstanceStrong().nextBytes(key)
		val randomSecret = key.toString()
		val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
		val spec: KeySpec = PBEKeySpec(randomSecret.toCharArray(), "randomSalt".toByteArray(), 65536, 256)
		val tmp = factory.generateSecret(spec)
		val secretKeySpec = SecretKeySpec(tmp.encoded, "AES")

		val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec)

		val cipherText = cipher.doFinal(File(path).readBytes())
		val encryptedData = ByteArray(iv.size + cipherText.size)
		System.arraycopy(iv, 0, encryptedData, 0, iv.size)
		System.arraycopy(cipherText, 0, encryptedData, iv.size, cipherText.size)

		val encryptedFile = kotlin.io.path.createTempFile(createZipName() + "-enc", ".zip")
		encryptedFile.writeBytes(cipherText)

		val keyFile = kotlin.io.path.createTempFile(createZipName() + "-key", ".txt")
		keyFile.writeText(Base64.getEncoder().encodeToString(randomSecret.toByteArray()))

		return Base64.getEncoder().encodeToString(randomSecret.toByteArray())
	}
}

fun main() {
	HOVerwaltung.instance().loadLatestHoModel()
//	System.setProperty("AppData", "/home/sebastien/dev/HO-work/db")
	val encrypter = DbEncrypterManager(UserManager.instance())
	encrypter.encrypt()
}
