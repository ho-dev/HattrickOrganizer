package core.specialevents;

public class LinearFit {
    private double f1, f2;
    private double d1, d2;

    // defines a straight line equation through the points (d1,f1) and (d2,f2)
    public LinearFit(double f1, double f2, double d1, double d2) {
        this.f1 = f1;
        this.f2 = f2;
        this.d1 = d1;
        this.d2 = d2;
    }

    // the value at d
    public double f(double d) {
        return f1 + (f2 - f1) / (d2 - d1) * (d - d1);
    }
}
