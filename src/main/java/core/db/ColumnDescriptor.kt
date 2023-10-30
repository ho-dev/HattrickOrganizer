package core.db

import java.sql.Types
import java.util.function.BiConsumer
import java.util.function.Function

/**
 *
 * @author Thorsten Dietz
 */
class ColumnDescriptor {
    val columnName: String?
    val type: Int
    private val length: Int
    val isNullable: Boolean
    val isPrimaryKey: Boolean
    var getter: Function<Any?, Any?>? = null
    var setter: BiConsumer<Any?, Any>? = null

    constructor(builder: Builder) {
        columnName = builder.columnName
        type = builder.type
        length = builder.length
        isNullable = builder.nullable
        isPrimaryKey = builder.primaryKey
        getter = builder.getter
        setter = builder.setter
    }

    class Builder {
        var columnName: String? = null
        var type = Types.VARCHAR
        var length = 0
        var nullable = true
        var primaryKey = false
        var getter: Function<Any?, Any?>? = null
        var setter: BiConsumer<Any?, Any>? = null
        fun setColumnName(columnName: String?): Builder {
            this.columnName = columnName
            return this
        }

        fun setType(type: Int): Builder {
            this.type = type
            return this
        }

        fun setLength(length: Int): Builder {
            this.length = length
            return this
        }

        fun isNullable(nullable: Boolean): Builder {
            this.nullable = nullable
            return this
        }

        fun isPrimaryKey(primaryKey: Boolean): Builder {
            this.primaryKey = primaryKey
            return this
        }

        fun setGetter(getter: Function<Any?, Any?>?): Builder {
            this.getter = getter
            return this
        }

        fun setSetter(setter: BiConsumer<Any?, Any>?): Builder {
            this.setter = setter
            return this
        }

        fun build(): ColumnDescriptor {
            return ColumnDescriptor(this)
        }

        companion object {
            fun newInstance(): Builder {
                return Builder()
            }
        }
    }

    @JvmOverloads
    constructor(columnName: String?, type: Int, nullable: Boolean, length: Int = 0) : this(
        columnName,
        type,
        nullable,
        false,
        length
    )

    @JvmOverloads
    constructor(columnName: String?, type: Int, nullable: Boolean, primaryKey: Boolean, length: Int = 0) {
        this.columnName = columnName
        this.type = type
        isNullable = nullable
        isPrimaryKey = primaryKey
        this.length = length
    }

    fun getCreateString(dbInfo: DBInfo?): String {
        val sql = StringBuilder(50)
        sql.append(" ")
        sql.append(columnName)
        sql.append(" ")
        sql.append(dbInfo!!.getTypeName(type))
        if (length > 0) {
            sql.append("(")
            sql.append(length)
            sql.append(")")
        }
        if (!isNullable) sql.append(" NOT NULL")
        if (isPrimaryKey) sql.append(" PRIMARY KEY")
        return sql.toString()
    }
}
