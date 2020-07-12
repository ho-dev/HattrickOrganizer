package core.training;

/**
 * Hattrick Date Object
 */
public class HattrickDate {
    /** season */
    private int _Season;

    /** Week */
    private int _Week;

    public HattrickDate(int hattrickSeason, int hattrickWeek) {
        this._Season = hattrickSeason;
        this._Week = hattrickWeek;
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

    public boolean isBetween(HattrickDate from, HattrickDate to) {
        if ( from.getSeason() < _Season || from.getSeason() == _Season && from.getWeek() <= _Week ){
            return to == null || to.getSeason() > _Season || to.getSeason() == _Season && to.getWeek() >= _Week;
        }
        return false;
    }

    public boolean isAfter(HattrickDate date) {
        return date != null && (this.getSeason() > date.getSeason() || this.getSeason()==date.getSeason() && this.getWeek() > date.getWeek());
    }

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
