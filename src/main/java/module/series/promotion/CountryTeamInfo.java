package module.series.promotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean used to generate the JSON payload submitted to the HO server.
 */
public class CountryTeamInfo {

    static class TeamRank {

        public TeamRank(int teamId, long score) {
            this.teamId = teamId;
            this.Score = score;
        }

        int teamId;
        long Score; // Uppercase “S” because this is what the submitted JSON expects.

        public int getTeamId() {
            return teamId;
        }

        public long getScore() {
            return Score;
        }

        public void setScore(long score) {
            this.Score = score;
        }
    }

    int leagueId;
    int season;
    String username;
    List<TeamRank> data = new ArrayList<>();
}
