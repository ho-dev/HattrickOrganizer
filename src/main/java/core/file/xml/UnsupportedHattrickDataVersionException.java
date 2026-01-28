package core.file.xml;

import lombok.Getter;

@Getter
class UnsupportedHattrickDataVersionException extends XMLParseException {

    private final String fileName;
    private final String expectedVersion;
    private final String actualVersion;

    public UnsupportedHattrickDataVersionException(String fileName, String expectedVersion, String actualVersion) {
        super("Unsupported Hattrick Data Version: '%s' (expected '%s' for filename '%s')"
            .formatted(actualVersion, expectedVersion, fileName));
        this.fileName = fileName;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }
}
