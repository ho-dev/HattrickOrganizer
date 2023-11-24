package module.playerOverview;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerSpeciality;
import core.datatype.CBItem;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.module.IModule;
import core.util.Helper;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


/**
 * This is a Skill Tester, where parameters of a player can be changed to see
 * what effect this will have on ratings for the player.
 */
final class SpielerTrainingsSimulatorPanel extends ImagePanel
        implements core.gui.Refreshable, ItemListener, ActionListener, FocusListener {

    @Serial
    private static final long serialVersionUID = 7657564758631332932L;

    //~ Static fields/initializers -----------------------------------------------------------------

    private static final Dimension CBSIZE = new Dimension(Helper.calcCellWidth(120),
            Helper.calcCellWidth(25));
    private static final Dimension PFEILSIZE = new Dimension(20, 20);

    //~ Instance fields ----------------------------------------------------------------------------

    private final ColorLabelEntry m_jpBestPos = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
    // Ratings Column
    private final DoubleLabelEntries m_jpRatingKeeper = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingCentralDefender = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingCentralDefenderTowardsWing = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingCentralDefenderOffensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingWingback = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingWingbackDefensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingWingbackTowardsMiddle = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingWingbackOffensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingeMidfielder = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingeMidfielderTowardsWing = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingeMidfielderDefensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingeMidfielderOffensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingWinger = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingWingerDefensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingWingerTowardsMiddle = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingWingerOffensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingForward = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingForwardTowardsWing = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoubleLabelEntries m_jpRatingForwardDefensive = new DoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);

    private final DoubleLabelEntries[] playerPositionValues = new DoubleLabelEntries[]{
            m_jpRatingKeeper,
            m_jpRatingCentralDefender,
            m_jpRatingCentralDefenderTowardsWing,
            m_jpRatingCentralDefenderOffensive,
            m_jpRatingWingback,
            m_jpRatingWingbackTowardsMiddle,
            m_jpRatingWingbackOffensive,
            m_jpRatingWingbackDefensive,
            m_jpRatingeMidfielder,
            m_jpRatingeMidfielderTowardsWing,
            m_jpRatingeMidfielderOffensive,
            m_jpRatingeMidfielderDefensive,
            m_jpRatingWinger,
            m_jpRatingWingerTowardsMiddle,
            m_jpRatingWingerOffensive,
            m_jpRatingWingerDefensive,
            m_jpRatingForward,
            m_jpRatingForwardTowardsWing,
            m_jpRatingForwardDefensive
    };

    private final byte[] playerPosition = new byte[]{
            IMatchRoleID.KEEPER,
            IMatchRoleID.CENTRAL_DEFENDER,
            IMatchRoleID.CENTRAL_DEFENDER_TOWING,
            IMatchRoleID.CENTRAL_DEFENDER_OFF,
            IMatchRoleID.BACK,
            IMatchRoleID.BACK_TOMID,
            IMatchRoleID.BACK_OFF,
            IMatchRoleID.BACK_DEF,
            IMatchRoleID.MIDFIELDER,
            IMatchRoleID.MIDFIELDER_TOWING,
            IMatchRoleID.MIDFIELDER_OFF,
            IMatchRoleID.MIDFIELDER_DEF,
            IMatchRoleID.WINGER,
            IMatchRoleID.WINGER_TOMID,
            IMatchRoleID.WINGER_OFF,
            IMatchRoleID.WINGER_DEF,
            IMatchRoleID.FORWARD,
            IMatchRoleID.FORWARD_TOWING,
            IMatchRoleID.FORWARD_DEF

    };

    private final ColorLabelEntry m_jpEPV = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
    private final JButton m_jbAddTempSpieler = new JButton(HOVerwaltung.instance().getLanguageString("AddTempspieler"));
    private final JButton m_jbRemoveTempSpieler = new JButton(HOVerwaltung.instance().getLanguageString("RemoveTempspieler"));
    private final JComboBox m_jcbErfahrung = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbFluegel = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbForm = new JComboBox(Helper.EINSTUFUNG_FORM);
    private final JComboBox m_jcbKondition = new JComboBox(Helper.EINSTUFUNG_KONDITION);
    private final JComboBox m_jcbPasspiel = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbSpielaufbau = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbStandard = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbTorschuss = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbTorwart = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbVerteidigung = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbSpeciality = new JComboBox(PlayerSpeciality.ITEMS);
    private final JComboBox m_jcbLoyalty = new JComboBox(PlayerAbility.ITEMS);
    private final JCheckBox m_jchHomegrown = new JCheckBox();
    private final JTextField jtfAge = new JTextField("17.0");
    private final JLabel m_jlErfahrung = new JLabel();
    private final JLabel m_jlFluegel = new JLabel();
    private final JLabel m_jlForm = new JLabel();
    private final JLabel m_jlKondition = new JLabel();
    private final JLabel m_jlName = new JLabel();
    private final JLabel m_jlPasspiel = new JLabel();
    private final JLabel m_jlSpielaufbau = new JLabel();
    private final JLabel m_jlStandard = new JLabel();
    private final JLabel m_jlTorschuss = new JLabel();
    private final JLabel m_jlTorwart = new JLabel();
    private final JLabel m_jlVerteidigung = new JLabel();
    private final JLabel m_jlLoyalty = new JLabel();
    private final JLabel m_jlHomeGrown = new JLabel();
    private Player m_clPlayer;
    private final Player tempPlayer = new Player();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerTrainingsSimulatorPanel object.
     */
    SpielerTrainingsSimulatorPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void setSpieler(Player player) {
        m_clPlayer = player;

        if (player != null) {
            setLabels();
            setCBs();

            //Remove for Temp player
            m_jbRemoveTempSpieler.setEnabled(player.getPlayerId() < 0);
        } else {
            resetLabels();
            resetCBs();
            m_jbRemoveTempSpieler.setEnabled(false);
        }
        invalidate();
        validate();
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(m_jbAddTempSpieler)) {
            final Player tempPlayer = new Player();
            tempPlayer.setHrfDate();
            tempPlayer.setNationalityId(HOVerwaltung.instance().getModel().getBasics().getLand());
            tempPlayer.setPlayerId(module.transfer.scout.TransferEingabePanel
                    .getNextTempSpielerID());
            tempPlayer.setLastName("Temp " + Math.abs(1000 + tempPlayer.getPlayerId()));
            tempPlayer.setAge(getAge());
            tempPlayer.setAgeDays(getAgeDays());
            tempPlayer.setExperience(((CBItem) m_jcbErfahrung.getSelectedItem()).getId());
            tempPlayer.setForm(((CBItem) m_jcbForm.getSelectedItem()).getId());
            tempPlayer.setStamina(((CBItem) m_jcbKondition.getSelectedItem()).getId());
            tempPlayer.setDefendingSkill(((CBItem) m_jcbVerteidigung.getSelectedItem()).getId());
            tempPlayer.setSpecialty(((CBItem) m_jcbSpeciality.getSelectedItem()).getId());
            tempPlayer.setScoringSkill(((CBItem) m_jcbTorschuss.getSelectedItem()).getId());
            tempPlayer.setGoalkeeperSkill(((CBItem) m_jcbTorwart.getSelectedItem()).getId());
            tempPlayer.setWingerSkill(((CBItem) m_jcbFluegel.getSelectedItem()).getId());
            tempPlayer.setPassingSkill(((CBItem) m_jcbPasspiel.getSelectedItem()).getId());
            tempPlayer.setSetPiecesSkill(((CBItem) m_jcbStandard.getSelectedItem()).getId());
            tempPlayer.setPlaymakingSkill(((CBItem) m_jcbSpielaufbau.getSelectedItem()).getId());
            tempPlayer.setLoyalty(((CBItem) m_jcbLoyalty.getSelectedItem()).getId());
            tempPlayer.setHomeGrown(m_jchHomegrown.isSelected());
            HOVerwaltung.instance().getModel().addPlayer(tempPlayer);
            RefreshManager.instance().doReInit();
            HOMainFrame.instance().showTab(IModule.PLAYEROVERVIEW);
        } else if (e.getSource().equals(m_jbRemoveTempSpieler)) {
            HOVerwaltung.instance().getModel().removePlayer(m_clPlayer);
            RefreshManager.instance().doReInit();
            HOMainFrame.instance().showTab(IModule.PLAYEROVERVIEW);
        }
    }

    public void itemStateChanged(ItemEvent itemEvent) {
        if ((itemEvent.getStateChange() == ItemEvent.SELECTED) || (itemEvent.getSource() == m_jchHomegrown)) {
            if (m_clPlayer != null) {
                setLabels();
            } else {
                resetLabels();
            }
        }
    }

    public void reInit() {
        setSpieler(null);
    }

    public void refresh() {
        setSpieler(null);
    }

    private void setCBs() {
        m_jlName.setText(m_clPlayer.getFullName());
        jtfAge.setText(m_clPlayer.getAge() + "." + m_clPlayer.getAgeDays());
        Helper.setComboBoxFromID(m_jcbForm, m_clPlayer.getForm());
        Helper.setComboBoxFromID(m_jcbErfahrung, m_clPlayer.getExperience());
        Helper.setComboBoxFromID(m_jcbKondition, m_clPlayer.getStamina());
        Helper.setComboBoxFromID(m_jcbSpielaufbau, m_clPlayer.getPlaymakingSkill());
        Helper.setComboBoxFromID(m_jcbFluegel, m_clPlayer.getWingerSkill());
        Helper.setComboBoxFromID(m_jcbTorschuss, m_clPlayer.getScoringSkill());
        Helper.setComboBoxFromID(m_jcbTorwart, m_clPlayer.getGoalkeeperSkill());
        Helper.setComboBoxFromID(m_jcbPasspiel, m_clPlayer.getPassingSkill());
        Helper.setComboBoxFromID(m_jcbVerteidigung, m_clPlayer.getDefendingSkill());
        Helper.setComboBoxFromID(m_jcbSpeciality, m_clPlayer.getSpecialty());
        Helper.setComboBoxFromID(m_jcbStandard, m_clPlayer.getSetPiecesSkill());
        Helper.setComboBoxFromID(m_jcbLoyalty, m_clPlayer.getLoyalty());
        m_jchHomegrown.setSelected(m_clPlayer.isHomeGrown());

        m_jlForm.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlKondition.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlErfahrung.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlSpielaufbau.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlFluegel.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlTorschuss.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlTorwart.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlPasspiel.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlVerteidigung.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlStandard.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlLoyalty.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlHomeGrown.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));

        m_jcbForm.setEnabled(true);
        m_jcbErfahrung.setEnabled(true);
        m_jcbKondition.setEnabled(true);
        m_jcbSpielaufbau.setEnabled(true);
        m_jcbFluegel.setEnabled(true);
        m_jcbTorschuss.setEnabled(true);
        m_jcbTorwart.setEnabled(true);
        m_jcbPasspiel.setEnabled(true);
        m_jcbVerteidigung.setEnabled(true);
        m_jcbSpeciality.setEnabled(true);
        m_jcbStandard.setEnabled(true);
        m_jcbLoyalty.setEnabled(true);
        m_jchHomegrown.setEnabled(true);
    }

    private void setLabels() {
        tempPlayer.setForm(((CBItem) m_jcbForm.getSelectedItem()).getId());
        tempPlayer.setExperience(((CBItem) m_jcbErfahrung.getSelectedItem()).getId());
        tempPlayer.setStamina(((CBItem) m_jcbKondition.getSelectedItem()).getId());
        tempPlayer.setDefendingSkill(((CBItem) m_jcbVerteidigung.getSelectedItem()).getId());
        tempPlayer.setSpecialty(((CBItem) m_jcbSpeciality.getSelectedItem()).getId());
        tempPlayer.setScoringSkill(((CBItem) m_jcbTorschuss.getSelectedItem()).getId());
        tempPlayer.setGoalkeeperSkill(((CBItem) m_jcbTorwart.getSelectedItem()).getId());
        tempPlayer.setWingerSkill(((CBItem) m_jcbFluegel.getSelectedItem()).getId());
        tempPlayer.setPassingSkill(((CBItem) m_jcbPasspiel.getSelectedItem()).getId());
        tempPlayer.setSetPiecesSkill(((CBItem) m_jcbStandard.getSelectedItem()).getId());
        tempPlayer.setPlaymakingSkill(((CBItem) m_jcbSpielaufbau.getSelectedItem()).getId());
        tempPlayer.setLoyalty(((CBItem) m_jcbLoyalty.getSelectedItem()).getId());
        tempPlayer.setHomeGrown(m_jchHomegrown.isSelected());

        m_jlForm.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getForm() - m_clPlayer.getForm(), true));
        m_jlKondition.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getStamina() - m_clPlayer.getStamina(), true));
        m_jlErfahrung.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getExperience() - m_clPlayer.getExperience(), true));
        m_jlSpielaufbau.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getPlaymakingSkill() - m_clPlayer.getPlaymakingSkill(), true));
        m_jlFluegel.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getWingerSkill() - m_clPlayer.getWingerSkill(), true));
        m_jlTorschuss.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getScoringSkill() - m_clPlayer.getScoringSkill(), true));
        m_jlTorwart.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getGoalkeeperSkill() - m_clPlayer.getGoalkeeperSkill(), true));
        m_jlPasspiel.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getPassingSkill() - m_clPlayer.getPassingSkill(), true));
        m_jlVerteidigung.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getDefendingSkill() - m_clPlayer.getDefendingSkill(), true));
        m_jlStandard.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getSetPiecesSkill() - m_clPlayer.getSetPiecesSkill(), true));
        m_jlLoyalty.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempPlayer.getLoyalty() - m_clPlayer.getLoyalty(), true));
        int hg = 0;
        if (m_clPlayer.isHomeGrown() != tempPlayer.isHomeGrown()) {
            if (m_clPlayer.isHomeGrown())
                hg = -1;
            else
                hg = 1;
        }
        m_jlHomeGrown.setIcon(ImageUtilities.getImageIcon4Veraenderung(hg, true));

        var r = tempPlayer.getIdealPositionRating();
        var idealPosition = tempPlayer.getIdealPosition();
        m_jpBestPos.setText(MatchRoleID.getNameForPosition(idealPosition)
                + " (" + Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals)
                .format(r) + ")");

        for (int i = 0; i < playerPositionValues.length; i++) {
            showWithCompare(playerPositionValues[i], playerPosition[i]);
        }

        tempPlayer.setAge(getAge());
        tempPlayer.setAgeDays(getAgeDays());
        tempPlayer.setLeadership(m_clPlayer.getLeadership());
        tempPlayer.setSpecialty(m_clPlayer.getSpecialty());
    }

    private void showWithCompare(DoubleLabelEntries labelEntry, byte playerPosition) {
        var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();

        var playerAbsoluteValue = ratingPredictionModel.getPlayerMatchAverageRating(m_clPlayer, playerPosition);
        var tmpAbsoluteValue = ratingPredictionModel.getPlayerMatchAverageRating(tempPlayer, playerPosition);
        //var tmpRelativeValue = tempPlayer.calcPosValue(playerPosition, true, true, null, false);
        var nbDecimals = core.model.UserParameter.instance().nbDecimals;
//        var leftLabelText = Helper.getNumberFormat(false, nbDecimals).format(tmpRelativeValue) + "%  " +
//                Helper.getNumberFormat(false, nbDecimals).format(tmpAbsoluteValue);
        var leftLabelText = Helper.getNumberFormat(false, nbDecimals).format(tmpAbsoluteValue);
        labelEntry.getLeft().setText(leftLabelText);

        var alternativePosition = tempPlayer.getAlternativeBestPositions();
        for (byte altPos : alternativePosition) {
            if (altPos == playerPosition) {
                labelEntry.getLeft().setBold(true);
                break;
            } else {
                labelEntry.getLeft().setBold(false);
            }
        }

        labelEntry.getRight().setSpecialNumber((float) (tmpAbsoluteValue-playerAbsoluteValue), false);
    }

    private int getAge() {
        int age = 17;
        if (m_clPlayer != null) {
            age = m_clPlayer.getAge();
        }
        try {
            age = Integer.parseInt(jtfAge.getText().replaceFirst("\\..*", ""));
        } catch (NumberFormatException ignored) {
        }
        return age;
    }

    private int getAgeDays() {
        int age = 0;
        if (m_clPlayer != null) {
            age = m_clPlayer.getAgeDays();
        }
        try {
            age = Integer.parseInt(jtfAge.getText().replaceFirst(".*\\.", ""));
        } catch (NumberFormatException ignored) {
        }
        return age;
    }

    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(2, 2, 2, 2);

        JPanel panel;
        JLabel label;

        setLayout(layout);

        //Eingaben-------
        final GridBagLayout eingabenLayout = new GridBagLayout();
        final GridBagConstraints eingabenconstraints = new GridBagConstraints();
        eingabenconstraints.anchor = GridBagConstraints.WEST;
        eingabenconstraints.fill = GridBagConstraints.NONE;
        eingabenconstraints.weightx = 0.0;
        eingabenconstraints.weighty = 0.0;
        eingabenconstraints.insets = new Insets(4, 4, 4, 4);

        panel = new ImagePanel();
        panel.setLayout(eingabenLayout);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.name"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 0;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 0;
        eingabenconstraints.gridwidth = 2;
        eingabenLayout.setConstraints(m_jlName, eingabenconstraints);
        panel.add(m_jlName);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.speciality"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 1;
        eingabenconstraints.gridwidth = 1;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbSpeciality.setPreferredSize(CBSIZE);
        m_jcbSpeciality.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 1;
        eingabenLayout.setConstraints(m_jcbSpeciality, eingabenconstraints);
        panel.add(m_jcbSpeciality);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.age"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 1;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        jtfAge.setPreferredSize(CBSIZE);
        jtfAge.addFocusListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 1;
        eingabenLayout.setConstraints(jtfAge, eingabenconstraints);
        panel.add(jtfAge);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.experience"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 2;
        eingabenconstraints.gridwidth = 1;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbErfahrung.setPreferredSize(CBSIZE);
        m_jcbErfahrung.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 2;
        eingabenLayout.setConstraints(m_jcbErfahrung, eingabenconstraints);
        panel.add(m_jcbErfahrung);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 2;
        m_jlErfahrung.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlErfahrung, eingabenconstraints);
        panel.add(m_jlErfahrung);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.form"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 2;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbForm.setPreferredSize(CBSIZE);
        m_jcbForm.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 2;
        eingabenLayout.setConstraints(m_jcbForm, eingabenconstraints);
        panel.add(m_jcbForm);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 2;
        m_jlForm.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlForm, eingabenconstraints);
        panel.add(m_jlForm);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 3;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbKondition.setPreferredSize(CBSIZE);
        m_jcbKondition.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 3;
        eingabenLayout.setConstraints(m_jcbKondition, eingabenconstraints);
        panel.add(m_jcbKondition);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 3;
        m_jlKondition.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlKondition, eingabenconstraints);
        panel.add(m_jlKondition);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 3;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbTorwart.setPreferredSize(CBSIZE);
        m_jcbTorwart.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 3;
        eingabenLayout.setConstraints(m_jcbTorwart, eingabenconstraints);
        panel.add(m_jcbTorwart);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 3;
        m_jlTorwart.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlTorwart, eingabenconstraints);
        panel.add(m_jlTorwart);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 4;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbSpielaufbau.setPreferredSize(CBSIZE);
        m_jcbSpielaufbau.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 4;
        eingabenLayout.setConstraints(m_jcbSpielaufbau, eingabenconstraints);
        panel.add(m_jcbSpielaufbau);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 4;
        m_jlSpielaufbau.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlSpielaufbau, eingabenconstraints);
        panel.add(m_jlSpielaufbau);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.passing"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 4;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbPasspiel.setPreferredSize(CBSIZE);
        m_jcbPasspiel.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 4;
        eingabenLayout.setConstraints(m_jcbPasspiel, eingabenconstraints);
        panel.add(m_jcbPasspiel);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 4;
        m_jlPasspiel.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlPasspiel, eingabenconstraints);
        panel.add(m_jlPasspiel);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.winger"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 5;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbFluegel.setPreferredSize(CBSIZE);
        m_jcbFluegel.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 5;
        eingabenLayout.setConstraints(m_jcbFluegel, eingabenconstraints);
        panel.add(m_jcbFluegel);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 5;
        m_jlFluegel.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlFluegel, eingabenconstraints);
        panel.add(m_jlFluegel);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.defending"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 5;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbVerteidigung.setPreferredSize(CBSIZE);
        m_jcbVerteidigung.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 5;
        eingabenLayout.setConstraints(m_jcbVerteidigung, eingabenconstraints);
        panel.add(m_jcbVerteidigung);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 5;
        m_jlVerteidigung.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlVerteidigung, eingabenconstraints);
        panel.add(m_jlVerteidigung);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 6;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbTorschuss.setPreferredSize(CBSIZE);
        m_jcbTorschuss.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 6;
        eingabenLayout.setConstraints(m_jcbTorschuss, eingabenconstraints);
        panel.add(m_jcbTorschuss);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 6;
        m_jlTorschuss.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlTorschuss, eingabenconstraints);
        panel.add(m_jlTorschuss);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 6;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbStandard.setPreferredSize(CBSIZE);
        m_jcbStandard.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 6;
        eingabenLayout.setConstraints(m_jcbStandard, eingabenconstraints);
        panel.add(m_jcbStandard);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 6;
        m_jlStandard.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlStandard, eingabenconstraints);
        panel.add(m_jlStandard);

        // Add loyalty label and combo
        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.loyalty"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 7;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbLoyalty.setPreferredSize(CBSIZE);
        m_jcbLoyalty.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 7;
        eingabenLayout.setConstraints(m_jcbLoyalty, eingabenconstraints);
        panel.add(m_jcbLoyalty);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 7;
        m_jlLoyalty.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlLoyalty, eingabenconstraints);
        panel.add(m_jlLoyalty);

        // Add homegrown label and checkbox
        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.motherclub"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 7;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jchHomegrown.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 7;
        eingabenLayout.setConstraints(m_jchHomegrown, eingabenconstraints);
        panel.add(m_jchHomegrown);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 7;
        m_jlHomeGrown.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlHomeGrown, eingabenconstraints);
        panel.add(m_jlHomeGrown);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        add(panel);

        //Button--------
        panel = new JPanel();
        m_jbAddTempSpieler.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_add_tempspieler"));
        m_jbAddTempSpieler.addActionListener(this);
        panel.add(m_jbAddTempSpieler);
        m_jbRemoveTempSpieler.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_remove_tempspieler"));
        m_jbRemoveTempSpieler.addActionListener(this);
        m_jbRemoveTempSpieler.setEnabled(false);
        panel.add(m_jbRemoveTempSpieler);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        add(panel);

        //Werte---------
        panel = new ImagePanel();
        panel.setLayout(new GridLayout(21, 2, 2, 2));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("BestePosition"));
        panel.add(label);
        panel.add(m_jpBestPos.getComponent(false));

        for (int i = 0; i < playerPositionValues.length; i++) {
            label = new JLabel(MatchRoleID.getNameForPosition(playerPosition[i]));
            label.setToolTipText(MatchRoleID.getNameForPosition(playerPosition[i]));
            panel.add(label);
            panel.add(playerPositionValues[i].getComponent(false));
        }

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Marktwert"));
        panel.add(label);
        panel.add(m_jpEPV.getComponent(false));

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        layout.setConstraints(panel, constraints);
        add(panel);
    }

    private void resetCBs() {
        m_jlName.setText("");
        jtfAge.setText("17.0");
        m_jlForm.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlKondition.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlErfahrung.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlSpielaufbau.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlFluegel.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlTorschuss.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlTorwart.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlPasspiel.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlVerteidigung.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlStandard.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlLoyalty.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));
        m_jlHomeGrown.setIcon(ImageUtilities.getImageIcon4Veraenderung(0, true));

        resetCB(m_jcbForm);
        resetCB(m_jcbErfahrung);
        resetCB(m_jcbKondition);
        resetCB(m_jcbSpielaufbau);
        resetCB(m_jcbFluegel);
        resetCB(m_jcbTorschuss);
        resetCB(m_jcbTorwart);
        resetCB(m_jcbPasspiel);
        resetCB(m_jcbVerteidigung);
        resetCB(m_jcbSpeciality);
        resetCB(m_jcbStandard);
        resetCB(m_jcbLoyalty);
        resetCheckBox(m_jchHomegrown);
    }

    private void resetCheckBox(JCheckBox chk) {
        chk.setSelected(false);
        chk.setEnabled(false);
    }

    private void resetCB(JComboBox cb) {
        Helper.setComboBoxFromID(cb, PlayerAbility.DISASTROUS);
        cb.setEnabled(false);
    }

    private void resetLabels() {
        m_jpBestPos.clear();
        for (DoubleLabelEntries playerPositionValue : playerPositionValues) {
            playerPositionValue.clear();
        }
        m_jpEPV.clear();
    }

    public void focusGained(FocusEvent arg0) {
    }

    public void focusLost(FocusEvent e) {
        if (e.getSource().equals(jtfAge)) {
            if (m_clPlayer != null) {
                setLabels();
            } else {
                resetLabels();
            }
        }
    }
}
