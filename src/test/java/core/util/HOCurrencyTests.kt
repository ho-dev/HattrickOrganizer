package core.util

import core.model.HOModel
import core.model.HOVerwaltung
import core.model.UserParameter
import core.model.XtraData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class HOCurrencyTests {
    @Test
    fun test() {
        // Prepare model
        val hov = HOVerwaltung.instance()
        hov.model = HOModel(1)
        hov.model.xtraDaten = XtraData()
        hov.model.xtraDaten.countryId = 3
        UserParameter.instance().currencyRate = 10f

        val c = AmountOfMoney(10)
        Assertions.assertEquals(0, c.toLocale())

        val e = AmountOfMoney(50)
        Assertions.assertEquals(10, e.toLocale())

        val d = AmountOfMoney(c.swedishKrona + 90)
        val nbsp = "\u00A0"
        Assertions.assertEquals("10" + nbsp + "€", d.toLocaleString())
    }
}
