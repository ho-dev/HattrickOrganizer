package core.db;

import core.util.HODateTime;
import core.util.HOLogger;
import java.sql.PreparedStatement;
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
	protected <T extends  Storable> List<T> load(Class<T> tClass, ResultSet rs, int max){
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
		}
		catch (Exception exception){
			var stringBuilder = new StringBuilder("load");
			if ( columnDescriptor != null){
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
	 * Statement builder class used to create the insert statement.
	 */
	public static class PreparedInsertStatementBuilder extends DBManager.PreparedStatementBuilder {
		/**
		 * constructor creates the insert statement
		 * @param table table that should create the insert statement
		 */
		public PreparedInsertStatementBuilder(AbstractTable table) {
			super(table.createInsertStatement());
		}
	}

	/**
	 * the standard insert statement builder object
	 */
	protected PreparedInsertStatementBuilder preparedInsertStatementBuilder;

	/**
	 * standard method to create the insert statement builder.
	 * This method should be overridden if a specialized insert is required by a derived class.
	 * @return PreparedInsertStatementBuilder
	 */
	protected PreparedInsertStatementBuilder createPreparedInsertStatementBuilder(){
		return new PreparedInsertStatementBuilder(this);
	}

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
	protected int executePreparedInsert(Object ... values){
		if ( preparedInsertStatementBuilder==null){
			preparedInsertStatementBuilder=createPreparedInsertStatementBuilder();
		}
		return adapter.executePreparedUpdate(preparedInsertStatementBuilder.getStatement(), values);
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
	 * Update statement builder class used to create a prepared update statement
	 */
	public static class PreparedUpdateStatementBuilder extends DBManager.PreparedStatementBuilder {
		/**
		 * constructor with user defined set part
		 * @param table derived table
		 * @param set String
		 */
		public PreparedUpdateStatementBuilder(AbstractTable table, String set) {
			super("UPDATE " + table.getTableName() + " " + set);
		}

		/**
		 * construct a standard update statement builder
		 * @param table table
		 */
		public PreparedUpdateStatementBuilder(AbstractTable table) {
			super(table.createUpdateStatement());
		}
	}

	/**
	 * the standard update statement builder object.
	 */
	protected PreparedUpdateStatementBuilder preparedUpdateStatementBuilder;

	/**
	 * create the standard update statement builder.
	 * This method should be overridden if a specialized update is required by a derived class.
	 * @return PreparedUpdateStatementBuilder
	 */
	protected PreparedUpdateStatementBuilder createPreparedUpdateStatementBuilder(){
		return new PreparedUpdateStatementBuilder(this);
	}

	/**
	 * execute the standard update statement
	 * @param values set first values must match the where clause value (idcolumns). the remaining columns are used in the SET part
	 * @return 1 on success, 0 on error, -1 if no update statement builder was defined.
	 */
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


	/**
	 * Delete statement builder class
	 */
	public static class PreparedDeleteStatementBuilder extends DBManager.PreparedStatementBuilder {
		/**
		 * standard constructor uses the first id columns to create the standard delete statement
		 * @param table table
		 */
		public PreparedDeleteStatementBuilder(AbstractTable table) {
			this(table, table.createSQLWhere());
		}

		/**
		 * constructor with user defined where clause used to create the delete statement
		 * @param table derived table
		 * @param where sql where clause
		 */
		public PreparedDeleteStatementBuilder(AbstractTable table, String where) {
			super("DELETE FROM " + table.getTableName() + " " + where);
		}
	}

	/**
	 * the standard delete statement builder object
	 */
	protected  PreparedDeleteStatementBuilder preparedDeleteStatementBuilder;

	/**
	 * create the standard delete statement builder.
	 * This method should be overridden if a specialized delete is required by a derived class.
	 * @return PreparedDeleteStatementBuilder
	 */
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this);
	}

	/**
	 * execute the standard delete statement
	 * @param whereValues the values must match the where clause value (idcolumns)
	 * @return 1 on success, 0 on error
	 */
	protected int executePreparedDelete(Object ... whereValues) {
		if ( preparedDeleteStatementBuilder==null){
			preparedDeleteStatementBuilder=createPreparedDeleteStatementBuilder();
		}
		return adapter.executePreparedUpdate(preparedDeleteStatementBuilder.getStatement(), whereValues);
	}

	// Select

	/**
	 * select statement builder class
	 */
	public static class PreparedSelectStatementBuilder extends DBManager.PreparedStatementBuilder {
		/**
		 * Construct select statement with user defined where clause
		 * @param table Table name
		 * @param where String sql where clause
		 * @param select Selected column list
		 */
		public PreparedSelectStatementBuilder(AbstractTable table, String where, String select) {
			super("SELECT " + select + " FROM " + table.getTableName() + " " + where);
		}

		/**
		 * Construct standard select * from table where ... statement
		 * @param table Table name
		 * @param where Where clause inclusive WHERE
		 */
		public PreparedSelectStatementBuilder(AbstractTable table, String where) {
			this(table, where, "*");
		}

		/**
		 * Construct standard select statement using the first idcolumns to build the where clause
		 * @param table derived table
		 */
		public PreparedSelectStatementBuilder(AbstractTable table) {
			this(table, table.createSQLWhere());
		}
	}

	/**
	 * create the standard where clause using the first idcolumns of the table
	 * @return String sql where clause
	 */
	private String createSQLWhere() {
		return " WHERE " + Arrays.stream(this.columns).limit(this.idColumns).map(i->i.getColumnName()+"=?").collect(Collectors.joining(" AND "));
	}

	/**
	 * the standard select statement builder object
	 */
	protected PreparedSelectStatementBuilder preparedSelectStatementBuilder;

	/**
	 * create the standard select statement builder
	 * This method should be overridden if a specialized select is required by a derived class.
	 * @return PreparedSelectStatementBuilder
	 */
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder() {
		return new PreparedSelectStatementBuilder(this);
	}

	/**
	 * create the standard prepared select statement
	 * @return PreparedStatement
	 */
	protected PreparedStatement getPreparedSelectStatement(){
		if ( preparedSelectStatementBuilder==null){
			preparedSelectStatementBuilder=createPreparedSelectStatementBuilder();
		}
		return preparedSelectStatementBuilder.getStatement();
	}

	public static class PreparedCheckIfExistStatementBuilder extends PreparedSelectStatementBuilder {
		public PreparedCheckIfExistStatementBuilder(AbstractTable table, String where) {
			super(table, where, "1");
		}

		public PreparedCheckIfExistStatementBuilder(AbstractTable table) {
			this(table, table.createSQLWhere());
		}
	}

	protected PreparedCheckIfExistStatementBuilder preparedCheckIfExistStatementBuilder;
	protected PreparedStatement getPreparedCheckIfExistStatement(){
		if ( preparedCheckIfExistStatementBuilder==null){
			preparedCheckIfExistStatementBuilder=createPreparedCheckIfExistStatementBuilder();
		}
		return preparedCheckIfExistStatementBuilder.getStatement();
	}
	protected PreparedCheckIfExistStatementBuilder createPreparedCheckIfExistStatementBuilder() {
		return new PreparedCheckIfExistStatementBuilder(this);
	}

	/**
	 * execute the standard select statement
	 * @param whereValues where values
	 * @return result set
	 */
	protected ResultSet executePreparedSelect (Object ... whereValues){
		return adapter.executePreparedQuery(getPreparedSelectStatement(), whereValues);
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
		return adapter.executePreparedQuery(getPreparedCheckIfExistStatement(), whereValues);
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

	public void tryChangeColumn(String columnName, String type_not_null) throws SQLException {
		if (columnExistsInTable(columnName)) {
			String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN " + columnName + " SET " + type_not_null;
			adapter.executeUpdate(sql);
		}
	}

	public void tryRenameColumn(String from, String to) throws SQLException {
		if (columnExistsInTable(from)) {
			String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN " + from + " RENAME TO " + to;
			adapter.executeUpdate(sql);
		}
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
