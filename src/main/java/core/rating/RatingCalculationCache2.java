package core.rating;

import java.util.HashMap;

abstract class RatingCalculationCache2<T1, T2> {
    private final HashMap<T1, HashMap<T2, Double>> theCache = new HashMap<>();

    public double get(T1 t1, T2 t2) {
        var c = theCache.get(t1);
        if (c != null) {
            var ret = c.get(t2);
            if (ret != null) {
                return ret;
            }
        }
        var r = calc(t1, t2);
        if (c == null) {
            c = new HashMap<>();
        }
        c.put(t2, r);
        theCache.put(t1, c);
        return r;
    }

    public abstract double calc(T1 t1, T2 t2);
}
