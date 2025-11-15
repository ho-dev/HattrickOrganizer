package module.training.ui;

import core.gui.RefreshManager;
import core.gui.comp.panel.LazyImagePanel;
import core.model.TranslationFacility;
import module.training.ui.model.ModelChange;
import module.training.ui.model.TrainingModel;

import javax.swing.*;
import java.awt.*;

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
        update();
        addListeners();
    }

    private boolean isUpdating=false;
    @Override
    protected void update() {
        if ( !isUpdating) {
            isUpdating=true;
            this.recapTable.refresh();
            this.model.fireModelChanged(ModelChange.FUTURE_TRAINING);
            isUpdating = false;
        }
    }

    private void addListeners() {
        this.model.addModelChangeListener(
                e->this.update()
        );
        RefreshManager.instance().registerRefreshable(() -> {
            if (isShowing()) {
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

        JLabel title = new JLabel(TranslationFacility.tr("ls.module.training.training_prediction"),
                SwingConstants.CENTER);

        title.setOpaque(false);
        add(title, BorderLayout.NORTH);

        // Add legend panel.
        add(new TrainingLegendPanel(), BorderLayout.SOUTH);
        recapTable = new TrainingRecapTable( this, this.model);
        add(recapTable, BorderLayout.CENTER);
    }

    public void storeUserSettings() {
        if ( this.recapTable != null) {
            this.recapTable.storeUserSettings();
        }
    }
}
