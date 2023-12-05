package core.db

import core.util.HOLogger
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.Instant
import java.util.*

/**
 * Cache for [PreparedStatement]s instances.
 *
 *
 * This cache tracks statistics about the various prepared statements:
 *
 *  * Creation timestamp,
 *  * Last access timestamp,
 *  * NUmber of accesses.
 *
 *
 *
 * The cache can be disabled by setting `cachedEnabled` to `false`.  When the cache
 * is disabled, the existing entries are closed and evicted, the stats dumped and cleared.  The cache can
 * be enabled or disabled via JMX in development mode.  By default, the cache is on.
 */
class StatementCache(private val connectionManager: ConnectionManager) {
    var cachedEnabled = true
        set(enabled) {
            field = enabled
            HOLogger.instance().info(StatementCache::class.java, "Cache enabled = $enabled")
            if (!field) {
                clearCache()
            }
        }

    private val cache = Collections.synchronizedMap(HashMap<String, PreparedStatement?>())

    val statementStats: MutableMap<String, CachedStatementStats> = Collections.synchronizedMap(HashMap())

    private fun getFromCache(query: String): PreparedStatement? {
        if (cachedEnabled) {
            val statement = cache[query]
            if (statement != null) {
                val stats = statementStats[query]
                statementStats[query] = CachedStatementStats(stats!!.created, Instant.now(), stats.count + 1)
                return statement
            }
        }
        return null
    }

    private fun createStatement(query: String): PreparedStatement? {
        var statement: PreparedStatement? = null
        try {
            statement = connectionManager.connection.prepareStatement(query)
            if (cachedEnabled) {
                cache[query] = statement
                statementStats[query] = CachedStatementStats(Instant.now(), Instant.now(), 1)
            }
        } catch (e: SQLException) {
            HOLogger.instance().error(
                StatementCache::class.java, """Error creating statement: $query
 Error: ${e.message}"""
            )
        }
        return statement
    }

    fun getPreparedStatement(query: String): PreparedStatement? {
        var statement = getFromCache(query)
        if (statement == null) {
            statement = createStatement(query)
        }
        return statement
    }

    fun clearCache() {
        for ((key, value) in cache) {
            try {
                value!!.close()
            } catch (e: SQLException) {
                HOLogger.instance().error(
                    StatementCache::class.java,
                    """Error closing prepared statement: $key
 ${e.message}"""
                )
            }
        }
        cache.clear()
        dumpStats()
        statementStats.clear()
    }

    fun dumpStats() {
        for ((key, value) in statementStats) {
            HOLogger.instance().info(StatementCache::class.java, "$key: $value")
        }
    }

    @JvmRecord
    data class CachedStatementStats(val created: Instant, val lastAccessed: Instant, val count: Int)
}
