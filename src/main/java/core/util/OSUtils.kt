package core.util;

/**
 * Provides OS-specific utility functions.
 */
public final class OSUtils {
    public final static String OS_NAME = System.getProperty("os.name").toLowerCase();
    public enum OS {WINDOWS, LINUX, MAC};
    private static OS os = determineOS();

    public static OS getOS() {return os;}

    public static boolean isMac() {
        return os == OS.MAC;
    }

    public static boolean isWindows() {
        return os == OS.WINDOWS;
    }

    public static boolean isLinux() {
        return os == OS.LINUX;
    }

    private static OS determineOS() {
        if (OS_NAME.contains("win")) os = OS.WINDOWS;
        else if (OS_NAME.contains("mac")) os = OS.MAC;
        else os = OS.LINUX;
        return os;
    }

}
