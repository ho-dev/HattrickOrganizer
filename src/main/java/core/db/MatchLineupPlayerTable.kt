package core.db

import core.db.DBManager.PreparedStatementBuilder
import core.model.enums.MatchType
import core.model.match.MatchLineupPosition
import core.model.player.IMatchRoleID
import core.model.series.Paarung
import core.util.HOLogger
import java.sql.*
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function
import kotlin.math.max
import kotlin.math.min

class MatchLineupPlayerTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.matchId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupPosition?)!!.matchId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchTyp")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.matchType.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as MatchLineupPosition?)!!.matchType = MatchType.getById(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TeamID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.teamId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupPosition?)!!.teamId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SpielerID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.playerId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupPosition?)!!.playerId = v as Int }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("RoleID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.roleId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupPosition?)!!.roleId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Taktik")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.behaviour }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as MatchLineupPosition?)!!.behaviour = (v as Int).toByte()
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("VName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.getSpielerVName() })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as MatchLineupPosition?)!!.spielerVName = v as String?
                    }).setType(
                Types.VARCHAR
            ).setLength(255).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NickName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.getNickName() }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchLineupPosition?)!!.nickName = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(255).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Name")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.getSpielerName() })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as MatchLineupPosition?)!!.spielerName = v as String?
                    }).setType(
                Types.VARCHAR
            ).setLength(255).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Rating")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.rating }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as MatchLineupPosition?)!!.rating = (v as Float).toDouble()
                }).setType(
                Types.REAL
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HoPosCode")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.getHoPosCode() }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupPosition?)!!.hoPosCode = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("STATUS")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.status }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupPosition?)!!.status = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("RatingStarsEndOfMatch")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.ratingStarsEndOfMatch })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any ->
                        (o as MatchLineupPosition?)!!.ratingStarsEndOfMatch = (v as Float).toDouble()
                    }).setType(
                Types.REAL
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("StartPosition")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.startPosition }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupPosition?)!!.startPosition = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("StartBehaviour")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.startBehavior }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchLineupPosition?)!!.startBehavior = v as Int })
                .setType(
                    Types.INTEGER
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("StartSetPieces")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchLineupPosition?)!!.isStartSetPiecesTaker })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchLineupPosition?)!!.setStartSetPiecesTaker(v as Boolean?) })
                .setType(
                    Types.BOOLEAN
                ).isNullable(true).build()
        )
    }

    override val createIndexStatement: Array<String?>
        get() = arrayOf(
            "CREATE INDEX iMATCHLINEUPPLAYER_1 ON $tableName(SpielerID)",
            "CREATE INDEX iMATCHLINEUPPLAYER_2 ON $tableName(MatchID,TeamID)",
            "SET TABLE $tableName NEW SPACE"
        )

    /**
     * Returns a list of ratings the player has played on: 0: Max,  1: Min,  2: Average,  3: posid
     */
    fun getAllRatings(playerID: Int): Vector<FloatArray> {
        val ratings = Vector<FloatArray>()

        //Iterate over possible combinations of position / behaviours
        for (i in IMatchRoleID.aPositionBehaviours) {
            val temp = getPlayerRatingForPosition(playerID, i)

            //Min found a value for the pos -> max> 0
            if (temp[0] > 0) {
                // Fill in the first value instead of the current value with the posid
                temp[3] = i.toFloat()
                ratings.add(temp)
            }
        }
        return ratings
    }

    private val getBewertungen4PlayerStatementBuilder = PreparedStatementBuilder(
        "SELECT MatchID, Rating FROM $tableName WHERE SpielerID=?"
    )

    /**
     * Gibt die beste, schlechteste und durchschnittliche Bewertung für den Player, sowie die
     * Anzahl der Bewertungen zurück // Match
     */
    fun getBewertungen4Player(spielerid: Int): FloatArray {
        //Max, Min, Durchschnitt
        val bewertungen = floatArrayOf(0f, 0f, 0f, 0f)
        try {
            val rs = adapter.executePreparedQuery(getBewertungen4PlayerStatementBuilder.getStatement(), spielerid)!!
            var i = 0
            while (rs.next()) {
                val rating = rs.getFloat("Rating")
                if (rating > -1) {
                    bewertungen[0] = max(bewertungen[0].toDouble(), rating.toDouble()).toFloat()
                    if (bewertungen[1] == 0f) {
                        bewertungen[1] = rating
                    }
                    bewertungen[1] = min(bewertungen[1].toDouble(), rating.toDouble()).toFloat()
                    bewertungen[2] += rating
                    i++
                }
            }
            if (i > 0) {
                bewertungen[2] = bewertungen[2] / i
            }
            bewertungen[3] = i.toFloat()
            //HOLogger.instance().log(getClass(),"Ratings     : " + i + " - " + bewertungen[0] + " / " + bewertungen[1] + " / " + bewertungen[2] + " / / " + bewertungen[3]);
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "DatenbankZugriff.getBewertungen4Player : $e")
        }
        return bewertungen
    }

    private val getPlayerRatingForPositionStatementBuilder = PreparedStatementBuilder(
        "SELECT MatchID, Rating FROM $tableName WHERE SpielerID=? AND HoPosCode=?"
    )

    /**
     * Returns the best, worst, and average rating for the player, as well as the number of ratings // match
     *
     * @param playerId Spielerid
     * @param position  Usere positionscodierung mit taktik
     */
    fun getPlayerRatingForPosition(playerId: Int, position: Int): FloatArray {
        //Max, Min, average
        val starsStatistics = floatArrayOf(0f, 0f, 0f, 0f)
        try {
            val rs = adapter.executePreparedQuery(
                getPlayerRatingForPositionStatementBuilder.getStatement(),
                playerId,
                position
            )!!
            var i = 0
            while (rs.next()) {
                val rating = rs.getFloat("Rating")
                if (rating > -1) {
                    starsStatistics[0] = max(starsStatistics[0].toDouble(), rating.toDouble()).toFloat()
                    if (starsStatistics[1] == 0f) {
                        starsStatistics[1] = rating
                    }
                    starsStatistics[1] = min(starsStatistics[1].toDouble(), rating.toDouble()).toFloat()
                    starsStatistics[2] += rating
                    i++
                }
            }
            if (i > 0) {
                starsStatistics[2] = starsStatistics[2] / i
            }
            starsStatistics[3] = i.toFloat()

        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "DatenbankZugriff.getPlayerRatingForPosition : $e")
        }
        return starsStatistics
    }

    fun storeMatchLineupPlayers(
        matchLineupPositions: List<MatchLineupPosition>?,
        matchType: MatchType,
        matchID: Int,
        teamID: Int
    ) {
        if (matchLineupPositions != null) {
            executePreparedDelete(matchID, matchType.id, teamID)
            for (p in matchLineupPositions) {
                p.matchId = matchID
                p.matchType = matchType
                p.teamId = teamID
                p.stored = false // replace (if record was available in database, it has to be deleted before storing)
                store(p)
            }
        }
    }

    fun getMatchLineupPlayers(matchID: Int, matchType: MatchType, teamID: Int): List<MatchLineupPosition?> {
        return load(MatchLineupPosition::class.java, matchID, matchType.id, teamID)
    }

    private val getMatchInsertsStatementBuilder = PreparedSelectStatementBuilder(this, " WHERE SpielerID = ?")

    init {
        idColumns = 3
    }

    fun getMatchInserts(objectPlayerID: Int): List<MatchLineupPosition?> {
        return load(
            MatchLineupPosition::class.java,
            adapter.executePreparedQuery(getMatchInsertsStatementBuilder.getStatement(), objectPlayerID)
        )
    }

    fun loadTopFlopRatings(
        matches: List<Paarung>,
        position: Int,
        count: Int,
        isBest: Boolean
    ): List<MatchLineupPosition?> {
        val args = ArrayList<Any>()
        val sql = StringBuilder("SELECT * FROM ")
        sql.append(TABLENAME).append(" WHERE SpielerID != 0 AND RATING>0 AND RoleID IN (")
        when (position) {
            IMatchRoleID.KEEPER.toInt() -> {
                args.add(IMatchRoleID.keeper)
                sql.append("?)")
            }

            IMatchRoleID.CENTRAL_DEFENDER.toInt() -> {
                args.add(IMatchRoleID.leftCentralDefender)
                args.add(IMatchRoleID.middleCentralDefender)
                args.add(IMatchRoleID.rightCentralDefender)
                sql.append("?,?,?)")
            }

            IMatchRoleID.BACK.toInt() -> {
                args.add(IMatchRoleID.leftBack)
                sql.append("?)")
            }

            IMatchRoleID.WINGER.toInt() -> {
                args.add(IMatchRoleID.leftWinger)
                args.add(IMatchRoleID.rightWinger)
                sql.append("?,?)")
            }

            IMatchRoleID.MIDFIELDER.toInt() -> {
                args.add(IMatchRoleID.leftInnerMidfield)
                args.add(IMatchRoleID.centralInnerMidfield)
                args.add(IMatchRoleID.rightInnerMidfield)
                sql.append("?,?,?)")
            }

            IMatchRoleID.FORWARD.toInt() -> {
                args.add(IMatchRoleID.leftForward)
                args.add(IMatchRoleID.centralForward)
                args.add(IMatchRoleID.rightForward)
                sql.append("?,?,?)")
            }
        }
        if (matches.isNotEmpty()) {
            sql.append(" AND MatchID IN ")
            var sep = '('
            for (match in matches) {
                args.add(match.matchId)
                sql.append(sep).append('?')
                sep = ','
            }
            sql.append(')')
        }
        sql.append(" ORDER BY RATING ")
        if (isBest) {
            sql.append("DESC")
        } else {
            sql.append("ASC")
        }
        sql.append(" LIMIT ").append(count)
        return load<MatchLineupPosition>(
            MatchLineupPosition::class.java, adapter.executePreparedQuery(
                DBManager.getPreparedStatement(sql.toString()), *args.toTypedArray()
            )
        )
    }

    companion object {
        /**
         * tablename
         */
        const val TABLENAME = "MATCHLINEUPPLAYER"
    }
}
