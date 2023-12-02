package core.jmx

import core.db.DBManager

class StatementCacheMonitor: StatementCacheMonitorMBean {
    override fun getStatistics(): Map<String, String> {
        val connectionManager = DBManager.instance().connectionManager
        return connectionManager.statementCache.statementStats.map {
            entry -> entry.key to entry.value.toString()
        }.toMap()
    }

    override fun getCachedStatementCount(): Int {
        val connectionManager = DBManager.instance().connectionManager
        return connectionManager.statementCache.statementStats.size
    }

    override fun setCacheEnabled(enabled: Boolean) {
        val connectionManager = DBManager.instance().connectionManager
        connectionManager.statementCache.cachedEnabled = enabled
    }
}
