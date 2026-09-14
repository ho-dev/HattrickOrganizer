package core.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class HtmlUtilsTest {

    private static Stream<Arguments> stringToHtml() {
        return Stream.of(
            arguments(null, null),
            arguments("", null), arguments("Hello World", "<html>Hello World</html>"),
            arguments("<b>Hello & World</b>", "<html>&lt;b&gt;Hello &amp; World&lt;/b&gt;</html>"),
            arguments("First line\nSecond line", "<html>First line<br>Second line</html>"),
            arguments("First line\r\nSecond line", "<html>First line<br>Second line</html>")
        );
    }

    @ParameterizedTest
    @MethodSource("stringToHtml")
    void stringToHtml(String plainText, String expected) {
        assertThat(HtmlUtils.stringToHtml(plainText)).isEqualTo(expected);
    }
}
