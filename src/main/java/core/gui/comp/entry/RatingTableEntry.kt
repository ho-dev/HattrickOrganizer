package core.gui.comp.entry

import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.gui.theme.HOColorName
import core.gui.theme.ImageUtilities
import core.gui.theme.ThemeManager
import core.util.Helper
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

/**
 * Displays the rating.
 */
class RatingTableEntry : AbstractHOTableEntry {
    private val bgColor = ThemeManager.getColor(HOColorName.TABLEENTRY_BG)
    private var m_clComponent: JComponent = JPanel()
    private var m_sTooltip = ""
    private var m_fRating = 0f
    private val starsAligned: Boolean

    @JvmOverloads
    constructor(_starsAligned: Boolean = false) {
        starsAligned = _starsAligned
        m_fRating = 0.0f
        createComponent()
    }

    @JvmOverloads
    constructor(f: Int?, _starsAligned: Boolean = false) {
        starsAligned = _starsAligned
        m_fRating = if (f == null) {
            0f
        } else {
            f / 2.0f
        }
        createComponent()
    }

    override fun getComponent(isSelected: Boolean): JComponent {
        m_clComponent.setBackground(if (isSelected) HODefaultTableCellRenderer.SELECTION_BG else bgColor)
        return m_clComponent
    }

    fun setRating(f: Float, forceUpdate: Boolean) {
        var curRating = f
        if (curRating < 0) {
            curRating = 0f
        }
        if (forceUpdate || curRating != m_fRating) {
            m_fRating = curRating / 2.0f
            updateComponent()
        }
        m_clComponent.repaint()
    }

    var rating: Float
        get() = m_fRating * 2.0f
        set(f) {
            setRating(f, false)
        }

    fun setToolTipText(text: String) {
        m_sTooltip = text
        updateComponent()
    }

    override fun clear() {
        val constraints = GridBagConstraints()
        val layout = GridBagLayout()
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
        constraints.weighty = 0.0
        constraints.gridy = 0
        m_clComponent.removeAll()
        m_clComponent.setLayout(layout)
        val jlabel = JLabel(ImageUtilities.NOIMAGEICON)
        jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
        constraints.gridx = 0
        layout.setConstraints(jlabel, constraints)
        m_clComponent.add(jlabel)
    }

    override fun compareTo(other: IHOTableEntry): Int {
        if (other is RatingTableEntry) {
            return rating.compareTo(other.rating)
        }
        return 0
    }

    override fun createComponent() {
        val renderer = JPanel()
        val layout = GridBagLayout()
        val constraints = GridBagConstraints()
        renderer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
        renderer.setLayout(layout)
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
        constraints.weighty = 0.0
        constraints.gridx = 0
        constraints.gridy = 0
        val starLabel = getStarsLabel(m_fRating)
        layout.setConstraints(starLabel, constraints)
        renderer.add(starLabel)
        renderer.setToolTipText(m_sTooltip)
        m_clComponent = renderer
    }

    override fun updateComponent() {
        m_clComponent.removeAll()
        val layout = GridBagLayout()
        val constraints = GridBagConstraints()
        m_clComponent.setLayout(layout)
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
        constraints.weighty = 0.0
        constraints.gridx = 0
        constraints.gridy = 0
        val starLabel = getStarsLabel(m_fRating)
        layout.setConstraints(starLabel, constraints)
        m_clComponent.add(starLabel)
        m_clComponent.setToolTipText(m_sTooltip)
        m_clComponent.repaint()
    }

    private fun getStarsLabel(_rating: Float): JLabel {
        val jlabel: JLabel
        if (_rating == 0f) {
            jlabel = JLabel(ImageUtilities.NOIMAGEICON)
        } else {
            jlabel = if (_rating == _rating.toInt().toFloat()) {
                if (starsAligned) {
                    JLabel("   " + Helper.INTEGERFORMAT.format(_rating.toDouble()))
                } else {
                    JLabel(Helper.INTEGERFORMAT.format(_rating.toDouble()))
                }
            } else {
                JLabel(Helper.DEFAULTDEZIMALFORMAT.format(_rating.toDouble()))
            }
            jlabel.setIcon(iconStar)
        }
        jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
        jlabel.setHorizontalTextPosition(SwingConstants.LEADING)
        jlabel.setHorizontalAlignment(SwingConstants.LEFT)
        return jlabel
    }

    companion object {
        private val iconStar = ImageUtilities.getStarIcon()
    }
}
