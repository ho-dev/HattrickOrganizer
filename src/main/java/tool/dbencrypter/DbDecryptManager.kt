package tool.dbencrypter

import java.io.File
import java.security.KeyFactory
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class DbDecryptManager {

	// TODO Extract private key for tests.
	// (Don't worry, this key below is just for testing, the real key won't be published!)
	private val privateKey = "-----BEGIN PRIVATE KEY-----\n" +
		"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCJbftX0ovvInT1\n" +
		"Tk3iLjoBvtJl9RkishekbAbfeBU9HJOT2ycozFWFJ0zW6voPkBaBMdnCgcCOQMWZ\n" +
		"wxUB3uPxKUj/uDlOeH8HbOloR0XVKT9loIPuNfoEfl/6CtobNHvcDG7XMN1fJ/wZ\n" +
		"a6wbFSe8DKallR4qTm/N4AmQ0hOjj4L+1P7ugyMC/7Iyp6IzExRINU/FlTFVYxh4\n" +
		"XpGcaEQKVoEeNs2HsDK8YXn8Z4+0bnDrMpgHnCw7TRaZa0MJ4sYI9tlA4R1IYTYD\n" +
		"0iG/4cRIXJts2Cl/xh7Q7r4aq3JKBMqDa18dABd0n1AfEwS1Ef1mQ43iEWH6dG17\n" +
		"AatCYozNAgMBAAECggEAAY+qycoeBdbt3jWIA/hDd26cBEV/Bhpg02DYySQblaij\n" +
		"8ouMS9XaaNtT76MWuSOJZelTkHaZ4ePRetgw/mbyTvSJnEITAJW017hAhIs+u1ev\n" +
		"HS/wH74kO3XWEEicRq/Xcl3/ylkHQsP5BK7o8xvP6j54jCv//joK4htX8Hm6eGKu\n" +
		"NjImrcaE63QBmaaZcK3gC9d/6+D/gkxOa4mTdtYZVbXBMQzjUc4aHkK4UzyMybRm\n" +
		"EqpN35W7kBC9EvnungqC2FesnEuqPR7S4opaGqBW4lKr4hYeYnOz7HnMPYEIIJI7\n" +
		"wctxP146UwI/rzK8O10btnQH6DdVu0O682fh73+vsQKBgQDB8fn0MmHolyr1+Q7N\n" +
		"bImDjnWi5dbfDqFmqrqm6cjTbODmSSYQ8gB56W0gWs6mFErEfsES5Mr+yO+ROfy0\n" +
		"Bi+85uRdAm0JsvBKm88UzwHbuR+wX2pMqO3Eh8jeEJPB2aV+pHvW3Uu0ph7QPMlL\n" +
		"zQ8s/8JdrPr56THg0BmYW4jUuQKBgQC1ZtIlEKZnpcvG1Jtg+PnBpnogR3GCJG9E\n" +
		"KtOKRs11JqBy1xnkv9Xqju1Fh9nkjVXH6PYQF270x7Wr+4o07wYdpI76GvEOU+pM\n" +
		"a4p6z15FhDcsd9oD16y50OT79bYpRMAmjYugukwhvJisxnaekT4aTt3nva/IqA2x\n" +
		"ldEXEZ5WtQKBgCXGmPXsfk+MaizA1xZCBsLeE6Gn/OMzeVKC/JoYPSqZMXEJXGW+\n" +
		"jUMxqTS9GzUUDMAbJEYm1DcuMiNqVQNHlTLJEj/fghd6h/0wPfSCoY0HkNmMrCrE\n" +
		"WJOIEyLVvcHrP6XcKdbfAajtFmFfBaJDDaqpIlWWpMfamQF95w5a/drZAoGAUEUX\n" +
		"aF9dwy+SZIFf4CFMI2zPTclPaQ9GTRvGT8HU2KonBao5QwfAWPK1+7aJrKD9/GAR\n" +
		"wj0cVCSrN1cvaQz739IZkoKpeHWZkAdmV8G7LXe8EmlWh6zAOdk9+mBYIxrRDD5G\n" +
		"MvE9DxCtXobTpocOvAV6HDnLtzbmGFb7FwIs1qECgYBgFopNnHj0KMJ2wLH0gAOT\n" +
		"Hh6zViQVm5/ZjWKGqxn4gfgiPwzMs91xG9b4s+2mzSZ64A2LxHi8Re39uh4m0q86\n" +
		"BNOjBxSYk/WUopdO8/U0X+BZ/mXMSnTCkz+9Yme1HB6gh5LHtjy4B//rAMJrv4aq\n" +
		"NbOxpIrXWydLAFVqvGuO4w==\n" +
		"-----END PRIVATE KEY-----\n"

	fun decrypt(cipheredZip:String, keyPath:String) {

		val normalized = privateKey
			.replace("-----BEGIN PRIVATE KEY-----", "")
			.replace(System.lineSeparator(), "")
			.replace("-----END PRIVATE KEY-----", "")

		val decodedKey = Base64.getDecoder().decode(normalized)
		val privateKeySpec = PKCS8EncodedKeySpec(decodedKey)
		val keyFactory = KeyFactory.getInstance("RSA")
		val privateKey = keyFactory.generatePrivate(privateKeySpec)

		val cipher = Cipher.getInstance("RSA")
		cipher.init(Cipher.DECRYPT_MODE, privateKey)

		val contentKey = File(keyPath).readText()
		val secret = cipher.doFinal(Base64.getDecoder().decode(contentKey))

		val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
		val spec: KeySpec = PBEKeySpec(String(secret).toCharArray(), "randomSalt".toByteArray(), 65536, 256)
		val tmp = factory.generateSecret(spec)
		val secretKeySpec = SecretKeySpec(tmp.encoded, "AES")

		val encryptedData: ByteArray = File(cipheredZip).readBytes()
		val iv = ByteArray(16)
		System.arraycopy(encryptedData, 0, iv, 0, iv.size)
		val ivspec = IvParameterSpec(iv)

		val symCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
		symCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec)
		val cipherText = ByteArray(encryptedData.size - 16)
		System.arraycopy(encryptedData, 16, cipherText, 0, cipherText.size)

		val decryptedText = symCipher.doFinal(cipherText)

		File("/tmp/output.zip").writeBytes(decryptedText)
		println(String(Base64.getEncoder().encode(secret)))
	}
}



fun main(args:Array<String>) {

	val decryptManager = DbDecryptManager()
	decryptManager.decrypt(args[0], args[1])

}
