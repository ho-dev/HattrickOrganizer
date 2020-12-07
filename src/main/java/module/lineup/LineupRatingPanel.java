package module.lineup;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.RasenPanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.Team;
import core.model.match.IMatchDetails;
import core.util.Helper;
import module.pluginFeedback.FeedbackPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;


final class LineupRatingPanel extends RasenPanel {

    private final static Color LABEL_BG = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    private final static Color LABEL_FG = ThemeManager.getColor(HOColorName.LEAGUE_FG);
    private final static Color BAD_LABEL_FG = ThemeManager.getColor(HOColorName.TABLEENTRY_DECLINE_FG);
    private final static Color TITLE_FG =ThemeManager.getColor(HOColorName.BLUE);
    private final Border BORDER_RATING_DEFAULT = BorderFactory.createMatteBorder(2, 2, 2, 2, ThemeManager.getColor(HOColorName.PANEL_BG));
    private final Border BORDER_RATING_BELOW_LIMIT = BorderFactory.createMatteBorder(2, 2, 2, 2, ThemeManager.getColor(HOColorName.RATING_BORDER_BELOW_LIMIT));
    private final Border BORDER_RATING_ABOVE_LIMIT = BorderFactory.createMatteBorder(2, 2, 2, 2, ThemeManager.getColor(HOColorName.RATING_BORDER_ABOVE_LIMIT));

    //~ Static fields/initializers -----------------------------------------------------------------
    public static final boolean REIHENFOLGE_STURM2VERTEIDIGUNG = false;

    //~ Instance fields ----------------------------------------------------------------------------

    int hatstat;
    double m_dCentralAttackRating, m_dRightAttackRating, m_dLeftAttackRating, m_dMidfieldRating;
    double m_dCentralDefenseRating, m_dLeftDefenseRating, m_dRightDefenseRating, loddar;

    private ColorLabelEntry m_jlCentralAttackRatingCompare = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private ColorLabelEntry m_jlCentralAttackRatingNumber = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private ColorLabelEntry m_jlRightAttackRatingCompare = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private ColorLabelEntry m_jlRightAttackRatingNumber = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private ColorLabelEntry m_jlLeftAttackRatingCompare = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private ColorLabelEntry m_jlLeftAttackRatingNumber = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private ColorLabelEntry m_jlMidfieldRatingCompare = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private ColorLabelEntry m_jlMidfieldRatingNumber = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private ColorLabelEntry m_jlCentralDefenseRatingCompare = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private ColorLabelEntry m_jlCentralDefenseRatingNumber = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private ColorLabelEntry m_jlLeftDefenseRatingCompare = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private ColorLabelEntry m_jlLeftDefenseRatingNumber = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private ColorLabelEntry m_jlRightDefenseRatingCompare = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private ColorLabelEntry m_jlRightDefenseRatingNumber = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private ColorLabelEntry m_jlHatstatMain = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private ColorLabelEntry m_jlLoddarMain = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private ColorLabelEntry m_jlHatstatCompare = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private ColorLabelEntry m_jlLoddarCompare = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private ColorLabelEntry m_jlTacticRating = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private ColorLabelEntry m_jlFormationExperience = new ColorLabelEntry("",
            LABEL_FG, LABEL_BG, SwingConstants.LEFT);
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
    private boolean m_bReihenfolge = REIHENFOLGE_STURM2VERTEIDIGUNG;
    private final JButton copyButton = new JButton();
    private final JButton feedbackButton = new JButton();
    private Dimension SIZE = new Dimension(Helper.calcCellWidth(160), Helper.calcCellWidth(40));
    private final Double COLOR_BORDERS_LIMIT_RATIO = 0.85;


    protected LineupRatingPanel() {
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

        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 1;
        mainLayout.setConstraints(m_jpLeftDefense, gbcMainLayout);
        mainPanel.add(m_jpLeftDefense);


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

        gbcMainLayout.gridx = 2;
        mainLayout.setConstraints(m_jpRightDefense, gbcMainLayout);
        mainPanel.add(m_jpRightDefense);


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
        JLabel lblHatStat = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.hatstats"));
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
        JLabel lblLoddar = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.loddarstats"));
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
        JLabel lbTactic = new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.tactic"));
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
        JLabel lbFormation = new JLabel(HOVerwaltung.instance().getLanguageString("ls.team.formation"));
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


        //--- BOTTOM RIGHT:   Copy Rating and Feedback button
        jpRatingValueAndDelta = new JPanel(new BorderLayout());
        jpRatingValueAndDelta.setOpaque(false);
        JPanel subButtonPanel = new JPanel();
        subButtonPanel.setOpaque(false);

        feedbackButton.setToolTipText(HOVerwaltung.instance().getLanguageString("Lineup.Feedback.ToolTip"));
        feedbackButton.setIcon(ThemeManager.getIcon(HOIconName.FEEDBACK));
        feedbackButton.addActionListener(e -> new FeedbackPanel());
        feedbackButton.setPreferredSize(new Dimension(24, 24));
        feedbackButton.setMaximumSize(new Dimension(24, 24));
        feedbackButton.setOpaque(false);
        feedbackButton.setContentAreaFilled(false);
        feedbackButton.setBorderPainted(false);
        subButtonPanel.add(feedbackButton);

        copyButton.setToolTipText(HOVerwaltung.instance().getLanguageString("Lineup.CopyRatings.ToolTip"));
        copyButton.setIcon(ThemeManager.getIcon(HOIconName.INFO));
        copyButton.addActionListener(new CopyListener(this));
        copyButton.setPreferredSize(new Dimension(18, 18));
        copyButton.setMaximumSize(new Dimension(18, 18));
        copyButton.setOpaque(false);
        copyButton.setContentAreaFilled(false);
        copyButton.setBorderPainted(false);
        subButtonPanel.add(copyButton);

        jpRatingValueAndDelta.add(subButtonPanel, BorderLayout.CENTER);

        gbcMainLayout.gridx = 2;
        gbcMainLayout.gridy = 5;
        mainLayout.setConstraints(jpRatingValueAndDelta, gbcMainLayout);
        mainPanel.add(jpRatingValueAndDelta);

        add(mainPanel);


        ////////////////////////////////////////////////////////////////////////
        initToolTips();

        //Alle zahlen auf 0, Default ist -oo
        m_jlLeftDefenseRatingCompare.setSpecialNumber(0f, false);
        m_jlCentralDefenseRatingCompare.setSpecialNumber(0f, false);
        m_jlRightDefenseRatingCompare.setSpecialNumber(0f, false);
        m_jlMidfieldRatingCompare.setSpecialNumber(0f, false);
        m_jlRightAttackRatingCompare.setSpecialNumber(0f, false);
        m_jlCentralAttackRatingCompare.setSpecialNumber(0f, false);
        m_jlLeftAttackRatingCompare.setSpecialNumber(0f, false);
        m_jlHatstatCompare.setSpecialNumber(0, false);
        m_jlLoddarCompare.setSpecialNumber(0f, false);
    }

    /**
     * Initialize all tool tips.
     */
    private void initToolTips() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
            m_jlLeftDefenseRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
            m_jlLeftDefenseRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
            m_jlLeftDefenseRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
            m_jlCentralDefenseRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_jlCentralDefenseRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_jlCentralDefenseRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_jlRightDefenseRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_jlRightDefenseRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_jlRightDefenseRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_jlMidfieldRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_jlMidfieldRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_jlMidfieldRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_jlRightAttackRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_jlRightAttackRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_jlRightAttackRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_jlCentralAttackRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_jlCentralAttackRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_jlCentralAttackRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_jlLeftAttackRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
            m_jlLeftAttackRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
            m_jlLeftAttackRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
        } else {
            m_jlLeftDefenseRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
            m_jlLeftDefenseRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
            m_jlLeftDefenseRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
            m_jlCentralDefenseRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_jlCentralDefenseRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_jlCentralDefenseRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_jlRightDefenseRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_jlRightDefenseRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_jlRightDefenseRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_jlMidfieldRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_jlMidfieldRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_jlMidfieldRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_jlRightAttackRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_jlRightAttackRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_jlRightAttackRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_jlCentralAttackRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_jlCentralAttackRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_jlCentralAttackRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_jlLeftAttackRatingText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
            m_jlLeftAttackRatingNumber.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
            m_jlLeftAttackRatingCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
        }
    }

    String getMidfieldRating() {
        return m_clFormat.format(m_dMidfieldRating);
    }
    String getLeftDefenseRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
            return m_clFormat.format(m_dRightDefenseRating);
        } else {
            return m_clFormat.format(m_dRightAttackRating);
        }
    }
    String getCentralDefenseRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
            return m_clFormat.format(m_dCentralDefenseRating);
        } else {
            return m_clFormat.format(m_dCentralAttackRating);
        }
    }
    String getRightDefenseRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
            return m_clFormat.format(m_dLeftDefenseRating);
        } else {
            return m_clFormat.format(m_dLeftAttackRating);
        }
    }

    String getLeftAttackRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
            return m_clFormat.format(m_dLeftAttackRating);
        } else {
            return m_clFormat.format(m_dLeftDefenseRating);
        }
    }
    String getCentralAttackRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
            return m_clFormat.format(m_dCentralAttackRating);
        } else {
            return m_clFormat.format(m_dCentralDefenseRating);
        }
    }
    String getRightAttackRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
            return m_clFormat.format(m_dRightAttackRating);
        } else {
            return m_clFormat.format(m_dRightDefenseRating);
        }
    }

    private String getFormationExperienceTooltip() {
        Team team = HOVerwaltung.instance().getModel().getTeam();
        StringBuilder builder = new StringBuilder();
        int exp = team.getFormationExperience550();
        builder.append("<html>");
        builder.append("<b>").append(HOVerwaltung.instance().getLanguageString("ls.team.formationexperience")).append("</b><br><br>");
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

}
