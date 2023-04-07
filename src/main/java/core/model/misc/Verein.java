// %163934374:de.hattrickorganizer.model%
package core.model.misc;

import core.db.AbstractTable;
import core.util.HODateTime;
import core.util.HOLogger;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Properties;


/**
 * Allgemeine Informationen über den Verein
 */
public final class Verein extends AbstractTable.Storable {

    /** Team Name */
    private String m_sTeamName;
    /** Datum */
    private HODateTime m_clDate;
    /** doctors */
    private int m_iMedicLevels;
    /** Co-Trainer */
    private int m_iAssistantTrainerLevels;
    /** FanClub */
    private int m_iFans;
    private int m_iFinancialDirectorLevels;
    private int m_iFormCoachLevels;
    /** Jugendmannschaft */
    private int m_iJugend;
    /** Pressesprecher */
    private int m_iSpokePersonLevels;
    private int hrfId;

    public int getGlobalRanking() {
        return m_iGlobalRanking;
    }

    public void setGlobalRanking(int v){
        this.m_iGlobalRanking = v;
    }

    /** Power Ranking */
    private int m_iGlobalRanking;

    public int getLeagueRanking() {
        return m_iLeagueRanking;
    }

    private int m_iLeagueRanking;

    public int getRegionRanking() {
        return m_iRegionRanking;
    }

    private int m_iRegionRanking;

    public int getPowerRating() {
        return m_iPowerRating;
    }

    private int m_iPowerRating;
    /** Psychologen */
    private int m_iSportPsychologistLevels;
    /** Siege */
    private int m_iSiege;
    private int m_iTacticalAssistantLevels;
    /** TeamID */
    private int m_iTeamID = -1;

    /** Ungeschlagen für # Spiele */
    private int m_iUngeschlagen;


    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new Verein object.
     */
    public Verein() {
    }

    /**
     * Creates a new Club object based on properties (e.g. from an hrf file).
     */
    public Verein(Properties properties) throws Exception {
        m_iAssistantTrainerLevels = NumberUtils.toInt(properties.getProperty("hjtranare"), 0);
        m_iSportPsychologistLevels = NumberUtils.toInt(properties.getProperty("psykolog"), 0);
        m_iSpokePersonLevels = NumberUtils.toInt(properties.getProperty("presstalesman"),0);
        m_iMedicLevels = NumberUtils.toInt(properties.getProperty("lakare"),0);
        m_iJugend = NumberUtils.toInt(properties.getProperty("juniorverksamhet"),0);
        m_iFans = NumberUtils.toInt(properties.getProperty("fanclub"),0);
        m_iUngeschlagen = NumberUtils.toInt(properties.getProperty("undefeated"),0);
        m_iSiege = NumberUtils.toInt(properties.getProperty("victories"),0);
        m_iFinancialDirectorLevels = NumberUtils.toInt(properties.getProperty("financialdirectorlevels"),0);
        m_iFormCoachLevels = NumberUtils.toInt(properties.getProperty("formcoachlevels"),0);
        m_iTacticalAssistantLevels = NumberUtils.toInt(properties.getProperty("tacticalassistantlevels"),0);
        m_iGlobalRanking = NumberUtils.toInt(properties.getProperty("globalranking"),0);
        m_iLeagueRanking = NumberUtils.toInt(properties.getProperty("leagueranking"),0);
        m_iRegionRanking = NumberUtils.toInt(properties.getProperty("regionranking"),0);
        m_iPowerRating = NumberUtils.toInt(properties.getProperty("powerrating"),0);
    }

    /**
     * Creates a new Verein object.
     */
    public Verein(ResultSet rs) throws Exception {
        try {
            m_iAssistantTrainerLevels = rs.getInt("COTrainer");
            m_iSportPsychologistLevels = rs.getInt("Pschyologen");
            m_iSpokePersonLevels = rs.getInt("PRManager");
            m_iMedicLevels = rs.getInt("Aerzte");
            m_iJugend = rs.getInt("Jugend");
            m_iFans = rs.getInt("Fans");
            m_iUngeschlagen = rs.getInt("Ungeschlagen");
            m_iSiege = rs.getInt("Siege");
            m_iTacticalAssistantLevels = rs.getInt("TacticAssist");
            m_iFormCoachLevels = rs.getInt("FormAssist");
            m_iFinancialDirectorLevels = rs.getInt("Finanzberater");
            m_iGlobalRanking = rs.getInt("globalranking");
            m_iLeagueRanking = rs.getInt("leagueranking");
            m_iRegionRanking = rs.getInt("regionranking");
            m_iPowerRating = rs.getInt("powerrating");
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"Konstruktor Verein: " + e);
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_iAerzte.
     *
     * @param m_iAerzte New value of property m_iAerzte.
     */
    public void setAerzte(int m_iAerzte) {
        this.m_iMedicLevels = m_iAerzte;
    }

    /**
     * Getter for property m_iAerzte.
     *
     * @return Value of property m_iAerzte.
     */
    public int getAerzte() {
        return m_iMedicLevels;
    }

    /**
     * Setter for property m_iCoTrainer.
     *
     * @param m_iCoTrainer New value of property m_iCoTrainer.
     */
    public void setCoTrainer(int m_iCoTrainer) {
        this.m_iAssistantTrainerLevels = m_iCoTrainer;
    }

    /**
     * Getter for property m_iCoTrainer.
     *
     * @return Value of property m_iCoTrainer.
     */
    public int getCoTrainer() {
        return m_iAssistantTrainerLevels;
    }

    /**
     * Setter for property m_clDate.
     *
     * @param m_clDate New value of property m_clDate.
     */
    public void setDate(HODateTime m_clDate) {
        this.m_clDate = m_clDate;
    }

    /**
     * Getter for property m_clDate.
     *
     * @return Value of property m_clDate.
     */
    public HODateTime getDate() {
        return m_clDate;
    }

    /**
     * Setter for property m_iFans.
     *
     * @param m_iFans New value of property m_iFans.
     */
    public void setFans(int m_iFans) {
        this.m_iFans = m_iFans;
    }

    /**
     * Getter for property m_iFans.
     *
     * @return Value of property m_iFans.
     */
    public int getFans() {
        return m_iFans;
    }

    public int getFinancialDirectorLevels() {
		return m_iFinancialDirectorLevels;
	}

	public void setFinancialDirectorLevels(int m_iFinancialDirectorLevels) {
		this.m_iFinancialDirectorLevels = m_iFinancialDirectorLevels;
	}

	public int getFormCoachLevels() {
		return m_iFormCoachLevels;
	}

	public void setFormCoachLevels(int m_iFormCoachLevels) {
		this.m_iFormCoachLevels = m_iFormCoachLevels;
	}

	/**
     * Setter for property m_iJugend.
     *
     * @param m_iJugend New value of property m_iJugend.
     */
    public void setJugend(int m_iJugend) {
        this.m_iJugend = m_iJugend;
    }

    /**
     * Getter for property m_iJugend.
     *
     * @return Value of property m_iJugend.
     */
    public int getJugend() {
        return m_iJugend;
    }

    /**
     * Setter for property m_iPRManager.
     *
     * @param m_iPRManager New value of property m_iPRManager.
     */
    public void setPRManager(int m_iPRManager) {
        this.m_iSpokePersonLevels = m_iPRManager;
    }

    /**
     * Getter for property m_iPRManager.
     *
     * @return Value of property m_iPRManager.
     */
    public int getPRManager() {
        return m_iSpokePersonLevels;
    }

    /**
     * Setter for property m_iPsychologen.
     *
     * @param m_iPsychologen New value of property m_iPsychologen.
     */
    public void setPsychologen(int m_iPsychologen) {
        this.m_iSportPsychologistLevels = m_iPsychologen;
    }

    /**
     * Getter for property m_iPsychologen.
     *
     * @return Value of property m_iPsychologen.
     */
    public int getPsychologen() {
        return m_iSportPsychologistLevels;
    }

    /**
     * Setter for property m_iSiege.
     *
     * @param m_iSiege New value of property m_iSiege.
     */
    public void setSiege(int m_iSiege) {
        this.m_iSiege = m_iSiege;
    }

    /**
     * Getter for property m_iSiege.
     *
     * @return Value of property m_iSiege.
     */
    public int getSiege() {
        return m_iSiege;
    }

    /**
     * Setter for property m_iTeamID.
     *
     * @param m_iTeamID New value of property m_iTeamID.
     */
    public void setTeamID(int m_iTeamID) {
        this.m_iTeamID = m_iTeamID;
    }

    /**
     * Getter for property m_iTeamID.
     *
     * @return Value of property m_iTeamID.
     */
    public int getTeamID() {
        return m_iTeamID;
    }

    /**
     * Setter for property m_sTeamName.
     *
     * @param m_sTeamName New value of property m_sTeamName.
     */
    public void setTeamName(java.lang.String m_sTeamName) {
        this.m_sTeamName = m_sTeamName;
    }

    /**
     * Getter for property m_sTeamName.
     *
     * @return Value of property m_sTeamName.
     */
    public java.lang.String getTeamName() {
        return m_sTeamName;
    }
  
	public int getTacticalAssistantLevels() {
		return m_iTacticalAssistantLevels;
	}

	public void setTacticalAssistantLevels(int m_iTacticalAssistantLevels) {
		this.m_iTacticalAssistantLevels = m_iTacticalAssistantLevels;
	}

    /**
     * Getter for property m_iTorwartTrainer.
     *
     * @return Value of property m_iTorwartTrainer.
     */
    @Deprecated
    public int getTorwartTrainer() {
        return 0;
    }

    /**
     * Setter for property m_iUngeschlagen.
     *
     * @param m_iUngeschlagen New value of property m_iUngeschlagen.
     */
    public void setUngeschlagen(int m_iUngeschlagen) {
        this.m_iUngeschlagen = m_iUngeschlagen;
    }

    /**
     * Getter for property m_iUngeschlagen.
     *
     * @return Value of property m_iUngeschlagen.
     */
    public int getUngeschlagen() {
        return m_iUngeschlagen;
    }

    public int getHrfId() {
        return hrfId;
    }

    public void setHrfId(int hrfId) {
        this.hrfId = hrfId;
    }

    public void setLeagueRanking(int v) {
        this.m_iLeagueRanking = v;
    }

    public void setRegionRanking(int v) {
        this.m_iRegionRanking = v;
    }

    public void setPowerRating(int v) {
        this.m_iPowerRating = v;
    }
}
