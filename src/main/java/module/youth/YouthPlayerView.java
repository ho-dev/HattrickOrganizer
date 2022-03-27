package module.youth;

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
import module.training.Skills;

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
    private HOLinesChart youthSkillChart;

    private JTable playerOverviewTable;
    private YouthPlayerOverviewTableModel playerOverviewTableModel;
    private YouthTableSorter playerOverviewTableSorter;

    private JLabel playerNameLabel;
    private YouthSkillInfoEditor[] playerSkillInfoEditors;
    private JEditorPane playerScoutCommentField;
    private JTable playerDetailsTable;
    private YouthPlayerDetailsTableModel playerDetailsTableModel;
    private YouthTableSorter playerDetailsTableSorter;

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
        split2.setLeftComponent(split1);
        setDividerLocation(split1, VERTICALSPLIT1_POSITION);
        setDividerLocation(split2, VERTICALSPLIT2_POSITION);

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
        var scoutAndEditorPanel = new JPanel(new GridBagLayout());
        var scoutAndEditorPanelConstraints = new GridBagConstraints();
        scoutAndEditorPanelConstraints.anchor=GridBagConstraints.FIRST_LINE_START;
        scoutAndEditorPanelConstraints.insets = new Insets(5,5,5,5);
        scoutAndEditorPanelConstraints.gridx=0;
        scoutAndEditorPanelConstraints.gridy=0;
        scoutAndEditorPanelConstraints.gridwidth=2;
        scoutAndEditorPanelConstraints.weightx=1;
        scoutAndEditorPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.youth.player.scoutcomment")+":"), scoutAndEditorPanelConstraints);
        scoutAndEditorPanelConstraints.gridy++;
        scoutAndEditorPanel.add(playerScoutCommentField, scoutAndEditorPanelConstraints);
        scoutAndEditorPanelConstraints.gridy++;
        scoutAndEditorPanelConstraints.gridwidth=1;

        var ystart = scoutAndEditorPanelConstraints.gridy;

        for ( int i=0; i<YouthPlayer.skillIds.length; i++){
            var skillInfoEditor = new YouthSkillInfoEditor(skillIDColorMap.get(YouthPlayer.skillIds[i]));
            skillInfoEditor.addCurrentValueChangeListener(currentValueChangeListener);
            skillInfoEditor.addStartValueChangeListener(startValueChangeListener);

            playerSkillInfoEditors[i] = skillInfoEditor;
            scoutAndEditorPanelConstraints.gridx=i%2;
            scoutAndEditorPanel.add(skillInfoEditor, scoutAndEditorPanelConstraints );
            if ( i%2 == 1 ) scoutAndEditorPanelConstraints.gridy++;
        }

        youthSkillChart = new HOLinesChart(false, "skill", null,"#,##0", null );
        youthSkillChart.setYAxisMin(1, 0.);
        youthSkillChart.setYAxisMax(1, 9.);
        var panel = new ImagePanel();
        panel.setLayout(new BorderLayout());
        panel.add(youthSkillChart.getPanel());
        scoutAndEditorPanelConstraints.gridheight = scoutAndEditorPanelConstraints.gridy+ystart+1;
        scoutAndEditorPanelConstraints.gridy = 0;
        scoutAndEditorPanelConstraints.gridx = 2;
        scoutAndEditorPanelConstraints.gridwidth = 2;
        scoutAndEditorPanelConstraints.weightx=1;
        scoutAndEditorPanelConstraints.weighty=1;
        scoutAndEditorPanelConstraints.fill =  GridBagConstraints.BOTH;
        panel.setPreferredSize(new Dimension(320, 120));
        scoutAndEditorPanel.add(panel, scoutAndEditorPanelConstraints);

        scoutAndEditorPanelConstraints.gridx = 0;
        scoutAndEditorPanelConstraints.gridy += scoutAndEditorPanelConstraints.gridheight+1;
        scoutAndEditorPanelConstraints.gridwidth = 1;
        scoutAndEditorPanelConstraints.gridheight = 1;
        scoutAndEditorPanelConstraints.weighty=1;
        scoutAndEditorPanel.add(new JPanel(), scoutAndEditorPanelConstraints); // empty rows to eat up remaining space
        split2.setRightComponent(new JScrollPane(scoutAndEditorPanel));

        initModel();
        RefreshManager.instance().registerRefreshable(this);
        playerOverviewTable.setDefaultRenderer(Object.class, new YouthPlayerOverviewTableCellRenderer());
        playerDetailsTable.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());

        this.add(split2, BorderLayout.CENTER);
    }

    private void setDividerLocation(JSplitPane split, String configKey) {
        var dividerLocation = ModuleConfig.instance().getInteger(configKey);
        if (dividerLocation != null) split.setDividerLocation(dividerLocation);
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

            playerDetailsTableSorter = new YouthTableSorter(playerDetailsTableModel, playerDetailsTable);
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

    private CurrentValueChangeListener currentValueChangeListener = new CurrentValueChangeListener();
    private StartValueChangeListener startValueChangeListener = new StartValueChangeListener();
    private boolean isRefreshingPlayerDetails =false;

    final private Map<Skills.HTSkillID, Color> skillIDColorMap = Map.of(
            Skills.HTSkillID.Keeper, Colors.getColor(Colors.COLOR_PLAYER_GK),
            Skills.HTSkillID.SetPieces, Colors.getColor(Colors.COLOR_PLAYER_SP),
            Skills.HTSkillID.Defender, Colors.getColor(Colors.COLOR_PLAYER_DE),
            Skills.HTSkillID.Scorer, Colors.getColor(Colors.COLOR_PLAYER_SC),
            Skills.HTSkillID.Winger, Colors.getColor(Colors.COLOR_PLAYER_WI),
            Skills.HTSkillID.Passing, Colors.getColor(Colors.COLOR_PLAYER_PS),
            Skills.HTSkillID.Playmaker, Colors.getColor(Colors.COLOR_PLAYER_PM)
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
        var splitPane = (JSplitPane)this.getComponent(0);
        ModuleConfig.instance().setInteger(VERTICALSPLIT2_POSITION, splitPane.getDividerLocation());
        splitPane = (JSplitPane)splitPane.getLeftComponent();
        ModuleConfig.instance().setInteger(VERTICALSPLIT1_POSITION, splitPane.getDividerLocation());
    }
}