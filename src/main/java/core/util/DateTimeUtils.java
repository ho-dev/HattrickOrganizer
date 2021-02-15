package core.util;

import core.model.UserParameter;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateTimeUtils {

	private static DateTimeFormatter cl_Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Europe/Stockholm"));

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private DateTimeUtils() {
	}

	/**
	 Format a datetime with HO! language interface
	 */
	public static String Format(Date date, String format) {
		Locale locale = Languages.lookup(UserParameter.instance().sprachDatei).getLocale();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, locale);
		return simpleDateFormat.format(date);
	}


	/**
        converts a Date into a SQL timestamp
	 */
	public static String DateToSQLtimeStamp(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String res = "TIMESTAMP '" + sdf.format(date) + "'";
		return res;
	}

	/**
	 converts an Instant into a SQL timestamp with the instant suppose to represent time in HT timeZone
	 */
	public static String InstantToSQLtimeStamp(Instant instant) {
		return cl_Formatter.format(instant);
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
	public enum Time {

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
