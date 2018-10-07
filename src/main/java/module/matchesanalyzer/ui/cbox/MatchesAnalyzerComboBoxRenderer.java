package module.matchesanalyzer.ui.cbox;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import module.matchesanalyzer.data.MatchesAnalyzerTeam;
import module.teamAnalyzer.manager.TeamManager;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


public class MatchesAnalyzerComboBoxRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		MatchesAnalyzerTeam team = (MatchesAnalyzerTeam)value;
		if(team == null) return component;

		int id = team.getId();
		if(id == TeamManager.getNextLeagueOpponent().getTeamId()) {
			component.setForeground(ThemeManager.getColor(HOColorName.MATCHESANALYZER_TEAM_LEAGUE_NEXT));
		} else if(id == TeamManager.getNextCupOpponent().getTeamId()) {
			component.setForeground(ThemeManager.getColor(HOColorName.MATCHESANALYZER_TEAM_CUP_NEXT));
		} else if(id == TeamManager.getNextTournamentOpponent().getTeamId()) {
			component.setForeground(ThemeManager.getColor(HOColorName.MATCHESANALYZER_TEAM_TOURNAMENT_NEXT));
		} else if(team.isTournament()) {
			component.setForeground(ThemeManager.getColor(HOColorName.MATCHESANALYZER_TEAM_TOURNAMENT));
		} else if(team.isMine()) {
			component.setForeground(ThemeManager.getColor(HOColorName.MATCHESANALYZER_TEAM_MYTEAM));
			component.setFont(component.getFont().deriveFont(Font.BOLD));
		}

		return component;
	}
}
