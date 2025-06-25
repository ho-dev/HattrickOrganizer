package core.util

import core.file.xml.XMLTeamDetailsParser
import core.model.HOConfigurationParameter
import core.model.WorldDetailLeague
import core.net.MyConnector
import java.text.NumberFormat
import java.util.*

class AmountOfMoney(var swedishKrona: Long) {

    companion object {

        private var currencyCodes : Set<String>? = null
        var currencyCode = HOConfigurationParameter("CurrencyCode", null)
        private var currency : Currency? = null

        public fun getCurrencyCodes() : Set<String> {
            if (currencyCodes == null) {
                currencyCodes = emptySet()
                for (worldDetails in WorldDetailLeague.allLeagues) {
                    var currency = getCurrency(worldDetails)
                    if (currency != null) {
                        var currencyInfo = getCurrencyInfo(currency)
                        currencyCodes?.plus(currencyInfo)
                    }
                }
            }
            return currencyCodes!!
        }

        public fun getCurrency() : Currency?{
            if ( currency == null){
                currency = getCurrency(currencyCode.getValue())
            }
            return currency
        }

        private fun getCurrency(value: WorldDetailLeague) : Currency? {
            for (_currency in Currency.getAvailableCurrencies()) {
                if (_currency.symbol.equals(value.currencyName)) {
                    return _currency;
                }
            }
            return null;
        }

        private fun getCurrency(currencyCode: String?) : Currency?{
            if ( currencyCode != null) return Currency.getInstance(currencyCode)
            return null
        }

        var currencyFormatter = getNumberFormat(currencyCode.getValue())

        /**
         * Parse currency value from string.
         * If value could not be parsed with currency formal an number format is tried.
         * @param v String to parse from
         * @return Integer, null on parse error
         */
        fun parseCurrency(v: String?): AmountOfMoney? {
            try {
                return AmountOfMoney((currencyFormatter.parse(v).toDouble() * getExchangeRate()).toLong())
            } catch (ex: Exception) {
                HOLogger.instance().error(Helper::class.java, "error parsing currency " + ex)
                return null
            }
        }


        private fun getNumberFormat(currencyCode: String?) : NumberFormat? {
            if (currencyCode != null) {
                for (locale in NumberFormat.getAvailableLocales()) {
                    var ret = NumberFormat.getCurrencyInstance(locale);
                    if (ret.currency.currencyCode.equals(currencyCode)) return ret
                }
            }
            return null
        }

        var exchangeRate :  Double? = null

        private fun getExchangeRate(): Double {
            if ( exchangeRate == null) {
                if (currencyCode.getValue() == null) {
                    val teamProperties = MyConnector.instance().getTeamDetails(-1)
                    val teamInfoList = XMLTeamDetailsParser.getTeamInfoFromString(teamProperties)
                    for (info in teamInfoList) {
                        if (info.isPrimaryTeam) {
                            val countryId = info.countryId.toInt()
                            for (worldDetails in WorldDetailLeague.allLeagues) {
                                if (worldDetails.countryId.equals(countryId)) {
                                    exchangeRate = worldDetails.currencyRate
                                    initCurrency(worldDetails.currencyName)
                                    break
                                }
                            }
                            break
                        }
                    }
                }

                if (currency == null) {
                    currency = getCurrency(currencyCode.getValue())
                }

                if ( currency != null) {
                    currencyFormatter = getNumberFormat(currency!!.currencyCode)
                    for (worldDetails in WorldDetailLeague.allLeagues) {
                        if (worldDetails.currencyName.equals(currency!!.symbol)) {
                            exchangeRate = worldDetails.currencyRate
                            break;
                        }

                    }
                }

                if ( exchangeRate == null) exchangeRate = 1.0

            }
            return exchangeRate!!.toDouble()
        }

        private fun initCurrency(currencySymbol: String) {
            for (_currency in Currency.getAvailableCurrencies()) {
                if (_currency.symbol.equals(currencySymbol)) {
                    currency = _currency
                    currencyCode.setValue(currency?.currencyCode)
                    currencyFormatter = getNumberFormat(currency?.currencyCode)
                    break
                }
            }
        }

        public fun setCurrencyCode(inCurrencyCode: String){
            currencyCode.setValue(inCurrencyCode)
            currency = Currency.getInstance(currencyCode.getValue())
            currencyFormatter = getNumberFormat(currencyCode.getValue())
            exchangeRate = null
        }

        fun getSelectedCurrencyCode(): String? {
            var cur = getCurrency()
            if (cur!=null){
                return getCurrencyInfo(cur)
            }
            return null
        }

        private fun getCurrencyInfo(cur: Currency?): String? {
            if ( cur != null) {
                return cur.currencyCode + ": " + cur.displayName + " (" + cur.symbol + ")"
            }
            return null
        }

        public fun fromLocale(amount : Long) : AmountOfMoney{
            return AmountOfMoney((amount * getExchangeRate()).toLong());
        }
    }

    /**
     * Rounding to the smallest monetary unit in hattrick, which are 100 swedish krona
     * and converting to the local currency
     */
    fun toLocale(): Long {
        return ((swedishKrona/100.0 + 0.5).toLong() * 100 / getExchangeRate()).toLong()
    }

    fun toLocaleString(): String {
        return currencyFormatter?.format(this.toLocale()) ?: this.toLocale().toString()
    }

    fun addAmount(amountOfMoney: AmountOfMoney) {
        this.swedishKrona += amountOfMoney.swedishKrona
    }
}