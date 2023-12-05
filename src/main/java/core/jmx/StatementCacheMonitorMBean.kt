package core.jmx

import core.db.StatementCache

interface StatementCacheMonitorMBean {
    fun getStatistics():Map<String, String>
    fun getCachedStatementCount(): Int

    fun setCacheEnabled(enabled: Boolean)
}
