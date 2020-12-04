package module.transfer.scout;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerSpeciality;
import core.datatype.CBItem;
//import core.epv.EPVData;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.HyperLinkLabel;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.module.IModule;
import core.util.HOLogger;
import core.util.Helper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;


/**
 * A player can be created and modified here.
 *
 * @author Marco Senn
 */
public class TransferEingabePanel extends ImagePanel implements ItemListener, ActionListener,
                                                                FocusListener, KeyListener
{
    //~ Static fields/initializers -----------------------------------------------------------------

	private static final long serialVersionUID = -3287232092187457640L;
	private static int iTempSpielerID = -1001;

    //~ Instance fields ----------------------------------------------------------------------------

    private ColorLabelEntry jpBestPosition = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
    		ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
    private DoubleLabelEntries jpRatingWingback = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private DoubleLabelEntries jpRatingWingbackDefensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingWingbackTowardsMiddle = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingWingbackOffensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingWinger = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private DoubleLabelEntries jpRatingWingerDefensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingWingerTowardsMiddle = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingWingerOffensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingDefender = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingDefenderTowardsWing = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingDefenderOffensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingMidfielder = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private DoubleLabelEntries jpRatingMidfielderTowardsWing = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingMidfielderDefensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingMidfielderOffensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingForward = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private DoubleLabelEntries jpRatingForwardTowardsWing = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingForwardDefensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private DoubleLabelEntries jpRatingKeeper = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private JButton jbAddTempSpieler = new JButton(HOVerwaltung.instance().getLanguageString("AddTempspieler"));
    private JButton jbRemove = new JButton(HOVerwaltung.instance().getLanguageString("ScoutEntfernen"));
    private JButton jbAdd = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.add"));
    private JButton jbMiniScout = new JButton(HOVerwaltung.instance().getLanguageString("ScoutMini"));
    private JButton jbApply = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.apply"));
    private JButton jbRemoveAll = new JButton(HOVerwaltung.instance().getLanguageString("Scout.RemoveAll"));
    private JComboBox jcbExperience = new JComboBox(PlayerAbility.ITEMS);
    private JComboBox jcbWinger = new JComboBox(PlayerAbility.ITEMS);
    private JComboBox jcbForm = new JComboBox(Helper.EINSTUFUNG_FORM);
    private JComboBox jcbStamina = new JComboBox(Helper.EINSTUFUNG_KONDITION);
    private JComboBox jcbPassing = new JComboBox(PlayerAbility.ITEMS);
    private JComboBox jcbSpeciality = new JComboBox(PlayerSpeciality.ITEMS);
    private JComboBox jcbPlaymaking = new JComboBox(PlayerAbility.ITEMS);
    private JComboBox jcbSetPieces = new JComboBox(PlayerAbility.ITEMS);
    private JComboBox jcbScoring = new JComboBox(PlayerAbility.ITEMS);
    private JComboBox jcbKeeper = new JComboBox(PlayerAbility.ITEMS);
    private JComboBox jcbDefending = new JComboBox(PlayerAbility.ITEMS);
    private JComboBox jcbLoyalty = new JComboBox(PlayerAbility.ITEMS);
    private JComboBox jcbLeadership = new JComboBox(PlayerAbility.ITEMS);
    private JCheckBox jchHomegrown = new JCheckBox();
    private JLabel jlStatus = new JLabel("<html><p>" + HOVerwaltung.instance().getLanguageString("scout_status") + ": <br /></p></html>");
    private JTextArea jtaCopyPaste = new JTextArea(5, 20);
    private JTextArea jtaNotes = new JTextArea();
    private JTextField jtfAge = new JTextField("17.0");
    private JTextField jtfTSI = new JTextField("1000");
    private JTextField jtfPrice = new JTextField("0");
	private JLabel jtfEPV = new JLabel("",SwingConstants.RIGHT);
    private ScoutEintrag clScoutEntry;
    private SpinnerDateModel clSpinnerModel = new SpinnerDateModel();
    private JSpinner jsSpinner = new JSpinner(clSpinnerModel);
    private JTextField jtfName = new JTextField();
    private JTextField jtfPlayerID = new JTextField("0");
    private TransferScoutPanel clOwner;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TransferEingabePanel object.
     *
     * @param owner the parent control holding this panel
     */
    public TransferEingabePanel(TransferScoutPanel owner) {
        clOwner = owner;
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Return next temporary playerID. Starting from -1.
     *
     * @return Returns next temporary playerID.
     */
    public static int getNextTempSpielerID() {
        return iTempSpielerID--;
    }

    /**
     * Set new scout entry or modify old
     *
     * @param scoutEintrag The new scout entry object
     */
    public final void setScoutEintrag(ScoutEintrag scoutEintrag) {
        if (scoutEintrag != null) {
            clScoutEntry = scoutEintrag;
            // If scout entry already exists
            if (clOwner.getTransferTable().getTransferTableModel()
            		.getScoutEintrag(clScoutEntry.getPlayerID()) != null) {
                jbAdd.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Transferscout_ersetzen"));
                jbAdd.setText(HOVerwaltung.instance().getLanguageString("ScoutErsetzen"));
                jbRemove.setEnabled(true);
            } else {
                jbAdd.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Transferscout_hinzufuegen"));
                jbAdd.setText(HOVerwaltung.instance().getLanguageString("ls.button.add"));
                jbRemove.setEnabled(false);
            }
            jbAdd.setEnabled(true);
        } else {
            clScoutEntry = new ScoutEintrag();
            jbAdd.setText(HOVerwaltung.instance().getLanguageString("ls.button.add"));
            jbRemove.setEnabled(false);
            jbAdd.setEnabled(false);
        }
        setCBs();
        setLabels();
        checkFields();
        invalidate();
        validate();
        repaint();
    }

    /**
     * Fired when any button is pressed
     *
     * @param actionEvent Event fired when button is pressed.
     */
    public final void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(jbApply)) {
            copyPaste();
        } else if (actionEvent.getSource().equals(jbAddTempSpieler)) {
            final core.model.player.Player tempPlayer = new core.model.player.Player();
            tempPlayer.setNationalitaet(HOVerwaltung.instance().getModel().getBasics().getLand());
            tempPlayer.setSpielerID(getNextTempSpielerID());
            if (jtfName.getText().trim().equals("")) {
                tempPlayer.setLastName("Temp " + Math.abs(1000 + tempPlayer.getSpielerID()));
            } else {
                tempPlayer.setLastName(jtfName.getText());
            }
            tempPlayer.setTSI(Integer.parseInt(jtfTSI.getText()));
            tempPlayer.setPlayerSpecialty(((CBItem) jcbSpeciality.getSelectedItem()).getId());
            tempPlayer.setAlter(Integer.parseInt(jtfAge.getText().replaceFirst("\\..*", "")));
            tempPlayer.setAgeDays(Integer.parseInt(jtfAge.getText().replaceFirst(".*\\.", "")));
            tempPlayer.setErfahrung(((CBItem) jcbExperience.getSelectedItem()).getId());
            tempPlayer.setForm(((CBItem) jcbForm.getSelectedItem()).getId());
            tempPlayer.setKondition(((CBItem) jcbStamina.getSelectedItem()).getId());
            tempPlayer.setVerteidigung(((CBItem)jcbDefending.getSelectedItem()).getId());
            tempPlayer.setTorschuss(((CBItem) jcbScoring.getSelectedItem()).getId());
            tempPlayer.setTorwart(((CBItem) jcbKeeper.getSelectedItem()).getId());
            tempPlayer.setFluegelspiel(((CBItem) jcbWinger.getSelectedItem()).getId());
            tempPlayer.setPasspiel(((CBItem) jcbPassing.getSelectedItem()).getId());
            tempPlayer.setStandards(((CBItem) jcbSetPieces.getSelectedItem()).getId());
            tempPlayer.setSpielaufbau(((CBItem) jcbPlaymaking.getSelectedItem()).getId());
            tempPlayer.setLoyalty(((CBItem)jcbLoyalty.getSelectedItem()).getId());
            tempPlayer.setFuehrung(((CBItem)jcbLeadership.getSelectedItem()).getId());
            tempPlayer.setHomeGrown(jchHomegrown.isSelected());
            HOVerwaltung.instance().getModel().addPlayer(tempPlayer);
            RefreshManager.instance().doReInit();
            HOMainFrame.instance().showTab(IModule.PLAYEROVERVIEW);
        }
		else if (actionEvent.getSource().equals(jbRemoveAll)) {
			clOwner.removeScoutEntries();
		}
    	else {
            clScoutEntry.setPlayerID(Integer.parseInt(jtfPlayerID.getText()));
            clScoutEntry.setPrice(Integer.parseInt(jtfPrice.getText()));
            clScoutEntry.setAlter(Integer.parseInt(jtfAge.getText().replaceFirst("\\..*", "")));
            clScoutEntry.setAgeDays(Integer.parseInt(jtfAge.getText().replaceFirst(".*\\.", "")));
            clScoutEntry.setTSI(Integer.parseInt(jtfTSI.getText()));
            clScoutEntry.setName(jtfName.getText());
            clScoutEntry.setInfo(jtaNotes.getText());
            clScoutEntry.setDeadline(new java.sql.Timestamp(clSpinnerModel.getDate().getTime()));
            if (actionEvent.getSource().equals(jbAdd)) {
                clOwner.addScoutEintrag(clScoutEntry);
            } else if (actionEvent.getSource().equals(jbRemove)) {
                clOwner.removeScoutEintrag(clScoutEntry);
            } else if (actionEvent.getSource().equals(jbMiniScout)) {
                new MiniScoutDialog(this);
            }
        }
        checkFields();
    }

    /**
     * Fired when panel receives focus
     *
     * @param focusEvent Event fired when panel receives focus
     */
    public void focusGained(FocusEvent focusEvent) {
    }

    /**
     * Fired when panel losts focus
     *
     * @param focusEvent Event fired when panel losts focus
     */
    public final void focusLost(FocusEvent focusEvent) {
        if (!Helper.parseInt(HOMainFrame.instance(), jtfTSI, false)
            || !Helper.parseInt(HOMainFrame.instance(), jtfPlayerID, false)
            || !Helper.parseInt(HOMainFrame.instance(), jtfPrice, false)) {
            return;
        }
        checkFields();
        if (focusEvent.getSource().equals(jtfAge)) {
			setLabels();
        }
    }

    /**
     * Fired when an item changes
     *
     * @param itemEvent Indicates which item has changed
     */
    public final void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED || itemEvent.getSource() == jchHomegrown) {
            setLabels();
        }
    }

    /**
     * Fired when a key is pressed
     *
     * @param keyEvent Event fired when a key is pressed
     */
    public void keyPressed(KeyEvent keyEvent) {
    }

    /**
     * Fired when a key is released
     *
     * @param keyEvent Event fired when a key is released
     */
    public final void keyReleased(KeyEvent keyEvent) {
        checkFields();
    }

    /**
     * Fired when a key is typed
     *
     * @param keyEvent Fired when a key is typed
     */
    public void keyTyped(KeyEvent keyEvent) {
    }

    /**
     * Set checkboxes to their corresponding value
     */
    private void setCBs() {
        clSpinnerModel.setValue(clScoutEntry.getDeadline());
        jtfPlayerID.setText(clScoutEntry.getPlayerID() + "");
        jtfName.setText(clScoutEntry.getName());
        jtfPrice.setText(clScoutEntry.getPrice() + "");
        jtfAge.setText(clScoutEntry.getAlter() + "." + clScoutEntry.getAgeDays());
        jtfTSI.setText(clScoutEntry.getTSI() + "");
        jtaNotes.setText(clScoutEntry.getInfo());
        jcbSpeciality.removeItemListener(this);
        jcbExperience.removeItemListener(this);
        jcbForm.removeItemListener(this);
        jcbStamina.removeItemListener(this);
        jcbPlaymaking.removeItemListener(this);
        jcbWinger.removeItemListener(this);
        jcbScoring.removeItemListener(this);
        jcbKeeper.removeItemListener(this);
        jcbPassing.removeItemListener(this);
        jcbDefending.removeItemListener(this);
        jcbSetPieces.removeItemListener(this);
        jcbLoyalty.removeActionListener(this);
        jcbLeadership.removeActionListener(this);
        jchHomegrown.removeActionListener(this);
        Helper.setComboBoxFromID(jcbSpeciality, clScoutEntry.getSpeciality());
        Helper.setComboBoxFromID(jcbExperience, clScoutEntry.getErfahrung());
        Helper.setComboBoxFromID(jcbForm, clScoutEntry.getForm());
        Helper.setComboBoxFromID(jcbStamina, clScoutEntry.getKondition());
        Helper.setComboBoxFromID(jcbPlaymaking, clScoutEntry.getSpielaufbau());
        Helper.setComboBoxFromID(jcbWinger, clScoutEntry.getFluegelspiel());
        Helper.setComboBoxFromID(jcbScoring, clScoutEntry.getTorschuss());
        Helper.setComboBoxFromID(jcbKeeper, clScoutEntry.getTorwart());
        Helper.setComboBoxFromID(jcbPassing, clScoutEntry.getPasspiel());
        Helper.setComboBoxFromID(jcbDefending, clScoutEntry.getVerteidigung());
        Helper.setComboBoxFromID(jcbSetPieces, clScoutEntry.getStandards());
        Helper.setComboBoxFromID(jcbLoyalty, clScoutEntry.getLoyalty());
        Helper.setComboBoxFromID(jcbLeadership, clScoutEntry.getLoyalty());
        jchHomegrown.setSelected(clScoutEntry.isHomegrown());
        jcbSpeciality.addItemListener(this);
        jcbExperience.addItemListener(this);
        jcbForm.addItemListener(this);
        jcbStamina.addItemListener(this);
        jcbPlaymaking.addItemListener(this);
        jcbWinger.addItemListener(this);
        jcbScoring.addItemListener(this);
        jcbKeeper.addItemListener(this);
        jcbPassing.addItemListener(this);
        jcbDefending.addItemListener(this);
        jcbSetPieces.addItemListener(this);
        jcbLoyalty.addItemListener(this);
        jcbLeadership.addItemListener(this);
        jchHomegrown.addItemListener(this);
    }

    /**
     * Set labels to the new values
     */
    private void setLabels() {
        final core.model.player.Player tempPlayer = new core.model.player.Player();
        tempPlayer.setPlayerSpecialty(((CBItem)jcbSpeciality.getSelectedItem()).getId());
        tempPlayer.setErfahrung(((CBItem)jcbExperience.getSelectedItem()).getId());
        tempPlayer.setFuehrung(((CBItem)jcbLeadership.getSelectedItem()).getId());
        tempPlayer.setForm(((CBItem)jcbForm.getSelectedItem()).getId());
        tempPlayer.setKondition(((CBItem)jcbStamina.getSelectedItem()).getId());
        tempPlayer.setVerteidigung(((CBItem)jcbDefending.getSelectedItem()).getId());
        tempPlayer.setTorschuss(((CBItem)jcbScoring.getSelectedItem()).getId());
        tempPlayer.setTorwart(((CBItem)jcbKeeper.getSelectedItem()).getId());
        tempPlayer.setFluegelspiel(((CBItem)jcbWinger.getSelectedItem()).getId());
        tempPlayer.setPasspiel(((CBItem)jcbPassing.getSelectedItem()).getId());
        tempPlayer.setStandards(((CBItem)jcbSetPieces.getSelectedItem()).getId());
        tempPlayer.setSpielaufbau(((CBItem)jcbPlaymaking.getSelectedItem()).getId());
        tempPlayer.setLoyalty(((CBItem)jcbLoyalty.getSelectedItem()).getId());
        tempPlayer.setHomeGrown(jchHomegrown.isSelected());
        tempPlayer.setAlter(Integer.parseInt(jtfAge.getText().replaceFirst("\\..*", "")));
        tempPlayer.setAgeDays(Integer.parseInt(jtfAge.getText().replaceFirst(".*\\.", "")));
//        EPVData data = new EPVData(tempPlayer);
//		double price = HOVerwaltung.instance().getModel().getEPV().getPrice(data);
//		jtfEPV.setText(NumberFormat.getCurrencyInstance().format(price));
        byte bIdealPosition = tempPlayer.getIdealPosition();
        jpBestPosition.setText(MatchRoleID.getNameForPosition(bIdealPosition)
                + " ("
                +  tempPlayer.getIdealPosStaerke(true, true, 1)
                + "%)");
        jpRatingKeeper.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.KEEPER, true)) + "");
        jpRatingDefender.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.CENTRAL_DEFENDER, true)) + "");
        jpRatingDefenderTowardsWing.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.CENTRAL_DEFENDER_TOWING, true)) + "");
        jpRatingDefenderOffensive.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.CENTRAL_DEFENDER_OFF, true)) + "");
        jpRatingWingback.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.BACK, true)) + "");
        jpRatingWingbackTowardsMiddle.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.BACK_TOMID, true)) + "");
        jpRatingWingbackOffensive.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.BACK_OFF, true)) + "");
        jpRatingWingbackDefensive.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.BACK_DEF, true)) + "");
        jpRatingMidfielder.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.MIDFIELDER, true)) + "");
        jpRatingMidfielderTowardsWing.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.MIDFIELDER_TOWING, true)) + "");
        jpRatingMidfielderOffensive.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.MIDFIELDER_OFF, true)) + "");
        jpRatingMidfielderDefensive.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.MIDFIELDER_DEF, true)) + "");
        jpRatingWinger.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.WINGER, true)) + "");
        jpRatingWingerTowardsMiddle.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.WINGER_TOMID, true)) + "");
        jpRatingWingerOffensive.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.WINGER_OFF, true)) + "");
        jpRatingWingerDefensive.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.WINGER_DEF, true)) + "");
        jpRatingForward.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.FORWARD, true)) + "");
        jpRatingForwardTowardsWing.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.FORWARD_TOWING, true)) + "");
        jpRatingForwardDefensive.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
        		.format(tempPlayer.calcPosValue(IMatchRoleID.FORWARD_DEF, true)) + "");
        clScoutEntry.setSpeciality(((CBItem) jcbSpeciality.getSelectedItem()).getId());
        clScoutEntry.setErfahrung(((CBItem) jcbExperience.getSelectedItem()).getId());
        clScoutEntry.setForm(((CBItem) jcbForm.getSelectedItem()).getId());
        clScoutEntry.setKondition(((CBItem) jcbStamina.getSelectedItem()).getId());
        clScoutEntry.setVerteidigung(((CBItem) jcbDefending.getSelectedItem()).getId());
        clScoutEntry.setTorschuss(((CBItem) jcbScoring.getSelectedItem()).getId());
        clScoutEntry.setTorwart(((CBItem) jcbKeeper.getSelectedItem()).getId());
        clScoutEntry.setFluegelspiel(((CBItem) jcbWinger.getSelectedItem()).getId());
        clScoutEntry.setPasspiel(((CBItem) jcbPassing.getSelectedItem()).getId());
        clScoutEntry.setStandards(((CBItem) jcbSetPieces.getSelectedItem()).getId());
        clScoutEntry.setSpielaufbau(((CBItem) jcbPlaymaking.getSelectedItem()).getId());
        clScoutEntry.setLoyalty(((CBItem) jcbLoyalty.getSelectedItem()).getId());
        clScoutEntry.setLeadership(((CBItem) jcbLeadership.getSelectedItem()).getId());
        clScoutEntry.setHomegrown(jchHomegrown.isSelected());
    }

    /**
     * Check inputfields of valid values
     */
    private void checkFields() {
        // When playerid already exists
        int id = 0;
        boolean valid = true;

        try {
            id = Integer.parseInt(jtfPlayerID.getText());
        } catch (NumberFormatException e) {
            valid = false;
        }

        if (valid && (clOwner.getTransferTable().getTransferTableModel().getScoutEintrag(id) != null)) {
            jbAdd.setEnabled(true);
            jbAdd.setText(HOVerwaltung.instance().getLanguageString("ScoutErsetzen"));
            jbRemove.setEnabled(true);
        } else {
            jbAdd.setEnabled(true);
            jbAdd.setText(HOVerwaltung.instance().getLanguageString("ls.button.add"));
            jbRemove.setEnabled(false);
        }
    }

    /**
     * Calls playerconverter and fills boxes to the corresponding values
     */
    private void copyPaste() {
        String message = "";
        List<String> errorFields = new ArrayList<String>();

        final PlayerConverter pc = new PlayerConverter();

        try {
            final module.transfer.scout.Player player;
            player = pc.build(jtaCopyPaste.getText());

            if (player != null) {
                jtfPlayerID.setText(player.getPlayerID() + "");
                jtfName.setText(player.getPlayerName());
                jtfAge.setText(player.getAge() + "." + player.getAgeDays());

                jtfPrice.setText(player.getPrice() + "");
                jtfTSI.setText(player.getTSI() + "");
                jtaNotes.setText(player.getInfo());

                jcbSpeciality.removeItemListener(this);
                Helper.setComboBoxFromID(jcbSpeciality,player.getSpeciality());
                jcbSpeciality.addItemListener(this);
                jcbExperience.removeItemListener(this);
                Helper.setComboBoxFromID(jcbExperience,player.getExperience());
                jcbExperience.addItemListener(this);
                jcbForm.removeItemListener(this);
                Helper.setComboBoxFromID(jcbForm, player.getForm());
                jcbForm.addItemListener(this);
                jcbStamina.removeItemListener(this);
                Helper.setComboBoxFromID(jcbStamina,player.getStamina());
                jcbStamina.addItemListener(this);
                jcbDefending.removeItemListener(this);
                Helper.setComboBoxFromID(jcbDefending,player.getDefense());
                jcbDefending.addItemListener(this);
                jcbScoring.removeItemListener(this);
                Helper.setComboBoxFromID(jcbScoring, player.getAttack());
                jcbScoring.addItemListener(this);
                jcbKeeper.removeItemListener(this);
                Helper.setComboBoxFromID(jcbKeeper,player.getGoalKeeping());
                jcbKeeper.addItemListener(this);
                jcbWinger.removeItemListener(this);
                Helper.setComboBoxFromID(jcbWinger, player.getWing());
                jcbWinger.addItemListener(this);
                jcbPassing.removeItemListener(this);
                Helper.setComboBoxFromID(jcbPassing, player.getPassing());
                jcbPassing.addItemListener(this);
                jcbSetPieces.removeItemListener(this);
                Helper.setComboBoxFromID(jcbSetPieces,player.getSetPieces());
                jcbSetPieces.addItemListener(this);
                jcbLoyalty.removeItemListener(this);
                Helper.setComboBoxFromID(jcbLoyalty,player.getLoyalty());
                jcbLoyalty.addItemListener(this);
                jcbLeadership.removeItemListener(this);
                Helper.setComboBoxFromID(jcbLeadership,player.getLeadership());
                jcbLeadership.addItemListener(this);
                jchHomegrown.removeItemListener(this);
                jchHomegrown.setSelected(player.isHomwGrown());
                jchHomegrown.addItemListener(this);

                // Listener stays here for recalculation of rating
                Helper.setComboBoxFromID(jcbPlaymaking,player.getPlayMaking());

                // Normally not working. Thus last positioned
                jsSpinner.setValue(pc.getDeadline());

                setLabels();
            }
        } catch (Exception e) {
        	HOLogger.instance().debug(getClass(), e);
            message = HOVerwaltung.instance().getLanguageString("scout_error");
            message += " <br>" + HOVerwaltung.instance().getLanguageString("bug_ticket");
        }

        jtaCopyPaste.setText("");

        if (message.equals("")) {
            switch (pc.getStatus()) {
                case PlayerConverter.WARNING:
                    message = HOVerwaltung.instance().getLanguageString("scout_warning");
                    message += " " + getFieldsTextList(pc.getErrorFields());
                    message += " <br>" + HOVerwaltung.instance().getLanguageString("bug_ticket");
                    if(pc.getNotSupportedFields().size()>0) {
                        message += " <br>" + HOVerwaltung.instance().getLanguageString("scout_not_supported_fields");
                        message += " " + getFieldsTextList(pc.getNotSupportedFields());
                    }
                    break;
                case PlayerConverter.ERROR:
                    message = HOVerwaltung.instance().getLanguageString("scout_error");
                    message += " <br>" + HOVerwaltung.instance().getLanguageString("bug_ticket");
                    break;
                case PlayerConverter.EMPTY_INPUT_ERROR:
                    message = HOVerwaltung.instance().getLanguageString("scout_error_input_empty");
                    break;
                default:
                    message = HOVerwaltung.instance().getLanguageString("scout_success");
                    if(pc.getNotSupportedFields().size()>0) {
                        message += " <br>" + HOVerwaltung.instance().getLanguageString("scout_not_supported_fields");
                        message += " " + getFieldsTextList(pc.getNotSupportedFields());
                    }
            }
        }
        jlStatus.setText("<html><p>" + HOVerwaltung.instance().getLanguageString("scout_status") + ": " + message + "</p></html>");
    }

    private String getFieldsTextList(List<String> fields){
        String errorFieldsTxt = "";
        if (fields.size()>0){
            //errorFieldsTxt = " (";
            for (int i=0;i<fields.size();i++) {
                if(i>=1) {
                    errorFieldsTxt += ", ";
                }
                errorFieldsTxt += fields.get(i);
            }
            // errorFieldsTxt += ")";
        }
        return errorFieldsTxt;
    }

    /**
     * Create components on the panel with default values
     */
    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(4, 4, 4, 4);

        JPanel panel;
        JPanel buttonPanel;
        JPanel copyPastePanel;
        JLabel jlExplainGuide;
        JLabel label;

        setLayout(layout);

        // Entries
        panel = new ImagePanel();
        panel.setLayout(new GridLayout(11, 4, 4, 4));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.id"));
        panel.add(label);
        jtfPlayerID.setHorizontalAlignment(JLabel.RIGHT);
        jtfPlayerID.addFocusListener(this);
        jtfPlayerID.addKeyListener(this);
        panel.add(jtfPlayerID);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.name"));
        panel.add(label);
        jtfName.addFocusListener(this);
        panel.add(jtfName);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.age"));
        panel.add(label);
        jtfAge.setHorizontalAlignment(JLabel.RIGHT);
        jtfAge.addFocusListener(this);
        panel.add(jtfAge);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.tsi"));
        panel.add(label);
        jtfTSI.setHorizontalAlignment(JLabel.RIGHT);
        jtfTSI.addFocusListener(this);
        panel.add(jtfTSI);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("scout_price"));
        panel.add(label);
        jtfPrice.setHorizontalAlignment(JLabel.RIGHT);
        jtfPrice.addFocusListener(this);
        panel.add(jtfPrice);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Ablaufdatum"));
        panel.add(label);
        jsSpinner.addFocusListener(this);
        panel.add(jsSpinner);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.speciality"));
        panel.add(label);
        jcbSpeciality.addItemListener(this);
        panel.add(jcbSpeciality);

		label = new JLabel("");
		panel.add(label);
		panel.add(jtfEPV);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.leadership"));
        panel.add(label);
        jcbLeadership.addItemListener(this);
        panel.add(jcbLeadership);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.experience"));
        panel.add(label);
        jcbExperience.addItemListener(this);
        panel.add(jcbExperience);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.form"));
        panel.add(label);
        jcbForm.addItemListener(this);
        panel.add(jcbForm);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina"));
        panel.add(label);
        jcbStamina.addItemListener(this);
        panel.add(jcbStamina);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper"));
        panel.add(label);
        jcbKeeper.addItemListener(this);
        panel.add(jcbKeeper);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking"));
        panel.add(label);
        jcbPlaymaking.addItemListener(this);
        panel.add(jcbPlaymaking);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.passing"));
        panel.add(label);
        jcbPassing.addItemListener(this);
        panel.add(jcbPassing);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.winger"));
        panel.add(label);
        jcbWinger.addItemListener(this);
        panel.add(jcbWinger);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.defending"));
        panel.add(label);
        jcbDefending.addItemListener(this);
        panel.add(jcbDefending);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring"));
        panel.add(label);
        jcbScoring.addItemListener(this);
        panel.add(jcbScoring);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces"));
        panel.add(label);
        jcbSetPieces.addItemListener(this);
        panel.add(jcbSetPieces);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.loyalty"));
        panel.add(label);
        jcbLoyalty.addItemListener(this);
        panel.add(jcbLoyalty);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.motherclub"));
        panel.add(label);
        jchHomegrown.addItemListener(this);
        panel.add(jchHomegrown);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        add(panel);

        // Notes
        panel = new ImagePanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder(HOVerwaltung.instance().getLanguageString("Notizen")));
        jtaNotes.addFocusListener(this);
        panel.add(new JScrollPane(jtaNotes), BorderLayout.CENTER);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        add(panel);

        // Copy & Paste
        panel = new ImagePanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder(HOVerwaltung.instance().getLanguageString("CopyPaste")));
        jtaCopyPaste.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Transferscout_CopyPaste"));

        copyPastePanel = new ImagePanel();
        copyPastePanel.setLayout(new BorderLayout());
        jlExplainGuide = new JLabel(HOVerwaltung.instance().getLanguageString("ExplainHowToUseTransferScout"));
        copyPastePanel.add(jlExplainGuide ,BorderLayout.NORTH);
        JLabel linkLabel = new HyperLinkLabel("https://github.com/akasolace/HO/wiki/Transfer-Scout", "https://github.com/akasolace/HO/wiki/Transfer-Scout");
        copyPastePanel.add(linkLabel, BorderLayout.CENTER);
        copyPastePanel.add(new JScrollPane(jtaCopyPaste),BorderLayout.SOUTH);
        panel.add(copyPastePanel, BorderLayout.NORTH);

        buttonPanel = new ImagePanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        jbApply.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.button.apply"));
        jbApply.addActionListener(this);
        layout.setConstraints(jbApply, constraints);
        buttonPanel.add(jbApply, BorderLayout.WEST);

        panel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(jlStatus, BorderLayout.SOUTH);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);

        add(panel);

        // Player values
        panel = new ImagePanel();
        panel.setLayout(new GridLayout(20, 2, 4, 4));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("BestePosition"));
        panel.add(label);
        panel.add(jpBestPosition.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.keeper"));
        panel.add(label);
        panel.add(jpRatingKeeper.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.centraldefender"));
        panel.add(label);
        panel.add(jpRatingDefender.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.centraldefendertowardswing"));
        panel.add(label);
        panel.add(jpRatingDefenderTowardsWing.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.centraldefenderoffensive"));
        panel.add(label);
        panel.add(jpRatingDefenderOffensive.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingback"));
        panel.add(label);
        panel.add(jpRatingWingback.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingbacktowardsmiddle"));
        panel.add(label);
        panel.add(jpRatingWingbackTowardsMiddle.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingbackoffensive"));
        panel.add(label);
        panel.add(jpRatingWingbackOffensive.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingbackdefensive"));
        panel.add(label);
        panel.add(jpRatingWingbackDefensive.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfielder"));
        panel.add(label);
        panel.add(jpRatingMidfielder.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfieldertowardswing"));
        panel.add(label);
        panel.add(jpRatingMidfielderTowardsWing.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfielderoffensive"));
        panel.add(label);
        panel.add(jpRatingMidfielderOffensive.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfielderdefensive"));
        panel.add(label);
        panel.add(jpRatingMidfielderDefensive.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.winger"));
        panel.add(label);
        panel.add(jpRatingWinger.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingertowardsmiddle"));
        panel.add(label);
        panel.add(jpRatingWingerTowardsMiddle.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingeroffensive"));
        panel.add(label);
        panel.add(jpRatingWingerOffensive.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingerdefensive"));
        panel.add(label);
        panel.add(jpRatingWingerDefensive.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.forward"));
        panel.add(label);
        panel.add(jpRatingForward.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.forwardtowardswing"));
        panel.add(label);
        panel.add(jpRatingForwardTowardsWing.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.forwarddefensive"));
        panel.add(label);
        panel.add(jpRatingForwardDefensive.getComponent(false));

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        layout.setConstraints(panel, constraints);
        add(panel);

        // Buttons
        panel = new ImagePanel();
        panel.setLayout(new GridLayout(6, 1, 4, 4));

        jbAdd.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Transferscout_hinzufuegen"));
        jbAdd.addActionListener(this);
        panel.add(jbAdd);
        jbRemove.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Transferscout_entfernen"));
        jbRemove.addActionListener(this);
        jbRemove.setEnabled(false);
        panel.add(jbRemove);
		jbRemoveAll.addActionListener(this);
		jbRemoveAll.setToolTipText(HOVerwaltung.instance().getLanguageString("Scout.tt_RemoveAll"));
		panel.add(jbRemoveAll);
        jbMiniScout.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Transferscout_Miniscout"));
        jbMiniScout.addActionListener(this);
        panel.add(jbMiniScout);
        jbAddTempSpieler.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_add_tempspieler"));
        jbAddTempSpieler.addActionListener(this);
        panel.add(jbAddTempSpieler);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        add(panel);

        setScoutEintrag(null);
    }
}
