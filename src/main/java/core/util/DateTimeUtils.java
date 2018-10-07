package core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private DateTimeUtils() {
	}

	/**
	 * Creates a new <code>Date</code> based on the given date with the time set
	 * to its minimum value. The returned date will represent a day at its the
	 * very first millisecond (00:00:00.000).
	 * 
	 * @param date
	 *            The date to set the time to its minimum.
	 * @return A new <code>Date</code> object based on the given date, with the
	 *         time set to its minimum.
	 */
	public static Date getDateWithMinTime(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		setMinTime(cal);
		return cal.getTime();
	}

	/**
	 * Creates a new <code>Date</code> based on the given date with the time set
	 * to its maximum value. The returned date will represent a day at its the
	 * very last millisecond (23:59:59.999).
	 * 
	 * @param date
	 *            The date to set the time to its maximum.
	 * @return A new <code>Date</code> object based on the given date, with the
	 *         time set to its maximum.
	 */
	public static Date getDateWithMaxTime(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		setMaxTime(cal);
		return cal.getTime();
	}

	/**
	 * Gets a date which is adjusted with the given amount of days (from today,
	 * depending on the system clock). The returned date will be in the future
	 * if a positive number of days is specified, and in the past for a negative
	 * number of days. The time of the returned date will be set according to
	 * the <code>timeAdjustment</code> parameter.
	 * 
	 * @param days
	 *            The number of days to adjust today's date with. Can be positve
	 *            or negativ.
	 * @param timeAdjustment
	 *            If {@link Time#MIN_TIME}, the time will be set to 00:00:00, if
	 *            {@link Time#MAX_TIME}, the time will be set to 23:59:59.
	 * @return The date of today adjusted with a given amount of days, with the
	 *         time set to 00:00:00. The time will stay unchanged (current
	 *         system time) for {@link Time#KEEP_TIME}
	 */
	public static Date getTodayAdjustedWithDays(int days, Time timeAdjustment) {
		return getDateAdjustedWithDays(new Date(), days, timeAdjustment);
	}

	/**
	 * Gets a new date which is adjusted with the given amount of days based on
	 * the given date. The returned date will be in the future if a positive
	 * number of days is specified, and in the past for a negative number of
	 * days. The time of the returned date will be set according to the
	 * <code>timeAdjustment</code> parameter.
	 * 
	 * @param date
	 *            The date to adjust.
	 * @param days
	 *            The number of days to adjust today's date with. Can be positve
	 *            or negativ.
	 * @param timeAdjustment
	 *            If {@link Time#MIN_TIME}, the time will be set to 00:00:00, if
	 *            {@link Time#MAX_TIME}, the time will be set to 23:59:59.
	 * @return The date of today adjusted with a given amount of days, with the
	 *         time set to 00:00:00. The time will stay unchanged (current
	 *         system time) for {@link Time#KEEP_TIME}
	 */
	public static Date getDateAdjustedWithDays(Date date, int days, Time timeAdjustment) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(GregorianCalendar.DAY_OF_MONTH, days);
		switch (timeAdjustment) {
		case MIN_TIME:
			setMinTime(cal);
			break;
		case MAX_TIME:
			setMaxTime(cal);
			break;
		}
		return cal.getTime();
	}

	/**
	 * Sets the calendar's time values to their maximum. This will be the very
	 * last millisecond of a day (23:59:59.999).
	 * 
	 * @param cal
	 *            The calendar to set the max. the time.
	 */
	private static void setMaxTime(Calendar cal) {
		cal.set(GregorianCalendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		cal.set(GregorianCalendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(GregorianCalendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(GregorianCalendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
	}

	/**
	 * Sets the calendar's time values to their minimum. This will be the very
	 * first millisecond of a day (00:00:00.000).
	 * 
	 * @param cal
	 *            The calendar to zero the time.
	 */
	private static void setMinTime(Calendar cal) {
		cal.set(GregorianCalendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
		cal.set(GregorianCalendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
		cal.set(GregorianCalendar.SECOND, cal.getMinimum(Calendar.SECOND));
		cal.set(GregorianCalendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
	}

	/**
	 * Specifies how time in a Date object is set/changed.
	 */
	public static enum Time {

		/**
		 * 00:00:00
		 */
		MIN_TIME,
		/**
		 * 23:59:59
		 */
		MAX_TIME,
		/**
		 * Keep the (current) time / leave time unchanged.
		 */
		KEEP_TIME;
	}
}
