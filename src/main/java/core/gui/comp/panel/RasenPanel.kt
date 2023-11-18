// %2560498359:de.hattrickorganizer.gui.templates%
package core.gui.comp.panel

import core.gui.theme.HOIconName
import core.gui.theme.ImageUtilities
import core.gui.theme.ThemeManager
import java.awt.*
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import javax.swing.JPanel

/**
 * JPanel mit HintergrundGrafik für Fenster
 *
 * @author Volker Fischer
 * @version 0.2.1a 28.02.02
 */
open class RasenPanel : JPanel {
    //~ Instance fields ----------------------------------------------------------------------------
    private var m_bPrint = false
    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new RasenPanel object.
     */
    constructor() : super() {
        init(false)
    }

    /**
     * Creates a new RasenPanel object.
     */
    constructor(layout: LayoutManager?) : super(layout) {
        init(false)
    }

    /**
     * Creates a new RasenPanel object.
     */
    constructor(forprint: Boolean) : super() {
        init(forprint)
    }

    /**
     * Creates a new RasenPanel object.
     */
    constructor(layout: LayoutManager?, forprint: Boolean) : super(layout) {
        init(forprint)
    }

    //~ Methods ------------------------------------------------------------------------------------
    override fun paint(g: Graphics) {
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

    private fun init(printing: Boolean) {
        m_bPrint = printing
        if (Companion.background == null) {
            Companion.background =
                ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.GRASSPANEL_BACKGROUND))
        }
        setBackground(Color.white)
    }

    companion object {
        //~ Static fields/initializers -----------------------------------------------------------------
        /**
         *
         */
        private const val serialVersionUID = -8146276344087586861L
        @JvmField
        var background: BufferedImage? = null
    }
}
