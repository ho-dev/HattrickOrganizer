package core.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public final class StreamUtils {

    private StreamUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> Optional<T> findOne(Stream<T> stream) {
        return stream.reduce((first, second) -> {
            throw new IllegalStateException("Found more than one element.");
        });
    }

    @SafeVarargs
    public static <T> Stream<T> concat(Stream<? extends T>... streams) {
        return Arrays.stream(streams).flatMap(Function.identity());
    }
}
