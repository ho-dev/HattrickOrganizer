package module.matches;

import core.datatype.ComboItem;

import static core.util.Helper.getTranslation;

public enum MatchLocation implements ComboItem {
    ALL(0, getTranslation("ls.module.lineup.matchlocation.all")),
    HOME(1, getTranslation("ls.module.lineup.matchlocation.home")),
    AWAY(2, getTranslation("ls.module.lineup.matchlocation.away")),
    NEUTRAL(3, getTranslation("ls.module.lineup.matchlocation.neutral"));

    private int id;
    private String text;

    MatchLocation(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }
}
