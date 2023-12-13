package module.youth;

import core.constants.player.PlayerSkill;
import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.module.config.ModuleConfig;
import core.util.Helper;
import core.util.chart.HOLinesChart;
import core.util.chart.LinesChartDataModel;
import module.statistics.Colors;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Comparator;
import java.util.Map;

public class YouthPlayerView extends JPanel implements Refreshable, ListSelectionListener {

    public static final String VERTICALSPLIT1_POSITION = "YouthPlayerView.VerticalSplitPosition";
    public static final String VERTICALSPLIT2_POSITION = "YouthPlayerView.VerticalSplit2Position";
    public static final String HORIZONTALSPLIT_POSITION = "YouthPlayerView.HorizontalSplitPosition";
    private final HOLinesChart youthSkillChart;

    private final JTable playerOverviewTable;
    private YouthPlayerOverviewTableModel playerOverviewTableModel;
    private YouthTableSorter playerOverviewTableSorter;

    private final JLabel playerNameLabel;
    private final YouthSkillInfoEditor[] playerSkillInfoEditors;
    private final JEditorPane playerScoutCommentField;
    private final JTable playerDetailsTable;
    private YouthPlayerDetailsTableModel playerDetailsTableModel;

    public YouthPlayerView() {
        super();
        playerOverviewTable = new JTable();
        playerDetailsTable = new JTable();
        playerNameLabel = new JLabel();

        playerScoutCommentField = new JEditorPane();
        playerScoutCommentField.setContentType("text/html");
        playerScoutCommentField.setEditable(false);

        playerSkillInfoEditors = new YouthSkillInfoEditor[YouthPlayer.skillIds.length];

        var split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
        var split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
        var split3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
        split2.setLeftComponent(split1);
        split2.setRightComponent(split3);

        setDividerLocation(split1, VERTICALSPLIT1_POSITION, 200);
        setDividerLocation(split2, VERTICALSPLIT2_POSITION, 400);
        setDividerLocation(split3, HORIZONTALSPLIT_POSITION, 800);

        // First section
        split1.setLeftComponent(new JScrollPane(playerOverviewTable));

        // Second section
        var developmentPanel = new JPanel(new BorderLayout());
        var topLinePanel = new JPanel(new BorderLayout());
        topLinePanel.add(playerNameLabel, BorderLayout.NORTH);
        topLinePanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.youth.player.trainingdevelopment")));
        developmentPanel.add(topLinePanel, BorderLayout.NORTH);
        developmentPanel.add(new JScrollPane(playerDetailsTable));
        split1.setRightComponent(developmentPanel);

        // Third section

        // Scout comment panel
        var scoutAndChartPanel = new JPanel(new GridBagLayout());
        var scoutAndChartPanelConstraints = new GridBagConstraints();
        scoutAndChartPanelConstraints.anchor=GridBagConstraints.FIRST_LINE_START;
        scoutAndChartPanelConstraints.insets = new Insets(5,5,5,5);
        scoutAndChartPanelConstraints.gridx=0;
        scoutAndChartPanelConstraints.gridy=0;
        scoutAndChartPanelConstraints.weighty=0;
        scoutAndChartPanelConstraints.weightx=1;
        scoutAndChartPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        scoutAndChartPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.youth.player.scoutcomment")+":"), scoutAndChartPanelConstraints);
        scoutAndChartPanelConstraints.gridy++;
        scoutAndChartPanel.add(playerScoutCommentField, scoutAndChartPanelConstraints);
        scoutAndChartPanelConstraints.gridy++;
        scoutAndChartPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.youth.player.development")+":"), scoutAndChartPanelConstraints);
        youthSkillChart = new HOLinesChart(false, "skill", null,"#,##0", null );
        youthSkillChart.setYAxisMin(1, 0.);
        youthSkillChart.setYAxisMax(1, 9.);
        var panel = new ImagePanel();
        panel.setLayout(new BorderLayout());
        panel.add(youthSkillChart.getPanel());
        scoutAndChartPanelConstraints.gridheight = 5;
        scoutAndChartPanelConstraints.gridy++;
        scoutAndChartPanelConstraints.weighty=1;
        scoutAndChartPanelConstraints.fill =  GridBagConstraints.BOTH;
        panel.setPreferredSize(new Dimension(320, 240));
        scoutAndChartPanel.add(panel, scoutAndChartPanelConstraints);

        // Skill editors
        var skillEditorPanel = new JPanel(new GridBagLayout());
        var skillEditorPanelConstraints = new GridBagConstraints();
        skillEditorPanelConstraints.insets = new Insets(5,5,5,5);
        skillEditorPanelConstraints.gridy=0;
        skillEditorPanelConstraints.gridwidth=1;
        skillEditorPanelConstraints.anchor=GridBagConstraints.WEST;
        skillEditorPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.youth.player.skilleditors")), skillEditorPanelConstraints);

        for ( int i=0; i<YouthPlayer.skillIds.length; i++){
            var skillInfoEditor = new YouthSkillInfoEditor(skillIDColorMap.get(YouthPlayer.skillIds[i]));
            CurrentValueChangeListener currentValueChangeListener = new CurrentValueChangeListener();
            skillInfoEditor.addCurrentValueChangeListener(currentValueChangeListener);
            StartValueChangeListener startValueChangeListener = new StartValueChangeListener();
            skillInfoEditor.addStartValueChangeListener(startValueChangeListener);

            playerSkillInfoEditors[i] = skillInfoEditor;
            skillEditorPanelConstraints.gridy++;
            skillEditorPanel.add(skillInfoEditor, skillEditorPanelConstraints );
        }

        split3.setLeftComponent(new JScrollPane(scoutAndChartPanel));
        split3.setRightComponent(new JScrollPane(skillEditorPanel));

        initModel();
        RefreshManager.instance().registerRefreshable(this);
        playerOverviewTable.setDefaultRenderer(Object.class, new YouthPlayerOverviewTableCellRenderer());
        playerDetailsTable.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());

        this.add(split2, BorderLayout.CENTER);
    }

    private void setDividerLocation(JSplitPane split, String configKey, int defaultPosition) {
        var dividerLocation = ModuleConfig.instance().getInteger(configKey);
        split.setDividerLocation(dividerLocation != null ? dividerLocation : defaultPosition);
    }

    @Override
    public final void reInit() {
        refresh();
    }

    private void initModel() {
        setLayout(new BorderLayout());
        initPlayerOverview();
        initPlayerDetails();
    }

    private void initPlayerOverview() {
        playerOverviewTable.setOpaque(false);
        if (playerOverviewTableModel == null) {
            playerOverviewTableModel = UserColumnController.instance().getYouthPlayerOverviewColumnModel();
            playerOverviewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            playerOverviewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            playerOverviewTable.setRowSelectionAllowed(true);
            var selectionModel = playerOverviewTable.getSelectionModel();
            selectionModel.addListSelectionListener(this);
            playerOverviewTableSorter = new YouthTableSorter(playerOverviewTableModel, playerOverviewTable);
            playerOverviewTable.setModel(playerOverviewTableSorter);
            playerOverviewTableModel.restoreUserSettings(playerOverviewTable);
        }
    }

    private boolean isRefreshingPlayerOverview=false;
    private void refreshPlayerOverview() {
        if ( isRefreshingPlayerOverview) return;
        try {
            isRefreshingPlayerOverview = true;
            var selection = this.playerOverviewTableSorter.getSelectedModelIndex();
            playerOverviewTableModel.initData();
            this.playerOverviewTableSorter.setSelectedModelIndex(selection);
        }
        finally {
            isRefreshingPlayerOverview=false;
        }
    }

    private void initPlayerDetails() {
        if (playerDetailsTableModel == null) {
            playerDetailsTableModel = UserColumnController.instance().getYouthPlayerDetailsColumnModel();
            playerDetailsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            playerDetailsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            playerDetailsTable.setRowSelectionAllowed(true);

            YouthTableSorter playerDetailsTableSorter = new YouthTableSorter(playerDetailsTableModel, playerDetailsTable);
            playerDetailsTable.setModel(playerDetailsTableSorter);
            playerDetailsTableModel.restoreUserSettings(playerDetailsTable);
        }
    }

    private class CurrentValueChangeListener  implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (isRefreshingPlayerDetails) return;
            var source = (JSlider) e.getSource();
            var skillInfoSlider = (YouthSkillInfoEditor.SkillInfoSlider) source.getParent();
            if (!source.getValueIsAdjusting()) {
                var skillInfo = skillInfoSlider.getSkillInfo();
                var oldValue = skillInfo.getCurrentValue();
                var newValue = skillInfoSlider.getSkillValue();
                var startValue = Math.max(0, skillInfo.getStartValue() + newValue - oldValue);
                skillInfo.setCurrentValue(newValue);
                skillInfo.setStartValue(startValue);
                refreshYouthPlayerDevelopment();
            }
            skillInfoSlider.setValueLabel();
        }
    }

    private boolean isRefreshingPlayerDevelopment = false;
    private void refreshYouthPlayerDevelopment() {
        if (isRefreshingPlayerDevelopment) return;
        try {
            isRefreshingPlayerDevelopment = true;
            var player = getSelectedPlayer();
            if (player != null) {
                player.calcTrainingDevelopment();
                refresh();
            }
        }
        finally {
            isRefreshingPlayerDevelopment = false;
        }
    }

    private class StartValueChangeListener implements  ChangeListener{
        @Override
        public void stateChanged(ChangeEvent e) {
            if (isRefreshingPlayerDetails) return;
            var source = (JSlider)e.getSource();
            var skillInfoSlider = (YouthSkillInfoEditor.SkillInfoSlider) source.getParent();
            if (!source.getValueIsAdjusting()) {
                var skillInfo = skillInfoSlider.getSkillInfo();
                var newStartValue = skillInfoSlider.getSkillValue();
                skillInfo.setCurrentValue(Math.max(skillInfo.getCurrentValue(), newStartValue));
                skillInfo.setStartValue(newStartValue);
                refreshYouthPlayerDevelopment();
            }
            skillInfoSlider.setValueLabel();
        }
    }

    private boolean isRefreshingPlayerDetails =false;

    final private Map<PlayerSkill, Color> skillIDColorMap = Map.of(
            PlayerSkill.KEEPER, Colors.getColor(Colors.COLOR_PLAYER_GK),
            PlayerSkill.SETPIECES, Colors.getColor(Colors.COLOR_PLAYER_SP),
            PlayerSkill.DEFENDING, Colors.getColor(Colors.COLOR_PLAYER_DE),
            PlayerSkill.SCORING, Colors.getColor(Colors.COLOR_PLAYER_SC),
            PlayerSkill.WINGER, Colors.getColor(Colors.COLOR_PLAYER_WI),
            PlayerSkill.PASSING, Colors.getColor(Colors.COLOR_PLAYER_PS),
            PlayerSkill.PLAYMAKING, Colors.getColor(Colors.COLOR_PLAYER_PM)
    );

    private void refreshPlayerDetails() {
        if (isRefreshingPlayerDetails) return;
        try {
            isRefreshingPlayerDetails = true;   // prevent recursions
            var player = getSelectedPlayer();
            if (player == null) {
                // reset previous selection
                player = playerDetailsTableModel.getYouthPlayer();
                if (player != null) setSelectedPlayer(player);
            }
            if (player != null) {
                playerNameLabel.setText(player.getFullName());
                var chartDataModels = new LinesChartDataModel[YouthPlayer.skillIds.length];
                for (int i = 0; i < YouthPlayer.skillIds.length; i++) {
                    var skillId = YouthPlayer.skillIds[i];
                    playerSkillInfoEditors[i].setSkillInfo(player.getSkillInfo(skillId));
                    chartDataModels[i] = new LinesChartDataModel(player.getSkillDevelopment(skillId), skillId.name(), true, skillIDColorMap.get(skillId), null);
                }
                youthSkillChart.setAllValues(chartDataModels, player.getSkillDevelopmentDates(), Helper.DEFAULTDEZIMALFORMAT, HOVerwaltung.instance().getLanguageString("Wochen"), "",false, true);
                playerScoutCommentField.setText(getScoutComment(player));
                playerDetailsTableModel.setYouthPlayer(player);
                playerDetailsTableModel.initData();
            }
        }
        finally {
            isRefreshingPlayerDetails = false;
        }
    }

    private String getScoutComment(YouthPlayer player) {
        var ret = new StringBuilder("<html>");
        player.getScoutComments().stream()
                .sorted(Comparator.comparingInt(YouthPlayer.ScoutComment::getIndex))
                .forEach(i->ret.append(formatLine(i.getText())));
        return ret.toString();
    }

    private String formatLine(String text) {
        /*if ( !text.endsWith("&nbsp;")) */
        return text + "<br/>";
    }

    @Override
    public final void refresh() {
        refreshPlayerOverview();
        refreshPlayerDetails();
    }

    private YouthPlayer getSelectedPlayer() {
        var row = this.playerOverviewTable.getSelectedRow();
        if ( row < 0 && this.playerOverviewTable.getRowCount() > 0){
            row = 0;
            initSelection(row);
        }
        if ( row > -1) {
            var index = playerOverviewTableSorter.modelIndex(row);
            var currentPlayers = HOVerwaltung.instance().getModel().getCurrentYouthPlayers();
            if (currentPlayers != null && currentPlayers.size() > index) {
                return currentPlayers.get(index);
            }
        }
        return null;
    }

    private void setSelectedPlayer(YouthPlayer selectedPlayer) {
        var currentPlayers = HOVerwaltung.instance().getModel().getCurrentYouthPlayers();
        for (int row=0; row<currentPlayers.size(); row++){
            var index = playerOverviewTableSorter.modelIndex(row);
            var player = currentPlayers.get(index);
            if ( player != null && player.getId() == selectedPlayer.getId()){
                this.playerOverviewTable.setRowSelectionInterval(row,row);
                break;
            }
        }
    }

    private boolean isSelectionInitialized=false;
    private void initSelection(int row) {
        isSelectionInitialized=true;
        this.playerOverviewTable.setRowSelectionInterval(row,row);
        isSelectionInitialized=false;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if ( !e.getValueIsAdjusting() && !isSelectionInitialized) {
            refreshPlayerDetails();
        }
    }

    public void storeUserSettings() {
        this.playerOverviewTableModel.storeUserSettings(playerOverviewTable);
        this.playerDetailsTableModel.storeUserSettings(playerDetailsTable);
        // store split pane divider positions
        var split2Pane = (JSplitPane)this.getComponent(0);
        ModuleConfig.instance().setInteger(VERTICALSPLIT2_POSITION, split2Pane.getDividerLocation());
        var split1Pane  = (JSplitPane)split2Pane.getLeftComponent();
        ModuleConfig.instance().setInteger(VERTICALSPLIT1_POSITION, split1Pane.getDividerLocation());
        var split3Pane  = (JSplitPane)split2Pane.getRightComponent();
        ModuleConfig.instance().setInteger(HORIZONTALSPLIT_POSITION, split3Pane.getDividerLocation());
    }
}