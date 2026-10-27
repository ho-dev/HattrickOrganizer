package core.util;

/**
 * Represents the major and minor version of Hattrick Organizer.
 * <p>
 * Versions are compared numerically, first by major and then by minor version number. Build numbers and development
 * stages are represented separately by {@link HOBuild}.
 *
 * @param major the non-negative major version number
 * @param minor the non-negative minor version number
 */
public record HOVersion(int major, int minor) implements Comparable<HOVersion> {

    public HOVersion {
        if (major < 0) {
            throw new IllegalArgumentException("major version number must not be negative");
        }
        if (minor < 0) {
            throw new IllegalArgumentException("minor version number must not be negative");
        }
    }

    public String getVersionStr() {
        return "%s.%s".formatted(major, minor);
    }

    @Override
    public int compareTo(HOVersion o) {
        int result = Integer.compare(major, o.major());
        return result != 0 ? result : Integer.compare(minor, o.minor());
    }
}
