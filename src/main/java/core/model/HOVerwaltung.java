package core.model;

import core.datatype.CBItem;
import core.db.DBManager;
import core.file.FileLoader;
import core.file.hrf.HRF;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.training.TrainingManager;
import core.util.HOLogger;
import core.util.UTF8Control;
import module.lineup.Lineup;

import java.io.InputStream;
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

	public int getPreviousID() {
		return m_clHoModel.getPreviousID();
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

		}

		return m_clInstance;
	}

	public void setResource(String pfad) {
		try {
                    languageBundle = ResourceBundle.getBundle("sprache." + pfad, new UTF8Control());
                } catch (UnsupportedOperationException e) {
                    // ResourceBundle.Control not supported in named modules in JDK9+
                    languageBundle = ResourceBundle.getBundle("sprache." + pfad);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}

	}

	public ResourceBundle getResource() {
		return languageBundle;
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

		// Make sure the training week list is up to date.
		//TrainingManager.instance().refreshTrainingWeeks();

		var hrfListe = DBManager.instance().getHRFsSince(hrfDate);

		int i=0;
		long s1, s2, lSum = 0, mSum = 0;
		HOLogger.instance().log(getClass(), "Subskill calculation prepared. " + new Date());
		HRF previousHRF=null;
		for (var hrf: hrfListe) {
			try {
				if (showWait ) {
					HOMainFrame.instance().setWaitInformation((int) ((i++ * 100d) / hrfListe.size()));
				}
				s1 = System.currentTimeMillis();
				//final HOModel model = this.loadModel((hrfListe.get(i)).getId());

				HOModel model = new HOModel(hrf, previousHRF);
				lSum += (System.currentTimeMillis() - s1);
				s2 = System.currentTimeMillis();
				model.calcSubskills();
				previousHRF=hrf;
				mSum += (System.currentTimeMillis() - s2);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "recalcSubskills : ");
				HOLogger.instance().log(getClass(), e);
			}
		}

		if (showWait ) {
			HOMainFrame.instance().resetInformation();
		}

		// Reload, because the subskills have changed
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
	 * internal method to load model from database
	 * (lineup ratings are only correct if id is the latest one)
	 */
	protected HOModel loadModel(int id) {
		return new HOModel(id);
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
		final Vector<String> sprachdateien = new Vector<>();

		try {
			// java.net.URL resource = new
			// gui.vorlagen.ImagePanel().getClass().getClassLoader().getResource(
			// "sprache" );

//            java.net.URL url = HOVerwaltung.class.getClassLoader().getResource("sprache");
//            java.net.JarURLConnection connection = (java.net.JarURLConnection) url.openConnection();
//            String filepath = (String)connection.getJarFileURL().toURI();

            java.io.InputStream is = HOVerwaltung.class.getClassLoader().getResourceAsStream("sprache/ListLanguages.txt");
            java.util.Scanner s = new java.util.Scanner(is);
            java.util.ArrayList<String> llist = new java.util.ArrayList<>();
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
	public static void checkLanguageFile(String languageFilename) {
		try {
			final InputStream translationFile = FileLoader.instance().getFileInputStream("sprache/" + languageFilename + ".properties");


			if (translationFile != null) {
				HOLogger.instance().info(HOVerwaltung.class, "language used for interface is: " + languageFilename);
				return;
			}
			else{
				HOLogger.instance().error(HOVerwaltung.class, "language set for interface (" + languageFilename +") can't be loaded ... reverting to English !");
				HOLogger.instance().log(HOVerwaltung.class, "language set for interface (" + languageFilename +") can't be loaded ... reverting to English !");
			}
		}
		catch (Exception e) {
			HOLogger.instance().error(HOVerwaltung.class, "language set for interface (" + languageFilename +") can't be loaded ... reverting to English !" + "   " + e);
			HOLogger.instance().log(HOVerwaltung.class, "language set for interface (" + languageFilename +") can't be loaded ... reverting to English !" + "   " + e);
			UserParameter.instance().sprachDatei = "English";
		}


	}
}
