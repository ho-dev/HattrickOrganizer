package module.statistics;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.util.chart.HOLinesChart;
import core.util.chart.LinesChartDataModel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.HOLogger;
import core.util.Helper;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Panel Club in Module Statistics
 */
public class ClubStatisticsPanel extends LazyImagePanel {

    private ImageCheckbox jcbAssistantTrainerLevels;
    private ImageCheckbox jcbFinancialDirectorLevels;
    private ImageCheckbox jcbFormCoachLevels;
    private ImageCheckbox jcbDoctorLevels;
    private ImageCheckbox jcbSpokePersonLevels;
    private ImageCheckbox jcbSportPsychologistLevels;
    private ImageCheckbox jcbTacticalAssistantLevels;
    private ImageCheckbox jcbYouthSquadLevel;
    private ImageCheckbox jcbYouthSquadInvestment;
    private ImageCheckbox jcbFanClubSize;
    private ImageCheckbox jcbGlobalRanking;
    private ImageCheckbox jcbLeagueRanking;
    private ImageCheckbox jcbRegionRanking;
    private ImageCheckbox jcbPowerRating;
    private JButton jbApply;
    private JButton jbUncheckAll;
    private JCheckBox jcbHelpLines;
    private JTextField jtfNbHRFs;
    private HOLinesChart oChartPanel;

    private static UserParameter gup = UserParameter.instance();
    
    @Override
    protected void initialize() {
        initComponents();
        addListeners();
        setNeedsRefresh(true);
        registerRefreshable(true);
    }

    @Override
    protected void update() {
        getData();
    }

    private void addListeners() {
        ActionListener checkBoxActionListener = e -> {
            if (e.getSource() == jcbHelpLines) {
                oChartPanel.setHelpLines(jcbHelpLines.isSelected());
                gup.statisticsClubHelpLines = jcbHelpLines.isSelected();
            }
            else if (e.getSource() == jcbAssistantTrainerLevels.getCheckbox()) {
                oChartPanel.setShow("AssistantTrainerLevel", jcbAssistantTrainerLevels.isSelected());
                gup.statisticsClubAssistantTrainersLevel = jcbAssistantTrainerLevels.isSelected();
            }
            else if (e.getSource() == jcbFinancialDirectorLevels.getCheckbox()) {
                oChartPanel.setShow("FinancialDirectorLevel", jcbFinancialDirectorLevels.isSelected());
                gup.statisticsClubFinancialDirectorsLevel = jcbFinancialDirectorLevels.isSelected();
            }
            else if (e.getSource() == jcbFormCoachLevels.getCheckbox()) {
                oChartPanel.setShow("FormCoachLevel", jcbFormCoachLevels.isSelected());
                gup.statisticsClubFormCoachsLevel = jcbFormCoachLevels.isSelected();
            }
            else if (e.getSource() == jcbDoctorLevels.getCheckbox()) {
                oChartPanel.setShow("DoctorLevel", jcbDoctorLevels.isSelected());
                gup.statisticsClubDoctorsLevel = jcbDoctorLevels.isSelected();
            }
            else if (e.getSource() == jcbSpokePersonLevels.getCheckbox()) {
                oChartPanel.setShow("SpokespersonLevel", jcbSpokePersonLevels.isSelected());
                gup.statisticsClubSpokePersonsLevel = jcbSpokePersonLevels.isSelected();
            }
            else if (e.getSource() == jcbSportPsychologistLevels.getCheckbox()) {
                oChartPanel.setShow("SportPsychologistLevel", jcbSportPsychologistLevels.isSelected());
                gup.statisticsClubSportPsychologistLevels = jcbSportPsychologistLevels.isSelected();
            }
            else if (e.getSource() == jcbTacticalAssistantLevels.getCheckbox()) {
                oChartPanel.setShow("TacticalAssistantLevel", jcbTacticalAssistantLevels.isSelected());
                gup.statisticsClubTacticalAssistantLevels = jcbTacticalAssistantLevels.isSelected();
            }
            else if (e.getSource() == jcbYouthSquadLevel.getCheckbox()) {
                oChartPanel.setShow("YouthSquadLevel", jcbYouthSquadLevel.isSelected());
                gup.statisticsClubYouthSquadLevel = jcbYouthSquadLevel.isSelected();
            }
            else if (e.getSource() == jcbYouthSquadInvestment.getCheckbox()) {
                oChartPanel.setShow("YouthSquadInvestment", jcbYouthSquadInvestment.isSelected());
                gup.statisticsClubYouthSquadInvestment = jcbYouthSquadInvestment.isSelected();
            }
            else if (e.getSource() == jcbFanClubSize.getCheckbox()) {
                oChartPanel.setShow("FanClubSize", jcbFanClubSize.isSelected());
                gup.statisticsClubFanClubSize = jcbFanClubSize.isSelected();
            }
            else if (e.getSource() == jcbGlobalRanking.getCheckbox()) {
                oChartPanel.setShow("GlobalRanking", jcbGlobalRanking.isSelected());
                gup.statisticsClubGlobalRanking = jcbGlobalRanking.isSelected();
            }
            else if (e.getSource() == jcbLeagueRanking.getCheckbox()) {
                oChartPanel.setShow("LeagueRanking", jcbLeagueRanking.isSelected());
                gup.statisticsClubLeagueRanking = jcbLeagueRanking.isSelected();
            }
            else if (e.getSource() == jcbRegionRanking.getCheckbox()) {
                oChartPanel.setShow("RegionRanking", jcbRegionRanking.isSelected());
                gup.statisticsClubRegionRanking = jcbRegionRanking.isSelected();
            }
            else if (e.getSource() == jcbPowerRating.getCheckbox()) {
                oChartPanel.setShow("PowerRating", jcbPowerRating.isSelected());
                gup.statisticsClubPowerRating = jcbPowerRating.isSelected();
            }
        };

        jcbHelpLines.addActionListener(checkBoxActionListener);
        jcbAssistantTrainerLevels.addActionListener(checkBoxActionListener);
        jcbFinancialDirectorLevels.addActionListener(checkBoxActionListener);
        jcbFormCoachLevels.addActionListener(checkBoxActionListener);
        jcbDoctorLevels.addActionListener(checkBoxActionListener);
        jcbSpokePersonLevels.addActionListener(checkBoxActionListener);
        jcbSportPsychologistLevels.addActionListener(checkBoxActionListener);
        jcbTacticalAssistantLevels.addActionListener(checkBoxActionListener);
        jcbYouthSquadLevel.addActionListener(checkBoxActionListener);
        jcbYouthSquadInvestment.addActionListener(checkBoxActionListener);
        jcbFanClubSize.addActionListener(checkBoxActionListener);
        jcbGlobalRanking.addActionListener(checkBoxActionListener);
        jcbLeagueRanking.addActionListener(checkBoxActionListener);
        jcbRegionRanking.addActionListener(checkBoxActionListener);
        jcbPowerRating.addActionListener(checkBoxActionListener);

        jbApply.addActionListener(e -> getData());

        jbUncheckAll.addActionListener(e -> uncheckAll());

        jtfNbHRFs.addFocusListener(new FocusAdapter() {
            @Override
            public final void focusLost(java.awt.event.FocusEvent focusEvent) {
                Helper.parseInt(HOMainFrame.instance(), jtfNbHRFs, false);
            }
        });
    }

    private void initComponents() {
        JLabel label;

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(2, 0, 2, 0);

        setLayout(layout);

        JPanel panel2 = new ImagePanel();
        GridBagLayout layout2 = new GridBagLayout();
        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.fill = GridBagConstraints.HORIZONTAL;
        constraints2.weightx = 0.0;
        constraints2.weighty = 0.0;
        constraints2.insets = new Insets(2, 2, 2, 2);

        panel2.setLayout(layout2);

        label = new JLabel(getLangStr("Wochen"));
        constraints2.fill = GridBagConstraints.HORIZONTAL;
        constraints2.anchor = GridBagConstraints.WEST;
        constraints2.gridx = 0;
        constraints2.gridy = 1;
        constraints2.gridwidth = 1;
        layout2.setConstraints(label, constraints2);
        panel2.add(label);
        jtfNbHRFs = new JTextField(String.valueOf(gup.statisticsClubNbWeeks));
        jtfNbHRFs.setHorizontalAlignment(SwingConstants.RIGHT);
        constraints2.gridx = 1;
        constraints2.gridy = 1;
        layout2.setConstraints(jtfNbHRFs, constraints2);
        panel2.add(jtfNbHRFs);

        constraints2.gridx = 0;
        constraints2.gridy = 4;
        constraints2.gridwidth = 2;
        jbApply = new JButton(getLangStr("ls.button.apply"));
        jbApply.setToolTipText(getLangStr("tt_Statistik_HRFAnzahluebernehmen"));
        layout2.setConstraints(jbApply, constraints2);
        panel2.add(jbApply);

        constraints2.gridwidth = 1;
        constraints2.gridy = 5;
        jcbHelpLines = new JCheckBox(getLangStr("Hilflinien"), gup.statisticsClubHelpLines);
        layout2.setConstraints(jcbHelpLines, constraints2);
        panel2.add(jcbHelpLines);

        constraints2.gridy = 6;
        jbUncheckAll = new JButton(getLangStr("ls.button.unselect_all"));
        jbUncheckAll.setToolTipText(getLangStr("ls.button.tt.unselect_all"));
        layout2.setConstraints(jbUncheckAll, constraints2);
        panel2.add(jbUncheckAll);

        constraints2.gridy = 7;
        jcbAssistantTrainerLevels = new ImageCheckbox(getLangStr("ls.module.statistics.club.assistant_trainers_level"),
                getColor(Colors.COLOR_CLUB_ASSISTANT_TRAINERS_LEVEL), gup.statisticsClubAssistantTrainersLevel);
        panel2.add(jcbAssistantTrainerLevels, constraints2);

        constraints2.gridy = 8;
        jcbFormCoachLevels = new ImageCheckbox(getLangStr("ls.module.statistics.club.form_coachs_level"),
                getColor(Colors.COLOR_CLUB_FORM_COACHS_LEVEL), gup.statisticsClubFormCoachsLevel);
        panel2.add(jcbFormCoachLevels, constraints2);

        constraints2.gridy = 9;
        jcbDoctorLevels = new ImageCheckbox(getLangStr("ls.module.statistics.club.doctors_level"),
                getColor(Colors.COLOR_CLUB_DOCTORS_LEVEL), gup.statisticsClubDoctorsLevel);
        panel2.add(jcbDoctorLevels, constraints2);

        constraints2.gridy = 10;
        jcbFinancialDirectorLevels = new ImageCheckbox(getLangStr("ls.module.statistics.club.financial_directors_level"),
                getColor(Colors.COLOR_CLUB_FINANCIAL_DIRECTORS_LEVEL), gup.statisticsClubFinancialDirectorsLevel);
        panel2.add(jcbFinancialDirectorLevels, constraints2);

        constraints2.gridy = 11;
        jcbSpokePersonLevels = new ImageCheckbox(getLangStr("ls.module.statistics.club.spokesperson"),
                getColor(Colors.COLOR_CLUB_SPOKE_PERSONS_LEVEL), gup.statisticsClubSpokePersonsLevel);
        panel2.add(jcbSpokePersonLevels, constraints2);

        constraints2.gridy = 12;
        jcbSportPsychologistLevels = new ImageCheckbox(getLangStr("ls.module.statistics.club.sport_psychologist_levels"),
                getColor(Colors.COLOR_CLUB_SPORT_PSYCHOLOGIST_LEVELS), gup.statisticsClubSportPsychologistLevels);
        panel2.add(jcbSportPsychologistLevels, constraints2);

        constraints2.gridy = 13;
        jcbTacticalAssistantLevels = new ImageCheckbox(getLangStr("ls.module.statistics.club.tactical_assistant_levels"),
                getColor(Colors.COLOR_CLUB_TACTICAL_ASSISTANT_LEVELS), gup.statisticsClubTacticalAssistantLevels);
        panel2.add(jcbTacticalAssistantLevels, constraints2);

        constraints2.gridy = 14;
        jcbYouthSquadLevel = new ImageCheckbox(getLangStr("ls.module.statistics.club.youth_squad_level"),
                getColor(Colors.COLOR_CLUB_YOUTH_SQUAD_LEVEL), gup.statisticsClubYouthSquadLevel);
        panel2.add(jcbYouthSquadLevel, constraints2);

        constraints2.gridy = 15;
        jcbYouthSquadInvestment = new ImageCheckbox(getLangStr("ls.module.statistics.club.youth_squad_investment"),
                getColor(Colors.COLOR_CLUB_YOUTH_SQUAD_INVESTMENT), gup.statisticsClubYouthSquadInvestment);
        panel2.add(jcbYouthSquadInvestment, constraints2);

        constraints2.gridy = 16;
        jcbFanClubSize = new ImageCheckbox(getLangStr("ls.module.statistics.club.fan_club_size"),
                getColor(Colors.COLOR_CLUB_FAN_CLUB_SIZE), gup.statisticsClubFanClubSize);
        panel2.add(jcbFanClubSize, constraints2);

        constraints2.gridy = 17;
        jcbGlobalRanking = new ImageCheckbox(getLangStr("ls.module.statistics.club.global_ranking"),
                getColor(Colors.COLOR_CLUB_GLOBAL_RANKING), gup.statisticsClubGlobalRanking);
        panel2.add(jcbGlobalRanking, constraints2);

        constraints2.gridy = 18;
        jcbLeagueRanking = new ImageCheckbox(getLangStr("ls.module.statistics.club.league_ranking"),
                getColor(Colors.COLOR_CLUB_LEAGUE_RANKING), gup.statisticsClubLeagueRanking);
        panel2.add(jcbLeagueRanking, constraints2);

        constraints2.gridy = 19;
        jcbRegionRanking = new ImageCheckbox(getLangStr("ls.module.statistics.club.region_ranking"),
                getColor(Colors.COLOR_CLUB_REGION_RANKING), gup.statisticsClubRegionRanking);
        panel2.add(jcbRegionRanking, constraints2);

        constraints2.gridy = 20;
        jcbPowerRating = new ImageCheckbox(getLangStr("ls.module.statistics.club.power_rating"),
                getColor(Colors.COLOR_CLUB_POWER_RATING), gup.statisticsClubPowerRating);
        panel2.add(jcbPowerRating, constraints2);


        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.01;
        constraints.weighty = 0.001;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel2, constraints);
        add(panel2);

        // Graph
        JPanel panel = new ImagePanel();
        panel.setLayout(new BorderLayout());

        oChartPanel = new HOLinesChart(true, null, null, null, "#,##0", 0d, 20d);
        panel.add(oChartPanel.getPanel());

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weighty = 1.0;
        constraints.weightx = 1.0;
        constraints.anchor = GridBagConstraints.NORTH;
        panel.setBorder(BorderFactory.createLineBorder(ThemeManager
                .getColor(HOColorName.PANEL_BORDER)));
        layout.setConstraints(panel, constraints);
        add(panel);
    }


    private void uncheckAll() {
        jcbAssistantTrainerLevels.setSelected(false); ;
        jcbFinancialDirectorLevels.setSelected(false);
        jcbFormCoachLevels.setSelected(false);
        jcbDoctorLevels.setSelected(false);
        jcbSpokePersonLevels.setSelected(false);
        jcbSportPsychologistLevels.setSelected(false);
        jcbTacticalAssistantLevels.setSelected(false);
        jcbYouthSquadLevel.setSelected(false);
        jcbYouthSquadInvestment.setSelected(false);
        jcbFanClubSize.setSelected(false);
        jcbGlobalRanking.setSelected(false);
        jcbLeagueRanking.setSelected(false);
        jcbRegionRanking.setSelected(false);
        jcbPowerRating.setSelected(false);

        oChartPanel.clear(); //TODO: make a function like setshow but that remove all series

        gup.statisticsClubAssistantTrainersLevel = false;
        gup.statisticsClubFinancialDirectorsLevel = false;
        gup.statisticsClubFormCoachsLevel = false;
        gup.statisticsClubDoctorsLevel = false;
        gup.statisticsClubSpokePersonsLevel = false;
        gup.statisticsClubSportPsychologistLevels = false;
        gup.statisticsClubTacticalAssistantLevels = false;
        gup.statisticsClubYouthSquadLevel = false;
        gup.statisticsClubYouthSquadInvestment = false;
        gup.statisticsClubFanClubSize = false;
        gup.statisticsClubGlobalRanking = false;
        gup.statisticsClubLeagueRanking = false;
        gup.statisticsClubRegionRanking = false;
        gup.statisticsClubPowerRating = false;

    }

    private void getData() {
        try {
            int nbHRFs = Integer.parseInt(jtfNbHRFs.getText());
            if (nbHRFs <= 0) {
                nbHRFs = 1;
            }

            gup.statistikFinanzenAnzahlHRF = nbHRFs;

            NumberFormat format = NumberFormat.getInstance();

            double[][] data = DBManager.instance().getDataForClubStatisticsPanel(nbHRFs); //TODO: finalize this one

            LinesChartDataModel[] models;
            models = new LinesChartDataModel[15];

            if (data.length > 0) {
                //TODO create all data models using the same name as the one using above for the bindings of the cb
                models[0] = new LinesChartDataModel(data[0], "AssistantTrainerLevel", jcbAssistantTrainerLevels.isSelected(),
                        getColor(Colors.COLOR_CLUB_ASSISTANT_TRAINERS_LEVEL), format);
                models[1] = new LinesChartDataModel(data[1], "FinancialDirectorLevel", jcbFinancialDirectorLevels.isSelected(),
                        getColor(Colors.COLOR_CLUB_FINANCIAL_DIRECTORS_LEVEL), format);
                models[2] = new LinesChartDataModel(data[2], "FormCoachLevel", jcbFormCoachLevels.isSelected(),
                        getColor(Colors.COLOR_CLUB_FORM_COACHS_LEVEL), format);
                models[3] = new LinesChartDataModel(data[3], "DoctorLevel", jcbDoctorLevels.isSelected(),
                        getColor(Colors.COLOR_CLUB_DOCTORS_LEVEL), format);
                models[4] = new LinesChartDataModel(data[4], "SpokespersonLevel", jcbSpokePersonLevels.isSelected(),
                        getColor(Colors.COLOR_CLUB_SPOKE_PERSONS_LEVEL), format);
                models[5] = new LinesChartDataModel(data[5], "SportPsychologistLevel", jcbSportPsychologistLevels.isSelected(),
                        getColor(Colors.COLOR_CLUB_SPORT_PSYCHOLOGIST_LEVELS), format);
                models[6] = new LinesChartDataModel(data[6], "TacticalAssistantLevel", jcbTacticalAssistantLevels.isSelected(),
                        getColor(Colors.COLOR_CLUB_TACTICAL_ASSISTANT_LEVELS), format);
                models[7] = new LinesChartDataModel(data[7], "YouthSquadLevel", jcbYouthSquadLevel.isSelected(),
                        getColor(Colors.COLOR_CLUB_YOUTH_SQUAD_LEVEL), format);
                models[8] = new LinesChartDataModel(data[8], "YouthSquadInvestment", jcbYouthSquadInvestment.isSelected(),
                        getColor(Colors.COLOR_CLUB_YOUTH_SQUAD_INVESTMENT), format);
                models[9] = new LinesChartDataModel(data[9], "FanClubSize", jcbFanClubSize.isSelected(),
                        getColor(Colors.COLOR_CLUB_FAN_CLUB_SIZE), format, 0d, true);
                models[10] = new LinesChartDataModel(data[10], "GlobalRanking", jcbGlobalRanking.isSelected(),
                        getColor(Colors.COLOR_CLUB_GLOBAL_RANKING), format);
                models[11] = new LinesChartDataModel(data[11], "LeagueRanking", jcbLeagueRanking.isSelected(),
                        getColor(Colors.COLOR_CLUB_LEAGUE_RANKING), format);
                models[12] = new LinesChartDataModel(data[12], "RegionRanking", jcbRegionRanking.isSelected(),
                        getColor(Colors.COLOR_CLUB_REGION_RANKING), format);
                models[13] = new LinesChartDataModel(data[13], "PowerRating", jcbPowerRating.isSelected(),
                        getColor(Colors.COLOR_CLUB_POWER_RATING), format);
            }

            oChartPanel.setAllValues(models, data[14], format, getLangStr("Wochen"), "",
                    false, jcbHelpLines.isSelected());

        } catch (Exception e) {
            HOLogger.instance().log(getClass(), e);
        }
    }

    private String getLangStr(String key) {
        return HOVerwaltung.instance().getLanguageString(key);
    }

    private Color getColor(int i) {return ThemeManager.getColor(HOColorName.PALETTE15[i]);}
}
