package module.lineup.lineup;

import core.datatype.CBItem;
import core.gui.HOMainFrame;
import core.gui.Updatable;
import core.gui.comp.panel.ImagePanel;
import core.gui.model.PlayerCBItem;
import core.gui.model.PlayerCBItemRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.match.Weather;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.training.TrainingPreviewPlayers;
import core.util.Helper;
import module.lineup.Lineup;
import module.lineup.LineupAssistantSelectorOverlay;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static core.model.UserParameter.GOALKEEPER_AT_TOP;
import static core.model.UserParameter.POSITIONNAMES_SHORT;
import static core.model.player.IMatchRoleID.*;


public class PlayerPositionPanel extends ImagePanel implements ItemListener, FocusListener {

    private static final PlayerCBItem oNullPlayer = new PlayerCBItem("", 0f, null, false, true);
    private static final Color defaultBorderColor = ThemeManager.getColor(HOColorName.PLAYER_POSITION_PANEL_BORDER);

    //~ Instance fields ----------------------------------------------------------------------------
    private final JComboBox<PlayerCBItem> m_jcbPlayer = new JComboBox<>();
    private final JComboBox<CBItem> m_jcbTactic = new JComboBox<>();
    private final JLabel m_jlPosition = new JLabel();
    private final PlayerCBItem m_clSelectedPlayer = new PlayerCBItem("", 0f, null, false, true);
    private final Updatable m_clUpdater;
    private PlayerCBItem @Nullable [] m_clCBItems = new PlayerCBItem[0];
    private final int m_iPositionID;
    private int iSelectedPlayerId = -1;
    private final GridBagLayout layout = new GridBagLayout();
    private final JLayeredPane jlp = new JLayeredPane();
    private final int layerIndex = 0;
    private Weather m_weather;
    private boolean m_useWeatherImpact;
    private Integer matchMinute;

    //constructor
    protected PlayerPositionPanel(Updatable updater, int positionsID, @Nullable Weather weather, boolean useWeatherImpact, Integer matchMinute) {
        super(false);

        m_clUpdater = updater;
        m_iPositionID = positionsID;
        m_weather = weather;
        m_useWeatherImpact = useWeatherImpact;
        this.matchMinute = matchMinute;

        setOpaque(true);

        initTaktik(null);
        initLabel();
        initComponents();
    }

    protected PlayerPositionPanel(Updatable updater, int positionsID) {
        this(updater, positionsID, null, false, null);
    }

    public int getPositionsID() {
        return m_iPositionID;
    }

    private byte getTactic() {
        CBItem cbTactic = (CBItem) m_jcbTactic.getSelectedItem();
        return (cbTactic != null) ? (byte)cbTactic.getId() : IMatchRoleID.NORMAL;
    }

    public @Nullable Player getSelectedPlayer() {
        final Object obj = m_jcbPlayer.getSelectedItem();

        if (obj instanceof PlayerCBItem) {
            return ((PlayerCBItem) obj).getPlayer();
        }

        return null;
    }


    @Override
    public void focusGained(FocusEvent event) {
        if (getSelectedPlayer() != null) {
            HOMainFrame.instance().selectPlayer(getSelectedPlayer());
        }
    }

    @Override
    public void focusLost(FocusEvent event) { }

    private void initComponents() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;

        setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, defaultBorderColor));

        jlp.setLayout(layout);

        BorderLayout bl = new BorderLayout();
        bl.setHgap(0);
        bl.setVgap(0);
        setLayout(bl);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 8, 0, 0);
        jlp.add(m_jlPosition, constraints, layerIndex);

        constraints.gridy = 1;
        constraints.gridwidth = 2;
        m_jcbPlayer.addFocusListener(this);
        m_jcbPlayer.setMaximumRowCount(10);
        m_jcbPlayer.setRenderer(new PlayerCBItemRenderer());
        m_jcbPlayer.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));

        //Show only if more than one tactic is possible
        if (m_jcbTactic.getItemCount() > 1) {

            constraints.insets = new Insets(2, 4, 0, 4);
            jlp.add(m_jcbPlayer, constraints, layerIndex);

            constraints.gridy = 2;
            constraints.insets = new Insets(2, 4, 5, 4);
            m_jcbTactic.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
            jlp.add(m_jcbTactic, constraints, layerIndex);
        }
        else {
            constraints.insets = new Insets(2, 4, 5, 4);
            jlp.add(m_jcbPlayer, constraints, layerIndex);
        }

        add(jlp, BorderLayout.CENTER);
    }


    @Override
    public void itemStateChanged(java.awt.event.ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            final Lineup lineup = HOVerwaltung.instance().getModel().getCurrentLineup();

            final Player player = getSelectedPlayer();
            setPlayerTooltip(player);

            //set player
            if (itemEvent.getSource().equals(m_jcbPlayer)) {
                //set pieces
                if (m_iPositionID == IMatchRoleID.setPieces) {
                    if (player != null) {
                        lineup.setKicker(player.getPlayerId());
                    } else {
                        lineup.setKicker(0);
                    }
                }
                //captain
                else if (m_iPositionID == IMatchRoleID.captain) {
                    if (player != null) {
                        lineup.setCaptain(player.getPlayerId());
                    } else {
                        lineup.setCaptain(0);
                    }
                }
                //Others
                else {
                    if (player != null) {
                        lineup.setSpielerAtPosition(m_iPositionID, player.getPlayerId());
                    } else {
                        lineup.setSpielerAtPosition(m_iPositionID, 0);
                    }
                    // adjust backup players
                    lineup.adjustBackupPlayers();
                }

                //Adjust tactic values
                setTactic(getTactic(), player);
            } else if (itemEvent.getSource().equals(m_jcbTactic)) {
                Objects.requireNonNull(lineup.getPositionById(m_iPositionID)).setBehaviour(getTactic());
            }

            if (player != null) {
                HOMainFrame.instance().selectPlayer(player);
            }

            //Update all other positions
            m_clUpdater.update();
        }
    }

    private void setPlayerTooltip(@Nullable Player player) {
        if (player != null) {
            String tooltipMessage ="<html>";
            tooltipMessage += "<b>" + player.getFullName() + "</b>";
            String trainingMsg = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getText();
            if (trainingMsg != null) {
                tooltipMessage += "<br>" + trainingMsg;
            }

            if (!((PlayerCBItem) Objects.requireNonNull(m_jcbPlayer.getSelectedItem())).isSetInBestPosition()) {
                tooltipMessage += "<br>" + getLangStr("ls.lineup.position.warning");
            }
            tooltipMessage +=  "</html>";
            m_jcbPlayer.setToolTipText(tooltipMessage);
        }
    }


    /**
     * Update the list of player in the ComboBox except for backup
     *
     * @param inCandidates     the list of players answering all filters criteria
     * @param plStartingLineup the players in the starting 11
     * @param plSubstitutes    the substitute players (not the backup)
     * @param matchMinute      minute used for rating calculation
     */
    public void refresh(List<Player> inCandidates, List<Player> plStartingLineup, List<Player> plSubstitutes, Weather weather, Boolean useWeatherImpact, int matchMinute) {
        var plCandidates = new ArrayList<>(inCandidates);
        Player selectedPlayer = null;
        HOModel model = HOVerwaltung.instance().getModel();
        Lineup lineup = model.getCurrentLineup();

        m_weather = weather;
        m_useWeatherImpact = useWeatherImpact;
        iSelectedPlayerId = -1;
        this.matchMinute = matchMinute;

        if (m_iPositionID == IMatchRoleID.setPieces) {
            selectedPlayer = model.getCurrentPlayer(lineup.getKicker());
            if (selectedPlayer != null) {
                iSelectedPlayerId = selectedPlayer.getPlayerId();
            }

            // Filter keeper from the candidates for SetPieces taker (not allowed by HT)
            Player keeper = lineup.getPlayerByPositionID(IMatchRoleID.keeper);
            if (keeper != null) {
                int iKeeperID = keeper.getPlayerId();
                plCandidates.removeIf(pl -> pl.getPlayerId() == iKeeperID);
            }
        } else if (m_iPositionID == IMatchRoleID.captain) {
            selectedPlayer = model.getCurrentPlayer(lineup.getCaptain());
            if (selectedPlayer != null) {
                iSelectedPlayerId = selectedPlayer.getPlayerId();
            }

        } else {
            final MatchRoleID position = lineup.getPositionById(m_iPositionID);

            if (position != null) {
                selectedPlayer = model.getCurrentPlayer(position.getPlayerId());

                if (selectedPlayer != null) {
                    m_jcbPlayer.setEnabled(true); // To be sure
                    iSelectedPlayerId = selectedPlayer.getPlayerId();
                } else {
                    // We want to enable the combobox if there is room in the lineup or if it is a substitute position
                    m_jcbPlayer.setEnabled((lineup.hasFreePosition()) || (m_iPositionID >= IMatchRoleID.startReserves));
                }

                setTactic(position.getTactic(), selectedPlayer);
            }
        }

        setPlayersList(plCandidates, selectedPlayer);

        // for all players in the combobox set correct values for isSelect (starting 11) and isAssis (it is a subsitute)
        for (int i = 0; i < m_jcbPlayer.getModel().getSize(); i++) {
            PlayerCBItem obj = m_jcbPlayer.getItemAt(i);
            if (obj != null) {
                if (obj.getPlayer() != null) {
                    obj.getEntry().setIsSelect(false);
                    obj.getEntry().setIsAssit(false);
                }
            }
        }
        if (plStartingLineup != null && plSubstitutes != null) {
            for (int i = 0; i < m_jcbPlayer.getModel().getSize(); i++) {
                PlayerCBItem obj = m_jcbPlayer.getItemAt(i);
                boolean isInLineup = false;
                if (obj != null) {
                    if (obj.getPlayer() != null) {
                        for (Player value : plStartingLineup) {
                            if (obj.getPlayer().getPlayerId() == value.getPlayerId()) {
                                obj.getEntry().setIsSelect(true);
                                isInLineup = true;
                                break;
                            }
                        }
                        if (!isInLineup) {
                            for (Player value : plSubstitutes) {
                                if (obj.getPlayer().getPlayerId() == value.getPlayerId()) {
                                    obj.getEntry().setIsAssit(true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        initLabel();
        repaint();
    }


    public void refresh2(List<Player> lPlayers, int playerIDcorrespondingSub, Weather weather, Boolean useWeatherImpact) {
        Player selectedPlayer = null;
        iSelectedPlayerId = -1;
        m_weather = weather;
        m_useWeatherImpact = useWeatherImpact;

        //Get currently setup player in that position
        var team = HOVerwaltung.instance().getModel().getCurrentLineup();
        final MatchRoleID position = team.getPositionById(m_iPositionID);
        if (position != null) {
            selectedPlayer = HOVerwaltung.instance().getModel().getCurrentPlayer(position.getPlayerId());
            setTactic(position.getTactic(), selectedPlayer);
        }
        setPlayersList2(lPlayers, selectedPlayer, playerIDcorrespondingSub);
        initLabel();
        repaint();
    }

    protected void setPlayersList(List<Player> oCandidates, @Nullable Player oSelectedPlayer) {

        m_jcbPlayer.removeItemListener(this);

        final DefaultComboBoxModel<PlayerCBItem> cbModel = ((DefaultComboBoxModel<PlayerCBItem>) m_jcbPlayer.getModel());

        //Remove all items
        cbModel.removeAllElements();

        //Ensure the number of m_clCBItems objects match what is needed

        assert m_clCBItems != null;
        if (m_clCBItems.length != oCandidates.size()) {
            PlayerCBItem[] tempCB = new PlayerCBItem[oCandidates.size()];

            //Fill with SpielerCBItem: Preferably reuse old ones
            for (int i = 0; i < tempCB.length; i++) {
                //Reuse
                if ((m_clCBItems.length > i) && (m_clCBItems[i] != null)) {
                    tempCB[i] = m_clCBItems[i];

                    //HOLogger.instance().log(getClass(), "Use old SpielerCBItem " + this.m_iPositionsID );
                }
                //Create new
                else {
                    tempCB[i] = new PlayerCBItem("", 0f, null, true, true);//HOLogger.instance().log(getClass(), "Create new SpielerCBItem " + this.m_iPositionsID );
                }
            }

            //Empty reference and reset
            m_clCBItems = tempCB;
        }

        //Put back current player if new filters allows it
        this.iSelectedPlayerId = -1;
        if (oSelectedPlayer != null) {
            int iSelectedPlayerID = oSelectedPlayer.getPlayerId();
            for (Player p : oCandidates) {
                if (p.getPlayerId() == iSelectedPlayerID) {
                    cbModel.addElement(createPlayerCBItem(m_clSelectedPlayer, oSelectedPlayer));
                    this.iSelectedPlayerId = iSelectedPlayerID;
                    break;
                }
            }
        }

        Lineup lineup = HOVerwaltung.instance().getModel().getCurrentLineup();
        if (iSelectedPlayerId == -1) {
            switch (m_iPositionID){
                case IMatchRoleID.setPieces -> lineup.setKicker(0);
                case IMatchRoleID.captain -> lineup.setCaptain(0);
                default -> lineup.setSpielerAtPosition(m_iPositionID, 0);
            }
        }

        //No Player
        cbModel.addElement(oNullPlayer);

        //Sort Player List
        PlayerCBItem[] cbItems = new PlayerCBItem[oCandidates.size()];

        for (int i = 0; i < oCandidates.size(); i++) {
            cbItems[i] = createPlayerCBItem(m_clCBItems[i], oCandidates.get(i));
        }

        Arrays.sort(cbItems);

        for (PlayerCBItem cbItem : cbItems) {
            //All Other players
            cbModel.addElement(cbItem);
        }


        m_jcbPlayer.addItemListener(this);

        setTactic(getTactic(), oSelectedPlayer);
        setPlayerTooltip(m_clSelectedPlayer.getPlayer());
    }

    protected void setPlayersList2(List<Player> allPlayers, @Nullable Player selectedPlayer, int playerIDcorrespondingSub) {

        m_jcbPlayer.removeItemListener(this);

        // list of all players currently set as subs
        List<Player> lSubs = new ArrayList<>();
        Lineup lineup = HOVerwaltung.instance().getModel().getCurrentLineup();
        for (Player player : allPlayers) {
            if (lineup.isPlayerASub(player.getPlayerId())) {
                lSubs.add(player);
            }
        }

        final DefaultComboBoxModel<PlayerCBItem> cbModel = ((DefaultComboBoxModel<PlayerCBItem>) m_jcbPlayer.getModel());

        //Remove all items
        cbModel.removeAllElements();

        //Ensure the number of m_clCBItems objects match what is needed
        assert m_clCBItems != null;
        if (m_clCBItems.length != lSubs.size()) {
            PlayerCBItem[] tempCB = new PlayerCBItem[lSubs.size()];

            //Fill with SpielerCBItem: Preferably reuse old ones
            for (int i = 0; i < tempCB.length; i++) {
                //Reuse
                if ((m_clCBItems.length > i) && (m_clCBItems[i] != null)) {
                    tempCB[i] = m_clCBItems[i];
                }
                //Create new
                else {
                    tempCB[i] = new PlayerCBItem("", 0f, null, true, true);
                }
            }

            //Empty reference and reset
            m_clCBItems = tempCB;
        }

        //Remove current Player if not a sub anymore
        if (selectedPlayer != null) {
            for (Player p : lSubs) {
                if (p.getPlayerId() == selectedPlayer.getPlayerId())
                    cbModel.addElement(createPlayerCBItem(m_clSelectedPlayer, selectedPlayer));
            }
        }

        //No Player
        cbModel.addElement(oNullPlayer);

        //Sort Player List
        PlayerCBItem[] cbItems = new PlayerCBItem[lSubs.size()];

        Player pp;
        for (int i = 0; i < lSubs.size(); i++) {
            pp = lSubs.get(i);
            if (pp.getPlayerId() != playerIDcorrespondingSub) {
                cbItems[i] = createPlayerCBItem(m_clCBItems[i], pp);
            }
        }

        cbItems = Arrays.stream(cbItems).filter(Objects::nonNull).toArray(PlayerCBItem[]::new);

        java.util.Arrays.sort(cbItems);

        for (PlayerCBItem cbItem : cbItems) {
            //All Other players
            cbModel.addElement(cbItem);
        }


        m_jcbPlayer.addItemListener(this);

        setTactic(getTactic(), selectedPlayer);
    }

    private void setTactic(byte tactic, @Nullable Player currentPlayer) {
        //remove listener
        m_jcbTactic.removeItemListener(this);

        //Update Tactic!
        initTaktik(currentPlayer);

        //Suche nach der Taktik
        Helper.setComboBoxFromID(m_jcbTactic, tactic);

        //Listener hinzu
        m_jcbTactic.addItemListener(this);
    }

    private void initLabel() {
        final Lineup lineup = HOVerwaltung.instance().getModel().getCurrentLineup();
        final int nextWeekTrain = TrainingPreviewPlayers.instance().getNextWeekTraining();

        if (m_iPositionID == IMatchRoleID.setPieces) {
            m_jlPosition.setText(getLangStr("match.setpiecestaker"));
        }
        else if (m_iPositionID == IMatchRoleID.captain) {
            m_jlPosition.setText(getLangStr("Spielfuehrer"));
        }
        else {
            final MatchRoleID position = lineup.getPositionById(m_iPositionID);

            if (position != null) {

                Font defaultFont = m_jlPosition.getFont();
                int fontSize = defaultFont.getSize();
                String fontFamily = defaultFont.getFamily();
                String hexColor = ImageUtilities.getHexColor(HOColorName.RED);

                final String  nameForPosition1 = "<html> <font family=" + fontFamily +  "size=" + fontSize + "pt>";
                final String  nameForPosition2 = "</font> <font family=" + fontFamily + "size=" + fontSize + "pt color=" + hexColor + ">&nbsp&nbsp";
                final String  nameForPosition3 = "</font></html>";
                final String nameForPosition = nameForPosition1 + getNameForLineupPosition(position.getPosition()) + nameForPosition2 + getTacticSymbol() + nameForPosition3;

                // Players on the lineup
                if (IMatchRoleID.aFieldMatchRoleID.contains(position.getId())) {
                    m_jlPosition.setText(nameForPosition);

                    if (MatchRoleID.isFullTrainPosition(position.getPosition(), nextWeekTrain)) {
                        this.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3,ThemeManager.getColor(HOColorName.LINEUP_FULL_TRAINING)));
                    }
                    else if (MatchRoleID.isPartialTrainPosition(position.getPosition(), nextWeekTrain)) {
                        this.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3,ThemeManager.getColor(HOColorName.LINEUP_PARTIAL_TRAINING)));
                    }
                    else{
                        this.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, defaultBorderColor));
                    }
                }
                // Subs
                else if (IMatchRoleID.aSubstitutesMatchRoleID.contains(position.getId())) {
                    m_jlPosition.setText(nameForPosition + " (#1)");
                }
                // Backups
                else {
                    m_jlPosition.setText(nameForPosition + " (#2)");
                }

                m_jlPosition.setToolTipText(MatchRoleID.getNameForPositionWithoutTactic(position.getPosition()));
            }
        }
        m_jlPosition.setFont(getFont().deriveFont(Font.BOLD));
    }

    private String getNameForLineupPosition(byte position) {
        if (UserParameter.instance().lineupPositionNamesSetting == POSITIONNAMES_SHORT) {
            return MatchRoleID.getShortNameForPosition(position);
        }
        return MatchRoleID.getNameForPosition(position);
    }

    private void initTaktik(@Nullable Player aktuellerPlayer) {
        m_jcbTactic.removeAllItems();

        switch (m_iPositionID) {
            case keeper -> m_jcbTactic.addItem(new CBItem(getLangStr("ls.player.behaviour.normal"), IMatchRoleID.NORMAL));
            case IMatchRoleID.rightBack, IMatchRoleID.leftBack, IMatchRoleID.leftWinger, IMatchRoleID.rightWinger -> {
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.towardsmiddle"), IMatchRoleID.TOWARDS_MIDDLE);
            }
            case IMatchRoleID.rightCentralDefender, IMatchRoleID.leftCentralDefender -> {
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.towardswing"), IMatchRoleID.TOWARDS_WING);
            }
            case IMatchRoleID.middleCentralDefender -> {
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
            }
            case IMatchRoleID.rightInnerMidfield, IMatchRoleID.leftInnerMidfield -> {
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.towardswing"), IMatchRoleID.TOWARDS_WING);
            }
            case IMatchRoleID.centralInnerMidfield -> {
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
            }
            case IMatchRoleID.rightForward, IMatchRoleID.leftForward -> {
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.towardswing"), IMatchRoleID.TOWARDS_WING);
            }
            case IMatchRoleID.centralForward -> {
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, getLangStr("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
            }
            default -> m_jcbTactic.addItem(new CBItem(getLangStr("ls.player.behaviour.normal"),
                    IMatchRoleID.NORMAL));
        }
    }

    private void addTactic(@Nullable Player currentPlayer, String text, byte playerPosition) {
        if (currentPlayer != null) {
            var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
            double rating;
            if ( matchMinute == null || matchMinute < 0 || matchMinute >= 120) {
                rating = ratingPredictionModel.getPlayerMatchAverageRating(currentPlayer, m_iPositionID, playerPosition);
            }
            else {
                rating = ratingPredictionModel.getPlayerRating(currentPlayer, m_iPositionID, playerPosition, matchMinute);
            }
            text += String.format(" (%.2f)", rating);
        }
        m_jcbTactic.addItem(new CBItem(text, playerPosition));
    }
    //-------------private-------------------------------------------------

    private PlayerCBItem createPlayerCBItem(PlayerCBItem item, @Nullable Player player) {
        if (player != null) {
            String playerName = player.getShortName();

            if (m_iPositionID == IMatchRoleID.setPieces) {
                var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
                var setPiecesRating = ratingPredictionModel.getPlayerSetPiecesStrength(player);
                item.setValues(playerName, (float) setPiecesRating, player, true);
                return item;
            } else if (m_iPositionID == IMatchRoleID.captain) {
                item.setValues(playerName,
                        Helper.round(
                                HOVerwaltung.instance().getModel().getCurrentLineup().getAverageExperience(player.getPlayerId()),
                                core.model.UserParameter.instance().nbDecimals),
                        player, true);
                return item;
            } else {
                var lineup = HOVerwaltung.instance().getModel().getCurrentLineup();
                final var position = lineup.getPositionById(m_iPositionID);

                if (position != null) {
                    var roleId = position.getRoleId();

                    switch (roleId) {
                        case substGK1, substGK2 -> roleId = keeper;
                        case substCD1, substCD2 -> roleId = leftCentralDefender;
                        case substWB1, substWB2 -> roleId = leftBack;
                        case substWI1, substWI2 -> roleId = leftWinger;
                        case substIM1, substIM2 -> roleId = leftInnerMidfield;
                        case substFW1, substFW2 -> roleId = leftForward;
                    }

                    double value=0.;
                    boolean bestPosition = false;
                    if ( roleId != substXT2 && roleId != substXT1) {
                        var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
                        if (this.matchMinute == null || this.matchMinute < 0 || this.matchMinute > 120) {
                            value = ratingPredictionModel.getPlayerMatchAverageRating(player, roleId, position.getBehaviour(), this.m_useWeatherImpact ? this.m_weather : Weather.UNKNOWN);
                        } else {
                            value = ratingPredictionModel.getPlayerRating(player, roleId, position.getBehaviour(), this.matchMinute, this.m_useWeatherImpact ? this.m_weather : Weather.UNKNOWN);
                        }
                        var alternativePositions = player.getAlternativeBestPositions();
                        for (byte altPos : alternativePositions) {
                            if (altPos == position.getPosition()) {
                                bestPosition = true;
                                break;
                            }
                        }
                    }

                    item.setValues(playerName, (float) value, player, bestPosition);
                    return item;
                }

            }
        }
        return oNullPlayer;
    }

    public int getiSelectedPlayerId() {
        return iSelectedPlayerId;
    }


    // return a tactic symbol if a player is offensive, defensive, towards wings etc.
    // used for lineup export
    public String getTacticSymbol() {
        byte tactic = getTactic();
        int positionsID = getPositionsID();
        int playerID = getiSelectedPlayerId();
        var orientation = UserParameter.instance().lineupOrientationSetting;
        String symbol = "";
        if (playerID != -1) {
            if (tactic == IMatchRoleID.OFFENSIVE) {
                symbol = orientation == GOALKEEPER_AT_TOP ? "▼" : "▲";
            } else if (tactic == IMatchRoleID.DEFENSIVE) {
                symbol = orientation == GOALKEEPER_AT_TOP ? "▲" : "▼";
            } else if (tactic == IMatchRoleID.TOWARDS_MIDDLE) {
                if (positionsID == IMatchRoleID.rightBack
                        || positionsID == IMatchRoleID.rightWinger) {
                    symbol = orientation == GOALKEEPER_AT_TOP ? "▶" : "◀";
                } else {
                    symbol = orientation == GOALKEEPER_AT_TOP ? "◀" : "▶";
                }
            } else if (tactic == IMatchRoleID.TOWARDS_WING) {
                if (positionsID == IMatchRoleID.rightCentralDefender
                        || positionsID == IMatchRoleID.rightInnerMidfield
                        || positionsID == IMatchRoleID.rightForward) {
                    symbol = orientation == GOALKEEPER_AT_TOP ? "◀" : "▶";
                } else {
                    symbol = orientation == GOALKEEPER_AT_TOP ? "▶" : "◀";
                }
            }
        }
        return symbol;
    }

    /**
     * Exposes the player combo box to reset the swap button if needed.
     *
     * @return the player {@link JComboBox}.
     */
    protected JComboBox<PlayerCBItem> getPlayerComboBox() {
        return m_jcbPlayer;
    }

    public LayoutManager getSwapLayout() {
        return layout;
    }

    public void addSwapItem(Component c) {
        jlp.add(c, layerIndex);
    }

    public void addAssistantOverlay(LineupAssistantSelectorOverlay overlay) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 3;
        jlp.add(overlay, constraints, layerIndex+1);
        repaint();
    }

    public void removeAssistantOverlay(LineupAssistantSelectorOverlay overlay) {
        jlp.remove(overlay);
        repaint();
    }


    public void addCaptainIcon(){

        JLabel jlCaptain = new JLabel();
        jlCaptain.setIcon(ImageUtilities.getSmileyIcon("smiley-coach"));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new java.awt.Insets(5, 0, 0, 8);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;

        jlp.add(jlCaptain, constraints, layerIndex);

    }

    public void addSetPiecesIcon(){

        JLabel jlSetPieces = new JLabel();
        jlSetPieces.setIcon(ImageUtilities.getSetPiecesIcon(21, 21));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new java.awt.Insets(2, 0, 0, 8);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;

        jlp.add(jlSetPieces, constraints, layerIndex);

    }

    private String getLangStr(String key) {
        return TranslationFacility.tr(key);
    }

}
