package module.lineup.lineup;

import core.db.DBManager;
import core.gui.CursorToolkit;
import core.gui.Refreshable;
import core.gui.model.MatchOrdersCBItem;
import core.gui.model.MatchOrdersRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.*;
import core.model.player.IMatchRoleID;
import core.net.OnlineWorker;
import core.util.GUIUtils;
import core.util.HOLogger;
import core.util.Helper;
import module.lineup.Lineup;
import module.teamAnalyzer.ui.MatchComboBoxRenderer;
import module.teamAnalyzer.vo.Team;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import static module.lineup.LineupPanel.TITLE_FG;


public class MatchAndLineupSelectionPanel extends JPanel implements Refreshable {

    private final int OWN_TEAM_ID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
    private final int MAX_PREVIOUS_LINEUP = 10;
    LineupPositionsPanel jpParent;
    private JComboBox<Team> m_jcbLoadLineup;
    private JComboBox<MatchOrdersCBItem> m_jcbUpcomingGames;
    private List<MatchOrdersCBItem> upcomingMatchesInDB;
    private JCheckBox m_jcbxLineupSimulation;
    private JCheckBox m_jcbxOfficialOnly;
    private JButton m_jbUploadLineup;
    private JButton m_jbDownloadLineup;
    private JButton m_jbGetRatingsPrediction;
    private @Nullable MatchOrdersCBItem m_clSelectedMatch;
    private Boolean bDataTooOld;
    private String sWarningDataTooOld;
    private Long lLastUpdateTime;


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
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.select_match"));

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        m_jcbUpcomingGames = new JComboBox<>();
        m_jcbUpcomingGames.setRenderer(new MatchOrdersRenderer());
        m_jcbUpcomingGames.setPreferredSize(new Dimension(325, 25));
//        m_jcbUpcomingGames.setMinimumSize(new Dimension(325, 25));
//        m_jcbUpcomingGames.setMaximumSize(new Dimension(325, 25));
        layout.setConstraints(m_jcbUpcomingGames, gbc);

        add(m_jcbUpcomingGames);

        gbc.gridx = 0;
        gbc.gridy = 1;
//        gbc.fill = GridBagConstraints.NONE;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.lineup_simulator"));

        gbc.gridx = 1;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
        m_jcbxLineupSimulation = new JCheckBox();
        m_jcbxLineupSimulation.setSelected(false);
        layout.setConstraints(m_jcbxLineupSimulation, gbc);
        add(m_jcbxLineupSimulation);


        // Panel with the 3 buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel jpButtons = new JPanel(new FlowLayout());

        m_jbUploadLineup = new JButton(Helper.getTranslation("lineup.upload.btn.upload"));
        m_jbUploadLineup.setToolTipText(Helper.getTranslation("lineup.upload.btn.upload.tooltip"));
        m_jbUploadLineup.setEnabled(false);

        m_jbDownloadLineup = new JButton(Helper.getTranslation("lineup.upload.btn.download"));
        m_jbDownloadLineup.setToolTipText(Helper.getTranslation("lineup.upload.btn.download.tooltip"));
        m_jbDownloadLineup.setEnabled((m_clSelectedMatch != null) && (m_clSelectedMatch.areOrdersSetInHT()));

        m_jbGetRatingsPrediction = new JButton(Helper.getTranslation("lineup.getRatingsPrediction.btn.label"));
        m_jbGetRatingsPrediction.setToolTipText(Helper.getTranslation("lineup.getRatingsPrediction.btn.tooltip"));
        m_jbGetRatingsPrediction.setEnabled(false);

        GUIUtils.equalizeComponentSizes(m_jbUploadLineup, m_jbDownloadLineup, m_jbGetRatingsPrediction);

        jpButtons.add(m_jbUploadLineup);
        jpButtons.add(m_jbDownloadLineup);
        jpButtons.add(m_jbGetRatingsPrediction);

        layout.setConstraints(jpButtons, gbc);
        add(jpButtons);

        // ===============================================

        gbc.gridx = 0;
        gbc.gridy = 3;
//        gbc.fill = GridBagConstraints.NONE;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.load_lineup"));

        gbc.gridx = 1;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
        m_jcbLoadLineup = new JComboBox<>();
        m_jcbLoadLineup.setRenderer(new MatchComboBoxRenderer(MatchComboBoxRenderer.RenderType.TYPE_2));
        layout.setConstraints(m_jcbLoadLineup, gbc);
        add(m_jcbLoadLineup);

        gbc.gridx = 0;
        gbc.gridy = 4;
//        gbc.fill = GridBagConstraints.NONE;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.official_game_only"));
        gbc.gridx = 1;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
        m_jcbxOfficialOnly = new JCheckBox();
        m_jcbxOfficialOnly.setSelected(false);
        layout.setConstraints(m_jcbxOfficialOnly, gbc);
        add(m_jcbxOfficialOnly);

        setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, ThemeManager.getColor(HOColorName.PLAYER_POSITION_PANEL_BORDER)));

        updateComponents();

        addListeners();
    }

    private void  updateComponents() {

        setUpcomingMatchesFromDB();

        lLastUpdateTime = DBManager.instance().getLatestUpdateTime();
        bDataTooOld = DBManager.instance().areDataTooOld();//java.text.DateFormat.getDateTimeInstance().format(dateHrf));
        sWarningDataTooOld = String.format(Helper.getTranslation("ls.module.lineup.dataTooOld.tt"), java.text.DateFormat.getDateTimeInstance().format(lLastUpdateTime));

        update_jcbUpcomingGames();
        update_jcbLoadLineup();

        if (bDataTooOld){
            m_jcbUpcomingGames.setEnabled(false);
            m_jcbUpcomingGames.setToolTipText(sWarningDataTooOld);

            m_jcbxLineupSimulation.setSelected(true);
            m_jcbxLineupSimulation.setEnabled(false);
            m_jcbxLineupSimulation.setToolTipText(sWarningDataTooOld);
        }
        else{
            m_jcbxLineupSimulation.setSelected(false);
            m_jcbxLineupSimulation.setEnabled(true);

            m_jcbUpcomingGames.setEnabled(true);
        }

        update_jcbUpcomingGames();



    }

    private void addListeners() {

        m_jcbLoadLineup.addActionListener(e -> adoptLineup());

        m_jbDownloadLineup.addActionListener(e -> downloadLineupFromHT());

        m_jcbxLineupSimulation.addActionListener( e -> {update_jcbUpcomingGames();});

        m_jcbUpcomingGames.addActionListener(e -> {
            m_clSelectedMatch = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();
            adjustLineupSettings();
            m_jbDownloadLineup.setEnabled((m_clSelectedMatch != null) && (m_clSelectedMatch.areOrdersSetInHT()));
        });

    }


    private void removeItemListeners() {

        for( ActionListener al : m_jcbLoadLineup.getActionListeners() ) {
            m_jcbLoadLineup.removeActionListener(al);
        }

        for( ActionListener al : m_jbDownloadLineup.getActionListeners() ) {
            m_jbDownloadLineup.removeActionListener(al);
        }

        for( ActionListener al : m_jcbUpcomingGames.getActionListeners() ) {
            m_jcbUpcomingGames.removeActionListener(al);
        }


    }

    private void addLabel(GridBagConstraints constraints, GridBagLayout layout, String sLabel) {
        JLabel label = new JLabel(sLabel);
        label.setForeground(TITLE_FG);
        label.setFont(getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        layout.setConstraints(label, constraints);
        add(label);
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


    private void setUpcomingMatchesFromDB(){
        MatchKurzInfo[] matches = DBManager.instance().getMatchesKurzInfo(OWN_TEAM_ID, MatchKurzInfo.UPCOMING);
        Arrays.sort(matches, Collections.reverseOrder());
        MatchOrdersCBItem clMatchOrdersTemp;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<MatchOrdersCBItem> upcomingMatches = new ArrayList<>();

        for (MatchKurzInfo match : matches) {
            if (match.getMatchDateAsTimestamp().after(now)) {
                clMatchOrdersTemp = new MatchOrdersCBItem();
                clMatchOrdersTemp.setMatchID(match.getMatchID());
                clMatchOrdersTemp.setMatchType(match.getMatchTyp());
                clMatchOrdersTemp.setMatchTime(match.getMatchDateAsTimestamp());
                clMatchOrdersTemp.setOpponentName(match.getOpponentName());
                clMatchOrdersTemp.setOrdersSetInHT(match.isOrdersGiven());
                if (match.getMatchTyp().isTournament()){
                    clMatchOrdersTemp.setLocation(IMatchDetails.LOCATION_TOURNAMENT);
                }
                else{
                    if (match.isHomeMatch()){
                        clMatchOrdersTemp.setLocation(IMatchDetails.LOCATION_HOME);
                    }
                    else if(match.isDerby()){
                        clMatchOrdersTemp.setLocation(IMatchDetails.LOCATION_AWAYDERBY);
                    }
                    else {
                        clMatchOrdersTemp.setLocation(IMatchDetails.LOCATION_AWAY);
                    }
                }

                clMatchOrdersTemp.setWeather(match.getWeather());
                clMatchOrdersTemp.setWeatherForecast(match.getWeatherForecast());
                upcomingMatches.add(clMatchOrdersTemp);
            }
        }

        upcomingMatchesInDB = upcomingMatches;

    }

    /*
        update items in UpcomingGames ComboBox except if data are too old or if Lineup simulator is checked
     */
    private void update_jcbUpcomingGames() {

        // remove all elements
        m_jcbUpcomingGames.removeAllItems();
        List<MatchOrdersCBItem> upcomingMatches = new ArrayList<>();

        // put them back only of data are recent enough and Lineup Simulator is not checked
        if(bDataTooOld || (m_jcbxLineupSimulation.isSelected())){
            upcomingMatches.add(null);
            m_clSelectedMatch = null;
        }
        else {
            upcomingMatches = upcomingMatchesInDB;

            int i = 0;
            for (MatchOrdersCBItem element : upcomingMatches) {
                m_jcbUpcomingGames.addItem(element);
                i++;
            }

            m_jcbUpcomingGames.setMaximumRowCount(i);
            m_clSelectedMatch = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();
        }

        m_jbDownloadLineup.setEnabled((m_clSelectedMatch != null) && (m_clSelectedMatch.areOrdersSetInHT()));
    }

    private void adoptLineup() {

        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        lineup.clearLineup();

        if (m_jcbLoadLineup.getSelectedItem() != null){
            jpParent.getLineupPanel().getLineupAssistantPanel().setGroupFilter(false);
            int iMatchID = ((Team)(m_jcbLoadLineup.getSelectedItem())).getMatchID();
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
        }

        jpParent.update();

        }

    private void downloadLineupFromHT() {

        MatchOrdersCBItem matchOrder = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();

        assert matchOrder != null;

        Lineup lineup;

        try {
            CursorToolkit.startWaitCursor(this);
            lineup = OnlineWorker.getLineupbyMatchId(matchOrder.getMatchID(), matchOrder.getMatchType());
        }
        finally {
            CursorToolkit.stopWaitCursor(this);
        }

        if (lineup != null) if (matchOrder != null) {
            lineup.setLocation(matchOrder.getLocation());
            lineup.setWeather(matchOrder.getWeather());
            lineup.setWeatherForecast(matchOrder.getWeatherForecast());
        }

        HOVerwaltung.instance().getModel().setLineup(lineup);
        jpParent.update();
    }


    private void adjustLineupSettings(){
        MatchOrdersCBItem matchOrder = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();
        assert matchOrder != null : "matchOrder is null";

        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        lineup.setLocation(matchOrder.getLocation());
        lineup.setWeather(matchOrder.getWeather());
        lineup.setWeatherForecast(matchOrder.getWeatherForecast());
        jpParent.update();
    }


    @Override
    public void reInit() {
        refresh();
    }

    @Override
    public void refresh() {
        HOLogger.instance().log(getClass(), "MatchAndLineupSelection panel:  refresh() has been called");
        removeItemListeners();
        HOLogger.instance().log(getClass(), "MatchAndLineupSelection panel:  items listenters have been removed");
        updateComponents();
        HOLogger.instance().log(getClass(), "MatchAndLineupSelection panel:  updateComponents() has been called");
        addListeners();
    }

}
