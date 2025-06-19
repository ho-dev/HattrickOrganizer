package core.util

import core.file.xml.XMLTeamDetailsParser
import core.model.HOConfigurationParameter
import core.model.WorldDetailLeague
import core.net.MyConnector
import core.net.OnlineWorker
import java.text.NumberFormat
import java.util.*

class AmountOfMoney(var swedishKrona: Int) {

    companion object {

        var currencyCode = HOConfigurationParameter("CurrencyCode", null)
        var currency = getCurrency(currencyCode.getValue())

        private fun getCurrency(value: String?) : Currency?{
            if ( value != null) return Currency.getInstance(currencyCode.getValue())
            return null
        }

        var currencyFormatter = getNumberFormat(currencyCode.getValue())

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
                    currencyCode.setValue(currency.currencyCode)
                    currencyFormatter = getNumberFormat(currency.currencyCode)
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
    }

    /**
     * Rounding to the smallest monetary unit in hattrick, which are 100 swedish krona
     * and converting to the local currency
     */
    fun toLocale(): Int {
        return ((swedishKrona/100.0 + 0.5).toInt() * 100 / getExchangeRate()).toInt()
    }

    fun toLocaleString(): String {
        return currencyFormatter?.format(this.toLocale()) ?: this.toLocale().toString()
    }
}