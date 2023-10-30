package core.db

import core.model.Tournament.TournamentDetails
import core.util.HODateTime
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class TournamentDetailsTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TournamentId")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.tournamentId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TournamentDetails?)!!.tournamentId = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).isPrimaryKey(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Name")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.name }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as TournamentDetails?)!!.name = v as String? })
                .setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TournamentType")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.tournamentType }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TournamentDetails?)!!.tournamentType = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Season")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.season }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TournamentDetails?)!!.season = (v as Int).toShort() })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LogoUrl")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.logoUrl }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as TournamentDetails?)!!.logoUrl = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(256).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TrophyType")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.trophyType }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TournamentDetails?)!!.trophyType = v as Int }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NumberOfTeams")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.numberOfTeams }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TournamentDetails?)!!.numberOfTeams = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NumberOfGroups")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.numberOfGroups }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TournamentDetails?)!!.numberOfGroups = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastMatchRound")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.lastMatchRound }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any ->
                    (p as TournamentDetails?)!!.lastMatchRound = (v as Int).toShort()
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FirstMatchRoundDate")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.firstMatchRoundDate.toDbTimestamp() })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any? ->
                        (p as TournamentDetails?)!!.firstMatchRoundDate = v as HODateTime?
                    }).setType(
                Types.TIMESTAMP
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NextMatchRoundDate")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.nextMatchRoundDate.toDbTimestamp() })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any? ->
                        (p as TournamentDetails?)!!.nextMatchRoundDate = v as HODateTime?
                    }).setType(
                Types.TIMESTAMP
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("IsMatchesOngoing")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.matchesOngoing }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as TournamentDetails?)!!.matchesOngoing = v as Boolean })
                .setType(
                    Types.BOOLEAN
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Creator_UserID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.creator_UserId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as TournamentDetails?)!!.creator_UserId = v as Int? })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Creator_Loginname")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as TournamentDetails?)!!.creator_Loginname }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? ->
                    (p as TournamentDetails?)!!.creator_Loginname = v as String?
                }).setType(
                Types.VARCHAR
            ).setLength(256).isNullable(true).build()
        )
    }

    fun getTournamentDetails(tournamentId: Int): TournamentDetails? {
        return loadOne(TournamentDetails::class.java, tournamentId)
    }

    /**
     * Store Tournament Details into DB
     */
    fun storeTournamentDetails(details: TournamentDetails?) {
        store(details)
    }

    companion object {
        const val TABLENAME = "TOURNAMENTDETAILS"
    }
}
