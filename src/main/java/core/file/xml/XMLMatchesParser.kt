package core.file.xml

import core.db.DBManager.getTournamentDetailsFromDB
import core.db.DBManager.storeTournamentDetailsIntoDB
import core.file.xml.XMLManager.parseString
import core.model.cup.CupLevel
import core.model.cup.CupLevelIndex
import core.model.enums.MatchType
import core.model.match.MatchKurzInfo
import core.net.OnlineWorker
import core.util.HODateTime
import core.util.HOLogger
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

object XMLMatchesParser {
    fun parseMatchesFromString(input: String): List<MatchKurzInfo> {
        return createMatches(parseString(input))
    }

    /**
     * Returns [Int] corresponding to status String.
     */
    private fun getStatus(status: String): Int {
        if (status.equals("FINISHED", ignoreCase = true)) {
            return MatchKurzInfo.FINISHED
        } else if (status.equals("ONGOING", ignoreCase = true)) {
            return MatchKurzInfo.ONGOING
        } else if (status.equals("UPCOMING", ignoreCase = true)) {
            return MatchKurzInfo.UPCOMING
        }
        return -1
    }

    private fun createMatches(doc: Document?): List<MatchKurzInfo> {
        var match: MatchKurzInfo
        val matchesList: MutableList<MatchKurzInfo> = ArrayList()

        if (doc != null) {
            val root = doc.documentElement
            try {
                val list:NodeList? = root.getElementsByTagName("Match")
                var ele: Element?
                var tmp: Element?

                if (list != null) {
                    for (i in 0..<list.length) {
                        match = MatchKurzInfo()
                        ele = list.item(i) as Element?
                        tmp = ele?.getElementsByTagName("MatchDate")?.item(0) as Element?
                        match.matchSchedule = HODateTime.fromHT(tmp?.firstChild?.nodeValue)

                        tmp = ele?.getElementsByTagName("MatchID")?.item(0) as Element?
                        match.matchID = tmp?.firstChild?.nodeValue?.toInt() ?: -1

                        tmp = ele?.getElementsByTagName("MatchType")?.item(0) as Element?
                        val iMatchType = tmp?.firstChild?.nodeValue?.toInt()
                        match.matchType = MatchType.getById(iMatchType)

                        if (iMatchType == 3) {
                            tmp = ele?.getElementsByTagName("CupLevel")?.item(0) as Element?
                            val iCupLevel = tmp?.firstChild?.nodeValue?.toInt()
                            match.cupLevel = CupLevel.fromInt(iCupLevel)

                            tmp = ele?.getElementsByTagName("CupLevelIndex")?.item(0) as Element?
                            val iCupLevelIndex = tmp?.firstChild?.nodeValue?.toInt()
                            match.cupLevelIndex = CupLevelIndex.fromInt(iCupLevelIndex)
                        } else if (iMatchType == 50) {
                            tmp = ele?.getElementsByTagName("MatchContextId")?.item(0) as Element?
                            val tournamentId = tmp?.firstChild?.nodeValue?.toInt()
                            if (tournamentId != null) {
                                match.matchContextId = tournamentId
                                var oTournamentDetails = getTournamentDetailsFromDB(tournamentId)
                                if (oTournamentDetails == null) {
                                    oTournamentDetails =
                                        OnlineWorker.getTournamentDetails(tournamentId) // download info about tournament from HT
                                    storeTournamentDetailsIntoDB(oTournamentDetails) // store tournament details into DB
                                }
                                match.tournamentTypeID = oTournamentDetails!!.tournamentType
                            }
                        }
                        tmp = ele?.getElementsByTagName("HomeTeam")?.item(0) as Element?
                        match.homeTeamID = tmp?.getElementsByTagName("HomeTeamID")?.item(0)
                            ?.firstChild?.nodeValue?.toInt() ?: -1
                        match.homeTeamName = tmp?.getElementsByTagName("HomeTeamName")?.item(0)
                            ?.firstChild?.nodeValue
                        tmp = ele?.getElementsByTagName("AwayTeam")?.item(0) as Element?
                        match.guestTeamID = tmp?.getElementsByTagName("AwayTeamID")?.item(0)
                            ?.firstChild?.nodeValue?.toInt() ?: -1
                        match.guestTeamName = tmp?.getElementsByTagName("AwayTeamName")?.item(0)
                            ?.firstChild?.nodeValue
                        tmp = ele?.getElementsByTagName("Status")?.item(0) as Element?
                        if (tmp?.firstChild?.nodeValue != null) {
                            match.matchStatus = getStatus(tmp.firstChild.nodeValue)
                        } else {
                            match.matchStatus -1
                        }
                        if (match.matchStatus == MatchKurzInfo.FINISHED) {
                            tmp = ele?.getElementsByTagName("HomeGoals")?.item(0) as Element?
                            match.homeTeamGoals = tmp?.firstChild?.nodeValue?.toInt() ?: -1
                            tmp = ele?.getElementsByTagName("AwayGoals")?.item(0) as Element?
                            match.guestTeamGoals = tmp?.firstChild?.nodeValue?.toInt() ?: -1
                        } else if (match.matchStatus == MatchKurzInfo.UPCOMING) {
                            try {
                                tmp = ele?.getElementsByTagName("OrdersGiven")?.item(0) as Element?
                                match.isOrdersGiven = tmp?.firstChild?.nodeValue.equals("TRUE", ignoreCase = true)
                            } catch (e: Exception) {
                                // We will end up here if the match is not the user's
                                match.isOrdersGiven = false
                            }
                        }
                        matchesList.add(match)
                    }
                }
            } catch (e: Exception) {
                HOLogger.instance().log(XMLMatchesParser::class.java, e)
                matchesList.clear()
            }
        }
        return matchesList
    }
}
