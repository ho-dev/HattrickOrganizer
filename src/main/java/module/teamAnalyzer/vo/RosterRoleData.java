package module.teamAnalyzer.vo;

import core.model.player.SpielerPosition;
import core.util.HelperWrapper;

public class RosterRoleData {
    //~ Instance fields ----------------------------------------------------------------------------
    private double avg;
    private double max;
    private double min;
    private int app;
    private int pos;

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

    public String getPositionDesc() {
        int posCode = HelperWrapper.instance().getPosition(pos);

        return SpielerPosition.getNameForPosition((byte) posCode);
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
