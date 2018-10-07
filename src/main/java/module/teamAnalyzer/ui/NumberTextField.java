// %1126721045698:hoplugins.commons.ui%
package module.teamAnalyzer.ui;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * configurable JLabel that handles numeric values
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class NumberTextField extends JTextField {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8515843904965462011L;
	private int _intDigits = 0;
    private int _maxDigits = 0;
    private short _decDigits = 0;

    /**
     * costructor for integer values only
     *
     * @param columns max number of chars
     */
    public NumberTextField(int columns) {
        super(columns);
        _maxDigits = columns;
        _decDigits = 0;
        _intDigits = _maxDigits;

        setDocument(new DecimalFieldDocument(_decDigits));
    }

    /**
     * Returns the int value
     *
     * @return value in the cell, or 0 if empty
     */
    public int getValue() {
        String s = getText();
        int value = 0;

        try {
            value = Integer.parseInt(s);
        }
        catch (Exception e) {
        }

        return value;
    }

    /**
     * Make sure any set String is valid.
     *
     * @param str String to check for validity
     * @param offs offset to be applied
     *
     * @return updated string
     */
    private String _getNewString(String str, int offs) {
        String currentText = getText();
        int currLen = currentText.length();
        String newString = "";

        if (offs == 0) {
            newString = str + currentText;
        }
        else if (offs >= currLen) {
            newString = currentText + str;
        }
        else {
            newString = currentText.substring(0, offs) + str
                + currentText.substring(offs, currLen);
        }

        return newString;
    }

    /**
     * Make sure this string is numberic
     *
     * @param str String to check for validity
     * @param offs offset to be applied
     *
     * @return true if number, or false if not
     */
    private boolean _validateNumberString(String str, int offs) {
        char c;

        String newString = this._getNewString(str, offs);

        int newLength = newString.length();

        if (newLength > _intDigits) {
            return false;
        }

        // accepts only numerical digits
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);

            if ((c < '0') || (c > '9')) {
                return false;
            }
        }

        newString = this._getNewString(str, offs);

        if (!newString.equals("") && !newString.equals(".")) {
            try {
                new Double(newString);
            }
            catch (NumberFormatException e) {
                return false;
            }

            return true;
        }
        
        return false;
       
    }

    /**
     * Document to manage conversion to number
     *
     * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
     */
    private class DecimalFieldDocument extends PlainDocument {
        /**
		 * 
		 */
		private static final long serialVersionUID = 5081324121361165478L;


        /**
         * Creates a new DecimalFieldDocument object.
         *
         * @param length lenght of numbetr
         */
        public DecimalFieldDocument(short length) {
            
        }

        /**
         * Method to insert string in the right position
         *
         * @param offs offset
         * @param str String to be inserted
         * @param a AttributeSet
         *
         * @throws BadLocationException an exception is thrown if the new string is not a number
         */
        @Override
		public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
            if (str != null) {
                if (!_validateNumberString(str.trim(), offs)) {
                    throw new BadLocationException(null, offs);
                }
                
               super.insertString(offs, str.trim(), a);

            }
        }
    }
}
