package core.db

import core.util.HOLogger
import java.math.BigDecimal
import java.sql.Date
import java.sql.SQLException
import java.sql.Timestamp
import java.sql.Types

internal class ModuleConfigTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf(ColumnDescriptor("CONFIG_KEY", Types.VARCHAR,
            nullable = false,
            primaryKey = true,
            length = 50
        ),
            ColumnDescriptor("CONFIG_VALUE", Types.VARCHAR, true, 256),
            ColumnDescriptor("CONFIG_DATATYPE", Types.INTEGER, false))
    }

    /**
     * update & insert method
     */
    fun saveConfig(values: Map<String?, Any?>) {
        for ((key, value) in values) {
            val updated = updateConfig(key, value)
            if (updated == 0) {
                insertConfig(key, value)
            }
        }
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder {
        return PreparedSelectStatementBuilder(this, "")
    }

    fun findAll(): Map<String, Any?> {
        val values = HashMap<String, Any?>()
        try {
            val rs = executePreparedSelect()
            if (rs != null) {
                while (rs.next()) {
                    values[rs.getString(columns[0].columnName)] = createObject(
                        rs.getString(columns[1].columnName), rs.getInt(
                            columns[2].columnName
                        )
                    )
                }
                rs.close()
            }
        } catch (e: SQLException) {
            HOLogger.instance().error(this.javaClass, e)
        }
        return values
    }

    private fun updateConfig(key: String?, value: Any?): Int {
        return executePreparedUpdate(
            value,
            getType(value),
            key
        )
    }

    private fun insertConfig(key: String?, value: Any?) {
        if (key == null) return
        executePreparedInsert(
            key,
            value,
            getType(value)
        )
    }

    fun deleteConfig(key: String?) {
        executePreparedDelete(key)
    }

    private fun getType(obj: Any?): Int {
        if (obj == null) return Types.NULL
        if (obj is Int) return Types.INTEGER
        if (obj is BigDecimal) return Types.DECIMAL
        if (obj is Timestamp) return Types.TIMESTAMP
        if (obj is Boolean) return Types.BOOLEAN
        return if (obj is Date) Types.DATE else Types.VARCHAR
    }

    private fun createObject(value: String?, type: Int): Any? {
        return if (value == null) null else when (type) {
            Types.INTEGER -> value.toInt()
            Types.DECIMAL -> BigDecimal(value)
            Types.TIMESTAMP -> Timestamp.valueOf(value)
            Types.BOOLEAN -> value.toBoolean()
            Types.DATE -> Date.valueOf(value)
            else -> value
        }
    }

    override fun insertDefaultValues() {
        if (findAll().isEmpty()) {
            val defaults = HashMap<String?, Any?>()
            defaults["TA_numericRating"] = java.lang.Boolean.FALSE
            defaults["TA_descriptionRating"] = java.lang.Boolean.TRUE
            defaults["TA_lineupCompare"] = java.lang.Boolean.TRUE
            defaults["TA_mixedLineup"] = java.lang.Boolean.FALSE
            defaults["TA_tacticDetail"] = java.lang.Boolean.FALSE
            defaults["TA_isStars"] = java.lang.Boolean.TRUE
            defaults["TA_isTotalStrength"] = java.lang.Boolean.TRUE
            defaults["TA_isSquad"] = java.lang.Boolean.TRUE
            defaults["TA_isSmartSquad"] = java.lang.Boolean.TRUE
            defaults["TA_isLoddarStats"] = java.lang.Boolean.TRUE
            defaults["TA_isShowPlayerInfo"] = java.lang.Boolean.FALSE
            defaults["TA_isCheckTeamName"] = java.lang.Boolean.TRUE
            saveConfig(defaults)
        }
    }

    companion object {
        const val TABLENAME = "MODULE_CONFIGURATION"
    }
}
