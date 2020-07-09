package core.training;

import java.util.HashMap;
import java.util.Map;

public class FuturePlayerTraining {
    public enum Priority {
        OSMOSIS_TRAINING(0),
        PARTIAL_TRAINING(1),
        FULL_TRAINING(2),
        BONUS_TRAINING(3);

        private int value;
        private static HashMap<Integer,Priority> map = new HashMap<>();

        Priority(int value) {
            this.value = value;
        }

        static {
            for (Priority p : Priority.values()) {
                map.put(p.value, p);
            }
        }

        public static Priority valueOf(int p) {
            return map.get(p);
        }

        public int getValue() {
            return value;
        }
    }

    private int playerId;
    private int fromSeason;
    private int fromWeek;
    private Integer toSeason;
    private Integer toWeek;
    private Priority priority;

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority prio) {
        this.priority = prio;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setFromSeason(int fromSeason) {
        this.fromSeason = fromSeason;
    }

    public void setFromWeek(int fromWeek) {
        this.fromWeek = fromWeek;
    }

    public void setToSeason(Integer toSeason) {
        this.toSeason = toSeason;
    }

    public void setToWeek(Integer toWeek) {
        this.toWeek = toWeek;
    }

    public boolean isInWeek(int hattrickSeason, int hattrickWeek) {
        if ( this.fromSeason < hattrickSeason || this.fromSeason == hattrickSeason && this.fromWeek <= hattrickWeek ){
            return toSeason == null || toSeason > hattrickSeason || toSeason == hattrickSeason && toWeek >= hattrickWeek;
        }
        return false;
    }


}
