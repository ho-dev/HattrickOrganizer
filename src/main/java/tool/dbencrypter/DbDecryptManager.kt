package tool.dbencrypter

import java.io.File
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.path.writeBytes

class DbDecryptManager {

	fun decrypt(cipheredZip:String, key:String) {
		val secret = String(Base64.getDecoder().decode(key))

		val secureRandom = SecureRandom()
		val iv = ByteArray(16)
		secureRandom.nextBytes(iv)
		val ivspec = IvParameterSpec(iv)

		val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
		val spec: KeySpec = PBEKeySpec(secret.toCharArray(), "randomSalt".toByteArray(), 65536, 256)
		val tmp = factory.generateSecret(spec)
		val secretKeySpec = SecretKeySpec(tmp.encoded, "AES")

		val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec)

		val cipherText = cipher.doFinal(File(cipheredZip).readBytes())
		val decryptedData = ByteArray(iv.size + cipherText.size)
		System.arraycopy(iv, 0, decryptedData, 0, iv.size)
		System.arraycopy(cipherText, 0, decryptedData, iv.size, cipherText.size)

		val decryptedFile = kotlin.io.path.createTempFile("dec", ".zip")
		decryptedFile.writeBytes(cipherText)
	}
}



fun main(args:Array<String>) {

	val decryptManager = DbDecryptManager()
	decryptManager.decrypt(args[0], args[1])

}
