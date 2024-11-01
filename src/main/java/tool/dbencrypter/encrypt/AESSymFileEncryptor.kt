package tool.dbencrypter.encrypt

import core.db.backup.HOZip
import core.util.HOLogger
import java.io.File
import java.nio.file.Path
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes

private const val AES_ALGORITHM_NAME = "AES"
private const val AES_ENCRYPTION_ALGORITHM_NAME = "AES/CBC/PKCS5Padding"
private const val KEY_DERIVATION_ALGORITHM_NAME = "PBKDF2WithHmacSHA256"
private const val SALT_VALUE = "randomSalt"

/**
 * Symmetric w
 */
class AESSymFileEncryptor : SymFileEncryptor {
	override fun encrypt(path: String, secret: String): String {
		val iv = generateRandomIv()
		val ivspec = IvParameterSpec(iv)

		val secretKeySpec = createSecretKeySpec(secret)
		val cipherText = doEncrypt(secretKeySpec, ivspec, path)
		val encryptedData = prependIv(iv, cipherText)

		// TODO move out code to write to file, this should be called by DbEncrypterManager
		val encryptedFile = writeToFile(encryptedData)

		return encryptedFile.toFile().path
	}

	private fun generateRandomIv(): ByteArray {
		val secureRandom = SecureRandom()
		val iv = ByteArray(16)
		secureRandom.nextBytes(iv)
		return iv
	}

	private fun writeToFile(encryptedData: ByteArray): Path {
		val encryptedFile = kotlin.io.path.createTempFile(HOZip.createZipName("enc-db-"))
		HOLogger.instance().debug(javaClass, "Encrypted DB: ${encryptedFile.pathString}")
		encryptedFile.writeBytes(encryptedData)
		return encryptedFile
	}

	private fun prependIv(iv: ByteArray, cipherText: ByteArray): ByteArray {
		val encryptedData = ByteArray(iv.size + cipherText.size)
		System.arraycopy(iv, 0, encryptedData, 0, iv.size)
		System.arraycopy(cipherText, 0, encryptedData, iv.size, cipherText.size)
		return encryptedData
	}

	private fun doEncrypt(
		secretKeySpec: SecretKeySpec,
		ivspec: IvParameterSpec,
		path: String
	): ByteArray {
		val cipher = Cipher.getInstance(AES_ENCRYPTION_ALGORITHM_NAME)
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec)
		val cipherText = cipher.doFinal(File(path).readBytes())
		return cipherText
	}

	private fun createSecretKeySpec(secret: String): SecretKeySpec {
		val factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM_NAME)
		// Derive 256-bit secret key from password.
		val spec: KeySpec = PBEKeySpec(secret.toCharArray(), SALT_VALUE.toByteArray(), 65536, 256)
		val tmp = factory.generateSecret(spec)
		val secretKeySpec = SecretKeySpec(tmp.encoded, AES_ALGORITHM_NAME)
		return secretKeySpec
	}
}
