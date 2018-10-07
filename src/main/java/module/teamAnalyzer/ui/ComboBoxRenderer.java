package module.teamAnalyzer.ui;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.vo.Team;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ComboBoxRenderer extends DefaultListCellRenderer {
    //~ Constructors -------------------------------------------------------------------------------
	private static final long serialVersionUID = -3551665867069804794L;

	/**
     * Creates a new ComboBoxRenderer object.
     */
    public ComboBoxRenderer() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /*
     * This method finds the image and text corresponding
     * to the selected value and returns the label, set up
     * to display the text and image.
     */
    @Override
	public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Team team = (Team) value;

        if (team == null) {
            setForeground(list.getForeground());

            return this;
        }

        if (team.getTeamId() == SystemManager.getLeagueOpponentId()) {
            setForeground(ThemeManager.getColor(HOColorName.TA_TEAM_LEAGUE_NEXT));
        } else if (team.getTeamId() == SystemManager.getCupOpponentId()) {
            setForeground(ThemeManager.getColor(HOColorName.TA_TEAM_CUP_NEXT));
        } else if (team.getTeamId() == SystemManager.getTournamentOpponentId()) {
        	setForeground(ThemeManager.getColor(HOColorName.TA_TEAM_TOURNAMENT_NEXT));
        } else if (team.isTournament()) {
        	setForeground(ThemeManager.getColor(HOColorName.TA_TEAM_TOURNAMENT));
        } else {
        	setForeground(list.getForeground());
        }
        return this;
         
        }
}
