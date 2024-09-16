package tool.dbencrypter

import core.db.user.UserManager
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JPanel

class DbEncrypterPanel: JPanel() {

	init {
		layout = BorderLayout()
		val encryptButton = JButton("Encrypt")
		encryptButton.addActionListener { _ ->
			val encrypter = DbEncrypterManager(UserManager.instance())
			encrypter.encrypt()
		}

		add(encryptButton, BorderLayout.CENTER)
	}
}
