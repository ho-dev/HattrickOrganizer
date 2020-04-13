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
import core.training.FutureTrainingManager;
import module.training.ui.model.ModelChange;
import module.training.ui.model.ModelChangeListener;
import module.training.ui.model.TrainingModel;
import module.training.ui.renderer.TrainingRecapRenderer;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;

/**
 * Recap Panel when future preview of skillups is shown ("Prediction" tab,
 * "Training Recap" table").
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class TrainingRecapPanel extends LazyImagePanel {

    private static final long serialVersionUID = 7240288702397251461L;
    private static final int fixedColumns = 5;
    private TrainingRecapTable recapTable;
    private final TrainingModel model;
    private boolean initialized = false;
    private boolean needsRefresh = false;

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
        RefreshManager.instance().registerRefreshable(new IRefreshable() {

            @Override
            public void refresh() {
                if (isShowing()) {
                    reload();
                } else {
                    needsRefresh = true;
                }
            }
        });

        this.model.addModelChangeListener(new ModelChangeListener() {

            @Override
            public void modelChanged(ModelChange change) {
                if (change == ModelChange.ACTIVE_PLAYER) {
                    selectPlayerFromModel();
                } else {
                    reload();
                }
            }
        });
    }

    /**
     * Get Columns name
     *
     * @return List of string
     */
    private Vector<String> getColumns() {
        Vector<String> columns = new Vector<String>();

        columns.add(HOVerwaltung.instance().getLanguageString("Spieler"));
        columns.add(HOVerwaltung.instance().getLanguageString("ls.player.age"));
        columns.add(HOVerwaltung.instance().getLanguageString("BestePosition"));
        columns.add("Speed");
        columns.add(HOVerwaltung.instance().getLanguageString("ls.player.id"));

        int actualSeason = HOVerwaltung.instance().getModel().getBasics().getSeason();
        int actualWeek = HOVerwaltung.instance().getModel().getBasics().getSpieltag();

        // We are in the middle where season has not been updated!
        try {
            if (HOVerwaltung.instance().getModel().getXtraDaten().getTrainingDate()
                    .after(HOVerwaltung.instance().getModel().getXtraDaten().getSeriesMatchDate())) {
                actualWeek++;

                if (actualWeek == 17) {
                    actualWeek = 1;
                    actualSeason++;
                }
            }
        } catch (Exception e1) {
            // Null when first time HO is launched
        }

        for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
            // calculate the week and season of the future training
            int week = (actualWeek + i) - 1;
            int season = actualSeason + (week / 16);
            week = (week % 16) + 1;
            columns.add(season + " " + week);
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
                if (player.getName().equals(name)) {
                    int viewIndex = this.recapTable.getLockedTable().convertRowIndexToView(i);
                    this.recapTable.getLockedTable().setRowSelectionInterval(viewIndex, viewIndex);
                    break;
                }
            }
        }
    }

    private void reAddTable() {
        if (recapTable != null) {
            remove(recapTable);
        }

        JTable table = new JTable(createTableModel());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        recapTable = new TrainingRecapTable(table, fixedColumns);
        recapTable.getScrollTable().setDefaultRenderer(Object.class, new TrainingRecapRenderer());

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
        List<Player> list = HOVerwaltung.instance().getModel().getAllSpieler();
        List<Vector<String>> players = new ArrayList<Vector<String>>();

        for (Player player : list) {
            FutureTrainingManager ftm = new FutureTrainingManager(player,
                    this.model.getFutureTrainings(), 0,
                    this.model.getTrainerLevel(), this.model.getAssistants());
            List<ISkillChange> su = ftm.getFutureSkillups();

            // Skip player!
            if (su.size() == 0) {
                continue;
            }

            HashMap<String, ISkillChange> maps = new HashMap<String, ISkillChange>();

            for (Iterator<ISkillChange> iterator = su.iterator(); iterator.hasNext(); ) {
                ISkillChange skillup = iterator.next();
                maps.put(skillup.getHtSeason() + " " + skillup.getHtWeek(), skillup);
            }

            Vector<String> row = new Vector<String>();

            row.add(player.getName());
            row.add(player.getAlterWithAgeDaysAsString());
            byte bIdealPosition = player.getIdealPosition();
            row.add(MatchRoleID.getNameForPosition(bIdealPosition)
                    + " ("
                    +  player.getIdealPosStaerke(true, true, 1)
                    + "%)");
            row.add(Integer.toString(ftm.getTrainingSpeed()));
            row.add(Integer.toString(player.getSpielerID()));

            for (int i = 0; i < UserParameter.instance().futureWeeks; i++) {
                ISkillChange s = (ISkillChange) maps.get(columns.get(i + fixedColumns));

                if (s == null) {
                    row.add("");
                } else {
                    row.add(s.getType() + " " + s.getValue());
                }
            }

            row.add(Integer.toString(player.getSpielerID()));
            players.add(row);
        }

        // Sort the players
        Collections.sort(players, new TrainingComparator(3, fixedColumns));

        BaseTableModel tableModel = new BaseTableModel(new Vector<Object>(), columns);
        // and add them to the model
        for (Vector<String> row : players) {
            tableModel.addRow(row);
        }

        return tableModel;
    }
}
