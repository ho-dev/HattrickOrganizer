package core.training;

/**
 * Hattrick Date Object
 *
 * The accuracy is weekly
 */
public class HattrickDate {
    /** season */
    private int _Season;

    /**
     * Week
     * Number of week. Is between 1 and 16.
     */
    private int _Week;

    public HattrickDate(int hattrickSeason, int hattrickWeek) {
        this._Season = hattrickSeason;
        this._Week = hattrickWeek;
    }

    public HattrickDate(String s) {
        var nr = s.split(" ");
        if (nr.length == 2) {
            this._Season = Integer.parseInt(nr[0]);
            this._Week = Integer.parseInt(nr[1]);
        } else {
            this._Week = 0;
            this._Season = 0;
        }
    }

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

    /**
     * Check if this date is between given dates
     * @param from HattrickDate, must be less or equal to
     * @param to HattrickDate, must be greater or equal from, if null an open end is assumed
     * @return true if this date is between to and from
     */
    public boolean isBetween(HattrickDate from, HattrickDate to) {
        if ( from.getSeason() < _Season || from.getSeason() == _Season && from.getWeek() <= _Week ){
            return to == null || to.getSeason() > _Season || to.getSeason() == _Season && to.getWeek() >= _Week;
        }
        return false;
    }

    /**
     * Check if this date is after the given date
     * @param date to compare with, if null an open end is assumed (=> return false)
     * @return true, if this date is after given date
     */
    public boolean isAfter(HattrickDate date) {
        return date != null && (this.getSeason() > date.getSeason() || this.getSeason()==date.getSeason() && this.getWeek() > date.getWeek());
    }

    /**
     * Add weeks to the date
     * @param i Number of weeks to add. Could be a negative number
     */
    public void addWeeks(int i) {
        this._Week += i;
        while ( this._Week > 16){
            this._Season++;
            this._Week -= 16;
        }
        while ( this._Week < 1){
            this._Season--;
            this._Week += 16;
        }
    }
}
