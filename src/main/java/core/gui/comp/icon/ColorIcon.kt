package core.gui.comp.icon

import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon

/**
 * Icon to display overview of a colour defined by a [Color] instance.
 */
class ColorIcon(private val colour: Color) : Icon {
    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        g.color = Color.BLACK
        g.drawRect(x, y, ICON_SIZE, ICON_SIZE)
        g.color = colour
        g.fillRect(x + 1, y + 1, ICON_SIZE - 1, ICON_SIZE - 1)
    }

    override fun getIconWidth(): Int {
        return ICON_SIZE
    }

    override fun getIconHeight(): Int {
        return ICON_SIZE
    }

    companion object {
        private const val ICON_SIZE = 8
    }
}
