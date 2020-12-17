package module.lineup.lineup;

import core.db.DBManager;
import core.gui.Refreshable;
import core.gui.model.MatchOrdersCBItems;
import core.gui.model.MatchOrdersRenderer;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineupPlayer;
import core.model.player.IMatchRoleID;
import core.util.GUIUtils;
import core.util.Helper;
import module.lineup.Lineup;
import module.teamAnalyzer.ui.MatchComboBoxRenderer;
import module.teamAnalyzer.vo.Team;
import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

import static module.lineup.LineupPanel.TITLE_FG;




public class MatchAndLineupSelectionPanel extends JPanel implements Refreshable {

    private final int OWN_TEAM_ID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
    private final int MAX_PREVIOUS_LINEUP = 10;
    LineupPositionsPanel jpParent;
    private JComboBox<Team> m_jcbLoadLineup;
    private JComboBox<MatchOrdersCBItems> m_jcbUpcomingGames;
    private JCheckBox m_jcbxLineupSimulation;
    private JCheckBox m_jcbxOfficialOnly;
    private JButton m_jbUploadLineup;
    private JButton m_jbDownloadLineup;
    private JButton m_jbRefreshMatches;
    private JButton m_jbGetRatingsPrediction;

    public MatchAndLineupSelectionPanel(LineupPositionsPanel parent) {
        jpParent = parent;
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
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.select_match"));

        gbc.gridx = 1;
        m_jcbUpcomingGames = new JComboBox<>();
        update_jcbUpcomingGames();
        m_jcbUpcomingGames.setRenderer(new MatchOrdersRenderer());
        layout.setConstraints(m_jcbUpcomingGames, gbc);
        add(m_jcbUpcomingGames);


        gbc.gridx = 0;
        gbc.gridy = 1;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.lineup_simulator"));

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        m_jcbxLineupSimulation = new JCheckBox();
        m_jcbxLineupSimulation.setSelected(false);
        layout.setConstraints(m_jcbxLineupSimulation, gbc);
        add(m_jcbxLineupSimulation);


        // Panel with the 4 buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel jpButtons = new JPanel(new FlowLayout());

        m_jbUploadLineup = new JButton(Helper.getTranslation("lineup.upload.btn.upload"));
        m_jbUploadLineup.setToolTipText(Helper.getTranslation("lineup.upload.btn.upload.tooltip"));
        m_jbUploadLineup.setEnabled(false);

        m_jbDownloadLineup = new JButton(Helper.getTranslation("lineup.upload.btn.download"));
        m_jbDownloadLineup.setToolTipText(Helper.getTranslation("lineup.upload.btn.download.tooltip"));
        m_jbDownloadLineup.setEnabled(false);

        m_jbRefreshMatches = new JButton(Helper.getTranslation("lineup.upload.btn.refresh"));
        m_jbRefreshMatches.setToolTipText(Helper.getTranslation("lineup.upload.btn.refresh.tooltip"));

        m_jbGetRatingsPrediction = new JButton(Helper.getTranslation("lineup.getRatingsPrediction.btn.label"));
        m_jbGetRatingsPrediction.setToolTipText(Helper.getTranslation("lineup.getRatingsPrediction.btn.tooltip"));
        m_jbGetRatingsPrediction.setEnabled(false);

        GUIUtils.equalizeComponentSizes(m_jbUploadLineup, m_jbDownloadLineup, m_jbRefreshMatches, m_jbGetRatingsPrediction);

        jpButtons.add(m_jbUploadLineup);
        jpButtons.add(m_jbDownloadLineup);
        jpButtons.add(m_jbRefreshMatches);
        jpButtons.add(m_jbGetRatingsPrediction);

        layout.setConstraints(jpButtons, gbc);
        add(jpButtons);

        // ===============================================

        gbc.gridx = 0;
        gbc.gridy = 3;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.load_lineup"));

        gbc.gridx = 1;
        m_jcbLoadLineup = new JComboBox<>();
        update_jcbLoadLineup();
        m_jcbLoadLineup.setRenderer(new MatchComboBoxRenderer(MatchComboBoxRenderer.RenderType.TYPE_2));
        layout.setConstraints(m_jcbLoadLineup, gbc);
        add(m_jcbLoadLineup);


        gbc.gridx = 0;
        gbc.gridy = 4;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.official_game_only"));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        m_jcbxOfficialOnly = new JCheckBox();
        m_jcbxOfficialOnly.setSelected(false);
        layout.setConstraints(m_jcbxOfficialOnly, gbc);
        add(m_jcbxOfficialOnly);



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

        m_jcbLoadLineup.addActionListener(e -> adoptLineup());

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


        Team oTeam;

        ArrayList<MatchKurzInfo> previousPlayedMatches = DBManager.instance().getPlayedMatchInfo(MAX_PREVIOUS_LINEUP);

        m_jcbLoadLineup.addItem(null);
        int i = 1;
        for (MatchKurzInfo match : previousPlayedMatches) {

            oTeam = new Team();

            if (match.getHeimID() == OWN_TEAM_ID) {
                oTeam.setName(match.getGastName());
                oTeam.setTeamId(match.getGastID());
                oTeam.setHomeMatch(true);
            }
            else
                {
                oTeam.setName(match.getHeimName());
                oTeam.setTeamId(match.getHeimID());
                oTeam.setHomeMatch(false);
            }
            oTeam.setTime(match.getMatchDateAsTimestamp());
            oTeam.setMatchType(match.getMatchTyp().getIconArrayIndex());
            oTeam.setMatchID(match.getMatchID());

            m_jcbLoadLineup.addItem(oTeam);
            i++;

        }

        m_jcbLoadLineup.setMaximumRowCount(i);

    }



    private void update_jcbUpcomingGames() {
        m_jcbUpcomingGames.removeAllItems();

        // use if orce refresh
        //List<MatchKurzInfo> aa =  OnlineWorker.getMatches(OWN_TEAM_ID, true,false, true);

        MatchKurzInfo[] matches = DBManager.instance().getMatchesKurzInfo(OWN_TEAM_ID);
        Arrays.sort(matches, Collections.reverseOrder());
//        matches.sort();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        List<MatchOrdersCBItems> upcomingMatches = new ArrayList<>();

        MatchOrdersCBItems clMatchOrdersTemp;

        for (MatchKurzInfo match : matches) {
            if (match.getMatchDateAsTimestamp().after(now)) {
                clMatchOrdersTemp = new MatchOrdersCBItems();
                clMatchOrdersTemp.setMatchID(match.getMatchID());
                clMatchOrdersTemp.setMatchType(match.getMatchTyp());
                clMatchOrdersTemp.setMatchTime(match.getMatchDateAsTimestamp());
                clMatchOrdersTemp.setOpponentName(match.getOpponentName());
                clMatchOrdersTemp.setOrdersSetInHT(match.isOrdersGiven());
                upcomingMatches.add(clMatchOrdersTemp);
            }
        }

        int i = 0;
        for (MatchOrdersCBItems element : upcomingMatches) {
            m_jcbUpcomingGames.addItem(element);
            i++;
        }
        m_jcbUpcomingGames.setMaximumRowCount(i);
    }

    private void adoptLineup() {

        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        lineup.clearLineup();

        int iMatchID = ((Team)(Objects.requireNonNull(m_jcbLoadLineup.getSelectedItem()))).getMatchID();


        Vector<MatchLineupPlayer> lineupPlayers = DBManager.instance().getMatchLineupPlayers(iMatchID, OWN_TEAM_ID);



        if (lineupPlayers != null) {
            for (MatchLineupPlayer lineupPlayer : lineupPlayers) {
                if (lineupPlayer.getId() == IMatchRoleID.setPieces) {
                    lineup.setKicker(lineupPlayer.getSpielerId());
                }
                else if (lineupPlayer.getId() == IMatchRoleID.captain) {
                    lineup.setCaptain(lineupPlayer.getSpielerId());
                }
                else {
                    lineup.setSpielerAtPosition(lineupPlayer.getId(), lineupPlayer.getSpielerId(), lineupPlayer.getTactic());
                }
            }
        }

        jpParent.update();

        }

}
