package core.db

import core.model.StaffMember
import core.model.StaffType
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class StaffTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HrfID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as StaffMember?)!!.hrfId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as StaffMember?)!!.hrfId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("id")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as StaffMember?)!!.id }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as StaffMember?)!!.id = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("index")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as StaffMember?)!!.index }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as StaffMember?)!!.index = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("stafftype")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as StaffMember?)!!.staffType.id }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any ->
                    (p as StaffMember?)!!.staffType = StaffType.getById(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("level")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as StaffMember?)!!.level }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as StaffMember?)!!.level = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("cost")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as StaffMember?)!!.cost }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as StaffMember?)!!.cost = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("name")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as StaffMember?)!!.name }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as StaffMember?)!!.name = v as String? })
                .setType(Types.VARCHAR).setLength(127).isNullable(false).build()
        )
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder? {
        return PreparedSelectStatementBuilder(this, "WHERE HrfID = ? ORDER BY index")
    }

    fun getStaffByHrfId(hrfId: Int): List<StaffMember?>? {
        return load(StaffMember::class.java, hrfId)
    }

    fun storeStaff(hrfId: Int, list: List<StaffMember>?) {
        if (list == null || hrfId < 0) {
            return
        }
        executePreparedDelete(hrfId)
        var index = 0
        for (staff in list) {
            staff.index = index++
            staff.hrfId = hrfId
            staff.stored = false
            store(staff)
        }
    }

    companion object {
        /** tablename  */
        const val TABLENAME = "STAFF"
    }
}
