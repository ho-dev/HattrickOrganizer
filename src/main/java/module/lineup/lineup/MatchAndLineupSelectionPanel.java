package module.lineup.lineup;

import core.db.DBManager;
import core.gui.CursorToolkit;
import core.gui.Refreshable;
import core.gui.model.MatchOrdersCBItem;
import core.gui.model.MatchOrdersRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.Ratings;
import core.model.match.*;
import core.model.player.IMatchRoleID;
import core.net.OnlineWorker;
import core.util.GUIUtils;
import core.util.HOLogger;
import core.util.Helper;
import core.util.XMLUtils;
import module.lineup.Lineup;
import module.lineup.LineupPanel;
import module.teamAnalyzer.ui.MatchComboBoxRenderer;
import module.teamAnalyzer.vo.Team;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

import static core.gui.HOMainFrame.instance;
import static module.lineup.LineupPanel.TITLE_FG;


public class MatchAndLineupSelectionPanel extends JPanel implements Refreshable {

    private final int OWN_TEAM_ID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
    private final int MAX_PREVIOUS_LINEUP = 10;
    LineupPanel lineupPanel;
    private JComboBox<Team> m_jcbLoadLineup;
    private JComboBox<MatchOrdersCBItem> m_jcbUpcomingGames;
    private List<MatchOrdersCBItem> upcomingMatchesInDB;
    private JCheckBox m_jcbxLineupSimulation;
    private JCheckBox m_jcbxOfficialOnly;
    private JButton m_jbUploadLineup;
    private JButton m_jbDownloadLineup;
    //private JButton m_jbGetRatingsPrediction;
    private @Nullable MatchOrdersCBItem m_clSelectedMatch;
    private String sWarningDataTooOld;
    private Long lLastUpdateTime;
    private ArrayList<MatchKurzInfo> previousPlayedMatchesAll = null;
    private ArrayList<MatchKurzInfo> previousPlayedMatchesOfficialOnly = null;

    public MatchOrdersCBItem getSelectedMatch() {
        return m_clSelectedMatch;
    }

    public MatchAndLineupSelectionPanel(LineupPanel parent) {
        lineupPanel = parent;
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
        m_jcbUpcomingGames.setPreferredSize(new Dimension(160, 25));
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

        // Panel with the 3 buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel jpButtons = new JPanel(new FlowLayout());

        m_jbUploadLineup = new JButton(Helper.getTranslation("lineup.upload.btn.upload"));
        m_jbUploadLineup.setToolTipText(Helper.getTranslation("lineup.upload.btn.upload.tooltip"));
        m_jbUploadLineup.setEnabled(m_clSelectedMatch != null);

        m_jbDownloadLineup = new JButton(Helper.getTranslation("lineup.upload.btn.download"));
        m_jbDownloadLineup.setToolTipText(Helper.getTranslation("lineup.upload.btn.download.tooltip"));
        m_jbDownloadLineup.setEnabled((m_clSelectedMatch != null) && (m_clSelectedMatch.areOrdersSetInHT()));
/*
        m_jbGetRatingsPrediction = new JButton(Helper.getTranslation("lineup.getRatingsPrediction.btn.label"));
        m_jbGetRatingsPrediction.setToolTipText(Helper.getTranslation("lineup.getRatingsPrediction.btn.tooltip"));
        m_jbGetRatingsPrediction.setEnabled(false);
*/
        GUIUtils.equalizeComponentSizes(m_jbUploadLineup, m_jbDownloadLineup/*, m_jbGetRatingsPrediction*/);

        jpButtons.add(m_jbUploadLineup);
        jpButtons.add(m_jbDownloadLineup);
//        jpButtons.add(m_jbGetRatingsPrediction);

        layout.setConstraints(jpButtons, gbc);
        add(jpButtons);

        // ===============================================

        gbc.gridx = 0;
        gbc.gridy = 3;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.load_lineup"));

        gbc.gridx = 1;
        m_jcbLoadLineup = new JComboBox<>();
        m_jcbLoadLineup.setRenderer(new MatchComboBoxRenderer(MatchComboBoxRenderer.RenderType.TYPE_2));
        layout.setConstraints(m_jcbLoadLineup, gbc);
        add(m_jcbLoadLineup);

        gbc.gridx = 0;
        gbc.gridy = 4;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.official_game_only"));
        gbc.gridx = 1;
        m_jcbxOfficialOnly = new JCheckBox();
        m_jcbxOfficialOnly.setSelected(false);
        layout.setConstraints(m_jcbxOfficialOnly, gbc);
        add(m_jcbxOfficialOnly);

        setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, ThemeManager.getColor(HOColorName.PLAYER_POSITION_PANEL_BORDER)));

        //updateComponents();

        addListeners();
    }

    private void  updateComponents() {

        setUpcomingMatchesFromDB();
        update_jcbUpcomingGames();
        update_jcbLoadLineup();

        if (upcomingMatchesInDB.size()==0){
            lLastUpdateTime = DBManager.instance().getLatestUpdateTime();
            sWarningDataTooOld = String.format(Helper.getTranslation("ls.module.lineup.dataTooOld.tt"), java.text.DateFormat.getDateTimeInstance().format(lLastUpdateTime));

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
        m_clSelectedMatch = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();
        if (lineupPanel.isTeamAttitudeInitialized()) {
            lineupPanel.setEnabledTeamAttitudeCB((m_clSelectedMatch != null) && m_clSelectedMatch.getMatchType().isCompetitive());

            MatchOrdersCBItem matchOrder = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();

            Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
            if (matchOrder != null) {
                lineup.setLocation(matchOrder.getLocation());
                lineup.setWeather(matchOrder.getWeather());
                lineup.setWeatherForecast(matchOrder.getWeatherForecast());
            }
            lineupPanel.refreshLineupSettingsPanel();
            lineupPanel.refreshLineupRatingPanel();
        }
        m_jbDownloadLineup.setEnabled((m_clSelectedMatch != null) && (m_clSelectedMatch.areOrdersSetInHT()));
        m_jbUploadLineup.setEnabled(m_clSelectedMatch != null);

    }

    private void addListeners() {

        m_jcbLoadLineup.addActionListener(e -> adoptLineup());

        m_jbDownloadLineup.addActionListener(e -> downloadLineupFromHT());

        m_jbUploadLineup.addActionListener(e -> uploadLineupToHT());

        m_jcbxLineupSimulation.addActionListener( e -> {
            Lineup lineup = HOVerwaltung.instance().getModel().getLineup();
            Ratings oRatingsBefore = lineup.getRatings();

            update_jcbUpcomingGames();
            if(! isLineupSimulator()) {
                lineupPanel.resetSettings();
                lineupPanel.updateLineupPositions();
            }

            lineupPanel.setPreviousRatings(oRatingsBefore);
            lineupPanel.calculateRatings();

        });

        m_jcbxOfficialOnly.addActionListener(e -> update_jcbLoadLineup(false));

        m_jcbUpcomingGames.addActionListener(e -> {
            m_clSelectedMatch = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();
            lineupPanel.setEnabledTeamAttitudeCB((m_clSelectedMatch != null) && m_clSelectedMatch.getMatchType().isCompetitive());
            adjustLineupSettings();
            m_jbDownloadLineup.setEnabled((m_clSelectedMatch != null) && (m_clSelectedMatch.areOrdersSetInHT()));
            m_jbUploadLineup.setEnabled(m_clSelectedMatch != null);
        });

    }


    private void removeItemListeners() {

        for( ActionListener al : m_jcbLoadLineup.getActionListeners() ) {
            m_jcbLoadLineup.removeActionListener(al);
        }

        for( ActionListener al : m_jbDownloadLineup.getActionListeners() ) {
            m_jbDownloadLineup.removeActionListener(al);
        }

        for( ActionListener al : m_jbUploadLineup.getActionListeners() ) {
            m_jbUploadLineup.removeActionListener(al);
        }

        for( ActionListener al : m_jcbxLineupSimulation.getActionListeners() ) {
            m_jcbxLineupSimulation.removeActionListener(al);
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

    private void update_jcbLoadLineup(){
        update_jcbLoadLineup(true);
    }

    private void update_jcbLoadLineup(boolean bForceRefresh) {
        m_jcbLoadLineup.removeAllItems();


        Team oTeam;

        ArrayList<MatchKurzInfo> previousPlayedMatches;

        if(m_jcbxOfficialOnly.isSelected()){
            if((previousPlayedMatchesOfficialOnly == null) || bForceRefresh){
                previousPlayedMatchesOfficialOnly = DBManager.instance().getOwnPlayedMatchInfo(MAX_PREVIOUS_LINEUP, true);
            }
            previousPlayedMatches = previousPlayedMatchesOfficialOnly;
        }
        else{
            if((previousPlayedMatchesAll == null) || bForceRefresh){
                previousPlayedMatchesAll = DBManager.instance().getOwnPlayedMatchInfo(MAX_PREVIOUS_LINEUP);
            }
            previousPlayedMatches = previousPlayedMatchesAll;
        }


        m_jcbLoadLineup.addItem(null);
        int i = 1;
        for (MatchKurzInfo match : previousPlayedMatches) {

            oTeam = new Team();

            if (match.getHomeTeamID() == OWN_TEAM_ID) {
                oTeam.setName(match.getGuestTeamName());
                oTeam.setTeamId(match.getGuestTeamID());
                oTeam.setHomeMatch(true);
            }
            else
                {
                oTeam.setName(match.getHomeTeamName());
                oTeam.setTeamId(match.getHomeTeamID());
                oTeam.setHomeMatch(false);
            }
            oTeam.setTime(match.getMatchDateAsTimestamp());
            oTeam.setMatchType(match.getMatchTypeExtended().getIconArrayIndex());
            oTeam.setMatchID(match.getMatchID());

            m_jcbLoadLineup.addItem(oTeam);
            i++;

        }

        m_jcbLoadLineup.setMaximumRowCount(i);

    }

    private void setUpcomingMatchesFromDB(){
        MatchKurzInfo[] matches = DBManager.instance().getMatchesKurzInfo(OWN_TEAM_ID, MatchKurzInfo.UPCOMING);
        Arrays.sort(matches, Collections.reverseOrder());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<MatchOrdersCBItem> upcomingMatches = new ArrayList<>();
        short location;

        for (MatchKurzInfo match : matches) {
            if (match.getMatchDateAsTimestamp().after(now)) {
                if (match.getMatchType().isTournament()){
                    location = IMatchDetails.LOCATION_TOURNAMENT;
                }
                else{
                    if (match.isHomeMatch()){
                        location = IMatchDetails.LOCATION_HOME;
                    }
                    else if(match.isDerby()){
                        location = IMatchDetails.LOCATION_AWAYDERBY;
                    }
                    else {
                        location = IMatchDetails.LOCATION_AWAY;
                    }
                }
                upcomingMatches.add(new MatchOrdersCBItem(match, location));
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
        if(m_jcbxLineupSimulation.isSelected()){
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
        m_jbUploadLineup.setEnabled(m_clSelectedMatch != null);
    }

    private void adoptLineup() {

        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        lineup.clearLineup();

        if (m_jcbLoadLineup.getSelectedItem() != null){
            lineupPanel.setAssistantGroupFilter(false);
            int iMatchID = ((Team)(m_jcbLoadLineup.getSelectedItem())).getMatchID();
            Vector<MatchLineupPosition> lineupPlayers = DBManager.instance().getMatchLineupPlayers(iMatchID, this.matchType, OWN_TEAM_ID);
            if (lineupPlayers != null) {
                for (MatchLineupPosition lineupPlayer : lineupPlayers) {
                    if (lineupPlayer.getRoleId() == IMatchRoleID.setPieces) {
                        lineup.setKicker(lineupPlayer.getPlayerId());
                    }
                    else if (lineupPlayer.getRoleId() == IMatchRoleID.captain) {
                        lineup.setCaptain(lineupPlayer.getPlayerId());
                    }
                    else {
                        lineup.setSpielerAtPosition(lineupPlayer.getRoleId(), lineupPlayer.getPlayerId(), lineupPlayer.getBehaviour());
                    }
                }
            }
        }

        lineupPanel.update();

        }

    private void downloadLineupFromHT() {

        MatchOrdersCBItem matchOrder = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();
        Lineup lineup;

        try {
            if(matchOrder != null) {
                CursorToolkit.startWaitCursor(this);
                lineup = OnlineWorker.getLineupbyMatchId(matchOrder.getMatchID(), matchOrder.getMatchType());
                if (lineup != null) {
                    lineup.setLocation(matchOrder.getLocation());
                    lineup.setWeather(matchOrder.getWeather());
                    lineup.setWeatherForecast(matchOrder.getWeatherForecast());
                    HOVerwaltung.instance().getModel().setLineup(lineup);
                }
            }
        }
        finally {
            CursorToolkit.stopWaitCursor(this);
        }

        lineupPanel.update();
    }

    private void adjustLineupSettings(){
        MatchOrdersCBItem matchOrder = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();

        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        if (matchOrder != null) {
            lineup.setLocation(matchOrder.getLocation());
            lineup.setWeather(matchOrder.getWeather());
            lineup.setWeatherForecast(matchOrder.getWeatherForecast());
        }
        lineupPanel.update();
    }

    public boolean isLineupSimulator(){
        return m_jcbxLineupSimulation.isSelected();
    }


    private void uploadLineupToHT() {

        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        if (!LineupCheck.doUpload(m_clSelectedMatch, lineup)) {
            return;
        }

        String result;

        try {
            CursorToolkit.startWaitCursor(this);
            assert m_clSelectedMatch != null : "Cann't push a lineup if selected game is null !";
            result = OnlineWorker.uploadMatchOrder(m_clSelectedMatch.getMatchID(), m_clSelectedMatch.getMatchType(), lineup);
        }
        finally {
            CursorToolkit.stopWaitCursor(this);
        }

        int messageType;
        boolean success = false;
        String message;
        try {
            Document doc = XMLUtils.createDocument(result);
            String successStr = XMLUtils.getAttributeValueFromNode(doc, "MatchData", "OrdersSet");
            if (successStr != null) {
                success = Boolean.parseBoolean(successStr);
                if (success) {
                    messageType = JOptionPane.PLAIN_MESSAGE;
                    message = Helper.getTranslation("lineup.upload.success");
                }
                else {
                    messageType = JOptionPane.ERROR_MESSAGE;
                    message = Helper.getTranslation("lineup.upload.fail")
                            + "\n" + XMLUtils.getTagData(doc, "Reason");
                }
            }
            else {
                messageType = JOptionPane.ERROR_MESSAGE;
                message = Helper.getTranslation("lineup.upload.result.parseerror");
                HOLogger.instance().log(getClass(), message + "\n" + result);
            }
        } catch (Exception e) {
            messageType = JOptionPane.ERROR_MESSAGE;
            message = Helper.getTranslation("lineup.upload.result.parseerror");
            HOLogger.instance().log(getClass(), message + "\n" + result);
            HOLogger.instance().log(getClass(), e);
        }

        if (success) {
            m_clSelectedMatch.setOrdersSetInHT(true);
            try {
                CursorToolkit.startWaitCursor(this);
                MatchKurzInfo refreshed = OnlineWorker.updateMatch(OWN_TEAM_ID, m_clSelectedMatch);
                if (refreshed != null) {
                    m_clSelectedMatch.merge(refreshed);
                }
                DBManager.instance().updateMatchKurzInfo(m_clSelectedMatch);
                refresh();
                //update_jcbUpcomingGamesAfterSendingMatchOrders(m_clSelectedMatch);
            }
            finally {
                CursorToolkit.stopWaitCursor(this);
            }
        }

        JOptionPane.showMessageDialog(instance(), message, Helper.getTranslation("lineup.upload.title"), messageType);
    }


    /*
            update items in UpcomingGames ComboBox except if data are too old or if Lineup simulator is checked
         */
    private void update_jcbUpcomingGamesAfterSendingMatchOrders(MatchOrdersCBItem selectedMatch) {

        removeItemListeners();


        int selectedMatchID = selectedMatch.getMatchID();

        // Update upcomingMatchesInDB with uploaded game
        List<MatchOrdersCBItem> copyUpcomingMatchesInDB = new ArrayList<>(upcomingMatchesInDB);
        upcomingMatchesInDB = new ArrayList<>();

        for (MatchOrdersCBItem element : copyUpcomingMatchesInDB) {
            if(element.getMatchID() == selectedMatchID) {
                upcomingMatchesInDB.add(selectedMatch);
            }
            else {
                m_jcbUpcomingGames.addItem(element);
            }
        }


        // update all elements in m_jcbUpcomingGames
        m_jcbUpcomingGames.removeAllItems();

        for (MatchOrdersCBItem element : upcomingMatchesInDB) {
                m_jcbUpcomingGames.addItem(element);
        }

        m_jcbUpcomingGames.setMaximumRowCount(upcomingMatchesInDB.size());
        m_clSelectedMatch = selectedMatch;

        m_jbDownloadLineup.setEnabled(true);
        m_jbUploadLineup.setEnabled(true);

        m_jcbUpcomingGames.setSelectedItem(selectedMatch);

        refresh();
    }

    @Override
    public void reInit() {
        refresh();
    }

    @Override
    public void refresh() {
//        HOLogger.instance().log(getClass(), " refresh() has been called");
        removeItemListeners();
        updateComponents();
        addListeners();
    }

}
