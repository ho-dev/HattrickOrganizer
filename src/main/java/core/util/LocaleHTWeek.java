package core.util;

/**
 * League's locale season and week
 * @param season number [1..]
 * @param week number [1..16]
 */
public record LocaleHTWeek ( int season, int week) {
    public long weeksSinceLeagueStart() {
        return (season - 1) * 16L + week - 1L;
    }

//    @Override
//    public String toString() {
//        return season + " (" + week + ")";
//    }

    public boolean equals(LocaleHTWeek other) {
        return this.week == other.week && this.season == other.season;
    }
}
