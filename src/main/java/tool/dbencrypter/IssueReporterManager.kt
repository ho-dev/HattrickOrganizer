package tool.dbencrypter

import core.model.UserParameter
import tool.dbencrypter.encrypt.DbEncrypterManager
import tool.dbencrypter.github.GithubApp
import java.io.File
import javax.swing.JFrame

/**
 * Main class of the issue reporter tool.
 */
class IssueReporterManager(private val githubApp: GithubApp, private val dbEncrypterManager: DbEncrypterManager) {

	fun launchDialog(owner: JFrame) {
		val reporterDialog = DbEncrypterDialog(owner, this)
		reporterDialog.start()
	}

	fun reportIssue(
		description: String,
		summary: String,
		attachDb: Boolean,
		progressManager: () -> Unit,
		codePrompt: (String) -> Unit
	) {
		var path: String? = null
		if (attachDb) {
			path = dbEncrypterManager.encrypt()
			println("Path: $path")
			// TODO Upload Encrypted database, and get link
		}

		progressManager()
		githubApp.requestDeviceCode(summary, description, codePrompt)
		progressManager()

		if (attachDb) {
			githubApp.uploadFile(File(path), "8", "ho-dev", "test-ghapp", UserParameter.instance().githubAccessToken)
		}
	}
}
