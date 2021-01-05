package module.lineup.lineup;

import core.gui.Refreshable;
import core.gui.model.MatchOrdersCBItem;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class MatchBanner extends JPanel implements Refreshable {

    MatchAndLineupSelectionPanel matchSelectionPanel;
    JLabel jlHomeTeam, jlAwayTeam, jlMatchTypeIcon, jlWeather;

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
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;


        // 1st row ================================================
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        jlMatchTypeIcon = new JLabel();
        layout.setConstraints(jlMatchTypeIcon, gbc);
        add(jlMatchTypeIcon);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel jlPH = new JLabel("          ");
        layout.setConstraints(jlPH, gbc);
        add(jlPH);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        jlWeather = new JLabel("");
        jlWeather.setHorizontalTextPosition(JLabel.LEFT);
        layout.setConstraints(jlWeather, gbc);
        add(jlWeather);

        // 2nd row ================================================
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        jlHomeTeam = new JLabel();
        jlHomeTeam.setHorizontalTextPosition(JLabel.CENTER);
        jlHomeTeam.setVerticalTextPosition(JLabel.BOTTOM);
        layout.setConstraints(jlHomeTeam, gbc);
        add(jlHomeTeam);

        gbc.gridx = 2;
        jlAwayTeam = new JLabel();
        jlAwayTeam.setHorizontalTextPosition(JLabel.CENTER);
        jlAwayTeam.setVerticalTextPosition(JLabel.BOTTOM);
        layout.setConstraints(jlAwayTeam, gbc);
        add(jlAwayTeam);

    }

    @Override
    public void refresh() {
        MatchOrdersCBItem selectedMatch = matchSelectionPanel.getSelectedMatch();

        if (selectedMatch != null) {

            ThemeManager tm = ThemeManager.instance();

            Icon weatherIcon = tm.getIcon(HOIconName.WEATHER[selectedMatch.getWeather().getId()]);
            jlWeather.setIcon(weatherIcon);

            Icon MatchTypeIcon = tm.getScaledIcon(HOIconName.MATCHICONS[selectedMatch.getMatchType().getIconArrayIndex()], 22, 22);
            jlMatchTypeIcon.setIcon(MatchTypeIcon);

            Icon homeTeamIcon = tm.getClubLogo(selectedMatch.getHomeTeamID());
            jlHomeTeam.setIcon(homeTeamIcon);
            jlHomeTeam.setText(selectedMatch.getHomeTeamName());


            Icon AwayTeamIcon = tm.getClubLogo(selectedMatch.getGuestTeamID());
            jlAwayTeam.setIcon(AwayTeamIcon);
            jlAwayTeam.setText(selectedMatch.getGuestTeamName());
        }
    }

    @Override
    public void reInit() {

    }
}
