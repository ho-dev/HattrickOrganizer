package module.teamAnalyzer.ui.component

import core.util.HODateTime
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Displays information about the team currently selected in the dropdown in
 * [module.teamAnalyzer.ui.FilterPanel].
 *
 * TODO i18n
 */
class TeamInfoPanel : JPanel() {

    private fun isBot(details: Map<String?, String?>):Boolean {
        val isBot = details.getOrDefault("IsBot", "False")
        return isBot.equals("True", ignoreCase = true)
    }

    fun setTeam(details: Map<String?, String?>) {
        removeAll()
        val isBot = isBot(details)

        // Column 1
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

        if (isBot) {
            gbc.gridy++
            val botStatusLabel = JLabel("Bot: ")
            botStatusLabel.font = boldFont
            add(botStatusLabel, gbc)
        }

        gbc.gridy++
        val leaguePositionLabel = JLabel("League Position: ")
        leaguePositionLabel.font = boldFont
        add(leaguePositionLabel, gbc)
        gbc.gridy++
        val currentStreakLabel = JLabel("Current League Streak: ")
        currentStreakLabel.font = boldFont
        add(currentStreakLabel, gbc)

        // Column 2
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

        if (isBot) {
            gbc.gridy++
            val botStatusValueLabel = JLabel()
            val boStatusDate = details["BotSince"]
            botStatusValueLabel.text = "since ${HODateTime.fromHT(boStatusDate).toLocaleDate()})"
            add(botStatusValueLabel, gbc)
        }

        gbc.gridy++
        val leaguePosText = "${details["LeagueRanking"]} in ${details["LeagueLevelUnitName"]} (${details["CountryName"]})"
        val leaguePositionValue = JLabel(leaguePosText)
        add(leaguePositionValue, gbc)
        gbc.gridy++
        val currentStreakValue = JLabel("Current League Streak: ")
        add(currentStreakValue, gbc)
    }
}
