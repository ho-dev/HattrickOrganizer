package core.util

import core.model.HOConfigurationParameter
import core.model.WorldDetailLeague
import core.model.WorldDetailsManager
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_HALF_UP
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
         * List of available currency codes
         */
        private var currencyCodes = HashSet<String>()

        /**
         * The currency code setting
         * It is set either by the first download with the currency code of the premier team
         * or by editing the currency settings in the options dialog.
         */
        private var currencyCode = HOConfigurationParameter("CurrencyCode", null)

        /**
         * The selected currency
         */
        private var currency: Currency? = null

        /**
         * Currency formatter
         */
        private var currencyFormatter: NumberFormat? = null

        /**
         * Exchange rate between swedish krona and selected currency
         */
        private var exchangeRate: BigDecimal? = null

        /**
         * Get the list of currency code infos from the list of leagues in world detail file
         * Hattrick international is removed from the list.
         */
        fun getCurrencyCodes(): Set<String> {
            if (currencyCodes.isEmpty()) {
                for (worldDetails in WorldDetailsManager.instance().leagues) {
                    if (worldDetails.countryId < 1000) {
                        val currency = getCurrency(worldDetails)
                        if (currency != null) {
                            val currencyInfo = getCurrencyInfo(currency)
                            if (currencyInfo != null) {
                                currencyCodes.add(currencyInfo)
                            }
                        }
                    }
                }
            }
            return currencyCodes
        }

        /**
         * Get the currency object belonging to the given world details league
         * First search goes through the available currencies trying to find a match between leagues currency name an currency's symbol.
         * If no match is found the list of ISO countries is search for a match of country code.
         */
        private fun getCurrency(worldDetailLeague: WorldDetailLeague): Currency? {
            for (_currency in Currency.getAvailableCurrencies()) {
                val symbol = _currency.symbol
                if (symbol.equals(worldDetailLeague.currencyName)) {
                    return _currency
                }
            }
            for (isoCountry in Locale.getISOCountries()){
                val locale = Locale("en", isoCountry)
                if (locale.country.equals(worldDetailLeague.countryCode)){
                    return Currency.getInstance(locale)
                }
            }

            return null
        }

        /**
         * Get the currency code setting.
         * If not initialized before, the currency code is examined from the league of the current team.
         */
        fun getCurrencyCode(): String {
            if (currencyCode.getValue() == null || currencyCode.getValue()?.isEmpty() == true) {
                val worldDetailLeague = WorldDetailLeague.getWorldDetailsLeagueOfPremierTeam()
                if (worldDetailLeague != null) {
                    for (_currency in Currency.getAvailableCurrencies()) {
                        if (_currency.symbol.equals(worldDetailLeague.currencyName)) {
                            currencyCode.setValue(_currency?.currencyCode)
                            return currencyCode.getValue()!!
                        }
                    }
                }
                currencyCode.setValue(NumberFormat.getCurrencyInstance().currency.currencyCode)
            }
            return currencyCode.getValue()!!
        }

        /**
         * Get the currency object setting
         * If not initialized before, the currency is examined from the league of the current team.
         */
        fun getCurrency(): Currency? {
            if ( this.currency == null) {
                val code = this.getCurrencyCode()
                this.currency = Currency.getInstance(code)
            }
            return this.currency
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
                val currencyCode = getCurrencyCode()
                for (locale in NumberFormat.getAvailableLocales()) {
                    val ret = NumberFormat.getCurrencyInstance(locale)
                    if (ret.currency.currencyCode.equals(currencyCode)) {
                        this.currencyFormatter = ret
                        return ret
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
                val curr = getCurrency()
                if (curr != null) {
                    var worldDetailLeague = WorldDetailLeague.getWorldDetailsLeagueOfPremierTeam()
                    if ( worldDetailLeague != null) {
                        if (!worldDetailLeague.currencyName.equals(curr.symbol)) {
                            worldDetailLeague =
                                WorldDetailsManager.instance().getWorldDetailsByCurrencySymbol(curr.symbol)
                        }
                        exchangeRate = BigDecimal.valueOf(worldDetailLeague.currencyRate)
                    }
                }
                if (exchangeRate == null) return BigDecimal(1)
            }
            return exchangeRate!!
        }

        /**
         * Set currency code.
         * All other currency settings are resetted if the new value differs from the current value.
         */
        fun setCurrencyCode(inCurrencyCode: String) : Boolean {
            var code = inCurrencyCode
            if ( code.contains(":")){
                code = inCurrencyCode.substringBefore(":")
            }
            if (!code.equals(this.currencyCode.getValue())) {
                currencyCode.setValue(code)
                currency = null
                currencyFormatter = null
                exchangeRate = null
                return true
            }
            return false
        }

        /**
         * Get a display string of the current currency code setting.
         */
        fun getSelectedCurrencyCode(): String? {
            val cur = getCurrency()
            if (cur != null) {
                return getCurrencyInfo(cur)
            }
            return null
        }

        /**
         * Format the currency display string, containing the code, display name and the symbol
         */
        private fun getCurrencyInfo(cur: Currency?): String? {
            if (cur != null) {
                return cur.currencyCode + ": " + cur.displayName + " (" + cur.symbol + ")"
            }
            return null
        }

        /**
         * Transform a locale amount to the internal one
         */
        fun fromLocale(amount: BigDecimal): AmountOfMoney {
            return AmountOfMoney(amount.times(getExchangeRate()))
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