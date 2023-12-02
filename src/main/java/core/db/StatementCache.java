package core.db;

import core.util.HOLogger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StatementCache {
    private final ConnectionManager connectionManager;

    private boolean cachedEnabled = true;

    private final Map<String, PreparedStatement> cache = Collections.synchronizedMap(new HashMap<>());

    private final Map<String, CachedStatementStats> statementStats = Collections.synchronizedMap(new HashMap<>());

    public StatementCache(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void setCachedEnabled(boolean enabled) {
        cachedEnabled = enabled;
    }

    private PreparedStatement getFromCache(String query) {
        if (cachedEnabled) {
            PreparedStatement statement = cache.get(query);
            if (statement != null) {
                CachedStatementStats stats = statementStats.get(query);
                statementStats.put(query, new CachedStatementStats(stats.created, Instant.now(), stats.count + 1));
                return statement;
            }
        }
        return null;
    }

    private PreparedStatement createStatement(String query) {
        PreparedStatement statement = null;
        try {
            statement = connectionManager.connection.prepareStatement(query);
            if (cachedEnabled) {
                cache.put(query, statement);
                statementStats.put(query, new CachedStatementStats(Instant.now(), Instant.now(), 1));
            }
        } catch (SQLException e) {
            HOLogger.instance().error(StatementCache.class, "Error creating statement: " + query
                    + "\n Error: " + e.getMessage());
        }

        return statement;
    }

    public PreparedStatement getPreparedStatement(String query) {
        PreparedStatement statement = getFromCache(query);
        if (statement == null) {
            statement = createStatement(query);
        }
        return statement;
    }


    record CachedStatementStats(Instant created, Instant lastAccessed, int count) {}
}
