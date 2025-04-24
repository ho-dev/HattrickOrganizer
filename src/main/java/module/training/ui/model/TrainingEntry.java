package module.training.ui.model;

import core.model.player.Player;
import core.training.FutureTrainingManager;
import core.util.HODateTime;
import module.training.ui.comp.TrainingPriorityCell;

public class TrainingEntry {
    private FutureTrainingManager futureTrainingManager;

    public Player getPlayer() {
        return this.futureTrainingManager.getPlayer();
    }

    public double getTrainingSpeed() {
        return this.futureTrainingManager.getTrainingSpeed();
    }

    public String getTrainingPriority()
    {
        var firstTrainingDate = this.futureTrainingManager.getFutureTrainings().isEmpty() ?
                HODateTime.now() :
                this.futureTrainingManager.getFutureTrainings().get(0).getTrainingDate();
        return getPlayer().getTrainingPriorityInformation(firstTrainingDate);
    }
}
