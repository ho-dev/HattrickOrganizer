package core.util;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class StreamUtils {

    private StreamUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Returns the single element of the collection, or {@link Optional#empty()} if the collection is empty.
     *
     * @param collection the collection expected to contain at most one element
     * @return an Optional containing the single element, or {@link Optional#empty()}
     * @throws IllegalStateException if the collection contains more than one element
     */
    public static <T> Optional<T> findOne(Collection<T> collection) {
        return findOne(collection.stream());
    }

    /**
     * Returns the single element of the stream, or {@link Optional#empty()} if the stream is empty.
     * <p>
     * If the stream contains more than one element, an exception is thrown.
     * <p>
     * Why is this method useful?
     * <p>
     * Java provides {@link Stream#findFirst()} and {@link Stream#findAny()}, but these methods
     * do not detect unexpected multiple matches. This can lead to subtle bugs that may
     * only become apparent much later or remain unnoticed entirely.
     * <p>
     * This method is intended for cases where <b>zero or one</b> element is expected,
     * ensuring that unexpected multiple matches are detected immediately.
     *
     * @param stream the stream expected to contain at most one element
     * @return an Optional containing the single element, or {@link Optional#empty()}
     * @throws IllegalStateException if the stream contains more than one element
     */
    public static <T> Optional<T> findOne(Stream<T> stream) {
        return findOne(stream, () -> new IllegalStateException("Found more than one element."));
    }

    /**
     * Returns the single element of the collection, or {@link Optional#empty()} if the collection is empty.
     *
     * @param collection        the collection expected to contain at most one element
     * @param exceptionSupplier supplies the exception to throw if more than one element is found
     * @return an Optional containing the single element, or {@link Optional#empty()}
     * @throws RuntimeException if the collection contains more than one element
     */
    public static <T> Optional<T> findOne(Collection<T> collection, Supplier<? extends RuntimeException> exceptionSupplier) {
        return findOne(collection.stream(), exceptionSupplier);
    }

    /**
     * Returns the single element of the stream, or {@link Optional#empty()} if the stream is empty.
     *
     * @param stream            the stream expected to contain at most one element
     * @param exceptionSupplier supplies the exception to throw if more than one element is found
     * @return an Optional containing the single element, or {@link Optional#empty()}
     * @throws RuntimeException if the stream contains more than one element
     */
    public static <T> Optional<T> findOne(Stream<T> stream, Supplier<? extends RuntimeException> exceptionSupplier) {
        return stream.reduce((first, second) -> {
            throw exceptionSupplier.get();
        });
    }

    /**
     * Concatenates any number of streams into a single stream.
     * <p>
     * The order of the streams and their elements is preserved.
     *
     * @param streams the streams to concatenate
     * @param <T>     the common element type of the streams
     * @return a stream containing all elements of the given streams
     */
    @SafeVarargs
    public static <T> Stream<T> concat(Stream<? extends T>... streams) {
        return Stream.of(streams).flatMap(Function.identity());
    }
}
