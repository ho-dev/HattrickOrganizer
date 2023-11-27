package core.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 * @author Thorsten Dietz
 */
final public class DBInfo {
    private DatabaseMetaData databaseMetaData;

    /**
     * return String for java.sql.Types
     *
     * @param type
     * @return String – String representation of the SQL type
     */
    public String getTypeName(int type) {

        // in future we have to change some type for some db
        return switch (type) {
            case Types.BOOLEAN -> "BOOLEAN";
            case Types.BIT -> "BIT";
            case Types.INTEGER -> "INTEGER";
            case Types.CHAR -> "CHAR";
            case Types.DATE -> "DATE";
            case Types.DECIMAL -> "DECIMAL";
            case Types.DOUBLE -> "DOUBLE";
            case Types.FLOAT -> "FLOAT";
            case Types.LONGVARCHAR -> "LONGVARCHAR";
            case Types.REAL -> "REAL";
            case Types.SMALLINT -> "SMALLINT";
            case Types.TIME -> "TIME";
            case Types.TIMESTAMP -> "TIMESTAMP";
            case Types.TINYINT -> "TINYINT";
            case Types.VARCHAR -> "VARCHAR";
            default -> "";
        };
    }

    DBInfo(DatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }

    /**
     * return all TableNames from current Database
     *
     * @return Object []
     */
    public Object[] getAllTablesNames() {
        String[] types = new String[2];
        types[0] = "TABLES"; //some DB want Tables
        types[1] = "TABLE"; // other Table
        ArrayList<String> tables = new ArrayList<String>();
        try {
            final ResultSet rs = databaseMetaData.getTables(null, null, "%", types);
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
        } catch (SQLException ex) {
            System.err.println("database connection: " + ex.getMessage());
        }
        return tables.toArray();
    }
}
