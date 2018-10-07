package core.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 * 
 * @author Thorsten Dietz
 *
 */
final public  class DBInfo {
	private DatabaseMetaData databaseMetaData;
	
	/**
	 * return String for java.sql.Types
	 * @param type
	 * @return
	 */
	public String getTypeName(int type){
		
		// in future we have to change some type for some db
		switch(type){
		case Types.BOOLEAN: return "BOOLEAN";
		case Types.BIT: return "BIT";
		case Types.INTEGER: return "INTEGER";
		case Types.CHAR: return "CHAR";
		case Types.DATE: return "DATE";
		case Types.DECIMAL: return "DECIMAL";
		case Types.DOUBLE: return "DOUBLE";
		case Types.FLOAT: return "FLOAT";
		case Types.LONGVARCHAR: return "LONGVARCHAR";
		case Types.REAL: return "REAL";
		case Types.SMALLINT: return "SMALLINT";
		case Types.TIME: return "TIME";
		case Types.TIMESTAMP: return "TIMESTAMP";
		case Types.TINYINT: return "TINYINT";
		case Types.VARCHAR: return "VARCHAR";
		default:
				return "";
		}
		
	}
	
	protected DBInfo(DatabaseMetaData databaseMetaData){
		this.databaseMetaData = databaseMetaData;
	}
	
	/**
	 * return all TableNames from current Database
	 * @return Object []
	 */
	public Object [] getAllTablesNames(){
		String[] types = new String[2];
        types[0] = "TABLES"; //some DB want Tables
        types[1] = "TABLE"; // other Table
        ArrayList<String> tables = new ArrayList<String>();
        try { 
            final ResultSet rs = databaseMetaData.getTables(null, null, "%", types);
            while(rs.next()){
            	tables.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
            
 
            //this.listTables();
        }
        catch (SQLException ex) {
            System.err.println("database connection: " + ex.getMessage());
        }
        return tables.toArray();
	}
}
