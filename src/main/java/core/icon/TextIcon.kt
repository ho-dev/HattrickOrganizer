package core.icon

import com.github.weisj.darklaf.properties.icons.DerivableIcon
import java.awt.*
import javax.swing.Icon

class TextIcon(
    private val text: String,
    private val color: Color,
    private val font: Font,
    private val width: Int,
    private val height: Int,
    private val baseline: Int
) : Icon, DerivableIcon<TextIcon> {
    override fun paintIcon(c: Component, gg: Graphics, x: Int, y: Int) {
        val g = gg.create() as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT)
        g.font = font
        val fm = g.getFontMetrics(font)
        val w = fm.stringWidth(text)
        val textX = x + (iconWidth - w) / 2
        val textY = y + baseline
        g.color = color
        g.drawString(text, textX, textY)
        g.dispose()
    }

    override fun getIconWidth(): Int {
        return width
    }

    override fun getIconHeight(): Int {
        return height
    }

    override fun derive(width: Int, height: Int): TextIcon {
        val base = Math.round(height * (baseline.toFloat() / this.height))
        val f = font.deriveFont(height * (font.size2D / this.height))
        return TextIcon(text, color, f, width, height, base)
    }
}
