package core.util;

public class MathUtils {

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
}
