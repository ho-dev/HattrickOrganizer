package core.db

import core.util.HODateTime
import module.nthrf.NtTeamDetails
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class NtTeamTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.hrfId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setHrfId(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TEAM_ID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.teamId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setTeamId(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MORALE")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.morale }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.morale = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SELFCONFIDENCE")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.selfConfidence }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.selfConfidence = v as Int? })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("xp253")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.xp253 }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setXp253(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("xp343")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.xp343 }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setXp343(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("xp352")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.xp352 }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setXp352(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("xp433")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.xp433 }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setXp433(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("xp442")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.xp442 }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setXp442(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("xp451")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.xp451 }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setXp451(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("xp523")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.xp523 }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setXp523(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("xp532")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.xp532 }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setXp532(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("xp541")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.xp541 }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setXp541(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("xp550")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.xp550 }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as NtTeamDetails?)!!.setXp550(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NAME")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.teamName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.teamName = v as String? })
                .setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SHORTNAME")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.teamNameShort }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.teamNameShort = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(127).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("COACHID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.coachId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.setCoachId(v as Int?) })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("COACHNAME")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.coachName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.coachName = v as String? }).setType(
                Types.VARCHAR
            ).setLength(127).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LEAGUEID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.leagueId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.setLeagueId(v as Int?) })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LEAGUENAME")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.leagueName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.leagueName = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(127).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SUPPORTERPOPULARITY")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.supportersPopularity }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.setSupportersPopularity(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("RATING")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.ratingScore }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.setRatingScore(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FANCLUBSIZE")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.fanclubSize }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.setFanclubSize(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("RANK")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.rank }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as NtTeamDetails?)!!.setRank(v as Int?) })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FETCHEDDATE")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as NtTeamDetails?)!!.fetchedDate.toDbTimestamp() })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as NtTeamDetails?)!!.fetchedDate = v as HODateTime?
                    }).setType(
                Types.TIMESTAMP
            ).isNullable(true).build()
        )
    }

    protected override val constraintStatements: Array<String?>
        protected get() = arrayOf(
            "  PRIMARY KEY (HRF_ID, TEAM_ID)"
        )

    fun storeNTTeam(ntTeamDetails: NtTeamDetails?) {
        ntTeamDetails?.let { store(it) }
    }

    private val selectBeforeStatementBuilder = PreparedSelectStatementBuilder(
        this,
        "WHERE TEAM_ID=? AND MORALE IS NOT NULL AND FETCHEDDATE<? ORDER BY HRF_ID DESC LIMIT 1"
    )
    private val selectTeamStatementBuilder =
        PreparedSelectStatementBuilder(this, "WHERE TEAM_ID=? AND MORALE IS NOT NULL ORDER BY HRF_ID DESC LIMIT 1")

    fun loadNTTeam(teamId: Int, matchDate: Timestamp?): NtTeamDetails? {
        return if (matchDate != null) {
            loadOne(
                NtTeamDetails::class.java,
                adapter!!.executePreparedQuery(selectBeforeStatementBuilder.getStatement(), teamId, matchDate)
            )
        } else {
            loadOne(
                NtTeamDetails::class.java,
                adapter!!.executePreparedQuery(selectTeamStatementBuilder.getStatement(), teamId)
            )
        }
    }

    fun loadNTTeams(hrfId: Int): List<NtTeamDetails?>? {
        return load(NtTeamDetails::class.java, hrfId)
    }

    companion object {
        const val TABLENAME = "NTTEAM"
    }
}