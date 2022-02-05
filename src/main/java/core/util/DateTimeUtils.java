package core.util;

import core.model.UserParameter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

public class DateTimeUtils {

	public static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("Europe/Stockholm");

	private static final DateTimeFormatter cl_Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(DEFAULT_TIMEZONE);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private static Map<String, String> cl_availableZoneIds;

	/**
	 * Utility class - private constructor enforces non-instantiability.
	 */
	private DateTimeUtils() {
	}

	public static Map<String, String> getAvailableZoneIds() {

		LocalDateTime localDateTime = LocalDateTime.now();

		if(cl_availableZoneIds == null) {
			cl_availableZoneIds = ZoneId.getAvailableZoneIds()
					.stream()
					.map(ZoneId::of)
					.map(zoneId -> new AbstractMap.SimpleEntry<>(zoneId.toString(), localDateTime.atZone(zoneId)
							.getOffset()
							.getId()
							.replaceAll("Z", "+00:00")))
					.sorted(Map.Entry.<String, String>comparingByValue().reversed())
					.collect(Collectors.toMap(
							AbstractMap.SimpleEntry::getKey,
							AbstractMap.SimpleEntry::getValue,
							(oldValue, newValue) -> oldValue,
							LinkedHashMap::new));
		}
		return cl_availableZoneIds;
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
	 * Converts a {@link Timestamp}, presumably stored as CET/CEST in the database (Hattrick default),
	 * and converts into an {@link Instant}.
	 *
	 * @param timestamp Timestamp to be converted to {@link Instant}.  The timestamp is presumed to be
	 *                  in CET/CEST timezone (depending on daylight savings time).
	 * @return Instant – Timestamp as an instant.
	 */
	public static Instant getCESTTimestampToInstant(final Timestamp timestamp) {
		ZoneRules rules = DEFAULT_TIMEZONE.getRules();
		String str = dateFormat.format(timestamp);

		// Add current offset for time in default timezone (Europe/Stockholm), and parse as an Instant.
		return Instant.parse(str + rules.getOffset(LocalDateTime.now()));
	}

  /**
	 Format a datetime with HO! language interface
	 */
	public static String FormatLongDate(Timestamp ts) {

		Locale locale = Languages.lookup(UserParameter.instance().sprachDatei).getLocale();

		DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
		FORMATTER.withLocale(locale);

		return ts.toLocalDateTime().format(FORMATTER);
	}

	/**
        converts a Date into a SQL timestamp
	 */
	public static String DateToSQLtimeStamp(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return "TIMESTAMP '" + sdf.format(date) + "'";
	}

	/**
	 converts an Instant into a SQL timestamp with the instant suppose to represent time in HT timeZone
	 */
	public static String InstantToSQLtimeStamp(Instant instant) {
		return cl_Formatter.format(instant);
	}


	/**
	 * return the zoneID from the hashCode
	 */
	public static ZoneId fromHash(int _hashCode){
		getAvailableZoneIds();
		for (var toto:cl_availableZoneIds.keySet()){
			if(toto.hashCode() == _hashCode){
				return ZoneId.of(toto);
			}
		}
		HOLogger.instance().error(DateTimeUtils.class, "ZoneID could not be identified from hashValue");
		return null;
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
