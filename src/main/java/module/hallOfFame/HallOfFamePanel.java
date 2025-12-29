package module.hallOfFame;

import core.constants.player.PlayerSkill;
import core.gui.comp.ImageCheckbox;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import core.gui.theme.HOColorName;
import core.model.HOConfigurationBooleanParameter;
import core.model.HOConfigurationParameter;
import core.model.TranslationFacility;
import core.model.player.Player;
import core.module.config.ModuleConfig;
import core.util.Helper;
import core.util.chart.HOLinesChart;
import core.util.chart.LinesChartDataModel;
import module.statistics.Colors;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class HallOfFamePanel extends JPanel {
    private final JPanel historyPanel = new JPanel(new BorderLayout());
    private final PlayersTable hallOfFameTable;

    private final HOConfigurationBooleanParameter chartDisplayForm = new HOConfigurationBooleanParameter("hof.chart.display.form", true);
    private final HOConfigurationBooleanParameter  chartDisplayLeadership= new HOConfigurationBooleanParameter("hof.chart.display.leadership", true);
    private final HOConfigurationBooleanParameter  chartDisplayTSI = new HOConfigurationBooleanParameter("hof.chart.display.tsi", true);
    private final HOConfigurationBooleanParameter chartDisplayWage = new HOConfigurationBooleanParameter("hof.chart.display.wage", true);
    private final HOConfigurationBooleanParameter chartDisplayDefence= new HOConfigurationBooleanParameter("hof.chart.display.defence", true);
    private final HOConfigurationBooleanParameter chartDisplayGoalkepper= new HOConfigurationBooleanParameter("hof.chart.display.goalkeeper", true);
    private final HOConfigurationBooleanParameter chartDisplayPlaymaking= new HOConfigurationBooleanParameter("hof.chart.display.playmaking", true);
    private final HOConfigurationBooleanParameter chartDisplayPassing= new HOConfigurationBooleanParameter("hof.chart.display.passing", true);
    private final HOConfigurationBooleanParameter chartDisplayWinger= new HOConfigurationBooleanParameter("hof.chart.display.winger", true);
    private final HOConfigurationBooleanParameter chartDisplaySetPieces= new HOConfigurationBooleanParameter("hof.chart.display.setpieces", true);
    private final HOConfigurationBooleanParameter chartDisplayScoring= new HOConfigurationBooleanParameter("hof.chart.display.scoring", true);
    private final HOConfigurationBooleanParameter chartDisplayCoachLevel= new HOConfigurationBooleanParameter("hof.chart.display.coachlevel", true);

    public HallOfFamePanel(){
        setLayout(new BorderLayout());
        var tableModel = UserColumnController.instance().getHallOfFameTableModel();
        this.hallOfFameTable = new PlayersTable(tableModel);
        this.hallOfFameTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.hallOfFameTable.addListSelectionListener(e -> refreshHistory());
        var splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
        splitPane.setLeftComponent(hallOfFameTable.getContainerComponent());
        historyPanel.add(new JLabel("History"), BorderLayout.NORTH);
        historyPanel.add(initChartSelectionPanel(), BorderLayout.WEST);
        splitPane.setRightComponent(historyPanel);

        add(splitPane, BorderLayout.CENTER);
        tableModel.initData();

        var dividerLocation = ModuleConfig.instance().getInteger("HallOfFamePanel.VerticalSplitPosition");
        splitPane.setDividerLocation(dividerLocation != null ? dividerLocation : 400);
        refreshHistory();
    }

    private JPanel initChartSelectionPanel(){

        final JPanel chartSelectionPanel = new ImagePanel();
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(2, 2, 2, 2);

        chartSelectionPanel.setLayout(layout);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(20,0,0,0);  //top padding

        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.leadership", Colors.COLOR_PLAYER_LEADERSHIP, chartDisplayLeadership));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.defence", Colors.COLOR_PLAYER_DE, chartDisplayDefence));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.keeper", Colors.COLOR_PLAYER_GK, chartDisplayGoalkepper));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.playmaking", Colors.COLOR_PLAYER_PM, chartDisplayPlaymaking));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.passing", Colors.COLOR_PLAYER_PS,chartDisplayPassing));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.winger", Colors.COLOR_PLAYER_WI,chartDisplayWinger));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.setpieces", Colors.COLOR_PLAYER_SP,chartDisplaySetPieces));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.scoring", Colors.COLOR_PLAYER_SC, chartDisplayScoring));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.coachlevel", Colors.COLOR_CLUB_FORM_COACHS_LEVEL, chartDisplayCoachLevel));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.form", Colors.COLOR_PLAYER_FORM, chartDisplayForm));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.tsi", Colors.COLOR_PLAYER_TSI, chartDisplayTSI));
        chartSelectionPanel.add(createSelectionCheckForm(layout, constraints, "ls.player.wage", Colors.COLOR_PLAYER_WAGE, chartDisplayWage));
        return chartSelectionPanel;
    }

    private Component createSelectionCheckForm(GridBagLayout layout, GridBagConstraints constraints, String label, HOColorName color, HOConfigurationBooleanParameter isSelected) {
        var form = new ImageCheckbox(TranslationFacility.tr(label), Colors.getColor(color), Boolean.TRUE.equals(isSelected.getValue()));
//        form.addActionListener(i->handleSelection(isSelected,(JCheckBox)i.getSource()));
        form.setOpaque(false);
        layout.setConstraints(form, constraints);
        constraints.gridy++;
        return form;
    }

    private void handleSelection(HOConfigurationBooleanParameter isSelected, JCheckBox source) {
        isSelected.setValue(source.isSelected());
        refreshHistory();
    }

    private void refreshHistory() {
        // Remove current chart
        var currentChart = ((BorderLayout)historyPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if ( currentChart != null){ this.historyPanel.remove(currentChart);}
        // Create and add new chart
        var historyChart  = new HOLinesChart(true, "skill", "wage",null, null, 0., 21. );
        this.historyPanel.add(historyChart.getPanel(), BorderLayout.CENTER);
        var players = this.hallOfFameTable.getSelectedPlayers();
        for (var player : players) {
            if (player instanceof HallOfFamePlayer hallOfFamePlayer) {
                var history = hallOfFamePlayer.getHistory();
                var prefix = player.getShortName() + " ";
                var exTrainer = history.stream().filter(i->i.getCoachSkill()>0).toList();
                if (!exTrainer.isEmpty() && Boolean.TRUE.equals(chartDisplayCoachLevel.getValue())) {
                    var exTrainerChartDataModels = new ArrayList<LinesChartDataModel>();
                    exTrainerChartDataModels.add(new LinesChartDataModel(
                            exTrainer.stream().mapToDouble(Player::getCoachSkill).toArray(),
                            prefix + TranslationFacility.tr("ls.team.coachingskill"),
                            true,
                            Colors.getColor(Colors.COLOR_CLUB_FORM_COACHS_LEVEL)));
                    historyChart.setAllValues(exTrainerChartDataModels.toArray(new LinesChartDataModel[0]),
                            exTrainer.stream().mapToDouble(i -> Date.from(i.getHrfDate().instant).getTime()).toArray(),
                            Helper.DEFAULTDEZIMALFORMAT,
                            TranslationFacility.tr("Datum"),
                            "", false, true);
                }
                var chartDataModels = new ArrayList<LinesChartDataModel>();
                if (Boolean.TRUE.equals(chartDisplayLeadership.getValue())) {
                    chartDataModels.add(new LinesChartDataModel(
                            history.stream().mapToDouble(Player::getLeadership).toArray(),
                            prefix + TranslationFacility.tr("ls.player.leadership"),
                            true,
                            Colors.getColor(Colors.COLOR_PLAYER_LEADERSHIP)));
                }
                if (Boolean.TRUE.equals(chartDisplayPlaymaking.getValue())) {
                    chartDataModels.add(new LinesChartDataModel(
                            history.stream().mapToDouble(i -> i.getSkillValue(PlayerSkill.PLAYMAKING)).toArray(),
                            prefix + TranslationFacility.tr("ls.player.skill.playmaking"),
                            true,
                            Colors.getColor(Colors.COLOR_PLAYER_PM)));
                }
                if (Boolean.TRUE.equals(chartDisplayDefence.getValue())) {
                    chartDataModels.add(new LinesChartDataModel(
                            history.stream().mapToDouble(i -> i.getSkillValue(PlayerSkill.DEFENDING)).toArray(),
                            prefix + TranslationFacility.tr("ls.player.skill.defending"),
                            true,
                            Colors.getColor(Colors.COLOR_PLAYER_DE)));
                }
                if (Boolean.TRUE.equals(chartDisplayForm.getValue())) {
                    chartDataModels.add(new LinesChartDataModel(
                            history.stream().mapToDouble(i -> i.getSkillValue(PlayerSkill.FORM)).toArray(),
                            prefix + TranslationFacility.tr("ls.player.skill.form"),
                            true,
                            Colors.getColor(Colors.COLOR_PLAYER_FORM)));
                }
                if (Boolean.TRUE.equals(chartDisplayGoalkepper.getValue())) {
                    chartDataModels.add(new LinesChartDataModel(
                            history.stream().mapToDouble(i -> i.getSkillValue(PlayerSkill.KEEPER)).toArray(),
                            prefix + TranslationFacility.tr("ls.player.skill.keeper"),
                            true,
                            Colors.getColor(Colors.COLOR_PLAYER_GK)));
                }
                if (Boolean.TRUE.equals(chartDisplayPassing.getValue())) {
                    chartDataModels.add(new LinesChartDataModel(
                            history.stream().mapToDouble(i -> i.getSkillValue(PlayerSkill.PASSING)).toArray(),
                            prefix + TranslationFacility.tr("ls.player.skill.passing"),
                            true,
                            Colors.getColor(Colors.COLOR_PLAYER_PS)));
                }
                if (Boolean.TRUE.equals(chartDisplayScoring.getValue())) {
                    chartDataModels.add(new LinesChartDataModel(
                            history.stream().mapToDouble(i -> i.getSkillValue(PlayerSkill.SCORING)).toArray(),
                            prefix + TranslationFacility.tr("ls.player.skill.scoring"),
                            true,
                            Colors.getColor(Colors.COLOR_PLAYER_SC)));
                }
                if (Boolean.TRUE.equals(chartDisplaySetPieces.getValue())) {
                    chartDataModels.add(new LinesChartDataModel(
                            history.stream().mapToDouble(i -> i.getSkillValue(PlayerSkill.SETPIECES)).toArray(),
                            prefix + TranslationFacility.tr("ls.player.skill.setpieces"),
                            true,
                            Colors.getColor(Colors.COLOR_PLAYER_SP)));
                }
                if (Boolean.TRUE.equals(chartDisplayTSI.getValue())) {
                    chartDataModels.add(new LinesChartDataModel(
                            history.stream().mapToDouble(Player::getTsi).toArray(),
                            prefix + TranslationFacility.tr("ls.player.tsi"),
                            true,
                            Colors.getColor(Colors.COLOR_PLAYER_TSI), 1, true));
                }
                if (Boolean.TRUE.equals(chartDisplayWage.getValue())) {
                    chartDataModels.add(new LinesChartDataModel(
                            history.stream().mapToDouble(i->i.getWage().toLocale().doubleValue()).toArray(),
                            prefix + TranslationFacility.tr("ls.player.tsi"),
                            true,
                            Colors.getColor(Colors.COLOR_PLAYER_TSI), 1, true));
                }


                historyChart.setAllValues(chartDataModels.toArray(new LinesChartDataModel[0]),
                        history.stream().mapToDouble(i-> Date.from(i.getHrfDate().instant).getTime()).toArray(),
                        Helper.DEFAULTDEZIMALFORMAT,
                        TranslationFacility.tr("Datum"),
                        "", false, true);
            }
        }
        historyPanel.revalidate();
        historyPanel.repaint();
    }

    public void storeUserSettings() {
        var tableModel = UserColumnController.instance().getHallOfFameTableModel();
        tableModel.storeUserSettings();

        var splitPane = (JSplitPane)this.getComponent(0);
        ModuleConfig.instance().setInteger("HallOfFamePanel.VerticalSplitPosition", splitPane.getDividerLocation());
    }
}
