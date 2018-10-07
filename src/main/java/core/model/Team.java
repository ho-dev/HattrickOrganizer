// %233313029:de.hattrickorganizer.model%
package core.model;

import core.util.HOLogger;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Enthält die Daten des Teams (nicht der Spieler!)
 */
public final class Team  {
	// ~ Instance fields

	/** formation xp 343 */
	private int formationXp343;

	/** formation xp 352 */
	private int formationXp352;

	/** formation xp 433 */
	private int formationXp433;

	/** formation xp 451 */
	private int formationXp451;

	/** formation xp 532 */
	private int formationXp532;

	/** formation xp 541 */
	private int formationXp541;

	/** formation xp 442 */
	private int formationXp442;

	/** formation xp 523 */
	private int formationXp523;

	/** formation xp 550 */
	private int formationXp550;

	/** formation xp 253 */
	private int formationXp253;

	/** Selbstvertrauen */
	private int m_iSelbstvertrauen;

	/** Stimmung */
	private int m_iStimmungInt;

	private int subStimmung;

	/** TrainingsArt */
	private int m_iTrainingsArt;

	// //////////////////////////////////////////////////////////////////////////////
	// Member
	// //////////////////////////////////////////////////////////////////////////////

	/** Trainingsintensität */
	private int m_iTrainingslevel;

	private int m_iStaminaTrainingPart;

	// ~ Constructors
	// -------------------------------------------------------------------------------

	// //////////////////////////////////////////////////////////////////////////////
	// Konstruktor
	// //////////////////////////////////////////////////////////////////////////////
	public Team(Properties properties) throws Exception {
		m_iTrainingslevel = Integer.parseInt(properties.getProperty("trlevel", "0"));
		m_iStaminaTrainingPart = Integer.parseInt(properties.getProperty("staminatrainingpart", "0"));
		m_iStimmungInt = Integer.parseInt(properties.getProperty("stamningvalue", "0"));
		m_iSelbstvertrauen = Integer.parseInt(properties.getProperty("sjalvfortroendevalue", "0"));
		formationXp433 = Integer.parseInt(properties.getProperty("exper433", "0"));
		formationXp451 = Integer.parseInt(properties.getProperty("exper451", "0"));
		formationXp352 = Integer.parseInt(properties.getProperty("exper352", "0"));
		formationXp532 = Integer.parseInt(properties.getProperty("exper532", "0"));
		formationXp343 = Integer.parseInt(properties.getProperty("exper343", "0"));
		formationXp541 = Integer.parseInt(properties.getProperty("exper541", "0"));
		formationXp442 = Integer.parseInt(properties.getProperty("exper442", "0"));
		formationXp523 = Integer.parseInt(properties.getProperty("exper523", "0"));
		formationXp550 = Integer.parseInt(properties.getProperty("exper550", "0"));
		formationXp253 = Integer.parseInt(properties.getProperty("exper253", "0"));
		m_iTrainingsArt = Integer.parseInt(properties.getProperty("trtypevalue", "-1"));
		subStimmung = 2;
	}

	/**
	 * Creates a new Team object.
	 */
	public Team(ResultSet rs) throws Exception {
		m_iTrainingslevel = rs.getInt("TrainingsIntensitaet");
		m_iStaminaTrainingPart = rs.getInt("StaminaTrainingPart");
		m_iSelbstvertrauen = rs.getInt("iSelbstvertrauen");
		m_iStimmungInt = rs.getInt("iStimmung");
		formationXp433 = rs.getInt("iErfahrung433");
		formationXp451 = rs.getInt("iErfahrung451");
		formationXp352 = rs.getInt("iErfahrung352");
		formationXp532 = rs.getInt("iErfahrung532");
		formationXp343 = rs.getInt("iErfahrung343");
		formationXp541 = rs.getInt("iErfahrung541");
		try {
			formationXp442 = rs.getInt("iErfahrung442");
			formationXp523 = rs.getInt("iErfahrung523");
			formationXp550 = rs.getInt("iErfahrung550");
			formationXp253 = rs.getInt("iErfahrung253");
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "Error(Team rs): " + e);
		}
		m_iTrainingsArt = rs.getInt("TrainingsArt");
		subStimmung = 2;
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------
	// //////////////////////////////////////////////////////////////////////////////
	// Static
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
	public void setFormationExperience343(int exp) {
		this.formationXp343 = exp;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFormationExperience343() {
		return formationXp343;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFormationExperience352(int exp) {
		this.formationXp352 = exp;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFormationExperience352() {
		return formationXp352;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFormationExperience433(int exp) {
		this.formationXp433 = exp;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFormationExperience433() {
		return formationXp433;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFormationExperience451(int exp) {
		this.formationXp451 = exp;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFormationExperience451() {
		return formationXp451;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFormationExperience532(int exp) {
		this.formationXp532 = exp;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFormationExperience532() {
		return formationXp532;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFormationExperience541(int exp) {
		this.formationXp541 = exp;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFormationExperience541() {
		return formationXp541;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFormationExperience442(int exp) {
		this.formationXp442 = exp;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFormationExperience442() {
		return formationXp442;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setFormationExperience523(int exp) {
		this.formationXp523 = exp;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getFormationExperience523() {
		return formationXp523;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFormationExperience550(int exp) {
		this.formationXp550 = exp;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getFormationExperience550() {
		return formationXp550;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setFormationExperience253(int exp) {
		this.formationXp253 = exp;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getFormationExperience253() {
		return formationXp253;
	}

	/**
	 * Setter for property m_iSelbstvertrauen.
	 * 
	 * @param m_iSelbstvertrauen
	 *            New value of property m_iSelbstvertrauen.
	 */
	public void setSelbstvertrauenAsInt(int m_iSelbstvertrauen) {
		this.m_iSelbstvertrauen = m_iSelbstvertrauen;
	}

	/**
	 * Getter for property m_iSelbstvertrauen.
	 * 
	 * @return Value of property m_iSelbstvertrauen.
	 */
	public int getSelbstvertrauenAsInt() {
		return m_iSelbstvertrauen;
	}

	/**
	 * Setter for property m_iStimmung.
	 * 
	 * @param m_iStimmung
	 *            New value of property m_iStimmung.
	 */
	public void setStimmungAsInt(int m_iStimmung) {
		this.m_iStimmungInt = m_iStimmung;
	}

	/**
	 * Getter for property m_iStimmung.
	 * 
	 * @return Value of property m_iStimmung.
	 */
	public int getStimmungAsInt() {
		return m_iStimmungInt;
	}

	/**
	 * Setter for property m_iTrainingsArt.
	 * 
	 * @param m_iTrainingsArt
	 *            New value of property m_iTrainingsArt.
	 */
	public void setTrainingsArtAsInt(int m_iTrainingsArt) {
		this.m_iTrainingsArt = m_iTrainingsArt;
	}

	/**
	 * Getter for property m_iTrainingsArt.
	 * 
	 * @return Value of property m_iTrainingsArt.
	 */
	public int getTrainingsArtAsInt() {
		return m_iTrainingsArt;
	}

	/**
	 * Setter for property m_iTrainingslevel.
	 * 
	 * @param m_iTrainingslevel
	 *            New value of property m_iTrainingslevel.
	 */
	public void setTrainingslevel(int m_iTrainingslevel) {
		this.m_iTrainingslevel = m_iTrainingslevel;
	}

	/**
	 * Set the stamina share amount in percent.
	 */
	public void setStaminaTrainingPart(int m_iStaminaTrainingPart) {
		this.m_iStaminaTrainingPart = m_iStaminaTrainingPart;
	}

	/**
	 * Getter for property m_iTrainingslevel.
	 * 
	 * @return Value of property m_iTrainingslevel.
	 */
	public int getTrainingslevel() {
		return m_iTrainingslevel;
	}

	/**
	 * Get the stamina share amount in percent.
	 */
	public int getStaminaTrainingPart() {
		return m_iStaminaTrainingPart;
	}

	/**
	 * Get the sublevel of the team spirit.
	 */
	public int getSubStimmung() {
		return subStimmung;
	}

	/**
	 * Set the sublevel of the team spirit.
	 */
	public void setSubStimmung(int i) {
		subStimmung = i;
	}

}
