package module.youth;

import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static java.lang.Math.max;

public class YouthPlayerView extends ImagePanel implements Refreshable, ListSelectionListener {

    private JTable playerOverviewTable;
    private YouthPlayerOverviewTableModel playerOverviewTableModel;
    private TableSorter playerOverviewTableSorter;

    private JLabel playerNameLabel;
    private JTable playerDetailsTable;
    private YouthPlayerDetailsTableModel playerDetailsTableModel;

    public YouthPlayerView() {
        super();

        playerOverviewTable = new JTable();

        var verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
        verticalSplitPane.setLeftComponent(new JScrollPane(playerOverviewTable));

        playerDetailsTable = new JTable();
        playerNameLabel = new JLabel();
        var detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.add(playerNameLabel, BorderLayout.NORTH);
        detailsPanel.add(new JScrollPane(playerDetailsTable));
        verticalSplitPane.setRightComponent(detailsPanel);

        initModel();
        RefreshManager.instance().registerRefreshable(this);
        playerOverviewTable.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
        playerDetailsTable.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
        this.add(verticalSplitPane);

    }

    @Override
    public void reInit() {
        initModel();
        playerOverviewTable.repaint();
    }

    private void initModel() {
        setLayout(new BorderLayout());

        playerOverviewTable.setOpaque(false);
        if (playerOverviewTableModel == null) {
            playerOverviewTableModel = UserColumnController.instance().getYouthPlayerOverviewColumnModel();
            playerOverviewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            playerOverviewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            playerOverviewTable.setRowSelectionAllowed(true);

            var selectionModel = playerOverviewTable.getSelectionModel();
            selectionModel.addListSelectionListener(this);


            playerDetailsTableModel = UserColumnController.instance().getYouthPlayerDetailsColumnModel();
            playerDetailsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            playerDetailsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            playerDetailsTable.setRowSelectionAllowed(true);
        }
        playerOverviewTableModel.initData();
        playerOverviewTableSorter = new TableSorter(playerOverviewTableModel, playerOverviewTableModel.getPositionInArray(99), getOrderByColumn());
        playerOverviewTable.setModel(playerOverviewTableSorter);
        playerOverviewTableSorter.initsort();

        initPlayerDetails();
    }

    private void initPlayerDetails() {
        var player = getSelectedPlayer();
        if ( player != null) {
            playerNameLabel.setText(player.getFullName());
            playerDetailsTable.setModel(playerDetailsTableModel);
            playerDetailsTableModel.setYouthPlayer(player);
            playerDetailsTableModel.initData();
            playerDetailsTable.repaint();
        }
    }

    private int getOrderByColumn() {
        return switch (UserParameter.instance().standardsortierung) {
            case UserParameter.SORT_NAME -> playerOverviewTableModel.getPositionInArray(0);
            default -> playerOverviewTableModel.getPositionInArray(0);
        };
    }

    @Override
    public void refresh() {
        ((YouthPlayerOverviewTableModel) this.getSorter().getModel()).initData();
        playerOverviewTable.repaint();

        playerDetailsTableModel.initData();
        playerDetailsTable.repaint();
    }

    private TableSorter getSorter() {
        return this.playerOverviewTableSorter;
    }

    private YouthPlayer getSelectedPlayer(){
        var row = max(0,this.playerOverviewTable.getSelectedRow());
        var currentPlayers =  HOVerwaltung.instance().getModel().getCurrentYouthPlayers();
        if ( currentPlayers != null && currentPlayers.size()> row){
            return currentPlayers.get(row);
        }
        return null;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        initPlayerDetails();
    }
}
