package core.model.misc;

import core.db.user.User;
import core.db.user.UserManager;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;


/**
 * Benutzerdaten
 */
public final class Basics  {
    //~ Instance fields ----------------------------------------------------------------------------

    /** Manager */
    private String m_sManager = "";

    /** TeamName */
    private String m_sTeamName = "";

    /** Datum der Erstellung */
    private java.sql.Timestamp m_clDatum = new java.sql.Timestamp(0);

    /** Date of activation */
    private java.sql.Timestamp m_tActivationDate = new java.sql.Timestamp(0);

    /** Land */
    private int m_iLand;

    /** Liga */
    private int m_iLiga;

    /** Season */
    private int m_iSeason;

    /** Spieltag */
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
    public Basics(Properties properties) throws Exception {
        try {
            m_clDatum = setDatumByString(properties.getProperty("date"));
        } catch (Exception e) {
            //Egal wenn nicht funzt...
        }

        try {
            m_iTeamId = Integer.parseInt(properties.getProperty("teamid", "0"));
        } catch (Exception e) {
            m_iTeamId = 0;
        }

        m_sTeamName = properties.getProperty("teamname", "").toString();
        m_sManager = properties.getProperty("owner", "").toString();

        try {
        	if (properties.containsKey("activationdate") && (!properties.getProperty("activationdate").equals("0"))) {
        		m_tActivationDate = setDatumByString(properties.getProperty("activationdate"));
        	} else {
        		m_tActivationDate = null;
        	}
        } catch (Exception e) {
            m_tActivationDate = new Timestamp(0);
        }

        try {
            m_iLand = Integer.parseInt(properties.getProperty("countryid", "0"));
        } catch (Exception e) {
            m_iLand = 0;
        }

        try {
            m_iLiga = Integer.parseInt(properties.getProperty("leagueid", "0"));
        } catch (Exception e) {
            m_iLiga = 0;
        }

        try {
            m_iSeason = Integer.parseInt(properties.getProperty("season", "0"));
        } catch (Exception e) {
            m_iSeason = 0;
        }

        try {
            m_iSpieltag = Integer.parseInt(properties.getProperty("matchround", "0"));
        } catch (Exception e) {
            m_iSpieltag = 0;
        }
        try {
            m_iRegionId = Integer.parseInt(properties.getProperty("regionid", "0"));
        } catch (Exception e) {
            m_iRegionId = 0;
        }
        
        try {
        	m_bHasSupporter = Boolean.parseBoolean(properties.getProperty("hassupporter", "false"));
        } catch (Exception e) {
        	m_bHasSupporter = false;
        }
    }

    /**
     * Creates a new Basics object.
     */
    public Basics(ResultSet rs) throws Exception {
        try {
            m_iTeamId = rs.getInt("TeamID");
            m_sTeamName = core.db.DBManager.deleteEscapeSequences(rs.getString("TeamName"));
            m_sManager = core.db.DBManager.deleteEscapeSequences(rs.getString("Manager"));
            m_iLand = rs.getInt("Land");
            m_iLiga = rs.getInt("Liga");
            m_iSeason = rs.getInt("Saison");
            m_iSpieltag = rs.getInt("Spieltag");
            m_clDatum = rs.getTimestamp("Datum");
            m_iRegionId = rs.getInt("Region");
            m_bHasSupporter = rs.getBoolean("HasSupporter");
            m_tActivationDate = rs.getTimestamp("ActivationDate");
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"Konstruktor Basics: " + e.toString());
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_clDatum.
     *
     * @param m_clDatum New value of property m_clDatum.
     */
    public void setDatum(java.sql.Timestamp m_clDatum) {
        this.m_clDatum = m_clDatum;
    }

    /**
     * Getter for property m_clDatum.
     *
     * @return Value of property m_clDatum.
     */
    public java.sql.Timestamp getDatum() {
        return m_clDatum;
    }

    public java.sql.Timestamp setDatumByString(String date) {
        try {
            //Hattrick
            final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                                                                           java.util.Locale.GERMANY);

            return new java.sql.Timestamp(simpleFormat.parse(date).getTime());
        } catch (Exception e) {
            try {
                //Hattrick
                final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd",
                                                                                               java.util.Locale.GERMANY);

                return new java.sql.Timestamp(simpleFormat.parse(date).getTime());
            } catch (Exception expc) {
                HOLogger.instance().log(getClass(),e);
                return new java.sql.Timestamp(System.currentTimeMillis());
            }
        }
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
}
