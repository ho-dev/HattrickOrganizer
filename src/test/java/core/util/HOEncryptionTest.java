package core.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

class HOEncryptionTest {

    private static Stream<Arguments> data() {
        return Stream.of(
            of("", ""),
            of("01234567890", "*),+.-0/21*"),
            of("ABCDEFGHIJLKMNOPQRSTUVWXQZ", "9<;>=@?BADFCEHGJILKNMPORIT"),
            of("abcdefghijlkmnopqrstuvwxqz", "Y~[^]`_badfcehgjilknmporit"),
            of("HO", "BG"),
            of("HattrickOrganizer", "BYnnla[cGl_Yhat]l")
        );
    }

    private static Stream<Arguments> notEncryptableCharacters() {
        return Stream.of(
            of(","),
            of("."),
            of("-"),
            of(";"),
            of(":"),
            of("_"),
            of("!"),
            of("\""),
            of("§"),
            of("$"),
            of("%"),
            of("&"),
            of("/"),
            of("("),
            of(")"),
            of("="),
            of("?"),
            of("`"),
            of("²"),
            of("³"),
            of("{"),
            of("["),
            of("]"),
            of("}"),
            of("\\"),
            of("¸"),
            of("+"),
            of("*"),
            of("~"),
            of("#"),
            of("'"),
            of("|"),
            of("<"),
            of(">"),
            of("^"),
            of("°"),
            of("€"),
            of("@"),
            of(" ")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void cryptString(String cleartext, String ciphertext) {
        assertThat(HOEncryption.cryptString(cleartext)).isEqualTo(ciphertext);
    }

    @Test
    void cryptString_with_null_result_is_empty() {
        assertThat(HOEncryption.cryptString(null)).isEqualTo(StringUtils.EMPTY);
    }

    @ParameterizedTest
    @MethodSource("data")
    void decryptString(String cleartext, String ciphertext) {
        assertThat(HOEncryption.decryptString(ciphertext)).isEqualTo(cleartext);
    }

    @Test
    void decryptString_with_null_result_is_empty() {
        assertThat(HOEncryption.decryptString(null)).isEqualTo(StringUtils.EMPTY);
    }

    @ParameterizedTest
    @MethodSource("notEncryptableCharacters")
    void cryptString_not_allowed_characters_result_is_null(String notAllowedCharacter) {
        assertThat(StringUtils.length(notAllowedCharacter)).isEqualTo(1);
        assertThat(HOEncryption.cryptString(notAllowedCharacter)).isNull();
    }
}
