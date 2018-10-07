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
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.model.player.SpielerPosition;
import core.rating.RatingPredictionManager;
import core.util.Helper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;


/**
 * Panel, in dem die Spielerposition dargestellt wird und geändert werden kann
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
    private int m_iPositionID = -1;

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
     * Gibt den aktuellen Spieler auf dieser Position zurück, oder null, wenn keiner gewählt wurde
     */
    public Spieler getSelectedPlayer() {
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
     * Erzeugt die Komponenten, Die CB für die Spieler und den Listener nicht vergessen!
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
            final Lineup aufstellung = HOVerwaltung.instance().getModel().getAufstellung();

            final Spieler spieler = getSelectedPlayer();

            //Spieler setzen
            if (itemEvent.getSource().equals(m_jcbPlayer)) {
                //Standart
                if (m_iPositionID == ISpielerPosition.setPieces) {
                    if (spieler != null) {
                        aufstellung.setKicker(spieler.getSpielerID());
                    } else {
                        aufstellung.setKicker(0);
                    }
                }
                //Spielführer
                else if (m_iPositionID == ISpielerPosition.captain) {
                    if (spieler != null) {
                        aufstellung.setKapitaen(spieler.getSpielerID());
                    } else {
                        aufstellung.setKapitaen(0);
                    }
                }
                //Andere
                else {
                    if (spieler != null) {
                        aufstellung.setSpielerAtPosition(m_iPositionID, spieler.getSpielerID());
                    } else {
                        aufstellung.setSpielerAtPosition(m_iPositionID, 0);
                    }
                }

                //CBFarben anpassen
                if (spieler != null) {
                    m_jcbPlayer.setForeground(SpielerLabelEntry.getForegroundForSpieler(spieler));
                }

                //Taktikwerte anpassen
                setTaktik(getTactic(), spieler);
            } else if (itemEvent.getSource().equals(m_jcbTactic)) {
                aufstellung.getPositionById(m_iPositionID).setTaktik(getTactic());
            }

            //Aktualisierung der Tabellen
            if (spieler != null) {
                HOMainFrame.instance().setActualSpieler(spieler);
            }

            //Alle anderen Position aktualisieren
            m_clUpdater.update();
        }
    }

    /**
     * Erneuert die Daten in den Komponenten
     */
    public void refresh(List<Spieler> spieler) {
        Spieler aktuellerSpieler = null;
		playerId = -1;
        if (m_iPositionID == ISpielerPosition.setPieces) {
            aktuellerSpieler = HOVerwaltung.instance().getModel().getSpieler(HOVerwaltung.instance()
                                                                                         .getModel()
                                                                                         .getAufstellung()
                                                                                         .getKicker());
			if (aktuellerSpieler!=null) {
				playerId = aktuellerSpieler.getSpielerID();
			}
			tacticOrder=-1;

			// Filter keeper from the spieler vector (can't be sp taker)
			// Make sure the incoming spieler list is not modified, it
			// seems to visit the captain position later.

			Spieler keeper = HOVerwaltung.instance().getModel().getAufstellung().
					getPlayerByPositionID(ISpielerPosition.keeper);
			if (keeper != null) {
				Vector<Spieler> tmpSpieler = new Vector<Spieler>(spieler.size() -1);
				for (int i = 0; i < spieler.size(); i++) {
					if ( keeper.getSpielerID() != spieler.get(i).getSpielerID()) {
						tmpSpieler.add(spieler.get(i));
					}
				}
				spieler = tmpSpieler;
			}
        } else if (m_iPositionID == ISpielerPosition.captain) {
            aktuellerSpieler = HOVerwaltung.instance().getModel().getSpieler(HOVerwaltung.instance()
                                                                                         .getModel()
                                                                                         .getAufstellung()
                                                                                         .getKapitaen());
			if (aktuellerSpieler!=null) {
				playerId = aktuellerSpieler.getSpielerID();
			}
			tacticOrder=-1;
        } else {
            //Aktuell aufgestellten Spieler holen
            final SpielerPosition position = HOVerwaltung.instance().getModel().getAufstellung()
                                                         .getPositionById(m_iPositionID);

            if (position != null) {
                aktuellerSpieler = HOVerwaltung.instance().getModel().getSpieler(position
                                                                                 .getSpielerId());

				if (aktuellerSpieler!=null) {
					m_jcbPlayer.setEnabled(true); // To be sure
					playerId = aktuellerSpieler.getSpielerID();
				} else {
					// We want to disable the player selection box if there is already 11 players on the field and this is an on field position.
					if ((HOVerwaltung.instance().getModel().getAufstellung().hasFreePosition() == false) &&
							(m_iPositionID >= ISpielerPosition.keeper) && (m_iPositionID < ISpielerPosition.startReserves)) {
						m_jcbPlayer.setEnabled(false);
					} else {
						// And enable empty positions if there is room in the lineup
						m_jcbPlayer.setEnabled(true);
					}
				}
				tacticOrder = position.getTaktik();
                setTaktik(position.getTaktik(), aktuellerSpieler);
            }
        }

        setSpielerListe(spieler, aktuellerSpieler);

        initLabel();

        repaint();
    }

    /**
     * Setzt die Liste der möglichen Spieler für diese Position und den aktuell gewählten Spieler
     */
    protected void setSpielerListe(List<Spieler> spielerListe, Spieler aktuellerSpieler) {
        //Listener entfernen
        m_jcbPlayer.removeItemListener(this);

        final DefaultComboBoxModel cbmodel = ((DefaultComboBoxModel) m_jcbPlayer.getModel());

        //Alle Items entfernen
        cbmodel.removeAllElements();

        //Genug CBItems für alle Spieler
        if (m_clCBItems.length != spielerListe.size()) {
            SpielerCBItem[] tempCB = new SpielerCBItem[spielerListe.size()];

            //Mit SpielerCBItems füllen: Vorzugsweise alte wiederverwenden
            for (int i = 0; i < tempCB.length; i++) {
                //Wiederverwenden
                if ((m_clCBItems.length > i) && (m_clCBItems[i] != null)) {
                    tempCB[i] = m_clCBItems[i];

                    //HOLogger.instance().log(getClass(), "Use old SpielerCBItem " + this.m_iPositionsID );
                }
                //Neu erzeugen
                else {
                    tempCB[i] = new SpielerCBItem("", 0f, null, true);

                    //HOLogger.instance().log(getClass(), "Create new SpielerCBItem " + this.m_iPositionsID );
                }
            }

            //Referenz leeren und neu setzen
            m_clCBItems = null;
            m_clCBItems = tempCB;
        }

        //Aktueller Spieler
        cbmodel.addElement(createSpielerCBItem(m_clSelectedPlayer, aktuellerSpieler));

        //Kein Spieler
        cbmodel.addElement(m_clNullSpieler);

        //Spielerliste sortieren
        SpielerCBItem[] cbItems = new SpielerCBItem[spielerListe.size()];

        for (int i = 0; i < spielerListe.size(); i++) {
            cbItems[i] = createSpielerCBItem(m_clCBItems[i], ((Spieler) spielerListe.get(i)));
        }

        java.util.Arrays.sort(cbItems);

        for (int i = 0; i < cbItems.length; i++) {
            //Alle anderen Spieler
            cbmodel.addElement(cbItems[i]);
        }

        //CBFarben anpassen
        if (aktuellerSpieler != null) {
            m_jcbPlayer.setForeground(SpielerLabelEntry.getForegroundForSpieler(aktuellerSpieler));
        }

        //Speicher freigeben
        cbItems = null;

        //Listener wieder hinzu
        m_jcbPlayer.addItemListener(this);

        //Minimized
        if ((m_clSelectedPlayer != null) && (m_clSelectedPlayer.getSpieler() != null)) {
            m_jlPlayer.setText(m_clSelectedPlayer.getSpieler().getName());
            m_jlPlayer.setIcon(ImageUtilities.getImage4Position(HOVerwaltung.instance().getModel().getAufstellung().getPositionBySpielerId(m_clSelectedPlayer.getSpieler().getSpielerID()),
                                                                                    m_clSelectedPlayer.getSpieler().getTrikotnummer()));
        } else {
            m_jlPlayer.setText("");
            m_jlPlayer.setIcon(null);
        }

        setTaktik(getTactic(), aktuellerSpieler);
    }

    /**
     * Setzt die aktuelle Taktik
     */
    private void setTaktik(byte taktik, Spieler aktuellerSpieler) {
        //Listener entfernen
        m_jcbTactic.removeItemListener(this);

        //Taktik neu füllen!
        initTaktik(aktuellerSpieler);

        //Suche nach der Taktik
        Helper.markierenComboBox(m_jcbTactic, taktik);

        //Listener hinzu
        m_jcbTactic.addItemListener(this);
    }

    /**
     * Setzt das Label
     */
    private void initLabel() {
    	final Lineup lineup = HOVerwaltung.instance().getModel().getAufstellung();

        if (m_iPositionID == ISpielerPosition.setPieces) {
            m_jlPosition.setText(HOVerwaltung.instance().getLanguageString("match.setpiecestaker"));
        } else if (m_iPositionID == ISpielerPosition.captain) {
            m_jlPosition.setText(HOVerwaltung.instance().getLanguageString("Spielfuehrer"));
        } else {
            final SpielerPosition position = lineup.getPositionById(m_iPositionID);

            if (position != null) {
                //Reserve
            	final String nameForPosition = (m_bMinimize)?SpielerPosition.getNameForPosition(position
                        .getPosition()):SpielerPosition.getNameForPosition(position.getPosition());

                if ((m_jcbTactic.getItemCount() == 1) && (position.getId() != ISpielerPosition.keeper)) {
                	//special naming for reserve defender
                	if (!m_bMinimize && position.getId() == ISpielerPosition.substDefender) {
                		m_jlPosition.setText(HOVerwaltung.instance().getLanguageString("Reserve")
                                + " " + HOVerwaltung.instance().getLanguageString("defender"));
                	} else {
                		m_jlPosition.setText(HOVerwaltung.instance().getLanguageString("Reserve")
                                         + " " + nameForPosition);
                	}
                } else {
                    m_jlPosition.setText(nameForPosition);
                }
            }
        }

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
     * Setzt die Taktik je nach SpielerPosition
     */
    private void initTaktik(Spieler aktuellerSpieler) {
        m_jcbTactic.removeAllItems();

        switch (m_iPositionID) {
            case ISpielerPosition.keeper: {
                m_jcbTactic.addItem(new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),
                                               ISpielerPosition.NORMAL));
                break;
            }

            case ISpielerPosition.rightBack:
            case ISpielerPosition.leftBack: {
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),ISpielerPosition.NORMAL);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"),ISpielerPosition.OFFENSIVE);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"),ISpielerPosition.DEFENSIVE);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardsmiddle"),ISpielerPosition.TOWARDS_MIDDLE);
                break;
            }

            case ISpielerPosition.rightCentralDefender:
            case ISpielerPosition.leftCentralDefender: {
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),ISpielerPosition.NORMAL);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"),ISpielerPosition.OFFENSIVE);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing"),ISpielerPosition.TOWARDS_WING);
                break;
            }

            case ISpielerPosition.middleCentralDefender: {
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),ISpielerPosition.NORMAL);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"),ISpielerPosition.OFFENSIVE);
            	break;
            }

            case ISpielerPosition.rightInnerMidfield:
            case ISpielerPosition.leftInnerMidfield: {
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),ISpielerPosition.NORMAL);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"),ISpielerPosition.OFFENSIVE);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"),ISpielerPosition.DEFENSIVE);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing"),ISpielerPosition.TOWARDS_WING);
            	break;
            }

            case ISpielerPosition.centralInnerMidfield: {
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),ISpielerPosition.NORMAL);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"),ISpielerPosition.OFFENSIVE);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"),ISpielerPosition.DEFENSIVE);
            	break;
            }

            case ISpielerPosition.leftWinger:
            case ISpielerPosition.rightWinger: {
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),ISpielerPosition.NORMAL);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.offensive"),ISpielerPosition.OFFENSIVE);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"),ISpielerPosition.DEFENSIVE);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardsmiddle"),ISpielerPosition.TOWARDS_MIDDLE);
                break;
            }

            case ISpielerPosition.rightForward:
            case ISpielerPosition.leftForward: {
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),ISpielerPosition.NORMAL);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"),ISpielerPosition.DEFENSIVE);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.towardswing"),ISpielerPosition.TOWARDS_WING);
                break;
            }

            case ISpielerPosition.centralForward: {
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),ISpielerPosition.NORMAL);
            	addTactic(aktuellerSpieler,HOVerwaltung.instance().getLanguageString("ls.player.behaviour.defensive"),ISpielerPosition.DEFENSIVE);
            	break;
            }


            case ISpielerPosition.substDefender:
            case ISpielerPosition.substForward:
            case ISpielerPosition.substInnerMidfield:
            case ISpielerPosition.substKeeper:
            case ISpielerPosition.substWinger: {
                m_jcbTactic.addItem(new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),
                                               ISpielerPosition.NORMAL));
                break;
            }

            default:
                m_jcbTactic.addItem(new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.behaviour.normal"),
                                               ISpielerPosition.NORMAL));
        }
    }


    private void addTactic(Spieler currentPlayer, String text, byte playerPosition){
    	if (currentPlayer != null) {
            text += " ("
                    + currentPlayer.calcPosValue(SpielerPosition.getPosition(m_iPositionID,
                    		playerPosition),
                                                    true) + ")";
        }

        m_jcbTactic.addItem(new CBItem(text, playerPosition));
    }
    //-------------private-------------------------------------------------

    /**
     * Generiert ein SpielerCBItem für einen Spieler
     */
    private SpielerCBItem createSpielerCBItem(SpielerCBItem item, Spieler spieler) {
        if (spieler != null) {
        	// Create a string with just initial as first name
        	String spielerName = spieler.getName().substring(0, 1) + "." + spieler.getName().substring(spieler.getName().indexOf(" ")+1);

        	if (m_iPositionID == ISpielerPosition.setPieces) {
                item.setValues(spielerName,
                               spieler.getStandards()
                               + spieler.getSubskill4Pos(PlayerSkill.SET_PIECES)
                               + RatingPredictionManager.getLoyaltyHomegrownBonus(spieler),
                               spieler);
                return item;
            } else if (m_iPositionID == ISpielerPosition.captain) {
                item.setValues(spielerName,
                               Helper.round(
                            		   HOVerwaltung.instance().getModel().getAufstellung().getAverageExperience(spieler.getSpielerID()),
                            		   core.model.UserParameter.instance().anzahlNachkommastellen),
                               spieler);
                return item;
            } else {
                final SpielerPosition position = HOVerwaltung.instance().getModel().getAufstellung()
                                                                                        .getPositionById(m_iPositionID);

                if (position != null) {
                    item.setValues(spielerName,
                                   spieler.calcPosValue(position.getPosition(), true), spieler);
                    return item;
                }

                return m_clNullSpieler;

            }
        }
        //Kein Spieler
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
		if (tactic == ISpielerPosition.OFFENSIVE && playerID != -1) {
			symbol = "▼";
		} else if (tactic == ISpielerPosition.DEFENSIVE && playerID != -1) {
			symbol = "▲";
		} else if (tactic == ISpielerPosition.TOWARDS_MIDDLE && playerID != -1) {
			if (positionsID == ISpielerPosition.rightBack
					|| positionsID == ISpielerPosition.rightWinger) {
				symbol = "▶";
			} else {
				symbol = "◀";
			}
		} else if (tactic == ISpielerPosition.TOWARDS_WING && playerID != -1) {
			if (positionsID == ISpielerPosition.rightCentralDefender
					|| positionsID == ISpielerPosition.rightInnerMidfield
					|| positionsID == ISpielerPosition.rightForward) {
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
