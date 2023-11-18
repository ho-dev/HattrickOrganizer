package core.gui.comp.icon

import core.gui.theme.HOColorName
import core.gui.theme.ThemeManager
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.Icon

/**
 * Icon that represents either a green upward triangle, or a red downward triangle
 * based on the direction chosen.
 *
 *
 * The triangle is currently used in the table league to show position evolution
 * in the league.  It cannot be resized as of yet.
 */
class DrawIcon(private val direction: Int) : Icon {
    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        if (direction == UPWARD_DIRECTION) {
            g2.color = WIN_COLOR
            g2.fillPolygon(intArrayOf(12, 20, 16), intArrayOf(18, 18, 12), 3)
        } else {
            g2.color = DEFEAT_COLOR
            g2.fillPolygon(intArrayOf(12, 20, 16), intArrayOf(12, 12, 18), 3)
        }
    }

    override fun getIconWidth(): Int {
        return 10
    }

    override fun getIconHeight(): Int {
        return 10
    }

    companion object {
        const val UPWARD_DIRECTION = 1
        const val DOWNWARD_DIRECTION = 0
        private val WIN_COLOR = ThemeManager.getColor(HOColorName.FORM_STREAK_WIN)
        private val DEFEAT_COLOR = ThemeManager.getColor(HOColorName.FORM_STREAK_DEFEAT)
    }
}
