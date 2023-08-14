package core.rating;

import java.util.HashMap;
import java.util.Map;

abstract class Cache3<T1, T2, T3> extends HashMap<T1, Map<T2, Map<T3, Double>>> {
    public double get(T1 t1, T2 t2, T3 t3) {
        Double d;
        Map<T3, Double> b = null;
        var a = this.get(t1);

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
        this.put(t1, a);
        return r;
    }

    public abstract double calc(T1 t1, T2 t2, T3 t3);
}
