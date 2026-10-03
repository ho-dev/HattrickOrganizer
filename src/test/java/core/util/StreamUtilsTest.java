package core.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StreamUtilsTest {

    @Test
    void findOneReturnsEmptyOptionalForEmptyStream() {
        Optional<String> result = StreamUtils.findOne(Stream.empty());

        assertThat(result).isEmpty();
    }

    @Test
    void findOneReturnsElementForStreamWithOneElement() {
        Optional<String> result = StreamUtils.findOne(Stream.of("foo"));

        assertThat(result).contains("foo");
    }

    @Test
    void findOneThrowsExceptionForStreamWithMoreThanOneElement() {
        assertThatThrownBy(() -> StreamUtils.findOne(Stream.of("foo", "bar")))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Found more than one element.");
    }

    @Test
    void concatReturnsEmptyStreamForNoStreams() {
        Stream<String> result = StreamUtils.concat();

        assertThat(result).isEmpty();
    }

    @Test
    void concatReturnsElementsOfSingleStream() {
        Stream<String> result = StreamUtils.concat(Stream.of("foo", "bar"));

        assertThat(result).containsExactly("foo", "bar");
    }

    @Test
    void concatConcatenatesMultipleStreams() {
        Stream<String> result = StreamUtils.concat(
            Stream.of("foo", "bar"),
            Stream.empty(),
            Stream.of("baz"),
            Stream.of("qux", "quux")
        );

        assertThat(result).containsExactly("foo", "bar", "baz", "qux", "quux");
    }
}
