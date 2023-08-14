package core.rating;

import module.lineup.Lineup;

import java.util.HashMap;
import java.util.Map;

abstract class TacticRatingCache extends HashMap<Long, Map<Integer, Double>> {
    public double get(Lineup lineup, Integer minute) {
        var revision = get(lineup.getRatingRevision());
        if (revision == null) {
            revision = new HashMap<>();
            clear();
            put(lineup.getRatingRevision(), revision);
        } else {
            var rating = revision.get(minute);
            if (rating != null) {
                return rating;
            }
        }
        var ret = calc(lineup, minute);
        revision.put(minute, ret);
        return ret;
    }

    public abstract double calc(Lineup lineup, Integer minute);
}
