package module.matches;

import core.datatype.ComboItem;

import static core.util.Helper.getTranslation;

public enum MatchLocation {
    ALL,
    HOME,
    AWAY,
    NEUTRAL;

    public static String getText(MatchLocation matchLocation) {
        switch (matchLocation) {
            case ALL: return getTranslation("ls.module.lineup.matchlocation.all");
            case HOME: return getTranslation("ls.module.lineup.matchlocation.home");
            case AWAY: return getTranslation("ls.module.lineup.matchlocation.away");
            case NEUTRAL: return getTranslation("ls.module.lineup.matchlocation.neutral");
        }
        return "Text not found";
    }
}
