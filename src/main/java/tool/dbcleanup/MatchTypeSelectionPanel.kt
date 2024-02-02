package tool.dbcleanup

import core.model.enums.MatchType
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JCheckBox
import javax.swing.JPanel

class MatchTypeSelectionPanel: JPanel() {
    val leagueMatches = JCheckBox("League")
    val friendlyMatches = JCheckBox("Friendly")
    val cupMatches = JCheckBox("Cup")
    val qualificationMatches = JCheckBox("Qualification")
    val tournamentMatches = JCheckBox("Tournament")

    init {
        layout = GridBagLayout()
        val gbc = GridBagConstraints()

        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridx = 0
        gbc.gridy = 0
        add(leagueMatches, gbc)

        gbc.gridx++
        add(friendlyMatches, gbc)

        gbc.gridx++
        add(cupMatches, gbc)

        gbc.gridx = 0
        gbc.gridy = 1
        add(qualificationMatches, gbc)

        gbc.gridx++
        add(tournamentMatches, gbc)

    }

    fun getSelectedMatchTypes(): List<MatchType> {
        val selectedMatchTypes = mutableListOf<MatchType>()

        if (leagueMatches.isSelected) {
            selectedMatchTypes.add(MatchType.LEAGUE)
        }

        if (friendlyMatches.isSelected) {
            selectedMatchTypes.addAll(MatchType.getFriendlyMatchTypes())
        }

        if (cupMatches.isSelected) {
            selectedMatchTypes.addAll(MatchType.getCupMatchTypes())
        }

        if (qualificationMatches.isSelected) {
            selectedMatchTypes.add(MatchType.QUALIFICATION)
        }

        if (tournamentMatches.isSelected) {
            selectedMatchTypes.addAll(MatchType.getTournamentMatchTypes())
        }

        return selectedMatchTypes
    }
}