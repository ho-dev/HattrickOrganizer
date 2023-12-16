package core.db;

import core.util.HODateTime;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	 * id columns count, is used to build the standard select, delete and update statements
	 */
	protected int idColumns = 1;

	/**
	 * Database connection
	 **/
	protected ConnectionManager connectionManager;

	/**
	 * constructor
	 *
	 * @param tableName String table name
	 */
	public AbstractTable(String tableName, ConnectionManager connectionManager) {
		this.tableName = tableName;
		this.connectionManager = connectionManager;
		initColumns();
	}

	protected String getTableType() {
		return "CACHED";
	}

	/**
	 * derived table class has to create the columns array
	 */
	protected abstract void initColumns();


	/**
	 * return the table name
	 * @return String
	 */
	protected String getTableName() {
		return tableName;
	}

	/**
	 * return the table columns
	 * @return array of ColumnDescriptor
	 */
	protected ColumnDescriptor[] getColumns() {
		return columns;
	}

	/**
	 * standard creates no index
	 * @return empty String array of create index statements
	 */
	protected String[] getCreateIndexStatement() {
		return new String[0];
	}

	/**
	 * standard creates no constraints. Constrains are added to the create table statements.
	 * @return empty String array of constraints
	 */
	protected String[] getConstraintStatements() {
		return new String[0];
	}

	/**
	 * stores the given object.
	 * if the object is already stored in database, update is called otherwise insert
	 * @param object that should be stored
	 * @param <T> Storable class (extends AbstractStorable)
	 */
	public <T extends Storable> void store(T object){
		if (object.isStored()) {
			update(object);
		} else if (0 < insert(object)) {
			object.setIsStored(true);
		}
	}

	/**
	 * create a new record of the storable object
	 * @param object that should be created
	 * @param <T> Storable class (extends AbstractStorable)
	 * @return 1 on success, 0 on error
	 */
	private <T extends Storable> int insert(T object) {
		return executePreparedInsert(Arrays.stream(columns).map(
				c->c.getter.apply(object)
		).toArray());
	}

	/**
	 * update an existing record.
	 * The first columns of the table are used in the where clause, the remaining as set values.
	 * Count of id columns is defined by field idcolumns.
	 * @param object that should be updated
	 * @param <T> Storable class (extends AbstractStorable)
	 * @return 1 on success, 0 on error
	 */
	private <T extends Storable> int update(T object) {
		var values = new ArrayList<>();
		values.addAll(Arrays.stream(columns).skip(idColumns).map(c->c.getter.apply(object)).toList());
		values.addAll(Arrays.stream(columns).limit(idColumns).map(c->c.getter.apply(object)).toList()); // where
		return executePreparedUpdate(values.toArray());
	}

	/**
	 * load one object.
	 * The first columns of the table are used as id columns.
	 * the specified where values must match the first id columns of the table.
	 * The count of where values is defined by idColumns
	 * @param tClass Storable class (extends Abstract.Storable)
	 * @param whereValues variable arguments describing the where values (count must match idColumns)
	 * @param <T> the object class to create
	 * @return one Object of type T
	 */
	public <T extends Storable> T loadOne(Class<T> tClass, Object ... whereValues) {
		return loadOne(tClass, executePreparedSelect(whereValues));
	}

	/**
	 * load one object of an externally created result set.
	 * @param tClass Storable class (extends AbstractStorable)
	 * @param rs result set
	 * @param <T> the object class to create
	 * @return one object of type T
	 */
	public <T extends Storable> T loadOne(Class<T> tClass, ResultSet rs) {
		var list = load(tClass, rs, 1);
		if (!list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	/**
	 * load a list of records
	 * @param tClass Storable class (extends AbstractStorable)
	 * @param whereValues variable arguments describing the where values (count must match idColumns)
	 * @param <T> the object class to create
	 * @return List of objects of type T
	 */
	public <T extends Storable> List<T> load(Class<T> tClass, Object ... whereValues) {
		return load(tClass, executePreparedSelect(whereValues), -1);
	}

	/**
	 * load a list of records of an externally created result set
	 * @param tClass Storable class (extends AbstractStorable)
	 * @param rs result set
	 * @param <T> the object class to create
	 * @return List of objects of type T
	 */
	public <T extends Storable> List<T> load(Class<T> tClass, ResultSet rs) {
		return load(tClass, rs, -1);
	}

	/**
	 * load a list of records
	 * @param tClass Storable class (extends AbstractStorable)
	 * @param rs result set
	 * @param max 1 to load one object, -1 to load all objects
	 * @param <T> the object class to create
	 * @return list of objects of type T
	 */
	protected <T extends Storable> List<T> load(Class<T> tClass, ResultSet rs, int max){
		var ret = new ArrayList<T>();
		ColumnDescriptor columnDescriptor = null;
		try{
			var constructor = tClass.getConstructor();
			if (rs != null) {
				while (rs.next() && 0 != max--) {
					var object = constructor.newInstance();
					for (var c : columns) {
						columnDescriptor = c;
						var value = switch (c.getType()) {
							case Types.CHAR, Types.LONGVARCHAR, Types.VARCHAR -> getString(rs, c.getColumnName());
							case Types.BIT, Types.SMALLINT, Types.TINYINT, Types.BIGINT, Types.INTEGER -> getInteger(rs,c.getColumnName());
							case Types.TIME, Types.DATE, Types.TIMESTAMP_WITH_TIMEZONE, Types.TIME_WITH_TIMEZONE, Types.TIMESTAMP -> getHODateTime(rs, c.getColumnName());
							case Types.BOOLEAN -> getBoolean(rs,c.getColumnName());
							case Types.DOUBLE -> getDouble(rs, c.getColumnName());
							case Types.DECIMAL, Types.FLOAT, Types.REAL -> getFloat(rs, c.getColumnName());
							default -> throw new IllegalStateException("Unexpected value: " + c.getType());
						};
						c.setter.accept(object, value);
					}
					object.setIsStored(true);
					ret.add(object);
				}
				rs.close();
			}
		} catch (Exception exception) {
			var stringBuilder = new StringBuilder("load");
			if (columnDescriptor != null) {
				stringBuilder.append(" ").append(columnDescriptor.getColumnName());
			}
			stringBuilder.append(": ").append(exception);
			HOLogger.instance().error(getClass(), stringBuilder.toString());
		}
		return ret;
	}

	/**
	 * return a float from a result set column
	 * @param rs result set
	 * @param columnName column name
	 * @return Float, null if the column was empty (null)
	 * @throws SQLException sql exception
	 */
	private Float getFloat(ResultSet rs, String columnName) throws SQLException {
		var ret = rs.getFloat(columnName);
		if (rs.wasNull()){
			return null;
		}
		return ret;
	}

	/**
	 * return String from a result set column
	 * @param rs result set
	 * @param columnName column name
	 * @return String, null if column was empty (null)
	 * @throws SQLException sql exception
	 */
	private String getString(ResultSet rs, String columnName) throws SQLException {
		var ret = rs.getString(columnName);
		if (rs.wasNull()){
			return "";
		}
		return ret;
	}

	/**
	 * return HODateTime from a result set column
	 * @param rs result set
	 * @param columnName column name
	 * @return HODateTime, null if the column was empty (null)
	 * @throws SQLException sql exception
	 */
	private HODateTime getHODateTime(ResultSet rs, String columnName) throws SQLException {
		var ts = rs.getTimestamp(columnName);
		if (rs.wasNull()){
			return null;
		}
		return HODateTime.fromDbTimestamp(ts);
	}

	/**
	 * return Double from a result set column
	 * @param rs result set
	 * @param columnName column name
	 * @return Double, null if column was empty (null)
	 * @throws SQLException sql exception
	 */
	private Double getDouble(ResultSet rs, String columnName) throws SQLException {
		var ret = rs.getDouble(columnName);
		if (rs.wasNull()){
			return null;
		}
		return ret;
	}

	/**
	 * return Boolean from result set column
	 * @param rs result set
	 * @param columnName column name
	 * @return Boolean, null if column was empty (null)
	 * @throws SQLException sql exception
	 */
	private Boolean getBoolean(ResultSet rs, String columnName) throws SQLException {
		var ret = rs.getBoolean(columnName);
		if (rs.wasNull()){
			return null;
		}
		return ret;
	}

	/**
	 * return Integer from result set colum
	 * @param rs result set
	 * @param columnName column name
	 * @return Integer, null if column was empty (null)
	 * @throws SQLException sql exception
	 */
	private Integer getInteger(ResultSet rs, String columnName) throws SQLException {
		var ret = rs.getInt(columnName);
		if (rs.wasNull()){
			return null;
		}
		return ret;
	}

	// Prepared Statements
	// Insert

	/**
	 * create sql string of the standard insert statement.
	 * @return sql string of the prepared statement.
	 */
	private String createInsertStatement() {
		return "INSERT INTO " + getTableName() +
				" (" +
				Arrays.stream(columns).map(ColumnDescriptor::getColumnName).collect(Collectors.joining(",")) +
				") VALUES (" +
				DBManager.getPlaceholders(columns.length) +
				")";
	}

	/**
	 * execute the prepared insert statement.
	 * @param values array of column values must match the defined columns of the table
	 * @return 1 on success, 0 on error
	 */
	protected int executePreparedInsert(Object... values) {
		return connectionManager.executePreparedUpdate(createInsertStatement(), values);
	}

	// Update


	/**
	 * create sql string of the standard update statement.
	 * the first columns of table are used in the where clause, the remaining columns in the SET part.
	 * The count of where values if given by the field id columns
	 * @return sql string of the prepared statement
	 */
	private String createUpdateStatement() {
		return "UPDATE " + getTableName() +
				" SET " +
				Arrays.stream(columns).skip(idColumns).map(i->i.getColumnName()+"=?").collect(Collectors.joining(",")) +
				createSQLWhere();
	}

	/**
	 * execute the standard update statement
	 * @param values set first values must match the where clause value (idcolumns). the remaining columns are used in the SET part
	 * @return 1 on success, 0 on error, -1 if no update statement builder was defined.
	 */
	protected int executePreparedUpdate(Object... values){
		return connectionManager.executePreparedUpdate(createUpdateStatement(), values);
	}

	// Delete

	protected String createDeleteStatement() {
		return createDeleteStatement(createSQLWhere());
	}

	protected String createDeleteStatement(String whereClause) {
		return "DELETE FROM " + getTableName() + " " + whereClause;
	}

	/**
	 * execute the standard delete statement
	 * @param whereValues the values must match the where clause value (idcolumns)
	 * @return 1 on success, 0 on error
	 */
	protected int executePreparedDelete(Object... whereValues) {
		return connectionManager.executePreparedUpdate(createDeleteStatement(), whereValues);
	}

	// Select

	/**
	 * create the standard where clause using the first idcolumns of the table
	 * @return String sql where clause
	 */
	private String createSQLWhere() {
		return " WHERE " + Arrays.stream(this.columns)
				.limit(this.idColumns)
				.map(i -> i.getColumnName() + " = ?")
				.collect(Collectors.joining(" AND "));
	}

	protected String createSelectStatement(String selectColumns, String whereClause) {
		return "SELECT " + selectColumns + " FROM " + getTableName() + " " + whereClause;
	}

	protected String createSelectStatement(String whereClause) {
		return createSelectStatement("*", whereClause);
	}

	protected String createSelectStatement() {
		return createSelectStatement(createSQLWhere());
	}


	protected String getPreparedCheckIfExistStatement() {
		return createSelectStatement("1", createSQLWhere());
	}

	/**
	 * execute the standard select statement
	 * @param whereValues where values
	 * @return result set
	 */
	protected ResultSet executePreparedSelect(Object... whereValues) {
		return connectionManager.executePreparedQuery(createSelectStatement(), whereValues);
	}

	/**
	 * Check if record is stored in database without loading it
	 * @param whereValues values for prepared select statement
	 * @return true if record is found
	 */
	protected boolean isStored(Object ... whereValues) {
		boolean ret = false;
		try{
			var rs = executePreparedCheckIfExist(whereValues);
			if (rs != null) {
				ret = rs.next();
				rs.close();
			}
		}
		catch (Exception exception){
			HOLogger.instance().error(getClass(), "load: " + exception);
		}
		return ret;
	}

	/**
	 * Create a select statement checking the existence of a record
	 * @param whereValues record keys values
	 * @return Non-empty result set, if records exists
	 */
	private ResultSet executePreparedCheckIfExist(Object ... whereValues) {
		return connectionManager.executePreparedQuery(getPreparedCheckIfExistStatement(), whereValues);
	}

	/**
	 * Create the table
	 * @throws SQLException sql exception
	 */
	public void createTable() throws SQLException {
		if (!tableExists(getTableName())) {
			ColumnDescriptor[] columns = getColumns();
			StringBuilder sql = new StringBuilder(500);
			sql.append("CREATE ").append(getTableType());
			sql.append(" TABLE ").append(getTableName());
			sql.append("(");

			for (int i = 0; i < columns.length; i++) {
				try {
					DBInfo dbInfo = connectionManager.getDbInfo();
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

			connectionManager.executeUpdate(sql.toString());

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
		connectionManager.executeUpdate("DROP TABLE IF EXISTS " + getTableName());
	}

	/**
	 * Truncate the current table (i.e. remove all rows)
	 */
	protected void truncateTable() {
		connectionManager.executeUpdate("DELETE FROM " + getTableName());
	}

	private boolean tableExists(String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '" + tableName + "'";
		ResultSet rs = this.connectionManager.executeQuery(sql);
		boolean result = rs != null && rs.next();
		if (rs != null) {
			rs.close();
		}
		return result;
	}

	public boolean tryAddColumn(String columnName, String columnType) throws SQLException {
		if (!columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " ADD COLUMN " + columnName + " " + columnType;
			connectionManager.executeUpdate(sql);
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
		try (ResultSet rs = connectionManager.executeQuery(sql)) {
			if (rs != null) return rs.next();
		}
		return false;
	}

	public void tryChangeColumn(String columnName, String type_not_null) throws SQLException {
		if (columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN " + columnName + " SET " + type_not_null;
			connectionManager.executeUpdate(sql);
		}
	}

	public void tryRenameColumn(String from, String to) throws SQLException {
		if (columnExistsInTable(from)) {
			String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN " + from + " RENAME TO " + to;
			connectionManager.executeUpdate(sql);
		}
	}

	public boolean tryDeleteColumn(String columnName) throws SQLException {
		if (columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " DROP COLUMN " + columnName;
			connectionManager.executeUpdate(sql);
			return true;
		}
		return false;
	}

	public void addPrimaryKey(String columns) {
		connectionManager.executeUpdate("ALTER TABLE " + tableName + " ADD PRIMARY KEY (" + columns + ")");
	}

	public void tryDropPrimaryKey() throws SQLException {
		if (primaryKeyExists()) {
			connectionManager.executeUpdate("ALTER TABLE " + getTableName() + " DROP PRIMARY KEY");
		}
	}

	public void tryDropIndex(String index) {
		connectionManager.executeUpdate("DROP INDEX " + index + " IF EXISTS");
	}

	public void tryAddIndex(String indexName, String columns) {
		connectionManager.executeUpdate("CREATE INDEX IF NOT EXISTS " + indexName + " ON " + tableName + " (" + columns + ")");
	}

	public boolean primaryKeyExists() throws SQLException {
		String sql = "SELECT 1 FROM information_schema.table_constraints WHERE constraint_type = 'PRIMARY KEY' AND table_name = '"
				+ getTableName().toUpperCase() + "'";
		try (ResultSet rs = connectionManager.executeQuery(sql)) {
			if (rs != null) return rs.next();
		}
		return false;
	}

	public static class Storable {
		private boolean isStored=false;

		public boolean isStored(){
			return this.isStored;
		}

		public void setIsStored(boolean v){
			this.isStored = v;
		}
	}
}
