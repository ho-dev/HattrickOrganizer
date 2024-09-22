package tool.dbencrypter

import core.db.backup.HOZip
import core.db.user.UserManager
import core.model.HOVerwaltung
import java.io.File
import java.security.KeyFactory
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.security.spec.X509EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes
import kotlin.io.path.writeText


private val s = listOf("script", "data", "backup", "log", "properties")

class DbEncrypterManager(private val userManager: UserManager) {

	private val publicKey = "-----BEGIN PUBLIC KEY-----\n" +
		"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiW37V9KL7yJ09U5N4i46\n" +
		"Ab7SZfUZIrIXpGwG33gVPRyTk9snKMxVhSdM1ur6D5AWgTHZwoHAjkDFmcMVAd7j\n" +
		"8SlI/7g5Tnh/B2zpaEdF1Sk/ZaCD7jX6BH5f+graGzR73Axu1zDdXyf8GWusGxUn\n" +
		"vAympZUeKk5vzeAJkNITo4+C/tT+7oMjAv+yMqeiMxMUSDVPxZUxVWMYeF6RnGhE\n" +
		"ClaBHjbNh7AyvGF5/GePtG5w6zKYB5wsO00WmWtDCeLGCPbZQOEdSGE2A9Ihv+HE\n" +
		"SFybbNgpf8Ye0O6+GqtySgTKg2tfHQAXdJ9QHxMEtRH9ZkON4hFh+nRtewGrQmKM\n" +
		"zQIDAQAB\n" +
		"-----END PUBLIC KEY-----\n"

	fun createZipName():String {
		val sdf = SimpleDateFormat("yyyy-MM-dd")
		return sdf.format(Date())
	}

	fun encrypt() {
		val hoZip = zipDatabase()
		encryptFile(hoZip.path)
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

	private fun encryptFile(path:String):String {
		val randomSecret = encryptFileSymmetrically(path)
		encryptKeyAsymmetrically(randomSecret)
		return Base64.getEncoder().encodeToString(randomSecret.toByteArray())
	}

	private fun encryptKeyAsymmetrically(randomSecret: String) {
		val normalized = publicKey
			.replace("-----BEGIN PUBLIC KEY-----", "")
			.replace(System.lineSeparator(), "")
			.replace("-----END PUBLIC KEY-----", "")
		val decodedKey = Base64.getDecoder().decode(normalized)
		val encodedKeySpec = X509EncodedKeySpec(decodedKey)
		val keyFactory = KeyFactory.getInstance("RSA")
		val encodedKey = keyFactory.generatePublic(encodedKeySpec)

		// Vanilla JDK does not support ECC, it seems.
		val asymCipher = Cipher.getInstance("RSA")
		asymCipher.init(Cipher.ENCRYPT_MODE, encodedKey)
		val encryptedKey = asymCipher.doFinal(randomSecret.toByteArray())

		val keyFile = kotlin.io.path.createTempFile(createZipName() + "-key", ".txt")
		println("Key File: ${keyFile.pathString}")
		keyFile.writeText(Base64.getEncoder().encodeToString(encryptedKey))
	}

	private fun encryptFileSymmetrically(path: String): String {
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
		println("Encrypted DB: ${encryptedFile.pathString}")
		encryptedFile.writeBytes(encryptedData)

		// Return random secret we just generated to encrypt
		return randomSecret
	}
}

fun main() {
	HOVerwaltung.instance().loadLatestHoModel()
	val encrypter = DbEncrypterManager(UserManager.instance())
	encrypter.encrypt()
}
