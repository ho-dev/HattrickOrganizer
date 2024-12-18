package core.util;

import core.model.HOVerwaltung;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class HODateTime implements Comparable<HODateTime> {

    /**
     * time zone of hattrick
     */
    public static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("Europe/Stockholm");

    /**
     * Date time format of chpp files
     */
    private static final DateTimeFormatter cl_Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(DEFAULT_TIMEZONE);
    private static final DateTimeFormatter cl_ShortFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(DEFAULT_TIMEZONE);

    /**
     * the birthday of hattrick
     * Monday, the 22nd of September 1997 (CET)
     *
     */
    public static final HODateTime HT_START = HODateTime.fromHT("1997-09-22 00:00:00");


    /**
     * internal time representation
     */
    public final Instant instant;

    /**
     * create an HODateTime instance (should it be private?)
     *
     * @param in Instant
     */
    public HODateTime(@NotNull Instant in) {
        this.instant = in;
    }
    public HODateTime(@NotNull HODateTime in){this.instant=in.instant;}

    public Instant getInstant() {
        return instant;
    }

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Create instance from HT (chpp) string
     *
     * @param htString HT string
     * @return HODateTime
     */
    public static HODateTime fromHT(String htString) {
        if (htString != null && !htString.isEmpty()) {
            try {
                LocalDateTime htTime = LocalDateTime.parse(htString, cl_Formatter);
                return new HODateTime(htTime.atZone(DEFAULT_TIMEZONE).toInstant());
            }
            catch (Exception ignored){
                var date = LocalDate.parse(htString, cl_ShortFormatter).atStartOfDay();
                return new HODateTime(date.atZone(DEFAULT_TIMEZONE).toInstant());
            }
        }
        return null;
    }

    /**
     * Create an instance from database timestamp
     *
     * @param timestamp database timestamp
     * @return HODateTime
     */
    public static HODateTime fromDbTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            return new HODateTime(timestamp.toInstant());
        }
        return null;
    }

    /**
     * Convert to database timestamp
     * @param time HODatetime
     * @return Timestamp
     */
    public static Timestamp toDbTimestamp(HODateTime time) {
        if (time != null) {
            return time.toDbTimestamp();
        }
        return null;
    }

    /**
     * Create an instance representing current time
     *
     * @return HODateTime
     */
    public static HODateTime now() {
        return new HODateTime(Instant.now());
    }

    /**
     * Create an instance from HT season, week
     *
     * @param week ht season, week
     * @return date time of the start of hattrick week
     */
    public static HODateTime fromHTWeek(HTWeek week) {
        return new HODateTime(HT_START.instant.plus(Duration.ofDays(((week.season - 1) * 16L + week.week - 1) * 7)));
    }

    /**
     * Convert to seconds since epoch (1-1-1070)
     * @param ts HODateTime
     * @return long, seconds since epoch
     */
    public static long toEpochSecond(HODateTime ts){
        if ( ts!= null) return ts.instant.getEpochSecond();
        return 0L;
    }

    /**
     * Convert to HT (chpp) string representation
     *
     * @return String
     */
    public String toHT() {
        return cl_Formatter.format(instant);
    }

    /**
     * Convert to date only string, using user's locale setting (system default)
     *
     * @return String
     */
    public String toLocaleDate() {
        return toLocaleDate(FormatStyle.MEDIUM);
    }

    /**
     * Convert date only string
     * @param style, FormatStyle
     * @return String
     */
    public String toLocaleDate(FormatStyle style) {
        var formatter = DateTimeFormatter.ofLocalizedDate(style).withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    /**
     * Convert to date and time string, using user's locale setting (system default)
     *
     * @return String
     */
    public String toLocaleDateTime() {
        return toLocaleDateTime(FormatStyle.MEDIUM);
    }

    /**
     * Convert HODateTime to date and time string
     * @param in HODateTime
     * @return String
     */
    public static String toLocaleDateTime(HODateTime in){
        if ( in != null) return in.toLocaleDateTime(FormatStyle.MEDIUM);
        return "";
    }

    /**
     * Convert to date and time string
     * @param style, FormatStyle
     * @return String
     */
    public String toLocaleDateTime(FormatStyle style) {
        var formatter = DateTimeFormatter.ofLocalizedDateTime(style).withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    /**
     * Convert HODateTime to date and time string
     * @param in, HODateTime
     * @param style, FormatStyle
     * @return String
     */
    public static String toLocaleDateTime(HODateTime in, FormatStyle style){
        if ( in != null) return in.toLocaleDateTime(style);
        return "";
    }

    /**
     * Convert to database timestamp
     *
     * @return Timestamp
     */
    public Timestamp toDbTimestamp() {
        return Timestamp.from(instant);
    }

    /**
     * Compare HODateTime instances
     *
     * @param o other HODateTime instance
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(@NotNull HODateTime o) {
        return instant.compareTo(o.instant);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof HODateTime that) {
            return that.canEqual(this) && instant.equals(that.instant);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return instant.hashCode();
    }

    protected boolean canEqual(Object o) {
        return o instanceof HODateTime;
    }

    public HODateTime minus(int i, ChronoUnit unit) {
        return new HODateTime(instant.minus(i, unit));
    }

    public HODateTime plus(int i, ChronoUnit unit) {
        return new HODateTime(instant.plus(i, unit));
    }

    public boolean isBefore(HODateTime t) {
        return instant.isBefore(t.instant);
    }

    public boolean isAfter(HODateTime t) {
        return instant.isAfter(t.instant);
    }

    public static Duration between(HODateTime from, HODateTime to) {
        return Duration.between(from.instant, to.instant);
    }

    /**
     * add amount of days and reset local time (if daylight saving happened)
     * @param i amount of days (may be negativ)
     * @return HODateTime
     */
    public HODateTime plusDaysAtSameLocalTime(long i) {
        int hour = instant.atZone(DEFAULT_TIMEZONE).getHour();
        int minute = instant.atZone(DEFAULT_TIMEZONE).getMinute();
        int second = instant.atZone(DEFAULT_TIMEZONE).getSecond();

        var ret = new HODateTime(instant.plus(i, ChronoUnit.DAYS).atZone(DEFAULT_TIMEZONE)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .toInstant());

        // day switch
        var diff = Duration.between(instant, ret.instant);
        if (diff.compareTo( Duration.ofHours(i*24 + 2)) > 0 ) {
            ret = ret.minus(1, ChronoUnit.DAYS);
        }
        else if (diff.compareTo( Duration.ofHours(i*24 - 2)) < 0 ) {
            ret = ret.plus(1, ChronoUnit.DAYS);
        }
        return ret;
    }

    public DayOfWeek DayOfWeek() {
        return instant.atZone(DEFAULT_TIMEZONE).getDayOfWeek();
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

        public HTWeek(int season, int week) {
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
                return new HTWeek(0, 0);
            }
        }

        public long sinceOrigin() {
            return (season-1) * 16L + week -1L;
        }

        @Override
        public String toString(){
            return season + " (" + week + ")";
        }
    }

    /**
     * Convert to absolut HT's season and week (same as swedish's league season)
     *
     * @return HTWeek
     */
    public HTWeek toHTWeek() {
        return calcHTWeek(instant);
    }

    private static HTWeek calcHTWeek(Instant instant){
        var dayDiff = ChronoUnit.DAYS.between(HT_START.instant, instant);
        return new HTWeek(
                (int) (dayDiff / (16 * 7) + 1),
                (int) ((dayDiff % (16 * 7)) / 7) + 1
        );
    }

    private static Duration durationBetweenWeekStartAndTrainingDate =null;

    /**
     * Convert to locale training season and week
     * training date differs from start of weeks
     * @return local training season/week
     */
    public HTWeek toTrainingWeek() {
        if (durationBetweenWeekStartAndTrainingDate == null) {
            var xtra = HOVerwaltung.instance().getModel().getXtraDaten();
            if (xtra != null) {
                var nextTrainingDate = xtra.getNextTrainingDate();
                var previousTrainingDate = nextTrainingDate.minus(7, ChronoUnit.DAYS);
                // check daylight saving
                var hourNext = nextTrainingDate.instant.atZone(DEFAULT_TIMEZONE).getHour();
                var hourPrevious = previousTrainingDate.instant.atZone(DEFAULT_TIMEZONE).getHour();
                var daylightSavingCorrection = 0;
                if (hourNext == hourPrevious + 1) {
                    daylightSavingCorrection = -1;
                } else if (hourNext == hourPrevious - 1) {
                    daylightSavingCorrection = 1;
                } else if (hourNext == hourPrevious + 23) {
                    daylightSavingCorrection = 1;
                } else if (hourNext == hourPrevious - 23) {
                    daylightSavingCorrection = -1;
                }
                var startOfWeek = HODateTime.fromHTWeek(nextTrainingDate.toHTWeek());
                durationBetweenWeekStartAndTrainingDate = HODateTime.between(startOfWeek, nextTrainingDate).minus(daylightSavingCorrection, ChronoUnit.HOURS);
            }
            else {
                durationBetweenWeekStartAndTrainingDate = Duration.ofSeconds(0);
            }
        }

        var trainingDateRelatedDate = new HODateTime(this.instant.minus(durationBetweenWeekStartAndTrainingDate));
        return trainingDateRelatedDate.toLocaleHTWeek();
    }

    /**
     * Convert to locale HT's season and week (user's league season)
     *
     * @return HTWeek
     */
    public HTWeek toLocaleHTWeek() {
        var ret = toHTWeek();
        ret.season += HOVerwaltung.instance().getModel().getBasics().getSeasonOffset();
        return ret;
    }

    public static BigDecimal daysFromNow(HODateTime to, int scale) {
        return daysBetween(HODateTime.now(), to, scale);
    }

    public static BigDecimal daysToNow(HODateTime from, int scale) {
        return daysBetween(from, HODateTime.now(), scale);
    }

    public static BigDecimal daysBetween(HODateTime from, HODateTime to, int scale) {
        return BigDecimal.valueOf(between(from, to).toMillis())
                .divide(BigDecimal.valueOf(TimeUnit.DAYS.toMillis(1)), scale, RoundingMode.HALF_UP);
    }

    public HODateTime nextLocalDay() {
        final var localDate = getLocalDateTime().toLocalDate();
        final var nextDay = localDate.plusDays(1);
        final var newInstant = Instant.from(nextDay.atStartOfDay(ZoneId.systemDefault()));
        return new HODateTime(newInstant);
    }

    public static class HODuration implements Comparable<HODuration>{
        public int seasons;
        public int days;

        public HODuration(int inSeasons, int inDays) {
            this.seasons = inSeasons;
            this.days = inDays;
            while (days > 111) {
                days -= 112;
                seasons++;
            }
            while (days < 0) {
                days += 112;
                seasons--;
            }
        }

        public static HODuration between(HODateTime from, HODateTime to) {
            return new HODuration(0, (int) Duration.between(from.instant, to.instant).plus(12, ChronoUnit.HOURS).toDays());
        }

        public HODuration plus(HODuration diff) {
            return new HODuration(this.seasons + diff.seasons, this.days + diff.days);
        }

        public HODuration minus(HODuration diff) {
            return new HODuration(this.seasons - diff.seasons, this.days - diff.days);
        }

        public String toString(){
            return seasons + " (" + days + ")";
        }
        public double toDouble() { return seasons + days/112.; }

        @Override
        public int compareTo(@NotNull HODateTime.HODuration o) {
            int ret = Integer.compare(this.seasons, o.seasons);
            if (ret==0) ret = Integer.compare(this.days, o.days);
            return ret;
        }
    }

    public String toString(){
        return this.instant.toString();
    }
}

