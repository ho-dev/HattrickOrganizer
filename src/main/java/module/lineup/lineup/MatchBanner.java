package module.lineup.lineup;

import core.gui.Refreshable;
import core.gui.model.MatchOrdersRenderer;
import core.gui.theme.ThemeManager;
import core.util.GUIUtils;
import core.util.Helper;
import module.teamAnalyzer.ui.MatchComboBoxRenderer;

import javax.swing.*;
import java.awt.*;

public class MatchBanner extends JPanel implements Refreshable {

    MatchAndLineupSelectionPanel matchSelectionPanel;
    JLabel jlHomeTeam, jlAwayTeam;

    public MatchBanner(MatchAndLineupSelectionPanel _matchSelectionPanel) {
        matchSelectionPanel = _matchSelectionPanel;
        initComponents();
        refresh();
        core.gui.RefreshManager.instance().registerRefreshable(this);
    }

    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints gbc = new GridBagConstraints();

        setLayout(layout);

        gbc.insets = new Insets(3,3 ,3 ,3 );
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        jlHomeTeam = new JLabel();
        layout.setConstraints(jlHomeTeam, gbc);
        add(jlHomeTeam);

        gbc.gridx = 1;
        gbc.gridy = 0;
        jlAwayTeam = new JLabel();
        layout.setConstraints(jlAwayTeam, gbc);
        add(jlAwayTeam);

    }

    @Override
    public void refresh() {
        var selectedMatch = matchSelectionPanel.getSelectedMatch();

        if (selectedMatch != null) {
            int iHomeTeamID = selectedMatch.getHeimID();
            int iAwayTeamID = selectedMatch.getGastID();

            Icon homeTeamIcon = ThemeManager.instance().getClubLogo(iHomeTeamID);
            jlHomeTeam.setIcon(homeTeamIcon);

            Icon AwayTeamIcon = ThemeManager.instance().getClubLogo(iAwayTeamID);
            jlAwayTeam.setIcon(AwayTeamIcon);
        }
    }

    @Override
    public void reInit() {

    }
}
