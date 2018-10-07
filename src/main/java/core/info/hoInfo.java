package core.info;

public class hoInfo {

    /**
     * HO Version
     */
    public static final double VERSION = 1.435d;
    /**
     * language version
     */
    public static final int SPRACHVERSION = 2;
    private static int revision = 0;
    /**
     * Is this a development version? Note that a "development" version can a
     * release ("Beta" or "DEV" version). The DEVELOPMENT flag is used by the
     * ant build script. Keep around.
     */
    private static final boolean DEVELOPMENT = true;
    /**
     * A RELEASE is when a build artifact gets delivered to users. Note that
     * even a DEVELOPMENT version can be a RELEASE ("Beta"). So when a version
     * is build (no matter if DEVELOPMENT or not), this flag should be set to
     * true. The main purpose for the flag is to disable code (unfinished new
     * features, debug code) which should not be seen in a release.
     */
    private static final boolean RELEASE = false;

    public static boolean isDevelopment() {
        return DEVELOPMENT;
    }

    public static boolean isRelease() {
        return RELEASE;
    }

    public static int getRevisionNumber() {
        return 0;
    }
}


