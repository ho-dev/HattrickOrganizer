// %127961774:de.hattrickorganizer.logik.xml%
/*
 * xmlLeagueDetailsParser.java
 *
 * Created on 12. Januar 2004, 14:04
 */
package core.file.xml;

import core.util.HOLogger;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author thomas.werth
 */
public class XMLLeagueDetailsParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLLeagueDetailsParser() {
	}

	public static Map<String, String> parseLeagueDetailsFromString(String str, String teamID) {
		return parseDetails(XMLManager.parseString(str), teamID);
	}

	private static Map<String, String> parseDetails(Document doc, String teamID) {
		Map<String, String> map = new MyHashtable();

		if (doc == null) {
			return map;
		}

		// Tabelle erstellen
		Element root = doc.getDocumentElement();

		try {
			// Daten füllen
			Element ele = (Element) root.getElementsByTagName("LeagueLevelUnitName").item(0);
			map.put("LeagueLevelUnitName", XMLManager.getFirstChildNodeValue(ele));

			// Einträge adden
			NodeList list = root.getElementsByTagName("Team");

			for (int i = 0; (list != null) && (i < list.getLength()); i++) {
				root = (Element) list.item(i);
				// Team suchen
				if (XMLManager.getFirstChildNodeValue(
						(Element) root.getElementsByTagName("TeamID").item(0)).equals(teamID)) {

					// Land
					ele = (Element) root.getElementsByTagName("TeamID").item(0);
					map.put("TeamID", XMLManager.getFirstChildNodeValue(ele));
					ele = (Element) root.getElementsByTagName("Position").item(0);
					map.put("Position", XMLManager.getFirstChildNodeValue(ele));
					ele = (Element) root.getElementsByTagName("TeamName").item(0);
					map.put("TeamName", XMLManager.getFirstChildNodeValue(ele));
					ele = (Element) root.getElementsByTagName("Matches").item(0);
					map.put("Matches", XMLManager.getFirstChildNodeValue(ele));
					ele = (Element) root.getElementsByTagName("GoalsFor").item(0);
					map.put("GoalsFor", XMLManager.getFirstChildNodeValue(ele));
					ele = (Element) root.getElementsByTagName("GoalsAgainst").item(0);
					map.put("GoalsAgainst", XMLManager.getFirstChildNodeValue(ele));
					ele = (Element) root.getElementsByTagName("Points").item(0);
					map.put("Points", XMLManager.getFirstChildNodeValue(ele));

					// fertig
					break;
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(XMLLeagueDetailsParser.class, e);
		}

		return map;
	}
}
