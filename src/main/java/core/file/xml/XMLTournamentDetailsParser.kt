package core.file.xml

import core.file.xml.XMLManager.parseString
import core.model.Tournament.TournamentDetails
import core.util.HODateTime
import core.util.HOLogger
import org.w3c.dom.Document
import org.w3c.dom.Element

object XMLTournamentDetailsParser {
	fun parseTournamentDetailsFromString(input: String): TournamentDetails {
        return createTournamentDetails(parseString(input))
    }

    private fun createTournamentDetails(doc: Document?): TournamentDetails {
        val oTournamentDetails = TournamentDetails()

        if (doc != null) {
            val root = doc.documentElement
            try {
                var list = root.getElementsByTagName("Tournament")
                var ele = list.item(0) as Element
                var tmp: Element = ele.getElementsByTagName("TournamentId").item(0) as Element
                oTournamentDetails.tournamentId = tmp.firstChild.nodeValue.toInt()
                tmp = ele.getElementsByTagName("Name").item(0) as Element
                oTournamentDetails.name = tmp.firstChild.nodeValue
                tmp = ele.getElementsByTagName("TournamentType").item(0) as Element
                oTournamentDetails.tournamentType = tmp.firstChild.nodeValue.toInt()
                tmp = ele.getElementsByTagName("Season").item(0) as Element
                oTournamentDetails.season = tmp.firstChild.nodeValue.toInt().toShort()
                tmp = ele.getElementsByTagName("LogoUrl").item(0) as Element
                if (tmp.firstChild != null) {
                    oTournamentDetails.logoUrl = tmp.firstChild.nodeValue
                }
                tmp = ele.getElementsByTagName("TrophyType").item(0) as Element
                oTournamentDetails.trophyType = tmp.firstChild.nodeValue.toInt()
                tmp = ele.getElementsByTagName("NumberOfTeams").item(0) as Element
                oTournamentDetails.numberOfTeams = tmp.firstChild.nodeValue.toInt()
                tmp = ele.getElementsByTagName("NumberOfGroups").item(0) as Element
                oTournamentDetails.numberOfGroups = tmp.firstChild.nodeValue.toInt()
                tmp = ele.getElementsByTagName("LastMatchRound").item(0) as Element
                oTournamentDetails.lastMatchRound = tmp.firstChild.nodeValue.toInt().toShort()
                tmp = ele.getElementsByTagName("FirstMatchRoundDate").item(0) as Element
                var tempDate = HODateTime.fromHT(tmp.firstChild.nodeValue)
                oTournamentDetails.firstMatchRoundDate = tempDate
                tmp = ele.getElementsByTagName("NextMatchRoundDate").item(0) as Element
                tempDate = HODateTime.fromHT(tmp.firstChild.nodeValue)
                oTournamentDetails.nextMatchRoundDate = tempDate
                tmp = ele.getElementsByTagName("IsMatchesOngoing").item(0) as Element
                oTournamentDetails.matchesOngoing = "1" == tmp.firstChild.nodeValue

                // Creator info
                list = root.getElementsByTagName("Creator")
                ele = list.item(0) as Element
                tmp = ele.getElementsByTagName("UserId").item(0) as Element
                oTournamentDetails.creator_UserId = tmp.firstChild.nodeValue.toInt()
                tmp = ele.getElementsByTagName("Loginname").item(0) as Element
                if (tmp.firstChild != null) {
                    oTournamentDetails.creator_Loginname = tmp.firstChild.nodeValue
                }
            } catch (e: Exception) {
                HOLogger.instance().log(XMLTournamentDetailsParser::class.java, e)
            }
        }
        return oTournamentDetails
    }
}
