package tool.arenasizer

import core.gui.theme.HOColorName
import core.gui.theme.ThemeManager
import core.model.HOVerwaltung
import core.util.HOLogger
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.swing.*

internal class ControlPanel : JPanel(), ActionListener {
    private val percentageFormat = DecimalFormat("###.# %")
    private val factorFormat = DecimalFormat("##")

    private val calculateButton = JButton(HOVerwaltung.instance().getLanguageString("Calculate"))
    private val fansField = JTextField(7)
    private val totalSizeField = JTextField(7)
    private val vipField = JTextField(7)
    private val basicField = JTextField(7) //Basic
    private val terracesField = JTextField(7) //Terraces
    private val roofField = JTextField(7) //Roof
    private val vipPercentField = JFormattedTextField(percentageFormat)
    private val basicPercentField = JFormattedTextField(percentageFormat)
    private val terracesPercentField = JFormattedTextField(percentageFormat)
    private val roofPercentField = JFormattedTextField(percentageFormat)
    private val factorNormalField = JFormattedTextField(factorFormat)
    private val exampleLabel = JLabel("")

    private val layout2 = GridBagLayout()
    private val constraints2 = GridBagConstraints()

    init {
        initialize()
    }

    private fun initialize() {
        constraints2.fill = GridBagConstraints.WEST
        constraints2.weightx = 0.0
        constraints2.weighty = 0.0
        constraints2.insets = Insets(3, 3, 3, 3)
        setLayout(layout2)
        var label: JLabel = JLabel(HOVerwaltung.instance().getLanguageString("Gesamtgroesse"))
        addToLayout(label, 0, 0)
        setFieldProperties(totalSizeField)
        // m_jtfGesamtgroesse.addFocusListener(this);
        addToLayout(totalSizeField, 1, 0)
        label = JLabel(HOVerwaltung.instance().getLanguageString("ls.club.arena.terraces"))
        addToLayout(label, 2, 0)
        setFieldProperties(terracesPercentField)
        addToLayout(terracesPercentField, 3, 0)
        label = JLabel(HOVerwaltung.instance().getLanguageString("ls.club.arena.basicseating"))
        addToLayout(label, 4, 0)
        setFieldProperties(basicPercentField)
        addToLayout(basicPercentField, 5, 0)
        label = JLabel(HOVerwaltung.instance().getLanguageString("ls.club.arena.seatsunderroof"))
        addToLayout(label, 6, 0)
        setFieldProperties(roofPercentField)
        addToLayout(roofPercentField, 7, 0)
        label = JLabel(HOVerwaltung.instance().getLanguageString("ls.club.arena.seatsinvipboxes"))
        addToLayout(label, 8, 0)
        setFieldProperties(vipPercentField)
        addToLayout(vipPercentField, 9, 0)
        terracesField.setHorizontalAlignment(SwingConstants.RIGHT)
        addToLayout(terracesField, 3, 1)
        basicField.setHorizontalAlignment(SwingConstants.RIGHT)
        addToLayout(basicField, 5, 1)
        roofField.setHorizontalAlignment(SwingConstants.RIGHT)
        addToLayout(roofField, 7, 1)
        vipField.setHorizontalAlignment(SwingConstants.RIGHT)
        addToLayout(vipField, 9, 1)
        calculateButton.setToolTipText(HOVerwaltung.instance().getLanguageString("Calculate"))
        calculateButton.addActionListener(this)
        addToLayout(calculateButton, 1, 1)
        label = JLabel(HOVerwaltung.instance().getLanguageString("Fans"))
        addToLayout(label, 0, 2)
        fansField.setHorizontalAlignment(SwingConstants.RIGHT)
        addToLayout(fansField, 1, 2)
        label = JLabel(HOVerwaltung.instance().getLanguageString("Durchschnitt"))
        addToLayout(label, 2, 2)
        setFieldProperties(factorNormalField)
        addToLayout(factorNormalField, 3, 2)
        constraints2.gridwidth = 2
        addToLayout(exampleLabel, 4, 2)
        constraints2.gridwidth = 1
        val dim = vipField.getPreferredSize()
        vipPercentField.preferredSize = dim
        basicPercentField.preferredSize = dim
        terracesPercentField.preferredSize = dim
        roofPercentField.preferredSize = dim
        factorNormalField.preferredSize = dim
        initStadium()
    }

    private fun addToLayout(c: JComponent, x: Int, y: Int) {
        constraints2.gridx = x
        constraints2.gridy = y
        layout2.setConstraints(c, constraints2)
        add(c)
    }

    private fun setFieldProperties(txt: JTextField) {
        txt.setHorizontalAlignment(SwingConstants.RIGHT)
        vipPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG))
    }

    //Init aus dem HRF
    private fun initStadium() {
        //Nur, wenn es eine HRFArena ist
        val m_clStadium = HOVerwaltung.instance().model.getStadium()
        val fans = HOVerwaltung.instance().model.getClub().fans
        fansField.text = fans.toString() + ""
        terracesField.text = m_clStadium.standing.toString() + ""
        basicField.text = m_clStadium.basicSeating.toString() + ""
        roofField.text = m_clStadium.seatingUnderRoof.toString() + ""
        vipField.text = m_clStadium.vip.toString() + ""
        totalSizeField.text = m_clStadium.totalSize().toString() + ""
        terracesPercentField.setValue(ArenaSizer.Companion.TERRACES_PERCENT)
        basicPercentField.setValue(ArenaSizer.Companion.BASICS_PERCENT)
        roofPercentField.setValue(ArenaSizer.Companion.ROOF_PERCENT)
        vipPercentField.setValue(ArenaSizer.Companion.VIP_PERCENT)
        factorNormalField.setValue(ArenaSizer.Companion.SUPPORTER_NORMAL)
        exampleLabel.setText(
            "=> " + fans + " * " + (factorNormalField.value as Number).toInt() + " = " + fans * (factorNormalField.value as Number).toInt() + " (" + HOVerwaltung.instance()
                .getLanguageString("Zuschauer") + " )"
        )
    }

    val stadium: Stadium
        get() {
            var steh = 0
            var sitz = 0
            var ueber = 0
            var loge = 0
            try {
                steh = terracesField.getText().toInt()
                sitz = basicField.getText().toInt()
                ueber = roofField.getText().toInt()
                loge = vipField.getText().toInt()
            } catch (e: NumberFormatException) {
                HOLogger.instance().log(javaClass, "Fehler: keine Zahl")
            }
            val stadium = Stadium()
            stadium.standing = steh
            stadium.basicSeating = sitz
            stadium.seatingUnderRoof = ueber
            stadium.vip = loge
            totalSizeField.text = stadium.totalSize().toString()
            return stadium
        }
    val supporter: Int
        get() = fansField.getText().toInt()
    val modifiedSupporter: IntArray
        get() {
            val supporter = IntArray(3)
            supporter[0] = ((factorNormalField.value as Number).toInt() + 5) * this.supporter
            supporter[1] = (factorNormalField.value as Number).toInt() * this.supporter
            supporter[2] = ((factorNormalField.value as Number).toInt() - 5) * this.supporter

            // the wrong place for this, only temp playCE
            exampleLabel.setText(
                "=> " + fansField.getText() + " * " + (factorNormalField.value as Number).toInt() + " = " + fansField.getText()
                    .toInt() * (factorNormalField.value as Number).toInt() + " (" + HOVerwaltung.instance()
                    .getLanguageString("Zuschauer") + " )"
            )
            return supporter
        }

    override fun actionPerformed(actionEvent: ActionEvent) {
        var size = BigDecimal.ZERO
        try {
            size = BigDecimal(totalSizeField.getText().toInt())
        } catch (e: NumberFormatException) {
            HOLogger.instance().log(javaClass, "Error: no number entered.")
        }

        // Rounding required post-BigDecimal conversion, as getValue() introduces rounding errors.
        val mc = MathContext(3, RoundingMode.HALF_EVEN)
        val tPercent = (terracesPercentField.value as Double).toBigDecimal().round(mc)
        val bPercent = (basicPercentField.value as Double).toBigDecimal().round(mc)
        val rPercent = (roofPercentField.value as Double).toBigDecimal().round(mc)
        val vPercent = (vipPercentField.value as Double).toBigDecimal().round(mc)

        val sum = tPercent.add(bPercent).add(rPercent).add(vPercent)

        if (sum.multiply(HUNDRED).compareTo(HUNDRED) != 0) {
            JOptionPane.showMessageDialog(
                getTopLevelAncestor(),
                sum.multiply(HUNDRED).setScale(1).toString() + " % <> " + HUNDRED + " %",
                HOVerwaltung.instance().getLanguageString("Fehler"),
                JOptionPane.ERROR_MESSAGE
            )
            terracesPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_ERROR_FG))
            basicPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_ERROR_FG))
            roofPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_ERROR_FG))
            vipPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_ERROR_FG))
        } else {
            terracesPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG))
            basicPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG))
            roofPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG))
            vipPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG))
            terracesField.text = tPercent.multiply(size).toInt().toString() + ""
            basicField.text = bPercent.multiply(size).toInt().toString() + ""
            roofField.text = rPercent.multiply(size).toInt().toString() + ""
            vipField.text = vPercent.multiply(size).toInt().toString() + ""

        }
    }

    companion object {
        private val HUNDRED = BigDecimal(100)
    }
}
