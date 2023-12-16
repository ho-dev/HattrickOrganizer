package core.db

import org.hsqldb.types.Types
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.DriverManager
import java.sql.SQLException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ConnectionManagerTest {
    private lateinit var connectionManager:ConnectionManager

    @BeforeEach
    fun setUp()  {
        val conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")
        connectionManager = ConnectionManager()
        connectionManager.connect(conn)
        conn.createStatement().execute("CREATE TABLE TEST (ID INT PRIMARY KEY, CONTENT VARCHAR(255))")
    }

    @Test
    fun testExecuteQueryThrowsSQLExceptionIfConnectionClosed() {
        val conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")
        connectionManager.connect(conn)
        conn.close()
        assertThrows(SQLException::class.java) { connectionManager.executeQuery("SELECT * FROM NOTHING") }
    }

    @Test
    fun testExecuteQueryThrowsIfSQLException() {
        assertThrows(SQLException::class.java) { connectionManager.executeQuery("SELECT * FROM NOTHING") }
    }

    @Test
    fun testExecuteQueryReturnsSelectResults() {
        val conn = connectionManager.connection
        conn!!.createStatement().executeUpdate("INSERT INTO TEST (ID, CONTENT) VALUES (1, 'Hello HO!')")

        val rs = connectionManager.executeQuery("SELECT * FROM TEST")
        assertTrue(rs!!.next())
        assertEquals("Hello HO!", rs.getString(2))
        rs.close()
    }

    @Test
    fun testExecuteUpdateThrowsExceptionIfConnectionClosed() {
        val conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")
        connectionManager.connect(conn)
        conn.close()
        assertThrows(SQLException::class.java) { connectionManager.executeUpdate("INSERT INTO NOTHING (ID, NO_NO) VALUES (NULL, NULL)") }
    }

    @Test
    fun testExecuteUpdateReturnsNumbersInserted() {
        assertEquals(1, connectionManager.executeUpdate("INSERT INTO TEST (ID, CONTENT) VALUES (1, 'Hello HO!')"))
        assertEquals(1, connectionManager.executeUpdate("INSERT INTO TEST (ID, CONTENT) VALUES (2, 'Hello HO!')"))
        assertEquals(1, connectionManager.executeUpdate("UPDATE TEST SET CONTENT = 'Hej HO!' WHERE ID = 1"))
        assertEquals(2, connectionManager.executeUpdate("UPDATE TEST SET CONTENT = 'Hallo HO!'"))
    }

    @Test
    fun testGetDbInfoReturnsMetadata() {
        val dbInfo = connectionManager.dbInfo
        assertEquals("TINYINT", dbInfo?.getTypeName(Types.TINYINT))
    }

    @Test
    fun testGetAllTableNamesReturnsAllTables() {
        val tableNames = connectionManager.getAllTableNames()
        assertEquals(1, tableNames.size)
        assertEquals("TEST", tableNames.first())
    }

    @Test
    fun testExecuteUpdateSQLExceptionReturnsZero() {
        assertThrows(SQLException::class.java) { connectionManager.executeUpdate("INSERT INTO TEST ") }
    }

    @Test
    fun testDisconnectUnsetsConnection() {
        connectionManager.disconnect()
        assertNull(connectionManager.connection)
    }

    @Test
    fun testExecutePreparedQueryReturnRecords() {
        val conn = connectionManager.connection
        conn!!.createStatement().executeUpdate("INSERT INTO TEST (ID, CONTENT) VALUES (1, 'Hello HO!')")

        val rs = connectionManager.executePreparedQuery("SELECT ID, CONTENT  FROM TEST WHERE ID = ? AND CONTENT LIKE ?", 1, "Hello%")
        assertNotNull(rs)
        assertTrue(rs!!.next())
        assertEquals("Hello HO!", rs.getString(2))
    }

    @Test
    fun testExecutePreparedUpdateReturnNumOfRecords() {
        val conn = connectionManager.connection
        conn!!.createStatement().executeUpdate("INSERT INTO TEST (ID, CONTENT) VALUES (1, 'Hello HO!')")

        val num = connectionManager.executePreparedUpdate("UPDATE TEST SET CONTENT = ? WHERE ID = ?", "Hallo HO!", 1)
        assertEquals(1, num)
    }

    @AfterEach
    fun cleanUp() {
        if (connectionManager.connection != null) {
            val conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")
            conn.createStatement().execute("DROP TABLE TEST")
        }
    }
}