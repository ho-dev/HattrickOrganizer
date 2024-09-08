package core.model;

import core.file.FileLoader;
import core.util.HOLogger;
import core.util.Languages;
import core.util.UTF8Control;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Translator {

    public static final String LANGUAGE_RESOURCE_PATH = "language";
    public static final String LANGUAGE_DEFAULT = "English";
    public static final String LANGUAGE_NO_TRANSLATION = "NoTranslation";
    public static final String LANGUAGE_FILE_EXTENSION = "properties";

    private static final String LANGUAGE_SUPPORTED_LANGUAGES_FILENAME = "ListLanguages.txt";
    private static final String PATH_SEPARATOR = "/";
    private static final String EXTENSION_SEPARATOR = ".";

    private final String language;
    private final ResourceBundle resourceBundle;
    private final Locale locale;

    public Translator(String language, ResourceBundle resourceBundle, Locale locale) {
        this.language = language;
        this.resourceBundle = resourceBundle;
        this.locale = locale;
    }

    public static String[] getSupportedLanguages() {
        String[] files = null;

        try {
            InputStream is = HOVerwaltung.class.getClassLoader().getResourceAsStream(LANGUAGE_RESOURCE_PATH + PATH_SEPARATOR + LANGUAGE_SUPPORTED_LANGUAGES_FILENAME);
            assert is != null;
            Scanner s = new Scanner(is);
            ArrayList<String> list = new ArrayList<>();
            while (s.hasNext()) {
                list.add(s.next());
            }
            s.close();

            files = list.toArray(new String[0]);

        } catch (Exception e) {
            HOLogger.instance().log(HOVerwaltung.class, e);
        }

        return files;
    }

    public static boolean isAvailable(String language) {
        InputStream translationFile = FileLoader.instance().getFileInputStream(LANGUAGE_RESOURCE_PATH + PATH_SEPARATOR + language + EXTENSION_SEPARATOR + LANGUAGE_FILE_EXTENSION);
        return translationFile != null;
    }

    public static Translator load(String language) {
        return new Translator(language, getResourceBundle(language), Languages.lookup(language).getLocale());
    }

    private static ResourceBundle getResourceBundle(String language) {
        final String baseName = LANGUAGE_RESOURCE_PATH + EXTENSION_SEPARATOR + language;
        try {
            return ResourceBundle.getBundle(baseName, new UTF8Control());
        } catch (UnsupportedOperationException e) {
            // ResourceBundle.Control not supported in named modules in JDK9+
            return ResourceBundle.getBundle(baseName);
        }
    }

    public static Translator loadDefault() {
        return load(LANGUAGE_DEFAULT);
    }

    public String getLanguage() {
        return this.language;
    }

    public ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }

    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Returns the String connected to the active language file or connected to the english language file. Returns !key!
     * if the key can not be found.
     *
     * @param key Key to be searched in language files
     *
     * @return String connected to the key or !key! if nothing can be found in language files
     */
    public String translate(String key) {
        String temp = null;
        try {
            if (resourceBundle != null) {
                temp = resourceBundle.getString(key);
            }
        } catch (RuntimeException e) {
            // Do nothing, it just throws error if key is missing.
        }
        if (temp != null)
            return temp;

        // Search in properties of default language if nothing found and active language not the default
        if (!isDefaultLanguage() && !isNoTranslation()) {
            ResourceBundle tempBundle = loadDefault().getResourceBundle();

            try {
                temp = tempBundle.getString(key);
            } catch (RuntimeException e) {
                // Ignore
            }

            if (temp != null)
                return temp;
        }

        HOLogger.instance().warning(getClass(), "translate: '" + key + "' not found!");
        return "!" + key + "!";
    }

    private boolean isDefaultLanguage() {
        return getLanguage().equalsIgnoreCase(LANGUAGE_DEFAULT);
    }

    private boolean isNoTranslation() {
        return getLanguage().equalsIgnoreCase(LANGUAGE_NO_TRANSLATION);
    }

    /**
     * Gets a parameterized message for the current language.
     *
     * @param key    the key for the message in the language file.
     * @param values the values for the message
     *
     * @return the message for the specified key where the placeholders are replaced by the given value(s).
     */
    public String translate(String key, Object... values) {
        String str = translate(key);

        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(locale);
        formatter.applyPattern(str);

        return formatter.format(values);
    }
}
