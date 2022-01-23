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
import core.util.Helper;
import module.lineup.Lineup;
import module.statistics.StatistikMainPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
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
    private PlayerOverviewTable m_playerOverviewTable;

    //~ Static fields/initializers -----------------------------------------------------------------

    public static final Dimension COMPONENTENSIZE = new Dimension(Helper.calcCellWidth(150),
            Helper.calcCellWidth(18));
    public static final Dimension COMPONENTENSIZE2 = new Dimension(Helper.calcCellWidth(65),
            Helper.calcCellWidth(18));
    private static final Dimension COMPONENTENSIZE3 = new Dimension(Helper.calcCellWidth(100),
            Helper.calcCellWidth(18));
    private static final Dimension COMPONENTENSIZE4 = new Dimension(Helper.calcCellWidth(50),
            Helper.calcCellWidth(18));
    private static final Dimension COMPONENTENSIZECB = new Dimension(Helper.calcCellWidth(150), 16);

    //~ Instance fields ----------------------------------------------------------------------------


    private JLabel jlName = new JLabel("");
    private JLabel jlPlayerDescription = new JLabel("");
    private final JPanel jpPlayer = new JPanel();
    private JLabel jlPlayerAvatar = new JLabel();
    private JLabel jlNationality = new JLabel();
    private JLabel jlSpecialty = new JLabel();
    private final JLabel jlInTeamSince = new JLabel();



    // Top Row, column 1


    private final ColorLabelEntry m_jpPositioned = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
    private RatingTableEntry m_jpRating = new RatingTableEntry();
    private final ColorLabelEntry m_jpBestPosition = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
    private DoubleLabelEntries m_jpLastMatchRating = new DoubleLabelEntries(
            new RatingTableEntry(),
            new MatchDateTableEntry(null, MatchType.NONE),
            new GridBagLayout());
    private JLabel m_lastMatchLink = null;

    // Top Row, column 2
    private final JComboBox m_jcbSquad = new JComboBox(GroupTeamFactory.TEAMSMILIES);
    private final JComboBox m_jcbInformation = new JComboBox(SMILEYS);
    private SpielerStatusLabelEntry m_jpStatus = new SpielerStatusLabelEntry();
    private final DoubleLabelEntries m_jllWage = new DoubleLabelEntries(new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.LEFT), new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.RIGHT));
    private final DoubleLabelEntries m_jllTSI = new DoubleLabelEntries(new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.LEFT), new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.RIGHT));
    private JComboBox m_jcbUserBestPosition = new JComboBox(MatchRoleID.POSITIONEN);

    // Top Row, column 3

    // Second Row, Column 1
    private final ColorLabelEntry m_jpStamina = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
    private final ColorLabelEntry m_jpStaminaChange = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpPlaymaking = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
    private final ColorLabelEntry m_jpPlaymakingChange = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpWinger = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
    private final ColorLabelEntry m_jpWingerChange = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpScoring = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
    private final ColorLabelEntry m_jpScoringChange = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.CENTER);
    // Second Row, Column 2
    private final ColorLabelEntry m_jpForm = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.LEFT);
    private final ColorLabelEntry m_jpFormChange = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpKeeper = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
    private final ColorLabelEntry m_jpKeeperChange = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpPassing = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
    private final ColorLabelEntry m_jpPassingChange = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpDefending = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
    private final ColorLabelEntry m_jpDefendingChange = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpSetPieces = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.LEFT);
    private final ColorLabelEntry m_jpSetPiecesChange = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.CENTER);
    // Second Row, Column 3
    private final ColorLabelEntry m_jpGoalsFriendly = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpGoalsLeague = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpGoalsCup = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpGoalsTotal = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpHattricks = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
    private final ColorLabelEntry m_jpMarketValue = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
            ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);

    // Third Row, Column 3
    private final JButton m_jbStatistics = new JButton(getSvgIcon(GOTOSTATISTIK));
    private final JButton m_jbAnalysisTop = new JButton(getSvgIcon(GOTOANALYSETOP));
    private final JButton m_jbAnalysisBottom = new JButton(getSvgIcon(GOTOANALYSEBOTTOM));
    private final JButton m_jbOffsets = new JButton(getSvgIcon(HOIconName.OFFSET));

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

    // Players
    private Player m_clPlayer;
    private Player m_clComparisonPlayer;

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

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerDetailPanel object.
     */
    protected PlayerDetailsPanel(PlayerOverviewTable playerOverviewTable) {
        m_playerOverviewTable = playerOverviewTable;
        initComponents();
        RefreshManager.instance().registerRefreshable(this);
    }

    //~ Methods ------------------------------------------------------------------------------------

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

    /**
     * refresh the display
     */
    @Override
    public void refresh() {
        setPlayer(m_clPlayer);
    }

    /**
     * set values of the player to fields
     */
    private void setLabels() {
        Icon playerAvatar = ThemeManager.instance().getPlayerAvatar(m_clPlayer.getPlayerID());
        jlPlayerAvatar.setIcon(playerAvatar);
//        m_jpAge.setText(m_clPlayer.getAgeStringFull());
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
        Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
        if (lineup.isPlayerInLineup(m_clPlayer.getPlayerID())
                && (lineup.getPositionByPlayerId(m_clPlayer.getPlayerID()) != null)) {
            m_jpPositioned.setIcon(ImageUtilities.getJerseyIcon(lineup.getPositionByPlayerId(m_clPlayer.getPlayerID()),
                    m_clPlayer.getTrikotnummer()));
            m_jpPositioned.setText(MatchRoleID.getNameForPosition(lineup.getPositionByPlayerId(m_clPlayer.getPlayerID())
                    .getPosition()));
        } else {
            m_jpPositioned.setIcon(ImageUtilities.getJerseyIcon(null, m_clPlayer.getTrikotnummer()));
            m_jpPositioned.setText("");
        }
        //Rating
        if (m_clPlayer.getBewertung() > 0) {
            m_jpRating.setRating(m_clPlayer.getBewertung());
        } else {
            m_jpRating.setRating(m_clPlayer.getLetzteBewertung());
        }
        m_jcbSquad.removeItemListener(this);
        m_jcbSquad.setSelectedItem(m_clPlayer.getTeamInfoSmilie());
        m_jcbSquad.addItemListener(this);
        m_jcbInformation.removeItemListener(this);
        m_jcbInformation.setSelectedItem(m_clPlayer.getManuellerSmilie());
        m_jcbInformation.addItemListener(this);
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
            m_jpForm.setText(PlayerAbility.getNameForSkill(m_clPlayer.getForm()) + "");
            m_jpFormChange.clear();
            m_jpStamina.setText(PlayerAbility.getNameForSkill(m_clPlayer.getKondition()) + "");
            m_jpStaminaChange.clear();
            m_jpKeeper.setText(PlayerAbility.getNameForSkill(m_clPlayer.getGKskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.KEEPER)) + "");
            m_jpKeeperChange.clear();
            m_jpDefending.setText(PlayerAbility.getNameForSkill(m_clPlayer.getDEFskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.DEFENDING)) + "");
            m_jpDefendingChange.clear();
            m_jpPlaymaking.setText(PlayerAbility.getNameForSkill(m_clPlayer.getPMskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.PLAYMAKING)) + "");
            m_jpPlaymakingChange.clear();
            m_jpPassing.setText(PlayerAbility.getNameForSkill(m_clPlayer.getPSskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.PASSING)) + "");
            m_jpPassingChange.clear();
            m_jpWinger.setText(PlayerAbility.getNameForSkill(m_clPlayer.getWIskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.WINGER)) + "");
            m_jpWingerChange.clear();
            m_jpSetPieces.setText(PlayerAbility.getNameForSkill(m_clPlayer.getSPskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.SET_PIECES)) + "");
            m_jpSetPiecesChange.clear();
            m_jpScoring.setText(PlayerAbility.getNameForSkill(m_clPlayer.getSCskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.SCORING)) + "");
            m_jpScoringChange.clear();
            m_jpBestPosition.setText(MatchRoleID.getNameForPosition(m_clPlayer.getIdealPosition())
                    + " ("
                    + Helper.getNumberFormat(false, core.model.UserParameter.instance().nbDecimals).format(
                    m_clPlayer.calcPosValue(m_clPlayer.getIdealPosition(), true, null, false))
                    + ")");
            for (int i = 0; i < playerPositionValues.length; i++) {
                showNormal(playerPositionValues[i], playerPosition[i]);
            }

        }
        else {
            final int previousSalary = (int) (m_clComparisonPlayer.getSalary() / core.model.UserParameter.instance().FXrate);
            m_jllWage.getLeft().setText(salarytext);
            m_jllWage.getRight().setSpecialNumber(salary - previousSalary, true);
            m_jllTSI.getLeft().setText(tsitext);
            m_jllTSI.getRight().setSpecialNumber(m_clPlayer.getTSI() - m_clComparisonPlayer.getTSI(), false);
            m_jpForm.setText(PlayerAbility.getNameForSkill(m_clPlayer.getForm()) + "");
            m_jpFormChange.setGraphicalChangeValue(m_clPlayer.getForm()
                    - m_clComparisonPlayer.getForm(), !m_clComparisonPlayer.isOld(), true);
            m_jpStamina.setText(PlayerAbility.getNameForSkill(m_clPlayer.getKondition()) + "");
            m_jpStaminaChange.setGraphicalChangeValue(m_clPlayer.getKondition()
                    - m_clComparisonPlayer.getKondition(), !m_clComparisonPlayer.isOld(), true);
            m_jpKeeper.setText(PlayerAbility.getNameForSkill(m_clPlayer.getGKskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.KEEPER)) + "");
            m_jpKeeperChange.setGraphicalChangeValue(m_clPlayer.getGKskill()
                            - m_clComparisonPlayer.getGKskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.KEEPER)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.KEEPER),
                    !m_clComparisonPlayer.isOld(), true);
            m_jpDefending.setText(PlayerAbility.getNameForSkill(m_clPlayer.getDEFskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.DEFENDING)) + "");
            m_jpDefendingChange.setGraphicalChangeValue(m_clPlayer.getDEFskill()
                            - m_clComparisonPlayer.getDEFskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.DEFENDING)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.DEFENDING),
                    !m_clComparisonPlayer.isOld(), true);
            m_jpPlaymaking.setText(PlayerAbility.getNameForSkill(m_clPlayer.getPMskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.PLAYMAKING)) + "");
            m_jpPlaymakingChange.setGraphicalChangeValue(m_clPlayer.getPMskill()
                            - m_clComparisonPlayer.getPMskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.PLAYMAKING)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.PLAYMAKING),
                    !m_clComparisonPlayer.isOld(), true);
            m_jpPassing.setText(PlayerAbility.getNameForSkill(m_clPlayer.getPSskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.PASSING)) + "");
            m_jpPassingChange.setGraphicalChangeValue(m_clPlayer.getPSskill()
                            - m_clComparisonPlayer.getPSskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.PASSING)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.PASSING),
                    !m_clComparisonPlayer.isOld(), true);
            m_jpWinger.setText(PlayerAbility.getNameForSkill(m_clPlayer.getWIskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.WINGER)) + "");
            m_jpWingerChange.setGraphicalChangeValue(m_clPlayer.getWIskill()
                            - m_clComparisonPlayer.getWIskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.WINGER)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.WINGER),
                    !m_clComparisonPlayer.isOld(), true);
            m_jpSetPieces.setText(PlayerAbility.getNameForSkill(m_clPlayer.getSPskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.SET_PIECES)) + "");
            m_jpSetPiecesChange.setGraphicalChangeValue(m_clPlayer.getSPskill()
                            - m_clComparisonPlayer.getSPskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.SET_PIECES)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.SET_PIECES),
                    !m_clComparisonPlayer.isOld(), true);
            m_jpScoring.setText(PlayerAbility.getNameForSkill(m_clPlayer.getSCskill()
                    + m_clPlayer.getSub4Skill(PlayerSkill.SCORING)) + "");
            m_jpScoringChange.setGraphicalChangeValue(m_clPlayer.getSCskill()
                            - m_clComparisonPlayer.getSCskill(),
                    m_clPlayer.getSub4Skill(PlayerSkill.SCORING)
                            - m_clComparisonPlayer.getSub4Skill(PlayerSkill.SCORING),
                    !m_clComparisonPlayer.isOld(), true);


            m_jpBestPosition.setText(MatchRoleID.getNameForPosition(m_clPlayer.getIdealPosition())
                    + " ("
                    + m_clPlayer.calcPosValue(m_clPlayer.getIdealPosition(), true, null, false)
                    + ")");
            for (int i = 0; i < playerPositionValues.length; i++) {
                showWithCompare(playerPositionValues[i], playerPosition[i]);
            }
        }
        m_jpGoalsFriendly.setText(m_clPlayer.getToreFreund() + "");
        m_jpGoalsLeague.setText(m_clPlayer.getToreLiga() + "");
        m_jpGoalsCup.setText(m_clPlayer.getTorePokal() + "");
        m_jpGoalsTotal.setText(m_clPlayer.getToreGesamt() + "");
        m_jpHattricks.setText(m_clPlayer.getHattrick() + "");
        jlSpecialty.setText(PlayerSpeciality.toString(m_clPlayer.getPlayerSpecialty()));
        jlSpecialty.setIcon(ImageUtilities.getSmallPlayerSpecialtyIcon(HOIconName.SPECIALTIES[m_clPlayer.getPlayerSpecialty()]));

        String playerDescription = "<html>" + Helper.getTranslation("ls.player_details.desc1", PlayerAgreeability.toString(m_clPlayer.getCharakter()), PlayerAggressiveness.toString(m_clPlayer.getAgressivitaet()), PlayerHonesty.toString(m_clPlayer.getAnsehen()));
        playerDescription += "<br>";
        playerDescription += Helper.getTranslation("ls.player_details.desc2", PlayerAbility.getNameForSkill(m_clPlayer.getExperience(), true, false, 0), PlayerAbility.getNameForSkill(m_clPlayer.getLeadership(), true, false, 0), PlayerAbility.getNameForSkill(m_clPlayer.getLoyalty(), true, false, 0));
        playerDescription += "</html>";
        jlPlayerDescription.setText(playerDescription);

        String playerName = "<html><B><span style='font-size:16px'>" + m_clPlayer.getFullName() + "</span></html></B></html>";
        jlName.setText(playerName);

        jlInTeamSince.setText(Helper.getTranslation("ImTeamSeit") + "  !!!! to fetch from XML !!!!!");
        if (m_clPlayer.isHomeGrown()) jlInTeamSince.setIcon(ThemeManager.getIcon(HOIconName.HOMEGROWN));

        m_jbStatistics.setEnabled(true);
        m_jbAnalysisTop.setEnabled(true);
        m_jbAnalysisBottom.setEnabled(true);
        m_jbOffsets.setEnabled(true);
    }

    private void showNormal(DoubleLabelEntries labelEntry, byte playerPosition) {
        labelEntry.getLeft().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance()
                .nbDecimals).format(m_clPlayer.calcPosValue(playerPosition, true, null, false)));

        byte[] alternativePosition = m_clPlayer.getAlternativeBestPositions();
        for (byte altPos : alternativePosition) {
            if (altPos == playerPosition) {
                labelEntry.getLeft().setBold(true);
                break;
            } else {
                labelEntry.getLeft().setBold(false);
            }
        }

        labelEntry.getRight().clear();
    }

    private void showWithCompare(DoubleLabelEntries labelEntry, byte playerPosition) {
        labelEntry.getLeft().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance()
                .nbDecimals).format(m_clPlayer.calcPosValue(playerPosition, true, null, false)));

        byte[] alternativePosition = m_clPlayer.getAlternativeBestPositions();
        for (byte altPos : alternativePosition) {
            if (altPos == playerPosition) {
                labelEntry.getLeft().setBold(true);
                break;
            } else {
                labelEntry.getLeft().setBold(false);
            }
        }

        labelEntry.getRight().setSpecialNumber(m_clPlayer.calcPosValue(playerPosition, true, null, false)
                - m_clComparisonPlayer.calcPosValue(playerPosition, true, null, false), false);
    }

    /**
     * return first player who is find in db
     *
     * @param player
     * @return player
     */
    private Player getComparisonPlayerFirstHRF(Player player) {
        return core.db.DBManager.instance()
                .getSpielerFirstHRF(player.getPlayerID());
    }

    /**
     * search player to compare
     */
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

    /**
     * initialize all fields
     */
    private void initComponents() {
        setLayout(new BorderLayout());

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
        layout.setConstraints(jlPlayerDescription, constraints);
        panel.add(jlPlayerDescription);

        // In the team since  =====================
        jlInTeamSince.setHorizontalTextPosition(SwingConstants.LEFT);
        setPosition(constraints, 0, 3);
        layout.setConstraints(jlInTeamSince, constraints);
        panel.add(jlInTeamSince);

        // create player panel ====================================================================
        final GridBagLayout layoutInnerPanel = new GridBagLayout();
        final GridBagConstraints constraintsInnerPanel = new GridBagConstraints();
        constraintsInnerPanel.fill = GridBagConstraints.BOTH;
        jpPlayer.setLayout(layoutInnerPanel);

        jlPlayerAvatar = new JLabel("");
        constraintsInnerPanel.gridx = 0;
        constraintsInnerPanel.gridy = 0;
        constraintsInnerPanel.gridheight = 7;
        layoutInnerPanel.setConstraints(jlPlayerAvatar, constraintsInnerPanel);
        jpPlayer.add(jlPlayerAvatar);


        JLabel label = new JLabel("");
        constraintsInnerPanel.gridx = 1;
        constraintsInnerPanel.gridheight = 1;
        constraintsInnerPanel.weighty = 0.5;   //force centering elements
        layoutInnerPanel.setConstraints(label, constraintsInnerPanel);
        jpPlayer.add(label);

        label = new JLabel(Helper.getTranslation("ls.player.tsi"), SwingConstants.RIGHT);
        label.setFont(Helper.getLabelFontAsBold(label));
        constraintsInnerPanel.gridx = 1;
        constraintsInnerPanel.gridy = 1;
        constraintsInnerPanel.gridheight = 1;
        constraintsInnerPanel.weighty = 0.0;
        constraintsInnerPanel.insets = new Insets(0,10,5,0);
        layoutInnerPanel.setConstraints(label, constraintsInnerPanel);
        jpPlayer.add(label);

        constraintsInnerPanel.gridx = 2;
        constraintsInnerPanel.gridy = 1;
        layoutInnerPanel.setConstraints(m_jllTSI.getComponent(false), constraintsInnerPanel);
        jpPlayer.add(m_jllTSI.getComponent(false));


        label = new JLabel(Helper.getTranslation("ls.player.wage"), SwingConstants.RIGHT);
        label.setFont(Helper.getLabelFontAsBold(label));
        constraintsInnerPanel.gridx = 1;
        constraintsInnerPanel.gridy = 2;
        layoutInnerPanel.setConstraints(label, constraintsInnerPanel);
        jpPlayer.add(label);

        constraintsInnerPanel.gridx = 2;
        constraintsInnerPanel.gridy = 2;
        layoutInnerPanel.setConstraints(m_jllWage.getComponent(false), constraintsInnerPanel);
        jpPlayer.add(m_jllWage.getComponent(false));

        label = new JLabel(Helper.getTranslation("ls.player.speciality"), SwingConstants.RIGHT);
        label.setFont(Helper.getLabelFontAsBold(label));
        constraintsInnerPanel.gridx = 1;
        constraintsInnerPanel.gridy = 3;
        layoutInnerPanel.setConstraints(label, constraintsInnerPanel);
        jpPlayer.add(label);

        constraintsInnerPanel.gridx = 2;
        constraintsInnerPanel.gridy = 3;
        layoutInnerPanel.setConstraints(jlSpecialty, constraintsInnerPanel);
        jpPlayer.add(jlSpecialty);

        label = new JLabel(Helper.getTranslation("ls.player.form"), SwingConstants.RIGHT);
        label.setFont(Helper.getLabelFontAsBold(label));
        constraintsInnerPanel.gridx = 1;
        constraintsInnerPanel.gridy = 4;
        layoutInnerPanel.setConstraints(label, constraintsInnerPanel);
        jpPlayer.add(label);

        label = new JLabel(Helper.getTranslation("ls.player.skill.stamina"), SwingConstants.RIGHT);
        label.setFont(Helper.getLabelFontAsBold(label));
        constraintsInnerPanel.gridx = 1;
        constraintsInnerPanel.gridy = 5;
        layoutInnerPanel.setConstraints(label, constraintsInnerPanel);
        jpPlayer.add(label);

        label = new JLabel("");
        constraintsInnerPanel.gridx = 1;
        constraintsInnerPanel.gridy = 6;
        constraintsInnerPanel.gridheight = 1;
        constraintsInnerPanel.weighty = 0.5;   //force centering elements
        constraintsInnerPanel.insets = new Insets(0,10,0,0);
        layoutInnerPanel.setConstraints(label, constraintsInnerPanel);
        jpPlayer.add(label);

        setPosition(constraints, 0, 4);
        layout.setConstraints(jpPlayer, constraints);
        panel.add(jpPlayer);

        // ***** Block 1


        label = new JLabel(HOVerwaltung.instance().getLanguageString("Aufgestellt"));
        initNormalLabel(0, 5, constraints, layout, panel, label);
        initNormalField(1, 5, constraints, layout, panel, m_jpPositioned.getComponent(false));

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

        label = new JLabel(HOVerwaltung.instance().getLanguageString("BestePosition"));
        initNormalLabel(0, 7, constraints, layout, panel, label);
        initNormalField(1, 7, constraints, layout, panel, m_jpBestPosition.getComponent(false));

        // ***** Block 2
        label = new JLabel(HOVerwaltung.instance().getLanguageString("Gruppe"));
        initNormalLabel(4, 5, constraints, layout, panel, label);
        m_jcbSquad.setPreferredSize(COMPONENTENSIZECB);
        m_jcbSquad.setBackground(ThemeManager.getColor(HOColorName.TABLEENTRY_BG));
        m_jcbSquad.setRenderer(new SmilieListCellRenderer());
        m_jcbSquad.addItemListener(this);
        setPosition(constraints, 5, 5);
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;
        layout.setConstraints(m_jcbSquad, constraints);
        panel.add(m_jcbSquad);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Info"));
        initNormalLabel(6, 5, constraints, layout, panel, label);

        m_jcbInformation.setMaximumRowCount(10);
        m_jcbInformation.setPreferredSize(COMPONENTENSIZECB);
        m_jcbInformation.setBackground(m_jcbSquad.getBackground());
        m_jcbInformation.setRenderer(new SmilieListCellRenderer());
        m_jcbInformation.addItemListener(this);
        setPosition(constraints, 7, 5);
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;

        layout.setConstraints(m_jcbInformation, constraints);
        panel.add(m_jcbInformation);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Status"));
        initNormalLabel(4, 6, constraints, layout, panel, label);
        initNormalField(5, 6, constraints, layout, panel, m_jpStatus.getComponent(false));


        label = new JLabel(HOVerwaltung.instance().getLanguageString("BestePosition"));
        initNormalLabel(4, 7, constraints, layout, panel, label);

        m_jcbUserBestPosition.setMaximumRowCount(20);
        m_jcbUserBestPosition.setPreferredSize(COMPONENTENSIZECB);
        m_jcbUserBestPosition.setBackground(m_jcbSquad.getBackground());
        m_jcbUserBestPosition.addItemListener(this);
        setPosition(constraints, 5, 7);
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;
        layout.setConstraints(m_jcbUserBestPosition, constraints);
        panel.add(m_jcbUserBestPosition);

        //empty row
        label = new JLabel();
        setPosition(constraints, 0, 8);
        constraints.weightx = 0.0;
        constraints.gridwidth = 4;
        layout.setConstraints(label, constraints);
        panel.add(label);

        constraints.gridwidth = 1;

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper"));
        initNormalLabel(4, 9, constraints, layout, panel, label);
        initYellowMainField(5, 9, constraints, layout, panel, m_jpKeeper.getComponent(false));
        initYellowChangesField(6, 9, constraints, layout, panel, m_jpKeeperChange.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking"));
        initNormalLabel(0, 10, constraints, layout, panel, label);
        initYellowMainField(1, 10, constraints, layout, panel, m_jpPlaymaking.getComponent(false));
        initYellowChangesField(2, 10, constraints, layout, panel, m_jpPlaymakingChange.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.passing"));
        initNormalLabel(4, 10, constraints, layout, panel, label);
        initYellowMainField(5, 10, constraints, layout, panel, m_jpPassing.getComponent(false));
        initYellowChangesField(6, 10, constraints, layout, panel, m_jpPassingChange.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.winger"));
        initNormalLabel(0, 11, constraints, layout, panel, label);
        initYellowMainField(1, 11, constraints, layout, panel, m_jpWinger.getComponent(false));
        initYellowChangesField(2, 11, constraints, layout, panel, m_jpWingerChange.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.defending"));
        initNormalLabel(4, 11, constraints, layout, panel, label);
        initYellowMainField(5, 11, constraints, layout, panel, m_jpDefending.getComponent(false));
        initYellowChangesField(6, 11, constraints, layout, panel, m_jpDefendingChange.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring"));
        initNormalLabel(0, 12, constraints, layout, panel, label);
        initYellowMainField(1, 12, constraints, layout, panel, m_jpScoring.getComponent(false));
        initYellowChangesField(2, 12, constraints, layout, panel, m_jpScoringChange.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces"));
        initNormalLabel(4, 12, constraints, layout, panel, label);
        initYellowMainField(5, 12, constraints, layout, panel, m_jpSetPieces.getComponent(false));
        initYellowChangesField(6, 12, constraints, layout, panel, m_jpSetPiecesChange.getComponent(false));

        //empty row
        label = new JLabel("  ");
        setPosition(constraints, 7, 1);
        constraints.weightx = 0.0;
        constraints.gridheight = 11;
        layout.setConstraints(label, constraints);
        panel.add(label);
        constraints.gridheight = 1;


        label = new JLabel();
        setPosition(constraints, 11, 8);
        constraints.weightx = 0.0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        layout.setConstraints(label, constraints);
        panel.add(label);
        constraints.gridwidth = 1;
        constraints.gridheight = 1;

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ToreFreund"));
        initNormalLabel(8, 9, constraints, layout, panel, label);
        initNormalField(9, 9, constraints, layout, panel, m_jpGoalsFriendly.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ToreLiga"));
        initNormalLabel(8, 10, constraints, layout, panel, label);
        initNormalField(9, 10, constraints, layout, panel, m_jpGoalsLeague.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("TorePokal"));
        initNormalLabel(8, 11, constraints, layout, panel, label);
        initNormalField(9, 11, constraints, layout, panel, m_jpGoalsCup.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ToreGesamt"));
        initNormalLabel(8, 12, constraints, layout, panel, label);
        initNormalField(9, 12, constraints, layout, panel, m_jpGoalsTotal.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Hattricks"));
        initNormalLabel(8, 13, constraints, layout, panel, label);
        initNormalField(9, 13, constraints, layout, panel, m_jpHattricks.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Marktwert"));
        initNormalLabel(8, 14, constraints, layout, panel, label);
        initNormalField(9, 14, constraints, layout, panel, m_jpMarketValue.getComponent(false));

        //Buttons
        final JPanel buttonpanel = new JPanel();
        buttonpanel.setOpaque(false);
        initButton(m_jbStatistics, HOVerwaltung.instance().getLanguageString("tt_Spieler_statistik"), buttonpanel);
        initButton(m_jbAnalysisTop, HOVerwaltung.instance().getLanguageString("tt_Spieler_analyse1"), buttonpanel);
        initButton(m_jbAnalysisBottom, HOVerwaltung.instance().getLanguageString("tt_Spieler_analyse2"), buttonpanel);
        initButton(m_jbOffsets, HOVerwaltung.instance().getLanguageString("tt_Spieler_offset"), buttonpanel);

        setPosition(constraints, 8, 16);
        constraints.weightx = 1.0;
        constraints.gridheight = 3;
        constraints.gridwidth = 4;
        layout.setConstraints(buttonpanel, constraints);
        panel.add(buttonpanel);
        constraints.gridheight = 1;
        constraints.gridwidth = 1;

        // Empty row
        label = new JLabel("  ");
        setPosition(constraints, 11, 1);
        constraints.weightx = 0.0;
        constraints.gridheight = 18;
        layout.setConstraints(label, constraints);
        panel.add(label);

        constraints.gridheight = 1;
        for (int i = 0; i < playerPositionValues.length; i++) {
            label = new JLabel(MatchRoleID.getShortNameForPosition(playerPosition[i]));
            label.setToolTipText(MatchRoleID.getNameForPosition(playerPosition[i]));
            initBlueLabel(i, constraints, layout, panel, label);
            initBlueField(i, constraints, layout, panel, playerPositionValues[i].getComponent(false));
        }
        add(panel, BorderLayout.CENTER);

        // at initialisation select first player to ensure clean display =====
        m_clPlayer = m_playerOverviewTable.getSorter().getSpieler(0);
    }

    /**
     * init a label
     *
     * @param x
     * @param y
     * @param constraints
     * @param layout
     * @param panel
     * @param label
     */
    private void initNormalLabel(int x, int y, GridBagConstraints constraints, GridBagLayout layout, JPanel panel, JLabel label) {
        constraints.gridwidth = 1;
        setPosition(constraints, x, y);
        constraints.weightx = 0.0;
        layout.setConstraints(label, constraints);
        panel.add(label);
    }

    /**
     * init a value field
     *
     * @param x
     * @param y
     * @param constraints
     * @param layout
     * @param panel
     * @param component
     */
    private void initNormalField(int x, int y, GridBagConstraints constraints, GridBagLayout layout, JPanel panel, JComponent component) {
        setPosition(constraints, x, y);
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;
        component.setPreferredSize(COMPONENTENSIZE);
        layout.setConstraints(component, constraints);
        panel.add(component);
    }

    /**
     * init a label
     *
     * @param y
     * @param constraints
     * @param layout
     * @param panel
     * @param label
     */
    private void initBlueLabel(int y, GridBagConstraints constraints, GridBagLayout layout, JPanel panel, JLabel label) {
        setPosition(constraints, 12, y);
        constraints.weightx = 0.0;
        layout.setConstraints(label, constraints);
        panel.add(label);
    }

    /**
     * init a value field
     *
     * @param y
     * @param constraints
     * @param layout
     * @param panel
     * @param component
     */
    private void initBlueField(int y, GridBagConstraints constraints, GridBagLayout layout, JPanel panel, JComponent component) {
        setPosition(constraints, 13, y);
        constraints.weightx = 1.0;
        component.setPreferredSize(COMPONENTENSIZE2);
        layout.setConstraints(component, constraints);
        panel.add(component);
    }

    /**
     * init a value field
     *
     * @param y
     * @param constraints
     * @param layout
     * @param panel
     * @param component
     */
    private void initYellowMainField(int x, int y, GridBagConstraints constraints, GridBagLayout layout, JPanel panel, JComponent component) {
        setPosition(constraints, x, y);
        constraints.weightx = 1.0;
        component.setPreferredSize(COMPONENTENSIZE3);
        layout.setConstraints(component, constraints);
        panel.add(component);
    }

    /**
     * init a value field
     *
     * @param y
     * @param constraints
     * @param layout
     * @param panel
     * @param component
     */
    private void initYellowChangesField(int x, int y, GridBagConstraints constraints, GridBagLayout layout, JPanel panel, JComponent component) {
        setPosition(constraints, x, y);
        constraints.weightx = 1.0;
        component.setPreferredSize(COMPONENTENSIZE4);
        layout.setConstraints(component, constraints);
        panel.add(component);
    }

    /**
     * set position in gridBag
     *
     * @param c
     * @param x
     * @param y
     */
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

    /**
     * clears all labels
     */
    private void resetLabels() {
        jlPlayerAvatar.setIcon(null);
//        m_jpAge.clear();
        jlNationality.setIcon(null);
        jlNationality.setToolTipText(null);
        jlNationality.setText("");
        m_jpPositioned.clear();
        m_jpStatus.clear();
        m_jcbSquad.setSelectedItem("");
        m_jcbInformation.setSelectedItem("");
        m_jpRating.clear();
        m_jllWage.clear();
        m_jllTSI.clear();
        m_jpForm.clear();
        m_jpStamina.clear();
        m_jpKeeper.clear();
        m_jpDefending.clear();
        m_jpPlaymaking.clear();
        m_jpPassing.clear();
        m_jpWinger.clear();
        m_jpSetPieces.clear();
        m_jpScoring.clear();

        m_jpFormChange.clear();
        m_jpStaminaChange.clear();
        m_jpKeeperChange.clear();
        m_jpDefendingChange.clear();
        m_jpPlaymakingChange.clear();
        m_jpPassingChange.clear();
        m_jpWingerChange.clear();
        m_jpSetPiecesChange.clear();
        m_jpScoringChange.clear();

        m_jpBestPosition.clear();
        m_jcbUserBestPosition.setSelectedItem("");

        for (int i = 0; i < playerPositionValues.length; i++) {
            playerPositionValues[i].clear();
        }
        m_jpGoalsFriendly.clear();
        m_jpGoalsLeague.clear();
        m_jpGoalsCup.clear();
        m_jpGoalsTotal.clear();
        m_jpHattricks.clear();
        jlSpecialty.setText("");
        jlSpecialty.setIcon(null);
        jlPlayerDescription.setText("");
        resetLabel(jlInTeamSince);;
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
}
