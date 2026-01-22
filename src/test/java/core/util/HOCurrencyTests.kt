package core.util

import core.model.HOModel
import core.model.HOVerwaltung
import core.model.UserParameter
import core.model.XtraData
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Locale

class HOCurrencyTests {
    var currentLocale: Locale = Locale.getDefault()

    @BeforeEach
    fun setup() {
        Locale.setDefault(Locale.GERMANY)
    }

    @Test
    fun test() {
        // Prepare model
        val hov = HOVerwaltung.instance()
        hov.model = HOModel(1)
        hov.model.xtraDaten = XtraData()
        hov.model.xtraDaten.countryId = 3
        UserParameter.instance().currencyRate = 10f

        val amountOfMoney = AmountOfMoney(10)
        AmountOfMoney.setExchangeRate(BigDecimal.valueOf(10))
        Assertions.assertEquals(BigDecimal.valueOf(1), amountOfMoney.toLocale())

        val e = AmountOfMoney(50)
        Assertions.assertEquals(BigDecimal.valueOf(5), e.toLocale())

        val d = AmountOfMoney(amountOfMoney.swedishKrona + BigDecimal.valueOf(90))
        val nbsp = "\u00A0"
        Assertions.assertEquals("10" + nbsp + "€", d.toLocaleString())
    }

    @AfterEach
    fun tearDown() {
        Locale.setDefault(currentLocale)
    }
}
