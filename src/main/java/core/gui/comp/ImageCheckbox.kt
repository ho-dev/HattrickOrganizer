package core.gui.comp

import java.awt.*
import java.awt.event.ActionListener
import java.awt.image.BufferedImage
import javax.swing.*

/**
 * Checkbox mit einem Bild
 */
class ImageCheckbox internal constructor(text: String?, color: Color?, selected: Boolean, alignment: Int) : JPanel() {
    //~ Instance fields ----------------------------------------------------------------------------
    val checkbox = JCheckBox()
    private val m_jlLabel = JLabel()

    /**
     * Creates a new ImageCheckbox object.
     *
     */
    constructor(text: String?, color: Color?, selected: Boolean) : this(text, color, selected, SwingConstants.RIGHT)

    /**
     * Creates a new ImageCheckbox object.
     *
     */
    init {
        setOpaque(false)
        val layout2 = GridBagLayout()
        val constraints2 = GridBagConstraints()
        constraints2.fill = GridBagConstraints.HORIZONTAL
        constraints2.weightx = 0.0
        constraints2.weighty = 0.0
        constraints2.insets = Insets(0, 0, 0, 0)
        setLayout(layout2)
        constraints2.gridx = 0
        constraints2.gridy = 0
        constraints2.weightx = 0.0
        checkbox.setSelected(selected)
        checkbox.setOpaque(false)
        layout2.setConstraints(checkbox, constraints2)
        add(checkbox)
        constraints2.gridx = 1
        constraints2.gridy = 0
        constraints2.weightx = 1.0
        m_jlLabel.setHorizontalTextPosition(alignment)
        m_jlLabel.setText(text)
        m_jlLabel.setIcon(getImageIcon4Color(color))
        layout2.setConstraints(m_jlLabel, constraints2)
        add(m_jlLabel)
    }

    var isSelected: Boolean
        get() = checkbox.isSelected
        set(selected) {
            checkbox.setSelected(selected)
        }

    fun setText(text: String?) {
        m_jlLabel.setText(text)
    }

    fun addActionListener(listener: ActionListener?) {
        checkbox.addActionListener(listener)
    }

    private fun getImageIcon4Color(color: Color?): ImageIcon {
        val bufferedImage = BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB)
        val g2d = bufferedImage.graphics as Graphics2D
        g2d.color = color
        g2d.fillRect(0, 0, 13, 13)
        return ImageIcon(bufferedImage)
    }
}
