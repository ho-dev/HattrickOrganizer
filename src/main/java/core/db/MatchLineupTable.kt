package core.db

import core.model.enums.MatchType
import core.model.match.MatchLineup
import core.model.match.SourceSystem
import core.util.HOLogger
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.stream.Collectors

class MatchLineupTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineup?)!!.matchID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineup?)!!.matchID = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchTyp")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineup?)!!.matchType.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as MatchLineup?)!!.matchTyp = MatchType.getById(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build()
        )
    }

    override val constraintStatements: Array<String?>
        get() = arrayOf(" PRIMARY KEY (MATCHID, MATCHTYP)")
    override val createIndexStatement: Array<String?>
        get() = arrayOf(
            "CREATE INDEX IMATCHLINEUP_1 ON $tableName(MatchID)"
        )

    fun loadMatchLineup(iMatchType: Int, matchID: Int): MatchLineup? {
        return loadOne(MatchLineup::class.java, matchID, iMatchType)
    }

    fun storeMatchLineup(lineup: MatchLineup?) {
        if (lineup != null) {
            lineup.stored = isStored(lineup.matchID, lineup.matchType.id)
            if (!lineup.stored) {    // do not update, because there is nothing to update (only ids in class)
                store(lineup)
            }
        }
    }

    private val loadYouthMatchLineupsStatementBuilder =
        PreparedSelectStatementBuilder(this, " WHERE MATCHTYP IN (${getMatchTypeInValues()})")

    fun loadYouthMatchLineups(): List<MatchLineup?> {
        return load(
            MatchLineup::class.java,
            adapter.executePreparedQuery(loadYouthMatchLineupsStatementBuilder.getStatement())
        )
    }

    private val deleteYouthMatchLineupsBeforeStatementBuilder =
        PreparedDeleteStatementBuilder(this, getDeleteYouthMatchLineupsBeforeStatementSQL())

    init {
        idColumns = 2
    }

    private fun getDeleteYouthMatchLineupsBeforeStatementSQL(): String {
            val matchTypes = getMatchTypeInValues()
            return " WHERE MATCHTYP IN (" +
                    matchTypes +
                    ") AND MATCHID IN (SELECT MATCHID FROM  MATCHDETAILS WHERE SpielDatum<? AND MATCHTYP IN (" +
                    matchTypes + "))"
        }

    fun deleteYouthMatchLineupsBefore(before: Timestamp?) {
        try {
            adapter.executePreparedUpdate(deleteYouthMatchLineupsBeforeStatementBuilder.getStatement(), before)
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "DB.deleteMatchLineupsBefore Error$e")
        }
    }

    private fun getMatchTypeInValues(): String {
        return MatchType.fromSourceSystem(SourceSystem.YOUTH).stream().map { i: MatchType -> i.id.toString() }
            .collect(Collectors.joining(","))
    }

    companion object {
        /** tablename  */
        const val TABLENAME = "MATCHLINEUP"
    }
}
