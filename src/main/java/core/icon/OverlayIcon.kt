package core.icon

import com.github.weisj.darklaf.properties.icons.DerivableIcon
import core.gui.theme.ImageUtilities
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon
import kotlin.math.max

class OverlayIcon @JvmOverloads constructor(
    private val icon: Icon,
    private val overlay: Icon,
    width: Int = -1,
    height: Int = -1
) : Icon, DerivableIcon<OverlayIcon> {
    private val width: Int
    private val height: Int

    init {
        this.width = if (width > 0) width else max(icon.iconWidth.toDouble(), overlay.iconWidth.toDouble())
            .toInt()
        this.height = if (height > 0) width else max(icon.iconHeight.toDouble(), overlay.iconHeight.toDouble())
            .toInt()
    }

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        val iconX = x + (iconWidth - icon.iconWidth) / 2
        val iconY = y + (iconHeight - icon.iconHeight) / 2
        val overlayX = x + (iconWidth - overlay.iconWidth) / 2
        val overlayY = y + (iconHeight - overlay.iconHeight) / 2
        icon.paintIcon(c, g, iconX, iconY)
        overlay.paintIcon(c, g, overlayX, overlayY)
    }

    override fun getIconWidth(): Int {
        return width
    }

    override fun getIconHeight(): Int {
        return height
    }

    override fun derive(width: Int, height: Int): OverlayIcon {
        val derivedIcon = ImageUtilities.getScaledIcon(
            icon, width * icon.iconWidth / this.width,
            height * icon.iconHeight / this.height
        )
        val derivedOverlay = ImageUtilities.getScaledIcon(
            overlay, width * overlay.iconWidth / this.width,
            height * overlay.iconHeight / this.height
        )
        return OverlayIcon(derivedIcon, derivedOverlay, width, height)
    }
}
