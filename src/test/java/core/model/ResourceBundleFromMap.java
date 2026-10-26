package core.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ResourceBundleFromMap extends ResourceBundle {
    private final Map<String, String> translations;

    public ResourceBundleFromMap(Map<String, String> translations) {
        super();
        this.translations = translations;
    }

    @Override
    protected Object handleGetObject(@NotNull String key) {
        return Optional.ofNullable(translations.get(key))
            .orElseThrow(() -> new AssertionError("Missing test translation: '%s'".formatted(key)));
    }

    @Override
    public @NotNull Enumeration<String> getKeys() {
        return Collections.enumeration(translations.keySet());
    }

    public static Translator createTranslator(String language, Map<String, String > translations) {
        return new Translator(language, new ResourceBundleFromMap(translations));
    }
}
