package tool.dbencrypter.encrypt

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
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes

private const val AES_ALGORITHM_NAME = "AES"
private const val AES_ENCRYPTION_ALGORITHM_NAME = "AES/CBC/PKCS5Padding"
private const val KEY_DERIVATION_ALGORITHM_NAME = "PBKDF2WithHmacSHA256"
private const val SALT_VALUE = "randomSalt"

class AESSymFileEncryptor: SymFileEncryptor {
	fun createZipName():String {
		val sdf = SimpleDateFormat("yyyy-MM-dd")
		return sdf.format(Date())
	}

	override fun encrypt(path: String, secret: String): String {
		val secureRandom = SecureRandom()
		val iv = ByteArray(16)
		secureRandom.nextBytes(iv)
		val ivspec = IvParameterSpec(iv)

		val factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM_NAME)
		// Derive 256-bit secret key from password.
		val spec: KeySpec = PBEKeySpec(secret.toCharArray(), SALT_VALUE.toByteArray(), 65536, 256)
		val tmp = factory.generateSecret(spec)
		val secretKeySpec = SecretKeySpec(tmp.encoded, AES_ALGORITHM_NAME)

		val cipher = Cipher.getInstance(AES_ENCRYPTION_ALGORITHM_NAME)
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec)

		val cipherText = cipher.doFinal(File(path).readBytes())
		val encryptedData = ByteArray(iv.size + cipherText.size)
		System.arraycopy(iv, 0, encryptedData, 0, iv.size)
		System.arraycopy(cipherText, 0, encryptedData, iv.size, cipherText.size)

		val encryptedFile = kotlin.io.path.createTempFile(createZipName() + "-enc", ".zip")
		println("Encrypted DB: ${encryptedFile.pathString}")
		encryptedFile.writeBytes(encryptedData)

		return encryptedFile.toFile().path
	}
}
