package core.util

import core.model.HOVerwaltung
import core.model.UserParameter
import java.text.NumberFormat

class HOCurrency(var swedishKrona: Int) {

    companion object {
        val currencyFactor  = 1.0 / UserParameter.instance().FXrate
        val currencyFormatter = getLocalCurrencyFormatter()

        private fun getLocalCurrencyFormatter(): NumberFormat {
            var ret = CurrencyUtils.getLeagueCurrencyFormater(HOVerwaltung.instance().getModel().getLeagueIdPremierTeam())
            ret.maximumFractionDigits = 0
            return ret
        }
    }

    /**
     * Rounding to the smallest monetary unit in hattrick, which are 100 swedish krona
     * and converting to the local currency
     */
    fun toLocale(): Int {
        return ((swedishKrona/100.0 + 0.5).toInt() * 100 * HOCurrency.currencyFactor).toInt()
    }

    fun toLocaleString(): String {
        return currencyFormatter.format(this.toLocale())
    }
}