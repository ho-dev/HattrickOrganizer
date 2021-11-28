package module.lineup.ratings;

import core.constants.player.PlayerAbility;
import core.datatype.CBItem;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.RasenPanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.*;
import core.model.match.IMatchDetails;
import core.rating.RatingPredictionConfig;
import core.util.Helper;
import module.lineup.CopyListener;
import module.lineup.Lineup;
import module.pluginFeedback.FeedbackPanel;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.*;

import static core.model.UserParameter.GOALKEEPER_AT_TOP;
import static module.lineup.LineupPanel.TITLE_FG;


public final class LineupRatingPanel extends RasenPanel implements core.gui.Refreshable {

    class RatingPanel extends JPanel {
        private Color color;

        public RatingPanel(ColorLabelEntry ratingNumber, ColorLabelEntry ratingCompare, JLabel ratingText ) {
            super(new BorderLayout());
            var jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
            ratingNumber.setFontStyle(Font.BOLD);
            jpRatingValueAndDelta.add(ratingNumber.getComponent(false));
            jpRatingValueAndDelta.add(ratingCompare.getComponent(false));

            var jpSectorRating = new JPanel(new GridLayout(2, 1));
            jpSectorRating.setBackground(LABEL_BG);
            jpSectorRating.add(ratingText);
            jpSectorRating.add(jpRatingValueAndDelta);

            add(jpSectorRating, BorderLayout.CENTER);
            setPreferredSize(SIZE);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int width = 10;
            int height = 10;
            g.setColor(color);
            g.fillOval(5, 5, width, height);
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setRatingRatio(double ratingRatio) {
            if (ratingRatio <= COLOR_BORDERS_LIMIT_RATIO) {
                setColor(RATING_BELOW_LIMIT);
            } else if (ratingRatio >= 1.0 / COLOR_BORDERS_LIMIT_RATIO) {
                setColor(RATING_ABOVE_LIMIT);
            } else {
                setColor(RATING_DEFAULT);
            }
        }
    }

    final static boolean IS_FEEDBACK_PLUGIN_ENABLED = false;
    private final MinuteTogglerPanel m_jpMinuteToggler = new MinuteTogglerPanel(this);
    private final static Color LABEL_BG = ThemeManager.getColor(HOColorName.PANEL_BG);
    private final static Color LABEL_FG = ThemeManager.getColor(HOColorName.LEAGUE_FG);
    private final static Color BAD_LABEL_FG = ThemeManager.getColor(HOColorName.TABLEENTRY_DECLINE_FG);
    private final static Color RATING_DEFAULT = ThemeManager.getColor(HOColorName.PLAYER_POSITION_PANEL_BORDER);
    private final static Color RATING_BELOW_LIMIT = ThemeManager.getColor(HOColorName.RATING_BORDER_BELOW_LIMIT);
    private final static Color RATING_ABOVE_LIMIT = ThemeManager.getColor(HOColorName.RATING_BORDER_ABOVE_LIMIT);
    int iHatStats;
    double m_dCentralAttackRating, m_dRightAttackRating, m_dLeftAttackRating, m_dMidfieldRating;
    double m_dCentralDefenseRating, m_dLeftDefenseRating, m_dRightDefenseRating, loddar;
    private final ColorLabelEntry m_jlCentralAttackRatingCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlCentralAttackRatingNumber = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
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
    private JLabel m_jlCentralAttackRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlRightAttackRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlLeftAttackRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlMidfieldRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlCentralDefenseRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlLeftDefenseRatingText = new JLabel("", SwingConstants.CENTER);
    private JLabel m_jlRightDefenseRatingText = new JLabel("", SwingConstants.CENTER);
    private RatingPanel m_jpCentralAttack;
    private RatingPanel m_jpRightAttack;
    private RatingPanel m_jpLeftAttack;
    private RatingPanel m_jpMidfield;
    private RatingPanel m_jpCentralDefense;
    private RatingPanel m_jpLeftDefense;
    private RatingPanel m_jpRightDefense;
    private JPanel m_jpHatStats = new JPanel(new BorderLayout());
    private JPanel m_jpLoddarStats = new JPanel(new BorderLayout());
    private JPanel m_jpTacticStats = new JPanel(new BorderLayout());
    private JPanel m_jpFormationStats = new JPanel(new BorderLayout());
    private NumberFormat m_clFormat;
    private final JButton m_jbCopyRatingButton = new JButton();
    private final JButton m_jbFeedbackButton = new JButton();
    private Dimension SIZE = new Dimension(Helper.calcCellWidth(120), Helper.calcCellWidth(40));
    private final Double COLOR_BORDERS_LIMIT_RATIO = 0.85;

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

    void setCentralAttack(double rating) {
        m_jlCentralAttackRatingNumber.setText(m_clFormat.format(rating));
        m_jlCentralAttackRatingCompare.setSpecialNumber((float) (rating - m_dCentralAttackRating), false);
        m_dCentralAttackRating = rating;
        m_jlCentralAttackRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }

    void setRightAttack(double rating) {
        m_jlRightAttackRatingNumber.setText(m_clFormat.format(rating));
        m_jlRightAttackRatingCompare.setSpecialNumber((float) (rating - m_dRightAttackRating), false);
        m_dRightAttackRating = rating;
        m_jlRightAttackRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }

    void setLeftAttack(double rating) {
        m_jlLeftAttackRatingNumber.setText(m_clFormat.format(rating));
        m_jlLeftAttackRatingCompare.setSpecialNumber((float) (rating - m_dLeftAttackRating), false);
        m_dLeftAttackRating = rating;
        m_jlLeftAttackRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }

    void setMidfield(double rating) {
        m_jlMidfieldRatingNumber.setText(m_clFormat.format(rating));
        m_jlMidfieldRatingCompare.setSpecialNumber((float) (rating - m_dMidfieldRating), false);
        m_dMidfieldRating = rating;
        m_jlMidfieldRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }


    void setCentralDefense(double rating) {
        m_jlCentralDefenseRatingNumber.setText(m_clFormat.format(rating));
        m_jlCentralDefenseRatingCompare.setSpecialNumber((float) (rating - m_dCentralDefenseRating), false);
        m_dCentralDefenseRating = rating;
        m_jlCentralDefenseRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }


    void setLeftDefense(double rating) {
        m_jlLeftDefenseRatingNumber.setText(m_clFormat.format(rating));
        m_jlLeftDefenseRatingCompare.setSpecialNumber((float) (rating - m_dLeftDefenseRating), false);
        m_dLeftDefenseRating = rating;
        m_jlLeftDefenseRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }


    void setRightDefense(double rating) {
        m_jlRightDefenseRatingNumber.setText(m_clFormat.format(rating));
        m_jlRightDefenseRatingCompare.setSpecialNumber((float) (rating - m_dRightDefenseRating), false);
        m_dRightDefenseRating = rating;
        m_jlRightDefenseRatingText.setText(PlayerAbility.getNameForSkill(rating, false, true));
    }


    void setiHatStats(int value) {
        m_jlHatstatMain.setText(Helper.INTEGERFORMAT.format(value));
        m_jlHatstatCompare.setSpecialNumber(value - iHatStats, false);
        iHatStats = value;
    }

    void setTactic(int iTacticType, float iTacticSkill) {
        m_jlTacticRating.setText((iTacticType == IMatchDetails.TAKTIK_NORMAL) ? "-" : PlayerAbility.getNameForSkill(iTacticSkill, false, false));
    }

    void setFormationExperience(String sFormationDescription, int iFormationExp) {
        String formationExperienceTooltip = getFormationExperienceTooltip();
        m_jlFormationExperience.setToolTipText(formationExperienceTooltip);

        if (iFormationExp == -1) {
            m_jlFormationExperience.setText(sFormationDescription);
            m_jlFormationExperience.setForeground(BAD_LABEL_FG);
        } else {
            m_jlFormationExperience.setText(sFormationDescription + " (" + PlayerAbility.toString(iFormationExp) + ")");
            m_jlFormationExperience.setForeground(LABEL_FG);
        }
    }

    void setLoddar(double value) {
        m_jlLoddarMain.setText(m_clFormat.format(value));
        m_jlLoddarCompare.setSpecialNumber((float) (value - loddar), false);
        loddar = value;
    }

    void calcRatingRatio() {

        double ratingRatio;
        final double avgRating = (m_dLeftDefenseRating + m_dCentralDefenseRating + m_dRightDefenseRating + m_dMidfieldRating + m_dRightAttackRating
                + m_dCentralAttackRating + m_dLeftAttackRating) / 7d;

        m_jpLeftDefense.setRatingRatio( m_dLeftDefenseRating / avgRating );
        m_jpCentralDefense.setRatingRatio( m_dCentralDefenseRating / avgRating );
        m_jpRightDefense.setRatingRatio( m_dRightDefenseRating / avgRating );
        m_jpMidfield.setRatingRatio( m_dMidfieldRating / avgRating );
        m_jpRightAttack.setRatingRatio( m_dRightAttackRating / avgRating );
        m_jpCentralAttack.setRatingRatio( m_dCentralAttackRating / avgRating );
        m_jpLeftAttack.setRatingRatio( m_dLeftAttackRating / avgRating );
    }

    /**
     * Initialize GUI components.
     */
    private void initComponents() {

        m_jpCentralAttack = new RatingPanel(m_jlCentralAttackRatingNumber, m_jlCentralAttackRatingCompare, m_jlCentralAttackRatingText);
        m_jpRightAttack = new RatingPanel(m_jlRightAttackRatingNumber, m_jlRightAttackRatingCompare, m_jlRightAttackRatingText);
        m_jpLeftAttack = new RatingPanel(m_jlLeftAttackRatingNumber, m_jlLeftAttackRatingCompare, m_jlLeftAttackRatingText);
        m_jpMidfield = new RatingPanel(m_jlMidfieldRatingNumber, m_jlMidfieldRatingCompare, m_jlMidfieldRatingText);
        m_jpCentralDefense = new RatingPanel(m_jlCentralDefenseRatingNumber, m_jlCentralDefenseRatingCompare, m_jlCentralDefenseRatingText);
        m_jpLeftDefense = new RatingPanel(m_jlLeftDefenseRatingNumber, m_jlLeftDefenseRatingCompare, m_jlLeftDefenseRatingText);
        m_jpRightDefense = new RatingPanel(m_jlRightDefenseRatingNumber, m_jlRightDefenseRatingCompare, m_jlRightDefenseRatingText);

        JPanel mainPanel;

        GridBagLayout mainLayout = new GridBagLayout();
        GridBagConstraints gbcMainLayout = new GridBagConstraints();
        gbcMainLayout.anchor = GridBagConstraints.CENTER;
        gbcMainLayout.insets = new Insets(5, 5, 0, 5);
        mainPanel = new JPanel(mainLayout);
        mainPanel.setOpaque(false);

        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 0;
        gbcMainLayout.gridwidth = 3;
        gbcMainLayout.fill = GridBagConstraints.HORIZONTAL;
        mainLayout.setConstraints(m_jpMinuteToggler, gbcMainLayout);
        mainPanel.add(m_jpMinuteToggler);

        // DEFENSE
        var defenseLabel = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.defense"));
        defenseLabel.setFont(defenseLabel.getFont().deriveFont(Font.BOLD));

        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 1 + getLineupRatingLabelRowNumber(0);
        gbcMainLayout.gridwidth = 3;
        //gbcMainLayout.fill = GridBagConstraints.HORIZONTAL;
        var defenseLabelPanel = new JPanel();
        defenseLabelPanel.add(defenseLabel);
        mainLayout.setConstraints(defenseLabelPanel, gbcMainLayout);
        mainPanel.add(defenseLabelPanel);

        //CENTRAL DEFENSE ========================
        gbcMainLayout.gridx = getLineupRatingColumnNumber(1);
        gbcMainLayout.gridy = 1 + getLineupRatingValueRowNumber(0);
        gbcMainLayout.gridwidth = 1;
        gbcMainLayout.insets = new Insets(0, 5, 0, 5);
        mainLayout.setConstraints(m_jpCentralDefense, gbcMainLayout);
        mainPanel.add(m_jpCentralDefense);

        //RIGHT DEFENSE ========================
        gbcMainLayout.gridx = getLineupRatingColumnNumber(0);
        //gbcMainLayout.gridy = 1 + getLineupRatingLabelRowNumber(0);
        mainLayout.setConstraints(m_jpRightDefense, gbcMainLayout);
        mainPanel.add(m_jpRightDefense);

        //LEFT DEFENSE ========================
        gbcMainLayout.gridx = getLineupRatingColumnNumber(2);
        mainLayout.setConstraints(m_jpLeftDefense, gbcMainLayout);
        mainPanel.add(m_jpLeftDefense);

        //Midfield ==================================================
        var midfieldLabel = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
        midfieldLabel.setFont(midfieldLabel.getFont().deriveFont(Font.BOLD));
        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 1 + getLineupRatingLabelRowNumber(1);
        gbcMainLayout.gridwidth = 3;
        gbcMainLayout.insets = new Insets(5, 5, 0, 5);
        var midfieldLabelPanel = new JPanel();
        midfieldLabelPanel.add(midfieldLabel);
        mainLayout.setConstraints(midfieldLabelPanel, gbcMainLayout);
        mainPanel.add(midfieldLabelPanel);

        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 1 + getLineupRatingValueRowNumber(1);
        //gbcMainLayout.gridwidth = 1;
        gbcMainLayout.insets = new Insets(0, 5, 0, 5);
        mainLayout.setConstraints(m_jpMidfield, gbcMainLayout);
        mainPanel.add(m_jpMidfield);

        // Attack
        var attackLabel = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.attack"));
        attackLabel.setFont(attackLabel.getFont().deriveFont(Font.BOLD));
        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 1 + getLineupRatingLabelRowNumber(2);
        gbcMainLayout.gridwidth = 3;
        gbcMainLayout.insets = new Insets(5, 5, 0, 5);
        var attackLabelPanel = new JPanel();
        attackLabelPanel.add(attackLabel);
        mainLayout.setConstraints(attackLabelPanel, gbcMainLayout);
        mainPanel.add(attackLabelPanel);

        //Right Attack ====================================================================
        gbcMainLayout.gridx = getLineupRatingColumnNumber(0);
        gbcMainLayout.gridy = 1 + getLineupRatingValueRowNumber(2);
        gbcMainLayout.gridwidth = 1;
        gbcMainLayout.insets = new Insets(0, 5, 0, 5);
        mainLayout.setConstraints(m_jpRightAttack, gbcMainLayout);
        mainPanel.add(m_jpRightAttack);

        //Left Attack ========================================================================
        gbcMainLayout.gridx = getLineupRatingColumnNumber(2);
        mainLayout.setConstraints(m_jpLeftAttack, gbcMainLayout);
        mainPanel.add(m_jpLeftAttack);

        //Central Attack ==============================================================
        gbcMainLayout.gridx = getLineupRatingColumnNumber(1);
        //gbcMainLayout.gridy = 1 + getLineupRatingLabelRowNumber(4);
        mainLayout.setConstraints(m_jpCentralAttack, gbcMainLayout);
        mainPanel.add(m_jpCentralAttack);

        //HATSTATS  ========================
        var jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlHatstatMain.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add(m_jlHatstatMain.getComponent(false));
        jpRatingValueAndDelta.add(m_jlHatstatCompare.getComponent(false));

        var jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        JLabel lblHatStat = new JLabel(getLangStr("ls.match.ratingtype.hatstats"));
        lblHatStat.setForeground(TITLE_FG);
        lblHatStat.setFont(getFont().deriveFont(Font.BOLD));
        lblHatStat.setHorizontalAlignment(SwingConstants.LEFT);
        jpSectorRating.add(lblHatStat);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpHatStats.add(jpSectorRating, BorderLayout.CENTER);
        m_jpHatStats.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 7;
        gbcMainLayout.insets = new Insets(5, 5, 0, 5);
        mainLayout.setConstraints(m_jpHatStats, gbcMainLayout);
        mainPanel.add(m_jpHatStats);

        // LODDAR ========================================
        jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlLoddarMain.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add(m_jlLoddarMain.getComponent(false));
        jpRatingValueAndDelta.add(m_jlLoddarCompare.getComponent(false));

        jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        JLabel lblLoddarStat = new JLabel(getLangStr("ls.match.ratingtype.loddarstats"));
        lblLoddarStat.setForeground(TITLE_FG);
        lblLoddarStat.setFont(getFont().deriveFont(Font.BOLD));
        lblLoddarStat.setHorizontalAlignment(SwingConstants.LEFT);
        jpSectorRating.add(lblLoddarStat);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpLoddarStats.add(jpSectorRating, BorderLayout.CENTER);
        m_jpLoddarStats.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 1;
        gbcMainLayout.gridy = 7;
        mainLayout.setConstraints(m_jpLoddarStats, gbcMainLayout);
        mainPanel.add(m_jpLoddarStats);

        // Tactic ========================================
        jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlTacticRating.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add(m_jlTacticRating.getComponent(false));

        jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        JLabel lblTactic = new JLabel(getLangStr("ls.team.tactic"));
        lblTactic.setForeground(TITLE_FG);
        lblTactic.setFont(getFont().deriveFont(Font.BOLD));
        lblTactic.setHorizontalAlignment(SwingConstants.LEFT);
        jpSectorRating.add(lblTactic);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpTacticStats.add(jpSectorRating, BorderLayout.CENTER);
        m_jpTacticStats.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 0;
        gbcMainLayout.gridy = 8;
        mainLayout.setConstraints(m_jpTacticStats, gbcMainLayout);
        mainPanel.add(m_jpTacticStats);

        // Formation experience ========================================
        jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
        m_jlFormationExperience.setFontStyle(Font.BOLD);
        jpRatingValueAndDelta.add(m_jlFormationExperience.getComponent(false));

        jpSectorRating = new JPanel(new GridLayout(2, 1));
        jpSectorRating.setBackground(LABEL_BG);
        JLabel lbFormation = new JLabel(getLangStr("ls.team.formation"));
        lbFormation.setForeground(TITLE_FG);
        lbFormation.setFont(getFont().deriveFont(Font.BOLD));
        lbFormation.setHorizontalAlignment(SwingConstants.LEFT);

        jpSectorRating.add(lbFormation);
        jpSectorRating.add(jpRatingValueAndDelta);

        m_jpFormationStats.add(jpSectorRating, BorderLayout.CENTER);
        m_jpFormationStats.setPreferredSize(SIZE);

        gbcMainLayout.gridx = 1;
        gbcMainLayout.gridy = 8;
        mainLayout.setConstraints(m_jpFormationStats, gbcMainLayout);
        mainPanel.add(m_jpFormationStats);

        //Panel for rating sharing button=====================================================
        GridBagLayout ratingPanelLayout = new GridBagLayout();
        GridBagConstraints gbcRatingPanelLayout = new GridBagConstraints();
        gbcRatingPanelLayout.anchor = GridBagConstraints.CENTER;
        gbcRatingPanelLayout.insets = new Insets(0, 0, 0, 0);
        JPanel jpSharing = new JPanel(ratingPanelLayout);
        jpSharing.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
        //jpSharing.setBorder(BORDER_RATING_DEFAULT);

        m_jbFeedbackButton.setIcon(ImageUtilities.getSvgIcon(HOIconName.UPLOAD, Map.of("strokeColor", TITLE_FG), 24, 24));
        m_jbFeedbackButton.addActionListener(e -> new FeedbackPanel());
        if (!IS_FEEDBACK_PLUGIN_ENABLED) {
            m_jbFeedbackButton.setVisible(false);
        }

        m_jbFeedbackButton.setPreferredSize(new Dimension(24, 24));
        m_jbFeedbackButton.setMinimumSize(new Dimension(24, 24));
        m_jbFeedbackButton.setMaximumSize(new Dimension(24, 24));
        m_jbFeedbackButton.setBorderPainted(false);
        m_jbFeedbackButton.setContentAreaFilled(false);
        m_jbFeedbackButton.setBorderPainted(false);
        gbcRatingPanelLayout.gridx = 1;
        gbcRatingPanelLayout.gridheight = 1;
        gbcRatingPanelLayout.insets = new Insets(5, 8, 0, 8);
        ratingPanelLayout.setConstraints(m_jbFeedbackButton, gbcRatingPanelLayout);
        jpSharing.add(m_jbFeedbackButton);

        m_jbCopyRatingButton.setIcon(ImageUtilities.getCopyIcon(22, TITLE_FG));
        m_jbCopyRatingButton.addActionListener(new CopyListener(this));
        m_jbCopyRatingButton.setBorderPainted(false);
        m_jbCopyRatingButton.setContentAreaFilled(false);
        m_jbCopyRatingButton.setBorderPainted(false);
        m_jbCopyRatingButton.setPreferredSize(new Dimension(24, 24));
        m_jbCopyRatingButton.setMinimumSize(new Dimension(24, 24));
        m_jbCopyRatingButton.setMaximumSize(new Dimension(24, 24));
        gbcRatingPanelLayout.gridy = 1;
        gbcRatingPanelLayout.insets = new Insets(5, 8, 5, 8);
        ratingPanelLayout.setConstraints(m_jbCopyRatingButton, gbcRatingPanelLayout);
        jpSharing.add(m_jbCopyRatingButton);

        gbcMainLayout.gridx = 2;
        //gbcMainLayout.gridy = 7;
        mainLayout.setConstraints(jpSharing, gbcMainLayout);
        mainPanel.add(jpSharing);

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
    }

    private int getLineupRatingLabelRowNumber(int i) {
        if (UserParameter.instance().lineupOrientationSetting == GOALKEEPER_AT_TOP) return 2 * i;
        return 4 - 2 * i;
    }

    private int getLineupRatingValueRowNumber(int i) {
        return getLineupRatingLabelRowNumber(i)+1;
    }

    private int getLineupRatingColumnNumber(int i) {
        if (UserParameter.instance().lineupOrientationSetting == GOALKEEPER_AT_TOP) return i;
        return 2 - i;
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
        m_jbCopyRatingButton.setToolTipText(getLangStr("Lineup.CopyRatings.ToolTip"));
        m_jbFeedbackButton.setToolTipText(getLangStr("Lineup.Feedback.ToolTip"));
    }

    public String getMidfieldRating() {
        return m_clFormat.format(m_dMidfieldRating);
    }

    public String getLeftDefenseRating() {
        return m_clFormat.format(m_dLeftDefenseRating);
    }

    public String getCentralDefenseRating() {
        return m_clFormat.format(m_dCentralDefenseRating);
    }

    public String getRightDefenseRating() {
        return m_clFormat.format(m_dRightDefenseRating);
    }

    public String getLeftAttackRating() {
        return m_clFormat.format(m_dLeftAttackRating);
    }

    public String getCentralAttackRating() {
        return m_clFormat.format(m_dCentralAttackRating);
    }

    public String getRightAttackRating() {
        return m_clFormat.format(m_dRightAttackRating);
    }

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

    private String getLangStr(String key) {
        return HOVerwaltung.instance().getLanguageString(key);
    }

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

    public void calculateRatings() {
        if (HOVerwaltung.instance().getModel().getTeam() != null) {
            final HOModel homodel = HOVerwaltung.instance().getModel();
            final Lineup currentLineup = homodel.getLineup();

            m_jpMinuteToggler.load();

            clear();
            if (currentLineup.getRatings().getLeftDefense().size() != 0 &&
                    currentLineup.getRatings().getLeftDefense().get(m_jpMinuteToggler.getCurrentKey())!=null) {

                setRightDefense(currentLineup.getRatings().getRightDefense().get(m_jpMinuteToggler.getCurrentKey()));
                setCentralDefense(currentLineup.getRatings().getCentralDefense().get(m_jpMinuteToggler.getCurrentKey()));
                setLeftDefense(currentLineup.getRatings().getLeftDefense().get(m_jpMinuteToggler.getCurrentKey()));
                setMidfield(currentLineup.getRatings().getMidfield().get(m_jpMinuteToggler.getCurrentKey()));
                setLeftAttack(currentLineup.getRatings().getLeftAttack().get(m_jpMinuteToggler.getCurrentKey()));
                setCentralAttack(currentLineup.getRatings().getCentralAttack().get(m_jpMinuteToggler.getCurrentKey()));
                setRightAttack(currentLineup.getRatings().getRightAttack().get(m_jpMinuteToggler.getCurrentKey()));
                setLoddar(Helper.round(currentLineup.getRatings().getLoddarStat().get(m_jpMinuteToggler.getCurrentKey()), 2));
                setiHatStats(currentLineup.getRatings().getHatStats().get(m_jpMinuteToggler.getCurrentKey()));
                int iTacticType = currentLineup.getTacticType();
                setTactic(iTacticType, currentLineup.getTacticLevel(iTacticType));
                setFormationExperience(currentLineup.getCurrentTeamFormationString(), currentLineup.getExperienceForCurrentTeamFormation());

                // Recalculate Borders
                calcRatingRatio();
            }
        }
    }

    public void setPreviousRatings(Ratings previousRatings) {
        final double t = m_jpMinuteToggler.getCurrentKey();

        m_dRightDefenseRating = previousRatings.getRightDefense().get(t);
        m_dCentralDefenseRating = previousRatings.getCentralDefense().get(t);
        m_dLeftDefenseRating = previousRatings.getLeftDefense().get(t);

        m_dMidfieldRating = previousRatings.getMidfield().get(m_jpMinuteToggler.getCurrentKey());

        m_dRightAttackRating = previousRatings.getRightAttack().get(t);
        m_dCentralAttackRating = previousRatings.getCentralAttack().get(t);
        m_dLeftAttackRating = previousRatings.getLeftAttack().get(t);
    }

    @Override
    public void refresh() {
        calculateRatings();
    }

    @Override
    public void reInit() {
        refresh();
    }

}