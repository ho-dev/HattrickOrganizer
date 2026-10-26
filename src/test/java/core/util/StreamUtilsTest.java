package core.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StreamUtilsTest {

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

    @Test
    void concatSupportsDifferentSubtypes() {
        Stream<Integer> integers = Stream.of(1, 2);
        Stream<Double> doubles = Stream.of(3.0, 4.0);

        Stream<Number> result = StreamUtils.concat(integers, doubles);

        assertThat(result).containsExactly(1, 2, 3.0, 4.0);
    }

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
    void findOneReturnsEmptyOptionalForEmptyCollection() {
        Optional<String> result = StreamUtils.findOne(List.<String>of());

        assertThat(result).isEmpty();
    }

    @Test
    void findOneReturnsElementForCollectionWithOneElement() {
        Optional<String> result = StreamUtils.findOne(List.of("foo"));

        assertThat(result).contains("foo");
    }

    @Test
    void findOneThrowsExceptionForCollectionWithMoreThanOneElement() {
        assertThatThrownBy(() -> StreamUtils.findOne(List.of("foo", "bar")))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Found more than one element.");
    }

    @Test
    void findOneThrowsSuppliedExceptionForStreamWithMoreThanOneElement() {
        assertThatThrownBy(() -> StreamUtils.findOne(
            Stream.of("foo", "bar"),
            () -> new IllegalArgumentException("Multiple elements found.")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Multiple elements found.");
    }

    @Test
    void findOneThrowsSuppliedExceptionForCollectionWithMoreThanOneElement() {
        assertThatThrownBy(() -> StreamUtils.findOne(
            List.of("foo", "bar"),
            () -> new IllegalArgumentException("Multiple elements found.")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Multiple elements found.");
    }

    @Test
    void findOneDoesNotInvokeExceptionSupplierForEmptyStream() {
        Optional<String> result = StreamUtils.findOne(
            Stream.empty(),
            () -> {
                throw new AssertionError("Exception supplier must not be invoked.");
            });

        assertThat(result).isEmpty();
    }

    @Test
    void findOneDoesNotInvokeExceptionSupplierForStreamWithOneElement() {
        Optional<String> result = StreamUtils.findOne(
            Stream.of("foo"),
            () -> {
                throw new AssertionError("Exception supplier must not be invoked.");
            });

        assertThat(result).contains("foo");
    }

    @Test
    void findOneDoesNotInvokeExceptionSupplierForEmptyCollection() {
        Optional<String> result = StreamUtils.findOne(
            List.of(),
            () -> {
                throw new AssertionError("Exception supplier must not be invoked.");
            });

        assertThat(result).isEmpty();
    }

    @Test
    void findOneDoesNotInvokeExceptionSupplierForCollectionWithOneElement() {
        Optional<String> result = StreamUtils.findOne(
            List.of("foo"),
            () -> {
                throw new AssertionError("Exception supplier must not be invoked.");
            });

        assertThat(result).contains("foo");
    }

    @Test
    void findOneThrowsExceptionForParallelStreamWithMoreThanOneElement() {
        assertThatThrownBy(() -> StreamUtils.findOne(
            Stream.of("foo", "bar", "baz").parallel()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Found more than one element.");
    }
}
