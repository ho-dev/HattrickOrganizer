package module.training.ui.comp;

import core.model.player.Player;
import core.model.player.MatchRoleID;
import core.training.HattrickDate;

public class TrainingPriorityCell implements Comparable<TrainingPriorityCell> {

    private String value;
    private int pos;
    private float strength;

    public TrainingPriorityCell(Player player, HattrickDate nextWeek) {
        value = player.getTrainingPriorityInformation(nextWeek);
        pos = MatchRoleID.getSortId(player.getIdealPosition(), false);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public float getSortLevel() {
        return pos * 100 - strength;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(TrainingPriorityCell other) {
        return this.value.compareTo(other.value);
    }
}
