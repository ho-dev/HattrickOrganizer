package module.training.ui.comp;

import core.model.player.Player;
import core.model.player.MatchRoleID;

public class BestPositionCell implements Comparable<BestPositionCell> {

    private String value;
    private int pos;
    private float strength;


    public BestPositionCell(Player player) {
        strength = player.getIdealPosStaerke(true);
        value = MatchRoleID.getNameForPosition(player.getIdealPosition()) + " ("
                + strength + ")";
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
    public int compareTo(BestPositionCell other) {

        if (this.getSortLevel() > other.getSortLevel()) {
            return -1;
        }

        if (this.getSortLevel() < other.getSortLevel()) {
            return 1;
        }

        return 0;
    }
}
