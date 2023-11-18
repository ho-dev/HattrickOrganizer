package core.gui.comp.entry

import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.gui.theme.HOIconName
import core.gui.theme.ThemeManager
import javax.swing.*

class TorLabelEntry @JvmOverloads constructor(goal: Int = 0) : AbstractHOTableEntry() {
    private var m_clComponent: JComponent = JPanel()
    private var m_iTore = 0

    init {
        if (BALLIMAGEICON == null) {
            BALLIMAGEICON = ThemeManager.getScaledIcon(HOIconName.BALL, 14, 14)
        }
        this.m_iTore = goal
        createComponent()
    }

    override fun getComponent(isSelected: Boolean): JComponent {
        m_clComponent.setBackground(if (isSelected) HODefaultTableCellRenderer.SELECTION_BG else ColorLabelEntry.BG_STANDARD)
        return m_clComponent
    }

    var tore: Int
        get() = m_iTore
        set(tore) {
            if (tore != m_iTore) {
                m_iTore = tore
                updateComponent()
            }
        }

    override fun clear() {
        m_clComponent.removeAll()
    }

    override fun compareTo(other: IHOTableEntry): Int {
        if (other is TorLabelEntry) {
            return if (tore < other.tore) {
                -1
            } else if (tore > other.tore) {
                1
            } else {
                0
            }
        }
        return 0
    }

    override fun createComponent() {
        val renderer = JPanel()
        renderer.setLayout(BoxLayout(renderer, 0))
        renderer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
        for (f in m_iTore downTo 1) {
            val jlabel = JLabel(BALLIMAGEICON)
            jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
            renderer.add(jlabel)
        }
        m_clComponent = renderer
    }

    override fun updateComponent() {
        m_clComponent.removeAll()
        for (f in m_iTore downTo 1) {
            val jlabel = JLabel(BALLIMAGEICON)
            jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
            m_clComponent.add(jlabel)
        }
    }

    companion object {
        private var BALLIMAGEICON: Icon? = null
    }
}
