package core.util;

import core.model.HOVerwaltung;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;

public class HODateTime implements Comparable<HODateTime> {
    /**
     * time zone of hattrick
     */
    public static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("Europe/Stockholm");
    /**
     * Date time format of chpp files
     */
    private static final DateTimeFormatter cl_Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(DEFAULT_TIMEZONE);
    /**
     * the birthday of hattrick
     */
    private static final HODateTime htStart = HODateTime.fromHT("1997-09-22 00:00:00");

    /**
     * internal time representation
     */
    public Instant instant;

    /**
     * create an HODateTime instance (should it be private?)
     * @param in Instant
     */
    public HODateTime(Instant in) {
        this.instant = in;
    }

    /**
     * Create instance from HT (chpp) string
     * @param htString HT string
     * @return HODateTime
     */
    public static HODateTime fromHT(String htString) {
        LocalDateTime htTime = LocalDateTime.parse(htString, cl_Formatter);
        return new HODateTime(htTime.atZone(DEFAULT_TIMEZONE).toInstant());
    }

    /**
     * Create an instance from database timestamp
     * @param timestamp database timestamp
     * @return HODateTime
     */
    public static HODateTime fromDbTimestamp(Timestamp timestamp) {
        return new HODateTime(timestamp.toInstant());
    }

    /**
     * Create an instance representing current time
     * @return HODateTime
     */
    public static HODateTime now() {
        return new HODateTime(Instant.now());
    }

    /**
     * Convert to HT (chpp) string representation
     * @return String
     */
    public String toHT() {
        return cl_Formatter.format(instant);
    }

    /**
     * Convert to date only string, using user's locale setting (system default)
     * @return String
     */
    public String toLocaleDate() {
        var formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    /**
     * Convert to date and time string, using user's locale setting (system default)
     * @return String
     */
    public String toLocaleDateTime() {
        var formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    /**
     * Convert to database timestamp
     * @return Timestamp
     */
    public Timestamp toDbTimestamp() {
        return Timestamp.from(instant);
    }

    /**
     * Compare HODateTime instances
     * @param o other HODateTime instance
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(@NotNull HODateTime o) {
        return instant.compareTo(o.instant);
    }

    /**
     * Internal class representing HT's season and week
     */
    public class HTWeek {
        /**
         * season number [1..]
         */
        public int season;
        /**
         * week number [1..16]
         */
        public int week;
    }

    /**
     * Convert to absolut HT's season and week (same as swedish's league season)
     * @return HTWeek
     */
    public HTWeek toHTWeek() {
        var dayDiff= ChronoUnit.DAYS.between(htStart.instant, instant);
        var ret = new HTWeek();
        ret.season = (int)(dayDiff / (16 * 7) + 1);
        ret.week = (int) ((dayDiff % (16 * 7)) / 7) + 1;
        return ret;
    }

    /**
     * Convert to locale HT's seasond and week (user's league season)
     * @return HTWeek
     */
    public HTWeek toLocaleHTWeek() {
        var ret = toHTWeek();
        ret.season += HOVerwaltung.instance().getModel().getBasics().getSeasonOffset();
        return ret;
    }
}

