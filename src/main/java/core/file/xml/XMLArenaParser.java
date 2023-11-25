// %2675225597:de.hattrickorganizer.logik.xml%
/*
 * XMLArenaParser.java
 *
 * Created on 5. Juni 2004, 15:40
 */
package core.file.xml;

import core.util.HOLogger;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author thetom
 */
public class XMLArenaParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLArenaParser() {
	}

	public static Map<String, String> parseArenaFromString(String str) {
		return parseDetails(XMLManager.parseString(str));
	}

	private static Map<String, String> parseDetails(Document doc) {
		Map<String, String> map = new SafeInsertMap();

		if (doc == null) {
			return map;
		}

		try {
			Element root = doc.getDocumentElement();
			Element ele = (Element) root.getElementsByTagName("FetchedDate")
					.item(0);
			map.put("FetchedDate", (XMLManager.getFirstChildNodeValue(ele)));

			// Root wechseln
			root = (Element) root.getElementsByTagName("Arena").item(0);
			ele = (Element) root.getElementsByTagName("ArenaID").item(0);
			map.put("ArenaID", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("ArenaName").item(0);
			map.put("ArenaName", (XMLManager.getFirstChildNodeValue(ele)));

			Element tmpRoot = (Element) root.getElementsByTagName("Team").item(
					0);
			ele = (Element) tmpRoot.getElementsByTagName("TeamID").item(0);
			map.put("TeamID", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) tmpRoot.getElementsByTagName("TeamName").item(0);
			map.put("TeamName", (XMLManager.getFirstChildNodeValue(ele)));

			tmpRoot = (Element) root.getElementsByTagName("League").item(0);
			ele = (Element) tmpRoot.getElementsByTagName("LeagueID").item(0);
			map.put("LeagueID", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) tmpRoot.getElementsByTagName("LeagueName").item(0);
			map.put("LeagueName", (XMLManager.getFirstChildNodeValue(ele)));

			tmpRoot = (Element) root.getElementsByTagName("Region").item(0);
			ele = (Element) tmpRoot.getElementsByTagName("RegionID").item(0);
			map.put("RegionID", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) tmpRoot.getElementsByTagName("RegionName").item(0);
			map.put("RegionName", (XMLManager.getFirstChildNodeValue(ele)));

			tmpRoot = (Element) root.getElementsByTagName("CurrentCapacity")
					.item(0);
			ele = (Element) tmpRoot.getElementsByTagName("RebuiltDate").item(0);

			if (XMLManager.getAttributeValue(ele, "Available").trim()
					.equalsIgnoreCase("true")) {
				map.put("RebuiltDate", (XMLManager.getFirstChildNodeValue(ele)));
			}

			ele = (Element) tmpRoot.getElementsByTagName("Terraces").item(0);
			map.put("Terraces", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) tmpRoot.getElementsByTagName("Basic").item(0);
			map.put("Basic", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) tmpRoot.getElementsByTagName("Roof").item(0);
			map.put("Roof", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) tmpRoot.getElementsByTagName("VIP").item(0);
			map.put("VIP", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) tmpRoot.getElementsByTagName("Total").item(0);
			map.put("Total", (XMLManager.getFirstChildNodeValue(ele)));

			tmpRoot = (Element) root.getElementsByTagName("ExpandedCapacity")
					.item(0);

			if (XMLManager.getAttributeValue(ele, "Available").trim()
					.equalsIgnoreCase("true")) {
				map.put("isExpanding", "1");
				ele = (Element) tmpRoot.getElementsByTagName("ExpansionDate")
						.item(0);
				map.put("ExpansionDate",
						(XMLManager.getFirstChildNodeValue(ele)));
				ele = (Element) tmpRoot.getElementsByTagName("Terraces")
						.item(0);
				map.put("ExTerraces", (XMLManager.getFirstChildNodeValue(ele)));
				ele = (Element) tmpRoot.getElementsByTagName("Basic").item(0);
				map.put("ExBasic", (XMLManager.getFirstChildNodeValue(ele)));
				ele = (Element) tmpRoot.getElementsByTagName("Roof").item(0);
				map.put("ExRoof", (XMLManager.getFirstChildNodeValue(ele)));
				ele = (Element) tmpRoot.getElementsByTagName("VIP").item(0);
				map.put("ExVIP", (XMLManager.getFirstChildNodeValue(ele)));
				ele = (Element) tmpRoot.getElementsByTagName("Total").item(0);
				map.put("ExTotal", (XMLManager.getFirstChildNodeValue(ele)));
			} else {
				map.put("isExpanding", "0");
				map.put("ExpansionDate", "0");
				map.put("ExTerraces", "0");
				map.put("ExBasic", "0");
				map.put("ExRoof", "0");
				map.put("ExVIP", "0");
				map.put("ExTotal", "0");
			}
		} catch (Exception e) {
			HOLogger.instance().log(XMLArenaParser.class, e);
		}

		return map;
	}
}
