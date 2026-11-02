package core.util;

import core.model.HOVerwaltung;

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

    public HTWeek(int season, int week) {
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

    public boolean equals(HTWeek other) {
        return this.week == other.week && this.season == other.season;
    }

    public static HTWeek fromLocaleHTWeek(LocaleHTWeek localeHtWeek) {
        return new HTWeek(localeHtWeek.season - HOVerwaltung.instance().getModel().getBasics().getSeasonOffset(), localeHtWeek.week);
    }
}
