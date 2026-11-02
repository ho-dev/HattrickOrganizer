package core.util;

/**
 * Internal class representing league's locale season and week
 */
public class LocaleHTWeek {
    /**
     * season number [1..]
     */
    public int season;
    /**
     * week number [1..16]
     */
    public int week;

    public LocaleHTWeek(int season, int week) {
        this.season = season;
        this.week = week;
    }

    public long weeksSinceLeagueStart() {
        return (season - 1) * 16L + week - 1L;
    }

    @Override
    public String toString() {
        return season + " (" + week + ")";
    }

    public boolean equals(LocaleHTWeek other) {
        return this.week == other.week && this.season == other.season;
    }
}
