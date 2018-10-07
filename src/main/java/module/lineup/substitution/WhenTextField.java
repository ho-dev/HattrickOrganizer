package module.lineup.substitution;

import core.util.StringUtils;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;


public class WhenTextField extends JFormattedTextField {

	private static final long serialVersionUID = 1207880109251770680L;
	private String noValueDisplayString;
	private String valueDisplayString;

	/**
	 * Creates a new WhenTextField.
	 * 
	 * @param noValueDisplayString
	 *            the string to display if the textield is in "display mode"
	 *            (does not contain the cursor, as opposed to "edit mode") and
	 *            the field has no value.
	 * @param valueDisplayString
	 *            the string to display if the textield is in "display mode"
	 *            (does not contain the cursor, as opposed to "edit mode") and
	 *            the field has a value. The string has to contain
	 *            <code>{0}</code> which will be replaced by the value.
	 */
	public WhenTextField(String noValueDisplayString, String valueDisplayString) {
		this.noValueDisplayString = noValueDisplayString;
		this.valueDisplayString = valueDisplayString;
		init();
		addListeners();
	}

	private void init() {
		JFormattedTextField.AbstractFormatter editFormatter = new EditFormatter();
		JFormattedTextField.AbstractFormatter displayFormatter = new DisplayFormatter();
		DefaultFormatterFactory factory = new DefaultFormatterFactory(displayFormatter, displayFormatter,
				editFormatter);
		setFormatterFactory(factory);
		setValue(Integer.valueOf(-1));
	}

	private void addListeners() {
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						selectAll();
					}
				});
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						commitEdit();
						transferFocus();
					} catch (ParseException ex) {
						throw new RuntimeException(ex);
					}
				}
			}
		});
	}

	private class DisplayFormatter extends JFormattedTextField.AbstractFormatter {

		private static final long serialVersionUID = -3082798484771841528L;

		@Override
		public Object stringToValue(String text) throws ParseException {
			// not needed
			return null;
		}

		@Override
		public String valueToString(Object obj) throws ParseException {
			Integer value = (Integer) obj;
			if (value == null || value.intValue() < 0) {
				return noValueDisplayString;
			}
			return MessageFormat.format(valueDisplayString, value);
		}

	}

	private class EditFormatter extends JFormattedTextField.AbstractFormatter {

		private static final long serialVersionUID = 4814824765566252119L;
		private DocumentFilter filter = new Filter();

		@Override
		public Object stringToValue(String text) throws ParseException {
			return (StringUtils.isEmpty(text)) ? Integer.valueOf(0) : Integer.parseInt(text);
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			return (value == null) ? "-1" : value.toString();
		}

		@Override
		protected DocumentFilter getDocumentFilter() {
			return this.filter;
		}

		private class Filter extends DocumentFilter {

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				StringBuilder builder = new StringBuilder();
				Document doc = fb.getDocument();
				builder.append(doc.getText(0, doc.getLength()));
				builder.replace(offset, offset + length, text);
				String content = builder.toString();
				if (StringUtils.isNumeric(content)) {
					int i = Integer.parseInt(content);
					if (i >= 0 && i <= 119) {
						super.replace(fb, offset, length, text, attrs);
					}
				}
			}
		}
	}
}
