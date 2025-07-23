package core.model;

import core.db.DBManager;
import core.file.hrf.HRF;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.util.HODateTime;
import core.util.HOLogger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class HOVerwaltung {


	/** singleton */
	protected static HOVerwaltung m_clInstance;

	/** das Model */
	protected HOModel m_clHoModel;

    public static boolean isNewModel(HOModel homodel) {
		return (homodel != null && ((instance().getModel() == null) ||
				(homodel.getBasics().getDatum().isAfter(instance().getModel().getBasics().getDatum()))));
	}

    public int getId() {
		return id;
	}

	private int id = -1;

	private final PropertyChangeSupport support;

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
		if ( oldModel != null && model != null) {
			for (var player : model.getCurrentPlayers()) {
				if (oldModel.getCurrentPlayers().stream().noneMatch(i -> i.getPlayerId() == player.getPlayerId())) {
					// Check if new player was youth player in old model
					// Todo: Check if player id can be used instead of name filtering
					var youthPlayer = oldModel.getCurrentYouthPlayers().stream().filter(i -> i.getFullName().equals(player.getFullName())).findFirst();
					if (youthPlayer.isPresent()) {
						for (var skill : youthPlayer.get().getCurrentSkills().values()) {
							var currentLevel = player.getValue4Skill(skill.getSkillID());
							if (currentLevel == (int) skill.getCurrentValue()) {
								// Estimate in youth academy matches the current skill level
								player.setSubskill4PlayerSkill(skill.getSkillID(), skill.getCurrentValue() - currentLevel);
							}
						}
					}
				}
			}
		}
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

			// TODO This seems to have side effects other than loading
			//   parameters from DB, this probably should be wrapped properly.
			DBManager.instance().getFaktorenFromDB();
		}
		return m_clInstance;
	}

	/**
	 * @deprecated Provided for compatibility. Please use {@link #setTranslator(String)} instead.
	 */
	@Deprecated(since = "9.0", forRemoval = true)
	public void setResource(String pfad) {
		setTranslator(pfad);
	}

	/**
	 * @deprecated Provided for compatibility. Please use {@link TranslationFacility#setLanguage(String)} instead.
	 */
	@Deprecated(since = "9.0", forRemoval = true)
	public void setTranslator(String language) {
		try {
			TranslationFacility.setTranslator(Translator.load(language));
		} catch (RuntimeException e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	/**
	 * @deprecated Provided for compatibility. Will be removed without substitution!
	 */
	@Deprecated(since = "9.0", forRemoval = true)
	public ResourceBundle getResource() {
		return Optional.ofNullable(TranslationFacility.getTranslator()).map(Translator::getResourceBundle).orElse(null);
	}

	/**
	 * läadt das zuletzt importtiert model ein
	 */
	public void loadLatestHoModel() {
		var latestHRF = DBManager.instance().getLatestHRF();
		if ( latestHRF != null && latestHRF.getHrfId() > -1){
			this.id = latestHRF.getHrfId();
		}
		setModel(loadModel(this.id));
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
		HOMainFrame.instance().resetInformation();

		var hrfListe = DBManager.instance().getHRFsSince(hrfDate);
		long s1, s2, lSum = 0, mSum = 0;
		HRF previousHRF = null;
		for (var hrf : hrfListe) {
			try {
				if (showWait) {
					HOMainFrame.instance().setWaitInformation();
				}
				s1 = System.currentTimeMillis();
				HOModel model = new HOModel(hrf, previousHRF);
				HODateTime trainingDateOfPreviousHRF;
				var previousHRFId = model.getPreviousID();
				if (previousHRFId != -1) {
					trainingDateOfPreviousHRF = DBManager.instance().getXtraDaten(previousHRFId).getNextTrainingDate();
				} else {
					trainingDateOfPreviousHRF = HOVerwaltung.instance().getModel().getBasics().getActivationDate();
				}
				var trainingDateHRF = DBManager.instance().getXtraDaten(hrf.getHrfId()).getNextTrainingDate();

				lSum += (System.currentTimeMillis() - s1);
				s2 = System.currentTimeMillis();
				model.calcSubskills(trainingDateOfPreviousHRF.minus(1, ChronoUnit.HOURS), trainingDateHRF.minus(1, ChronoUnit.HOURS));
				previousHRF = hrf;
				mSum += (System.currentTimeMillis() - s2);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(), "recalcSubskills : "+ e);
			}
		}

		if (showWait) {
			HOMainFrame.instance().setWaitInformation();
		}

		// Reload, because the subskills have changed
		loadLatestHoModel();

		if (showWait) {
			HOMainFrame.instance().setWaitInformation();
		}

		RefreshManager.instance().doReInit();
		HOLogger.instance().log(
				getClass(),
				"Subskill calculation done. " + new Date() + " - took "
						+ (System.currentTimeMillis() - start) + "ms ("
						+ (System.currentTimeMillis() - start) / 1000L + " sec), lSum=" + lSum
						+ ", mSum=" + mSum);

		if (showWait) {
			HOMainFrame.instance().setInformationCompleted();
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
	 * @deprecated Provided for compatibility. Please use {@link TranslationFacility#tr(String)} instead.
	 *
	 * @param key
	 *            Key to be searched in language files
	 * 
	 * @return String connected to the key or !key! if nothing can be found in
	 *         language files
	 */
	@Deprecated(since = "9.0", forRemoval = true)
	public String getLanguageString(String key) {
		return TranslationFacility.tr(key);
	}

	/**
	 * Gets a parameterized message for the current language.
	 *
	 * @deprecated Provided for compatibility. Please use {@link TranslationFacility#tr(String, Object...)} instead.
	 *
	 * @param key
	 *            the key for the message in the language file.
	 * @param values
	 *            the values for the message
	 * @return the message for the specified key where the placeholders are
	 *         replaced by the given value(s).
	 */
	@Deprecated(since = "9.0", forRemoval = true)
	public String getLanguageString(String key, Object... values) {
		return TranslationFacility.tr(key, values);
	}

	/**
	 * Checked die Sprachdatei oder Fragt nach einer passenden
	 */
	public static void checkLanguageFile(String languageFilename) {
		try {
			if (Translator.isAvailable(languageFilename)) {
				HOLogger.instance().info(HOVerwaltung.class, "language used for interface is: " + languageFilename);
			}
			else{
				HOLogger.instance().error(HOVerwaltung.class, "language set for interface (" + languageFilename + ") can't be loaded ... reverting to " + Translator.LANGUAGE_DEFAULT + " !");
				UserParameter.instance().sprachDatei = Translator.LANGUAGE_DEFAULT;
			}
		}
		catch (Exception e) {
			HOLogger.instance().error(HOVerwaltung.class, "language set for interface (" + languageFilename + ") can't be loaded ... reverting to " + Translator.LANGUAGE_DEFAULT + " !" + "   " + e);
			UserParameter.instance().sprachDatei = Translator.LANGUAGE_DEFAULT;
		}
	}

	/**
	 * @deprecated Provided for compatibility. Will be removed without substitution!
	 */
	@Deprecated(since = "9.0", forRemoval = true)
    public void clearLanguageBundle() {
		clearTranslator();
	}

	/**
	 * @deprecated Provided for compatibility. Will be removed without substitution!
	 */
	@Deprecated(since = "9.0", forRemoval = true)
	public void clearTranslator() {
		TranslationFacility.setTranslator(null);
	}
}
