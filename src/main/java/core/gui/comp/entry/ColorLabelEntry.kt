package core.gui.comp.entry

import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.gui.theme.HOColorName
import core.gui.theme.ImageUtilities
import core.gui.theme.ThemeManager
import core.model.UserParameter
import core.util.Helper
import java.awt.Color
import java.awt.Font
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.math.abs

class ColorLabelEntry : JLabel, IHOTableEntry {
    //~ Instance fields ----------------------------------------------------------------------------
    private var m_clBGColor = BG_STANDARD
    private var m_clFGColor = FG_STANDARD

    //For use by compareTo()
    var number = Double.NEGATIVE_INFINITY
        private set

    //~ Constructors -------------------------------------------------------------------------------
    constructor(text: String?) : super(text, LEFT) {
        number = Double.NEGATIVE_INFINITY
        createComponent()
        setOpaque(false)
    }

    /**
     * Colour Label without icon
     */
    constructor(
        text: String?,
        foreground: Color,
        background: Color,
        horizontalOrientation: Int
    ) : this(Double.NEGATIVE_INFINITY, text, foreground, background, horizontalOrientation)

    /**
     * Colour Label with text and sortIndex
     */
    constructor(
        sortIndex: Double, text: String?, foreground: Color, background: Color,
        horizontalOrientation: Int
    ) : super(text, horizontalOrientation) {
        number = sortIndex
        m_clFGColor = foreground
        m_clBGColor = background
        createComponent()
    }

    /**
     * Colour Label with icon and sortIndex
     */
    constructor(
        icon: Icon?, sortIndex: Double, foreground: Color, background: Color,
        horizontalOrientation: Int
    ) : super("", icon, horizontalOrientation) {
        number = sortIndex
        m_clFGColor = foreground
        m_clBGColor = background
        createComponent()
    }

    /**
     * Colour Label with Image for representation of changes
     */
    constructor(integerNumber: Int, number: Double, current: Boolean, background: Color, withText: Boolean) {
        if (integerNumber != 0 || !withText) {
            setIcon(ImageUtilities.getImageIcon4Veraenderung(integerNumber, current))
        }
        setHorizontalAlignment(RIGHT)
        m_clBGColor = background

        // Create Component first, then change the text accordingly [setValueAsText()]
        createComponent()
        if (integerNumber == 0 && abs(number) > 0.005 && withText) {
            // Yes, we want negative numbers too
            val zahl2 = integerNumber + number
            setValueAsText(
                zahl2, background, false, false,
                UserParameter.instance().nbDecimals, true
            )
        }
    }

    /**
     * Colour Label with Image for representation of changes (with text as a string)
     *
     * @param changeVal  Change value for the icon
     * @param text       text to show
     * @param sortVal    value for sort
     * @param current    current or old data set
     * @param background background color
     * @param withText   show the text?
     */
    constructor(
        changeVal: Int, text: String?, sortVal: Double, current: Boolean, background: Color,
        withText: Boolean
    ) {
        if (changeVal != 0 || !withText) {
            setIcon(ImageUtilities.getImageIcon4Veraenderung(changeVal, current))
        }
        setHorizontalAlignment(RIGHT)
        m_clBGColor = background
        number = sortVal

        // Create Component first, then change the text accordingly [setValueAsText()]
        createComponent()
        if (withText) setText(text) else setText("")
    }

    /**
     * Colour label to represent changes of background colour
     */
    constructor(
        newNumber: Float, bgColor: Color?, currencyFormat: Boolean,
        invertColour: Boolean, decimalPlaces: Int
    ) {
        setHorizontalAlignment(RIGHT)
        createComponent()
        setValueAsText(newNumber.toDouble(), bgColor, currencyFormat, invertColour, decimalPlaces, true)
    }

    /**
     * Colour Label to present value of money with background colour, decimal places are only for
     * non-currency interest
     */
    constructor(
        newNumber: Double, bgColor: Color?, currencyFormat: Boolean,
        decimalPlaces: Int
    ) {
        setHorizontalAlignment(RIGHT)
        createComponent()
        setValueAsText(newNumber, bgColor, currencyFormat, false, decimalPlaces, false)
    }

    /**
     * Helper method to format the value of 'number' and instance fields accordingly.
     */
    private fun setValueAsText(
        newNumber: Double, bgColor: Color?, currencyFormat: Boolean,
        invertColour: Boolean, decimalPlaces: Int, colorAndSign: Boolean
    ) {
        number = newNumber
        setText(
            (if (number > 0 && colorAndSign) "+" else "") +
                    Helper.getNumberFormat(currencyFormat, decimalPlaces).format(number)
        )
        if (colorAndSign) {
            m_clFGColor = if (number > 0 && !invertColour || number < 0 && invertColour) {
                // Positive change
                ThemeManager.getColor(HOColorName.TABLEENTRY_IMPROVEMENT_FG)
            } else if (number == 0.0) {
                // Neutral
                setText("")
                FG_STANDARD
            } else {
                // Negative change
                ThemeManager.getColor(HOColorName.TABLEENTRY_DECLINE_FG)
            }
        }
        if (bgColor != null) m_clBGColor = bgColor
        updateComponent()
    }

    fun setAlignment(orientation: Int) {
        setHorizontalAlignment(orientation)
    }

    override fun getComponent(isSelected: Boolean): JComponent {
        if (isSelected) {
            setBackground(HODefaultTableCellRenderer.SELECTION_BG)
        } else {
            setBackground(m_clBGColor)
        }
        setForeground(if (isSelected) HODefaultTableCellRenderer.SELECTION_FG else m_clFGColor)
        return this
    }

    fun setFGColor(fgcolor: Color) {
        m_clFGColor = fgcolor
        updateComponent()
    }

    fun setFontStyle(fontStyle: Int) {
        setFont(font.deriveFont(fontStyle))
    }

    fun deriveFont(fontStyle: Int, size: Float) {
        setFont(font.deriveFont(fontStyle, size))
    }

    /**
     * Sets the change graphics (For values without sub-skills, e.g. form/stamina/XP...)
     */
    fun setGraphicalChangeValue(number: Double, current: Boolean, withText: Boolean) {
        setIcon(ImageUtilities.getImageIcon4Veraenderung(Helper.round(number, 1).toInt(), current))
        if (withText) {
            setGraphicalChangeValue(number)
        }
        updateComponent()
    }

    /**
     * Sets the change graphics (For values with sub-skills, e.g. the normal skills)
     */
    fun setGraphicalChangeValue(
        integerNumber: Int, number: Double, current: Boolean,
        withText: Boolean
    ) {
        setIcon(ImageUtilities.getImageIcon4Veraenderung(Helper.round(integerNumber.toFloat(), 1).toInt(), current))
        if (withText) {
            setGraphicalChangeValue(integerNumber + number)
        }
        updateComponent()
    }

    /**
     * @param number
     */
    private fun setGraphicalChangeValue(number: Double) {
        setValueAsText(
            number, null, false, false,
            UserParameter.instance().nbDecimals,
            true
        )
    }

    fun setIcon(icon: Icon?, imageAusrichtung: Int) {
        setIcon(icon)
        setHorizontalTextPosition(imageAusrichtung)
        updateComponent()
    }

    fun setSpecialNumber(number: Int, currencyformat: Boolean) {
        setSpecialNumber(number, currencyformat, false)
    }

    fun setSpecialNumber(number: Int, currencyformat: Boolean, showZero: Boolean) {
        setValueAsText(number.toDouble(), null, currencyformat, false, 0, true)
        if (number == 0 && !showZero) {
            setText("")
            updateComponent()
        }
    }

    fun setSpecialNumber(number: Float, currencyformat: Boolean) {
        setValueAsText(
            number.toDouble(), null, currencyformat, false,
            UserParameter.instance().nbDecimals,
            true
        )
    }

    fun setSpecialNumber(number: Float, currencyformat: Boolean, showDecimal: Boolean) {
        val nbDec = if (showDecimal) UserParameter.instance().nbDecimals else 0
        setValueAsText(number.toDouble(), null, currencyformat, false, nbDec, true)
    }

    override fun clear() {
        setText("")
        setIcon(null)
        updateComponent()
    }

    /**
     * Compare two ColorLabelEntry objects based on non negative number or text.
     */
    override fun compareTo(other: IHOTableEntry): Int {
        if (other is ColorLabelEntry) {
            if (number.toFloat() != Float.NEGATIVE_INFINITY) {
                val number1 = number
                val number2 = other.number
                return if (number1 < number2) {
                    -1
                } else if (number1 > number2) {
                    1
                } else {
                    text.compareTo(other.text)
                }
            }
            //Not number -> String
            return text.compareTo(other.text)
        }
        return 0
    }

    //-------------------------------------------------------------    
    override fun createComponent() {
        setOpaque(true)
        setForeground(m_clFGColor)
    }

    override fun updateComponent() {
        setBackground(m_clBGColor)
        setForeground(m_clFGColor)
    }

    override fun compareToThird(obj: IHOTableEntry): Int {
        return this.compareTo(obj)
    }

    fun setBold(bold: Boolean) {
        val style = if (bold) Font.BOLD else Font.PLAIN
        setFont(font.deriveFont(style))
    }

    companion object {
        @JvmField
        val FG_STANDARD = ThemeManager.getColor(HOColorName.TABLEENTRY_FG)
        @JvmField
        val BG_STANDARD = ThemeManager.getColor(HOColorName.TABLEENTRY_BG)
        @JvmField
        val BG_PLAYERSPECIALVALUES = ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG)
        @JvmField
        val BG_SINGLEPLAYERVALUES = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG)
        @JvmField
        val BG_PLAYERSPOSITIONVALUES = ThemeManager.getColor(HOColorName.PLAYER_POS_BG)
        @JvmField
        val BG_PLAYERSSUBPOSITIONVALUES = ThemeManager.getColor(HOColorName.PLAYER_SUBPOS_BG)
    }
}
