package core.gui;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;
import core.util.Helper;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.compare.ComparableUtils.is;

public class LabelWithSignedNumber extends JLabel {

    private static final String PLUS_SIGN = "+";

    private final String notAvailableString = TranslationFacility.tr("ls.general_label.not_available_abbreviation");

    public LabelWithSignedNumber(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public void setNumber(Integer number) {
        final var numberformat = Helper.getNumberFormat( 0);
        if (number != null) {
            setForeground(getColor(number));
            setText(formatNumberWithSign(numberformat, number));
        } else {
            setForeground(getColor(0));
            setText(notAvailableString);
        }
    }

    public void setPercentNumber(BigDecimal percentage) {
        if (percentage != null) {
            setForeground(getColor(percentage));
            setText(formatNumberWithSign(percentage));
        } else {
            setForeground(getColor(0));
            setText(notAvailableString);
        }
    }

    private static Color getColor(int number) {
        return getColorFromCompareResult(Integer.compare(number, 0));
    }

    private static Color getColor(BigDecimal number) {
        return getColorFromCompareResult(number.compareTo(ZERO));
    }

    private static Color getColorFromCompareResult(int compareResult) {
        if (compareResult > 0) {
            return ThemeManager.getColor(HOColorName.TABLEENTRY_IMPROVEMENT_FG);
        } else if (compareResult < 0) {
            return ThemeManager.getColor(HOColorName.TABLEENTRY_DECLINE_FG);
        } else {
            return ThemeManager.getColor(HOColorName.TABLEENTRY_FG);
        }
    }

    private static String formatNumberWithSign(NumberFormat numberformat, int number) {
        final String prefix = (number > 0) ? PLUS_SIGN : EMPTY;
        return prefix + numberformat.format(number);
    }

    private static String formatNumberWithSign(BigDecimal percentage) {
        final String prefix = is(percentage).greaterThan(ZERO) ? PLUS_SIGN : EMPTY;
        return prefix + percentageValueString(percentage);
    }

    public static String percentString(int w, int g) {
        return percentageValueString(BigDecimal.valueOf(w).divide(BigDecimal.valueOf(g), 4, RoundingMode.HALF_UP));
    }

    public static String percentageValueString(BigDecimal percentage) {
        final BigDecimal percent = percentage
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
        return String.format("%s %%", percent);
    }
}