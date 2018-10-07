// %3261485518:de.hattrickorganizer.logik.xml%
/*
 * xmlMatchOrderParser.java
 *
 * Created on 14. Juni 2004, 18:18
 */
package core.file.xml;


import core.model.player.ISpielerPosition;
import core.util.HOLogger;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Parser for the matchorders.
 * 
 * @author TheTom
 */
public class XMLMatchOrderParser {

	/** List of positions used in property files */
	private final static String[] PLAYERPOSITIONS = { "Keeper", "RightBack", "RightCentralDefender",
			"MiddleCentralDefender", "LeftCentralDefender", "LeftBack", "RightWinger", "RightInnerMidfield",
			"CentralInnerMidfield", "LeftInnerMidfield", "LeftWinger", "RightForward", "CentralForward",
			"LeftForward", "SubstKeeper", "SubstBack", "SubstInsideMid", "SubstWinger", "SubstForward",
			"Kicker", "Captain" };

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLMatchOrderParser() {
	}

	/**
	 * Parse match orders from a file.
	 */
	public static Map<String, String> parseMatchOrder(File datei) {
		return parseDetails(XMLManager.parseFile(datei));
	}

	public static Map<String, String> parseMatchOrderFromString(String xmlData) {
		return parseDetails(XMLManager.parseString(xmlData));
	}

	/**
	 * Create a player from the given XML.
	 */
	private static void addPlayer(Element ele, Map<String, String> map) throws Exception {
		Element tmp = null;
		int roleID = -1;
		String behavior = "-1";
		String spielerID = "-1";
		String name = "";

		tmp = (Element) ele.getElementsByTagName("PlayerID").item(0);
		spielerID = XMLManager.getFirstChildNodeValue(tmp);

		if (spielerID.trim().equals("")) {
			spielerID = "-1";
		}

		tmp = (Element) ele.getElementsByTagName("RoleID").item(0);
		if (tmp != null) {
			roleID = Integer.parseInt(XMLManager.getFirstChildNodeValue(tmp));
		}

		tmp = (Element) ele.getElementsByTagName("PlayerName").item(0);
		name = XMLManager.getFirstChildNodeValue(tmp);

		// individual orders only for the 10 players in the lineup (starting11 -
		// keeper)
		if ((roleID > ISpielerPosition.keeper) && (roleID < ISpielerPosition.startReserves)) {
			tmp = (Element) ele.getElementsByTagName("Behaviour").item(0);
			behavior = XMLManager.getFirstChildNodeValue(tmp);
		}

		switch (roleID) {
		// Why do only some have the check for previous entry? I guess it is due
		// to duplicate positions
		// In the 1.3 xml, hopefully no more. They don't hurt, so they are not
		// removed.

		case ISpielerPosition.keeper:
			map.put("KeeperID", spielerID);
			map.put("KeeperName", name);
			map.put("KeeperOrder", "0");
			break;

		case ISpielerPosition.rightBack:
			map.put("RightBackID", spielerID);
			map.put("RightBackName", name);
			map.put("RightBackOrder", behavior);
			break;

		case ISpielerPosition.rightCentralDefender:
			if (!map.containsKey("RightCentralDefenderID")) {
				map.put("RightCentralDefenderID", spielerID);
				map.put("RightCentralDefenderName", name);
				map.put("RightCentralDefenderOrder", behavior);
			} else {
				addAdditionalPlayer(map, roleID, spielerID, name, behavior);
			}
			break;

		case ISpielerPosition.middleCentralDefender:
			if (!map.containsKey("MiddleCentralDefenderID")) {
				map.put("MiddleCentralDefenderID", spielerID);
				map.put("MiddleCentralDefenderName", name);
				map.put("MiddleCentralDefenderOrder", behavior);
			} else {
				addAdditionalPlayer(map, roleID, spielerID, name, behavior);
			}
			break;

		case ISpielerPosition.leftCentralDefender:
			map.put("LeftCentralDefenderID", spielerID);
			map.put("LeftCentralDefenderName", name);
			map.put("LeftCentralDefenderOrder", behavior);
			break;

		case ISpielerPosition.leftBack:
			map.put("LeftBackID", spielerID);
			map.put("LeftBackName", name);
			map.put("LeftBackOrder", behavior);
			break;

		case ISpielerPosition.leftWinger:
			if (!map.containsKey("LeftWingerID")) {
				map.put("LeftWingerID", spielerID);
				map.put("LeftWingerName", name);
				map.put("LeftWingerOrder", behavior);
			} else {
				addAdditionalPlayer(map, roleID, spielerID, name, behavior);
			}
			break;

		case ISpielerPosition.leftInnerMidfield:
			if (!map.containsKey("LeftInnerMidfieldID")) {
				map.put("LeftInnerMidfieldID", spielerID);
				map.put("LeftInnerMidfieldName", name);
				map.put("LeftInnerMidfieldOrder", behavior);
			} else {
				addAdditionalPlayer(map, roleID, spielerID, name, behavior);
			}
			break;

		case ISpielerPosition.centralInnerMidfield:
			if (!map.containsKey("CentralInnerMidfieldID")) {
				map.put("CentralInnerMidfieldID", spielerID);
				map.put("CentralInnerMidfieldName", name);
				map.put("CentralInnerMidfieldOrder", behavior);
			} else {
				addAdditionalPlayer(map, roleID, spielerID, name, behavior);
			}
			break;

		case ISpielerPosition.rightInnerMidfield:
			map.put("RightInnerMidfieldID", spielerID);
			map.put("RightInnerMidfieldName", name);
			map.put("RightInnerMidfieldOrder", behavior);
			break;

		case ISpielerPosition.rightWinger:
			if (!map.containsKey("RightWingerID")) {
				map.put("RightWingerID", spielerID);
				map.put("RightWingerName", name);
				map.put("RightWingerOrder", behavior);
			} else {
				addAdditionalPlayer(map, roleID, spielerID, name, behavior);
			}
			break;

		case ISpielerPosition.rightForward:
			if (!map.containsKey("RightForward")) {
				map.put("RightForwardID", spielerID);
				map.put("RightForwardName", name);
				map.put("RightForwardOrder", behavior);
			} else {
				addAdditionalPlayer(map, roleID, spielerID, name, behavior);
			}
			break;

		case ISpielerPosition.centralForward:
			if (!map.containsKey("CentralForward")) {
				map.put("CentralForwardID", spielerID);
				map.put("CentralForwardName", name);
				map.put("CentralForwardOrder", behavior);
			} else {
				addAdditionalPlayer(map, roleID, spielerID, name, behavior);
			}
			break;

		case ISpielerPosition.leftForward:
			if (!map.containsKey("LeftForward")) {
				map.put("LeftForwardID", spielerID);
				map.put("LeftForwardName", name);
				map.put("LeftForwardOrder", behavior);
			} else {
				addAdditionalPlayer(map, roleID, spielerID, name, behavior);
			}
			break;

		case ISpielerPosition.substKeeper:
			map.put("SubstKeeperID", spielerID);
			map.put("SubstKeeperName", name);
			break;

		case ISpielerPosition.substDefender:
			map.put("SubstBackID", spielerID);
			map.put("SubstBackName", name);
			break;

		case ISpielerPosition.substInnerMidfield:
			map.put("SubstInsideMidID", spielerID);
			map.put("SubstInsideMidName", name);
			break;

		case ISpielerPosition.substWinger:
			map.put("SubstWingerID", spielerID);
			map.put("SubstWingerName", name);
			break;

		case ISpielerPosition.substForward:
			map.put("SubstForwardID", spielerID);
			map.put("SubstForwardName", name);
			break;

		case ISpielerPosition.setPieces:
			map.put("KickerID", spielerID);
			map.put("KickerName", name);
			break;

		case ISpielerPosition.captain:
			map.put("CaptainID", spielerID);
			map.put("CaptainName", name);
			break;
		}
		
		// Penalty positions
		for (int i = ISpielerPosition.penaltyTaker1 ; i <= ISpielerPosition.penaltyTaker11 ; i++) {
			if (roleID == i) {
				map.put("PenaltyTaker" + (i - ISpielerPosition.penaltyTaker1 ), spielerID);
			}
		}
	}

	private static void addAdditionalPlayer(Map<String, String> map, int roleID, String spielerID,
			String name, String behavior) {
		String key = "Additional1";
		if (!map.containsKey(key + "ID")) {
			map.put(key + "ID", spielerID);
			map.put(key + "Role", String.valueOf(roleID));
			map.put(key + "Name", name);
			map.put(key + "Behaviour", behavior);
			return;
		}
		key = "Additional2";
		if (!map.containsKey(key + "ID")) {
			map.put(key + "ID", spielerID);
			map.put(key + "Role", String.valueOf(roleID));
			map.put(key + "Name", name);
			map.put(key + "Behaviour", behavior);
			return;
		}
		key = "Additional3";
		if (!map.containsKey(key + "ID")) {
			map.put(key + "ID", spielerID);
			map.put(key + "Role", String.valueOf(roleID));
			map.put(key + "Name", name);
			map.put(key + "Behaviour", behavior);
			return;
		}
		key = "Additional4";
		if (!map.containsKey(key + "ID")) {
			map.put(key + "ID", spielerID);
			map.put(key + "Role", String.valueOf(roleID));
			map.put(key + "Name", name);
			map.put(key + "Behaviour", behavior);
			return;
		}
		// max. 4 additional/repositioned players in the new lineup?
	}

	private static void addPlayerOrder(Element ele, Map<String, String> map, int num) throws Exception {
		Element tmp = null;
		String playerOrderID = "" + num;
		String playerIn = "-1";
		String playerOut = "-1";
		String orderType = "-1";
		String minute = "-1";
		String matchMinuteCriteria = "-1";
		String pos = "-1";
		String behaviour = "-1";
		String card = "-1";
		String standing = "-1";

		tmp = (Element) ele.getElementsByTagName("MatchMinuteCriteria").item(0);
		if (tmp != null) {
			matchMinuteCriteria = XMLManager.getFirstChildNodeValue(tmp);
		}

		tmp = (Element) ele.getElementsByTagName("GoalDiffCriteria").item(0);
		if (tmp != null) {
			standing = XMLManager.getFirstChildNodeValue(tmp);
		}
		tmp = (Element) ele.getElementsByTagName("RedCardCriteria").item(0);
		if (tmp != null) {
			card = XMLManager.getFirstChildNodeValue(tmp);
		}
		tmp = (Element) ele.getElementsByTagName("SubjectPlayerID").item(0);
		if (tmp != null) {
			playerOut = XMLManager.getFirstChildNodeValue(tmp);
		}
		tmp = (Element) ele.getElementsByTagName("ObjectPlayerID").item(0);
		if (tmp != null) {
			playerIn = XMLManager.getFirstChildNodeValue(tmp);
		}

		tmp = (Element) ele.getElementsByTagName("OrderType").item(0);
		if (tmp != null) {
			orderType = XMLManager.getFirstChildNodeValue(tmp);
		}
		tmp = (Element) ele.getElementsByTagName("NewPositionId").item(0);
		if (tmp != null) {
			pos = XMLManager.getFirstChildNodeValue(tmp);
		}
		tmp = (Element) ele.getElementsByTagName("NewPositionBehaviour").item(0);
		if (tmp != null) {
			behaviour = XMLManager.getFirstChildNodeValue(tmp);
		}

		map.put("subst" + num + "playerOrderID", playerOrderID);
		map.put("subst" + num + "playerIn", playerIn);
		map.put("subst" + num + "playerOut", playerOut);
		map.put("subst" + num + "orderType", orderType);
		map.put("subst" + num + "minute", minute);
		map.put("subst" + num + "matchMinuteCriteria", matchMinuteCriteria);
		map.put("subst" + num + "pos", pos);
		map.put("subst" + num + "behaviour", behaviour);
		map.put("subst" + num + "card", card);
		map.put("subst" + num + "standing", standing);

	}

	/**
	 * erstellt das MAtchlineup Objekt
	 */
	private static Hashtable<String, String> parseDetails(Document doc) {
		Element ele = null;
		Element root = null;
		final MyHashtable hash = new MyHashtable();
		NodeList list = null;

		if (doc == null) {
			return hash;
		}

		// Tabelle erstellen
		root = doc.getDocumentElement();

		try {
			ele = (Element) root.getElementsByTagName("Version").item(0);

			// Nach Format ab Version 1.2 parsen
			// Daten füllen
			// Fetchdate
			ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
			hash.put("FetchedDate", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("MatchID").item(0);
			hash.put("MatchID", (XMLManager.getFirstChildNodeValue(ele)));

			// Root wechseln
			root = (Element) root.getElementsByTagName("MatchData").item(0);

			if (!XMLManager.getAttributeValue(root, "Available").trim().equalsIgnoreCase("true")) {
				return hash;
			}

			ele = (Element) root.getElementsByTagName("Attitude").item(0);

			if (XMLManager.getAttributeValue(ele, "Available").trim().equalsIgnoreCase("true")) {
				hash.put("Attitude", (XMLManager.getFirstChildNodeValue(ele)));
			} else {
				hash.put("Attitude", "0");
			}
			
			ele = (Element) root.getElementsByTagName("CoachModifier").item(0);
			
			// bingeling doesn't like the name coachModifier, so we always use styleOfPlay
			if (XMLManager.getAttributeValue(ele, "Available").trim().equalsIgnoreCase("true")) {
				hash.put("StyleOfPlay", (XMLManager.getFirstChildNodeValue(ele)));
			} else {
				hash.put("StyleOfPlay", "0");
			}
			

			ele = (Element) root.getElementsByTagName("HomeTeamID").item(0);
			hash.put("HomeTeamID", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("HomeTeamName").item(0);
			hash.put("HomeTeamName", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("AwayTeamID").item(0);
			hash.put("AwayTeamID", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("AwayTeamName").item(0);
			hash.put("AwayTeamName", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("MatchDate").item(0);
			hash.put("MatchDate", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("ArenaID").item(0);
			hash.put("ArenaID", (XMLManager.getFirstChildNodeValue(ele)));
			ele = (Element) root.getElementsByTagName("ArenaName").item(0);
			hash.put("ArenaName", (XMLManager.getFirstChildNodeValue(ele)));
			
			ele = (Element) root.getElementsByTagName("TacticType").item(0);

			hash.put("TacticType", (XMLManager.getFirstChildNodeValue(ele)));
			
			// Root wechseln
			Element child = (Element) root.getElementsByTagName("Lineup").item(0);

			list = child.getElementsByTagName("Player");

			for (int i = 0; (list != null) && (i < list.getLength()); i++) {
				addPlayer((Element) list.item(i), hash);
			}
			fillEmptySpotsWithAdditionalPlayers(hash);

			child = (Element) root.getElementsByTagName("PlayerOrders").item(0);

			list = child.getElementsByTagName("PlayerOrder");
			for (int i = 0; (list != null) && (i < list.getLength()); i++) {
				addPlayerOrder((Element) list.item(i), hash, i);
			}

		} catch (Exception e) {
			HOLogger.instance().log(XMLMatchOrderParser.class,
					"XMLMatchOrderParser.parseDetails Exception gefangen: " + e);
			HOLogger.instance().log(XMLMatchOrderParser.class, e);
		}

		return hash;
	}

	private static void fillEmptySpotsWithAdditionalPlayers(Map<String, String> map) {
		// Does this have any role? I guess noone will miss it if deleted after
		// 553 change

		try {
			int a = 1;
			String add = map.get("Additional" + a + "ID");
			if (add == null) {
				return;
			} else {
				String pos = getNextFreeSlot(map);
				if (pos != null) {
					map.put(pos + "ID", add);
					map.put(pos + "Name", map.get("Additional" + a + "Name"));
				}
			}
			a = 2;
			add = map.get("Additional" + a + "ID");
			if (add == null) {
				return;
			} else {
				String pos = getNextFreeSlot(map);
				if (pos != null) {
					map.put(pos + "ID", add);
					map.put(pos + "Name", map.get("Additional" + a + "Name"));
				}
			}
			a = 3;
			add = map.get("Additional" + a + "ID");
			if (add == null) {
				return;
			} else {
				String pos = getNextFreeSlot(map);
				if (pos != null) {
					map.put(pos + "ID", add);
					map.put(pos + "Name", map.get("Additional" + a + "Name"));
				}
			}
			a = 4;
			add = map.get("Additional" + a + "ID");
			if (add == null) {
				return;
			} else {
				String pos = getNextFreeSlot(map);
				if (pos != null) {
					map.put(pos + "ID", add);
					map.put(pos + "Name", map.get("Additional" + a + "Name"));
				}
			}
		} catch (Exception e) {
			HOLogger.instance().debug(XMLMatchOrderParser.class, e);
		}
	}

	private static String getNextFreeSlot(Map<String, String> map) {
		for (String pos : PLAYERPOSITIONS) {
			if (!map.containsKey(pos + "ID")) {
				return pos;
			}
		}
		return null;
	}
}
