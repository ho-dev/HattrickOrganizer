package module.youth;

import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.module.config.ModuleConfig;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Comparator;

import static java.lang.Math.max;
import static module.youth.YouthPanel.YOUTHPLAYERVIEW_VERTICALSPLIT_POSITION;

public class YouthPlayerView extends ImagePanel implements Refreshable, ListSelectionListener {

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

        var verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
        verticalSplitPane.setLeftComponent(new JScrollPane(playerOverviewTable));

        playerDetailsTable = new JTable();
        playerNameLabel = new JLabel();
        playerSkillInfoEditors = new YouthSkillInfoEditor[YouthPlayer.skillIds.length];
        playerScoutCommentField = new JEditorPane();
        playerScoutCommentField.setContentType("text/html");
        playerScoutCommentField.setEditable(false);

        var detailsPanel = new JPanel(new GridBagLayout());
        var constraints = new GridBagConstraints();
        int y=0;
        //constraints.gridwidth=2;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.weightx=1;
        constraints.weighty=0;
        constraints.gridx=0;
        constraints.gridy=y++;
        constraints.insets = new Insets(5,5,5,5);
        detailsPanel.add(playerNameLabel, constraints);

        // Scout comment panel
        var scoutCommentPanel = new JPanel(new BorderLayout());
        scoutCommentPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.youth.player.scoutcomment")+":"), BorderLayout.NORTH);
        scoutCommentPanel.add(playerScoutCommentField);

        constraints = new GridBagConstraints();
        constraints.gridx=0;
        constraints.gridy=y++;
        constraints.weightx=1;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5,5,5,5);
        detailsPanel.add(scoutCommentPanel, constraints );

        // Training development table
        var tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("ls.youth.player.trainingdevelopment")), BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(playerDetailsTable));

        constraints = new GridBagConstraints();
        constraints.gridx=0;
        constraints.gridy=y++;
        constraints.gridwidth=2;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.weightx=1;
        constraints.weighty=1;
        constraints.anchor=GridBagConstraints.NORTH;
        constraints.insets = new Insets(5,5,5,5);
        detailsPanel.add(tablePanel, constraints);

        for ( int i=0; i<YouthPlayer.skillIds.length; i++){
            var skillInfoEditor = new YouthSkillInfoEditor();
            playerSkillInfoEditors[i] = skillInfoEditor;
            constraints = new GridBagConstraints();
            constraints.gridx=i%2;
            constraints.gridy=y;
            if ( i%2 == 1 ) y++;
            constraints.weightx=1;
            constraints.fill=GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(5,5,5,5);
            detailsPanel.add(skillInfoEditor, constraints );
        }

        verticalSplitPane.setRightComponent(new JScrollPane(detailsPanel));

        initModel();
        RefreshManager.instance().registerRefreshable(this);
        playerOverviewTable.setDefaultRenderer(Object.class, new YouthPlayerOverviewTableCellRenderer());
        playerDetailsTable.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());

        var dividerLocation = ModuleConfig.instance().getInteger(YOUTHPLAYERVIEW_VERTICALSPLIT_POSITION);
        if (dividerLocation != null) verticalSplitPane.setDividerLocation(dividerLocation);
        this.add(verticalSplitPane);
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
    }
}