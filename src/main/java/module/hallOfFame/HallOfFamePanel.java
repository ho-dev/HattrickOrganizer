package module.hallOfFame;

import core.constants.player.PlayerSkill;
import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import core.model.TranslationFacility;
import core.module.config.ModuleConfig;
import core.util.Helper;
import core.util.chart.HOLinesChart;
import core.util.chart.LinesChartDataModel;
import module.statistics.Colors;
import module.youth.YouthPlayer;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.stream.Collectors;

public class HallOfFamePanel extends JPanel {
    private PlayersTable hallOfFameTable;

    public HallOfFamePanel(){
        setLayout(new BorderLayout());
        var tableModel = UserColumnController.instance().getHallOfFameTableModel();
        this.hallOfFameTable = new PlayersTable(tableModel);
        var splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
        splitPane.setLeftComponent(hallOfFameTable.getContainerComponent());
        var historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(new JLabel("History"), BorderLayout.NORTH);
        var chart  = new HOLinesChart(false, "skill", null,null, null, 0., 9. );
        historyPanel.add(chart.getPanel());
        splitPane.setRightComponent(historyPanel);

        add(splitPane, BorderLayout.CENTER);
        tableModel.initData();

        var dividerLocation = ModuleConfig.instance().getInteger("HallOfFamePanel.VerticalSplitPosition");
        splitPane.setDividerLocation(dividerLocation != null ? dividerLocation : 400);
    }

    private void refreshHistory() {

        var players = this.hallOfFameTable.getSelectedPlayers();
        for (var player : players) {

//            playerNameLabel.setText(player.getFullName());
            var chartDataModels = new LinesChartDataModel[1];
//            for (int i = 0; i < chartDataModels.length; i++) {
//                var skillId = YouthPlayer.skillIds[i];
//                playerSkillInfoEditors[i].setSkillInfo(player.getSkillInfo(skillId));
            if (player instanceof HallOfFamePlayer hallOfFamePlayer) {
                var exTrainer = hallOfFamePlayer.getExTrainer();
                chartDataModels[0] = new LinesChartDataModel(exTrainer.ratings.stream().mapToDouble(i -> i.coachLevel).toArray(), "CoachLevel", true, Colors.getColor(Colors.COLOR_PLAYER_PM));
            }
//            youthSkillChart.setAllValues(chartDataModels, player.getSkillDevelopmentDates(), Helper.DEFAULTDEZIMALFORMAT, TranslationFacility.tr("Wochen"), "", false, true);
//            playerScoutCommentField.setText(getScoutComment(player));
//            playerDetailsTableModel.setYouthPlayer(player);
//            playerDetailsTableModel.initData();
        }
    }

    public void storeUserSettings() {
        var tableModel = UserColumnController.instance().getHallOfFameTableModel();
        tableModel.storeUserSettings();

        var splitPane = (JSplitPane)this.getComponent(0);
        ModuleConfig.instance().setInteger("HallOfFamePanel.VerticalSplitPosition", splitPane.getDividerLocation());
    }
}
