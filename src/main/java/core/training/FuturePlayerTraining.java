package core.training;

import java.util.HashMap;
import java.util.Map;

public class FuturePlayerTraining {

    public HattrickDate getFrom() {
        return from;
    }

    public void setFrom(HattrickDate from) {
        this.from = from;
    }

    public HattrickDate getTo() {
        return to;
    }

    public void setTo(HattrickDate to) {
        this.to = to;
    }

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
    private HattrickDate from;
    private HattrickDate to;
    private Priority priority;

    public FuturePlayerTraining(int playerId, FuturePlayerTraining.Priority prio, HattrickDate from, HattrickDate to){
        this.playerId=playerId;
        this.priority= prio;
        this.from = from;
        this.to = to;
    }

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

    public boolean isInWeek(HattrickDate week) {
        return week.isBetween(this.from, this.to);
    }

    /**
     * Cut the given time interval from the current training interval
     * @param from HattrickDate
     * @param to HattrickDate
     * * @return false if remaining training interval is not empty
     *          true if training is completely replaced by the given interval
     */
    public boolean cut(HattrickDate from, HattrickDate to) {
        if ( from.isAfter(this.to) || this.from.isAfter(to)){
            // this is outside the given interval
            return false;
        }

        if ( this.from.isAfter(from)){
            this.from = to;
            this.from.addWeeks(1);
            return false;
        }
        else if ( from.isAfter(this.from)){
            this.to = from;
            this.to.addWeeks(-1);
            return false;
        }
        return true; // completely replaced
    }

}
