package module.lineup.lineup;

import core.db.DBManager;
import core.gui.Refreshable;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.util.Helper;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.manager.TeamManager;
import module.teamAnalyzer.ui.MatchComboBoxRenderer;
import module.teamAnalyzer.vo.Team;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static module.lineup.LineupPanel.TITLE_FG;

public class MatchAndLineupSelectionPanel extends JPanel implements Refreshable {

    private final int OWN_TEAM_ID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
    private final int MAX_PREVIOUS_LINEUP = 10;
    private JButton m_jbDownloadLineup;
    private JLabel m_jlLineupStatus;
    private JComboBox<Team> m_jcbLoadLineup;
    private JComboBox<Team> m_jcbUpcomingGames;
    private JCheckBox m_jcbxLineupSimulation;

    public MatchAndLineupSelectionPanel() {
        initComponents();
        core.gui.RefreshManager.instance().registerRefreshable(this);
    }

    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints gbc = new GridBagConstraints();

        setLayout(layout);


        gbc.gridx = 0;
        gbc.gridy = 0;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.select_match"));

        gbc.gridx = 1;
        m_jcbUpcomingGames = new JComboBox<>();
        update_jcbUpcomingGames();
        m_jcbUpcomingGames.setRenderer(new MatchComboBoxRenderer());
        layout.setConstraints(m_jcbUpcomingGames, gbc);
        add(m_jcbUpcomingGames);


        gbc.gridx = 0;
        gbc.gridy = 1;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.lineup_simulator"));

        gbc.gridx = 1;
        m_jcbxLineupSimulation = new JCheckBox();
        m_jcbxLineupSimulation.setSelected(false);
        layout.setConstraints(m_jcbxLineupSimulation, gbc);
        add(m_jcbxLineupSimulation);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.load_lineup"));

        gbc.gridx = 1;
        m_jcbLoadLineup = new JComboBox<>();
        update_jcbLoadLineup();
        m_jcbLoadLineup.setRenderer(new MatchComboBoxRenderer());
        layout.setConstraints(m_jcbLoadLineup, gbc);
        add(m_jcbLoadLineup);




        addListeners();
    }

    private void setComponents() {}

    private void addListeners() {
//        ActionListener checkBoxActionListener = e -> {
//            if (e.getSource() == jcbHelpLines) {
//                oChartPanel.setHelpLines(jcbHelpLines.isSelected());
//                gup.statisticsClubHelpLines = jcbHelpLines.isSelected();
//            }
//        }
//
//        jcbHelpLines.addActionListener(checkBoxActionListener);

//        m_jbSelectStartingLineup.addActionListener(e -> popup.setVisible(true));

    }

    private void removeItemListeners() {}


    private void addLabel(GridBagConstraints constraints, GridBagLayout layout, String sLabel) {
        JLabel label = new JLabel(sLabel);
        label.setForeground(TITLE_FG);
        label.setFont(getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        layout.setConstraints(label, constraints);
        add(label);
    }


    @Override
    public void reInit() {
        refresh();
    }

    @Override
    public void refresh() {
        removeItemListeners();
        setComponents();
        addListeners();
    }



    private void update_jcbLoadLineup() {
        m_jcbLoadLineup.removeAllItems();
        int i = 0;

        Team oTeam;

        ArrayList<MatchKurzInfo> previousPlayedMatches = DBManager.instance().getPlayedMatchInfo(MAX_PREVIOUS_LINEUP);

        for (MatchKurzInfo match : previousPlayedMatches) {

            oTeam = new Team();

            if (match.getHeimID() == OWN_TEAM_ID) {
                oTeam.setName(match.getGastName());
                oTeam.setTeamId(match.getGastID());
            }
            else
                {
                oTeam.setName(match.getHeimName());
                oTeam.setTeamId(match.getHeimID());
            }
            oTeam.setTime(match.getMatchDateAsTimestamp());
            oTeam.setMatchType(match.getMatchTyp().getIconArrayIndex());

            m_jcbLoadLineup.addItem(oTeam);
            i++;

        }

        m_jcbLoadLineup.setMaximumRowCount(i);

    }



    private void update_jcbUpcomingGames() {
        m_jcbUpcomingGames.removeAllItems();
        int i = 0;
        for (Team element : TeamManager.getTeams(false)) {
            m_jcbUpcomingGames.addItem(element);
            if (SystemManager.getActiveTeamId() == element.getTeamId()) {
                m_jcbUpcomingGames.setSelectedItem(element);
            }
            i++;
        }

        m_jcbUpcomingGames.setMaximumRowCount(i);

    }


}
