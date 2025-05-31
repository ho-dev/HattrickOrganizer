package module.training.ui;

import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.NumericDocument;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import core.model.TranslationFacility;
import core.model.enums.MatchType;
import core.model.player.Player;
import core.net.OnlineWorker;
import core.training.TrainingManager;
import core.util.Helper;
import core.util.HelperWrapper;
import core.util.StringUtils;
import module.training.ui.model.ModelChange;
import module.training.ui.model.TrainingModel;
import module.training.ui.renderer.OutputTableRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The Panel where the main training table is shown ("Training").
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class OutputPanel extends LazyImagePanel {

    private final TrainingProgressTableModel trainingProgressTableModel;
    private FixedColumnsTable trainingProgressTable;

//    private JTable fixedOutputTable;
//    private JTable outputTable;
    private JButton importButton;
    private JButton calculateButton;
    private final TrainingModel trainingModel;
    private FutureTrainingPrioPopup trainingPrioPopUp;

    /**
     * Creates a new OutputPanel object.
     */
    public OutputPanel(TrainingModel model) {
        super();
        this.trainingProgressTableModel = UserColumnController.instance().getTrainingProgressTableModel();
        this.trainingModel = model;
    }

    @Override
    protected void initialize() {
        initComponents();
        addListeners();
        registerRefreshable(true);
        update();
        setNeedsRefresh(false);
    }

    @Override
    protected void update() {
        this.trainingProgressTableModel.initData();
    }

    /**
     * Import a match from Hattrick
     */
    @SuppressWarnings("deprecation")
    private void importMatches() {

        JTextField tf = new JTextField();
        tf.setDocument(new NumericDocument(10));
        Object[] objs = {TranslationFacility.tr("ls.match.id"), tf};

        int value = JOptionPane.showConfirmDialog(HOMainFrame.instance(), objs,
                TranslationFacility.tr("ImportMatch"), JOptionPane.OK_CANCEL_OPTION);

        String input = tf.getText();
        if (value == JOptionPane.YES_OPTION && !StringUtils.isEmpty(input)) {

            int matchID = Integer.parseInt(input);

            if (HelperWrapper.instance().isUserMatch(input, MatchType.LEAGUE)) {
                if (OnlineWorker.downloadMatchData(matchID, MatchType.LEAGUE, false)) {
                    Helper.showMessage(null,
                            TranslationFacility.tr("MatchImported"),
                            TranslationFacility.tr("ImportOK"), 1);
                    RefreshManager.instance().doRefresh();
                }
            } else {
                Helper.showMessage(null, TranslationFacility.tr("NotUserMatch"),
                        TranslationFacility.tr("ImportError"), 1);
            }
        }
    }

    private void addListeners() {
        var playerIdColumnModelIndex = this.trainingProgressTableModel.getPlayerIdColumn().getIndex();
        this.trainingProgressTable.getSelectionModel().addListSelectionListener(new PlayerSelectionListener(this.trainingModel, this.trainingProgressTable, playerIdColumnModelIndex));
        this.trainingProgressTable.getSelectionModel().addListSelectionListener(e -> {
            var index = trainingProgressTable.getSelectedRow();
            trainingProgressTable.getSelectionModel().setSelectionInterval(index, index);
        });
//        this.fixedOutputTable.getSelectionModel().addListSelectionListener(e -> {
//            var index = fixedOutputTable.getSelectedRow();
//            outputTable.getSelectionModel().setSelectionInterval(index, index);
//        });

        this.importButton.addActionListener(arg0 -> importMatches());
        this.calculateButton.addActionListener(arg0 -> {
            // recalcSubskills() causes UI update via RefreshManager, so no
            // need to update UI ourself
            TrainingManager.instance().recalcSubskills(true);
        });

        this.trainingModel.addModelChangeListener(change -> {
            if (change == ModelChange.ACTIVE_PLAYER) {
                selectPlayerFromModel();
            }
        });

        this.trainingProgressTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (trainingProgressTable.getSelectedRow() < 0)
                    return;
                if (e.getComponent() instanceof JTable) {
                    trainingPrioPopUp.updateActivePlayer();
                    trainingPrioPopUp.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void selectPlayerFromModel() {
        this.trainingProgressTable.clearSelection();
        Player player = this.trainingModel.getActivePlayer();
        if (player != null) {
            var playerIdColumnModelIndex = this.trainingProgressTableModel.getPlayerIdColumn().getIndex();
            var playerIdColumnViewIndex = this.trainingProgressTable.convertColumnIndexToView(playerIdColumnModelIndex);
            for (int i = 0; i < this.trainingProgressTable.getRowCount(); i++) {
                String val = ((ColorLabelEntry) trainingProgressTable.getValueAt(i, playerIdColumnViewIndex)).getText();
                int id = Integer.parseInt(val);
                if (player.getPlayerId() == id) {
                    this.trainingProgressTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    /**
     * Initialize the object layout
     */
    private void initComponents() {
        setLayout(new BorderLayout());

        this.trainingProgressTableModel.setModel(this.trainingModel);
        this.trainingProgressTable = new FixedColumnsTable(this.trainingProgressTableModel);
        this.trainingProgressTable.setDefaultRenderer(Object.class, new OutputTableRenderer(false));

//        fixedOutputTable = new OutputTable(UserColumnController.instance().getTrainingProgressTableModel());
//        fixedOutputTable.getTableHeader().setReorderingAllowed(false);
//        fixedOutputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        fixedOutputTable.setDefaultRenderer(Object.class, new OutputTableRenderer(true));
//        outputTable = new OutputTable(new TrainingProgressTableModel(this.model));
//        outputTable.getTableHeader().setReorderingAllowed(false);
//        outputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        outputTable.setDefaultRenderer(Object.class, new OutputTableRenderer(false));
//
//        // Setup column models
//        for (int i=0; i< outputTable.getModel().getColumnCount(); i++){
//            int fixedColumns = 1;
//            if ( i < fixedColumns){
//                var col = outputTable.getColumnModel().getColumn(0);
//                outputTable.getColumnModel().removeColumn(col);
//            }
//            else {
//                var col = fixedOutputTable.getColumnModel().getColumn(fixedColumns);
//                fixedOutputTable.getColumnModel().removeColumn(col);
//            }
//        }
//
//        fixedOutputTable.getColumnModel().getColumn(0).setPreferredWidth(150);
//
//        outputTable.getColumnModel().getColumn(0).setPreferredWidth(60);
//        outputTable.getColumnModel().getColumn(1).setPreferredWidth(140);
//
//        for (int i = 2; i < outputTable.getColumnCount(); i++) {
//            TableColumn column = outputTable.getColumnModel().getColumn(i);
//            column.setPreferredWidth(70);
//        }

//        // Hide playerId column
//        var nColumns = outputTable.getColumnModel().getColumnCount();
//        var playerIDCol = outputTable.getColumnModel().getColumn(nColumns-1);
//        playerIDCol.setPreferredWidth(0);
//        playerIDCol.setMinWidth(0);
//        playerIDCol.setMaxWidth(0);
//
//        outputTable.setAutoResizeMode(0);
//        outputTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
//        fixedOutputTable.setPreferredScrollableViewportSize(new Dimension(150, 70));
//        outputTable.setAutoCreateRowSorter(true);
//        fixedOutputTable.setAutoCreateRowSorter(true);
//
//        TableRowSorter<TableModel> sorter = new TableRowSorter<>(outputTable.getModel());
//        outputTable.setRowSorter(sorter);
//        fixedOutputTable.setRowSorter(sorter);
//        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
//
//        int columnIndexToSort = 0;
//        sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
//        sorter.setSortKeys(sortKeys);
//        sorter.sort();
//        var scrollPane = new JScrollPane(outputTable);
//        var fixedScrollPane = new JScrollPane(fixedOutputTable);
//        var bar = fixedScrollPane.getVerticalScrollBar();
//        var bar2 = scrollPane.getVerticalScrollBar();
//        bar.setPreferredSize(new Dimension(0, 0));
//        // Synchronize vertical scrolling
//        AdjustmentListener adjustmentListener = e -> {
//            if (e.getSource() == bar2) {
//                bar.setValue(e.getValue());
//            } else {
//                bar2.setValue(e.getValue());
//            }
//        };
//        bar.addAdjustmentListener(adjustmentListener);
//        bar2.addAdjustmentListener(adjustmentListener);
//
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
//        splitPane.setLeftComponent(fixedScrollPane);
//        splitPane.setRightComponent(scrollPane);
        add(this.trainingProgressTable.getContainerComponent(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());

        this.importButton = new JButton(TranslationFacility.tr("ImportMatch"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(6, 8, 6, 4);
        buttonPanel.add(this.importButton, gbc);

        this.calculateButton = new JButton(TranslationFacility.tr("ls.menu.file.subskillrecalculation"));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 4, 6, 8);
        buttonPanel.add(this.calculateButton, gbc);

        add(buttonPanel, BorderLayout.NORTH);
        trainingPrioPopUp = new FutureTrainingPrioPopup(this, this.trainingModel);
    }

}