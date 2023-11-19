package core.model

import core.db.AbstractTable.Storable
import core.util.HODateTime
import org.apache.commons.lang3.math.NumberUtils
import java.util.*

class XtraData(properties: Properties) : Storable() {
    /**
     * Getter for property m_sLogoURL.
     *
     * @return Value of property m_sLogoURL.
     */
    var logoURL: String? = null
    /**
     * Getter for property m_clEconomyDate.
     *
     * @return Value of property m_clEconomyDate.
     */
    /**
     * Setter for property m_clEconomyDate.
     *
     * @param m_clEconomyDate New value of property m_clEconomyDate.
     */
    var economyDate: HODateTime? = null
    /**
     * Getter for property m_clSeriesMatchDate.
     *
     * @return Value of property m_clSeriesMatchDate.
     */
    /**
     * Setter for property m_clSeriesMatchDate.
     *
     * @param m_clSeriesMatchDate New value of property m_clSeriesMatchDate.
     */
    var seriesMatchDate: HODateTime? = null

    /**
     * Getter for property m_clTrainingDate.
     * This is the date for the next training.
     *
     * @return Value of property m_clTrainingDate.
     */
    var nextTrainingDate: HODateTime? = null

    /**
     * Getter for property m_bHasPromoted.
     *
     * @return Value of property m_bHasPromoted.
     */
    /**
     * Setter for property m_bHasPromoted.
     *
     * @param m_bHasPromoted New value of property m_bHasPromoted.
     */
    var isHasPromoted = false
    /**
     * Getter for property m_dCurrencyRate.
     *
     * @return Value of property m_dCurrencyRate.
     */
    /**
     * Setter for property m_dCurrencyRate.
     *
     * @param m_dCurrencyRate New value of property m_dCurrencyRate.
     */
    var currencyRate = -1.0

    /**
     * Id of the user's premier team
     * (same currency with all teams of the user)
     */
    var countryId: Int? = null
    /**
     * Getter for property m_iLeagueLevelUnitID.
     *
     * @return Value of property m_iLeagueLevelUnitID.
     */
    /**
     * Setter for property m_iLeagueLevelUnitID.
     *
     * @param m_iLeagueLevelUnitID New value of property m_iLeagueLevelUnitID.
     */
    /**
     * The ID number of the LeagueLevelUnit.
     * In week 16 of a season this ID switches to the value of the next season (could be a different one)
     */
    var leagueLevelUnitID: Int? = -1
    var hrfId = 0
    //~ Constructors -------------------------------------------------------------------------------
    init {
        currencyRate = NumberUtils.toDouble(properties.getProperty("currencyrate"), 1.0)
        countryId = getInteger(properties, "countryid", null)
        isHasPromoted = properties.getProperty("haspromoted", "FALSE").toBoolean()
        logoURL = properties.getProperty("logourl", "")
        seriesMatchDate = HODateTime.fromHT(properties.getProperty("seriesmatchdate"))
        economyDate = HODateTime.fromHT(properties.getProperty("economydate"))
        nextTrainingDate = HODateTime.fromHT(properties.getProperty("trainingdate"))
        leagueLevelUnitID = getInteger(properties, "leaguelevelunitid", -1)
    }

    private fun getInteger(properties: Properties, key: String, def: Int?): Int? {
        try {
            return properties.getProperty(key).toInt()
        } catch (ignored: Exception) {
        }
        return def
    }

    //~ Methods ------------------------------------------------------------------------------------
    /**
     * Setter for property m_clTrainingDate.
     *
     * @param m_clTrainingDate New value of property m_clTrainingDate.
     */
    fun setTrainingDate(m_clTrainingDate: HODateTime?) {
        nextTrainingDate = m_clTrainingDate
    }

    fun getTrainingDateAfterWeeks(nWeek: Int): HODateTime {
        return nextTrainingDate!!.plusDaysAtSameLocalTime(nWeek * 7L)
    }
}
