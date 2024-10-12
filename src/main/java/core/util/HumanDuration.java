package core.util;

import core.model.TranslationFacility;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;

@Builder
@EqualsAndHashCode
@Getter
public class HumanDuration {

    private static final String DURATION_SUB_FORMAT = "%s%s";

    private long days;
    private long hours;
    private long minutes;
    private long seconds;

    public static HumanDuration of(Duration duration) {
        return fromSeconds(duration.toSeconds());
    }

    public static HumanDuration fromSeconds(long duration) {
        long seconds = duration;
        final long days = seconds / 86400L;
        seconds -= days * 86400L;
        final long hours = seconds / 3600L;
        seconds -= hours * 3600L;
        final long minutes = seconds / 60L;
        seconds -= minutes * 60L;
        return HumanDuration.builder()
                .days(days)
                .hours(hours)
                .minutes(minutes)
                .seconds(seconds)
                .build();
    }

    public String toHumanString() {
        ArrayList<String> strings = new ArrayList<>();
        if (days != 0) {
            final var unit = getLanguageString("Duration.days_abbreviation");
            strings.add(String.format(DURATION_SUB_FORMAT, days, unit));
        }
        if (hours != 0) {
            final var unit = getLanguageString("Duration.hours_abbreviation");
            strings.add(String.format(DURATION_SUB_FORMAT, hours, unit));
        }
        if (minutes != 0) {
            final var unit = getLanguageString("Duration.minutes_abbreviation");
            strings.add(String.format(DURATION_SUB_FORMAT, minutes, unit));
        }
        if (seconds != 0) {
            final var unit = getLanguageString("Duration.seconds_abbreviation");
            strings.add(String.format(DURATION_SUB_FORMAT, seconds, unit));
        }
        return String.join(", ", strings);
    }

    private static String getLanguageString(String key) {
        return TranslationFacility.tr(key);
    }
}
