package core.training;

import core.model.HOVerwaltung;
import core.util.HODateTime;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

public class FuturePlayerTraining {


    public boolean contains(Instant trainingDate) {
        // from<=date & to>date
        if ( !from.instant.isAfter(trainingDate)  ) {
            if  ( to == null ) return true;
            var endOfToWeek = to.instant.plus(Duration.ofDays(7));
            return endOfToWeek.isAfter(trainingDate);
        }
        return false;
    }

    public boolean endsBefore(HODateTime nextWeek) {
        return to != null && to.instant.isBefore(nextWeek.instant);
    }

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

        public String toString(){
            return switch (value) {
                case 3 -> HOVerwaltung.instance().getLanguageString("trainpre.fulltrain");
                case 2 -> HOVerwaltung.instance().getLanguageString("trainpre.partialtrain");
                case 1 -> HOVerwaltung.instance().getLanguageString("trainpre.osmosistrain");
                case 0 -> HOVerwaltung.instance().getLanguageString("trainpre.notrain");
                default -> "";
            };
        }
    }

    /**
     * Player Id
     */
    private int playerId;
    /**
     * first week of training interval
     */
    private HODateTime from;
    /**
     * last week of training interval (null if training is planned forever)
     */
    private HODateTime to;
    /**
     * priority of the training (overrides automatic determination by best position)
     */
    private Priority priority;

    public FuturePlayerTraining(int playerId, FuturePlayerTraining.Priority prio, HODateTime from, HODateTime to) {
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

    public HODateTime getFrom() {
        return from;
    }

    public void setFrom(HODateTime from) {
        this.from = from;
    }

    public HODateTime getTo() {
        return to;
    }

    public void setTo(HODateTime to) {
        this.to = to;
    }


    /**
     * Cut the given time interval from the current training interval
     *
     * @param from HattrickDate
     * @param to   HattrickDate, null means open end
     *
     * @return false if remaining training interval is not empty
     *          true if training is completely replaced by the new interval
     */
    public boolean cut(HODateTime from, HODateTime to) {
        if (from.instant.isAfter(this.to.instant) || this.from.instant.isAfter(to.instant)) {
            // this is outside the given interval
            return false;
        }

        if (from.instant.isAfter(this.from.instant)) {
            this.to = from;
            this.to.instant.minus(Duration.ofDays(7));
            return false;
        }
        if ( to != null && (this.to == null || this.to.instant.isAfter(to.instant))) {
            this.from = to;
            this.from.instant.plus(Duration.ofDays(7));
            return false;
        }
        return true; // completely replaced
    }

}