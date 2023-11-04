/*
 * xmlMatchArchivParser.java
 *
 * Created on 28. Juli 2004, 15:32
 */
package core.file.xml

import core.db.DBManager
import core.model.Tournament.TournamentDetails
import core.model.cup.CupLevel
import core.model.cup.CupLevelIndex
import core.model.match.MatchKurzInfo
import core.model.enums.MatchType
import core.net.OnlineWorker
import core.util.HODateTime
import core.util.HOLogger

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

/**
 * @author TheTom
 */
object XMLMatchArchivParser {


    fun parseMatchesFromString(input: String): List<MatchKurzInfo> {
        return createMatches(XMLManager.parseString(input))
    }

    private fun createMatches(doc: Document?): List<MatchKurzInfo> {
        val matches = mutableListOf<MatchKurzInfo>()

        if (doc == null) {
            return matches
        }

        val root = doc.documentElement
        var ele: Element
        var tmp: Element

        try {

            val isYouth = root.getElementsByTagName("IsYouth").item(0).firstChild.nodeValue.toBoolean()
            val nodeList: NodeList = root.getElementsByTagName("Match")

            for (i in 0..<nodeList.length) {
                val match = MatchKurzInfo()
                ele = nodeList.item(i) as Element

                // Daten füllen
                tmp = ele.getElementsByTagName("MatchDate").item(0) as Element
                match.matchSchedule = HODateTime.fromHT(tmp.firstChild.nodeValue)
                tmp = ele.getElementsByTagName("MatchID").item(0) as Element
                match.matchID = Integer.parseInt(tmp.firstChild.nodeValue)
                tmp = ele.getElementsByTagName("MatchType").item(0) as Element

                val matchType:Int = if (!isYouth) {
                    tmp.firstChild.nodeValue.toInt()
                } else {
                    // workaround for isyouth=true (MatchType is missing if isYouth==true)
                    MatchType.YOUTHLEAGUE.id;
                }

                match.matchType = MatchType.getById(matchType)

                tmp = ele.getElementsByTagName("MatchContextId").item(0) as Element
                val tournamentId = tmp.firstChild.nodeValue.toInt()
                match.matchContextId = tournamentId

                if (matchType == 3) {
                    tmp = ele.getElementsByTagName("CupLevel").item(0) as Element
                    val iCupLevel = Integer.parseInt(tmp.firstChild.nodeValue)
                    match.cupLevel = CupLevel.fromInt(iCupLevel)
                    tmp = ele.getElementsByTagName("CupLevelIndex").item(0) as Element
                    val iCupLevelIndex = Integer.parseInt(tmp.firstChild.nodeValue)
                    match.cupLevelIndex = CupLevelIndex.fromInt(iCupLevelIndex)
                } else if (matchType == 50) {
                    var oTournamentDetails: TournamentDetails? = DBManager.getTournamentDetailsFromDB(tournamentId)
                    if (oTournamentDetails == null) {
                        oTournamentDetails =
                            OnlineWorker.getTournamentDetails(tournamentId) // download info about tournament from HT
                        DBManager.storeTournamentDetailsIntoDB(oTournamentDetails) // store tournament details into DB
                    }
                    if (oTournamentDetails != null) {
                        match.tournamentTypeID = oTournamentDetails.tournamentType
                    }
                }

                tmp = ele.getElementsByTagName("HomeTeam").item(0) as Element
                match.homeTeamID = Integer.parseInt(
                    tmp.getElementsByTagName("HomeTeamID").item(0).firstChild.nodeValue
                )
                match.homeTeamName = tmp.getElementsByTagName("HomeTeamName").item(0).firstChild.nodeValue
                tmp = ele.getElementsByTagName("AwayTeam").item(0) as Element
                match.guestTeamID = Integer.parseInt(
                    tmp.getElementsByTagName("AwayTeamID").item(0)
                        .firstChild.nodeValue
                )
                match.guestTeamName = tmp.getElementsByTagName("AwayTeamName").item(0).firstChild.nodeValue
                tmp = ele.getElementsByTagName("HomeGoals").item(0) as Element
                match.homeTeamGoals = Integer.parseInt(tmp.firstChild.nodeValue)
                tmp = ele.getElementsByTagName("AwayGoals").item(0) as Element
                match.guestTeamGoals = Integer.parseInt(tmp.firstChild.nodeValue)
                match.isOrdersGiven = true
                match.matchStatus = MatchKurzInfo.FINISHED
                matches.add(match)
            }
        } catch (e: Exception) {
            matches.clear()
            HOLogger.instance().log(XMLMatchArchivParser.javaClass, e)
        }
        return matches
    }
}
