package core.gui.comp.panel

import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Panel displaying two labels that are semantically related, such as main value and comparison value.
 * This is similar to [DoubleLabelEntries], except this may live outside
 * a table.
 */
class DoubleLabelPanel : JPanel() {
    private var leftLabel = JLabel()
    private var rightLabel = JLabel()

    init {
        setOpaque(false)
        addLabels()
    }

    private fun addLabels() {
        val layout = FlowLayout()
        layout.vgap = 0
        setLayout(layout)
        // Fix right label width to avoid components moving when values change.
        rightLabel.minimumSize = Dimension(90, 10)
        rightLabel.maximumSize = Dimension(90, 10)
        rightLabel.preferredSize = Dimension(90, 10)
        add(leftLabel)
        add(rightLabel)
    }

    fun setLeftLabel(leftLabel: JLabel) {
        this.leftLabel = leftLabel
        updateComponent()
    }

    fun setRightLabel(rightLabel: JLabel) {
        this.rightLabel = rightLabel
    }

    fun updateComponent() {
        this.removeAll()
        addLabels()
        revalidate()
        repaint()
    }
}
