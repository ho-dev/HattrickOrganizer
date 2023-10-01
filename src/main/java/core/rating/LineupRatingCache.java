package core.rating;

import module.lineup.Lineup;

import java.util.HashMap;
import java.util.Map;

abstract class LineupRatingCache extends HashMap<Long, Map<RatingPredictionModel.RatingSector, Map<Integer, Double>>> {
    public double get(Lineup lineup, RatingPredictionModel.RatingSector s, Integer minute) {
        var revision = get(lineup.getRatingRevision());
        if (revision == null) {
            revision = new HashMap<>();
            clear();
            put(lineup.getRatingRevision(), revision);
        }
        var sector = revision.get(s);
        if (sector != null) {
            var rating = sector.get(minute);
            if (rating != null) {
                return rating;
            }
        } else {
            sector = new HashMap<>();
            revision.put(s, sector);
        }

        var ret = calc(lineup, s, minute);
        sector.put(minute, ret);
        return ret;
    }

    public abstract double calc(Lineup lineup, RatingPredictionModel.RatingSector s, Integer minute);
}
