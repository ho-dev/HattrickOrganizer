// %127961774:de.hattrickorganizer.logik.xml%
/*
 * xmlLeagueDetailsParser.java
 *
 * Created on 12. Januar 2004, 14:04
 */
package core.file.xml;

import core.util.HOLogger;

import java.util.HashMap;
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

		Element root = doc.getDocumentElement();

		try {

			Element ele = (Element) root.getElementsByTagName("LeagueLevelUnitName").item(0);
			map.put("LeagueLevelUnitName", XMLManager.getFirstChildNodeValue(ele));

			NodeList list = root.getElementsByTagName("Team");

			for (int i = 0; (list != null) && (i < list.getLength()); i++) {
				root = (Element) list.item(i);
				// Team suchen
				if (XMLManager.getFirstChildNodeValue(
						(Element) root.getElementsByTagName("TeamID").item(0)).equals(teamID)) {

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


	/**
	 * Parses the details as a league details XML string, and creates {@link TeamStats} details
	 * for all the teams present in the league.
	 *
	 * @param details — League details XML as a String.
	 * @return Map<String, TeamStats> — Team details held as {@link TeamStats}, indexed by team ID as a String.
	 */
	public static Map<String, TeamStats> parseLeagueDetails(String details) {
		Document document = XMLManager.parseString(details);

		Map<String, TeamStats> teamInfoMap = new HashMap<>();

		if (document == null) {
			return teamInfoMap;
		}

		document.getDocumentElement().normalize();

		Element root = document.getDocumentElement();
		String leagueName = root.getElementsByTagName("LeagueLevelUnitName").item(0).getTextContent();
		int leagueLevel = Integer.parseInt(root.getElementsByTagName("LeagueLevel").item(0).getTextContent());
		NodeList list = document.getElementsByTagName("Team");

		if (list != null) {
			for (int i = 0; i < list.getLength(); i++) {
				TeamStats teamStats = new TeamStats();
				Element elt = (Element) list.item(i);
				String teamId = elt.getElementsByTagName("TeamID").item(0).getTextContent();
				teamStats.setTeamId(Integer.parseInt(teamId));
				teamStats.setTeamName(elt.getElementsByTagName("TeamName").item(0).getTextContent());

				teamStats.setLeagueRank(leagueLevel);
				teamStats.setLeagueName(leagueName);

				teamStats.setPosition(Integer.parseInt(elt.getElementsByTagName("Position").item(0).getTextContent()));
				teamStats.setPoints(Integer.parseInt(elt.getElementsByTagName("Points").item(0).getTextContent()));

				teamStats.setGoalsFor(Integer.parseInt(elt.getElementsByTagName("GoalsFor").item(0).getTextContent()));
				teamStats.setGoalsAgainst(Integer.parseInt(elt.getElementsByTagName("GoalsAgainst").item(0).getTextContent()));


				teamInfoMap.put(teamId, teamStats);
			}
		}

		return teamInfoMap;
	}
}
