/*
 * XMLArenaParser.java
 *
 * Created on 5. Juni 2004, 15:40
 */
package core.file.xml

import core.util.HOLogger

import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * 
 * @author thetom
 */
object XMLArenaParser {

	fun parseArenaFromString(str: String): Map<String, String> {
		return parseDetails(XMLManager.parseString(str))
	}

	private fun parseDetails(doc: Document?): Map<String, String> {
		val map = SafeInsertMap()

		if (doc == null) {
			return map
		}

		try {
			var root = doc.documentElement
			var ele = root.getElementsByTagName("FetchedDate").item(0) as Element
			map.insert("FetchedDate", XMLManager.getFirstChildNodeValue(ele))

			// Change of root
			root = root.getElementsByTagName("Arena").item(0) as Element
			ele = root.getElementsByTagName("ArenaID").item(0) as Element
			map.insert("ArenaID", XMLManager.getFirstChildNodeValue(ele))
			ele = root.getElementsByTagName("ArenaName").item(0) as Element
			map.insert("ArenaName", (XMLManager.getFirstChildNodeValue(ele)))

			var tmpRoot = root.getElementsByTagName("Team").item(0) as Element
			ele = tmpRoot.getElementsByTagName("TeamID").item(0) as Element
			map.insert("TeamID", (XMLManager.getFirstChildNodeValue(ele)))
			ele = tmpRoot.getElementsByTagName("TeamName").item(0) as Element
			map.insert("TeamName", (XMLManager.getFirstChildNodeValue(ele)))

			tmpRoot = root.getElementsByTagName("League").item(0) as Element
			ele = tmpRoot.getElementsByTagName("LeagueID").item(0) as Element
			map.insert("LeagueID", (XMLManager.getFirstChildNodeValue(ele)))
			ele = tmpRoot.getElementsByTagName("LeagueName").item(0) as Element
			map.insert("LeagueName", (XMLManager.getFirstChildNodeValue(ele)))

			tmpRoot = root.getElementsByTagName("Region").item(0) as Element
			ele = tmpRoot.getElementsByTagName("RegionID").item(0) as Element
			map.insert("RegionID", (XMLManager.getFirstChildNodeValue(ele)))
			ele = tmpRoot.getElementsByTagName("RegionName").item(0) as Element
			map.insert("RegionName", (XMLManager.getFirstChildNodeValue(ele)))

			tmpRoot = root.getElementsByTagName("CurrentCapacity").item(0) as Element
			ele = tmpRoot.getElementsByTagName("RebuiltDate").item(0) as Element

			if (XMLManager.getAttributeValue(ele, "Available").trim().equals("true", true)) {
				map.insert("RebuiltDate", (XMLManager.getFirstChildNodeValue(ele)))
			}

			ele = tmpRoot.getElementsByTagName("Terraces").item(0) as Element
			map.insert("Terraces", (XMLManager.getFirstChildNodeValue(ele)))
			ele = tmpRoot.getElementsByTagName("Basic").item(0) as Element
			map.insert("Basic", (XMLManager.getFirstChildNodeValue(ele)))
			ele = tmpRoot.getElementsByTagName("Roof").item(0) as Element
			map.insert("Roof", (XMLManager.getFirstChildNodeValue(ele)))
			ele = tmpRoot.getElementsByTagName("VIP").item(0) as Element
			map.insert("VIP", (XMLManager.getFirstChildNodeValue(ele)))
			ele = tmpRoot.getElementsByTagName("Total").item(0) as Element
			map.insert("Total", (XMLManager.getFirstChildNodeValue(ele)))

			tmpRoot = root.getElementsByTagName("ExpandedCapacity").item(0) as Element

			if (XMLManager.getAttributeValue(ele, "Available").trim().equals("true", true)) {
				map.insert("isExpanding", "1")
				ele = tmpRoot.getElementsByTagName("ExpansionDate").item(0) as Element
				map.insert("ExpansionDate", (XMLManager.getFirstChildNodeValue(ele)))
				ele = tmpRoot.getElementsByTagName("Terraces").item(0) as Element
				map.insert("ExTerraces", (XMLManager.getFirstChildNodeValue(ele)))
				ele = tmpRoot.getElementsByTagName("Basic").item(0) as Element
				map.insert("ExBasic", (XMLManager.getFirstChildNodeValue(ele)))
				ele = tmpRoot.getElementsByTagName("Roof").item(0) as Element
				map.insert("ExRoof", (XMLManager.getFirstChildNodeValue(ele)))
				ele = tmpRoot.getElementsByTagName("VIP").item(0) as Element
				map.insert("ExVIP", (XMLManager.getFirstChildNodeValue(ele)))
				ele = tmpRoot.getElementsByTagName("Total").item(0) as Element
				map.insert("ExTotal", (XMLManager.getFirstChildNodeValue(ele)))
			} else {
				map.insert("isExpanding", "0")
				map.insert("ExpansionDate", "0")
				map.insert("ExTerraces", "0")
				map.insert("ExBasic", "0")
				map.insert("ExRoof", "0")
				map.insert("ExVIP", "0")
				map.insert("ExTotal", "0")
			}
		} catch (e: Exception) {
			HOLogger.instance().log(XMLArenaParser.javaClass, e)
		}

		return map
	}
}
