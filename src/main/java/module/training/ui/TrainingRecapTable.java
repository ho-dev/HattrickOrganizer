package module.training.ui;

import core.gui.comp.panel.LazyImagePanel;
import core.gui.comp.table.PlayersTable;
import core.gui.model.UserColumnController;
import module.training.ui.model.TrainingModel;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TrainingRecapTable extends JScrollPane {

    private final FutureTrainingPrioPopup trainingPrioPopUp;

    private final PlayersTable trainingPredictionTable;

    public void storeUserSettings(){
        var tableModel = (TrainingPredictionTableModel)trainingPredictionTable.getModel();
        if ( tableModel != null) {
            tableModel.storeUserSettings();
        }
    }

    public void refresh() {
        var tableModel = (TrainingPredictionTableModel)trainingPredictionTable.getModel();
        tableModel.initData();
        this.trainingPredictionTable.initSelection();
    }

    /**
     * Creates a new TrainingRecapTable object.
     *
     * @param model         training model
     */
    public TrainingRecapTable(LazyImagePanel panel, TrainingModel model) {
        var tableModel = UserColumnController.instance().getTrainingPredictionTableModel();
        tableModel.setTrainingModel(model);
        this.trainingPredictionTable = new PlayersTable(tableModel, 3);
        this.setViewportView(this.trainingPredictionTable.getContainerComponent());
        this.trainingPredictionTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
}
