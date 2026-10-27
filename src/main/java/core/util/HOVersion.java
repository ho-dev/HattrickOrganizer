package core.util;

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
