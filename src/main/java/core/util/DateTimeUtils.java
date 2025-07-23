package core.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.*;
import java.util.stream.Collectors;

public class DateTimeUtils {

	public static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("Europe/Stockholm");

	private static final DateTimeFormatter cl_Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(DEFAULT_TIMEZONE);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private static Map<String, String> cl_availableZoneIds;

	private DateTimeUtils() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
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
		KEEP_TIME
	}
}
