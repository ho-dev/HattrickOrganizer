package core.util;

import java.util.Locale;

public enum Languages {
    BULGARIAN(1),
    CATALAN(2),
    CHINESE(3),
    CZECH(4),
    DANISH(5),
    DEUTSCH(6),
    ENGLISH(7),
    FINNISH(8),
    FRENCH(9);
//    Galego,
//            Georgian,
//    Greeklish,
//    Hangul(Korean),
//    Hebrew,
//    Hrvatski(Croatian),
//    Indonesian,
//            Italiano,
//    Japanese,
//            Latvija,
//    Lithuanian,
//            Magyar,
//    Nederlands,
//            Norsk,
//    Persian,
//            Polish,
//    Portugues,
//            PortuguesBrasil,
//    Romanian,
//            Russian,
//    Serbian(Cyrillic),
//    Slovak,
//            Slovenian,
//    Spanish,
//            Spanish_sudamericano,
//    Svenska,
//            Turkish,
//    Ukranian,
//            Vlaams;

    private int value;

    Languages(int value) {
        this.value = value;
    }

    public static Languages lookup(String language) {
        for (Languages l : Languages.values()) {
            if (l.name().equalsIgnoreCase(language)) {
                return l;
            }
        }
        return ENGLISH;
    }


    public int getValue() {
        return value;
    }

    public Locale getLocale() {
        return switch(this){
            case FRENCH -> Locale.FRENCH;
            default -> Locale.ENGLISH;
        };
    }

}
