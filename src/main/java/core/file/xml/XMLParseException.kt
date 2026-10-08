package core.file.xml

open class XMLParseException @JvmOverloads constructor(
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)
