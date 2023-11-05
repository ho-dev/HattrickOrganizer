package core.file.xml

import core.file.xml.XMLManager.getFirstChildNodeValue
import core.file.xml.XMLManager.parseString
import core.util.HOLogger
import org.w3c.dom.Document
import org.w3c.dom.Element

object XMLRegionParser {
    fun parseRegionDetailsFromString(str: String): Map<String, String> {
        return parseDetails(parseString(str))
    }

    private fun parseDetails(doc: Document?): Map<String, String> {
        val map = SafeInsertMap()
        if (doc == null) {
            return map
        }
        try {
            var root:Element? = doc.documentElement
            val ele = root?.getElementsByTagName("FetchedDate")?.item(0) as Element?
            map.insert("FetchedDate", getFirstChildNodeValue(ele))

            // Get Region Data info
            root = root?.getElementsByTagName("League")?.item(0) as Element?
            extract(root, map, "LeagueID")
            extract(root, map, "LeagueName")

            root = root?.getElementsByTagName("Region")?.item(0) as Element?
            extract(root, map, "RegionID")
            extract(root, map, "RegionName")
            extract(root, map, "NumberOfUsers")
            extract(root, map, "NumberOfOnline")
            extract(root, map, "WeatherID")
            extract(root, map, "TomorrowWeatherID")
        } catch (e: Exception) {
            HOLogger.instance().log(XMLRegionParser::class.java, e)
        }
        return map
    }

    private fun extract(root: Element?, map: SafeInsertMap, key: String) {
        map.insert(key, getFirstChildNodeValue(root?.getElementsByTagName(key)?.item(0) as Element?))
    }
}
