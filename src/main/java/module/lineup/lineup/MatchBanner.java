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

    public MatchBanner(LineupPositionsPanel parent) {
        initComponents();
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
        JLabel jlHomeTeam = new JLabel();
        Icon homeTeamIcon = ThemeManager.instance().getClubLogo(1754082);
        jlHomeTeam.setIcon(homeTeamIcon);
        layout.setConstraints(jlHomeTeam, gbc);
        add(jlHomeTeam);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void reInit() {

    }
}
