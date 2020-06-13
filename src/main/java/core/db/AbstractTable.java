package core.db;


import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;



public abstract class AbstractTable {
	
	/** tableName**/
	private String tableName = "";
	
	/** describes a tableColumn (name, datatype, nullable ..) **/
	protected ColumnDescriptor[] columns;
	
	/** Database connection **/
	protected  JDBCAdapter  adapter 	= null;
	
	/**
	 * constructor
	 * @param tableName
	 */
	public AbstractTable(String tableName,JDBCAdapter  adapter){
		this.tableName = tableName;
		this.adapter = adapter;
		initColumns();
	}
	
	protected String getTableType() {return"CACHED";}
	
	protected abstract void initColumns();
	
	
	protected String getTableName(){
		return tableName;
	}
	protected void setColumns(ColumnDescriptor[] columns){
		this.columns = columns;
	}
	
	protected ColumnDescriptor[] getColumns(){
		return columns;
	}
	
	protected String[] getCreateIndizeStatements(){
		return new String[0];
	}
	
	protected String[] getConstraintStatements(){
		return new String[0];
	}
	
	protected int delete(String[] whereColumns, String[] whereValues) {
		
		final StringBuffer sql = new StringBuffer("DELETE FROM ");
		sql.append(getTableName());
		if (whereColumns != null && whereValues != null && whereColumns.length == whereValues.length) {
		//Where bedingungen beachten
		if ((whereValues != null) && (whereColumns != null) && (whereColumns.length == whereValues.length) && (whereValues.length > 0)) {
			sql.append(" WHERE " + whereColumns[0] + " = " + whereValues[0]);

			for (int i = 1; i < whereValues.length; i++) {
				sql.append(" AND " + whereColumns[i] + " = " + whereValues[i]);
			}
		}
		}
	return adapter.executeUpdate(sql.toString());
	

	
}
	public void createTable() throws SQLException {
		if(!tableExists(getTableName())){
			ColumnDescriptor[] columns = getColumns();
			StringBuffer sql = new StringBuffer(500);
			sql.append("CREATE ").append(getTableType());
			sql.append(" TABLE ").append(getTableName());
			sql.append("(");

			for (int i = 0; i < columns.length; i++) {
				try {
					DBInfo dbInfo = adapter.getDBInfo();
					sql.append(columns[i].getCreateString(dbInfo));
				} catch (Exception e) {
					HOLogger.instance().log(getClass(),e);
				}
				if (i < columns.length - 1)
					sql.append(",");
				else
					sql.append(" ");
			}
		
			String[] contraints = getConstraintStatements();
			for (int i = 0; i < contraints.length; i++) {
				sql.append(",");
				sql.append(contraints[i]);
			}
			sql.append(" ) ");
		
			adapter.executeUpdate(sql.toString());
		
			insertDefaultValues();
		}
	}
	
	protected ResultSet getSelectByHrfID(int hrfID) {
		final StringBuffer sql = new StringBuffer("SELECT * FROM ");
		sql.append(tableName);
		sql.append(" WHERE HRF_ID = ");
		sql.append(hrfID);
		return adapter.executeQuery(sql.toString());
	}

	protected void insertDefaultValues(){
		// override if values exists
	}
	
	/** 
	 * Drop the current table
	 */
	protected void dropTable() {
		adapter.executeUpdate("DROP TABLE IF EXISTS "+getTableName());
	}
	
	/**
	 * Truncate the current table (i.e. remove all rows)
	 */
	protected void truncateTable() {
		adapter.executeQuery("DELETE FROM "+getTableName());
	}
	
	private boolean tableExists(String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '" + tableName + "'";
		ResultSet rs = this.adapter.executeQuery(sql);
		return rs.next();
	}
}
