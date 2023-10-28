package module.training.ui;

import core.gui.comp.panel.LazyImagePanel;
import core.model.HOVerwaltung;
import core.training.FuturePlayerTraining;
import core.util.HODateTime;
import core.util.HOLogger;
import module.training.ui.model.TrainingModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FutureTrainingPrioPopup  extends JPopupMenu implements ActionListener {

    private final JMenuItem fullTrainingMenuItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("trainpre.fulltrain"));
    private final JMenuItem partialTrainingMenuItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("trainpre.partialtrain"));
    private final JMenuItem osmosisTrainingMenuItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("trainpre.osmosistrain"));
    private final JMenuItem noTrainingMenuItem = new JMenuItem(HOVerwaltung.instance().getLanguageString("trainpre.notrain"));
    private final JMenuItem bestPositionTrainingMenuItem = new JMenuItem("");

    private final LazyImagePanel panel;
    private final TrainingModel model;
    private int[] cols;

    public FutureTrainingPrioPopup(LazyImagePanel panel, TrainingModel model ) {
        this.panel = panel;
        this.model = model;
        fullTrainingMenuItem.addActionListener(this);
        partialTrainingMenuItem.addActionListener(this);
        partialTrainingMenuItem.setEnabled(model.isPartialTrainingAvailable());
        osmosisTrainingMenuItem.addActionListener(this);
        osmosisTrainingMenuItem.setEnabled(model.isOsmosisTrainingAvailable());
        noTrainingMenuItem.addActionListener(this);
        bestPositionTrainingMenuItem.addActionListener(this);
        this.add(fullTrainingMenuItem);
        this.add(partialTrainingMenuItem);
        this.add(osmosisTrainingMenuItem);
        this.add(bestPositionTrainingMenuItem);
        this.add(noTrainingMenuItem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Training Priority Popup Menu Actions
        var player = model.getActivePlayer();
        if (player == null) return;

        HODateTime from;
        HODateTime to = null;   // forever
        var futureTrainings = model.getFutureTrainings();

        if (futureTrainings == null || futureTrainings.isEmpty()) {
            HOLogger.instance().warning(FutureTrainingPrioPopup.class, "Empty future trainings.");
            return;
        }

        if (cols == null) {
            from = futureTrainings.get(0).getTrainingDate();
        }
        else {
            from = futureTrainings.get(cols[0]).getTrainingDate();
            var toColNr = cols[cols.length - 1];
            if ( toColNr < futureTrainings.size()-1){
                to = futureTrainings.get(toColNr).getTrainingDate();
            }
        }

        if (e.getSource().equals(fullTrainingMenuItem)) {
            player.setFutureTraining( FuturePlayerTraining.Priority.FULL_TRAINING, from, to);
        } else if (e.getSource().equals(partialTrainingMenuItem)) {
            player.setFutureTraining( FuturePlayerTraining.Priority.PARTIAL_TRAINING, from, to);
        } else if (e.getSource().equals(osmosisTrainingMenuItem)) {
            player.setFutureTraining( FuturePlayerTraining.Priority.OSMOSIS_TRAINING, from, to);
        } else if (e.getSource().equals(noTrainingMenuItem)) {
            player.setFutureTraining( FuturePlayerTraining.Priority.NO_TRAINING, from, to);
        } else if (e.getSource().equals(bestPositionTrainingMenuItem)) {
            player.setFutureTraining( null, from, to);  // reset best position
        }

        panel.setNeedsRefresh(true);
    }

    public void setSelectedColumns(int[] cols) {
        this.cols = cols;
        partialTrainingMenuItem.setEnabled(model.isPartialTrainingAvailable(cols));
        osmosisTrainingMenuItem.setEnabled(model.isOsmosisTrainingAvailable(cols));
        updateActivePlayer();
    }

    public void updateActivePlayer() {
        if ( model.getActivePlayer() != null) {
            bestPositionTrainingMenuItem.setText(model.getActivePlayer().getBestPositionInfo());
        }
    }
}
