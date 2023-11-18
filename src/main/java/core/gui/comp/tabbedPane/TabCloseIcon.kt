package core.gui.comp.tabbedPane

import core.gui.theme.HOIconName
import core.gui.theme.ThemeManager
import core.model.HOVerwaltung
import java.awt.Component
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JOptionPane
import javax.swing.JTabbedPane

internal class TabCloseIcon(mTabbedPane: JTabbedPane, offset: Int) : Icon {
    private val mIcon = ThemeManager.getIcon(HOIconName.TABBEDPANE_CLOSE)

    @Transient
    private var mPosition: Rectangle? = null
    private var xOffset = 0
    private val mTabbedPane: JTabbedPane

    init {
        xOffset = offset
        this.mTabbedPane = mTabbedPane
        this.mTabbedPane.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                // asking for isConsumed is *very* important, otherwise more
                // than one tab might get closed!
                if (!e.isConsumed && mPosition!!.contains(e.x, e.y)) {
                    closeTab()
                    e.consume()
                }
            }
        })
    }

    /**
     * when painting, remember last position painted. add extra xOffset pixels
     * to allow for Nimbus skin
     */
    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        mPosition = Rectangle(x + xOffset, y, iconWidth, iconHeight)
        mIcon.paintIcon(c, g, x + xOffset, y)
    }

    /**
     * Returns the total width of the close icon, including `xOffset`.
     */
    override fun getIconWidth(): Int {
        return mIcon.iconWidth + xOffset
    }

    /**
     * just delegate
     */
    override fun getIconHeight(): Int {
        return mIcon.iconHeight
    }

    private fun closeTab() {
        val title = HOVerwaltung.instance().getLanguageString("confirmation.title")
        val message = HOVerwaltung.instance().getLanguageString("tab.close.confirm.msg")
        if (JOptionPane.showConfirmDialog(
                mTabbedPane,
                message,
                title,
                JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION
        ) {
            val index = mTabbedPane.selectedIndex
            mTabbedPane.remove(index)
        }
    }
}
