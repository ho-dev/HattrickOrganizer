package core.util

import core.model.HOConfigurationIntParameter
import core.model.WorldDetailLeague
import core.model.WorldDetailsManager
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

/**
 * Amounts of money are stored in swedish krona and displayed in players' locale
 */
class AmountOfMoney(var swedishKrona: BigDecimal) {
    constructor(swedishKrona: Long) : this(BigDecimal.valueOf(swedishKrona, 0))

    /**
     * The companion object handles the aspects of currency and formatting settings
     */
    companion object {

        /**
         * List of available currencies
         */
        private var currencyInfo = HashSet<String>()

        /**
         * The currency setting (country id)
         * It is set either by the first download with the currency code of the premier team
         * or by editing the currency settings in the options dialog.
         */
        private var currencyCountryId = HOConfigurationIntParameter("CurrencyCountryId")

        /**
         * Currency formatter
         */
        private var currencyFormatter: NumberFormat? = null

        /**
         * Exchange rate between swedish krona and selected currency
         */
        private var exchangeRate: BigDecimal? = null

        /**
         * Get the list of currency info from the list of leagues in world detail file
         * Hattrick international is removed from the list.
         */
        fun getCurrencyInfo(): Set<String> {
            if (currencyInfo.isEmpty()) {
                for (worldDetails in WorldDetailsManager.instance().leagues) {
                    if (worldDetails.countryId < 1000) {
                        val info = getCurrencyInfo(worldDetails)
                        if ( info != null ) currencyInfo.add(info)
                    }
                }
            }
            return currencyInfo
        }

        /**
         * Parse currency value from string.
         * If value could not be parsed with currency formal an number format is tried.
         * @param v String to parse from
         * @return Integer, null on parse error
         */
        fun parse(v: String?): AmountOfMoney? {
            var amount: Number?
            try {
                amount = currencyFormatter?.parse(v)
            } catch (_: Exception) {
                try {
                    amount = Helper.getNumberFormat(0).parse(v)
                } catch (ex: Exception) {
                    HOLogger.instance().error(Helper::class.java, "error parsing currency " + ex)
                    return null
                }
            }
            return AmountOfMoney(BigDecimal(amount?.toDouble() ?: 0.0))
        }

        /**
         * Get the currency formatter object
         */
        private fun getCurrencyFormatter(): NumberFormat {
            if (this.currencyFormatter == null) {
                val countryId = currencyCountryId.getIntValue()
                if (countryId != null) {
                    val worldDetailLeague = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(countryId)
                    for (locale in NumberFormat.getAvailableLocales()) {
                        val ret = NumberFormat.getCurrencyInstance(locale)
                        if (ret.currency.getSymbol().equals(worldDetailLeague.currencyName) || locale.country.equals(worldDetailLeague.countryCode)) {
                            this.currencyFormatter = ret
                            return ret
                        }
                    }
                }
                this.currencyFormatter = NumberFormat.getCurrencyInstance()
            }
            return this.currencyFormatter!!
        }

        /**
         * Get the exchange rate between internal swedish krona value and currency setting
         */
         fun getExchangeRate(): BigDecimal {
            if (exchangeRate == null) {
                var countryId = currencyCountryId.getIntValue()
                if ( countryId == null){
                    val worldDetailLeague = WorldDetailLeague.getWorldDetailsLeagueOfPremierTeam()
                    if (worldDetailLeague != null){
                        countryId = worldDetailLeague.countryId
                        currencyCountryId.setIntValue(countryId)
                    }
                }
                if (countryId != null) {
                    val worldDetailLeague = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(countryId)
                    if ( worldDetailLeague != null) {
                        exchangeRate = BigDecimal.valueOf(worldDetailLeague.currencyRate)
                    }
                }
                if (exchangeRate == null) return BigDecimal(1)
            }
            return exchangeRate!!
        }

        /**
         * Set currency.
         * All other currency settings are reset if the new value differs from the current value.
         */
        fun setCurrencyCountry(inCurrencyInfo: String) : Boolean {
            if ( inCurrencyInfo.contains("(")){
                val countryCode = inCurrencyInfo.substringAfter("(").substringBefore(")")
                for (country in WorldDetailsManager.instance().leagues){
                    if ( country.countryCode.equals(countryCode)){
                        currencyCountryId.setIntValue(country.countryId)
                        currencyFormatter = null
                        exchangeRate = null
                        return true
                    }
                }
            }
            return false
        }

        /**
         * Get a display string of the current currency setting.
         */
        fun getSelectedCurrencyCode(): String? {
            val worldDetailLeague = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(currencyCountryId.getIntValue())
            if (worldDetailLeague != null) {
                return getCurrencyInfo(worldDetailLeague)
            }
            return null
        }

        /**
         * Format the currency display string, containing the country name, currency name and the country code
         */
        private fun getCurrencyInfo(worldDetails: WorldDetailLeague?): String? {
            if (worldDetails != null) {
                return worldDetails.countryName + " - " + worldDetails.currencyName + " (" + worldDetails.countryCode + ")"
            }
            return null
        }

        /**
         * Get the currency name (symbol) of current setting
         */
        fun getCurrencyName() : String {
            val worldDetailLeague = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(currencyCountryId.getIntValue())
            if (worldDetailLeague != null) {
                return worldDetailLeague.currencyName
            }
            return ""

        }

    }

    /**
     * Convert to the local currency
     */
    fun toLocale(): BigDecimal {
        return this.swedishKrona / getExchangeRate()
    }

    /**
     * Format the amount to a locale display string
     */
    @JvmOverloads
    fun toLocaleString( decimals : Int = 0): String {
        val formatter =  getCurrencyFormatter()
        formatter.maximumFractionDigits=decimals
        formatter.minimumFractionDigits=decimals
        return formatter.format(this.toLocale())
    }

    /**
     * Add an amount to the current value
     */
    fun add(amountOfMoney: AmountOfMoney) {
        this.swedishKrona += amountOfMoney.swedishKrona
    }

    /**
     * Subtract an amount from the current value
     */
    fun subtract(amountOfMoney: AmountOfMoney) {
        this.swedishKrona -= amountOfMoney.swedishKrona
    }

    /**
     * Return the sum of 2 amounts.
     */
    fun plus(amount: AmountOfMoney): AmountOfMoney {
        return AmountOfMoney(this.swedishKrona + amount.swedishKrona)
    }

    /**
     * Return the difference of 2 amounts.
     */
    fun minus(amount: AmountOfMoney): AmountOfMoney {
        return AmountOfMoney(this.swedishKrona - amount.swedishKrona)
    }

    /**
     * Return the product of 2 amounts.
     */
    fun times(factor: BigDecimal): AmountOfMoney {
        return AmountOfMoney(this.swedishKrona.times(factor))
    }

    /**
     * Return the division of 2 amounts
     */
    fun divide(divisor: BigDecimal): AmountOfMoney {
        try {
            val amount = this.swedishKrona.divide(divisor, RoundingMode.HALF_UP)
            return AmountOfMoney(amount)
        }catch (_ : Exception){
            val d = this.swedishKrona.toDouble() / divisor.toDouble()
            return AmountOfMoney(BigDecimal.valueOf(d))
        }
    }

    fun divide(divisor: AmountOfMoney): BigDecimal {
        return this.swedishKrona.divide(divisor.swedishKrona, 2, RoundingMode.HALF_UP)
    }

    /**
     * Returns true if the given amounts are equal
     */
    fun equals(other: AmountOfMoney): Boolean {
        return this.swedishKrona.equals(other.swedishKrona)
    }

    /**
     * Returns true if the current amount is greater than the given one.
     */
    fun isGreaterThan(i: AmountOfMoney): Boolean {
        return this.swedishKrona.compareTo(i.swedishKrona) == 1
    }

    /**
     * Returns true if the current amount is less than the given one
     */
    fun isLessThan(i: AmountOfMoney): Boolean {
        return this.swedishKrona.compareTo(i.swedishKrona) == -1
    }
}