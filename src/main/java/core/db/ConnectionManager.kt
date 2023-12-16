package core.db

import core.util.HOLogger

import java.sql.*

/**
 * Provides the connection functions to the database
 */
class ConnectionManager {
    var connection: Connection? = null

    var statement: Statement? = null

    var dbInfo: DBInfo? = null
        get() {
            if (field == null) {
                field = DBInfo(connection?.metaData)
            }
            return field
        }

    var statementCache: StatementCache = StatementCache(this)

    /**
     * Closes the connection
     */
    fun disconnect() {
        try {
            val statement = connection?.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
            statement?.execute("SHUTDOWN")
            connection?.close()
        } catch (e: Exception) {
            HOLogger.instance().error(javaClass, "ConnectionManager.disconnect : $e")
        } finally {
            connection = null
        }
    }

    /**
     * Execute a SQL Select statement
     *
     * @param sqlStatement
     *            Sql query with placeholders
     *
     * @return ResultSet of the query
     */
    fun executeQuery(sqlStatement: String): ResultSet? {
        checkConnectionNotClosed()
        return statement?.executeQuery(sqlStatement)

    }

    private fun checkConnectionNotClosed() {
        if (connection?.isClosed == true) {
            throw SQLException("Connection closed")
        }
    }

    fun executePreparedQuery(query: String, vararg params: Any): ResultSet? {
        val preparedStatement = statementCache.getPreparedStatement(query)
        if (preparedStatement != null) {
            return executePreparedQuery(preparedStatement, *params)
        }
        return null
    }

    private fun executePreparedQuery(preparedStatement: PreparedStatement, vararg params: Any): ResultSet {
        checkConnectionNotClosed()
        for ((i, p) in params.withIndex()) {
            preparedStatement.setObject(i + 1, p)
        }
        return preparedStatement.executeQuery()
    }

    /**
     * Executes an SQL INSERT, UPDATE or DELETE statement. In addition, SQL
     * statements that return nothing, such as SQL DDL statements, can be
     * executed.
     *
     * @param sqlStatement
     *            INSERT, UPDATE or DELETE statement
     *
     * @return either the row count for SQL Data Manipulation Language (DML)
     *         statements or 0 for SQL statements that return nothing
     *
     */
    fun executeUpdate(sqlStatement: String): Int {
        checkConnectionNotClosed()
        return statement?.executeUpdate(sqlStatement) ?: 0
    }

    fun executePreparedUpdate(insert: String, vararg params: Any): Int {
        val preparedStatement = statementCache.getPreparedStatement(insert)
        if (preparedStatement != null) {
            return executePreparedUpdate(preparedStatement, *params)
        }
        return 0
    }

    private fun executePreparedUpdate(preparedStatement: PreparedStatement, vararg params: Any): Int {
        for ((i, p) in params.withIndex()) {
            preparedStatement.setObject(i + 1, p)
        }
        return preparedStatement.executeUpdate()
    }

    /**
     * Connects to the requested database
     *
     * @param url
     *            The path to the Server
     * @param user
     *            User
     * @param password
     *            Password
     * @param driver
     *            The driver to user
     *
     */
    fun connect(url: String, user: String, password: String, driver: String) {
        // Initialise the Database Driver Object
        Class.forName(driver)
        val connection = DriverManager.getConnection(url, user, password)
        connect(connection)
    }

    fun connect(conn: Connection) {
        connection = conn
        statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
        statementCache = StatementCache(this)
    }

    fun getAllTableNames(): Array<String> {
        try {
            return dbInfo?.getAllTablesNames() ?: emptyArray()
        } catch (e: Exception) {
            HOLogger.instance().error(javaClass, "ConnectionManager.getAllTableNames : $e")
            throw e
        }
    }
}
