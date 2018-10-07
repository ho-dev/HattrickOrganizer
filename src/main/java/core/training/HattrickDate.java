package core.training;

/**
 * Hattrick Date Object
 */
public class HattrickDate {
    /** season */
    private int _Season;

    /** Week */
    private int _Week;

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Set Hattrick Season
     *
     * @param iSeason
     */
    public final void setSeason(int iSeason) {
        this._Season = iSeason;
    }

    /**
     * Get Hattrick Season
     *
     * @return season
     */
    public final int getSeason() {
        return _Season;
    }

    /**
     * Set Hattrick week
     *
     * @param iWeek
     */
    public final void setWeek(int iWeek) {
        this._Week = iWeek;
    }

    /**
     * Get Hattrick
     *
     * @return week
     */
    public final int getWeek() {
        return _Week;
    }

    /**
     * toString method: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public final String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Skillup[");
        buffer.append("week = " + _Week);
        buffer.append(", season = " + _Season);
        buffer.append("]");
        return buffer.toString();
    }
}
