package tool.dbencrypter

import tool.dbencrypter.encrypt.DbEncrypterManager
import tool.dbencrypter.github.GithubApp

class IssueReporterManager(private val githubApp: GithubApp, private val dbEncrypterManager: DbEncrypterManager) {


	fun reportIssue(description: String, summary: String, attachDb: Boolean, progressManager: () -> Unit) {
		if (attachDb) {
			dbEncrypterManager.encrypt()
			// TODO Upload Encrypted database, and get link
		}

		progressManager()
		githubApp.requestDeviceCode()
		progressManager()
	}
}
