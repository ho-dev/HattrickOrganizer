package core.util;

/**
 * Country/League's local season and week
 *
 * @param season number [1..]
 * @param week   number [1..16]
 */
public record LocalHTWeek(int season, int week) {
    public long weeksSinceLeagueStart() {
        return (season - 1) * 16L + week - 1L;
    }

    public String getDisplayString() {
        return "%d (%d)".formatted(season, week);
    }
}
