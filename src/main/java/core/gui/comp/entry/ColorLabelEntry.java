package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.util.Helper;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.awt.Font;
import javax.swing.*;

public class ColorLabelEntry extends JLabel implements IHOTableEntry {

    public static final Color FG_STANDARD = ThemeManager.getColor(HOColorName.TABLEENTRY_FG);
    public static final Color BG_STANDARD = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);

    public static final Color BG_PLAYERSPECIALVALUES = ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
    public static final Color BG_SINGLEPLAYERVALUES = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
    public static final Color BG_PLAYERSPOSITIONVALUES = ThemeManager.getColor(HOColorName.PLAYER_POS_BG);
    public static final Color BG_PLAYERSSUBPOSITIONVALUES = ThemeManager.getColor(HOColorName.PLAYER_SUBPOS_BG);

    //~ Instance fields ----------------------------------------------------------------------------
    private Color m_clBGColor = ColorLabelEntry.BG_STANDARD;
    private Color m_clFGColor = ColorLabelEntry.FG_STANDARD;

    //For use by compareTo()
    private double number = Double.NEGATIVE_INFINITY;


    //~ Constructors -------------------------------------------------------------------------------

    public ColorLabelEntry(String text) {
        super(text, SwingConstants.LEFT);
        number = Double.NEGATIVE_INFINITY;
        createComponent();
        setOpaque(false);
    }

    /**
     * Colour Label without icon
     */
    public ColorLabelEntry(String text, Color foreground, Color background, int horizontalOrientation) {
        this(Double.NEGATIVE_INFINITY, text, foreground, background, horizontalOrientation);
    }

    /**
     * Colour Label with text and sortIndex
     */
    public ColorLabelEntry(double sortIndex, String text, Color foreground, Color background,
                           int horizontalOrientation) {
        super(text, horizontalOrientation);
        number = sortIndex;
        m_clFGColor = foreground;
        m_clBGColor = background;
        createComponent();
    }

    /**
     * Colour Label with icon and sortIndex
     */
    public ColorLabelEntry(Icon icon, double sortIndex, Color foreground, Color background,
                           int horizontalOrientation) {
        super("", icon, horizontalOrientation);
        number = sortIndex;
        m_clFGColor = foreground;
        m_clBGColor = background;
        createComponent();
    }

    /**
     * Colour Label with Image for representation of changes
     */
    public ColorLabelEntry(int integerNumber, double number, boolean current, Color background, boolean withText) {
        if ((integerNumber != 0) || !withText) {
            setIcon(ImageUtilities.getImageIcon4Veraenderung(integerNumber, current));
        }

        setHorizontalAlignment(SwingConstants.RIGHT);
        m_clBGColor = background;

        // Create Component first, then change the text accordingly [setValueAsText()]
        createComponent();

        if ((integerNumber == 0) && (Math.abs(number) > 0.005d) && withText) {
            // Yes, we want negative numbers too
            final double zahl2 = integerNumber + number;
            setValueAsText(zahl2, background, false, false,
                    core.model.UserParameter.instance().nbDecimals, true);
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
    public ColorLabelEntry(int changeVal, String text, double sortVal, boolean current, Color background,
                           boolean withText) {

        if ((changeVal != 0) || !withText) {
            setIcon(ImageUtilities.getImageIcon4Veraenderung(changeVal, current));
        }

        setHorizontalAlignment(SwingConstants.RIGHT);
        m_clBGColor = background;
        number = sortVal;

        // Create Component first, then change the text accordingly [setValueAsText()]
        createComponent();

        if (withText)
            setText(text);
        else
            setText("");
    }


    /**
     * Colour label to represent changes of background colour
     */
    public ColorLabelEntry(float newNumber, Color bg_color, boolean currencyformat,
                           boolean invertColour, int decimalPlaces) {
        setHorizontalAlignment(SwingConstants.RIGHT);
        createComponent();
        setValueAsText(newNumber, bg_color, currencyformat, invertColour, decimalPlaces, true);
    }

    /**
     * Colour Label to present value of money with background colour, decimal places are only for
     * non-currency interest
     */
    public ColorLabelEntry(double newNumber, Color bg_color, boolean currencyformat,
                           int decimalPlaces) {
        setHorizontalAlignment(SwingConstants.RIGHT);
        createComponent();
        setValueAsText(newNumber, bg_color, currencyformat, false, decimalPlaces, false);
    }

    /**
     * Helper method to format the value of 'number' and instance fields accordingly.
     */
    private void setValueAsText(double newNumber, Color bg_color, boolean currencyformat,
                                boolean invertColour, int decimalPlaces, boolean colorAndSign) {
        this.number = newNumber;

        setText((number > 0 && colorAndSign ? "+" : "") +
                Helper.getNumberFormat(currencyformat, decimalPlaces).format(number));

        if (colorAndSign) {
            if (number > 0 && !invertColour || number < 0 && invertColour) {
                // Positive change
                m_clFGColor = ThemeManager.getColor(HOColorName.TABLEENTRY_IMPROVEMENT_FG);
            } else if (number == 0) {
                // Neutral
                setText("");
                m_clFGColor = FG_STANDARD;
            } else {
                // Negative change
                m_clFGColor = ThemeManager.getColor(HOColorName.TABLEENTRY_DECLINE_FG);
            }
        }
        if (bg_color != null)
            m_clBGColor = bg_color;
        updateComponent();
    }

    public final void setAlignment(int orientation) {
        setHorizontalAlignment(orientation);
    }

    @Override
    public final JComponent getComponent(boolean isSelected) {

        if (isSelected) {
            setBackground(HODefaultTableCellRenderer.SELECTION_BG);

        } else {
            setBackground(m_clBGColor);
        }
        setForeground(isSelected ? HODefaultTableCellRenderer.SELECTION_FG : m_clFGColor);
        return this;
    }

    public final void setFGColor(Color fgcolor) {
        m_clFGColor = fgcolor;
        updateComponent();
    }

    public final void setFontStyle(int fontStyle) {
        setFont(getFont().deriveFont(fontStyle));
    }

    public void deriveFont(int fontStyle, float size){
        setFont(getFont().deriveFont(fontStyle,size));
    }

    /**
     * Sets the change graphics (For values without sub-skills, e.g. form/stamina/XP...)
     */
    public final void setGraphicalChangeValue(double number, boolean current, boolean withText) {

        setIcon(ImageUtilities.getImageIcon4Veraenderung((int) Helper.round(number, 1), current));

        if (withText) {
            setGraphicalChangeValue(number);
        }
        updateComponent();
    }

    /**
     * Sets the change graphics (For values with sub-skills, e.g. the normal skills)
     */
    public void setGraphicalChangeValue(int integerNumber, double number, boolean current,
                                              boolean withText) {

        setIcon(ImageUtilities.getImageIcon4Veraenderung((int) Helper.round(integerNumber, 1), current));

        if (withText) {
            setGraphicalChangeValue(integerNumber + number);
        }
        updateComponent();
    }

    /**
     * @param number Double
     */
    private void setGraphicalChangeValue(double number) {
        setValueAsText(number, null, false, false,
                core.model.UserParameter.instance().nbDecimals,
                true);
    }

    /**
     * Initialize adding of icons to this entry
     * Previously set icon (or text) will be moved to newly created child components of this entry.
     * Layout is initialized to box layout.
     */
    private void initAdd() {
        var isInitDone = this.getLayout() != null && this.getLayout().getClass() == BoxLayout.class;
        if (!isInitDone){
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            var icon = this.getIcon();
            if (icon!=null){
                super.setIcon(null);
                this.add(new JLabel(icon));
            }
            var text = this.getText();
            if (!text.isEmpty()){
                super.setText("");
                this.add(new JLabel(text));
            }
        }
    }

    /**
     * Add an icon to the entry
     * A child Jlabel is added, which holds the specified icon.
     * @param icon Icon to be added
     */
    public void addIcon(Icon icon){
        initAdd();
        this.add(new JLabel(icon));
    }

    /**
     * Add a text label to the entry
     * A child JLabel is added, which holds the specified text.
     * @param text Text of the new child component
     */
    public void addText(String text){
        initAdd();
        this.add(new JLabel(text));
    }

    /**
     * Set the icon
     * (would be overwritten by added components)
     * @param icon Icon to be set
     * @param textPosition Horizontal text position (swing constant value)
     */
    public void setIcon(Icon icon, int textPosition) {
        setIcon(icon);
        setHorizontalTextPosition(textPosition);
        updateComponent();
    }

    public void setSpecialNumber(int number, boolean currencyformat) {
        setSpecialNumber(number, currencyformat, false);
    }

    public final void setSpecialNumber(int number, boolean currencyformat, boolean showZero) {
        setValueAsText(number, null, currencyformat, false, 0, true);
        if (number == 0 && !showZero) {
            setText("");
            updateComponent();
        }
    }

    public final void setSpecialNumber(float number, boolean currencyformat) {
        setValueAsText(number, null, currencyformat, false,
                core.model.UserParameter.instance().nbDecimals,
                true);
    }

    public final void setSpecialNumber(float number, boolean currencyformat, boolean showDecimal) {
        var nbDec = showDecimal ? core.model.UserParameter.instance().nbDecimals : 0;
        setValueAsText(number, null, currencyformat, false, nbDec, true);
    }

    public final double getNumber() {
        return number;
    }

    @Override
    public final void clear() {
        setText("");
        setIcon(null);
        updateComponent();
    }

    /**
     * Compare two ColorLabelEntry objects based on non negative number or text.
     */
    @Override
    public final int compareTo(@NotNull IHOTableEntry obj) {
        if (obj instanceof ColorLabelEntry entry) {

            if (number != Float.NEGATIVE_INFINITY) {
                final double number1 = number;
                final double number2 = entry.getNumber();

                if (number1 < number2) {
                    return -1;
                } else if (number1 > number2) {
                    return 1;
                } else {
                    return getText().compareTo(entry.getText());
                }
            }
            //Not number -> String
            return getText().compareTo(entry.getText());

        }

        return 0;
    }

    //-------------------------------------------------------------    

    @Override
    public final void createComponent() {
        setOpaque(true);
        setForeground(m_clFGColor);
    }

    @Override
    public final void updateComponent() {
        setBackground(m_clBGColor);
        setForeground(m_clFGColor);
    }

    @Override
    public final int compareToThird(IHOTableEntry obj) {
        return this.compareTo(obj);
    }

    public void setBold(boolean bold) {
        int style = (bold) ? Font.BOLD : Font.PLAIN;
        setFont(getFont().deriveFont(style));
    }
}