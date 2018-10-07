package core.gui.comp;

import core.util.StringUtils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A document for text components which allows numeric chars only.
 */
public class NumericDocument extends PlainDocument {

	private static final long serialVersionUID = -7376216000843726838L;
	private int maxLength = -1;
	private boolean allowNegatives;

	/**
	 * Constructs a new NumericDocument. Will not allow negatives, length is not
	 * limited.
	 */
	public NumericDocument() {
		super();
	}

	/**
	 * Constructs a new NumericDocument.
	 * 
	 * @param allowNegatives
	 *            <code>true</code> to allow negative values, <code>false</code>
	 *            otherwise.
	 */
	public NumericDocument(boolean allowNegatives) {
		this.allowNegatives = allowNegatives;
	}

	/**
	 * Constructs a new NumericDocument with a maximum number of digits.
	 * 
	 * @param maxLength
	 *            the maximum number of digits allowed.
	 * @throws IllegalArgumentException
	 *             if the given maxLength is less than (or equal) 0.
	 */
	public NumericDocument(int maxLength) {
		this(maxLength, false);
	}

	/**
	 * Constructs a new NumericDocument with a maximum number of digits.
	 * 
	 * @param maxLength
	 *            the maximum number of digits allowed.
	 * @param allowNegatives
	 *            <code>true</code> to allow negative values, <code>false</code>
	 *            otherwise.
	 * @throws IllegalArgumentException
	 *             if the given maxLength is less than (or equal) 0.
	 */
	public NumericDocument(int maxLength, boolean allowNegatives) {
		this();
		this.allowNegatives = allowNegatives;
		setMaxLength(maxLength);
	}

	/**
	 * Returns <code>true</code> if negative Numbers are allowed (a minus sign
	 * can be inserted at offset 0 in this case), <code>false</code> if not.
	 * 
	 * @return <code>true</code> if negative Numbers are allowed,
	 *         <code>false</code> otherwise.
	 */
	public boolean isAllowNegatives() {
		return this.allowNegatives;
	}

	/**
	 * Gets the maximum length a text in this document can have.
	 * 
	 * @return the maximum length or <code>-1</code> if the length is not
	 *         limited.
	 */
	public int getMaxLength() {
		return this.maxLength;
	}

	@Override
	public void insertString(int offs, String toBInserted, AttributeSet a)
			throws BadLocationException {
		if (StringUtils.isEmpty(toBInserted)) {
			return;
		}

		// if maxlength > -1 ==> check length
		if (this.maxLength > -1) {
			// if too long ==> do not insert
			if (getLength() + toBInserted.length() > this.maxLength) {
				return;
			}
		}

		// if there is already a minus sign, nothing can be inserted with
		// offset 0, because minus sign has to be the first char
		if (this.isAllowNegatives()) {
			String currentText = getText(0, getLength());
			if (!currentText.isEmpty() && offs == 0
					&& currentText.charAt(0) == '-') {
				return;
			}
		}

		if (StringUtils.isNumeric(toBInserted)) {
			super.insertString(offs, toBInserted, a);
		} else {
			if (this.allowNegatives) {
				if (offs == 0 && toBInserted.charAt(0) == '-'
						&& StringUtils.isNumeric(toBInserted.substring(1))) {
					super.insertString(offs, toBInserted, a);
				}
			}
		}
	}

	private void setMaxLength(int maxLength) throws IllegalArgumentException {
		if (maxLength <= 0) {
			throw new IllegalArgumentException(
					"the maximum length has to be greater than 0!");
		}
		this.maxLength = maxLength;
	}
}
