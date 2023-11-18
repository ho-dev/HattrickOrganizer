/*
 * SkillEntry.java
 *
 * Created on 4. September 2004, 14:22
 */
package core.gui.comp.entry

import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.gui.theme.HOColorName
import core.gui.theme.ThemeManager
import core.model.UserParameter
import core.util.Helper
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

/**
 * Skill display of a player (decimal places in gray)
 *
 * @author Pirania
 */
class SkillEntry(zahl: Double, foreground: Color, background: Color) : AbstractHOTableEntry() {
    //~ Instance fields ----------------------------------------------------------------------------
    private var m_clBGColor = ColorLabelEntry.BG_STANDARD
    private var m_clFGColor = ColorLabelEntry.FG_STANDARD
    private var m_clComponent: JComponent? = null
    private var m_jlLabel1: JLabel? = null
    private var m_jlLabel2: JLabel? = null
    private var m_sNachkomma = ""
    private var m_sText = ""

    /**
     * Gibt die Zahl zurück
     */
    var zahl = Double.NEGATIVE_INFINITY
        private set
    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new SkillEntry object.
     */
    init {
        this.zahl = zahl
        m_clFGColor = foreground
        m_clBGColor = background
        createText()
        createComponent()
    }
    //~ Methods ------------------------------------------------------------------------------------
    /**
     * Gibt eine passende Komponente zurück
     */
    override fun getComponent(isSelected: Boolean): JComponent {
        m_clComponent!!.setBackground(if (isSelected) HODefaultTableCellRenderer.SELECTION_BG else m_clBGColor)
        m_jlLabel1!!.setForeground(if (isSelected) HODefaultTableCellRenderer.SELECTION_FG else m_clFGColor)
        m_jlLabel2!!.setForeground(if (isSelected) HODefaultTableCellRenderer.SELECTION_FG else m_clFGColor)
        return m_clComponent!!
    }

    override fun clear() {
        zahl = 0.0
        updateComponent()
    }

    /**
     * Vergleich zum Sortieren
     */
    override fun compareTo(other: IHOTableEntry): Int {
        if (other is SkillEntry) {

            //Zahl?
            val zahl1 = zahl
            val zahl2: Double = other.zahl
            return zahl1.compareTo(zahl2)
        }
        return 0
    }

    /**
     * Erstellt eine passende Komponente
     */
    override fun createComponent() {
        val layout = GridBagLayout()
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        constraints.insets = Insets(0, 0, 0, 0)
        val panel = JPanel(layout)
        m_jlLabel1 = JLabel(m_sText, SwingConstants.RIGHT)
        m_jlLabel1!!.setForeground(m_clFGColor)
        constraints.anchor = GridBagConstraints.EAST
        layout.setConstraints(m_jlLabel1, constraints)
        panel.add(m_jlLabel1)
        m_jlLabel2 = JLabel(m_sNachkomma, SwingConstants.LEFT)
        m_jlLabel2!!.setForeground(m_clFGColor2)
        m_jlLabel2!!.setFont(m_jlLabel1!!.font.deriveFont(m_jlLabel1!!.font.size2D - 1f))
        constraints.weightx = 0.0
        constraints.weighty = 1.0
        constraints.anchor = GridBagConstraints.SOUTHWEST
        layout.setConstraints(m_jlLabel2, constraints)
        panel.add(m_jlLabel2)
        m_clComponent = panel
        m_clComponent?.setOpaque(true)
    }

    /**
     * Erzeugt die beiden Texte aus der Zahl
     */
    fun createText() {
        m_sText = zahl.toInt().toString()
        m_sNachkomma = if (UserParameter.instance().nbDecimals == 1) {
            Helper.DEFAULTDEZIMALFORMAT.format(zahl - zahl.toInt())
        } else {
            Helper.DEZIMALFORMAT_2STELLEN.format(zahl - zahl.toInt())
        }
        var index = m_sNachkomma.indexOf(',')
        if (index < 0) {
            index = m_sNachkomma.indexOf('.')
        }
        if (index >= 0) {
            m_sNachkomma = m_sNachkomma.substring(index)
        }
    }

    override fun updateComponent() {
        m_jlLabel1!!.setText(m_sText)
        m_jlLabel2!!.setText(m_sNachkomma)
        m_jlLabel1!!.setBackground(m_clBGColor)
        m_jlLabel1!!.setForeground(m_clFGColor)
        m_jlLabel2!!.setBackground(m_clBGColor)
        m_jlLabel2!!.setForeground(m_clFGColor2)
        m_jlLabel2!!.setFont(m_jlLabel1!!.font.deriveFont(m_jlLabel1!!.font.size2D - 1f))
    }

    companion object {
        private val m_clFGColor2 = ThemeManager.getColor(HOColorName.SKILLENTRY2_BG)
    }
}
