package core.db

import core.model.HOVerwaltung
import core.util.HODateTime
import core.util.HOLogger
import module.ifa.IfaMatch
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class IfaMatchTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MATCHID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as IfaMatch?)!!.matchId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as IfaMatch?)!!.matchId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchTyp")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as IfaMatch?)!!.matchTyp }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as IfaMatch?)!!.matchTyp = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PLAYEDDATE")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as IfaMatch?)!!.playedDate.toDbTimestamp() }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as IfaMatch?)!!.playedDate = v as HODateTime? }).setType(
                Types.TIMESTAMP
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HOMETEAMID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as IfaMatch?)!!.homeTeamId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as IfaMatch?)!!.homeTeamId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AWAYTEAMID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as IfaMatch?)!!.awayTeamId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as IfaMatch?)!!.awayTeamId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HOMETEAMGOALS")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as IfaMatch?)!!.homeTeamGoals }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as IfaMatch?)!!.homeTeamGoals = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AWAYTEAMGOALS")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as IfaMatch?)!!.awayTeamGoals }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as IfaMatch?)!!.awayTeamGoals = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HOME_LEAGUEID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as IfaMatch?)!!.homeLeagueId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as IfaMatch?)!!.homeLeagueId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AWAY_LEAGUEID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as IfaMatch?)!!.awayLeagueId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as IfaMatch?)!!.awayLeagueId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build()
        )
    }

    protected override val constraintStatements: Array<String?>
        get() = arrayOf(" PRIMARY KEY (MATCHID, MATCHTYP)")

    fun isMatchInDB(matchId: Int, matchTyp: Int): Boolean {
        val match = loadOne(IfaMatch::class.java, matchId, matchTyp)
        return match != null
    }

    fun getLastMatchDate(): Timestamp? {
        val select = "SELECT MAX(PLAYEDDATE) FROM $tableName"
        val rs = adapter.executeQuery(select)
        try {
            if (rs != null && rs.next()) {
                return rs.getTimestamp(1)
            }
        } catch (e: Exception) {
            HOLogger.instance().error(this.javaClass, e)
        }
        return null
    }

    private val getHomeMatchesStatementBuilder =
        PreparedSelectStatementBuilder(this, "WHERE HOMETEAMID=? ORDER BY AWAY_LEAGUEID ASC")
    private val getAwayMatchesStatementBuilder =
        PreparedSelectStatementBuilder(this, "WHERE AWAYTEAMID=? ORDER BY HOME_LEAGUEID ASC")

    init {
        idColumns = 2
    }

    fun getMatches(home: Boolean): Array<IfaMatch?> {
        val list = load(
            IfaMatch::class.java,
            adapter.executePreparedQuery(
                if (home) getHomeMatchesStatementBuilder.getStatement() else getAwayMatchesStatementBuilder.getStatement(),
                HOVerwaltung.instance().model.getBasics().teamId
            )
        )
        return list.toTypedArray<IfaMatch?>()
    }

    fun insertMatch(match: IfaMatch?) {
        store(match)
    }

    override fun createPreparedDeleteStatementBuilder(): PreparedDeleteStatementBuilder {
        return PreparedDeleteStatementBuilder(this, "")
    }

    fun deleteAllMatches() {
        executePreparedDelete()
    }

    companion object {
        const val TABLENAME = "IFA_MATCHES"
    }
}
