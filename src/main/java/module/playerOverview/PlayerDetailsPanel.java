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
    private final JPanel jpPlayerGeneral = new JPanel();
    private final JPanel jpPlayerSkill = new JPanel();
    private JLabel jlPlayerAvatar = new JLabel();
    private JLabel jlNationality = new JLabel();
    private JLabel jlSpecialty = new JLabel();
    private final JLabel jlInTeamSince = new JLabel();
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
    private PlayerStatusLabelEntry m_jpStatus = new PlayerStatusLabelEntry(BGcolor, true);
    private final DoubleLabelEntries m_jllWage = new DoubleLabelEntries(new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.LEFT), new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.RIGHT));
    private final DoubleLabelEntries m_jllTSI = new DoubleLabelEntries(new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.LEFT), new ColorLabelEntry("", FGcolor, BGcolor, SwingConstants.RIGHT));
    private JComboBox m_jcbUserBestPosition = new JComboBox(MatchRoleID.POSITIONEN);

    // Top Row, column 3

    // Second Row, Column 1






    // Second Row, Column 2


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
            m_jclFormChange.clear();

            m_jclStaminaChange.clear();
            m_jclGKchange.clear();
            m_jclDEchange.clear();

            m_jclPMchange.clear();
            m_jclPSchange.clear();

            m_jclWIchange.clear();
            m_jclSPchange.clear();

            m_jclSCchange.clear();
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

        int iSpecialty = m_clPlayer.getPlayerSpecialty();
        if (iSpecialty == 0) {
            jlSpecialty.setText("-");
        }
        else{
            jlSpecialty.setText(PlayerSpeciality.toString(iSpecialty));
        }
        jlSpecialty.setIcon(ImageUtilities.getSmallPlayerSpecialtyIcon(HOIconName.SPECIALTIES[iSpecialty]));

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
        layout.setConstraints(jlPlayerDescription, constraints);
        panel.add(jlPlayerDescription);

        // In the team since  =====================
        jlInTeamSince.setHorizontalTextPosition(SwingConstants.LEFT);
        setPosition(constraints, 0, 3);
        layout.setConstraints(jlInTeamSince, constraints);
        panel.add(jlInTeamSince);

        // create player general panel ====================================================================
        final GridBagLayout layoutPlayerGeneralPanel = new GridBagLayout();
        final GridBagConstraints constraintsPlayerGeneralPanel = new GridBagConstraints();
        constraintsPlayerGeneralPanel.fill = GridBagConstraints.BOTH;
//        jpPlayerGeneral.setBackground(Color.ORANGE);
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
        layoutPlayerGeneralPanel.setConstraints(jlSpecialty, constraintsPlayerGeneralPanel);
        jpPlayerGeneral.add(jlSpecialty);

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

        constraints.gridwidth = 6;
        setPosition(constraints, 0, 4);
        layout.setConstraints(jpPlayerGeneral, constraints);
        panel.add(jpPlayerGeneral);


        // create player skill panel ====================================================================
        final GridBagLayout layoutPlayerSkilllPanel = new GridBagLayout();
        final GridBagConstraints constraintsPlayerSkillPanel = new GridBagConstraints();
        constraintsPlayerSkillPanel.fill = GridBagConstraints.BOTH;
        jpPlayerSkill.setLayout(layoutPlayerSkilllPanel);
//        jpPlayerSkill.setBackground(Color.RED);

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


        setPosition(constraints, 6, 4);
        layout.setConstraints(jpPlayerSkill, constraints);
        panel.add(jpPlayerSkill);

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
        buttonpanel.setBackground(Color.GREEN);
        buttonpanel.setOpaque(true);
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


        m_jclFormChange.clear();
        m_jclStaminaChange.clear();
        m_jclGKchange.clear();
        m_jclDEchange.clear();
        m_jclPMchange.clear();
        m_jclPSchange.clear();
        m_jclWIchange.clear();
        m_jclSPchange.clear();
        m_jclSCchange.clear();

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
