package core.gui.comp

import core.gui.Credits
import core.gui.theme.HOColorName
import core.gui.theme.ThemeManager
import core.util.BrowserLauncher
import core.util.HOLogger
import java.awt.Cursor
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.font.TextAttribute
import javax.swing.JLabel

class HyperLinkLabel() : JLabel() {
    var url: String? = null

    init {
        init()
    }

    constructor(text: String?, url: String?) : this() {
        this.url = url
        setText(text)
    }

    constructor(url: String?) : this() {
        this.url = url
        setText(url)
    }

    private fun init() {
        val map: MutableMap<TextAttribute, Any?> = HashMap()
        map[TextAttribute.UNDERLINE] = TextAttribute.UNDERLINE_ON
        val font = font.deriveFont(map)
        setFont(font)
        setForeground(LINK_COLOR)
        addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
            }

            override fun mouseExited(e: MouseEvent) {
                setCursor(Cursor.getDefaultCursor())
            }

            override fun mouseClicked(e: MouseEvent) {
                try {
                    BrowserLauncher.openURL(url)
                } catch (ex: Exception) {
                    HOLogger.instance().log(Credits::class.java, ex)
                }
            }
        })
    }

    companion object {
        private val LINK_COLOR = ThemeManager.getColor(HOColorName.LINK_LABEL_FG)
    }
}
