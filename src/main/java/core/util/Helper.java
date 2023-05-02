package core.util;

import core.constants.player.PlayerAbility;
import core.datatype.CBItem;
import core.datatype.ComboItem;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;
import javax.swing.*;

/**
 * Helper class
 * Methods are used in several dialogs or panels
 */
public class Helper {

	/**
	 * Form selections
	 */
	public static final CBItem[] EINSTUFUNG_FORM = {
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.NON_EXISTENT), PlayerAbility.NON_EXISTENT),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.DISASTROUS), PlayerAbility.DISASTROUS),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.WRETCHED), PlayerAbility.WRETCHED),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.POOR), PlayerAbility.POOR),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.WEAK), PlayerAbility.WEAK),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.INADEQUATE), PlayerAbility.INADEQUATE),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.PASSABLE), PlayerAbility.PASSABLE),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.SOLID), PlayerAbility.SOLID),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.EXCELLENT), PlayerAbility.EXCELLENT)};

	/**
	 * Stamina selections
	 */
	public static final CBItem[] EINSTUFUNG_KONDITION = {
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.NON_EXISTENT), PlayerAbility.NON_EXISTENT),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.DISASTROUS), PlayerAbility.DISASTROUS),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.WRETCHED), PlayerAbility.WRETCHED),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.POOR), PlayerAbility.POOR),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.WEAK), PlayerAbility.WEAK),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.INADEQUATE), PlayerAbility.INADEQUATE),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.PASSABLE), PlayerAbility.PASSABLE),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.SOLID), PlayerAbility.SOLID),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.EXCELLENT), PlayerAbility.EXCELLENT),
			new CBItem(PlayerAbility.getNameForSkill(PlayerAbility.FORMIDABLE), PlayerAbility.FORMIDABLE)};

	/**
	 * Lineup position selections
	 */
	public static final CBItem[] SPIELERPOSITIONEN = {
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.KEEPER), IMatchRoleID.KEEPER),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.CENTRAL_DEFENDER), IMatchRoleID.CENTRAL_DEFENDER),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.CENTRAL_DEFENDER_OFF), IMatchRoleID.CENTRAL_DEFENDER_OFF),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.CENTRAL_DEFENDER_TOWING), IMatchRoleID.CENTRAL_DEFENDER_TOWING),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.BACK), IMatchRoleID.BACK),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.BACK_OFF), IMatchRoleID.BACK_OFF),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.BACK_DEF), IMatchRoleID.BACK_DEF),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.BACK_TOMID), IMatchRoleID.BACK_TOMID),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.MIDFIELDER), IMatchRoleID.MIDFIELDER),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.MIDFIELDER_OFF), IMatchRoleID.MIDFIELDER_OFF),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.MIDFIELDER_DEF), IMatchRoleID.MIDFIELDER_DEF),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.MIDFIELDER_TOWING), IMatchRoleID.MIDFIELDER_TOWING),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.WINGER), IMatchRoleID.WINGER),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.WINGER_OFF), IMatchRoleID.WINGER_OFF),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.WINGER_DEF), IMatchRoleID.WINGER_DEF),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.WINGER_TOMID), IMatchRoleID.WINGER_TOMID),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.FORWARD), IMatchRoleID.FORWARD),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.FORWARD_DEF), IMatchRoleID.FORWARD_DEF),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.FORWARD_DEF_TECH), IMatchRoleID.FORWARD_DEF_TECH),
			new CBItem(MatchRoleID.getNameForPosition(IMatchRoleID.FORWARD_TOWING), IMatchRoleID.FORWARD_TOWING)};


	/**
	 * Currency formatter
	 * Matches country of user's premier team
	 */
	public static NumberFormat CURRENCYFORMAT = CurrencyUtils.getLeagueCurrencyFormater(HOVerwaltung.instance().getModel().getLeagueIdPremierTeam());

	/**
	 * Integer format
	 * used by parser in parseFloat
	 */
	public static DecimalFormat INTEGERFORMAT = new DecimalFormat("#0");

	/**
	 * Decimal format
	 * - 1 fraction digit
	 */
	public static DecimalFormat DEFAULTDEZIMALFORMAT = new DecimalFormat("#0.0");

	/**
	 * Decimal format
	 * - 2 fraction digits
	 */
	public static DecimalFormat DEZIMALFORMAT_2STELLEN = new DecimalFormat("#0.00");

	/**
	 * Prevent recursive displaying of message pane
	 */
	public static boolean paneShown;

	/**
	 * Calculate cell width
	 */
	public static int calcCellWidth(int width) {
		return (int) (((float) width) * UserParameter.instance().fontSize / 12.0);
	}

	/**
	 * Check contents of text fields
	 * Returns integer array if text is a comma separated integer list
	 * 	null, if not
	 */
	public static int[] generateIntArray(String text) {
		// String message = "";
		final int[] tempzahlen = new int[100];

		try {
			int index = 0;
			var buffer = new StringBuilder();

			for (int i = 0; i < text.length(); i++) {
				if (text.charAt(i) != ',') {
					buffer.append(text.charAt(i));
				} else { // Komma gefunden
					// buffer ist nicht leer
					if (!buffer.toString().trim().equals("")) {
						tempzahlen[index] = Integer.parseInt(buffer.toString().trim());

						/*
						 * if ( !negativErlaubt && tempzahlen[index] < 0 ) {
						 * //message = "Keinen negativen Werte erlaubt!"; throw
						 * new NumberFormatException(); } //Groesser als
						 * Maximalwert if ( tempzahlen[index] > maxValue ) {
						 * //message = "Ein Wert ist zu hoch!"; throw new
						 * NumberFormatException(); }
						 */
						index++;
					}

					buffer = new StringBuilder();
				}
			}

			if (!buffer.toString().trim().equals("")) {
				// Es folgt am Ende kein , mehr ->
				tempzahlen[index] = Integer.parseInt(buffer.toString().trim());

				/*
				 * if ( !negativErlaubt && tempzahlen[index] < 0 ) { //message =
				 * "Keinen negativen Werte erlaubt!"; throw new
				 * NumberFormatException(); } //Groesser als Maximalwert if (
				 * tempzahlen[index] > maxValue ) { //message =
				 * "Ein Wert ist zu hoch!"; throw new NumberFormatException(); }
				 */
				index++;
			}

			// Zahlen in passenden Array kopieren
			final int[] zahlen = new int[index];

			System.arraycopy(tempzahlen, 0, zahlen, 0, index);

			return zahlen;
		} catch (NumberFormatException nfe) {
			/*
			 * if (message.equals("") ) { message =
			 * "Eine Eingabe ist keine Zahl!"; } showMessage( parent, message,
			 * "Fehler", javax.swing.JOptionPane.ERROR_MESSAGE);
			 */
			return null;
		}
	}

	/**
	 * Select combo box entry given by id
	 * @param combobox JComboBox
	 * @param id is searched in combo box model
	 */
	public static void setComboBoxFromID(JComboBox<? extends ComboItem> combobox, int id) {
		final javax.swing.ComboBoxModel<? extends ComboItem> model = combobox.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			var modelEntry = model.getElementAt(i);
			if (modelEntry.getId() == id) {
				if (modelEntry != combobox.getSelectedItem()) {
					combobox.setSelectedItem(modelEntry);
				}
				break;
			}
		}
	}

	/**
	 * Check the contents of a text field
	 * Return true, if text field contains an integer value
	 * 		false, if text field cannot be parsed as intger, field is set to 0
	 */
	public static boolean parseInt(Window parent, JTextField field, boolean negativErlaubt) {
		String message = "";

		try {
			final int temp = Integer.parseInt(field.getText());

			if (!negativErlaubt && (temp < 0)) {
				message = HOVerwaltung.instance().getLanguageString("negativVerboten");
				throw new NumberFormatException();
			}

			field.setText(String.valueOf(temp));
			return true;
		} catch (NumberFormatException nfe) {
			if (message.equals("")) {
				message = HOVerwaltung.instance().getLanguageString("keineZahl");
			}

			showMessage(parent, message,
					HOVerwaltung.instance().getLanguageString("Fehler"), JOptionPane.ERROR_MESSAGE);

			field.setText(String.valueOf(0));
			return false;
		}
	}


	/**
	 * Round to one fraction digit
	 */
	public static float round(float wert) {
		return Helper.round(wert, 1);
	}


	/**
	 * Round a double value
	 */
	public static double round(double value, int nbDecimals) {
		double corr = Math.pow(10.0, nbDecimals);
		return Math.round(value * corr) / corr;
	}

	/**
	 * Round a float value
	 */
	public static float round(float value, int nbDecimals) {
		double corr = Math.pow(10.0, nbDecimals);
		return (float) (Math.round(value * corr) / corr);
	}

	/**
	 * Display a message dialog
	 * Recursions are prevented
	 */
	public static void showMessage(Component parent, String message, String titel, int typ) {
		//new gui.ShowMessageThread( parent, message, titel, typ );
		//Ignorieren, wenn schon ein Fehler angezeigt wird.
		if (!paneShown) {
			paneShown = true;
			javax.swing.JOptionPane.showMessageDialog(parent, message, titel, typ);
			paneShown = false;
		}
	}

	/**
	 * Sort a two dimensional in array
	 * @param toSort array
	 * @param spaltenindex column index
	 * @return array
	 */
	public static int[][] sortintArray(int[][] toSort, int spaltenindex) {
		try {
			if ((toSort == null) || (toSort.length == 0) || (toSort[0].length == 0)) {
				return null;
			}

			final int[][] ergebnis = new int[toSort.length][toSort[0].length];
			final int[] sortSpalte = new int[toSort.length];

			// find sort column
			for (int i = 0; i < toSort.length; i++) {
				sortSpalte[i] = toSort[i][spaltenindex];
			}

			java.util.Arrays.sort(sortSpalte);

			// scan all entries, search value in toSort and copy the value to the result
			for (int i = 0; i < toSort.length; i++) {
				for (int[] ints : toSort) {
					if (sortSpalte[i] == ints[spaltenindex]) {
						System.arraycopy(ints, 0, ergebnis[i], 0, ints.length);
						break;
					}
				}
			}

			return ergebnis;
		} catch (Exception e) {
			HOLogger.instance().log(Helper.class, "Helper.sortintArray:  " + e);
			return null;
		}
	}

	/**
	 * Returns a NumberFormat based on the parameters
	 */
	public static NumberFormat getNumberFormat(boolean currencyformat, int nbDecimals) {
		NumberFormat numFormat;
		if (currencyformat) {
			numFormat = Helper.CURRENCYFORMAT;
		} else {
			numFormat = NumberFormat.getNumberInstance();
		}
		numFormat.setMinimumFractionDigits(nbDecimals);
		numFormat.setMaximumFractionDigits(nbDecimals);
		return numFormat;
	}

	/**
	 * Format value as currency string
	 * @param v value
	 * @return String
	 */
	public static String formatCurrency(float v) {
		return Helper.getNumberFormat(true, 0).format(v);
	}

	/**
	 * Parse currency value from string.
	 * If value could not be parsed with currency formal an number format is tried.
	 * @param v String to parse from
	 * @return Integer, null on parse error
	 */
	public static Integer parseCurrency(String v) {
		try {
			return Helper.getNumberFormat(true, 0).parse(v).intValue();
		} catch (Exception ignored) {
			try {
				return Helper.getNumberFormat(false, 0).parse(v).intValue();
			}
			catch ( Exception ex) {
				HOLogger.instance().error(Helper.class, "error parsing currency " + ex);
				return null;
			}
		}
	}


	/**
	 * Decrypt string
	 * encrypted by method crypt
	 */
	public static String decryptString(String text) {
		byte[] encoded;

		if (text == null) {
			return "";
		}

		encoded = text.getBytes();

		for (int i = 0; (i < encoded.length); ++i) {
			//check ob Zeichen gleich ~ = 126 ?
			if (encoded[i] == 126) {
				//Dann mit tilde ersetzen slash = 92
				encoded[i] = 92;
			}

			encoded[i] += 7;

			if ((encoded[i] % 2) == 0) {
				++encoded[i];
			} else {
				--encoded[i];
			}
		}

		return new String(encoded);
	}

	/**
	 * Encrypt a string consisting on numbers and characters only
	 */
	public static String cryptString(String text) {
		byte[] encoded;

		if (text == null) {
			return "";
		}

		for (int j = 0; j < text.length(); j++) {
			if (!Character.isLetterOrDigit(text.charAt(j))) {
				return null;
			}
		}

		encoded = text.getBytes();

		for (int i = 0; (i < encoded.length); ++i) {
			if ((encoded[i] % 2) == 0) {
				++encoded[i];
			} else {
				--encoded[i];
			}

			encoded[i] -= 7;

			//check for slash character = 92 ?
			if (encoded[i] == 92) {
				// replace it by  ~ = 126
				encoded[i] = 126;
			}
		}

		return new String(encoded);
	}

	/**
	 * Copy vector to array
	 * @param src  vector
	 * @param dest result array, has to be constructed by caller with correct size (Vector.size()
	 */
	public static <T> void copyVector2Array(Vector<T> src, T[] dest) {
		for (int i = 0;
			 (src != null) && (dest != null) && (dest.length >= src.size()) && (i < src.size());
			 i++) {
			dest[i] = src.elementAt(i);
		}
	}

	/**
	 * Copy array to vector
	 * @param src  array
	 * @param dest vector
	 */
	public static <T> void copyArray2Vector(T[] src, Vector<T> dest) {
		for (int i = 0; (src != null) && (dest != null) && (i < src.length); i++) {
			dest.addElement(src[i]);
		}
	}

	/**
	 * Find maximum value of an array
	 * (works only for values >=0)
	 * @param werte double[]
	 * @return maximum value
	 */
	public static double getMaxValue(double[] werte) {
		double max = 0;
		for (int i = 0; (werte != null) && (i < werte.length); i++) {
			if (werte[i] > max) {
				max = werte[i];
			}
		}
		return (max);
	}

	/**
	 * Find translation string of key
	 * @param key String (see poeditor)
	 * @return String, translation
	 */
	public static String getTranslation(String key) {
		return core.model.HOVerwaltung.instance().getLanguageString(key);
	}

	/**
	 * Find translation replacing placeholders with values in argument list
	 * @param key String (see poeditor)
	 * @param messageArguments place holder values
	 * @return String translation
	 */
	public static String getTranslation(String key, Object... messageArguments) {
		return core.model.HOVerwaltung.instance().getLanguageString(key, messageArguments);
	}


	public static Font getLabelFontAsBold(JLabel label) {
		Font f = label.getFont();
		return getLabelFontAsBold(f);
	}

	public static Font getLabelFontAsBold(Font f) {
		return f.deriveFont(f.getStyle() | Font.BOLD);
	}
}
