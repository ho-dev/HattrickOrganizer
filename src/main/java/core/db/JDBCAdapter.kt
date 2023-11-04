// %4089797104:de.hattrickorganizer.database%
package core.db

import core.util.ExceptionUtils
import core.util.HOLogger
import java.sql.*

/**
 * Provides the connection functions to the database
 */
class JDBCAdapter
/**
 * Creates new JDBCApapter
 */
{
    private var m_clConnection: Connection? = null
    private var m_clStatement: Statement? = null
    private var m_clDBInfo: DBInfo? = null

    /**
     * Closes the connection
     */
    fun disconnect() {
        m_clConnection = try {
            val statement =
                m_clConnection!!.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
            statement.execute("SHUTDOWN")
            m_clConnection!!.close()
            null
        } catch (e: Exception) {
            HOLogger.instance().error(javaClass, "JDBCAdapter.disconnect : $e")
            null
        }
    }

    /**
     * Execute a SQL Select statement
     *
     * @param sqlStatement
     * Sql query with placeholders
     *
     * @return ResultSet of the query
     */
    fun executeQuery(sqlStatement: String): ResultSet? {
        try {
            return if (m_clConnection!!.isClosed) {
                null
            } else m_clStatement!!.executeQuery(sqlStatement)
        } catch (e: Exception) {
            HOLogger.instance().error(
                javaClass,
                """
                    executeQuery : $e
                    Statement: $sqlStatement
                    ${ExceptionUtils.getStackTrace(e)}
                    """.trimIndent()
            )
        }
        return null
    }

    fun createPreparedStatement(sql: String): PreparedStatement? {
        try {
            return m_clConnection!!.prepareStatement(sql)
        } catch (e: Exception) {
            HOLogger.instance().error(
                javaClass, """
     createPreparedStatement : $e
     Statement: $sql
     ${ExceptionUtils.getStackTrace(e)}
     """.trimIndent()
            )
        }
        return null
    }

    fun executePreparedQuery(preparedStatement: PreparedStatement?, vararg params: Any?): ResultSet? {
        return if (preparedStatement == null) null else try {
            if (m_clConnection!!.isClosed) {
                return null
            }
            var i = 0
            for (p in params) {
                preparedStatement.setObject(++i, p)
            }
            preparedStatement.executeQuery()
        } catch (e: Exception) {
            HOLogger.instance().error(
                javaClass, """
     executePreparedQuery : $e
     Statement: $preparedStatement
     ${ExceptionUtils.getStackTrace(e)}
     """.trimIndent()
            )
            null
        }
    }

    /**
     * Executes an SQL INSERT, UPDATE or DELETE statement. In addition, SQL
     * statements that return nothing, such as SQL DDL statements, can be
     * executed.
     *
     * @param sqlStatement
     * INSERT, UPDATE or DELETE statement
     *
     * @return either the row count for SQL Data Manipulation Language (DML)
     * statements or 0 for SQL statements that return nothing
     */
    fun executeUpdate(sqlStatement: String?): Int {
        return try {
            if (m_clConnection!!.isClosed) {
                return 0
            }
            return m_clStatement!!.executeUpdate(sqlStatement)
        } catch (e: Exception) {
            HOLogger.instance().error(
                javaClass,
                """
                JDBCAdapter.executeUpdate : $e
                Statement: $sqlStatement
                ${ExceptionUtils.getStackTrace(e)}
                """.trimIndent()
            )
            return 0
        }
    }

    fun executePreparedUpdate(preparedStatement: PreparedStatement?, vararg params: Any?): Int {
        var ret = 0
        return try {
            if (m_clConnection!!.isClosed) {
                return 0
            }
            var i = 0
            for (p in params) {
                preparedStatement!!.setObject(++i, p)
            }
            ret = preparedStatement!!.executeUpdate()
            ret
        } catch (e: Exception) {
            HOLogger.instance().error(
                javaClass,
                """
                JDBCAdapter.executeUpdate : $e
                Statement: ${preparedStatement.toString()}
                ${ExceptionUtils.getStackTrace(e)}
                """.trimIndent()
            )
            0
        }
    }

    /**
     * Connects to the requested database
     *
     * @param URL
     * The path to the Server
     * @param User
     * User
     * @param PWD
     * Password
     * @param Driver
     * The driver to user
     */
    @Throws(Exception::class)
    fun connect(URL: String?, User: String?, PWD: String?, Driver: String?) {
        try {
            // Initialise the Database Driver Object
            Class.forName(Driver)
            m_clConnection = DriverManager.getConnection(URL, User, PWD)
            m_clStatement = m_clConnection!!.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            )
        } catch (e: Exception) {
            if (m_clConnection != null) {
                try {
                    m_clConnection!!.close()
                } catch (ex: Exception) {
                    HOLogger.instance().error(
                        javaClass,
                        "JDBCAdapter.connect : " + ex.message
                    )
                }
            }
            HOLogger.instance().error(javaClass, "JDBCAdapter.connect : " + e.message)
            throw e
        }
    }

    @get:Throws(Exception::class)
    val dBInfo: DBInfo
        /**
         *
         * @return DBInfo
         * @throws Exception
         */
        get() {
            if (m_clDBInfo == null) m_clDBInfo = DBInfo(m_clConnection!!.metaData)
            return m_clDBInfo!!
        }
    fun getAllTableNames(): Array<Any> {
        return try {
            dBInfo.getAllTablesNames()
        } catch (e: Exception) {
            HOLogger.instance().error(javaClass, "JDBCAdapter.getAllTableNames : $e")
            arrayOf<Any>(e.message as Any)
        }
    }
}
