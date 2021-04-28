package module.youth;

import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.module.config.ModuleConfig;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Comparator;

public class YouthPlayerView extends JPanel implements Refreshable, ListSelectionListener {

    public static final String VERTICALSPLIT1_POSITION = "YouthPlayerView.VerticalSplitPosition";
    public static final String VERTICALSPLIT2_POSITION = "YouthPlayerView.VerticalSplit2Position";

    private JTable playerOverviewTable;
    private YouthPlayerOverviewTableModel playerOverviewTableModel;
    private TableSorter playerOverviewTableSorter;

    private JLabel playerNameLabel;
    private YouthSkillInfoEditor[] playerSkillInfoEditors;
    private JEditorPane playerScoutCommentField;
    private JTable playerDetailsTable;
    private YouthPlayerDetailsTableModel playerDetailsTableModel;
    private TableSorter playerDetailsTableSorter;

    public YouthPlayerView() {
        super();
        playerOverviewTable = new JTable();
        playerDetailsTable = new JTable();
        playerNameLabel = new JLabel();
        playerSkillInfoEditors = new YouthSkillInfoEditor[YouthPlayer.skillIds.length];
        playerScoutCommentField = new JEditorPane();
        playerScoutCommentField.setContentType("text/html");
        playerScoutCommentField.setEditable(false);

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
        scoutAndEditorPanelConstraints.weightx=1;
        scoutAndEditorPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.youth.player.scoutcomment")+":"), scoutAndEditorPanelConstraints);
        scoutAndEditorPanelConstraints.gridy++;
        scoutAndEditorPanel.add(playerScoutCommentField, scoutAndEditorPanelConstraints);
        scoutAndEditorPanelConstraints.gridy++;

        for ( int i=0; i<YouthPlayer.skillIds.length; i++){
            var skillInfoEditor = new YouthSkillInfoEditor();
            playerSkillInfoEditors[i] = skillInfoEditor;
            scoutAndEditorPanelConstraints.gridx=i%2;
            scoutAndEditorPanel.add(skillInfoEditor, scoutAndEditorPanelConstraints );
            if ( i%2 == 1 ) scoutAndEditorPanelConstraints.gridy++;
        }
        scoutAndEditorPanelConstraints.gridy++;
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
            playerOverviewTableSorter = new TableSorter(playerOverviewTableModel, playerOverviewTableModel.getPositionInArray(0), getOrderByColumn());
            playerOverviewTable.setModel(playerOverviewTableSorter);

            playerOverviewTableModel.restoreUserSettings(playerOverviewTable);
            playerOverviewTableSorter.addMouseListenerToHeaderInTable(playerOverviewTable);
            playerOverviewTableSorter.initsort();
        }
    }

    private void refreshPlayerOverview() {
        playerOverviewTableModel.initData();
    }

    private void initPlayerDetails() {
        if (playerDetailsTableModel == null) {
            playerDetailsTableModel = UserColumnController.instance().getYouthPlayerDetailsColumnModel();
            playerDetailsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            playerDetailsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            playerDetailsTable.setRowSelectionAllowed(true);

            playerDetailsTableSorter = new TableSorter(playerDetailsTableModel, playerDetailsTableModel.getPositionInArray(0), playerDetailsTableModel.getPositionInArray(0));
            playerDetailsTable.setModel(playerDetailsTableSorter);
            playerDetailsTableModel.restoreUserSettings(playerDetailsTable);
            playerDetailsTableSorter.addMouseListenerToHeaderInTable(playerDetailsTable);
            playerDetailsTableSorter.initsort();
        }
    }

    private class CurrentValueChangeListener  implements ChangeListener{

        @Override
        public void stateChanged(ChangeEvent e) {
           var source = (JSlider)e.getSource();
            var skillInfoSlider = (YouthSkillInfoEditor.SkillInfoSlider) source.getParent();
            if (!source.getValueIsAdjusting()) {
                var skillInfo = skillInfoSlider.getSkillInfo();
                var oldValue = skillInfo.getCurrentValue();
                var newValue = skillInfoSlider.getSkillValue();
                var startValue = Math.max(0,skillInfo.getStartValue()+newValue-oldValue);
                skillInfo.setCurrentValue(newValue);
                skillInfo.setStartValue(skillInfoSlider.getSkillValue());
                refreshYouthPlayerDevelopment();
            }
            skillInfoSlider.setValueLabel();
        }
    }

    private void refreshYouthPlayerDevelopment() {
        var player = getSelectedPlayer();
        if (player != null) {
            player.calcTrainingDevelopment();
            refresh();
        }
    }

    private class StartValueChangeListener implements  ChangeListener{
        @Override
        public void stateChanged(ChangeEvent e) {
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

    private void refreshPlayerDetails() {
        var player = getSelectedPlayer();
        if ( player == null){
            // reset previous selection
            player = playerDetailsTableModel.getYouthPlayer();
            if ( player != null) setSelectedPlayer(player);
        }
        if (player != null) {
            playerNameLabel.setText(player.getFullName());
            for ( int i=0; i< YouthPlayer.skillIds.length; i++){
                playerSkillInfoEditors[i].setSkillInfo(player.getSkillInfo(YouthPlayer.skillIds[i]));
                playerSkillInfoEditors[i].addCurrentValueChangeListener(currentValueChangeListener);
                playerSkillInfoEditors[i].addStartValueChangeListener(startValueChangeListener);
            }
            playerScoutCommentField.setText(getScoutComment(player));
            playerDetailsTableModel.setYouthPlayer(player);
            playerDetailsTableModel.initData();
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

    private int getOrderByColumn() {
        return switch (UserParameter.instance().standardsortierung) {
            case UserParameter.SORT_NAME -> playerOverviewTableModel.getPositionInArray(0);
            default -> playerOverviewTableModel.getPositionInArray(0);
        };
    }

    @Override
    public final void refresh() {
        refreshPlayerOverview();
        refreshPlayerDetails();
    }

    private YouthPlayer getSelectedPlayer() {
        var row = this.playerOverviewTable.getSelectedRow();
        if ( row < 0 && this.playerOverviewTable.getRowCount() > 0){
            // init selection
            this.playerOverviewTable.setRowSelectionInterval(0,0);
            row = 0;
        }
        if ( row > -1) {
            var index = playerOverviewTableSorter.getIndex(row);
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
            var index = playerOverviewTableSorter.getIndex(row);
            var player = currentPlayers.get(index);
            if ( player != null && player.getId() == selectedPlayer.getId()){
                this.playerOverviewTable.setRowSelectionInterval(row,row);
                break;
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        refreshPlayerDetails();
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