package core.model.misc;

import core.db.DBManager;
import core.db.user.UserManager;
import core.training.HattrickDate;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
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
    private Timestamp m_clDatum = new Timestamp(0);

    /** Date of activation */
    private Timestamp m_tActivationDate = new Timestamp(0);

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
        m_clDatum = getTimestamp(properties, "date");
        m_iTeamId = getInt(properties, "teamid", 0);
        m_sYouthTeamName = properties.getProperty("youthteamname", "");
        setYouthTeamId(getInteger(properties, "youthteamid"));
        m_sTeamName = properties.getProperty("teamname", "");
        m_sManager = properties.getProperty("owner", "");
        m_tActivationDate = getTimestamp(properties, "activationdate");
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

    private Timestamp getTimestamp(Properties properties, String key) {
        try {
            return parseHattrickDate(properties.getProperty(key));
        } catch (Exception ignored) {}
        return null;
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
            m_clDatum = rs.getTimestamp("Datum");
            m_iRegionId = rs.getInt("Region");
            m_bHasSupporter = rs.getBoolean("HasSupporter");
            m_tActivationDate = rs.getTimestamp("ActivationDate");
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
    public void setDatum(Timestamp m_clDatum) {
        this.m_clDatum = m_clDatum;
    }

    /**
     * Getter for property m_clDatum.
     *
     * @return Value of property m_clDatum.
     */
    public Timestamp getDatum() {
        return m_clDatum;
    }


    /**
     * Hattrick date time is presented as CE(ST)
     * @param sDate string fetched from Hattrick files
     * @param localized whether or not the date is localized (normally only for display purposes)
     * @return timestamp localized in CE(ST) or in user default if localized is set to true
     */
    public static Timestamp parseHattrickDate(String sDate, boolean localized) {
        if (sDate.length() > 19) {
            sDate = sDate.substring(0, 19);
        }

        if (sDate.length() == 19) {
            LocalDateTime htDateTimeNonLocalized = LocalDateTime.parse(sDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (localized) {
                ZoneId zoneId = ZoneId.of("Europe/Stockholm");
                ZonedDateTime htLocalDateTime = ZonedDateTime.of(htDateTimeNonLocalized, zoneId);

                //TODO: let user specify a time zone or select system default then change the line below #884

                ZonedDateTime userLocalDateTime = htLocalDateTime.withZoneSameInstant(ZoneId.systemDefault());

                return Timestamp.valueOf(userLocalDateTime.toLocalDateTime());
            } else {
                return Timestamp.valueOf(htDateTimeNonLocalized);
            }
        } else if (sDate.length() > 0) {
            try {
                //Hattrick
                final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd",
                        java.util.Locale.GERMANY);

                return new Timestamp(simpleFormat.parse(sDate).getTime());
            } catch (Exception e) {
                HOLogger.instance().log(Basics.class, e);
            }
        }
        return null;
    }

    /**
     * Hattrick date time is presented as CE(ST)
     * @param sDate string fetched from Hattrick files
     * @return timestamp localized in CE(ST)
     */
    public static Timestamp parseHattrickDate(String sDate) {
            return parseHattrickDate(sDate, false);
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
    public void setActivationDate(Timestamp m_tActivationDate) {
        this.m_tActivationDate = m_tActivationDate;
    }

    /**
     * Getter for property m_tActivationDate.
     *
     * @return Value of property m_tActivationDate.
     */
    public Timestamp getActivationDate() {
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

    public HattrickDate getHattrickWeek(){
        return new HattrickDate(this.m_iSeason, this.m_iSpieltag);
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
