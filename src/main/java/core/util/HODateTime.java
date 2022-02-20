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
    public static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("Europe/Stockholm");
    private static final DateTimeFormatter cl_Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(DEFAULT_TIMEZONE);
    private static final HODateTime htStart = HODateTime.fromHT("1997-09-22 00:00:00");

    public Instant instant;

    public HODateTime(Instant in) {
        this.instant = in;
    }

    public static HODateTime fromHT(String htString) {
        LocalDateTime htTime = LocalDateTime.parse(htString, cl_Formatter);
        return new HODateTime(htTime.atZone(DEFAULT_TIMEZONE).toInstant());
    }

    public static HODateTime fromDbTimestamp(Timestamp timestamp) {
        return new HODateTime(timestamp.toInstant());
    }

    public static HODateTime now() {
        return new HODateTime(Instant.now());
    }

    public String toHT() {
        return cl_Formatter.format(instant);
    }

    public String toLocaleDate() {
        var formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    public String toLocaleDateTime() {
        var formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    public Timestamp toDbTimestamp() {
        return Timestamp.from(instant);
    }

    @Override
    public int compareTo(@NotNull HODateTime o) {
        return instant.compareTo(o.instant);
    }

    public class HTWeek {
        public int season;
        public int week;
    }

    public HTWeek toHTWeek() {
        var dayDiff= ChronoUnit.DAYS.between(htStart.instant, instant);
        var ret = new HTWeek();
        ret.season = (int)(dayDiff / (16 * 7) + 1);
        ret.week = (int) ((dayDiff % (16 * 7)) / 7) + 1;
        return ret;
    }

    public HTWeek toLocaleHTWeek() {
        var ret = toHTWeek();
        ret.season += HOVerwaltung.instance().getModel().getBasics().getSeasonOffset();
        return ret;
    }
}

