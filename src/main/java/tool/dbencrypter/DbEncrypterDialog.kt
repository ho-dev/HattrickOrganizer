package tool.dbencrypter

import core.model.TranslationFacility
import java.awt.BorderLayout
import javax.swing.JDialog
import javax.swing.JFrame

class DbEncrypterDialog(val owner: JFrame, private val reporterManager: IssueReporterManager) : JDialog(owner, true) {

	init {
		setSize(600, 400)
		title = TranslationFacility.tr("reporter.dialog.title")
		defaultCloseOperation = DISPOSE_ON_CLOSE
		isLocationByPlatform = true
		initComponents()
	}

	private fun initComponents() {
		val mainPanel = DbEncrypterPanel(reporterManager)
		contentPane.layout = BorderLayout()
		contentPane.add(mainPanel, BorderLayout.CENTER)
	}

	fun start() {
		isVisible = true
	}

	fun close() {
		isVisible = false
		dispose()
	}
}
