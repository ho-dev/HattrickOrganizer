/*
 * XMLSpielplanParser.java
 *
 * Created on 7. Oktober 2003, 13:42
 */
package core.file.xml

import core.file.xml.XMLManager.parseString
import core.model.series.Paarung
import core.util.HODateTime
import core.util.HOLogger
import module.series.Spielplan
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

/**
 *
 * @author thomas.werth
 */
object XMLSpielplanParser {
	fun parseSpielplanFromString(input: String): Spielplan? {
        val plan: Spielplan? = try {
            createSpielplan(parseString(input))
        } catch (e: RuntimeException) {
            HOLogger.instance().error(
                XMLSpielplanParser::class.java,
                "parseSpielplanFromString: $e\ninput xml was:\n $input"
            )
            throw e
        }
        return plan
    }

    private fun createPaarung(ele: Element?): Paarung {
        var tmp: Element?
        val spiel = Paarung()
        tmp = ele?.getElementsByTagName("MatchID")?.item(0) as Element?
        spiel.matchId = tmp?.firstChild?.nodeValue?.toInt() ?: -1
        tmp = ele?.getElementsByTagName("MatchRound")?.item(0) as Element?
        spiel.spieltag = tmp?.firstChild?.nodeValue?.toInt() ?: -1
        tmp = ele?.getElementsByTagName("HomeTeamID")?.item(0) as Element?
        spiel.heimId = tmp?.firstChild?.nodeValue?.toInt() ?: -1
        tmp = ele?.getElementsByTagName("AwayTeamID")?.item(0) as Element?
        spiel.gastId = tmp?.firstChild?.nodeValue?.toInt() ?: -1
        tmp = ele?.getElementsByTagName("HomeTeamName")?.item(0) as Element?
        spiel.heimName = tmp?.firstChild?.nodeValue
        tmp = ele?.getElementsByTagName("AwayTeamName")?.item(0) as Element?
        spiel.gastName = tmp?.firstChild?.nodeValue
        tmp = ele?.getElementsByTagName("MatchDate")?.item(0) as Element?
        spiel.datum = HODateTime.fromHT(tmp?.firstChild?.nodeValue)

        if (ele != null && ele.getElementsByTagName("AwayGoals").length > 0) {
            tmp = ele.getElementsByTagName("AwayGoals").item(0) as Element?
            spiel.toreGast = tmp?.firstChild?.nodeValue?.toInt() ?: -1
            tmp = ele.getElementsByTagName("HomeGoals").item(0) as Element?
            spiel.toreHeim = tmp?.firstChild?.nodeValue?.toInt() ?: -1
        }
        return spiel
    }

    private fun createSpielplan(doc: Document?): Spielplan? {
        var plan:Spielplan? = Spielplan()
        var ele: Element?
        val list: NodeList?

        if (doc == null) {
            return plan
        }

        // Tabelle erstellen
        val root: Element? = doc.documentElement
        try {
            if (plan != null) {
                // Daten füllen
                ele = root?.getElementsByTagName("LeagueLevelUnitID")?.item(0) as Element?
                plan.ligaId = ele?.firstChild?.nodeValue?.toInt() ?: -1
                try {
                    ele = root?.getElementsByTagName("LeagueLevelUnitName")?.item(0) as Element?
                    plan.ligaName = ele?.firstChild?.nodeValue
                } catch (e: Exception) {
                    plan.ligaName = ""
                }
                ele = root?.getElementsByTagName("Season")?.item(0) as Element?
                plan.saison = ele?.firstChild?.nodeValue?.toInt() ?: -1
                ele = root?.getElementsByTagName("FetchedDate")?.item(0) as Element?
                plan.fetchDate = HODateTime.fromHT(ele?.firstChild?.nodeValue)

                list = root?.getElementsByTagName("Match")
                if (list != null) {
                    for (i in 0..<list.length) {
                        plan.addEintrag(createPaarung(list.item(i) as Element?))
                    }
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(XMLSpielplanParser::class.java, e)
            plan = null
        }
        return plan
    }
}
