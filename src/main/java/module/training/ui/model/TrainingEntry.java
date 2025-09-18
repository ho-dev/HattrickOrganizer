package module.training.ui.model;

import core.model.enums.MatchType;
import core.model.player.Player;
import core.model.player.SkillChange;
import core.training.FutureTrainingManager;
import core.util.HODateTime;
import module.training.PastTrainingManager;

import java.util.List;

public class TrainingEntry {
    private final FutureTrainingManager futureTrainingManager;
    private final PastTrainingManager pastTrainingManager;

    public TrainingEntry(FutureTrainingManager futureTrainingManager){
        this.pastTrainingManager=null;
        this.futureTrainingManager=futureTrainingManager;
    }
    public TrainingEntry(PastTrainingManager pastTrainingManager, FutureTrainingManager futureTrainingManager){
        this.pastTrainingManager=pastTrainingManager;
        this.futureTrainingManager=futureTrainingManager;
    }

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

    public List<SkillChange> getFutureSkillups() {
        return futureTrainingManager.getFutureSkillups();
    }
}
