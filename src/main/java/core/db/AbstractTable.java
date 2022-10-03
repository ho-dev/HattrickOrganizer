package core.db;


import core.util.HOLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AbstractTable {

	/**
	 * tableName
	 **/
	private final String tableName;

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

	// Prepared Statements
	// Insert
	public static class PreparedInsertStatementBuilder extends DBManager.PreparedStatementBuilder {
		public PreparedInsertStatementBuilder(AbstractTable table) {
			super(table.createInsertStatement());
		}
	}
	protected PreparedInsertStatementBuilder preparedInsertStatementBuilder;
	protected PreparedInsertStatementBuilder createPreparedInsertStatementBuilder(){
		return new PreparedInsertStatementBuilder(this);
	}
	private String createInsertStatement() {
		return "INSERT INTO " + getTableName() +
				" (" +
				Arrays.stream(columns).map(ColumnDescriptor::getColumnName).collect(Collectors.joining(",")) +
				") VALUES (" +
				DBManager.getPlaceholders(columns.length) +
				")";
	}
	protected int executePreparedInsert(Object ... values){
		if ( preparedInsertStatementBuilder==null){
			preparedInsertStatementBuilder=createPreparedInsertStatementBuilder();
		}
		return adapter.executePreparedUpdate(preparedInsertStatementBuilder.getStatement(), values);
	}

	// Update
	private String createUpdateStatement() {
		return "UPDATE " + getTableName() +
				" SET " +
				Arrays.stream(columns).skip(1).map(i->i.getColumnName()+"=?").collect(Collectors.joining(",")) +
				" WHERE " +
				columns[0].getColumnName() +
				"=?";
	}
	public static class PreparedUpdateStatementBuilder extends DBManager.PreparedStatementBuilder {
		public PreparedUpdateStatementBuilder(AbstractTable table, String set) {
			super("UPDATE " + table.getTableName() + " " + set);
		}
		public PreparedUpdateStatementBuilder(AbstractTable table) {
			super(table.createUpdateStatement());
		}
	}

	protected PreparedUpdateStatementBuilder preparedUpdateStatementBuilder;
	protected PreparedUpdateStatementBuilder createPreparedUpdateStatementBuilder(){
		return new PreparedUpdateStatementBuilder(this);
	}
	protected int executePreparedUpdate(Object ... values){
		if (preparedUpdateStatementBuilder==null){
			preparedUpdateStatementBuilder=createPreparedUpdateStatementBuilder();
		}
		if ( preparedUpdateStatementBuilder != null) {
			return adapter.executePreparedUpdate(preparedUpdateStatementBuilder.getStatement(), values);
		}
		HOLogger.instance().error(getClass(), "no update statement builder created");
		return  -1;
	}

	// Delete
	public static class PreparedDeleteStatementBuilder extends DBManager.PreparedStatementBuilder {
		public PreparedDeleteStatementBuilder(AbstractTable table) {
			super("DELETE FROM " + table.getTableName() + " WHERE " + table.getColumns()[0].getColumnName() + "=?");
		}
		public PreparedDeleteStatementBuilder(AbstractTable table, String where) {
			super("DELETE FROM " + table.getTableName() + " " + where);
		}
	}
	protected  PreparedDeleteStatementBuilder preparedDeleteStatementBuilder;
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this);
	}
	protected int executePreparedDelete(Object ... whereValues) {
		if ( preparedDeleteStatementBuilder==null){
			preparedDeleteStatementBuilder=createPreparedDeleteStatementBuilder();
		}
		return adapter.executePreparedUpdate(preparedDeleteStatementBuilder.getStatement(), whereValues);
	}

	// Select
	public static class PreparedSelectStatementBuilder extends DBManager.PreparedStatementBuilder {
		public PreparedSelectStatementBuilder(AbstractTable table, String where) {
			super("SELECT * FROM " + table.getTableName() + " " + where);
		}
		public PreparedSelectStatementBuilder(AbstractTable table) {
			super("SELECT * FROM " + table.getTableName() + " WHERE " + table.getColumns()[0].getColumnName() + "=?");
		}
	}
	protected PreparedSelectStatementBuilder preparedSelectStatementBuilder;
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder() {
		return new PreparedSelectStatementBuilder(this);
	}

	protected PreparedStatement getPreparedSelectStatement(){
		if ( preparedSelectStatementBuilder==null){
			preparedSelectStatementBuilder=createPreparedSelectStatementBuilder();
		}
		return preparedSelectStatementBuilder.getStatement();
	}
	protected ResultSet executePreparedSelect (Object ... whereValues){
		return adapter.executePreparedQuery(getPreparedSelectStatement(), whereValues);
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

			adapter.executeUpdate(sql.toString());

			insertDefaultValues();
		}
	}
	protected void insertDefaultValues() {
		// override if values exists
	}

	/**
	 * Drop the current table
	 */
	protected void tryDropTable() {
		adapter.executeUpdate("DROP TABLE IF EXISTS " + getTableName());
	}

	/**
	 * Truncate the current table (i.e. remove all rows)
	 */
	protected void truncateTable() {
		adapter.executeUpdate("DELETE FROM " + getTableName());
	}

	private boolean tableExists(String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '" + tableName + "'";
		ResultSet rs = this.adapter.executeQuery(sql);
		return rs != null && rs.next();
	}

	public boolean tryAddColumn(String columnName, String columnType) throws SQLException {
		if (!columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " ADD COLUMN " + columnName + " " + columnType;
			adapter.executeUpdate(sql);
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
		ResultSet rs = adapter.executeQuery(sql);
		if (rs != null) return rs.next();
		return false;
	}

	public boolean tryChangeColumn(String columnName, String type_not_null) throws SQLException {
		if (columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN " + columnName + " SET " + type_not_null;
			adapter.executeUpdate(sql);
			return true;
		}
		return false;
	}

	public boolean tryRenameColumn(String from, String to) throws SQLException {
		if (columnExistsInTable(from)) {
			String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN " + from + " RENAME TO " + to;
			adapter.executeUpdate(sql);
			return true;
		}
		return false;
	}

	public boolean tryDeleteColumn(String columnName) throws SQLException {
		if (columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " DROP COLUMN " + columnName;
			adapter.executeUpdate(sql);
			return true;
		}
		return false;
	}

	public void addPrimaryKey(String columns) {
		adapter.executeUpdate("ALTER TABLE " + tableName + " ADD PRIMARY KEY (" + columns + ")");
	}

	public void tryDropPrimaryKey() throws SQLException {
		if (primaryKeyExists()) {
			adapter.executeUpdate("ALTER TABLE " + getTableName() + " DROP PRIMARY KEY");
		}
	}

	public void tryDropIndex(String index) {
		adapter.executeUpdate("DROP INDEX " + index + " IF EXISTS");
	}

	public void tryAddIndex(String indexName, String columns) {
		adapter.executeUpdate("CREATE INDEX IF NOT EXISTS " + indexName + " ON " + tableName + " (" + columns + ")");
	}

	public boolean primaryKeyExists() throws SQLException {
		String sql = "SELECT 1 FROM information_schema.table_constraints WHERE constraint_type = 'PRIMARY KEY' AND table_name = '"
				+ getTableName().toUpperCase() + "'";
		ResultSet rs = adapter.executeQuery(sql);
		if (rs != null) return rs.next();
		return false;
	}

}
