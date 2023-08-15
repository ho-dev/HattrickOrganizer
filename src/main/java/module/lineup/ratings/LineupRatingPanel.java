package module.lineup.ratings;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.RasenPanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.*;
import core.model.match.IMatchDetails;
import core.rating.RatingPredictionModel;
import core.util.Helper;
import module.lineup.CopyListener;
import module.lineup.Lineup;
import module.lineup.LineupPanel;

import java.awt.*;
import java.text.NumberFormat;
import javax.swing.*;

import static core.model.UserParameter.GOALKEEPER_AT_TOP;
import static module.lineup.LineupPanel.TITLE_FG;


public final class LineupRatingPanel extends RasenPanel implements core.gui.Refreshable {

    private final LineupPanel lineupPanel;

    class RatingPanel extends JPanel {

        private final ColorLabelEntry compare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
        private final ColorLabelEntry number = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
        private final JLabel text = new JLabel("", SwingConstants.CENTER);
        private Color color;
        private Double rating=0.;

        public RatingPanel(String tooltipText) {
            super(new BorderLayout());
            var jpRatingValueAndDelta = new JPanel(new GridLayout(1, 2));
            number.setFontStyle(Font.BOLD);
            jpRatingValueAndDelta.add(number.getComponent(false));
            jpRatingValueAndDelta.add(compare.getComponent(false));

            var jpSectorRating = new JPanel(new GridLayout(2, 1));
            jpSectorRating.setBackground(LABEL_BG);
            jpSectorRating.add(text);
            jpSectorRating.add(jpRatingValueAndDelta);

            add(jpSectorRating, BorderLayout.CENTER);
            setPreferredSize(SIZE);

            compare.setSpecialNumber(0f, false);

            text.setToolTipText(tooltipText);
            number.setToolTipText(tooltipText);
            compare.setToolTipText(tooltipText);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int width = 10;
            int height = 10;
            g.setColor(color);
            g.fillOval(5, this.getHeight() - 15, width, height);
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setRatingRatio(double ratingRatio) {
            double COLOR_BORDERS_LIMIT_RATIO = 0.85;
            if (ratingRatio <= COLOR_BORDERS_LIMIT_RATIO) {
                setColor(RATING_BELOW_LIMIT);
            } else if (ratingRatio >= 1.0 / COLOR_BORDERS_LIMIT_RATIO) {
                setColor(RATING_ABOVE_LIMIT);
            } else {
                setColor(RATING_DEFAULT);
            }
        }

        public void clear() {
            text.setText("");
            number.clear();
            compare.clear();
        }

        public void setRating(Double rating) {
            if(rating!=null ) {
                number.setText(m_clFormat.format(rating));
                compare.setSpecialNumber((float) (rating - this.rating), false);
                this.rating = rating;
                text.setText(PlayerAbility.getNameForSkill(rating, false, true));
            }
        }

        public double getRating(){
            if ( rating!=null) return rating;
            return 0.;
        }
    }

    private final MinuteTogglerPanel m_jpMinuteToggler = new MinuteTogglerPanel(this);
    private final static Color LABEL_BG = ThemeManager.getColor(HOColorName.PANEL_BG);
    private final static Color LABEL_FG = ThemeManager.getColor(HOColorName.LEAGUE_FG);
    private final static Color BAD_LABEL_FG = ThemeManager.getColor(HOColorName.TABLEENTRY_DECLINE_FG);
    private final static Color RATING_DEFAULT = ThemeManager.getColor(HOColorName.PLAYER_POSITION_PANEL_BORDER);
    private final static Color RATING_BELOW_LIMIT = ThemeManager.getColor(HOColorName.RATING_BORDER_BELOW_LIMIT);
    private final static Color RATING_ABOVE_LIMIT = ThemeManager.getColor(HOColorName.RATING_BORDER_ABOVE_LIMIT);
    int iHatStats;
    double loddar;
    private final ColorLabelEntry m_jlHatstatMain = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlLoddarMain = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.RIGHT);
    private final ColorLabelEntry m_jlHatstatCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlLoddarCompare = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlTacticRating = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.CENTER);
    private final ColorLabelEntry m_jlFormationExperience = new ColorLabelEntry("", LABEL_FG, LABEL_BG, SwingConstants.LEFT);
    private RatingPanel m_jpCentralAttack;
    private RatingPanel m_jpRightAttack;
    private RatingPanel m_jpLeftAttack;
    private RatingPanel m_jpMidfield;
    private RatingPanel m_jpCentralDefense;
    private RatingPanel m_jpLeftDefense;
    private RatingPanel m_jpRightDefense;
    private final JPanel m_jpHatStats = new JPanel(new BorderLayout());
    private final JPanel m_jpLoddarStats = new JPanel(new BorderLayout());
    private final JPanel m_jpTacticStats = new JPanel(new BorderLayout());
    private final JPanel m_jpFormationStats = new JPanel(new BorderLayout());
    private final NumberFormat m_clFormat;
    private final JButton m_jbCopyRatingButton = new JButton();
    private final JButton m_jbFeedbackButton = new JButton();
    private final Dimension SIZE = new Dimension(Helper.calcCellWidth(120), Helper.calcCellWidth(40));

    public LineupRatingPanel(LineupPanel lineupPanel) {
        this.lineupPanel = lineupPanel;
        initComponents();

        if (core.model.UserParameter.instance().nbDecimals == 1) {
            m_clFormat = Helper.DEFAULTDEZIMALFORMAT;
        } else {
            m_clFormat = Helper.DEZIMALFORMAT_2STELLEN;
        }
    }


    public void clear() {
        m_jpCentralAttack.clear();
        m_jpRightAttack.clear();
        m_jpLeftAttack.clear();
        m_jpMidfield.clear();
        m_jpCentralDefense.clear();
        m_jpLeftDefense.clear();
        m_jpRightDefense.clear();
        m_jlHatstatMain.clear();
        m_jlHatstatCompare.clear();
        m_jlLoddarMain.clear();
        m_jlLoddarCompare.clear();
        m_jlTacticRating.clear();
        m_jlFormationExperience.clear();
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

        final double avgRating = (m_jpLeftDefense.getRating() +
                m_jpCentralDefense.getRating() +
                m_jpRightDefense.getRating() +
                m_jpMidfield.getRating() +
                m_jpRightAttack.getRating() +
                m_jpCentralAttack.getRating() +
                m_jpLeftAttack.getRating()) / 7d;

        m_jpLeftDefense.setRatingRatio(m_jpLeftDefense.getRating() / avgRating);
        m_jpCentralDefense.setRatingRatio(m_jpCentralDefense.getRating() / avgRating);
        m_jpRightDefense.setRatingRatio(m_jpRightDefense.getRating() / avgRating);
        m_jpMidfield.setRatingRatio(m_jpMidfield.getRating() / avgRating);
        m_jpRightAttack.setRatingRatio(m_jpRightAttack.getRating() / avgRating);
        m_jpCentralAttack.setRatingRatio(m_jpCentralAttack.getRating() / avgRating);
        m_jpLeftAttack.setRatingRatio(m_jpLeftAttack.getRating() / avgRating);
    }

    /**
     * Initialize GUI components.
     */
    private void initComponents() {

        m_jpCentralAttack = new RatingPanel(getLangStr("ls.match.ratingsector.centralattack"));
        m_jpRightAttack = new RatingPanel(getLangStr("ls.match.ratingsector.rightattack"));
        m_jpLeftAttack = new RatingPanel(getLangStr("ls.match.ratingsector.leftattack"));
        m_jpMidfield = new RatingPanel(getLangStr("ls.match.ratingsector.midfield"));
        m_jpCentralDefense = new RatingPanel(getLangStr("ls.match.ratingsector.centraldefence"));
        m_jpLeftDefense = new RatingPanel(getLangStr("ls.match.ratingsector.leftdefence"));
        m_jpRightDefense = new RatingPanel(getLangStr("ls.match.ratingsector.rightdefence"));

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
        m_jbCopyRatingButton.setToolTipText(getLangStr("Lineup.CopyRatings.ToolTip"));
        m_jbFeedbackButton.setToolTipText(getLangStr("Lineup.Feedback.ToolTip"));
    }

    public String getMidfieldRating() {
        return m_clFormat.format(m_jpMidfield.getRating());
    }

    public String getLeftDefenseRating() {
        return m_clFormat.format(m_jpLeftDefense.getRating());
    }

    public String getCentralDefenseRating() {
        return m_clFormat.format(m_jpCentralDefense.getRating());
    }

    public String getRightDefenseRating() {
        return m_clFormat.format(m_jpRightDefense.getRating());
    }

    public String getLeftAttackRating() {
        return m_clFormat.format(m_jpLeftAttack.getRating());
    }

    public String getCentralAttackRating() {
        return m_clFormat.format(m_jpCentralAttack.getRating());
    }

    public String getRightAttackRating() {
        return m_clFormat.format(m_jpRightAttack.getRating());
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

    abstract static class MinuteRating {
        public abstract double get(Lineup lineup, RatingPredictionModel.RatingSector s);
        public abstract double hatstats(Lineup lineup);
        public abstract double loddar(Lineup lineup);
    }

    public int getSelectedMatchMinute(){
        return m_jpMinuteToggler.getCurrentKey();
    }

    public void refreshRatings(){
        this.lineupPanel.update();
    }

    public void calculateRatings() {
        if (HOVerwaltung.instance().getModel().getTeam() != null) {
            final HOModel homodel = HOVerwaltung.instance().getModel();

            var team = homodel.getCurrentLineupTeam();
            final Lineup currentLineup = team.getLineup();
            m_jpMinuteToggler.load();
            clear();

            var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();

            MinuteRating minuteRating;
            final int minute = m_jpMinuteToggler.getCurrentKey();
            if (minute < 0) {
                // -90 -> average
                // -120 -> average incl. extra time
                minuteRating = new MinuteRating() {
                    @Override
                    public double get(Lineup lineup, RatingPredictionModel.RatingSector s) {
                        return ratingPredictionModel.getAverageRating(lineup, s, -minute);
                    }

                    @Override
                    public double hatstats(Lineup lineup) {
                        return ratingPredictionModel.getAverageHatStats(lineup, -minute);
                    }

                    @Override
                    public double loddar(Lineup lineup) {
                        return ratingPredictionModel.getAverageLoddarStats(lineup, -minute);
                    }
                };
            } else {
                minuteRating = new MinuteRating() {
                    @Override
                    public double get(Lineup lineup, RatingPredictionModel.RatingSector s) {
                        return ratingPredictionModel.getRating(lineup, s, minute);
                    }

                    @Override
                    public double hatstats(Lineup lineup) {
                        return ratingPredictionModel.getHatStats(lineup, minute);
                    }

                    @Override
                    public double loddar(Lineup lineup) {
                        return ratingPredictionModel.getLoddarStats(lineup, minute);
                    }
                };
            }


            m_jpRightDefense.setRating(minuteRating.get(currentLineup, RatingPredictionModel.RatingSector.Defence_Right));
            m_jpCentralDefense.setRating(minuteRating.get(currentLineup, RatingPredictionModel.RatingSector.Defence_Central));
            m_jpLeftDefense.setRating(minuteRating.get(currentLineup, RatingPredictionModel.RatingSector.Defence_Left));
            m_jpMidfield.setRating(minuteRating.get(currentLineup, RatingPredictionModel.RatingSector.Midfield));
            m_jpLeftAttack.setRating(minuteRating.get(currentLineup, RatingPredictionModel.RatingSector.Attack_Left));
            m_jpCentralAttack.setRating(minuteRating.get(currentLineup, RatingPredictionModel.RatingSector.Attack_Central));
            m_jpRightAttack.setRating(minuteRating.get(currentLineup, RatingPredictionModel.RatingSector.Attack_Right));

            setLoddar(Helper.round(minuteRating.loddar(currentLineup), 2));
            setiHatStats((int) minuteRating.hatstats(currentLineup));
            int iTacticType = currentLineup.getTacticType();
            setTactic(iTacticType, (float) ratingPredictionModel.getTacticRating(currentLineup, minute));
            setFormationExperience(currentLineup.getCurrentTeamFormationString(), currentLineup.getExperienceForCurrentTeamFormation());

            // Recalculate Borders
            calcRatingRatio();
        }
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