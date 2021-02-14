package core.training;

import core.util.DateTimeInfo;
import module.transfer.test.HTWeek;

import java.time.*;

/**
 * Hattrick Date Object
 *
 * The accuracy is weekly
 */
public class HattrickDate {

    /**
     * Date representing the hattrick week
     */
    private Instant date;

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
        this.date = toInstant();
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
        this.date= toInstant();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Set Hattrick Season
     *
     * @param iSeason
     */
    public final void setSeason(int iSeason) {
        this._Season = iSeason;
        this.date = toInstant();
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
        this.date = toInstant();
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
    public boolean isAfter(Instant date) {
        return this.date.isAfter(date);
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
        this.date = toInstant();
    }


    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof HattrickDate)) {
            return false;
        }

        HattrickDate d = (HattrickDate) o;

        return d.getSeason() == this.getSeason() &&
                d.getWeek() == this.getWeek();
    }

    //Idea from effective Java : Item 9
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getWeek();
        result = 31 * result + getSeason();
        return result;
    }


    private static Instant getOrigin()
    {
        var orig = new DateTimeInfo("1997-08-22");
        return orig.getHattrickTime().toInstant();
    }

    public Instant toInstant()
    {
        var val = getOrigin();
        val.plus(Duration.ofDays((this._Season-1)*112 + (this._Week-1)*7));
        return val;
    }

    public static HattrickDate getHattrickDateByDate(Instant date) {
        var origin = getOrigin();
        long msDiff = date.getEpochSecond() - origin.getEpochSecond();
        long dayDiff = msDiff / 60 / 60 / 24;
        int season = (int) Math.floor(dayDiff / (16 * 7)) + 1;
        int week = (int) Math.floor((dayDiff % (16 * 7)) / 7) + 1;

        return new HattrickDate(season, week);
    }


}
