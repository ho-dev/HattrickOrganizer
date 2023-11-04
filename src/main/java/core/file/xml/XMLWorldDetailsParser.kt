/*
 * xmlLeagureFixturesMiniParser.java
 *
 * Created on 12. Januar 2004, 13:38
 */
package core.file.xml

import core.model.HOVerwaltung
import core.model.WorldDetailLeague
import core.util.HODateTime
import core.util.HOLogger

import org.w3c.dom.Document
import org.w3c.dom.Element

object XMLWorldDetailsParser {

    fun parseWorldDetailsFromString(inputStream: String, leagueID: String): Map<String, String> {
        return parseDetails(XMLManager.parseString(inputStream), leagueID)
    }

    fun parseDetails(doc: Document?): List<WorldDetailLeague> {
        val detailsList = mutableListOf<WorldDetailLeague>()

        if (doc == null) {
            return detailsList
        }

        var root = doc.documentElement

        try {
            root = root.getElementsByTagName("LeagueList").item(0) as (Element)
            val list = root.getElementsByTagName("League")

            for (i in 0..<list.length) {
                root = list.item(i) as (Element)
                val tmp = WorldDetailLeague()
                var ele = root.getElementsByTagName("LeagueID").item(0) as Element
                tmp.leagueId = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele))
                ele = root.getElementsByTagName("EnglishName").item(0) as Element
                tmp.countryName = XMLManager.getFirstChildNodeValue(ele)
                ele = root.getElementsByTagName("ActiveTeams").item(0) as Element
                tmp.activeUsers = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele))

                root = root.getElementsByTagName("Country").item(0) as Element
                ele = root.getElementsByTagName("CountryID").item(0) as Element
                tmp.countryId = Integer.parseInt(XMLManager.getFirstChildNodeValue(ele))
                detailsList.add(tmp)
            }
        } catch (e: Exception) {
            HOLogger.instance().log(XMLWorldDetailsParser.javaClass, e)
        }

        return detailsList
    }

    private fun initWorldDetailsMap(): MutableMap<String, String> {
        val map = SafeInsertMap()
        val model = HOVerwaltung.instance().model

        if (model != null) {
            val basics = model.getBasics()
            val extraData = model.getXtraDaten()
            if (basics != null && extraData != null) {
                var trainingDate = extraData.getNextTrainingDate()
                var economyDate = extraData.getEconomyDate()
                var seriesMatchDate = extraData.getSeriesMatchDate()
                val now = HODateTime.now()
                while (trainingDate.isBefore(now)) trainingDate = trainingDate.plusDaysAtSameLocalTime(7)
                while (economyDate.isBefore(now)) economyDate = economyDate.plusDaysAtSameLocalTime(7)
                while (seriesMatchDate.isBefore(now)) seriesMatchDate = seriesMatchDate.plusDaysAtSameLocalTime(7)

                map.insert("LeagueID", "" + basics.liga)
                map.insert("Season", "" + basics.season)
                map.insert("SeasonOffset", "" + basics.seasonOffset)
                map.insert("MatchRound", "" + basics.spieltag)
                map.insert("TrainingDate", trainingDate.toHT())
                map.insert("EconomyDate", economyDate.toHT())
                map.insert("SeriesMatchDate", seriesMatchDate.toHT())
                map.insert("CountryID", "" + model.getXtraDaten().countryId)
            }
        }
        return map
    }

    private fun parseDetails(doc: Document?, leagueID: String): Map<String, String> {
        val map = initWorldDetailsMap()
        if (doc == null) {
            return map
        }

        var root = doc.documentElement

        try {
            // Daten füllen
            root = root.getElementsByTagName("LeagueList").item(0) as Element
            val list = root.getElementsByTagName("League")

            for (i in 0..<list.length) {
                val leagueElement = (list.item(i) as Element).getElementsByTagName("LeagueID").item(0) as Element
                val tempLeagueID = XMLManager.getFirstChildNodeValue(leagueElement)

                if (tempLeagueID == leagueID) {
                    root = list.item(i) as Element

                    // Land
                    var ele = root.getElementsByTagName("LeagueID").item(0) as Element
                    map.put("LeagueID", (XMLManager.getFirstChildNodeValue(ele)))
                    ele = root.getElementsByTagName("Season").item(0) as Element
                    map.put("Season", (XMLManager.getFirstChildNodeValue(ele)))
                    ele = root.getElementsByTagName("SeasonOffset").item(0) as Element
                    map.put("SeasonOffset", (XMLManager.getFirstChildNodeValue(ele)))
                    ele = root.getElementsByTagName("MatchRound").item(0) as Element
                    map.put("MatchRound", (XMLManager.getFirstChildNodeValue(ele)))

                    // Dates
                    ele = root.getElementsByTagName("TrainingDate").item(0) as Element
                    map.put("TrainingDate", (XMLManager.getFirstChildNodeValue(ele)))
                    ele = root.getElementsByTagName("EconomyDate").item(0) as Element
                    map.put("EconomyDate", (XMLManager.getFirstChildNodeValue(ele)))
                    ele = root.getElementsByTagName("SeriesMatchDate").item(0) as Element
                    map.put("SeriesMatchDate", (XMLManager.getFirstChildNodeValue(ele)))

                    // Country
                    root = root.getElementsByTagName("Country").item(0) as Element
                    ele = root.getElementsByTagName("CountryID").item(0) as Element
                    map.put("CountryID", (XMLManager.getFirstChildNodeValue(ele)))

                    // Remove for ugly second team fix

                    ele = root.getElementsByTagName("CurrencyRate").item(0) as Element
                    map.put("CurrencyRate", (XMLManager.getFirstChildNodeValue(ele)))

                    // fertig
                    break
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(XMLWorldDetailsParser.javaClass, e)
        }

        return map
    }

    fun updateTeamInfoWithCurrency(info: TeamInfo, input: String): TeamInfo {

        val doc = XMLManager.parseString(input)
        var root = doc!!.documentElement

        root = root.getElementsByTagName("LeagueList").item(0) as Element
        root = root.getElementsByTagName("Country").item(0) as Element

        var ele = root.getElementsByTagName("CurrencyRate").item(0) as Element
        info.currencyRate = XMLManager.getFirstChildNodeValue(ele)

        ele = root.getElementsByTagName("CountryID").item(0) as Element
        info.countryId = XMLManager.getFirstChildNodeValue(ele)

        return info
    }

}
