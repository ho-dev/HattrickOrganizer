package core.model;

import static java.util.Objects.requireNonNullElseGet;

public class TranslationFacility {

    private static final String INITIAL_TRANSLATOR = Translator.LANGUAGE_NO_TRANSLATION;

    private static Translator translator;
    static {
        translator = Translator.load(INITIAL_TRANSLATOR);
    }

    private TranslationFacility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Translator getTranslator() {
        return translator;
    }

    public static void setLanguage(String language) {
        setTranslator(Translator.load(language));
    }

    public static void setTranslator(Translator newTranslator) {
        translator = requireNonNullElseGet(newTranslator, () -> Translator.load(INITIAL_TRANSLATOR));
    }

    public static String tr(String key) {
        return translator.translate(key);
    }

    public static String tr(String key, Object... values) {
        return translator.translate(key, values);
    }
}
