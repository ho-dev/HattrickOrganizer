package core.gui.comp

import core.gui.theme.ImageUtilities
import java.awt.*
import javax.swing.JPanel

class CustomProgressBar(
    private val m_colorBG: Color,
    private val m_colorFill: Color,
    private val m_colorBorder: Color,
    private val m_width: Int,
    private val m_height: Int,
    f: Font
) : JPanel() {
    private val m_minimum = 0.0
    private val m_maximum = 100.0
    var value = 100.0
        private set
    private var m_leftText = ""
    private var m_rightText = ""
    private val m_f: Font

    init {
        m_f = f.deriveFont(Font.BOLD, 16f)
        preferredSize = Dimension(m_width, m_height)
        setBackground(m_colorBG)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        //border
        g.color = m_colorBorder
        g.drawRect(0, 0, m_width - 1, m_height - 1)

        //fill progress
        if (value != 0.0) {
            val drawAmount = ((value - m_minimum) / (m_maximum - m_minimum) * m_width).toInt()
            val leftBlockWidth = drawAmount - 2
            val rightBlockWidth = m_width - 2 - leftBlockWidth
            g.color = m_colorFill
            g.fillRect(1, 1, leftBlockWidth, m_height - 2) //-2 to account for border
            g.font = m_f
            val c = Canvas()
            val fm = c.getFontMetrics(m_f)
            val textHeight = fm.height
            val y = (m_height - textHeight) / 2 + 15
            if (m_leftText !== "") {
                val leftTextWidth = fm.stringWidth(m_leftText)
                g.color = ImageUtilities.getColorForContrast(m_colorFill)
                g.drawString(m_leftText, (leftBlockWidth - leftTextWidth) / 2, y)
            }
            if (m_rightText !== "") {
                val rightTextWidth = fm.stringWidth(m_rightText)
                g.color = ImageUtilities.getColorForContrast(m_colorBG)
                g.drawString(m_rightText, leftBlockWidth + (rightBlockWidth - rightTextWidth) / 2, y)
            }
        }
    }

    fun setValue(val1: Int, val2: Int, min_width: Double) {
        value = val1.toDouble() / (val1 + val2).toDouble()
        val leftValue = Math.round(value * 100).toInt()
        val rightValue = 100 - leftValue
        m_leftText = "$leftValue%"
        m_rightText = "$rightValue%"
        if (value < min_width) {
            value = min_width
        } else if (value > 1 - min_width) {
            value = 1 - min_width
        }
        value = value * 100
    }

    fun resetValue() {
        value = 0.0
        m_leftText = ""
        m_rightText = ""
    }
}
