package core.db

import core.db.DBManager.PreparedStatementBuilder
import core.util.HODateTime
import core.util.HOLogger
import java.sql.*
import java.util.*
import java.util.stream.Collectors

abstract class AbstractTable(val tableName: String, protected var adapter: JDBCAdapter) {
    /**
     * return the table name
     * @return String
     */
    /**
     * return the table columns
     * @return array of ColumnDescriptor
     */
    /**
     * describes a tableColumn (name, datatype, nullable ..)
     */
    protected lateinit var columns: Array<ColumnDescriptor>

    /**
     * id columns count, is used to build the standard select, delete and update statements
     */
    protected var idColumns = 1
    private val tableType: String
        get() = "CACHED"

    /**
     * derived table class has to create the columns array
     */
    protected abstract fun initColumns()

    open val createIndexStatement: Array<String?> = arrayOfNulls(0)
    protected open val constraintStatements: Array<String?>
        /**
         * standard creates no constraints. Constrains are added to the create table statements.
         * @return empty String array of constraints
         */
        get() = arrayOfNulls(0)

    /**
     * constructor
     *
     * @param tableName String table name
     */
    init {
        this.initColumns()
    }

    /**
     * stores the given object.
     * if the object is already stored in database, update is called otherwise insert
     * @param obj that should be stored
     * @param <T> Storable class (extends AbstractStorable)
    </T> */
    fun <T : Storable?> store(obj: T) {
        if (obj!!.stored) {
            update<T>(obj)
        } else if (0 < insert<T>(obj)) {
            obj.stored = true
        }
    }

    /**
     * create a new record of the storable object
     * @param `object` that should be created
     * @param <T> Storable class (extends AbstractStorable)
     * @return 1 on success, 0 on error
    </T> */
    private fun <T : Storable?> insert(obj: T): Int {
        return executePreparedInsert(*Arrays.stream(columns).map { c: ColumnDescriptor -> c.getter!!.apply(obj) }
            .toArray())
    }

    /**
     * update an existing record.
     * The first columns of the table are used in the where clause, the remaining as set values.
     * Count of id columns is defined by field idcolumns.
     * @param object that should be updated
     * @param <T> Storable class (extends AbstractStorable)
     * @return 1 on success, 0 on error
    </T> */
    private fun <T : Storable?> update(`object`: T): Int {
        val values = ArrayList<Any?>()
        values.addAll(
            Arrays.stream(columns).skip(idColumns.toLong()).map { c: ColumnDescriptor -> c.getter!!.apply(`object`) }
                .toList())
        values.addAll(
            Arrays.stream(columns).limit(idColumns.toLong()).map { c: ColumnDescriptor -> c.getter!!.apply(`object`) }
                .toList()) // where
        return executePreparedUpdate(*values.toTypedArray())
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
    </T> */
    fun <T : Storable?> loadOne(tClass: Class<T>, vararg whereValues: Any?): T? {
        return loadOne(tClass, executePreparedSelect(*whereValues))
    }

    /**
     * load one object of an externally created result set.
     * @param tClass Storable class (extends AbstractStorable)
     * @param rs result set
     * @param <T> the object class to create
     * @return one object of type T
    </T> */
    fun <T : Storable?> loadOne(tClass: Class<T>, rs: ResultSet?): T? {
        val list = load(tClass, rs, 1)
        return if (list.size > 0) {
            list[0]
        } else null
    }

    /**
     * load a list of records
     * @param tClass Storable class (extends AbstractStorable)
     * @param whereValues variable arguments describing the where values (count must match idColumns)
     * @param <T> the object class to create
     * @return List of objects of type T
    </T> */
    fun <T : Storable?> load(tClass: Class<T>, vararg whereValues: Any?): List<T> {
        return load(tClass, executePreparedSelect(*whereValues), -1)
    }

    /**
     * load a list of records of an externally created result set
     * @param tClass Storable class (extends AbstractStorable)
     * @param rs result set
     * @param <T> the object class to create
     * @return List of objects of type T
    </T> */
    fun <T : Storable?> load(tClass: Class<T>, rs: ResultSet?): List<T> {
        return load(tClass, rs, -1)
    }

    /**
     * load a list of records
     * @param tClass Storable class (extends AbstractStorable)
     * @param rs result set
     * @param max 1 to load one object, -1 to load all objects
     * @param <T> the object class to create
     * @return list of objects of type T
    </T> */
    protected fun <T : Storable?> load(tClass: Class<T>, rs: ResultSet?, max: Int): List<T> {
        var maxIndex = max
        val ret = ArrayList<T>()
        var columnDescriptor: ColumnDescriptor? = null
        try {
            val constructor = tClass.getConstructor()
            if (rs != null) {
                while (rs.next() && 0 != maxIndex--) {
                    val obj = constructor.newInstance()
                    for (c in columns) {
                        columnDescriptor = c
                        val value:Any? = when (c.type) {
                            Types.CHAR, Types.LONGVARCHAR, Types.VARCHAR -> getString(rs, c.columnName)
                            Types.BIT, Types.SMALLINT, Types.TINYINT, Types.BIGINT, Types.INTEGER -> getInteger(rs, c.columnName)
                            Types.TIME, Types.DATE, Types.TIMESTAMP_WITH_TIMEZONE, Types.TIME_WITH_TIMEZONE, Types.TIMESTAMP -> getHODateTime(rs, c.columnName)
                            Types.BOOLEAN -> getBoolean(rs, c.columnName)
                            Types.DOUBLE -> getDouble(rs, c.columnName)
                            Types.DECIMAL, Types.FLOAT, Types.REAL -> getFloat(rs, c.columnName)
                            else -> throw IllegalStateException("Unexpected value: " + c.type)
                        }
                        if (value != null) {
                            c.setter!!.accept(obj, value)
                        }
                    }
                    obj?.stored = true
                    ret.add(obj)
                }
                rs.close()
            }
        } catch (exception: Exception) {
            val stringBuilder = StringBuilder("load")
            if (columnDescriptor != null) {
                stringBuilder.append(" ").append(columnDescriptor.columnName)
            }
            stringBuilder.append(": ").append(exception)
            HOLogger.instance().error(javaClass, stringBuilder.toString())
        }
        return ret
    }

    /**
     * return a float from a result set column
     * @param rs result set
     * @param columnName column name
     * @return Float, null if the column was empty (null)
     * @throws SQLException sql exception
     */
    @Throws(SQLException::class)
    private fun getFloat(rs: ResultSet, columnName: String?): Float? {
        val ret = rs.getFloat(columnName)
        return if (rs.wasNull()) {
            null
        } else ret
    }

    /**
     * return String from a result set column
     * @param rs result set
     * @param columnName column name
     * @return String, null if column was empty (null)
     * @throws SQLException sql exception
     */
    @Throws(SQLException::class)
    private fun getString(rs: ResultSet, columnName: String?): String {
        val ret = rs.getString(columnName)
        return if (rs.wasNull()) {
            ""
        } else ret
    }

    /**
     * return HODateTime from a result set column
     * @param rs result set
     * @param columnName column name
     * @return HODateTime, null if the column was empty (null)
     * @throws SQLException sql exception
     */
    @Throws(SQLException::class)
    private fun getHODateTime(rs: ResultSet, columnName: String?): HODateTime? {
        val ts = rs.getTimestamp(columnName)
        return if (rs.wasNull()) {
            null
        } else HODateTime.fromDbTimestamp(ts)
    }

    /**
     * return Double from a result set column
     * @param rs result set
     * @param columnName column name
     * @return Double, null if column was empty (null)
     * @throws SQLException sql exception
     */
    @Throws(SQLException::class)
    private fun getDouble(rs: ResultSet, columnName: String?): Double? {
        val ret = rs.getDouble(columnName)
        return if (rs.wasNull()) {
            null
        } else ret
    }

    /**
     * return Boolean from result set column
     * @param rs result set
     * @param columnName column name
     * @return Boolean, null if column was empty (null)
     * @throws SQLException sql exception
     */
    @Throws(SQLException::class)
    private fun getBoolean(rs: ResultSet, columnName: String?): Boolean? {
        val ret = rs.getBoolean(columnName)
        return if (rs.wasNull()) {
            null
        } else ret
    }

    /**
     * return Integer from result set colum
     * @param rs result set
     * @param columnName column name
     * @return Integer, null if column was empty (null)
     * @throws SQLException sql exception
     */
    @Throws(SQLException::class)
    private fun getInteger(rs: ResultSet, columnName: String?): Int? {
        val ret = rs.getInt(columnName)
        return if (rs.wasNull()) {
            null
        } else ret
    }
    // Prepared Statements
    // Insert
    /**
     * Statement builder class used to create the insert statement.
     */
    class PreparedInsertStatementBuilder
    /**
     * constructor creates the insert statement
     * @param table table that should create the insert statement
     */
        (table: AbstractTable) : PreparedStatementBuilder(table.createInsertStatement())

    /**
     * the standard insert statement builder object
     */
    protected var preparedInsertStatementBuilder: PreparedInsertStatementBuilder? = null

    /**
     * standard method to create the insert statement builder.
     * This method should be overridden if a specialized insert is required by a derived class.
     * @return PreparedInsertStatementBuilder
     */
    protected fun createPreparedInsertStatementBuilder(): PreparedInsertStatementBuilder {
        return PreparedInsertStatementBuilder(this)
    }

    /**
     * create sql string of the standard insert statement.
     * @return sql string of the prepared statement.
     */
    private fun createInsertStatement(): String {
        return "INSERT INTO " + tableName +
                " (" +
                Arrays.stream<ColumnDescriptor>(columns).map<String?> { obj: ColumnDescriptor -> obj.columnName }
                    .collect(Collectors.joining(",")) +
                ") VALUES (" +
                DBManager.getPlaceholders(columns.size) +
                ")"
    }

    /**
     * execute the prepared insert statement.
     * @param values array of column values must match the defined columns of the table
     * @return 1 on success, 0 on error
     */
    protected fun executePreparedInsert(vararg values: Any?): Int {
        if (preparedInsertStatementBuilder == null) {
            preparedInsertStatementBuilder = createPreparedInsertStatementBuilder()
        }
        return adapter.executePreparedUpdate(preparedInsertStatementBuilder!!.getStatement(), *values)
    }
    // Update
    /**
     * create sql string of the standard update statement.
     * the first columns of table are used in the where clause, the remaining columns in the SET part.
     * The count of where values if given by the field id columns
     * @return sql string of the prepared statement
     */
    private fun createUpdateStatement(): String {
        return "UPDATE " + tableName +
                " SET " +
                Arrays.stream(columns).skip(idColumns.toLong()).map { i: ColumnDescriptor -> i.columnName + "=?" }
                    .collect(Collectors.joining(",")) +
                createSQLWhere()
    }

    /**
     * Update statement builder class used to create a prepared update statement
     */
    class PreparedUpdateStatementBuilder : PreparedStatementBuilder {
        /**
         * constructor with user defined set part
         * @param table derived table
         * @param set String
         */
        constructor(table: AbstractTable, set: String) : super("UPDATE ${table.tableName} $set")

        /**
         * construct a standard update statement builder
         * @param table table
         */
        constructor(table: AbstractTable) : super(table.createUpdateStatement())
    }

    /**
     * the standard update statement builder object.
     */
    protected var preparedUpdateStatementBuilder: PreparedUpdateStatementBuilder? = null

    /**
     * create the standard update statement builder.
     * This method should be overridden if a specialized update is required by a derived class.
     * @return PreparedUpdateStatementBuilder
     */
    fun createPreparedUpdateStatementBuilder(): PreparedUpdateStatementBuilder {
        return PreparedUpdateStatementBuilder(this)
    }

    /**
     * execute the standard update statement
     * @param values set first values must match the where clause value (idcolumns). the remaining columns are used in the SET part
     * @return 1 on success, 0 on error, -1 if no update statement builder was defined.
     */
    protected fun executePreparedUpdate(vararg values: Any?): Int {
        if (preparedUpdateStatementBuilder == null) {
            preparedUpdateStatementBuilder = createPreparedUpdateStatementBuilder()
        }
        return adapter.executePreparedUpdate(preparedUpdateStatementBuilder!!.getStatement(), *values)
    }
    // Delete
    /**
     * Delete statement builder class
     */
    class PreparedDeleteStatementBuilder : PreparedStatementBuilder {
        /**
         * standard constructor uses the first id columns to create the standard delete statement
         * @param table table
         */
        constructor(table: AbstractTable) : super("DELETE FROM " + table.tableName + table.createSQLWhere())

        /**
         * constructor with user defined where clause used to create the delete statement
         * @param table derived table
         * @param where sql where clause
         */
        constructor(table: AbstractTable, where: String) : super("DELETE FROM " + table.tableName + " " + where)
    }

    /**
     * the standard delete statement builder object
     */
    protected var preparedDeleteStatementBuilder: PreparedDeleteStatementBuilder? = null

    /**
     * create the standard delete statement builder.
     * This method should be overridden if a specialized delete is required by a derived class.
     * @return PreparedDeleteStatementBuilder
     */
    protected open fun createPreparedDeleteStatementBuilder(): PreparedDeleteStatementBuilder? {
        return PreparedDeleteStatementBuilder(this)
    }

    /**
     * execute the standard delete statement
     * @param whereValues the values must match the where clause value (idcolumns)
     * @return 1 on success, 0 on error
     */
    fun executePreparedDelete(vararg whereValues: Any?): Int {
        if (preparedDeleteStatementBuilder == null) {
            preparedDeleteStatementBuilder = createPreparedDeleteStatementBuilder()
        }
        return adapter.executePreparedUpdate(preparedDeleteStatementBuilder!!.getStatement(), *whereValues)
    }
    // Select
    /**
     * select statement builder class
     */
    class PreparedSelectStatementBuilder : PreparedStatementBuilder {
        /**
         * construct select statement with user defined where clause
         * @param table derived table
         * @param where String sql where clause
         */
        constructor(table: AbstractTable, where: String) : super("SELECT * FROM " + table.tableName + " " + where)

        /**
         * construct standard select statement using the first idcolumns to build the where clause
         * @param table derived table
         */
        constructor(table: AbstractTable) : super("SELECT * FROM " + table.tableName + table.createSQLWhere())
    }

    /**
     * create the standard where clause using the first idcolumns of the table
     * @return String sql where clause
     */
    private fun createSQLWhere(): String {
        return " WHERE " + Arrays.stream(columns).limit(idColumns.toLong())
            .map { i: ColumnDescriptor -> i.columnName + "=?" }
            .collect(Collectors.joining(" AND "))
    }

    /**
     * the standard select statement builder object
     */
    private var preparedSelectStatementBuilder: PreparedSelectStatementBuilder? = null

    /**
     * create the standard select statement builder
     * This method should be overridden if a specialized select is required by a derived class.
     * @return PreparedSelectStatementBuilder
     */
    protected open fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder? {
        return PreparedSelectStatementBuilder(this)
    }

    val preparedSelectStatement: PreparedStatement?
        /**
         * create the standard prepared select statement
         * @return PreparedStatement
         */
        get() {
            if (preparedSelectStatementBuilder == null) {
                preparedSelectStatementBuilder = createPreparedSelectStatementBuilder()
            }
            return preparedSelectStatementBuilder!!.getStatement()
        }

    /**
     * execute the standard select statement
     * @param whereValues where values
     * @return result set
     */
    protected fun executePreparedSelect(vararg whereValues: Any?): ResultSet? {
        return adapter.executePreparedQuery(preparedSelectStatement, *whereValues)
    }

    /**
     * Check if record is stored in database without loading it
     * @param whereValues values for prepared select statement
     * @return true if record is found
     */
    fun isStored(vararg whereValues: Any?): Boolean {
        var ret = false
        try {
            val rs = executePreparedSelect(*whereValues)
            if (rs != null) {
                if (rs.next()) {
                    ret = true
                }
                rs.close()
            }
        } catch (exception: Exception) {
            HOLogger.instance().error(javaClass, "load: $exception")
        }
        return ret
    }

    /**
     * Create the table
     * @throws SQLException sql exception
     */
    @Throws(SQLException::class)
    fun createTable() {
        if (!tableExists(tableName)) {
            val columns = columns
            val sql = StringBuilder(500)
            sql.append("CREATE ").append(tableType)
            sql.append(" TABLE ").append(tableName)
            sql.append("(")
            for (i in columns.indices) {
                try {
                    val dbInfo = adapter.dBInfo
                    sql.append(columns[i].getCreateString(dbInfo))
                } catch (e: Exception) {
                    HOLogger.instance().log(javaClass, e)
                }
                if (i < columns.size - 1) sql.append(",") else sql.append(" ")
            }
            val constraintStatements = constraintStatements
            for (constraint in constraintStatements) {
                sql.append(",")
                sql.append(constraint)
            }
            sql.append(" ) ")
            adapter.executeUpdate(sql.toString())
            insertDefaultValues()
        }
    }

    protected open fun insertDefaultValues() {
        // override if values exists
    }

    /**
     * Drop the current table
     */
    fun tryDropTable() {
        adapter.executeUpdate("DROP TABLE IF EXISTS " + tableName)
    }

    /**
     * Truncate the current table (i.e. remove all rows)
     */
    fun truncateTable() {
        adapter.executeUpdate("DELETE FROM " + tableName)
    }

    @Throws(SQLException::class)
    private fun tableExists(tableName: String): Boolean {
        val sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '$tableName'"
        val rs = adapter.executeQuery(sql)
        return rs != null && rs.next()
    }

    @Throws(SQLException::class)
    fun tryAddColumn(columnName: String, columnType: String): Boolean {
        if (!columnExistsInTable(columnName)) {
            adapter.executeUpdate("ALTER TABLE $tableName ADD COLUMN $columnName $columnType")
            return true
        }
        return false
    }

    @Throws(SQLException::class)
    private fun columnExistsInTable(columnName: String): Boolean {
        val sql = ("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME = '"
                + tableName.uppercase(Locale.getDefault())
                + "' AND COLUMN_NAME = '"
                + columnName.uppercase(Locale.getDefault())
                + "'")
        val rs = adapter.executeQuery(sql)
        return rs?.next() ?: false
    }

    @Throws(SQLException::class)
    fun tryChangeColumn(columnName: String, type_not_null: String) {
        if (columnExistsInTable(columnName)) {
            val sql = "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " SET " + type_not_null
            adapter.executeUpdate(sql)
        }
    }

    @Throws(SQLException::class)
    fun tryRenameColumn(from: String, to: String) {
        if (columnExistsInTable(from)) {
            val sql = "ALTER TABLE " + tableName + " ALTER COLUMN " + from + " RENAME TO " + to
            adapter.executeUpdate(sql)
        }
    }

    @Throws(SQLException::class)
    fun tryDeleteColumn(columnName: String): Boolean {
        if (columnExistsInTable(columnName)) {
            val sql = "ALTER TABLE " + tableName + " DROP COLUMN " + columnName
            adapter.executeUpdate(sql)
            return true
        }
        return false
    }

    fun addPrimaryKey(columns: String) {
        adapter.executeUpdate("ALTER TABLE $tableName ADD PRIMARY KEY ($columns)")
    }

    @Throws(SQLException::class)
    fun tryDropPrimaryKey() {
        if (primaryKeyExists()) {
            adapter.executeUpdate("ALTER TABLE " + tableName + " DROP PRIMARY KEY")
        }
    }

    fun tryDropIndex(index: String) {
        adapter.executeUpdate("DROP INDEX $index IF EXISTS")
    }

    fun tryAddIndex(indexName: String, columns: String) {
        adapter.executeUpdate("CREATE INDEX IF NOT EXISTS $indexName ON $tableName ($columns)")
    }

    @Throws(SQLException::class)
    fun primaryKeyExists(): Boolean {
        val sql =
            ("SELECT 1 FROM information_schema.table_constraints WHERE constraint_type = 'PRIMARY KEY' AND table_name = '"
                    + tableName.uppercase(Locale.getDefault()) + "'")
        val rs = adapter.executeQuery(sql)
        return rs?.next() ?: false
    }

    open class Storable {
        var stored = false
    }
}
