package module.training.ui;

import core.gui.comp.panel.LazyImagePanel;
import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;
import module.training.ui.model.TrainingModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.temporal.ChronoUnit;
import java.util.Vector;


public class TrainingRecapTable extends JScrollPane {

//    static final int fixedColumns = 5;
    private final FutureTrainingPrioPopup trainingPrioPopUp;

    private final PlayersTable trainingPredictionTable;
//    private final JTable fixed;
//    private final JTable scrollTable;

    private final TrainingModel trainingModel;

    /**
     * Get Columns name
     *
     * @return List of string
     */
    Vector<String> getColumns() {
        var columns = new Vector<String>();
        columns.add(TranslationFacility.tr("Spieler"));
        columns.add(TranslationFacility.tr("ls.player.age"));
        columns.add(TranslationFacility.tr("BestePosition"));
        columns.add("Speed");
        columns.add(TranslationFacility.tr("ls.player.id"));

        var actualWeek = HOVerwaltung.instance().getModel().getBasics().getHattrickWeek();

        // We are in the middle where season has not been updated!
        try {
            if (HOVerwaltung.instance().getModel().getXtraDaten().getNextTrainingDate()
                    .isAfter(HOVerwaltung.instance().getModel().getXtraDaten().getSeriesMatchDate())) {
                actualWeek = actualWeek.plus(7, ChronoUnit.DAYS);
            }
        } catch (Exception e1) {
            // Null when first time HO is launched
        }

        for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
            var htweek = actualWeek.toLocaleHTWeek();
            columns.add(htweek.season + " " + htweek.week);
            actualWeek = actualWeek.plus(7, ChronoUnit.DAYS);
        }

        columns.add(TranslationFacility.tr("ls.player.id"));
        return columns;
    }

//    TableModel createTableModel() {
//        Vector<String> columns = getColumns();
//        var rows = createRows();
//        BaseTableModel tableModel = new BaseTableModel(new Vector<>(), columns);
//        // and add them to the model
//        for (Vector<String> row : rows) {
//            tableModel.addRow(row);
//        }
//        return tableModel;
//    }
//
    public void refresh() {
        var model = (TrainingPredictionTableModel)this.trainingPredictionTable.getModel();
        model.initData();
    }
//
//    private void deleteRows(JTable table) {
//        var model = (DefaultTableModel)table.getModel();
//        model.setNumRows(0);
//    }
//
//    private List<Vector<String>> createRows() {
//        Vector<String> columns = getColumns();
//        List<Vector<String>> rows = new ArrayList<>();
//        List<Player> players = HOVerwaltung.instance().getModel().getCurrentPlayers();
//
//        for (Player player : players) {
//            FutureTrainingManager ftm = new FutureTrainingManager(player,
//                    this.trainingModel.getFutureTrainings());
//            var skillChanges = ftm.getFutureSkillups();
//
//            HashMap<String, SkillChange> maps = new HashMap<>();
//            for (var s : skillChanges) {
//                maps.put(s.getHtSeason() + " " + s.getHtWeek(), s);
//            }
//
//            Vector<String> row = new Vector<>();
//            row.add(player.getFullName());
//            row.add(player.getAgeWithDaysAsString());
//            byte bIdealPosition = player.getIdealPosition();
//            row.add(String.format(MatchRoleID.getNameForPosition(bIdealPosition) + " (%.2f)", player.getIdealPositionRating()));
//            row.add(Integer.toString((int) ftm.getTrainingSpeed()));
//            row.add(Integer.toString(player.getPlayerId()));
//
//            for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
//                var s = maps.get(columns.get(i + fixedColumns));
//
//                if (s == null) {
//                    row.add("");
//                } else {
//                    row.add(s.getType().toInt() + " " + s.getValue() + " " + s.getChange());
//                }
//            }
//
//            row.add(Integer.toString(player.getPlayerId()));
//            rows.add(row);
//        }
//
//        // Sort the players
//        rows.sort(new TrainingComparator(3, fixedColumns));
//        return rows;
//    }
//
//    /**
//     * Fixed table renderer to add special background colors depending on training speed
//     */
//    private static class FixedTrainingRecapRenderer extends DefaultTableCellRenderer {
//
//        /* (non-Javadoc)
//         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, Object, boolean, boolean, int, int)
//         */
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
//                                                       boolean hasFocus, int row, int column) {
//            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//
//            this.setIcon(null);
//            this.setToolTipText("");
//
//            if (column == 0) {
//                String tooltip;
//                int playerId = Integer.parseInt((String) table.getValueAt(row, table.getColumnCount() - 1));
//                Player player = HOVerwaltung.instance().getModel().getCurrentPlayer(playerId);
//
//                if ( player != null ) {
//                    this.setOpaque(true);
//                    this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
//                    this.setText(player.getFullName());
//
//                    tooltip = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getText();
//                    if (tooltip == null) {
//                        tooltip = "";
//                    }
//                    this.setToolTipText(tooltip);
//                    this.setIcon(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getIcon());
//                }
//            }
//
//            if (!isSelected) {
//                int speed = Integer.parseInt((String) table.getValueAt(row, 3));
//                Color bg_color;
//                // Speed range is 16 to 125
//                if (speed > (125 + 50) / 2) {
//                    bg_color = ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
//                } else if (speed > (50 + 16) / 2) {
//                    bg_color = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
//                } else {
//                    bg_color = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
//                }
//
//                setBackground(bg_color);
//            }
//            return this;
//        }
//    }

    /**
     * Creates a new TrainingRecapTable object.
     *
     * @param model         training model
     */
    public TrainingRecapTable(LazyImagePanel panel, TrainingModel model) {
        trainingModel = model;

//        var table = new JTable(createTableModel());
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        TableColumnModel columnModel = table.getColumnModel();
//        columnModel.setColumnSelectionAllowed(true);
//        ListSelectionModel columnSelectionModel = columnModel.getSelectionModel();
//        columnSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//        ListSelectionModel rowSelectionModel = table.getSelectionModel();
//        rowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

//        this.setViewportView(table);

        var tableModel = UserColumnController.instance().getTrainingPredictionTableModel();
        tableModel.setTrainingModel(this.trainingModel);
        this.trainingPredictionTable = new PlayersTable(tableModel, 5);
        this.setViewportView(this.trainingPredictionTable.getContainerComponent());
        this.trainingPredictionTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        scrollTable = table;
//        fixed = new JTable(scrollTable.getModel());
//        fixed.setFocusable(false);
//        fixed.setSelectionModel(scrollTable.getSelectionModel());
//        fixed.getTableHeader().setReorderingAllowed(false);
//        fixed.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); //$NON-NLS-1$
//        scrollTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); //$NON-NLS-1$
//
//        //  Remove the fixed columns from the main table
//        for (int i = 0; i < fixedColumns; i++) {
//            TableColumnModel _columnModel = scrollTable.getColumnModel();
//
//            _columnModel.removeColumn(_columnModel.getColumn(0));
//        }
//
//        scrollTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        fixed.setSelectionModel(scrollTable.getSelectionModel());
//
//        //  Remove the non-fixed columns from the fixed table
//        while (fixed.getColumnCount() > fixedColumns) {
//            TableColumnModel _columnModel = fixed.getColumnModel();
//
//            _columnModel.removeColumn(_columnModel.getColumn(fixedColumns));
//        }
//
//        var fontSize = UserParameter.instance().fontSize;
//        fixed.getColumnModel().getColumn(0).setMaxWidth(12*fontSize);
//        fixed.getColumnModel().getColumn(0).setMinWidth(12*fontSize);
//        fixed.getColumnModel().getColumn(0).setWidth(12*fontSize);
//        fixed.getColumnModel().getColumn(1).setMaxWidth(5*fontSize);
//        fixed.getColumnModel().getColumn(1).setMinWidth(5*fontSize);
//        fixed.getColumnModel().getColumn(1).setWidth(5*fontSize);
//        fixed.getColumnModel().getColumn(2).setMaxWidth(17*fontSize);
//        fixed.getColumnModel().getColumn(2).setMinWidth(17*fontSize);
//        fixed.getColumnModel().getColumn(2).setPreferredWidth(17*fontSize);
//        fixed.getColumnModel().getColumn(3).setMaxWidth(0);
//        fixed.getColumnModel().getColumn(3).setMinWidth(0);
//        fixed.getColumnModel().getColumn(3).setPreferredWidth(0);
//        fixed.getColumnModel().getColumn(4).setMaxWidth(0);
//        fixed.getColumnModel().getColumn(4).setMinWidth(0);
//        fixed.getColumnModel().getColumn(4).setPreferredWidth(0);
//
//        //  Add the fixed table to the scroll pane
//        fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
//        setRowHeaderView(fixed);
//
//        fixed.setDefaultRenderer(Object.class, new FixedTrainingRecapRenderer());
//        scrollTable.setDefaultRenderer(Object.class, new TrainingRecapRenderer(this.trainingModel));
////        // Required for darklaf, see https://github.com/weisJ/darklaf/issues/164
////        scroll.setDefaultRenderer(String.class, new TrainingRecapRenderer(this.trainingModel));
//
//        setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, fixed.getTableHeader());
//
//        // Hide the last column
//        int lastSTCol = fixed.getColumnCount() - 1;
//        scrollTable.getTableHeader().getColumnModel().getColumn(lastSTCol).setPreferredWidth(0);
//        scrollTable.getTableHeader().getColumnModel().getColumn(lastSTCol).setMinWidth(0);
//        scrollTable.getTableHeader().getColumnModel().getColumn(lastSTCol).setMaxWidth(0);

//        JTable lockedTable = getLockedTable();
//        lockedTable.getSelectionModel().addListSelectionListener(
//                new PlayerSelectionListener(this.trainingModel, fixed, lastSTCol));

        this.trainingPredictionTable.getTableHeader().setReorderingAllowed(false);
        trainingPrioPopUp = new FutureTrainingPrioPopup(panel, model);
        this.trainingPredictionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (trainingPredictionTable.getSelectedRow() < 0)
                    return;

                if ( e.getComponent() instanceof JTable ) {
                    var cols = trainingPredictionTable.getSelectedColumns();
                    trainingPrioPopUp.setSelectedColumns(cols);
                    trainingPrioPopUp.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

//    /**
//     * Returns the Locked LeftTable
//     *
//     * @return Jtable
//     */
//    public JTable getLockedTable() {
//        return fixed;
//    }
//
//    /**
//     * Returns the Scrollable RightTable
//     *
//     * @return Jtable
//     */
//    public JTable getScrollTable() {
//        return scrollTable;
//    }
}
