package core.db

import core.model.player.*
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class SpielerNotizenTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SpielerID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Player.Notes?)!!.playerId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Player.Notes?)!!.playerId = v as Int })
                .setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Notiz")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Player.Notes?)!!.note }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Player.Notes?)!!.note = v as String? })
                .setType(Types.VARCHAR).setLength(2048).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Spielberechtigt")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Player.Notes?)!!.isEligibleToPlay }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Player.Notes?)!!.isEligibleToPlay = v as Boolean })
                .setType(
                    Types.BOOLEAN
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TeamInfoSmilie")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Player.Notes?)!!.teamInfoSmilie }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Player.Notes?)!!.teamInfoSmilie = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(127).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ManuellerSmilie")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Player.Notes?)!!.manuelSmilie }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Player.Notes?)!!.manuelSmilie = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(127).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("userPos")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Player.Notes?)!!.userPos }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Player.Notes?)!!.userPos = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("isFired")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Player.Notes?)!!.isFired }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Player.Notes?)!!.isFired = v as Boolean })
                .setType(Types.BOOLEAN).isNullable(false).build()
        )
    }

    fun storeNotes(notes: Player.Notes) {
        notes.stored = isStored(notes.playerId)
        store(notes)
    }

    fun load(playerId: Int): Player.Notes {
        var ret = loadOne(Player.Notes::class.java, playerId)
        if (ret == null) ret = Player.Notes(playerId)
        return ret
    }

    companion object {
        /** tablename  */
        const val TABLENAME = "SPIELERNOTIZ"
    }
}
