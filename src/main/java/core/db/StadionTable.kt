package core.db

import tool.arenasizer.Stadium
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class StadionTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.hrfId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.hrfId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).isPrimaryKey(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("StadionName")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.name }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Stadium?)!!.name = (v as String?)!! })
                .setType(Types.VARCHAR).setLength(127).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AnzSteh")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.standing }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.standing = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AnzSitz")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.basicSeating }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.basicSeating = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AnzDach")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.seatingUnderRoof }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.seatingUnderRoof = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AnzLogen")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.vip }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.vip = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AusbauSteh")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.expansionStanding }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.expansionStanding = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AusbauSitz")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.expansionBasicSeating }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.expansionBasicSeating = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AusbauDach")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.expansionSeatingUnderRoof }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.expansionSeatingUnderRoof = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AusbauLogen")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.expansionVip }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.expansionVip = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Ausbau")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.expansion }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.expansion = 0 != v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AusbauKosten")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.expansionCosts }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.expansionCosts = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ArenaID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Stadium?)!!.arenaId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Stadium?)!!.arenaId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build()
        )
    }

    /**
     * save Arena
     *
     * @param hrfId   foreign key of status info
     * @param stadion stadium to store
     */
    fun saveStadion(hrfId: Int, stadion: Stadium?) {
        if (stadion != null) {
            stadion.hrfId = hrfId
            store(stadion)
        }
    }

    fun getStadion(hrfID: Int): Stadium? {
        return loadOne(Stadium::class.java, hrfID)
    }

    companion object {
        const val TABLENAME = "STADION"
    }
}