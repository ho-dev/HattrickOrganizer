package core.db

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.DriverManager
import java.sql.Types

internal class DBInfoTest {
    @Test
    fun testGetTypeNameReturnsCorrectString() {
        val dbInfo = DBInfo(null)
        assertEquals("BOOLEAN", dbInfo.getTypeName(Types.BOOLEAN))
        assertEquals("BIT", dbInfo.getTypeName(Types.BIT))
        assertEquals("INTEGER", dbInfo.getTypeName(Types.INTEGER))
        assertEquals("CHAR", dbInfo.getTypeName(Types.CHAR))
        assertEquals("DATE", dbInfo.getTypeName(Types.DATE))
        assertEquals("DECIMAL", dbInfo.getTypeName(Types.DECIMAL))
        assertEquals("DOUBLE", dbInfo.getTypeName(Types.DOUBLE))
        assertEquals("FLOAT", dbInfo.getTypeName(Types.FLOAT))
        assertEquals("LONGVARCHAR", dbInfo.getTypeName(Types.LONGVARCHAR))
        assertEquals("REAL", dbInfo.getTypeName(Types.REAL))
        assertEquals("SMALLINT", dbInfo.getTypeName(Types.SMALLINT))
        assertEquals("TIME", dbInfo.getTypeName(Types.TIME))
        assertEquals("TIMESTAMP", dbInfo.getTypeName(Types.TIMESTAMP))
        assertEquals("TINYINT", dbInfo.getTypeName(Types.TINYINT))
        assertEquals("VARCHAR", dbInfo.getTypeName(Types.VARCHAR))
        assertEquals("", dbInfo.getTypeName(42))
    }

    @Test
    fun testGetAllTableNames() {
        val conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")
        conn.createStatement().execute("CREATE TABLE TEST (ID INTEGER PRIMARY KEY)")

        val dbInfo = DBInfo(conn.metaData)
        val tableNames = dbInfo.getAllTablesNames()
        assertEquals(1, tableNames.size)
        assertEquals("TEST", tableNames[0])

        conn?.close()
    }

    companion object {
        @AfterAll
        @JvmStatic
        fun cleanUp() {
            val conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")
            conn.createStatement().execute("DROP TABLE TEST")
        }
    }
}
