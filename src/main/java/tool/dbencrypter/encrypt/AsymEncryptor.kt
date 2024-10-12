package tool.dbencrypter.encrypt

interface AsymEncryptor {

    /**
	 * Encrypts a secret using an asymmetric public key.
	 *
	 * @param randomSecret Arbitrary string containing a secret to be encrypted.
	 * @param publicKey Asymmetric key in PEM format.
	 * @return String – encrypted secret, base64-encoded
	 */
	fun encryptSecret(randomSecret:String, publicKey: String)
}
