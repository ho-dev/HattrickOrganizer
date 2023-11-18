package core.db

import core.model.enums.MatchType
import core.model.match.MatchTeamRating
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class MatchTeamRatingTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchTeamRating?)!!.matchId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchTeamRating?)!!.matchId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchTyp")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchTeamRating?)!!.matchTyp.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as MatchTeamRating?)!!.matchTyp = MatchType.getById(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TeamID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchTeamRating?)!!.teamId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchTeamRating?)!!.teamId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FanclubSize")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchTeamRating?)!!.fanclubSize }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchTeamRating?)!!.fanclubSize = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PowerRating")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchTeamRating?)!!.powerRating }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchTeamRating?)!!.setPowerRating(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GlobalRanking")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchTeamRating?)!!.globalRanking }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchTeamRating?)!!.setGlobalRanking(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("RegionRanking")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchTeamRating?)!!.regionRanking }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchTeamRating?)!!.setRegionRanking(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LeagueRanking")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchTeamRating?)!!.leagueRanking }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchTeamRating?)!!.setLeagueRanking(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NumberOfVictories")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchTeamRating?)!!.numberOfVictories }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchTeamRating?)!!.setNumberOfVictories(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NumberOfUndefeated")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchTeamRating?)!!.numberOfUndefeated }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchTeamRating?)!!.setNumberOfUndefeated(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build()
        )
    }

    protected override val constraintStatements: Array<String?>
        get() = arrayOf(" PRIMARY KEY (MATCHID, MATCHTYP, TEAMID)")
    private val loadBothTeamRatingStatementBuilder = PreparedSelectStatementBuilder(
        this,
        "WHERE MatchID = ? AND MatchTyp = ?"
    )

    init {
        idColumns = 3
    }

    fun load(matchID: Int, matchType: Int): List<MatchTeamRating?>? {
        return load(
            MatchTeamRating::class.java,
            adapter.executePreparedQuery(loadBothTeamRatingStatementBuilder.getStatement(), matchID, matchType)
        )
    }

    fun storeTeamRating(teamRating: MatchTeamRating?) {
        if (teamRating != null) {
            teamRating.stored = isStored(teamRating.matchId, teamRating.matchTyp.id, teamRating.teamId)
            store(teamRating)
        }
    }

    companion object {
        const val TABLENAME = "MATCHTEAMRATING"
    }
}
