package core.util

import core.db.PersistenceManager
import core.model.HOVerwaltung
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.util.*

class HOCurrencyTests {
    private lateinit var originalLocale: Locale

    @BeforeEach
    fun setGermanLocale() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.GERMANY)
    }

    @AfterEach
    fun restoreLocale() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun test() {
        // given
        val persistenceManager = mock(PersistenceManager::class.java)

        val hoVerwaltung = mock(HOVerwaltung::class.java)
        `when`(hoVerwaltung.persistenceManager).thenReturn(persistenceManager)

        mockStatic(HOVerwaltung::class.java).use { mockedStatic ->
            mockedStatic.`when`<HOVerwaltung> {
                HOVerwaltung.instance()
            }.thenReturn(hoVerwaltung)

            AmountOfMoney.setExchangeRate(BigDecimal.TEN)

            val amountOfMoney = AmountOfMoney(10)
            assertThat(amountOfMoney.toLocale()).isEqualTo(BigDecimal.ONE)

            val money = AmountOfMoney(50)
            assertThat(money.toLocale()).isEqualTo(BigDecimal.valueOf(5))

            val d = AmountOfMoney(amountOfMoney.swedishKrona + BigDecimal.valueOf(90))
            val nbsp = "\u00A0"
            assertThat(d.toLocaleString()).isEqualTo("10" + nbsp + "€")
        }
    }
}
