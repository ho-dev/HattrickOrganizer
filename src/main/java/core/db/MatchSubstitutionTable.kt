package core.db

import core.model.enums.MatchType
import module.lineup.substitution.model.GoalDiffCriteria
import module.lineup.substitution.model.MatchOrderType
import module.lineup.substitution.model.RedCardCriteria
import module.lineup.substitution.model.Substitution
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class MatchSubstitutionTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    init {
        idColumns = 3
    }

    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.matchId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Substitution?)!!.matchId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchTyp")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.matchType.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as Substitution?)!!.matchType = MatchType.getById(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TeamID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.teamId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Substitution?)!!.teamId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PlayerOrderID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.playerOrderId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Substitution?)!!.playerOrderId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PlayerIn")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.objectPlayerID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Substitution?)!!.setObjectPlayerID(v as Int) })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PlayerOut")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.subjectPlayerID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Substitution?)!!.setSubjectPlayerID(v as Int) })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("OrderType")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.orderType.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as Substitution?)!!.orderType = MatchOrderType.fromInt(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchMinuteCriteria")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.matchMinuteCriteria }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as Substitution?)!!.matchMinuteCriteria = (v as Int).toByte()
                        .toInt()
                }).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Pos")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.roleId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Substitution?)!!.setRoleId((v as Int).toByte()) })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Behaviour")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.behaviour }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Substitution?)!!.behaviour = (v as Int).toByte() })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Card")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.redCardCriteria.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as Substitution?)!!.redCardCriteria = RedCardCriteria.getById((v as Int).toByte())
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Standing")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Substitution?)!!.standing.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as Substitution?)!!.standing = GoalDiffCriteria.getById((v as Int).toByte())
                }).setType(
                Types.INTEGER
            ).isNullable(false).build()
        )
    }

    override val createIndexStatement: Array<String?>
        get() = arrayOf(
            "CREATE INDEX IMATCHSUBSTITUTION_1 ON $tableName(PlayerOrderID)",
            "CREATE INDEX IMATCHSUBSTITUTION_0 ON $tableName(MatchID,MatchTyp,TeamID)"
        )

    /**
     * Returns an array with substitution belonging to the match-team.
     *
     * @param teamId  The teamId for the team in question
     * @param matchId The matchId for the match in question
     */
    fun getMatchSubstitutionsByMatchTeam(iMatchType: Int, teamId: Int, matchId: Int): List<Substitution?> {
        return load(Substitution::class.java, matchId, iMatchType, teamId)
    }

    /**
     * Stores the substitutions in the database. The ID for each substitution
     * must be unique for the match. All previous substitutions for the
     * team/match combination will be deleted.
     */
    fun storeMatchSubstitutionsByMatchTeam(matchType: MatchType, matchId: Int, teamId: Int, subs: List<Substitution?>) {
        if (matchId == DUMMY || teamId == DUMMY) {
            // Rather not...
            return
        }
        executePreparedDelete(matchId, matchType.id, teamId)
        for (sub in subs) {
            if (sub == null) {
                continue
            }
            sub.matchId = matchId
            sub.matchType = matchType
            sub.teamId = teamId
            sub.stored = false
            store(sub)
        }
    }

    companion object {
        /**
         * tablename
         */
        const val TABLENAME = "MATCHSUBSTITUTION"

        // Dummy value for ids not used (hrf, team, match)
        private const val DUMMY = -101
    }
}
