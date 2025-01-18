package tool.dbencrypter

import tool.dbencrypter.encrypt.DbEncrypterManager
import tool.dbencrypter.github.GithubApp
import javax.swing.JFrame

class IssueReporterManager(private val githubApp: GithubApp, private val dbEncrypterManager: DbEncrypterManager) {

	fun launchDialog(owner: JFrame) {
		val reporterDialog = DbEncrypterDialog(owner, this)
		reporterDialog.start()
	}

	fun reportIssue(description: String, summary: String, attachDb: Boolean, progressManager: () -> Unit) {
		if (attachDb) {
			dbEncrypterManager.encrypt()
			// TODO Upload Encrypted database, and get link
		}

		progressManager()
		githubApp.requestDeviceCode(summary, description)
		progressManager()
	}
}
