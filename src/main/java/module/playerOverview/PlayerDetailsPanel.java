package module.playerOverview;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerAggressiveness;
import core.constants.player.PlayerAgreeability;
import core.constants.player.PlayerHonesty;
import core.constants.player.PlayerSkill;
import core.constants.player.PlayerSpeciality;
import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.entry.MatchDateTableEntry;
import core.gui.comp.entry.RatingTableEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.renderer.SmilieListCellRenderer;
import core.gui.theme.*;
import core.model.FactorObject;
import core.model.FormulaFactors;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.enums.MatchType;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.module.IModule;
import core.net.HattrickLink;
import core.util.DateTimeUtils;
import core.util.HTDatetime;
import core.util.Helper;
import module.statistics.StatistikMainPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import static core.gui.theme.HOIconName.*;
import static core.gui.theme.ImageUtilities.getSvgIcon;
import static core.model.player.IMatchRoleID.UNKNOWN;
import static core.model.player.IMatchRoleID.UNSELECTABLE;


/**
 * Shows player details for the selected player
 */
public final class PlayerDetailsPanel extends ImagePanel implements Refreshable, ItemListener, ActionListener {

    private Color BGcolor = ThemeManager.getColor(HOColorName.PANEL_BG);
    private Color FGcolor = ColorLabelEntry.FG_STANDARD;
    private Color BORDER_COLOR = ThemeManager.getColor(HOColorName.PLAYER_DETAILS_BAR_BORDER_COLOR);
    private PlayerOverviewTable m_playerOverviewTable;
    private Font f;
    private JLabel jlName = new JLabel("");
    private JLabel m_jlPlayerDescription = new JLabel("");
    private final JPanel jpPlayerGeneral = new JPanel();
    private final JPanel jpPlayerSkill = new JPanel();
    private final JPanel jpPlayerGoalsStats = new JPanel();
    private final JPanel jpPlayerOtherInfos = new JPanel();
    private JLabel jlPlayerAvatar = new JLabel();
    private JLabel jlNationality = new JLabel();
    private JLabel m_jlSpecialty = new JLabel();
    private final JLabel m_jlInTeamSince = new JLabel();
    private JProgressBar jpbForm, jpbStamina, jpbGK, jpbDE, jpbPM, jpbWI, jpbPS, jpbSC, jpbSP;
    private final ColorLabelEntry m_jclPMchange = new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.CENTER);
    private final ColorLabelEntry m_jclSCchange = new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.CENTER);
    private final ColorLabelEntry m_jclWIchange = new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.CENTER);
    private final ColorLabelEntry m_jclDEchange = new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.CENTER);
    private final ColorLabelEntry m_jclGKchange = new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.CENTER);
    private final ColorLabelEntry m_jclPSchange = new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.CENTER);
    private final ColorLabelEntry m_jclSPchange = new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.CENTER);
    private final ColorLabelEntry m_jclFormChange = new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.CENTER);
    private final ColorLabelEntry m_jclStaminaChange = new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.CENTER);
    private final JLabel m_jlCareerGoals = new JLabel("");
    private final JLabel m_jlTeamGoals = new JLabel("");
    private final JLabel m_jlHattricks = new JLabel("");
    private final JLabel m_jlSeasonSeriesGoals = new JLabel("");
    private final JLabel m_jlSeasonCupGoals = new JLabel("");
    private final JLabel m_jlBestPosition = new JLabel("");
    private final JComboBox m_jcbSquad = new JComboBox(GroupTeamFactory.TEAMS_GROUPS);
    private final JComboBox m_jcbInformation = new JComboBox(SMILEYS);

    private RatingTableEntry m_jpRating = new RatingTableEntry();
    private DoubleLabelEntries m_jpLastMatchRating = new DoubleLabelEntries(
            new RatingTableEntry(),
            new MatchDateTableEntry(null, MatchType.NONE),
            new GridBagLayout());
    private JLabel m_lastMatchLink = null;

    private PlayerStatusLabelEntry m_jpStatus = new PlayerStatusLabelEntry(BGcolor, true);
    private final DoubleLabelEntries m_jllWage = new DoubleLabelEntries(new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.LEFT), new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.RIGHT));
    private final DoubleLabelEntries m_jllTSI = new DoubleLabelEntries(new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.LEFT), new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.RIGHT));
    private JComboBox m_jcbUserBestPosition = new JComboBox(MatchRoleID.POSITIONEN);

    private final JButton m_jbStatistics = new JButton(getSvgIcon(GOTOSTATISTIK));
    private final JButton m_jbAnalysisTop = new JButton(getSvgIcon(GOTOANALYSETOP));
    private final JButton m_jbAnalysisBottom = new JButton(getSvgIcon(GOTOANALYSEBOTTOM));
    private final JButton m_jbOffsets = new JButton(getSvgIcon(HOIconName.OFFSET));

    private Player m_clPlayer;
    private Player m_clComparisonPlayer;

    /**
     * Constructor
     */
    protected PlayerDetailsPanel(PlayerOverviewTable playerOverviewTable) {
        m_playerOverviewTable = playerOverviewTable;
        initComponents();
        RefreshManager.instance().registerRefreshable(this);
    }


    /**
     * Set the player to be shown
     */
    public void setPlayer(Player player) {
        m_clPlayer = player;
        if (m_clPlayer != null) {
            findComparisonPlayer();
            setLabels();
        } else {
            resetLabels();
        }
        invalidate();
        validate();
        repaint();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionevent) {
        if (actionevent.getSource().equals(m_jbStatistics)) {
            HOMainFrame.instance().showTab(IModule.STATISTICS);
            ((StatistikMainPanel) HOMainFrame.instance().getTabbedPane().getModulePanel(IModule.STATISTICS)).setShowSpieler(m_clPlayer.getPlayerID());
        } else if (actionevent.getSource().equals(m_jbAnalysisTop)) {
            HOMainFrame.instance().showTab(IModule.PLAYERANALYSIS);
            HOMainFrame.instance().getSpielerAnalyseMainPanel().setSpieler4Top(m_clPlayer.getPlayerID());
        } else if (actionevent.getSource().equals(m_jbAnalysisBottom)) {
            HOMainFrame.instance().showTab(IModule.PLAYERANALYSIS);
            HOMainFrame.instance().getSpielerAnalyseMainPanel().setSpieler4Bottom(m_clPlayer.getPlayerID());
        } else if (actionevent.getSource().equals(m_jbOffsets)) {
            new PlayerSubskillOffsetDialog(HOMainFrame.instance(), m_clPlayer).setVisible(true);
        }
    }


    @Override
    public void itemStateChanged(java.awt.event.ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            if (m_clPlayer != null) {
                if (itemEvent.getSource().equals(m_jcbSquad)) {
                    m_clPlayer.setTeamInfoSmilie(m_jcbSquad.getSelectedItem().toString());
                } else if (itemEvent.getSource().equals(m_jcbInformation)) {
                    m_clPlayer.setManuellerSmilie(m_jcbInformation.getSelectedItem().toString());
                } else if (itemEvent.getSource().equals(m_jcbUserBestPosition)) {
                    m_clPlayer.setUserPosFlag((byte) ((core.datatype.CBItem) m_jcbUserBestPosition
                            .getSelectedItem()).getId());
                }
                HOMainFrame.instance().getSpielerUebersichtPanel().update();
            }
        }
    }

    /**
     * set the player to compare and refresh the display
     */
    @Override
    public void reInit() {
        if (m_clPlayer != null) {
            findComparisonPlayer();
        }
        setPlayer(null);
    }


    @Override
    public void refresh() {
        setPlayer(m_clPlayer);
    }

    private void setLabels() {
        Icon playerAvatar = ThemeManager.instance().getPlayerAvatar(m_clPlayer.getPlayerID());
        jlPlayerAvatar.setIcon(playerAvatar);
        m_jpLastMatchRating.clear();
        if (m_clPlayer.getLastMatchRating() > 0) {
            MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(m_clPlayer.getLastMatchId(), null);
            if (info != null) {
                ((RatingTableEntry) m_jpLastMatchRating.getTableEntryLeft()).setRating((float)m_clPlayer.getLastMatchRating());
                ((MatchDateTableEntry) m_jpLastMatchRating.getTableEntryRight()).setMatchInfo(m_clPlayer.getLastMatchDate(), info.getMatchTypeExtended());
            }
        }
        jlNationality.setIcon(ImageUtilities.getCountryFlagIcon(m_clPlayer.getNationalityAsInt()));
        jlNationality.setToolTipText(m_clPlayer.getNationalityAsString());
        jlNationality.setText(m_clPlayer.getAgeStringFull());


        //Rating
        if (m_clPlayer.getRating() > 0) {
            m_jpRating.setRating(m_clPlayer.getRating());
        } else {
            m_jpRating.setRating(m_clPlayer.getPreviousRating());
        }

        setCB(m_jcbSquad, m_clPlayer.getTeamGroup());
        setCB(m_jcbInformation, m_clPlayer.getInfoSmiley());



        m_jpStatus.setPlayer(m_clPlayer);
        m_jcbUserBestPosition.removeItemListener(this);
        m_jcbUserBestPosition.removeAllItems();
        for (CBItem item : getPositions()) {
            m_jcbUserBestPosition.addItem(item);
        }
        Helper.setComboBoxFromID(m_jcbUserBestPosition, m_clPlayer.getUserPosFlag());
        m_jcbUserBestPosition.addItemListener(this);
        final int salary = (int) (m_clPlayer.getSalary() / core.model.UserParameter.instance().FXrate);
        final String salarytext = Helper.getNumberFormat(true, 0).format(salary);
        final String tsitext = Helper.getNumberFormat(false, 0).format(m_clPlayer.getTSI());
        if (m_clComparisonPlayer == null) {
            m_jllWage.getLeft().setText(salarytext);
            m_jllWage.getRight().clear();
            m_jllTSI.getLeft().setText(tsitext);
            m_jllTSI.getRight().clear();
            m_jclFormChange.clear();

            m_jclStaminaChange.clear();
            m_jclGKchange.clear();
            m_jclDEchange.clear();

            m_jclPMchange.clear();
            m_jclPSchange.clear();

            m_jclWIchange.clear();
            m_jclSPchange.clear();

            m_jclSCchange.clear();

        }
        else {
            final int previousSalary = (int) (m_clComparisonPlayer.getSalary() / core.model.UserParameter.instance().FXrate);
            m_jllWage.getLeft().setText(salarytext);
            m_jllWage.getRight().setSpecialNumber(salary - previousSalary, true);
            m_jllTSI.getLeft().setText(tsitext);
            m_jllTSI.getRight().setSpecialNumber(m_clPlayer.getTSI() - m_clComparisonPlayer.getTSI(), false);
            m_jclFormChange.setGraphicalChangeValue(m_clPlayer.getForm()
                    - m_clComparisonPlayer.getForm(), !m_clComparisonPlayer.isOld(), true);

            m_jclStaminaChange.setGraphicalChangeValue(m_clPlayer.getStamina()
                    - m_clComparisonPlayer.getStamina(), !m_clComparisonPlayer.isOld(), true);
            m_jclGKchange.setGraphicalChangeValue(m_clPlayer.getGKskill()
                            - m_clComparisonPlayer.getGKskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.KEEPER)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.KEEPER),
                    !m_clComparisonPlayer.isOld(), true);
            m_jclDEchange.setGraphicalChangeValue(m_clPlayer.getDEFskill()
                            - m_clComparisonPlayer.getDEFskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.DEFENDING)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.DEFENDING),
                    !m_clComparisonPlayer.isOld(), true);

            m_jclPMchange.setGraphicalChangeValue(m_clPlayer.getPMskill()
                            - m_clComparisonPlayer.getPMskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.PLAYMAKING)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.PLAYMAKING),
                    !m_clComparisonPlayer.isOld(), true);
            m_jclPSchange.setGraphicalChangeValue(m_clPlayer.getPSskill()
                            - m_clComparisonPlayer.getPSskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.PASSING)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.PASSING),
                    !m_clComparisonPlayer.isOld(), true);

            m_jclWIchange.setGraphicalChangeValue(m_clPlayer.getWIskill()
                            - m_clComparisonPlayer.getWIskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.WINGER)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.WINGER),
                    !m_clComparisonPlayer.isOld(), true);
            m_jclSPchange.setGraphicalChangeValue(m_clPlayer.getSPskill()
                            - m_clComparisonPlayer.getSPskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.SET_PIECES)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.SET_PIECES),
                    !m_clComparisonPlayer.isOld(), true);

            m_jclSCchange.setGraphicalChangeValue(m_clPlayer.getSCskill()
                            - m_clComparisonPlayer.getSCskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.SCORING)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.SCORING),
                    !m_clComparisonPlayer.isOld(), true);

        }

        m_jlCareerGoals.setText(m_clPlayer.getAllOfficialGoals() + "");
        m_jlTeamGoals.setText(m_clPlayer.getGoalsCurrentTeam() +"");
        m_jlHattricks.setText(m_clPlayer.getHattrick() + "");
        m_jlSeasonSeriesGoals.setText(m_clPlayer.getSeasonSeriesGoal() + "");
        m_jlSeasonCupGoals.setText(m_clPlayer.getSeasonCupGoal() + "");

        m_jlBestPosition.setText(MatchRoleID.getNameForPosition(m_clPlayer.getIdealPosition())
                + " ("
                + Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals).format(
                m_clPlayer.calcPosValue(m_clPlayer.getIdealPosition(), true, null, false))
                + ")");

        int iSpecialty = m_clPlayer.getPlayerSpecialty();
        if (iSpecialty == 0) {
            m_jlSpecialty.setText("-");
        }
        else{
            m_jlSpecialty.setText(PlayerSpeciality.toString(iSpecialty));
        }
        m_jlSpecialty.setIcon(ImageUtilities.getSmallPlayerSpecialtyIcon(HOIconName.SPECIALTIES[iSpecialty]));

        String playerDescription = "<html>" + Helper.getTranslation("ls.player_details.desc1", PlayerAgreeability.toString(m_clPlayer.getCharakter()), PlayerAggressiveness.toString(m_clPlayer.getAgressivitaet()), PlayerHonesty.toString(m_clPlayer.getAnsehen()));
        playerDescription += "<br>";
        playerDescription += Helper.getTranslation("ls.player_details.desc2", PlayerAbility.getNameForSkill(m_clPlayer.getExperience(), true, false, 0), PlayerAbility.getNameForSkill(m_clPlayer.getLeadership(), true, false, 0), PlayerAbility.getNameForSkill(m_clPlayer.getLoyalty(), true, false, 0));
        playerDescription += "</html>";
        m_jlPlayerDescription.setText(playerDescription);

        String playerName = "<html><B><span style='font-size:16px'>" + m_clPlayer.getFullName() + "</span></html></B></html>";
        jlName.setText(playerName);

        HTDatetime dtArrivalDate = new HTDatetime(m_clPlayer.getArrivalDate());
        String arrivalDate = DateTimeUtils.FormatLongDate(dtArrivalDate.getUserLocalizedTimeAsTimestamp());

        m_jlInTeamSince.setText(Helper.getTranslation("ImTeamSeit") + " " + arrivalDate);
        if (m_clPlayer.isHomeGrown()) m_jlInTeamSince.setIcon(ThemeManager.getIcon(HOIconName.HOMEGROWN));

        m_jbStatistics.setEnabled(true);
        m_jbAnalysisTop.setEnabled(true);
        m_jbAnalysisBottom.setEnabled(true);
        m_jbOffsets.setEnabled(true);

        formatBar(jpbForm, m_clPlayer.getForm(), true);
        formatBar(jpbStamina, m_clPlayer.getStamina(), true);

        formatBar(jpbGK, m_clPlayer.getSkill(PlayerSkill.KEEPER, true));
        formatBar(jpbDE, m_clPlayer.getSkill(PlayerSkill.DEFENDING, true));
        formatBar(jpbPM, m_clPlayer.getSkill(PlayerSkill.PLAYMAKING, true));
        formatBar(jpbWI, m_clPlayer.getSkill(PlayerSkill.WINGER, true));
        formatBar(jpbPS, m_clPlayer.getSkill(PlayerSkill.PASSING, true));
        formatBar(jpbSC, m_clPlayer.getSkill(PlayerSkill.SCORING, true));
        formatBar(jpbSP, m_clPlayer.getSkill(PlayerSkill.SET_PIECES, true));

    }

    private Player getComparisonPlayerFirstHRF(Player player) {
        return core.db.DBManager.instance()
                .getSpielerFirstHRF(player.getPlayerID());
    }

    private void findComparisonPlayer() {
        final int id = m_clPlayer.getPlayerID();
        for (int i = 0;
             (SpielerTrainingsVergleichsPanel.getVergleichsPlayer() != null)
                     && (i < SpielerTrainingsVergleichsPanel.getVergleichsPlayer().size()); i++) {
            Player comparisonPlayer = (Player) SpielerTrainingsVergleichsPanel
                    .getVergleichsPlayer().get(i);
            if (comparisonPlayer.getPlayerID() == id) {
                // Found it
                m_clComparisonPlayer = comparisonPlayer;
                return;
            }
        }
        if (SpielerTrainingsVergleichsPanel.isVergleichsMarkierung()) {
            m_clComparisonPlayer = getComparisonPlayerFirstHRF(m_clPlayer);
            return;
        }
        //Not found
        m_clComparisonPlayer = null;
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        f =  new JLabel("").getFont();
        f = f.deriveFont(f.getStyle() | Font.BOLD);

        final JPanel panel = new ImagePanel();
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(1, 2, 1, 1);
        panel.setLayout(layout);

        constraints.gridwidth = 10;
        setPosition(constraints, 0, 0);
        layout.setConstraints(jlName, constraints);
        panel.add(jlName);

        setPosition(constraints, 0, 1);
        layout.setConstraints(jlNationality, constraints);
        panel.add(jlNationality);

        // Player description  =====================
        setPosition(constraints, 0, 2);
        layout.setConstraints(m_jlPlayerDescription, constraints);
        panel.add(m_jlPlayerDescription);

        // In the team since  =====================
        m_jlInTeamSince.setHorizontalTextPosition(SwingConstants.LEFT);
        setPosition(constraints, 0, 3);
        layout.setConstraints(m_jlInTeamSince, constraints);
        panel.add(m_jlInTeamSince);

        // create player general panel ====================================================================
        final GridBagLayout layoutPlayerGeneralPanel = new GridBagLayout();
        final GridBagConstraints constraintsPlayerGeneralPanel = new GridBagConstraints();
        constraintsPlayerGeneralPanel.fill = GridBagConstraints.BOTH;
        jpPlayerGeneral.setLayout(layoutPlayerGeneralPanel);

        jlPlayerAvatar = new JLabel("");
        constraintsPlayerGeneralPanel.gridx = 0;
        constraintsPlayerGeneralPanel.gridy = 0;
        constraintsPlayerGeneralPanel.gridheight = 8;
        layoutPlayerGeneralPanel.setConstraints(jlPlayerAvatar, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(jlPlayerAvatar);

        constraintsPlayerGeneralPanel.gridx = 1;
        constraintsPlayerGeneralPanel.gridheight = 1;

        JLabel label = createLabel("Status");
        constraintsPlayerGeneralPanel.gridy = 1;
        constraintsPlayerGeneralPanel.weighty = 0.0;
        constraintsPlayerGeneralPanel.insets = new Insets(0,10,5,0);
        layoutPlayerGeneralPanel.setConstraints(label, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(label);

        constraintsPlayerGeneralPanel.gridx = 2;
        JComponent jcPlayerStatus = m_jpStatus.getComponent(false);
        layoutPlayerGeneralPanel.setConstraints(jcPlayerStatus, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(jcPlayerStatus);

        label = createLabel("ls.player.tsi");
        constraintsPlayerGeneralPanel.gridx = 1;
        constraintsPlayerGeneralPanel.gridy = 2;
        layoutPlayerGeneralPanel.setConstraints(label, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(label);

        constraintsPlayerGeneralPanel.gridx = 2;
        constraintsPlayerGeneralPanel.gridy = 2;
        layoutPlayerGeneralPanel.setConstraints(m_jllTSI.getComponent(false), constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(m_jllTSI.getComponent(false));

        label = createLabel("ls.player.wage");
        constraintsPlayerGeneralPanel.gridx = 1;
        constraintsPlayerGeneralPanel.gridy = 3;
        layoutPlayerGeneralPanel.setConstraints(label, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(label);

        constraintsPlayerGeneralPanel.gridx = 2;
        constraintsPlayerGeneralPanel.gridy = 3;
        layoutPlayerGeneralPanel.setConstraints(m_jllWage.getComponent(false), constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(m_jllWage.getComponent(false));

        label = createLabel("ls.player.speciality");
        constraintsPlayerGeneralPanel.gridx = 1;
        constraintsPlayerGeneralPanel.gridy = 4;
        layoutPlayerGeneralPanel.setConstraints(label, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(label);

        constraintsPlayerGeneralPanel.gridx = 2;
        constraintsPlayerGeneralPanel.gridy = 4;
        layoutPlayerGeneralPanel.setConstraints(m_jlSpecialty, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(m_jlSpecialty);

        label = createLabel("ls.player.form");
        constraintsPlayerGeneralPanel.gridx = 1;
        constraintsPlayerGeneralPanel.gridy = 5;
        layoutPlayerGeneralPanel.setConstraints(label, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(label);

        jpbForm = createBar(8);
        constraintsPlayerGeneralPanel.gridx = 2;
        constraintsPlayerGeneralPanel.gridy = 5;
        layoutPlayerGeneralPanel.setConstraints(jpbForm, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(jpbForm);

        constraintsPlayerGeneralPanel.insets = new Insets(0,10,0,0);

        label = createLabel("ls.player.skill.stamina");
        constraintsPlayerGeneralPanel.gridx = 1;
        constraintsPlayerGeneralPanel.gridy = 6;
        layoutPlayerGeneralPanel.setConstraints(label, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(label);

        jpbStamina = createBar(9);
        constraintsPlayerGeneralPanel.gridx = 2;
        constraintsPlayerGeneralPanel.gridy = 6;
        layoutPlayerGeneralPanel.setConstraints(jpbStamina, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(jpbStamina);

        constraints.gridwidth = 4;
        setPosition(constraints, 0, 4);
        layout.setConstraints(jpPlayerGeneral, constraints);
        panel.add(jpPlayerGeneral);


        // create player skill panel ====================================================================
        final GridBagLayout layoutPlayerSkilllPanel = new GridBagLayout();
        final GridBagConstraints constraintsPlayerSkillPanel = new GridBagConstraints();
        constraintsPlayerSkillPanel.fill = GridBagConstraints.BOTH;
        jpPlayerSkill.setLayout(layoutPlayerSkilllPanel);

        constraintsPlayerSkillPanel.insets = new Insets(0,10,5,0);

        label = createLabel("ls.player.skill.keeper");
        constraintsPlayerSkillPanel.gridx = 0;
        constraintsPlayerSkillPanel.gridy = 0;
        layoutPlayerSkilllPanel.setConstraints(label, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(label);

        jpbGK = createBar(20);
        constraintsPlayerSkillPanel.gridx = 1;
        layoutPlayerSkilllPanel.setConstraints(jpbGK, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(jpbGK);

        constraintsPlayerSkillPanel.gridx = 2;
        layoutPlayerSkilllPanel.setConstraints(m_jclGKchange, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(m_jclGKchange);

        label = createLabel("ls.player.skill.defending");
        constraintsPlayerSkillPanel.gridx = 0;
        constraintsPlayerSkillPanel.gridy = 1;
        layoutPlayerSkilllPanel.setConstraints(label, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(label);

        jpbDE = createBar(20);
        constraintsPlayerSkillPanel.gridx = 1;
        layoutPlayerSkilllPanel.setConstraints(jpbDE, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(jpbDE);

        constraintsPlayerSkillPanel.gridx = 2;
        layoutPlayerSkilllPanel.setConstraints(m_jclDEchange, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(m_jclDEchange);


        label = createLabel("ls.player.skill.playmaking");
        constraintsPlayerSkillPanel.gridx = 0;
        constraintsPlayerSkillPanel.gridy = 2;
        layoutPlayerSkilllPanel.setConstraints(label, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(label);

        jpbPM = createBar(20);
        constraintsPlayerSkillPanel.gridx = 1;
        constraintsPlayerSkillPanel.gridy = 2;
        layoutPlayerSkilllPanel.setConstraints(jpbPM, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(jpbPM);

        constraintsPlayerSkillPanel.gridx = 2;
        layoutPlayerSkilllPanel.setConstraints(m_jclPMchange, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(m_jclPMchange);

        label = createLabel("ls.player.skill.winger");
        constraintsPlayerSkillPanel.gridx = 0;
        constraintsPlayerSkillPanel.gridy = 3;
        layoutPlayerSkilllPanel.setConstraints(label, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(label);

        jpbWI = createBar(20);
        constraintsPlayerSkillPanel.gridx = 1;
        layoutPlayerSkilllPanel.setConstraints(jpbWI, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(jpbWI);

        constraintsPlayerSkillPanel.gridx = 2;;
        layoutPlayerSkilllPanel.setConstraints(m_jclWIchange, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(m_jclWIchange);

        label = createLabel("ls.player.skill.passing");
        constraintsPlayerSkillPanel.gridx = 0;
        constraintsPlayerSkillPanel.gridy = 4;
        layoutPlayerSkilllPanel.setConstraints(label, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(label);

        jpbPS = createBar(20);
        constraintsPlayerSkillPanel.gridx = 1;
        layoutPlayerSkilllPanel.setConstraints(jpbPS, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(jpbPS);

        constraintsPlayerSkillPanel.gridx = 2;
        layoutPlayerSkilllPanel.setConstraints(m_jclPSchange, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(m_jclPSchange);


        label = createLabel("ls.player.skill.scoring");
        constraintsPlayerSkillPanel.gridx = 0;
        constraintsPlayerSkillPanel.gridy = 5;
        layoutPlayerSkilllPanel.setConstraints(label, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(label);

        jpbSC = createBar(20);
        constraintsPlayerSkillPanel.gridx = 1;
        layoutPlayerSkilllPanel.setConstraints(jpbSC, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(jpbSC);

        constraintsPlayerSkillPanel.gridx = 2;
        layoutPlayerSkilllPanel.setConstraints(m_jclSCchange, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(m_jclSCchange);


        constraintsPlayerSkillPanel.insets = new Insets(0,10,0,0);

        label = createLabel("ls.player.skill.setpieces");
        constraintsPlayerSkillPanel.gridx = 0;
        constraintsPlayerSkillPanel.gridy = 6;
        layoutPlayerSkilllPanel.setConstraints(label, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(label);

        jpbSP = createBar(20);
        constraintsPlayerSkillPanel.gridx = 1;
        constraintsPlayerSkillPanel.gridy = 6;
        layoutPlayerSkilllPanel.setConstraints(jpbSP, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(jpbSP);

        constraintsPlayerSkillPanel.gridx = 2;
        layoutPlayerSkilllPanel.setConstraints(m_jclSPchange, constraintsPlayerSkillPanel);
        jpPlayerSkill.add(m_jclSPchange);


        setPosition(constraints, 4, 4);
        layout.setConstraints(jpPlayerSkill, constraints);
        panel.add(jpPlayerSkill);

        // Create goals statistics panel =============================================
        final GridBagLayout layoutPlayerGoalsStatsPanel = new GridBagLayout();
        final GridBagConstraints constraintsPlayerGoalsStatsPanel = new GridBagConstraints();
        constraintsPlayerGoalsStatsPanel.fill = GridBagConstraints.BOTH;
        constraintsPlayerGoalsStatsPanel.insets = new Insets(0,10,5,0);

        jpPlayerGoalsStats.setLayout(layoutPlayerGoalsStatsPanel);

        String title = "⚽ " + Helper.getTranslation("ls.module.player_analysis.stats");
        TitledBorder titledBorder = BorderFactory.createTitledBorder(title.toUpperCase());
        titledBorder.setTitleFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        titledBorder.setTitleColor(ThemeManager.getColor(HOColorName.LINEUP_HIGHLIGHT_FG));
        jpPlayerGoalsStats.setBorder(titledBorder);


        label = createLabel("ls.player.career_goals");
        constraintsPlayerGoalsStatsPanel.gridx = 0;
        constraintsPlayerGoalsStatsPanel.gridy = 0;
        layoutPlayerGoalsStatsPanel.setConstraints(label, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(label);

        constraintsPlayerGoalsStatsPanel.gridx = 1;
        layoutPlayerGoalsStatsPanel.setConstraints(m_jlCareerGoals, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(m_jlCareerGoals);


        label = createLabel("ls.player.team_goals");
        constraintsPlayerGoalsStatsPanel.gridx = 0;
        constraintsPlayerGoalsStatsPanel.gridy = 1;
        layoutPlayerGoalsStatsPanel.setConstraints(label, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(label);

        constraintsPlayerGoalsStatsPanel.gridx = 1;
        layoutPlayerGoalsStatsPanel.setConstraints(m_jlTeamGoals, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(m_jlTeamGoals);


        label = createLabel("ls.player.hattricks");
        constraintsPlayerGoalsStatsPanel.gridx = 0;
        constraintsPlayerGoalsStatsPanel.gridy = 2;
        layoutPlayerGoalsStatsPanel.setConstraints(label, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(label);

        constraintsPlayerGoalsStatsPanel.gridx = 1;
        layoutPlayerGoalsStatsPanel.setConstraints(m_jlHattricks, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(m_jlHattricks);


        label = createLabel("ls.player.season_series_goals");
        constraintsPlayerGoalsStatsPanel.gridx = 0;
        constraintsPlayerGoalsStatsPanel.gridy = 3;
        layoutPlayerGoalsStatsPanel.setConstraints(label, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(label);

        constraintsPlayerGoalsStatsPanel.gridx = 1;
        layoutPlayerGoalsStatsPanel.setConstraints(m_jlSeasonSeriesGoals, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(m_jlSeasonSeriesGoals);


        label = createLabel("ls.player.season_cup_goals");
        constraintsPlayerGoalsStatsPanel.gridx = 0;
        constraintsPlayerGoalsStatsPanel.gridy = 4;
        layoutPlayerGoalsStatsPanel.setConstraints(label, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(label);

        constraintsPlayerGoalsStatsPanel.gridx = 1;
        layoutPlayerGoalsStatsPanel.setConstraints(m_jlSeasonCupGoals, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(m_jlSeasonCupGoals);


        label = new JLabel(Helper.getTranslation("ls.module.player_analysis.goals_stats_disclaimer"));
        Font newLabelFont = new Font(label.getFont().getName(), Font.ITALIC, label.getFont().getSize());
        label.setFont(newLabelFont);

        constraintsPlayerGoalsStatsPanel.gridx = 0;
        constraintsPlayerGoalsStatsPanel.gridy = 5;
        constraintsPlayerGoalsStatsPanel.insets = new Insets(10,10,5,0);
        layoutPlayerGoalsStatsPanel.setConstraints(label, constraintsPlayerGoalsStatsPanel);
        jpPlayerGoalsStats.add(label);

        setPosition(constraints, 8, 4);
        layout.setConstraints(jpPlayerGoalsStats, constraints);
        panel.add(jpPlayerGoalsStats);

        // ==========================================================================
        final GridBagLayout layoutPlayerOtherInfos = new GridBagLayout();
        final GridBagConstraints constraintsPlayerOtherInfos = new GridBagConstraints();
        constraintsPlayerOtherInfos.fill = GridBagConstraints.BOTH;
        constraintsPlayerOtherInfos.insets = new Insets(0,10,5,0);
        jpPlayerOtherInfos.setLayout(layoutPlayerOtherInfos);

        label = createLabel("Gruppe");
        constraintsPlayerOtherInfos.gridx = 0;
        constraintsPlayerOtherInfos.gridy = 0;
        constraintsPlayerOtherInfos.insets = new Insets(0,10,5,0);
        layoutPlayerOtherInfos.setConstraints(label, constraintsPlayerOtherInfos);
        jpPlayerOtherInfos.add(label);

        m_jcbSquad.setRenderer(new SmilieListCellRenderer());
        m_jcbSquad.addItemListener(this);
        constraintsPlayerOtherInfos.gridx = 1;
        layoutPlayerOtherInfos.setConstraints(m_jcbSquad, constraintsPlayerOtherInfos);
        jpPlayerOtherInfos.add(m_jcbSquad);


        label = createLabel("Info");
        constraintsPlayerOtherInfos.gridx = 0;
        constraintsPlayerOtherInfos.gridy = 1;
        layoutPlayerOtherInfos.setConstraints(label, constraintsPlayerOtherInfos);
        jpPlayerOtherInfos.add(label);

        m_jcbInformation.setRenderer(new SmilieListCellRenderer());
        m_jcbInformation.addItemListener(this);
        constraintsPlayerOtherInfos.gridx = 1;
        layoutPlayerOtherInfos.setConstraints(m_jcbInformation, constraintsPlayerOtherInfos);
        jpPlayerOtherInfos.add(m_jcbInformation);


        label = createLabel("BestePosition");
        constraintsPlayerOtherInfos.gridx = 2;
        constraintsPlayerOtherInfos.gridy = 0;
        layoutPlayerOtherInfos.setConstraints(label, constraintsPlayerOtherInfos);
        jpPlayerOtherInfos.add(label);

        constraintsPlayerOtherInfos.gridx = 3;
        layoutPlayerOtherInfos.setConstraints(m_jlBestPosition, constraintsPlayerOtherInfos);
        jpPlayerOtherInfos.add(m_jlBestPosition);


        label = createLabel("ls.module.player_analysis.override_best_position");
        constraintsPlayerOtherInfos.gridx = 2;
        constraintsPlayerOtherInfos.gridy = 1;
        layoutPlayerOtherInfos.setConstraints(label, constraintsPlayerOtherInfos);
        jpPlayerOtherInfos.add(label);

        m_jcbUserBestPosition.setToolTipText("override best position: has impact if option 'best position first' is selected in Lineup tool");
        m_jcbUserBestPosition.setMaximumRowCount(20);
        m_jcbUserBestPosition.addItemListener(this);
        constraintsPlayerOtherInfos.gridx = 3;
        layoutPlayerOtherInfos.setConstraints(m_jcbUserBestPosition, constraintsPlayerOtherInfos);
        jpPlayerOtherInfos.add(m_jcbUserBestPosition);

        //Buttons
        final JPanel jpButtonsPanel = new JPanel();
        final GridBagLayout layoutButtonPanel = new GridBagLayout();
        final GridBagConstraints constraintsButtonsPanel = new GridBagConstraints();
        constraintsButtonsPanel.fill = GridBagConstraints.BOTH;
        jpButtonsPanel.setLayout(layoutButtonPanel);

        initButton(m_jbStatistics, HOVerwaltung.instance().getLanguageString("tt_Spieler_statistik"), jpButtonsPanel);
        initButton(m_jbAnalysisTop, HOVerwaltung.instance().getLanguageString("tt_Spieler_analyse1"), jpButtonsPanel);
        initButton(m_jbAnalysisBottom, HOVerwaltung.instance().getLanguageString("tt_Spieler_analyse2"), jpButtonsPanel);
        initButton(m_jbOffsets, HOVerwaltung.instance().getLanguageString("tt_Spieler_offset"), jpButtonsPanel);

        constraintsPlayerOtherInfos.gridy = 0;
        constraintsPlayerOtherInfos.gridx = 4;
        constraintsPlayerOtherInfos.gridheight = 2;

        layoutPlayerOtherInfos.setConstraints(jpButtonsPanel, constraintsPlayerOtherInfos);
        jpPlayerOtherInfos.add(jpButtonsPanel);

        setPosition(constraints, 0, 5);
        constraints.gridwidth = 12;
        layout.setConstraints(jpPlayerOtherInfos, constraints);
        panel.add(jpPlayerOtherInfos);

        // =========================================================================================


        label = new JLabel(HOVerwaltung.instance().getLanguageString("LastMatchRating"));
        initNormalLabel(0, 6, constraints, layout, panel, label);
        initNormalField(1, 6, constraints, layout, panel, m_jpLastMatchRating.getComponent(false));
        m_lastMatchLink = ((MatchDateTableEntry)m_jpLastMatchRating.getTableEntryRight()).getMatchLink();
        m_lastMatchLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (m_clPlayer != null) {
                    if (e.isShiftDown()) {
                        int matchId = m_clPlayer.getLastMatchId();
                        MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(matchId, null);
                        HattrickLink.showMatch(matchId + "", info.getMatchType().isOfficial());
                    } else {
                        HOMainFrame.instance().showMatch(m_clPlayer.getLastMatchId());
                    }
                }
            }
        });


        add(panel, BorderLayout.CENTER);

        // at initialisation select first player to ensure clean display =====
        m_clPlayer = m_playerOverviewTable.getSorter().getSpieler(0);
    }

    private void initNormalLabel(int x, int y, GridBagConstraints constraints, GridBagLayout layout, JPanel panel, JLabel label) {
        constraints.gridwidth = 1;
        setPosition(constraints, x, y);
        constraints.weightx = 0.0;
        layout.setConstraints(label, constraints);
        panel.add(label);
    }

    private void initNormalField(int x, int y, GridBagConstraints constraints, GridBagLayout layout, JPanel panel, JComponent component) {
        setPosition(constraints, x, y);
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;
        layout.setConstraints(component, constraints);
        panel.add(component);
    }

    private void setPosition(GridBagConstraints c, int x, int y) {
        c.gridx = x;
        c.gridy = y;
    }

    private void initButton(JButton button, String tooltipText, JPanel buttonpanel) {
        button.setToolTipText(tooltipText);
        button.setPreferredSize(new Dimension(28, 28));
        button.setEnabled(false);
        button.addActionListener(this);
        buttonpanel.add(button);
    }

    private void resetLabel(JLabel thisLabel){
        thisLabel.setIcon(null);
        thisLabel.setToolTipText(null);
        thisLabel.setText("");
    }

    private void setCB(JComboBox thisCB, String selectedItem){
        thisCB.removeItemListener(this);
        thisCB.setSelectedItem(selectedItem);
        thisCB.addItemListener(this);
    }

    private void resetLabels() {
        jlPlayerAvatar.setIcon(null);
        jlNationality.setIcon(null);
        jlNationality.setToolTipText(null);
        jlNationality.setText("");
        m_jpStatus.clear();
        m_jcbSquad.setSelectedItem("");
        m_jcbInformation.setSelectedItem("");
        m_jpRating.clear();
        m_jllWage.clear();
        m_jllTSI.clear();
        m_jclFormChange.clear();
        m_jclStaminaChange.clear();
        m_jclGKchange.clear();
        m_jclDEchange.clear();
        m_jclPMchange.clear();
        m_jclPSchange.clear();
        m_jclWIchange.clear();
        m_jclSPchange.clear();
        m_jclSCchange.clear();
        m_jcbUserBestPosition.setSelectedItem("");
        resetLabel(m_jlCareerGoals);
        resetLabel(m_jlTeamGoals);
        resetLabel(m_jlSeasonSeriesGoals);
        resetLabel(m_jlSeasonCupGoals);
        resetLabel(m_jlHattricks);
        resetLabel(m_jlSpecialty);
        resetLabel(m_jlPlayerDescription);
        resetLabel(m_jlInTeamSince);
        resetLabel(m_jlBestPosition);
        m_jbStatistics.setEnabled(false);
        m_jbAnalysisTop.setEnabled(false);
        m_jbAnalysisBottom.setEnabled(false);
        m_jbOffsets.setEnabled(false);

        m_jpLastMatchRating.clear();
    }

    public CBItem[] getPositions() {

        final FactorObject[] allPos = FormulaFactors.instance().getAllObj();
        byte[] altPositions = m_clPlayer.getAlternativeBestPositions();

        CBItem[] positions = new CBItem[allPos.length + 1];

        positions[0] = new CBItem(MatchRoleID.getNameForPosition(UNKNOWN), UNKNOWN);
        positions[1] = new CBItem(MatchRoleID.getNameForPosition(UNSELECTABLE), UNSELECTABLE);

        int k = 2;
        String text = "";
        for (FactorObject allPo : allPos) {
            if (allPo.getPosition() == IMatchRoleID.FORWARD_DEF_TECH) continue;
            text = MatchRoleID.getNameForPosition(allPo.getPosition())
                    + " ("
                    + Helper.getNumberFormat(false, 1).format(
                    m_clPlayer.calcPosValue(allPo.getPosition(), true, true, null, false))
                    + "%)";
            for (byte altPos : altPositions
            ) {
                if (altPos == allPo.getPosition()) {
                    text += " *";
                }
            }
            positions[k] = new CBItem(text, allPo.getPosition());
            k++;
        }

        return positions;
    }

    private Color getColorForSkill(int iSkill){
       var bgColor =  switch (iSkill){
            case 7, 8 -> ThemeManager.getColor(HOColorName.GREEN);
            case 5, 6 -> ThemeManager.getColor(HOColorName.YELLOW);
            case 3, 4 -> ThemeManager.getColor(HOColorName.ORANGE);
            default -> ThemeManager.getColor(HOColorName.RED);
        };
        return bgColor;
    }

    private JProgressBar createBar(int iMax){
        JProgressBar bar = new JProgressBar(0, iMax);
        bar.setUI(new MyProgressUI());
        bar.setStringPainted(true);
        return bar;
    }

    private void formatBar(JProgressBar bar, float value){
        formatBar(bar, value, false);
    }

    private void formatBar(JProgressBar bar, float value, boolean varyingColor) {

        int nbDecimal = (value - (int)value) == 0f ? 0 : 2 ;
        bar.setString(PlayerAbility.getNameForSkill(value, true, false, nbDecimal));

        bar.setValue((int) value);
        Color _fgColor = varyingColor ? getColorForSkill((int) value) : ThemeManager.getColor(HOColorName.GREEN);
        bar.setForeground(_fgColor);
        bar.setBorderPainted(true);
        bar.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    private JLabel createLabel(String i18nText){
        JLabel _label = new JLabel(Helper.getTranslation(i18nText), SwingConstants.RIGHT);
        _label.setFont(Helper.getLabelFontAsBold(_label));
        return _label;
    }

    private static class MyProgressUI extends BasicProgressBarUI {

        private Color aColor;
        private Color bColor;

        @Override
        protected void paintDeterminate(Graphics g, JComponent c) {
            Color saved = g.getColor();
            aColor = saved;
            bColor = c.getForeground();

            Rectangle vr = SwingUtilities.calculateInnerArea(c, new Rectangle());
            Rectangle or = progressBar.getBounds();
            Insets insets = c.getInsets();

            int amountFull = getAmountFull(insets, or.width, or.height);


             g.setColor(c.getForeground());
             g.fillRect(vr.x, vr.y, amountFull, vr.height);

             if (progressBar.isStringPainted() && !progressBar.getString().equals("")) {
                 paintString(g, 0, 0, or.width, or.height, amountFull, insets);
             }
            g.setColor(saved);
        }

        @Override
        protected Color getSelectionBackground() {
            return ImageUtilities.getColorForContrast(this.aColor);
        }

        @Override
        protected Color getSelectionForeground() {
            return ImageUtilities.getColorForContrast(this.bColor);
        }
    }

}
