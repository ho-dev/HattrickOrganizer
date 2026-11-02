package core.util;

import core.model.HOVerwaltung;

/**
 * Hattrick's absolute season and week (from Swedish league)
 *
 * @param season number [1..]
 * @param week   number [1..16]
 */
public record HTWeek(int season, int week) {
    public static HTWeek fromLocaleHTWeek(LocaleHTWeek localeHtWeek) {
        return new HTWeek(localeHtWeek.season() - HOVerwaltung.instance().getModel().getBasics().getSeasonOffset(), localeHtWeek.week());
    }
}
