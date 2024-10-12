package tool.dbencrypter.encrypt

interface SymFileEncryptor {

	/**
	 * Encrypts a file at path <code>path</code> symmetrically, using <code>secret</code>
	 * as secret key.
	 *
	 * Returns the path to the encrypted file.
	 */
	fun encrypt(path:String, secret: String):String
}
