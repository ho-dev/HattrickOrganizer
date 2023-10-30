package core.db

import core.model.HOVerwaltung
import core.util.HODateTime
import module.transfer.PlayerTransfer
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class TransferTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    private val getTransferStatements = HashMap<String, PreparedStatement?>()
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("transferid")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.transferId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as PlayerTransfer?)!!.transferId = v as Int })
                .setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("date")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.date.toDbTimestamp() }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerTransfer?)!!.date = v as HODateTime? }).setType(
                Types.TIMESTAMP
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("week")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.week }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as PlayerTransfer?)!!.week = v as Int })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("season")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.season }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as PlayerTransfer?)!!.season = v as Int })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("playerid")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.playerId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as PlayerTransfer?)!!.playerId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("playername")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.playerName }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerTransfer?)!!.playerName = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(127).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("buyerid")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.buyerid }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? ->
                    (p as PlayerTransfer?)!!.setBuyerid(
                        (v as Int?)!!
                    )
                }).setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("buyername")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.buyerName }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerTransfer?)!!.buyerName = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(256).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("sellerid")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.sellerid }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? ->
                    (p as PlayerTransfer?)!!.setSellerid(
                        (v as Int?)!!
                    )
                }).setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("sellername")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.sellerName }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerTransfer?)!!.sellerName = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(256).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("price")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.price }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerTransfer?)!!.setPrice(convertCurrency(v as Int?)) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("marketvalue")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.marketvalue }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerTransfer?)!!.setMarketvalue(convertCurrency(v as Int?)) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("tsi")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerTransfer?)!!.tsi }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerTransfer?)!!.tsi = (v as Int?)!! })
                .setType(Types.INTEGER).isNullable(true).build()
        )
    }

    override val createIndexStatement: Array<String?>
        get() = arrayOf(
            "CREATE INDEX pl_id ON " + tableName + "(" + columns[4].columnName + ")",
            "CREATE INDEX buy_id ON " + tableName + "(" + columns[6].columnName + ")",
            "CREATE INDEX sell_id ON " + tableName + "(" + columns[8].columnName + ")"
        )
    private var currencyRate = 0.0

    /**
     * convert currency value in swedish krone to local currency
     * @param v Integer value to convert
     * @return Integer converted local currency
     */
    private fun convertCurrency(v: Int?): Int? {
        if (v != null) {
            if (currencyRate == 0.0) {
                val xtra = HOVerwaltung.instance().model.getXtraDaten()
                currencyRate = xtra?.currencyRate ?: 1.0
            }
            return (v / currencyRate).toInt()
        }
        return null
    }

    /**
     * Remove a transfer from the HO database
     *
     * @param transferId Transfer ID
     */
    fun removeTransfer(transferId: Int) {
        try {
            executePreparedDelete(transferId)
        } catch (e: Exception) {
            // ignore
        }
    }

    /**
     * Gets requested transfer
     *
     * @param transferId Transfer ID
     */
    fun getTransfer(transferId: Int): PlayerTransfer? {
        return loadOne(PlayerTransfer::class.java, transferId)
    }

    private val getAllTransfersStatementBuilder =
        PreparedSelectStatementBuilder(this, " WHERE playerid = ? ORDER BY date DESC")
    private val getTransfersStatementBuilder =
        PreparedSelectStatementBuilder(this, " WHERE playerid = ? AND (buyerid=? OR sellerid=?) ORDER BY date DESC")

    /**
     * Gets a list of transfers.
     *
     * @param playerid Player id for selecting transfers.
     * @param allTransfers If `false` this method will only return transfers for your
     * own team, otherwise it will return all transfers for the player.
     *
     * @return List of transfers.
     */
    fun getTransfers(playerid: Int, allTransfers: Boolean): List<PlayerTransfer?> {
        if (!allTransfers) {
            val teamid = HOVerwaltung.instance().model.getBasics().teamId
            return load(
                PlayerTransfer::class.java,
                adapter.executePreparedQuery(getTransfersStatementBuilder.getStatement(), playerid, teamid, teamid)
            )
        }
        return load(
            PlayerTransfer::class.java,
            adapter.executePreparedQuery(getAllTransfersStatementBuilder.getStatement(), playerid)
        )
    }

    /**
     * Gets a list of transfers for your own team.
     *
     * @param season Season number for selecting transfers.
     * @param bought `true` to include BUY transfers.
     * @param sold `true` to include SELL transfers.
     *
     * @return List of transfers.
     */
    fun getTransfers(season: Int, bought: Boolean, sold: Boolean): List<PlayerTransfer?> {
        val teamId = HOVerwaltung.instance().model.getBasics().teamId
        return getTransfers(teamId, season, bought, sold)
    }

    /**
     * Gets a list of transfers.
     *
     * @param teamid Team id to select transfers for.
     * @param season Season number for selecting transfers.
     * @param bought `true` to include BUY transfers.
     * @param sold `true` to include SELL transfers.
     *
     * @return List of transfers.
     */
    fun getTransfers(teamid: Int, season: Int, bought: Boolean, sold: Boolean): List<PlayerTransfer?> {
        val sqlStmt = StringBuilder() //$NON-NLS-1$
        val params = ArrayList<Any>()
        var sep = " WHERE"
        if (season != 0) {
            sqlStmt.append(sep).append(" season = ?")
            params.add(season) //$NON-NLS-1$
            sep = " AND"
        }
        if (bought || sold) {
            sqlStmt.append(sep).append(" (") //$NON-NLS-1$
            if (bought) {
                sqlStmt.append(" buyerid = ?")
                params.add(teamid) //$NON-NLS-1$
            }
            if (bought && sold) {
                sqlStmt.append(" OR") //$NON-NLS-1$
            }
            if (sold) {
                sqlStmt.append(" sellerid = ?")
                params.add(teamid) //$NON-NLS-1$
            }
            sqlStmt.append(")") //$NON-NLS-1$
        }
        sqlStmt.append(" ORDER BY date DESC") //$NON-NLS-1$
        val sql = sqlStmt.toString()
        var statement = getTransferStatements[sql]
        if (statement == null) {
            statement = PreparedSelectStatementBuilder(this, sql).getStatement()
            getTransferStatements[sql] = statement
        }
        return load(PlayerTransfer::class.java, adapter.executePreparedQuery(statement, *params.toTypedArray()))
    }

    /**
     * Adds a transfer to the HO database
     *
     * @param transfer Transfer information
     */
    fun storeTransfer(transfer: PlayerTransfer) {
        transfer.stored = isStored(transfer.transferId)
        store(transfer)
    }

    companion object {
        const val TABLENAME = "TRANSFER"
    }
}
