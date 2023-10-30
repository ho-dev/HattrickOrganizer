package core.db

import module.teamAnalyzer.manager.PlayerDataManager
import module.teamAnalyzer.vo.PlayerInfo
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

/**
 * The Table UserConfiguration contain all User properties. CONFIG_KEY = Primary
 * Key, fieldname of the class CONFIG_VALUE = value of the field, save as
 * VARCHAR. Convert to right datatype if loaded
 *
 * @since 1.36
 */
internal class TAPlayerTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PLAYERID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.playerId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as PlayerInfo?)!!.playerId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("WEEK")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.week }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as PlayerInfo?)!!.week = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TEAMID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.teamId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as PlayerInfo?)!!.teamId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("STATUS")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.status }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.setStatus((v as Int?)!!) })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SPECIALEVENT")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.specialEvent }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.specialEvent = (v as Int?)!! })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TSI")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.tsi }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.tsi = (v as Int?)!! })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FORM")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.form }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.form = (v as Int?)!! })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AGE")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.age }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.age = (v as Int?)!! })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("EXPERIENCE")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.experience }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.experience = (v as Int?)!! })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SALARY")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.salary }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.salary = (v as Int?)!! })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("STAMINA")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.stamina }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.stamina = (v as Int?)!! })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MOTHERCLUBBONUS")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.motherClubBonus }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.motherClubBonus = (v as Boolean?)!! })
                .setType(
                    Types.BOOLEAN
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LOYALTY")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.loyalty }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.loyalty = (v as Int?)!! })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NAME")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as PlayerInfo?)!!.name }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as PlayerInfo?)!!.name = v as String? })
                .setType(Types.VARCHAR).setLength(100).isNullable(true).build()
        )
    }

    override val createIndexStatement: Array<String?>
        get() = arrayOf(
            "CREATE INDEX ITA_PLAYER_PLAYERID_WEEK ON " + TABLENAME
                    + " (playerid, week)"
        )

    fun getPlayerInfo(playerId: Int, week: Int, season: Int): PlayerInfo {
        var ret = loadOne(PlayerInfo::class.java, playerId, PlayerDataManager.calcWeekNumber(season, week))
        if (ret == null) ret = PlayerInfo()
        return ret
    }

    private val loadLatestPlayerInfoStatementBuilder = PreparedSelectStatementBuilder(
        this,
        " WHERE PLAYERID = ? ORDER BY WEEK DESC LIMIT 1"
    )

    init {
        idColumns = 2
    }

    /**
     * Returns the specialEvent code for a player
     *
     * @param playerId
     * the playerId
     *
     * @return a numeric code
     */
    fun getLatestPlayerInfo(playerId: Int): PlayerInfo {
        var ret = loadOne(
            PlayerInfo::class.java,
            adapter.executePreparedQuery(loadLatestPlayerInfoStatementBuilder.getStatement(), playerId)
        )
        if (ret == null) ret = PlayerInfo()
        return ret
    }

    /**
     * Add a player to a team
     *
     * @param info PlayerInfo
     */
    fun storePlayer(info: PlayerInfo) {
        val week = PlayerDataManager.calcCurrentWeekNumber()
        info.stored = isStored(info.playerId, week)
        info.week = week
        store(info)
    }

    companion object {
        const val TABLENAME = "TA_PLAYER"
    }
}
