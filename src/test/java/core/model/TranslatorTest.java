package core.model;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.of;

class TranslatorTest {

    private static final List<String> SUPPORTED_LANGUAGES = List.of(
            "Bulgarian",
            "Catalan",
            "Chinese",
            "Czech",
            "Danish",
            "English",
            "Estonian",
            "Finnish",
            "French",
            "Galego",
            "Georgian",
            "German",
            "Greek",
            "Hangul(Korean)",
            "Hebrew",
            "Hrvatski(Croatian)",
            "Indonesian",
            "Italiano",
            "Japanese",
            "Latvija",
            "Lithuanian",
            "Magyar",
            "Nederlands",
            "Norsk",
            "Persian",
            "Polish",
            "Portugues",
            "PortuguesBrasil",
            "Romanian",
            "Russian",
            "Serbian(Cyrillic)",
            "Slovak",
            "Slovenian",
            "Spanish",
            "Spanish(AR)",
            "Svenska",
            "Turkish",
            "Ukranian",
            "Vlaams");

    private static Stream<Arguments> whitelist() {
        return Stream.concat(
                SUPPORTED_LANGUAGES.stream().map(Arguments::of),
                Stream.of(Arguments.of(Translator.LANGUAGE_NO_TRANSLATION))
        );
    }

    private static Stream<Arguments> blacklist() {
        return Stream.of(
                        "Latin",
                        "FOOBAR",
                        "",
                        null)
                .map(Arguments::of);
    }

    @Test
    void getSupportedLanguages() {
        // when
        final var supportedLanguages = Translator.getSupportedLanguages();

        // then
        assertThat(supportedLanguages).isEqualTo(SUPPORTED_LANGUAGES.toArray(new String[0]));
    }

    @ParameterizedTest
    @MethodSource("whitelist")
    void isAvailable_true(String language) {
        // when
        final var available = Translator.isAvailable(language);

        // then
        assertThat(available).isTrue();
    }

    @ParameterizedTest
    @MethodSource("blacklist")
    void isAvailable_false(String language) {
        // when
        final var available = Translator.isAvailable(language);

        // then
        assertThat(available).isFalse();
    }

    @ParameterizedTest
    @MethodSource("whitelist")
    void load_available(String language) {
        // when
        final var translator = Translator.load(language);

        // then
        assertThat(translator).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("blacklist")
    void load_missing_throws_exception(String language) {
        // when-then
        assertThatThrownBy(() -> Translator.load(language))
                .isInstanceOf(MissingResourceException.class);
    }

    @Test
    void loadDefault() {
        // when
        final var translator = Translator.loadDefault();

        // then
        assertThat(translator.getLanguage()).isEqualTo(Translator.LANGUAGE_DEFAULT);
    }

    @ParameterizedTest
    @MethodSource("whitelist")
    void getLanguage(String language) {
        // given
        final var translator = Translator.load(language);

        // when-then
        assertThat(translator.getLanguage()).isEqualTo(language);
    }

    @ParameterizedTest
    @MethodSource("whitelist")
    void getResourceBundle(String language) {
        // given
        final var translator = Translator.load(language);

        // when-then
        assertThat(translator.getResourceBundle()).isNotNull();
    }

    private static Stream<Arguments> translate() {
        return Stream.of(
                of("English", null, EMPTY),
                of("English", EMPTY, EMPTY),
                of("English", "ls.core.preferences.misc.language", "Language"),
                of("English", "ls.button.save", "Save"),
                of("English", "ls.player.shirtnumber", "Shirt number"),
                of("German", "ls.core.preferences.misc.language", "Sprache"),
                of("German", "ls.button.save", "Speichern"),
                of("German", "ls.player.shirtnumber", "Trikotnummer"),
                of(Translator.LANGUAGE_DEFAULT, "ls.core.preferences.misc.language", "Language"),
                of(Translator.LANGUAGE_DEFAULT, "ls.button.save", "Save"),
                of(Translator.LANGUAGE_DEFAULT, "ls.player.shirtnumber", "Shirt number")
        );
    }

    @ParameterizedTest
    @MethodSource
    void translate(String language, String key, String expectedTranslation) {
        // given
        final var translator = Translator.load(language);

        // when-then
        assertThat(translator.translate(key)).isEqualTo(expectedTranslation);
    }

    private static Stream<Arguments> translateWithVariables() {
        return Stream.of(
                of("English", "ls.teamanalyzer.bot_since", new String[]{"EVER"}, "since EVER"),
                of("English", "ls.teamanalyzer.league_position_val", ArrayUtils.toObject(new int[]{1, 2, 3}), "1 in 2 (3)")
        );
    }

    @ParameterizedTest
    @MethodSource
    void translateWithVariables(String language, String key, Object[] values, String expectedTranslation) {
        // given
        final var translator = Translator.load(language);

        // when-then
        assertThat(translator.translate(key, values)).isEqualTo(expectedTranslation);
    }
}
