package core.util

import core.model.HOConfigurationParameter
import core.model.HOVerwaltung
import core.model.WorldDetailLeague
import core.model.WorldDetailsManager
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class AmountOfMoney(var swedishKrona: BigDecimal) {
    constructor(swedishKrona: Long) : this(BigDecimal.valueOf(swedishKrona))

    companion object {

        private var currencyCodes = HashSet<String>()
        private var currencyCode = HOConfigurationParameter("CurrencyCode", null)
        private var currency: Currency? = null
        private var currencyFormatter: NumberFormat? = null

        fun getCurrencyCodes(): Set<String> {
            if (currencyCodes.isEmpty()) {
                for (worldDetails in WorldDetailsManager.instance().leagues) {
                    val currency = getCurrency(worldDetails)
                    if (currency != null) {
                        val currencyInfo = getCurrencyInfo(currency)
                        if ( currencyInfo != null) {
                            currencyCodes.add(currencyInfo)
                        }
                    }
                }
            }
            return currencyCodes!!
        }

        private fun getCurrency(worldDetailLeague: WorldDetailLeague): Currency? {
            for (_currency in Currency.getAvailableCurrencies()) {
                val symbol = _currency.symbol;
                if (symbol.equals(worldDetailLeague.currencyName)) {
                    return _currency
                }
            }
            for (isoCountry in Locale.getISOCountries()){
                var locale = Locale("en", isoCountry)
                if (locale.country.equals(worldDetailLeague.countryCode)){
                    return Currency.getInstance(locale)
                }
            }

            return null
        }

        fun getWorldDetailsLeague() : WorldDetailLeague{
            val countryId = HOVerwaltung.instance().model.xtraDaten.countryId
            return WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(countryId)
        }

        fun getCurrencyCode(): String {
            if (currencyCode.getValue() == null) {
                val worldDetailLeague = getWorldDetailsLeague()
                for (_currency in Currency.getAvailableCurrencies()) {
                    if (_currency.symbol.equals(worldDetailLeague.currencyName)) {
                        currencyCode.setValue(_currency?.currencyCode)
                        return currencyCode.getValue()!!
                    }
                }
                currencyCode.setValue(NumberFormat.getCurrencyInstance().currency.currencyCode)
            }
            return currencyCode.getValue()!!
        }

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

        private var exchangeRate: BigDecimal? = null

        private fun getExchangeRate(): BigDecimal {
            if (exchangeRate == null) {
                val curr = getCurrency()
                if (curr != null) {
                    var worldDetailLeague = getWorldDetailsLeague()
                    if (!worldDetailLeague.currencyName.equals(curr.symbol)){
                        worldDetailLeague = WorldDetailsManager.instance().getWorldDetailsByCurrencySymbol(curr.symbol)
                    }
                    exchangeRate = BigDecimal.valueOf(worldDetailLeague.currencyRate)
                }
                if (exchangeRate == null) exchangeRate = BigDecimal(1)
            }
            return exchangeRate!!
        }

        fun setCurrencyCode(inCurrencyCode: String) {
            if (!inCurrencyCode.equals(this.currencyCode.getValue())) {
                currencyCode.setValue(inCurrencyCode)
                currency = null
                currencyFormatter = null
                exchangeRate = null
            }
        }

        fun getSelectedCurrencyCode(): String? {
            val cur = getCurrency()
            if (cur != null) {
                return getCurrencyInfo(cur)
            }
            return null
        }

        private fun getCurrencyInfo(cur: Currency?): String? {
            if (cur != null) {
                return cur.currencyCode + ": " + cur.displayName + " (" + cur.symbol + ")"
            }
            return null
        }

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

    @JvmOverloads
    fun toLocaleString( decimals : Int = 0): String {
        val formatter =  getCurrencyFormatter()
        formatter.maximumFractionDigits=decimals
        formatter.minimumFractionDigits=decimals
        return formatter.format(this.toLocale())
    }

    fun add(amountOfMoney: AmountOfMoney) {
        this.swedishKrona += amountOfMoney.swedishKrona
    }

    fun subtract(amountOfMoney: AmountOfMoney) {
        this.swedishKrona -= amountOfMoney.swedishKrona
    }

    fun plus(amount: AmountOfMoney): AmountOfMoney {
        return AmountOfMoney(this.swedishKrona + amount.swedishKrona)
    }

    fun minus(amount: AmountOfMoney): AmountOfMoney {
        return AmountOfMoney(this.swedishKrona - amount.swedishKrona)
    }

    fun times(factor: BigDecimal): AmountOfMoney {
        return AmountOfMoney(this.swedishKrona.times(factor))
    }

    fun divide(divisor: BigDecimal): AmountOfMoney {
        try {
            var amount = this.swedishKrona.divide(divisor)
            return AmountOfMoney(amount)
        }catch (_ : Exception){
            var d = this.swedishKrona.toDouble() / divisor.toDouble()
            return AmountOfMoney(BigDecimal.valueOf(d))
        }
    }

    fun divide(divisor: AmountOfMoney): BigDecimal {
        return this.swedishKrona.divide(divisor.swedishKrona)
    }

    fun equals(other: AmountOfMoney): Boolean {
        return this.swedishKrona.equals(other.swedishKrona)
    }

    fun isGreaterThan(i: AmountOfMoney): Boolean {
        return this.swedishKrona.compareTo(i.swedishKrona) == 1
    }

    fun isLessThan(i: AmountOfMoney): Boolean {
        return this.swedishKrona.compareTo(i.swedishKrona) == -1
    }
}