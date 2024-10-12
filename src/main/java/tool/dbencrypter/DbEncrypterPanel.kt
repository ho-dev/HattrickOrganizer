package tool.dbencrypter

import core.db.user.UserManager
import tool.dbencrypter.encrypt.DbEncrypterManager
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class DbEncrypterPanel: JPanel() {

	init {
		layout = BorderLayout()
		val encryptButton = JButton("Encrypt")
		encryptButton.addActionListener { _ ->
			val encrypter = DbEncrypterManager(UserManager.instance())
			encrypter.encrypt()
		}

		add(JLabel("Encrypt"), BorderLayout.NORTH)
		add(encryptButton, BorderLayout.SOUTH)
	}
}
