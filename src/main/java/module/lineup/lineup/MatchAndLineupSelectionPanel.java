package module.lineup.lineup;

import core.datatype.CBItem;
import core.db.DBManager;
import core.db.user.UserManager;
import core.gui.CursorToolkit;
import core.gui.Refreshable;
import core.gui.model.MatchOrdersCBItem;
import core.gui.model.MatchOrdersRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.Ratings;
import core.model.enums.MatchType;
import core.model.match.*;
import core.model.player.IMatchRoleID;
import core.model.player.TrainerType;
import core.net.OnlineWorker;
import core.util.*;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static core.gui.HOMainFrame.instance;
import static module.lineup.LineupPanel.TITLE_FG;


public class MatchAndLineupSelectionPanel extends JPanel implements Refreshable {

    private final int OWN_TEAM_ID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
    LineupPanel lineupPanel;
    private JComboBox<MatchOrdersCBItem> m_jcbUpcomingGames;
    private List<MatchOrdersCBItem> upcomingMatchesInDB;
    private JButton m_jbUploadLineup;
    private JButton m_jbDownloadLineup;

    private JComboBox<CBItem> m_jcbTeamAttitude;
    private JComboBox<CBItem> m_jcbTactic;
    private JComboBox<CBItem> m_jcbStyleOfPlay;

    private @Nullable MatchOrdersCBItem m_clSelectedMatch;

    final String offensive_sop = HOVerwaltung.instance().getLanguageString("ls.team.styleofplay.offensive");
    final String defensive_sop = HOVerwaltung.instance().getLanguageString("ls.team.styleofplay.defensive");
    final String neutral_sop = HOVerwaltung.instance().getLanguageString("ls.team.styleofplay.neutral");

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

        gbc.insets = new Insets(2, 2, 2, 2);
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
        gbc.gridy++;
        addLabel(gbc, layout, Helper.getTranslation("ls.team.teamattitude"));

        gbc.gridx = 1;
        m_jcbTeamAttitude = new JComboBox<>(new CBItem[]{
                new CBItem(
                        HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.playitcool"),
                        IMatchDetails.EINSTELLUNG_PIC),
                new CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.normal"),
                        IMatchDetails.EINSTELLUNG_NORMAL),
                new CBItem(HOVerwaltung.instance().getLanguageString(
                        "ls.team.teamattitude.matchoftheseason"), IMatchDetails.EINSTELLUNG_MOTS)});
        layout.setConstraints(m_jcbTeamAttitude, gbc);
        add(m_jcbTeamAttitude);

        gbc.gridx = 0;
        gbc.gridy++;
        addLabel(gbc, layout, Helper.getTranslation("ls.team.tactic"));

        gbc.gridx = 1;
        m_jcbTactic = new JComboBox<>(new CBItem[]{
                new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_NORMAL),
                        IMatchDetails.TAKTIK_NORMAL),
                new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_PRESSING),
                        IMatchDetails.TAKTIK_PRESSING),
                new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_KONTER),
                        IMatchDetails.TAKTIK_KONTER),
                new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_MIDDLE),
                        IMatchDetails.TAKTIK_MIDDLE),
                new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_WINGS),
                        IMatchDetails.TAKTIK_WINGS),
                new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_CREATIVE),
                        IMatchDetails.TAKTIK_CREATIVE),
                new CBItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_LONGSHOTS),
                        IMatchDetails.TAKTIK_LONGSHOTS)});
        layout.setConstraints(m_jcbTactic, gbc);
        add(m_jcbTactic);

        gbc.gridx = 0;
        gbc.gridy++;
        addLabel(gbc, layout, Helper.getTranslation("ls.team.styleofPlay"));

        gbc.gridx = 1;
        m_jcbStyleOfPlay = new JComboBox<>();
        layout.setConstraints(m_jcbStyleOfPlay, gbc);
        add(m_jcbStyleOfPlay);

        // Panel with the 3 buttons
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel jpButtons = new JPanel(new FlowLayout());

        m_jbUploadLineup = new JButton(Helper.getTranslation("lineup.upload.btn.upload"));
        m_jbUploadLineup.setToolTipText(Helper.getTranslation("lineup.upload.btn.upload.tooltip"));
        m_jbUploadLineup.setEnabled(m_clSelectedMatch != null);

        m_jbDownloadLineup = new JButton(Helper.getTranslation("lineup.upload.btn.download"));
        m_jbDownloadLineup.setToolTipText(Helper.getTranslation("lineup.upload.btn.download.tooltip"));
        m_jbDownloadLineup.setEnabled((m_clSelectedMatch != null) && (m_clSelectedMatch.areOrdersSetInHT()));

        GUIUtils.equalizeComponentSizes(m_jbUploadLineup, m_jbDownloadLineup/*, m_jbGetRatingsPrediction*/);

        jpButtons.add(m_jbUploadLineup);
        jpButtons.add(m_jbDownloadLineup);
        layout.setConstraints(jpButtons, gbc);
        add(jpButtons);

        setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, ThemeManager.getColor(HOColorName.PLAYER_POSITION_PANEL_BORDER)));

        addListeners();
    }

    private void updateComponents() {

        setUpcomingMatchesFromDB();
        update_jcbUpcomingGames();

        m_clSelectedMatch = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();
        if (m_jcbTeamAttitude!=null) {
            setEnabledTeamAttitudeCB((m_clSelectedMatch != null) && m_clSelectedMatch.getMatchTypeExtended().isCompetitive());

            MatchOrdersCBItem matchOrder = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();

            Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
            if (matchOrder != null) {
                lineup.setLocation(matchOrder.getLocation());
                lineup.setWeather(matchOrder.getWeather());
                lineup.setWeatherForecast(matchOrder.getWeatherForecast());
            }
        }
        m_jbDownloadLineup.setEnabled((m_clSelectedMatch != null) && (m_clSelectedMatch.areOrdersSetInHT()));
        m_jbUploadLineup.setEnabled(m_clSelectedMatch != null);

        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

        // refresh lineup settings
        Helper.setComboBoxFromID(m_jcbTactic, lineup.getTacticType());
        updateStyleOfPlayComboBox();

    }

    private void addListeners() {

        m_jbDownloadLineup.addActionListener(e -> downloadLineupFromHT());

        m_jbUploadLineup.addActionListener(e -> uploadLineupToHT());

        m_jcbUpcomingGames.addActionListener(e -> {
            m_clSelectedMatch = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();
            setEnabledTeamAttitudeCB((m_clSelectedMatch != null) && m_clSelectedMatch.getMatchType().isCompetitive());
            adjustLineupSettings();
            m_jbDownloadLineup.setEnabled((m_clSelectedMatch != null) && (m_clSelectedMatch.areOrdersSetInHT()));
            m_jbUploadLineup.setEnabled(m_clSelectedMatch != null);
        });

        m_jcbStyleOfPlay.addActionListener(e -> {
            // StyleOfPlay changed (directly or indirectly)
            var lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
            var styleOfPlay = ((CBItem) Objects.requireNonNull(m_jcbStyleOfPlay.getSelectedItem(), "ERROR: Style Of Play is null")).getId();
            lineup.setStyleOfPlay(styleOfPlay);
            lineupPanel.refreshLineupRatingPanel();
        });

        m_jcbTeamAttitude.addActionListener(e -> {
            // Attitude changed
            var lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
            var attitude = ((CBItem) Objects.requireNonNull(m_jcbTeamAttitude.getSelectedItem(), "ERROR: Attitude is null")).getId();
            lineup.setAttitude(attitude);
            lineupPanel.refreshLineupRatingPanel();
        });

        m_jcbTactic.addActionListener(e -> {
            // Tactic changed
            var lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
            var tactic = ((CBItem) Objects.requireNonNull(m_jcbTactic.getSelectedItem(), "ERROR: Tactic type is null")).getId();
            lineup.setTacticType(tactic);
            lineupPanel.refreshLineupRatingPanel();
        });
    }

    private void removeListeners() {

        for( ActionListener al : m_jbDownloadLineup.getActionListeners() ) {
            m_jbDownloadLineup.removeActionListener(al);
        }

        for( ActionListener al : m_jbUploadLineup.getActionListeners() ) {
            m_jbUploadLineup.removeActionListener(al);
        }

        for( ActionListener al : m_jcbUpcomingGames.getActionListeners() ) {
            m_jcbUpcomingGames.removeActionListener(al);
        }

        for( ActionListener al : m_jcbStyleOfPlay.getActionListeners() ) {
            m_jcbStyleOfPlay.removeActionListener(al);
        }

        for( ActionListener al : m_jcbTactic.getActionListeners() ) {
            m_jcbTactic.removeActionListener(al);
        }

        for( ActionListener al : m_jcbTeamAttitude.getActionListeners() ) {
            m_jcbTeamAttitude.removeActionListener(al);
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

    private void setUpcomingMatchesFromDB(){
        MatchKurzInfo[] matches = DBManager.instance().getMatchesKurzInfo(OWN_TEAM_ID, MatchKurzInfo.UPCOMING);
        Arrays.sort(matches, Collections.reverseOrder());
        var now = HODateTime.now();
        List<MatchOrdersCBItem> upcomingMatches = new ArrayList<>();
        short location;

        for (MatchKurzInfo match : matches) {
            if (match.getMatchSchedule().isAfter(now)) {
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
        var upcomingMatches = upcomingMatchesInDB;

        int i = 0;
        int selected = 0;
        for (MatchOrdersCBItem element : upcomingMatches) {
            m_jcbUpcomingGames.addItem(element);
            if (m_clSelectedMatch != null && element.getMatchID() == m_clSelectedMatch.getMatchID()) selected = i;
            i++;
        }

        m_jcbUpcomingGames.setMaximumRowCount(i);
        m_jcbUpcomingGames.setSelectedIndex(selected);
        m_clSelectedMatch = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();

        m_jbDownloadLineup.setEnabled((m_clSelectedMatch != null) && (m_clSelectedMatch.areOrdersSetInHT()));
        m_jbUploadLineup.setEnabled(m_clSelectedMatch != null);
    }

    private void downloadLineupFromHT() {
        MatchOrdersCBItem matchOrder = (MatchOrdersCBItem) m_jcbUpcomingGames.getSelectedItem();
        try {
            if(matchOrder != null) {
                CursorToolkit.startWaitCursor(this);
                var matchLineupTeam = OnlineWorker.getLineupbyMatchId(matchOrder.getMatchID(), matchOrder.getMatchType());
                if (matchLineupTeam != null) {
                    var lineup = matchLineupTeam.getLineup();
                    lineup.setLocation(matchOrder.getLocation());
                    lineup.setWeather(matchOrder.getWeather());
                    lineup.setWeatherForecast(matchOrder.getWeatherForecast());
                    HOVerwaltung.instance().getModel().storeLineup(matchLineupTeam);
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

    private void uploadLineupToHT() {

        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        if (!LineupCheck.doUpload(m_clSelectedMatch, lineup)) {
            return;
        }

        String result;

        try {
            CursorToolkit.startWaitCursor(this);
            assert m_clSelectedMatch != null : "Can't push a lineup if selected game is null !";
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

                // store lineup in database
                var lineupTeam = new MatchLineupTeam( m_clSelectedMatch.getMatchType(), m_clSelectedMatch.getMatchID(),
                        HOVerwaltung.instance().getModel().getBasics().getTeamName(), OWN_TEAM_ID, 0);
                lineupTeam.setLineup(lineup);
                DBManager.instance().storeMatchLineupTeam(lineupTeam);
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

        removeListeners();


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
        removeListeners();
        updateComponents();
        addListeners();
    }

    // each time updateStyleOfPlayBox gets called we need to add all elements back so that we can load stored lineups
    // so we need addAllStyleOfPlayItems() after every updateStyleOfPlayBox()
    private void updateStyleOfPlayComboBox()
    {
        var lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        var oldValue = StyleOfPlay.fromInt(lineup.getStyleOfPlay());
        // NT Team can select whatever Style of Play they like
        if (!UserManager.instance().getCurrentUser().isNtTeam()) {

            // remove all combo box items and add new ones.
            List<Integer> legalValues = getValidStyleOfPlayValues();

            m_jcbStyleOfPlay.removeAllItems();

            for (int value : legalValues) {
                CBItem cbItem;
                if (value == 0) {
                    cbItem = new CBItem(neutral_sop, value);
                } else if (value > 0) {
                    cbItem = new CBItem((value * 10) + "% " + offensive_sop, value);
                } else {
                    cbItem = new CBItem((Math.abs(value) * 10) + "% " + defensive_sop, value);
                }
                m_jcbStyleOfPlay.addItem(cbItem);
            }

            // Set trainer default value
            setStyleOfPlay(getDefaultTrainerStyleOfPlay());
            // Attempt to set the old value. If it is not possible it will do nothing.
            setStyleOfPlay(oldValue);
        }
        var item = (CBItem)(m_jcbStyleOfPlay.getSelectedItem());
        var ret = 0;
        if ( item != null) ret = item.getId();

        lineup.setStyleOfPlay(ret);
    }

    private List<Integer> getValidStyleOfPlayValues()
    {
        TrainerType trainer;
        int tacticalAssistants;
        try {
            trainer = HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp();
            tacticalAssistants = HOVerwaltung.instance().getModel().getClub().getTacticalAssistantLevels();

        } catch (Exception e) {
            trainer = TrainerType.Balanced;
            tacticalAssistants = 0;
            HOLogger.instance().error(getClass(), "Model not ready, put default value " + trainer + " for trainer and "  + tacticalAssistants + " for tactical Assistants.");
        }

        int min=-10, max=10;

        switch (trainer) {
            case Defensive -> max = -10 + 2 * tacticalAssistants;  // Defensive
            case Offensive -> min = 10 - 2 * tacticalAssistants;   // Offensive
            case Balanced -> {     			                   // Neutral
                min = - tacticalAssistants;
                max = tacticalAssistants;
            }
            default -> HOLogger.instance().error(getClass(), "Illegal trainer type found: " + trainer);
        }

        return IntStream.rangeClosed(min, max).boxed().collect(Collectors.toList());
    }

    public void setStyleOfPlay(StyleOfPlay style){
        Helper.setComboBoxFromID(m_jcbStyleOfPlay, StyleOfPlay.toInt(style));
    }

    private StyleOfPlay getDefaultTrainerStyleOfPlay() {
        TrainerType trainer;
        try {
            trainer = HOVerwaltung.instance().getModel().getTrainer().getTrainerTyp();
        } catch (Exception e) {
            return StyleOfPlay.Neutral();  // Happens for instance with empty db
        }

        return switch (trainer) {
            case Defensive -> StyleOfPlay.Defensive(); // Defensive
            case Offensive -> StyleOfPlay.Offensive(); // Offensive
            default -> StyleOfPlay.Neutral();  // Neutral
        };
    }

    public void setEnabledTeamAttitudeCB(boolean enabled) {
        int attitude;
        if (!enabled){
            attitude = MatchTeamAttitude.toInt(MatchTeamAttitude.Normal); // core.model.match.IMatchDetails.EINSTELLUNG_NORMAL;
        }
        else {
            var lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
            if ( lineup != null){
                attitude = lineup.getAttitude();
            }
            else{
                attitude = MatchTeamAttitude.toInt(MatchTeamAttitude.Normal);
            }
        }
        Helper.setComboBoxFromID(m_jcbTeamAttitude, attitude);
        m_jcbTeamAttitude.setEnabled(enabled);
    }

}
