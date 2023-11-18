package core.db.frontend

import java.awt.Color
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JTextPane
import javax.swing.text.BadLocationException
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext
import javax.swing.text.StyledDocument

internal open class HighlightingKeyListener(private val textPane: JTextPane) : KeyAdapter() {
    private val doc: StyledDocument = textPane.styledDocument
    private var caretPastePosition = 0

    init {
        addStylesToDocument(doc)
    }

    override fun keyPressed(keyEvent: KeyEvent) {
        if (keyEvent.keyCode == 17) caretPastePosition = textPane.caretPosition
    }

    override fun keyReleased(keyEvent: KeyEvent) {
        try {
            if (keyEvent.keyCode == 86 && keyEvent.isControlDown || keyEvent.keyCode == 17) coloringWords(
                caretPastePosition,
                textPane.caretPosition - caretPastePosition
            )
            if (keyEvent.keyCode != 10) {
                coloringWords(textPane.caretPosition, null)
                if (keyEvent.keyCode == 32) coloringWords(textPane.caretPosition - 1, null)
            }
        } catch (e: BadLocationException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    @Throws(BadLocationException::class)
    protected fun coloringWords(initPos: Int, length: Int?) {
        var word: IntArray
        if (length == null) {
            if (textPane.getText(initPos, 1) == " " || textPane.getText(initPos, 1) == "\n") {
                word = getLastWord(initPos)
                if (checkWord(textPane.getText(word[0], word[1]))) doc.setCharacterAttributes(
                    word[0],
                    word[1],
                    doc.getStyle("blue"),
                    true
                ) else doc.setCharacterAttributes(
                    word[0], word[1], doc.getStyle("regular"), true
                )
            }
            if (textPane.getText(
                    if (initPos <= 0) 0 else initPos - 1,
                    1
                ) == " " || textPane.getText(if (initPos <= 0) 0 else initPos - 1, 1) == "\n" || initPos == 0
            ) {
                word = getNextWord(initPos)
                if (checkWord(textPane.getText(word[0], word[1]))) doc.setCharacterAttributes(
                    word[0],
                    word[1],
                    doc.getStyle("blue"),
                    true
                ) else doc.setCharacterAttributes(
                    word[0], word[1], doc.getStyle("regular"), true
                )
            }
            if (textPane.getText(if (initPos <= 0) 0 else initPos - 1, 1) != " " && textPane.getText(
                    initPos,
                    1
                ) != "\n" && textPane.getText(initPos, 1) != " " && initPos != 0
            ) {
                word = getCurrentWord(initPos)
                if (checkWord(textPane.getText(word[0], word[1]))) doc.setCharacterAttributes(
                    word[0],
                    word[1],
                    doc.getStyle("blue"),
                    true
                ) else doc.setCharacterAttributes(
                    word[0], word[1], doc.getStyle("regular"), true
                )
            }
        } else {
            var pos = initPos
            while (pos < initPos + length) {
                word = getNextWord(pos)
                coloringWords(pos, null)
                pos = pos + word[1] + 1
            }
        }
    }

    @Throws(BadLocationException::class)
    private fun getCurrentWord(initPos: Int): IntArray {
        val word = IntArray(2)
        var min = if (initPos <= 0) initPos else initPos - 1
        var max = initPos
        if (textPane.getText(min, 1) == " " || textPane.getText(min, 1) == "\n" || textPane.getText(
                max,
                1
            ) == " " || textPane.getText(max, 1) == "\n"
        ) return word
        while (min > 0) {
            min--
            if (textPane.getText(min, 1) == " " || min == 0 || textPane.getText(min, 1) == "\n") break
        }
        while (max < textPane.styledDocument.length) {
            if (textPane.getText(max, 1) == " " || textPane.getText(max, 1) == "\n") break
            max++
        }
        word[0] = if (min != 0) min + 1 else min
        word[1] = if (min != 0) max - min - 1 else max - min
        return word
    }

    @Throws(BadLocationException::class)
    private fun getLastWord(initPos: Int): IntArray {
        val word = IntArray(2)
        var min = initPos
        var length = 0
        var foundChar = false
        while (min > 0) {
            min--
            if (textPane.getText(min, 1) == " " || min == 0 || textPane.getText(min, 1) == "\n") {
                if (foundChar) break
            } else {
                length++
                foundChar = true
            }
        }
        word[0] = if (min != 0) min + 1 else 0
        word[1] = if (min != 0) length else length + 1
        return word
    }

    @Synchronized
    @Throws(BadLocationException::class)
    private fun getNextWord(initPos: Int): IntArray {
        var curInitPos = initPos
        val word = IntArray(2)
        var max = if (curInitPos >= 1) curInitPos else 0
        var length = 0
        var foundChar = false
        while (max < textPane.styledDocument.length) {
            if (textPane.getText(max, 1) == " " || textPane.getText(max, 1) == "\n") {
                if (foundChar) break
            } else {
                if (!foundChar) curInitPos = max
                length++
                foundChar = true
            }
            max++
        }
        word[0] = curInitPos
        word[1] = if (length != 0) length else length + 1
        return word
    }

    private fun checkWord(word: String): Boolean {
        for (i in KEYWORDS.indices) if (word.equals(KEYWORDS[i], ignoreCase = true)) return true
        return false
    }

    private fun addStylesToDocument(sdoc: StyledDocument) {
        val def = StyleContext.getDefaultStyleContext().getStyle("default")
        val regular = sdoc.addStyle("regular", def)
        StyleConstants.setFontFamily(def, "SansSerif")
        var s = sdoc.addStyle("italic", regular)
        StyleConstants.setItalic(s, true)
        s = sdoc.addStyle("bold", regular)
        StyleConstants.setBold(s, true)
        s = sdoc.addStyle("small", regular)
        StyleConstants.setFontSize(s, 10)
        s = sdoc.addStyle("large", regular)
        StyleConstants.setFontSize(s, 16)
        s = sdoc.addStyle("blue", regular)
        StyleConstants.setForeground(s, Color.blue)
        s = sdoc.addStyle("red", regular)
        StyleConstants.setForeground(s, Color.red)
    }

    companion object {
        private val KEYWORDS = arrayOf(
            "SELECT", "FROM", "WHERE", "JOIN", "INNER", "OUTER", "CROSS", "GROUP", "ORDER", "BY",
            "HAVING", "INSERT", "UPDATE", "INTO", "VALUES"
        )
    }
}