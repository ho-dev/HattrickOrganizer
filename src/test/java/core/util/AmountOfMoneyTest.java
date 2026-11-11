package core.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

class AmountOfMoneyTest {

    private static Locale originalLocale;

    @BeforeAll
    static void beforeAll() {
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("sv-SE"));
    }

    @AfterAll
    static void afterAll() {
        Locale.setDefault(originalLocale);
    }

    private static Stream<Arguments> test_parse() {
        return Stream.of(
            of("10", new AmountOfMoney(10)),
            of("-10", new AmountOfMoney(-10)),
            of("4075888", new AmountOfMoney(4075888)),
            of("-4075888", new AmountOfMoney(-4075888)),
            of("-4074090", new AmountOfMoney(-4074090))
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_parse(String string, AmountOfMoney expected) {
        assertThat(AmountOfMoney.Companion.parse(string)).isEqualTo(expected);
    }
}
