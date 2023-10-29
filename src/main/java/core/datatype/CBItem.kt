package core.datatype

/**
 * Combo item that associates an ID to a String.
 *
 * @author thomas.werth
 */
open class CBItem(override val text: String, override val id: Int) : ComboItem {

    override fun equals(obj: Any?): Boolean {
        return if (obj is CBItem) {
            id == obj.id
        } else false
    }

    override fun toString(): String {
        return text
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + id
        return result
    }
}
