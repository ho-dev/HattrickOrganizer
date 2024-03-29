package core.option;

/**
 * An instance of this class is created when the option dialog opens.
 *
 * <p>The class stores the information whether a restart or a reInit is needed
 * after changing something in the options.
 *
 * @author dable
 */

public class OptionManager {
	
	private static OptionManager m_clInstance;
	
	/**
	 * Is a restart needed after changes in the option dialog?
	 */
	private boolean restartNeeded;
	/**
	 * Is a reInit needed after changes in the option dialog?
	 */
	private boolean reInitNeeded;

	private boolean optionsChanged = false;
	

	private OptionManager() {
		restartNeeded = false;
		reInitNeeded = false;
	}
	
	/**
	 * Creates an instance of OptionManager
	 *
	 * @return instance of OptionManager
	 */
	public static OptionManager instance() {
		if (m_clInstance == null) {
			m_clInstance = new OptionManager();
		}

		return m_clInstance;
	}
	
	/**
	 * Deletes the instance of OptionManager
	 * (New instance should be created when options are loaded again)
	 *
	 */
	public static void deleteInstance() {
		if (m_clInstance != null) {
			m_clInstance = null;
		}
	}
	
	// Setter and Getter

	public boolean isRestartNeeded() {
		return restartNeeded;
	}

	public void setRestartNeeded() {
		this.restartNeeded = true;
		this.optionsChanged = true;
	}

	public boolean isReInitNeeded() {
		return reInitNeeded;
	}

	public void setReInitNeeded() {
		this.reInitNeeded = true;
		this.optionsChanged = true;
	}

	public boolean isOptionsChanged() {
		return optionsChanged;
	}

	public void setOptionsChanged(boolean optionsChanged) {
		this.optionsChanged = optionsChanged;
	}
}
