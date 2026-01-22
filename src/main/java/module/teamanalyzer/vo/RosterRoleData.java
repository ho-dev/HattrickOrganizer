package module.teamanalyzer.vo;

public class RosterRoleData {
    //~ Instance fields ----------------------------------------------------------------------------
    private double avg;
    private double max;
    private double min;
    private int app;
    private final int pos;

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new RoleData object.
     */
    public RosterRoleData(int pos) {
        this.pos = pos;
        this.max = -1;
        this.min = 10000;
    }

    //~ Methods ------------------------------------------------------------------------------------
    public int getApp() {
        return app;
    }

    public double getAvg() {
        return avg;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public int getPos() {
        return pos;
    }

    public void addMatch(double rating) {
        if (rating > max) {
            max = rating;
        }

        if (rating < min) {
            min = rating;
        }

        avg = ((avg * app) + rating) / (app + 1d);
        app++;
    }
}
