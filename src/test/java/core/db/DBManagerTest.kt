package core.db

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DBManagerTest {

    @Test
    fun testGetPlaceholders() {
        Assertions.assertEquals("", DBManager.getPlaceholders(0))
        Assertions.assertEquals("?", DBManager.getPlaceholders(1))
        Assertions.assertEquals("?,?", DBManager.getPlaceholders(2))
    }
}