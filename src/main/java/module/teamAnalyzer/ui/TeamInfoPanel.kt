package module.teamAnalyzer.ui

import core.model.HOVerwaltung
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
 */
class TeamInfoPanel : JPanel() {

    private fun isBot(details: Map<String?, String?>):Boolean {
        val isBot = details.getOrDefault("IsBot", "False")
        return isBot.equals("True", ignoreCase = true)
    }

    fun setTeam(details: Map<String?, String?>) {
        removeAll()
        val hoVerwaltung = HOVerwaltung.instance()
        val isBot = isBot(details)

        // Column 1
//        val infoLabel = JLabel()
//        infoLabel.icon = ImageIcon(details["LogoURL"])
//        infoLabel.text = hoVerwaltung.getLanguageString("ls.teamanalyzer.info")
        border = BorderFactory.createTitledBorder(hoVerwaltung.getLanguageString("ls.teamanalyzer.info"))
        val gbc = GridBagConstraints()
        layout = GridBagLayout()

        gbc.fill = GridBagConstraints.NONE
        gbc.weightx = 1.0
        gbc.anchor = GridBagConstraints.WEST
        gbc.gridx = 0
        gbc.gridy = 0
        val managerLabel = JLabel(hoVerwaltung.getLanguageString("ls.teamanalyzer.manager"))
        val boldFont = managerLabel.font.deriveFont(Font.BOLD)
        managerLabel.font = boldFont
        add(managerLabel, gbc)
        gbc.gridy++
        val lastLoginLabel = JLabel(hoVerwaltung.getLanguageString("ls.teamanalyzer.last_login"))
        lastLoginLabel.font = boldFont
        add(lastLoginLabel, gbc)

        if (isBot) {
            gbc.gridy++
            val botStatusLabel = JLabel(hoVerwaltung.getLanguageString("ls.teamanalyzer.bot"))
            botStatusLabel.font = boldFont
            add(botStatusLabel, gbc)
        }

        if (details.containsKey("LeaguePosition")) {
            gbc.gridy++
            val leaguePositionLabel = JLabel(hoVerwaltung.getLanguageString("ls.teamanalyzer.league_position"))
            leaguePositionLabel.font = boldFont
            add(leaguePositionLabel, gbc)
        }

        // Column 2
        gbc.gridx = 1
        gbc.gridy = 0
        gbc.anchor = GridBagConstraints.EAST
        val loginValueLabel = JLabel()
        loginValueLabel.text = if (details["Loginname"].isNullOrBlank()) hoVerwaltung.getLanguageString("ls.teamanalyzer.na") else details["Loginname"]
        add(loginValueLabel, gbc)
        gbc.gridy++
        val lastLoginDateLabel = JLabel()
        val lastLoginDate = HODateTime.fromHT(details["LastLoginDate"])
        lastLoginDateLabel.text = if (details["Loginname"].isNullOrBlank()) hoVerwaltung.getLanguageString("ls.teamanalyzer.na") else lastLoginDate.toLocaleDateTime()
        add(lastLoginDateLabel, gbc)

        if (isBot) {
            gbc.gridy++
            val botStatusValueLabel = JLabel()
            val boStatusDate = details["BotSince"]
            botStatusValueLabel.text = hoVerwaltung.getLanguageString("ls.teamanalyzer.bot_since", HODateTime.fromHT(boStatusDate).toLocaleDate())
            add(botStatusValueLabel, gbc)
        }

        if (details.containsKey("LeaguePosition")) {
            gbc.gridy++
            val leaguePosText = hoVerwaltung.getLanguageString("ls.teamanalyzer.league_position_val",
                details["LeaguePosition"], details["LeagueLevelUnitName"], details["CountryName"])
            val leaguePositionValue = JLabel(leaguePosText)
            add(leaguePositionValue, gbc)
        }

    }
}
