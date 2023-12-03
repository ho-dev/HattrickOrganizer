package core.training;

import core.db.AbstractTable;
import core.model.HOVerwaltung;
import core.util.HODateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuturePlayerTraining extends AbstractTable.Storable {


    public boolean contains(HODateTime trainingDate) {
        // from<=date & to>date
        if (!this.getFrom().isAfter(trainingDate)) {
            if (this.getTo() == null) return true;
            var endOfToWeek = this.getTo().plus(7, ChronoUnit.DAYS);
            return endOfToWeek.isAfter(trainingDate);
        }
        return false;
    }

    public boolean endsBefore(HODateTime nextWeek) {
        return getTo() != null && nextWeek.isAfter(getTo());
    }

    public int getFromWeek() {
        return fromWeek;
    }

    public void setFromWeek(int fromWeek) {
        this.fromWeek = fromWeek;
    }

    public int getFromSeason() {
        return fromSeason;
    }

    public void setFromSeason(int fromSeason) {
        this.fromSeason = fromSeason;
    }

    public Integer getToWeek() {
        return toWeek;
    }

    public void setToWeek(Integer toWeek) {
        this.toWeek = toWeek;
    }

    public Integer getToSeason() {
        return toSeason;
    }

    public void setToSeason(Integer toSeason) {
        this.toSeason = toSeason;
    }

    public enum Priority {
        NO_TRAINING(0),
        OSMOSIS_TRAINING(1),
        PARTIAL_TRAINING(2),
        FULL_TRAINING(3);

        private final int value;
        private static final HashMap<Integer, Priority> map = new HashMap<>();

        Priority(int value) {
            this.value = value;
        }

        static {
            for (Priority p : Priority.values()) {
                map.put(p.value, p);
            }
        }

        public static Priority valueOf(Integer p) {
            return map.get(p);
        }

        public int getValue() {
            return value;
        }

        public String toString() {
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

    private int fromWeek;
    private int fromSeason;

    private HODateTime _from;
    /**
     * last week of training interval (null if training is planned forever)
     */
    private Integer toWeek;
    private Integer toSeason;

    private HODateTime _to;
    /**
     * priority of the training (overrides automatic determination by best position)
     */
    private Priority priority;

    public FuturePlayerTraining(int playerId, FuturePlayerTraining.Priority prio, HODateTime from, HODateTime to) {
        this.playerId = playerId;
        this.priority = prio;
        setFrom(from);
        setTo(to);
    }

    /**
     * constructor is used by AbstractTable.load
     */
    public FuturePlayerTraining(){}

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
        if ( this._from == null){
            this._from = HODateTime.fromHTWeek(new HODateTime.HTWeek(fromSeason, fromWeek));
        }
        return _from;
    }

    public void setFrom(HODateTime from) {
        this._from = from;
        var htWeek = from.toHTWeek();
        this.fromSeason = htWeek.season;
        this.fromWeek = htWeek.week;
    }

    boolean toInitDone = false;
    public HODateTime getTo() {
        if ( !toInitDone) {
            if (toSeason != null && toWeek != null) {
                _to = HODateTime.fromHTWeek(new HODateTime.HTWeek(toSeason, toWeek));
            } else {
                _to = null;
            }
        }
        return _to;
    }

    public void setTo(HODateTime to) {
        this._to = to;
        if (to != null){
            var htWeek = to.toHTWeek();
            this.toSeason = htWeek.season;
            this.toWeek = htWeek.week;
        }
        else {
            this.toSeason = null;
            this.toWeek = null;
        }
    }

    /**
     * Cut the given time interval from the current training interval
     *
     * @param from HattrickDate
     * @param to   HattrickDate, null means open end
     * @return list of remaining training intervals
     *  if nothing has to be changed the list contains the current interval itself
     *  otherwise the list contains
     *  no entry, it the new range overlaps this interval completely
     *  one entry, if the current interval is shortened
     *  two intervals, if this interval overlaps the given time range
     */
    public List<FuturePlayerTraining> cut(HODateTime from, HODateTime to) {
        var ret = new ArrayList<FuturePlayerTraining>();

        if (this.getTo() != null && from.isAfter(this.getTo()) || to != null && this.getFrom().isAfter(to)) {
            // this is outside the given interval
            ret.add(this);
            return ret; // nothing changed
        }

        if (from.isAfter(this.getFrom())) {
            if (this.getTo() != null && to != null && !this.getTo().isAfter(to)) {
                ret.add(new FuturePlayerTraining(this.playerId, this.priority, this.getFrom(), from.minus(7, ChronoUnit.DAYS)));
            }
            else {
                var newFutureTraining = new FuturePlayerTraining(this.playerId, this.priority, this.getFrom(), from.minus(7, ChronoUnit.DAYS));
                ret.add(newFutureTraining);
            }
        }
        if (to != null && (this.getTo() == null || this.getTo().isAfter(to))) {
            ret.add(new FuturePlayerTraining(this.playerId, this.priority, to, getTo()));
        }
        return ret; // completely replaced
    }
}