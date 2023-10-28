package core.model;

import core.db.AbstractTable;
import core.util.HODateTime;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Properties;

public class XtraData extends AbstractTable.Storable {

    private String m_sLogoURL;
    private HODateTime m_clEconomyDate;
    private HODateTime m_clSeriesMatchDate;
    private HODateTime m_TrainingDate;
    private boolean m_bHasPromoted;
    private double m_dCurrencyRate = -1.0d;

    /**
     * Id of the user's premier team
     * (same currency with all teams of the user)
     */
    private Integer m_iCountryId;

    /**
     * The ID number of the LeagueLevelUnit.
     * In week 16 of a season this ID switches to the value of the next season (could be a different one)
     */
    private int m_iLeagueLevelUnitID = -1;
    private int hrfId;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of XtraData
     */
    public XtraData(Properties properties) {
        m_dCurrencyRate = NumberUtils.toDouble(properties.getProperty("currencyrate"), 1);
        m_iCountryId = getInteger(properties, "countryid", null);
        m_bHasPromoted = Boolean.parseBoolean(properties.getProperty("haspromoted", "FALSE"));
        m_sLogoURL = properties.getProperty("logourl", "");
        m_clSeriesMatchDate = HODateTime.fromHT(properties.getProperty("seriesmatchdate"));
        m_clEconomyDate = HODateTime.fromHT(properties.getProperty("economydate"));
        m_TrainingDate = HODateTime.fromHT(properties.getProperty("trainingdate"));
        m_iLeagueLevelUnitID = getInteger(properties, "leaguelevelunitid", -1);
    }

    private Integer getInteger(Properties properties, String key, Integer def) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (Exception ignored) {
        }
        return def;
    }

    /**
     * Creates a new XtraData object. This constructor is used by AbstractTable
     */
    public XtraData(){}


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
    public final void setEconomyDate(HODateTime m_clEconomyDate) {
        this.m_clEconomyDate = m_clEconomyDate;
    }

    /**
     * Getter for property m_clEconomyDate.
     *
     * @return Value of property m_clEconomyDate.
     */
    public final HODateTime getEconomyDate() {
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
     * Getter for property m_sLogoURL.
     *
     * @return Value of property m_sLogoURL.
     */
    public final String getLogoURL() {
        return m_sLogoURL;
    }

    public void setLogoURL(String logoURL){
        this.m_sLogoURL=logoURL;
    }

    /**
     * Setter for property m_clSeriesMatchDate.
     *
     * @param m_clSeriesMatchDate New value of property m_clSeriesMatchDate.
     */
    public final void setSeriesMatchDate(HODateTime m_clSeriesMatchDate) {
        this.m_clSeriesMatchDate = m_clSeriesMatchDate;
    }

    /**
     * Getter for property m_clSeriesMatchDate.
     *
     * @return Value of property m_clSeriesMatchDate.
     */
    public final HODateTime getSeriesMatchDate() {
        return m_clSeriesMatchDate;
    }

    /**
     * Setter for property m_clTrainingDate.
     *
     * @param m_clTrainingDate New value of property m_clTrainingDate.
     */
    public final void setTrainingDate(HODateTime m_clTrainingDate) {
        this.m_TrainingDate = m_clTrainingDate;
    }

    /**
     * Getter for property m_clTrainingDate.
     * This is the date for the next training.
     *
     * @return Value of property m_clTrainingDate.
     */
    public final HODateTime getNextTrainingDate() {
        return m_TrainingDate;
    }

    public HODateTime getTrainingDateAfterWeeks(int nWeek)
    {
        return m_TrainingDate.plusDaysAtSameLocalTime(nWeek* 7L);
    }

    public Integer getCountryId() {
        return m_iCountryId;
    }

    public void setCountryId(Integer m_iCountryId) {
        this.m_iCountryId = m_iCountryId;
    }

    public int getHrfId() {
        return this.hrfId;
    }

    public void setHrfId(int hrfId) {
        this.hrfId = hrfId;
    }
}
