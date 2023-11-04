package core.db

import core.db.DBManager.PreparedStatementBuilder
import core.model.enums.MatchType
import core.model.match.MatchEvent
import core.model.match.Matchdetails
import core.util.HODateTime
import core.util.HOLogger
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.stream.Collectors

internal class MatchDetailsTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.matchID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.matchID = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchTyp")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.matchType.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as Matchdetails?)!!.matchType = MatchType.getById(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ArenaId")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.arenaID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.arenaID = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ArenaName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.arenaName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Matchdetails?)!!.arenaName = v as String? })
                .setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Fetchdatum")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.fetchDatum.toDbTimestamp() })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Matchdetails?)!!.fetchDatum = v as HODateTime? })
                .setType(
                    Types.TIMESTAMP
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestTeamName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Matchdetails?)!!.setGastName(v as String?) })
                .setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestTeamId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.setGastId(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastEinstellung")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestEinstellung }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestEinstellung = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastTore")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestGoals }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestGoals = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastLeftAtt")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestLeftAtt }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestLeftAtt = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastLeftDef")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestLeftDef }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestLeftDef = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastMidAtt")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestMidAtt }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestMidAtt = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastMidDef")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestMidDef }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestMidDef = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastMidfield")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestMidfield }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestMidfield = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastRightAtt")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestRightAtt }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestRightAtt = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastRightDef")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestRightDef }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestRightDef = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastTacticSkill")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestTacticSkill }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestTacticSkill = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastTacticType")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestTacticType }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestTacticType = v as Int }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GASTHATSTATS")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.guestHatStats }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.guestHatStats = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeTeamName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Matchdetails?)!!.setHeimName(v as String?) })
                .setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimId")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeTeamId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.setHeimId(v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimEinstellung")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeEinstellung }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeEinstellung = v as Int }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimTore")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeGoals }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeGoals = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimLeftAtt")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeLeftAtt }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeLeftAtt = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimLeftDef")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeLeftDef }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeLeftDef = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimMidAtt")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeMidAtt }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeMidAtt = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimMidDef")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeMidDef }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeMidDef = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimMidfield")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeMidfield }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeMidfield = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimRightAtt")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeRightAtt }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeRightAtt = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimRightDef")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeRightDef }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeRightDef = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimTacticSkill")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeTacticSkill }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeTacticSkill = v as Int }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimTacticType")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeTacticType }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeTacticType = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HEIMHATSTATS")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.homeHatStats }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.homeHatStats = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SpielDatum")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.matchDate.toDbTimestamp() })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Matchdetails?)!!.setSpielDatum(v as HODateTime?) })
                .setType(
                    Types.TIMESTAMP
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("WetterId")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.wetterId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.wetterId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Zuschauer")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.zuschauer }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.zuschauer = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Matchreport")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.matchreport }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Matchdetails?)!!.matchreport = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(20000).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("RegionID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.regionId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.regionId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("soldTerraces")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.soldTerraces }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.soldTerraces = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("soldBasic")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.soldBasic }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.soldBasic = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("soldRoof")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.soldRoof }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.soldRoof = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("soldVIP")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.soldVIP }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as Matchdetails?)!!.soldVIP = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("RatingIndirectSetPiecesDef").setGetter(
                Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.ratingIndirectSetPiecesDef }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Matchdetails?)!!.setRatingIndirectSetPiecesDef(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("RatingIndirectSetPiecesAtt").setGetter(
                Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.ratingIndirectSetPiecesAtt }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Matchdetails?)!!.setRatingIndirectSetPiecesAtt(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HomeGoal0")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getHomeGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED) })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as Matchdetails?)!!.setHomeGoalsInPart(
                            MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED,
                            v as Int?
                        )
                    }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HomeGoal1")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getHomeGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF) })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as Matchdetails?)!!.setHomeGoalsInPart(
                            MatchEvent.MatchPartId.FIRST_HALF,
                            v as Int?
                        )
                    }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HomeGoal2")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getHomeGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF) })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as Matchdetails?)!!.setHomeGoalsInPart(
                            MatchEvent.MatchPartId.SECOND_HALF,
                            v as Int?
                        )
                    }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HomeGoal3")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getHomeGoalsInPart(MatchEvent.MatchPartId.OVERTIME) })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as Matchdetails?)!!.setHomeGoalsInPart(
                            MatchEvent.MatchPartId.OVERTIME,
                            v as Int?
                        )
                    }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HomeGoal4")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getHomeGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST) })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as Matchdetails?)!!.setHomeGoalsInPart(
                            MatchEvent.MatchPartId.PENALTY_CONTEST,
                            v as Int?
                        )
                    }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GuestGoal0")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getGuestGoalsInPart(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED) })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as Matchdetails?)!!.setGuestGoalsInPart(
                            MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED,
                            v as Int?
                        )
                    }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GuestGoal1")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getGuestGoalsInPart(MatchEvent.MatchPartId.FIRST_HALF) })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as Matchdetails?)!!.setGuestGoalsInPart(
                            MatchEvent.MatchPartId.FIRST_HALF,
                            v as Int?
                        )
                    }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GuestGoal2")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getGuestGoalsInPart(MatchEvent.MatchPartId.SECOND_HALF) })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as Matchdetails?)!!.setGuestGoalsInPart(
                            MatchEvent.MatchPartId.SECOND_HALF,
                            v as Int?
                        )
                    }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GuestGoal3")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getGuestGoalsInPart(MatchEvent.MatchPartId.OVERTIME) })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as Matchdetails?)!!.setGuestGoalsInPart(
                            MatchEvent.MatchPartId.OVERTIME,
                            v as Int?
                        )
                    }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GuestGoal4")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getGuestGoalsInPart(MatchEvent.MatchPartId.PENALTY_CONTEST) })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as Matchdetails?)!!.setGuestGoalsInPart(
                            MatchEvent.MatchPartId.PENALTY_CONTEST,
                            v as Int?
                        )
                    }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HomeFormation")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getFormation(true) }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Matchdetails?)!!.setHomeFormation(v as String?) })
                .setType(
                    Types.VARCHAR
                ).setLength(5).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AwayFormation")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as Matchdetails?)!!.getFormation(false) }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as Matchdetails?)!!.setAwayFormation(v as String?) })
                .setType(
                    Types.VARCHAR
                ).setLength(5).isNullable(true).build()
        )
    }

     override val constraintStatements: Array<String?>
         get() = arrayOf(
            "  PRIMARY KEY (MATCHID, MATCHTYP)"
        )
    override val createIndexStatement: Array<String?>
         get() = arrayOf(
            "CREATE INDEX IMATCHDETAILS_1 ON $tableName(MatchID)",
            "CREATE INDEX matchdetails_heimid_idx ON $tableName (HeimId)",
            "CREATE INDEX matchdetails_gastid_idx ON $tableName (GastID)"
        )

    fun loadMatchDetails(iMatchType: Int, matchId: Int): Matchdetails {
        var ret = loadOne(Matchdetails::class.java, matchId, iMatchType)
        if (ret == null) {
            ret = Matchdetails()
        }
        return ret
    }

    fun storeMatchDetails(details: Matchdetails?) {
        if (details != null) {
            details.stored = isStored(details.matchID, details.matchType.id)
            store(details)
        }
    }

    private val isMatchIFKRatingAvailableStatementBuilder = PreparedStatementBuilder(
        "SELECT RatingIndirectSetPiecesDef FROM $tableName WHERE MatchId=?"
    )

    fun isMatchIFKRatingAvailable(matchId: Int): Boolean {
        try {
            val rs = adapter.executePreparedQuery(isMatchIFKRatingAvailableStatementBuilder.getStatement(), matchId)!!
            if (rs.next()) {
                rs.getInt(1)
                return !rs.wasNull()
            }
        } catch (e: Exception) {
            HOLogger.instance().log(
                javaClass,
                "DatenbankZugriff.isMatchIFKRatingAvailable : $e"
            )
        }
        return false
    }

    private val deleteYouthMatchDetailsBeforeStatementBuilder =
        PreparedDeleteStatementBuilder(this, "WHERE MATCHTYP IN $placeHolderYouthMatchTypes AND SPIELDATUM<?")

    fun deleteYouthMatchDetailsBefore(before: Timestamp) {
        try {
            val params = ArrayList<Any>()
            params.addAll(MatchType.getYouthMatchType().stream().map { obj: MatchType -> obj.id }.toList())
            params.add(before)
            adapter.executePreparedUpdate(
                deleteYouthMatchDetailsBeforeStatementBuilder.getStatement(),
                *params.toTypedArray()
            )
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "DB.deleteMatchLineupsBefore Error$e")
        }
    }

    private val getLastYouthMatchDateStatementBuilder = PreparedStatementBuilder(
        "select max(SpielDatum) from $tableName WHERE MATCHTYP IN $placeHolderYouthMatchTypes"
    )

    init {
        idColumns = 2
    }

    fun getLastYouthMatchDate(): Timestamp? {
            try {
                val rs = adapter.executePreparedQuery(
                    getLastYouthMatchDateStatementBuilder.getStatement(),
                    *MatchType.getYouthMatchType().stream().map { obj: MatchType -> obj.id }
                        .toArray())!!
                if (rs.next()) {
                    return rs.getTimestamp(1)
                }
            } catch (ignored: Exception) {
            }
            return null
        }

    companion object {
        const val TABLENAME = "MATCHDETAILS"
        private var placeHolderYouthMatchTypes: String? = null
             get() {
                if (field == null) {
                    val youthMatchTypes = MatchType.getYouthMatchType()
                    val placeholders =
                        youthMatchTypes.stream().map { _ -> "?" }.collect(Collectors.joining(","))
                    field = "($placeholders)"
                }
                return field
            }
    }
}
