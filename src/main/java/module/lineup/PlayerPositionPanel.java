package module.lineup;

import core.constants.player.PlayerSkill;
import core.datatype.CBItem;
import core.gui.HOMainFrame;
import core.gui.Updateable;
import core.gui.comp.panel.ImagePanel;
import core.gui.model.SpielerCBItem;
import core.gui.model.PlayerCBItemRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.rating.RatingPredictionManager;
import core.training.TrainingPreviewPlayers;
import core.util.Helper;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;


/**
 * Panel in which the player position is displayed and can be changed
 */
class PlayerPositionPanel extends ImagePanel implements ItemListener, FocusListener {

    private static final SpielerCBItem oNullPlayer = new SpielerCBItem("", 0f, null, false, true);
    private static final Color defaultBorderColor = ThemeManager.getColor(HOColorName.PLAYER_POSITION_PANEL_BORDER);

    //~ Instance fields ----------------------------------------------------------------------------
    private final JComboBox<SpielerCBItem> m_jcbPlayer = new JComboBox<>();
    private final JComboBox<CBItem> m_jcbTactic = new JComboBox<>();
    private final JLabel m_jlPosition = new JLabel();
    private final JLabel m_jlPlayer = new JLabel();
    private final SpielerCBItem m_clSelectedPlayer = new SpielerCBItem("", 0f, null, false, true);
    private final Updateable m_clUpdater;
    private SpielerCBItem[] m_clCBItems = new SpielerCBItem[0];
    private final int m_iPositionID;

    private int playerId = -1;
    private int tacticOrder = -1;

    private final GridBagLayout layout = new GridBagLayout();
    private final JLayeredPane jlp = new JLayeredPane();
    private final int layerIndex = 0;

    //constructor
    protected PlayerPositionPanel(Updateable updater, int positionsID) {
        super(false);

        m_clUpdater = updater;
        m_iPositionID = positionsID;

        setOpaque(true);

        initTaktik(null);
        initLabel();
        initComponents();
    }

    protected int getPositionsID() {
        return m_iPositionID;
    }

    private byte getTactic() {
        CBItem cbTactic = (CBItem) m_jcbTactic.getSelectedItem();
        return (cbTactic != null) ? (byte)cbTactic.getId() : IMatchRoleID.NORMAL;
    }

    /**
     * Returns the current player at this position (could be null)
     */
    public @Nullable Player getSelectedPlayer() {
        final Object obj = m_jcbPlayer.getSelectedItem();

        if (obj instanceof SpielerCBItem) {
            return ((SpielerCBItem) obj).getPlayer();
        }

        return null;
    }


    @Override
    public void focusGained(FocusEvent event) {
        if (getSelectedPlayer() != null) {
            HOMainFrame.instance().setActualSpieler(getSelectedPlayer());
        }
    }

    @Override
    public void focusLost(FocusEvent event) { }

    /**
     * Create the components, don't forget the CB for the players and the listener!
     */
    private void initComponents() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, defaultBorderColor));

        jlp.setLayout(layout);

        // No gaps around the layeredpane.
        FlowLayout fl = new FlowLayout();
        fl.setHgap(0);
        fl.setVgap(0);
        fl.setAlignment(FlowLayout.CENTER);
        setLayout(fl);

        constraints.weightx = 1.0;

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
        m_jcbPlayer.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));

        //Show only if more than one tactic is possible
        if (m_jcbTactic.getItemCount() > 1) {

            constraints.insets = new Insets(2, 4, 0, 4);
            jlp.add(m_jcbPlayer, constraints, layerIndex);

            constraints.gridy = 2;
            constraints.insets = new Insets(2, 4, 5, 4);
            m_jcbTactic.setBackground(m_jcbPlayer.getBackground());
            jlp.add(m_jcbTactic, constraints, layerIndex);
        }
        else {

            constraints.insets = new Insets(2, 4, 5, 4);
            jlp.add(m_jcbPlayer, constraints, layerIndex);
        }

        add(jlp);
    }

    //-------------Listener------------------------------------------------
    @Override
    public void itemStateChanged(java.awt.event.ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            final Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

            final Player player = getSelectedPlayer();
            setPlayerTooltip(player);

            //set player
            if (itemEvent.getSource().equals(m_jcbPlayer)) {
                //set pieces
                if (m_iPositionID == IMatchRoleID.setPieces) {
                    if (player != null) {
                        lineup.setKicker(player.getSpielerID());
                    } else {
                        lineup.setKicker(0);
                    }
                }
                //captain
                else if (m_iPositionID == IMatchRoleID.captain) {
                    if (player != null) {
                        lineup.setKapitaen(player.getSpielerID());
                    } else {
                        lineup.setKapitaen(0);
                    }
                }
                //Others
                else {
                    if (player != null) {
                        lineup.setSpielerAtPosition(m_iPositionID, player.getSpielerID());
                    } else {
                        lineup.setSpielerAtPosition(m_iPositionID, 0);
                    }
                    // adjust backup players
                    lineup.adjustBackupPlayers();
                }

                //Adjust tactic values
                setTaktik(getTactic(), player);
            } else if (itemEvent.getSource().equals(m_jcbTactic)) {
                lineup.getPositionById(m_iPositionID).setTaktik(getTactic());
            }

            if (player != null) {
                HOMainFrame.instance().setActualSpieler(player);
            }

            //Update all other positions
            m_clUpdater.update();
        }
    }

    private void setPlayerTooltip(@Nullable Player player) {
                if (player != null) {
                        String playerName = player.getFullName();
                        setToolTipText(playerName);
                        m_jlPlayer.setToolTipText(playerName);
                        m_jlPosition.setToolTipText(playerName);
                        m_jcbPlayer.setToolTipText(playerName);
                    }
            }

    /**
     * Update the list of player in the ComboBox except for backup
     */
    public void refresh(List<Player> player, List<Player> selectPlayer, List<Player> assitPlayer) {
        Player selectedPlayer = null;
        playerId = -1;
        if (m_iPositionID == IMatchRoleID.setPieces) {
            selectedPlayer = HOVerwaltung.instance().getModel().getCurrentPlayer(HOVerwaltung.instance()
                    .getModel()
                    .getLineupWithoutRatingRecalc()
                    .getKicker());
            if (selectedPlayer != null) {
                playerId = selectedPlayer.getSpielerID();
            }
            tacticOrder = -1;

            // Filter keeper from the player vector (can't be sp taker)
            // Make sure the incoming player list is not modified, it
            // seems to visit the captain position later.

            Player keeper = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().
                    getPlayerByPositionID(IMatchRoleID.keeper);
            if (keeper != null) {
                Vector<Player> tmpPlayer = new Vector<Player>(player.size() - 1);
                for (int i = 0; i < player.size(); i++) {
                    if (keeper.getSpielerID() != player.get(i).getSpielerID()) {
                        tmpPlayer.add(player.get(i));
                    }
                }
                player = tmpPlayer;
            }
        } else if (m_iPositionID == IMatchRoleID.captain) {
            selectedPlayer = HOVerwaltung.instance().getModel().getCurrentPlayer(HOVerwaltung.instance()
                    .getModel()
                    .getLineupWithoutRatingRecalc()
                    .getKapitaen());
            if (selectedPlayer != null) {
                playerId = selectedPlayer.getSpielerID();
            }
            tacticOrder = -1;
        } else {
            //Get currently setup player
            final MatchRoleID position = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc()
                    .getPositionById(m_iPositionID);

            if (position != null) {
                selectedPlayer = HOVerwaltung.instance().getModel().getCurrentPlayer(position
                        .getSpielerId());

                if (selectedPlayer != null) {
                    m_jcbPlayer.setEnabled(true); // To be sure
                    playerId = selectedPlayer.getSpielerID();
                } else {
                    // We want to disable the player selection box if there is already 11 players on the field and this is an on field position.
                    if ((!HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().hasFreePosition()) &&
                            (m_iPositionID >= IMatchRoleID.keeper) && (m_iPositionID < IMatchRoleID.startReserves)) {
                        m_jcbPlayer.setEnabled(false);
                    } else {
                        // And enable empty positions if there is room in the lineup
                        m_jcbPlayer.setEnabled(true);
                    }
                }
                tacticOrder = position.getTaktik();
                setTaktik(position.getTaktik(), selectedPlayer);
            }
        }

        setSpielerListe(player, selectedPlayer);

        for (int i = 0; i < m_jcbPlayer.getModel().getSize(); i++) {
            SpielerCBItem obj = m_jcbPlayer.getItemAt(i);
            if (obj != null) {
                if (obj.getPlayer() != null) {
                    obj.getEntry().setIsSelect(false);
                    obj.getEntry().setIsAssit(false);
                }
            }
        }

        if (selectPlayer != null && assitPlayer != null) {
            for (int i = 0; i < m_jcbPlayer.getModel().getSize(); i++) {
                SpielerCBItem obj = m_jcbPlayer.getItemAt(i);
                boolean isInLineup = false;
                if (obj != null) {
                    if (obj.getPlayer() != null) {
                        for (Player value : selectPlayer) {
                            if (obj.getPlayer().getSpielerID() == value.getSpielerID()) {
                                obj.getEntry().setIsSelect(true);
                                isInLineup = true;
                                break;
                            }
                        }
                        if (!isInLineup) {
                            for (Player value : assitPlayer) {
                                if (obj.getPlayer().getSpielerID() == value.getSpielerID()) {
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


    /**
     * Update the list of player in the Backup ComboBox
     * Only authorized player are the one already listed as subs
     */
    public void refresh2(List<Player> lPlayers, int playerIDcorrespondingSub) {
        Player selectedPlayer = null;
        playerId = -1;

        //Get currently setup player in that position
        final MatchRoleID position = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getPositionById(m_iPositionID);
        if (position != null) {
            selectedPlayer = HOVerwaltung.instance().getModel().getCurrentPlayer(position.getSpielerId());
            setTaktik(position.getTaktik(), selectedPlayer);
        }

        setSpielerListe2(lPlayers, selectedPlayer, playerIDcorrespondingSub);
        initLabel();
        repaint();
    }

    /**
     * Sets the list of possible players for this position and the currently selected player
     */
    protected void setSpielerListe(List<Player> playerListe, @Nullable Player aktuellerPlayer) {
        //Listener entfernen
        m_jcbPlayer.removeItemListener(this);

        final DefaultComboBoxModel cbmodel = ((DefaultComboBoxModel) m_jcbPlayer.getModel());

        //Remove all items
        cbmodel.removeAllElements();

        //Ensure the number of m_clCBItems objects match what is needed

        if (m_clCBItems.length != playerListe.size()) {
            SpielerCBItem[] tempCB = new SpielerCBItem[playerListe.size()];

            //Fill with SpielerCBItem: Preferably reuse old ones
            for (int i = 0; i < tempCB.length; i++) {
                //Reuse
                if ((m_clCBItems.length > i) && (m_clCBItems[i] != null)) {
                    tempCB[i] = m_clCBItems[i];

                    //HOLogger.instance().log(getClass(), "Use old SpielerCBItem " + this.m_iPositionsID );
                }
                //Create new
                else {
                    tempCB[i] = new SpielerCBItem("", 0f, null, true, true);

                    //HOLogger.instance().log(getClass(), "Create new SpielerCBItem " + this.m_iPositionsID );
                }
            }

            //Empty reference and reset
            m_clCBItems = null;
            m_clCBItems = tempCB;
        }

        //Current Player
        cbmodel.addElement(createSpielerCBItem(m_clSelectedPlayer, aktuellerPlayer));

        //No Player
        cbmodel.addElement(oNullPlayer);

        //Sort Player List
        SpielerCBItem[] cbItems = new SpielerCBItem[playerListe.size()];

        for (int i = 0; i < playerListe.size(); i++) {
            cbItems[i] = createSpielerCBItem(m_clCBItems[i], ((Player) playerListe.get(i)));
        }

        java.util.Arrays.sort(cbItems);

        for (int i = 0; i < cbItems.length; i++) {
            //All Other players
            cbmodel.addElement(cbItems[i]);
        }

        //Listener wieder hinzu
        m_jcbPlayer.addItemListener(this);

        //Minimized
        if ((m_clSelectedPlayer != null) && (m_clSelectedPlayer.getPlayer() != null)) {
            m_jlPlayer.setText(m_clSelectedPlayer.getPlayer().getShortName());
            m_jlPlayer.setIcon(ImageUtilities.getJerseyIcon(HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getPositionBySpielerId(m_clSelectedPlayer.getPlayer().getSpielerID()),
                    m_clSelectedPlayer.getPlayer().getTrikotnummer()));
        } else {
            m_jlPlayer.setText("");
            m_jlPlayer.setIcon(null);
        }

        setTaktik(getTactic(), aktuellerPlayer);

        setPlayerTooltip(m_clSelectedPlayer.getPlayer());
    }


    /**
     * Sets the list of possible players for backup players
     */
    protected void setSpielerListe2(List<Player> allPlayers, @Nullable Player selectedPlayer, int playerIDcorrespondingSub) {

        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

        // list of all players currently set as subs
        List<Player> lSubs = new ArrayList<>();

        for (Player player : allPlayers) {
            if (lineup.isPlayerASub(player.getSpielerID())) {
                lSubs.add(player);
            }
        }

        //Listener entfernen
        m_jcbPlayer.removeItemListener(this);

        final DefaultComboBoxModel cbmodel = ((DefaultComboBoxModel) m_jcbPlayer.getModel());

        //Remove all items
        cbmodel.removeAllElements();

        //Ensure the number of m_clCBItems objects match what is needed
        if (m_clCBItems.length != lSubs.size()) {
            SpielerCBItem[] tempCB = new SpielerCBItem[lSubs.size()];

            //Fill with SpielerCBItem: Preferably reuse old ones
            for (int i = 0; i < tempCB.length; i++) {
                //Reuse
                if ((m_clCBItems.length > i) && (m_clCBItems[i] != null)) {
                    tempCB[i] = m_clCBItems[i];
                }
                //Create new
                else {
                    tempCB[i] = new SpielerCBItem("", 0f, null, true, true);
                }
            }

            //Empty reference and reset
            m_clCBItems = null;
            m_clCBItems = tempCB;
        }

        //Remove current Player if not a sub anymore
        if (selectedPlayer != null) {
            for (Player p : lSubs) {
                if (p.getSpielerID() == selectedPlayer.getSpielerID())
                    cbmodel.addElement(createSpielerCBItem(m_clSelectedPlayer, selectedPlayer));
            }
        }

        //No Player
        cbmodel.addElement(oNullPlayer);

        //Sort Player List
        SpielerCBItem[] cbItems = new SpielerCBItem[lSubs.size()];

        Player pp;
        for (int i = 0; i < lSubs.size(); i++) {
            pp = lSubs.get(i);
            if (pp.getSpielerID() != playerIDcorrespondingSub) {
                cbItems[i] = createSpielerCBItem(m_clCBItems[i], pp);
            }
        }

        cbItems = Arrays.stream(cbItems).filter(Objects::nonNull).toArray(SpielerCBItem[]::new);

        java.util.Arrays.sort(cbItems);

        for (int i = 0; i < cbItems.length; i++) {
            //All Other players
            cbmodel.addElement(cbItems[i]);
        }

        //Listener wieder hinzu
        m_jcbPlayer.addItemListener(this);

        //Minimized
        if ((m_clSelectedPlayer != null) && (m_clSelectedPlayer.getPlayer() != null)) {
            m_jlPlayer.setText(m_clSelectedPlayer.getPlayer().getShortName());
            m_jlPlayer.setIcon(ImageUtilities.getJerseyIcon(HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getPositionBySpielerId(m_clSelectedPlayer.getPlayer().getSpielerID()),
                    m_clSelectedPlayer.getPlayer().getTrikotnummer()));
        } else {
            m_jlPlayer.setText("");
            m_jlPlayer.setIcon(null);
        }

        setTaktik(getTactic(), selectedPlayer);
    }

    /**
     * Set the current tactic
     */
    private void setTaktik(byte taktik, @Nullable Player aktuellerPlayer) {
        //remove listener
        m_jcbTactic.removeItemListener(this);

        //Update Tactic!
        initTaktik(aktuellerPlayer);

        //Suche nach der Taktik
        Helper.markierenComboBox(m_jcbTactic, taktik);

        //Listener hinzu
        m_jcbTactic.addItemListener(this);
    }

    /**
     * Setzt das Label
     */
    private void initLabel() {
        final Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        final int nextWeekTrain = TrainingPreviewPlayers.instance().getNextWeekTraining();

        if (m_iPositionID == IMatchRoleID.setPieces) {
            m_jlPosition.setText(HOVerwaltung.instance().getLanguageString("match.setpiecestaker"));
        } else if (m_iPositionID == IMatchRoleID.captain) {
            m_jlPosition.setText(HOVerwaltung.instance().getLanguageString("Spielfuehrer"));
        } else {
            final MatchRoleID position = lineup.getPositionById(m_iPositionID);

            if (position != null) {
                final String nameForPosition = MatchRoleID.getNameForPosition(position.getPosition());

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
            }
            m_jlPosition.setFont(getFont().deriveFont(Font.BOLD));
        }

        //Minimized
        if ((m_clSelectedPlayer != null) && (m_clSelectedPlayer.getPlayer() != null)) {
            m_jlPlayer.setText(m_clSelectedPlayer.getPlayer().getShortName());
            m_jlPlayer.setIcon(ImageUtilities.getJerseyIcon(lineup.getPositionBySpielerId(m_clSelectedPlayer.getPlayer().getSpielerID()),
                    m_clSelectedPlayer.getPlayer()
                            .getTrikotnummer()));
        } else {
            m_jlPlayer.setText("");
            m_jlPlayer.setIcon(null);
        }
    }

    /**
     * Setzt die Taktik je nach MatchRoleID
     */
    private void initTaktik(@Nullable Player aktuellerPlayer) {
        m_jcbTactic.removeAllItems();

        switch (m_iPositionID) {
            case IMatchRoleID.keeper: {
                m_jcbTactic.addItem(new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),
                        IMatchRoleID.NORMAL));
                break;
            }

            case IMatchRoleID.rightBack:
            case IMatchRoleID.leftBack: {
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardsmiddle"), IMatchRoleID.TOWARDS_MIDDLE);
                break;
            }

            case IMatchRoleID.rightCentralDefender:
            case IMatchRoleID.leftCentralDefender: {
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing"), IMatchRoleID.TOWARDS_WING);
                break;
            }

            case IMatchRoleID.middleCentralDefender: {
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
                break;
            }

            case IMatchRoleID.rightInnerMidfield:
            case IMatchRoleID.leftInnerMidfield: {
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing"), IMatchRoleID.TOWARDS_WING);
                break;
            }

            case IMatchRoleID.centralInnerMidfield: {
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
                break;
            }

            case IMatchRoleID.leftWinger:
            case IMatchRoleID.rightWinger: {
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardsmiddle"), IMatchRoleID.TOWARDS_MIDDLE);
                break;
            }

            case IMatchRoleID.rightForward:
            case IMatchRoleID.leftForward: {
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing"), IMatchRoleID.TOWARDS_WING);
                break;
            }

            case IMatchRoleID.centralForward: {
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
                addTactic(aktuellerPlayer, HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
                break;
            }


            case IMatchRoleID.substCD1:
            case IMatchRoleID.substCD2:
            case IMatchRoleID.substFW1:
            case IMatchRoleID.substFW2:
            case IMatchRoleID.substIM1:
            case IMatchRoleID.substIM2:
            case IMatchRoleID.substGK1:
            case IMatchRoleID.substGK2:
            case IMatchRoleID.substWI1:
            case IMatchRoleID.substWI2: {
                m_jcbTactic.addItem(new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),
                        IMatchRoleID.NORMAL));
                break;
            }

            default:
                m_jcbTactic.addItem(new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),
                        IMatchRoleID.NORMAL));
        }
    }


    private void addTactic(Player currentPlayer, String text, byte playerPosition) {
        if (currentPlayer != null) {
            text += " ("
                    + currentPlayer.calcPosValue(MatchRoleID.getPosition(m_iPositionID,
                    playerPosition),
                    true) + ")";
        }

        m_jcbTactic.addItem(new CBItem(text, playerPosition));
    }
    //-------------private-------------------------------------------------

    /**
     * Generiert ein SpielerCBItem für einen Player
     */
    private SpielerCBItem createSpielerCBItem(SpielerCBItem item, Player player) {
        if (player != null) {
            String spielerName = player.getShortName();

            if (m_iPositionID == IMatchRoleID.setPieces) {
                item.setValues(spielerName,
                        player.getSPskill()
                                + player.getSub4Skill(PlayerSkill.SET_PIECES)
                                + RatingPredictionManager.getLoyaltyHomegrownBonus(player),
                        player, false);
                return item;
            } else if (m_iPositionID == IMatchRoleID.captain) {
                item.setValues(spielerName,
                        Helper.round(
                                HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getAverageExperience(player.getSpielerID()),
                                core.model.UserParameter.instance().nbDecimals),
                        player, false);
                return item;
            } else {
                final MatchRoleID position = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getPositionById(m_iPositionID);

                float value = player.calcPosValue(position.getPosition(), true);

                byte[] alternativePositions = player.getAlternativePositions();
                boolean alternativePosition = false;
                for (byte altPos : alternativePositions) {
                    if (altPos == position.getPosition()) {
                        alternativePosition = true;
                        break;
                    }
                }

                if (position != null) {
                    item.setValues(spielerName, value, player, alternativePosition);
                    return item;
                }

                return oNullPlayer;

            }
        }
        //Kein Player
        return oNullPlayer;

    }

    public int getPlayerId() {
        return playerId;
    }

    public int getTacticOrder() {
        return tacticOrder;
    }

    // return a tactic symbol if a player is offensive, defensive, towards wings etc.
    // used for lineup export
    public String getTacticSymbol() {
        byte tactic = getTactic();
        int positionsID = getPositionsID();
        int playerID = getPlayerId();
        String symbol = "";
        if (tactic == IMatchRoleID.OFFENSIVE && playerID != -1) {
            symbol = "▼";
        } else if (tactic == IMatchRoleID.DEFENSIVE && playerID != -1) {
            symbol = "▲";
        } else if (tactic == IMatchRoleID.TOWARDS_MIDDLE && playerID != -1) {
            if (positionsID == IMatchRoleID.rightBack
                    || positionsID == IMatchRoleID.rightWinger) {
                symbol = "▶";
            } else {
                symbol = "◀";
            }
        } else if (tactic == IMatchRoleID.TOWARDS_WING && playerID != -1) {
            if (positionsID == IMatchRoleID.rightCentralDefender
                    || positionsID == IMatchRoleID.rightInnerMidfield
                    || positionsID == IMatchRoleID.rightForward) {
                symbol = "◀︎";
            } else {
                symbol = "▶";
            }
        }
        return symbol;
    }

    /**
     * Exposes the player combo box to reset the swap button if needed.
     *
     * @return the player {@link JComboBox}.
     */
    protected JComboBox getPlayerComboBox() {
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

}
