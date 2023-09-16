package core.rating;

import java.util.HashMap;

abstract class RatingCalculationCache<T1> {
    private final HashMap<T1, Double> theCache = new HashMap<>();
    public double get(T1 t1) {
        var ret = theCache.get(t1);
        if (ret != null) {
            return ret;
        }
        ret = calc(t1);
        theCache.put(t1, ret);
        return ret;
    }

    public abstract double calc(T1 t1);
}


