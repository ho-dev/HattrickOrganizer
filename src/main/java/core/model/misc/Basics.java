package core.model.misc;

import core.db.DBManager;
import core.db.user.UserManager;
import core.util.HODateTime;
import core.util.HOLogger;
import module.transfer.test.HTWeek;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;


/**
 * Benutzerdaten
 */
public final class Basics  {
    /**
     * youth team id (0 if non existing or no access in case of foreign teams)
     *          null: unknown (the time before youth team information were downloaded)
     *          0: team has no youth team
     */
    private Integer youthTeamId;
    /**
     * youth team name (empty if non existing or no access in case of foreign teams)
     */
    private String m_sYouthTeamName;

    /** Manager */
    private String m_sManager = "";

    /** TeamName */
    private String m_sTeamName = "";

    /** Datum der Erstellung */
    private HODateTime m_clDatum = HODateTime.htStart;

    /** Date of activation */
    private HODateTime m_tActivationDate = HODateTime.htStart;

    /** Land */
    private int m_iLand;

    /** The globally unique LeagueID. */
    private int m_iLiga;

    /** The current season number of the league. */
    private int m_iSeason;

    /**
     * The season offset to swedish's season.
     * (it is a negative number for leagues which started after swedish leagues)
     */
    private int m_iSeasonOffset;

    /** The current match round of the league. */
    private int m_iSpieltag;

    /** TeamId */
    private int m_iTeamId;

    /** Region Id */
    private int m_iRegionId;
    
    private boolean m_bHasSupporter;
    
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new Basics object.
     */
    public Basics(){

    }

    /**
     * Creates a new Basics object.
     */
    public Basics(Properties properties) {
        m_clDatum = HODateTime.fromHT(properties.getProperty( "date"));
        m_iTeamId = getInt(properties, "teamid", 0);
        m_sYouthTeamName = properties.getProperty("youthteamname", "");
        setYouthTeamId(getInteger(properties, "youthteamid"));
        m_sTeamName = properties.getProperty("teamname", "");
        m_sManager = properties.getProperty("owner", "");
        m_tActivationDate = HODateTime.fromHT(properties.getProperty( "activationdate"));
        m_iLand = getInt(properties, "countryid", 0);
        m_iLiga = getInt(properties, "leagueid", 0);
        m_iSeason = getInt(properties, "season", 0);
        m_iSeasonOffset = getInt(properties, "seasonoffset", 0);
        m_iSpieltag = getInt(properties, "matchround", 0);
        m_iRegionId = getInt(properties, "regionid", 0);
        m_bHasSupporter = getBoolean(properties, "hassupporter", false);
    }

    private Integer getInteger(Properties properties, String key) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (Exception ignored) {}
        return null;
    }

    private boolean getBoolean(Properties properties, String key, boolean def) {
        try {
            return Boolean.parseBoolean(properties.getProperty(key));
        } catch (Exception ignored) {}
        return def;
    }

    private int getInt(Properties properties, String key, int def) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (Exception ignored) {}
        return def;
    }

    /**
     * Creates a new Basics object.
     */
    public Basics(ResultSet rs) {
        try {
            m_iTeamId = rs.getInt("TeamID");
            m_sTeamName = core.db.DBManager.deleteEscapeSequences(rs.getString("TeamName"));
            m_sManager = core.db.DBManager.deleteEscapeSequences(rs.getString("Manager"));
            m_iLand = rs.getInt("Land");
            m_iLiga = rs.getInt("Liga");
            m_iSeason = rs.getInt("Saison");
            m_iSeasonOffset = rs.getInt("SeasonOffset");
            m_iSpieltag = rs.getInt("Spieltag");
            m_clDatum = HODateTime.fromDbTimestamp(rs.getTimestamp("Datum"));
            m_iRegionId = rs.getInt("Region");
            m_bHasSupporter = rs.getBoolean("HasSupporter");
            m_tActivationDate = HODateTime.fromDbTimestamp(rs.getTimestamp("ActivationDate"));
            m_sYouthTeamName = core.db.DBManager.deleteEscapeSequences(rs.getString("YouthTeamName"));
            setYouthTeamId(DBManager.getInteger(rs,"YouthTeamID"));
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"Constructor Basics: " + e);
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_clDatum.
     *
     * @param m_clDatum New value of property m_clDatum.
     */
    public void setDatum(HODateTime m_clDatum) {
        this.m_clDatum = m_clDatum;
    }

    /**
     * Getter for property m_clDatum.
     *
     * @return Value of property m_clDatum.
     */
    public HODateTime getDatum() {
        return m_clDatum;
    }


    /**
	 * @return the m_bHasSupporter
	 */
	public boolean isHasSupporter() {
		return m_bHasSupporter;
	}

	/**
	 * @param m_bHasSupporter the m_bHasSupporter to set
	 */
	public void setHasSupporter(boolean m_bHasSupporter) {
		this.m_bHasSupporter = m_bHasSupporter;
	}

	/**
     * Setter for property m_iLand.
     *
     * @param m_iLand New value of property m_iLand.
     */
    public void setLand(int m_iLand) {
        this.m_iLand = m_iLand;
    }

    /**
     * Getter for property m_iLand.
     *
     * @return Value of property m_iLand.
     */
    public int getLand() {
        return m_iLand;
    }

    /**
     * Setter for property m_iLiga.
     *
     * @param m_iLiga New value of property m_iLiga.
     */
    public void setLiga(int m_iLiga) {
        this.m_iLiga = m_iLiga;
    }

    /**
     * Getter for property m_iLiga.
     *
     * @return Value of property m_iLiga.
     */
    public int getLiga() {
        return m_iLiga;
    }

    /**
     * Setter for property m_sManager.
     *
     * @param m_sManager New value of property m_sManager.
     */
    public void setManager(java.lang.String m_sManager) {
        this.m_sManager = m_sManager;
    }

    /**
     * Getter for property m_sManager.
     *
     * @return Value of property m_sManager.
     */
    public java.lang.String getManager() {
        return m_sManager;
    }

    /**
     * Setter for property m_tActivationDate.
     *
     * @param m_tActivationDate New value of property m_tActivationDate.
     */
    public void setActivationDate(HODateTime m_tActivationDate) {
        this.m_tActivationDate = m_tActivationDate;
    }

    /**
     * Getter for property m_tActivationDate.
     *
     * @return Value of property m_tActivationDate.
     */
    public HODateTime getActivationDate() {
        return m_tActivationDate;
    }

    /**
     * Setter for property m_iSeason.
     *
     * @param m_iSeason New value of property m_iSeason.
     */
    public void setSeason(int m_iSeason) {
        this.m_iSeason = m_iSeason;
    }

    /**
     * Getter for property m_iSeason.
     *
     * @return Value of property m_iSeason.
     */
    public int getSeason() {
        return m_iSeason;
    }

    /**
     * Setter for property m_iSpieltag.
     *
     * @param m_iSpieltag New value of property m_iSpieltag.
     */
    public void setSpieltag(int m_iSpieltag) {
        this.m_iSpieltag = m_iSpieltag;
    }

    /**
     * Getter for property m_iSpieltag.
     *
     * @return Value of property m_iSpieltag.
     */
    public int getSpieltag() {
        return m_iSpieltag;
    }

    public HODateTime getHattrickWeek() {
        var week = new HODateTime.HTWeek(this.m_iSeason - this.m_iSeasonOffset, this.m_iSpieltag);
        return HODateTime.fromHTWeek(week);
    }

    /**
     * Setter for property m_iTeamId.
     *
     * @param m_iTeamId New value of property m_iTeamId.
     */
    public void setTeamId(int m_iTeamId) {
        this.m_iTeamId = m_iTeamId;
    }

    /**
     * Getter for property m_iTeamId.
     *
     * @return Value of property m_iTeamId.
     */
    public int getTeamId() {
        return m_iTeamId;
    }

    /**
     * Is national team
     *
     * @return true if it's a national team.
     */
    public boolean isNationalTeam() {
        return UserManager.instance().getCurrentUser().isNtTeam();
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
    
    /**
     * Sets the Region ID
     * 
     * @param regionId	new value
     */
    public void setRegionId (int regionId) {
    	this.m_iRegionId = regionId;
    }
    
    /**
     * Gets the Region ID
     * @return	the region id
     */
    public int getRegionId () {
    	return m_iRegionId;
    }

    public int getSeasonOffset() {
        return m_iSeasonOffset;
    }

    public void setSeasonOffset(int seasonOffset) {
        this.m_iSeasonOffset = seasonOffset;
    }

    public Integer getYouthTeamId() {
        return youthTeamId;
    }

    public boolean hasYouthTeam(){
        return youthTeamId != null && youthTeamId > 0;
    }

    /**
     * Set the youth team id
     * @param m_iYouthTeamId youth team id
     */
    public void setYouthTeamId(Integer m_iYouthTeamId) {
        if (m_iYouthTeamId != null && m_iYouthTeamId >= 0) {
            this.youthTeamId = m_iYouthTeamId;
        } else {
            this.youthTeamId = null;
        }
    }

    public String getYouthTeamName() {
        return m_sYouthTeamName;
    }

    public void setYouthTeamName(String m_sYouthTeamName) {
        this.m_sYouthTeamName = m_sYouthTeamName;
    }
}
