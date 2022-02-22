package core.util;

import core.model.HOVerwaltung;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.Duration;
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
    public static final HODateTime htStart = HODateTime.fromHT("1997-09-22 00:00:00");

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
     * Create an instance from HT season, week
     * @param week ht season, week
     * @return date time of the start of hattrick week
     */
    public static HODateTime fromHTWeek(HTWeek week) {
        return new HODateTime(htStart.instant.plus(Duration.ofDays(((week.season-1)*16+ week.week-1)*7)));
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
        return toLocalDate(FormatStyle.MEDIUM);
    }

    public String toLocalDate(FormatStyle style){
        var formatter = DateTimeFormatter.ofLocalizedDate(style).withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    /**
     * Convert to date and time string, using user's locale setting (system default)
     * @return String
     */
    public String toLocaleDateTime() {
        return toLocaleDateTime(FormatStyle.MEDIUM);
    }

    public String toLocaleDateTime(FormatStyle style){
        var formatter = DateTimeFormatter.ofLocalizedDateTime(style).withZone(ZoneId.systemDefault());
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

    public HODateTime minus(int i, ChronoUnit unit) {
        return new HODateTime(instant.minus(i,unit));
    }

    public HODateTime plus(int i, ChronoUnit unit) {
        return new HODateTime(instant.plus(i,unit));
    }

    public boolean isBefore(HODateTime t) {
        return instant.isBefore(t.instant);
    }

    public boolean isAfter(HODateTime t) {
        return instant.isAfter(t.instant);
    }

    /**
     * Internal class representing HT's season and week
     */
    public static class HTWeek {
        /**
         * season number [1..]
         */
        public int season;
        /**
         * week number [1..16]
         */
        public int week;

        public HTWeek(int season, int week){
            this.season = season;
            this.week = week;
        }

        public static HTWeek fromString(String s) {
            var nr = s.split(" ");
            if (nr.length == 2) {
                return new HTWeek(
                    Integer.parseInt(nr[0]),
                    Integer.parseInt(nr[1])
                );
            } else {
                return new HTWeek(0,0);
            }
        }
    }

    /**
     * Convert to absolut HT's season and week (same as swedish's league season)
     * @return HTWeek
     */
    public HTWeek toHTWeek() {
        var dayDiff= ChronoUnit.DAYS.between(htStart.instant, instant);
        var ret = new HTWeek(
                (int)(dayDiff / (16 * 7) + 1),
                (int) ((dayDiff % (16 * 7)) / 7) + 1
        );
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

