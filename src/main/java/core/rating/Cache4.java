package core.rating;

import java.util.HashMap;

abstract class Cache4<T1, T2, T3, T4> extends HashMap<T1, HashMap<T2, HashMap<T3, HashMap<T4, Double>>>> {
    public double get(T1 t1, T2 t2, T3 t3, T4 t4) {
        Double d;
        HashMap<T4, Double> c = null;
        HashMap<T3, HashMap<T4, Double>> b = null;
        HashMap<T2, HashMap<T3, HashMap<T4, Double>>> a = this.get(t1);
        if (a != null) {
            b = a.get(t2);
            if (b != null) {
                c = b.get(t3);
                if (c != null) {
                    d = c.get(t4);
                    if (d != null) {
                        return d;
                    }
                }
            }
        }
        var r = calc(t1, t2, t3, t4);
        if (c == null) {
            c = new HashMap<>();
        }
        c.put(t4, r);
        if (b == null) {
            b = new HashMap<>();
        }
        b.put(t3, c);
        if (a == null) {
            a = new HashMap<>();
        }
        a.put(t2, b);
        this.put(t1, a);
        return r;
    }

    public abstract double calc(T1 t1, T2 t2, T3 t3, T4 t4);
}
