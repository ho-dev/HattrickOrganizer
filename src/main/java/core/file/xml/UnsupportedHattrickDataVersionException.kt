package core.file.xml

internal class UnsupportedHattrickDataVersionException(
    val fileName: String?,
    val expectedVersion: String?,
    val actualVersion: String?
) : XMLParseException(
    String.format(
        "Unsupported Hattrick Data Version: '%s' (expected '%s' for filename '%s')",
        actualVersion, expectedVersion, fileName
    )
)
