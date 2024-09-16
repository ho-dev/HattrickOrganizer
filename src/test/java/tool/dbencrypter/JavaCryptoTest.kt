package tool.dbencrypter

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.security.Security
import javax.crypto.Cipher


internal class JavaCryptoTest {

	@Test
	fun testAlgorithmsPresence() {
		for (provider in Security.getProviders()) {
			System.out.println(provider.name)
			for (key in provider.stringPropertyNames()) println("\t" + key + "\t" + provider.getProperty(key))
		}
	}

	@Test
	fun checkAlgorithmPresent() {
		assertDoesNotThrow {
			Cipher.getInstance("RSA")
		}
	}
}
