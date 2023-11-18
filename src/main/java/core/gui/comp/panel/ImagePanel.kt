package core.gui.comp.panel

import core.gui.theme.HOBooleanName
import core.gui.theme.HOIconName
import core.gui.theme.ImageUtilities
import core.gui.theme.ThemeManager
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.LayoutManager
import java.awt.TexturePaint
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import javax.swing.JPanel

open class ImagePanel : JPanel {
    private var m_bPrint = false

    constructor() : super() {
        init(false)
    }

    constructor(layout: LayoutManager?) : super(layout) {
        init(false)
    }

    constructor(bPrint: Boolean) : super() {
        init(bPrint)
    }

    /**
     * Creates a new ImagePanel object.
     */
    constructor(layout: LayoutManager?, bPrint: Boolean) : super(layout) {
        init(bPrint)
    }

    //~ Methods ------------------------------------------------------------------------------------
    override fun paint(g: Graphics) {
        if (!ThemeManager.instance().isSet(HOBooleanName.IMAGEPANEL_BG_PAINTED)) {
            super.paint(g)
        } else {
            val g2d = g as Graphics2D
            paintComponent(g2d)
            if (!m_bPrint) {
                val tr: Rectangle2D = Rectangle2D.Double(
                    0.0,
                    0.0,
                    Companion.background!!.width.toDouble(),
                    Companion.background!!.height.toDouble()
                )
                val tp = TexturePaint(Companion.background, tr)
                g2d.paint = tp
                g2d.fill(g2d.clip)
            }
            paintChildren(g2d)
            paintBorder(g2d)
        }
    }

    private fun init(printing: Boolean) {
        m_bPrint = printing
        if (Companion.background == null) {
            Companion.background =
                ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.IMAGEPANEL_BACKGROUND))
        }
    }

    companion object {
        @JvmField
        var background: BufferedImage? = null
    }
}
