package core.model;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TranslationFacilityTest {

    @Test
    void getTranslator() {
        // given
        TranslationFacility.setLanguage(Translator.LANGUAGE_DEFAULT);

        // when
        final var translator = TranslationFacility.getTranslator();

        // then
        assertThat(translator.getLanguage()).isEqualTo(Translator.LANGUAGE_DEFAULT);
    }

    @Test
    void setLanguage_with_German() {
        // given
        TranslationFacility.setLanguage("German");

        // when
        final var translator = TranslationFacility.getTranslator();

        // then
        assertThat(translator.getLanguage()).isEqualTo("German");
    }

    @Test
    void setLanguage_unknown_throws_exception() {
        // when-then
        assertThatThrownBy(() -> TranslationFacility.setLanguage("foobar")).isInstanceOf(MissingResourceException.class);
    }

    @Test
    void setTranslator() {
        // when
        TranslationFacility.setTranslator(null);

        // then
        assertThat(TranslationFacility.getTranslator()).isNotNull();
        assertThat(TranslationFacility.getTranslator().getLanguage()).isEqualTo(Translator.LANGUAGE_NO_TRANSLATION);
    }

    @Test
    void tr_with_initial_translator_results_in_no_translation() {
        // given
        TranslationFacility.setTranslator(null);

        // when
        final var translation = TranslationFacility.tr("ls.button.save");

        // then
        assertThat(translation).isEqualTo("!ls.button.save!");
    }

    @Test
    void tr() {
        // given
        final var key = "key";
        final var expectedTranslation = "value";
        Translator translator = createTranslator("Custom", Map.ofEntries(Map.entry(key, expectedTranslation)));
        TranslationFacility.setTranslator(translator);

        // when
        final var translation = TranslationFacility.tr(key);

        // then
        assertThat(translation).isEqualTo(expectedTranslation);
    }

    @Test
    void trSingularOrPlural() {
        // given
        final var keySingular = "singular";
        final var keyPlural = "plural";
        final var expectedTranslationSingular = "person";
        final var expectedTranslationPlural = "people";
        Translator translator = createTranslator("Custom",
            Map.ofEntries(Map.entry(keySingular, expectedTranslationSingular), Map.entry(keyPlural, expectedTranslationPlural)));
        TranslationFacility.setTranslator(translator);

        // when
        final var translationSingular = TranslationFacility.trSingularOrPlural(true, keySingular, keyPlural);
        final var translationPlural = TranslationFacility.trSingularOrPlural(false, keySingular, keyPlural);

        // then
        assertThat(translationSingular).isEqualTo(expectedTranslationSingular);
        assertThat(translationPlural).isEqualTo(expectedTranslationPlural);
    }

    @Test
    void tr_with_variables() {
        // given
        TranslationFacility.setLanguage("English");

        // when
        final var translation = TranslationFacility.tr("ls.teamanalyzer.bot_since", "EVER");

        // then
        assertThat(translation).isEqualTo("since EVER");
    }

    @SuppressWarnings("SameParameterValue")
    private static Translator createTranslator(String language, Map<String, String > translations) {
        return new Translator(language, new ResourceBundleFromMap(translations));
    }

    private static final class ResourceBundleFromMap extends ResourceBundle {
        private final Map<String, String> translations;
        public ResourceBundleFromMap(Map<String, String> translations) {
            super();
            this.translations = translations;
        }
        @Override
        protected Object handleGetObject(@NotNull String key) {
            return Optional.ofNullable(translations.get(key)).orElseThrow();
        }

        @Override
        public @NotNull Enumeration<String> getKeys() {
            throw new NotImplementedException();
        }
    }
}
