package module.training;

/**
 * This value object represents a week of training. It contains the last hrf id before a training
 * update and the first hrf id after the update. It also contains the effect of the training as a
 * seperate value object.
 *
 * @author NetHyperon
 */
public class TrainWeekEffect {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Average form */
    private double avgForm;

    /** Average TSI */
    private int avgTSI;

    /** Value for total decrease in form */
    private int formDecrease;

    /** Value for total increase in form */
    private int formIncrease;

    /** HRF id after training update */
    private int hrfIdAfter;

    /** HRF id before training update */
    private int hrfIdBefore;

    /** Number of skillups */
    private int skillups;

    /** Value for total TSI */
    private int totalTSI;
    private int trainingType;

    /** Training season */
    private int trainseason;

    /** Training week */
    private int trainweek;

    /** Value for total decrease in TSI */
    private int tsiDecrease;

    /** Value for total increase in TSI */
    private int tsiIncrease;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TrainWeek object.
     *
     * @param week Training week
     * @param season training season
     * @param beforeHRF HRF id before training update
     * @param afterHRF HRF id after training update
     */
    public TrainWeekEffect(int week, int season, int beforeHRF, int afterHRF) {
        this.hrfIdBefore = beforeHRF;
        this.hrfIdAfter = afterHRF;
        this.trainweek = week;
        this.trainseason = season;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Set the amount of skillups
     *
     * @param amount Amount of skillups
     */
    public void setAmountSkillups(int amount) {
        this.skillups = amount;
    }

    /**
     * Get the amount of skillups
     *
     * @return Amount of skillups
     */
    public int getAmountSkillups() {
        return skillups;
    }

    /**
     * Set the average form
     *
     * @param avgForm Average form
     */
    public void setAverageForm(double avgForm) {
        this.avgForm = avgForm;
    }

    /**
     * Get the avarage form
     *
     * @return Average form
     */
    public double getAverageForm() {
        return avgForm;
    }

    /**
     * Set the average TSI
     *
     * @param avgTSI Average TSI
     */
    public void setAverageTSI(int avgTSI) {
        this.avgTSI = avgTSI;
    }

    /**
     * Get the avarage TSI
     *
     * @return Average TSI
     */
    public int getAverageTSI() {
        return avgTSI;
    }

    /**
     * Get the total decrease in form
     *
     * @return Form decrease
     */
    public int getFormDecrease() {
        return formDecrease;
    }

    /**
     * Get the total increase in form
     *
     * @return Form increase
     */
    public int getFormIncrease() {
        return formIncrease;
    }

    /**
     * Get the HRF id after the training update
     *
     * @return id
     */
    public int getHRFafterUpdate() {
        return this.hrfIdAfter;
    }

    /**
     * Get the HRF id before the training update
     *
     * @return id
     */
    public int getHRFbeforeUpdate() {
        return this.hrfIdBefore;
    }

    /**
     * Get the training HT season
     *
     * @return Training HT season
     */
    public int getHattrickSeason() {
        return trainseason;
    }

    /**
     * Get the training HT week
     *
     * @return Training HT week
     */
    public int getHattrickWeek() {
        return trainweek;
    }

    /**
     * Get the total decrease in TSI
     *
     * @return TSI decrease
     */
    public int getTSIDecrease() {
        return tsiDecrease;
    }

    /**
     * Get the total increase in TSI
     *
     * @return TSI increase
     */
    public int getTSIIncrease() {
        return tsiIncrease;
    }

    /**
     * Set the total TSI
     *
     * @param totalTSI Total TSI
     */
    public void setTotalTSI(int totalTSI) {
        this.totalTSI = totalTSI;
    }

    /**
     * Get the total TSI
     *
     * @return Total TSI
     */
    public int getTotalTSI() {
        return totalTSI;
    }

    public void setTrainingType(int trainingType) {
        this.trainingType = trainingType;
    }

    public int getTrainingType() {
        return trainingType;
    }

    /**
     * Add a form value, depending on the value it will be added to the increase or decrease.
     *
     * @param value Form value to add
     */
    public void addForm(int value) {
        if (value > 0) {
            this.formIncrease += value;
        } else if (value < 0) {
            this.formDecrease += value;
        }
    }

    /**
     * Add a TSI value, depending on the value it will be added to the increase or decrease.
     *
     * @param value TSI value to add
     */
    public void addTSI(int value) {
        if (value > 0) {
            this.tsiIncrease += value;
        } else if (value < 0) {
            this.tsiDecrease += value;
        }
    }

    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("TrainWeekEffect[");
        buffer.append(" HT season = " + trainseason);
        buffer.append(", HT week = " + trainweek);
        buffer.append(", hrf before = " + hrfIdBefore);
        buffer.append(", hrf after = " + hrfIdAfter);
        buffer.append("]");

        return buffer.toString();
    }
}
