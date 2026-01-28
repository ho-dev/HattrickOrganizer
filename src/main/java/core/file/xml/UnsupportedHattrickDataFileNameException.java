package core.file.xml;

import lombok.Getter;

@Getter
class UnsupportedHattrickDataFileNameException extends XMLParseException {

    private final String expectedVersion;
    private final String actualVersion;

    public UnsupportedHattrickDataFileNameException(String expectedFileName, String actualFileName) {
        super("Unsupported Hattrick Data FileName: '%s' (expected '%s')"
            .formatted(actualFileName, expectedFileName));
        this.expectedVersion = expectedFileName;
        this.actualVersion = actualFileName;
    }
}
