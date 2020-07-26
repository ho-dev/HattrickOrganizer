// %3839090226:hoplugins.trainingExperience.ui%
package module.training.ui;

import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.NumericDocument;
import core.gui.comp.panel.LazyImagePanel;
import core.model.HOVerwaltung;
import core.model.match.MatchType;
import core.model.player.Player;
import core.net.OnlineWorker;
import core.training.TrainingManager;
import core.util.Helper;
import core.util.HelperWrapper;
import core.util.StringUtils;
import module.training.ui.model.ModelChange;
import module.training.ui.model.ModelChangeListener;
import module.training.ui.model.OutputTableModel;
import module.training.ui.model.TrainingModel;
import module.training.ui.renderer.OutputTableRenderer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * The Panel where the main training table is shown ("Training").
 *
 * <p>
 * TODO Costomize to show only players that received training?
 * </p>
 *
 * <p>
 * TODO Maybe i want to test for players that haven't received trainings to
 * preview effect of change of training.
 * </p>
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class OutputPanel extends LazyImagePanel {

    private static final long serialVersionUID = 7955126207696897546L;
    private JTable outputTable;
    private JButton importButton;
    private JButton calculateButton;
    private final TrainingModel model;
    private FutureTrainingPrioPopup trainingPrioPopUp;

    /**
     * Creates a new OutputPanel object.
     */
    public OutputPanel(TrainingModel model) {
        super();
        this.model = model;
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
        ((OutputTableModel) outputTable.getModel()).fillWithData();
        trainingPrioPopUp = new FutureTrainingPrioPopup(this, model);
        outputTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (outputTable.getSelectedRow() < 0)
                    return;
                if (e.getComponent() instanceof JTable) {
                    trainingPrioPopUp.updateActivePlayer();
                    trainingPrioPopUp.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Import a match from Hattrick
     */
    @SuppressWarnings("deprecation")
    private void importMatches() {

        JTextField tf = new JTextField();
        tf.setDocument(new NumericDocument(10));
        Object[] objs = {HOVerwaltung.instance().getLanguageString("ls.match.id"), tf};

        int value = JOptionPane.showConfirmDialog(HOMainFrame.instance(), objs, HOVerwaltung
                .instance().getLanguageString("ImportMatch"), JOptionPane.OK_CANCEL_OPTION);

        String input = tf.getText();
        if (value == JOptionPane.YES_OPTION && !StringUtils.isEmpty(input)) {

            Integer matchID = Integer.valueOf(input);

            if (HelperWrapper.instance().isUserMatch(input, MatchType.LEAGUE)) {
                if (OnlineWorker.downloadMatchData(matchID, MatchType.LEAGUE, false)) {
                    Helper.showMessage(null,
                            HOVerwaltung.instance().getLanguageString("MatchImported"),
                            HOVerwaltung.instance().getLanguageString("ImportOK"), 1);
                    RefreshManager.instance().doRefresh();
                }
            } else {
                Helper.showMessage(null, HOVerwaltung.instance().getLanguageString("NotUserMatch"),
                        HOVerwaltung.instance().getLanguageString("ImportError"), 1);
            }
        }
    }

    private void addListeners() {
        this.outputTable.getSelectionModel().addListSelectionListener(
                new PlayerSelectionListener(this.model, this.outputTable,
                        OutputTableModel.COL_PLAYER_ID));

        this.importButton.addActionListener(arg0 -> importMatches());

        this.calculateButton.addActionListener(arg0 -> {
            // recalcSubskills() causes UI update via RefreshManager, so no
            // need to update UI ourself
            TrainingManager.instance().recalcSubskills(true);
        });

        this.model.addModelChangeListener(change -> {
            if (change == ModelChange.ACTIVE_PLAYER) {
                selectPlayerFromModel();
            }
        });
    }

    private void selectPlayerFromModel() {
        this.outputTable.clearSelection();
        Player player = this.model.getActivePlayer();
        if (player != null) {
            OutputTableModel tblModel = (OutputTableModel) this.outputTable.getModel();
            for (int i = 0; i < tblModel.getRowCount(); i++) {
                String val = (String) tblModel.getValueAt(i, OutputTableModel.COL_PLAYER_ID);
                int id = Integer.parseInt(val);
                if (player.getSpielerID() == id) {
                    int viewIndex = this.outputTable.convertRowIndexToView(i);
                    this.outputTable.setRowSelectionInterval(viewIndex, viewIndex);
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

        outputTable = new OutputTable(new OutputTableModel(this.model));
        outputTable.getTableHeader().setReorderingAllowed(false);
        outputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outputTable.setDefaultRenderer(Object.class, new OutputTableRenderer());

        for (int i = 0; i < outputTable.getColumnCount(); i++) {
            TableColumn column = outputTable.getColumnModel().getColumn(i);

            switch (i) {
                case 0 -> column.setPreferredWidth(150);
                case 1 -> column.setPreferredWidth(60);
                case 2 -> column.setPreferredWidth(140);
                default -> column.setPreferredWidth(70);
            }
        }

        // Hide column 11 (playerId)
        TableColumn playerIDCol = outputTable.getTableHeader().getColumnModel().getColumn(11);
        playerIDCol.setPreferredWidth(0);
        playerIDCol.setMinWidth(0);
        playerIDCol.setMaxWidth(0);

        // Hide column 12 (training speed)
        playerIDCol = outputTable.getTableHeader().getColumnModel().getColumn(12);
        playerIDCol.setPreferredWidth(0);
        playerIDCol.setMinWidth(0);
        playerIDCol.setMaxWidth(0);

        outputTable.setAutoResizeMode(0);
        outputTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        outputTable.setAutoCreateRowSorter(true);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(outputTable.getModel());
        outputTable.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();

        int columnIndexToSort = 12;
        sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));

        sorter.setSortKeys(sortKeys);
        sorter.sort();

        add(new JScrollPane(outputTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());

        this.importButton = new JButton(HOVerwaltung.instance().getLanguageString("ImportMatch"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(6, 8, 6, 4);
        buttonPanel.add(this.importButton, gbc);

        this.calculateButton = new JButton(HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation"));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 4, 6, 8);
        buttonPanel.add(this.calculateButton, gbc);

        add(buttonPanel, BorderLayout.NORTH);
    }

    private class OutputTable extends JTable {
        private static final long serialVersionUID = 1089805262735794338L;

        public OutputTable(TableModel dm) {
            super(dm);
        }

        @Override
        public String getToolTipText(MouseEvent e) {
            OutputTableModel tableModel = (OutputTableModel) getModel();
            Point p = e.getPoint();
            int realColumnIndex = convertColumnIndexToModel(columnAtPoint(p));
            int realRowIndex = convertRowIndexToModel(rowAtPoint(p));

            if (realColumnIndex == 0) {
                Object obj = tableModel.getToolTipAt(realRowIndex, realColumnIndex);
                return obj.toString();
            }

            if ((realColumnIndex > 2) && (realColumnIndex < 11)) {
                Object obj = tableModel.getToolTipAt(realRowIndex, realColumnIndex);

                return obj.toString();
            }

            return "";
        }
    }
}
