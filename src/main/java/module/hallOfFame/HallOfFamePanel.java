package module.hallOfFame;

import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import core.model.TranslationFacility;
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
        historyChart  = new HOLinesChart(false, "skill", null,null, null, 0., 9. );
        historyPanel.add(historyChart.getPanel());
        splitPane.setRightComponent(historyPanel);

        add(splitPane, BorderLayout.CENTER);
        tableModel.initData();

        var dividerLocation = ModuleConfig.instance().getInteger("HallOfFamePanel.VerticalSplitPosition");
        splitPane.setDividerLocation(dividerLocation != null ? dividerLocation : 400);
    }

    private void refreshHistory() {

        var players = this.hallOfFameTable.getSelectedPlayers();
        for (var player : players) {
            var chartDataModels = new ArrayList<LinesChartDataModel>();
            if (player instanceof HallOfFamePlayer hallOfFamePlayer) {
                var exTrainer = hallOfFamePlayer.getHistory();
                var prefix = player.getShortName() + " ";
                chartDataModels.add(new LinesChartDataModel(exTrainer.ratings.stream().mapToDouble(i -> i.coachLevel).toArray(), prefix + TranslationFacility.tr("ls.team.coachingskill"), true, Colors.getColor(Colors.COLOR_PLAYER_PM)));
                chartDataModels.add(new LinesChartDataModel(exTrainer.ratings.stream().mapToDouble(i -> i.leadership).toArray(), prefix + TranslationFacility.tr("ls.player.leadership"), true, Colors.getColor(Colors.COLOR_PLAYER_WI)));
                historyChart.setAllValues(chartDataModels.toArray(new LinesChartDataModel[0]),
                        exTrainer.ratings.stream().mapToDouble(i-> Date.from(i.time.instant).getTime()).toArray(),
                        Helper.DEFAULTDEZIMALFORMAT,
                        TranslationFacility.tr("Wochen"),
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
