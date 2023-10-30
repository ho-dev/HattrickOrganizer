package core.db

import core.db.DBManager.PreparedStatementBuilder
import core.model.HOVerwaltung
import core.model.cup.CupLevel
import core.model.cup.CupLevelIndex
import core.model.enums.MatchType
import core.model.match.MatchKurzInfo
import core.model.match.Weather
import core.model.match.Weather.Forecast
import core.util.HODateTime
import core.util.HOLogger
import module.matches.MatchLocation
import module.matches.MatchesPanel
import module.matches.statistics.MatchesOverviewCommonPanel
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.stream.Collectors

/**
 * Class used to store in DB, [Matches] Table fetched via CHPP
 */
internal class MatchesKurzInfoTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.matchID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchKurzInfo?)!!.matchID = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchTyp")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.getMatchType().id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any ->
                    (o as MatchKurzInfo?)!!.matchType = MatchType.getById(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.homeTeamName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchKurzInfo?)!!.homeTeamName = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.homeTeamID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchKurzInfo?)!!.homeTeamID = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastName")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.guestTeamName }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchKurzInfo?)!!.guestTeamName = v as String? })
                .setType(
                    Types.VARCHAR
                ).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.guestTeamID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchKurzInfo?)!!.guestTeamID = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchDate")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.matchSchedule.toDbTimestamp() })
                .setSetter(
                    BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                        (o as MatchKurzInfo?)!!.matchSchedule = v as HODateTime?
                    }).setType(
                Types.TIMESTAMP
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HeimTore")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.homeTeamGoals }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchKurzInfo?)!!.homeTeamGoals = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GastTore")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.guestTeamGoals }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchKurzInfo?)!!.guestTeamGoals = v as Int }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Aufstellung")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.isOrdersGiven }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchKurzInfo?)!!.isOrdersGiven = v as Boolean })
                .setType(
                    Types.BOOLEAN
                ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Status")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.matchStatus }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any -> (o as MatchKurzInfo?)!!.matchStatus = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CupLevel")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.cupLevel.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                    (o as MatchKurzInfo?)!!.cupLevel = CupLevel.fromInt(v as Int?)
                }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CupLevelIndex")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.cupLevelIndex.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                    (o as MatchKurzInfo?)!!.cupLevelIndex = CupLevelIndex.fromInt(v as Int?)
                }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchContextId")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.matchContextId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchKurzInfo?)!!.setMatchContextId(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TournamentTypeID")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.tournamentTypeID }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchKurzInfo?)!!.setTournamentTypeID(v as Int?) })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ArenaId")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.arenaId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchKurzInfo?)!!.setArenaId(v as Int?) })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("RegionId")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.regionId }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchKurzInfo?)!!.setRegionId(v as Int?) })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("isDerby")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.isDerby }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchKurzInfo?)!!.setIsDerby(v as Boolean?) })
                .setType(
                    Types.BOOLEAN
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("isNeutral")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.isNeutral }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchKurzInfo?)!!.setIsNeutral(v as Boolean?) })
                .setType(
                    Types.BOOLEAN
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Weather")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.weather.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                    (o as MatchKurzInfo?)!!.weather = Weather.getById(v as Int?)
                }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("WeatherForecast")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.weatherForecast.id }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? ->
                    (o as MatchKurzInfo?)!!.weatherForecast = Forecast.getById(v as Int?)
                }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Duration")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.getDuration() }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchKurzInfo?)!!.duration = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("isObsolete")
                .setGetter(Function<Any?, Any?> { o: Any? -> (o as MatchKurzInfo?)!!.isObsolet }).setSetter(
                BiConsumer<Any?, Any> { o: Any?, v: Any? -> (o as MatchKurzInfo?)!!.setisObsolet(v as Boolean?) })
                .setType(
                    Types.BOOLEAN
                ).isNullable(true).build()
        )
    }

    override val constraintStatements: Array<String?>
        get() = arrayOf(" PRIMARY KEY (MATCHID, MATCHTYP)")
    override val createIndexStatement: Array<String?>
        get() = arrayOf(
            "CREATE INDEX IMATCHKURZINFO_1 ON " + tableName + "(" + columns[0].columnName + ")",
            "CREATE INDEX matchkurzinfo_heimid_idx ON " + tableName + "(" + columns[3].columnName + ")",
            "CREATE INDEX matchkurzinfo_gastid_idx ON " + tableName + "(" + columns[5].columnName + ")"
        )

    /**
     * Saves matches into storeMatchKurzInfo table
     */
    fun storeMatchKurzInfos(matches: List<MatchKurzInfo>?) {
        if (matches == null) return
        for (match in matches) {
            match.stored = isStored(match.matchID, match.getMatchType().id)
            store(match)
        }
    }

    fun update(match: MatchKurzInfo) {
        match.stored = true
        store(match)
    }

    private val preparedStatements = HashMap<String, PreparedStatement?>()

    fun getMatchesKurzInfo(teamId: Int, matchType: Int, statistic: Int, home: Boolean): MatchKurzInfo? {
        val sql = StringBuilder(200)
        var column = ""
        var column2 = ""
        when (statistic) {
            MatchesOverviewCommonPanel.HighestVictory -> {
                column = if (home) "(HEIMTORE-GASTTORE) AS DIFF " else "(GASTTORE-HEIMTORE) AS DIFF "
                column2 = if (home) ">" else "<"
            }

            MatchesOverviewCommonPanel.HighestDefeat -> {
                column = if (home) "(GASTTORE-HEIMTORE) AS DIFF " else "(HEIMTORE-GASTTORE) AS DIFF "
                column2 = if (home) "<" else ">"
            }
        }
        sql.append("SELECT ").append(tableName).append(".*,")
        sql.append(column)
        sql.append(" FROM ").append(tableName)
        sql.append(" WHERE ").append(if (home) "HEIMID=?" else "GASTID=?")
        sql.append(" AND HEIMTORE ").append(column2).append(" GASTTORE ")
        sql.append(getMatchTypWhereClause(matchType))
        sql.append(" ORDER BY DIFF DESC ")
        return loadOne<MatchKurzInfo>(
            MatchKurzInfo::class.java, adapter.executePreparedQuery(
                DBManager.getPreparedStatement(sql.toString()), teamId
            )
        )
    }

    /**
     * Return the list of n latest played matches (own team)
     */
    fun getPlayedMatchInfo(iNbGames: Int?, bOfficialOnly: Boolean, ownTeam: Boolean): List<MatchKurzInfo?> {
        val teamId = HOVerwaltung.instance().model.getBasics().teamId
        val params = ArrayList<Any>()
        val where = StringBuilder(" WHERE Status=? ")
        params.add(MatchKurzInfo.FINISHED)
        if (ownTeam) {
            where.append(" AND ( GastID = ? OR HeimID = ?)")
            params.add(teamId)
            params.add(teamId)
        }
        if (bOfficialOnly) {
            where.append(officialMatchTypWhereClause)
        }
        where.append(" ORDER BY MatchDate DESC")
        if (iNbGames != null) {
            where.append(" LIMIT ").append(iNbGames)
        }
        return loadMatchesKurzInfo(where.toString(), *params.toTypedArray())
    }

    /**
     * Important: If the teamid = -1 the match type must be ALL_GAMES!
     * @param teamId The Teamid or -1 for all
     * @param matchType Which matches? Constants in the GamesPanel!
     * @param matchLocation Home, Away, Neutral
     * @param from filter match schedule date
     * @param includeUpcoming if false filter finished matches only
     * @return MatchKurzInfo[] – Array of match info.
     */
    fun getMatchesKurzInfo(
        teamId: Int,
        matchType: Int,
        matchLocation: MatchLocation?,
        from: Timestamp?,
        includeUpcoming: Boolean
    ): List<MatchKurzInfo?> {
        var currentMatchType = matchType
        var currentIncludeUpcoming = includeUpcoming
        val liste = ArrayList<MatchKurzInfo?>()
        // Without TeamID ino only All_Match possible
        if (teamId < 0 && currentMatchType != MatchesPanel.ALL_GAMES) {
            return liste
        }
        val params = ArrayList<Any?>()
        // filter time
        val sql = StringBuilder(" WHERE MatchDate>= ? ")
        params.add(from)
        if (currentMatchType != MatchesPanel.ALL_GAMES) {
            // OTHER TEAM GAMES =============================================
            if (currentMatchType == MatchesPanel.OTHER_TEAM_GAMES) {
                sql.append(" AND ( GastID != ? AND HeimID != ? )")
                params.add(teamId)
                params.add(teamId)
            } else {
                when (matchLocation) {
                    MatchLocation.HOME -> {
                        sql.append(" AND HeimID = ? AND (isNeutral is NULL OR isNeutral=false) ")
                        params.add(teamId)
                    }

                    MatchLocation.AWAY -> {
                        sql.append(" AND GastID = ? AND (is Neutral is NULL OR isNeutral=false) ")
                        params.add(teamId)
                    }

                    MatchLocation.NEUTRAL -> {
                        sql.append(" AND (HeimID = ? OR GastID = ?) AND (isNeutral=true) ")
                        params.add(teamId)
                        params.add(teamId)
                    }

                    MatchLocation.ALL -> {
                        sql.append(" AND (HeimID = ? OR GastID= ?) ")
                        params.add(teamId)
                        params.add(teamId)
                    }

                    null -> TODO()
                }
            }
        }

        // Filter matchType
        if (currentMatchType >= 10) {
            currentMatchType -= 10
            currentIncludeUpcoming = false
        }
        if (!currentIncludeUpcoming) {
            sql.append(" AND Status=?")
            params.add(MatchKurzInfo.FINISHED)
        }
        sql.append(getMatchTypWhereClause(currentMatchType))
        sql.append(" ORDER BY MatchDate DESC")
        return loadMatchesKurzInfo(sql.toString(), *params.toTypedArray())
    }

    fun getMatchesKurzInfoUpComing(teamId: Int): List<MatchKurzInfo?> {
        // Ohne Matchid nur AlleSpiele möglich!
        return if (teamId < 0) {
            ArrayList()
        } else loadMatchesKurzInfo(
            " WHERE ( GastID = ? OR HeimID = ? ) AND Status= ? AND MatchTyp!= ? ORDER BY MatchDate DESC",
            teamId,
            teamId,
            MatchKurzInfo.UPCOMING,
            MatchType.LEAGUE.id
        )
    }

    fun loadLastMatchesKurzInfo(teamId: Int): MatchKurzInfo? {
        return loadOneMatchesKurzInfo(
            " WHERE ( GastID = ? OR HeimID = ? ) AND Status = ? ORDER BY MatchDate DESC LIMIT 1",
            teamId,
            teamId,
            MatchKurzInfo.FINISHED
        )
    }

    fun loadNextMatchesKurzInfo(teamId: Int): MatchKurzInfo? {
        return loadOneMatchesKurzInfo(
            " WHERE ( GastID = ? OR HeimID = ? ) AND MatchDate > ? AND Status = ? ORDER BY MatchDate ASC LIMIT 1",
            teamId,
            teamId,
            HODateTime.now().toDbTimestamp(),
            MatchKurzInfo.UPCOMING
        )
    }

    private fun loadOneMatchesKurzInfo(sql: String, vararg params: Any): MatchKurzInfo? {
        val matches = loadMatchesKurzInfo(sql, *params)
        return if (!matches.isEmpty()) matches[0] else null
    }

    private val officialMatchTypWhereClause: StringBuilder
        get() {
            val sql = StringBuilder(100)
            val officialTypes = MatchType.getOfficialMatchTypes()
            sql.append(" AND MatchTyp IN (")
            var sep = ' '
            for (t in officialTypes) {
                sql.append(sep).append(t.id)
                sep = ','
            }
            sql.append(" )")
            return sql
        }

    /**
     * Check if a match is already in the database.
     */
    fun isMatchInDB(matchid: Int, matchType: MatchType): Boolean {
        return isStored(matchid, matchType.id)
    }

    private val hasUnsureWeatherForecastStatementBuilder = PreparedStatementBuilder(
        "SELECT WeatherForecast FROM $tableName WHERE MatchId=?"
    )

    init {
        idColumns = 2
    }

    fun hasUnsureWeatherForecast(matchId: Int): Boolean {
        try {
            val rs = adapter.executePreparedQuery(hasUnsureWeatherForecastStatementBuilder.getStatement(), matchId)!!
            if (rs.next()) {
                val forecast = Forecast.getById(rs.getInt(1))
                return if (rs.wasNull()) true else !forecast.isSure
            }
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "DatenbankZugriff.hasUnsureWeatherForecast : $e")
        }
        return false
    }

    /**
     * Get all matches with a certain status for the given team from the
     * database.
     *
     * @param teamId
     * the teamid or -1 for all matches
     * @param matchStatus
     * the match status (e.g. IMatchKurzInfo.UPCOMING) or -1 to
     * ignore this parameter
     */
    fun getMatchesKurzInfo(teamId: Int, matchStatus: Int): List<MatchKurzInfo?> {
        val sql = StringBuilder()
        val params = ArrayList<Any>()
        var sep = "WHERE"
        if (teamId > -1) {
            sql.append(sep).append(" GastID=? OR HeimID=?")
            sep = " AND"
            params.add(teamId)
            params.add(teamId)
        }
        if (matchStatus > -1) {
            sql.append(sep).append(" Status=?")
            params.add(matchStatus)
        }
        sql.append(" ORDER BY MatchDate DESC")
        return loadMatchesKurzInfo(sql.toString(), *params.toTypedArray())
    }

    fun loadMatchesKurzInfo(sql: String, vararg params: Any?): List<MatchKurzInfo?> {
        return load(MatchKurzInfo::class.java, adapter.executePreparedQuery(getMatchKurzInfoStatement(sql), *params))
    }

    /**
     * Get all matches for the given team from the database.
     *
     * @param teamId
     * the teamid or -1 for all matches
     */
    fun getMatchesKurzInfo(teamId: Int): List<MatchKurzInfo?>? {
        return getMatchesKurzInfo(teamId, -1)
    }

    /**
     * Returns the MatchKurzInfo for the match. Returns null if not found.
     *
     * @param matchid the ID for the match
     * @return The kurz info object or null
     */
    fun getMatchesKurzInfoByMatchID(matchid: Int, matchType: MatchType?): MatchKurzInfo? {
        return if (matchType != null && matchType != MatchType.NONE) loadOne(
            MatchKurzInfo::class.java,
            matchid,
            matchType.id
        ) else loadOneMatchesKurzInfo(" WHERE MATCHID=? ", matchid)
    }

    fun getLastMatchWithMatchId(matchId: Int): MatchKurzInfo? {
        // Find latest match with id = matchId
        // Here we order by MatchDate, which happens to be string, which is somehow risky,
        // but it seems to be done in other places.
        return loadOneMatchesKurzInfo(
            " WHERE MATCHID=? AND Status=? ORDER BY MATCHDATE DESC LIMIT 1",
            matchId,
            MatchKurzInfo.FINISHED
        )
    }

    /**
     * Gets first upcoming match with team id.
     *
     * @param teamId the team id
     * @return the first upcoming match with team id
     */
    fun getFirstUpcomingMatchWithTeamId(teamId: Int): MatchKurzInfo? {
        return loadOneMatchesKurzInfo(
            " WHERE (GastID=? OR HeimID=?) AND Status=? ORDER BY MATCHDATE ASC LIMIT 1",
            teamId,
            teamId,
            MatchKurzInfo.UPCOMING
        )
    }

    private fun getMatchKurzInfoStatement(where: String): PreparedStatement? {
        var ret = preparedStatements[where]
        if (ret == null) {
            ret = createSelectStatement(where)
            preparedStatements[where] = ret
        }
        return ret
    }

    private fun createSelectStatement(where: String): PreparedStatement? {
        return PreparedSelectStatementBuilder(this, where).getStatement()
    }

    fun getMatchesKurzInfo(teamId: Int, status: Int, from: Timestamp?, matchTypes: List<Int?>): List<MatchKurzInfo?> {
        val params = ArrayList<Any?>()
        val whereClause = StringBuilder(" WHERE (GastID=? OR HeimID=?) AND Status=?")
        params.add(teamId)
        params.add(teamId)
        params.add(status)
        if (from != null) {
            whereClause.append(" AND MATCHDATE > ?")
            params.add(from)
        }
        params.addAll(matchTypes)
        val placeholders = matchTypes.stream().map { _ -> "?" }.collect(Collectors.joining(","))
        whereClause.append(" AND MATCHTYP IN (").append(placeholders).append(") ORDER BY MatchDate DESC")
        return loadMatchesKurzInfo(whereClause.toString(), *params.toTypedArray())
    }

    fun getMatchesKurzInfo(
        teamId: Int,
        from: Timestamp,
        to: Timestamp,
        matchTypes: List<MatchType>
    ): List<MatchKurzInfo?> {
        val typeListAsInt = matchTypes.stream().map { obj: MatchType -> obj.id }.toList()
        val placeholders = matchTypes.stream().map { _ -> "?" }.collect(Collectors.joining(","))
        val params = ArrayList<Any>()
        params.add(teamId)
        params.add(teamId)
        params.add(from)
        params.add(to)
        params.addAll(typeListAsInt)
        params.add(MatchKurzInfo.UPCOMING)
        params.add(MatchKurzInfo.FINISHED)
        return loadMatchesKurzInfo(
            "WHERE (HEIMID = ? OR GASTID = ?) AND MATCHDATE BETWEEN ? AND ? AND MATCHTYP in ($placeholders) AND STATUS in (?, ?) ORDER BY MatchDate DESC",
            *params.toTypedArray()
        )
    }

    companion object {
        const val TABLENAME = "MATCHESKURZINFO"
        fun getMatchTypWhereClause(iMatchType: Int): StringBuilder {
            val sql = StringBuilder(100)
            when (iMatchType) {
                MatchesPanel.OWN_OFFICIAL_GAMES -> sql.append(" AND ( MatchTyp=").append(MatchType.QUALIFICATION.id)
                    .append(" OR MatchTyp=").append(MatchType.LEAGUE.id).append(" OR MatchTyp=")
                    .append(MatchType.CUP.id).append(" )")

                MatchesPanel.OWN_NATIONAL_CUP_GAMES -> sql.append(" AND MatchTyp = ").append(MatchType.CUP.id)
                    .append(" AND CUPLEVEL = ").append(CupLevel.NATIONALorDIVISIONAL.id)

                MatchesPanel.OWN_LEAGUE_GAMES -> sql.append(" AND MatchTyp=").append(MatchType.LEAGUE.id)
                MatchesPanel.OWN_FRIENDLY_GAMES -> sql.append(" AND ( MatchTyp=").append(MatchType.FRIENDLYNORMAL.id)
                    .append(" OR MatchTyp=").append(MatchType.FRIENDLYCUPRULES.id)
                    .append(" OR MatchTyp=").append(MatchType.INTFRIENDLYCUPRULES.id)
                    .append(" OR MatchTyp=").append(MatchType.INTFRIENDLYNORMAL.id).append(" )")

                MatchesPanel.OWN_TOURNAMENT_GAMES -> sql.append(" AND ( MatchTyp=").append(MatchType.TOURNAMENTGROUP.id)
                    .append(" OR MatchTyp=").append(MatchType.TOURNAMENTPLAYOFF.id).append(" )")

                MatchesPanel.OWN_SECONDARY_CUP_GAMES -> sql.append(" AND MatchTyp = ").append(MatchType.CUP.id)
                    .append(" AND CUPLEVEL != ").append(CupLevel.NATIONALorDIVISIONAL.id)

                MatchesPanel.OWN_QUALIF_GAMES -> sql.append(" AND MatchTyp=").append(MatchType.QUALIFICATION.id)
                MatchesPanel.OWN_CUP_GAMES -> sql.append(" AND (MatchTyp=").append(MatchType.CUP.id).append(")")
            }
            return sql
        }
    }
}
