package core.db

import core.model.misc.Basics
import core.util.HODateTime
import org.junit.jupiter.api.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BasicsTableTest {
    private lateinit var conn:Connection
    private lateinit var basicsTable:BasicsTable

    @BeforeAll
    fun setUp()  {
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")
        val connectionManager = ConnectionManager()
        connectionManager.connect(conn)

        basicsTable = BasicsTable(connectionManager)
        basicsTable.createTable()
    }

    @Test
    fun testCreateIndexStatementCreatesCorrectStatement() {
        val indices = basicsTable.createIndexStatement
        Assertions.assertNotNull(indices)
        Assertions.assertEquals(1, indices.size)
        Assertions.assertEquals("CREATE INDEX IBASICS_2 ON BASICS(Datum)", indices[0])
    }

    @Test
    fun testSaveBasicsStoresRecord() {
        val basics = Basics()
        basicsTable.saveBasics(42, basics)

        val stmt = conn.createStatement()
        val rs:ResultSet? = stmt.executeQuery("SELECT * FROM BASICS")
        if (rs?.next() == true) {
            val id = rs.getInt("HRF_ID")
            Assertions.assertEquals(42, id)
        } else {
            Assertions.fail("Record not stored")
        }
        rs?.close()
        stmt.close()
    }

    /** #2587: Sunday of the season break - the league is in season 96, HO's calendar still in 95. */
    private fun seasonBreakBasics(seasonOffset: Int?): Basics {
        val basics = Basics()
        basics.datum = HODateTime.fromHT("2026-11-08 12:00:00")
        basics.season = 96
        basics.setSeasonOffset(seasonOffset)
        return basics
    }

    @Test
    fun testLoadBasicsKeepsStoredZeroSeasonOffset() {
        basicsTable.saveBasics(43, seasonBreakBasics(0))

        val loaded = basicsTable.loadBasics(43)
        Assertions.assertEquals(0, loaded.storedSeasonOffset)
    }

    @Test
    fun testLoadBasicsDerivesMissingSeasonOffset() {
        basicsTable.saveBasics(44, seasonBreakBasics(null))

        val loaded = basicsTable.loadBasics(44)
        Assertions.assertEquals(1, loaded.storedSeasonOffset)
    }

    companion object {
        @AfterAll
        @JvmStatic
        fun cleanUp() {
            val conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")
            conn.createStatement().execute("DROP TABLE BASICS")
        }
    }
}
