package core.model;

import core.util.HOLogger;

/**
 * @author thetom
 */
public class XtraData  {
    //~ Instance fields ----------------------------------------------------------------------------

    private String m_sLogoURL;
    private java.sql.Timestamp m_clEconomyDate;
    private java.sql.Timestamp m_clSeriesMatchDate;
    private java.sql.Timestamp m_clTrainingDate;
    private boolean m_bHasPromoted;
    private double m_dCurrencyRate = -1.0d;
    private int m_iLeagueLevelUnitID = -1;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of XtraData
     */
    public XtraData(java.util.Properties properties) throws Exception {
        m_dCurrencyRate = Double.parseDouble(properties.getProperty("currencyrate", "1"));
        m_bHasPromoted = Boolean.valueOf(properties.getProperty("haspromoted", "FALSE"))
                                .booleanValue();
        m_sLogoURL = properties.getProperty("logourl", "");
        m_clSeriesMatchDate = core.util.Helper.parseDate(properties.getProperty("seriesmatchdate"));
        m_clEconomyDate = core.util.Helper.parseDate(properties.getProperty("economydate"));
        m_clTrainingDate = core.util.Helper.parseDate(properties.getProperty("trainingdate"));

        try {
            m_iLeagueLevelUnitID = Integer.parseInt(properties.getProperty("leaguelevelunitid"));
        } catch (Exception e) {
        }
    }

    /**
     * Creates a new XtraData object.
     */
    public XtraData(java.sql.ResultSet rs) throws Exception {
        try {
            m_dCurrencyRate = rs.getDouble("CurrencyRate");
            m_sLogoURL = core.db.DBManager.deleteEscapeSequences(rs.getString("LogoURL"));
            m_bHasPromoted = rs.getBoolean("HasPromoted");
            m_clSeriesMatchDate = rs.getTimestamp("SeriesMatchDate");
            m_clTrainingDate = rs.getTimestamp("TrainingDate");
            m_clEconomyDate = rs.getTimestamp("EconomyDate");
            m_iLeagueLevelUnitID = rs.getInt("LeagueLevelUnitID");
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"XtraData: " + e.toString());

            //HOLogger.instance().log(getClass(),e);
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_dCurrencyRate.
     *
     * @param m_dCurrencyRate New value of property m_dCurrencyRate.
     */
    public final void setCurrencyRate(double m_dCurrencyRate) {
        this.m_dCurrencyRate = m_dCurrencyRate;
    }

    /**
     * Getter for property m_dCurrencyRate.
     *
     * @return Value of property m_dCurrencyRate.
     */
    public final double getCurrencyRate() {
        return m_dCurrencyRate;
    }

    /**
     * Setter for property m_clEconomyDate.
     *
     * @param m_clEconomyDate New value of property m_clEconomyDate.
     */
    public final void setEconomyDate(java.sql.Timestamp m_clEconomyDate) {
        this.m_clEconomyDate = m_clEconomyDate;
    }

    /**
     * Getter for property m_clEconomyDate.
     *
     * @return Value of property m_clEconomyDate.
     */
    public final java.sql.Timestamp getEconomyDate() {
        return m_clEconomyDate;
    }

    /**
     * Setter for property m_bHasPromoted.
     *
     * @param m_bHasPromoted New value of property m_bHasPromoted.
     */
    public final void setHasPromoted(boolean m_bHasPromoted) {
        this.m_bHasPromoted = m_bHasPromoted;
    }

    /**
     * Getter for property m_bHasPromoted.
     *
     * @return Value of property m_bHasPromoted.
     */
    public final boolean isHasPromoted() {
        return m_bHasPromoted;
    }

    /**
     * Setter for property m_iLeagueLevelUnitID.
     *
     * @param m_iLeagueLevelUnitID New value of property m_iLeagueLevelUnitID.
     */
    public final void setLeagueLevelUnitID(int m_iLeagueLevelUnitID) {
        this.m_iLeagueLevelUnitID = m_iLeagueLevelUnitID;
    }

    /**
     * Getter for property m_iLeagueLevelUnitID.
     *
     * @return Value of property m_iLeagueLevelUnitID.
     */
    public final int getLeagueLevelUnitID() {
        return m_iLeagueLevelUnitID;
    }

    /**
     * Setter for property m_sLogoURL.
     *
     * @param m_sLogoURL New value of property m_sLogoURL.
     */
    public final void setLogoURL(java.lang.String m_sLogoURL) {
        this.m_sLogoURL = m_sLogoURL;
    }

    /**
     * Getter for property m_sLogoURL.
     *
     * @return Value of property m_sLogoURL.
     */
    public final java.lang.String getLogoURL() {
        return m_sLogoURL;
    }

    /**
     * Setter for property m_clSeriesMatchDate.
     *
     * @param m_clSeriesMatchDate New value of property m_clSeriesMatchDate.
     */
    public final void setSeriesMatchDate(java.sql.Timestamp m_clSeriesMatchDate) {
        this.m_clSeriesMatchDate = m_clSeriesMatchDate;
    }

    /**
     * Getter for property m_clSeriesMatchDate.
     *
     * @return Value of property m_clSeriesMatchDate.
     */
    public final java.sql.Timestamp getSeriesMatchDate() {
        return m_clSeriesMatchDate;
    }

    /**
     * Setter for property m_clTrainingDate.
     *
     * @param m_clTrainingDate New value of property m_clTrainingDate.
     */
    public final void setTrainingDate(java.sql.Timestamp m_clTrainingDate) {
        this.m_clTrainingDate = m_clTrainingDate;
    }

    /**
     * Getter for property m_clTrainingDate.
     * This is the date for the next training.
     *
     * @return Value of property m_clTrainingDate.
     */
    public final java.sql.Timestamp getTrainingDate() {
        return m_clTrainingDate;
    }
}
