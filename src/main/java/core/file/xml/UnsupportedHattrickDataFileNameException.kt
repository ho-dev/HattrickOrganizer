package core.file.xml

internal class UnsupportedHattrickDataFileNameException(val expectedVersion: String?, val actualVersion: String?) :
    XMLParseException(
        String.format("Unsupported Hattrick Data FileName: '%s' (expected '%s')", actualVersion, expectedVersion)
    )
