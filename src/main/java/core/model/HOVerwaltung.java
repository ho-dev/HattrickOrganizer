package core.model;


import core.db.DBManager;
import core.file.FileLoader;
import core.file.hrf.HRF;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.util.HOLogger;
import core.util.Languages;
import core.util.UTF8Control;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.stream.Collectors;


public class HOVerwaltung {


	/** singelton */
	protected static HOVerwaltung m_clInstance;

	/** das Model */
	protected HOModel m_clHoModel;

	/** Resource */
	protected ResourceBundle languageBundle;
	protected Locale m_locale;

    public static boolean isNewModel(HOModel homodel) {
		return (homodel != null && ((instance().getModel() == null) ||
				(homodel.getBasics().getDatum().isAfter(instance().getModel().getBasics().getDatum()))));
	}

    public int getId() {
		return id;
	}

	public int getPreviousID() {
		return m_clHoModel.getPreviousID();
	}

	private int id;

	private PropertyChangeSupport support;

	/**
	 * Creates a new HOVerwaltung object.
	 */
	private HOVerwaltung() {
		support = new PropertyChangeSupport(this);
	}

	// -----------------Hilfsmethoden---------------------------------------------

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}


	/**
	 * Set the HOModel.
	 */
	public void setModel(HOModel model) {
		HOModel oldModel = m_clHoModel;
		m_clHoModel = model;
		support.firePropertyChange("m_clHoModel", oldModel, m_clHoModel);
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
			  m_locale = Languages.lookup(UserParameter.instance().sprachDatei).getLocale();
		}
		catch (UnsupportedOperationException e) {
			// ResourceBundle.Control not supported in named modules in JDK9+
			languageBundle = ResourceBundle.getBundle("sprache." + pfad);
			m_locale = Languages.lookup(UserParameter.instance().sprachDatei).getLocale();
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}

	}

	public ResourceBundle getResource() {
		return languageBundle;
	}

	public Locale getM_locale() {return m_locale;}

	/**
	 * läadt das zuletzt importtiert model ein
	 */
	public void loadLatestHoModel() {
		int id = DBManager.instance().getLatestHrfId();
		this.id = id;
		setModel(loadModel(id));
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

		int progress=0;
		long s1, s2, lSum = 0, mSum = 0;

		HRF previousHRF=hrfListe.get(0);

		int nbSteps =  hrfListe.size() + 1;

		for (var hrf: hrfListe.stream().skip(1).collect(Collectors.toList())) {
			try {
				if (showWait ) {
					HOMainFrame.instance().setWaitInformation(progress * 100 / nbSteps);
				}

				s1 = System.currentTimeMillis();

				HOModel model = new HOModel(hrf, previousHRF);
				var trainingDateOfPreviousHRF = DBManager.instance().getXtraDaten(previousHRF.getHrfId()).getNextTrainingDate();
				var trainingDateHRF = DBManager.instance().getXtraDaten(hrf.getHrfId()).getNextTrainingDate();

				lSum += (System.currentTimeMillis() - s1);
				s2 = System.currentTimeMillis();
				model.calcSubskills(trainingDateOfPreviousHRF, trainingDateHRF);
				previousHRF=hrf;
				mSum += (System.currentTimeMillis() - s2);
				progress++;

			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "recalcSubskills : ");
				HOLogger.instance().log(getClass(), e);
			}
		}

		if (showWait ) {
			progress ++;
			HOMainFrame.instance().setWaitInformation(progress * 100 / nbSteps);
		}

		// Reload, because the subskills have changed
		loadLatestHoModel();

		if (showWait ) {
			progress ++;
			HOMainFrame.instance().setWaitInformation(progress * 100 / nbSteps);
		}

		RefreshManager.instance().doReInit();
		HOLogger.instance().log(
				getClass(),
				"Subskill calculation done. " + new Date() + " - took "
						+ (System.currentTimeMillis() - start) + "ms ("
						+ (System.currentTimeMillis() - start) / 1000L + " sec), lSum=" + lSum
						+ ", mSum=" + mSum);


		if (showWait ) {
			HOMainFrame.instance().resetInformation();
		}

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

		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(m_locale);
		formatter.applyPattern(str);

		return formatter.format(values);
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

            files = llist.toArray(new String[0]);

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
