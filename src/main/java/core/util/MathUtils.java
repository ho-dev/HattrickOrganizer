package core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MathUtils {

    private MathUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static void checkNonNegative(String role, double x) {
        if (!(x >= 0)) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
    }

    public static boolean fuzzyEquals(double a, double b, double tolerance) {
        checkNonNegative("tolerance", tolerance);
        return
              Math.copySign(a - b, 1.0) <= tolerance
    // copySign(x, 1.0) is a branch-free version of abs(x), but with different NaN semantics
     || (a == b) // needed to ensure that infinities equal themselves
      || (Double.isNaN(a) && Double.isNaN(b));
       }

    public static Optional<BigDecimal> average(List<BigDecimal> bigDecimals, RoundingMode roundingMode, int scale) {
        if (bigDecimals.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal sum = bigDecimals.stream().map(Objects::requireNonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Optional.of(sum.divide(BigDecimal.valueOf(bigDecimals.size()), scale, roundingMode));
    }
}
