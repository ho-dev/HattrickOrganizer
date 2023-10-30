package core.db

import core.db.DBManager.PreparedStatementBuilder
import core.util.HODateTime
import core.util.HOLogger
import module.series.Spielplan
import java.sql.*
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class SpielplanTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LigaID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Spielplan?)!!.ligaId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Spielplan?)!!.ligaId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Saison")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Spielplan?)!!.saison }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Spielplan?)!!.saison = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LigaName")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Spielplan?)!!.ligaName }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Spielplan?)!!.ligaName = v as String? })
                .setType(Types.VARCHAR).setLength(256).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FetchDate")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Spielplan?)!!.fetchDate.toDbTimestamp() }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Spielplan?)!!.fetchDate = v as HODateTime? }).setType(
                Types.TIMESTAMP
            ).isNullable(false).build()
        )
    }

    private val getAllSpielplaeneStatementBuilder = PreparedSelectStatementBuilder(this, "ORDER BY Saison DESC")

    /**
     * Returns all the game schedules from the database.
     */
    fun getAllSpielplaene(): List<Spielplan?> {
        return load(
            Spielplan::class.java,
            adapter.executePreparedQuery(getAllSpielplaeneStatementBuilder.getStatement())
        )
    }


    /**
     * Gets a game schedule from the database; returns the latest if either param is -1.
     *
     * @param ligaId ID of the series.
     * @param saison Season number.
     */
    fun getSpielplan(ligaId: Int, saison: Int): Spielplan? {
        return loadOne(Spielplan::class.java, ligaId, saison)
    }

    private val getLigaID4SaisonIDStatementBuilder = PreparedStatementBuilder(
        "SELECT LigaID FROM $tableName WHERE Saison=? ORDER BY FETCHDATE DESC LIMIT 1"
    )

    /**
     * Gibt eine Ligaid zu einer Seasonid zurück, oder -1, wenn kein Eintrag in der DB gefunden
     * wurde
     */
    fun getLigaID4SaisonID(seasonid: Int): Int {
        var ligaid = -1
        try {
            val rs = adapter.executePreparedQuery(getLigaID4SaisonIDStatementBuilder.getStatement(), seasonid)!!
            if (rs.next()) {
                ligaid = rs.getInt("LigaID")
            }
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "DatenbankZugriff.getLigaID4SeasonID : $e")
        }
        return ligaid
    }

    /**
     * Saves a game schedule ([Spielplan]) with its associated fixtures.
     *
     * @param plan Spielplan to save.
     */
    fun storeSpielplan(plan: Spielplan?) {
        if (plan != null) {
            plan.stored = isStored(plan.ligaId, plan.saison)
            store(plan)
        }
    }

    private val loadLatestSpielplanStatementBuilder = PreparedSelectStatementBuilder(
        this,
        " ORDER BY FetchDate DESC LIMIT 1"
    )

    init {
        idColumns = 2
    }

    fun getLatestSpieplan(): Spielplan? {
        return loadOne(
            Spielplan::class.java,
            adapter.executePreparedQuery(loadLatestSpielplanStatementBuilder.getStatement())
        )
    }

    /**
     * load all league ids
     */
    fun getAllLigaIDs(): Array<Int?>? {
            val vligaids = Vector<Int>()
            var ligaids: Array<Int?>? = null
            try {
                val sql = "SELECT DISTINCT LigaID FROM SPIELPLAN"
                val rs = adapter.executeQuery(sql)
                while (rs != null && rs.next()) {
                    vligaids.add(rs.getInt("LigaID"))
                }
                ligaids = arrayOfNulls(vligaids.size)
                for (i in vligaids.indices) {
                    ligaids[i] = vligaids[i]
                }
            } catch (e: Exception) {
                HOLogger.instance().log(javaClass, "DatenbankZugriff.getAllLigaIDs : $e")
            }
            return ligaids
        }

    companion object {
        const val TABLENAME = "SPIELPLAN"
    }
}
