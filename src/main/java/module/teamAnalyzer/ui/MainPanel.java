package module.teamAnalyzer.ui;

import module.teamAnalyzer.vo.TeamLineup;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class MainPanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------
	private static final long serialVersionUID = -6374854816698657464L;
	private RosterPanel rosterPanel = new RosterPanel();
    private TeamPanel teamPanel = new TeamPanel();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TeamPanel object.
     */
    public MainPanel() {
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------
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
