package module.lineup.ratings;

import core.constants.player.PlayerAbility;
import core.datatype.CBItem;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ComboBoxTitled;
import core.gui.comp.panel.RasenPanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.Team;
import core.model.match.IMatchDetails;
import core.rating.RatingPredictionConfig;
import core.util.Helper;
import module.lineup.CopyListener;
import module.lineup.Lineup;
import module.pluginFeedback.FeedbackPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.Border;
import static module.lineup.LineupPanel.TITLE_FG;


public final class LineupRatingPanel extends RasenPanel implements core.gui.Refreshable{

    private final MinuteTogglerPanel m_jpMinuteToggler = new MinuteTogglerPanel(this);
    private final static Color LABEL_BG = ThemeManager.getColor(HOColorName.PANEL_BG);
    private final static Color LABEL_FG = ThemeManager.getColor(HOColorName.LEAGUE_FG);
    private final static Color BAD_LABEL_FG = ThemeManager.getColor(HOColorName.TABLEENTRY_DECLINE_FG);
    private Border BORDER_RATING_DEFAULT = BorderFactory.createMatteBorder(3, 3, 3, 3, ThemeManager.getColor(HOColorName.PLAYER_POSITION_PANEL_BORDER));
    private final Border BORDER_RATING_BELOW_LIMIT = BorderFactory.createMatteBorder(3, 3, 3, 3, ThemeManager.getColor(HOColorName.RATING_BORDER_BELOW_LIMIT));
    private final Border BORDER_RATING_ABOVE_LIMIT = BorderFactory.createMatteBorder(3, 3, 3, 3, ThemeManager.getColor(HOColorName.RATING_BORDER_ABOVE_LIMIT));
    int hatstat;
    double m_dCentralAttackRating, m_dRightAttackRating, m_dLeftAttackRating, m_dMidfieldRating;
    double m_dCentralDefenseRating, m_dLeftDefenseRating, m_dRightDefenseRating, loddar;
    private final ColorLabelEntry m_jlCentralAttackRatingCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlCentralAttackRatingNumber = new ColorLabelEntry("",  LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlRightAttackRatingCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlRightAttackRatingNumber = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlLeftAttackRatingCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlLeftAttackRatingNumber = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlMidfieldRatingCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlMidfieldRatingNumber = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlCentralDefenseRatingCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlCentralDefenseRatingNumber = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlLeftDefenseRatingCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlLeftDefenseRatingNumber = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlRightDefenseRatingCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlRightDefenseRatingNumber = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlHatstatMain = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlLoddarMain = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlHatstatCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlLoddarCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlTacticRating = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlFormationExperience = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.LEFT);
    private JComboBox<CBItem> m_jcbPredictionModel;
    private JLabel m_jlCentralAttackRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlRightAttackRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlLeftAttackRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlMidfieldRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlCentralDefenseRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlLeftDefenseRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlRightDefenseRatingText = new JLabel("", SwingConstants.CENTER);
    private JPanel m_jpCentalAttack = new JPanel(new BorderLayout());
    private JPanel m_jpRightAttack = new JPanel(new BorderLayout());
    private JPanel m_jpLeftAttack = new JPanel(new BorderLayout());
    private JPanel m_jpMidfield = new JPanel(new BorderLayout());
    private JPanel m_jpCentralDefense = new JPanel(new BorderLayout());
    private JPanel m_jpLeftDefense = new JPanel(new BorderLayout());
    private JPanel m_jpRightDefense = new JPanel(new BorderLayout());
    private JPanel m_jpGlobalRating = new JPanel(new BorderLayout());
    private NumberFormat m_clFormat;
    private final JButton m_jbCopyRatingButton = new JButton();
    private final JButton m_jbFeedbackButton = new JButton();
    private Dimension SIZE = new Dimension(Helper.calcCellWidth(160), Helper.calcCellWidth(40));
    private final Double COLOR_BORDERS_LIMIT_RATIO = 0.85;
    private static ActionListener cbActionListener;


    public LineupRatingPanel() {
        initComponents();

        if (core.model.UserParameter.instance().nbDecimals == 1) {
            m_clFormat = Helper.DEFAULTDEZIMALFORMAT;
        } else {
            m_clFormat = Helper.DEZIMALFORMAT_2STELLEN;
        }
    }


    public void clear() {
        m_jlLeftDefenseRatingText.setText("");
        m_jlLeftDefenseRatingNumber.clear();
        m_jlLeftDefenseRatingCompare.clear();
        m_jlCentralDefenseRatingText.setText("");
        m_jlCentralDefenseRatingNumber.clear();
        m_jlCentralDefenseRatingCompare.clear();
        m_jlRightDefenseRatingText.setText("");
        m_jlRightDefenseRatingNumber.clear();
        m_jlRightDefenseRatingCompare.clear();
        m_jlMidfieldRatingText.setText("");
        m_jlMidfieldRatingNumber.clear();
        m_jlMidfieldRatingCompare.clear();
        m_jlRightAttackRatingText.setText("");
        m_jlRightAttackRatingNumber.clear();
        m_jlRightAttackRatingCompare.clear();
        m_jlCentralAttackRatingText.setText("");
        m_jlCentralAttackRatingNumber.clear();
        m_jlCentralAttackRatingCompare.clear();
        m_jlLeftAttackRatingText.setText("");
        m_jlLeftAttackRatingNumber.clear();
        m_jlLeftAttackRatingCompare.clear();
        m_jlHatstatMain.clear();
        m_jlHatstatCompare.clear();
        m_jlLoddarMain.clear();
        m_jlLoddarCompare.clear();
        m_jlTacticRating.clear();
        m_jlFormationExperience.clear();
    }

    protected void setCentralAttack(double rating) {
        m_jlCentralAttackRatingNumber.setText(m_clFormat.format(rating));
        m_jlCentralAttackRatingCompare.setSpecialNumber((float) (rating - m_dCentralAttackRating), false);
        m_dCentralAttackRating = rating;
        m_jlCentralAttackRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }

    protected void setRightAttack(double rating) {
        m_jlRightAttackRatingNumber.setText(m_clFormat.format(rating));
        m_jlRightAttackRatingCompare.setSpecialNumber((float) (rating - m_dRightAttackRating), false);
        m_dRightAttackRating = rating;
        m_jlRightAttackRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }

    protected void setLeftAttack(double rating) {
        m_jlLeftAttackRatingNumber.setText(m_clFormat.format(rating));
        m_jlLeftAttackRatingCompare.setSpecialNumber((float) (rating - m_dLeftAttackRating), false);
        m_dLeftAttackRating = rating;
        m_jlLeftAttackRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }

    protected void setMidfield(double rating) {
        m_jlMidfieldRatingNumber.setText(m_clFormat.format(rating));
        m_jlMidfieldRatingCompare.setSpecialNumber((float) (rating - m_dMidfieldRating), false);
        m_dMidfieldRating = rating;
        m_jlMidfieldRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }


    protected void setCentralDefense(double rating) {
        m_jlCentralDefenseRatingNumber.setText(m_clFormat.format(rating));
        m_jlCentralDefenseRatingCompare.setSpecialNumber((float) (rating - m_dCentralDefenseRating), false);
        m_dCentralDefenseRating = rating;
        m_jlCentralDefenseRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }


    protected void setLeftDefense(double rating) {
        m_jlLeftDefenseRatingNumber.setText(m_clFormat.format(rating));
        m_jlLeftDefenseRatingCompare.setSpecialNumber((float) (rating - m_dLeftDefenseRating), false);
        m_dLeftDefenseRating = rating;
        m_jlLeftDefenseRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }


    protected void setRightDefense(double rating) {
        m_jlRightDefenseRatingNumber.setText(m_clFormat.format(rating));
        m_jlRightDefenseRatingCompare.setSpecialNumber((float) (rating - m_dRightDefenseRating), false);
        m_dRightDefenseRating = rating;
        m_jlRightDefenseRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }


    protected void setHatstat(int value) {
        m_jlHatstatMain.setText(Helper.INTEGERFORMAT.format(value));
        m_jlHatstatCompare.setSpecialNumber(value - hatstat, false);
        hatstat = value;
    }

    protected void setTactic(int iTacticType, float iTacticSkill){
        m_jlTacticRating.setText((iTacticType == IMatchDetails.TAKTIK_NORMAL) ? "-" : PlayerAbility.getNameForSkill(iTacticSkill, false, false));
    }

    protected void setFormationExperience(String sFormationDescription, int iFormationExp){
        String formationExperienceTooltip = getFormationExperienceTooltip();
        m_jlFormationExperience.setToolTipText(formationExperienceTooltip);

        if (iFormationExp == -1){
            m_jlFormationExperience.setText(sFormationDescription);
            m_jlFormationExperience.setForeground(BAD_LABEL_FG);
        }
        else{
            m_jlFormationExperience.setText(sFormationDescription + " (" + PlayerAbility.toString(iFormationExp) + ")");
            m_jlFormationExperience.setForeground(LABEL_FG);
        }
    }

    protected void setLoddar(double value) {
        m_jlLoddarMain.setText(m_clFormat.format(value));
        m_jlLoddarCompare.setSpecialNumber((float) (value - loddar), false);
        loddar = value;
    }



    protected void calcColorBorders() {

        double ratingRatio;
        final double avgRating = (m_dLeftDefenseRating + m_dCentralDefenseRating + m_dRightDefenseRating + m_dMidfieldRating + m_dRightAttackRating
                                    + m_dCentralAttackRating + m_dLeftAttackRating) / 7d;

        //Left Defense ============================================
        ratingRatio = m_dLeftDefenseRating / avgRating;

        if (ratingRatio <= COLOR_BORDERS_LIMIT_RATIO) {
            m_jpLeftDefense.setBorder(BORDER_RATING_BELOW_LIMIT);
        }
        else if (ratingRatio >= 1.0/COLOR_BORDERS_LIMIT_RATIO) {
            m_jpLeftDefense.setBorder(BORDER_RATING_ABOVE_LIMIT);
        }
        else{
            m_jpLeftDefense.setBorder(BORDER_RATING_DEFAULT);
        }

        //Central Defense ============================================
        ratingRatio = m_dCentralDefenseRating / avgRating;

        if (ratingRatio <= COLOR_BORDERS_LIMIT_RATIO) {
            m_jpCentralDefense.setBorder(BORDER_RATING_BELOW_LIMIT);
        }
        else if (ratingRatio >= 1.0/COLOR_BORDERS_LIMIT_RATIO) {
            m_jpCentralDefense.setBorder(BORDER_RATING_ABOVE_LIMIT);
        }
        else{
            m_jpCentralDefense.setBorder(BORDER_RATING_DEFAULT);
        }


        //Right Defense ============================================
        ratingRatio = m_dRightDefenseRating / avgRating;

        if (ratingRatio <= COLOR_BORDERS_LIMIT_RATIO) {
            m_jpRightDefense.setBorder(BORDER_RATING_BELOW_LIMIT);
        }
        else if (ratingRatio >= 1.0/COLOR_BORDERS_LIMIT_RATIO) {
            m_jpRightDefense.setBorder(BORDER_RATING_ABOVE_LIMIT);
        }
        else{
            m_jpRightDefense.setBorder(BORDER_RATING_DEFAULT);
        }

        // Midifield ============================================
        ratingRatio = m_dMidfieldRating / avgRating;

        if (ratingRatio <= COLOR_BORDERS_LIMIT_RATIO) {
            m_jpMidfield.setBorder(BORDER_RATING_BELOW_LIMIT);
        }
        else if (ratingRatio >= 1.0/COLOR_BORDERS_LIMIT_RATIO) {
            m_jpMidfield.setBorder(BORDER_RATING_ABOVE_LIMIT);
        }
        else{
            m_jpMidfield.setBorder(BORDER_RATING_DEFAULT);
        }


        // Right Attack ============================================
        ratingRatio = m_dRightAttackRating / avgRating;

        if (ratingRatio <= COLOR_BORDERS_LIMIT_RATIO) {
            m_jpRightAttack.setBorder(BORDER_RATING_BELOW_LIMIT);
        }
        else if (ratingRatio >= 1.0/COLOR_BORDERS_LIMIT_RATIO) {
            m_jpRightAttack.setBorder(BORDER_RATING_ABOVE_LIMIT);
        }
        else{
            m_jpRightAttack.setBorder(BORDER_RATING_DEFAULT);
        }

        // Central Attack ============================================
        ratingRatio = m_dCentralAttackRating / avgRating;

        if (ratingRatio <= COLOR_BORDERS_LIMIT_RATIO) {
            m_jpCentalAttack.setBorder(BORDER_RATING_BELOW_LIMIT);
        }
        else if (ratingRatio >= 1.0/COLOR_BORDERS_LIMIT_RATIO) {
            m_jpCentalAttack.setBorder(BORDER_RATING_ABOVE_LIMIT);
        }
        else{
            m_jpCentalAttack.setBorder(BORDER_RATING_DEFAULT);
        }


        // Left Attack ============================================
        ratingRatio = m_dLeftAttackRating / avgRating;

        if (ratingRatio <= COLOR_BORDERS_LIMIT_RATIO) {
            m_jpLeftAttack.setBorder(BORDER_RATING_BELOW_LIMIT);
        }
        else if (ratingRatio >= 1.0/COLOR_BORDERS_LIMIT_RATIO) {
            m_jpLeftAttack.setBorder(BORDER_RATING_ABOVE_LIMIT);
        }
        else{
            m_jpLeftAttack.setBorder(BORDER_RATING_DEFAULT);
        }


    }

    /**
     * Initialize GUI components.
     */
    private void initComponents() {

        JPanel jpRatingValueAndDelta;
        JPanel jpSectorRating;
        JPanel mainPanel;

        GridBagLayout mainLayout = new GridBagLayout();
        GridBagConstraints gbcMainLayout = new GridBagConstraints();
        gbcMainLayout.anchor = GridBagConstraints.CENTER;
        gbcMainLayout.gridwidth = 2;
        gbcMainLayout.insets = new Insets(5, 5, 0, 5);
        mainPanel = new JPanel(mainLayout);
        mainPanel.setOpaque(false);


        //CENTRAL DEFENSE ========================
        jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlCentralDefenseRatingNumber.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add(m_jlCentralDefenseRatingNumber.getComponent(false));
        jpRatingValueAndDelta.add(m_jlCentralDefenseRatingCompare.getComponent(false));

        jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        jpSectorRating.add(m_jlCentralDefenseRatingText);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpCentralDefense.add(jpSectorRating, BorderLayout.CENTER);
        m_jpCentralDefense.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 1;
        gbcMainLayout.gridy = 0;
        mainLayout.setConstraints(m_jpCentralDefense, gbcMainLayout);
        mainPanel.add(m_jpCentralDefense);


        //RIGHT DEFENSE ========================
        jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlRightDefenseRatingNumber.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add( m_jlRightDefenseRatingNumber.getComponent(false));
        jpRatingValueAndDelta.add(m_jlRightDefenseRatingCompare.getComponent(false));

        jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        jpSectorRating.add(m_jlRightDefenseRatingText);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpRightDefense.add(jpSectorRating, BorderLayout.CENTER);
        m_jpRightDefense.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 1;
        mainLayout.setConstraints(m_jpRightDefense, gbcMainLayout);
        mainPanel.add(m_jpRightDefense);

        //LEFT DEFENSE ========================
        jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlLeftDefenseRatingNumber.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add(m_jlLeftDefenseRatingNumber.getComponent(false));
        jpRatingValueAndDelta.add(m_jlLeftDefenseRatingCompare.getComponent(false));

        jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        jpSectorRating.add(m_jlLeftDefenseRatingText);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpLeftDefense.add(jpSectorRating, BorderLayout.CENTER);
        m_jpLeftDefense.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 2;
        mainLayout.setConstraints(m_jpLeftDefense, gbcMainLayout);
        mainPanel.add(m_jpLeftDefense);


        //Midfield ==================================================
        jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlMidfieldRatingNumber.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add(m_jlMidfieldRatingNumber.getComponent(false));
        jpRatingValueAndDelta.add(m_jlMidfieldRatingCompare.getComponent(false));

        jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        jpSectorRating.add(m_jlMidfieldRatingText);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpMidfield.add(jpSectorRating, BorderLayout.CENTER);
        m_jpMidfield.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 1;
        gbcMainLayout.gridy = 2;
        mainLayout.setConstraints(m_jpMidfield, gbcMainLayout);
        mainPanel.add(m_jpMidfield);


        //Right Attack ====================================================================
        jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlRightAttackRatingNumber.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add( m_jlRightAttackRatingNumber.getComponent(false));
        jpRatingValueAndDelta.add(m_jlRightAttackRatingCompare.getComponent(false));

        jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        jpSectorRating.add(m_jlRightAttackRatingText);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpRightAttack.add(jpSectorRating, BorderLayout.CENTER);
        m_jpRightAttack.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 3;
        mainLayout.setConstraints(m_jpRightAttack, gbcMainLayout);
        mainPanel.add(m_jpRightAttack);



        //Left Attack ========================================================================
        jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlLeftAttackRatingNumber.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add( m_jlLeftAttackRatingNumber.getComponent(false));
        jpRatingValueAndDelta.add(m_jlLeftAttackRatingCompare.getComponent(false));

        jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        jpSectorRating.add(m_jlLeftAttackRatingText);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpLeftAttack.add(jpSectorRating, BorderLayout.CENTER);
        m_jpLeftAttack.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 2;
        mainLayout.setConstraints(m_jpLeftAttack, gbcMainLayout);
        mainPanel.add(m_jpLeftAttack);


        //Central Attack ==============================================================
        jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlCentralAttackRatingNumber.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add(m_jlCentralAttackRatingNumber.getComponent(false));
        jpRatingValueAndDelta.add(m_jlCentralAttackRatingCompare.getComponent(false));

        jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        jpSectorRating.add(m_jlCentralAttackRatingText);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpCentalAttack.add(jpSectorRating, BorderLayout.CENTER);
        m_jpCentalAttack.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 1;
        gbcMainLayout.gridy = 4;
        mainLayout.setConstraints(m_jpCentalAttack, gbcMainLayout);
        mainPanel.add(m_jpCentalAttack);


        //HatStats + Loddar + Tactic    ===============================
        GridBagLayout globalRatingsLayout = new GridBagLayout();
        GridBagConstraints gbcGlobalRatingsLayout = new GridBagConstraints();
        gbcGlobalRatingsLayout.anchor = GridBagConstraints.CENTER;
        gbcGlobalRatingsLayout.gridwidth = 1;
        gbcGlobalRatingsLayout.insets = new Insets(5, 5, 0, 5);
        m_jpGlobalRating = new JPanel(globalRatingsLayout);


        //HATSTATS  ========================
        JLabel lblHatStat = new JLabel(getLangStr("ls.match.ratingtype.hatstats"));
        lblHatStat.setForeground(TITLE_FG);
        lblHatStat.setFont(getFont().deriveFont(Font.BOLD));
        lblHatStat.setHorizontalAlignment(SwingConstants.LEFT);

        gbcGlobalRatingsLayout.gridx = 0;
        gbcGlobalRatingsLayout.gridy = 0;
        gbcGlobalRatingsLayout.anchor = GridBagConstraints.WEST;
        globalRatingsLayout.setConstraints(lblHatStat, gbcGlobalRatingsLayout);
        m_jpGlobalRating.add(lblHatStat);

        m_jlHatstatMain.setFontStyle(Font.BOLD);
        gbcGlobalRatingsLayout.gridx = 1;
        gbcGlobalRatingsLayout.anchor = GridBagConstraints.CENTER;
        globalRatingsLayout.setConstraints(m_jlHatstatMain, gbcGlobalRatingsLayout);
        m_jpGlobalRating.add(m_jlHatstatMain);

        gbcGlobalRatingsLayout.gridx = 2;
        globalRatingsLayout.setConstraints(m_jlHatstatCompare, gbcGlobalRatingsLayout);
        m_jpGlobalRating.add(m_jlHatstatCompare);

        // LODDAR ========================================
        JLabel lblLoddar = new JLabel(getLangStr("ls.match.ratingtype.loddarstats"));
        lblLoddar.setForeground(TITLE_FG);
        lblLoddar.setFont(getFont().deriveFont(Font.BOLD));
        lblHatStat.setHorizontalAlignment(SwingConstants.LEFT);


        gbcGlobalRatingsLayout.gridx = 0;
        gbcGlobalRatingsLayout.gridy = 1;
        gbcGlobalRatingsLayout.anchor = GridBagConstraints.WEST;
        globalRatingsLayout.setConstraints(lblLoddar, gbcGlobalRatingsLayout);
        m_jpGlobalRating.add(lblLoddar);

        m_jlLoddarMain.setFontStyle(Font.BOLD);
        gbcGlobalRatingsLayout.gridx = 1;
        gbcGlobalRatingsLayout.anchor = GridBagConstraints.CENTER;
        globalRatingsLayout.setConstraints(m_jlLoddarMain, gbcGlobalRatingsLayout);
        m_jpGlobalRating.add(m_jlLoddarMain);

        gbcGlobalRatingsLayout.gridx = 2;
        globalRatingsLayout.setConstraints(m_jlLoddarCompare, gbcGlobalRatingsLayout);
        m_jpGlobalRating.add(m_jlLoddarCompare);


        // Tactic ========================================
        JLabel lbTactic = new JLabel(getLangStr("ls.team.tactic"));
        lbTactic.setForeground(TITLE_FG);
        lbTactic.setFont(getFont().deriveFont(Font.BOLD));
        lbTactic.setHorizontalAlignment(SwingConstants.LEFT);

        gbcGlobalRatingsLayout.gridx = 0;
        gbcGlobalRatingsLayout.gridy = 2;
        gbcGlobalRatingsLayout.anchor = GridBagConstraints.WEST;
        globalRatingsLayout.setConstraints(lbTactic, gbcGlobalRatingsLayout);
        m_jpGlobalRating.add(lbTactic);

        gbcGlobalRatingsLayout.gridx = 1;
        gbcGlobalRatingsLayout.anchor = GridBagConstraints.CENTER;
        globalRatingsLayout.setConstraints(m_jlTacticRating, gbcGlobalRatingsLayout);
        m_jpGlobalRating.add(m_jlTacticRating);



        // Formation experience ========================================
        JLabel lbFormation = new JLabel(getLangStr("ls.team.formation"));
        lbFormation.setForeground(TITLE_FG);
        lbFormation.setFont(getFont().deriveFont(Font.BOLD));
        lbFormation.setHorizontalAlignment(SwingConstants.LEFT);

        gbcGlobalRatingsLayout.gridx = 0;
        gbcGlobalRatingsLayout.gridy = 3;
        gbcGlobalRatingsLayout.anchor = GridBagConstraints.WEST;
        gbcGlobalRatingsLayout.anchor = GridBagConstraints.CENTER;
        globalRatingsLayout.setConstraints(lbFormation, gbcGlobalRatingsLayout);
        m_jpGlobalRating.add(lbFormation);

        gbcGlobalRatingsLayout.gridx = 1;
        gbcGlobalRatingsLayout.gridwidth = 2;
        globalRatingsLayout.setConstraints(m_jlFormationExperience, gbcGlobalRatingsLayout);
        m_jpGlobalRating.add(m_jlFormationExperience);

        // Add the box
        m_jpGlobalRating.setBackground(LABEL_BG);
        m_jpGlobalRating.setBorder(BORDER_RATING_DEFAULT);
        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 5;
        mainLayout.setConstraints(m_jpGlobalRating, gbcMainLayout);
        mainPanel.add(m_jpGlobalRating);


        //Panel for Rating model selection =====================================================
        GridBagLayout ratingPanelLayout = new GridBagLayout();
        GridBagConstraints gbcRatingPanelLayout = new GridBagConstraints();
        gbcRatingPanelLayout.anchor = GridBagConstraints.CENTER;
        gbcRatingPanelLayout.insets = new Insets(0, 0, 0, 0);
        JPanel jpRatingModelAndSharing = new JPanel(ratingPanelLayout);
        jpRatingModelAndSharing.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
        jpRatingModelAndSharing.setBorder(BORDER_RATING_DEFAULT);


        m_jcbPredictionModel = new JComboBox<>(getPredictionItems());
        JPanel m_jpPredictionModel = new ComboBoxTitled(getLangStr("PredictionType"), m_jcbPredictionModel, true);
        m_jpPredictionModel.setBorder(null);
        gbcRatingPanelLayout.gridx = 0;
        gbcRatingPanelLayout.gridy = 0;
        gbcRatingPanelLayout.gridheight = 2;
        ratingPanelLayout.setConstraints(m_jpPredictionModel, gbcRatingPanelLayout);
        jpRatingModelAndSharing.add(m_jpPredictionModel);

        m_jbFeedbackButton.setIcon(ImageUtilities.getSvgIcon(HOIconName.UPLOAD, Map.of("strokeColor", TITLE_FG), 24, 24));
        m_jbFeedbackButton.addActionListener(e -> new FeedbackPanel());
        m_jbFeedbackButton.setPreferredSize(new Dimension(24, 24));
        m_jbFeedbackButton.setMinimumSize(new Dimension(24, 24));
        m_jbFeedbackButton.setMaximumSize(new Dimension(24, 24));
        m_jbFeedbackButton.setBorderPainted(false);
        m_jbFeedbackButton.setContentAreaFilled(false);
        m_jbFeedbackButton.setBorderPainted(false);
        gbcRatingPanelLayout.gridx = 1;
        gbcRatingPanelLayout.gridheight = 1;
        gbcRatingPanelLayout.insets = new Insets(5 , 8 , 0, 8);
        ratingPanelLayout.setConstraints(m_jbFeedbackButton, gbcRatingPanelLayout);
        jpRatingModelAndSharing.add(m_jbFeedbackButton);

        m_jbCopyRatingButton.setIcon(ImageUtilities.getCopyIcon(22, TITLE_FG));
        m_jbCopyRatingButton.addActionListener(new CopyListener(this));
        m_jbCopyRatingButton.setBorderPainted(false);
        m_jbCopyRatingButton.setContentAreaFilled(false);
        m_jbCopyRatingButton.setBorderPainted(false);
        m_jbCopyRatingButton.setPreferredSize(new Dimension(24, 24));
        m_jbCopyRatingButton.setMinimumSize(new Dimension(24, 24));
        m_jbCopyRatingButton.setMaximumSize(new Dimension(24, 24));
        gbcRatingPanelLayout.gridy = 1;
        gbcRatingPanelLayout.insets = new Insets(5 , 8, 5, 8);
        ratingPanelLayout.setConstraints(m_jbCopyRatingButton, gbcRatingPanelLayout);
        jpRatingModelAndSharing.add(m_jbCopyRatingButton);


        gbcMainLayout.gridx = 2;
        gbcMainLayout.gridy = 5;
        mainLayout.setConstraints(jpRatingModelAndSharing, gbcMainLayout);
        mainPanel.add(jpRatingModelAndSharing);


        //create final panel =============================================

        add(mainPanel);

        initToolTips();

        //initialize all rating compare to 0
        m_jlLeftDefenseRatingCompare.setSpecialNumber(0f, false);
        m_jlCentralDefenseRatingCompare.setSpecialNumber(0f, false);
        m_jlRightDefenseRatingCompare.setSpecialNumber(0f, false);
        m_jlMidfieldRatingCompare.setSpecialNumber(0f, false);
        m_jlRightAttackRatingCompare.setSpecialNumber(0f, false);
        m_jlCentralAttackRatingCompare.setSpecialNumber(0f, false);
        m_jlLeftAttackRatingCompare.setSpecialNumber(0f, false);
        m_jlHatstatCompare.setSpecialNumber(0, false);
        m_jlLoddarCompare.setSpecialNumber(0f, false);

        cbActionListener = e -> {
            if (e.getSource().equals(m_jcbPredictionModel)) {
                RatingPredictionConfig.setInstancePredictionType(((CBItem) Objects.requireNonNull(m_jcbPredictionModel.getSelectedItem())).getId());
                setRatings();
            }
        };

        addListeners();
    }


    private void initToolTips() {
            m_jlLeftDefenseRatingText.setToolTipText(getLangStr("ls.match.ratingsector.leftdefence"));
            m_jlLeftDefenseRatingNumber.setToolTipText(getLangStr("ls.match.ratingsector.leftdefence"));
            m_jlLeftDefenseRatingCompare.setToolTipText(getLangStr("ls.match.ratingsector.leftdefence"));
            m_jlCentralDefenseRatingText.setToolTipText(getLangStr("ls.match.ratingsector.centraldefence"));
            m_jlCentralDefenseRatingNumber.setToolTipText(getLangStr("ls.match.ratingsector.centraldefence"));
            m_jlCentralDefenseRatingCompare.setToolTipText(getLangStr("ls.match.ratingsector.centraldefence"));
            m_jlRightDefenseRatingText.setToolTipText(getLangStr("ls.match.ratingsector.rightdefence"));
            m_jlRightDefenseRatingNumber.setToolTipText(getLangStr("ls.match.ratingsector.rightdefence"));
            m_jlRightDefenseRatingCompare.setToolTipText(getLangStr("ls.match.ratingsector.rightdefence"));
            m_jlMidfieldRatingText.setToolTipText(getLangStr("ls.match.ratingsector.midfield"));
            m_jlMidfieldRatingNumber.setToolTipText(getLangStr("ls.match.ratingsector.midfield"));
            m_jlMidfieldRatingCompare.setToolTipText(getLangStr("ls.match.ratingsector.midfield"));
            m_jlRightAttackRatingText.setToolTipText(getLangStr("ls.match.ratingsector.rightattack"));
            m_jlRightAttackRatingNumber.setToolTipText(getLangStr("ls.match.ratingsector.rightattack"));
            m_jlRightAttackRatingCompare.setToolTipText(getLangStr("ls.match.ratingsector.rightattack"));
            m_jlCentralAttackRatingText.setToolTipText(getLangStr("ls.match.ratingsector.centralattack"));
            m_jlCentralAttackRatingNumber.setToolTipText(getLangStr("ls.match.ratingsector.centralattack"));
            m_jlCentralAttackRatingCompare.setToolTipText(getLangStr("ls.match.ratingsector.centralattack"));
            m_jlLeftAttackRatingText.setToolTipText(getLangStr("ls.match.ratingsector.leftattack"));
            m_jlLeftAttackRatingNumber.setToolTipText(getLangStr("ls.match.ratingsector.leftattack"));
            m_jlLeftAttackRatingCompare.setToolTipText(getLangStr("ls.match.ratingsector.leftattack"));
            m_jbFeedbackButton.setToolTipText(getLangStr("Lineup.Feedback.ToolTip"));
            m_jbCopyRatingButton.setToolTipText(getLangStr("Lineup.CopyRatings.ToolTip"));
            m_jcbPredictionModel.setToolTipText(getLangStr("Lineup.PredictionModel.ToolTip"));
    }

    public String getMidfieldRating() {return m_clFormat.format(m_dMidfieldRating);}
    public String getLeftDefenseRating() {return m_clFormat.format(m_dLeftDefenseRating);}
    public String getCentralDefenseRating() {return m_clFormat.format(m_dCentralDefenseRating);}
    public String getRightDefenseRating() {return m_clFormat.format(m_dRightDefenseRating);}
    public String getLeftAttackRating() { return m_clFormat.format(m_dLeftAttackRating);}
    public String getCentralAttackRating() {return m_clFormat.format(m_dCentralAttackRating);}
    public String getRightAttackRating() {return m_clFormat.format(m_dRightAttackRating);}

    private String getFormationExperienceTooltip() {
        Team team = HOVerwaltung.instance().getModel().getTeam();
        StringBuilder builder = new StringBuilder();
        int exp = team.getFormationExperience550();
        builder.append("<html>");
        builder.append("<b>").append(getLangStr("ls.team.formationexperience")).append("</b><br><br>");
        builder.append("5-5-0&#160&#160&#160");
        builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
        exp = team.getFormationExperience541();
        builder.append("5-4-1&#160&#160&#160");
        builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
        exp = team.getFormationExperience532();
        builder.append("5-3-2&#160&#160&#160");
        builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
        exp = team.getFormationExperience523();
        builder.append("5-2-3&#160&#160&#160");
        builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
        exp = team.getFormationExperience451();
        builder.append("4-5-1&#160&#160&#160");
        builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
        exp = team.getFormationExperience442();
        builder.append("4-4-2&#160&#160&#160");
        builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
        exp = team.getFormationExperience433();
        builder.append("4-3-3&#160&#160&#160");
        builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
        exp = team.getFormationExperience352();
        builder.append("3-5-2&#160&#160&#160");
        builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
        exp = team.getFormationExperience343();
        builder.append("3-4-3&#160&#160&#160");
        builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
        exp = team.getFormationExperience253();
        builder.append("2-5-3&#160&#160&#160");
        builder.append(PlayerAbility.toString(exp)).append(" (").append(exp).append(")<br>");
        builder.append("</html>");
        return builder.toString();
    }

    private String getLangStr(String key) {return HOVerwaltung.instance().getLanguageString(key);}

    private CBItem[] getPredictionItems() {
        final ResourceBundle bundle = HOVerwaltung.instance().getResource();
        String[] allPredictionNames = RatingPredictionConfig.getAllPredictionNames();
        CBItem[] allItems = new CBItem[allPredictionNames.length];
        for (int i = 0; i < allItems.length; i++) {
            String predictionName = allPredictionNames[i];
            if (bundle.containsKey("prediction." + predictionName))
                predictionName = HOVerwaltung.instance().getLanguageString(
                        "prediction." + predictionName);
            allItems[i] = new CBItem(predictionName, i);
        }
        return allItems;
    }

    public void setRatings() {
        if (HOVerwaltung.instance().getModel().getTeam() != null) {
            final HOModel homodel = HOVerwaltung.instance().getModel();
            final Lineup currentLineup = homodel.getLineup();

            clear();
            setRightDefense(currentLineup.getRatings().getLeftDefense().get(m_jpMinuteToggler.getCurrentKey()));
            setCentralDefense(currentLineup.getRatings().getCentralDefense().get(m_jpMinuteToggler.getCurrentKey()));
            setLeftDefense(currentLineup.getRatings().getRightDefense().get(m_jpMinuteToggler.getCurrentKey()));
            setMidfield(currentLineup.getRatings().getMidfield().get(m_jpMinuteToggler.getCurrentKey()));
            setLeftAttack(currentLineup.getRatings().getLeftAttack().get(m_jpMinuteToggler.getCurrentKey()));
            setCentralAttack(currentLineup.getRatings().getCentralAttack().get(m_jpMinuteToggler.getCurrentKey()));
            setRightAttack(currentLineup.getRatings().getRightAttack().get(m_jpMinuteToggler.getCurrentKey()));
            setLoddar(Helper.round(currentLineup.getRatings().getLoddarStat().get(m_jpMinuteToggler.getCurrentKey()), 2));
            setHatstat(currentLineup.getRatings().getHatStats().get(m_jpMinuteToggler.getCurrentKey()));
            int iTacticType = currentLineup.getTacticType();
            setTactic(iTacticType, currentLineup.getTacticLevel(iTacticType));
            setFormationExperience(currentLineup.getCurrentTeamFormationString(), currentLineup.getExperienceForCurrentTeamFormation());

            // Recalculate Borders
            calcColorBorders();
        }
    }

    private void addListeners() {
        m_jcbPredictionModel.addActionListener(cbActionListener);
    }

    private void removeListeners() {
        m_jcbPredictionModel.removeActionListener(cbActionListener);
    }

    @Override
    public void refresh() {
        removeListeners();
        setPredictionModel(RatingPredictionConfig.getInstancePredictionType());
        setRatings();
        addListeners();
    }

    @Override
    public void reInit() {
        refresh();
    }

    private void setPredictionModel(int newPredictionType) {
        core.util.Helper.setComboBoxFromID(m_jcbPredictionModel, newPredictionType);
    }

}
