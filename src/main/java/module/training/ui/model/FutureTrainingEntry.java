package module.training.ui.model;

import core.model.player.Player;
import core.model.player.SkillChange;
import core.training.FuturePlayerTraining;
import core.training.FutureTrainingManager;
import core.training.WeeklyTrainingType;
import core.util.HODateTime;

import java.util.List;

public class FutureTrainingEntry {
    private final FutureTrainingManager futureTrainingManager;

    public FutureTrainingEntry(FutureTrainingManager futureTrainingManager){
        this.futureTrainingManager=futureTrainingManager;
    }

    public Player getPlayer() {
        return this.futureTrainingManager.getPlayer();
    }

    public double getTrainingSpeed() {
        return this.futureTrainingManager.getTrainingSpeed();
    }

    public String getTrainingPriorityInformation()
    {
        var firstTrainingDate = this.futureTrainingManager.getFutureTrainings().isEmpty() ?
                HODateTime.now() :
                this.futureTrainingManager.getFutureTrainings().get(0).getTrainingDate();
        return getPlayer().getTrainingPriorityInformation(firstTrainingDate);
    }

    public List<SkillChange> getFutureSkillChanges() {
        return futureTrainingManager.getFutureSkillChanges();
    }

    public FuturePlayerTraining.Priority getTrainingPriority(int trainingWeekIndex) {
        var trainingPerWeek = futureTrainingManager.getFutureTrainings().get(trainingWeekIndex);
        var	wt = WeeklyTrainingType.instance(trainingPerWeek.getTrainingType());
        var player = futureTrainingManager.getPlayer();
        return player.getFuturePlayerTrainingPriority(wt, trainingPerWeek.getTrainingDate());
    }
}
