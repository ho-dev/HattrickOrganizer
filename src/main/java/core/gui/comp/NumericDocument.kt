package core.gui.comp

import core.util.StringUtils
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.PlainDocument

/**
 * A document for text components which allows numeric chars only.
 */
class NumericDocument : PlainDocument {
    private var maxLength = -1

    /**
     * Returns `true` if negative Numbers are allowed (a minus sign
     * can be inserted at offset 0 in this case), `false` if not.
     *
     * @return `true` if negative Numbers are allowed,
     * `false` otherwise.
     */
    var isAllowNegatives = false
        private set

    /**
     * Constructs a new NumericDocument. Will not allow negatives, length is not
     * limited.
     */
    constructor() : super()

    /**
     * Constructs a new NumericDocument.
     *
     * @param allowNegatives
     * `true` to allow negative values, `false`
     * otherwise.
     */
    constructor(allowNegatives: Boolean) {
        isAllowNegatives = allowNegatives
    }

    /**
     * Constructs a new NumericDocument with a maximum number of digits.
     *
     * @param maxLength
     * the maximum number of digits allowed.
     * @throws IllegalArgumentException
     * if the given maxLength is less than (or equal) 0.
     */
    @JvmOverloads
    constructor(maxLength: Int, allowNegatives: Boolean = false) : this() {
        isAllowNegatives = allowNegatives
        setMaxLength(maxLength)
    }

    /**
     * Gets the maximum length a text in this document can have.
     *
     * @return the maximum length or `-1` if the length is not
     * limited.
     */
    fun getMaxLength(): Int {
        return maxLength
    }

    @Throws(BadLocationException::class)
    override fun insertString(offs: Int, toBInserted: String, a: AttributeSet) {
        if (StringUtils.isEmpty(toBInserted)) {
            return
        }

        // if maxlength > -1 ==> check length
        if (maxLength > -1) {
            // if too long ==> do not insert
            if (length + toBInserted.length > maxLength) {
                return
            }
        }

        // if there is already a minus sign, nothing can be inserted with
        // offset 0, because minus sign has to be the first char
        if (isAllowNegatives) {
            val currentText = getText(0, length)
            if (!currentText.isEmpty() && offs == 0 && currentText[0] == '-') {
                return
            }
        }
        if (StringUtils.isNumeric(toBInserted)) {
            super.insertString(offs, toBInserted, a)
        } else {
            if (isAllowNegatives) {
                if (offs == 0 && toBInserted[0] == '-' && StringUtils.isNumeric(toBInserted.substring(1))) {
                    super.insertString(offs, toBInserted, a)
                }
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun setMaxLength(maxLength: Int) {
        require(maxLength > 0) { "the maximum length has to be greater than 0!" }
        this.maxLength = maxLength
    }

    companion object {
        private const val serialVersionUID = -7376216000843726838L
    }
}
