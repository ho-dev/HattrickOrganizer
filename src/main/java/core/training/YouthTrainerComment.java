package core.training;

import core.model.player.YouthPlayer;

public class YouthTrainerComment extends YouthPlayer.ScoutComment {
    private int youthMatchId;

    public void setMatchId(int match_id) {
        this.youthMatchId=match_id;
    }

    public int getYouthMatchId() {
        return youthMatchId;
    }
}
