// %776182880:hoplugins.trainingExperience.ui%
package module.training.ui;

import core.gui.IRefreshable;
import core.gui.RefreshManager;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.model.BaseTableModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.ISkillChange;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.training.FuturePlayerTraining;
import core.training.FutureTrainingManager;
import core.training.HattrickDate;
import module.training.ui.model.ModelChange;
import module.training.ui.model.ModelChangeListener;
import module.training.ui.model.TrainingModel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.*;

/**
 * Recap Panel when future preview of skillups is shown ("Prediction" tab,
 * "Training Recap" table").
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class TrainingRecapPanel extends LazyImagePanel implements ActionListener {

    private static final long serialVersionUID = 7240288702397251461L;
    private static final int fixedColumns = 5;
    private TrainingRecapTable recapTable;
    private final TrainingModel model;

    /**
     * Creates a new TrainingRecapPanel object.
     */
    public TrainingRecapPanel(TrainingModel model) {
        this.model = model;
    }

    @Override
    protected void initialize() {
        initComponents();
        addListeners();
        setNeedsRefresh(true);
    }

    @Override
    protected void update() {
        reload();
    }

    /**
     * Reload the panel
     */
    private void reload() {
        reAddTable();
    }

    private void addListeners() {
        RefreshManager.instance().registerRefreshable(() -> {
            if (isShowing()) {
                reload();
            }
        });

        this.model.addModelChangeListener(change -> {
            if (change == ModelChange.ACTIVE_PLAYER) {
                selectPlayerFromModel();
            } else {
                reload();
            }
        });
    }

    /**
     * Get Columns name
     *
     * @return List of string
     */
    private Vector<String> getColumns() {
        Vector<String> columns = new Vector<>();

        columns.add(HOVerwaltung.instance().getLanguageString("Spieler"));
        columns.add(HOVerwaltung.instance().getLanguageString("ls.player.age"));
        columns.add(HOVerwaltung.instance().getLanguageString("BestePosition"));
        columns.add("Speed");
        columns.add(HOVerwaltung.instance().getLanguageString("ls.player.id"));

        var actualWeek = HOVerwaltung.instance().getModel().getBasics().getHattrickWeek(); //.getSpieltag();

        // We are in the middle where season has not been updated!
        try {
            if (HOVerwaltung.instance().getModel().getXtraDaten().getTrainingDate()
                    .after(HOVerwaltung.instance().getModel().getXtraDaten().getSeriesMatchDate())) {
                actualWeek.addWeeks(1);
            }
        } catch (Exception e1) {
            // Null when first time HO is launched
        }

        for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
            // calculate the week and season of the future training
            //int week = (actualWeek + i) - 1;
            //int season = actualSeason + (week / 16);
            //week = (week % 16) + 1;
            columns.add(actualWeek.getSeason() + " " + actualWeek.getWeek());
            actualWeek.addWeeks(1);
        }

        columns.add(HOVerwaltung.instance().getLanguageString("ls.player.id"));

        return columns;
    }

    /**
     * Initialize the GUI
     */
    private void initComponents() {
        setOpaque(false);
        setLayout(new BorderLayout());

        setOpaque(false);

        JLabel title = new JLabel(HOVerwaltung.instance().getLanguageString("Recap"),
                SwingConstants.CENTER);

        title.setOpaque(false);
        add(title, BorderLayout.NORTH);

        // Add legend panel.
        add(new TrainingLegendPanel(), BorderLayout.SOUTH);
    }

    private void selectPlayerFromModel() {
        this.recapTable.getLockedTable().clearSelection();
        Player player = this.model.getActivePlayer();
        if (player != null) {
            for (int i = 0; i < this.recapTable.getLockedTable().getRowCount(); i++) {
                String name = (String) this.recapTable.getLockedTable().getValueAt(i, 0);
                if (player.getFullName().equals(name)) {
                    int viewIndex = this.recapTable.getLockedTable().convertRowIndexToView(i);
                    this.recapTable.getLockedTable().setRowSelectionInterval(viewIndex, viewIndex);
                    break;
                }
            }
        }
    }


    private JMenuItem fullTrainingMenuItem;
    private JMenuItem partialTrainingMenuItem;
    private JMenuItem osmosisTrainingMenuItem;
    private JMenuItem noTrainingMenuItem;
    private JPopupMenu trainingPrioPopUp;
    private JTable table;

    private void reAddTable() {
        if (recapTable != null) {
            remove(recapTable);
        }

        table = new JTable(createTableModel());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.setColumnSelectionAllowed(true);
        ListSelectionModel columnSelectionModel = columnModel.getSelectionModel();
        columnSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        ListSelectionModel rowSelectionModel = table.getSelectionModel();
        rowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        fullTrainingMenuItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("trainpre.fulltrain"));
        fullTrainingMenuItem.addActionListener(this);
        partialTrainingMenuItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("trainpre.partialtrain"));
        partialTrainingMenuItem.addActionListener(this);
        osmosisTrainingMenuItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("trainpre.osmosistrain"));
        osmosisTrainingMenuItem.addActionListener(this);
        noTrainingMenuItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("trainpre.notrain"));
        noTrainingMenuItem.addActionListener(this);
        trainingPrioPopUp = new JPopupMenu();
        trainingPrioPopUp.add(fullTrainingMenuItem);
        trainingPrioPopUp.add(partialTrainingMenuItem);
        trainingPrioPopUp.add(osmosisTrainingMenuItem);
        trainingPrioPopUp.add(noTrainingMenuItem);
        //table.setComponentPopupMenu(trainingPrioPopUp);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (table.getSelectedRow() < 0)
                    return;

                if ( e.getComponent() instanceof JTable ) {
                    var cols = table.getSelectedColumns();
                    partialTrainingMenuItem.setEnabled(model.isPartialTrainingAvailable(cols));
                    osmosisTrainingMenuItem.setEnabled(model.isOsmosisTrainingAvailable(cols));
                    trainingPrioPopUp.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        recapTable = new TrainingRecapTable(this.model, table, fixedColumns);

        // Hide the last column
        JTable scrollTable = recapTable.getScrollTable();
        int lastSTCol = scrollTable.getColumnCount() - 1;
        scrollTable.getTableHeader().getColumnModel().getColumn(lastSTCol).setPreferredWidth(0);
        scrollTable.getTableHeader().getColumnModel().getColumn(lastSTCol).setMinWidth(0);
        scrollTable.getTableHeader().getColumnModel().getColumn(lastSTCol).setMaxWidth(0);

        JTable lockedTable = recapTable.getLockedTable();
        lockedTable.getSelectionModel().addListSelectionListener(
                new PlayerSelectionListener(this.model, scrollTable, lastSTCol));
        recapTable.getScrollTable().getTableHeader().setReorderingAllowed(false);
        add(recapTable, BorderLayout.CENTER);
    }

    private TableModel createTableModel() {

        Vector<String> columns = getColumns();
        List<Player> list = HOVerwaltung.instance().getModel().getCurrentPlayers();
        List<Vector<String>> players = new ArrayList<>();

        for (Player player : list) {
            FutureTrainingManager ftm = new FutureTrainingManager(player,
                    this.model.getFutureTrainings(), 0,
                    this.model.getTrainerLevel(), this.model.getAssistants());
            List<ISkillChange> skillChanges = ftm.getFutureSkillups();

            HashMap<String, ISkillChange> maps = new HashMap<>();
            for ( var s: skillChanges){
                maps.put(s.getHtSeason() + " " + s.getHtWeek(), s);
            }

            Vector<String> row = new Vector<>();

            row.add(player.getFullName());
            row.add(player.getAlterWithAgeDaysAsString());
            byte bIdealPosition = player.getIdealPosition();
            row.add(MatchRoleID.getNameForPosition(bIdealPosition)
                    + " ("
                    +  player.getIdealPosStaerke(true, true, 1)
                    + "%)");
            row.add(Integer.toString(ftm.getTrainingSpeed()));
            row.add(Integer.toString(player.getSpielerID()));

            for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
                ISkillChange s = maps.get(columns.get(i + fixedColumns));

                if (s == null) {
                    row.add("");
                } else {
                    row.add(s.getType() + " " + s.getValue() + " " + s.getChange());
                }
            }

            row.add(Integer.toString(player.getSpielerID()));
            players.add(row);
        }

        // Sort the players
        players.sort(new TrainingComparator(3, fixedColumns));

        BaseTableModel tableModel = new BaseTableModel(new Vector<>(), columns);
        // and add them to the model
        for (Vector<String> row : players) {
            tableModel.addRow(row);
        }

        return tableModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Training Priority Popup Menu Actions
        var player = model.getActivePlayer();
        if (player == null) return;
        var cols = table.getSelectedColumns();
        if (cols == null || cols.length == 0) return;

        var from = getWeek(getColumns().get(cols[0]+fixedColumns));
        HattrickDate to = null;
        var toColNr = cols[cols.length - 1]+fixedColumns;
        if (toColNr < getColumns().size()) {
            to = getWeek(getColumns().get(toColNr));
        }

        FuturePlayerTraining.Priority prio = null;
        boolean isTrainingPrioItem = false;
        if (e.getSource().equals(fullTrainingMenuItem)) {
            prio = FuturePlayerTraining.Priority.FULL_TRAINING;
            isTrainingPrioItem = true;
        } else if (e.getSource().equals(partialTrainingMenuItem)) {
            prio = FuturePlayerTraining.Priority.PARTIAL_TRAINING;
            isTrainingPrioItem = true;
        } else if (e.getSource().equals(osmosisTrainingMenuItem)) {
            prio = FuturePlayerTraining.Priority.OSMOSIS_TRAINING;
            isTrainingPrioItem = true;
        } else if (e.getSource().equals(noTrainingMenuItem)) {
            isTrainingPrioItem = true;
        }
        if (isTrainingPrioItem) {
            player.setFutureTraining( prio, from, to);
            reload();
        }
    }

    private HattrickDate getWeek(String s) {
        return new HattrickDate(s);
    }
}
