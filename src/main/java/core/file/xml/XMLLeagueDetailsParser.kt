/*
 * xmlLeagueDetailsParser.java
 *
 * Created on 12. Januar 2004, 14:04
 */
package core.file.xml

import core.util.HOLogger

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

/**
 * 
 * @author thomas.werth
 */
object XMLLeagueDetailsParser {

	fun parseLeagueDetailsFromString(str: String, teamID: String): Map<String, String> {
		return parseDetails(XMLManager.parseString(str), teamID)
	}

	private fun parseDetails(doc: Document?, teamID: String): Map<String, String> {
		val map = SafeInsertMap()

		if (doc == null) {
			return map
		}

		var root:Element? = doc.documentElement

		try {

			var ele: Element? = root?.getElementsByTagName("LeagueLevelUnitName")?.item(0) as Element?
			map.insert("LeagueLevelUnitName", XMLManager.getFirstChildNodeValue(ele))

			val list:NodeList? = root?.getElementsByTagName("Team")
			if (list != null) {
				for (i in 0..<list.length) {
					root = list.item(i) as Element?
					ele = root?.getElementsByTagName("TeamID")?.item(0) as Element?

					if (XMLManager.getFirstChildNodeValue(ele) == teamID) {
						ele = root?.getElementsByTagName("TeamID")?.item(0) as Element?
						map.insert("TeamID", XMLManager.getFirstChildNodeValue(ele))
						ele = root?.getElementsByTagName("Position")?.item(0) as Element?
						map.insert("Position", XMLManager.getFirstChildNodeValue(ele))
						ele = root?.getElementsByTagName("TeamName")?.item(0) as Element?
						map.insert("TeamName", XMLManager.getFirstChildNodeValue(ele))
						ele = root?.getElementsByTagName("Matches")?.item(0) as Element?
						map.insert("Matches", XMLManager.getFirstChildNodeValue(ele))
						ele = root?.getElementsByTagName("GoalsFor")?.item(0) as Element?
						map.insert("GoalsFor", XMLManager.getFirstChildNodeValue(ele))
						ele = root?.getElementsByTagName("GoalsAgainst")?.item(0) as Element?
						map.insert("GoalsAgainst", XMLManager.getFirstChildNodeValue(ele))
						ele = root?.getElementsByTagName("Points")?.item(0) as Element?
						map.insert("Points", XMLManager.getFirstChildNodeValue(ele))

						// Done!
						break
					}
				}
			}
		} catch (e: Exception) {
			HOLogger.instance().log(XMLLeagueDetailsParser.javaClass, e)
		}

		return map
	}


	/**
	 * Parses the details as a league details XML string, and creates {@link TeamStats} details
	 * for all the teams present in the league.
	 *
	 * @param details — League details XML as a String.
	 * @return Map<String, TeamStats> — Team details held as {@link TeamStats}, indexed by team ID as a String.
	 */
	fun parseLeagueDetails(details: String): Map<String, TeamStats> {
		val document = XMLManager.parseString(details)
		val teamInfoMap = mutableMapOf<String, TeamStats>()

		if (document == null) {
			return teamInfoMap
		}

		val root:Element? = document.documentElement
		root?.normalize()

		val leagueName = root?.getElementsByTagName("LeagueLevelUnitName")?.item(0)?.textContent
		val leagueLevel = root?.getElementsByTagName("LeagueLevel")?.item(0)?.textContent?.toInt()
		val list:NodeList? = document.getElementsByTagName("Team")

		if (list != null) {
			for (i in 0..<list.length) {
				val teamStats = TeamStats()
				val elt:Element? = list.item(i) as Element?
				val teamId = elt?.getElementsByTagName("TeamID")?.item(0)?.textContent
				teamStats.teamId = teamId?.toInt() ?: -1
				teamStats.teamName = elt?.getElementsByTagName("TeamName")?.item(0)?.textContent

				teamStats.leagueRank = leagueLevel ?: -1
				teamStats.leagueName = leagueName

				teamStats.position = elt?.getElementsByTagName("Position")?.item(0)?.textContent?.toInt() ?: -1
				teamStats.points = elt?.getElementsByTagName("Points")?.item(0)?.textContent?.toInt() ?: -1

				teamStats.goalsFor = elt?.getElementsByTagName("GoalsFor")?.item(0)?.textContent?.toInt() ?: -1
				teamStats.goalsAgainst = elt?.getElementsByTagName("GoalsAgainst")?.item(0)?.textContent?.toInt() ?: -1

				if (teamId != null) {
					teamInfoMap[teamId] = teamStats
				}
			}
		}

		return teamInfoMap
	}
}
