package core.db

import core.model.series.Liga
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class LigaTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Liga?)!!.hrfId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Liga?)!!.hrfId = v as Int }).setType(Types.INTEGER)
                .isNullable(false).isPrimaryKey(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LigaName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Liga?)!!.liga }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Liga?)!!.liga = v as String? }).setType(Types.VARCHAR)
                .isNullable(false).setLength(127).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Punkte")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Liga?)!!.punkte }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Liga?)!!.punkte = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ToreFuer")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Liga?)!!.toreFuer }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Liga?)!!.toreFuer = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ToreGegen")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Liga?)!!.toreGegen }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Liga?)!!.toreGegen = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Platz")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Liga?)!!.platzierung }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Liga?)!!.platzierung = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Spieltag")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Liga?)!!.spieltag }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Liga?)!!.spieltag = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build()
        )
    }

     override val createIndexStatement: Array<String?>
         get() = arrayOf(
            "CREATE INDEX ILIGA_1 ON " + tableName + "(" + columns[0].columnName + ")"
        )

    /**
     * store league
     */
    fun saveLiga(hrfId: Int, liga: Liga?) {
        if (liga != null) {
            liga.hrfId = hrfId
            store(liga)
        }
    }

    fun getLiga(hrfID: Int): Liga? {
        return loadOne(Liga::class.java, hrfID)
    }

    companion object {
        /** tablename  */
        const val TABLENAME = "LIGA"
    }
}
