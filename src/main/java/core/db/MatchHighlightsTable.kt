package core.db

import core.model.enums.MatchType
import core.model.match.MatchEvent
import core.model.match.Matchdetails
import core.model.match.SourceSystem
import core.util.HODateTime
import core.util.HOLogger
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.stream.Collectors

internal class MatchHighlightsTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.matchId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchEvent?)!!.matchId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchTyp")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.matchType.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as MatchEvent?)!!.matchType = MatchType.getById(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("EVENT_INDEX")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.matchEventIndex }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchEvent?)!!.setMatchEventIndex(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TeamId")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.teamID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchEvent?)!!.teamID = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MATCH_EVENT_ID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.matchEventID.value }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchEvent?)!!.setMatchEventID(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchDate")
                .setGetter(Function<Any?, Any?> { o: Any? ->
                    HODateTime.toDbTimestamp(
                        (o as MatchEvent?)!!.matchDate
                    )
                }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchEvent?)!!.matchDate = v as HODateTime? })
                .setType(
                    Types.TIMESTAMP
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Minute")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.minute }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchEvent?)!!.minute = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SpielerId")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.playerId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchEvent?)!!.playerId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SpielerName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.playerName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchEvent?)!!.playerName = v as String? })
                .setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SpielerHeim")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.spielerHeim }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchEvent?)!!.spielerHeim = v as Boolean })
                .setType(Types.BOOLEAN).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GehilfeID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.assistingPlayerId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchEvent?)!!.assistingPlayerId = v as Int }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GehilfeName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.assistingPlayerName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchEvent?)!!.assistingPlayerName = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GehilfeHeim")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.gehilfeHeim }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchEvent?)!!.gehilfeHeim = v as Boolean })
                .setType(Types.BOOLEAN).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("EventText")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.eventText }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchEvent?)!!.eventText = v as String? })
                .setType(Types.VARCHAR).setLength(5000).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("INJURY_TYPE")
                .setGetter(Function<Any?, Any?> { o: Any? ->
                    Matchdetails.eInjuryType.toInteger(
                        (o as MatchEvent?)!!.getM_eInjuryType()
                    )
                }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchEvent?)!!.setM_eInjuryType(v as Int?) })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchPart")
                .setGetter(Function<Any?, Any?> { o: Any? ->
                    MatchEvent.MatchPartId.toInteger(
                        (o as MatchEvent?)!!.matchPartId
                    )
                }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                    (o as MatchEvent?)!!.matchPartId = MatchEvent.MatchPartId.fromMatchPartId(v as Int?)
                }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("EventVariation")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchEvent?)!!.eventVariation }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchEvent?)!!.eventVariation = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build()
        )
    }

    override val createIndexStatement: Array<String?>
        get() = arrayOf(
            "CREATE INDEX iMATCHHIGHLIGHTS_1 ON $tableName (MatchID)",
            "CREATE INDEX matchhighlights_teamid_idx ON $tableName (TeamId)",
            "CREATE INDEX matchhighlights_eventid_idx ON $tableName (MATCH_EVENT_ID)",
            "SET TABLE $tableName NEW SPACE"
        )

    fun storeMatchHighlights(details: Matchdetails?) {
        if (details != null) {
            // Remove existing entries
            executePreparedDelete(details.matchID, details.matchType.id)
            val vHighlights = details.downloadHighlightsIfMissing()
            for (highlight in vHighlights) {
                highlight.stored = false
                highlight.matchDate = details.matchDate
                highlight.matchType = details.matchType
                highlight.matchId = details.matchID
                store(highlight)
            }
        }
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder? {
        return PreparedSelectStatementBuilder(this, "WHERE MatchId=? AND MatchTyp=? ORDER BY EVENT_INDEX, Minute")
    }

    /**
     * @param matchId the match id
     * @return the match highlights
     */
    fun getMatchHighlights(iMatchType: Int, matchId: Int): List<MatchEvent?>? {
        return load(MatchEvent::class.java, matchId, iMatchType)
    }

    private val deleteYouthMatchHighlightsBeforeStatementBuilder = PreparedDeleteStatementBuilder(
        this,
        deleteYouthMatchHighlightsBeforeStatementSQL
    )

    init {
        idColumns = 2
    }

    private val deleteYouthMatchHighlightsBeforeStatementSQL: String
        private get() {
            val lMatchTypes = MatchType.fromSourceSystem(SourceSystem.valueOf(SourceSystem.YOUTH.value))
            val inValues = lMatchTypes.stream().map { p: MatchType -> p.id.toString() }.collect(Collectors.joining(","))
            return " WHERE MatchTyp IN (" +
                    inValues +
                    ") AND MatchDate IS NOT NULL AND MatchDate<?"
        }

    fun deleteYouthMatchHighlightsBefore(before: Timestamp?) {
        try {
            adapter!!.executePreparedUpdate(deleteYouthMatchHighlightsBeforeStatementBuilder.getStatement(), before)
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "DB.deleteMatchLineupsBefore Error$e")
        }
    }

    companion object {
        const val TABLENAME = "MATCHHIGHLIGHTS"
    }
}
