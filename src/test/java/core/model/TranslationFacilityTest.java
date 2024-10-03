package core.model;

import org.junit.jupiter.api.Test;

import java.util.MissingResourceException;

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
    void tr_with_variables() {
        // given
        TranslationFacility.setLanguage("English");

        // when
        final var translation = TranslationFacility.tr("ls.teamanalyzer.bot_since", "EVER");

        // then
        assertThat(translation).isEqualTo("since EVER");
    }
}