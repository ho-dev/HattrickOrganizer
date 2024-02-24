package module.teamAnalyzer.ui.component

import core.util.HODateTime
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

class TeamInfoPanel : JPanel() {

    fun setTeam(details: Map<String?, String?>) {
        removeAll()
        border = BorderFactory.createTitledBorder("Info")
        val gbc = GridBagConstraints()
        layout = GridBagLayout()

        gbc.fill = GridBagConstraints.NONE
        gbc.weightx = 1.0
        gbc.anchor = GridBagConstraints.WEST
        gbc.gridx = 0
        gbc.gridy = 0
        val managerLabel = JLabel("Manager: ")
        val boldFont = managerLabel.font.deriveFont(Font.BOLD)
        managerLabel.font = boldFont
        add(managerLabel, gbc)
        gbc.gridy++
        val lastLoginLabel = JLabel("Last Login: ")
        lastLoginLabel.font = boldFont
        add(lastLoginLabel, gbc)
        gbc.gridy++
        val botStatusLabel = JLabel("Bot?: ")
        botStatusLabel.font = boldFont
        add(botStatusLabel, gbc)

        gbc.gridx = 1
        gbc.gridy = 0
        gbc.anchor = GridBagConstraints.EAST
        val loginValueLabel = JLabel()
        loginValueLabel.text = if (details["Loginname"].isNullOrBlank()) "–" else details["Loginname"]
        add(loginValueLabel, gbc)
        gbc.gridy++
        val lastLoginDateLabel = JLabel()
        val lastLoginDate = HODateTime.fromHT(details["LastLoginDate"])
        lastLoginDateLabel.text = if (details["Loginname"].isNullOrBlank()) "–" else lastLoginDate.toLocaleDateTime()
        add(lastLoginDateLabel, gbc)
        gbc.gridy++
        val botStatusValueLabel = JLabel()
        val botStatusValue = details["IsBot"]
        val boStatusDate = details["BotSince"]
        botStatusValueLabel.text = if (!botStatusValue.isNullOrBlank() && botStatusValue.toString().equals("True", ignoreCase = true)) {
            "Y (since ${HODateTime.fromHT(boStatusDate).toLocaleDate()})"
        } else {
                "N"
        }
        add(botStatusValueLabel, gbc)
    }
}
