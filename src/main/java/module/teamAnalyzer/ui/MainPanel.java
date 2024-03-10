package module.teamAnalyzer.ui;

import module.teamAnalyzer.vo.TeamLineup;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * Main panel displays the lineup for both sides for a selected game
 * in the {@link RecapPanel}.
 */
public class MainPanel extends JPanel {
	private final RosterPanel rosterPanel = new RosterPanel();
    private final TeamPanel teamPanel = new TeamPanel();

    /**
     * Creates a new TeamPanel object.
     */
    public MainPanel() {
        jbInit();
    }

    public TeamLineupData getMyTeamLineupPanel() {
        return teamPanel.getMyTeamLineupPanel();
    }

    public TeamLineupData getOpponentTeamLineupPanel() {
        return teamPanel.getOpponentTeamLineupPanel();
    }

    public RosterPanel getRosterPanel() {
        return rosterPanel;
    }

    public void jbInit() {
        setOpaque(false);
        setLayout(new BorderLayout());
        add(teamPanel, BorderLayout.CENTER);
    }

    public void reload(TeamLineup lineup, int week, int season) {
        teamPanel.reload(lineup, week, season);
    }
}
