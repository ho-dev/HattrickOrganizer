package core.util;

/**
 * Provides OS-specific utility functions.
 */
public class OSUtils {
    public final static String OS_NAME = System.getProperty("os.name").toLowerCase(java.util.Locale.ENGLISH);

    private OSUtils() {
    }

    /**
     * Checks whether the current OS is Mac.
     * @return boolean – true if Mac, false otherwise.
     */
    public static boolean isMac() {
        return OS_NAME.contains("mac");
    }
}
