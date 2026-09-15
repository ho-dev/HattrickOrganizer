package core.model.match;

import core.model.TranslationFacility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.mockStatic;

class MatchResultFormatterTest {

    private static final String SEPARATOR = "-";

    private MockedStatic<TranslationFacility> translationFacility;

    @BeforeEach
    void setup() {
        translationFacility = mockStatic(TranslationFacility.class);
        translationFacility
            .when(() -> TranslationFacility.tr(MatchResultFormatter.TRANSLATION_KEY_SEPARATOR))
            .thenReturn(SEPARATOR);
    }

    @AfterEach
    void tearDown() {
        translationFacility.close();
    }

    private static Stream<Arguments> format() {
        return Stream.of(
            // regular result
            of(0, 0, "", " 0 - 0"),
            // regular result
            of(2, 1, "", " 2 - 1"),
            // double-digit home score does not get a leading space
            of(10, 2, "", "10 - 2"),
            // result extension
            of(2, 1, "a.e.t.", " 2 - 1 a.e.t."),
            // unknown home score
            of(-1, 2, "", "   - "),
            // unknown away score
            of(2, -1, "", "   - ")
        );
    }

    @ParameterizedTest
    @MethodSource
    void format(int homeGoals, int awayGoals, String resultExtensionAbbreviation, String expected) {
        final var result = MatchResultFormatter.format(homeGoals, awayGoals, resultExtensionAbbreviation);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void format_withNullAsResultExtension_throws() {
        assertThatThrownBy(() ->
            MatchResultFormatter.format(0, 0, null))
            .isInstanceOf(NullPointerException.class);
    }
}
