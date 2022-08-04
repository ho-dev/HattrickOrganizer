package core.db;


import core.util.HOLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public abstract class AbstractTable {

	/**
	 * tableName
	 **/
	private String tableName;

	/**
	 * describes a tableColumn (name, datatype, nullable ..)
	 **/
	protected ColumnDescriptor[] columns;

	/**
	 * Database connection
	 **/
	protected JDBCAdapter adapter;

	/**
	 * constructor
	 *
	 * @param tableName String table name
	 */
	public AbstractTable(String tableName, JDBCAdapter adapter) {
		this.tableName = tableName;
		this.adapter = adapter;
		initColumns();
	}

	protected String getTableType() {
		return "CACHED";
	}

	protected abstract void initColumns();


	protected String getTableName() {
		return tableName;
	}

	protected void setColumns(ColumnDescriptor[] columns) {
		this.columns = columns;
	}

	protected ColumnDescriptor[] getColumns() {
		return columns;
	}

	protected String[] getCreateIndexStatement() {
		return new String[0];
	}

	protected String[] getConstraintStatements() {
		return new String[0];
	}

	public PreparedStatement createSelectStatement(String where) {
		var sql = new StringBuilder("SELECT * FROM ");
		sql.append(getTableName()).append(" ").append(where);
		return adapter.createPreparedStatement(sql.toString());
	}
	public PreparedStatement createUpdateStatement(String set) {
		var sql = new StringBuilder("UPDATE ");
		sql.append(getTableName()).append(" ").append(set);
		return adapter.createPreparedStatement(sql.toString());
	}

	public PreparedStatement createDeleteStatement(String ... whereColumns) {
		var sql = new StringBuilder("DELETE FROM ");
		sql.append(getTableName());
		if (whereColumns != null && whereColumns.length > 0) {
			var sep = " WHERE ";
			for (var c : whereColumns) {
				sql.append(sep).append(c).append("=?");
				sep = " AND ";
			}
		} else {
			HOLogger.instance().error(getClass(), "it is not allowed to delete without filter: " + sql);
			return null;
		}

		return adapter.createPreparedStatement(sql.toString());
	}
	protected int delete(PreparedStatement preparedStatement, Object ... whereValues) {
		return adapter.executePreparedUpdate(preparedStatement, whereValues);
	}

	public void createTable() throws SQLException {
		if (!tableExists(getTableName())) {
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
					HOLogger.instance().log(getClass(), e);
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

			adapter._executeUpdate(sql.toString());

			insertDefaultValues();
		}
	}

	private PreparedStatement selectByHrfIDStatement;

	protected ResultSet getSelectByHrfID(int hrfID) {
		if (selectByHrfIDStatement == null) {
			selectByHrfIDStatement = adapter.createPreparedStatement("SELECT * FROM " + tableName + " WHERE HRF_ID = ?");
		}
		return adapter.executePreparedQuery(selectByHrfIDStatement, hrfID);
	}

	protected void insertDefaultValues() {
		// override if values exists
	}

	/**
	 * Drop the current table
	 */
	protected void tryDropTable() {
		adapter._executeUpdate("DROP TABLE IF EXISTS " + getTableName());
	}

	/**
	 * Truncate the current table (i.e. remove all rows)
	 */
	protected void truncateTable() {
		adapter._executeUpdate("DELETE FROM " + getTableName());
	}

	private boolean tableExists(String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '" + tableName + "'";
		ResultSet rs = this.adapter._executeQuery(sql);
		return rs.next();
	}

	public boolean tryAddColumn(String columnName, String columnType) throws SQLException {
		if (!columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " ADD COLUMN " + columnName + " " + columnType;
			adapter._executeUpdate(sql);
			return true;
		}
		return false;
	}

	private boolean columnExistsInTable(String columnName) throws SQLException {
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME = '"
				+ getTableName().toUpperCase()
				+ "' AND COLUMN_NAME = '"
				+ columnName.toUpperCase()
				+ "'";
		ResultSet rs = adapter._executeQuery(sql);
		if (rs != null) return rs.next();
		return false;
	}

	public boolean tryChangeColumn(String columnName, String type_not_null) throws SQLException {
		if (columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN " + columnName + " SET " + type_not_null;
			adapter._executeUpdate(sql);
			return true;
		}
		return false;
	}

	public boolean tryRenameColumn(String from, String to) throws SQLException {
		if (columnExistsInTable(from)) {
			String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN " + from + " RENAME TO " + to;
			adapter._executeUpdate(sql);
			return true;
		}
		return false;
	}

	public boolean tryDeleteColumn(String columnName) throws SQLException {
		if (columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " DROP COLUMN " + columnName;
			adapter._executeUpdate(sql);
			return true;
		}
		return false;
	}

	public void addPrimaryKey(String columns) {
		adapter._executeUpdate("ALTER TABLE " + tableName + " ADD PRIMARY KEY (" + columns + ")");
	}

	public void tryDropPrimaryKey() throws SQLException {
		if (primaryKeyExists()) {
			adapter._executeUpdate("ALTER TABLE " + getTableName() + " DROP PRIMARY KEY");
		}
	}

	public void tryDropIndex(String index) {
		adapter._executeUpdate("DROP INDEX " + index + " IF EXISTS");
	}

	public void tryAddIndex(String indexName, String columns) {
		adapter._executeUpdate("CREATE INDEX IF NOT EXISTS " + indexName + " ON " + tableName + " (" + columns + ")");
	}

	public boolean primaryKeyExists() throws SQLException {
		String sql = "SELECT 1 FROM information_schema.table_constraints WHERE constraint_type = 'PRIMARY KEY' AND table_name = '"
				+ getTableName().toUpperCase() + "'";
		ResultSet rs = adapter._executeQuery(sql);
		if (rs != null) return rs.next();
		return false;
	}

	private PreparedStatement createInsertStatement() {
		var ret = new StringBuilder("INSERT INTO ");
		ret.append(getTableName());
		var valuePlaceholders = new StringBuilder(" VALUES ");
		var sep = "(";
		for (var c : columns) {
			valuePlaceholders.append(sep).append("?");
			ret.append(sep).append(c.getColumnName());
			sep = ",";
		}
		ret.append(")").append(valuePlaceholders).append(")");
		return adapter.createPreparedStatement(ret.toString());
	}
	private PreparedStatement insertStatement;
	protected  PreparedStatement getInsertStatement(){
		if ( insertStatement == null){
			insertStatement = createInsertStatement();
		}
		return insertStatement;
	}
}
