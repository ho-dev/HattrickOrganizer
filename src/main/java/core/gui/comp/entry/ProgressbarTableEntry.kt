package core.gui.comp.entry

import core.gui.comp.renderer.HODefaultTableCellRenderer
import java.awt.Color
import java.text.NumberFormat
import javax.swing.JComponent
import javax.swing.JProgressBar

/**
 * Progress bar as table cell.
 */
class ProgressbarTableEntry(
    private var m_iAktuellerWert: Int, private var m_iMinWert: Int, private var m_iMaxWert: Int, fractionDigits: Int,
    faktor4Label: Double, bgcolor: Color,
    fgcolor: Color, addText: String
) : AbstractHOTableEntry() {
    //~ Instance fields ----------------------------------------------------------------------------
    private var m_clBGColor = Color.WHITE
    private var m_clFGColor = Color.BLUE
    private var m_clProgressbar: JProgressBar? = null
    private var m_sAddText = ""
    private var m_dFaktor4Label = 1.0
    private val nf: NumberFormat

    init {
        nf = NumberFormat.getNumberInstance()
        nf.setMinimumFractionDigits(fractionDigits)
        nf.setMaximumFractionDigits(fractionDigits)
        m_dFaktor4Label = faktor4Label
        m_clBGColor = bgcolor
        m_clFGColor = fgcolor
        m_sAddText = addText
        createComponent()
    }

    var aktuellerWert: Int
        /**
         * Getter for property m_iAktuellerWert.
         *
         * @return Value of property m_iAktuellerWert.
         */
        get() = m_iAktuellerWert
        /**
         * Setter for property m_iAktuellerWert.
         *
         * @param m_iAktuellerWert New value of property m_iAktuellerWert.
         */
        set(m_iAktuellerWert) {
            this.m_iAktuellerWert = m_iAktuellerWert
            updateComponent()
        }

    /**
     * Implement getComponent().
     */
    override fun getComponent(isSelected: Boolean): JComponent {
        if (isSelected) {
            m_clProgressbar!!.setOpaque(true)
            m_clProgressbar!!.setBackground(HODefaultTableCellRenderer.SELECTION_BG)
        } else {
            m_clProgressbar!!.setOpaque(true)
            m_clProgressbar!!.setBackground(m_clBGColor)
        }
        return m_clProgressbar!!
    }


    /**
     * Clear value.
     */
    override fun clear() {
        m_clProgressbar!!.setString("")
        m_clProgressbar!!.setValue(0)
    }

    /**
     * Implement compareTo() for sorting.
     */
    override fun compareTo(other: IHOTableEntry): Int {
        if (other is ProgressbarTableEntry) {
            return if (aktuellerWert < other.aktuellerWert) {
                -1
            } else if (aktuellerWert > other.aktuellerWert) {
                1
            } else {
                0
            }
        }
        return 0
    }

    /**
     * Create the component and set the text.
     */
    override fun createComponent() {
        m_clProgressbar = JProgressBar()
        m_clProgressbar!!.setStringPainted(true)
        updateComponent()
    }

    /**
     * Update label text.
     */
    override fun updateComponent() {
        m_clProgressbar!!.minimum = m_iMinWert
        m_clProgressbar!!.maximum = m_iMaxWert
        m_clProgressbar!!.setValue(m_iAktuellerWert)
        m_clProgressbar!!.setBackground(m_clBGColor)
        m_clProgressbar!!.setForeground(m_clFGColor)
        m_clProgressbar!!.setString(nf.format(m_iAktuellerWert * m_dFaktor4Label) + m_sAddText)
    }
}
