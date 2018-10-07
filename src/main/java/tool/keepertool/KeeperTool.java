// %2855852157:de.hattrickorganizer.logik%
package tool.keepertool;

/**
 * Main class to calculate the keeper subskill
 *
 * @author draghetto
 */
class KeeperTool {
    //~ Instance fields ----------------------------------------------------------------------------

    private double coeff = 1.0d / 3.3993;
    private int form;
    private int tsi;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new KeeperTool object.
     *
     * @param _form keeper form
     * @param _tsi keeper tsi
     */
    KeeperTool(int _form, int _tsi) {
        this.tsi = _tsi;
        this.form = _form;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Return the calculated subskill if form is average
     *
     * @return the subskill
     */
    final double getAvg() {
        final double fc = getFormCorrector(form, 0.5);
        final double val = tsi / (6.5258 * fc);
        return Math.pow(val, coeff) + 1;
    }

    /**
     * Return the calculated subskill if form is high
     *
     * @return the subskill
     */
    final double getMax() {
        final double fc = getFormCorrector(form, 1);
        final double val = tsi / (6.5258 * fc);
        return Math.pow(val, coeff) + 1;
    }

    /**
     * Return the calculated subskill if form is low
     *
     * @return the subskill
     */
    final double getMin() {
        final double fc = getFormCorrector(form, 0);
        final double val = tsi / (6.5258 * fc);
        return Math.pow(val, coeff) + 1;
    }

    /**
     * Returns the form multiplier
     *
     * @param _form keeper form
     * @param mod form modifier, to simulate low, average and high
     *
     * @return the multiplier
     */
    private double getFormCorrector(int _form, double mod) {
        return 1 + ((_form - mod - 5.5) / 10.5);
    }
}
