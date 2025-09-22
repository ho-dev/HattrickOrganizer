package module.training.ui.model;

import core.model.player.Player;
import core.model.player.SkillChange;
import core.training.FutureTrainingManager;
import core.training.WeeklyTrainingType;
import core.util.HODateTime;
import module.training.PastTrainingManager;

import java.util.List;

public class FutureTrainingEntry {
    private final FutureTrainingManager futureTrainingManager;
//    private final PastTrainingManager pastTrainingManager;

    public FutureTrainingEntry(FutureTrainingManager futureTrainingManager){
//        this.pastTrainingManager=null;
        this.futureTrainingManager=futureTrainingManager;
    }
    public FutureTrainingEntry(PastTrainingManager pastTrainingManager, FutureTrainingManager futureTrainingManager){
//        this.pastTrainingManager=pastTrainingManager;
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

    public List<SkillChange> getFutureSkillups() {
        return futureTrainingManager.getFutureSkillups();
    }

    public int getTrainingPriority(HODateTime.HTWeek htWeek) {
        var training = this.trainingModel.getFutureTrainings().get(column);
        var	wt = WeeklyTrainingType.instance(training.getTrainingType());
        var prio = player.getFuturePlayerTrainingPriority(wt, training.getTrainingDate());
        if (prio != null) {
            switch (prio) {
                case FULL_TRAINING:
                    this.setBackground(FULL_TRAINING_BG);
                    break;
                case PARTIAL_TRAINING:
                    if (this.trainingModel.isPartialTrainingAvailable(new int [] {column})) {
                        this.setBackground(PARTIAL_TRAINING_BG);
                    }
                    break;
                case OSMOSIS_TRAINING:
                    if ( this.trainingModel.isOsmosisTrainingAvailable(new int[]{column})){
                        this.setBackground(OSMOSIS_TRAINING_BG);
                    }
                    break;
            }
        }
    }
}
