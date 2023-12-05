package module.training.ui;

import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.BaseTableModel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.training.FutureTrainingManager;
import core.model.player.SkillChange;
import core.training.TrainingPreviewPlayers;
import module.training.ui.model.TrainingModel;
import module.training.ui.renderer.TrainingRecapRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


public class TrainingRecapTable extends JScrollPane {

    static final int fixedColumns = 5;
    private final FutureTrainingPrioPopup trainingPrioPopUp;
    private final JTable fixed;
    private final JTable scroll;

    private final TrainingModel trainingModel;

    /**
     * Get Columns name
     *
     * @return List of string
     */
    Vector<String> getColumns() {
        var columns = new Vector<String>();
        columns.add(HOVerwaltung.instance().getLanguageString("Spieler"));
        columns.add(HOVerwaltung.instance().getLanguageString("ls.player.age"));
        columns.add(HOVerwaltung.instance().getLanguageString("BestePosition"));
        columns.add("Speed");
        columns.add(HOVerwaltung.instance().getLanguageString("ls.player.id"));

        var actualWeek = HOVerwaltung.instance().getModel().getBasics().getHattrickWeek(); //.getSpieltag();

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

        columns.add(HOVerwaltung.instance().getLanguageString("ls.player.id"));
        return columns;
    }

    TableModel createTableModel() {
        Vector<String> columns = getColumns();
        var rows = createRows();
        BaseTableModel tableModel = new BaseTableModel(new Vector<>(), columns);
        // and add them to the model
        for (Vector<String> row : rows) {
            tableModel.addRow(row);
        }
        return tableModel;
    }

    public void refresh() {
        deleteRows(scroll);
        deleteRows(fixed);
        var rows = createRows();
        var model = (DefaultTableModel)scroll.getModel();
        for (var row: rows) {
            model.addRow(row);
        }
    }

    private void deleteRows(JTable table) {
        var model = (DefaultTableModel)table.getModel();
        model.setNumRows(0);
    }

    private List<Vector<String>> createRows() {
        Vector<String> columns = getColumns();
        List<Vector<String>> rows = new ArrayList<>();
        List<Player> players = HOVerwaltung.instance().getModel().getCurrentPlayers();

        for (Player player : players) {
            FutureTrainingManager ftm = new FutureTrainingManager(player,
                    this.trainingModel.getFutureTrainings());
            var skillChanges = ftm.getFutureSkillups();

            HashMap<String, SkillChange> maps = new HashMap<>();
            for ( var s: skillChanges){
                maps.put(s.getHtSeason() + " " + s.getHtWeek(), s);
            }

            Vector<String> row = new Vector<>();

            row.add(player.getFullName());
            row.add(player.getAgeWithDaysAsString());
            byte bIdealPosition = player.getIdealPosition();
            row.add(MatchRoleID.getNameForPosition(bIdealPosition)
                    + " ("
                    +  player.getIdealPositionRating()
                    + ")");
            row.add(Integer.toString(ftm.getTrainingSpeed()));
            row.add(Integer.toString(player.getPlayerId()));

            for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
                var s = maps.get(columns.get(i + fixedColumns));

                if (s == null) {
                    row.add("");
                } else {
                    row.add(s.getType().toInt() + " " + s.getValue() + " " + s.getChange());
                }
            }

            row.add(Integer.toString(player.getPlayerId()));
            rows.add(row);
        }

        // Sort the players
        rows.sort(new TrainingComparator(3, fixedColumns));
        return rows;
    }

    /**
     * Fixed table renderer to add special background colors depending on training speed
     */
    private static class FixedTrainingRecapRenderer extends DefaultTableCellRenderer {

        /* (non-Javadoc)
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            this.setIcon(null);
            this.setToolTipText("");

            if (column == 0) {
                String tooltip;
                int playerId = Integer.parseInt((String) table.getValueAt(row, table.getColumnCount() - 1));
                Player player = HOVerwaltung.instance().getModel().getCurrentPlayer(playerId);

                if ( player != null ) {
                    this.setOpaque(true);
                    this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
                    this.setText(player.getFullName());

                    tooltip = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getText();
                    if (tooltip == null) {
                        tooltip = "";
                    }
                    this.setToolTipText(tooltip);
                    this.setIcon(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getIcon());
                }
            }
            
            if (isSelected) {
                return this;
            }

            int speed = Integer.parseInt((String) table.getValueAt(row, 3));
            Color bg_color;
            // Speed range is 16 to 125
            if (speed > (125 + 50) / 2) {
                bg_color = ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
            } else if (speed > (50 + 16) / 2) {
                bg_color = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
            } else {
                bg_color = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
            }

            setBackground(bg_color);
            return this;
        }
    }

    /**
     * Creates a new TrainingRecapTable object.
     *
     * @param model         training model
     */
    public TrainingRecapTable(LazyImagePanel panel, TrainingModel model) {
        trainingModel = model;

        var table = new JTable(createTableModel());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.setColumnSelectionAllowed(true);
        ListSelectionModel columnSelectionModel = columnModel.getSelectionModel();
        columnSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        ListSelectionModel rowSelectionModel = table.getSelectionModel();
        rowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.setViewportView(table);

        scroll = table;
        fixed = new JTable(scroll.getModel());
        fixed.setFocusable(false);
        fixed.setSelectionModel(scroll.getSelectionModel());
        fixed.getTableHeader().setReorderingAllowed(false);
        fixed.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); //$NON-NLS-1$
        scroll.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); //$NON-NLS-1$

        //  Remove the fixed columns from the main table
        for (int i = 0; i < fixedColumns; i++) {
            TableColumnModel _columnModel = scroll.getColumnModel();

            _columnModel.removeColumn(_columnModel.getColumn(0));
        }

        scroll.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fixed.setSelectionModel(scroll.getSelectionModel());

        //  Remove the non-fixed columns from the fixed table
        while (fixed.getColumnCount() > fixedColumns) {
            TableColumnModel _columnModel = fixed.getColumnModel();

            _columnModel.removeColumn(_columnModel.getColumn(fixedColumns));
        }

        fixed.getColumnModel().getColumn(0).setMaxWidth(150);
        fixed.getColumnModel().getColumn(0).setMinWidth(150);
        fixed.getColumnModel().getColumn(0).setWidth(150);
        fixed.getColumnModel().getColumn(1).setMaxWidth(60);
        fixed.getColumnModel().getColumn(1).setMinWidth(60);
        fixed.getColumnModel().getColumn(1).setWidth(60);
        fixed.getColumnModel().getColumn(2).setMaxWidth(200);
        fixed.getColumnModel().getColumn(2).setMinWidth(200);
        fixed.getColumnModel().getColumn(2).setPreferredWidth(200);
        fixed.getColumnModel().getColumn(3).setMaxWidth(0);
        fixed.getColumnModel().getColumn(3).setMinWidth(0);
        fixed.getColumnModel().getColumn(3).setPreferredWidth(0);
        fixed.getColumnModel().getColumn(4).setMaxWidth(0);
        fixed.getColumnModel().getColumn(4).setMinWidth(0);
        fixed.getColumnModel().getColumn(4).setPreferredWidth(0);

        //  Add the fixed table to the scroll pane
        fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
        setRowHeaderView(fixed);

        fixed.setDefaultRenderer(Object.class, new FixedTrainingRecapRenderer());
        scroll.setDefaultRenderer(Object.class, new TrainingRecapRenderer(this.trainingModel));
//        // Required for darklaf, see https://github.com/weisJ/darklaf/issues/164
//        scroll.setDefaultRenderer(String.class, new TrainingRecapRenderer(this.trainingModel));

        setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, fixed.getTableHeader());

        // Hide the last column
        JTable scrollTable = getScrollTable();
        int lastSTCol = scrollTable.getColumnCount() - 1;
        scrollTable.getTableHeader().getColumnModel().getColumn(lastSTCol).setPreferredWidth(0);
        scrollTable.getTableHeader().getColumnModel().getColumn(lastSTCol).setMinWidth(0);
        scrollTable.getTableHeader().getColumnModel().getColumn(lastSTCol).setMaxWidth(0);

        JTable lockedTable = getLockedTable();
        lockedTable.getSelectionModel().addListSelectionListener(
                new PlayerSelectionListener(this.trainingModel, scrollTable, lastSTCol));
        getScrollTable().getTableHeader().setReorderingAllowed(false);
        trainingPrioPopUp = new FutureTrainingPrioPopup(panel, model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (table.getSelectedRow() < 0)
                    return;

                if ( e.getComponent() instanceof JTable ) {
                    var cols = table.getSelectedColumns();
                    trainingPrioPopUp.setSelectedColumns(cols);
                    trainingPrioPopUp.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Returns the Locked LeftTable
     *
     * @return Jtable
     */
    public JTable getLockedTable() {
        return fixed;
    }

    /**
     * Returns the Scrollable RightTable
     *
     * @return Jtable
     */
    public JTable getScrollTable() {
        return scroll;
    }
}
