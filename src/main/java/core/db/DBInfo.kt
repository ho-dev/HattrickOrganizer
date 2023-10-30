package core.db

import java.sql.DatabaseMetaData
import java.sql.SQLException
import java.sql.Types

/**
 *
 * @author Thorsten Dietz
 */
class DBInfo(private val databaseMetaData: DatabaseMetaData) {
    /**
     * return String for java.sql.Types
     * @param type
     * @return
     */
    fun getTypeName(type: Int): String {

        // in future we have to change some type for some db
        return when (type) {
            Types.BOOLEAN -> "BOOLEAN"
            Types.BIT -> "BIT"
            Types.INTEGER -> "INTEGER"
            Types.CHAR -> "CHAR"
            Types.DATE -> "DATE"
            Types.DECIMAL -> "DECIMAL"
            Types.DOUBLE -> "DOUBLE"
            Types.FLOAT -> "FLOAT"
            Types.LONGVARCHAR -> "LONGVARCHAR"
            Types.REAL -> "REAL"
            Types.SMALLINT -> "SMALLINT"
            Types.TIME -> "TIME"
            Types.TIMESTAMP -> "TIMESTAMP"
            Types.TINYINT -> "TINYINT"
            Types.VARCHAR -> "VARCHAR"
            else -> ""
        }
    }

    /**
     * return all TableNames from current Database
     * @return Object []
     */
    fun getAllTablesNames(): Array<Any> {
        val types = arrayOfNulls<String>(2)
        types[0] = "TABLES" //some DB want Tables
        types[1] = "TABLE" // other Table
        val tables = ArrayList<String>()
        try {
            val rs = databaseMetaData.getTables(null, null, "%", types)
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"))
            }
            rs.close()
        } catch (ex: SQLException) {
            System.err.println("database connection: " + ex.message)
        }
        return tables.toTypedArray()
    }
}
