package tool.dbencrypter.encrypt

import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import kotlin.io.path.pathString
import kotlin.io.path.writeText

/**
 * Encrypts a String using RSA.
 */
class RSAAsymEncryptor: AsymEncryptor {
	private fun createZipName():String {
		val sdf = SimpleDateFormat("yyyy-MM-dd")
		return sdf.format(Date())
	}

	override fun encryptSecret(randomSecret: String, publicKey:String) {

		// TODO Move to a util class
		val normalized = publicKey
			.replace("-----BEGIN PUBLIC KEY-----", "")
			.replace(System.lineSeparator(), "")
			.replace("-----END PUBLIC KEY-----", "")
		val decodedKey = Base64.getDecoder().decode(normalized)
		val encodedKeySpec = X509EncodedKeySpec(decodedKey)
		val keyFactory = KeyFactory.getInstance("RSA")
		val encodedKey = keyFactory.generatePublic(encodedKeySpec)

		// Vanilla JDK does not support ECC.
		val asymCipher = Cipher.getInstance("RSA")
		asymCipher.init(Cipher.ENCRYPT_MODE, encodedKey)
		val encryptedKey = asymCipher.doFinal(randomSecret.toByteArray())

		val keyFile = kotlin.io.path.createTempFile(createZipName() + "-key", ".txt")
		println("Key File: ${keyFile.pathString}")
		keyFile.writeText(Base64.getEncoder().encodeToString(encryptedKey))
	}
}
