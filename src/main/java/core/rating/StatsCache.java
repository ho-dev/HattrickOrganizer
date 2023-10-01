package core.rating;

import module.lineup.Lineup;

import java.util.HashMap;
import java.util.Map;

abstract class StatsCache extends HashMap<Long, Map<Integer, Double>> {
    Double average90;
    Double average120;

    public double get(Lineup lineup, Integer minute) {
        var revision = get(lineup.getRatingRevision());
        if (revision == null) {
            // forget previous calculation
            clear();
            average90 = null;
            average120 = null;
            // init new one
            revision = new HashMap<>();
            put(lineup.getRatingRevision(), revision);
        } else {
            var m = revision.get(minute);
            if (m != null) {
                return m;
            }
        }

        var ret = calc(lineup, minute);
        revision.put(minute, ret);
        return ret;
    }

    public abstract double calc(Lineup lineup, int minute);

    public double getAverage90(Lineup lineup) {
        if (average90 == null || get(lineup.getRatingRevision()) == null) {
            average90 = calcAverage(lineup, 90);
        }
        return average90;
    }

    public double getAverage120(Lineup lineup) {
        if (average120 == null || get(lineup.getRatingRevision()) == null) {
            average120 = calcAverage(lineup, 120);
        }
        return average120;
    }

    private double calcAverage(Lineup lineup, int minutes) {
        var iStart = 0;
        var lastRating = 0.;
        var sumRating = 0.;
        for (var m : RatingPredictionModel.getRatingChangeMinutes(lineup, minutes)) {
            var rating = get(lineup, m);
            sumRating += lastRating * (m - iStart);
            lastRating = rating;
            iStart = m;
        }
        sumRating += lastRating * (minutes - iStart);
        return sumRating / minutes;
    }
}
