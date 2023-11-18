package core.gui.comp.entry

import core.gui.comp.renderer.HODefaultTableCellRenderer
import java.awt.GridLayout
import java.awt.LayoutManager
import javax.swing.JPanel

/**
 * Panel with two horizontal elements, left and right.
 */
internal class DoubleLabel : JPanel() {
    private var layout: LayoutManager = GridLayout(1, 2)

    init {
        setLayout(layout)
        setOpaque(true)
        setBackground(HODefaultTableCellRenderer.SELECTION_BG)
    }

    fun setLayoutManager(manager: LayoutManager?) {
        if (manager != null) {
            layout = manager
            setLayout(layout)
            repaint()
        }
    }
}
