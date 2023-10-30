package core.db

import core.model.HOVerwaltung
import core.model.match.*
import core.model.match.MatchEvent.MatchEventID
import core.util.HOLogger
import module.matches.MatchLocation
import module.matches.statistics.MatchesOverviewCommonPanel
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

internal object MatchesOverviewQuery {
    fun getMatchesKurzInfoStatisticsCount(teamId: Int, matchtype: Int, statistic: Int): Int {
        var tmp = 0
        val sql = StringBuilder(200)
        val rs: ResultSet?
        var whereHomeClause = ""
        var whereAwayClause = ""
        sql.append("SELECT COUNT(*) AS C ")
        sql.append(" FROM MATCHESKURZINFO ")
        sql.append(" WHERE ")
        when (statistic) {
            MatchesOverviewCommonPanel.LeadingHTLosingFT, MatchesOverviewCommonPanel.TrailingHTWinningFT -> return getChangeGameStat(
                teamId,
                statistic
            )

            MatchesOverviewCommonPanel.WonWithoutOppGoal -> {
                whereHomeClause = " AND HEIMTORE > GASTTORE AND GASTTORE = 0 )"
                whereAwayClause = " AND HEIMTORE < GASTTORE AND HEIMTORE = 0 ))"
            }

            MatchesOverviewCommonPanel.LostWithoutOwnGoal -> {
                whereHomeClause = " AND HEIMTORE < GASTTORE AND HEIMTORE = 0 )"
                whereAwayClause = " AND HEIMTORE > GASTTORE AND GASTTORE = 0 ))"
            }

            MatchesOverviewCommonPanel.FiveGoalsDiffWin -> {
                whereHomeClause = " AND HEIMTORE > GASTTORE AND (HEIMTORE - GASTTORE ) >= 5 )"
                whereAwayClause = " AND HEIMTORE < GASTTORE AND (GASTTORE - HEIMTORE ) >= 5 ))"
            }

            MatchesOverviewCommonPanel.FiveGoalsDiffDefeat -> {
                whereHomeClause = " AND HEIMTORE < GASTTORE AND (GASTTORE - HEIMTORE ) >= 5 )"
                whereAwayClause = " AND HEIMTORE > GASTTORE AND (HEIMTORE - GASTTORE ) >= 5 ))"
            }
        }
        sql.append(" ((HEIMID = ?").append(whereHomeClause)
        sql.append(" OR (GASTID = ?").append(whereAwayClause)
        sql.append(MatchesKurzInfoTable.Companion.getMatchTypWhereClause(matchtype))
        rs = Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executePreparedQuery(
            DBManager.getPreparedStatement(sql.toString()), teamId, teamId
        )
        try {
            if (rs!!.next()) {
                tmp = rs.getInt("C")
            }
        } catch (e: SQLException) {
            HOLogger.instance().log(MatchesOverviewQuery::class.java, e)
        }
        return tmp
    }

    fun getChangeGameStat(teamId: Int, statistic: Int): Int {
        val sql = StringBuilder(200)
        val rs: ResultSet?
        var tmp = 0
        sql.append(
            """
				SELECT MK_MatchTyp, DIFFH, DIFF, MK_HEIMID, MK_GASTID, MATCHID  
				FROM (SELECT (MATCHHIGHLIGHTS.HEIMTORE - MATCHHIGHLIGHTS.GASTTORE) as DIFFH, (MATCHESKURZINFO.HEIMTORE - MATCHESKURZINFO.GASTTORE) as DIFF, HEIMID, GASTID, MATCHID, TYP, MINUTE, 
				MATCHHIGHLIGHTS.TEAMID as MH_TEAMID, MATCHESKURZINFO.HEIMID as MK_HEIMID, MATCHESKURZINFO.GASTID as MK_GASTID, MATCHESKURZINFO.MATCHTYP as MK_MatchTyp 
				FROM
				MATCHHIGHLIGHTS JOIN MATCHESKURZINFO ON MATCHHIGHLIGHTS.MATCHID = MATCHESKURZINFO.MATCHID) WHERE TYP = 0 AND MINUTE = 45 AND MH_TEAMID = 0 ");
				
				""".trimIndent()
        )
        when (statistic) {
            MatchesOverviewCommonPanel.LeadingHTLosingFT -> sql.append("AND ((MK_HEIMID = ? AND DIFFH >0 AND DIFF <0) or (MK_GASTID = ? AND DIFFH <0 AND DIFF >0)) ")
            MatchesOverviewCommonPanel.TrailingHTWinningFT -> sql.append("AND ((MK_HEIMID = ? AND DIFFH <0 AND DIFF >0) or (MK_GASTID = ? AND DIFFH >0 AND DIFF <0)) ")
        }
        sql.append("AND (MK_MatchTyp=2 OR MK_MatchTyp=1 OR MK_MatchTyp=3 )")
        rs = Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executePreparedQuery(
            DBManager.getPreparedStatement(sql.toString()), teamId, teamId
        )
        try {
            var i = 0
            while (rs!!.next()) {
                tmp = i
                i++
            }
        } catch (e: SQLException) {
            HOLogger.instance().log(MatchesOverviewQuery::class.java, e)
        }
        return tmp
    }

    fun getGoalsByActionType(
        ownTeam: Boolean,
        iMatchType: Int,
        matchLocation: MatchLocation?
    ): Array<MatchesHighlightsStat?> {
        val teamId = HOVerwaltung.instance().model.getBasics().teamId
        val rows = arrayOfNulls<MatchesHighlightsStat>(9)
        rows[0] = MatchesHighlightsStat("highlight_penalty", MatchEvent.penaltyME)
        rows[1] = MatchesHighlightsStat("highlight_freekick", MatchEvent.freekickME)
        rows[2] = MatchesHighlightsStat("highlight_links", MatchEvent.leftAttackME)
        rows[3] = MatchesHighlightsStat("highlight_middle", MatchEvent.CentralAttackME)
        rows[4] = MatchesHighlightsStat("highlight_rechts", MatchEvent.RightAttackME)
        rows[5] = MatchesHighlightsStat("IFK", MatchEvent.IFKME)
        rows[6] = MatchesHighlightsStat("ls.match.event.longshot", MatchEvent.LSME)
        rows[7] = MatchesHighlightsStat("highlight_counter", MatchEvent.CounterAttackME)
        rows[8] = MatchesHighlightsStat("highlight_special", MatchEvent.specialME)
        for (row in rows) {
            if (!row!!.isTitle) fillMatchesOverviewChanceRow(ownTeam, teamId, row, iMatchType, matchLocation)
        }
        return rows
    }

    private fun fillMatchesOverviewChanceRow(
        ownTeam: Boolean,
        teamId: Int,
        row: MatchesHighlightsStat?,
        iMatchType: Int,
        matchLocation: MatchLocation?
    ) {
        val sql = StringBuilder(200)
        val rs: ResultSet?
        val params: MutableList<Any> = ArrayList()
        sql.append("SELECT MATCH_EVENT_ID, COUNT(*) AS C FROM MATCHHIGHLIGHTS JOIN MATCHESKURZINFO ON MATCHHIGHLIGHTS.MATCHID = MATCHESKURZINFO.MATCHID WHERE TEAMID")
        if (!ownTeam) {
            sql.append("!")
        }
        sql.append("=?").append(" AND MATCH_EVENT_ID IN(")
        params.add(teamId)
        sql.append(createSubTypePlaceholders(row!!.subtyps, params)).append(")")
        sql.append(MatchesKurzInfoTable.Companion.getMatchTypWhereClause(iMatchType))
        sql.append(getMatchLocationWhereClause(matchLocation, teamId))
        sql.append(" GROUP BY MATCH_EVENT_ID")
        rs = Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executePreparedQuery(
            DBManager.getPreparedStatement(sql.toString()), *params.toTypedArray()
        )
        if (rs == null) {
            HOLogger.instance().log(MatchesOverviewQuery::class.java, sql.toString())
        } else {
            try {
                var iConverted = 0
                var iMissed = 0
                while (rs.next()) {
                    val iMatchEventID = rs.getInt("MATCH_EVENT_ID")
                    if (MatchEvent.isGoalEvent(iMatchEventID)) {
                        iConverted += rs.getInt("C")
                    } else {
                        iMissed += rs.getInt("C")
                    }
                }
                rs.close()
                row.goals = iConverted
                row.setNoGoals(iMissed)
            } catch (e: SQLException) {
                HOLogger.instance().log(MatchesOverviewQuery::class.java, e)
            }
        }
    }

    private fun createSubTypePlaceholders(subtypes: List<MatchEventID>, params: MutableList<Any>): StringBuilder {
        var sep = ""
        val placeholders = StringBuilder()
        for (id in subtypes) {
            placeholders.append(sep).append("?")
            params.add(id.value)
            sep = ","
        }
        return placeholders
    }

    private fun getMatchLocationWhereClause(matchLocation: MatchLocation?, teamId: Int): StringBuilder {
        val sql = StringBuilder(50)
        when (matchLocation) {
            MatchLocation.HOME -> sql.append(" AND HeimID=").append(teamId)
                .append(" AND (isNeutral is NULL OR isNeutral=false) ")

            MatchLocation.AWAY -> sql.append(" AND GastID=").append(teamId)
                .append(" AND (isNeutral is NULL OR isNeutral=false) ")

            MatchLocation.NEUTRAL -> sql.append(" AND (HeimID=").append(teamId).append(" OR GastID=").append(teamId)
                .append(") AND (isNeutral=true) ")

            MatchLocation.ALL -> sql.append(" AND (HeimID=").append(teamId).append(" OR GastID=").append(teamId)
                .append(") ")

            null -> TODO()
        }
        return sql
    }

    fun getMatchesOverviewValues(matchtype: Int, matchLocation: MatchLocation): Array<MatchesOverviewRow> {
        val rows = ArrayList<MatchesOverviewRow>(20)
        rows.add(
            MatchesOverviewRow(
                HOVerwaltung.instance().getLanguageString("AlleSpiele"),
                MatchesOverviewRow.TYPE_ALL
            )
        )
        rows.add(
            MatchesOverviewRow(
                HOVerwaltung.instance().getLanguageString("ls.team.formation"),
                MatchesOverviewRow.TYPE_TITLE
            )
        )
        rows.add(MatchesOverviewRow("5-5-0", MatchesOverviewRow.TYPE_SYSTEM))
        rows.add(MatchesOverviewRow("5-4-1", MatchesOverviewRow.TYPE_SYSTEM))
        rows.add(MatchesOverviewRow("5-3-2", MatchesOverviewRow.TYPE_SYSTEM))
        rows.add(MatchesOverviewRow("5-2-3", MatchesOverviewRow.TYPE_SYSTEM))
        rows.add(MatchesOverviewRow("4-5-1", MatchesOverviewRow.TYPE_SYSTEM))
        rows.add(MatchesOverviewRow("4-4-2", MatchesOverviewRow.TYPE_SYSTEM))
        rows.add(MatchesOverviewRow("4-3-3", MatchesOverviewRow.TYPE_SYSTEM))
        rows.add(MatchesOverviewRow("3-5-2", MatchesOverviewRow.TYPE_SYSTEM))
        rows.add(MatchesOverviewRow("3-4-3", MatchesOverviewRow.TYPE_SYSTEM))
        rows.add(MatchesOverviewRow("2-5-3", MatchesOverviewRow.TYPE_SYSTEM))
        rows.add(
            MatchesOverviewRow(
                HOVerwaltung.instance().getLanguageString("ls.team.tactic"),
                MatchesOverviewRow.TYPE_TITLE
            )
        )
        rows.add(
            MatchesOverviewRow(
                Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_NORMAL),
                MatchesOverviewRow.TYPE_TACTICS,
                IMatchDetails.TAKTIK_NORMAL
            )
        )
        rows.add(
            MatchesOverviewRow(
                Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_PRESSING),
                MatchesOverviewRow.TYPE_TACTICS,
                IMatchDetails.TAKTIK_PRESSING
            )
        )
        rows.add(
            MatchesOverviewRow(
                Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_KONTER),
                MatchesOverviewRow.TYPE_TACTICS,
                IMatchDetails.TAKTIK_KONTER
            )
        )
        rows.add(
            MatchesOverviewRow(
                Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_MIDDLE),
                MatchesOverviewRow.TYPE_TACTICS,
                IMatchDetails.TAKTIK_MIDDLE
            )
        )
        rows.add(
            MatchesOverviewRow(
                Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_WINGS),
                MatchesOverviewRow.TYPE_TACTICS,
                IMatchDetails.TAKTIK_WINGS
            )
        )
        rows.add(
            MatchesOverviewRow(
                Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_CREATIVE),
                MatchesOverviewRow.TYPE_TACTICS,
                IMatchDetails.TAKTIK_CREATIVE
            )
        )
        rows.add(
            MatchesOverviewRow(
                Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_LONGSHOTS),
                MatchesOverviewRow.TYPE_TACTICS,
                IMatchDetails.TAKTIK_LONGSHOTS
            )
        )
        rows.add(
            MatchesOverviewRow(
                HOVerwaltung.instance().getLanguageString("ls.team.teamattitude"),
                MatchesOverviewRow.TYPE_TITLE
            )
        )
        rows.add(
            MatchesOverviewRow(
                Matchdetails.getNameForEinstellung(IMatchDetails.EINSTELLUNG_PIC),
                MatchesOverviewRow.TYPE_MOT,
                IMatchDetails.EINSTELLUNG_PIC
            )
        )
        rows.add(
            MatchesOverviewRow(
                Matchdetails.getNameForEinstellung(IMatchDetails.EINSTELLUNG_NORMAL),
                MatchesOverviewRow.TYPE_MOT,
                IMatchDetails.EINSTELLUNG_NORMAL
            )
        )
        rows.add(
            MatchesOverviewRow(
                Matchdetails.getNameForEinstellung(IMatchDetails.EINSTELLUNG_MOTS),
                MatchesOverviewRow.TYPE_MOT,
                IMatchDetails.EINSTELLUNG_MOTS
            )
        )
        rows.add(
            MatchesOverviewRow(
                HOVerwaltung.instance().getLanguageString("ls.match.weather"),
                MatchesOverviewRow.TYPE_TITLE
            )
        )
        rows.add(MatchesOverviewRow("IMatchDetails.WETTER_SONNE", MatchesOverviewRow.TYPE_WEATHER, Weather.SUNNY.id))
        rows.add(
            MatchesOverviewRow(
                "IMatchDetails.WETTER_WOLKIG",
                MatchesOverviewRow.TYPE_WEATHER,
                Weather.PARTIALLY_CLOUDY.id
            )
        )
        rows.add(
            MatchesOverviewRow(
                "IMatchDetails.WETTER_BEWOELKT",
                MatchesOverviewRow.TYPE_WEATHER,
                Weather.OVERCAST.id
            )
        )
        rows.add(MatchesOverviewRow("IMatchDetails.WETTER_REGEN", MatchesOverviewRow.TYPE_WEATHER, Weather.RAINY.id))
        setMatchesOverviewValues(rows, matchtype, true, matchLocation)
        setMatchesOverviewValues(rows, matchtype, false, matchLocation)
        return rows.toTypedArray<MatchesOverviewRow>()
    }

    private fun setMatchesOverviewValues(
        rows: ArrayList<MatchesOverviewRow>,
        matchtype: Int,
        home: Boolean,
        matchLocation: MatchLocation
    ) {
        if (home && matchLocation == MatchLocation.AWAY || !home && matchLocation == MatchLocation.HOME) {
            return
        }
        val teamId = HOVerwaltung.instance().model.getBasics().teamId
        val whereClause = StringBuilder(100)
        whereClause.append(getMatchLocationWhereClause(matchLocation, teamId, home))
        whereClause.append(MatchesKurzInfoTable.Companion.getMatchTypWhereClause(matchtype))
        setMatchesOverviewRow(rows[0], whereClause.toString(), home)
        setFormationRows(rows, whereClause, home)
        setRows(rows, whereClause, home)
    }

    private fun getMatchLocationWhereClause(matchLocation: MatchLocation, teamId: Int, home: Boolean): StringBuilder {
        val sql = StringBuilder(500)
        when (matchLocation) {
            MatchLocation.HOME -> sql.append(" AND (isNeutral is NULL OR isNeutral=false) AND HeimID=").append(teamId)
            MatchLocation.AWAY -> sql.append(" AND (isNeutral is NULL OR isNeutral=false) AND GastID=").append(teamId)
            MatchLocation.NEUTRAL -> {
                sql.append(" AND isNeutral=true")
                sql.append(" AND ").append(if (home) "HEIMID=" else "GASTID=").append(teamId)
            }

            MatchLocation.ALL -> sql.append(" AND ").append(if (home) "HEIMID=" else "GASTID=").append(teamId)
        }
        return sql
    }

    private fun setFormationRows(rows: ArrayList<MatchesOverviewRow>, whereClause: StringBuilder, home: Boolean) {
        val sql = StringBuilder(500)
        sql.append("select MATCHID,HEIMTORE,GASTTORE, ")
        sql.append("LOCATE('5-5-0',MATCHREPORT) AS F550,")
        sql.append("LOCATE('5-4-1',MATCHREPORT) AS F541,")
        sql.append("LOCATE('5-3-2',MATCHREPORT) AS F532,")
        sql.append("LOCATE('5-2-3',MATCHREPORT) AS F523,")
        sql.append("LOCATE('4-5-1',MATCHREPORT) AS F451,")
        sql.append("LOCATE('4-4-2',MATCHREPORT) AS F442,")
        sql.append("LOCATE('4-3-3',MATCHREPORT) AS F433,")
        sql.append("LOCATE('3-5-2',MATCHREPORT) AS F352,")
        sql.append("LOCATE('3-4-3',MATCHREPORT) AS F343,")
        sql.append("LOCATE('2-5-3',MATCHREPORT) AS F253")
        sql.append(" FROM MATCHDETAILS inner join MATCHESKURZINFO ON MATCHDETAILS.MATCHID = MATCHESKURZINFO.MATCHID ")
        sql.append(" where 1=1 ")
        sql.append(whereClause)
        try {
            val rs =
                Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executePreparedQuery(
                    DBManager.getPreparedStatement(sql.toString())
                )
            while (rs!!.next()) {
                val fArray = arrayOf("0", "", "")
                setSystem(rs.getInt("F550"), "5-5-0", fArray)
                setSystem(rs.getInt("F541"), "5-4-1", fArray)
                setSystem(rs.getInt("F532"), "5-3-2", fArray)
                setSystem(rs.getInt("F523"), "5-2-3", fArray)
                setSystem(rs.getInt("F451"), "4-5-1", fArray)
                setSystem(rs.getInt("F442"), "4-4-2", fArray)
                setSystem(rs.getInt("F433"), "4-3-3", fArray)
                setSystem(rs.getInt("F352"), "3-5-2", fArray)
                setSystem(rs.getInt("F343"), "3-4-3", fArray)
                setSystem(rs.getInt("F253"), "2-5-3", fArray)
                for (i in 1 until rows.size) {
                    val txt = if (home) fArray[1] else if (fArray[2].length == 0) fArray[1] else fArray[2]
                    if (rows[i].type == 1 && rows[i].description == txt) {
                        rows[i].setMatchResult(rs.getInt("HEIMTORE"), rs.getInt("GASTTORE"), home)
                    }
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(MatchesOverviewQuery::class.java, e)
        }
    }

    private fun setSystem(column: Int, formation: String, fArray: Array<String>) {
        val max = fArray[0].toInt()
        if (column > 0) {
            if (max == 0) {
                fArray[0] = column.toString()
                fArray[1] = formation
            } else if (max > column) {
                fArray[2] = fArray[1]
                fArray[1] = formation
            } else {
                fArray[0] = column.toString()
                fArray[2] = formation
            }
        }
    }

    private fun setRows(rows: ArrayList<MatchesOverviewRow>, whereClause: StringBuilder, home: Boolean) {
        for (i in 1 until rows.size) {
            if (rows[i].typeValue > Int.MIN_VALUE) {
                val whereSpecial = " AND " + rows[i].getColumnName(home) + " = " + rows[i].typeValue
                setMatchesOverviewRow(rows[i], whereClause.toString() + whereSpecial, home)
            }
        }
    }

    private fun setMatchesOverviewRow(row: MatchesOverviewRow, whereClause: String, home: Boolean) {
        val sql = StringBuilder(500)
        val from = " FROM MATCHDETAILS inner join MATCHESKURZINFO ON MATCHDETAILS.MATCHID = MATCHESKURZINFO.MATCHID "
        sql.append("SELECT SUM(ANZAHL) AS A1,SUM(G1) AS G,SUM(U1) AS U,SUM(V1) AS V, SUM(HTORE1) AS HEIMTORE, SUM(GTORE1) AS GASTTORE FROM (")
        sql.append("select  COUNT(*) AS ANZAHL, 0 AS G1,0 AS U1, 0 AS V1, SUM(HEIMTORE) AS HTORE1, SUM(GASTTORE) AS GTORE1 ")
            .append(from).append(" where 1 = 1 ")
        sql.append(whereClause).append(" UNION ")
        sql.append("SELECT 0 AS ANZAHL,  COUNT(*) AS G1,0 AS U1, 0 AS V1, 0 AS HTORE1, 0 AS GTORE1 ").append(from)
            .append(" where HEIMTORE ").append(if (home) ">" else "<").append(" GASTTORE ")
        sql.append(whereClause).append(" UNION ")
        sql.append("SELECT  0 AS ANZAHL,  0 AS G1,COUNT(*) AS U1, 0 AS V1, 0 AS HTORE1, 0 AS GTORE1 ").append(from)
            .append(" where HEIMTORE = GASTTORE ")
        sql.append(whereClause).append(" UNION ")
        sql.append("select  0 AS ANZAHL,  0 AS G1, 0 AS U1, COUNT(*) AS V1, 0 AS HTORE1, 0 AS GTORE1 ").append(from)
            .append(" where HEIMTORE ").append(if (home) "<" else ">").append(" GASTTORE ")
        sql.append(whereClause)
        sql.append(")")
        try {
            val rs =
                Objects.requireNonNull<JDBCAdapter?>(DBManager.jdbcAdapter).executePreparedQuery(
                    DBManager.getPreparedStatement(sql.toString())
                )
            if (rs == null) {
                HOLogger.instance().log(MatchesOverviewQuery::class.java, sql.toString())
                return
            }
            if (rs.next()) {
                row.count = rs.getInt("A1")
                row.win = rs.getInt("G")
                row.draw = rs.getInt("U")
                row.loss = rs.getInt("V")
                row.homeGoals = rs.getInt(if (home) "HEIMTORE" else "GASTTORE")
                row.awayGoals = rs.getInt(if (home) "GASTTORE" else "HEIMTORE")
            }
        } catch (e: Exception) {
            HOLogger.instance().log(MatchesOverviewQuery::class.java, e)
        }
    }
}
