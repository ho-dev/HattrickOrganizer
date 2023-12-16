package core.db

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.sql.Connection
import java.sql.DriverManager
import java.time.Instant
import java.time.temporal.ChronoUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatementCacheTest {
    private lateinit var conn: Connection
    private lateinit var statementCache: StatementCache

    @BeforeAll
    fun setUp()  {
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")
        val connectionManager = ConnectionManager()
        connectionManager.connect(conn)
        conn.createStatement().execute("CREATE TABLE TEST (ID INT PRIMARY KEY, CONTENT VARCHAR(255))")

        statementCache = StatementCache(connectionManager)
    }

    @Test
    fun testGetPreparedStatementRetrievesStatementFromCacheWhenEnabled() {
        statementCache.cachedEnabled = true
        val stmt = statementCache.getPreparedStatement("INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)")
        val stats = statementCache.statementStats
        assertEquals(1, stats.count())

        val otherStmt = statementCache.getPreparedStatement("INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)")
        assertTrue(stmt == otherStmt)
        assertEquals(1, stats.count())
    }

    @Test
    fun testGetPreparedStatementCreatesNewStatementWhenCacheNotEnabled() {
        statementCache.cachedEnabled = false
        val stmt = statementCache.getPreparedStatement("INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)")
        val stats = statementCache.statementStats
        assertEquals(0, stats.count())

        val otherStmt = statementCache.getPreparedStatement("INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)")
        assertTrue(stmt != otherStmt)
        assertEquals(0, stats.count())
    }

    @Test
    fun testCacheGetsClearedWhenDisablingIt() {
        statementCache.cachedEnabled = true
        statementCache.getPreparedStatement("INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)")
        val stats = statementCache.statementStats
        assertEquals(1, stats.count())

        statementCache.cachedEnabled = false
        assertEquals(0, stats.count())
    }

    @Test
    fun testStatsTrackDetailsAboutStatements() {
        val stmt = "INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)"
        statementCache.cachedEnabled = true
        statementCache.getPreparedStatement(stmt)
        val stats = statementCache.statementStats
        assertEquals(1, stats.count())

        val rec = statementCache.statementStats[stmt]
        assertTrue(ChronoUnit.SECONDS.between(rec!!.created, Instant.now()) < 1)
        assertTrue(ChronoUnit.SECONDS.between(rec.lastAccessed, Instant.now()) < 1)
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
