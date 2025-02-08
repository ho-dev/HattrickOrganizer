package tool.dbencrypter

import core.model.TranslationFacility
import java.awt.*
import javax.swing.*

class DbEncrypterPanel(issueReporterManager: IssueReporterManager) : JPanel() {

	init {
		layout = GridBagLayout()
		val gbc = GridBagConstraints()

		gbc.insets = Insets(5, 5, 5, 5)

		// Row 0
		gbc.gridy = 0
		gbc.fill = GridBagConstraints.HORIZONTAL
		gbc.weightx = 1.0
		gbc.weighty = 1.0

		gbc.gridx = 0
		add(Box.createHorizontalStrut(42), gbc)
		gbc.gridx = 1
		add(Box.createHorizontalStrut(42), gbc)
		gbc.gridx = 2
		add(Box.createHorizontalStrut(42), gbc)

		// Row 1
		gbc.gridy++
		gbc.gridx = 0
		gbc.gridwidth = 3
		gbc.anchor = GridBagConstraints.NORTH

		add(
			JLabel(
				"<html>Report an issue to Github.  " +
					"You must first create an account if you don't already have one.</html>"
			),
			gbc
		)

		// Row 2
		gbc.gridy++
		gbc.gridx = 0
		gbc.gridwidth = 1

		val summaryLabel = JLabel(TranslationFacility.tr("reporter.summary"))
		add(summaryLabel, gbc)

		gbc.gridx = 1
		gbc.gridwidth = 2

		val summaryInput = JTextField()
		add(summaryInput, gbc)

		// Row 3
		gbc.gridy++
		gbc.gridx = 0
		gbc.gridwidth = 1

		val descriptionLabel = JLabel(TranslationFacility.tr("reporter.description"))
		add(descriptionLabel, gbc)

		gbc.gridx = 1
		gbc.gridwidth = GridBagConstraints.REMAINDER
		val currentFill = gbc.fill
		gbc.fill = GridBagConstraints.BOTH

		val descriptionInput = JTextArea()
		descriptionInput.rows = 10
		add(descriptionInput, gbc)

		// Row 4
		gbc.fill = currentFill
		gbc.gridy++
		gbc.gridx = 0
		gbc.gridwidth = 1

		add(JLabel(TranslationFacility.tr("Attach encrypted database?")), gbc)

		gbc.anchor = GridBagConstraints.WEST
		gbc.fill = GridBagConstraints.NONE
		gbc.gridx = 1
		gbc.gridwidth = 2

		val attachCheckbox = JCheckBox("")
		add(attachCheckbox, gbc)

		// Row 5
		gbc.anchor = GridBagConstraints.NORTH
		gbc.fill = currentFill
		gbc.gridy++
		gbc.gridx = 0
		gbc.gridwidth = 2

		add(JLabel(TranslationFacility.tr("reporter.encrypt")), gbc)

		gbc.gridx = 2
		gbc.gridwidth = GridBagConstraints.REMAINDER

		val encryptButton = JButton(TranslationFacility.tr("reporter.encrypt"))
		encryptButton.addActionListener { _ ->
			issueReporterManager.reportIssue(
				descriptionInput.text,
				summaryInput.text,
				attachCheckbox.isSelected,
				DbEncrypterPanel::doSomething,
				DbEncrypterPanel::promptDeviceCode
			)
		}

		add(encryptButton, gbc)
	}

	companion object {
		private fun doSomething() {
			println("Update")
		}

		private fun promptDeviceCode(code: String) {
			val largeFont = Font("Ubuntu Sans", Font.BOLD, 90)

			val mainPanel = JPanel()
			mainPanel.layout = BorderLayout()
			mainPanel.add(
				JLabel("Copy and paste the following code in the GitHub Activation page, and click OK once done:"),
				BorderLayout.NORTH
			)
			val labelCode = JTextField(code)
			labelCode.font = largeFont
			labelCode.isEditable = false
			labelCode.border = BorderFactory.createEmptyBorder()
			mainPanel.add(labelCode, BorderLayout.CENTER)

			JOptionPane.showMessageDialog(null, mainPanel)
		}
	}
}
