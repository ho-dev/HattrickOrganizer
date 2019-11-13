package module.training.ui.comp;

import core.model.player.Player;
import core.model.player.MatchRoleID;

public class BestPositionCell implements Comparable<BestPositionCell> {

    private String value;
    private int pos;


    public BestPositionCell(Player player) {
        value = MatchRoleID.getNameForPosition(player.getIdealPosition()) + " ("
                + player.getIdealPosStaerke(true) + ")";
        pos = MatchRoleID.getSortId(player.getIdealPosition(), false);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(BestPositionCell other) {

        if (this.getPos() > other.getPos()) {
            return -1;
        }

        if (this.getPos() < other.getPos()) {
            return 1;
        }

        return 0;
    }
}
