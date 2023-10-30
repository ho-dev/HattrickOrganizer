package core.db

import core.util.HODateTime
import module.teamAnalyzer.vo.SquadInfo
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class SquadInfoTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TEAMID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.teamId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.teamId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LASTMATCH")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.lastMatchDate.toDbTimestamp() })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as SquadInfo?)!!.lastMatchDate = v as HODateTime? })
                .setType(
                    Types.TIMESTAMP
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FETCHDATE")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.fetchDate.toDbTimestamp() }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as SquadInfo?)!!.fetchDate = v as HODateTime? }).setType(
                Types.TIMESTAMP
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("BRUISED")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.bruisedCount }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.bruisedCount = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("INJURED")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.injuredCount }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.injuredCount = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("INJUREDWEEKS")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.injuredWeeksSum }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.injuredWeeksSum = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("YELLOWCARDS")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.singleYellowCards }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.singleYellowCards = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TWOYELLOWCARDS")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.twoYellowCards }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.twoYellowCards = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SUSPENDED")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.redCards }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.redCards = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TRANSFERLISTED")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.transferListedCount }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.transferListedCount = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TSISUM")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.gettSISum() }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.settSISum(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SALARY")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.salarySum }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.salarySum = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PLAYER")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.playerCount }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.playerCount = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MOTHERCLUB")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as SquadInfo?)!!.homegrownCount }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as SquadInfo?)!!.homegrownCount = v as Int })
                .setType(Types.INTEGER).isNullable(false).build()
        )
    }

    override val createIndexStatement: Array<String?>
        get() = arrayOf(
            "CREATE INDEX ITA_SQUAD_TEAMID_MATCH ON " + TABLENAME
                    + " (TEAMID, LASTMATCH)"
        )

    fun storeSquadInfo(squadInfo: SquadInfo) {
        squadInfo.stored = isStored(squadInfo.teamId, squadInfo.lastMatchDate.toDbTimestamp())
        store(squadInfo)
    }

    private val loadAllSquadInfoStatementBuilder = PreparedSelectStatementBuilder(this, "WHERE TEAMID=?")

    init {
        idColumns = 2
    }

    fun loadSquadInfo(teamId: Int): List<SquadInfo?> {
        return load(
            SquadInfo::class.java,
            adapter.executePreparedQuery(loadAllSquadInfoStatementBuilder.getStatement(), teamId)
        )
    }

    companion object {
        const val TABLENAME = "SQUAD"
    }
}
