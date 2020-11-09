package tool.updater;

/**
 * major.minor.minimus
 * 
 * 
 */

public class Version implements Comparable<Version> {

	private int major;
	private int minor;
	private int buildNumber;

	public Version(String version) {
		init(version);
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getBuildNumber() {
		return buildNumber;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other == null || other.getClass() != getClass()) {
			return false;
		}

		Version otherVersion = (Version) other;

		return this.major == otherVersion.major && this.minor == otherVersion.minor
				&& this.buildNumber == otherVersion.buildNumber;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + major;
		result = 31 * result + minor;
		result = 31 * result + buildNumber;
		return result;
	}

	/**
	 * Compares this version with an other version.
	 * 
	 * @param other
	 *            an other version
	 * @return a negative integer, zero, or a positive integer as this version
	 *         is less than, equal to, or greater than the specified version.
	 */
	@Override
	public int compareTo(Version other) {
		int result = this.major - other.major;
		if (result != 0) {
			return result;
		}

		result = this.minor - other.minor;
		if (result != 0) {
			return result;
		}

		result = this.buildNumber - other.buildNumber;
		return result;
	}

	/**
	 * Checks if this version is before an other version.
	 * 
	 * @param other
	 *            an other version
	 * @return <code>true</code> if this version is before the given version,
	 *         <code>false</code> if this version is after (or the same) the
	 *         other version.
	 */
	public boolean isBefore(Version other) {
		return this.compareTo(other) < 0;
	}

	/**
	 * Returns a string representation of this version in the format
	 * major.minor.minimus (e.g.: 1.5.1).
	 * 
	 * @return the string representation of this version.
	 */
	@Override
	public String toString() {
		return this.major + "." + this.minor + "." + this.buildNumber;
	}

	private void init(String version) {
		String[] splitted = version.split("\\.");

		if (splitted.length < 2) {
			throw new IllegalArgumentException("Wrong version number: " + version);
		}

		try {
			this.major = Integer.parseInt(splitted[0]);
			this.minor = Integer.parseInt(splitted[1]);
			if (splitted.length > 2) {
				this.buildNumber = Integer.parseInt(splitted[2]);
			}
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Wrong version number: " + version, ex);
		}

		if (this.major < 0 || this.minor < 0 || this.buildNumber < 0) {
			throw new IllegalArgumentException("Wrong version number: " + version);
		}
	}

}
