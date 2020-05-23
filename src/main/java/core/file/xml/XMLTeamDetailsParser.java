// %168276825:de.hattrickorganizer.logik.xml%
/*
 * xmlTeamDetailsParser.java
 *
 * Created on 12. Januar 2004, 10:26
 */
package core.file.xml;

import core.util.HOLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author thomas.werth
 */
public class XMLTeamDetailsParser {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLTeamDetailsParser() {
	}

	public static String fetchRegionID(String xmlFile) {
		return fetchTeamDetail(xmlFile, "Region", "RegionID");
	}

	public static String fetchArenaID(String xmlFile) {
		return fetchTeamDetail(xmlFile, "Arena", "ArenaID");
	}

	private static String fetchTeamDetail(String xmlFile, String section, String attribute){
		try {
			Document doc = XMLManager.parseString(xmlFile);

			if (doc == null) {
				return "-1";
			}

			// Tabelle erstellen
			Element root = doc.getDocumentElement();

			// Root wechseln
			root = (Element) root.getElementsByTagName("Team").item(0);
			root = (Element) root.getElementsByTagName(section).item(0);
			Element ele = (Element) root.getElementsByTagName(attribute).item(0);
			return XMLManager.getFirstChildNodeValue(ele);
		} catch (Exception ex) {
			HOLogger.instance().log(XMLTeamDetailsParser.class, ex);
		}

		return "-1";
	}

	public static Map<String, String> parseTeamdetailsFromString(String inputStream, int teamId) {
		return parseDetails(XMLManager.parseString(inputStream), teamId);
	}

	private static Map<String, String> parseDetails(Document doc, int teamId) {
		Element ele, root;
		Map<String, String> hash = new core.file.xml.MyHashtable();

		if (doc == null) {
			return hash;
		}

		root = doc.getDocumentElement();

		try {

			// FetchedDate
			ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
			hash.put("FetchedDate", (XMLManager.getFirstChildNodeValue(ele)));

			// User
			root = (Element) root.getElementsByTagName("User").item(0);
			ele = (Element) root.getElementsByTagName("Loginname").item(0);
			hash.put("Loginname", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("LastLoginDate").item(0);
			hash.put("LastLoginDate", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("SupporterTier").item(0);
			String supportValue = XMLManager.getFirstChildNodeValue(ele);
			String supportStatus = "False";
			if (supportValue.trim().length() > 0) {
				supportStatus = "True";
			}
			hash.put("HasSupporter", supportStatus);

			// We need to find the correct team

			Element team = null;
			ele = (Element) doc.getDocumentElement().getElementsByTagName("Teams").item(0);
			if ( ele != null) {
				root = ele;
				NodeList list = root.getElementsByTagName("Team");
				for (int i = 0; (list != null) && (i < list.getLength()); i++) {
					team = (Element) list.item(i);

					ele = (Element) team.getElementsByTagName("TeamID").item(0);
					if (Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)) == teamId) {
						break;
					}
				}
			}
			else {
				team = (Element)doc.getDocumentElement().getElementsByTagName("Team").item(0);
			}

			if (team == null) { 
				return hash;
			}
			
			ele = (Element) team.getElementsByTagName("TeamID").item(0);
			hash.put("TeamID", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) team.getElementsByTagName("TeamName").item(0);
			hash.put("TeamName", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) team.getElementsByTagName("FoundedDate").item(0);
			hash.put("ActivationDate", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) team.getElementsByTagName("HomePage").item(0);
			hash.put("HomePage", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) team.getElementsByTagName("LogoURL").item(0);
			hash.put("LogoURL", (XMLManager.getFirstChildNodeValue(ele)));

			root = (Element) team.getElementsByTagName("League").item(0);
			ele = (Element) root.getElementsByTagName("LeagueID").item(0);
			hash.put("LeagueID", (XMLManager.getFirstChildNodeValue(ele)));

			try {
				ele = (Element) team.getElementsByTagName("LeagueLevel").item(0);
				hash.put("LeagueLevel", (XMLManager.getFirstChildNodeValue(ele)));
				ele = (Element) team.getElementsByTagName("LeagueLevelUnitName").item(0);
				hash.put("LeagueLevelUnitName", (XMLManager.getFirstChildNodeValue(ele)));
				ele = (Element) team.getElementsByTagName("LeagueLevelUnitID").item(0);
				hash.put("LeagueLevelUnitID", (XMLManager.getFirstChildNodeValue(ele)));
			} catch (Exception ex) {
				HOLogger.instance().log(XMLTeamDetailsParser.class, ex);
			}

			try {
				ele = (Element) team.getElementsByTagName("NumberOfVictories").item(0);
				hash.put("NumberOfVictories", (XMLManager.getFirstChildNodeValue(ele)));
				ele = (Element) team.getElementsByTagName("NumberOfUndefeated").item(0);
				hash.put("NumberOfUndefeated", (XMLManager.getFirstChildNodeValue(ele)));
			} catch (Exception exp) {
				HOLogger.instance().log(XMLTeamDetailsParser.class, exp);
			}

			root = (Element) team.getElementsByTagName("Trainer").item(0);
			ele = (Element) root.getElementsByTagName("PlayerID").item(0);
			hash.put("TrainerID", (XMLManager.getFirstChildNodeValue(ele)));

			root = (Element) team.getElementsByTagName("Arena").item(0);
			ele = (Element) root.getElementsByTagName("ArenaName").item(0);
			hash.put("ArenaName", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("ArenaID").item(0);
			hash.put("ArenaID", (XMLManager.getFirstChildNodeValue(ele)));
			root = (Element) team.getElementsByTagName("Region").item(0);
			ele = (Element) root.getElementsByTagName("RegionID").item(0);
			hash.put("RegionID", (XMLManager.getFirstChildNodeValue(ele)));

			// Power Rating
			Element PowerRating = (Element)doc.getDocumentElement().getElementsByTagName("PowerRating").item(0);
			ele = (Element) PowerRating.getElementsByTagName("GlobalRanking").item(0);
			hash.put("GlobalRanking", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) PowerRating.getElementsByTagName("LeagueRanking").item(0);
			hash.put("LeagueRanking", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) PowerRating.getElementsByTagName("RegionRanking").item(0);
			hash.put("RegionRanking", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) PowerRating.getElementsByTagName("PowerRating").item(0);
			hash.put("PowerRating", (XMLManager.getFirstChildNodeValue(ele)));

			if (team.getElementsByTagName("TeamRank").getLength() > 0) {
				hash.put("TeamRank", team.getElementsByTagName("TeamRank").item(0).getTextContent());
			}

		} catch (Exception e) {
			HOLogger.instance().log(XMLTeamDetailsParser.class, e);
		}

		return hash;
	}
	
	
	public static List<TeamInfo> getTeamInfoFromString(String input) {
		Document doc = XMLManager.parseString(input);
		List<TeamInfo> ret = new ArrayList<>();
		
		Element root = doc.getDocumentElement();
		root = (Element) root.getElementsByTagName("Teams").item(0);

		NodeList list = root.getElementsByTagName("Team");
		
		for (int i = 0; (list != null) && (i < list.getLength()); i++) {
			Element team = (Element) list.item(i);
			Element ele;
			
			TeamInfo info = new TeamInfo();
			
			ele = (Element) team.getElementsByTagName("TeamID").item(0);
			info.setTeamId(Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)));
			
			ele = (Element) team.getElementsByTagName("TeamName").item(0);
			info.setName(XMLManager.getFirstChildNodeValue(ele));

			ele = (Element) team.getElementsByTagName("IsPrimaryClub").item(0);
			info.setPrimaryTeam(Boolean.parseBoolean(XMLManager.getFirstChildNodeValue(ele)));

			
			Element league = (Element) team.getElementsByTagName("League").item(0);
			ele = (Element) league.getElementsByTagName("LeagueName").item(0);
			info.setCountry(XMLManager.getFirstChildNodeValue(ele));
			
			ele = (Element) league.getElementsByTagName("LeagueID").item(0);
			info.setLeagueId(Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)));
			
			ele = (Element) team.getElementsByTagName("LeagueLevelUnit").item(0);
			ele = (Element) ele.getElementsByTagName("LeagueLevelUnitName").item(0);
			info.setLeague(XMLManager.getFirstChildNodeValue(ele));
			
			ret.add(info);
		}
		
		return ret;
	}
		
}
