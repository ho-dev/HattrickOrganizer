package core.gui.comp.panel

import core.datatype.CBItem
import core.gui.theme.HOColorName
import core.gui.theme.ThemeManager
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

class ComboBoxTitled @JvmOverloads constructor(
    private val m_title: String,
    private val m_jcbItems: JComboBox<CBItem>,
    private val m_bInverseColor: Boolean = false
) : JPanel() {
    private val jlp = JPanel()
    private var m_jlTitle: JLabel? = null
    private val layout = GridBagLayout()

    init {
        initComponents()
    }

    /**
     * Create the components, don't forget the CB for the players and the listener!
     */
    private fun initComponents() {
        val bgColor =
            if (m_bInverseColor) ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER) else ThemeManager.getColor(
                HOColorName.PANEL_BG
            )
        val bgCBColor =
            if (m_bInverseColor) ThemeManager.getColor(HOColorName.PANEL_BG) else ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER)
        val borderColor =
            if (m_bInverseColor) ThemeManager.getColor(HOColorName.PANEL_BG) else ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER)
        setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, borderColor))
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
        constraints.weighty = 0.0
        jlp.setLayout(layout)
        m_jlTitle = JLabel(m_title)
        m_jlTitle!!.setFont(font.deriveFont(Font.BOLD))
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.insets = Insets(5, 12, 0, 0)
        jlp.add(m_jlTitle, constraints)
        constraints.gridy = 1
        constraints.insets = Insets(5, 8, 5, 8)
        m_jcbItems.setBackground(bgCBColor)
        jlp.add(m_jcbItems, constraints)
        jlp.setBackground(bgColor)
        val bl = BorderLayout()
        bl.hgap = 0
        bl.vgap = 0
        setLayout(bl)
        add(jlp, BorderLayout.CENTER)
    }
}
