package core.db

import core.model.*
import core.util.HOLogger
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

/**
 * The Table UserConfiguration contain all User properties.
 * CONFIG_KEY = Primary Key, fieldname of the class
 * CONFIG_VALUE = value of the field, save as VARCHAR. Convert to right datatype if loaded
 *
 * @since 1.36
 */
internal class UserConfigurationTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CONFIG_KEY")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as _Configuration?)!!.key }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as _Configuration?)!!.key = v as String? })
                .setType(Types.VARCHAR).setLength(50).isPrimaryKey(true).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CONFIG_VALUE")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as _Configuration?)!!.value }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as _Configuration?)!!.value = v as String? })
                .setType(Types.VARCHAR).setLength(256).isNullable(true).build()
        )
    }

    fun storeConfiguration(key: String?, value: String?) {
        val _config = _Configuration()
        _config.key = key
        _config.value = value
        _config.stored = isStored(key)
        store(_config)
    }

    private val getAllStringValuesStatementBuilder = PreparedSelectStatementBuilder(this, "")

    private fun getAllStringValues(): HashMap<String?, String?> {
            val map = HashMap<String?, String?>()
            val configs = load(
                _Configuration::class.java,
                adapter.executePreparedQuery(getAllStringValuesStatementBuilder.getStatement())
            )
            for (config: _Configuration? in configs) {
                map[config!!.key] = config.value
            }
            return map
        }

    fun getDBVersion(): Int {
            val config = loadOne(_Configuration::class.java, "DBVersion")
            if (config != null) return config.value!!.toInt()
            try {
                HOLogger.instance().log(javaClass, "Old DB version.")
                val rs = adapter.executeQuery("SELECT DBVersion FROM UserParameter")
                if (rs != null && rs.next()) {
                    val ret = rs.getInt(1)
                    rs.close()
                    return ret
                }
            } catch (e1: Exception) {
                HOLogger.instance().log(javaClass, e1)
            }
            return 0
        }

    /**
     * Get the last HO release where we have completed successfully a config update
     *
     * @return the ho version of the last conf update
     */
    fun getLastConfUpdate(): Double  {
            val config = loadOne(_Configuration::class.java, "LastConfUpdate")
            return if (config != null) config.value!!.toDouble() else 0.0
        }

    /**
     * update/ insert method
     *
     * @param obj Configuration
     */
    fun storeConfigurations(obj: Configuration) {
        val values = obj.values
        for (conf in values!!.entries) {
            storeConfiguration(conf.key, conf.value ?: "")
        }
    }

    /**
     * @param obj Configuration
     */
    fun loadConfigurations(obj: Configuration) {
        // initialize with default value
        val m:Map<String?, String?>? = obj.values
        val storedValues: Map<String?, String?> = getAllStringValues().filterNot { entry ->  entry.value == null }

        if (m != null) {
            obj.values = m + storedValues
        }
    }

    class _Configuration() : Storable() {
        var key: String? = null
        var value: String? = null
    }

    companion object {
        val TABLENAME = "USERCONFIGURATION"
    }
}