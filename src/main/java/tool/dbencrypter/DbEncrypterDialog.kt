package tool.dbencrypter

import java.awt.BorderLayout
import javax.swing.JDialog
import javax.swing.JFrame

internal class DbEncrypterDialog(private var owner:JFrame): JDialog(owner, true) {

	init {
		defaultCloseOperation = DISPOSE_ON_CLOSE
		initComponents()
	}

	private fun initComponents() {
		val mainPanel = DbEncrypterPanel()
		contentPane.layout = BorderLayout()
		contentPane.add(mainPanel, BorderLayout.CENTER)

		pack()
		isVisible = true
	}
}
