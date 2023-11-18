package core.gui.comp.entry

import core.gui.theme.HOColorName
import core.gui.theme.ThemeManager
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.LayoutManager
import javax.swing.JComponent
import javax.swing.SwingConstants

/**
 * A panel with two labels to display two values in the same column, e.g. (value, diff).
 *
 *
 * The two labels within the resulting components will have equal width, unless the layout
 * manager has been set.
 */
open class DoubleLabelEntries : AbstractHOTableEntry {
    //~ Instance fields ----------------------------------------------------------------------------
    private var m_clComponent = DoubleLabel()

    lateinit var tableEntryLeft: IHOTableEntry
    lateinit var tableEntryRight: IHOTableEntry

    private var layout: LayoutManager? = null
    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new DoubleLabelEntries object.
     */
    constructor()

    /**
     * Creates a new DoubleLabelEntries object.
     *
     */
    constructor(color: Color?) : super() {
        tableEntryLeft = ColorLabelEntry(
            "", ColorLabelEntry.FG_STANDARD, color,
            SwingConstants.RIGHT
        )
        tableEntryRight = ColorLabelEntry(
            "", DIFF_COLOR, color,
            SwingConstants.CENTER
        )
        createComponent()
    }

    /**
     * Creates a new DoubleLabelEntries object.
     *
     */
    @JvmOverloads
    constructor(links: IHOTableEntry, rechts: IHOTableEntry, layout: LayoutManager? = null) {
        tableEntryLeft = links
        tableEntryRight = rechts
        setLayoutManager(layout)
        createComponent()
    }

    override fun getComponent(isSelected: Boolean): JComponent {
        m_clComponent.removeAll()
        m_clComponent.setOpaque(false)
        val links = tableEntryLeft.getComponent(isSelected)
        val rechts = tableEntryRight.getComponent(isSelected)
        if (layout != null) {
            m_clComponent.setLayoutManager(layout)
        }

        // If the layout is a GridBagLayout, force the components to take
        // the full space of their respective cell.
        if (layout is GridBagLayout) {
            val gbc = GridBagConstraints()
            gbc.fill = GridBagConstraints.BOTH
            gbc.weightx = 1.0
            gbc.weighty = 1.0
            m_clComponent.add(links, gbc)
            m_clComponent.add(rechts, gbc)
        } else {
            m_clComponent.add(links)
            m_clComponent.add(rechts)
        }
        return m_clComponent
    }

    fun setLabels(links: IHOTableEntry, rechts: IHOTableEntry) {
        tableEntryLeft = links
        tableEntryRight = rechts
        updateComponent()
    }

    val left: ColorLabelEntry
        /**
         * Only use if left is a [ColorLabelEntry].
         */
        get() = tableEntryLeft as ColorLabelEntry
    val right: ColorLabelEntry
        /**
         * Only use if right is a [ColorLabelEntry].
         */
        get() = tableEntryRight as ColorLabelEntry

    override fun clear() {
        tableEntryLeft.clear()
        tableEntryRight.clear()
    }

    override fun compareTo(other: IHOTableEntry): Int {
        if (other is DoubleLabelEntries) {
            return tableEntryLeft.compareTo(other.tableEntryLeft)
        }
        return 0
    }

    final override fun createComponent() {
        m_clComponent = DoubleLabel()
    }

    override fun updateComponent() {
        tableEntryLeft.updateComponent()
        tableEntryRight.updateComponent()
    }

    private fun setLayoutManager(layout: LayoutManager?) {
        this.layout = layout
    }

    companion object {
        private val DIFF_COLOR = ThemeManager.getColor(HOColorName.FG_INJURED)
    }
}
