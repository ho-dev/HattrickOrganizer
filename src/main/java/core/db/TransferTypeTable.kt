package core.db

import module.transfer.TransferType
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class TransferTypeTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PLAYER_ID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TransferType?)!!.playerId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TransferType?)!!.playerId = v as Int })
                .setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TYPE")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TransferType?)!!.transferType }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as TransferType?)!!.transferType = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build()
        )
    }

    fun storeTransferType(type: TransferType) {
        type.stored = isStored(type.playerId)
        store(type)
    }

    fun loadTransferType(playerId: Int): TransferType? {
        return loadOne(TransferType::class.java, playerId)
    }

    companion object {
        const val TABLENAME = "TRANSFERTYPE"
    }
}
