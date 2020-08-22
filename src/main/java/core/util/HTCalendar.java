package core.util;

import java.sql.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <code>HTCalendar</code> is a class for calculating the Hattrick season and week number for a
 * given date. Dates before October 15, 2000 can not be calculated using this calendar and will
 * return both 0 for season and week.<br>
 * Note:<br>
 * October 15, 2000 was the start of Swedish season 11 and the first season for the first
 * non-Swedish leagues that were created, namely:<br>
 *
 * <ul>
 * <li>
 * Argentina
 * </li>
 * <li>
 * Germany
 * </li>
 * <li>
 * Italy
 * </li>
 * <li>
 * France
 * </li>
 * <li>
 * England
 * </li>
 * <li>
 * USA
 * </li>
 * <li>
 * Mexico
 * </li>
 * </ul>
 *
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
public class HTCalendar {
    /** Number of weeks in a HT season */
    private static final int WEEK_IN_SEASON = 16;

    /** Minimum number of elapsed week */
    private static final int MIN_ELAPSED_WEEKS = 177;

    /** Start date of Swedich HT season 10 */
    private static final Calendar HT_START = new GregorianCalendar(2000, 9, 15,
            0, 0);

    /** Correction factor for number of seasons */
    private static final int SE_CORRECTION = 11;

    /**
     * Calendar representing the date for which the Hattrick season and week needs to be
     * calculated.
     */
    private Calendar calendar;

    /** Calculated total Hattrick weeks */
    private int elapsedWeeks;
    private int firstDayOfTheWeek = Calendar.SUNDAY;
    private int seasonCorrection;

    /**
     * Gets the calculated Hattrick season  or 0 if the date is before the league's first season.
     *
     * @return The calculated Hattrick season.
     */
    public final int getHTSeason() {
        if (elapsedWeeks < MIN_ELAPSED_WEEKS) {
            return 0;
        }
        else {
            final int season = ((elapsedWeeks - 1) / WEEK_IN_SEASON)
                - this.seasonCorrection;

            if (season > 0) {
                return season;
            }
            else {
                return 0;
            }
        }
    }

    /**
     * Gets the calculated Hattrick week. or 0 if the date is before the league's first season.
     *
     * @return The calculated Hattrick week.
     */
    public final int getHTWeek() {
        if (getHTSeason() == 0) {
            return 0;
        }
        else {
            final int week = elapsedWeeks % WEEK_IN_SEASON;

            if (week != 0) {
                return week;
            }
            else {
                return WEEK_IN_SEASON;
            }
        }
    }

    /**
     * Sets the date for which to calculate the Hattrick season and week.
     *
     * @param time Date for which to calculate the Hattrick season and week.
     */
    public final void setTime(Timestamp time) {
        setTime(new Date(time.getTime()));
    }

    /**
     * Sets the date for which to calculate the Hattrick season and week.
     *
     * @param time Date for which to calculate the Hattrick season and week.
     */
    public final void setTime(Date time) {
        final Calendar cal = new GregorianCalendar();

        cal.setTime(time);
        setTime(cal);
    }

    /**
     * Sets the date for which to calculate the Hattrick season and week.
     *
     * @param cal Date for which to calculate the Hattrick season and week.
     */
    public final void setTime(Calendar cal) {
        //this.calendar = DateUtil.resetDay(cal);
        this.calendar = cal;
        calculate();
    }

    /**
     * Gets the date for which the Hattrick season and week is calculated.
     *
     * @return Date for which the Hattrick season and week is calculated.
     */
    public final Date getTime() {
        return this.calendar.getTime();
    }

    /**
     * Initializes the HTCalendar for flip-over point.
     *
     * @param marker Calendar representing a day on which the week ends/starts
     */
    public void initialize(Calendar marker) {
        this.firstDayOfTheWeek = marker.get(Calendar.DAY_OF_WEEK);
        HT_START.set(Calendar.HOUR_OF_DAY, marker.get(Calendar.HOUR_OF_DAY));
        HT_START.set(Calendar.MINUTE, marker.get(Calendar.MINUTE));
    }

    /**
     * Sets the season correction factor for the local league.
     */
    public void setSeasonCorrection(int correction) {
        this.seasonCorrection = correction;
    }

    /**
     * Calculates the Hattrick season and week.
     */
    private void calculate() {
        final Calendar cal = new GregorianCalendar();

        cal.setTime(this.calendar.getTime());

        final Calendar workCal = new GregorianCalendar();

        workCal.setTime(HT_START.getTime());

        int dayOfMonth = workCal.get(Calendar.DAY_OF_MONTH);

        dayOfMonth += (this.firstDayOfTheWeek - 1);

        if (this.firstDayOfTheWeek >= Calendar.FRIDAY) {
            dayOfMonth -= 7;
        }

        workCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        for (elapsedWeeks = 0; workCal.before(cal) || workCal.equals(cal);
            elapsedWeeks++) {
            workCal.add(Calendar.WEEK_OF_YEAR, 1);
        }

        elapsedWeeks += (WEEK_IN_SEASON * SE_CORRECTION);
    }
    
    public static Date resetDay(Date date) {
        final Calendar cal = new GregorianCalendar();

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
}
}
