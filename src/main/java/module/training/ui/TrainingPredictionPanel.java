package module.training.ui;

import core.gui.RefreshManager;
import core.gui.comp.panel.LazyImagePanel;
import core.model.HOVerwaltung;
import core.model.player.Player;
import module.training.ui.model.ModelChange;
import module.training.ui.model.TrainingModel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;


public class TrainingPredictionPanel extends LazyImagePanel  {

    private TrainingRecapTable recapTable;
    private final TrainingModel model;

    /**
     * Creates a new TrainingRecapPanel object.
     */
    public TrainingPredictionPanel(TrainingModel model) {
        this.model = model;
    }

    @Override
    protected void initialize() {
        initComponents();
        addListeners();
//        setNeedsRefresh(true);
    }

    @Override
    protected void update() {
        this.recapTable.refresh();
    }

    /**
     * Reload the panel
    private void reload() {
        addRecapTable();
    }
     */

    private void addListeners() {
        RefreshManager.instance().registerRefreshable(() -> {
            if (isShowing()) {
                update();
            }
        });

        this.model.addModelChangeListener(change -> {
            if (change == ModelChange.ACTIVE_PLAYER) {
                selectPlayerFromModel();
            } else {
                update();
            }
        });
    }

    /**
     * Initialize the GUI
     */
    private void initComponents() {
        setOpaque(false);
        setLayout(new BorderLayout());

        setOpaque(false);

        JLabel title = new JLabel(HOVerwaltung.instance().getLanguageString("ls.module.training.training_prediction"),
                SwingConstants.CENTER);

        title.setOpaque(false);
        add(title, BorderLayout.NORTH);

        // Add legend panel.
        add(new TrainingLegendPanel(), BorderLayout.SOUTH);
        recapTable = new TrainingRecapTable( this, this.model);
        add(recapTable, BorderLayout.CENTER);
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
}
