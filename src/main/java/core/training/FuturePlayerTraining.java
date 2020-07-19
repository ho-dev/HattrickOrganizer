package core.training;

import java.util.HashMap;
import java.util.Map;

public class FuturePlayerTraining {


    public enum Priority {
        NO_TRAINING(0),
        OSMOSIS_TRAINING(1),
        PARTIAL_TRAINING(2),
        FULL_TRAINING(3);

        private int value;
        private static HashMap<Integer, Priority> map = new HashMap<>();

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

    /**
     * Player Id
     */
    private int playerId;
    /**
     * first week of training interval
     */
    private HattrickDate from;
    /**
     * last week of training interval (null if training is planned forever)
     */
    private HattrickDate to;
    /**
     * priority of the training (overrides automatic determination by best position)
     */
    private Priority priority;

    public FuturePlayerTraining(int playerId, FuturePlayerTraining.Priority prio, HattrickDate from, HattrickDate to) {
        this.playerId = playerId;
        this.priority = prio;
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

    /**
     * check if week is during the planned training interval
     * @param week week to test
     * @return true if week is during current interval
     */
    public boolean isInWeek(HattrickDate week) {
        return week.isBetween(this.from, this.to);
    }

    /**
     * Cut the given time interval from the current training interval
     *
     * @param from HattrickDate
     * @param to   HattrickDate
     *
     * @return false if remaining training interval is not empty
     *          true if training is completely replaced by the new interval
     */
    public boolean cut(HattrickDate from, HattrickDate to) {
        if (from.isAfter(this.to) || this.from.isAfter(to)) {
            // this is outside the given interval
            return false;
        }

        if (from.isAfter(this.from)) {
            this.to = from;
            this.to.addWeeks(-1);
            return false;
        }
        if ( to != null && this.to.isAfter(to)) {
            this.from = to;
            this.from.addWeeks(1);
            return false;
        }
        return true; // completely replaced
    }

}