package module.hallOfFame;

import core.constants.player.PlayerSkill;
import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
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
    private final HOLinesChart historyChart;
    private final PlayersTable hallOfFameTable;

    public HallOfFamePanel(){
        setLayout(new BorderLayout());
        var tableModel = UserColumnController.instance().getHallOfFameTableModel();
        this.hallOfFameTable = new PlayersTable(tableModel);
        this.hallOfFameTable.addListSelectionListener(e -> refreshHistory());
        var splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
        splitPane.setLeftComponent(hallOfFameTable.getContainerComponent());
        var historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(new JLabel("History"), BorderLayout.NORTH);
        historyChart  = new HOLinesChart(false, "skill", null,null, null, 0., 21. );
        historyPanel.add(historyChart.getPanel());
        splitPane.setRightComponent(historyPanel);

        add(splitPane, BorderLayout.CENTER);
        tableModel.initData();

        var dividerLocation = ModuleConfig.instance().getInteger("HallOfFamePanel.VerticalSplitPosition");
        splitPane.setDividerLocation(dividerLocation != null ? dividerLocation : 400);
    }

    private void refreshHistory() {
        this.historyChart.clearAllPlots();
        var players = this.hallOfFameTable.getSelectedPlayers();
        for (var player : players) {
            if (player instanceof HallOfFamePlayer hallOfFamePlayer) {
                var history = hallOfFamePlayer.getHistory();
                var prefix = player.getShortName() + " ";
                var exTrainer = history.stream().filter(i->i.getCoachSkill()>0).toList();
                if (!exTrainer.isEmpty()) {
                    var exTrainerChartDataModels = new ArrayList<LinesChartDataModel>();
                    exTrainerChartDataModels.add(new LinesChartDataModel(exTrainer.stream().mapToDouble(Player::getCoachSkill).toArray(), prefix + TranslationFacility.tr("ls.team.coachingskill"), true, Colors.getColor(Colors.COLOR_CLUB_FORM_COACHS_LEVEL)));
                    this.historyChart.setAllValues(exTrainerChartDataModels.toArray(new LinesChartDataModel[0]),
                            exTrainer.stream().mapToDouble(i -> Date.from(i.getHrfDate().instant).getTime()).toArray(),
                            Helper.DEFAULTDEZIMALFORMAT,
                            TranslationFacility.tr("Datum"),
                            "", false, true);
                }
                var chartDataModels = new ArrayList<LinesChartDataModel>();
                chartDataModels.add(new LinesChartDataModel(history.stream().mapToDouble(Player::getLeadership).toArray(), prefix + TranslationFacility.tr("ls.player.leadership"), true, Colors.getColor(Colors.COLOR_PLAYER_LEADERSHIP)));
                chartDataModels.add(new LinesChartDataModel(history.stream().mapToDouble(i->i.getSkillValue(PlayerSkill.PLAYMAKING)).toArray(), prefix + TranslationFacility.tr("ls.player.skill.playmaking"), true, Colors.getColor(Colors.COLOR_PLAYER_PM)));
                this.historyChart.setAllValues(chartDataModels.toArray(new LinesChartDataModel[0]),
                        history.stream().mapToDouble(i-> Date.from(i.getHrfDate().instant).getTime()).toArray(),
                        Helper.DEFAULTDEZIMALFORMAT,
                        TranslationFacility.tr("Datum"),
                        "", false, true);
            }
        }
    }

    public void storeUserSettings() {
        var tableModel = UserColumnController.instance().getHallOfFameTableModel();
        tableModel.storeUserSettings();

        var splitPane = (JSplitPane)this.getComponent(0);
        ModuleConfig.instance().setInteger("HallOfFamePanel.VerticalSplitPosition", splitPane.getDividerLocation());
    }
}
