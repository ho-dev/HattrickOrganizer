package core.db

import core.model.enums.MatchType
import core.model.match.MatchLineupTeam
import core.model.match.MatchTacticType
import core.model.match.MatchTeamAttitude
import core.model.match.StyleOfPlay
import core.util.HOLogger
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function
import kotlin.math.min

class MatchLineupTeamTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupTeam?)!!.matchId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupTeam?)!!.matchId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchTyp")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupTeam?)!!.getMatchType().id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as MatchLineupTeam?)!!.matchType = MatchType.getById(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TeamID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupTeam?)!!.teamID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupTeam?)!!.teamID = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Erfahrung")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupTeam?)!!.experience }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupTeam?)!!.experience = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TeamName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupTeam?)!!.teamName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchLineupTeam?)!!.teamName = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(265).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("StyleOfPlay")
                .setGetter(Function<Any?, Any?> { o: Any? ->
                    StyleOfPlay.toInt(
                        (o as MatchLineupTeam?)!!.styleOfPlay
                    )
                }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                    (o as MatchLineupTeam?)!!.styleOfPlay = StyleOfPlay.fromInt(v as Int?)
                }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Attitude")
                .setGetter(Function<Any?, Any?> { o: Any? ->
                    MatchTeamAttitude.toInt(
                        (o as MatchLineupTeam?)!!.matchTeamAttitude
                    )
                }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                    (o as MatchLineupTeam?)!!.matchTeamAttitude = MatchTeamAttitude.fromInt(v as Int?)
                }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Tactic")
                .setGetter(Function<Any?, Any?> { o: Any? ->
                    MatchTacticType.toInt(
                        (o as MatchLineupTeam?)!!.matchTacticType
                    )
                }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                    (o as MatchLineupTeam?)!!.matchTacticType = MatchTacticType.fromInt(v as Int?)
                }).setType(
                Types.INTEGER
            ).isNullable(true).build()
        )
    }

    protected override val constraintStatements: Array<String?>
        protected get() = arrayOf(
            "  PRIMARY KEY (" + columns[0].columnName + "," + columns[1].columnName + "," + columns[2].columnName + ")"
        )

    fun loadMatchLineupTeam(iMatchType: Int, matchID: Int, teamID: Int): MatchLineupTeam? {
        return loadOne(MatchLineupTeam::class.java, matchID, iMatchType, teamID)
    }

    fun deleteMatchLineupTeam(team: MatchLineupTeam) {
        executePreparedDelete(team.matchId, team.getMatchType().id, team.teamID)
    }

    fun storeMatchLineupTeam(team: MatchLineupTeam?) {
        if (team != null) {
            team.stored = isStored(team.matchId, team.getMatchType().id, team.teamID)
            store(team)
        }
    }

    private val loadTemplateStatementBuilder = PreparedSelectStatementBuilder(
        this,
        " WHERE TeamID<0 AND MATCHTYP=0 AND MATCHID=-1"
    )

    init {
        idColumns = 3
    }

    fun getTemplateMatchLineupTeam(): List<MatchLineupTeam?> {
        return load(
            MatchLineupTeam::class.java,
            adapter!!.executePreparedQuery(loadTemplateStatementBuilder.getStatement())
        )
    }

    fun getTemplateMatchLineupTeamNextNumber(): Int {
        try {
            val sql = "SELECT MIN(TEAMID) FROM $tableName WHERE MatchTyp=0 AND MATCHID=-1"
            val rs = adapter!!.executeQuery(sql)
            if (rs != null) {
                if (rs.next()) {
                    return min(-1.0, (rs.getInt(1) - 1).toDouble()).toInt()
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, e)
        }
        return -1
    }

    companion object {
        /**
         * tablename
         */
        const val TABLENAME = "MATCHLINEUPTEAM"
    }
}