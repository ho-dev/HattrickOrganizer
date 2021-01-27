// %163934374:de.hattrickorganizer.model%
package core.model.misc;

import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Properties;


/**
 * Allgemeine Informationen über den Verein
 */
public final class Verein {
	
	// for locale indepemdent parsing of input with commas
    final DecimalFormat DF = new DecimalFormat("0", new DecimalFormatSymbols(Locale.GERMANY));

    /** Team Name */
    private String m_sTeamName;
    /** Datum */
    private Timestamp m_clDate;
    /** Jungendspieler gezogen */
    private boolean m_bYouthPull;
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
    /** Investition */
    private int m_iJugendGeld;
    /** Pressesprecher */
    private int m_iSpokePersonLevels;

    public int getGlobalRanking() {
        return m_iGlobalRanking;
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
        m_iAssistantTrainerLevels = DF.parse(properties.getProperty("hjtranare", "0")).intValue();
        m_iSportPsychologistLevels = DF.parse(properties.getProperty("psykolog", "0")).intValue();
        m_iSpokePersonLevels = DF.parse(properties.getProperty("presstalesman", "0")).intValue();
        m_iMedicLevels = DF.parse(properties.getProperty("lakare", "0")).intValue();
        m_iJugend = DF.parse(properties.getProperty("juniorverksamhet", "0")).intValue();
        m_iFans = DF.parse(properties.getProperty("fanclub", "0")).intValue();
        m_iUngeschlagen = DF.parse(properties.getProperty("undefeated", "0")).intValue();
        m_iSiege = DF.parse(properties.getProperty("victories", "0")).intValue();
        m_iFinancialDirectorLevels = DF.parse(properties.getProperty("financialdirectorlevels", "0")).intValue();
        m_iFormCoachLevels = DF.parse(properties.getProperty("formcoachlevels", "0")).intValue();
        m_iTacticalAssistantLevels = DF.parse(properties.getProperty("tacticalassistantlevels", "0")).intValue();
        m_iGlobalRanking = DF.parse(properties.getProperty("globalranking", "0")).intValue();
        m_iLeagueRanking = DF.parse(properties.getProperty("leagueranking", "0")).intValue();
        m_iRegionRanking = DF.parse(properties.getProperty("regionranking", "0")).intValue();
        m_iPowerRating = DF.parse(properties.getProperty("powerrating", "0")).intValue();
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
            HOLogger.instance().log(getClass(),"Konstruktor Verein: " + e.toString());
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
    public void setDate(java.sql.Timestamp m_clDate) {
        this.m_clDate = m_clDate;
    }

    /**
     * Getter for property m_clDate.
     *
     * @return Value of property m_clDate.
     */
    public java.sql.Timestamp getDate() {
        return m_clDate;
    }

    public void setDateFromString(String date) {
        m_clDate = Basics.parseHattrickDate(date);
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

    /**
     * Setter for property m_iFinanzberater.
     *
     * @param m_iFinanzberater New value of property m_iFinanzberater.
     */
    @Deprecated
    public void setFinanzberater(int m_iFinanzberater) {
    }

    /**
     * Getter for property m_iFinanzberater.
     *
     * @return Value of property m_iFinanzberater.
     */
    @Deprecated
    public int getFinanzberater() {
        return 0;
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
     * Setter for property m_iJugendGeld.
     *
     * @param m_iJugendGeld New value of property m_iJugendGeld.
     */
    public void setJugendGeld(int m_iJugendGeld) {
        this.m_iJugendGeld = m_iJugendGeld;
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
     * Setter for property m_iTorwartTrainer.
     *
     * @param m_iTorwartTrainer New value of property m_iTorwartTrainer.
     */
    @Deprecated
    public void setTorwartTrainer(int m_iTorwartTrainer) {}

    ////////////////////////////////////////////////////////////////////////////////
    //Accessor
    ////////////////////////////////////////////////////////////////////////////////

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

    /**
     * Setter for property m_bYouthPull.
     *
     * @param m_bYouthPull New value of property m_bYouthPull.
     */
    public void setYouthPull(boolean m_bYouthPull) {
        this.m_bYouthPull = m_bYouthPull;
    }

    /**
     * Getter for property m_bYouthPull.
     *
     * @return Value of property m_bYouthPull.
     */
    public boolean isYouthPull() {
        return m_bYouthPull;
    }
}
