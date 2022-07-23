package core.db;


import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;


public abstract class AbstractTable {
	
	/** tableName**/
	private String tableName;
	
	/** describes a tableColumn (name, datatype, nullable ..) **/
	protected ColumnDescriptor[] columns;
	
	/** Database connection **/
	protected  JDBCAdapter  adapter;
	
	/**
	 * constructor
	 * @param tableName String table name
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
	
	protected String[] getCreateIndexStatement(){
		return new String[0];
	}
	
	protected String[] getConstraintStatements(){
		return new String[0];
	}
	
	protected int delete(String[] whereColumns, String[] whereValues) {
		
		final StringBuilder sql = new StringBuilder("DELETE FROM ");
		sql.append(getTableName());

		// construct the Where placeholder list
		if ((whereValues != null) && (whereColumns != null) && (whereColumns.length == whereValues.length) && (whereValues.length > 0)) {
			sql.append(" WHERE ").append(whereColumns[0]).append(" = ?");

			for (int i = 1; i < whereValues.length; i++) {
				sql.append(" AND ").append(whereColumns[i]).append(" = ?");
			}
		}
		else {
			HOLogger.instance().error(getClass(), "it is not allowed to delete without filter: " + sql);
			return 0;
		}
		return adapter.executePreparedUpdate(sql.toString(), whereValues);
	}

	public void createTable() throws SQLException {
		if(!tableExists(getTableName())){
			ColumnDescriptor[] columns = getColumns();
			StringBuilder sql = new StringBuilder(500);
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
		
			String[] constraintStatements = getConstraintStatements();
			for (String constraint : constraintStatements) {
				sql.append(",");
				sql.append(constraint);
			}
			sql.append(" ) ");
		
			adapter.executePreparedUpdate(sql.toString());
		
			insertDefaultValues();
		}
	}
	
	protected ResultSet getSelectByHrfID(int hrfID) {
		String sql = "SELECT * FROM " + tableName + " WHERE HRF_ID = ?";
		return adapter.executePreparedQuery(sql, hrfID);
	}

	protected void insertDefaultValues(){
		// override if values exists
	}
	
	/** 
	 * Drop the current table
	 */
	protected void tryDropTable() {
		adapter.executePreparedUpdate("DROP TABLE IF EXISTS "+getTableName());
	}
	
	/**
	 * Truncate the current table (i.e. remove all rows)
	 */
	protected void truncateTable() {
		adapter.executePreparedUpdate("DELETE FROM "+getTableName());
	}
	
	private boolean tableExists(String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = ?";
		ResultSet rs = this.adapter.executePreparedQuery(sql, tableName);
		return rs.next();
	}

	public boolean tryAddColumn(String columnName, String columnType) throws SQLException {
		if ( ! columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " ADD COLUMN " + columnName + " " + columnType;
			adapter.executePreparedUpdate(sql);
			return true;
		}
		return false;
	}

	private boolean columnExistsInTable(String columnName) throws SQLException {
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?";
//
//		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME = '"
//				+ getTableName().toUpperCase()
//				+ "' AND COLUMN_NAME = '"
//				+ columnName.toUpperCase()
//				+ "'";
		ResultSet rs = adapter.executePreparedQuery(sql, getTableName().toUpperCase(), columnName.toUpperCase());
		if ( rs != null ) return rs.next();
		return false;
	}

	public boolean tryChangeColumn(String columnName, String type_not_null) throws SQLException {
		if ( columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN " + columnName + " SET " + type_not_null;
			adapter.executePreparedUpdate(sql);
			return true;
		}
		return false;
	}

	public boolean tryRenameColumn(String from, String to) throws SQLException {
		if ( columnExistsInTable(from)) {
			String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN " + from + " RENAME TO " + to;
			adapter.executePreparedUpdate(sql);
			return true;
		}
		return false;
	}

	public boolean tryDeleteColumn(String columnName) throws SQLException {
		if ( columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " DROP COLUMN " + columnName;
			adapter.executePreparedUpdate(sql);
			return true;
		}
		return false;
	}

	public void addPrimaryKey(String columns) {
		adapter.executePreparedUpdate("ALTER TABLE " + tableName + " ADD PRIMARY KEY (" + columns + ")");
	}

	public void tryDropPrimaryKey() throws SQLException {
		if (primaryKeyExists()) {
			adapter.executePreparedUpdate("ALTER TABLE " + getTableName() + " DROP PRIMARY KEY");
		}
	}

	public void tryDropIndex(String index) {
		adapter.executePreparedUpdate("DROP INDEX " + index + " IF EXISTS");
	}

	public void tryAddIndex(String indexName, String columns) {
		adapter.executePreparedUpdate("CREATE INDEX IF NOT EXISTS " + indexName +  " ON " + tableName + " (" + columns + ")");
	}

	public boolean primaryKeyExists() throws SQLException {
		String sql = "SELECT 1 FROM information_schema.table_constraints WHERE constraint_type = ? AND table_name = ?";
		ResultSet rs = adapter.executePreparedQuery(sql, "PRIMARY KEY", getTableName().toUpperCase());
		if ( rs != null ) return rs.next();
		return false;
	}

	public  String createInsertStatement() {
		var ret = new StringBuilder("INSERT INTO ");
		ret.append(getTableName());

		var valuePlaceholders = new StringBuilder(" VALUES ");
		var sep = "(";
		for ( var c : columns){
			valuePlaceholders.append(sep).append("?");
			ret.append(sep).append(c.getColumnName());
			sep=",";
		}

		ret.append(")").append(valuePlaceholders).append(")");
		return ret.toString();
	}


}
