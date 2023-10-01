package core.rating;

import java.util.HashMap;

abstract class RatingCalculationCache3<T1, T2, T3> {
    private final HashMap<T1, HashMap<T2, HashMap<T3, Double>>> theCache = new HashMap<>();

    public double get(T1 t1, T2 t2, T3 t3) {
        Double d;
        HashMap<T3, Double> b = null;
        var a = theCache.get(t1);

        if (a != null) {
            b = a.get(t2);
            if (b != null) {
                d = b.get(t3);
                if (d != null) {
                    return d;
                }
            }
        }
        var r = calc(t1, t2, t3);
        if (b == null) {
            b = new HashMap<>();
        }
        b.put(t3, r);
        if (a == null) {
            a = new HashMap<>();
        }
        a.put(t2, b);
        theCache.put(t1, a);
        return r;
    }

    public abstract double calc(T1 t1, T2 t2, T3 t3);
}
