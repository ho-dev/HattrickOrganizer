package core.model;

import core.datatype.CBItem;
import core.db.DBManager;
import core.file.ExampleFileFilter;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.net.login.LoginWaitDialog;
import core.training.TrainingManager;
import core.util.HOLogger;
import core.util.UTF8Control;
import module.lineup.Lineup;
import core.HO;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * @author tom
 */
public class HOVerwaltung {
	// ~ Static fields/initializers
	// -----------------------------------------------------------------

	/** singelton */
	protected static HOVerwaltung m_clInstance;

	// ~ Instance fields
	// ----------------------------------------------------------------------------

	/** das Model */
	protected HOModel m_clHoModel;

	/** Resource */
	protected ResourceBundle languageBundle;

	public int getId() {
		return id;
	}

	private int id;

	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Creates a new HOVerwaltung object.
	 */
	private HOVerwaltung() {
	}

	// -----------------Hilfsmethoden---------------------------------------------

	/**
	 * Set the HOModel.
	 */
	public void setModel(HOModel model) {
		m_clHoModel = model;
	}

	public HOModel getModel() {
		return m_clHoModel;
	}

	/**
	 * Get the HOVerwaltung singleton instance.
	 */
	public static HOVerwaltung instance() {
		if (m_clInstance == null) {
			m_clInstance = new HOVerwaltung();

			DBManager.instance().getFaktorenFromDB();

			// Krücke bisher
			// berechnung.FormulaFactors.instance ().init ();
		}

		return m_clInstance;
	}

	public void setResource(String pfad) {
		try {
			 languageBundle = ResourceBundle.getBundle("sprache." + pfad, new UTF8Control());
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}

	}

	public ResourceBundle getResource() {
		return languageBundle;
	}

	/**
	 * ersetzt das aktuelle model durch das aus der DB mit der angegebenen ID
	 */
	public void loadHoModel(int id) {
		m_clHoModel = loadModel(id);
	}

	/**
	 * läadt das zuletzt importtiert model ein
	 */
	public void loadLatestHoModel() {
		int id = DBManager.instance().getLatestHrfId();
		this.id = id;
		m_clHoModel = loadModel(id);
	}

	/**
	 * Recalculate subskills since a certain HRF date. If the HRF date is null,
	 * the whole training history is recalculated.
	 */
	public void recalcSubskills(boolean showWait, Timestamp hrfDate) {
		HOLogger.instance().log(getClass(), "Start full subskill calculation. " + new Date());
		long start = System.currentTimeMillis();
		if (hrfDate == null) {
			hrfDate = new Timestamp(0);
		}

		LoginWaitDialog waitDialog = null;

		if (showWait) {
			waitDialog = new LoginWaitDialog(HOMainFrame.instance(), false);
			waitDialog.setVisible(true);
		}

		// Make sure the training week list is up to date.
		TrainingManager.instance().refreshTrainingWeeks();
		
		final Vector<CBItem> hrfListe = new Vector<CBItem>();
		hrfListe.addAll(DBManager.instance().getCBItemHRFListe(hrfDate));
		Collections.reverse(hrfListe);
		long s1, s2, lSum = 0, mSum = 0;
		HOLogger.instance().log(getClass(), "Subskill calculation prepared. " + new Date());
		for (int i = 0; i < hrfListe.size(); i++) {
			try {
				if (showWait && waitDialog != null) {
					waitDialog.setValue((int) ((i * 100d) / hrfListe.size()));
				}
				s1 = System.currentTimeMillis();
				final HOModel model = this.loadModel((hrfListe.get(i)).getId());
				lSum += (System.currentTimeMillis() - s1);
				s2 = System.currentTimeMillis();
				model.calcSubskills();
				mSum += (System.currentTimeMillis() - s2);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "recalcSubskills : ");
				HOLogger.instance().log(getClass(), e);
			}
		}

		if (showWait && waitDialog != null) {
			waitDialog.setVisible(false);
		}

		// Erneut laden, da sich die Subskills geändert haben
		loadLatestHoModel();

		RefreshManager.instance().doReInit();
		HOLogger.instance().log(
				getClass(),
				"Subskill calculation done. " + new Date() + " - took "
						+ (System.currentTimeMillis() - start) + "ms ("
						+ (System.currentTimeMillis() - start) / 1000L + " sec), lSum=" + lSum
						+ ", mSum=" + mSum);
	}

	/**
	 * interne Func die ein Model aus der DB lädt
	 */
	protected HOModel loadModel(int id) {
		final HOModel model = new HOModel();
		model.setSpieler(DBManager.instance().getSpieler(id));
		model.setAllOldSpieler(DBManager.instance().getAllSpieler());
		model.setAufstellung(DBManager.instance().getAufstellung(id, Lineup.DEFAULT_NAME));
		model.setLastAufstellung(DBManager.instance().getAufstellung(id, Lineup.DEFAULT_NAMELAST));
		model.setBasics(DBManager.instance().getBasics(id));
		model.setFinanzen(DBManager.instance().getFinanzen(id));
		model.setLiga(DBManager.instance().getLiga(id));
		model.setStadium(DBManager.instance().getStadion(id));
		model.setTeam(DBManager.instance().getTeam(id));
		model.setVerein(DBManager.instance().getVerein(id));
		model.setID(id);
		model.setSpielplan(DBManager.instance().getSpielplan(-1, -1));
		model.setXtraDaten(DBManager.instance().getXtraDaten(id));
		model.setStaff(DBManager.instance().getStaffByHrfId(id));
		
		return model;
	}


	/**
	 * Returns the String connected to the active language file or connected to
	 * the english language file. Returns !key! if the key can not be found.
	 * 
	 * @param key
	 *            Key to be searched in language files
	 * 
	 * @return String connected to the key or !key! if nothing can be found in
	 *         language files
	 */
	public String getLanguageString(String key) {
		String temp = null;
		try {
			temp = languageBundle.getString(key);
		} catch (Exception e) {
			// Do nothing, it just throws error if key is missing. 
		}
			if (temp != null)
			return temp;
		// Search in english.properties if nothing found and active language not
		// english
		if (!core.model.UserParameter.instance().sprachDatei.equalsIgnoreCase("english")) {
			
			ResourceBundle tempBundle = ResourceBundle.getBundle("sprache.English", new UTF8Control());

			try {
				temp = tempBundle.getString(key);
			} catch (Exception e) {
				// Ignore
			}
			
			if (temp != null)
				return temp;
		}

		HOLogger.instance().warning(getClass(), "getLanguageString: '" + key + "' not found!");
		return "!" + key + "!";
	}

	/**
	 * Gets a parameterized message for the current language.
	 * 
	 * @param key
	 *            the key for the message in the language file.
	 * @param values
	 *            the values for the message
	 * @return the message for the specified key where the placeholders are
	 *         replaced by the given value(s).
	 */
	public String getLanguageString(String key, Object... values) {
		String str = getLanguageString(key);
		return MessageFormat.format(str, values);
	}

	public static String[] getLanguageFileNames() {
		String[] files = null;
		final Vector<String> sprachdateien = new Vector<String>();

		try {
			// java.net.URL resource = new
			// gui.vorlagen.ImagePanel().getClass().getClassLoader().getResource(
			// "sprache" );

//            java.net.URL url = HOVerwaltung.class.getClassLoader().getResource("sprache");
//            java.net.JarURLConnection connection = (java.net.JarURLConnection) url.openConnection();
//            String filepath = (String)connection.getJarFileURL().toURI();

            java.io.InputStream is = HOVerwaltung.class.getClassLoader().getResourceAsStream("sprache/ListLanguages.txt");
            java.util.Scanner s = new java.util.Scanner(is);
            java.util.ArrayList<String> llist = new java.util.ArrayList<String>();
            while (s.hasNext()){
                llist.add(s.next());
            }
            s.close();

            files = llist.toArray(new String[llist.size()]);

		} catch (Exception e) {
			HOLogger.instance().log(HOVerwaltung.class, e);
		}

		return files;
	}

	/**
	 * Checked die Sprachdatei oder Fragt nach einer passenden
	 */
	public static void checkLanguageFile(String dateiname) {
		try {
            final java.io.InputStream sprachdatei = HOVerwaltung.class.getClassLoader().getResourceAsStream("sprache/" + dateiname
					+ ".properties");

			if (sprachdatei != null) {
				double sprachfileversion = 0;
				ResourceBundle temp = ResourceBundle.getBundle("sprache." + dateiname, new UTF8Control());

				try {
					sprachfileversion = Double.parseDouble(temp.getString("Version"));
				} catch (Exception e) {
					HOLogger.instance().log(HOMainFrame.class, "not use " + dateiname);
				}

//				if (sprachfileversion >= HO.SPRACHVERSION) {
//					HOLogger.instance().log(HOMainFrame.class, "use " + dateiname);
//
//					// ok!!
//					return;
//				}

				HOLogger.instance().log(HOMainFrame.class, "use " + dateiname);
				// ok!!
				return;

				//HOLogger.instance().log(HOMainFrame.class, "not use " + dateiname);

			}
		} catch (Exception e) {
			HOLogger.instance().log(HOMainFrame.class, "not use " + e);
		}

		// Irgendein Fehler -> neue Datei aussuchen!
		// new gui.menue.optionen.InitOptionsDialog();
		UserParameter.instance().sprachDatei = "English";
	}
}
