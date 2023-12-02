package core.db

import core.model.misc.Basics
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

    companion object {
        @AfterAll
        @JvmStatic
        fun cleanUp() {
            val conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")
            conn.createStatement().execute("DROP TABLE BASICS")
        }
    }
}
