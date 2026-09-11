package module.matches;

import core.model.TranslationFacility;

public enum MatchLocation {
    ALL,
    HOME,
    AWAY,
    NEUTRAL;

    public static String getText(MatchLocation matchLocation) {
        switch (matchLocation) {
            case ALL: return TranslationFacility.tr("ls.module.lineup.matchlocation.all");
            case HOME: return TranslationFacility.tr("ls.module.lineup.matchlocation.home");
            case AWAY: return TranslationFacility.tr("ls.module.lineup.matchlocation.away");
            case NEUTRAL: return TranslationFacility.tr("ls.module.lineup.matchlocation.neutral");
        }
        return "Text not found";
    }
}
