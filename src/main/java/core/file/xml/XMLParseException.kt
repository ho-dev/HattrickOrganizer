package core.file.xml

internal open class XMLParseException @JvmOverloads constructor(message: String? = null, cause: Throwable? = null) :
    RuntimeException(message) {
    constructor(cause: Throwable?) : this(cause?.message, cause)

    init {
        if (cause != null) super.initCause(cause)
    }
}
