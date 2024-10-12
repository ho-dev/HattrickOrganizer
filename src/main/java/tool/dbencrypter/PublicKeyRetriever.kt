package tool.dbencrypter

interface PublicKeyRetriever {

	/**
	 * Retrieves a public key for asymmetric crypto as a String.
	 * The key returned as String is in PEM format.
	 */
	fun retrievePublicKey(keyName:String):String
}
