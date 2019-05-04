package module.lineup;

import core.constants.player.PlayerSkill;
import core.datatype.CBItem;
import core.gui.HOMainFrame;
import core.gui.Updateable;
import core.gui.comp.entry.SpielerLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.model.SpielerCBItem;
import core.gui.model.SpielerCBItemRenderer;
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

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;


/**
 * Panel in which the player position is displayed and can be changed
 */
class PlayerPositionPanel extends ImagePanel implements ItemListener, FocusListener
{
    //~ Static fields/initializers -----------------------------------------------------------------
	private static final long serialVersionUID = 3121389904504282953L;

	protected static int PLAYER_POSITION_PANEL_WIDTH = Helper.calcCellWidth(160);
	protected static int PLAYER_POSITION_PANEL_HEIGHT_FULL = Helper.calcCellWidth(80);
	// Used for positions with no tactics box
	protected static int PLAYER_POSITION_PANEL_HEIGHT_REDUCED = Helper.calcCellWidth(50);


	protected static int MINI_PLAYER_POSITION_PANEL_WIDTH = Helper.calcCellWidth(120);
	protected static int MINI_PLAYER_POSITION_PANEL_HEIGHT = Helper.calcCellWidth(32);

	private static SpielerCBItem m_clNullSpieler = new SpielerCBItem("", 0f, null, true);

    //~ Instance fields ----------------------------------------------------------------------------
    private final JComboBox m_jcbPlayer = new JComboBox();
    private final JComboBox m_jcbTactic = new JComboBox();
    private final JLabel m_jlPosition = new JLabel();
    //Für Minimized
    private final JLabel m_jlPlayer = new JLabel();
    private final SpielerCBItem m_clSelectedPlayer = new SpielerCBItem("", 0f, null, true);
    private Updateable m_clUpdater;
    private SpielerCBItem[] m_clCBItems = new SpielerCBItem[0];
    private boolean m_bMinimize;
    private int m_iPositionID;

	private int playerId = -1;
	private int tacticOrder = -1;

	private final GridBagLayout layout = new GridBagLayout();
	private JLayeredPane jlp = new JLayeredPane();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerPositionsPanel object.
     */
    protected PlayerPositionPanel(Updateable updater, int positionsID) {
        this(updater, positionsID, false, false);
    }

    /**
     * Creates a new SpielerPositionsPanel object.
     */
    protected PlayerPositionPanel(Updateable updater, int positionsID, boolean print,
                                 boolean minimize) {
        super(print || minimize);

        m_clUpdater = updater;
        m_iPositionID = positionsID;
        m_bMinimize = minimize;

        setOpaque(true);

        initTaktik(null);
        initLabel();
        initComponents(true);
    }

    //~ Methods ------------------------------------------------------------------------------------

    //--------------------------------------------------------

    /**
     * Gibt die PositionsID zurück
     */
    protected int getPositionsID() {
        return m_iPositionID;
    }

    /**
     * Gibt den aktuellen Player auf dieser Position zurück, oder null, wenn keiner gewählt wurde
     */
    public Player getSelectedPlayer() {
        final Object obj = m_jcbPlayer.getSelectedItem();

        if ((obj != null) && obj instanceof SpielerCBItem) {
            return ((SpielerCBItem) obj).getSpieler();
        }

        return null;
    }

    /**
     * Gibt die Taktik an
     */
    private byte getTactic() {
        return (byte) ((CBItem) m_jcbTactic.getSelectedItem()).getId();
    }

    @Override
	public void focusGained(FocusEvent event) {
        if (getSelectedPlayer() != null) {
            HOMainFrame.instance().setActualSpieler(getSelectedPlayer());
        }
    }

    @Override
	public void focusLost(FocusEvent event) {
        //nix
    }

    /**
     * Erzeugt die Komponenten, Die CB für die Player und den Listener nicht vergessen!
     */
    private void initComponents(boolean aenderbar) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0;
        constraints.insets = new Insets(1, 2, 1, 2);

        //Minimiert
        if (m_bMinimize) {
            // This is the realm of the miniposframe, no jlp...
        	setLayout(layout);
        	setBorder(javax.swing.BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.LINEUP_POS_MIN_BORDER)));//Color.lightGray));
            setBackground(ThemeManager.getColor(HOColorName.LINEUP_POS_MIN_BG));//Color.WHITE);

            constraints.gridx = 0;
            constraints.gridy = 0;
            add(m_jlPosition, constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            add(m_jlPlayer, constraints);

            setPreferredSize(new Dimension(MINI_PLAYER_POSITION_PANEL_WIDTH,MINI_PLAYER_POSITION_PANEL_HEIGHT));
        }
        //Normal
        else {
        	jlp.setLayout(layout);
        	// No gaps around the layeredpane.
        	FlowLayout fl = new FlowLayout();
        	fl.setHgap(0);
        	fl.setVgap(0);
        	fl.setAlignment(FlowLayout.CENTER);
        	setLayout(fl);

        	setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            jlp.add(m_jlPosition, constraints, 1);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            m_jcbPlayer.addFocusListener(this);
            m_jcbPlayer.setMaximumRowCount(15);
            m_jcbPlayer.setRenderer(new SpielerCBItemRenderer());
            jlp.add(m_jcbPlayer, constraints, 1);

            if (!aenderbar) {
                m_jcbPlayer.setEnabled(false);
            }

            m_jcbPlayer.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));// Color.white

            //Nur anzeigen, wenn mehr als eine Taktik möglich ist
            if (m_jcbTactic.getItemCount() > 1) {
                constraints.gridx = 0;
                constraints.gridy = 2;
                constraints.gridwidth = 2;
                if (!aenderbar) {
                    m_jcbTactic.setEnabled(false);
                }

                m_jcbTactic.setBackground(m_jcbPlayer.getBackground());
                jlp.add(m_jcbTactic, constraints, 1);
                setPreferredSize(new Dimension(PLAYER_POSITION_PANEL_WIDTH,PLAYER_POSITION_PANEL_HEIGHT_FULL));
            } else {
                setPreferredSize(new Dimension(PLAYER_POSITION_PANEL_WIDTH, PLAYER_POSITION_PANEL_HEIGHT_REDUCED));
            }
            jlp.setPreferredSize(getPreferredSize());
            add(jlp);
        }
    }

    //-------------Listener------------------------------------------------
    @Override
	public void itemStateChanged(java.awt.event.ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            final Lineup aufstellung = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

            final Player player = getSelectedPlayer();

            //set player
            if (itemEvent.getSource().equals(m_jcbPlayer)) {
                //set pieces
                if (m_iPositionID == IMatchRoleID.setPieces) {
                    if (player != null) {
                        aufstellung.setKicker(player.getSpielerID());
                    } else {
                        aufstellung.setKicker(0);
                    }
                }
                //captain
                else if (m_iPositionID == IMatchRoleID.captain) {
                    if (player != null) {
                        aufstellung.setKapitaen(player.getSpielerID());
                    } else {
                        aufstellung.setKapitaen(0);
                    }
                }
                //Others
                else {
                    if (player != null) {
                        aufstellung.setSpielerAtPosition(m_iPositionID, player.getSpielerID());
                    } else {
                        aufstellung.setSpielerAtPosition(m_iPositionID, 0);
                    }
                }

                //Adjust colors
                if (player != null) {
                    m_jcbPlayer.setForeground(SpielerLabelEntry.getForegroundForSpieler(player));
                }

                //Taktikwerte anpassen
                setTaktik(getTactic(), player);
            } else if (itemEvent.getSource().equals(m_jcbTactic)) {
                aufstellung.getPositionById(m_iPositionID).setTaktik(getTactic());
            }

            //Adjust tactic values
            if (player != null) {
                HOMainFrame.instance().setActualSpieler(player);
            }

            //Update all other positions
            m_clUpdater.update();
        }
    }

    /**
     * Update the list of player in the ComboBox except for backup
     */
    public void refresh(List<Player> player) {
        Player aktuellerPlayer = null;
		playerId = -1;
        if (m_iPositionID == IMatchRoleID.setPieces) {
            aktuellerPlayer = HOVerwaltung.instance().getModel().getSpieler(HOVerwaltung.instance()
                                                                                         .getModel()
                                                                                         .getLineupWithoutRatingRecalc()
                                                                                         .getKicker());
			if (aktuellerPlayer !=null) {
				playerId = aktuellerPlayer.getSpielerID();
			}
			tacticOrder=-1;

			// Filter keeper from the player vector (can't be sp taker)
			// Make sure the incoming player list is not modified, it
			// seems to visit the captain position later.

			Player keeper = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().
					getPlayerByPositionID(IMatchRoleID.keeper);
			if (keeper != null) {
				Vector<Player> tmpPlayer = new Vector<Player>(player.size() -1);
				for (int i = 0; i < player.size(); i++) {
					if ( keeper.getSpielerID() != player.get(i).getSpielerID()) {
						tmpPlayer.add(player.get(i));
					}
				}
				player = tmpPlayer;
			}
        } else if (m_iPositionID == IMatchRoleID.captain) {
            aktuellerPlayer = HOVerwaltung.instance().getModel().getSpieler(HOVerwaltung.instance()
                                                                                         .getModel()
                                                                                         .getLineupWithoutRatingRecalc()
                                                                                         .getKapitaen());
			if (aktuellerPlayer !=null) {
				playerId = aktuellerPlayer.getSpielerID();
			}
			tacticOrder=-1;
        } else {
            //Get currently setup player
            final MatchRoleID position = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc()
                                                         .getPositionById(m_iPositionID);

            if (position != null) {
                aktuellerPlayer = HOVerwaltung.instance().getModel().getSpieler(position
                                                                                 .getSpielerId());

				if (aktuellerPlayer !=null) {
					m_jcbPlayer.setEnabled(true); // To be sure
					playerId = aktuellerPlayer.getSpielerID();
				} else {
					// We want to disable the player selection box if there is already 11 players on the field and this is an on field position.
					if ((HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().hasFreePosition() == false) &&
							(m_iPositionID >= IMatchRoleID.keeper) && (m_iPositionID < IMatchRoleID.startReserves)) {
						m_jcbPlayer.setEnabled(false);
					} else {
						// And enable empty positions if there is room in the lineup
						m_jcbPlayer.setEnabled(true);
					}
				}
				tacticOrder = position.getTaktik();
                setTaktik(position.getTaktik(), aktuellerPlayer);
            }
        }

        setSpielerListe(player, aktuellerPlayer);

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
        if (position != null) {selectedPlayer = HOVerwaltung.instance().getModel().getSpieler(position.getSpielerId());
            setTaktik(position.getTaktik(), selectedPlayer);}

        setSpielerListe2(lPlayers, selectedPlayer, playerIDcorrespondingSub);
        initLabel();
        repaint();
    }

    /**
     * Sets the list of possible players for this position and the currently selected player
     */
    protected void setSpielerListe(List<Player> playerListe, Player aktuellerPlayer) {
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
                    tempCB[i] = new SpielerCBItem("", 0f, null, true);

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
        cbmodel.addElement(m_clNullSpieler);

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

        //Adjust CB color
        // Color player name depending of status (injured, warned, ...)
        if (aktuellerPlayer != null) {
            m_jcbPlayer.setForeground(SpielerLabelEntry.getForegroundForSpieler(aktuellerPlayer));
        }

        //Listener wieder hinzu
        m_jcbPlayer.addItemListener(this);

        //Minimized
        if ((m_clSelectedPlayer != null) && (m_clSelectedPlayer.getSpieler() != null)) {
            m_jlPlayer.setText(m_clSelectedPlayer.getSpieler().getName());
            m_jlPlayer.setIcon(ImageUtilities.getImage4Position(HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getPositionBySpielerId(m_clSelectedPlayer.getSpieler().getSpielerID()),
                                                                                    m_clSelectedPlayer.getSpieler().getTrikotnummer()));
        } else {
            m_jlPlayer.setText("");
            m_jlPlayer.setIcon(null);
        }

        setTaktik(getTactic(), aktuellerPlayer);
    }


    /**
     * Sets the list of possible players for backup players
     */
    protected void setSpielerListe2(List<Player> allPlayers, Player selectedPlayer, int playerIDcorrespondingSub) {

        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();

        // list of all players currently set as subs
        List<Player> lSubs = new ArrayList<Player>();

        for (Player player: allPlayers) {
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
                    tempCB[i] = m_clCBItems[i];}
                //Create new
                else {
                    tempCB[i] = new SpielerCBItem("", 0f, null, true);
                }
            }

            //Empty reference and reset
            m_clCBItems = null;
            m_clCBItems = tempCB;
        }

        //Remove current Player if not a sub anymore
        if (selectedPlayer != null){
        for (Player p : lSubs) {
            if (p.getSpielerID() == selectedPlayer.getSpielerID()) cbmodel.addElement(createSpielerCBItem(m_clSelectedPlayer, selectedPlayer));
        }}

        //No Player
        cbmodel.addElement(m_clNullSpieler);

        //Sort Player List
        SpielerCBItem[] cbItems = new SpielerCBItem[lSubs.size()];

        Player pp;
        for (int i = 0; i < lSubs.size(); i++) {
            pp = lSubs.get(i);
            if (pp.getSpielerID() != playerIDcorrespondingSub) {cbItems[i] = createSpielerCBItem(m_clCBItems[i], pp);}
        }

        cbItems = Arrays.stream(cbItems).filter(value -> value != null).toArray(size -> new SpielerCBItem[size]);

        java.util.Arrays.sort(cbItems);

        for (int i = 0; i < cbItems.length; i++) {
            //All Other players
            cbmodel.addElement(cbItems[i]);
        }

        //Adjust CB color
        // Color player name depending of status (injured, warned, ...)
        if (selectedPlayer != null) {
            m_jcbPlayer.setForeground(SpielerLabelEntry.getForegroundForSpieler(selectedPlayer));
        }

        //Listener wieder hinzu
        m_jcbPlayer.addItemListener(this);

        //Minimized
        if ((m_clSelectedPlayer != null) && (m_clSelectedPlayer.getSpieler() != null)) {
            m_jlPlayer.setText(m_clSelectedPlayer.getSpieler().getName());
            m_jlPlayer.setIcon(ImageUtilities.getImage4Position(HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getPositionBySpielerId(m_clSelectedPlayer.getSpieler().getSpielerID()),
                    m_clSelectedPlayer.getSpieler().getTrikotnummer()));
        } else {
            m_jlPlayer.setText("");
            m_jlPlayer.setIcon(null);
        }

        setTaktik(getTactic(), selectedPlayer);
    }

    /**
     * Set the current tactic
     */
    private void setTaktik(byte taktik, Player aktuellerPlayer) {
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
                        this.setBackground(Color.BLUE);
                    }
                    else if (MatchRoleID.isPartialTrainPosition(position.getPosition(), nextWeekTrain)) {
                        this.setBackground(Color.CYAN);
                    }
                    else {
                        this.setBackground(Color.WHITE);
                    }
                }
                // Subs
                else if (IMatchRoleID.aSubstitutesMatchRoleID.contains(position.getId())){
                    m_jlPosition.setText(nameForPosition + " (#1)");
                }
                // Backups
                else {
                m_jlPosition.setText(nameForPosition + " (#2)");
               }
        }}

        //Minimized
        if ((m_clSelectedPlayer != null) && (m_clSelectedPlayer.getSpieler() != null)) {
            m_jlPlayer.setText(m_clSelectedPlayer.getSpieler().getName());
            m_jlPlayer.setIcon(ImageUtilities.getImage4Position(lineup.getPositionBySpielerId(m_clSelectedPlayer.getSpieler().getSpielerID()),
                                                                                    m_clSelectedPlayer.getSpieler()
                                                                                                        .getTrikotnummer()));
        } else {
            m_jlPlayer.setText("");
            m_jlPlayer.setIcon(null);
        }
    }

    /**
     * Setzt die Taktik je nach MatchRoleID
     */
    private void initTaktik(Player aktuellerPlayer) {
        m_jcbTactic.removeAllItems();

        switch (m_iPositionID) {
            case IMatchRoleID.keeper: {
                m_jcbTactic.addItem(new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),
                                               IMatchRoleID.NORMAL));
                break;
            }

            case IMatchRoleID.rightBack:
            case IMatchRoleID.leftBack: {
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardsmiddle"), IMatchRoleID.TOWARDS_MIDDLE);
                break;
            }

            case IMatchRoleID.rightCentralDefender:
            case IMatchRoleID.leftCentralDefender: {
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing"), IMatchRoleID.TOWARDS_WING);
                break;
            }

            case IMatchRoleID.middleCentralDefender: {
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
            	break;
            }

            case IMatchRoleID.rightInnerMidfield:
            case IMatchRoleID.leftInnerMidfield: {
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing"), IMatchRoleID.TOWARDS_WING);
            	break;
            }

            case IMatchRoleID.centralInnerMidfield: {
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
            	break;
            }

            case IMatchRoleID.leftWinger:
            case IMatchRoleID.rightWinger: {
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"), IMatchRoleID.OFFENSIVE);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardsmiddle"), IMatchRoleID.TOWARDS_MIDDLE);
                break;
            }

            case IMatchRoleID.rightForward:
            case IMatchRoleID.leftForward: {
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing"), IMatchRoleID.TOWARDS_WING);
                break;
            }

            case IMatchRoleID.centralForward: {
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"), IMatchRoleID.NORMAL);
            	addTactic(aktuellerPlayer,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"), IMatchRoleID.DEFENSIVE);
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


    private void addTactic(Player currentPlayer, String text, byte playerPosition){
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
        	// Create a string with just initial as first name
        	String spielerName = player.getName().substring(0, 1) + "." + player.getName().substring(player.getName().indexOf(" ")+1);

        	if (m_iPositionID == IMatchRoleID.setPieces) {
                item.setValues(spielerName,
                               player.getStandards()
                               + player.getSubskill4Pos(PlayerSkill.SET_PIECES)
                               + RatingPredictionManager.getLoyaltyHomegrownBonus(player),
                        player);
                return item;
            } else if (m_iPositionID == IMatchRoleID.captain) {
                item.setValues(spielerName,
                               Helper.round(
                            		   HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getAverageExperience(player.getSpielerID()),
                            		   core.model.UserParameter.instance().anzahlNachkommastellen),
                        player);
                return item;
            } else {
                final MatchRoleID position = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc().getPositionById(m_iPositionID);

                if (position != null) {
                    item.setValues(spielerName,
                                   player.calcPosValue(position.getPosition(), true), player);
                    return item;
                }

                return m_clNullSpieler;

            }
        }
        //Kein Player
        return m_clNullSpieler;

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

	public void addSwapItem (Component c) {
		jlp.add(c,1);
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
        jlp.add(overlay, constraints, 2);
        repaint();
 	}

	public void removeAssistantOverlay(LineupAssistantSelectorOverlay overlay) {
		jlp.remove(overlay);
		repaint();
	}

}
